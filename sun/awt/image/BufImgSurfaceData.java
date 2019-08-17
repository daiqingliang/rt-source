package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;

public class BufImgSurfaceData extends SurfaceData {
  BufferedImage bufImg;
  
  private BufferedImageGraphicsConfig graphicsConfig;
  
  RenderLoops solidloops;
  
  private static final int DCM_RGBX_RED_MASK = -16777216;
  
  private static final int DCM_RGBX_GREEN_MASK = 16711680;
  
  private static final int DCM_RGBX_BLUE_MASK = 65280;
  
  private static final int DCM_555X_RED_MASK = 63488;
  
  private static final int DCM_555X_GREEN_MASK = 1984;
  
  private static final int DCM_555X_BLUE_MASK = 62;
  
  private static final int DCM_4444_RED_MASK = 3840;
  
  private static final int DCM_4444_GREEN_MASK = 240;
  
  private static final int DCM_4444_BLUE_MASK = 15;
  
  private static final int DCM_4444_ALPHA_MASK = 61440;
  
  private static final int DCM_ARGBBM_ALPHA_MASK = 16777216;
  
  private static final int DCM_ARGBBM_RED_MASK = 16711680;
  
  private static final int DCM_ARGBBM_GREEN_MASK = 65280;
  
  private static final int DCM_ARGBBM_BLUE_MASK = 255;
  
  private static final int CACHE_SIZE = 5;
  
  private static RenderLoops[] loopcache;
  
  private static SurfaceType[] typecache;
  
  private static native void initIDs(Class paramClass1, Class paramClass2);
  
  public static SurfaceData createData(BufferedImage paramBufferedImage) {
    SampleModel sampleModel;
    SurfaceType surfaceType;
    SurfaceData surfaceData;
    if (paramBufferedImage == null)
      throw new NullPointerException("BufferedImage cannot be null"); 
    ColorModel colorModel = paramBufferedImage.getColorModel();
    int i = paramBufferedImage.getType();
    switch (i) {
      case 4:
        surfaceData = createDataIC(paramBufferedImage, SurfaceType.IntBgr);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 1:
        surfaceData = createDataIC(paramBufferedImage, SurfaceType.IntRgb);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 2:
        surfaceData = createDataIC(paramBufferedImage, SurfaceType.IntArgb);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 3:
        surfaceData = createDataIC(paramBufferedImage, SurfaceType.IntArgbPre);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 5:
        surfaceData = createDataBC(paramBufferedImage, SurfaceType.ThreeByteBgr, 2);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 6:
        surfaceData = createDataBC(paramBufferedImage, SurfaceType.FourByteAbgr, 3);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 7:
        surfaceData = createDataBC(paramBufferedImage, SurfaceType.FourByteAbgrPre, 3);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 8:
        surfaceData = createDataSC(paramBufferedImage, SurfaceType.Ushort565Rgb, null);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 9:
        surfaceData = createDataSC(paramBufferedImage, SurfaceType.Ushort555Rgb, null);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 13:
        switch (colorModel.getTransparency()) {
          case 1:
            if (isOpaqueGray((IndexColorModel)colorModel)) {
              SurfaceType surfaceType1 = SurfaceType.Index8Gray;
              break;
            } 
            surfaceType = SurfaceType.ByteIndexedOpaque;
            break;
          case 2:
            surfaceType = SurfaceType.ByteIndexedBm;
            break;
          case 3:
            surfaceType = SurfaceType.ByteIndexed;
            break;
          default:
            throw new InternalError("Unrecognized transparency");
        } 
        surfaceData = createDataBC(paramBufferedImage, surfaceType, 0);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 10:
        surfaceData = createDataBC(paramBufferedImage, SurfaceType.ByteGray, 0);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 11:
        surfaceData = createDataSC(paramBufferedImage, SurfaceType.UshortGray, null);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
      case 12:
        sampleModel = paramBufferedImage.getRaster().getSampleModel();
        switch (sampleModel.getSampleSize(0)) {
          case 1:
            surfaceType = SurfaceType.ByteBinary1Bit;
            break;
          case 2:
            surfaceType = SurfaceType.ByteBinary2Bit;
            break;
          case 4:
            surfaceType = SurfaceType.ByteBinary4Bit;
            break;
          default:
            throw new InternalError("Unrecognized pixel size");
        } 
        surfaceData = createDataBP(paramBufferedImage, surfaceType);
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
    } 
    WritableRaster writableRaster = paramBufferedImage.getRaster();
    int j = writableRaster.getNumBands();
    if (writableRaster instanceof IntegerComponentRaster && writableRaster.getNumDataElements() == 1 && ((IntegerComponentRaster)writableRaster).getPixelStride() == 1) {
      SurfaceType surfaceType1 = SurfaceType.AnyInt;
      if (colorModel instanceof DirectColorModel) {
        DirectColorModel directColorModel = (DirectColorModel)colorModel;
        int k = directColorModel.getAlphaMask();
        int m = directColorModel.getRedMask();
        int n = directColorModel.getGreenMask();
        int i1 = directColorModel.getBlueMask();
        if (j == 3 && k == 0 && m == -16777216 && n == 16711680 && i1 == 65280) {
          surfaceType1 = SurfaceType.IntRgbx;
        } else if (j == 4 && k == 16777216 && m == 16711680 && n == 65280 && i1 == 255) {
          surfaceType1 = SurfaceType.IntArgbBm;
        } else {
          surfaceType1 = SurfaceType.AnyDcm;
        } 
      } 
      surfaceData = createDataIC(paramBufferedImage, surfaceType1);
    } else if (writableRaster instanceof ShortComponentRaster && writableRaster.getNumDataElements() == 1 && ((ShortComponentRaster)writableRaster).getPixelStride() == 1) {
      SurfaceType surfaceType1 = SurfaceType.AnyShort;
      IndexColorModel indexColorModel = null;
      if (colorModel instanceof DirectColorModel) {
        DirectColorModel directColorModel = (DirectColorModel)colorModel;
        int k = directColorModel.getAlphaMask();
        int m = directColorModel.getRedMask();
        int n = directColorModel.getGreenMask();
        int i1 = directColorModel.getBlueMask();
        if (j == 3 && k == 0 && m == 63488 && n == 1984 && i1 == 62) {
          surfaceType1 = SurfaceType.Ushort555Rgbx;
        } else if (j == 4 && k == 61440 && m == 3840 && n == 240 && i1 == 15) {
          surfaceType1 = SurfaceType.Ushort4444Argb;
        } 
      } else if (colorModel instanceof IndexColorModel) {
        indexColorModel = (IndexColorModel)colorModel;
        if (indexColorModel.getPixelSize() == 12) {
          if (isOpaqueGray(indexColorModel)) {
            surfaceType1 = SurfaceType.Index12Gray;
          } else {
            surfaceType1 = SurfaceType.UshortIndexed;
          } 
        } else {
          indexColorModel = null;
        } 
      } 
      surfaceData = createDataSC(paramBufferedImage, surfaceType1, indexColorModel);
    } else {
      surfaceData = new BufImgSurfaceData(writableRaster.getDataBuffer(), paramBufferedImage, SurfaceType.Custom);
    } 
    ((BufImgSurfaceData)surfaceData).initSolidLoops();
    return surfaceData;
  }
  
  public static SurfaceData createData(Raster paramRaster, ColorModel paramColorModel) { throw new InternalError("SurfaceData not implemented for Raster/CM"); }
  
  public static SurfaceData createDataIC(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType) {
    IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)paramBufferedImage.getRaster();
    BufImgSurfaceData bufImgSurfaceData = new BufImgSurfaceData(integerComponentRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
    bufImgSurfaceData.initRaster(integerComponentRaster.getDataStorage(), integerComponentRaster.getDataOffset(0) * 4, 0, integerComponentRaster.getWidth(), integerComponentRaster.getHeight(), integerComponentRaster.getPixelStride() * 4, integerComponentRaster.getScanlineStride() * 4, null);
    return bufImgSurfaceData;
  }
  
  public static SurfaceData createDataSC(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType, IndexColorModel paramIndexColorModel) {
    ShortComponentRaster shortComponentRaster = (ShortComponentRaster)paramBufferedImage.getRaster();
    BufImgSurfaceData bufImgSurfaceData = new BufImgSurfaceData(shortComponentRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
    bufImgSurfaceData.initRaster(shortComponentRaster.getDataStorage(), shortComponentRaster.getDataOffset(0) * 2, 0, shortComponentRaster.getWidth(), shortComponentRaster.getHeight(), shortComponentRaster.getPixelStride() * 2, shortComponentRaster.getScanlineStride() * 2, paramIndexColorModel);
    return bufImgSurfaceData;
  }
  
  public static SurfaceData createDataBC(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType, int paramInt) {
    ByteComponentRaster byteComponentRaster = (ByteComponentRaster)paramBufferedImage.getRaster();
    BufImgSurfaceData bufImgSurfaceData = new BufImgSurfaceData(byteComponentRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
    ColorModel colorModel = paramBufferedImage.getColorModel();
    IndexColorModel indexColorModel = (colorModel instanceof IndexColorModel) ? (IndexColorModel)colorModel : null;
    bufImgSurfaceData.initRaster(byteComponentRaster.getDataStorage(), byteComponentRaster.getDataOffset(paramInt), 0, byteComponentRaster.getWidth(), byteComponentRaster.getHeight(), byteComponentRaster.getPixelStride(), byteComponentRaster.getScanlineStride(), indexColorModel);
    return bufImgSurfaceData;
  }
  
  public static SurfaceData createDataBP(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType) {
    BytePackedRaster bytePackedRaster = (BytePackedRaster)paramBufferedImage.getRaster();
    BufImgSurfaceData bufImgSurfaceData = new BufImgSurfaceData(bytePackedRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
    ColorModel colorModel = paramBufferedImage.getColorModel();
    IndexColorModel indexColorModel = (colorModel instanceof IndexColorModel) ? (IndexColorModel)colorModel : null;
    bufImgSurfaceData.initRaster(bytePackedRaster.getDataStorage(), bytePackedRaster.getDataBitOffset() / 8, bytePackedRaster.getDataBitOffset() & 0x7, bytePackedRaster.getWidth(), bytePackedRaster.getHeight(), 0, bytePackedRaster.getScanlineStride(), indexColorModel);
    return bufImgSurfaceData;
  }
  
  public RenderLoops getRenderLoops(SunGraphics2D paramSunGraphics2D) { return (paramSunGraphics2D.paintState <= 1 && paramSunGraphics2D.compositeState <= 0) ? this.solidloops : super.getRenderLoops(paramSunGraphics2D); }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return this.bufImg.getRaster(); }
  
  protected native void initRaster(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, IndexColorModel paramIndexColorModel);
  
  public BufImgSurfaceData(DataBuffer paramDataBuffer, BufferedImage paramBufferedImage, SurfaceType paramSurfaceType) {
    super(SunWritableRaster.stealTrackable(paramDataBuffer), paramSurfaceType, paramBufferedImage.getColorModel());
    this.bufImg = paramBufferedImage;
  }
  
  protected BufImgSurfaceData(SurfaceType paramSurfaceType, ColorModel paramColorModel) { super(paramSurfaceType, paramColorModel); }
  
  public void initSolidLoops() { this.solidloops = getSolidLoops(getSurfaceType()); }
  
  public static RenderLoops getSolidLoops(SurfaceType paramSurfaceType) {
    for (byte b = 4; b >= 0; b--) {
      SurfaceType surfaceType = typecache[b];
      if (surfaceType == paramSurfaceType)
        return loopcache[b]; 
      if (surfaceType == null)
        break; 
    } 
    RenderLoops renderLoops = makeRenderLoops(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, paramSurfaceType);
    System.arraycopy(loopcache, 1, loopcache, 0, 4);
    System.arraycopy(typecache, 1, typecache, 0, 4);
    loopcache[4] = renderLoops;
    typecache[4] = paramSurfaceType;
    return renderLoops;
  }
  
  public SurfaceData getReplacement() { return restoreContents(this.bufImg); }
  
  public GraphicsConfiguration getDeviceConfiguration() {
    if (this.graphicsConfig == null)
      this.graphicsConfig = BufferedImageGraphicsConfig.getConfig(this.bufImg); 
    return this.graphicsConfig;
  }
  
  public Rectangle getBounds() { return new Rectangle(this.bufImg.getWidth(), this.bufImg.getHeight()); }
  
  protected void checkCustomComposite() {}
  
  private static native void freeNativeICMData(long paramLong);
  
  public Object getDestination() { return this.bufImg; }
  
  static  {
    initIDs(IndexColorModel.class, ICMColorData.class);
    loopcache = new RenderLoops[5];
    typecache = new SurfaceType[5];
  }
  
  public static final class ICMColorData {
    private long pData = 0L;
    
    private ICMColorData(long param1Long) { this.pData = param1Long; }
    
    public void finalize() {
      if (this.pData != 0L) {
        BufImgSurfaceData.freeNativeICMData(this.pData);
        this.pData = 0L;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\BufImgSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */