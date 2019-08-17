package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;

public class EventQueueDelegate {
  private static final Object EVENT_QUEUE_DELEGATE_KEY = new StringBuilder("EventQueueDelegate.Delegate");
  
  public static void setDelegate(Delegate paramDelegate) { AppContext.getAppContext().put(EVENT_QUEUE_DELEGATE_KEY, paramDelegate); }
  
  public static Delegate getDelegate() { return (Delegate)AppContext.getAppContext().get(EVENT_QUEUE_DELEGATE_KEY); }
  
  public static interface Delegate {
    AWTEvent getNextEvent(EventQueue param1EventQueue) throws InterruptedException;
    
    Object beforeDispatch(AWTEvent param1AWTEvent) throws InterruptedException;
    
    void afterDispatch(AWTEvent param1AWTEvent, Object param1Object) throws InterruptedException;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\EventQueueDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */