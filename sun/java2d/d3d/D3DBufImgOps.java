package sun.java2d.d3d;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.pipe.BufferedBufImgOps;

class D3DBufImgOps extends BufferedBufImgOps {
  static boolean renderImageWithOp(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2) {
    if (paramBufferedImageOp instanceof ConvolveOp) {
      if (!isConvolveOpValid((ConvolveOp)paramBufferedImageOp))
        return false; 
    } else if (paramBufferedImageOp instanceof RescaleOp) {
      if (!isRescaleOpValid((RescaleOp)paramBufferedImageOp, paramBufferedImage))
        return false; 
    } else if (paramBufferedImageOp instanceof LookupOp) {
      if (!isLookupOpValid((LookupOp)paramBufferedImageOp, paramBufferedImage))
        return false; 
    } else {
      return false;
    } 
    SurfaceData surfaceData1 = paramSunGraphics2D.surfaceData;
    if (!(surfaceData1 instanceof D3DSurfaceData) || paramSunGraphics2D.interpolationType == 3 || paramSunGraphics2D.compositeState > 1)
      return false; 
    paramSunGraphics2D;
    SurfaceData surfaceData2 = surfaceData1.getSourceSurfaceData(paramBufferedImage, 0, CompositeType.SrcOver, null);
    if (!(surfaceData2 instanceof D3DSurfaceData)) {
      paramSunGraphics2D;
      surfaceData2 = surfaceData1.getSourceSurfaceData(paramBufferedImage, 0, CompositeType.SrcOver, null);
      if (!(surfaceData2 instanceof D3DSurfaceData))
        return false; 
    } 
    D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)surfaceData2;
    D3DGraphicsDevice d3DGraphicsDevice = (D3DGraphicsDevice)d3DSurfaceData.getDeviceConfiguration().getDevice();
    if (d3DSurfaceData.getType() != 3 || !d3DGraphicsDevice.isCapPresent(65536))
      return false; 
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    D3DBlitLoops.IsoBlit(surfaceData2, surfaceData1, paramBufferedImage, paramBufferedImageOp, paramSunGraphics2D.composite, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.transform, paramSunGraphics2D.interpolationType, 0, 0, i, j, paramInt1, paramInt2, (paramInt1 + i), (paramInt2 + j), true);
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DBufImgOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */