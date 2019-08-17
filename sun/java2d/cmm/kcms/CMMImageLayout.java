package sun.java2d.cmm.kcms;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ShortComponentRaster;

class CMMImageLayout {
  private static final int typeBase = 256;
  
  public static final int typeComponentUByte = 256;
  
  public static final int typeComponentUShort12 = 257;
  
  public static final int typeComponentUShort = 258;
  
  public static final int typePixelUByte = 259;
  
  public static final int typePixelUShort12 = 260;
  
  public static final int typePixelUShort = 261;
  
  public static final int typeShort555 = 262;
  
  public static final int typeShort565 = 263;
  
  public static final int typeInt101010 = 264;
  
  public static final int typeIntRGBPacked = 265;
  
  public int Type;
  
  public int NumCols;
  
  public int NumRows;
  
  public int OffsetColumn;
  
  public int OffsetRow;
  
  public int NumChannels;
  
  public final boolean hasAlpha;
  
  public Object[] chanData;
  
  public int[] DataOffsets;
  
  public int[] sampleInfo;
  
  private int[] dataArrayLength;
  
  private static final int MAX_NumChannels = 9;
  
  public CMMImageLayout(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws ImageLayoutException {
    this.Type = 256;
    this.chanData = new Object[paramInt2];
    this.DataOffsets = new int[paramInt2];
    this.dataArrayLength = new int[paramInt2];
    this.NumCols = paramInt1;
    this.NumRows = 1;
    this.OffsetColumn = paramInt2;
    this.OffsetRow = this.NumCols * this.OffsetColumn;
    this.NumChannels = paramInt2;
    for (byte b = 0; b < paramInt2; b++) {
      this.chanData[b] = paramArrayOfByte;
      this.DataOffsets[b] = b;
      this.dataArrayLength[b] = paramArrayOfByte.length;
    } 
    this.hasAlpha = false;
    verify();
  }
  
  public CMMImageLayout(short[] paramArrayOfShort, int paramInt1, int paramInt2) throws ImageLayoutException {
    this.Type = 258;
    this.chanData = new Object[paramInt2];
    this.DataOffsets = new int[paramInt2];
    this.dataArrayLength = new int[paramInt2];
    this.NumCols = paramInt1;
    this.NumRows = 1;
    this.OffsetColumn = safeMult(2, paramInt2);
    this.OffsetRow = this.NumCols * this.OffsetColumn;
    this.NumChannels = paramInt2;
    for (byte b = 0; b < paramInt2; b++) {
      this.chanData[b] = paramArrayOfShort;
      this.DataOffsets[b] = b * 2;
      this.dataArrayLength[b] = 2 * paramArrayOfShort.length;
    } 
    this.hasAlpha = false;
    verify();
  }
  
  public CMMImageLayout(BufferedImage paramBufferedImage) throws ImageLayoutException {
    this.Type = paramBufferedImage.getType();
    this.NumCols = paramBufferedImage.getWidth();
    this.NumRows = paramBufferedImage.getHeight();
    WritableRaster writableRaster = paramBufferedImage.getRaster();
    switch (this.Type) {
      case 1:
      case 2:
      case 4:
        this.NumChannels = 3;
        this.hasAlpha = (this.Type == 2);
        b = this.hasAlpha ? 4 : 3;
        this.chanData = new Object[b];
        this.DataOffsets = new int[b];
        this.dataArrayLength = new int[b];
        this.sampleInfo = new int[b];
        this.OffsetColumn = 4;
        if (writableRaster instanceof IntegerComponentRaster) {
          IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)writableRaster;
          int j = safeMult(4, integerComponentRaster.getPixelStride());
          if (j != this.OffsetColumn)
            throw new ImageLayoutException("Incompatible raster type"); 
          this.OffsetRow = safeMult(4, integerComponentRaster.getScanlineStride());
          int i = safeMult(4, integerComponentRaster.getDataOffset(0));
          int[] arrayOfInt = integerComponentRaster.getDataStorage();
          for (byte b1 = 0; b1 < 3; b1++) {
            this.chanData[b1] = arrayOfInt;
            this.DataOffsets[b1] = i;
            this.dataArrayLength[b1] = 4 * arrayOfInt.length;
            if (this.Type == 4) {
              this.sampleInfo[b1] = 3 - b1;
            } else {
              this.sampleInfo[b1] = b1 + true;
            } 
          } 
          if (this.hasAlpha) {
            this.chanData[3] = arrayOfInt;
            this.DataOffsets[3] = i;
            this.dataArrayLength[3] = 4 * arrayOfInt.length;
            this.sampleInfo[3] = 0;
          } 
          break;
        } 
        throw new ImageLayoutException("Incompatible raster type");
      case 5:
      case 6:
        this.NumChannels = 3;
        this.hasAlpha = (this.Type == 6);
        if (this.hasAlpha) {
          this.OffsetColumn = 4;
          b = 4;
        } else {
          this.OffsetColumn = 3;
          b = 3;
        } 
        this.chanData = new Object[b];
        this.DataOffsets = new int[b];
        this.dataArrayLength = new int[b];
        if (writableRaster instanceof ByteComponentRaster) {
          ByteComponentRaster byteComponentRaster = (ByteComponentRaster)writableRaster;
          int j = byteComponentRaster.getPixelStride();
          if (j != this.OffsetColumn)
            throw new ImageLayoutException("Incompatible raster type"); 
          this.OffsetRow = byteComponentRaster.getScanlineStride();
          int i = byteComponentRaster.getDataOffset(0);
          byte[] arrayOfByte = byteComponentRaster.getDataStorage();
          for (int k = 0; k < b; k++) {
            this.chanData[k] = arrayOfByte;
            this.DataOffsets[k] = i - k;
            this.dataArrayLength[k] = arrayOfByte.length;
          } 
          break;
        } 
        throw new ImageLayoutException("Incompatible raster type");
      case 10:
        this.Type = 256;
        this.NumChannels = 1;
        this.hasAlpha = false;
        this.chanData = new Object[1];
        this.DataOffsets = new int[1];
        this.dataArrayLength = new int[1];
        this.OffsetColumn = 1;
        if (writableRaster instanceof ByteComponentRaster) {
          ByteComponentRaster byteComponentRaster = (ByteComponentRaster)writableRaster;
          int i = byteComponentRaster.getPixelStride();
          if (i != this.OffsetColumn)
            throw new ImageLayoutException("Incompatible raster type"); 
          this.OffsetRow = byteComponentRaster.getScanlineStride();
          byte[] arrayOfByte = byteComponentRaster.getDataStorage();
          this.chanData[0] = arrayOfByte;
          this.dataArrayLength[0] = arrayOfByte.length;
          this.DataOffsets[0] = byteComponentRaster.getDataOffset(0);
          break;
        } 
        throw new ImageLayoutException("Incompatible raster type");
      case 11:
        this.Type = 258;
        this.NumChannels = 1;
        this.hasAlpha = false;
        this.chanData = new Object[1];
        this.DataOffsets = new int[1];
        this.dataArrayLength = new int[1];
        this.OffsetColumn = 2;
        if (writableRaster instanceof ShortComponentRaster) {
          ShortComponentRaster shortComponentRaster = (ShortComponentRaster)writableRaster;
          int i = safeMult(2, shortComponentRaster.getPixelStride());
          if (i != this.OffsetColumn)
            throw new ImageLayoutException("Incompatible raster type"); 
          this.OffsetRow = safeMult(2, shortComponentRaster.getScanlineStride());
          this.DataOffsets[0] = safeMult(2, shortComponentRaster.getDataOffset(0));
          short[] arrayOfShort = shortComponentRaster.getDataStorage();
          this.chanData[0] = arrayOfShort;
          this.dataArrayLength[0] = 2 * arrayOfShort.length;
          break;
        } 
        throw new ImageLayoutException("Incompatible raster type");
      default:
        throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
    } 
    verify();
  }
  
  public CMMImageLayout(BufferedImage paramBufferedImage, SinglePixelPackedSampleModel paramSinglePixelPackedSampleModel, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws ImageLayoutException {
    this.Type = 265;
    this.NumChannels = 3;
    this.NumCols = paramBufferedImage.getWidth();
    this.NumRows = paramBufferedImage.getHeight();
    this.hasAlpha = (paramInt4 >= 0);
    byte b1 = this.hasAlpha ? 4 : 3;
    this.chanData = new Object[b1];
    this.DataOffsets = new int[b1];
    this.dataArrayLength = new int[b1];
    this.sampleInfo = new int[b1];
    this.OffsetColumn = 4;
    int i = paramSinglePixelPackedSampleModel.getScanlineStride();
    this.OffsetRow = safeMult(4, i);
    WritableRaster writableRaster = paramBufferedImage.getRaster();
    DataBufferInt dataBufferInt = (DataBufferInt)writableRaster.getDataBuffer();
    int j = writableRaster.getSampleModelTranslateX();
    int k = writableRaster.getSampleModelTranslateY();
    int m = safeMult(k, i);
    int n = safeMult(4, j);
    n = safeAdd(n, m);
    int i1 = safeAdd(dataBufferInt.getOffset(), -n);
    int[] arrayOfInt = dataBufferInt.getData();
    for (byte b2 = 0; b2 < b1; b2++) {
      this.chanData[b2] = arrayOfInt;
      this.DataOffsets[b2] = i1;
      this.dataArrayLength[b2] = arrayOfInt.length * 4;
    } 
    this.sampleInfo[0] = paramInt1;
    this.sampleInfo[1] = paramInt2;
    this.sampleInfo[2] = paramInt3;
    if (this.hasAlpha)
      this.sampleInfo[3] = paramInt4; 
    verify();
  }
  
  public CMMImageLayout(BufferedImage paramBufferedImage, ComponentSampleModel paramComponentSampleModel) throws ImageLayoutException {
    ColorModel colorModel = paramBufferedImage.getColorModel();
    int i = colorModel.getNumColorComponents();
    if (i < 0 || i > 9)
      throw new ImageLayoutException("Invalid image layout"); 
    this.hasAlpha = colorModel.hasAlpha();
    WritableRaster writableRaster = paramBufferedImage.getRaster();
    int[] arrayOfInt1 = paramComponentSampleModel.getBankIndices();
    int[] arrayOfInt2 = paramComponentSampleModel.getBandOffsets();
    this.NumChannels = i;
    this.NumCols = paramBufferedImage.getWidth();
    this.NumRows = paramBufferedImage.getHeight();
    if (this.hasAlpha)
      i++; 
    this.chanData = new Object[i];
    this.DataOffsets = new int[i];
    this.dataArrayLength = new int[i];
    int j = writableRaster.getSampleModelTranslateY();
    int k = writableRaster.getSampleModelTranslateX();
    int m = paramComponentSampleModel.getScanlineStride();
    int n = paramComponentSampleModel.getPixelStride();
    int i1 = safeMult(m, j);
    int i2 = safeMult(n, k);
    i2 = safeAdd(i2, i1);
    switch (paramComponentSampleModel.getDataType()) {
      case 0:
        this.Type = 256;
        this.OffsetColumn = n;
        this.OffsetRow = m;
        dataBufferByte = (DataBufferByte)writableRaster.getDataBuffer();
        arrayOfInt3 = dataBufferByte.getOffsets();
        for (b = 0; b < i; b++) {
          byte[] arrayOfByte = dataBufferByte.getData(arrayOfInt1[b]);
          this.chanData[b] = arrayOfByte;
          this.dataArrayLength[b] = arrayOfByte.length;
          int i3 = safeAdd(arrayOfInt3[arrayOfInt1[b]], -i2);
          i3 = safeAdd(i3, arrayOfInt2[b]);
          this.DataOffsets[b] = i3;
        } 
        break;
      case 1:
        this.Type = 258;
        this.OffsetColumn = safeMult(2, n);
        this.OffsetRow = safeMult(2, m);
        dataBufferUShort = (DataBufferUShort)writableRaster.getDataBuffer();
        arrayOfInt3 = dataBufferUShort.getOffsets();
        for (b = 0; b < i; b++) {
          short[] arrayOfShort = dataBufferUShort.getData(arrayOfInt1[b]);
          this.chanData[b] = arrayOfShort;
          this.dataArrayLength[b] = arrayOfShort.length * 2;
          int i3 = safeAdd(arrayOfInt3[arrayOfInt1[b]], -i2);
          i3 = safeAdd(i3, arrayOfInt2[b]);
          this.DataOffsets[b] = safeMult(2, i3);
        } 
        break;
      default:
        throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
    } 
    verify();
  }
  
  public CMMImageLayout(Raster paramRaster, ComponentSampleModel paramComponentSampleModel) throws ImageLayoutException {
    int i = paramRaster.getNumBands();
    if (i < 0 || i > 9)
      throw new ImageLayoutException("Invalid image layout"); 
    int[] arrayOfInt1 = paramComponentSampleModel.getBankIndices();
    int[] arrayOfInt2 = paramComponentSampleModel.getBandOffsets();
    this.NumChannels = i;
    this.NumCols = paramRaster.getWidth();
    this.NumRows = paramRaster.getHeight();
    this.hasAlpha = false;
    this.chanData = new Object[i];
    this.DataOffsets = new int[i];
    this.dataArrayLength = new int[i];
    int j = paramComponentSampleModel.getScanlineStride();
    int k = paramComponentSampleModel.getPixelStride();
    int m = paramRaster.getMinX();
    int n = paramRaster.getMinY();
    int i1 = paramRaster.getSampleModelTranslateX();
    int i2 = paramRaster.getSampleModelTranslateY();
    int i3 = safeAdd(n, -i2);
    i3 = safeMult(i3, j);
    int i4 = safeAdd(m, -i1);
    i4 = safeMult(i4, k);
    i4 = safeAdd(i4, i3);
    switch (paramComponentSampleModel.getDataType()) {
      case 0:
        this.Type = 256;
        this.OffsetColumn = k;
        this.OffsetRow = j;
        dataBufferByte = (DataBufferByte)paramRaster.getDataBuffer();
        arrayOfInt3 = dataBufferByte.getOffsets();
        for (b = 0; b < i; b++) {
          byte[] arrayOfByte = dataBufferByte.getData(arrayOfInt1[b]);
          this.chanData[b] = arrayOfByte;
          this.dataArrayLength[b] = arrayOfByte.length;
          int i5 = safeAdd(arrayOfInt3[arrayOfInt1[b]], i4);
          this.DataOffsets[b] = safeAdd(i5, arrayOfInt2[b]);
        } 
        break;
      case 1:
        this.Type = 258;
        this.OffsetColumn = safeMult(2, k);
        this.OffsetRow = safeMult(2, j);
        dataBufferUShort = (DataBufferUShort)paramRaster.getDataBuffer();
        arrayOfInt3 = dataBufferUShort.getOffsets();
        for (b = 0; b < i; b++) {
          short[] arrayOfShort = dataBufferUShort.getData(arrayOfInt1[b]);
          this.chanData[b] = arrayOfShort;
          this.dataArrayLength[b] = arrayOfShort.length * 2;
          int i5 = safeAdd(arrayOfInt3[arrayOfInt1[b]], i4);
          i5 = safeAdd(i5, arrayOfInt2[b]);
          this.DataOffsets[b] = safeMult(2, i5);
        } 
        break;
      default:
        throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
    } 
    verify();
  }
  
  private final void verify() throws ImageLayoutException {
    int i = safeMult(this.OffsetRow, this.NumRows - 1);
    int j = safeMult(this.OffsetColumn, this.NumCols - 1);
    i = safeAdd(i, j);
    int k = this.NumChannels;
    if (this.hasAlpha)
      k++; 
    for (byte b = 0; b < k; b++) {
      int m = this.DataOffsets[b];
      if (m < 0 || m >= this.dataArrayLength[b])
        throw new ImageLayoutException("Invalid image layout"); 
      m = safeAdd(m, i);
      if (m < 0 || m >= this.dataArrayLength[b])
        throw new ImageLayoutException("Invalid image layout"); 
    } 
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
  
  public static class ImageLayoutException extends Exception {
    public ImageLayoutException(String param1String) { super(param1String); }
    
    public ImageLayoutException(String param1String, Throwable param1Throwable) { super(param1String, param1Throwable); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\kcms\CMMImageLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */