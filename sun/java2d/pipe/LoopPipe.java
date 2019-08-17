package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.DrawParallelogram;
import sun.java2d.loops.FillParallelogram;
import sun.java2d.loops.FillSpans;

public class LoopPipe implements PixelDrawPipe, PixelFillPipe, ParallelogramPipe, ShapeDrawPipe, LoopBasedPipe {
  static final RenderingEngine RenderEngine = RenderingEngine.getInstance();
  
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramSunGraphics2D.transX;
    int j = paramSunGraphics2D.transY;
    paramSunGraphics2D.loops.drawLineLoop.DrawLine(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramInt1 + i, paramInt2 + j, paramInt3 + i, paramInt4 + j);
  }
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSunGraphics2D.loops.drawRectLoop.DrawRect(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4); }
  
  public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { paramSunGraphics2D.shapepipe.draw(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)); }
  
  public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSunGraphics2D.shapepipe.draw(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { paramSunGraphics2D.shapepipe.draw(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0)); }
  
  public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    int[] arrayOfInt = { paramInt };
    paramSunGraphics2D.loops.drawPolygonsLoop.DrawPolygons(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramArrayOfInt1, paramArrayOfInt2, arrayOfInt, 1, paramSunGraphics2D.transX, paramSunGraphics2D.transY, false);
  }
  
  public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    int[] arrayOfInt = { paramInt };
    paramSunGraphics2D.loops.drawPolygonsLoop.DrawPolygons(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramArrayOfInt1, paramArrayOfInt2, arrayOfInt, 1, paramSunGraphics2D.transX, paramSunGraphics2D.transY, true);
  }
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSunGraphics2D.loops.fillRectLoop.FillRect(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4); }
  
  public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { paramSunGraphics2D.shapepipe.fill(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)); }
  
  public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSunGraphics2D.shapepipe.fill(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { paramSunGraphics2D.shapepipe.fill(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2)); }
  
  public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    shapeSpanIterator = getFillSSI(paramSunGraphics2D);
    try {
      shapeSpanIterator.setOutputArea(paramSunGraphics2D.getCompClip());
      shapeSpanIterator.appendPoly(paramArrayOfInt1, paramArrayOfInt2, paramInt, paramSunGraphics2D.transX, paramSunGraphics2D.transY);
      fillSpans(paramSunGraphics2D, shapeSpanIterator);
    } finally {
      shapeSpanIterator.dispose();
    } 
  }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    if (paramSunGraphics2D.strokeState == 0) {
      byte b2;
      byte b1;
      Path2D.Float float;
      if (paramSunGraphics2D.transformState <= 1) {
        if (paramShape instanceof Path2D.Float) {
          float = (Path2D.Float)paramShape;
        } else {
          float = new Path2D.Float(paramShape);
        } 
        b1 = paramSunGraphics2D.transX;
        b2 = paramSunGraphics2D.transY;
      } else {
        float = new Path2D.Float(paramShape, paramSunGraphics2D.transform);
        b1 = 0;
        b2 = 0;
      } 
      paramSunGraphics2D.loops.drawPathLoop.DrawPath(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), b1, b2, float);
      return;
    } 
    if (paramSunGraphics2D.strokeState == 3) {
      fill(paramSunGraphics2D, paramSunGraphics2D.stroke.createStrokedShape(paramShape));
      return;
    } 
    shapeSpanIterator = getStrokeSpans(paramSunGraphics2D, paramShape);
    try {
      fillSpans(paramSunGraphics2D, shapeSpanIterator);
    } finally {
      shapeSpanIterator.dispose();
    } 
  }
  
  public static ShapeSpanIterator getFillSSI(SunGraphics2D paramSunGraphics2D) {
    boolean bool = (paramSunGraphics2D.stroke instanceof BasicStroke && paramSunGraphics2D.strokeHint != 2);
    return new ShapeSpanIterator(bool);
  }
  
  public static ShapeSpanIterator getStrokeSpans(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    ShapeSpanIterator shapeSpanIterator = new ShapeSpanIterator(false);
    try {
      shapeSpanIterator.setOutputArea(paramSunGraphics2D.getCompClip());
      shapeSpanIterator.setRule(1);
      BasicStroke basicStroke = (BasicStroke)paramSunGraphics2D.stroke;
      boolean bool1 = (paramSunGraphics2D.strokeState <= 1);
      boolean bool2 = (paramSunGraphics2D.strokeHint != 2);
      RenderEngine.strokeTo(paramShape, paramSunGraphics2D.transform, basicStroke, bool1, bool2, false, shapeSpanIterator);
    } catch (Throwable throwable) {
      shapeSpanIterator.dispose();
      shapeSpanIterator = null;
      throw new InternalError("Unable to Stroke shape (" + throwable.getMessage() + ")", throwable);
    } 
    return shapeSpanIterator;
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    if (paramSunGraphics2D.strokeState == 0) {
      byte b2;
      byte b1;
      Path2D.Float float;
      if (paramSunGraphics2D.transformState <= 1) {
        if (paramShape instanceof Path2D.Float) {
          float = (Path2D.Float)paramShape;
        } else {
          float = new Path2D.Float(paramShape);
        } 
        b1 = paramSunGraphics2D.transX;
        b2 = paramSunGraphics2D.transY;
      } else {
        float = new Path2D.Float(paramShape, paramSunGraphics2D.transform);
        b1 = 0;
        b2 = 0;
      } 
      paramSunGraphics2D.loops.fillPathLoop.FillPath(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), b1, b2, float);
      return;
    } 
    shapeSpanIterator = getFillSSI(paramSunGraphics2D);
    try {
      shapeSpanIterator.setOutputArea(paramSunGraphics2D.getCompClip());
      AffineTransform affineTransform = (paramSunGraphics2D.transformState == 0) ? null : paramSunGraphics2D.transform;
      shapeSpanIterator.appendPath(paramShape.getPathIterator(affineTransform));
      fillSpans(paramSunGraphics2D, shapeSpanIterator);
    } finally {
      shapeSpanIterator.dispose();
    } 
  }
  
  private static void fillSpans(SunGraphics2D paramSunGraphics2D, SpanIterator paramSpanIterator) {
    if (paramSunGraphics2D.clipState == 2) {
      paramSpanIterator = paramSunGraphics2D.clipRegion.filter(paramSpanIterator);
    } else {
      FillSpans fillSpans = paramSunGraphics2D.loops.fillSpansLoop;
      if (fillSpans != null) {
        fillSpans.FillSpans(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramSpanIterator);
        return;
      } 
    } 
    int[] arrayOfInt = new int[4];
    SurfaceData surfaceData = paramSunGraphics2D.getSurfaceData();
    while (paramSpanIterator.nextSpan(arrayOfInt)) {
      int i = arrayOfInt[0];
      int j = arrayOfInt[1];
      int k = arrayOfInt[2] - i;
      int m = arrayOfInt[3] - j;
      paramSunGraphics2D.loops.fillRectLoop.FillRect(paramSunGraphics2D, surfaceData, i, j, k, m);
    } 
  }
  
  public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10) {
    FillParallelogram fillParallelogram = paramSunGraphics2D.loops.fillParallelogramLoop;
    fillParallelogram.FillParallelogram(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10);
  }
  
  public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12) {
    DrawParallelogram drawParallelogram = paramSunGraphics2D.loops.drawParallelogramLoop;
    drawParallelogram.DrawParallelogram(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, paramDouble11, paramDouble12);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\LoopPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */