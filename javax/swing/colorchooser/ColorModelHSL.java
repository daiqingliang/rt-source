package javax.swing.colorchooser;

final class ColorModelHSL extends ColorModel {
  ColorModelHSL() { super("hsl", new String[] { "Hue", "Saturation", "Lightness", "Transparency" }); }
  
  void setColor(int paramInt, float[] paramArrayOfFloat) {
    super.setColor(paramInt, paramArrayOfFloat);
    RGBtoHSL(paramArrayOfFloat, paramArrayOfFloat);
    paramArrayOfFloat[3] = 1.0F - paramArrayOfFloat[3];
  }
  
  int getColor(float[] paramArrayOfFloat) {
    paramArrayOfFloat[3] = 1.0F - paramArrayOfFloat[3];
    HSLtoRGB(paramArrayOfFloat, paramArrayOfFloat);
    return super.getColor(paramArrayOfFloat);
  }
  
  int getMaximum(int paramInt) { return (paramInt == 0) ? 360 : 100; }
  
  float getDefault(int paramInt) { return (paramInt == 0) ? -1.0F : ((paramInt == 2) ? 0.5F : 1.0F); }
  
  private static float[] HSLtoRGB(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    if (paramArrayOfFloat2 == null)
      paramArrayOfFloat2 = new float[3]; 
    float f1 = paramArrayOfFloat1[0];
    float f2 = paramArrayOfFloat1[1];
    float f3 = paramArrayOfFloat1[2];
    if (f2 > 0.0F) {
      f1 = (f1 < 1.0F) ? (f1 * 6.0F) : 0.0F;
      float f4 = f3 + f2 * ((f3 > 0.5F) ? (1.0F - f3) : f3);
      float f5 = 2.0F * f3 - f4;
      paramArrayOfFloat2[0] = normalize(f4, f5, (f1 < 4.0F) ? (f1 + 2.0F) : (f1 - 4.0F));
      paramArrayOfFloat2[1] = normalize(f4, f5, f1);
      paramArrayOfFloat2[2] = normalize(f4, f5, (f1 < 2.0F) ? (f1 + 4.0F) : (f1 - 2.0F));
    } else {
      paramArrayOfFloat2[0] = f3;
      paramArrayOfFloat2[1] = f3;
      paramArrayOfFloat2[2] = f3;
    } 
    return paramArrayOfFloat2;
  }
  
  private static float[] RGBtoHSL(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    if (paramArrayOfFloat2 == null)
      paramArrayOfFloat2 = new float[3]; 
    float f1 = max(paramArrayOfFloat1[0], paramArrayOfFloat1[1], paramArrayOfFloat1[2]);
    float f2 = min(paramArrayOfFloat1[0], paramArrayOfFloat1[1], paramArrayOfFloat1[2]);
    float f3 = f1 + f2;
    float f4 = f1 - f2;
    if (f4 > 0.0F)
      f4 /= ((f3 > 1.0F) ? (2.0F - f3) : f3); 
    paramArrayOfFloat2[0] = getHue(paramArrayOfFloat1[0], paramArrayOfFloat1[1], paramArrayOfFloat1[2], f1, f2);
    paramArrayOfFloat2[1] = f4;
    paramArrayOfFloat2[2] = f3 / 2.0F;
    return paramArrayOfFloat2;
  }
  
  static float min(float paramFloat1, float paramFloat2, float paramFloat3) {
    float f = (paramFloat1 < paramFloat2) ? paramFloat1 : paramFloat2;
    return (f < paramFloat3) ? f : paramFloat3;
  }
  
  static float max(float paramFloat1, float paramFloat2, float paramFloat3) {
    float f = (paramFloat1 > paramFloat2) ? paramFloat1 : paramFloat2;
    return (f > paramFloat3) ? f : paramFloat3;
  }
  
  static float getHue(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5) {
    float f = paramFloat4 - paramFloat5;
    if (f > 0.0F) {
      if (paramFloat4 == paramFloat1) {
        f = (paramFloat2 - paramFloat3) / f;
        if (f < 0.0F)
          f += 6.0F; 
      } else if (paramFloat4 == paramFloat2) {
        f = 2.0F + (paramFloat3 - paramFloat1) / f;
      } else {
        f = 4.0F + (paramFloat1 - paramFloat2) / f;
      } 
      f /= 6.0F;
    } 
    return f;
  }
  
  private static float normalize(float paramFloat1, float paramFloat2, float paramFloat3) { return (paramFloat3 < 1.0F) ? (paramFloat2 + (paramFloat1 - paramFloat2) * paramFloat3) : ((paramFloat3 < 3.0F) ? paramFloat1 : ((paramFloat3 < 4.0F) ? (paramFloat2 + (paramFloat1 - paramFloat2) * (4.0F - paramFloat3)) : paramFloat2)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\ColorModelHSL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */