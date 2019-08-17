package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class BevelBorder extends AbstractBorder {
  public static final int RAISED = 0;
  
  public static final int LOWERED = 1;
  
  protected int bevelType;
  
  protected Color highlightOuter;
  
  protected Color highlightInner;
  
  protected Color shadowInner;
  
  protected Color shadowOuter;
  
  public BevelBorder(int paramInt) { this.bevelType = paramInt; }
  
  public BevelBorder(int paramInt, Color paramColor1, Color paramColor2) { this(paramInt, paramColor1.brighter(), paramColor1, paramColor2, paramColor2.brighter()); }
  
  @ConstructorProperties({"bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor"})
  public BevelBorder(int paramInt, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4) {
    this(paramInt);
    this.highlightOuter = paramColor1;
    this.highlightInner = paramColor2;
    this.shadowOuter = paramColor3;
    this.shadowInner = paramColor4;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.bevelType == 0) {
      paintRaisedBevel(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    } else if (this.bevelType == 1) {
      paintLoweredBevel(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    paramInsets.set(2, 2, 2, 2);
    return paramInsets;
  }
  
  public Color getHighlightOuterColor(Component paramComponent) {
    Color color = getHighlightOuterColor();
    return (color != null) ? color : paramComponent.getBackground().brighter().brighter();
  }
  
  public Color getHighlightInnerColor(Component paramComponent) {
    Color color = getHighlightInnerColor();
    return (color != null) ? color : paramComponent.getBackground().brighter();
  }
  
  public Color getShadowInnerColor(Component paramComponent) {
    Color color = getShadowInnerColor();
    return (color != null) ? color : paramComponent.getBackground().darker();
  }
  
  public Color getShadowOuterColor(Component paramComponent) {
    Color color = getShadowOuterColor();
    return (color != null) ? color : paramComponent.getBackground().darker().darker();
  }
  
  public Color getHighlightOuterColor() { return this.highlightOuter; }
  
  public Color getHighlightInnerColor() { return this.highlightInner; }
  
  public Color getShadowInnerColor() { return this.shadowInner; }
  
  public Color getShadowOuterColor() { return this.shadowOuter; }
  
  public int getBevelType() { return this.bevelType; }
  
  public boolean isBorderOpaque() { return true; }
  
  protected void paintRaisedBevel(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Color color = paramGraphics.getColor();
    int i = paramInt4;
    int j = paramInt3;
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(getHighlightOuterColor(paramComponent));
    paramGraphics.drawLine(0, 0, 0, i - 2);
    paramGraphics.drawLine(1, 0, j - 2, 0);
    paramGraphics.setColor(getHighlightInnerColor(paramComponent));
    paramGraphics.drawLine(1, 1, 1, i - 3);
    paramGraphics.drawLine(2, 1, j - 3, 1);
    paramGraphics.setColor(getShadowOuterColor(paramComponent));
    paramGraphics.drawLine(0, i - 1, j - 1, i - 1);
    paramGraphics.drawLine(j - 1, 0, j - 1, i - 2);
    paramGraphics.setColor(getShadowInnerColor(paramComponent));
    paramGraphics.drawLine(1, i - 2, j - 2, i - 2);
    paramGraphics.drawLine(j - 2, 1, j - 2, i - 3);
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(color);
  }
  
  protected void paintLoweredBevel(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Color color = paramGraphics.getColor();
    int i = paramInt4;
    int j = paramInt3;
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(getShadowInnerColor(paramComponent));
    paramGraphics.drawLine(0, 0, 0, i - 1);
    paramGraphics.drawLine(1, 0, j - 1, 0);
    paramGraphics.setColor(getShadowOuterColor(paramComponent));
    paramGraphics.drawLine(1, 1, 1, i - 2);
    paramGraphics.drawLine(2, 1, j - 2, 1);
    paramGraphics.setColor(getHighlightOuterColor(paramComponent));
    paramGraphics.drawLine(1, i - 1, j - 1, i - 1);
    paramGraphics.drawLine(j - 1, 1, j - 1, i - 2);
    paramGraphics.setColor(getHighlightInnerColor(paramComponent));
    paramGraphics.drawLine(2, i - 2, j - 2, i - 2);
    paramGraphics.drawLine(j - 2, 2, j - 2, i - 3);
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(color);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\BevelBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */