package com.sun.media.sound;

public final class SoftLinearResampler extends SoftAbstractResampler {
  public int getPadding() { return 2; }
  
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
        float f5 = paramArrayOfFloat1[k];
        paramArrayOfFloat4[i++] = f5 + (paramArrayOfFloat1[k + 1] - f5) * f4;
        f2 += f1;
      } 
    } else {
      while (f2 < f3 && i < j) {
        int k = (int)f2;
        float f4 = f2 - k;
        float f5 = paramArrayOfFloat1[k];
        paramArrayOfFloat4[i++] = f5 + (paramArrayOfFloat1[k + 1] - f5) * f4;
        f2 += f1;
        f1 += paramFloat2;
      } 
    } 
    paramArrayOfFloat2[0] = f2;
    paramArrayOfInt[0] = i;
    paramArrayOfFloat3[0] = f1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftLinearResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */