package java.awt.image;

public abstract class SampleModel {
  protected int width;
  
  protected int height;
  
  protected int numBands;
  
  protected int dataType;
  
  private static native void initIDs();
  
  public SampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    long l = paramInt2 * paramInt3;
    if (paramInt2 <= 0 || paramInt3 <= 0)
      throw new IllegalArgumentException("Width (" + paramInt2 + ") and height (" + paramInt3 + ") must be > 0"); 
    if (l >= 2147483647L)
      throw new IllegalArgumentException("Dimensions (width=" + paramInt2 + " height=" + paramInt3 + ") are too large"); 
    if (paramInt1 < 0 || (paramInt1 > 5 && paramInt1 != 32))
      throw new IllegalArgumentException("Unsupported dataType: " + paramInt1); 
    if (paramInt4 <= 0)
      throw new IllegalArgumentException("Number of bands must be > 0"); 
    this.dataType = paramInt1;
    this.width = paramInt2;
    this.height = paramInt3;
    this.numBands = paramInt4;
  }
  
  public final int getWidth() { return this.width; }
  
  public final int getHeight() { return this.height; }
  
  public final int getNumBands() { return this.numBands; }
  
  public abstract int getNumDataElements();
  
  public final int getDataType() { return this.dataType; }
  
  public int getTransferType() { return this.dataType; }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[this.numBands];
    } 
    for (byte b = 0; b < this.numBands; b++)
      arrayOfInt[b] = getSample(paramInt1, paramInt2, b, paramDataBuffer); 
    return arrayOfInt;
  }
  
  public abstract Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer);
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, DataBuffer paramDataBuffer) {
    int i4;
    int i3;
    double[] arrayOfDouble2;
    float[] arrayOfFloat2;
    int i2;
    int[] arrayOfInt2;
    int i1;
    int n;
    short[] arrayOfShort2;
    byte[] arrayOfByte2;
    double[] arrayOfDouble1;
    int[] arrayOfInt1;
    float[] arrayOfFloat1;
    byte[] arrayOfByte1;
    short[] arrayOfShort1;
    int i = getTransferType();
    int j = getNumDataElements();
    byte b = 0;
    Object object = null;
    int k = paramInt1 + paramInt3;
    int m = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || k < 0 || k > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || m < 0 || m > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    switch (i) {
      case 0:
        if (paramObject == null) {
          arrayOfByte2 = new byte[j * paramInt3 * paramInt4];
        } else {
          arrayOfByte2 = (byte[])paramObject;
        } 
        for (n = paramInt2; n < m; n++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, n, object, paramDataBuffer);
            byte[] arrayOfByte = (byte[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfByte2[b++] = arrayOfByte[b1]; 
          } 
        } 
        arrayOfByte1 = arrayOfByte2;
        break;
      case 1:
      case 2:
        if (arrayOfByte1 == null) {
          arrayOfShort2 = new short[j * paramInt3 * paramInt4];
        } else {
          arrayOfShort2 = (short[])arrayOfByte1;
        } 
        for (i1 = paramInt2; i1 < m; i1++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, i1, object, paramDataBuffer);
            short[] arrayOfShort = (short[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfShort2[b++] = arrayOfShort[b1]; 
          } 
        } 
        arrayOfShort1 = arrayOfShort2;
        break;
      case 3:
        if (arrayOfShort1 == null) {
          arrayOfInt2 = new int[j * paramInt3 * paramInt4];
        } else {
          arrayOfInt2 = (int[])arrayOfShort1;
        } 
        for (i2 = paramInt2; i2 < m; i2++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, i2, object, paramDataBuffer);
            int[] arrayOfInt = (int[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfInt2[b++] = arrayOfInt[b1]; 
          } 
        } 
        arrayOfInt1 = arrayOfInt2;
        break;
      case 4:
        if (arrayOfInt1 == null) {
          arrayOfFloat2 = new float[j * paramInt3 * paramInt4];
        } else {
          arrayOfFloat2 = (float[])arrayOfInt1;
        } 
        for (i3 = paramInt2; i3 < m; i3++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, i3, object, paramDataBuffer);
            float[] arrayOfFloat = (float[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfFloat2[b++] = arrayOfFloat[b1]; 
          } 
        } 
        arrayOfFloat1 = arrayOfFloat2;
        break;
      case 5:
        if (arrayOfFloat1 == null) {
          arrayOfDouble2 = new double[j * paramInt3 * paramInt4];
        } else {
          arrayOfDouble2 = (double[])arrayOfFloat1;
        } 
        for (i4 = paramInt2; i4 < m; i4++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, i4, object, paramDataBuffer);
            double[] arrayOfDouble = (double[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfDouble2[b++] = arrayOfDouble[b1]; 
          } 
        } 
        arrayOfDouble1 = arrayOfDouble2;
        break;
    } 
    return arrayOfDouble1;
  }
  
  public abstract void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer);
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, DataBuffer paramDataBuffer) {
    int i4;
    double[] arrayOfDouble2;
    int i3;
    double[] arrayOfDouble1;
    float[] arrayOfFloat2;
    float[] arrayOfFloat1;
    int i2;
    int[] arrayOfInt2;
    int[] arrayOfInt1;
    int i1;
    short[] arrayOfShort2;
    int n;
    short[] arrayOfShort1;
    byte[] arrayOfByte2;
    byte[] arrayOfByte1;
    byte b = 0;
    Object object = null;
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt1 + paramInt3;
    int m = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || k < 0 || k > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || m < 0 || m > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    switch (i) {
      case 0:
        arrayOfByte1 = (byte[])paramObject;
        arrayOfByte2 = new byte[j];
        for (n = paramInt2; n < m; n++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfByte2[b1] = arrayOfByte1[b++]; 
            setDataElements(i5, n, arrayOfByte2, paramDataBuffer);
          } 
        } 
        break;
      case 1:
      case 2:
        arrayOfShort1 = (short[])paramObject;
        arrayOfShort2 = new short[j];
        for (i1 = paramInt2; i1 < m; i1++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfShort2[b1] = arrayOfShort1[b++]; 
            setDataElements(i5, i1, arrayOfShort2, paramDataBuffer);
          } 
        } 
        break;
      case 3:
        arrayOfInt1 = (int[])paramObject;
        arrayOfInt2 = new int[j];
        for (i2 = paramInt2; i2 < m; i2++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfInt2[b1] = arrayOfInt1[b++]; 
            setDataElements(i5, i2, arrayOfInt2, paramDataBuffer);
          } 
        } 
        break;
      case 4:
        arrayOfFloat1 = (float[])paramObject;
        arrayOfFloat2 = new float[j];
        for (i3 = paramInt2; i3 < m; i3++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfFloat2[b1] = arrayOfFloat1[b++]; 
            setDataElements(i5, i3, arrayOfFloat2, paramDataBuffer);
          } 
        } 
        break;
      case 5:
        arrayOfDouble1 = (double[])paramObject;
        arrayOfDouble2 = new double[j];
        for (i4 = paramInt2; i4 < m; i4++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfDouble2[b1] = arrayOfDouble1[b++]; 
            setDataElements(i5, i4, arrayOfDouble2, paramDataBuffer);
          } 
        } 
        break;
    } 
  }
  
  public float[] getPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat, DataBuffer paramDataBuffer) {
    float[] arrayOfFloat;
    if (paramArrayOfFloat != null) {
      arrayOfFloat = paramArrayOfFloat;
    } else {
      arrayOfFloat = new float[this.numBands];
    } 
    for (byte b = 0; b < this.numBands; b++)
      arrayOfFloat[b] = getSampleFloat(paramInt1, paramInt2, b, paramDataBuffer); 
    return arrayOfFloat;
  }
  
  public double[] getPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble, DataBuffer paramDataBuffer) {
    double[] arrayOfDouble;
    if (paramArrayOfDouble != null) {
      arrayOfDouble = paramArrayOfDouble;
    } else {
      arrayOfDouble = new double[this.numBands];
    } 
    for (byte b = 0; b < this.numBands; b++)
      arrayOfDouble[b] = getSampleDouble(paramInt1, paramInt2, b, paramDataBuffer); 
    return arrayOfDouble;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[this.numBands * paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          arrayOfInt[b++] = getSample(m, k, b1, paramDataBuffer); 
      } 
    } 
    return arrayOfInt;
  }
  
  public float[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat, DataBuffer paramDataBuffer) {
    float[] arrayOfFloat;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    if (paramArrayOfFloat != null) {
      arrayOfFloat = paramArrayOfFloat;
    } else {
      arrayOfFloat = new float[this.numBands * paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          arrayOfFloat[b++] = getSampleFloat(m, k, b1, paramDataBuffer); 
      } 
    } 
    return arrayOfFloat;
  }
  
  public double[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble, DataBuffer paramDataBuffer) {
    double[] arrayOfDouble;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    if (paramArrayOfDouble != null) {
      arrayOfDouble = paramArrayOfDouble;
    } else {
      arrayOfDouble = new double[this.numBands * paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          arrayOfDouble[b++] = getSampleDouble(m, k, b1, paramDataBuffer); 
      } 
    } 
    return arrayOfDouble;
  }
  
  public abstract int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer);
  
  public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) { return getSample(paramInt1, paramInt2, paramInt3, paramDataBuffer); }
  
  public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) { return getSample(paramInt1, paramInt2, paramInt3, paramDataBuffer); }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || i < paramInt1 || i > this.width || paramInt2 < 0 || j < paramInt2 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        arrayOfInt[b++] = getSample(m, k, paramInt5, paramDataBuffer); 
    } 
    return arrayOfInt;
  }
  
  public float[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOfFloat, DataBuffer paramDataBuffer) {
    float[] arrayOfFloat;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || i < paramInt1 || i > this.width || paramInt2 < 0 || j < paramInt2 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates"); 
    if (paramArrayOfFloat != null) {
      arrayOfFloat = paramArrayOfFloat;
    } else {
      arrayOfFloat = new float[paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        arrayOfFloat[b++] = getSampleFloat(m, k, paramInt5, paramDataBuffer); 
    } 
    return arrayOfFloat;
  }
  
  public double[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfDouble, DataBuffer paramDataBuffer) {
    double[] arrayOfDouble;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || i < paramInt1 || i > this.width || paramInt2 < 0 || j < paramInt2 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates"); 
    if (paramArrayOfDouble != null) {
      arrayOfDouble = paramArrayOfDouble;
    } else {
      arrayOfDouble = new double[paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        arrayOfDouble[b++] = getSampleDouble(m, k, paramInt5, paramDataBuffer); 
    } 
    return arrayOfDouble;
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    for (byte b = 0; b < this.numBands; b++)
      setSample(paramInt1, paramInt2, b, paramArrayOfInt[b], paramDataBuffer); 
  }
  
  public void setPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat, DataBuffer paramDataBuffer) {
    for (byte b = 0; b < this.numBands; b++)
      setSample(paramInt1, paramInt2, b, paramArrayOfFloat[b], paramDataBuffer); 
  }
  
  public void setPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble, DataBuffer paramDataBuffer) {
    for (byte b = 0; b < this.numBands; b++)
      setSample(paramInt1, paramInt2, b, paramArrayOfDouble[b], paramDataBuffer); 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          setSample(m, k, b1, paramArrayOfInt[b++], paramDataBuffer); 
      } 
    } 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          setSample(m, k, b1, paramArrayOfFloat[b++], paramDataBuffer); 
      } 
    } 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          setSample(m, k, b1, paramArrayOfDouble[b++], paramDataBuffer); 
      } 
    } 
  }
  
  public abstract void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer);
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat, DataBuffer paramDataBuffer) {
    int i = (int)paramFloat;
    setSample(paramInt1, paramInt2, paramInt3, i, paramDataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble, DataBuffer paramDataBuffer) {
    int i = (int)paramDouble;
    setSample(paramInt1, paramInt2, paramInt3, i, paramDataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        setSample(m, k, paramInt5, paramArrayOfInt[b++], paramDataBuffer); 
    } 
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOfFloat, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        setSample(m, k, paramInt5, paramArrayOfFloat[b++], paramDataBuffer); 
    } 
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfDouble, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        setSample(m, k, paramInt5, paramArrayOfDouble[b++], paramDataBuffer); 
    } 
  }
  
  public abstract SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2);
  
  public abstract SampleModel createSubsetSampleModel(int[] paramArrayOfInt);
  
  public abstract DataBuffer createDataBuffer();
  
  public abstract int[] getSampleSize();
  
  public abstract int getSampleSize(int paramInt);
  
  static  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\SampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */