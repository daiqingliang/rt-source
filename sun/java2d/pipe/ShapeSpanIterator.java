package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.geom.PathIterator;
import sun.awt.geom.PathConsumer2D;

public final class ShapeSpanIterator implements SpanIterator, PathConsumer2D {
  long pData;
  
  public static native void initIDs();
  
  public ShapeSpanIterator(boolean paramBoolean) { setNormalize(paramBoolean); }
  
  public void appendPath(PathIterator paramPathIterator) {
    float[] arrayOfFloat = new float[6];
    setRule(paramPathIterator.getWindingRule());
    while (!paramPathIterator.isDone()) {
      addSegment(paramPathIterator.currentSegment(arrayOfFloat), arrayOfFloat);
      paramPathIterator.next();
    } 
    pathDone();
  }
  
  public native void appendPoly(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3);
  
  private native void setNormalize(boolean paramBoolean);
  
  public void setOutputAreaXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { setOutputAreaXYXY(paramInt1, paramInt2, Region.dimAdd(paramInt1, paramInt3), Region.dimAdd(paramInt2, paramInt4)); }
  
  public native void setOutputAreaXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public void setOutputArea(Rectangle paramRectangle) { setOutputAreaXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  public void setOutputArea(Region paramRegion) { setOutputAreaXYXY(paramRegion.lox, paramRegion.loy, paramRegion.hix, paramRegion.hiy); }
  
  public native void setRule(int paramInt);
  
  public native void addSegment(int paramInt, float[] paramArrayOfFloat);
  
  public native void getPathBox(int[] paramArrayOfInt);
  
  public native void intersectClipBox(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native boolean nextSpan(int[] paramArrayOfInt);
  
  public native void skipDownTo(int paramInt);
  
  public native long getNativeIterator();
  
  public native void dispose();
  
  public native void moveTo(float paramFloat1, float paramFloat2);
  
  public native void lineTo(float paramFloat1, float paramFloat2);
  
  public native void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  public native void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);
  
  public native void closePath();
  
  public native void pathDone();
  
  public native long getNativeConsumer();
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\ShapeSpanIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */