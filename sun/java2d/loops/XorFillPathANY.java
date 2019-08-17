package sun.java2d.loops;

import java.awt.geom.Path2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorFillPathANY extends FillPath {
  XorFillPathANY() { super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any); }
  
  public void FillPath(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, Path2D.Float paramFloat) {
    PixelWriter pixelWriter = GeneralRenderer.createXorPixelWriter(paramSunGraphics2D, paramSurfaceData);
    ProcessPath.fillPath(new PixelWriterDrawHandler(paramSurfaceData, pixelWriter, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.strokeHint), paramFloat, paramInt1, paramInt2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\XorFillPathANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */