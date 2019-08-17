package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class ShortInterleavedRaster extends ShortComponentRaster {
  private int maxX = this.minX + this.width;
  
  private int maxY = this.minY + this.height;
  
  public ShortInterleavedRaster(SampleModel paramSampleModel, Point paramPoint) { this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public ShortInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) { this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public ShortInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, ShortInterleavedRaster paramShortInterleavedRaster) {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramShortInterleavedRaster);
    if (!(paramDataBuffer instanceof DataBufferUShort))
      throw new RasterFormatException("ShortInterleavedRasters must have ushort DataBuffers"); 
    DataBufferUShort dataBufferUShort = (DataBufferUShort)paramDataBuffer;
    this.data = stealData(dataBufferUShort, 0);
    if (paramSampleModel instanceof java.awt.image.PixelInterleavedSampleModel || (paramSampleModel instanceof ComponentSampleModel && paramSampleModel.getNumBands() == 1)) {
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)paramSampleModel;
      this.scanlineStride = componentSampleModel.getScanlineStride();
      this.pixelStride = componentSampleModel.getPixelStride();
      this.dataOffsets = componentSampleModel.getBandOffsets();
      int i = paramRectangle.x - paramPoint.x;
      int j = paramRectangle.y - paramPoint.y;
      for (byte b = 0; b < getNumDataElements(); b++)
        this.dataOffsets[b] = this.dataOffsets[b] + i * this.pixelStride + j * this.scanlineStride; 
    } else if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      this.scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
      this.pixelStride = 1;
      this.dataOffsets = new int[1];
      this.dataOffsets[0] = dataBufferUShort.getOffset();
      int i = paramRectangle.x - paramPoint.x;
      int j = paramRectangle.y - paramPoint.y;
      this.dataOffsets[0] = this.dataOffsets[0] + i + j * this.scanlineStride;
    } else {
      throw new RasterFormatException("ShortInterleavedRasters must have PixelInterleavedSampleModel, SinglePixelPackedSampleModel or 1 band ComponentSampleModel.  Sample model is " + paramSampleModel);
    } 
    this.bandOffset = this.dataOffsets[0];
    verify();
  }
  
  public int[] getDataOffsets() { return (int[])this.dataOffsets.clone(); }
  
  public int getDataOffset(int paramInt) { return this.dataOffsets[paramInt]; }
  
  public int getScanlineStride() { return this.scanlineStride; }
  
  public int getPixelStride() { return this.pixelStride; }
  
  public short[] getDataStorage() { return this.data; }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject) {
    short[] arrayOfShort;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfShort = new short[this.numDataElements];
    } else {
      arrayOfShort = (short[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    for (byte b = 0; b < this.numDataElements; b++)
      arrayOfShort[b] = this.data[this.dataOffsets[b] + i]; 
    return arrayOfShort;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    short[] arrayOfShort;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfShort = new short[paramInt3 * paramInt4 * this.numDataElements];
    } else {
      arrayOfShort = (short[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < paramInt4) {
      int j = i;
      byte b = 0;
      while (b < paramInt3) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          arrayOfShort[b1++] = this.data[this.dataOffsets[b3] + j]; 
        b++;
        j += this.pixelStride;
      } 
      b2++;
      i += this.scanlineStride;
    } 
    return arrayOfShort;
  }
  
  public short[] getShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, short[] paramArrayOfShort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfShort == null)
      paramArrayOfShort = new short[this.numDataElements * paramInt3 * paramInt4]; 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride + this.dataOffsets[paramInt5];
    int j = 0;
    if (this.pixelStride == 1) {
      if (this.scanlineStride == paramInt3) {
        System.arraycopy(this.data, i, paramArrayOfShort, 0, paramInt3 * paramInt4);
      } else {
        byte b = 0;
        while (b < paramInt4) {
          System.arraycopy(this.data, i, paramArrayOfShort, j, paramInt3);
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
          paramArrayOfShort[j++] = this.data[k];
          b1++;
          k += this.pixelStride;
        } 
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
      paramArrayOfShort = new short[this.numDataElements * paramInt3 * paramInt4]; 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < paramInt4) {
      int j = i;
      byte b = 0;
      while (b < paramInt3) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          paramArrayOfShort[b1++] = this.data[this.dataOffsets[b3] + j]; 
        b++;
        j += this.pixelStride;
      } 
      b2++;
      i += this.scanlineStride;
    } 
    return paramArrayOfShort;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    short[] arrayOfShort = (short[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    for (byte b = 0; b < this.numDataElements; b++)
      this.data[this.dataOffsets[b] + i] = arrayOfShort[b]; 
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
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < paramInt4) {
      int j = i;
      byte b = 0;
      while (b < paramInt3) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          this.data[this.dataOffsets[b3] + j] = arrayOfShort[b1++]; 
        b++;
        j += this.pixelStride;
      } 
      b2++;
      i += this.scanlineStride;
    } 
    markDirty();
  }
  
  public void putShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, short[] paramArrayOfShort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride + this.dataOffsets[paramInt5];
    int j = 0;
    if (this.pixelStride == 1) {
      if (this.scanlineStride == paramInt3) {
        System.arraycopy(paramArrayOfShort, 0, this.data, i, paramInt3 * paramInt4);
      } else {
        byte b = 0;
        while (b < paramInt4) {
          System.arraycopy(paramArrayOfShort, j, this.data, i, paramInt3);
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
          this.data[k] = paramArrayOfShort[j++];
          b1++;
          k += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
    } 
    markDirty();
  }
  
  public void putShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short[] paramArrayOfShort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < paramInt4) {
      int j = i;
      byte b = 0;
      while (b < paramInt3) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          this.data[this.dataOffsets[b3] + j] = paramArrayOfShort[b1++]; 
        b++;
        j += this.pixelStride;
      } 
      b2++;
      i += this.scanlineStride;
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
    return new ShortInterleavedRaster(sampleModel, this.dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new RasterFormatException("negative " + ((paramInt1 <= 0) ? "width" : "height")); 
    SampleModel sampleModel = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new ShortInterleavedRaster(sampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster() { return createCompatibleWritableRaster(this.width, this.height); }
  
  public String toString() { return new String("ShortInterleavedRaster: width = " + this.width + " height = " + this.height + " #numDataElements " + this.numDataElements); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ShortInterleavedRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */