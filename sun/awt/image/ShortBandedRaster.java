package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class ShortBandedRaster extends SunWritableRaster {
  int[] dataOffsets;
  
  int scanlineStride;
  
  short[][] data;
  
  private int maxX = this.minX + this.width;
  
  private int maxY = this.minY + this.height;
  
  public ShortBandedRaster(SampleModel paramSampleModel, Point paramPoint) { this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public ShortBandedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) { this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public ShortBandedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, ShortBandedRaster paramShortBandedRaster) {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramShortBandedRaster);
    if (!(paramDataBuffer instanceof DataBufferUShort))
      throw new RasterFormatException("ShortBandedRaster must have ushort DataBuffers"); 
    DataBufferUShort dataBufferUShort = (DataBufferUShort)paramDataBuffer;
    if (paramSampleModel instanceof BandedSampleModel) {
      BandedSampleModel bandedSampleModel = (BandedSampleModel)paramSampleModel;
      this.scanlineStride = bandedSampleModel.getScanlineStride();
      int[] arrayOfInt1 = bandedSampleModel.getBankIndices();
      int[] arrayOfInt2 = bandedSampleModel.getBandOffsets();
      int[] arrayOfInt3 = dataBufferUShort.getOffsets();
      this.dataOffsets = new int[arrayOfInt1.length];
      this.data = new short[arrayOfInt1.length][];
      int i = paramRectangle.x - paramPoint.x;
      int j = paramRectangle.y - paramPoint.y;
      for (byte b = 0; b < arrayOfInt1.length; b++) {
        this.data[b] = stealData(dataBufferUShort, arrayOfInt1[b]);
        this.dataOffsets[b] = arrayOfInt3[arrayOfInt1[b]] + i + j * this.scanlineStride + arrayOfInt2[b];
      } 
    } else {
      throw new RasterFormatException("ShortBandedRasters must have BandedSampleModels");
    } 
    verify();
  }
  
  public int[] getDataOffsets() { return (int[])this.dataOffsets.clone(); }
  
  public int getDataOffset(int paramInt) { return this.dataOffsets[paramInt]; }
  
  public int getScanlineStride() { return this.scanlineStride; }
  
  public int getPixelStride() { return 1; }
  
  public short[][] getDataStorage() { return this.data; }
  
  public short[] getDataStorage(int paramInt) { return this.data[paramInt]; }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject) {
    short[] arrayOfShort;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfShort = new short[this.numDataElements];
    } else {
      arrayOfShort = (short[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX;
    for (byte b = 0; b < this.numDataElements; b++)
      arrayOfShort[b] = this.data[b][this.dataOffsets[b] + i]; 
    return arrayOfShort;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    short[] arrayOfShort;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfShort = new short[this.numDataElements * paramInt3 * paramInt4];
    } else {
      arrayOfShort = (short[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX;
    for (byte b = 0; b < this.numDataElements; b++) {
      int j = b;
      short[] arrayOfShort1 = this.data[b];
      int k = this.dataOffsets[b];
      int m = i;
      byte b1 = 0;
      while (b1 < paramInt4) {
        int n = k + m;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          arrayOfShort[j] = arrayOfShort1[n++];
          j += this.numDataElements;
        } 
        b1++;
        m += this.scanlineStride;
      } 
    } 
    return arrayOfShort;
  }
  
  public short[] getShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, short[] paramArrayOfShort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfShort == null)
      paramArrayOfShort = new short[this.scanlineStride * paramInt4]; 
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX + this.dataOffsets[paramInt5];
    if (this.scanlineStride == paramInt3) {
      System.arraycopy(this.data[paramInt5], i, paramArrayOfShort, 0, paramInt3 * paramInt4);
    } else {
      int j = 0;
      byte b = 0;
      while (b < paramInt4) {
        System.arraycopy(this.data[paramInt5], i, paramArrayOfShort, j, paramInt3);
        j += paramInt3;
        b++;
        i += this.scanlineStride;
      } 
    } 
    return paramArrayOfShort;
  }
  
  public short[] getShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short[] paramArrayOfShort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfShort == null)
      paramArrayOfShort = new short[this.numDataElements * this.scanlineStride * paramInt4]; 
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX;
    for (byte b = 0; b < this.numDataElements; b++) {
      int j = b;
      short[] arrayOfShort = this.data[b];
      int k = this.dataOffsets[b];
      int m = i;
      byte b1 = 0;
      while (b1 < paramInt4) {
        int n = k + m;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          paramArrayOfShort[j] = arrayOfShort[n++];
          j += this.numDataElements;
        } 
        b1++;
        m += this.scanlineStride;
      } 
    } 
    return paramArrayOfShort;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    short[] arrayOfShort = (short[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX;
    for (byte b = 0; b < this.numDataElements; b++)
      this.data[b][this.dataOffsets[b] + i] = arrayOfShort[b]; 
    markDirty();
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster) {
    int i = paramInt1 + paramRaster.getMinX();
    int j = paramInt2 + paramRaster.getMinY();
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    if (i < this.minX || j < this.minY || i + k > this.maxX || j + m > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    setDataElements(i, j, k, m, paramRaster);
  }
  
  private void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Raster paramRaster) {
    if (paramInt3 <= 0 || paramInt4 <= 0)
      return; 
    int i = paramRaster.getMinX();
    int j = paramRaster.getMinY();
    Object object = null;
    for (int k = 0; k < paramInt4; k++) {
      object = paramRaster.getDataElements(i, j + k, paramInt3, 1, object);
      setDataElements(paramInt1, paramInt2 + k, paramInt3, 1, object);
    } 
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    short[] arrayOfShort = (short[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX;
    for (byte b = 0; b < this.numDataElements; b++) {
      int j = b;
      short[] arrayOfShort1 = this.data[b];
      int k = this.dataOffsets[b];
      int m = i;
      byte b1 = 0;
      while (b1 < paramInt4) {
        int n = k + m;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          arrayOfShort1[n++] = arrayOfShort[j];
          j += this.numDataElements;
        } 
        b1++;
        m += this.scanlineStride;
      } 
    } 
    markDirty();
  }
  
  public void putShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, short[] paramArrayOfShort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX + this.dataOffsets[paramInt5];
    int j = 0;
    if (this.scanlineStride == paramInt3) {
      System.arraycopy(paramArrayOfShort, 0, this.data[paramInt5], i, paramInt3 * paramInt4);
    } else {
      byte b = 0;
      while (b < paramInt4) {
        System.arraycopy(paramArrayOfShort, j, this.data[paramInt5], i, paramInt3);
        j += paramInt3;
        b++;
        i += this.scanlineStride;
      } 
    } 
    markDirty();
  }
  
  public void putShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short[] paramArrayOfShort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX;
    for (byte b = 0; b < this.numDataElements; b++) {
      int j = b;
      short[] arrayOfShort = this.data[b];
      int k = this.dataOffsets[b];
      int m = i;
      byte b1 = 0;
      while (b1 < paramInt4) {
        int n = k + m;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          arrayOfShort[n++] = paramArrayOfShort[j];
          j += this.numDataElements;
        } 
        b1++;
        m += this.scanlineStride;
      } 
    } 
    markDirty();
  }
  
  public WritableRaster createWritableChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) {
    SampleModel sampleModel;
    if (paramInt1 < this.minX)
      throw new RasterFormatException("x lies outside raster"); 
    if (paramInt2 < this.minY)
      throw new RasterFormatException("y lies outside raster"); 
    if (paramInt1 + paramInt3 < paramInt1 || paramInt1 + paramInt3 > this.minX + this.width)
      throw new RasterFormatException("(x + width) is outside of Raster"); 
    if (paramInt2 + paramInt4 < paramInt2 || paramInt2 + paramInt4 > this.minY + this.height)
      throw new RasterFormatException("(y + height) is outside of Raster"); 
    if (paramArrayOfInt != null) {
      sampleModel = this.sampleModel.createSubsetSampleModel(paramArrayOfInt);
    } else {
      sampleModel = this.sampleModel;
    } 
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new ShortBandedRaster(sampleModel, this.dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public Raster createChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) { return createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt); }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new RasterFormatException("negative " + ((paramInt1 <= 0) ? "width" : "height")); 
    SampleModel sampleModel = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new ShortBandedRaster(sampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster() { return createCompatibleWritableRaster(this.width, this.height); }
  
  private void verify() {
    if (this.width <= 0 || this.height <= 0 || this.height > Integer.MAX_VALUE / this.width)
      throw new RasterFormatException("Invalid raster dimension"); 
    if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height)
      throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride); 
    if (this.minX - this.sampleModelTranslateX < 0L || this.minY - this.sampleModelTranslateY < 0L)
      throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")"); 
    if (this.height > 1 || this.minY - this.sampleModelTranslateY > 0)
      for (byte b1 = 0; b1 < this.data.length; b1++) {
        if (this.scanlineStride > this.data[b1].length)
          throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride); 
      }  
    int i;
    for (i = 0; i < this.dataOffsets.length; i++) {
      if (this.dataOffsets[i] < 0)
        throw new RasterFormatException("Data offsets for band " + i + "(" + this.dataOffsets[i] + ") must be >= 0"); 
    } 
    i = (this.height - 1) * this.scanlineStride;
    if (this.width - 1 > Integer.MAX_VALUE - i)
      throw new RasterFormatException("Invalid raster dimension"); 
    int j = i + this.width - 1;
    int k = 0;
    byte b;
    for (b = 0; b < this.numDataElements; b++) {
      if (this.dataOffsets[b] > Integer.MAX_VALUE - j)
        throw new RasterFormatException("Invalid raster dimension"); 
      int m = j + this.dataOffsets[b];
      if (m > k)
        k = m; 
    } 
    for (b = 0; b < this.numDataElements; b++) {
      if (this.data[b].length <= k)
        throw new RasterFormatException("Data array too small (should be > " + k + " )"); 
    } 
  }
  
  public String toString() { return new String("ShortBandedRaster: width = " + this.width + " height = " + this.height + " #numBands " + this.numBands + " #dataElements " + this.numDataElements); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ShortBandedRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */