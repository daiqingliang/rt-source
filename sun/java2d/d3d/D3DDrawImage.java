package sun.java2d.d3d;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.DrawImage;

public class D3DDrawImage extends DrawImage {
  protected void renderImageXform(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor) {
    if (paramInt1 != 3) {
      SurfaceData surfaceData1 = paramSunGraphics2D.surfaceData;
      paramSunGraphics2D;
      SurfaceData surfaceData2 = surfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
      if (surfaceData2 != null && !isBgOperation(surfaceData2, paramColor)) {
        SurfaceType surfaceType1 = surfaceData2.getSurfaceType();
        SurfaceType surfaceType2 = surfaceData1.getSurfaceType();
        TransformBlit transformBlit = TransformBlit.getFromCache(surfaceType1, paramSunGraphics2D.imageComp, surfaceType2);
        if (transformBlit != null) {
          transformBlit.Transform(surfaceData2, surfaceData1, paramSunGraphics2D.composite, paramSunGraphics2D.getCompClip(), paramAffineTransform, paramInt1, paramInt2, paramInt3, 0, 0, paramInt4 - paramInt2, paramInt5 - paramInt3);
          return;
        } 
      } 
    } 
    super.renderImageXform(paramSunGraphics2D, paramImage, paramAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramColor);
  }
  
  public void transformImage(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2) {
    if (paramBufferedImageOp != null) {
      if (paramBufferedImageOp instanceof AffineTransformOp) {
        AffineTransformOp affineTransformOp = (AffineTransformOp)paramBufferedImageOp;
        transformImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, affineTransformOp.getTransform(), affineTransformOp.getInterpolationType());
        return;
      } 
      if (D3DBufImgOps.renderImageWithOp(paramSunGraphics2D, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2))
        return; 
      paramBufferedImage = paramBufferedImageOp.filter(paramBufferedImage, null);
    } 
    copyImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DDrawImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */