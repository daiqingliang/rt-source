package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.ConstructorProperties;

public class LineBorder extends AbstractBorder {
  private static Border blackLine;
  
  private static Border grayLine;
  
  protected int thickness;
  
  protected Color lineColor;
  
  protected boolean roundedCorners;
  
  public static Border createBlackLineBorder() {
    if (blackLine == null)
      blackLine = new LineBorder(Color.black, 1); 
    return blackLine;
  }
  
  public static Border createGrayLineBorder() {
    if (grayLine == null)
      grayLine = new LineBorder(Color.gray, 1); 
    return grayLine;
  }
  
  public LineBorder(Color paramColor) { this(paramColor, 1, false); }
  
  public LineBorder(Color paramColor, int paramInt) { this(paramColor, paramInt, false); }
  
  @ConstructorProperties({"lineColor", "thickness", "roundedCorners"})
  public LineBorder(Color paramColor, int paramInt, boolean paramBoolean) {
    this.lineColor = paramColor;
    this.thickness = paramInt;
    this.roundedCorners = paramBoolean;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.thickness > 0 && paramGraphics instanceof Graphics2D) {
      Rectangle2D.Float float2;
      Rectangle2D.Float float1;
      Graphics2D graphics2D = (Graphics2D)paramGraphics;
      Color color = graphics2D.getColor();
      graphics2D.setColor(this.lineColor);
      int i = this.thickness;
      int j = i + i;
      if (this.roundedCorners) {
        float f = 0.2F * i;
        float1 = new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, i, i);
        float2 = new RoundRectangle2D.Float((paramInt1 + i), (paramInt2 + i), (paramInt3 - j), (paramInt4 - j), f, f);
      } else {
        float1 = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
        float2 = new Rectangle2D.Float((paramInt1 + i), (paramInt2 + i), (paramInt3 - j), (paramInt4 - j));
      } 
      Path2D.Float float = new Path2D.Float(0);
      float.append(float1, false);
      float.append(float2, false);
      graphics2D.fill(float);
      graphics2D.setColor(color);
    } 
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    paramInsets.set(this.thickness, this.thickness, this.thickness, this.thickness);
    return paramInsets;
  }
  
  public Color getLineColor() { return this.lineColor; }
  
  public int getThickness() { return this.thickness; }
  
  public boolean getRoundedCorners() { return this.roundedCorners; }
  
  public boolean isBorderOpaque() { return !this.roundedCorners; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\LineBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */