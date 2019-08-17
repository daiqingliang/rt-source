package java.awt.event;

import java.awt.Component;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public class FocusEvent extends ComponentEvent {
  public static final int FOCUS_FIRST = 1004;
  
  public static final int FOCUS_LAST = 1005;
  
  public static final int FOCUS_GAINED = 1004;
  
  public static final int FOCUS_LOST = 1005;
  
  boolean temporary;
  
  Component opposite;
  
  private static final long serialVersionUID = 523753786457416396L;
  
  public FocusEvent(Component paramComponent1, int paramInt, boolean paramBoolean, Component paramComponent2) {
    super(paramComponent1, paramInt);
    this.temporary = paramBoolean;
    this.opposite = paramComponent2;
  }
  
  public FocusEvent(Component paramComponent, int paramInt, boolean paramBoolean) { this(paramComponent, paramInt, paramBoolean, null); }
  
  public FocusEvent(Component paramComponent, int paramInt) { this(paramComponent, paramInt, false); }
  
  public boolean isTemporary() { return this.temporary; }
  
  public Component getOppositeComponent() { return (this.opposite == null) ? null : ((SunToolkit.targetToAppContext(this.opposite) == AppContext.getAppContext()) ? this.opposite : null); }
  
  public String paramString() {
    String str;
    switch (this.id) {
      case 1004:
        str = "FOCUS_GAINED";
        break;
      case 1005:
        str = "FOCUS_LOST";
        break;
      default:
        str = "unknown type";
        break;
    } 
    return str + (this.temporary ? ",temporary" : ",permanent") + ",opposite=" + getOppositeComponent();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\FocusEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */