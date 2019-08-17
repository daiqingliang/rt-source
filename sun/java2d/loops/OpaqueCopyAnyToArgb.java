package sun.java2d.loops;

import java.awt.Composite;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import sun.awt.image.IntegerComponentRaster;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.SpanIterator;

class OpaqueCopyAnyToArgb extends Blit {
  OpaqueCopyAnyToArgb() { super(SurfaceType.Any, CompositeType.SrcNoEa, SurfaceType.IntArgb); }
  
  public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Raster raster1 = paramSurfaceData1.getRaster(paramInt1, paramInt2, paramInt5, paramInt6);
    ColorModel colorModel = paramSurfaceData1.getColorModel();
    Raster raster2 = paramSurfaceData2.getRaster(paramInt3, paramInt4, paramInt5, paramInt6);
    IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)raster2;
    int[] arrayOfInt1 = integerComponentRaster.getDataStorage();
    Region region = CustomComponent.getRegionOfInterest(paramSurfaceData1, paramSurfaceData2, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    SpanIterator spanIterator = region.getSpanIterator();
    Object object = null;
    int i = integerComponentRaster.getScanlineStride();
    paramInt1 -= paramInt3;
    paramInt2 -= paramInt4;
    int[] arrayOfInt2 = new int[4];
    while (spanIterator.nextSpan(arrayOfInt2)) {
      int j = integerComponentRaster.getDataOffset(0) + arrayOfInt2[1] * i + arrayOfInt2[0];
      for (int k = arrayOfInt2[1]; k < arrayOfInt2[3]; k++) {
        int m = j;
        for (int n = arrayOfInt2[0]; n < arrayOfInt2[2]; n++) {
          object = raster1.getDataElements(n + paramInt1, k + paramInt2, object);
          arrayOfInt1[m++] = colorModel.getRGB(object);
        } 
        j += i;
      } 
    } 
    integerComponentRaster.markDirty();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\OpaqueCopyAnyToArgb.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */