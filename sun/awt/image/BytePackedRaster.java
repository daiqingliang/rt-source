package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class BytePackedRaster extends SunWritableRaster {
  int dataBitOffset;
  
  int scanlineStride;
  
  int pixelBitStride;
  
  int bitMask;
  
  byte[] data;
  
  int shiftOffset;
  
  int type;
  
  private int maxX = this.minX + this.width;
  
  private int maxY = this.minY + this.height;
  
  private static native void initIDs();
  
  public BytePackedRaster(SampleModel paramSampleModel, Point paramPoint) { this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public BytePackedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) { this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public BytePackedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, BytePackedRaster paramBytePackedRaster) {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramBytePackedRaster);
    if (!(paramDataBuffer instanceof DataBufferByte))
      throw new RasterFormatException("BytePackedRasters must havebyte DataBuffers"); 
    DataBufferByte dataBufferByte = (DataBufferByte)paramDataBuffer;
    this.data = stealData(dataBufferByte, 0);
    if (dataBufferByte.getNumBanks() != 1)
      throw new RasterFormatException("DataBuffer for BytePackedRasters must only have 1 bank."); 
    int i = dataBufferByte.getOffset();
    if (paramSampleModel instanceof MultiPixelPackedSampleModel) {
      MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)paramSampleModel;
      this.type = 11;
      this.pixelBitStride = multiPixelPackedSampleModel.getPixelBitStride();
      if (this.pixelBitStride != 1 && this.pixelBitStride != 2 && this.pixelBitStride != 4)
        throw new RasterFormatException("BytePackedRasters must have a bit depth of 1, 2, or 4"); 
      this.scanlineStride = multiPixelPackedSampleModel.getScanlineStride();
      this.dataBitOffset = multiPixelPackedSampleModel.getDataBitOffset() + i * 8;
      int j = paramRectangle.x - paramPoint.x;
      int k = paramRectangle.y - paramPoint.y;
      this.dataBitOffset += j * this.pixelBitStride + k * this.scanlineStride * 8;
      this.bitMask = (1 << this.pixelBitStride) - 1;
      this.shiftOffset = 8 - this.pixelBitStride;
    } else {
      throw new RasterFormatException("BytePackedRasters must haveMultiPixelPackedSampleModel");
    } 
    verify(false);
  }
  
  public int getDataBitOffset() { return this.dataBitOffset; }
  
  public int getScanlineStride() { return this.scanlineStride; }
  
  public int getPixelBitStride() { return this.pixelBitStride; }
  
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
    int i = this.dataBitOffset + (paramInt1 - this.minX) * this.pixelBitStride;
    byte b = this.data[(paramInt2 - this.minY) * this.scanlineStride + (i >> 3)] & 0xFF;
    int j = this.shiftOffset - (i & 0x7);
    arrayOfByte[0] = (byte)(b >> j & this.bitMask);
    return arrayOfByte;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) { return getByteData(paramInt1, paramInt2, paramInt3, paramInt4, (byte[])paramObject); }
  
  public Object getPixelData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    byte[] arrayOfByte1;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfByte1 = new byte[this.numDataElements * paramInt3 * paramInt4];
    } else {
      arrayOfByte1 = (byte[])paramObject;
    } 
    int i = this.pixelBitStride;
    int j = this.dataBitOffset + (paramInt1 - this.minX) * i;
    int k = (paramInt2 - this.minY) * this.scanlineStride;
    byte b1 = 0;
    byte[] arrayOfByte2 = this.data;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int m = j;
      for (byte b = 0; b < paramInt3; b++) {
        int n = this.shiftOffset - (m & 0x7);
        arrayOfByte1[b1++] = (byte)(this.bitMask & arrayOfByte2[k + (m >> 3)] >> n);
        m += i;
      } 
      k += this.scanlineStride;
    } 
    return arrayOfByte1;
  }
  
  public byte[] getByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte) { return getByteData(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte); }
  
  public byte[] getByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfByte == null)
      paramArrayOfByte = new byte[paramInt3 * paramInt4]; 
    int i = this.pixelBitStride;
    int j = this.dataBitOffset + (paramInt1 - this.minX) * i;
    int k = (paramInt2 - this.minY) * this.scanlineStride;
    byte b1 = 0;
    byte[] arrayOfByte = this.data;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int m = j;
      byte b;
      for (b = 0; b < paramInt3 && (m & 0x7) != 0; b++) {
        int i1 = this.shiftOffset - (m & 0x7);
        paramArrayOfByte[b1++] = (byte)(this.bitMask & arrayOfByte[k + (m >> 3)] >> i1);
        m += i;
      } 
      int n = k + (m >> 3);
      switch (i) {
        case 1:
          while (b < paramInt3 - 7) {
            byte b3 = arrayOfByte[n++];
            paramArrayOfByte[b1++] = (byte)(b3 >> 7 & true);
            paramArrayOfByte[b1++] = (byte)(b3 >> 6 & true);
            paramArrayOfByte[b1++] = (byte)(b3 >> 5 & true);
            paramArrayOfByte[b1++] = (byte)(b3 >> 4 & true);
            paramArrayOfByte[b1++] = (byte)(b3 >> 3 & true);
            paramArrayOfByte[b1++] = (byte)(b3 >> 2 & true);
            paramArrayOfByte[b1++] = (byte)(b3 >> 1 & true);
            paramArrayOfByte[b1++] = (byte)(b3 & true);
            m += 8;
            b += 8;
          } 
          break;
        case 2:
          while (b < paramInt3 - 7) {
            byte b3 = arrayOfByte[n++];
            paramArrayOfByte[b1++] = (byte)(b3 >> 6 & 0x3);
            paramArrayOfByte[b1++] = (byte)(b3 >> 4 & 0x3);
            paramArrayOfByte[b1++] = (byte)(b3 >> 2 & 0x3);
            paramArrayOfByte[b1++] = (byte)(b3 & 0x3);
            b3 = arrayOfByte[n++];
            paramArrayOfByte[b1++] = (byte)(b3 >> 6 & 0x3);
            paramArrayOfByte[b1++] = (byte)(b3 >> 4 & 0x3);
            paramArrayOfByte[b1++] = (byte)(b3 >> 2 & 0x3);
            paramArrayOfByte[b1++] = (byte)(b3 & 0x3);
            m += 16;
            b += 8;
          } 
          break;
        case 4:
          while (b < paramInt3 - 7) {
            byte b3 = arrayOfByte[n++];
            paramArrayOfByte[b1++] = (byte)(b3 >> 4 & 0xF);
            paramArrayOfByte[b1++] = (byte)(b3 & 0xF);
            b3 = arrayOfByte[n++];
            paramArrayOfByte[b1++] = (byte)(b3 >> 4 & 0xF);
            paramArrayOfByte[b1++] = (byte)(b3 & 0xF);
            b3 = arrayOfByte[n++];
            paramArrayOfByte[b1++] = (byte)(b3 >> 4 & 0xF);
            paramArrayOfByte[b1++] = (byte)(b3 & 0xF);
            b3 = arrayOfByte[n++];
            paramArrayOfByte[b1++] = (byte)(b3 >> 4 & 0xF);
            paramArrayOfByte[b1++] = (byte)(b3 & 0xF);
            m += 32;
            b += 8;
          } 
          break;
      } 
      while (b < paramInt3) {
        int i1 = this.shiftOffset - (m & 0x7);
        paramArrayOfByte[b1++] = (byte)(this.bitMask & arrayOfByte[k + (m >> 3)] >> i1);
        m += i;
        b++;
      } 
      k += this.scanlineStride;
    } 
    return paramArrayOfByte;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    byte[] arrayOfByte = (byte[])paramObject;
    int i = this.dataBitOffset + (paramInt1 - this.minX) * this.pixelBitStride;
    int j = (paramInt2 - this.minY) * this.scanlineStride + (i >> 3);
    int k = this.shiftOffset - (i & 0x7);
    byte b = this.data[j];
    b = (byte)(b & (this.bitMask << k ^ 0xFFFFFFFF));
    b = (byte)(b | (arrayOfByte[0] & this.bitMask) << k);
    this.data[j] = b;
    markDirty();
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster) {
    if (!(paramRaster instanceof BytePackedRaster) || ((BytePackedRaster)paramRaster).pixelBitStride != this.pixelBitStride) {
      super.setDataElements(paramInt1, paramInt2, paramRaster);
      return;
    } 
    int i = paramRaster.getMinX();
    int j = paramRaster.getMinY();
    int k = i + paramInt1;
    int m = j + paramInt2;
    int n = paramRaster.getWidth();
    int i1 = paramRaster.getHeight();
    if (k < this.minX || m < this.minY || k + n > this.maxX || m + i1 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    setDataElements(k, m, i, j, n, i1, (BytePackedRaster)paramRaster);
  }
  
  private void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, BytePackedRaster paramBytePackedRaster) {
    if (paramInt5 <= 0 || paramInt6 <= 0)
      return; 
    byte[] arrayOfByte1 = paramBytePackedRaster.data;
    byte[] arrayOfByte2 = this.data;
    int i = paramBytePackedRaster.scanlineStride;
    int j = this.scanlineStride;
    int k = paramBytePackedRaster.dataBitOffset + 8 * (paramInt4 - paramBytePackedRaster.minY) * i + (paramInt3 - paramBytePackedRaster.minX) * paramBytePackedRaster.pixelBitStride;
    int m = this.dataBitOffset + 8 * (paramInt2 - this.minY) * j + (paramInt1 - this.minX) * this.pixelBitStride;
    int n = paramInt5 * this.pixelBitStride;
    if ((k & 0x7) == (m & 0x7)) {
      int i1 = m & 0x7;
      if (i1 != 0) {
        int i2 = 8 - i1;
        int i3 = k >> 3;
        int i4 = m >> 3;
        int i5 = 255 >> i1;
        if (n < i2) {
          i5 &= 255 << i2 - n;
          i2 = n;
        } 
        for (byte b = 0; b < paramInt6; b++) {
          byte b1 = arrayOfByte2[i4];
          b1 &= (i5 ^ 0xFFFFFFFF);
          b1 |= arrayOfByte1[i3] & i5;
          arrayOfByte2[i4] = (byte)b1;
          i3 += i;
          i4 += j;
        } 
        k += i2;
        m += i2;
        n -= i2;
      } 
      if (n >= 8) {
        int i2 = k >> 3;
        int i3 = m >> 3;
        int i4 = n >> 3;
        if (i4 == i && i == j) {
          System.arraycopy(arrayOfByte1, i2, arrayOfByte2, i3, i * paramInt6);
        } else {
          for (byte b = 0; b < paramInt6; b++) {
            System.arraycopy(arrayOfByte1, i2, arrayOfByte2, i3, i4);
            i2 += i;
            i3 += j;
          } 
        } 
        int i5 = i4 * 8;
        k += i5;
        m += i5;
        n -= i5;
      } 
      if (n > 0) {
        int i2 = k >> 3;
        int i3 = m >> 3;
        int i4 = 65280 >> n & 0xFF;
        for (byte b = 0; b < paramInt6; b++) {
          byte b1 = arrayOfByte2[i3];
          b1 &= (i4 ^ 0xFFFFFFFF);
          b1 |= arrayOfByte1[i2] & i4;
          arrayOfByte2[i3] = (byte)b1;
          i2 += i;
          i3 += j;
        } 
      } 
    } else {
      int i1 = m & 0x7;
      if (i1 != 0 || n < 8) {
        int i2 = 8 - i1;
        int i3 = k >> 3;
        int i4 = m >> 3;
        int i5 = k & 0x7;
        int i6 = 8 - i5;
        int i7 = 255 >> i1;
        if (n < i2) {
          i7 &= 255 << i2 - n;
          i2 = n;
        } 
        int i8 = arrayOfByte1.length - 1;
        for (byte b = 0; b < paramInt6; b++) {
          byte b1 = arrayOfByte1[i3];
          byte b2 = 0;
          if (i3 < i8)
            b2 = arrayOfByte1[i3 + 1]; 
          byte b3 = arrayOfByte2[i4];
          b3 &= (i7 ^ 0xFFFFFFFF);
          b3 |= (b1 << i5 | (b2 & 0xFF) >> i6) >> i1 & i7;
          arrayOfByte2[i4] = (byte)b3;
          i3 += i;
          i4 += j;
        } 
        k += i2;
        m += i2;
        n -= i2;
      } 
      if (n >= 8) {
        int i2 = k >> 3;
        int i3 = m >> 3;
        int i4 = n >> 3;
        int i5 = k & 0x7;
        int i6 = 8 - i5;
        int i7;
        for (i7 = 0; i7 < paramInt6; i7++) {
          int i8 = i2 + i7 * i;
          int i9 = i3 + i7 * j;
          byte b = arrayOfByte1[i8];
          for (byte b1 = 0; b1 < i4; b1++) {
            byte b2 = arrayOfByte1[i8 + 1];
            byte b3 = b << i5 | (b2 & 0xFF) >> i6;
            arrayOfByte2[i9] = (byte)b3;
            b = b2;
            i8++;
            i9++;
          } 
        } 
        i7 = i4 * 8;
        k += i7;
        m += i7;
        n -= i7;
      } 
      if (n > 0) {
        int i2 = k >> 3;
        int i3 = m >> 3;
        int i4 = 65280 >> n & 0xFF;
        int i5 = k & 0x7;
        int i6 = 8 - i5;
        int i7 = arrayOfByte1.length - 1;
        for (byte b = 0; b < paramInt6; b++) {
          byte b1 = arrayOfByte1[i2];
          byte b2 = 0;
          if (i2 < i7)
            b2 = arrayOfByte1[i2 + 1]; 
          byte b3 = arrayOfByte2[i3];
          b3 &= (i4 ^ 0xFFFFFFFF);
          b3 |= (b1 << i5 | (b2 & 0xFF) >> i6) & i4;
          arrayOfByte2[i3] = (byte)b3;
          i2 += i;
          i3 += j;
        } 
      } 
    } 
    markDirty();
  }
  
  public void setRect(int paramInt1, int paramInt2, Raster paramRaster) {
    if (!(paramRaster instanceof BytePackedRaster) || ((BytePackedRaster)paramRaster).pixelBitStride != this.pixelBitStride) {
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
    setDataElements(n, i1, k, m, i, j, (BytePackedRaster)paramRaster);
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) { putByteData(paramInt1, paramInt2, paramInt3, paramInt4, (byte[])paramObject); }
  
  public void putByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte) { putByteData(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte); }
  
  public void putByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramInt3 == 0 || paramInt4 == 0)
      return; 
    int i = this.pixelBitStride;
    int j = this.dataBitOffset + (paramInt1 - this.minX) * i;
    int k = (paramInt2 - this.minY) * this.scanlineStride;
    byte b1 = 0;
    byte[] arrayOfByte = this.data;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int m = j;
      byte b;
      for (b = 0; b < paramInt3 && (m & 0x7) != 0; b++) {
        int i1 = this.shiftOffset - (m & 0x7);
        byte b3 = arrayOfByte[k + (m >> 3)];
        b3 &= (this.bitMask << i1 ^ 0xFFFFFFFF);
        b3 |= (paramArrayOfByte[b1++] & this.bitMask) << i1;
        arrayOfByte[k + (m >> 3)] = (byte)b3;
        m += i;
      } 
      int n = k + (m >> 3);
      switch (i) {
        case 1:
          while (b < paramInt3 - 7) {
            byte b3 = (paramArrayOfByte[b1++] & true) << 7;
            b3 |= (paramArrayOfByte[b1++] & true) << 6;
            b3 |= (paramArrayOfByte[b1++] & true) << 5;
            b3 |= (paramArrayOfByte[b1++] & true) << 4;
            b3 |= (paramArrayOfByte[b1++] & true) << 3;
            b3 |= (paramArrayOfByte[b1++] & true) << 2;
            b3 |= (paramArrayOfByte[b1++] & true) << 1;
            b3 |= paramArrayOfByte[b1++] & true;
            arrayOfByte[n++] = (byte)b3;
            m += 8;
            b += 8;
          } 
          break;
        case 2:
          while (b < paramInt3 - 7) {
            byte b3 = (paramArrayOfByte[b1++] & 0x3) << 6;
            b3 |= (paramArrayOfByte[b1++] & 0x3) << 4;
            b3 |= (paramArrayOfByte[b1++] & 0x3) << 2;
            b3 |= paramArrayOfByte[b1++] & 0x3;
            arrayOfByte[n++] = (byte)b3;
            b3 = (paramArrayOfByte[b1++] & 0x3) << 6;
            b3 |= (paramArrayOfByte[b1++] & 0x3) << 4;
            b3 |= (paramArrayOfByte[b1++] & 0x3) << 2;
            b3 |= paramArrayOfByte[b1++] & 0x3;
            arrayOfByte[n++] = (byte)b3;
            m += 16;
            b += 8;
          } 
          break;
        case 4:
          while (b < paramInt3 - 7) {
            byte b3 = (paramArrayOfByte[b1++] & 0xF) << 4;
            b3 |= paramArrayOfByte[b1++] & 0xF;
            arrayOfByte[n++] = (byte)b3;
            b3 = (paramArrayOfByte[b1++] & 0xF) << 4;
            b3 |= paramArrayOfByte[b1++] & 0xF;
            arrayOfByte[n++] = (byte)b3;
            b3 = (paramArrayOfByte[b1++] & 0xF) << 4;
            b3 |= paramArrayOfByte[b1++] & 0xF;
            arrayOfByte[n++] = (byte)b3;
            b3 = (paramArrayOfByte[b1++] & 0xF) << 4;
            b3 |= paramArrayOfByte[b1++] & 0xF;
            arrayOfByte[n++] = (byte)b3;
            m += 32;
            b += 8;
          } 
          break;
      } 
      while (b < paramInt3) {
        int i1 = this.shiftOffset - (m & 0x7);
        byte b3 = arrayOfByte[k + (m >> 3)];
        b3 &= (this.bitMask << i1 ^ 0xFFFFFFFF);
        b3 |= (paramArrayOfByte[b1++] & this.bitMask) << i1;
        arrayOfByte[k + (m >> 3)] = (byte)b3;
        m += i;
        b++;
      } 
      k += this.scanlineStride;
    } 
    markDirty();
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[paramInt3 * paramInt4]; 
    int i = this.pixelBitStride;
    int j = this.dataBitOffset + (paramInt1 - this.minX) * i;
    int k = (paramInt2 - this.minY) * this.scanlineStride;
    byte b1 = 0;
    byte[] arrayOfByte = this.data;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int m = j;
      byte b;
      for (b = 0; b < paramInt3 && (m & 0x7) != 0; b++) {
        int i1 = this.shiftOffset - (m & 0x7);
        paramArrayOfInt[b1++] = this.bitMask & arrayOfByte[k + (m >> 3)] >> i1;
        m += i;
      } 
      int n = k + (m >> 3);
      switch (i) {
        case 1:
          while (b < paramInt3 - 7) {
            byte b3 = arrayOfByte[n++];
            paramArrayOfInt[b1++] = b3 >> 7 & true;
            paramArrayOfInt[b1++] = b3 >> 6 & true;
            paramArrayOfInt[b1++] = b3 >> 5 & true;
            paramArrayOfInt[b1++] = b3 >> 4 & true;
            paramArrayOfInt[b1++] = b3 >> 3 & true;
            paramArrayOfInt[b1++] = b3 >> 2 & true;
            paramArrayOfInt[b1++] = b3 >> 1 & true;
            paramArrayOfInt[b1++] = b3 & true;
            m += 8;
            b += 8;
          } 
          break;
        case 2:
          while (b < paramInt3 - 7) {
            byte b3 = arrayOfByte[n++];
            paramArrayOfInt[b1++] = b3 >> 6 & 0x3;
            paramArrayOfInt[b1++] = b3 >> 4 & 0x3;
            paramArrayOfInt[b1++] = b3 >> 2 & 0x3;
            paramArrayOfInt[b1++] = b3 & 0x3;
            b3 = arrayOfByte[n++];
            paramArrayOfInt[b1++] = b3 >> 6 & 0x3;
            paramArrayOfInt[b1++] = b3 >> 4 & 0x3;
            paramArrayOfInt[b1++] = b3 >> 2 & 0x3;
            paramArrayOfInt[b1++] = b3 & 0x3;
            m += 16;
            b += 8;
          } 
          break;
        case 4:
          while (b < paramInt3 - 7) {
            byte b3 = arrayOfByte[n++];
            paramArrayOfInt[b1++] = b3 >> 4 & 0xF;
            paramArrayOfInt[b1++] = b3 & 0xF;
            b3 = arrayOfByte[n++];
            paramArrayOfInt[b1++] = b3 >> 4 & 0xF;
            paramArrayOfInt[b1++] = b3 & 0xF;
            b3 = arrayOfByte[n++];
            paramArrayOfInt[b1++] = b3 >> 4 & 0xF;
            paramArrayOfInt[b1++] = b3 & 0xF;
            b3 = arrayOfByte[n++];
            paramArrayOfInt[b1++] = b3 >> 4 & 0xF;
            paramArrayOfInt[b1++] = b3 & 0xF;
            m += 32;
            b += 8;
          } 
          break;
      } 
      while (b < paramInt3) {
        int i1 = this.shiftOffset - (m & 0x7);
        paramArrayOfInt[b1++] = this.bitMask & arrayOfByte[k + (m >> 3)] >> i1;
        m += i;
        b++;
      } 
      k += this.scanlineStride;
    } 
    return paramArrayOfInt;
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = this.pixelBitStride;
    int j = this.dataBitOffset + (paramInt1 - this.minX) * i;
    int k = (paramInt2 - this.minY) * this.scanlineStride;
    byte b1 = 0;
    byte[] arrayOfByte = this.data;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int m = j;
      byte b;
      for (b = 0; b < paramInt3 && (m & 0x7) != 0; b++) {
        int i1 = this.shiftOffset - (m & 0x7);
        byte b3 = arrayOfByte[k + (m >> 3)];
        b3 &= (this.bitMask << i1 ^ 0xFFFFFFFF);
        b3 |= (paramArrayOfInt[b1++] & this.bitMask) << i1;
        arrayOfByte[k + (m >> 3)] = (byte)b3;
        m += i;
      } 
      int n = k + (m >> 3);
      switch (i) {
        case 1:
          while (b < paramInt3 - 7) {
            int i1 = (paramArrayOfInt[b1++] & true) << 7;
            i1 |= (paramArrayOfInt[b1++] & true) << 6;
            i1 |= (paramArrayOfInt[b1++] & true) << 5;
            i1 |= (paramArrayOfInt[b1++] & true) << 4;
            i1 |= (paramArrayOfInt[b1++] & true) << 3;
            i1 |= (paramArrayOfInt[b1++] & true) << 2;
            i1 |= (paramArrayOfInt[b1++] & true) << 1;
            i1 |= paramArrayOfInt[b1++] & true;
            arrayOfByte[n++] = (byte)i1;
            m += 8;
            b += 8;
          } 
          break;
        case 2:
          while (b < paramInt3 - 7) {
            int i1 = (paramArrayOfInt[b1++] & 0x3) << 6;
            i1 |= (paramArrayOfInt[b1++] & 0x3) << 4;
            i1 |= (paramArrayOfInt[b1++] & 0x3) << 2;
            i1 |= paramArrayOfInt[b1++] & 0x3;
            arrayOfByte[n++] = (byte)i1;
            i1 = (paramArrayOfInt[b1++] & 0x3) << 6;
            i1 |= (paramArrayOfInt[b1++] & 0x3) << 4;
            i1 |= (paramArrayOfInt[b1++] & 0x3) << 2;
            i1 |= paramArrayOfInt[b1++] & 0x3;
            arrayOfByte[n++] = (byte)i1;
            m += 16;
            b += 8;
          } 
          break;
        case 4:
          while (b < paramInt3 - 7) {
            int i1 = (paramArrayOfInt[b1++] & 0xF) << 4;
            i1 |= paramArrayOfInt[b1++] & 0xF;
            arrayOfByte[n++] = (byte)i1;
            i1 = (paramArrayOfInt[b1++] & 0xF) << 4;
            i1 |= paramArrayOfInt[b1++] & 0xF;
            arrayOfByte[n++] = (byte)i1;
            i1 = (paramArrayOfInt[b1++] & 0xF) << 4;
            i1 |= paramArrayOfInt[b1++] & 0xF;
            arrayOfByte[n++] = (byte)i1;
            i1 = (paramArrayOfInt[b1++] & 0xF) << 4;
            i1 |= paramArrayOfInt[b1++] & 0xF;
            arrayOfByte[n++] = (byte)i1;
            m += 32;
            b += 8;
          } 
          break;
      } 
      while (b < paramInt3) {
        int i1 = this.shiftOffset - (m & 0x7);
        byte b3 = arrayOfByte[k + (m >> 3)];
        b3 &= (this.bitMask << i1 ^ 0xFFFFFFFF);
        b3 |= (paramArrayOfInt[b1++] & this.bitMask) << i1;
        arrayOfByte[k + (m >> 3)] = (byte)b3;
        m += i;
        b++;
      } 
      k += this.scanlineStride;
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
    return new BytePackedRaster(sampleModel, this.dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new RasterFormatException("negative " + ((paramInt1 <= 0) ? "width" : "height")); 
    SampleModel sampleModel = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new BytePackedRaster(sampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster() { return createCompatibleWritableRaster(this.width, this.height); }
  
  private void verify(boolean paramBoolean) {
    if (this.dataBitOffset < 0)
      throw new RasterFormatException("Data offsets must be >= 0"); 
    if (this.width <= 0 || this.height <= 0 || this.height > Integer.MAX_VALUE / this.width)
      throw new RasterFormatException("Invalid raster dimension"); 
    if (this.width - 1 > Integer.MAX_VALUE / this.pixelBitStride)
      throw new RasterFormatException("Invalid raster dimension"); 
    if (this.minX - this.sampleModelTranslateX < 0L || this.minY - this.sampleModelTranslateY < 0L)
      throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")"); 
    if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height)
      throw new RasterFormatException("Invalid scanline stride"); 
    if ((this.height > 1 || this.minY - this.sampleModelTranslateY > 0) && this.scanlineStride > this.data.length)
      throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride); 
    long l = this.dataBitOffset + (this.height - 1) * this.scanlineStride * 8L + (this.width - 1) * this.pixelBitStride + this.pixelBitStride - 1L;
    if (l < 0L || l / 8L >= this.data.length)
      throw new RasterFormatException("raster dimensions overflow array bounds"); 
    if (paramBoolean && this.height > 1) {
      l = (this.width * this.pixelBitStride - 1);
      if (l / 8L >= this.scanlineStride)
        throw new RasterFormatException("data for adjacent scanlines overlaps"); 
    } 
  }
  
  public String toString() { return new String("BytePackedRaster: width = " + this.width + " height = " + this.height + " #channels " + this.numBands + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY); }
  
  static  {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\BytePackedRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */