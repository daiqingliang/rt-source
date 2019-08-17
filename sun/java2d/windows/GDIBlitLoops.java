package sun.java2d.windows;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

public class GDIBlitLoops extends Blit {
  int rmask;
  
  int gmask;
  
  int bmask;
  
  boolean indexed = false;
  
  public static void register() {
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = { new GDIBlitLoops(SurfaceType.IntRgb, GDIWindowSurfaceData.AnyGdi), new GDIBlitLoops(SurfaceType.Ushort555Rgb, GDIWindowSurfaceData.AnyGdi, 31744, 992, 31), new GDIBlitLoops(SurfaceType.Ushort565Rgb, GDIWindowSurfaceData.AnyGdi, 63488, 2016, 31), new GDIBlitLoops(SurfaceType.ThreeByteBgr, GDIWindowSurfaceData.AnyGdi), new GDIBlitLoops(SurfaceType.ByteIndexedOpaque, GDIWindowSurfaceData.AnyGdi, true), new GDIBlitLoops(SurfaceType.Index8Gray, GDIWindowSurfaceData.AnyGdi, true), new GDIBlitLoops(SurfaceType.ByteGray, GDIWindowSurfaceData.AnyGdi) };
    GraphicsPrimitiveMgr.register(arrayOfGraphicsPrimitive);
  }
  
  public GDIBlitLoops(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2) { this(paramSurfaceType1, paramSurfaceType2, 0, 0, 0); }
  
  public GDIBlitLoops(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, boolean paramBoolean) {
    this(paramSurfaceType1, paramSurfaceType2, 0, 0, 0);
    this.indexed = paramBoolean;
  }
  
  public GDIBlitLoops(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, int paramInt1, int paramInt2, int paramInt3) {
    super(paramSurfaceType1, CompositeType.SrcNoEa, paramSurfaceType2);
    this.rmask = paramInt1;
    this.gmask = paramInt2;
    this.bmask = paramInt3;
  }
  
  public native void nativeBlit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean);
  
  public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { nativeBlit(paramSurfaceData1, paramSurfaceData2, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, this.rmask, this.gmask, this.bmask, this.indexed); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\windows\GDIBlitLoops.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */