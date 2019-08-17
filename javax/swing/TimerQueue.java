package javax.swing;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import sun.awt.AppContext;

class TimerQueue implements Runnable {
  private static final Object sharedInstanceKey = new StringBuffer("TimerQueue.sharedInstanceKey");
  
  private static final Object expiredTimersKey = new StringBuffer("TimerQueue.expiredTimersKey");
  
  private final DelayQueue<DelayedTimer> queue = new DelayQueue();
  
  private final Lock runningLock = new ReentrantLock();
  
  private static final Object classLock = new Object();
  
  private static final long NANO_ORIGIN = System.nanoTime();
  
  public TimerQueue() { startIfNeeded(); }
  
  public static TimerQueue sharedInstance() {
    synchronized (classLock) {
      TimerQueue timerQueue = (TimerQueue)SwingUtilities.appContextGet(sharedInstanceKey);
      if (timerQueue == null) {
        timerQueue = new TimerQueue();
        SwingUtilities.appContextPut(sharedInstanceKey, timerQueue);
      } 
      return timerQueue;
    } 
  }
  
  void startIfNeeded() {
    if (!this.running) {
      this.runningLock.lock();
      if (this.running)
        return; 
      try {
        final ThreadGroup threadGroup = AppContext.getAppContext().getThreadGroup();
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
              public Object run() {
                Thread thread = new Thread(threadGroup, TimerQueue.this, "TimerQueue");
                thread.setDaemon(true);
                thread.setPriority(5);
                thread.start();
                return null;
              }
            });
        this.running = true;
      } finally {
        this.runningLock.unlock();
      } 
    } 
  }
  
  void addTimer(Timer paramTimer, long paramLong) {
    paramTimer.getLock().lock();
    try {
      if (!containsTimer(paramTimer))
        addTimer(new DelayedTimer(paramTimer, TimeUnit.MILLISECONDS.toNanos(paramLong) + now())); 
    } finally {
      paramTimer.getLock().unlock();
    } 
  }
  
  private void addTimer(DelayedTimer paramDelayedTimer) {
    assert paramDelayedTimer != null && !containsTimer(paramDelayedTimer.getTimer());
    timer = paramDelayedTimer.getTimer();
    timer.getLock().lock();
    try {
      timer.delayedTimer = paramDelayedTimer;
      this.queue.add(paramDelayedTimer);
    } finally {
      timer.getLock().unlock();
    } 
  }
  
  void removeTimer(Timer paramTimer) {
    paramTimer.getLock().lock();
    try {
      if (paramTimer.delayedTimer != null) {
        this.queue.remove(paramTimer.delayedTimer);
        paramTimer.delayedTimer = null;
      } 
    } finally {
      paramTimer.getLock().unlock();
    } 
  }
  
  boolean containsTimer(Timer paramTimer) {
    paramTimer.getLock().lock();
    try {
      return (paramTimer.delayedTimer != null);
    } finally {
      paramTimer.getLock().unlock();
    } 
  }
  
  public void run() {
    this.runningLock.lock();
    try {
      while (this.running) {
        try {
          DelayedTimer delayedTimer = (DelayedTimer)this.queue.take();
          timer = delayedTimer.getTimer();
          timer.getLock().lock();
          try {
            DelayedTimer delayedTimer1 = timer.delayedTimer;
            if (delayedTimer1 == delayedTimer) {
              timer.post();
              timer.delayedTimer = null;
              if (timer.isRepeats()) {
                delayedTimer1.setTime(now() + TimeUnit.MILLISECONDS.toNanos(timer.getDelay()));
                addTimer(delayedTimer1);
              } 
            } 
            timer.getLock().newCondition().awaitNanos(1L);
          } catch (SecurityException securityException) {
          
          } finally {
            timer.getLock().unlock();
          } 
        } catch (InterruptedException interruptedException) {
          if (AppContext.getAppContext().isDisposed())
            break; 
        } 
      } 
    } catch (ThreadDeath threadDeath) {
      for (DelayedTimer delayedTimer : this.queue)
        delayedTimer.getTimer().cancelEvent(); 
      throw threadDeath;
    } finally {
      this.running = false;
      this.runningLock.unlock();
    } 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("TimerQueue (");
    boolean bool = true;
    for (DelayedTimer delayedTimer : this.queue) {
      if (!bool)
        stringBuilder.append(", "); 
      stringBuilder.append(delayedTimer.getTimer().toString());
      bool = false;
    } 
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
  
  private static long now() { return System.nanoTime() - NANO_ORIGIN; }
  
  static class DelayedTimer implements Delayed {
    private static final AtomicLong sequencer = new AtomicLong(0L);
    
    private final long sequenceNumber;
    
    private final Timer timer;
    
    DelayedTimer(Timer param1Timer, long param1Long) {
      this.timer = param1Timer;
      this.time = param1Long;
      this.sequenceNumber = sequencer.getAndIncrement();
    }
    
    public final long getDelay(TimeUnit param1TimeUnit) { return param1TimeUnit.convert(this.time - TimerQueue.now(), TimeUnit.NANOSECONDS); }
    
    final void setTime(long param1Long) { this.time = param1Long; }
    
    final Timer getTimer() { return this.timer; }
    
    public int compareTo(Delayed param1Delayed) {
      if (param1Delayed == this)
        return 0; 
      if (param1Delayed instanceof DelayedTimer) {
        DelayedTimer delayedTimer = (DelayedTimer)param1Delayed;
        long l1 = this.time - delayedTimer.time;
        return (l1 < 0L) ? -1 : ((l1 > 0L) ? 1 : ((this.sequenceNumber < delayedTimer.sequenceNumber) ? -1 : 1));
      } 
      long l = getDelay(TimeUnit.NANOSECONDS) - param1Delayed.getDelay(TimeUnit.NANOSECONDS);
      return (l == 0L) ? 0 : ((l < 0L) ? -1 : 1);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\TimerQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */