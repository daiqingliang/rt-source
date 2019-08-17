package sun.applet;

import java.util.EventObject;

public class AppletEvent extends EventObject {
  private Object arg;
  
  private int id;
  
  public AppletEvent(Object paramObject1, int paramInt, Object paramObject2) {
    super(paramObject1);
    this.arg = paramObject2;
    this.id = paramInt;
  }
  
  public int getID() { return this.id; }
  
  public Object getArgument() { return this.arg; }
  
  public String toString() {
    null = getClass().getName() + "[source=" + this.source + " + id=" + this.id;
    if (this.arg != null)
      null = null + " + arg=" + this.arg; 
    return null + " ]";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */