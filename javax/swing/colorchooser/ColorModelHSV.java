package javax.swing.colorchooser;

final class ColorModelHSV extends ColorModel {
  ColorModelHSV() { super("hsv", new String[] { "Hue", "Saturation", "Value", "Transparency" }); }
  
  void setColor(int paramInt, float[] paramArrayOfFloat) {
    super.setColor(paramInt, paramArrayOfFloat);
    RGBtoHSV(paramArrayOfFloat, paramArrayOfFloat);
    paramArrayOfFloat[3] = 1.0F - paramArrayOfFloat[3];
  }
  
  int getColor(float[] paramArrayOfFloat) {
    paramArrayOfFloat[3] = 1.0F - paramArrayOfFloat[3];
    HSVtoRGB(paramArrayOfFloat, paramArrayOfFloat);
    return super.getColor(paramArrayOfFloat);
  }
  
  int getMaximum(int paramInt) { return (paramInt == 0) ? 360 : 100; }
  
  float getDefault(int paramInt) { return (paramInt == 0) ? -1.0F : 1.0F; }
  
  private static float[] HSVtoRGB(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    if (paramArrayOfFloat2 == null)
      paramArrayOfFloat2 = new float[3]; 
    float f1 = paramArrayOfFloat1[0];
    float f2 = paramArrayOfFloat1[1];
    float f3 = paramArrayOfFloat1[2];
    paramArrayOfFloat2[0] = f3;
    paramArrayOfFloat2[1] = f3;
    paramArrayOfFloat2[2] = f3;
    if (f2 > 0.0F) {
      f1 = (f1 < 1.0F) ? (f1 * 6.0F) : 0.0F;
      int i = (int)f1;
      float f = f1 - i;
      switch (i) {
        case 0:
          paramArrayOfFloat2[1] = paramArrayOfFloat2[1] * (1.0F - f2 * (1.0F - f));
          paramArrayOfFloat2[2] = paramArrayOfFloat2[2] * (1.0F - f2);
          break;
        case 1:
          paramArrayOfFloat2[0] = paramArrayOfFloat2[0] * (1.0F - f2 * f);
          paramArrayOfFloat2[2] = paramArrayOfFloat2[2] * (1.0F - f2);
          break;
        case 2:
          paramArrayOfFloat2[0] = paramArrayOfFloat2[0] * (1.0F - f2);
          paramArrayOfFloat2[2] = paramArrayOfFloat2[2] * (1.0F - f2 * (1.0F - f));
          break;
        case 3:
          paramArrayOfFloat2[0] = paramArrayOfFloat2[0] * (1.0F - f2);
          paramArrayOfFloat2[1] = paramArrayOfFloat2[1] * (1.0F - f2 * f);
          break;
        case 4:
          paramArrayOfFloat2[0] = paramArrayOfFloat2[0] * (1.0F - f2 * (1.0F - f));
          paramArrayOfFloat2[1] = paramArrayOfFloat2[1] * (1.0F - f2);
          break;
        case 5:
          paramArrayOfFloat2[1] = paramArrayOfFloat2[1] * (1.0F - f2);
          paramArrayOfFloat2[2] = paramArrayOfFloat2[2] * (1.0F - f2 * f);
          break;
      } 
    } 
    return paramArrayOfFloat2;
  }
  
  private static float[] RGBtoHSV(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    if (paramArrayOfFloat2 == null)
      paramArrayOfFloat2 = new float[3]; 
    float f1 = ColorModelHSL.max(paramArrayOfFloat1[0], paramArrayOfFloat1[1], paramArrayOfFloat1[2]);
    float f2 = ColorModelHSL.min(paramArrayOfFloat1[0], paramArrayOfFloat1[1], paramArrayOfFloat1[2]);
    float f3 = f1 - f2;
    if (f3 > 0.0F)
      f3 /= f1; 
    paramArrayOfFloat2[0] = ColorModelHSL.getHue(paramArrayOfFloat1[0], paramArrayOfFloat1[1], paramArrayOfFloat1[2], f1, f2);
    paramArrayOfFloat2[1] = f3;
    paramArrayOfFloat2[2] = f1;
    return paramArrayOfFloat2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\ColorModelHSV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */