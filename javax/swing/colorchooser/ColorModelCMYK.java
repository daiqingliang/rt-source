package javax.swing.colorchooser;

final class ColorModelCMYK extends ColorModel {
  ColorModelCMYK() { super("cmyk", new String[] { "Cyan", "Magenta", "Yellow", "Black", "Alpha" }); }
  
  void setColor(int paramInt, float[] paramArrayOfFloat) {
    super.setColor(paramInt, paramArrayOfFloat);
    paramArrayOfFloat[4] = paramArrayOfFloat[3];
    RGBtoCMYK(paramArrayOfFloat, paramArrayOfFloat);
  }
  
  int getColor(float[] paramArrayOfFloat) {
    CMYKtoRGB(paramArrayOfFloat, paramArrayOfFloat);
    paramArrayOfFloat[3] = paramArrayOfFloat[4];
    return super.getColor(paramArrayOfFloat);
  }
  
  private static float[] CMYKtoRGB(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    if (paramArrayOfFloat2 == null)
      paramArrayOfFloat2 = new float[3]; 
    paramArrayOfFloat2[0] = 1.0F + paramArrayOfFloat1[0] * paramArrayOfFloat1[3] - paramArrayOfFloat1[3] - paramArrayOfFloat1[0];
    paramArrayOfFloat2[1] = 1.0F + paramArrayOfFloat1[1] * paramArrayOfFloat1[3] - paramArrayOfFloat1[3] - paramArrayOfFloat1[1];
    paramArrayOfFloat2[2] = 1.0F + paramArrayOfFloat1[2] * paramArrayOfFloat1[3] - paramArrayOfFloat1[3] - paramArrayOfFloat1[2];
    return paramArrayOfFloat2;
  }
  
  private static float[] RGBtoCMYK(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    if (paramArrayOfFloat2 == null)
      paramArrayOfFloat2 = new float[4]; 
    float f = ColorModelHSL.max(paramArrayOfFloat1[0], paramArrayOfFloat1[1], paramArrayOfFloat1[2]);
    if (f > 0.0F) {
      paramArrayOfFloat2[0] = 1.0F - paramArrayOfFloat1[0] / f;
      paramArrayOfFloat2[1] = 1.0F - paramArrayOfFloat1[1] / f;
      paramArrayOfFloat2[2] = 1.0F - paramArrayOfFloat1[2] / f;
    } else {
      paramArrayOfFloat2[0] = 0.0F;
      paramArrayOfFloat2[1] = 0.0F;
      paramArrayOfFloat2[2] = 0.0F;
    } 
    paramArrayOfFloat2[3] = 1.0F - f;
    return paramArrayOfFloat2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\ColorModelCMYK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */