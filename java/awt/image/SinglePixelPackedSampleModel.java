package java.awt.image;

import java.util.Arrays;

public class SinglePixelPackedSampleModel extends SampleModel {
  private int[] bitMasks;
  
  private int[] bitOffsets;
  
  private int[] bitSizes;
  
  private int maxBitSize;
  
  private int scanlineStride;
  
  private static native void initIDs();
  
  public SinglePixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt) {
    this(paramInt1, paramInt2, paramInt3, paramInt2, paramArrayOfInt);
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 3)
      throw new IllegalArgumentException("Unsupported data type " + paramInt1); 
  }
  
  public SinglePixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfInt.length);
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 3)
      throw new IllegalArgumentException("Unsupported data type " + paramInt1); 
    this.dataType = paramInt1;
    this.bitMasks = (int[])paramArrayOfInt.clone();
    this.scanlineStride = paramInt4;
    this.bitOffsets = new int[this.numBands];
    this.bitSizes = new int[this.numBands];
    int i = (int)((1L << DataBuffer.getDataTypeSize(paramInt1)) - 1L);
    this.maxBitSize = 0;
    for (byte b = 0; b < this.numBands; b++) {
      byte b1 = 0;
      byte b2 = 0;
      this.bitMasks[b] = this.bitMasks[b] & i;
      int j = this.bitMasks[b];
      if (j != 0) {
        while ((j & true) == 0) {
          j >>>= 1;
          b1++;
        } 
        while ((j & true) == 1) {
          j >>>= 1;
          b2++;
        } 
        if (j != 0)
          throw new IllegalArgumentException("Mask " + paramArrayOfInt[b] + " must be contiguous"); 
      } 
      this.bitOffsets[b] = b1;
      this.bitSizes[b] = b2;
      if (b2 > this.maxBitSize)
        this.maxBitSize = b2; 
    } 
  }
  
  public int getNumDataElements() { return 1; }
  
  private long getBufferSize() { return (this.scanlineStride * (this.height - 1) + this.width); }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) { return new SinglePixelPackedSampleModel(this.dataType, paramInt1, paramInt2, this.bitMasks); }
  
  public DataBuffer createDataBuffer() {
    DataBufferUShort dataBufferUShort;
    DataBufferInt dataBufferInt;
    DataBufferByte dataBufferByte = null;
    int i = (int)getBufferSize();
    switch (this.dataType) {
      case 0:
        dataBufferByte = new DataBufferByte(i);
        break;
      case 1:
        dataBufferUShort = new DataBufferUShort(i);
        break;
      case 3:
        dataBufferInt = new DataBufferInt(i);
        break;
    } 
    return dataBufferInt;
  }
  
  public int[] getSampleSize() { return (int[])this.bitSizes.clone(); }
  
  public int getSampleSize(int paramInt) { return this.bitSizes[paramInt]; }
  
  public int getOffset(int paramInt1, int paramInt2) { return paramInt2 * this.scanlineStride + paramInt1; }
  
  public int[] getBitOffsets() { return (int[])this.bitOffsets.clone(); }
  
  public int[] getBitMasks() { return (int[])this.bitMasks.clone(); }
  
  public int getScanlineStride() { return this.scanlineStride; }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfInt) {
    if (paramArrayOfInt.length > this.numBands)
      throw new RasterFormatException("There are only " + this.numBands + " bands"); 
    int[] arrayOfInt = new int[paramArrayOfInt.length];
    for (byte b = 0; b < paramArrayOfInt.length; b++)
      arrayOfInt[b] = this.bitMasks[paramArrayOfInt[b]]; 
    return new SinglePixelPackedSampleModel(this.dataType, this.width, this.height, this.scanlineStride, arrayOfInt);
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    int[] arrayOfInt2;
    short[] arrayOfShort2;
    byte[] arrayOfByte2;
    int[] arrayOfInt1;
    byte[] arrayOfByte1;
    short[] arrayOfShort1;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    switch (i) {
      case 0:
        if (paramObject == null) {
          arrayOfByte2 = new byte[1];
        } else {
          arrayOfByte2 = (byte[])paramObject;
        } 
        arrayOfByte2[0] = (byte)paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
        arrayOfByte1 = arrayOfByte2;
        break;
      case 1:
        if (arrayOfByte1 == null) {
          arrayOfShort2 = new short[1];
        } else {
          arrayOfShort2 = (short[])arrayOfByte1;
        } 
        arrayOfShort2[0] = (short)paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
        arrayOfShort1 = arrayOfShort2;
        break;
      case 3:
        if (arrayOfShort1 == null) {
          arrayOfInt2 = new int[1];
        } else {
          arrayOfInt2 = (int[])arrayOfShort1;
        } 
        arrayOfInt2[0] = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
        arrayOfInt1 = arrayOfInt2;
        break;
    } 
    return arrayOfInt1;
  }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[this.numBands];
    } else {
      arrayOfInt = paramArrayOfInt;
    } 
    int i = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
    for (byte b = 0; b < this.numBands; b++)
      arrayOfInt[b] = (i & this.bitMasks[b]) >>> this.bitOffsets[b]; 
    return arrayOfInt;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4 * this.numBands];
    } 
    int k = paramInt2 * this.scanlineStride + paramInt1;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      for (int m = 0; m < paramInt3; m++) {
        int n = paramDataBuffer.getElem(k + m);
        for (byte b = 0; b < this.numBands; b++)
          arrayOfInt[b1++] = (n & this.bitMasks[b]) >>> this.bitOffsets[b]; 
      } 
      k += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
    return (i & this.bitMasks[paramInt3]) >>> this.bitOffsets[paramInt3];
  }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt3 > this.width || paramInt2 + paramInt4 > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    } 
    int i = paramInt2 * this.scanlineStride + paramInt1;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      for (int j = 0; j < paramInt3; j++) {
        int k = paramDataBuffer.getElem(i + j);
        arrayOfInt[b1++] = (k & this.bitMasks[paramInt5]) >>> this.bitOffsets[paramInt5];
      } 
      i += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    switch (i) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        paramDataBuffer.setElem(paramInt2 * this.scanlineStride + paramInt1, arrayOfByte[0] & 0xFF);
        break;
      case 1:
        arrayOfShort = (short[])paramObject;
        paramDataBuffer.setElem(paramInt2 * this.scanlineStride + paramInt1, arrayOfShort[0] & 0xFFFF);
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        paramDataBuffer.setElem(paramInt2 * this.scanlineStride + paramInt1, arrayOfInt[0]);
        break;
    } 
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1;
    int j = paramDataBuffer.getElem(i);
    for (byte b = 0; b < this.numBands; b++) {
      j &= (this.bitMasks[b] ^ 0xFFFFFFFF);
      j |= paramArrayOfInt[b] << this.bitOffsets[b] & this.bitMasks[b];
    } 
    paramDataBuffer.setElem(i, j);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int k = paramInt2 * this.scanlineStride + paramInt1;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      for (int m = 0; m < paramInt3; m++) {
        int n = paramDataBuffer.getElem(k + m);
        for (byte b = 0; b < this.numBands; b++) {
          n &= (this.bitMasks[b] ^ 0xFFFFFFFF);
          int i1 = paramArrayOfInt[b1++];
          n |= i1 << this.bitOffsets[b] & this.bitMasks[b];
        } 
        paramDataBuffer.setElem(k + m, n);
      } 
      k += this.scanlineStride;
    } 
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
    i &= (this.bitMasks[paramInt3] ^ 0xFFFFFFFF);
    i |= paramInt4 << this.bitOffsets[paramInt3] & this.bitMasks[paramInt3];
    paramDataBuffer.setElem(paramInt2 * this.scanlineStride + paramInt1, i);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt3 > this.width || paramInt2 + paramInt4 > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      for (int j = 0; j < paramInt3; j++) {
        int k = paramDataBuffer.getElem(i + j);
        k &= (this.bitMasks[paramInt5] ^ 0xFFFFFFFF);
        int m = paramArrayOfInt[b1++];
        k |= m << this.bitOffsets[paramInt5] & this.bitMasks[paramInt5];
        paramDataBuffer.setElem(i + j, k);
      } 
      i += this.scanlineStride;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof SinglePixelPackedSampleModel))
      return false; 
    SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramObject;
    return (this.width == singlePixelPackedSampleModel.width && this.height == singlePixelPackedSampleModel.height && this.numBands == singlePixelPackedSampleModel.numBands && this.dataType == singlePixelPackedSampleModel.dataType && Arrays.equals(this.bitMasks, singlePixelPackedSampleModel.bitMasks) && Arrays.equals(this.bitOffsets, singlePixelPackedSampleModel.bitOffsets) && Arrays.equals(this.bitSizes, singlePixelPackedSampleModel.bitSizes) && this.maxBitSize == singlePixelPackedSampleModel.maxBitSize && this.scanlineStride == singlePixelPackedSampleModel.scanlineStride);
  }
  
  public int hashCode() {
    null = 0;
    null = this.width;
    null <<= 8;
    null ^= this.height;
    null <<= 8;
    null ^= this.numBands;
    null <<= 8;
    null ^= this.dataType;
    null <<= 8;
    byte b;
    for (b = 0; b < this.bitMasks.length; b++) {
      null ^= this.bitMasks[b];
      null <<= 8;
    } 
    for (b = 0; b < this.bitOffsets.length; b++) {
      null ^= this.bitOffsets[b];
      null <<= 8;
    } 
    for (b = 0; b < this.bitSizes.length; b++) {
      null ^= this.bitSizes[b];
      null <<= 8;
    } 
    null ^= this.maxBitSize;
    null <<= 8;
    return this.scanlineStride;
  }
  
  static  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\SinglePixelPackedSampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */