package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.peer.ComponentPeer;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import sun.awt.AWTAccessor;
import sun.awt.AWTAutoShutdown;
import sun.awt.AppContext;
import sun.awt.EventQueueItem;
import sun.awt.FwDispatcher;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.util.logging.PlatformLogger;

public class EventQueue {
  private static final AtomicInteger threadInitNumber = new AtomicInteger(0);
  
  private static final int LOW_PRIORITY = 0;
  
  private static final int NORM_PRIORITY = 1;
  
  private static final int HIGH_PRIORITY = 2;
  
  private static final int ULTIMATE_PRIORITY = 3;
  
  private static final int NUM_PRIORITIES = 4;
  
  private Queue[] queues = new Queue[4];
  
  private EventQueue nextQueue;
  
  private EventQueue previousQueue;
  
  private final Lock pushPopLock;
  
  private final Condition pushPopCond;
  
  private static final Runnable dummyRunnable = new Runnable() {
      public void run() {}
    };
  
  private EventDispatchThread dispatchThread;
  
  private final ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
  
  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  
  private long mostRecentEventTime = System.currentTimeMillis();
  
  private long mostRecentKeyEventTime = System.currentTimeMillis();
  
  private WeakReference<AWTEvent> currentEvent;
  
  private final AppContext appContext;
  
  private final String name = "AWT-EventQueue-" + threadInitNumber.getAndIncrement();
  
  private FwDispatcher fwDispatcher;
  
  private static final int PAINT = 0;
  
  private static final int UPDATE = 1;
  
  private static final int MOVE = 2;
  
  private static final int DRAG = 3;
  
  private static final int PEER = 4;
  
  private static final int CACHE_LENGTH = 5;
  
  private static final JavaSecurityAccess javaSecurityAccess;
  
  private static final PlatformLogger getEventLog() {
    if (eventLog == null)
      eventLog = PlatformLogger.getLogger("java.awt.event.EventQueue"); 
    return eventLog;
  }
  
  public EventQueue() {
    for (byte b = 0; b < 4; b++)
      this.queues[b] = new Queue(); 
    this.appContext = AppContext.getAppContext();
    this.pushPopLock = (Lock)this.appContext.get(AppContext.EVENT_QUEUE_LOCK_KEY);
    this.pushPopCond = (Condition)this.appContext.get(AppContext.EVENT_QUEUE_COND_KEY);
  }
  
  public void postEvent(AWTEvent paramAWTEvent) {
    SunToolkit.flushPendingEvents(this.appContext);
    postEventPrivate(paramAWTEvent);
  }
  
  private final void postEventPrivate(AWTEvent paramAWTEvent) {
    paramAWTEvent.isPosted = true;
    this.pushPopLock.lock();
    try {
      if (this.nextQueue != null) {
        this.nextQueue.postEventPrivate(paramAWTEvent);
        return;
      } 
      if (this.dispatchThread == null) {
        if (paramAWTEvent.getSource() == AWTAutoShutdown.getInstance())
          return; 
        initDispatchThread();
      } 
      postEvent(paramAWTEvent, getPriority(paramAWTEvent));
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  private static int getPriority(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof PeerEvent) {
      PeerEvent peerEvent = (PeerEvent)paramAWTEvent;
      if ((peerEvent.getFlags() & 0x2L) != 0L)
        return 3; 
      if ((peerEvent.getFlags() & 0x1L) != 0L)
        return 2; 
      if ((peerEvent.getFlags() & 0x4L) != 0L)
        return 0; 
    } 
    int i = paramAWTEvent.getID();
    return (i >= 800 && i <= 801) ? 0 : 1;
  }
  
  private void postEvent(AWTEvent paramAWTEvent, int paramInt) {
    if (coalesceEvent(paramAWTEvent, paramInt))
      return; 
    EventQueueItem eventQueueItem = new EventQueueItem(paramAWTEvent);
    cacheEQItem(eventQueueItem);
    boolean bool = (paramAWTEvent.getID() == this.waitForID) ? 1 : 0;
    if ((this.queues[paramInt]).head == null) {
      boolean bool1 = noEvents();
      (this.queues[paramInt]).head = (this.queues[paramInt]).tail = eventQueueItem;
      if (bool1) {
        if (paramAWTEvent.getSource() != AWTAutoShutdown.getInstance())
          AWTAutoShutdown.getInstance().notifyThreadBusy(this.dispatchThread); 
        this.pushPopCond.signalAll();
      } else if (bool) {
        this.pushPopCond.signalAll();
      } 
    } else {
      (this.queues[paramInt]).tail.next = eventQueueItem;
      (this.queues[paramInt]).tail = eventQueueItem;
      if (bool)
        this.pushPopCond.signalAll(); 
    } 
  }
  
  private boolean coalescePaintEvent(PaintEvent paramPaintEvent) {
    ComponentPeer componentPeer = ((Component)paramPaintEvent.getSource()).peer;
    if (componentPeer != null)
      componentPeer.coalescePaintEvent(paramPaintEvent); 
    EventQueueItem[] arrayOfEventQueueItem = ((Component)paramPaintEvent.getSource()).eventCache;
    if (arrayOfEventQueueItem == null)
      return false; 
    int i = eventToCacheIndex(paramPaintEvent);
    if (i != -1 && arrayOfEventQueueItem[i] != null) {
      PaintEvent paintEvent = mergePaintEvents(paramPaintEvent, (PaintEvent)(arrayOfEventQueueItem[i]).event);
      if (paintEvent != null) {
        (arrayOfEventQueueItem[i]).event = paintEvent;
        return true;
      } 
    } 
    return false;
  }
  
  private PaintEvent mergePaintEvents(PaintEvent paramPaintEvent1, PaintEvent paramPaintEvent2) {
    Rectangle rectangle1 = paramPaintEvent1.getUpdateRect();
    Rectangle rectangle2 = paramPaintEvent2.getUpdateRect();
    return rectangle2.contains(rectangle1) ? paramPaintEvent2 : (rectangle1.contains(rectangle2) ? paramPaintEvent1 : null);
  }
  
  private boolean coalesceMouseEvent(MouseEvent paramMouseEvent) {
    EventQueueItem[] arrayOfEventQueueItem = ((Component)paramMouseEvent.getSource()).eventCache;
    if (arrayOfEventQueueItem == null)
      return false; 
    int i = eventToCacheIndex(paramMouseEvent);
    if (i != -1 && arrayOfEventQueueItem[i] != null) {
      (arrayOfEventQueueItem[i]).event = paramMouseEvent;
      return true;
    } 
    return false;
  }
  
  private boolean coalescePeerEvent(PeerEvent paramPeerEvent) {
    EventQueueItem[] arrayOfEventQueueItem = ((Component)paramPeerEvent.getSource()).eventCache;
    if (arrayOfEventQueueItem == null)
      return false; 
    int i = eventToCacheIndex(paramPeerEvent);
    if (i != -1 && arrayOfEventQueueItem[i] != null) {
      paramPeerEvent = paramPeerEvent.coalesceEvents((PeerEvent)(arrayOfEventQueueItem[i]).event);
      if (paramPeerEvent != null) {
        (arrayOfEventQueueItem[i]).event = paramPeerEvent;
        return true;
      } 
      arrayOfEventQueueItem[i] = null;
    } 
    return false;
  }
  
  private boolean coalesceOtherEvent(AWTEvent paramAWTEvent, int paramInt) {
    int i = paramAWTEvent.getID();
    Component component = (Component)paramAWTEvent.getSource();
    for (EventQueueItem eventQueueItem = (this.queues[paramInt]).head; eventQueueItem != null; eventQueueItem = eventQueueItem.next) {
      if (eventQueueItem.event.getSource() == component && eventQueueItem.event.getID() == i) {
        AWTEvent aWTEvent = component.coalesceEvents(eventQueueItem.event, paramAWTEvent);
        if (aWTEvent != null) {
          eventQueueItem.event = aWTEvent;
          return true;
        } 
      } 
    } 
    return false;
  }
  
  private boolean coalesceEvent(AWTEvent paramAWTEvent, int paramInt) { return !(paramAWTEvent.getSource() instanceof Component) ? false : ((paramAWTEvent instanceof PeerEvent) ? coalescePeerEvent((PeerEvent)paramAWTEvent) : ((((Component)paramAWTEvent.getSource()).isCoalescingEnabled() && coalesceOtherEvent(paramAWTEvent, paramInt)) ? 1 : ((paramAWTEvent instanceof PaintEvent) ? coalescePaintEvent((PaintEvent)paramAWTEvent) : ((paramAWTEvent instanceof MouseEvent) ? coalesceMouseEvent((MouseEvent)paramAWTEvent) : 0)))); }
  
  private void cacheEQItem(EventQueueItem paramEventQueueItem) {
    int i = eventToCacheIndex(paramEventQueueItem.event);
    if (i != -1 && paramEventQueueItem.event.getSource() instanceof Component) {
      Component component = (Component)paramEventQueueItem.event.getSource();
      if (component.eventCache == null)
        component.eventCache = new EventQueueItem[5]; 
      component.eventCache[i] = paramEventQueueItem;
    } 
  }
  
  private void uncacheEQItem(EventQueueItem paramEventQueueItem) {
    int i = eventToCacheIndex(paramEventQueueItem.event);
    if (i != -1 && paramEventQueueItem.event.getSource() instanceof Component) {
      Component component = (Component)paramEventQueueItem.event.getSource();
      if (component.eventCache == null)
        return; 
      component.eventCache[i] = null;
    } 
  }
  
  private static int eventToCacheIndex(AWTEvent paramAWTEvent) {
    switch (paramAWTEvent.getID()) {
      case 800:
        return 0;
      case 801:
        return 1;
      case 503:
        return 2;
      case 506:
        return (paramAWTEvent instanceof sun.awt.dnd.SunDropTargetEvent) ? -1 : 3;
    } 
    return (paramAWTEvent instanceof PeerEvent) ? 4 : -1;
  }
  
  private boolean noEvents() {
    for (byte b = 0; b < 4; b++) {
      if ((this.queues[b]).head != null)
        return false; 
    } 
    return true;
  }
  
  public AWTEvent getNextEvent() throws InterruptedException {
    while (true) {
      SunToolkit.flushPendingEvents(this.appContext);
      this.pushPopLock.lock();
      try {
        AWTEvent aWTEvent = getNextEventPrivate();
        if (aWTEvent != null)
          return aWTEvent; 
        AWTAutoShutdown.getInstance().notifyThreadFree(this.dispatchThread);
        this.pushPopCond.await();
      } finally {
        this.pushPopLock.unlock();
      } 
    } 
  }
  
  AWTEvent getNextEventPrivate() throws InterruptedException {
    for (byte b = 3; b >= 0; b--) {
      if ((this.queues[b]).head != null) {
        EventQueueItem eventQueueItem = (this.queues[b]).head;
        (this.queues[b]).head = eventQueueItem.next;
        if (eventQueueItem.next == null)
          (this.queues[b]).tail = null; 
        uncacheEQItem(eventQueueItem);
        return eventQueueItem.event;
      } 
    } 
    return null;
  }
  
  AWTEvent getNextEvent(int paramInt) throws InterruptedException {
    while (true) {
      SunToolkit.flushPendingEvents(this.appContext);
      this.pushPopLock.lock();
      try {
        for (byte b = 0; b < 4; b++) {
          EventQueueItem eventQueueItem1 = (this.queues[b]).head;
          EventQueueItem eventQueueItem2 = null;
          while (eventQueueItem1 != null) {
            if (eventQueueItem1.event.getID() == paramInt) {
              if (eventQueueItem2 == null) {
                (this.queues[b]).head = eventQueueItem1.next;
              } else {
                eventQueueItem2.next = eventQueueItem1.next;
              } 
              if ((this.queues[b]).tail == eventQueueItem1)
                (this.queues[b]).tail = eventQueueItem2; 
              uncacheEQItem(eventQueueItem1);
              return eventQueueItem1.event;
            } 
            eventQueueItem2 = eventQueueItem1;
            eventQueueItem1 = eventQueueItem1.next;
          } 
        } 
        this.waitForID = paramInt;
        this.pushPopCond.await();
        this.waitForID = 0;
      } finally {
        this.pushPopLock.unlock();
      } 
    } 
  }
  
  public AWTEvent peekEvent() throws InterruptedException {
    this.pushPopLock.lock();
    try {
      for (byte b = 3; b >= 0; b--) {
        if ((this.queues[b]).head != null)
          return (this.queues[b]).head.event; 
      } 
    } finally {
      this.pushPopLock.unlock();
    } 
    return null;
  }
  
  public AWTEvent peekEvent(int paramInt) throws InterruptedException {
    this.pushPopLock.lock();
    try {
      for (byte b = 3; b >= 0; b--) {
        for (EventQueueItem eventQueueItem = (this.queues[b]).head; eventQueueItem != null; eventQueueItem = eventQueueItem.next) {
          if (eventQueueItem.event.getID() == paramInt)
            return eventQueueItem.event; 
        } 
      } 
    } finally {
      this.pushPopLock.unlock();
    } 
    return null;
  }
  
  protected void dispatchEvent(final AWTEvent event) {
    final Object src = paramAWTEvent.getSource();
    final PrivilegedAction<Void> action = new PrivilegedAction<Void>() {
        public Void run() {
          if (EventQueue.this.fwDispatcher == null || EventQueue.this.isDispatchThreadImpl()) {
            EventQueue.this.dispatchEventImpl(event, src);
          } else {
            EventQueue.this.fwDispatcher.scheduleDispatch(new Runnable() {
                  public void run() {
                    if (EventQueue.null.this.this$0.dispatchThread.filterAndCheckEvent(event))
                      EventQueue.null.this.this$0.dispatchEventImpl(event, src); 
                  }
                });
          } 
          return null;
        }
      };
    AccessControlContext accessControlContext1 = AccessController.getContext();
    AccessControlContext accessControlContext2 = getAccessControlContextFrom(object);
    final AccessControlContext eventAcc = paramAWTEvent.getAccessControlContext();
    if (accessControlContext2 == null) {
      javaSecurityAccess.doIntersectionPrivilege(privilegedAction, accessControlContext1, accessControlContext3);
    } else {
      javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
            public Void run() {
              javaSecurityAccess.doIntersectionPrivilege(action, eventAcc);
              return null;
            }
          }accessControlContext1, accessControlContext2);
    } 
  }
  
  private static AccessControlContext getAccessControlContextFrom(Object paramObject) { return (paramObject instanceof Component) ? ((Component)paramObject).getAccessControlContext() : ((paramObject instanceof MenuComponent) ? ((MenuComponent)paramObject).getAccessControlContext() : ((paramObject instanceof TrayIcon) ? ((TrayIcon)paramObject).getAccessControlContext() : null)); }
  
  private void dispatchEventImpl(AWTEvent paramAWTEvent, Object paramObject) {
    paramAWTEvent.isPosted = true;
    if (paramAWTEvent instanceof ActiveEvent) {
      setCurrentEventAndMostRecentTimeImpl(paramAWTEvent);
      ((ActiveEvent)paramAWTEvent).dispatch();
    } else if (paramObject instanceof Component) {
      ((Component)paramObject).dispatchEvent(paramAWTEvent);
      paramAWTEvent.dispatched();
    } else if (paramObject instanceof MenuComponent) {
      ((MenuComponent)paramObject).dispatchEvent(paramAWTEvent);
    } else if (paramObject instanceof TrayIcon) {
      ((TrayIcon)paramObject).dispatchEvent(paramAWTEvent);
    } else if (paramObject instanceof AWTAutoShutdown) {
      if (noEvents())
        this.dispatchThread.stopDispatching(); 
    } else if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
      getEventLog().fine("Unable to dispatch event: " + paramAWTEvent);
    } 
  }
  
  public static long getMostRecentEventTime() { return Toolkit.getEventQueue().getMostRecentEventTimeImpl(); }
  
  private long getMostRecentEventTimeImpl() {
    this.pushPopLock.lock();
    try {
      return (Thread.currentThread() == this.dispatchThread) ? this.mostRecentEventTime : System.currentTimeMillis();
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  long getMostRecentEventTimeEx() {
    this.pushPopLock.lock();
    try {
      return this.mostRecentEventTime;
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  public static AWTEvent getCurrentEvent() throws InterruptedException { return Toolkit.getEventQueue().getCurrentEventImpl(); }
  
  private AWTEvent getCurrentEventImpl() throws InterruptedException {
    this.pushPopLock.lock();
    try {
      return (Thread.currentThread() == this.dispatchThread) ? (AWTEvent)this.currentEvent.get() : null;
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  public void push(EventQueue paramEventQueue) {
    if (getEventLog().isLoggable(PlatformLogger.Level.FINE))
      getEventLog().fine("EventQueue.push(" + paramEventQueue + ")"); 
    this.pushPopLock.lock();
    try {
      EventQueue eventQueue;
      for (eventQueue = this; eventQueue.nextQueue != null; eventQueue = eventQueue.nextQueue);
      if (eventQueue.fwDispatcher != null)
        throw new RuntimeException("push() to queue with fwDispatcher"); 
      if (eventQueue.dispatchThread != null && eventQueue.dispatchThread.getEventQueue() == this) {
        paramEventQueue.dispatchThread = eventQueue.dispatchThread;
        eventQueue.dispatchThread.setEventQueue(paramEventQueue);
      } 
      while (eventQueue.peekEvent() != null) {
        try {
          paramEventQueue.postEventPrivate(eventQueue.getNextEventPrivate());
        } catch (InterruptedException interruptedException) {
          if (getEventLog().isLoggable(PlatformLogger.Level.FINE))
            getEventLog().fine("Interrupted push", interruptedException); 
        } 
      } 
      if (eventQueue.dispatchThread != null)
        eventQueue.postEventPrivate(new InvocationEvent(eventQueue, dummyRunnable)); 
      paramEventQueue.previousQueue = eventQueue;
      eventQueue.nextQueue = paramEventQueue;
      if (this.appContext.get(AppContext.EVENT_QUEUE_KEY) == eventQueue)
        this.appContext.put(AppContext.EVENT_QUEUE_KEY, paramEventQueue); 
      this.pushPopCond.signalAll();
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  protected void pop() {
    if (getEventLog().isLoggable(PlatformLogger.Level.FINE))
      getEventLog().fine("EventQueue.pop(" + this + ")"); 
    this.pushPopLock.lock();
    try {
      EventQueue eventQueue1;
      for (eventQueue1 = this; eventQueue1.nextQueue != null; eventQueue1 = eventQueue1.nextQueue);
      EventQueue eventQueue2 = eventQueue1.previousQueue;
      if (eventQueue2 == null)
        throw new EmptyStackException(); 
      eventQueue1.previousQueue = null;
      eventQueue2.nextQueue = null;
      while (eventQueue1.peekEvent() != null) {
        try {
          eventQueue2.postEventPrivate(eventQueue1.getNextEventPrivate());
        } catch (InterruptedException interruptedException) {
          if (getEventLog().isLoggable(PlatformLogger.Level.FINE))
            getEventLog().fine("Interrupted pop", interruptedException); 
        } 
      } 
      if (eventQueue1.dispatchThread != null && eventQueue1.dispatchThread.getEventQueue() == this) {
        eventQueue2.dispatchThread = eventQueue1.dispatchThread;
        eventQueue1.dispatchThread.setEventQueue(eventQueue2);
      } 
      if (this.appContext.get(AppContext.EVENT_QUEUE_KEY) == this)
        this.appContext.put(AppContext.EVENT_QUEUE_KEY, eventQueue2); 
      eventQueue1.postEventPrivate(new InvocationEvent(eventQueue1, dummyRunnable));
      this.pushPopCond.signalAll();
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  public SecondaryLoop createSecondaryLoop() { return createSecondaryLoop(null, null, 0L); }
  
  SecondaryLoop createSecondaryLoop(Conditional paramConditional, EventFilter paramEventFilter, long paramLong) {
    this.pushPopLock.lock();
    try {
      if (this.nextQueue != null)
        return this.nextQueue.createSecondaryLoop(paramConditional, paramEventFilter, paramLong); 
      if (this.fwDispatcher != null)
        return new FwSecondaryLoopWrapper(this.fwDispatcher.createSecondaryLoop(), paramEventFilter); 
      if (this.dispatchThread == null)
        initDispatchThread(); 
      return new WaitDispatchSupport(this.dispatchThread, paramConditional, paramEventFilter, paramLong);
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  public static boolean isDispatchThread() {
    EventQueue eventQueue = Toolkit.getEventQueue();
    return eventQueue.isDispatchThreadImpl();
  }
  
  final boolean isDispatchThreadImpl() {
    EventQueue eventQueue = this;
    this.pushPopLock.lock();
    try {
      for (EventQueue eventQueue1 = eventQueue.nextQueue; eventQueue1 != null; eventQueue1 = eventQueue.nextQueue)
        eventQueue = eventQueue1; 
      if (eventQueue.fwDispatcher != null)
        return eventQueue.fwDispatcher.isDispatchThread(); 
      return (Thread.currentThread() == eventQueue.dispatchThread);
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  final void initDispatchThread() {
    this.pushPopLock.lock();
    try {
      if (this.dispatchThread == null && !this.threadGroup.isDestroyed() && !this.appContext.isDisposed()) {
        this.dispatchThread = (EventDispatchThread)AccessController.doPrivileged(new PrivilegedAction<EventDispatchThread>() {
              public EventDispatchThread run() {
                EventDispatchThread eventDispatchThread = new EventDispatchThread(EventQueue.this.threadGroup, EventQueue.this.name, EventQueue.this);
                eventDispatchThread.setContextClassLoader(EventQueue.this.classLoader);
                eventDispatchThread.setPriority(6);
                eventDispatchThread.setDaemon(false);
                AWTAutoShutdown.getInstance().notifyThreadBusy(eventDispatchThread);
                return eventDispatchThread;
              }
            });
        this.dispatchThread.start();
      } 
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  final void detachDispatchThread(EventDispatchThread paramEventDispatchThread) {
    SunToolkit.flushPendingEvents(this.appContext);
    this.pushPopLock.lock();
    try {
      if (paramEventDispatchThread == this.dispatchThread)
        this.dispatchThread = null; 
      AWTAutoShutdown.getInstance().notifyThreadFree(paramEventDispatchThread);
      if (peekEvent() != null)
        initDispatchThread(); 
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  final EventDispatchThread getDispatchThread() {
    this.pushPopLock.lock();
    try {
      return this.dispatchThread;
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  final void removeSourceEvents(Object paramObject, boolean paramBoolean) {
    SunToolkit.flushPendingEvents(this.appContext);
    this.pushPopLock.lock();
    try {
      for (byte b = 0; b < 4; b++) {
        EventQueueItem eventQueueItem1 = (this.queues[b]).head;
        EventQueueItem eventQueueItem2 = null;
        while (eventQueueItem1 != null) {
          if (eventQueueItem1.event.getSource() == paramObject && (paramBoolean || (!(eventQueueItem1.event instanceof SequencedEvent) && !(eventQueueItem1.event instanceof SentEvent) && !(eventQueueItem1.event instanceof java.awt.event.FocusEvent) && !(eventQueueItem1.event instanceof java.awt.event.WindowEvent) && !(eventQueueItem1.event instanceof java.awt.event.KeyEvent) && !(eventQueueItem1.event instanceof InputMethodEvent)))) {
            if (eventQueueItem1.event instanceof SequencedEvent)
              ((SequencedEvent)eventQueueItem1.event).dispose(); 
            if (eventQueueItem1.event instanceof SentEvent)
              ((SentEvent)eventQueueItem1.event).dispose(); 
            if (eventQueueItem1.event instanceof InvocationEvent)
              AWTAccessor.getInvocationEventAccessor().dispose((InvocationEvent)eventQueueItem1.event); 
            if (eventQueueItem2 == null) {
              (this.queues[b]).head = eventQueueItem1.next;
            } else {
              eventQueueItem2.next = eventQueueItem1.next;
            } 
            uncacheEQItem(eventQueueItem1);
          } else {
            eventQueueItem2 = eventQueueItem1;
          } 
          eventQueueItem1 = eventQueueItem1.next;
        } 
        (this.queues[b]).tail = eventQueueItem2;
      } 
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  long getMostRecentKeyEventTime() {
    this.pushPopLock.lock();
    try {
      return this.mostRecentKeyEventTime;
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  static void setCurrentEventAndMostRecentTime(AWTEvent paramAWTEvent) { Toolkit.getEventQueue().setCurrentEventAndMostRecentTimeImpl(paramAWTEvent); }
  
  private void setCurrentEventAndMostRecentTimeImpl(AWTEvent paramAWTEvent) {
    this.pushPopLock.lock();
    try {
      if (Thread.currentThread() != this.dispatchThread)
        return; 
      this.currentEvent = new WeakReference(paramAWTEvent);
      long l = Float.MIN_VALUE;
      if (paramAWTEvent instanceof InputEvent) {
        InputEvent inputEvent = (InputEvent)paramAWTEvent;
        l = inputEvent.getWhen();
        if (paramAWTEvent instanceof java.awt.event.KeyEvent)
          this.mostRecentKeyEventTime = inputEvent.getWhen(); 
      } else if (paramAWTEvent instanceof InputMethodEvent) {
        InputMethodEvent inputMethodEvent = (InputMethodEvent)paramAWTEvent;
        l = inputMethodEvent.getWhen();
      } else if (paramAWTEvent instanceof ActionEvent) {
        ActionEvent actionEvent = (ActionEvent)paramAWTEvent;
        l = actionEvent.getWhen();
      } else if (paramAWTEvent instanceof InvocationEvent) {
        InvocationEvent invocationEvent = (InvocationEvent)paramAWTEvent;
        l = invocationEvent.getWhen();
      } 
      this.mostRecentEventTime = Math.max(this.mostRecentEventTime, l);
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  public static void invokeLater(Runnable paramRunnable) { Toolkit.getEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), paramRunnable)); }
  
  public static void invokeAndWait(Runnable paramRunnable) { invokeAndWait(Toolkit.getDefaultToolkit(), paramRunnable); }
  
  static void invokeAndWait(Object paramObject, Runnable paramRunnable) throws InterruptedException, InvocationTargetException {
    if (isDispatchThread())
      throw new Error("Cannot call invokeAndWait from the event dispatcher thread"); 
    class AWTInvocationLock {};
    AWTInvocationLock aWTInvocationLock = new AWTInvocationLock();
    InvocationEvent invocationEvent = new InvocationEvent(paramObject, paramRunnable, aWTInvocationLock, true);
    synchronized (aWTInvocationLock) {
      Toolkit.getEventQueue().postEvent(invocationEvent);
      while (!invocationEvent.isDispatched())
        aWTInvocationLock.wait(); 
    } 
    Throwable throwable = invocationEvent.getThrowable();
    if (throwable != null)
      throw new InvocationTargetException(throwable); 
  }
  
  private void wakeup(boolean paramBoolean) {
    this.pushPopLock.lock();
    try {
      if (this.nextQueue != null) {
        this.nextQueue.wakeup(paramBoolean);
      } else if (this.dispatchThread != null) {
        this.pushPopCond.signalAll();
      } else if (!paramBoolean) {
        initDispatchThread();
      } 
    } finally {
      this.pushPopLock.unlock();
    } 
  }
  
  private void setFwDispatcher(FwDispatcher paramFwDispatcher) {
    if (this.nextQueue != null) {
      this.nextQueue.setFwDispatcher(paramFwDispatcher);
    } else {
      this.fwDispatcher = paramFwDispatcher;
    } 
  }
  
  static  {
    AWTAccessor.setEventQueueAccessor(new AWTAccessor.EventQueueAccessor() {
          public Thread getDispatchThread(EventQueue param1EventQueue) { return param1EventQueue.getDispatchThread(); }
          
          public boolean isDispatchThreadImpl(EventQueue param1EventQueue) { return param1EventQueue.isDispatchThreadImpl(); }
          
          public void removeSourceEvents(EventQueue param1EventQueue, Object param1Object, boolean param1Boolean) { param1EventQueue.removeSourceEvents(param1Object, param1Boolean); }
          
          public boolean noEvents(EventQueue param1EventQueue) { return param1EventQueue.noEvents(); }
          
          public void wakeup(EventQueue param1EventQueue, boolean param1Boolean) { param1EventQueue.wakeup(param1Boolean); }
          
          public void invokeAndWait(Object param1Object, Runnable param1Runnable) throws InterruptedException, InvocationTargetException { EventQueue.invokeAndWait(param1Object, param1Runnable); }
          
          public void setFwDispatcher(EventQueue param1EventQueue, FwDispatcher param1FwDispatcher) { param1EventQueue.setFwDispatcher(param1FwDispatcher); }
          
          public long getMostRecentEventTime(EventQueue param1EventQueue) { return param1EventQueue.getMostRecentEventTimeImpl(); }
        });
    javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
  }
  
  private class FwSecondaryLoopWrapper implements SecondaryLoop {
    private final SecondaryLoop loop;
    
    private final EventFilter filter;
    
    public FwSecondaryLoopWrapper(SecondaryLoop param1SecondaryLoop, EventFilter param1EventFilter) {
      this.loop = param1SecondaryLoop;
      this.filter = param1EventFilter;
    }
    
    public boolean enter() {
      if (this.filter != null)
        EventQueue.this.dispatchThread.addEventFilter(this.filter); 
      return this.loop.enter();
    }
    
    public boolean exit() {
      if (this.filter != null)
        EventQueue.this.dispatchThread.removeEventFilter(this.filter); 
      return this.loop.exit();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\EventQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */