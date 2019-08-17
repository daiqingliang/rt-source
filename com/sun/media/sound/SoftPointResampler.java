package com.sun.media.sound;

public final class SoftPointResampler extends SoftAbstractResampler {
  public int getPadding() { return 100; }
  
  public void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt) {
    float f1 = paramArrayOfFloat3[0];
    float f2 = paramArrayOfFloat2[0];
    int i = paramArrayOfInt[0];
    float f3 = paramFloat1;
    float f4 = paramInt;
    if (paramFloat2 == 0.0F) {
      while (f2 < f3 && i < f4) {
        paramArrayOfFloat4[i++] = paramArrayOfFloat1[(int)f2];
        f2 += f1;
      } 
    } else {
      while (f2 < f3 && i < f4) {
        paramArrayOfFloat4[i++] = paramArrayOfFloat1[(int)f2];
        f2 += f1;
        f1 += paramFloat2;
      } 
    } 
    paramArrayOfFloat2[0] = f2;
    paramArrayOfInt[0] = i;
    paramArrayOfFloat3[0] = f1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftPointResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */