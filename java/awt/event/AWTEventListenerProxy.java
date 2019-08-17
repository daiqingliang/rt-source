package java.awt.event;

import java.awt.AWTEvent;
import java.util.EventListenerProxy;

public class AWTEventListenerProxy extends EventListenerProxy<AWTEventListener> implements AWTEventListener {
  private final long eventMask;
  
  public AWTEventListenerProxy(long paramLong, AWTEventListener paramAWTEventListener) {
    super(paramAWTEventListener);
    this.eventMask = paramLong;
  }
  
  public void eventDispatched(AWTEvent paramAWTEvent) { ((AWTEventListener)getListener()).eventDispatched(paramAWTEvent); }
  
  public long getEventMask() { return this.eventMask; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\AWTEventListenerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */