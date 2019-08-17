package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class CompoundBorder extends AbstractBorder {
  protected Border outsideBorder = null;
  
  protected Border insideBorder = null;
  
  public CompoundBorder() {}
  
  @ConstructorProperties({"outsideBorder", "insideBorder"})
  public CompoundBorder(Border paramBorder1, Border paramBorder2) {}
  
  public boolean isBorderOpaque() { return ((this.outsideBorder == null || this.outsideBorder.isBorderOpaque()) && (this.insideBorder == null || this.insideBorder.isBorderOpaque())); }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1;
    int j = paramInt2;
    int k = paramInt3;
    int m = paramInt4;
    if (this.outsideBorder != null) {
      this.outsideBorder.paintBorder(paramComponent, paramGraphics, i, j, k, m);
      Insets insets = this.outsideBorder.getBorderInsets(paramComponent);
      i += insets.left;
      j += insets.top;
      k = k - insets.right - insets.left;
      m = m - insets.bottom - insets.top;
    } 
    if (this.insideBorder != null)
      this.insideBorder.paintBorder(paramComponent, paramGraphics, i, j, k, m); 
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    paramInsets.top = paramInsets.left = paramInsets.right = paramInsets.bottom = 0;
    if (this.outsideBorder != null) {
      Insets insets = this.outsideBorder.getBorderInsets(paramComponent);
      paramInsets.top += insets.top;
      paramInsets.left += insets.left;
      paramInsets.right += insets.right;
      paramInsets.bottom += insets.bottom;
    } 
    if (this.insideBorder != null) {
      Insets insets = this.insideBorder.getBorderInsets(paramComponent);
      paramInsets.top += insets.top;
      paramInsets.left += insets.left;
      paramInsets.right += insets.right;
      paramInsets.bottom += insets.bottom;
    } 
    return paramInsets;
  }
  
  public Border getOutsideBorder() { return this.outsideBorder; }
  
  public Border getInsideBorder() { return this.insideBorder; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\CompoundBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */