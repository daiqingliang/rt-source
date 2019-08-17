package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class ByteComponentRaster extends SunWritableRaster {
  protected int bandOffset;
  
  protected int[] dataOffsets;
  
  protected int scanlineStride;
  
  protected int pixelStride;
  
  protected byte[] data;
  
  int type;
  
  private int maxX = this.minX + this.width;
  
  private int maxY = this.minY + this.height;
  
  private static native void initIDs();
  
  public ByteComponentRaster(SampleModel paramSampleModel, Point paramPoint) { this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public ByteComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) { this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public ByteComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, ByteComponentRaster paramByteComponentRaster) {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramByteComponentRaster);
    if (!(paramDataBuffer instanceof DataBufferByte))
      throw new RasterFormatException("ByteComponentRasters must have byte DataBuffers"); 
    DataBufferByte dataBufferByte = (DataBufferByte)paramDataBuffer;
    this.data = stealData(dataBufferByte, 0);
    if (dataBufferByte.getNumBanks() != 1)
      throw new RasterFormatException("DataBuffer for ByteComponentRasters must only have 1 bank."); 
    int i = dataBufferByte.getOffset();
    if (paramSampleModel instanceof ComponentSampleModel) {
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)paramSampleModel;
      this.type = 1;
      this.scanlineStride = componentSampleModel.getScanlineStride();
      this.pixelStride = componentSampleModel.getPixelStride();
      this.dataOffsets = componentSampleModel.getBandOffsets();
      int j = paramRectangle.x - paramPoint.x;
      int k = paramRectangle.y - paramPoint.y;
      for (byte b = 0; b < getNumDataElements(); b++)
        this.dataOffsets[b] = this.dataOffsets[b] + i + j * this.pixelStride + k * this.scanlineStride; 
    } else if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      this.type = 7;
      this.scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
      this.pixelStride = 1;
      this.dataOffsets = new int[1];
      this.dataOffsets[0] = i;
      int j = paramRectangle.x - paramPoint.x;
      int k = paramRectangle.y - paramPoint.y;
      this.dataOffsets[0] = this.dataOffsets[0] + j * this.pixelStride + k * this.scanlineStride;
    } else {
      throw new RasterFormatException("IntegerComponentRasters must have ComponentSampleModel or SinglePixelPackedSampleModel");
    } 
    this.bandOffset = this.dataOffsets[0];
    verify();
  }
  
  public int[] getDataOffsets() { return (int[])this.dataOffsets.clone(); }
  
  public int getDataOffset(int paramInt) { return this.dataOffsets[paramInt]; }
  
  public int getScanlineStride() { return this.scanlineStride; }
  
  public int getPixelStride() { return this.pixelStride; }
  
  public byte[] getDataStorage() { return this.data; }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject) {
    byte[] arrayOfByte;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfByte = new byte[this.numDataElements];
    } else {
      arrayOfByte = (byte[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    for (byte b = 0; b < this.numDataElements; b++)
      arrayOfByte[b] = this.data[this.dataOffsets[b] + i]; 
    return arrayOfByte;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    byte[] arrayOfByte;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfByte = new byte[paramInt3 * paramInt4 * this.numDataElements];
    } else {
      arrayOfByte = (byte[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < paramInt4) {
      int j = i;
      byte b = 0;
      while (b < paramInt3) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          arrayOfByte[b1++] = this.data[this.dataOffsets[b3] + j]; 
        b++;
        j += this.pixelStride;
      } 
      b2++;
      i += this.scanlineStride;
    } 
    return arrayOfByte;
  }
  
  public byte[] getByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfByte == null)
      paramArrayOfByte = new byte[this.scanlineStride * paramInt4]; 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride + this.dataOffsets[paramInt5];
    int j = 0;
    if (this.pixelStride == 1) {
      if (this.scanlineStride == paramInt3) {
        System.arraycopy(this.data, i, paramArrayOfByte, 0, paramInt3 * paramInt4);
      } else {
        byte b = 0;
        while (b < paramInt4) {
          System.arraycopy(this.data, i, paramArrayOfByte, j, paramInt3);
          j += paramInt3;
          b++;
          i += this.scanlineStride;
        } 
      } 
    } else {
      byte b = 0;
      while (b < paramInt4) {
        int k = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          paramArrayOfByte[j++] = this.data[k];
          b1++;
          k += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
    } 
    return paramArrayOfByte;
  }
  
  public byte[] getByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfByte == null)
      paramArrayOfByte = new byte[this.numDataElements * this.scanlineStride * paramInt4]; 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < paramInt4) {
      int j = i;
      byte b = 0;
      while (b < paramInt3) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          paramArrayOfByte[b1++] = this.data[this.dataOffsets[b3] + j]; 
        b++;
        j += this.pixelStride;
      } 
      b2++;
      i += this.scanlineStride;
    } 
    return paramArrayOfByte;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    byte[] arrayOfByte = (byte[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    for (byte b = 0; b < this.numDataElements; b++)
      this.data[this.dataOffsets[b] + i] = arrayOfByte[b]; 
    markDirty();
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster) {
    int i = paramRaster.getMinX() + paramInt1;
    int j = paramRaster.getMinY() + paramInt2;
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
    if (paramRaster instanceof ByteComponentRaster) {
      ByteComponentRaster byteComponentRaster = (ByteComponentRaster)paramRaster;
      byte[] arrayOfByte = byteComponentRaster.getDataStorage();
      if (this.numDataElements == 1) {
        int m = byteComponentRaster.getDataOffset(0);
        int n = byteComponentRaster.getScanlineStride();
        int i1 = m;
        int i2 = this.dataOffsets[0] + (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX;
        if (this.pixelStride == byteComponentRaster.getPixelStride()) {
          paramInt3 *= this.pixelStride;
          for (byte b = 0; b < paramInt4; b++) {
            System.arraycopy(arrayOfByte, i1, this.data, i2, paramInt3);
            i1 += n;
            i2 += this.scanlineStride;
          } 
          markDirty();
          return;
        } 
      } 
    } 
    for (int k = 0; k < paramInt4; k++) {
      object = paramRaster.getDataElements(i, j + k, paramInt3, 1, object);
      setDataElements(paramInt1, paramInt2 + k, paramInt3, 1, object);
    } 
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    byte[] arrayOfByte = (byte[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    if (this.numDataElements == 1) {
      int j = 0;
      int k = i + this.dataOffsets[0];
      for (byte b = 0; b < paramInt4; b++) {
        int m = i;
        System.arraycopy(arrayOfByte, j, this.data, k, paramInt3);
        j += paramInt3;
        k += this.scanlineStride;
      } 
      markDirty();
      return;
    } 
    byte b2 = 0;
    while (b2 < paramInt4) {
      int j = i;
      byte b = 0;
      while (b < paramInt3) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          this.data[this.dataOffsets[b3] + j] = arrayOfByte[b1++]; 
        b++;
        j += this.pixelStride;
      } 
      b2++;
      i += this.scanlineStride;
    } 
    markDirty();
  }
  
  public void putByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride + this.dataOffsets[paramInt5];
    int j = 0;
    if (this.pixelStride == 1) {
      if (this.scanlineStride == paramInt3) {
        System.arraycopy(paramArrayOfByte, 0, this.data, i, paramInt3 * paramInt4);
      } else {
        byte b = 0;
        while (b < paramInt4) {
          System.arraycopy(paramArrayOfByte, j, this.data, i, paramInt3);
          j += paramInt3;
          b++;
          i += this.scanlineStride;
        } 
      } 
    } else {
      byte b = 0;
      while (b < paramInt4) {
        int k = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          this.data[k] = paramArrayOfByte[j++];
          b1++;
          k += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
    } 
    markDirty();
  }
  
  public void putByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    int j = 0;
    if (this.numDataElements == 1) {
      i += this.dataOffsets[0];
      if (this.pixelStride == 1) {
        if (this.scanlineStride == paramInt3) {
          System.arraycopy(paramArrayOfByte, 0, this.data, i, paramInt3 * paramInt4);
        } else {
          for (byte b = 0; b < paramInt4; b++) {
            System.arraycopy(paramArrayOfByte, j, this.data, i, paramInt3);
            j += paramInt3;
            i += this.scanlineStride;
          } 
        } 
      } else {
        byte b = 0;
        while (b < paramInt4) {
          int k = i;
          byte b1 = 0;
          while (b1 < paramInt3) {
            this.data[k] = paramArrayOfByte[j++];
            b1++;
            k += this.pixelStride;
          } 
          b++;
          i += this.scanlineStride;
        } 
      } 
    } else {
      byte b = 0;
      while (b < paramInt4) {
        int k = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          for (byte b2 = 0; b2 < this.numDataElements; b2++)
            this.data[this.dataOffsets[b2] + k] = paramArrayOfByte[j++]; 
          b1++;
          k += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
    } 
    markDirty();
  }
  
  public Raster createChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) { return createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt); }
  
  public WritableRaster createWritableChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) {
    SampleModel sampleModel;
    if (paramInt1 < this.minX)
      throw new RasterFormatException("x lies outside the raster"); 
    if (paramInt2 < this.minY)
      throw new RasterFormatException("y lies outside the raster"); 
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
    return new ByteComponentRaster(sampleModel, this.dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new RasterFormatException("negative " + ((paramInt1 <= 0) ? "width" : "height")); 
    SampleModel sampleModel = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new ByteComponentRaster(sampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster() { return createCompatibleWritableRaster(this.width, this.height); }
  
  protected final void verify() {
    if (this.width <= 0 || this.height <= 0 || this.height > Integer.MAX_VALUE / this.width)
      throw new RasterFormatException("Invalid raster dimension"); 
    int i;
    for (i = 0; i < this.dataOffsets.length; i++) {
      if (this.dataOffsets[i] < 0)
        throw new RasterFormatException("Data offsets for band " + i + "(" + this.dataOffsets[i] + ") must be >= 0"); 
    } 
    if (this.minX - this.sampleModelTranslateX < 0L || this.minY - this.sampleModelTranslateY < 0L)
      throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")"); 
    if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height)
      throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride); 
    if ((this.height > 1 || this.minY - this.sampleModelTranslateY > 0) && this.scanlineStride > this.data.length)
      throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride); 
    i = (this.height - 1) * this.scanlineStride;
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
  
  public String toString() { return new String("ByteComponentRaster: width = " + this.width + " height = " + this.height + " #numDataElements " + this.numDataElements + " dataOff[0] = " + this.dataOffsets[0]); }
  
  static  {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ByteComponentRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */