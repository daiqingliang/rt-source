package java.awt.event;

import java.awt.Component;
import java.awt.Rectangle;

public class PaintEvent extends ComponentEvent {
  public static final int PAINT_FIRST = 800;
  
  public static final int PAINT_LAST = 801;
  
  public static final int PAINT = 800;
  
  public static final int UPDATE = 801;
  
  Rectangle updateRect;
  
  private static final long serialVersionUID = 1267492026433337593L;
  
  public PaintEvent(Component paramComponent, int paramInt, Rectangle paramRectangle) {
    super(paramComponent, paramInt);
    this.updateRect = paramRectangle;
  }
  
  public Rectangle getUpdateRect() { return this.updateRect; }
  
  public void setUpdateRect(Rectangle paramRectangle) { this.updateRect = paramRectangle; }
  
  public String paramString() {
    String str;
    switch (this.id) {
      case 800:
        str = "PAINT";
        break;
      case 801:
        str = "UPDATE";
        break;
      default:
        str = "unknown type";
        break;
    } 
    return str + ",updateRect=" + ((this.updateRect != null) ? this.updateRect.toString() : "null");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\PaintEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */