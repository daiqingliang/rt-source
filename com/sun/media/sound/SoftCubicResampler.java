package com.sun.media.sound;

public final class SoftCubicResampler extends SoftAbstractResampler {
  public int getPadding() { return 3; }
  
  public void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt) {
    float f1 = paramArrayOfFloat3[0];
    float f2 = paramArrayOfFloat2[0];
    int i = paramArrayOfInt[0];
    float f3 = paramFloat1;
    int j = paramInt;
    if (paramFloat2 == 0.0F) {
      while (f2 < f3 && i < j) {
        int k = (int)f2;
        float f4 = f2 - k;
        float f5 = paramArrayOfFloat1[k - 1];
        float f6 = paramArrayOfFloat1[k];
        float f7 = paramArrayOfFloat1[k + 1];
        float f8 = paramArrayOfFloat1[k + 2];
        float f9 = f8 - f7 + f6 - f5;
        float f10 = f5 - f6 - f9;
        float f11 = f7 - f5;
        float f12 = f6;
        paramArrayOfFloat4[i++] = ((f9 * f4 + f10) * f4 + f11) * f4 + f12;
        f2 += f1;
      } 
    } else {
      while (f2 < f3 && i < j) {
        int k = (int)f2;
        float f4 = f2 - k;
        float f5 = paramArrayOfFloat1[k - 1];
        float f6 = paramArrayOfFloat1[k];
        float f7 = paramArrayOfFloat1[k + 1];
        float f8 = paramArrayOfFloat1[k + 2];
        float f9 = f8 - f7 + f6 - f5;
        float f10 = f5 - f6 - f9;
        float f11 = f7 - f5;
        float f12 = f6;
        paramArrayOfFloat4[i++] = ((f9 * f4 + f10) * f4 + f11) * f4 + f12;
        f2 += f1;
        f1 += paramFloat2;
      } 
    } 
    paramArrayOfFloat2[0] = f2;
    paramArrayOfInt[0] = i;
    paramArrayOfFloat3[0] = f1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftCubicResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */