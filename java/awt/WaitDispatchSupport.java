package java.awt;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.awt.PeerEvent;
import sun.util.logging.PlatformLogger;

class WaitDispatchSupport implements SecondaryLoop {
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.event.WaitDispatchSupport");
  
  private EventDispatchThread dispatchThread;
  
  private EventFilter filter;
  
  private long interval;
  
  private static Timer timer;
  
  private TimerTask timerTask;
  
  private AtomicBoolean keepBlockingEDT = new AtomicBoolean(false);
  
  private AtomicBoolean keepBlockingCT = new AtomicBoolean(false);
  
  private AtomicBoolean afterExit = new AtomicBoolean(false);
  
  private final Runnable wakingRunnable = new Runnable() {
      public void run() {
        log.fine("Wake up EDT");
        synchronized (WaitDispatchSupport.getTreeLock()) {
          WaitDispatchSupport.this.keepBlockingCT.set(false);
          WaitDispatchSupport.getTreeLock().notifyAll();
        } 
        log.fine("Wake up EDT done");
      }
    };
  
  private static void initializeTimer() {
    if (timer == null)
      timer = new Timer("AWT-WaitDispatchSupport-Timer", true); 
  }
  
  public WaitDispatchSupport(EventDispatchThread paramEventDispatchThread) { this(paramEventDispatchThread, null); }
  
  public WaitDispatchSupport(EventDispatchThread paramEventDispatchThread, Conditional paramConditional) {
    if (paramEventDispatchThread == null)
      throw new IllegalArgumentException("The dispatchThread can not be null"); 
    this.dispatchThread = paramEventDispatchThread;
    this.extCondition = paramConditional;
    this.condition = new Conditional() {
        public boolean evaluate() {
          if (log.isLoggable(PlatformLogger.Level.FINEST))
            log.finest("evaluate(): blockingEDT=" + WaitDispatchSupport.this.keepBlockingEDT.get() + ", blockingCT=" + WaitDispatchSupport.this.keepBlockingCT.get()); 
          boolean bool = (WaitDispatchSupport.this.extCondition != null) ? WaitDispatchSupport.this.extCondition.evaluate() : 1;
          if (!WaitDispatchSupport.this.keepBlockingEDT.get() || !bool) {
            if (WaitDispatchSupport.this.timerTask != null) {
              WaitDispatchSupport.this.timerTask.cancel();
              WaitDispatchSupport.this.timerTask = null;
            } 
            return false;
          } 
          return true;
        }
      };
  }
  
  public WaitDispatchSupport(EventDispatchThread paramEventDispatchThread, Conditional paramConditional, EventFilter paramEventFilter, long paramLong) {
    this(paramEventDispatchThread, paramConditional);
    this.filter = paramEventFilter;
    if (paramLong < 0L)
      throw new IllegalArgumentException("The interval value must be >= 0"); 
    this.interval = paramLong;
    if (paramLong != 0L)
      initializeTimer(); 
  }
  
  public boolean enter() {
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine("enter(): blockingEDT=" + this.keepBlockingEDT.get() + ", blockingCT=" + this.keepBlockingCT.get()); 
    if (!this.keepBlockingEDT.compareAndSet(false, true)) {
      log.fine("The secondary loop is already running, aborting");
      return false;
    } 
    try {
      if (this.afterExit.get()) {
        log.fine("Exit was called already, aborting");
        return false;
      } 
      final Runnable run = new Runnable() {
          public void run() {
            log.fine("Starting a new event pump");
            if (WaitDispatchSupport.this.filter == null) {
              WaitDispatchSupport.this.dispatchThread.pumpEvents(WaitDispatchSupport.this.condition);
            } else {
              WaitDispatchSupport.this.dispatchThread.pumpEventsForFilter(WaitDispatchSupport.this.condition, WaitDispatchSupport.this.filter);
            } 
          }
        };
      Thread thread = Thread.currentThread();
      if (thread == this.dispatchThread) {
        if (log.isLoggable(PlatformLogger.Level.FINEST))
          log.finest("On dispatch thread: " + this.dispatchThread); 
        if (this.interval != 0L) {
          if (log.isLoggable(PlatformLogger.Level.FINEST))
            log.finest("scheduling the timer for " + this.interval + " ms"); 
          timer.schedule(this.timerTask = new TimerTask() {
                public void run() {
                  if (WaitDispatchSupport.this.keepBlockingEDT.compareAndSet(true, false))
                    WaitDispatchSupport.this.wakeupEDT(); 
                }
              }this.interval);
        } 
        SequencedEvent sequencedEvent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
        if (sequencedEvent != null) {
          if (log.isLoggable(PlatformLogger.Level.FINE))
            log.fine("Dispose current SequencedEvent: " + sequencedEvent); 
          sequencedEvent.dispose();
        } 
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                run.run();
                return null;
              }
            });
      } else {
        if (log.isLoggable(PlatformLogger.Level.FINEST))
          log.finest("On non-dispatch thread: " + thread); 
        this.keepBlockingCT.set(true);
        synchronized (getTreeLock()) {
          if (this.afterExit.get())
            return false; 
          if (this.filter != null)
            this.dispatchThread.addEventFilter(this.filter); 
          try {
            EventQueue eventQueue = this.dispatchThread.getEventQueue();
            eventQueue.postEvent(new PeerEvent(this, runnable, 1L));
            if (this.interval > 0L) {
              long l = System.currentTimeMillis();
              while (this.keepBlockingCT.get() && (this.extCondition == null || this.extCondition.evaluate()) && l + this.interval > System.currentTimeMillis())
                getTreeLock().wait(this.interval); 
            } else {
              while (this.keepBlockingCT.get() && (this.extCondition == null || this.extCondition.evaluate()))
                getTreeLock().wait(); 
            } 
            if (log.isLoggable(PlatformLogger.Level.FINE))
              log.fine("waitDone " + this.keepBlockingEDT.get() + " " + this.keepBlockingCT.get()); 
          } catch (InterruptedException interruptedException) {
            if (log.isLoggable(PlatformLogger.Level.FINE))
              log.fine("Exception caught while waiting: " + interruptedException); 
          } finally {
            if (this.filter != null)
              this.dispatchThread.removeEventFilter(this.filter); 
          } 
        } 
      } 
      return true;
    } finally {
      this.keepBlockingEDT.set(false);
      this.keepBlockingCT.set(false);
      this.afterExit.set(false);
    } 
  }
  
  public boolean exit() {
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine("exit(): blockingEDT=" + this.keepBlockingEDT.get() + ", blockingCT=" + this.keepBlockingCT.get()); 
    this.afterExit.set(true);
    if (this.keepBlockingEDT.getAndSet(false)) {
      wakeupEDT();
      return true;
    } 
    return false;
  }
  
  private static final Object getTreeLock() { return Component.LOCK; }
  
  private void wakeupEDT() {
    if (log.isLoggable(PlatformLogger.Level.FINEST))
      log.finest("wakeupEDT(): EDT == " + this.dispatchThread); 
    EventQueue eventQueue = this.dispatchThread.getEventQueue();
    eventQueue.postEvent(new PeerEvent(this, this.wakingRunnable, 1L));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\WaitDispatchSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */