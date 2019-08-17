package sun.java2d.pipe;

import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;

public class AlphaPaintPipe implements CompositePipe {
  static WeakReference cachedLastRaster;
  
  static WeakReference cachedLastColorModel;
  
  static WeakReference cachedLastData;
  
  private static final int TILE_SIZE = 32;
  
  public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt) {
    PaintContext paintContext = paramSunGraphics2D.paint.createContext(paramSunGraphics2D.getDeviceColorModel(), paramRectangle, paramShape.getBounds2D(), paramSunGraphics2D.cloneTransform(), paramSunGraphics2D.getRenderingHints());
    return new TileContext(paramSunGraphics2D, paintContext);
  }
  
  public boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return true; }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    TileContext tileContext = (TileContext)paramObject;
    PaintContext paintContext = tileContext.paintCtxt;
    SunGraphics2D sunGraphics2D = tileContext.sunG2D;
    SurfaceData surfaceData1 = tileContext.dstData;
    SurfaceData surfaceData2 = null;
    Raster raster = null;
    if (tileContext.lastData != null && tileContext.lastRaster != null) {
      surfaceData2 = (SurfaceData)tileContext.lastData.get();
      raster = (Raster)tileContext.lastRaster.get();
      if (surfaceData2 == null || raster == null) {
        surfaceData2 = null;
        raster = null;
      } 
    } 
    ColorModel colorModel = tileContext.paintModel;
    for (int i = 0; i < paramInt6; i += 32) {
      int j = paramInt4 + i;
      int k = Math.min(paramInt6 - i, 32);
      for (int m = 0; m < paramInt5; m += 32) {
        int n = paramInt3 + m;
        int i1 = Math.min(paramInt5 - m, 32);
        Raster raster1 = paintContext.getRaster(n, j, i1, k);
        if (raster1.getMinX() != 0 || raster1.getMinY() != 0)
          raster1 = raster1.createTranslatedChild(0, 0); 
        if (raster != raster1) {
          raster = raster1;
          tileContext.lastRaster = new WeakReference(raster);
          BufferedImage bufferedImage = new BufferedImage(colorModel, (WritableRaster)raster1, colorModel.isAlphaPremultiplied(), null);
          surfaceData2 = BufImgSurfaceData.createData(bufferedImage);
          tileContext.lastData = new WeakReference(surfaceData2);
          tileContext.lastMask = null;
          tileContext.lastBlit = null;
        } 
        if (paramArrayOfByte == null) {
          if (tileContext.lastBlit == null) {
            CompositeType compositeType = sunGraphics2D.imageComp;
            if (CompositeType.SrcOverNoEa.equals(compositeType) && colorModel.getTransparency() == 1)
              compositeType = CompositeType.SrcNoEa; 
            tileContext.lastBlit = Blit.getFromCache(surfaceData2.getSurfaceType(), compositeType, surfaceData1.getSurfaceType());
          } 
          tileContext.lastBlit.Blit(surfaceData2, surfaceData1, sunGraphics2D.composite, null, 0, 0, n, j, i1, k);
        } else {
          if (tileContext.lastMask == null) {
            CompositeType compositeType = sunGraphics2D.imageComp;
            if (CompositeType.SrcOverNoEa.equals(compositeType) && colorModel.getTransparency() == 1)
              compositeType = CompositeType.SrcNoEa; 
            tileContext.lastMask = MaskBlit.getFromCache(surfaceData2.getSurfaceType(), compositeType, surfaceData1.getSurfaceType());
          } 
          int i2 = paramInt1 + i * paramInt2 + m;
          tileContext.lastMask.MaskBlit(surfaceData2, surfaceData1, sunGraphics2D.composite, null, 0, 0, n, j, i1, k, paramArrayOfByte, i2, paramInt2);
        } 
      } 
    } 
  }
  
  public void skipTile(Object paramObject, int paramInt1, int paramInt2) {}
  
  public void endSequence(Object paramObject) {
    TileContext tileContext = (TileContext)paramObject;
    if (tileContext.paintCtxt != null)
      tileContext.paintCtxt.dispose(); 
    synchronized (AlphaPaintPipe.class) {
      if (tileContext.lastData != null) {
        cachedLastRaster = tileContext.lastRaster;
        if (cachedLastColorModel == null || cachedLastColorModel.get() != tileContext.paintModel)
          cachedLastColorModel = new WeakReference(tileContext.paintModel); 
        cachedLastData = tileContext.lastData;
      } 
    } 
  }
  
  static class TileContext {
    SunGraphics2D sunG2D;
    
    PaintContext paintCtxt;
    
    ColorModel paintModel;
    
    WeakReference lastRaster;
    
    WeakReference lastData;
    
    MaskBlit lastMask;
    
    Blit lastBlit;
    
    SurfaceData dstData;
    
    public TileContext(SunGraphics2D param1SunGraphics2D, PaintContext param1PaintContext) {
      this.sunG2D = param1SunGraphics2D;
      this.paintCtxt = param1PaintContext;
      this.paintModel = param1PaintContext.getColorModel();
      this.dstData = param1SunGraphics2D.getSurfaceData();
      synchronized (AlphaPaintPipe.class) {
        if (AlphaPaintPipe.cachedLastColorModel != null && AlphaPaintPipe.cachedLastColorModel.get() == this.paintModel) {
          this.lastRaster = AlphaPaintPipe.cachedLastRaster;
          this.lastData = AlphaPaintPipe.cachedLastData;
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\AlphaPaintPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */