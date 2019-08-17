package sun.java2d.loops;

import java.awt.Composite;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class MaskBlit extends GraphicsPrimitive {
  public static final String methodSignature = "MaskBlit(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache blitcache = new RenderCache(20);
  
  public static MaskBlit locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (MaskBlit)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public static MaskBlit getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    Object object = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (object != null)
      return (MaskBlit)object; 
    MaskBlit maskBlit = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (maskBlit == null) {
      System.out.println("mask blit loop not found for:");
      System.out.println("src:  " + paramSurfaceType1);
      System.out.println("comp: " + paramCompositeType);
      System.out.println("dst:  " + paramSurfaceType2);
    } else {
      blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, maskBlit);
    } 
    return maskBlit;
  }
  
  protected MaskBlit(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public MaskBlit(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void MaskBlit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, byte[] paramArrayOfByte, int paramInt7, int paramInt8);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    if (CompositeType.Xor.equals(paramCompositeType))
      throw new InternalError("Cannot construct MaskBlit for XOR mode"); 
    General general = new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    setupGeneralBinaryOp(general);
    return general;
  }
  
  public GraphicsPrimitive traceWrap() { return new TraceMaskBlit(this); }
  
  static  {
    GraphicsPrimitiveMgr.registerGeneral(new MaskBlit(null, null, null));
  }
  
  private static class General extends MaskBlit implements GraphicsPrimitive.GeneralBinaryOp {
    Blit convertsrc;
    
    Blit convertdst;
    
    MaskBlit performop;
    
    Blit convertresult;
    
    WeakReference srcTmp;
    
    WeakReference dstTmp;
    
    public General(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) { super(param1SurfaceType1, param1CompositeType, param1SurfaceType2); }
    
    public void setPrimitives(Blit param1Blit1, Blit param1Blit2, GraphicsPrimitive param1GraphicsPrimitive, Blit param1Blit3) {
      this.convertsrc = param1Blit1;
      this.convertdst = param1Blit2;
      this.performop = (MaskBlit)param1GraphicsPrimitive;
      this.convertresult = param1Blit3;
    }
    
    public void MaskBlit(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, byte[] param1ArrayOfByte, int param1Int7, int param1Int8) {
      byte b4;
      byte b3;
      byte b2;
      byte b1;
      Region region;
      SurfaceData surfaceData2;
      SurfaceData surfaceData1;
      if (this.convertsrc == null) {
        surfaceData1 = param1SurfaceData1;
        b1 = param1Int1;
        b2 = param1Int2;
      } else {
        SurfaceData surfaceData = null;
        if (this.srcTmp != null)
          surfaceData = (SurfaceData)this.srcTmp.get(); 
        surfaceData1 = convertFrom(this.convertsrc, param1SurfaceData1, param1Int1, param1Int2, param1Int5, param1Int6, surfaceData);
        b1 = 0;
        b2 = 0;
        if (surfaceData1 != surfaceData)
          this.srcTmp = new WeakReference(surfaceData1); 
      } 
      if (this.convertdst == null) {
        surfaceData2 = param1SurfaceData2;
        b3 = param1Int3;
        b4 = param1Int4;
        region = param1Region;
      } else {
        SurfaceData surfaceData = null;
        if (this.dstTmp != null)
          surfaceData = (SurfaceData)this.dstTmp.get(); 
        surfaceData2 = convertFrom(this.convertdst, param1SurfaceData2, param1Int3, param1Int4, param1Int5, param1Int6, surfaceData);
        b3 = 0;
        b4 = 0;
        region = null;
        if (surfaceData2 != surfaceData)
          this.dstTmp = new WeakReference(surfaceData2); 
      } 
      this.performop.MaskBlit(surfaceData1, surfaceData2, param1Composite, region, b1, b2, b3, b4, param1Int5, param1Int6, param1ArrayOfByte, param1Int7, param1Int8);
      if (this.convertresult != null)
        convertTo(this.convertresult, surfaceData2, param1SurfaceData2, param1Region, param1Int3, param1Int4, param1Int5, param1Int6); 
    }
  }
  
  private static class TraceMaskBlit extends MaskBlit {
    MaskBlit target;
    
    public TraceMaskBlit(MaskBlit param1MaskBlit) {
      super(param1MaskBlit.getNativePrim(), param1MaskBlit.getSourceType(), param1MaskBlit.getCompositeType(), param1MaskBlit.getDestType());
      this.target = param1MaskBlit;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void MaskBlit(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, byte[] param1ArrayOfByte, int param1Int7, int param1Int8) {
      tracePrimitive(this.target);
      this.target.MaskBlit(param1SurfaceData1, param1SurfaceData2, param1Composite, param1Region, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1ArrayOfByte, param1Int7, param1Int8);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\MaskBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */