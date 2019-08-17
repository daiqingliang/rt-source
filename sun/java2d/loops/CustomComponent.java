package sun.java2d.loops;

import java.awt.Rectangle;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public final class CustomComponent {
  public static void register() {
    Class clazz = CustomComponent.class;
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = { new GraphicsPrimitiveProxy(clazz, "OpaqueCopyAnyToArgb", Blit.methodSignature, Blit.primTypeID, SurfaceType.Any, CompositeType.SrcNoEa, SurfaceType.IntArgb), new GraphicsPrimitiveProxy(clazz, "OpaqueCopyArgbToAny", Blit.methodSignature, Blit.primTypeID, SurfaceType.IntArgb, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorCopyArgbToAny", Blit.methodSignature, Blit.primTypeID, SurfaceType.IntArgb, CompositeType.Xor, SurfaceType.Any) };
    GraphicsPrimitiveMgr.register(arrayOfGraphicsPrimitive);
  }
  
  public static Region getRegionOfInterest(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Region region = Region.getInstanceXYWH(paramInt3, paramInt4, paramInt5, paramInt6);
    region = region.getIntersection(paramSurfaceData2.getBounds());
    Rectangle rectangle = paramSurfaceData1.getBounds();
    rectangle.translate(paramInt3 - paramInt1, paramInt4 - paramInt2);
    region = region.getIntersection(rectangle);
    if (paramRegion != null)
      region = region.getIntersection(paramRegion); 
    return region;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\CustomComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */