package sun.java2d.cmm.lcms;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ShortComponentRaster;

class LCMSImageLayout {
  public static final int SWAPFIRST = 16384;
  
  public static final int DOSWAP = 1024;
  
  public static final int PT_RGB_8 = CHANNELS_SH(3) | BYTES_SH(1);
  
  public static final int PT_GRAY_8 = CHANNELS_SH(1) | BYTES_SH(1);
  
  public static final int PT_GRAY_16 = CHANNELS_SH(1) | BYTES_SH(2);
  
  public static final int PT_RGBA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
  
  public static final int PT_ARGB_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x4000;
  
  public static final int PT_BGR_8 = 0x400 | CHANNELS_SH(3) | BYTES_SH(1);
  
  public static final int PT_ABGR_8 = 0x400 | EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
  
  public static final int PT_BGRA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x400 | 0x4000;
  
  public static final int DT_BYTE = 0;
  
  public static final int DT_SHORT = 1;
  
  public static final int DT_INT = 2;
  
  public static final int DT_DOUBLE = 3;
  
  boolean isIntPacked = false;
  
  int pixelType;
  
  int dataType;
  
  int width;
  
  int height;
  
  int nextRowOffset;
  
  private int nextPixelOffset;
  
  int offset;
  
  private boolean imageAtOnce = false;
  
  Object dataArray;
  
  private int dataArrayLength;
  
  public static int BYTES_SH(int paramInt) { return paramInt; }
  
  public static int EXTRA_SH(int paramInt) { return paramInt << 7; }
  
  public static int CHANNELS_SH(int paramInt) { return paramInt << 3; }
  
  private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3) throws ImageLayoutException {
    this.pixelType = paramInt2;
    this.width = paramInt1;
    this.height = 1;
    this.nextPixelOffset = paramInt3;
    this.nextRowOffset = safeMult(paramInt3, paramInt1);
    this.offset = 0;
  }
  
  private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws ImageLayoutException {
    this.pixelType = paramInt3;
    this.width = paramInt1;
    this.height = paramInt2;
    this.nextPixelOffset = paramInt4;
    this.nextRowOffset = safeMult(paramInt4, paramInt1);
    this.offset = 0;
  }
  
  public LCMSImageLayout(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) throws ImageLayoutException {
    this(paramInt1, paramInt2, paramInt3);
    this.dataType = 0;
    this.dataArray = paramArrayOfByte;
    this.dataArrayLength = paramArrayOfByte.length;
    verify();
  }
  
  public LCMSImageLayout(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3) throws ImageLayoutException {
    this(paramInt1, paramInt2, paramInt3);
    this.dataType = 1;
    this.dataArray = paramArrayOfShort;
    this.dataArrayLength = 2 * paramArrayOfShort.length;
    verify();
  }
  
  public LCMSImageLayout(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) throws ImageLayoutException {
    this(paramInt1, paramInt2, paramInt3);
    this.dataType = 2;
    this.dataArray = paramArrayOfInt;
    this.dataArrayLength = 4 * paramArrayOfInt.length;
    verify();
  }
  
  public LCMSImageLayout(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3) throws ImageLayoutException {
    this(paramInt1, paramInt2, paramInt3);
    this.dataType = 3;
    this.dataArray = paramArrayOfDouble;
    this.dataArrayLength = 8 * paramArrayOfDouble.length;
    verify();
  }
  
  private LCMSImageLayout() {}
  
  public static LCMSImageLayout createImageLayout(BufferedImage paramBufferedImage) throws ImageLayoutException {
    int i;
    ByteComponentRaster byteComponentRaster;
    IntegerComponentRaster integerComponentRaster;
    ShortComponentRaster shortComponentRaster;
    ColorModel colorModel;
    LCMSImageLayout lCMSImageLayout = new LCMSImageLayout();
    switch (paramBufferedImage.getType()) {
      case 1:
        lCMSImageLayout.pixelType = PT_ARGB_8;
        lCMSImageLayout.isIntPacked = true;
        break;
      case 2:
        lCMSImageLayout.pixelType = PT_ARGB_8;
        lCMSImageLayout.isIntPacked = true;
        break;
      case 4:
        lCMSImageLayout.pixelType = PT_ABGR_8;
        lCMSImageLayout.isIntPacked = true;
        break;
      case 5:
        lCMSImageLayout.pixelType = PT_BGR_8;
        break;
      case 6:
        lCMSImageLayout.pixelType = PT_ABGR_8;
        break;
      case 10:
        lCMSImageLayout.pixelType = PT_GRAY_8;
        break;
      case 11:
        lCMSImageLayout.pixelType = PT_GRAY_16;
        break;
      default:
        colorModel = paramBufferedImage.getColorModel();
        if (colorModel instanceof ComponentColorModel) {
          ComponentColorModel componentColorModel = (ComponentColorModel)colorModel;
          int[] arrayOfInt = componentColorModel.getComponentSize();
          for (int j : arrayOfInt) {
            if (j != 8)
              return null; 
          } 
          return createImageLayout(paramBufferedImage.getRaster());
        } 
        return null;
    } 
    lCMSImageLayout.width = paramBufferedImage.getWidth();
    lCMSImageLayout.height = paramBufferedImage.getHeight();
    switch (paramBufferedImage.getType()) {
      case 1:
      case 2:
      case 4:
        integerComponentRaster = (IntegerComponentRaster)paramBufferedImage.getRaster();
        lCMSImageLayout.nextRowOffset = safeMult(4, integerComponentRaster.getScanlineStride());
        lCMSImageLayout.nextPixelOffset = safeMult(4, integerComponentRaster.getPixelStride());
        lCMSImageLayout.offset = safeMult(4, integerComponentRaster.getDataOffset(0));
        lCMSImageLayout.dataArray = integerComponentRaster.getDataStorage();
        lCMSImageLayout.dataArrayLength = 4 * integerComponentRaster.getDataStorage().length;
        lCMSImageLayout.dataType = 2;
        if (lCMSImageLayout.nextRowOffset == lCMSImageLayout.width * 4 * integerComponentRaster.getPixelStride())
          lCMSImageLayout.imageAtOnce = true; 
        lCMSImageLayout.verify();
        return lCMSImageLayout;
      case 5:
      case 6:
        byteComponentRaster = (ByteComponentRaster)paramBufferedImage.getRaster();
        lCMSImageLayout.nextRowOffset = byteComponentRaster.getScanlineStride();
        lCMSImageLayout.nextPixelOffset = byteComponentRaster.getPixelStride();
        i = paramBufferedImage.getSampleModel().getNumBands() - 1;
        lCMSImageLayout.offset = byteComponentRaster.getDataOffset(i);
        lCMSImageLayout.dataArray = byteComponentRaster.getDataStorage();
        lCMSImageLayout.dataArrayLength = byteComponentRaster.getDataStorage().length;
        lCMSImageLayout.dataType = 0;
        if (lCMSImageLayout.nextRowOffset == lCMSImageLayout.width * byteComponentRaster.getPixelStride())
          lCMSImageLayout.imageAtOnce = true; 
        lCMSImageLayout.verify();
        return lCMSImageLayout;
      case 10:
        byteComponentRaster = (ByteComponentRaster)paramBufferedImage.getRaster();
        lCMSImageLayout.nextRowOffset = byteComponentRaster.getScanlineStride();
        lCMSImageLayout.nextPixelOffset = byteComponentRaster.getPixelStride();
        lCMSImageLayout.dataArrayLength = byteComponentRaster.getDataStorage().length;
        lCMSImageLayout.offset = byteComponentRaster.getDataOffset(0);
        lCMSImageLayout.dataArray = byteComponentRaster.getDataStorage();
        lCMSImageLayout.dataType = 0;
        if (lCMSImageLayout.nextRowOffset == lCMSImageLayout.width * byteComponentRaster.getPixelStride())
          lCMSImageLayout.imageAtOnce = true; 
        lCMSImageLayout.verify();
        return lCMSImageLayout;
      case 11:
        shortComponentRaster = (ShortComponentRaster)paramBufferedImage.getRaster();
        lCMSImageLayout.nextRowOffset = safeMult(2, shortComponentRaster.getScanlineStride());
        lCMSImageLayout.nextPixelOffset = safeMult(2, shortComponentRaster.getPixelStride());
        lCMSImageLayout.offset = safeMult(2, shortComponentRaster.getDataOffset(0));
        lCMSImageLayout.dataArray = shortComponentRaster.getDataStorage();
        lCMSImageLayout.dataArrayLength = 2 * shortComponentRaster.getDataStorage().length;
        lCMSImageLayout.dataType = 1;
        if (lCMSImageLayout.nextRowOffset == lCMSImageLayout.width * 2 * shortComponentRaster.getPixelStride())
          lCMSImageLayout.imageAtOnce = true; 
        lCMSImageLayout.verify();
        return lCMSImageLayout;
    } 
    return null;
  }
  
  private void verify() {
    if (this.offset < 0 || this.offset >= this.dataArrayLength)
      throw new ImageLayoutException("Invalid image layout"); 
    if (this.nextPixelOffset != getBytesPerPixel(this.pixelType))
      throw new ImageLayoutException("Invalid image layout"); 
    int i = safeMult(this.nextRowOffset, this.height - 1);
    int j = safeMult(this.nextPixelOffset, this.width - 1);
    j = safeAdd(j, i);
    int k = safeAdd(this.offset, j);
    if (k < 0 || k >= this.dataArrayLength)
      throw new ImageLayoutException("Invalid image layout"); 
  }
  
  static int safeAdd(int paramInt1, int paramInt2) throws ImageLayoutException {
    long l = paramInt1;
    l += paramInt2;
    if (l < -2147483648L || l > 2147483647L)
      throw new ImageLayoutException("Invalid image layout"); 
    return (int)l;
  }
  
  static int safeMult(int paramInt1, int paramInt2) throws ImageLayoutException {
    long l = paramInt1;
    l *= paramInt2;
    if (l < -2147483648L || l > 2147483647L)
      throw new ImageLayoutException("Invalid image layout"); 
    return (int)l;
  }
  
  public static LCMSImageLayout createImageLayout(Raster paramRaster) {
    LCMSImageLayout lCMSImageLayout = new LCMSImageLayout();
    if (paramRaster instanceof ByteComponentRaster && paramRaster.getSampleModel() instanceof ComponentSampleModel) {
      ByteComponentRaster byteComponentRaster = (ByteComponentRaster)paramRaster;
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)paramRaster.getSampleModel();
      lCMSImageLayout.pixelType = CHANNELS_SH(byteComponentRaster.getNumBands()) | BYTES_SH(1);
      int[] arrayOfInt = componentSampleModel.getBandOffsets();
      BandOrder bandOrder = BandOrder.getBandOrder(arrayOfInt);
      int i = 0;
      switch (bandOrder) {
        case INVERTED:
          lCMSImageLayout.pixelType |= 0x400;
          i = componentSampleModel.getNumBands() - 1;
          break;
        case DIRECT:
          break;
        default:
          return null;
      } 
      lCMSImageLayout.nextRowOffset = byteComponentRaster.getScanlineStride();
      lCMSImageLayout.nextPixelOffset = byteComponentRaster.getPixelStride();
      lCMSImageLayout.offset = byteComponentRaster.getDataOffset(i);
      lCMSImageLayout.dataArray = byteComponentRaster.getDataStorage();
      lCMSImageLayout.dataType = 0;
      lCMSImageLayout.width = byteComponentRaster.getWidth();
      lCMSImageLayout.height = byteComponentRaster.getHeight();
      if (lCMSImageLayout.nextRowOffset == lCMSImageLayout.width * byteComponentRaster.getPixelStride())
        lCMSImageLayout.imageAtOnce = true; 
      return lCMSImageLayout;
    } 
    return null;
  }
  
  private static int getBytesPerPixel(int paramInt) {
    int i = 0x7 & paramInt;
    int j = 0xF & paramInt >> 3;
    int k = 0x7 & paramInt >> 7;
    return i * (j + k);
  }
  
  private enum BandOrder {
    DIRECT, INVERTED, ARBITRARY, UNKNOWN;
    
    public static BandOrder getBandOrder(int[] param1ArrayOfInt) {
      BandOrder bandOrder = UNKNOWN;
      int i = param1ArrayOfInt.length;
      for (int j = 0; bandOrder != ARBITRARY && j < param1ArrayOfInt.length; j++) {
        switch (LCMSImageLayout.null.$SwitchMap$sun$java2d$cmm$lcms$LCMSImageLayout$BandOrder[bandOrder.ordinal()]) {
          case 1:
            if (param1ArrayOfInt[j] == j) {
              bandOrder = DIRECT;
              break;
            } 
            if (param1ArrayOfInt[j] == i - 1 - j) {
              bandOrder = INVERTED;
              break;
            } 
            bandOrder = ARBITRARY;
            break;
          case 2:
            if (param1ArrayOfInt[j] != j)
              bandOrder = ARBITRARY; 
            break;
          case 3:
            if (param1ArrayOfInt[j] != i - 1 - j)
              bandOrder = ARBITRARY; 
            break;
        } 
      } 
      return bandOrder;
    }
  }
  
  public static class ImageLayoutException extends Exception {
    public ImageLayoutException(String param1String) { super(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\lcms\LCMSImageLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */