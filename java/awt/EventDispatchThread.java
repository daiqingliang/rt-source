package java.awt;

import java.util.ArrayList;
import sun.awt.EventQueueDelegate;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDragSourceContextPeer;
import sun.util.logging.PlatformLogger;

class EventDispatchThread extends Thread {
  private static final PlatformLogger eventLog = PlatformLogger.getLogger("java.awt.event.EventDispatchThread");
  
  private EventQueue theQueue;
  
  private static final int ANY_EVENT = -1;
  
  private ArrayList<EventFilter> eventFilters = new ArrayList();
  
  EventDispatchThread(ThreadGroup paramThreadGroup, String paramString, EventQueue paramEventQueue) {
    super(paramThreadGroup, paramString);
    setEventQueue(paramEventQueue);
  }
  
  public void stopDispatching() { this.doDispatch = false; }
  
  public void run() {
    try {
      pumpEvents(new Conditional() {
            public boolean evaluate() { return true; }
          });
    } finally {
      getEventQueue().detachDispatchThread(this);
    } 
  }
  
  void pumpEvents(Conditional paramConditional) { pumpEvents(-1, paramConditional); }
  
  void pumpEventsForHierarchy(Conditional paramConditional, Component paramComponent) { pumpEventsForHierarchy(-1, paramConditional, paramComponent); }
  
  void pumpEvents(int paramInt, Conditional paramConditional) { pumpEventsForHierarchy(paramInt, paramConditional, null); }
  
  void pumpEventsForHierarchy(int paramInt, Conditional paramConditional, Component paramComponent) { pumpEventsForFilter(paramInt, paramConditional, new HierarchyEventFilter(paramComponent)); }
  
  void pumpEventsForFilter(Conditional paramConditional, EventFilter paramEventFilter) { pumpEventsForFilter(-1, paramConditional, paramEventFilter); }
  
  void pumpEventsForFilter(int paramInt, Conditional paramConditional, EventFilter paramEventFilter) {
    addEventFilter(paramEventFilter);
    this.doDispatch = true;
    while (this.doDispatch && !isInterrupted() && paramConditional.evaluate())
      pumpOneEventForFilters(paramInt); 
    removeEventFilter(paramEventFilter);
  }
  
  void addEventFilter(EventFilter paramEventFilter) {
    if (eventLog.isLoggable(PlatformLogger.Level.FINEST))
      eventLog.finest("adding the event filter: " + paramEventFilter); 
    synchronized (this.eventFilters) {
      if (!this.eventFilters.contains(paramEventFilter))
        if (paramEventFilter instanceof ModalEventFilter) {
          ModalEventFilter modalEventFilter = (ModalEventFilter)paramEventFilter;
          byte b = 0;
          for (b = 0; b < this.eventFilters.size(); b++) {
            EventFilter eventFilter = (EventFilter)this.eventFilters.get(b);
            if (eventFilter instanceof ModalEventFilter) {
              ModalEventFilter modalEventFilter1 = (ModalEventFilter)eventFilter;
              if (modalEventFilter1.compareTo(modalEventFilter) > 0)
                break; 
            } 
          } 
          this.eventFilters.add(b, paramEventFilter);
        } else {
          this.eventFilters.add(paramEventFilter);
        }  
    } 
  }
  
  void removeEventFilter(EventFilter paramEventFilter) {
    if (eventLog.isLoggable(PlatformLogger.Level.FINEST))
      eventLog.finest("removing the event filter: " + paramEventFilter); 
    synchronized (this.eventFilters) {
      this.eventFilters.remove(paramEventFilter);
    } 
  }
  
  boolean filterAndCheckEvent(AWTEvent paramAWTEvent) {
    boolean bool = true;
    synchronized (this.eventFilters) {
      for (int i = this.eventFilters.size() - 1; i >= 0; i--) {
        EventFilter eventFilter = (EventFilter)this.eventFilters.get(i);
        EventFilter.FilterAction filterAction = eventFilter.acceptEvent(paramAWTEvent);
        if (filterAction == EventFilter.FilterAction.REJECT) {
          bool = false;
          break;
        } 
        if (filterAction == EventFilter.FilterAction.ACCEPT_IMMEDIATELY)
          break; 
      } 
    } 
    return (bool && SunDragSourceContextPeer.checkEvent(paramAWTEvent));
  }
  
  void pumpOneEventForFilters(int paramInt) {
    AWTEvent aWTEvent = null;
    boolean bool = false;
    try {
      EventQueue eventQueue = null;
      EventQueueDelegate.Delegate delegate = null;
      do {
        eventQueue = getEventQueue();
        delegate = EventQueueDelegate.getDelegate();
        if (delegate != null && paramInt == -1) {
          aWTEvent = delegate.getNextEvent(eventQueue);
        } else {
          aWTEvent = (paramInt == -1) ? eventQueue.getNextEvent() : eventQueue.getNextEvent(paramInt);
        } 
        bool = filterAndCheckEvent(aWTEvent);
        if (bool)
          continue; 
        aWTEvent.consume();
      } while (!bool);
      if (eventLog.isLoggable(PlatformLogger.Level.FINEST))
        eventLog.finest("Dispatching: " + aWTEvent); 
      Object object = null;
      if (delegate != null)
        object = delegate.beforeDispatch(aWTEvent); 
      eventQueue.dispatchEvent(aWTEvent);
      if (delegate != null)
        delegate.afterDispatch(aWTEvent, object); 
    } catch (ThreadDeath threadDeath) {
      this.doDispatch = false;
      throw threadDeath;
    } catch (InterruptedException interruptedException) {
      this.doDispatch = false;
    } catch (Throwable throwable) {
      processException(throwable);
    } 
  }
  
  private void processException(Throwable paramThrowable) {
    if (eventLog.isLoggable(PlatformLogger.Level.FINE))
      eventLog.fine("Processing exception: " + paramThrowable); 
    getUncaughtExceptionHandler().uncaughtException(this, paramThrowable);
  }
  
  public EventQueue getEventQueue() { return this.theQueue; }
  
  public void setEventQueue(EventQueue paramEventQueue) { this.theQueue = paramEventQueue; }
  
  private static class HierarchyEventFilter implements EventFilter {
    private Component modalComponent;
    
    public HierarchyEventFilter(Component param1Component) { this.modalComponent = param1Component; }
    
    public EventFilter.FilterAction acceptEvent(AWTEvent param1AWTEvent) {
      if (this.modalComponent != null) {
        int i = param1AWTEvent.getID();
        boolean bool1 = (i >= 500 && i <= 507) ? 1 : 0;
        boolean bool2 = (i >= 1001 && i <= 1001) ? 1 : 0;
        boolean bool3 = (i == 201) ? 1 : 0;
        if (Component.isInstanceOf(this.modalComponent, "javax.swing.JInternalFrame"))
          return bool3 ? EventFilter.FilterAction.REJECT : EventFilter.FilterAction.ACCEPT; 
        if (bool1 || bool2 || bool3) {
          Object object = param1AWTEvent.getSource();
          if (object instanceof sun.awt.ModalExclude)
            return EventFilter.FilterAction.ACCEPT; 
          if (object instanceof Component) {
            Component component = (Component)object;
            boolean bool = false;
            if (this.modalComponent instanceof Container)
              while (component != this.modalComponent && component != null) {
                if (component instanceof Window && SunToolkit.isModalExcluded((Window)component)) {
                  bool = true;
                  break;
                } 
                component = component.getParent();
              }  
            if (!bool && component != this.modalComponent)
              return EventFilter.FilterAction.REJECT; 
          } 
        } 
      } 
      return EventFilter.FilterAction.ACCEPT;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\EventDispatchThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */