package java.awt.image;

import java.util.Arrays;

public class ComponentSampleModel extends SampleModel {
  protected int[] bandOffsets;
  
  protected int[] bankIndices;
  
  protected int numBands = 1;
  
  protected int numBanks = 1;
  
  protected int scanlineStride;
  
  protected int pixelStride;
  
  private static native void initIDs();
  
  public ComponentSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt) {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfInt.length);
    this.dataType = paramInt1;
    this.pixelStride = paramInt4;
    this.scanlineStride = paramInt5;
    this.bandOffsets = (int[])paramArrayOfInt.clone();
    this.numBands = this.bandOffsets.length;
    if (paramInt4 < 0)
      throw new IllegalArgumentException("Pixel stride must be >= 0"); 
    if (paramInt5 < 0)
      throw new IllegalArgumentException("Scanline stride must be >= 0"); 
    if (this.numBands < 1)
      throw new IllegalArgumentException("Must have at least one band."); 
    if (paramInt1 < 0 || paramInt1 > 5)
      throw new IllegalArgumentException("Unsupported dataType."); 
    this.bankIndices = new int[this.numBands];
    for (byte b = 0; b < this.numBands; b++)
      this.bankIndices[b] = 0; 
    verify();
  }
  
  public ComponentSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfInt2.length);
    this.dataType = paramInt1;
    this.pixelStride = paramInt4;
    this.scanlineStride = paramInt5;
    this.bandOffsets = (int[])paramArrayOfInt2.clone();
    this.bankIndices = (int[])paramArrayOfInt1.clone();
    if (paramInt4 < 0)
      throw new IllegalArgumentException("Pixel stride must be >= 0"); 
    if (paramInt5 < 0)
      throw new IllegalArgumentException("Scanline stride must be >= 0"); 
    if (paramInt1 < 0 || paramInt1 > 5)
      throw new IllegalArgumentException("Unsupported dataType."); 
    int i = this.bankIndices[0];
    if (i < 0)
      throw new IllegalArgumentException("Index of bank 0 is less than 0 (" + i + ")"); 
    for (byte b = 1; b < this.bankIndices.length; b++) {
      if (this.bankIndices[b] > i) {
        i = this.bankIndices[b];
      } else if (this.bankIndices[b] < 0) {
        throw new IllegalArgumentException("Index of bank " + b + " is less than 0 (" + i + ")");
      } 
    } 
    this.numBanks = i + 1;
    this.numBands = this.bandOffsets.length;
    if (this.bandOffsets.length != this.bankIndices.length)
      throw new IllegalArgumentException("Length of bandOffsets must equal length of bankIndices."); 
    verify();
  }
  
  private void verify() { int i = getBufferSize(); }
  
  private int getBufferSize() {
    int i = this.bandOffsets[0];
    for (null = 1; null < this.bandOffsets.length; null++)
      i = Math.max(i, this.bandOffsets[null]); 
    if (i < 0 || i > 2147483646)
      throw new IllegalArgumentException("Invalid band offset"); 
    if (this.pixelStride < 0 || this.pixelStride > Integer.MAX_VALUE / this.width)
      throw new IllegalArgumentException("Invalid pixel stride"); 
    if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height)
      throw new IllegalArgumentException("Invalid scanline stride"); 
    null = i + 1;
    int j = this.pixelStride * (this.width - 1);
    if (j > Integer.MAX_VALUE - null)
      throw new IllegalArgumentException("Invalid pixel stride"); 
    null += j;
    j = this.scanlineStride * (this.height - 1);
    if (j > Integer.MAX_VALUE - null)
      throw new IllegalArgumentException("Invalid scan stride"); 
    return j;
  }
  
  int[] orderBands(int[] paramArrayOfInt, int paramInt) {
    int[] arrayOfInt1 = new int[paramArrayOfInt.length];
    int[] arrayOfInt2 = new int[paramArrayOfInt.length];
    int i;
    for (i = 0; i < arrayOfInt1.length; i++)
      arrayOfInt1[i] = i; 
    for (i = 0; i < arrayOfInt2.length; i++) {
      byte b1 = i;
      for (byte b2 = i + 1; b2 < arrayOfInt2.length; b2++) {
        if (paramArrayOfInt[arrayOfInt1[b1]] > paramArrayOfInt[arrayOfInt1[b2]])
          b1 = b2; 
      } 
      arrayOfInt2[arrayOfInt1[b1]] = i * paramInt;
      arrayOfInt1[b1] = arrayOfInt1[i];
    } 
    return arrayOfInt2;
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    int[] arrayOfInt;
    Object object = null;
    int i = this.bandOffsets[0];
    int j = this.bandOffsets[0];
    int k;
    for (k = 1; k < this.bandOffsets.length; k++) {
      i = Math.min(i, this.bandOffsets[k]);
      j = Math.max(j, this.bandOffsets[k]);
    } 
    j -= i;
    k = this.bandOffsets.length;
    int m = Math.abs(this.pixelStride);
    int n = Math.abs(this.scanlineStride);
    int i1 = Math.abs(j);
    if (m > n) {
      if (m > i1) {
        if (n > i1) {
          arrayOfInt = new int[this.bandOffsets.length];
          for (byte b1 = 0; b1 < k; b1++)
            arrayOfInt[b1] = this.bandOffsets[b1] - i; 
          n = i1 + 1;
          m = n * paramInt2;
        } else {
          arrayOfInt = orderBands(this.bandOffsets, n * paramInt2);
          m = k * n * paramInt2;
        } 
      } else {
        m = n * paramInt2;
        arrayOfInt = orderBands(this.bandOffsets, m * paramInt1);
      } 
    } else if (m > i1) {
      arrayOfInt = new int[this.bandOffsets.length];
      for (byte b1 = 0; b1 < k; b1++)
        arrayOfInt[b1] = this.bandOffsets[b1] - i; 
      m = i1 + 1;
      n = m * paramInt1;
    } else if (n > i1) {
      arrayOfInt = orderBands(this.bandOffsets, m * paramInt1);
      n = k * m * paramInt1;
    } else {
      n = m * paramInt1;
      arrayOfInt = orderBands(this.bandOffsets, n * paramInt2);
    } 
    int i2 = 0;
    if (this.scanlineStride < 0) {
      i2 += n * paramInt2;
      n *= -1;
    } 
    if (this.pixelStride < 0) {
      i2 += m * paramInt1;
      m *= -1;
    } 
    for (byte b = 0; b < k; b++)
      arrayOfInt[b] = arrayOfInt[b] + i2; 
    return new ComponentSampleModel(this.dataType, paramInt1, paramInt2, m, n, this.bankIndices, arrayOfInt);
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
    return new ComponentSampleModel(this.dataType, this.width, this.height, this.pixelStride, this.scanlineStride, arrayOfInt1, arrayOfInt2);
  }
  
  public DataBuffer createDataBuffer() {
    DataBufferFloat dataBufferFloat;
    DataBufferDouble dataBufferDouble;
    DataBufferUShort dataBufferUShort;
    DataBufferInt dataBufferInt;
    DataBufferShort dataBufferShort;
    DataBufferByte dataBufferByte = null;
    int i = getBufferSize();
    switch (this.dataType) {
      case 0:
        dataBufferByte = new DataBufferByte(i, this.numBanks);
        break;
      case 1:
        dataBufferUShort = new DataBufferUShort(i, this.numBanks);
        break;
      case 2:
        dataBufferShort = new DataBufferShort(i, this.numBanks);
        break;
      case 3:
        dataBufferInt = new DataBufferInt(i, this.numBanks);
        break;
      case 4:
        dataBufferFloat = new DataBufferFloat(i, this.numBanks);
        break;
      case 5:
        dataBufferDouble = new DataBufferDouble(i, this.numBanks);
        break;
    } 
    return dataBufferDouble;
  }
  
  public int getOffset(int paramInt1, int paramInt2) { return paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[0]; }
  
  public int getOffset(int paramInt1, int paramInt2, int paramInt3) { return paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3]; }
  
  public final int[] getSampleSize() {
    int[] arrayOfInt = new int[this.numBands];
    int i = getSampleSize(0);
    for (byte b = 0; b < this.numBands; b++)
      arrayOfInt[b] = i; 
    return arrayOfInt;
  }
  
  public final int getSampleSize(int paramInt) { return DataBuffer.getDataTypeSize(this.dataType); }
  
  public final int[] getBankIndices() { return (int[])this.bankIndices.clone(); }
  
  public final int[] getBandOffsets() { return (int[])this.bandOffsets.clone(); }
  
  public final int getScanlineStride() { return this.scanlineStride; }
  
  public final int getPixelStride() { return this.pixelStride; }
  
  public final int getNumDataElements() { return getNumBands(); }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte b5;
    double[] arrayOfDouble2;
    byte b4;
    float[] arrayOfFloat2;
    byte b3;
    int[] arrayOfInt2;
    byte b2;
    short[] arrayOfShort2;
    byte b1;
    byte[] arrayOfByte2;
    int[] arrayOfInt1;
    short[] arrayOfShort1;
    float[] arrayOfFloat1;
    double[] arrayOfDouble1;
    byte[] arrayOfByte1;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
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
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    for (byte b = 0; b < this.numBands; b++)
      arrayOfInt[b] = paramDataBuffer.getElem(this.bankIndices[b], i + this.bandOffsets[b]); 
    return arrayOfInt;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt2 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4 * this.numBands];
    } 
    int k = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int m = k;
      for (byte b = 0; b < paramInt3; b++) {
        for (byte b3 = 0; b3 < this.numBands; b3++)
          arrayOfInt[b1++] = paramDataBuffer.getElem(this.bankIndices[b3], m + this.bandOffsets[b3]); 
        m += this.pixelStride;
      } 
      k += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    return paramDataBuffer.getElem(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3]);
  }
  
  public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    return paramDataBuffer.getElemFloat(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3]);
  }
  
  public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    return paramDataBuffer.getElemDouble(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3]);
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
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt5];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int j = i;
      for (byte b = 0; b < paramInt3; b++) {
        arrayOfInt[b1++] = paramDataBuffer.getElem(this.bankIndices[paramInt5], j);
        j += this.pixelStride;
      } 
      i += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte b5;
    double[] arrayOfDouble;
    byte b4;
    byte b3;
    float[] arrayOfFloat;
    int[] arrayOfInt;
    byte b2;
    short[] arrayOfShort;
    byte b1;
    byte[] arrayOfByte;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
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
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    for (byte b = 0; b < this.numBands; b++)
      paramDataBuffer.setElem(this.bankIndices[b], i + this.bandOffsets[b], paramArrayOfInt[b]); 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int k = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int m = k;
      for (byte b = 0; b < paramInt3; b++) {
        for (byte b3 = 0; b3 < this.numBands; b3++)
          paramDataBuffer.setElem(this.bankIndices[b3], m + this.bandOffsets[b3], paramArrayOfInt[b1++]); 
        m += this.pixelStride;
      } 
      k += this.scanlineStride;
    } 
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    paramDataBuffer.setElem(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3], paramInt4);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    paramDataBuffer.setElemFloat(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3], paramFloat);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    paramDataBuffer.setElemDouble(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3], paramDouble);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt3 > this.width || paramInt2 + paramInt4 > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt5];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int j = i;
      for (byte b = 0; b < paramInt3; b++) {
        paramDataBuffer.setElem(this.bankIndices[paramInt5], j, paramArrayOfInt[b1++]);
        j += this.pixelStride;
      } 
      i += this.scanlineStride;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof ComponentSampleModel))
      return false; 
    ComponentSampleModel componentSampleModel = (ComponentSampleModel)paramObject;
    return (this.width == componentSampleModel.width && this.height == componentSampleModel.height && this.numBands == componentSampleModel.numBands && this.dataType == componentSampleModel.dataType && Arrays.equals(this.bandOffsets, componentSampleModel.bandOffsets) && Arrays.equals(this.bankIndices, componentSampleModel.bankIndices) && this.numBands == componentSampleModel.numBands && this.numBanks == componentSampleModel.numBanks && this.scanlineStride == componentSampleModel.scanlineStride && this.pixelStride == componentSampleModel.pixelStride);
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
    for (b = 0; b < this.bandOffsets.length; b++) {
      null ^= this.bandOffsets[b];
      null <<= 8;
    } 
    for (b = 0; b < this.bankIndices.length; b++) {
      null ^= this.bankIndices[b];
      null <<= 8;
    } 
    null ^= this.numBands;
    null <<= 8;
    null ^= this.numBanks;
    null <<= 8;
    null ^= this.scanlineStride;
    null <<= 8;
    return this.pixelStride;
  }
  
  static  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ComponentSampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */