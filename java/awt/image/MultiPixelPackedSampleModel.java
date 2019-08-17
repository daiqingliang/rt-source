package java.awt.image;

public class MultiPixelPackedSampleModel extends SampleModel {
  int pixelBitStride;
  
  int bitMask;
  
  int pixelsPerDataElement;
  
  int dataElementSize;
  
  int dataBitOffset;
  
  int scanlineStride;
  
  public MultiPixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this(paramInt1, paramInt2, paramInt3, paramInt4, (paramInt2 * paramInt4 + DataBuffer.getDataTypeSize(paramInt1) - 1) / DataBuffer.getDataTypeSize(paramInt1), 0);
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 3)
      throw new IllegalArgumentException("Unsupported data type " + paramInt1); 
  }
  
  public MultiPixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    super(paramInt1, paramInt2, paramInt3, 1);
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 3)
      throw new IllegalArgumentException("Unsupported data type " + paramInt1); 
    this.dataType = paramInt1;
    this.pixelBitStride = paramInt4;
    this.scanlineStride = paramInt5;
    this.dataBitOffset = paramInt6;
    this.dataElementSize = DataBuffer.getDataTypeSize(paramInt1);
    this.pixelsPerDataElement = this.dataElementSize / paramInt4;
    if (this.pixelsPerDataElement * paramInt4 != this.dataElementSize)
      throw new RasterFormatException("MultiPixelPackedSampleModel does not allow pixels to span data element boundaries"); 
    this.bitMask = (1 << paramInt4) - 1;
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) { return new MultiPixelPackedSampleModel(this.dataType, paramInt1, paramInt2, this.pixelBitStride); }
  
  public DataBuffer createDataBuffer() {
    DataBufferInt dataBufferInt;
    DataBufferUShort dataBufferUShort;
    DataBufferByte dataBufferByte = null;
    int i = this.scanlineStride * this.height;
    switch (this.dataType) {
      case 0:
        dataBufferByte = new DataBufferByte(i + (this.dataBitOffset + 7) / 8);
        break;
      case 1:
        dataBufferUShort = new DataBufferUShort(i + (this.dataBitOffset + 15) / 16);
        break;
      case 3:
        dataBufferInt = new DataBufferInt(i + (this.dataBitOffset + 31) / 32);
        break;
    } 
    return dataBufferInt;
  }
  
  public int getNumDataElements() { return 1; }
  
  public int[] getSampleSize() { return new int[] { this.pixelBitStride }; }
  
  public int getSampleSize(int paramInt) { return this.pixelBitStride; }
  
  public int getOffset(int paramInt1, int paramInt2) {
    null = paramInt2 * this.scanlineStride;
    return (paramInt1 * this.pixelBitStride + this.dataBitOffset) / this.dataElementSize;
  }
  
  public int getBitOffset(int paramInt) { return (paramInt * this.pixelBitStride + this.dataBitOffset) % this.dataElementSize; }
  
  public int getScanlineStride() { return this.scanlineStride; }
  
  public int getPixelBitStride() { return this.pixelBitStride; }
  
  public int getDataBitOffset() { return this.dataBitOffset; }
  
  public int getTransferType() { return (this.pixelBitStride > 16) ? 3 : ((this.pixelBitStride > 8) ? 1 : 0); }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfInt) {
    if (paramArrayOfInt != null && paramArrayOfInt.length != 1)
      throw new RasterFormatException("MultiPixelPackedSampleModel has only one band."); 
    return createCompatibleSampleModel(this.width, this.height);
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height || paramInt3 != 0)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int j = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + i / this.dataElementSize);
    int k = this.dataElementSize - (i & this.dataElementSize - 1) - this.pixelBitStride;
    return j >> k & this.bitMask;
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height || paramInt3 != 0)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int j = paramInt2 * this.scanlineStride + i / this.dataElementSize;
    int k = this.dataElementSize - (i & this.dataElementSize - 1) - this.pixelBitStride;
    int m = paramDataBuffer.getElem(j);
    m &= (this.bitMask << k ^ 0xFFFFFFFF);
    m |= (paramInt4 & this.bitMask) << k;
    paramDataBuffer.setElem(j, m);
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
    int j = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int k = this.dataElementSize - (j & this.dataElementSize - 1) - this.pixelBitStride;
    int m = 0;
    switch (i) {
      case 0:
        if (paramObject == null) {
          arrayOfByte2 = new byte[1];
        } else {
          arrayOfByte2 = (byte[])paramObject;
        } 
        m = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + j / this.dataElementSize);
        arrayOfByte2[0] = (byte)(m >> k & this.bitMask);
        arrayOfByte1 = arrayOfByte2;
        break;
      case 1:
        if (arrayOfByte1 == null) {
          arrayOfShort2 = new short[1];
        } else {
          arrayOfShort2 = (short[])arrayOfByte1;
        } 
        m = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + j / this.dataElementSize);
        arrayOfShort2[0] = (short)(m >> k & this.bitMask);
        arrayOfShort1 = arrayOfShort2;
        break;
      case 3:
        if (arrayOfShort1 == null) {
          arrayOfInt2 = new int[1];
        } else {
          arrayOfInt2 = (int[])arrayOfShort1;
        } 
        m = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + j / this.dataElementSize);
        arrayOfInt2[0] = m >> k & this.bitMask;
        arrayOfInt1 = arrayOfInt2;
        break;
    } 
    return arrayOfInt1;
  }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[this.numBands];
    } 
    int i = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int j = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + i / this.dataElementSize);
    int k = this.dataElementSize - (i & this.dataElementSize - 1) - this.pixelBitStride;
    arrayOfInt[0] = j >> k & this.bitMask;
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    int j = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int k = paramInt2 * this.scanlineStride + j / this.dataElementSize;
    int m = this.dataElementSize - (j & this.dataElementSize - 1) - this.pixelBitStride;
    int n = paramDataBuffer.getElem(k);
    n &= (this.bitMask << m ^ 0xFFFFFFFF);
    switch (i) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        n |= (arrayOfByte[0] & 0xFF & this.bitMask) << m;
        paramDataBuffer.setElem(k, n);
        break;
      case 1:
        arrayOfShort = (short[])paramObject;
        n |= (arrayOfShort[0] & 0xFFFF & this.bitMask) << m;
        paramDataBuffer.setElem(k, n);
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        n |= (arrayOfInt[0] & this.bitMask) << m;
        paramDataBuffer.setElem(k, n);
        break;
    } 
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int j = paramInt2 * this.scanlineStride + i / this.dataElementSize;
    int k = this.dataElementSize - (i & this.dataElementSize - 1) - this.pixelBitStride;
    int m = paramDataBuffer.getElem(j);
    m &= (this.bitMask << k ^ 0xFFFFFFFF);
    m |= (paramArrayOfInt[0] & this.bitMask) << k;
    paramDataBuffer.setElem(j, m);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof MultiPixelPackedSampleModel))
      return false; 
    MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)paramObject;
    return (this.width == multiPixelPackedSampleModel.width && this.height == multiPixelPackedSampleModel.height && this.numBands == multiPixelPackedSampleModel.numBands && this.dataType == multiPixelPackedSampleModel.dataType && this.pixelBitStride == multiPixelPackedSampleModel.pixelBitStride && this.bitMask == multiPixelPackedSampleModel.bitMask && this.pixelsPerDataElement == multiPixelPackedSampleModel.pixelsPerDataElement && this.dataElementSize == multiPixelPackedSampleModel.dataElementSize && this.dataBitOffset == multiPixelPackedSampleModel.dataBitOffset && this.scanlineStride == multiPixelPackedSampleModel.scanlineStride);
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
    null ^= this.pixelBitStride;
    null <<= 8;
    null ^= this.bitMask;
    null <<= 8;
    null ^= this.pixelsPerDataElement;
    null <<= 8;
    null ^= this.dataElementSize;
    null <<= 8;
    null ^= this.dataBitOffset;
    null <<= 8;
    return this.scanlineStride;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\MultiPixelPackedSampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */