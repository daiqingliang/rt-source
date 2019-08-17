package com.sun.media.sound;

public final class SoftSincResampler extends SoftAbstractResampler {
  float[][][] sinc_table = new float[this.sinc_scale_size][this.sinc_table_fsize][];
  
  int sinc_scale_size = 100;
  
  int sinc_table_fsize = 800;
  
  int sinc_table_size = 30;
  
  int sinc_table_center = this.sinc_table_size / 2;
  
  public SoftSincResampler() {
    for (byte b = 0; b < this.sinc_scale_size; b++) {
      float f = (float)(1.0D / (1.0D + Math.pow(b, 1.1D) / 10.0D));
      for (byte b1 = 0; b1 < this.sinc_table_fsize; b1++)
        this.sinc_table[b][b1] = sincTable(this.sinc_table_size, -b1 / this.sinc_table_fsize, f); 
    } 
  }
  
  public static double sinc(double paramDouble) { return (paramDouble == 0.0D) ? 1.0D : (Math.sin(Math.PI * paramDouble) / Math.PI * paramDouble); }
  
  public static float[] wHanning(int paramInt, float paramFloat) {
    float[] arrayOfFloat = new float[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfFloat[b] = (float)(-0.5D * Math.cos(6.283185307179586D * (b + paramFloat) / paramInt) + 0.5D); 
    return arrayOfFloat;
  }
  
  public static float[] sincTable(int paramInt, float paramFloat1, float paramFloat2) {
    int i = paramInt / 2;
    float[] arrayOfFloat = wHanning(paramInt, paramFloat1);
    for (int j = 0; j < paramInt; j++)
      arrayOfFloat[j] = (float)(arrayOfFloat[j] * sinc((((-i + j) + paramFloat1) * paramFloat2)) * paramFloat2); 
    return arrayOfFloat;
  }
  
  public int getPadding() { return this.sinc_table_size / 2 + 2; }
  
  public void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt) {
    float f1 = paramArrayOfFloat3[0];
    float f2 = paramArrayOfFloat2[0];
    int i = paramArrayOfInt[0];
    float f3 = paramFloat1;
    int j = paramInt;
    int k = this.sinc_scale_size - 1;
    if (paramFloat2 == 0.0F) {
      int m = (int)((f1 - 1.0F) * 10.0F);
      if (m < 0) {
        m = 0;
      } else if (m > k) {
        m = k;
      } 
      float[][] arrayOfFloat = this.sinc_table[m];
      while (f2 < f3 && i < j) {
        int n = (int)f2;
        float[] arrayOfFloat1 = arrayOfFloat[(int)((f2 - n) * this.sinc_table_fsize)];
        int i1 = n - this.sinc_table_center;
        float f = 0.0F;
        byte b = 0;
        while (b < this.sinc_table_size) {
          f += paramArrayOfFloat1[i1] * arrayOfFloat1[b];
          b++;
          i1++;
        } 
        paramArrayOfFloat4[i++] = f;
        f2 += f1;
      } 
    } else {
      while (f2 < f3 && i < j) {
        int m = (int)f2;
        int n = (int)((f1 - 1.0F) * 10.0F);
        if (n < 0) {
          n = 0;
        } else if (n > k) {
          n = k;
        } 
        float[][] arrayOfFloat = this.sinc_table[n];
        float[] arrayOfFloat1 = arrayOfFloat[(int)((f2 - m) * this.sinc_table_fsize)];
        int i1 = m - this.sinc_table_center;
        float f = 0.0F;
        byte b = 0;
        while (b < this.sinc_table_size) {
          f += paramArrayOfFloat1[i1] * arrayOfFloat1[b];
          b++;
          i1++;
        } 
        paramArrayOfFloat4[i++] = f;
        f2 += f1;
        f1 += paramFloat2;
      } 
    } 
    paramArrayOfFloat2[0] = f2;
    paramArrayOfInt[0] = i;
    paramArrayOfFloat3[0] = f1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftSincResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */