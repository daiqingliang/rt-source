package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class SoftBevelBorder extends BevelBorder {
  public SoftBevelBorder(int paramInt) { super(paramInt); }
  
  public SoftBevelBorder(int paramInt, Color paramColor1, Color paramColor2) { super(paramInt, paramColor1, paramColor2); }
  
  @ConstructorProperties({"bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor"})
  public SoftBevelBorder(int paramInt, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4) { super(paramInt, paramColor1, paramColor2, paramColor3, paramColor4); }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Color color = paramGraphics.getColor();
    paramGraphics.translate(paramInt1, paramInt2);
    if (this.bevelType == 0) {
      paramGraphics.setColor(getHighlightOuterColor(paramComponent));
      paramGraphics.drawLine(0, 0, paramInt3 - 2, 0);
      paramGraphics.drawLine(0, 0, 0, paramInt4 - 2);
      paramGraphics.drawLine(1, 1, 1, 1);
      paramGraphics.setColor(getHighlightInnerColor(paramComponent));
      paramGraphics.drawLine(2, 1, paramInt3 - 2, 1);
      paramGraphics.drawLine(1, 2, 1, paramInt4 - 2);
      paramGraphics.drawLine(2, 2, 2, 2);
      paramGraphics.drawLine(0, paramInt4 - 1, 0, paramInt4 - 2);
      paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 1, 0);
      paramGraphics.setColor(getShadowOuterColor(paramComponent));
      paramGraphics.drawLine(2, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.drawLine(paramInt3 - 1, 2, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.setColor(getShadowInnerColor(paramComponent));
      paramGraphics.drawLine(paramInt3 - 2, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
    } else if (this.bevelType == 1) {
      paramGraphics.setColor(getShadowOuterColor(paramComponent));
      paramGraphics.drawLine(0, 0, paramInt3 - 2, 0);
      paramGraphics.drawLine(0, 0, 0, paramInt4 - 2);
      paramGraphics.drawLine(1, 1, 1, 1);
      paramGraphics.setColor(getShadowInnerColor(paramComponent));
      paramGraphics.drawLine(2, 1, paramInt3 - 2, 1);
      paramGraphics.drawLine(1, 2, 1, paramInt4 - 2);
      paramGraphics.drawLine(2, 2, 2, 2);
      paramGraphics.drawLine(0, paramInt4 - 1, 0, paramInt4 - 2);
      paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 1, 0);
      paramGraphics.setColor(getHighlightOuterColor(paramComponent));
      paramGraphics.drawLine(2, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.drawLine(paramInt3 - 1, 2, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.setColor(getHighlightInnerColor(paramComponent));
      paramGraphics.drawLine(paramInt3 - 2, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
    } 
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(color);
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    paramInsets.set(3, 3, 3, 3);
    return paramInsets;
  }
  
  public boolean isBorderOpaque() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\SoftBevelBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */