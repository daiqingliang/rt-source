package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.SpanIterator;

class SetFillSpansANY extends FillSpans {
  SetFillSpansANY() { super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any); }
  
  public void FillSpans(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, SpanIterator paramSpanIterator) {
    PixelWriter pixelWriter = GeneralRenderer.createSolidPixelWriter(paramSunGraphics2D, paramSurfaceData);
    int[] arrayOfInt = new int[4];
    while (paramSpanIterator.nextSpan(arrayOfInt))
      GeneralRenderer.doSetRect(paramSurfaceData, pixelWriter, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\SetFillSpansANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */