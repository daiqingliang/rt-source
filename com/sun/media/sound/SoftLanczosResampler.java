package com.sun.media.sound;

public final class SoftLanczosResampler extends SoftAbstractResampler {
  float[][] sinc_table = new float[this.sinc_table_fsize][];
  
  int sinc_table_fsize = 2000;
  
  int sinc_table_size = 5;
  
  int sinc_table_center = this.sinc_table_size / 2;
  
  public SoftLanczosResampler() {
    for (byte b = 0; b < this.sinc_table_fsize; b++)
      this.sinc_table[b] = sincTable(this.sinc_table_size, -b / this.sinc_table_fsize); 
  }
  
  public static double sinc(double paramDouble) { return (paramDouble == 0.0D) ? 1.0D : (Math.sin(Math.PI * paramDouble) / Math.PI * paramDouble); }
  
  public static float[] sincTable(int paramInt, float paramFloat) {
    int i = paramInt / 2;
    float[] arrayOfFloat = new float[paramInt];
    for (int j = 0; j < paramInt; j++) {
      float f = (-i + j) + paramFloat;
      if (f < -2.0F || f > 2.0F) {
        arrayOfFloat[j] = 0.0F;
      } else if (f == 0.0F) {
        arrayOfFloat[j] = 1.0F;
      } else {
        arrayOfFloat[j] = (float)(2.0D * Math.sin(Math.PI * f) * Math.sin(Math.PI * f / 2.0D) / Math.PI * f * Math.PI * f);
      } 
    } 
    return arrayOfFloat;
  }
  
  public int getPadding() { return this.sinc_table_size / 2 + 2; }
  
  public void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt) {
    float f1 = paramArrayOfFloat3[0];
    float f2 = paramArrayOfFloat2[0];
    int i = paramArrayOfInt[0];
    float f3 = paramFloat1;
    int j = paramInt;
    if (paramFloat2 == 0.0F) {
      while (f2 < f3 && i < j) {
        int k = (int)f2;
        float[] arrayOfFloat = this.sinc_table[(int)((f2 - k) * this.sinc_table_fsize)];
        int m = k - this.sinc_table_center;
        float f = 0.0F;
        byte b = 0;
        while (b < this.sinc_table_size) {
          f += paramArrayOfFloat1[m] * arrayOfFloat[b];
          b++;
          m++;
        } 
        paramArrayOfFloat4[i++] = f;
        f2 += f1;
      } 
    } else {
      while (f2 < f3 && i < j) {
        int k = (int)f2;
        float[] arrayOfFloat = this.sinc_table[(int)((f2 - k) * this.sinc_table_fsize)];
        int m = k - this.sinc_table_center;
        float f = 0.0F;
        byte b = 0;
        while (b < this.sinc_table_size) {
          f += paramArrayOfFloat1[m] * arrayOfFloat[b];
          b++;
          m++;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftLanczosResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */