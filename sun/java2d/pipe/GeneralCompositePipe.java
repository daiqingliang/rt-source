package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.CompositeContext;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;

public class GeneralCompositePipe implements CompositePipe {
  public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt) {
    RenderingHints renderingHints = paramSunGraphics2D.getRenderingHints();
    ColorModel colorModel = paramSunGraphics2D.getDeviceColorModel();
    PaintContext paintContext = paramSunGraphics2D.paint.createContext(colorModel, paramRectangle, paramShape.getBounds2D(), paramSunGraphics2D.cloneTransform(), renderingHints);
    CompositeContext compositeContext = paramSunGraphics2D.composite.createContext(paintContext.getColorModel(), colorModel, renderingHints);
    return new TileContext(paramSunGraphics2D, paintContext, compositeContext, colorModel);
  }
  
  public boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return true; }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    WritableRaster writableRaster;
    Raster raster3;
    TileContext tileContext = (TileContext)paramObject;
    PaintContext paintContext = tileContext.paintCtxt;
    CompositeContext compositeContext = tileContext.compCtxt;
    SunGraphics2D sunGraphics2D = tileContext.sunG2D;
    Raster raster1 = paintContext.getRaster(paramInt3, paramInt4, paramInt5, paramInt6);
    ColorModel colorModel = paintContext.getColorModel();
    SurfaceData surfaceData = sunGraphics2D.getSurfaceData();
    Raster raster2 = surfaceData.getRaster(paramInt3, paramInt4, paramInt5, paramInt6);
    if (raster2 instanceof WritableRaster && paramArrayOfByte == null) {
      writableRaster = (WritableRaster)raster2;
      writableRaster = writableRaster.createWritableChild(paramInt3, paramInt4, paramInt5, paramInt6, 0, 0, null);
      raster3 = writableRaster;
    } else {
      raster3 = raster2.createChild(paramInt3, paramInt4, paramInt5, paramInt6, 0, 0, null);
      writableRaster = raster3.createCompatibleWritableRaster();
    } 
    compositeContext.compose(raster1, raster3, writableRaster);
    if (raster2 != writableRaster && writableRaster.getParent() != raster2)
      if (raster2 instanceof WritableRaster && paramArrayOfByte == null) {
        ((WritableRaster)raster2).setDataElements(paramInt3, paramInt4, writableRaster);
      } else {
        ColorModel colorModel1 = sunGraphics2D.getDeviceColorModel();
        BufferedImage bufferedImage = new BufferedImage(colorModel1, writableRaster, colorModel1.isAlphaPremultiplied(), null);
        SurfaceData surfaceData1 = BufImgSurfaceData.createData(bufferedImage);
        if (paramArrayOfByte == null) {
          Blit blit = Blit.getFromCache(surfaceData1.getSurfaceType(), CompositeType.SrcNoEa, surfaceData.getSurfaceType());
          blit.Blit(surfaceData1, surfaceData, AlphaComposite.Src, null, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6);
        } else {
          MaskBlit maskBlit = MaskBlit.getFromCache(surfaceData1.getSurfaceType(), CompositeType.SrcNoEa, surfaceData.getSurfaceType());
          maskBlit.MaskBlit(surfaceData1, surfaceData, AlphaComposite.Src, null, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfByte, paramInt1, paramInt2);
        } 
      }  
  }
  
  public void skipTile(Object paramObject, int paramInt1, int paramInt2) {}
  
  public void endSequence(Object paramObject) {
    TileContext tileContext = (TileContext)paramObject;
    if (tileContext.paintCtxt != null)
      tileContext.paintCtxt.dispose(); 
    if (tileContext.compCtxt != null)
      tileContext.compCtxt.dispose(); 
  }
  
  class TileContext {
    SunGraphics2D sunG2D;
    
    PaintContext paintCtxt;
    
    CompositeContext compCtxt;
    
    ColorModel compModel;
    
    Object pipeState;
    
    public TileContext(SunGraphics2D param1SunGraphics2D, PaintContext param1PaintContext, CompositeContext param1CompositeContext, ColorModel param1ColorModel) {
      this.sunG2D = param1SunGraphics2D;
      this.paintCtxt = param1PaintContext;
      this.compCtxt = param1CompositeContext;
      this.compModel = param1ColorModel;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\GeneralCompositePipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */