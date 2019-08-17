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

public class ByteInterleavedRaster extends ByteComponentRaster {
  boolean inOrder;
  
  int dbOffset;
  
  int dbOffsetPacked;
  
  boolean packed = false;
  
  int[] bitMasks;
  
  int[] bitOffsets;
  
  private int maxX = this.minX + this.width;
  
  private int maxY = this.minY + this.height;
  
  public ByteInterleavedRaster(SampleModel paramSampleModel, Point paramPoint) { this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public ByteInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) { this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  private boolean isInterleaved(ComponentSampleModel paramComponentSampleModel) {
    int i = this.sampleModel.getNumBands();
    if (i == 1)
      return true; 
    int[] arrayOfInt1 = paramComponentSampleModel.getBankIndices();
    for (byte b1 = 0; b1 < i; b1++) {
      if (arrayOfInt1[b1] != 0)
        return false; 
    } 
    int[] arrayOfInt2 = paramComponentSampleModel.getBandOffsets();
    int j = arrayOfInt2[0];
    int k = j;
    for (byte b2 = 1; b2 < i; b2++) {
      int m = arrayOfInt2[b2];
      if (m < j)
        j = m; 
      if (m > k)
        k = m; 
    } 
    return !(k - j >= paramComponentSampleModel.getPixelStride());
  }
  
  public ByteInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, ByteInterleavedRaster paramByteInterleavedRaster) {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramByteInterleavedRaster);
    if (!(paramDataBuffer instanceof DataBufferByte))
      throw new RasterFormatException("ByteInterleavedRasters must have byte DataBuffers"); 
    DataBufferByte dataBufferByte = (DataBufferByte)paramDataBuffer;
    this.data = stealData(dataBufferByte, 0);
    int i = paramRectangle.x - paramPoint.x;
    int j = paramRectangle.y - paramPoint.y;
    if (paramSampleModel instanceof java.awt.image.PixelInterleavedSampleModel || (paramSampleModel instanceof ComponentSampleModel && isInterleaved((ComponentSampleModel)paramSampleModel))) {
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)paramSampleModel;
      this.scanlineStride = componentSampleModel.getScanlineStride();
      this.pixelStride = componentSampleModel.getPixelStride();
      this.dataOffsets = componentSampleModel.getBandOffsets();
      for (byte b = 0; b < getNumDataElements(); b++)
        this.dataOffsets[b] = this.dataOffsets[b] + i * this.pixelStride + j * this.scanlineStride; 
    } else if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      this.packed = true;
      this.bitMasks = singlePixelPackedSampleModel.getBitMasks();
      this.bitOffsets = singlePixelPackedSampleModel.getBitOffsets();
      this.scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
      this.pixelStride = 1;
      this.dataOffsets = new int[1];
      this.dataOffsets[0] = dataBufferByte.getOffset();
      this.dataOffsets[0] = this.dataOffsets[0] + i * this.pixelStride + j * this.scanlineStride;
    } else {
      throw new RasterFormatException("ByteInterleavedRasters must have PixelInterleavedSampleModel, SinglePixelPackedSampleModel or interleaved ComponentSampleModel.  Sample model is " + paramSampleModel);
    } 
    this.bandOffset = this.dataOffsets[0];
    this.dbOffsetPacked = paramDataBuffer.getOffset() - this.sampleModelTranslateY * this.scanlineStride - this.sampleModelTranslateX * this.pixelStride;
    this.dbOffset = this.dbOffsetPacked - i * this.pixelStride + j * this.scanlineStride;
    this.inOrder = false;
    if (this.numDataElements == this.pixelStride) {
      this.inOrder = true;
      for (byte b = 1; b < this.numDataElements; b++) {
        if (this.dataOffsets[b] - this.dataOffsets[0] != b) {
          this.inOrder = false;
          break;
        } 
      } 
    } 
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
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) { return getByteData(paramInt1, paramInt2, paramInt3, paramInt4, (byte[])paramObject); }
  
  public byte[] getByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfByte == null)
      paramArrayOfByte = new byte[paramInt3 * paramInt4]; 
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
      paramArrayOfByte = new byte[this.numDataElements * paramInt3 * paramInt4]; 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    int j = 0;
    if (this.inOrder) {
      i += this.dataOffsets[0];
      int k = paramInt3 * this.pixelStride;
      if (this.scanlineStride == k) {
        System.arraycopy(this.data, i, paramArrayOfByte, j, k * paramInt4);
      } else {
        byte b = 0;
        while (b < paramInt4) {
          System.arraycopy(this.data, i, paramArrayOfByte, j, k);
          j += k;
          b++;
          i += this.scanlineStride;
        } 
      } 
    } else if (this.numDataElements == 1) {
      i += this.dataOffsets[0];
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
    } else if (this.numDataElements == 2) {
      i += this.dataOffsets[0];
      int k = this.dataOffsets[1] - this.dataOffsets[0];
      byte b = 0;
      while (b < paramInt4) {
        int m = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          paramArrayOfByte[j++] = this.data[m];
          paramArrayOfByte[j++] = this.data[m + k];
          b1++;
          m += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
    } else if (this.numDataElements == 3) {
      i += this.dataOffsets[0];
      int k = this.dataOffsets[1] - this.dataOffsets[0];
      int m = this.dataOffsets[2] - this.dataOffsets[0];
      byte b = 0;
      while (b < paramInt4) {
        int n = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          paramArrayOfByte[j++] = this.data[n];
          paramArrayOfByte[j++] = this.data[n + k];
          paramArrayOfByte[j++] = this.data[n + m];
          b1++;
          n += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
    } else if (this.numDataElements == 4) {
      i += this.dataOffsets[0];
      int k = this.dataOffsets[1] - this.dataOffsets[0];
      int m = this.dataOffsets[2] - this.dataOffsets[0];
      int n = this.dataOffsets[3] - this.dataOffsets[0];
      byte b = 0;
      while (b < paramInt4) {
        int i1 = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          paramArrayOfByte[j++] = this.data[i1];
          paramArrayOfByte[j++] = this.data[i1 + k];
          paramArrayOfByte[j++] = this.data[i1 + m];
          paramArrayOfByte[j++] = this.data[i1 + n];
          b1++;
          i1 += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
    } else {
      byte b = 0;
      while (b < paramInt4) {
        int k = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          for (byte b2 = 0; b2 < this.numDataElements; b2++)
            paramArrayOfByte[j++] = this.data[this.dataOffsets[b2] + k]; 
          b1++;
          k += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
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
    int i = paramRaster.getMinX();
    int j = paramRaster.getMinY();
    int k = paramInt1 + i;
    int m = paramInt2 + j;
    int n = paramRaster.getWidth();
    int i1 = paramRaster.getHeight();
    if (k < this.minX || m < this.minY || k + n > this.maxX || m + i1 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    setDataElements(k, m, i, j, n, i1, paramRaster);
  }
  
  private void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Raster paramRaster) {
    if (paramInt5 <= 0 || paramInt6 <= 0)
      return; 
    int i = paramRaster.getMinX();
    int j = paramRaster.getMinY();
    Object object = null;
    if (paramRaster instanceof ByteInterleavedRaster) {
      ByteInterleavedRaster byteInterleavedRaster = (ByteInterleavedRaster)paramRaster;
      byte[] arrayOfByte = byteInterleavedRaster.getDataStorage();
      if (this.inOrder && byteInterleavedRaster.inOrder && this.pixelStride == byteInterleavedRaster.pixelStride) {
        int m = byteInterleavedRaster.getDataOffset(0);
        int n = byteInterleavedRaster.getScanlineStride();
        int i1 = byteInterleavedRaster.getPixelStride();
        int i2 = m + (paramInt4 - j) * n + (paramInt3 - i) * i1;
        int i3 = this.dataOffsets[0] + (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
        int i4 = paramInt5 * this.pixelStride;
        for (byte b = 0; b < paramInt6; b++) {
          System.arraycopy(arrayOfByte, i2, this.data, i3, i4);
          i2 += n;
          i3 += this.scanlineStride;
        } 
        markDirty();
        return;
      } 
    } 
    for (int k = 0; k < paramInt6; k++) {
      object = paramRaster.getDataElements(i, j + k, paramInt5, 1, object);
      setDataElements(paramInt1, paramInt2 + k, paramInt5, 1, object);
    } 
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) { putByteData(paramInt1, paramInt2, paramInt3, paramInt4, (byte[])paramObject); }
  
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
    if (this.inOrder) {
      i += this.dataOffsets[0];
      int k = paramInt3 * this.pixelStride;
      if (k == this.scanlineStride) {
        System.arraycopy(paramArrayOfByte, 0, this.data, i, k * paramInt4);
      } else {
        byte b = 0;
        while (b < paramInt4) {
          System.arraycopy(paramArrayOfByte, j, this.data, i, k);
          j += k;
          b++;
          i += this.scanlineStride;
        } 
      } 
    } else if (this.numDataElements == 1) {
      i += this.dataOffsets[0];
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
    } else if (this.numDataElements == 2) {
      i += this.dataOffsets[0];
      int k = this.dataOffsets[1] - this.dataOffsets[0];
      byte b = 0;
      while (b < paramInt4) {
        int m = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          this.data[m] = paramArrayOfByte[j++];
          this.data[m + k] = paramArrayOfByte[j++];
          b1++;
          m += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
    } else if (this.numDataElements == 3) {
      i += this.dataOffsets[0];
      int k = this.dataOffsets[1] - this.dataOffsets[0];
      int m = this.dataOffsets[2] - this.dataOffsets[0];
      byte b = 0;
      while (b < paramInt4) {
        int n = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          this.data[n] = paramArrayOfByte[j++];
          this.data[n + k] = paramArrayOfByte[j++];
          this.data[n + m] = paramArrayOfByte[j++];
          b1++;
          n += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
      } 
    } else if (this.numDataElements == 4) {
      i += this.dataOffsets[0];
      int k = this.dataOffsets[1] - this.dataOffsets[0];
      int m = this.dataOffsets[2] - this.dataOffsets[0];
      int n = this.dataOffsets[3] - this.dataOffsets[0];
      byte b = 0;
      while (b < paramInt4) {
        int i1 = i;
        byte b1 = 0;
        while (b1 < paramInt3) {
          this.data[i1] = paramArrayOfByte[j++];
          this.data[i1 + k] = paramArrayOfByte[j++];
          this.data[i1 + m] = paramArrayOfByte[j++];
          this.data[i1 + n] = paramArrayOfByte[j++];
          b1++;
          i1 += this.pixelStride;
        } 
        b++;
        i += this.scanlineStride;
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
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (this.packed) {
      int j = paramInt2 * this.scanlineStride + paramInt1 + this.dbOffsetPacked;
      byte b = this.data[j];
      return (b & this.bitMasks[paramInt3]) >>> this.bitOffsets[paramInt3];
    } 
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.dbOffset;
    return this.data[i + this.dataOffsets[paramInt3]] & 0xFF;
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (this.packed) {
      int i = paramInt2 * this.scanlineStride + paramInt1 + this.dbOffsetPacked;
      int j = this.bitMasks[paramInt3];
      byte b = this.data[i];
      b = (byte)(b & (j ^ 0xFFFFFFFF));
      b = (byte)(b | paramInt4 << this.bitOffsets[paramInt3] & j);
      this.data[i] = b;
    } else {
      int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.dbOffset;
      this.data[i + this.dataOffsets[paramInt3]] = (byte)paramInt4;
    } 
    markDirty();
  }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt) {
    int[] arrayOfInt;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    } 
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    byte b = 0;
    if (this.packed) {
      i += this.dbOffsetPacked;
      int j = this.bitMasks[paramInt5];
      int k = this.bitOffsets[paramInt5];
      for (byte b1 = 0; b1 < paramInt4; b1++) {
        int m = i;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          byte b3 = this.data[m++];
          arrayOfInt[b++] = (b3 & j) >>> k;
        } 
        i += this.scanlineStride;
      } 
    } else {
      i += this.dbOffset + this.dataOffsets[paramInt5];
      for (byte b1 = 0; b1 < paramInt4; b1++) {
        int j = i;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          arrayOfInt[b++] = this.data[j] & 0xFF;
          j += this.pixelStride;
        } 
        i += this.scanlineStride;
      } 
    } 
    return arrayOfInt;
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    byte b = 0;
    if (this.packed) {
      i += this.dbOffsetPacked;
      int j = this.bitMasks[paramInt5];
      for (byte b1 = 0; b1 < paramInt4; b1++) {
        int k = i;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          byte b3 = this.data[k];
          b3 = (byte)(b3 & (j ^ 0xFFFFFFFF));
          int m = paramArrayOfInt[b++];
          b3 = (byte)(b3 | m << this.bitOffsets[paramInt5] & j);
          this.data[k++] = b3;
        } 
        i += this.scanlineStride;
      } 
    } else {
      i += this.dbOffset + this.dataOffsets[paramInt5];
      for (byte b1 = 0; b1 < paramInt4; b1++) {
        int j = i;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          this.data[j] = (byte)paramArrayOfInt[b++];
          j += this.pixelStride;
        } 
        i += this.scanlineStride;
      } 
    } 
    markDirty();
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) {
    int[] arrayOfInt;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4 * this.numBands];
    } 
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    byte b = 0;
    if (this.packed) {
      i += this.dbOffsetPacked;
      for (byte b1 = 0; b1 < paramInt4; b1++) {
        for (int j = 0; j < paramInt3; j++) {
          byte b2 = this.data[i + j];
          for (byte b3 = 0; b3 < this.numBands; b3++)
            arrayOfInt[b++] = (b2 & this.bitMasks[b3]) >>> this.bitOffsets[b3]; 
        } 
        i += this.scanlineStride;
      } 
    } else {
      i += this.dbOffset;
      int j = this.dataOffsets[0];
      if (this.numBands == 1) {
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int k = i + j;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            arrayOfInt[b++] = this.data[k] & 0xFF;
            k += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } else if (this.numBands == 2) {
        int k = this.dataOffsets[1] - j;
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int m = i + j;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            arrayOfInt[b++] = this.data[m] & 0xFF;
            arrayOfInt[b++] = this.data[m + k] & 0xFF;
            m += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } else if (this.numBands == 3) {
        int k = this.dataOffsets[1] - j;
        int m = this.dataOffsets[2] - j;
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int n = i + j;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            arrayOfInt[b++] = this.data[n] & 0xFF;
            arrayOfInt[b++] = this.data[n + k] & 0xFF;
            arrayOfInt[b++] = this.data[n + m] & 0xFF;
            n += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } else if (this.numBands == 4) {
        int k = this.dataOffsets[1] - j;
        int m = this.dataOffsets[2] - j;
        int n = this.dataOffsets[3] - j;
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int i1 = i + j;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            arrayOfInt[b++] = this.data[i1] & 0xFF;
            arrayOfInt[b++] = this.data[i1 + k] & 0xFF;
            arrayOfInt[b++] = this.data[i1 + m] & 0xFF;
            arrayOfInt[b++] = this.data[i1 + n] & 0xFF;
            i1 += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } else {
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int k = i;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            for (byte b3 = 0; b3 < this.numBands; b3++)
              arrayOfInt[b++] = this.data[k + this.dataOffsets[b3]] & 0xFF; 
            k += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } 
    } 
    return arrayOfInt;
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    byte b = 0;
    if (this.packed) {
      i += this.dbOffsetPacked;
      for (byte b1 = 0; b1 < paramInt4; b1++) {
        for (int j = 0; j < paramInt3; j++) {
          int k = 0;
          for (byte b2 = 0; b2 < this.numBands; b2++) {
            int m = paramArrayOfInt[b++];
            k |= m << this.bitOffsets[b2] & this.bitMasks[b2];
          } 
          this.data[i + j] = (byte)k;
        } 
        i += this.scanlineStride;
      } 
    } else {
      i += this.dbOffset;
      int j = this.dataOffsets[0];
      if (this.numBands == 1) {
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int k = i + j;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            this.data[k] = (byte)paramArrayOfInt[b++];
            k += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } else if (this.numBands == 2) {
        int k = this.dataOffsets[1] - j;
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int m = i + j;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            this.data[m] = (byte)paramArrayOfInt[b++];
            this.data[m + k] = (byte)paramArrayOfInt[b++];
            m += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } else if (this.numBands == 3) {
        int k = this.dataOffsets[1] - j;
        int m = this.dataOffsets[2] - j;
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int n = i + j;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            this.data[n] = (byte)paramArrayOfInt[b++];
            this.data[n + k] = (byte)paramArrayOfInt[b++];
            this.data[n + m] = (byte)paramArrayOfInt[b++];
            n += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } else if (this.numBands == 4) {
        int k = this.dataOffsets[1] - j;
        int m = this.dataOffsets[2] - j;
        int n = this.dataOffsets[3] - j;
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int i1 = i + j;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            this.data[i1] = (byte)paramArrayOfInt[b++];
            this.data[i1 + k] = (byte)paramArrayOfInt[b++];
            this.data[i1 + m] = (byte)paramArrayOfInt[b++];
            this.data[i1 + n] = (byte)paramArrayOfInt[b++];
            i1 += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } else {
        for (byte b1 = 0; b1 < paramInt4; b1++) {
          int k = i;
          for (byte b2 = 0; b2 < paramInt3; b2++) {
            for (byte b3 = 0; b3 < this.numBands; b3++)
              this.data[k + this.dataOffsets[b3]] = (byte)paramArrayOfInt[b++]; 
            k += this.pixelStride;
          } 
          i += this.scanlineStride;
        } 
      } 
    } 
    markDirty();
  }
  
  public void setRect(int paramInt1, int paramInt2, Raster paramRaster) {
    if (!(paramRaster instanceof ByteInterleavedRaster)) {
      super.setRect(paramInt1, paramInt2, paramRaster);
      return;
    } 
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    int k = paramRaster.getMinX();
    int m = paramRaster.getMinY();
    int n = paramInt1 + k;
    int i1 = paramInt2 + m;
    if (n < this.minX) {
      int i2 = this.minX - n;
      i -= i2;
      k += i2;
      n = this.minX;
    } 
    if (i1 < this.minY) {
      int i2 = this.minY - i1;
      j -= i2;
      m += i2;
      i1 = this.minY;
    } 
    if (n + i > this.maxX)
      i = this.maxX - n; 
    if (i1 + j > this.maxY)
      j = this.maxY - i1; 
    setDataElements(n, i1, k, m, i, j, paramRaster);
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
    return new ByteInterleavedRaster(sampleModel, this.dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new RasterFormatException("negative " + ((paramInt1 <= 0) ? "width" : "height")); 
    SampleModel sampleModel = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new ByteInterleavedRaster(sampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster() { return createCompatibleWritableRaster(this.width, this.height); }
  
  public String toString() { return new String("ByteInterleavedRaster: width = " + this.width + " height = " + this.height + " #numDataElements " + this.numDataElements + " dataOff[0] = " + this.dataOffsets[0]); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ByteInterleavedRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */