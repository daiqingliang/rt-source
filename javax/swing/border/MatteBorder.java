package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;

public class MatteBorder extends EmptyBorder {
  protected Color color;
  
  protected Icon tileIcon;
  
  public MatteBorder(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.color = paramColor;
  }
  
  public MatteBorder(Insets paramInsets, Color paramColor) {
    super(paramInsets);
    this.color = paramColor;
  }
  
  public MatteBorder(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Icon paramIcon) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.tileIcon = paramIcon;
  }
  
  public MatteBorder(Insets paramInsets, Icon paramIcon) {
    super(paramInsets);
    this.tileIcon = paramIcon;
  }
  
  public MatteBorder(Icon paramIcon) { this(-1, -1, -1, -1, paramIcon); }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Insets insets = getBorderInsets(paramComponent);
    Color color1 = paramGraphics.getColor();
    paramGraphics.translate(paramInt1, paramInt2);
    if (this.tileIcon != null)
      this.color = (this.tileIcon.getIconWidth() == -1) ? Color.gray : null; 
    if (this.color != null) {
      paramGraphics.setColor(this.color);
      paramGraphics.fillRect(0, 0, paramInt3 - insets.right, insets.top);
      paramGraphics.fillRect(0, insets.top, insets.left, paramInt4 - insets.top);
      paramGraphics.fillRect(insets.left, paramInt4 - insets.bottom, paramInt3 - insets.left, insets.bottom);
      paramGraphics.fillRect(paramInt3 - insets.right, 0, insets.right, paramInt4 - insets.bottom);
    } else if (this.tileIcon != null) {
      int i = this.tileIcon.getIconWidth();
      int j = this.tileIcon.getIconHeight();
      paintEdge(paramComponent, paramGraphics, 0, 0, paramInt3 - insets.right, insets.top, i, j);
      paintEdge(paramComponent, paramGraphics, 0, insets.top, insets.left, paramInt4 - insets.top, i, j);
      paintEdge(paramComponent, paramGraphics, insets.left, paramInt4 - insets.bottom, paramInt3 - insets.left, insets.bottom, i, j);
      paintEdge(paramComponent, paramGraphics, paramInt3 - insets.right, 0, insets.right, paramInt4 - insets.bottom, i, j);
    } 
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(color1);
  }
  
  private void paintEdge(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    paramGraphics = paramGraphics.create(paramInt1, paramInt2, paramInt3, paramInt4);
    int i = -(paramInt2 % paramInt6);
    for (paramInt1 = -(paramInt1 % paramInt5); paramInt1 < paramInt3; paramInt1 += paramInt5) {
      for (paramInt2 = i; paramInt2 < paramInt4; paramInt2 += paramInt6)
        this.tileIcon.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2); 
    } 
    paramGraphics.dispose();
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) { return computeInsets(paramInsets); }
  
  public Insets getBorderInsets() { return computeInsets(new Insets(0, 0, 0, 0)); }
  
  private Insets computeInsets(Insets paramInsets) {
    if (this.tileIcon != null && this.top == -1 && this.bottom == -1 && this.left == -1 && this.right == -1) {
      int i = this.tileIcon.getIconWidth();
      int j = this.tileIcon.getIconHeight();
      paramInsets.top = j;
      paramInsets.right = i;
      paramInsets.bottom = j;
      paramInsets.left = i;
    } else {
      paramInsets.left = this.left;
      paramInsets.top = this.top;
      paramInsets.right = this.right;
      paramInsets.bottom = this.bottom;
    } 
    return paramInsets;
  }
  
  public Color getMatteColor() { return this.color; }
  
  public Icon getTileIcon() { return this.tileIcon; }
  
  public boolean isBorderOpaque() { return (this.color != null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\MatteBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */