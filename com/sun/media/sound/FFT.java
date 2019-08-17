package com.sun.media.sound;

public final class FFT {
  private final double[] w;
  
  private final int fftFrameSize;
  
  private final int sign;
  
  private final int[] bitm_array;
  
  private final int fftFrameSize2;
  
  public FFT(int paramInt1, int paramInt2) {
    this.w = computeTwiddleFactors(paramInt1, paramInt2);
    this.fftFrameSize = paramInt1;
    this.sign = paramInt2;
    this.fftFrameSize2 = paramInt1 << 1;
    this.bitm_array = new int[this.fftFrameSize2];
    for (byte b = 2; b < this.fftFrameSize2; b += 2) {
      byte b2 = 2;
      byte b1 = 0;
      while (b2 < this.fftFrameSize2) {
        if ((b & b2) != 0)
          b1++; 
        b1 <<= 1;
        b2 <<= 1;
      } 
      this.bitm_array[b] = b1;
    } 
  }
  
  public void transform(double[] paramArrayOfDouble) {
    bitreversal(paramArrayOfDouble);
    calc(this.fftFrameSize, paramArrayOfDouble, this.sign, this.w);
  }
  
  private static final double[] computeTwiddleFactors(int paramInt1, int paramInt2) {
    int i = (int)(Math.log(paramInt1) / Math.log(2.0D));
    double[] arrayOfDouble = new double[(paramInt1 - 1) * 4];
    byte b1 = 0;
    int j = 0;
    byte b2 = 2;
    while (j < i) {
      byte b = b2;
      b2 <<= 1;
      double d1 = 1.0D;
      double d2 = 0.0D;
      double d3 = Math.PI / (b >> 1);
      double d4 = Math.cos(d3);
      double d5 = paramInt2 * Math.sin(d3);
      for (boolean bool = false; bool < b; bool += true) {
        arrayOfDouble[b1++] = d1;
        arrayOfDouble[b1++] = d2;
        double d = d1;
        d1 = d * d4 - d2 * d5;
        d2 = d * d5 + d2 * d4;
      } 
      j++;
    } 
    b1 = 0;
    j = arrayOfDouble.length >> 1;
    b2 = 0;
    byte b3 = 2;
    while (b2 < i - 1) {
      byte b4 = b3;
      b3 *= 2;
      byte b5 = b1 + b4;
      for (boolean bool = false; bool < b4; bool += true) {
        double d1 = arrayOfDouble[b1++];
        double d2 = arrayOfDouble[b1++];
        double d3 = arrayOfDouble[b5++];
        double d4 = arrayOfDouble[b5++];
        arrayOfDouble[j++] = d1 * d3 - d2 * d4;
        arrayOfDouble[j++] = d1 * d4 + d2 * d3;
      } 
      b2++;
    } 
    return arrayOfDouble;
  }
  
  private static final void calc(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2) {
    int i = paramInt1 << 1;
    byte b1 = 2;
    if (b1 >= i)
      return; 
    byte b2 = b1 - 2;
    if (paramInt2 == -1) {
      calcF4F(paramInt1, paramArrayOfDouble1, b2, b1, paramArrayOfDouble2);
    } else {
      calcF4I(paramInt1, paramArrayOfDouble1, b2, b1, paramArrayOfDouble2);
    } 
  }
  
  private static final void calcF2E(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2) {
    int i = paramInt3;
    for (int j = 0; j < i; j += 2) {
      double d1 = paramArrayOfDouble2[paramInt2++];
      double d2 = paramArrayOfDouble2[paramInt2++];
      int k = j + i;
      double d3 = paramArrayOfDouble1[k];
      double d4 = paramArrayOfDouble1[k + 1];
      double d5 = paramArrayOfDouble1[j];
      double d6 = paramArrayOfDouble1[j + 1];
      double d7 = d3 * d1 - d4 * d2;
      double d8 = d3 * d2 + d4 * d1;
      paramArrayOfDouble1[k] = d5 - d7;
      paramArrayOfDouble1[k + 1] = d6 - d8;
      paramArrayOfDouble1[j] = d5 + d7;
      paramArrayOfDouble1[j + 1] = d6 + d8;
    } 
  }
  
  private static final void calcF4F(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2) {
    int i = paramInt1 << 1;
    int j = paramArrayOfDouble2.length >> 1;
    while (paramInt3 < i) {
      if (paramInt3 << 2 == i) {
        calcF4FE(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      } 
      int k = paramInt3;
      int m = paramInt3 << 1;
      if (m == i) {
        calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      } 
      paramInt3 <<= 2;
      int n = paramInt2 + k;
      int i1 = paramInt2 + j;
      paramInt2 += 2;
      n += 2;
      i1 += 2;
      int i2;
      for (i2 = 0; i2 < i; i2 += paramInt3) {
        int i3 = i2 + k;
        double d1 = paramArrayOfDouble1[i3];
        double d2 = paramArrayOfDouble1[i3 + 1];
        double d3 = paramArrayOfDouble1[i2];
        double d4 = paramArrayOfDouble1[i2 + 1];
        i2 += m;
        i3 += m;
        double d5 = paramArrayOfDouble1[i3];
        double d6 = paramArrayOfDouble1[i3 + 1];
        double d7 = paramArrayOfDouble1[i2];
        double d8 = paramArrayOfDouble1[i2 + 1];
        double d9 = d1;
        double d10 = d2;
        d1 = d3 - d9;
        d2 = d4 - d10;
        d3 += d9;
        d4 += d10;
        double d11 = d7;
        double d12 = d8;
        double d13 = d5;
        double d14 = d6;
        d9 = d13 - d11;
        d10 = d14 - d12;
        d5 = d1 + d10;
        d6 = d2 - d9;
        d1 -= d10;
        d2 += d9;
        d9 = d11 + d13;
        d10 = d12 + d14;
        d7 = d3 - d9;
        d8 = d4 - d10;
        d3 += d9;
        d4 += d10;
        paramArrayOfDouble1[i3] = d5;
        paramArrayOfDouble1[i3 + 1] = d6;
        paramArrayOfDouble1[i2] = d7;
        paramArrayOfDouble1[i2 + 1] = d8;
        i2 -= m;
        i3 -= m;
        paramArrayOfDouble1[i3] = d1;
        paramArrayOfDouble1[i3 + 1] = d2;
        paramArrayOfDouble1[i2] = d3;
        paramArrayOfDouble1[i2 + 1] = d4;
      } 
      for (i2 = 2; i2 < k; i2 += 2) {
        double d1 = paramArrayOfDouble2[paramInt2++];
        double d2 = paramArrayOfDouble2[paramInt2++];
        double d3 = paramArrayOfDouble2[n++];
        double d4 = paramArrayOfDouble2[n++];
        double d5 = paramArrayOfDouble2[i1++];
        double d6 = paramArrayOfDouble2[i1++];
        int i3;
        for (i3 = i2; i3 < i; i3 += paramInt3) {
          int i4 = i3 + k;
          double d7 = paramArrayOfDouble1[i4];
          double d8 = paramArrayOfDouble1[i4 + 1];
          double d9 = paramArrayOfDouble1[i3];
          double d10 = paramArrayOfDouble1[i3 + 1];
          i3 += m;
          i4 += m;
          double d11 = paramArrayOfDouble1[i4];
          double d12 = paramArrayOfDouble1[i4 + 1];
          double d13 = paramArrayOfDouble1[i3];
          double d14 = paramArrayOfDouble1[i3 + 1];
          double d15 = d7 * d1 - d8 * d2;
          double d16 = d7 * d2 + d8 * d1;
          d7 = d9 - d15;
          d8 = d10 - d16;
          d9 += d15;
          d10 += d16;
          double d17 = d13 * d3 - d14 * d4;
          double d18 = d13 * d4 + d14 * d3;
          double d19 = d11 * d5 - d12 * d6;
          double d20 = d11 * d6 + d12 * d5;
          d15 = d19 - d17;
          d16 = d20 - d18;
          d11 = d7 + d16;
          d12 = d8 - d15;
          d7 -= d16;
          d8 += d15;
          d15 = d17 + d19;
          d16 = d18 + d20;
          d13 = d9 - d15;
          d14 = d10 - d16;
          d9 += d15;
          d10 += d16;
          paramArrayOfDouble1[i4] = d11;
          paramArrayOfDouble1[i4 + 1] = d12;
          paramArrayOfDouble1[i3] = d13;
          paramArrayOfDouble1[i3 + 1] = d14;
          i3 -= m;
          i4 -= m;
          paramArrayOfDouble1[i4] = d7;
          paramArrayOfDouble1[i4 + 1] = d8;
          paramArrayOfDouble1[i3] = d9;
          paramArrayOfDouble1[i3 + 1] = d10;
        } 
      } 
      paramInt2 += (k << 1);
    } 
    calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
  }
  
  private static final void calcF4I(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2) {
    int i = paramInt1 << 1;
    int j = paramArrayOfDouble2.length >> 1;
    while (paramInt3 < i) {
      if (paramInt3 << 2 == i) {
        calcF4IE(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      } 
      int k = paramInt3;
      int m = paramInt3 << 1;
      if (m == i) {
        calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      } 
      paramInt3 <<= 2;
      int n = paramInt2 + k;
      int i1 = paramInt2 + j;
      paramInt2 += 2;
      n += 2;
      i1 += 2;
      int i2;
      for (i2 = 0; i2 < i; i2 += paramInt3) {
        int i3 = i2 + k;
        double d1 = paramArrayOfDouble1[i3];
        double d2 = paramArrayOfDouble1[i3 + 1];
        double d3 = paramArrayOfDouble1[i2];
        double d4 = paramArrayOfDouble1[i2 + 1];
        i2 += m;
        i3 += m;
        double d5 = paramArrayOfDouble1[i3];
        double d6 = paramArrayOfDouble1[i3 + 1];
        double d7 = paramArrayOfDouble1[i2];
        double d8 = paramArrayOfDouble1[i2 + 1];
        double d9 = d1;
        double d10 = d2;
        d1 = d3 - d9;
        d2 = d4 - d10;
        d3 += d9;
        d4 += d10;
        double d11 = d7;
        double d12 = d8;
        double d13 = d5;
        double d14 = d6;
        d9 = d11 - d13;
        d10 = d12 - d14;
        d5 = d1 + d10;
        d6 = d2 - d9;
        d1 -= d10;
        d2 += d9;
        d9 = d11 + d13;
        d10 = d12 + d14;
        d7 = d3 - d9;
        d8 = d4 - d10;
        d3 += d9;
        d4 += d10;
        paramArrayOfDouble1[i3] = d5;
        paramArrayOfDouble1[i3 + 1] = d6;
        paramArrayOfDouble1[i2] = d7;
        paramArrayOfDouble1[i2 + 1] = d8;
        i2 -= m;
        i3 -= m;
        paramArrayOfDouble1[i3] = d1;
        paramArrayOfDouble1[i3 + 1] = d2;
        paramArrayOfDouble1[i2] = d3;
        paramArrayOfDouble1[i2 + 1] = d4;
      } 
      for (i2 = 2; i2 < k; i2 += 2) {
        double d1 = paramArrayOfDouble2[paramInt2++];
        double d2 = paramArrayOfDouble2[paramInt2++];
        double d3 = paramArrayOfDouble2[n++];
        double d4 = paramArrayOfDouble2[n++];
        double d5 = paramArrayOfDouble2[i1++];
        double d6 = paramArrayOfDouble2[i1++];
        int i3;
        for (i3 = i2; i3 < i; i3 += paramInt3) {
          int i4 = i3 + k;
          double d7 = paramArrayOfDouble1[i4];
          double d8 = paramArrayOfDouble1[i4 + 1];
          double d9 = paramArrayOfDouble1[i3];
          double d10 = paramArrayOfDouble1[i3 + 1];
          i3 += m;
          i4 += m;
          double d11 = paramArrayOfDouble1[i4];
          double d12 = paramArrayOfDouble1[i4 + 1];
          double d13 = paramArrayOfDouble1[i3];
          double d14 = paramArrayOfDouble1[i3 + 1];
          double d15 = d7 * d1 - d8 * d2;
          double d16 = d7 * d2 + d8 * d1;
          d7 = d9 - d15;
          d8 = d10 - d16;
          d9 += d15;
          d10 += d16;
          double d17 = d13 * d3 - d14 * d4;
          double d18 = d13 * d4 + d14 * d3;
          double d19 = d11 * d5 - d12 * d6;
          double d20 = d11 * d6 + d12 * d5;
          d15 = d17 - d19;
          d16 = d18 - d20;
          d11 = d7 + d16;
          d12 = d8 - d15;
          d7 -= d16;
          d8 += d15;
          d15 = d17 + d19;
          d16 = d18 + d20;
          d13 = d9 - d15;
          d14 = d10 - d16;
          d9 += d15;
          d10 += d16;
          paramArrayOfDouble1[i4] = d11;
          paramArrayOfDouble1[i4 + 1] = d12;
          paramArrayOfDouble1[i3] = d13;
          paramArrayOfDouble1[i3 + 1] = d14;
          i3 -= m;
          i4 -= m;
          paramArrayOfDouble1[i4] = d7;
          paramArrayOfDouble1[i4 + 1] = d8;
          paramArrayOfDouble1[i3] = d9;
          paramArrayOfDouble1[i3 + 1] = d10;
        } 
      } 
      paramInt2 += (k << 1);
    } 
    calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
  }
  
  private static final void calcF4FE(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2) {
    int i = paramInt1 << 1;
    int j = paramArrayOfDouble2.length >> 1;
    while (paramInt3 < i) {
      int k = paramInt3;
      int m = paramInt3 << 1;
      if (m == i) {
        calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      } 
      paramInt3 <<= 2;
      int n = paramInt2 + k;
      int i1 = paramInt2 + j;
      for (int i2 = 0; i2 < k; i2 += 2) {
        double d1 = paramArrayOfDouble2[paramInt2++];
        double d2 = paramArrayOfDouble2[paramInt2++];
        double d3 = paramArrayOfDouble2[n++];
        double d4 = paramArrayOfDouble2[n++];
        double d5 = paramArrayOfDouble2[i1++];
        double d6 = paramArrayOfDouble2[i1++];
        int i3 = i2 + k;
        double d7 = paramArrayOfDouble1[i3];
        double d8 = paramArrayOfDouble1[i3 + 1];
        double d9 = paramArrayOfDouble1[i2];
        double d10 = paramArrayOfDouble1[i2 + 1];
        i2 += m;
        i3 += m;
        double d11 = paramArrayOfDouble1[i3];
        double d12 = paramArrayOfDouble1[i3 + 1];
        double d13 = paramArrayOfDouble1[i2];
        double d14 = paramArrayOfDouble1[i2 + 1];
        double d15 = d7 * d1 - d8 * d2;
        double d16 = d7 * d2 + d8 * d1;
        d7 = d9 - d15;
        d8 = d10 - d16;
        d9 += d15;
        d10 += d16;
        double d17 = d13 * d3 - d14 * d4;
        double d18 = d13 * d4 + d14 * d3;
        double d19 = d11 * d5 - d12 * d6;
        double d20 = d11 * d6 + d12 * d5;
        d15 = d19 - d17;
        d16 = d20 - d18;
        d11 = d7 + d16;
        d12 = d8 - d15;
        d7 -= d16;
        d8 += d15;
        d15 = d17 + d19;
        d16 = d18 + d20;
        d13 = d9 - d15;
        d14 = d10 - d16;
        d9 += d15;
        d10 += d16;
        paramArrayOfDouble1[i3] = d11;
        paramArrayOfDouble1[i3 + 1] = d12;
        paramArrayOfDouble1[i2] = d13;
        paramArrayOfDouble1[i2 + 1] = d14;
        i2 -= m;
        i3 -= m;
        paramArrayOfDouble1[i3] = d7;
        paramArrayOfDouble1[i3 + 1] = d8;
        paramArrayOfDouble1[i2] = d9;
        paramArrayOfDouble1[i2 + 1] = d10;
      } 
      paramInt2 += (k << 1);
    } 
  }
  
  private static final void calcF4IE(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2) {
    int i = paramInt1 << 1;
    int j = paramArrayOfDouble2.length >> 1;
    while (paramInt3 < i) {
      int k = paramInt3;
      int m = paramInt3 << 1;
      if (m == i) {
        calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      } 
      paramInt3 <<= 2;
      int n = paramInt2 + k;
      int i1 = paramInt2 + j;
      for (int i2 = 0; i2 < k; i2 += 2) {
        double d1 = paramArrayOfDouble2[paramInt2++];
        double d2 = paramArrayOfDouble2[paramInt2++];
        double d3 = paramArrayOfDouble2[n++];
        double d4 = paramArrayOfDouble2[n++];
        double d5 = paramArrayOfDouble2[i1++];
        double d6 = paramArrayOfDouble2[i1++];
        int i3 = i2 + k;
        double d7 = paramArrayOfDouble1[i3];
        double d8 = paramArrayOfDouble1[i3 + 1];
        double d9 = paramArrayOfDouble1[i2];
        double d10 = paramArrayOfDouble1[i2 + 1];
        i2 += m;
        i3 += m;
        double d11 = paramArrayOfDouble1[i3];
        double d12 = paramArrayOfDouble1[i3 + 1];
        double d13 = paramArrayOfDouble1[i2];
        double d14 = paramArrayOfDouble1[i2 + 1];
        double d15 = d7 * d1 - d8 * d2;
        double d16 = d7 * d2 + d8 * d1;
        d7 = d9 - d15;
        d8 = d10 - d16;
        d9 += d15;
        d10 += d16;
        double d17 = d13 * d3 - d14 * d4;
        double d18 = d13 * d4 + d14 * d3;
        double d19 = d11 * d5 - d12 * d6;
        double d20 = d11 * d6 + d12 * d5;
        d15 = d17 - d19;
        d16 = d18 - d20;
        d11 = d7 + d16;
        d12 = d8 - d15;
        d7 -= d16;
        d8 += d15;
        d15 = d17 + d19;
        d16 = d18 + d20;
        d13 = d9 - d15;
        d14 = d10 - d16;
        d9 += d15;
        d10 += d16;
        paramArrayOfDouble1[i3] = d11;
        paramArrayOfDouble1[i3 + 1] = d12;
        paramArrayOfDouble1[i2] = d13;
        paramArrayOfDouble1[i2 + 1] = d14;
        i2 -= m;
        i3 -= m;
        paramArrayOfDouble1[i3] = d7;
        paramArrayOfDouble1[i3 + 1] = d8;
        paramArrayOfDouble1[i2] = d9;
        paramArrayOfDouble1[i2 + 1] = d10;
      } 
      paramInt2 += (k << 1);
    } 
  }
  
  private final void bitreversal(double[] paramArrayOfDouble) {
    if (this.fftFrameSize < 4)
      return; 
    int i = this.fftFrameSize2 - 2;
    for (int j = 0; j < this.fftFrameSize; j += 4) {
      int k = this.bitm_array[j];
      if (j < k) {
        int i1 = j;
        int i2 = k;
        double d3 = paramArrayOfDouble[i1];
        paramArrayOfDouble[i1] = paramArrayOfDouble[i2];
        paramArrayOfDouble[i2] = d3;
        i1++;
        i2++;
        double d4 = paramArrayOfDouble[i1];
        paramArrayOfDouble[i1] = paramArrayOfDouble[i2];
        paramArrayOfDouble[i2] = d4;
        i1 = i - j;
        i2 = i - k;
        d3 = paramArrayOfDouble[i1];
        paramArrayOfDouble[i1] = paramArrayOfDouble[i2];
        paramArrayOfDouble[i2] = d3;
        i1++;
        i2++;
        d4 = paramArrayOfDouble[i1];
        paramArrayOfDouble[i1] = paramArrayOfDouble[i2];
        paramArrayOfDouble[i2] = d4;
      } 
      int m = k + this.fftFrameSize;
      int n = j + 2;
      double d1 = paramArrayOfDouble[n];
      paramArrayOfDouble[n] = paramArrayOfDouble[m];
      paramArrayOfDouble[m] = d1;
      n++;
      m++;
      double d2 = paramArrayOfDouble[n];
      paramArrayOfDouble[n] = paramArrayOfDouble[m];
      paramArrayOfDouble[m] = d2;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\FFT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */