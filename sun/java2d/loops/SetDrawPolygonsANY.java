package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class SetDrawPolygonsANY extends DrawPolygons {
  SetDrawPolygonsANY() { super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any); }
  
  public void DrawPolygons(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    PixelWriter pixelWriter = GeneralRenderer.createSolidPixelWriter(paramSunGraphics2D, paramSurfaceData);
    int i = 0;
    Region region = paramSunGraphics2D.getCompClip();
    for (byte b = 0; b < paramInt1; b++) {
      int j = paramArrayOfInt3[b];
      GeneralRenderer.doDrawPoly(paramSurfaceData, pixelWriter, paramArrayOfInt1, paramArrayOfInt2, i, j, region, paramInt2, paramInt3, paramBoolean);
      i += j;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\SetDrawPolygonsANY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */