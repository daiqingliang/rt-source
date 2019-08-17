package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorDrawGlyphListANY extends DrawGlyphList {
  XorDrawGlyphListANY() { super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any); }
  
  public void DrawGlyphList(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList) {
    PixelWriter pixelWriter = GeneralRenderer.createXorPixelWriter(paramSunGraphics2D, paramSurfaceData);
    GeneralRenderer.doDrawGlyphList(paramSurfaceData, pixelWriter, paramGlyphList, paramSunGraphics2D.getCompClip());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\XorDrawGlyphListANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */