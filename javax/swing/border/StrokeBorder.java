package javax.swing.border;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.beans.ConstructorProperties;

public class StrokeBorder extends AbstractBorder {
  private final BasicStroke stroke;
  
  private final Paint paint;
  
  public StrokeBorder(BasicStroke paramBasicStroke) { this(paramBasicStroke, null); }
  
  @ConstructorProperties({"stroke", "paint"})
  public StrokeBorder(BasicStroke paramBasicStroke, Paint paramPaint) {
    if (paramBasicStroke == null)
      throw new NullPointerException("border's stroke"); 
    this.stroke = paramBasicStroke;
    this.paint = paramPaint;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    float f = this.stroke.getLineWidth();
    if (f > 0.0F) {
      paramGraphics = paramGraphics.create();
      if (paramGraphics instanceof Graphics2D) {
        Graphics2D graphics2D = (Graphics2D)paramGraphics;
        graphics2D.setStroke(this.stroke);
        graphics2D.setPaint((this.paint != null) ? this.paint : ((paramComponent == null) ? null : paramComponent.getForeground()));
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.draw(new Rectangle2D.Float(paramInt1 + f / 2.0F, paramInt2 + f / 2.0F, paramInt3 - f, paramInt4 - f));
      } 
      paramGraphics.dispose();
    } 
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    int i = (int)Math.ceil(this.stroke.getLineWidth());
    paramInsets.set(i, i, i, i);
    return paramInsets;
  }
  
  public BasicStroke getStroke() { return this.stroke; }
  
  public Paint getPaint() { return this.paint; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\StrokeBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */