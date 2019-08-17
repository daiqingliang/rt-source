package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorDrawRectANY extends DrawRect {
  XorDrawRectANY() { super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any); }
  
  public void DrawRect(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    PixelWriter pixelWriter = GeneralRenderer.createXorPixelWriter(paramSunGraphics2D, paramSurfaceData);
    GeneralRenderer.doDrawRect(pixelWriter, paramSunGraphics2D, paramSurfaceData, paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\XorDrawRectANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */