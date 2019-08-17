package com.sun.media.sound;

public final class SoftLinearResampler2 extends SoftAbstractResampler {
  public int getPadding() { return 2; }
  
  public void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt) {
    float f1 = paramArrayOfFloat3[0];
    float f2 = paramArrayOfFloat2[0];
    int i = paramArrayOfInt[0];
    float f3 = paramFloat1;
    int j = paramInt;
    if (f2 >= f3 || i >= j)
      return; 
    int k = (int)(f2 * 32768.0F);
    int m = (int)(f3 * 32768.0F);
    int n = (int)(f1 * 32768.0F);
    f1 = n * 3.0517578E-5F;
    if (paramFloat2 == 0.0F) {
      int i1 = m - k;
      int i2 = i1 % n;
      if (i2 != 0)
        i1 += n - i2; 
      int i3 = i + i1 / n;
      if (i3 < j)
        j = i3; 
      while (i < j) {
        int i4 = k >> 15;
        float f4 = f2 - i4;
        float f5 = paramArrayOfFloat1[i4];
        paramArrayOfFloat4[i++] = f5 + (paramArrayOfFloat1[i4 + 1] - f5) * f4;
        k += n;
        f2 += f1;
      } 
    } else {
      int i1 = (int)(paramFloat2 * 32768.0F);
      paramFloat2 = i1 * 3.0517578E-5F;
      while (k < m && i < j) {
        int i2 = k >> 15;
        float f4 = f2 - i2;
        float f5 = paramArrayOfFloat1[i2];
        paramArrayOfFloat4[i++] = f5 + (paramArrayOfFloat1[i2 + 1] - f5) * f4;
        f2 += f1;
        k += n;
        f1 += paramFloat2;
        n += i1;
      } 
    } 
    paramArrayOfFloat2[0] = f2;
    paramArrayOfInt[0] = i;
    paramArrayOfFloat3[0] = f1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftLinearResampler2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */