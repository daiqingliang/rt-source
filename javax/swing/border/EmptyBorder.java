package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;
import java.io.Serializable;

public class EmptyBorder extends AbstractBorder implements Serializable {
  protected int left;
  
  protected int right;
  
  protected int top;
  
  protected int bottom;
  
  public EmptyBorder(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.top = paramInt1;
    this.right = paramInt4;
    this.bottom = paramInt3;
    this.left = paramInt2;
  }
  
  @ConstructorProperties({"borderInsets"})
  public EmptyBorder(Insets paramInsets) {
    this.top = paramInsets.top;
    this.right = paramInsets.right;
    this.bottom = paramInsets.bottom;
    this.left = paramInsets.left;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    paramInsets.left = this.left;
    paramInsets.top = this.top;
    paramInsets.right = this.right;
    paramInsets.bottom = this.bottom;
    return paramInsets;
  }
  
  public Insets getBorderInsets() { return new Insets(this.top, this.left, this.bottom, this.right); }
  
  public boolean isBorderOpaque() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\EmptyBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */