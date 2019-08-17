package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.Serializable;

public abstract class AbstractBorder implements Border, Serializable {
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public Insets getBorderInsets(Component paramComponent) { return getBorderInsets(paramComponent, new Insets(0, 0, 0, 0)); }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    paramInsets.left = paramInsets.top = paramInsets.right = paramInsets.bottom = 0;
    return paramInsets;
  }
  
  public boolean isBorderOpaque() { return false; }
  
  public Rectangle getInteriorRectangle(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return getInteriorRectangle(paramComponent, this, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public static Rectangle getInteriorRectangle(Component paramComponent, Border paramBorder, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Insets insets;
    if (paramBorder != null) {
      insets = paramBorder.getBorderInsets(paramComponent);
    } else {
      insets = new Insets(0, 0, 0, 0);
    } 
    return new Rectangle(paramInt1 + insets.left, paramInt2 + insets.top, paramInt3 - insets.right - insets.left, paramInt4 - insets.top - insets.bottom);
  }
  
  public int getBaseline(Component paramComponent, int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Width and height must be >= 0"); 
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(Component paramComponent) {
    if (paramComponent == null)
      throw new NullPointerException("Component must be non-null"); 
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  static boolean isLeftToRight(Component paramComponent) { return paramComponent.getComponentOrientation().isLeftToRight(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\AbstractBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */