package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public abstract class SpanShapeRenderer implements ShapeDrawPipe {
  static final RenderingEngine RenderEngine = RenderingEngine.getInstance();
  
  public static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    if (paramSunGraphics2D.stroke instanceof java.awt.BasicStroke) {
      shapeSpanIterator = LoopPipe.getStrokeSpans(paramSunGraphics2D, paramShape);
      try {
        renderSpans(paramSunGraphics2D, paramSunGraphics2D.getCompClip(), paramShape, shapeSpanIterator);
      } finally {
        shapeSpanIterator.dispose();
      } 
    } else {
      fill(paramSunGraphics2D, paramSunGraphics2D.stroke.createStrokedShape(paramShape));
    } 
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    if (paramShape instanceof Rectangle2D && (paramSunGraphics2D.transform.getType() & 0x30) == 0) {
      renderRect(paramSunGraphics2D, (Rectangle2D)paramShape);
      return;
    } 
    Region region = paramSunGraphics2D.getCompClip();
    shapeSpanIterator = LoopPipe.getFillSSI(paramSunGraphics2D);
    try {
      shapeSpanIterator.setOutputArea(region);
      shapeSpanIterator.appendPath(paramShape.getPathIterator(paramSunGraphics2D.transform));
      renderSpans(paramSunGraphics2D, region, paramShape, shapeSpanIterator);
    } finally {
      shapeSpanIterator.dispose();
    } 
  }
  
  public abstract Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt);
  
  public abstract void renderBox(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void endSequence(Object paramObject);
  
  public void renderRect(SunGraphics2D paramSunGraphics2D, Rectangle2D paramRectangle2D) {
    double[] arrayOfDouble = { paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight() };
    arrayOfDouble[2] = arrayOfDouble[2] + arrayOfDouble[0];
    arrayOfDouble[3] = arrayOfDouble[3] + arrayOfDouble[1];
    if (arrayOfDouble[2] <= arrayOfDouble[0] || arrayOfDouble[3] <= arrayOfDouble[1])
      return; 
    paramSunGraphics2D.transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 2);
    if (arrayOfDouble[2] < arrayOfDouble[0]) {
      double d = arrayOfDouble[2];
      arrayOfDouble[2] = arrayOfDouble[0];
      arrayOfDouble[0] = d;
    } 
    if (arrayOfDouble[3] < arrayOfDouble[1]) {
      double d = arrayOfDouble[3];
      arrayOfDouble[3] = arrayOfDouble[1];
      arrayOfDouble[1] = d;
    } 
    int[] arrayOfInt = { (int)arrayOfDouble[0], (int)arrayOfDouble[1], (int)arrayOfDouble[2], (int)arrayOfDouble[3] };
    Rectangle rectangle = new Rectangle(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
    Region region = paramSunGraphics2D.getCompClip();
    region.clipBoxToBounds(arrayOfInt);
    if (arrayOfInt[0] >= arrayOfInt[2] || arrayOfInt[1] >= arrayOfInt[3])
      return; 
    Object object = startSequence(paramSunGraphics2D, paramRectangle2D, rectangle, arrayOfInt);
    if (region.isRectangular()) {
      renderBox(object, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
    } else {
      SpanIterator spanIterator = region.getSpanIterator(arrayOfInt);
      while (spanIterator.nextSpan(arrayOfInt))
        renderBox(object, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]); 
    } 
    endSequence(object);
  }
  
  public void renderSpans(SunGraphics2D paramSunGraphics2D, Region paramRegion, Shape paramShape, ShapeSpanIterator paramShapeSpanIterator) {
    object = null;
    int[] arrayOfInt = new int[4];
    try {
      paramShapeSpanIterator.getPathBox(arrayOfInt);
      Rectangle rectangle = new Rectangle(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
      paramRegion.clipBoxToBounds(arrayOfInt);
      if (arrayOfInt[0] >= arrayOfInt[2] || arrayOfInt[1] >= arrayOfInt[3])
        return; 
      paramShapeSpanIterator.intersectClipBox(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
      object = startSequence(paramSunGraphics2D, paramShape, rectangle, arrayOfInt);
      spanClipLoop(object, paramShapeSpanIterator, paramRegion, arrayOfInt);
    } finally {
      if (object != null)
        endSequence(object); 
    } 
  }
  
  public void spanClipLoop(Object paramObject, SpanIterator paramSpanIterator, Region paramRegion, int[] paramArrayOfInt) {
    if (!paramRegion.isRectangular())
      paramSpanIterator = paramRegion.filter(paramSpanIterator); 
    while (paramSpanIterator.nextSpan(paramArrayOfInt)) {
      int i = paramArrayOfInt[0];
      int j = paramArrayOfInt[1];
      renderBox(paramObject, i, j, paramArrayOfInt[2] - i, paramArrayOfInt[3] - j);
    } 
  }
  
  public static class Composite extends SpanShapeRenderer {
    CompositePipe comppipe;
    
    public Composite(CompositePipe param1CompositePipe) { this.comppipe = param1CompositePipe; }
    
    public Object startSequence(SunGraphics2D param1SunGraphics2D, Shape param1Shape, Rectangle param1Rectangle, int[] param1ArrayOfInt) { return this.comppipe.startSequence(param1SunGraphics2D, param1Shape, param1Rectangle, param1ArrayOfInt); }
    
    public void renderBox(Object param1Object, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { this.comppipe.renderPathTile(param1Object, null, 0, param1Int3, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void endSequence(Object param1Object) { this.comppipe.endSequence(param1Object); }
  }
  
  public static class Simple extends SpanShapeRenderer implements LoopBasedPipe {
    public Object startSequence(SunGraphics2D param1SunGraphics2D, Shape param1Shape, Rectangle param1Rectangle, int[] param1ArrayOfInt) { return param1SunGraphics2D; }
    
    public void renderBox(Object param1Object, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      SunGraphics2D sunGraphics2D = (SunGraphics2D)param1Object;
      SurfaceData surfaceData = sunGraphics2D.getSurfaceData();
      sunGraphics2D.loops.fillRectLoop.FillRect(sunGraphics2D, surfaceData, param1Int1, param1Int2, param1Int3, param1Int4);
    }
    
    public void endSequence(Object param1Object) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\SpanShapeRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */