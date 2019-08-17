package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorDrawLineANY extends DrawLine {
  XorDrawLineANY() { super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any); }
  
  public void DrawLine(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    PixelWriter pixelWriter = GeneralRenderer.createXorPixelWriter(paramSunGraphics2D, paramSurfaceData);
    if (paramInt2 >= paramInt4) {
      GeneralRenderer.doDrawLine(paramSurfaceData, pixelWriter, null, paramSunGraphics2D.getCompClip(), paramInt3, paramInt4, paramInt1, paramInt2);
    } else {
      GeneralRenderer.doDrawLine(paramSurfaceData, pixelWriter, null, paramSunGraphics2D.getCompClip(), paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\XorDrawLineANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */