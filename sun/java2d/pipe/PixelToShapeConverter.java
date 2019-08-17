package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import sun.java2d.SunGraphics2D;

public class PixelToShapeConverter implements PixelDrawPipe, PixelFillPipe {
  ShapeDrawPipe outpipe;
  
  public PixelToShapeConverter(ShapeDrawPipe paramShapeDrawPipe) { this.outpipe = paramShapeDrawPipe; }
  
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.outpipe.draw(paramSunGraphics2D, new Line2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.outpipe.draw(paramSunGraphics2D, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.outpipe.fill(paramSunGraphics2D, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this.outpipe.draw(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)); }
  
  public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this.outpipe.fill(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)); }
  
  public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.outpipe.draw(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.outpipe.fill(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this.outpipe.draw(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0)); }
  
  public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this.outpipe.fill(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2)); }
  
  private Shape makePoly(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt, boolean paramBoolean) {
    GeneralPath generalPath = new GeneralPath(0);
    if (paramInt > 0) {
      generalPath.moveTo(paramArrayOfInt1[0], paramArrayOfInt2[0]);
      for (byte b = 1; b < paramInt; b++)
        generalPath.lineTo(paramArrayOfInt1[b], paramArrayOfInt2[b]); 
      if (paramBoolean)
        generalPath.closePath(); 
    } 
    return generalPath;
  }
  
  public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { this.outpipe.draw(paramSunGraphics2D, makePoly(paramArrayOfInt1, paramArrayOfInt2, paramInt, false)); }
  
  public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { this.outpipe.draw(paramSunGraphics2D, makePoly(paramArrayOfInt1, paramArrayOfInt2, paramInt, true)); }
  
  public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { this.outpipe.fill(paramSunGraphics2D, makePoly(paramArrayOfInt1, paramArrayOfInt2, paramInt, true)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\PixelToShapeConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */