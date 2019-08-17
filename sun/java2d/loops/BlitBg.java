package sun.java2d.loops;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class BlitBg extends GraphicsPrimitive {
  public static final String methodSignature = "BlitBg(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache blitcache = new RenderCache(20);
  
  public static BlitBg locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (BlitBg)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public static BlitBg getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    Object object = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (object != null)
      return (BlitBg)object; 
    BlitBg blitBg = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (blitBg == null) {
      System.out.println("blitbg loop not found for:");
      System.out.println("src:  " + paramSurfaceType1);
      System.out.println("comp: " + paramCompositeType);
      System.out.println("dst:  " + paramSurfaceType2);
    } else {
      blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, blitBg);
    } 
    return blitBg;
  }
  
  protected BlitBg(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public BlitBg(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void BlitBg(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public GraphicsPrimitive traceWrap() { return new TraceBlitBg(this); }
  
  static  {
    GraphicsPrimitiveMgr.registerGeneral(new BlitBg(null, null, null));
  }
  
  private static class General extends BlitBg {
    CompositeType compositeType;
    
    private static Font defaultFont = new Font("Dialog", 0, 12);
    
    public General(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) {
      super(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
      this.compositeType = param1CompositeType;
    }
    
    public void BlitBg(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7) {
      ColorModel colorModel = param1SurfaceData2.getColorModel();
      boolean bool1 = (param1Int1 >>> 24 != 255);
      if (!colorModel.hasAlpha() && bool1)
        colorModel = ColorModel.getRGBdefault(); 
      WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(param1Int6, param1Int7);
      boolean bool2 = colorModel.isAlphaPremultiplied();
      BufferedImage bufferedImage = new BufferedImage(colorModel, writableRaster, bool2, null);
      SurfaceData surfaceData = BufImgSurfaceData.createData(bufferedImage);
      Color color = new Color(param1Int1, bool1);
      SunGraphics2D sunGraphics2D = new SunGraphics2D(surfaceData, color, color, defaultFont);
      FillRect fillRect = FillRect.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, surfaceData.getSurfaceType());
      Blit blit1;
      Blit blit2 = (blit1 = Blit.getFromCache(param1SurfaceData1.getSurfaceType(), CompositeType.SrcOverNoEa, surfaceData.getSurfaceType())).getFromCache(surfaceData.getSurfaceType(), this.compositeType, param1SurfaceData2.getSurfaceType());
      fillRect.FillRect(sunGraphics2D, surfaceData, 0, 0, param1Int6, param1Int7);
      blit1.Blit(param1SurfaceData1, surfaceData, AlphaComposite.SrcOver, null, param1Int2, param1Int3, 0, 0, param1Int6, param1Int7);
      blit2.Blit(surfaceData, param1SurfaceData2, param1Composite, param1Region, 0, 0, param1Int4, param1Int5, param1Int6, param1Int7);
    }
  }
  
  private static class TraceBlitBg extends BlitBg {
    BlitBg target;
    
    public TraceBlitBg(BlitBg param1BlitBg) {
      super(param1BlitBg.getSourceType(), param1BlitBg.getCompositeType(), param1BlitBg.getDestType());
      this.target = param1BlitBg;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void BlitBg(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7) {
      tracePrimitive(this.target);
      this.target.BlitBg(param1SurfaceData1, param1SurfaceData2, param1Composite, param1Region, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1Int7);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\BlitBg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */