package java.awt.event;

import java.awt.Window;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public class WindowEvent extends ComponentEvent {
  public static final int WINDOW_FIRST = 200;
  
  public static final int WINDOW_OPENED = 200;
  
  public static final int WINDOW_CLOSING = 201;
  
  public static final int WINDOW_CLOSED = 202;
  
  public static final int WINDOW_ICONIFIED = 203;
  
  public static final int WINDOW_DEICONIFIED = 204;
  
  public static final int WINDOW_ACTIVATED = 205;
  
  public static final int WINDOW_DEACTIVATED = 206;
  
  public static final int WINDOW_GAINED_FOCUS = 207;
  
  public static final int WINDOW_LOST_FOCUS = 208;
  
  public static final int WINDOW_STATE_CHANGED = 209;
  
  public static final int WINDOW_LAST = 209;
  
  Window opposite;
  
  int oldState;
  
  int newState;
  
  private static final long serialVersionUID = -1567959133147912127L;
  
  public WindowEvent(Window paramWindow1, int paramInt1, Window paramWindow2, int paramInt2, int paramInt3) {
    super(paramWindow1, paramInt1);
    this.opposite = paramWindow2;
    this.oldState = paramInt2;
    this.newState = paramInt3;
  }
  
  public WindowEvent(Window paramWindow1, int paramInt, Window paramWindow2) { this(paramWindow1, paramInt, paramWindow2, 0, 0); }
  
  public WindowEvent(Window paramWindow, int paramInt1, int paramInt2, int paramInt3) { this(paramWindow, paramInt1, null, paramInt2, paramInt3); }
  
  public WindowEvent(Window paramWindow, int paramInt) { this(paramWindow, paramInt, null, 0, 0); }
  
  public Window getWindow() { return (this.source instanceof Window) ? (Window)this.source : null; }
  
  public Window getOppositeWindow() { return (this.opposite == null) ? null : ((SunToolkit.targetToAppContext(this.opposite) == AppContext.getAppContext()) ? this.opposite : null); }
  
  public int getOldState() { return this.oldState; }
  
  public int getNewState() { return this.newState; }
  
  public String paramString() {
    switch (this.id) {
      case 200:
        null = "WINDOW_OPENED";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      case 201:
        null = "WINDOW_CLOSING";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      case 202:
        null = "WINDOW_CLOSED";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      case 203:
        null = "WINDOW_ICONIFIED";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      case 204:
        null = "WINDOW_DEICONIFIED";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      case 205:
        null = "WINDOW_ACTIVATED";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      case 206:
        null = "WINDOW_DEACTIVATED";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      case 207:
        null = "WINDOW_GAINED_FOCUS";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      case 208:
        null = "WINDOW_LOST_FOCUS";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
      case 209:
        null = "WINDOW_STATE_CHANGED";
        return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
    } 
    null = "unknown type";
    return null + ",opposite=" + getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\WindowEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */