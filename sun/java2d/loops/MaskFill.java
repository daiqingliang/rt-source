package sun.java2d.loops;

import java.awt.Composite;
import java.awt.image.BufferedImage;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class MaskFill extends GraphicsPrimitive {
  public static final String methodSignature = "MaskFill(...)".toString();
  
  public static final String fillPgramSignature = "FillAAPgram(...)".toString();
  
  public static final String drawPgramSignature = "DrawAAPgram(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache fillcache = new RenderCache(10);
  
  public static MaskFill locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (MaskFill)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public static MaskFill locatePrim(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (MaskFill)GraphicsPrimitiveMgr.locatePrim(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public static MaskFill getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    Object object = fillcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (object != null)
      return (MaskFill)object; 
    MaskFill maskFill = locatePrim(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (maskFill != null)
      fillcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, maskFill); 
    return maskFill;
  }
  
  protected MaskFill(String paramString, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramString, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected MaskFill(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public MaskFill(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6);
  
  public native void FillAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public native void DrawAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8);
  
  public boolean canDoParallelograms() { return (getNativePrim() != 0L); }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    if (SurfaceType.OpaqueColor.equals(paramSurfaceType1) || SurfaceType.AnyColor.equals(paramSurfaceType1)) {
      if (CompositeType.Xor.equals(paramCompositeType))
        throw new InternalError("Cannot construct MaskFill for XOR mode"); 
      return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    } 
    throw new InternalError("MaskFill can only fill with colors");
  }
  
  public GraphicsPrimitive traceWrap() { return new TraceMaskFill(this); }
  
  static  {
    GraphicsPrimitiveMgr.registerGeneral(new MaskFill(null, null, null));
  }
  
  private static class General extends MaskFill {
    FillRect fillop;
    
    MaskBlit maskop;
    
    public General(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) {
      super(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
      this.fillop = FillRect.locate(param1SurfaceType1, CompositeType.SrcNoEa, SurfaceType.IntArgb);
      this.maskop = MaskBlit.locate(SurfaceType.IntArgb, param1CompositeType, param1SurfaceType2);
    }
    
    public void MaskFill(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, byte[] param1ArrayOfByte, int param1Int5, int param1Int6) {
      BufferedImage bufferedImage = new BufferedImage(param1Int3, param1Int4, 2);
      SurfaceData surfaceData = BufImgSurfaceData.createData(bufferedImage);
      Region region = param1SunGraphics2D.clipRegion;
      param1SunGraphics2D.clipRegion = null;
      int i = param1SunGraphics2D.pixel;
      param1SunGraphics2D.pixel = surfaceData.pixelFor(param1SunGraphics2D.getColor());
      this.fillop.FillRect(param1SunGraphics2D, surfaceData, 0, 0, param1Int3, param1Int4);
      param1SunGraphics2D.pixel = i;
      param1SunGraphics2D.clipRegion = region;
      this.maskop.MaskBlit(surfaceData, param1SurfaceData, param1Composite, null, 0, 0, param1Int1, param1Int2, param1Int3, param1Int4, param1ArrayOfByte, param1Int5, param1Int6);
    }
  }
  
  private static class TraceMaskFill extends MaskFill {
    MaskFill target;
    
    MaskFill fillPgramTarget;
    
    MaskFill drawPgramTarget;
    
    public TraceMaskFill(MaskFill param1MaskFill) {
      super(param1MaskFill.getSourceType(), param1MaskFill.getCompositeType(), param1MaskFill.getDestType());
      this.target = param1MaskFill;
      this.fillPgramTarget = new MaskFill(fillPgramSignature, param1MaskFill.getSourceType(), param1MaskFill.getCompositeType(), param1MaskFill.getDestType());
      this.drawPgramTarget = new MaskFill(drawPgramSignature, param1MaskFill.getSourceType(), param1MaskFill.getCompositeType(), param1MaskFill.getDestType());
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void MaskFill(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, byte[] param1ArrayOfByte, int param1Int5, int param1Int6) {
      tracePrimitive(this.target);
      this.target.MaskFill(param1SunGraphics2D, param1SurfaceData, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1ArrayOfByte, param1Int5, param1Int6);
    }
    
    public void FillAAPgram(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, Composite param1Composite, double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6) {
      tracePrimitive(this.fillPgramTarget);
      this.target.FillAAPgram(param1SunGraphics2D, param1SurfaceData, param1Composite, param1Double1, param1Double2, param1Double3, param1Double4, param1Double5, param1Double6);
    }
    
    public void DrawAAPgram(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, Composite param1Composite, double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8) {
      tracePrimitive(this.drawPgramTarget);
      this.target.DrawAAPgram(param1SunGraphics2D, param1SurfaceData, param1Composite, param1Double1, param1Double2, param1Double3, param1Double4, param1Double5, param1Double6, param1Double7, param1Double8);
    }
    
    public boolean canDoParallelograms() { return this.target.canDoParallelograms(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\MaskFill.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */