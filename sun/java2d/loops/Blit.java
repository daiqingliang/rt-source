package sun.java2d.loops;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.SpanIterator;

public class Blit extends GraphicsPrimitive {
  public static final String methodSignature = "Blit(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache blitcache = new RenderCache(20);
  
  public static Blit locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (Blit)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public static Blit getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    Object object = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (object != null)
      return (Blit)object; 
    Blit blit = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (blit == null) {
      System.out.println("blit loop not found for:");
      System.out.println("src:  " + paramSurfaceType1);
      System.out.println("comp: " + paramCompositeType);
      System.out.println("dst:  " + paramSurfaceType2);
    } else {
      blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, blit);
    } 
    return blit;
  }
  
  protected Blit(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public Blit(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    if (paramCompositeType.isDerivedFrom(CompositeType.Xor)) {
      GeneralXorBlit generalXorBlit = new GeneralXorBlit(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
      setupGeneralBinaryOp(generalXorBlit);
      return generalXorBlit;
    } 
    return paramCompositeType.isDerivedFrom(CompositeType.AnyAlpha) ? new GeneralMaskBlit(paramSurfaceType1, paramCompositeType, paramSurfaceType2) : AnyBlit.instance;
  }
  
  public GraphicsPrimitive traceWrap() { return new TraceBlit(this); }
  
  static  {
    GraphicsPrimitiveMgr.registerGeneral(new Blit(null, null, null));
  }
  
  private static class AnyBlit extends Blit {
    public static AnyBlit instance = new AnyBlit();
    
    public AnyBlit() { super(SurfaceType.Any, CompositeType.Any, SurfaceType.Any); }
    
    public void Blit(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
      ColorModel colorModel1 = param1SurfaceData1.getColorModel();
      ColorModel colorModel2 = param1SurfaceData2.getColorModel();
      CompositeContext compositeContext = param1Composite.createContext(colorModel1, colorModel2, new RenderingHints(null));
      Raster raster = param1SurfaceData1.getRaster(param1Int1, param1Int2, param1Int5, param1Int6);
      WritableRaster writableRaster = (WritableRaster)param1SurfaceData2.getRaster(param1Int3, param1Int4, param1Int5, param1Int6);
      if (param1Region == null)
        param1Region = Region.getInstanceXYWH(param1Int3, param1Int4, param1Int5, param1Int6); 
      int[] arrayOfInt = { param1Int3, param1Int4, param1Int3 + param1Int5, param1Int4 + param1Int6 };
      SpanIterator spanIterator = param1Region.getSpanIterator(arrayOfInt);
      param1Int1 -= param1Int3;
      param1Int2 -= param1Int4;
      while (spanIterator.nextSpan(arrayOfInt)) {
        int i = arrayOfInt[2] - arrayOfInt[0];
        int j = arrayOfInt[3] - arrayOfInt[1];
        Raster raster1 = raster.createChild(param1Int1 + arrayOfInt[0], param1Int2 + arrayOfInt[1], i, j, 0, 0, null);
        WritableRaster writableRaster1 = writableRaster.createWritableChild(arrayOfInt[0], arrayOfInt[1], i, j, 0, 0, null);
        compositeContext.compose(raster1, writableRaster1, writableRaster1);
      } 
      compositeContext.dispose();
    }
  }
  
  private static class GeneralMaskBlit extends Blit {
    MaskBlit performop;
    
    public GeneralMaskBlit(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) {
      super(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
      this.performop = MaskBlit.locate(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
    }
    
    public void Blit(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) { this.performop.MaskBlit(param1SurfaceData1, param1SurfaceData2, param1Composite, param1Region, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, null, 0, 0); }
  }
  
  private static class GeneralXorBlit extends Blit implements GraphicsPrimitive.GeneralBinaryOp {
    Blit convertsrc;
    
    Blit convertdst;
    
    Blit performop;
    
    Blit convertresult;
    
    WeakReference srcTmp;
    
    WeakReference dstTmp;
    
    public GeneralXorBlit(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) { super(param1SurfaceType1, param1CompositeType, param1SurfaceType2); }
    
    public void setPrimitives(Blit param1Blit1, Blit param1Blit2, GraphicsPrimitive param1GraphicsPrimitive, Blit param1Blit3) {
      this.convertsrc = param1Blit1;
      this.convertdst = param1Blit2;
      this.performop = (Blit)param1GraphicsPrimitive;
      this.convertresult = param1Blit3;
    }
    
    public void Blit(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
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
      this.performop.Blit(surfaceData1, surfaceData2, param1Composite, region, b1, b2, b3, b4, param1Int5, param1Int6);
      if (this.convertresult != null)
        convertTo(this.convertresult, surfaceData2, param1SurfaceData2, param1Region, param1Int3, param1Int4, param1Int5, param1Int6); 
    }
  }
  
  private static class TraceBlit extends Blit {
    Blit target;
    
    public TraceBlit(Blit param1Blit) {
      super(param1Blit.getSourceType(), param1Blit.getCompositeType(), param1Blit.getDestType());
      this.target = param1Blit;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void Blit(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
      tracePrimitive(this.target);
      this.target.Blit(param1SurfaceData1, param1SurfaceData2, param1Composite, param1Region, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\Blit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */