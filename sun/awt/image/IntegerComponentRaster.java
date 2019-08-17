package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class IntegerComponentRaster extends SunWritableRaster {
  static final int TYPE_CUSTOM = 0;
  
  static final int TYPE_BYTE_SAMPLES = 1;
  
  static final int TYPE_USHORT_SAMPLES = 2;
  
  static final int TYPE_INT_SAMPLES = 3;
  
  static final int TYPE_BYTE_BANDED_SAMPLES = 4;
  
  static final int TYPE_USHORT_BANDED_SAMPLES = 5;
  
  static final int TYPE_INT_BANDED_SAMPLES = 6;
  
  static final int TYPE_BYTE_PACKED_SAMPLES = 7;
  
  static final int TYPE_USHORT_PACKED_SAMPLES = 8;
  
  static final int TYPE_INT_PACKED_SAMPLES = 9;
  
  static final int TYPE_INT_8BIT_SAMPLES = 10;
  
  static final int TYPE_BYTE_BINARY_SAMPLES = 11;
  
  protected int bandOffset;
  
  protected int[] dataOffsets;
  
  protected int scanlineStride;
  
  protected int pixelStride;
  
  protected int[] data;
  
  protected int numDataElems;
  
  int type;
  
  private int maxX = this.minX + this.width;
  
  private int maxY = this.minY + this.height;
  
  private static native void initIDs();
  
  public IntegerComponentRaster(SampleModel paramSampleModel, Point paramPoint) { this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public IntegerComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) { this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public IntegerComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, IntegerComponentRaster paramIntegerComponentRaster) {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramIntegerComponentRaster);
    if (!(paramDataBuffer instanceof DataBufferInt))
      throw new RasterFormatException("IntegerComponentRasters must haveinteger DataBuffers"); 
    DataBufferInt dataBufferInt = (DataBufferInt)paramDataBuffer;
    if (dataBufferInt.getNumBanks() != 1)
      throw new RasterFormatException("DataBuffer for IntegerComponentRasters must only have 1 bank."); 
    this.data = stealData(dataBufferInt, 0);
    if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      int[] arrayOfInt = singlePixelPackedSampleModel.getBitOffsets();
      boolean bool = false;
      int i;
      for (i = 1; i < arrayOfInt.length; i++) {
        if (arrayOfInt[i] % 8 != 0)
          bool = true; 
      } 
      this.type = bool ? 9 : 10;
      this.scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
      this.pixelStride = 1;
      this.dataOffsets = new int[1];
      this.dataOffsets[0] = dataBufferInt.getOffset();
      this.bandOffset = this.dataOffsets[0];
      i = paramRectangle.x - paramPoint.x;
      int j = paramRectangle.y - paramPoint.y;
      this.dataOffsets[0] = this.dataOffsets[0] + i + j * this.scanlineStride;
      this.numDataElems = singlePixelPackedSampleModel.getNumDataElements();
    } else {
      throw new RasterFormatException("IntegerComponentRasters must have SinglePixelPackedSampleModel");
    } 
    verify();
  }
  
  public int[] getDataOffsets() { return (int[])this.dataOffsets.clone(); }
  
  public int getDataOffset(int paramInt) { return this.dataOffsets[paramInt]; }
  
  public int getScanlineStride() { return this.scanlineStride; }
  
  public int getPixelStride() { return this.pixelStride; }
  
  public int[] getDataStorage() { return this.data; }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject) {
    int[] arrayOfInt;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfInt = new int[this.numDataElements];
    } else {
      arrayOfInt = (int[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    for (byte b = 0; b < this.numDataElements; b++)
      arrayOfInt[b] = this.data[this.dataOffsets[b] + i]; 
    return arrayOfInt;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    int[] arrayOfInt;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject instanceof int[]) {
      arrayOfInt = (int[])paramObject;
    } else {
      arrayOfInt = new int[this.numDataElements * paramInt3 * paramInt4];
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < paramInt4) {
      int j = i;
      byte b = 0;
      while (b < paramInt3) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          arrayOfInt[b1++] = this.data[this.dataOffsets[b3] + j]; 
        b++;
        j += this.pixelStride;
      } 
      b2++;
      i += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int[] arrayOfInt = (int[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    for (byte b = 0; b < this.numDataElements; b++)
      this.data[this.dataOffsets[b] + i] = arrayOfInt[b]; 
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
    int[] arrayOfInt = null;
    if (paramRaster instanceof IntegerComponentRaster && this.pixelStride == 1 && this.numDataElements == 1) {
      IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)paramRaster;
      if (integerComponentRaster.getNumDataElements() != 1)
        throw new ArrayIndexOutOfBoundsException("Number of bands does not match"); 
      arrayOfInt = integerComponentRaster.getDataStorage();
      int m = integerComponentRaster.getScanlineStride();
      int n = integerComponentRaster.getDataOffset(0);
      int i1 = n;
      int i2 = this.dataOffsets[0] + (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX;
      if (integerComponentRaster.getPixelStride() == this.pixelStride) {
        paramInt3 *= this.pixelStride;
        for (byte b = 0; b < paramInt4; b++) {
          System.arraycopy(arrayOfInt, i1, this.data, i2, paramInt3);
          i1 += m;
          i2 += this.scanlineStride;
        } 
        markDirty();
        return;
      } 
    } 
    Object object = null;
    for (int k = 0; k < paramInt4; k++) {
      object = paramRaster.getDataElements(i, j + k, paramInt3, 1, object);
      setDataElements(paramInt1, paramInt2 + k, paramInt3, 1, object);
    } 
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int[] arrayOfInt = (int[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < paramInt4) {
      int j = i;
      byte b = 0;
      while (b < paramInt3) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          this.data[this.dataOffsets[b3] + j] = arrayOfInt[b1++]; 
        b++;
        j += this.pixelStride;
      } 
      b2++;
      i += this.scanlineStride;
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
      throw new RasterFormatException("(x + width) is outside raster"); 
    if (paramInt2 + paramInt4 < paramInt2 || paramInt2 + paramInt4 > this.minY + this.height)
      throw new RasterFormatException("(y + height) is outside raster"); 
    if (paramArrayOfInt != null) {
      sampleModel = this.sampleModel.createSubsetSampleModel(paramArrayOfInt);
    } else {
      sampleModel = this.sampleModel;
    } 
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new IntegerComponentRaster(sampleModel, this.dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public Raster createChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) { return createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt); }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new RasterFormatException("negative " + ((paramInt1 <= 0) ? "width" : "height")); 
    SampleModel sampleModel = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new IntegerComponentRaster(sampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster() { return createCompatibleWritableRaster(this.width, this.height); }
  
  protected final void verify() {
    if (this.width <= 0 || this.height <= 0 || this.height > Integer.MAX_VALUE / this.width)
      throw new RasterFormatException("Invalid raster dimension"); 
    if (this.dataOffsets[0] < 0)
      throw new RasterFormatException("Data offset (" + this.dataOffsets[0] + ") must be >= 0"); 
    if (this.minX - this.sampleModelTranslateX < 0L || this.minY - this.sampleModelTranslateY < 0L)
      throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")"); 
    if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height)
      throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride); 
    if ((this.height > 1 || this.minY - this.sampleModelTranslateY > 0) && this.scanlineStride > this.data.length)
      throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride); 
    int i = (this.height - 1) * this.scanlineStride;
    if (this.pixelStride < 0 || this.pixelStride > Integer.MAX_VALUE / this.width || this.pixelStride > this.data.length)
      throw new RasterFormatException("Incorrect pixel stride: " + this.pixelStride); 
    int j = (this.width - 1) * this.pixelStride;
    if (j > Integer.MAX_VALUE - i)
      throw new RasterFormatException("Incorrect raster attributes"); 
    j += i;
    int k = 0;
    for (byte b = 0; b < this.numDataElements; b++) {
      if (this.dataOffsets[b] > Integer.MAX_VALUE - j)
        throw new RasterFormatException("Incorrect band offset: " + this.dataOffsets[b]); 
      int m = j + this.dataOffsets[b];
      if (m > k)
        k = m; 
    } 
    if (this.data.length <= k)
      throw new RasterFormatException("Data array too small (should be > " + k + " )"); 
  }
  
  public String toString() { return new String("IntegerComponentRaster: width = " + this.width + " height = " + this.height + " #Bands = " + this.numBands + " #DataElements " + this.numDataElements + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY + " dataOffset[0] " + this.dataOffsets[0]); }
  
  static  {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\IntegerComponentRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */