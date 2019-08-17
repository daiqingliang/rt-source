package java.awt.image;

public final class BandedSampleModel extends ComponentSampleModel {
  public BandedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { super(paramInt1, paramInt2, paramInt3, 1, paramInt2, createIndicesArray(paramInt4), createOffsetArray(paramInt4)); }
  
  public BandedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2) { super(paramInt1, paramInt2, paramInt3, 1, paramInt4, paramArrayOfInt1, paramArrayOfInt2); }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    int[] arrayOfInt;
    if (this.numBanks == 1) {
      arrayOfInt = orderBands(this.bandOffsets, paramInt1 * paramInt2);
    } else {
      arrayOfInt = new int[this.bandOffsets.length];
    } 
    return new BandedSampleModel(this.dataType, paramInt1, paramInt2, paramInt1, this.bankIndices, arrayOfInt);
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfInt) {
    if (paramArrayOfInt.length > this.bankIndices.length)
      throw new RasterFormatException("There are only " + this.bankIndices.length + " bands"); 
    int[] arrayOfInt1 = new int[paramArrayOfInt.length];
    int[] arrayOfInt2 = new int[paramArrayOfInt.length];
    for (byte b = 0; b < paramArrayOfInt.length; b++) {
      arrayOfInt1[b] = this.bankIndices[paramArrayOfInt[b]];
      arrayOfInt2[b] = this.bandOffsets[paramArrayOfInt[b]];
    } 
    return new BandedSampleModel(this.dataType, this.width, this.height, this.scanlineStride, arrayOfInt1, arrayOfInt2);
  }
  
  public DataBuffer createDataBuffer() {
    null = null;
    int i = this.scanlineStride * this.height;
    switch (this.dataType) {
      case 0:
        return new DataBufferByte(i, this.numBanks);
      case 1:
        return new DataBufferUShort(i, this.numBanks);
      case 2:
        return new DataBufferShort(i, this.numBanks);
      case 3:
        return new DataBufferInt(i, this.numBanks);
      case 4:
        return new DataBufferFloat(i, this.numBanks);
      case 5:
        return new DataBufferDouble(i, this.numBanks);
    } 
    throw new IllegalArgumentException("dataType is not one of the supported types.");
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte b5;
    byte b4;
    double[] arrayOfDouble2;
    byte b3;
    float[] arrayOfFloat2;
    int[] arrayOfInt2;
    byte b2;
    byte b1;
    short[] arrayOfShort2;
    byte[] arrayOfByte2;
    float[] arrayOfFloat1;
    byte[] arrayOfByte1;
    short[] arrayOfShort1;
    double[] arrayOfDouble1;
    int[] arrayOfInt1;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt2 * this.scanlineStride + paramInt1;
    switch (i) {
      case 0:
        if (paramObject == null) {
          arrayOfByte2 = new byte[j];
        } else {
          arrayOfByte2 = (byte[])paramObject;
        } 
        for (b1 = 0; b1 < j; b1++)
          arrayOfByte2[b1] = (byte)paramDataBuffer.getElem(this.bankIndices[b1], k + this.bandOffsets[b1]); 
        arrayOfByte1 = arrayOfByte2;
        break;
      case 1:
      case 2:
        if (arrayOfByte1 == null) {
          arrayOfShort2 = new short[j];
        } else {
          arrayOfShort2 = (short[])arrayOfByte1;
        } 
        for (b2 = 0; b2 < j; b2++)
          arrayOfShort2[b2] = (short)paramDataBuffer.getElem(this.bankIndices[b2], k + this.bandOffsets[b2]); 
        arrayOfShort1 = arrayOfShort2;
        break;
      case 3:
        if (arrayOfShort1 == null) {
          arrayOfInt2 = new int[j];
        } else {
          arrayOfInt2 = (int[])arrayOfShort1;
        } 
        for (b3 = 0; b3 < j; b3++)
          arrayOfInt2[b3] = paramDataBuffer.getElem(this.bankIndices[b3], k + this.bandOffsets[b3]); 
        arrayOfInt1 = arrayOfInt2;
        break;
      case 4:
        if (arrayOfInt1 == null) {
          arrayOfFloat2 = new float[j];
        } else {
          arrayOfFloat2 = (float[])arrayOfInt1;
        } 
        for (b4 = 0; b4 < j; b4++)
          arrayOfFloat2[b4] = paramDataBuffer.getElemFloat(this.bankIndices[b4], k + this.bandOffsets[b4]); 
        arrayOfFloat1 = arrayOfFloat2;
        break;
      case 5:
        if (arrayOfFloat1 == null) {
          arrayOfDouble2 = new double[j];
        } else {
          arrayOfDouble2 = (double[])arrayOfFloat1;
        } 
        for (b5 = 0; b5 < j; b5++)
          arrayOfDouble2[b5] = paramDataBuffer.getElemDouble(this.bankIndices[b5], k + this.bandOffsets[b5]); 
        arrayOfDouble1 = arrayOfDouble2;
        break;
    } 
    return arrayOfDouble1;
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
    int i = paramInt2 * this.scanlineStride + paramInt1;
    for (byte b = 0; b < this.numBands; b++)
      arrayOfInt[b] = paramDataBuffer.getElem(this.bankIndices[b], i + this.bandOffsets[b]); 
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
    for (byte b = 0; b < this.numBands; b++) {
      int k = paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[b];
      int m = b;
      int n = this.bankIndices[b];
      for (byte b1 = 0; b1 < paramInt4; b1++) {
        int i1 = k;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          arrayOfInt[m] = paramDataBuffer.getElem(n, i1++);
          m += this.numBands;
        } 
        k += this.scanlineStride;
      } 
    } 
    return arrayOfInt;
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    return paramDataBuffer.getElem(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3]);
  }
  
  public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    return paramDataBuffer.getElemFloat(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3]);
  }
  
  public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    return paramDataBuffer.getElemDouble(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3]);
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
    int i = paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt5];
    byte b1 = 0;
    int j = this.bankIndices[paramInt5];
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int k = i;
      for (byte b = 0; b < paramInt3; b++)
        arrayOfInt[b1++] = paramDataBuffer.getElem(j, k++); 
      i += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte b5;
    double[] arrayOfDouble;
    byte b4;
    float[] arrayOfFloat;
    byte b3;
    int[] arrayOfInt;
    byte b2;
    byte b1;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt2 * this.scanlineStride + paramInt1;
    switch (i) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        for (b1 = 0; b1 < j; b1++)
          paramDataBuffer.setElem(this.bankIndices[b1], k + this.bandOffsets[b1], arrayOfByte[b1] & 0xFF); 
        break;
      case 1:
      case 2:
        arrayOfShort = (short[])paramObject;
        for (b2 = 0; b2 < j; b2++)
          paramDataBuffer.setElem(this.bankIndices[b2], k + this.bandOffsets[b2], arrayOfShort[b2] & 0xFFFF); 
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        for (b3 = 0; b3 < j; b3++)
          paramDataBuffer.setElem(this.bankIndices[b3], k + this.bandOffsets[b3], arrayOfInt[b3]); 
        break;
      case 4:
        arrayOfFloat = (float[])paramObject;
        for (b4 = 0; b4 < j; b4++)
          paramDataBuffer.setElemFloat(this.bankIndices[b4], k + this.bandOffsets[b4], arrayOfFloat[b4]); 
        break;
      case 5:
        arrayOfDouble = (double[])paramObject;
        for (b5 = 0; b5 < j; b5++)
          paramDataBuffer.setElemDouble(this.bankIndices[b5], k + this.bandOffsets[b5], arrayOfDouble[b5]); 
        break;
    } 
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1;
    for (byte b = 0; b < this.numBands; b++)
      paramDataBuffer.setElem(this.bankIndices[b], i + this.bandOffsets[b], paramArrayOfInt[b]); 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    for (byte b = 0; b < this.numBands; b++) {
      int k = paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[b];
      int m = b;
      int n = this.bankIndices[b];
      for (byte b1 = 0; b1 < paramInt4; b1++) {
        int i1 = k;
        for (byte b2 = 0; b2 < paramInt3; b2++) {
          paramDataBuffer.setElem(n, i1++, paramArrayOfInt[m]);
          m += this.numBands;
        } 
        k += this.scanlineStride;
      } 
    } 
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    paramDataBuffer.setElem(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3], paramInt4);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    paramDataBuffer.setElemFloat(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3], paramFloat);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    paramDataBuffer.setElemDouble(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3], paramDouble);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt3 > this.width || paramInt2 + paramInt4 > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt5];
    byte b1 = 0;
    int j = this.bankIndices[paramInt5];
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int k = i;
      for (byte b = 0; b < paramInt3; b++)
        paramDataBuffer.setElem(j, k++, paramArrayOfInt[b1++]); 
      i += this.scanlineStride;
    } 
  }
  
  private static int[] createOffsetArray(int paramInt) {
    int[] arrayOfInt = new int[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfInt[b] = 0; 
    return arrayOfInt;
  }
  
  private static int[] createIndicesArray(int paramInt) {
    int[] arrayOfInt = new int[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfInt[b] = b; 
    return arrayOfInt;
  }
  
  public int hashCode() { return super.hashCode() ^ 0x2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\BandedSampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */