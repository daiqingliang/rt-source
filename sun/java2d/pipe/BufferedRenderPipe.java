package sun.java2d.pipe;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.ProcessPath;

public abstract class BufferedRenderPipe implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, ParallelogramPipe {
  ParallelogramPipe aapgrampipe = new AAParallelogramPipe(null);
  
  static final int BYTES_PER_POLY_POINT = 8;
  
  static final int BYTES_PER_SCANLINE = 12;
  
  static final int BYTES_PER_SPAN = 16;
  
  protected RenderQueue rq;
  
  protected RenderBuffer buf;
  
  private BufferedDrawHandler drawHandler;
  
  public BufferedRenderPipe(RenderQueue paramRenderQueue) {
    this.rq = paramRenderQueue;
    this.buf = paramRenderQueue.getBuffer();
    this.drawHandler = new BufferedDrawHandler();
  }
  
  public ParallelogramPipe getAAParallelogramPipe() { return this.aapgrampipe; }
  
  protected abstract void validateContext(SunGraphics2D paramSunGraphics2D);
  
  protected abstract void validateContextAA(SunGraphics2D paramSunGraphics2D);
  
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramSunGraphics2D.transX;
    int j = paramSunGraphics2D.transY;
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D);
      this.rq.ensureCapacity(20);
      this.buf.putInt(10);
      this.buf.putInt(paramInt1 + i);
      this.buf.putInt(paramInt2 + j);
      this.buf.putInt(paramInt3 + i);
      this.buf.putInt(paramInt4 + j);
    } finally {
      this.rq.unlock();
    } 
  }
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D);
      this.rq.ensureCapacity(20);
      this.buf.putInt(11);
      this.buf.putInt(paramInt1 + paramSunGraphics2D.transX);
      this.buf.putInt(paramInt2 + paramSunGraphics2D.transY);
      this.buf.putInt(paramInt3);
      this.buf.putInt(paramInt4);
    } finally {
      this.rq.unlock();
    } 
  }
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D);
      this.rq.ensureCapacity(20);
      this.buf.putInt(20);
      this.buf.putInt(paramInt1 + paramSunGraphics2D.transX);
      this.buf.putInt(paramInt2 + paramSunGraphics2D.transY);
      this.buf.putInt(paramInt3);
      this.buf.putInt(paramInt4);
    } finally {
      this.rq.unlock();
    } 
  }
  
  public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { draw(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)); }
  
  public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { fill(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)); }
  
  public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { draw(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { fill(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { draw(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0)); }
  
  public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { fill(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2)); }
  
  protected void drawPoly(final SunGraphics2D sg2d, final int[] xPoints, final int[] yPoints, final int nPoints, final boolean isClosed) {
    if (paramArrayOfInt1 == null || paramArrayOfInt2 == null)
      throw new NullPointerException("coordinate array"); 
    if (paramArrayOfInt1.length < paramInt || paramArrayOfInt2.length < paramInt)
      throw new ArrayIndexOutOfBoundsException("coordinate array"); 
    if (paramInt < 2)
      return; 
    if (paramInt == 2 && !paramBoolean) {
      drawLine(paramSunGraphics2D, paramArrayOfInt1[0], paramArrayOfInt2[0], paramArrayOfInt1[1], paramArrayOfInt2[1]);
      return;
    } 
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D);
      int i = paramInt * 8;
      int j = 20 + i;
      if (j <= this.buf.capacity()) {
        if (j > this.buf.remaining())
          this.rq.flushNow(); 
        this.buf.putInt(12);
        this.buf.putInt(paramInt);
        this.buf.putInt(paramBoolean ? 1 : 0);
        this.buf.putInt(paramSunGraphics2D.transX);
        this.buf.putInt(paramSunGraphics2D.transY);
        this.buf.put(paramArrayOfInt1, 0, paramInt);
        this.buf.put(paramArrayOfInt2, 0, paramInt);
      } else {
        this.rq.flushAndInvokeNow(new Runnable() {
              public void run() { BufferedRenderPipe.this.drawPoly(xPoints, yPoints, nPoints, isClosed, this.val$sg2d.transX, this.val$sg2d.transY); }
            });
      } 
    } finally {
      this.rq.unlock();
    } 
  }
  
  protected abstract void drawPoly(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3);
  
  public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { drawPoly(paramSunGraphics2D, paramArrayOfInt1, paramArrayOfInt2, paramInt, false); }
  
  public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { drawPoly(paramSunGraphics2D, paramArrayOfInt1, paramArrayOfInt2, paramInt, true); }
  
  public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { fill(paramSunGraphics2D, new Polygon(paramArrayOfInt1, paramArrayOfInt2, paramInt)); }
  
  protected void drawPath(SunGraphics2D paramSunGraphics2D, Path2D.Float paramFloat, int paramInt1, int paramInt2) {
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D);
      this.drawHandler.validate(paramSunGraphics2D);
      ProcessPath.drawPath(this.drawHandler, paramFloat, paramInt1, paramInt2);
    } finally {
      this.rq.unlock();
    } 
  }
  
  protected void fillPath(SunGraphics2D paramSunGraphics2D, Path2D.Float paramFloat, int paramInt1, int paramInt2) {
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D);
      this.drawHandler.validate(paramSunGraphics2D);
      this.drawHandler.startFillPath();
      ProcessPath.fillPath(this.drawHandler, paramFloat, paramInt1, paramInt2);
      this.drawHandler.endFillPath();
    } finally {
      this.rq.unlock();
    } 
  }
  
  private native int fillSpans(RenderQueue paramRenderQueue, long paramLong1, int paramInt1, int paramInt2, SpanIterator paramSpanIterator, long paramLong2, int paramInt3, int paramInt4);
  
  protected void fillSpans(SunGraphics2D paramSunGraphics2D, SpanIterator paramSpanIterator, int paramInt1, int paramInt2) {
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D);
      this.rq.ensureCapacity(24);
      int i = fillSpans(this.rq, this.buf.getAddress(), this.buf.position(), this.buf.capacity(), paramSpanIterator, paramSpanIterator.getNativeIterator(), paramInt1, paramInt2);
      this.buf.position(i);
    } finally {
      this.rq.unlock();
    } 
  }
  
  public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10) {
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D);
      this.rq.ensureCapacity(28);
      this.buf.putInt(22);
      this.buf.putFloat((float)paramDouble5);
      this.buf.putFloat((float)paramDouble6);
      this.buf.putFloat((float)paramDouble7);
      this.buf.putFloat((float)paramDouble8);
      this.buf.putFloat((float)paramDouble9);
      this.buf.putFloat((float)paramDouble10);
    } finally {
      this.rq.unlock();
    } 
  }
  
  public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12) {
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D);
      this.rq.ensureCapacity(36);
      this.buf.putInt(15);
      this.buf.putFloat((float)paramDouble5);
      this.buf.putFloat((float)paramDouble6);
      this.buf.putFloat((float)paramDouble7);
      this.buf.putFloat((float)paramDouble8);
      this.buf.putFloat((float)paramDouble9);
      this.buf.putFloat((float)paramDouble10);
      this.buf.putFloat((float)paramDouble11);
      this.buf.putFloat((float)paramDouble12);
    } finally {
      this.rq.unlock();
    } 
  }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    if (paramSunGraphics2D.strokeState == 0) {
      byte b2;
      byte b1;
      Path2D.Float float;
      if (paramShape instanceof Polygon && paramSunGraphics2D.transformState < 3) {
        float = (Polygon)paramShape;
        drawPolygon(paramSunGraphics2D, float.xpoints, float.ypoints, float.npoints);
        return;
      } 
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
      drawPath(paramSunGraphics2D, float, b1, b2);
    } else if (paramSunGraphics2D.strokeState < 3) {
      shapeSpanIterator = LoopPipe.getStrokeSpans(paramSunGraphics2D, paramShape);
      try {
        fillSpans(paramSunGraphics2D, shapeSpanIterator, 0, 0);
      } finally {
        shapeSpanIterator.dispose();
      } 
    } else {
      fill(paramSunGraphics2D, paramSunGraphics2D.stroke.createStrokedShape(paramShape));
    } 
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    AffineTransform affineTransform;
    int j;
    int i;
    if (paramSunGraphics2D.strokeState == 0) {
      if (paramSunGraphics2D.transformState <= 1) {
        if (paramShape instanceof Path2D.Float) {
          affineTransform = (Path2D.Float)paramShape;
        } else {
          affineTransform = new Path2D.Float(paramShape);
        } 
        i = paramSunGraphics2D.transX;
        j = paramSunGraphics2D.transY;
      } else {
        affineTransform = new Path2D.Float(paramShape, paramSunGraphics2D.transform);
        i = 0;
        j = 0;
      } 
      fillPath(paramSunGraphics2D, affineTransform, i, j);
      return;
    } 
    if (paramSunGraphics2D.transformState <= 1) {
      affineTransform = null;
      i = paramSunGraphics2D.transX;
      j = paramSunGraphics2D.transY;
    } else {
      affineTransform = paramSunGraphics2D.transform;
      i = j = 0;
    } 
    shapeSpanIterator = LoopPipe.getFillSSI(paramSunGraphics2D);
    try {
      Region region = paramSunGraphics2D.getCompClip();
      shapeSpanIterator.setOutputAreaXYXY(region.getLoX() - i, region.getLoY() - j, region.getHiX() - i, region.getHiY() - j);
      shapeSpanIterator.appendPath(paramShape.getPathIterator(affineTransform));
      fillSpans(paramSunGraphics2D, shapeSpanIterator, i, j);
    } finally {
      shapeSpanIterator.dispose();
    } 
  }
  
  private class AAParallelogramPipe implements ParallelogramPipe {
    private AAParallelogramPipe() {}
    
    public void fillParallelogram(SunGraphics2D param1SunGraphics2D, double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8, double param1Double9, double param1Double10) {
      BufferedRenderPipe.this.rq.lock();
      try {
        BufferedRenderPipe.this.validateContextAA(param1SunGraphics2D);
        BufferedRenderPipe.this.rq.ensureCapacity(28);
        BufferedRenderPipe.this.buf.putInt(23);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double5);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double6);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double7);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double8);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double9);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double10);
      } finally {
        BufferedRenderPipe.this.rq.unlock();
      } 
    }
    
    public void drawParallelogram(SunGraphics2D param1SunGraphics2D, double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8, double param1Double9, double param1Double10, double param1Double11, double param1Double12) {
      BufferedRenderPipe.this.rq.lock();
      try {
        BufferedRenderPipe.this.validateContextAA(param1SunGraphics2D);
        BufferedRenderPipe.this.rq.ensureCapacity(36);
        BufferedRenderPipe.this.buf.putInt(16);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double5);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double6);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double7);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double8);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double9);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double10);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double11);
        BufferedRenderPipe.this.buf.putFloat((float)param1Double12);
      } finally {
        BufferedRenderPipe.this.rq.unlock();
      } 
    }
  }
  
  private class BufferedDrawHandler extends ProcessPath.DrawHandler {
    private int scanlineCount;
    
    private int scanlineCountIndex;
    
    private int remainingScanlines;
    
    BufferedDrawHandler() { super(0, 0, 0, 0); }
    
    void validate(SunGraphics2D param1SunGraphics2D) {
      Region region = param1SunGraphics2D.getCompClip();
      setBounds(region.getLoX(), region.getLoY(), region.getHiX(), region.getHiY(), param1SunGraphics2D.strokeHint);
    }
    
    public void drawLine(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      BufferedRenderPipe.this.rq.ensureCapacity(20);
      BufferedRenderPipe.this.buf.putInt(10);
      BufferedRenderPipe.this.buf.putInt(param1Int1);
      BufferedRenderPipe.this.buf.putInt(param1Int2);
      BufferedRenderPipe.this.buf.putInt(param1Int3);
      BufferedRenderPipe.this.buf.putInt(param1Int4);
    }
    
    public void drawPixel(int param1Int1, int param1Int2) {
      BufferedRenderPipe.this.rq.ensureCapacity(12);
      BufferedRenderPipe.this.buf.putInt(13);
      BufferedRenderPipe.this.buf.putInt(param1Int1);
      BufferedRenderPipe.this.buf.putInt(param1Int2);
    }
    
    private void resetFillPath() {
      BufferedRenderPipe.this.buf.putInt(14);
      this.scanlineCountIndex = BufferedRenderPipe.this.buf.position();
      BufferedRenderPipe.this.buf.putInt(0);
      this.scanlineCount = 0;
      this.remainingScanlines = BufferedRenderPipe.this.buf.remaining() / 12;
    }
    
    private void updateScanlineCount() { BufferedRenderPipe.this.buf.putInt(this.scanlineCountIndex, this.scanlineCount); }
    
    public void startFillPath() {
      BufferedRenderPipe.this.rq.ensureCapacity(20);
      resetFillPath();
    }
    
    public void drawScanline(int param1Int1, int param1Int2, int param1Int3) {
      if (this.remainingScanlines == 0) {
        updateScanlineCount();
        BufferedRenderPipe.this.rq.flushNow();
        resetFillPath();
      } 
      BufferedRenderPipe.this.buf.putInt(param1Int1);
      BufferedRenderPipe.this.buf.putInt(param1Int2);
      BufferedRenderPipe.this.buf.putInt(param1Int3);
      this.scanlineCount++;
      this.remainingScanlines--;
    }
    
    public void endFillPath() { updateScanlineCount(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\BufferedRenderPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */