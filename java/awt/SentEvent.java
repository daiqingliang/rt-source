package java.awt;

import sun.awt.AppContext;
import sun.awt.SunToolkit;

class SentEvent extends AWTEvent implements ActiveEvent {
  private static final long serialVersionUID = -383615247028828931L;
  
  static final int ID = 1007;
  
  boolean dispatched;
  
  private AWTEvent nested;
  
  private AppContext toNotify;
  
  SentEvent() { this(null); }
  
  SentEvent(AWTEvent paramAWTEvent) { this(paramAWTEvent, null); }
  
  SentEvent(AWTEvent paramAWTEvent, AppContext paramAppContext) {
    super((paramAWTEvent != null) ? paramAWTEvent.getSource() : Toolkit.getDefaultToolkit(), 1007);
    this.nested = paramAWTEvent;
    this.toNotify = paramAppContext;
  }
  
  public void dispatch() {
    try {
      if (this.nested != null)
        Toolkit.getEventQueue().dispatchEvent(this.nested); 
    } finally {
      this.dispatched = true;
      if (this.toNotify != null)
        SunToolkit.postEvent(this.toNotify, new SentEvent()); 
      synchronized (this) {
        notifyAll();
      } 
    } 
  }
  
  final void dispose() {
    this.dispatched = true;
    if (this.toNotify != null)
      SunToolkit.postEvent(this.toNotify, new SentEvent()); 
    synchronized (this) {
      notifyAll();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\SentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */