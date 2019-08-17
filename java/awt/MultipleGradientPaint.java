package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.lang.ref.SoftReference;
import java.util.Arrays;

public abstract class MultipleGradientPaint implements Paint {
  final int transparency;
  
  final float[] fractions;
  
  final Color[] colors;
  
  final AffineTransform gradientTransform;
  
  final CycleMethod cycleMethod;
  
  final ColorSpaceType colorSpace;
  
  ColorModel model;
  
  float[] normalizedIntervals;
  
  boolean isSimpleLookup;
  
  SoftReference<int[][]> gradients;
  
  SoftReference<int[]> gradient;
  
  int fastGradientArraySize;
  
  MultipleGradientPaint(float[] paramArrayOfFloat, Color[] paramArrayOfColor, CycleMethod paramCycleMethod, ColorSpaceType paramColorSpaceType, AffineTransform paramAffineTransform) {
    if (paramArrayOfFloat == null)
      throw new NullPointerException("Fractions array cannot be null"); 
    if (paramArrayOfColor == null)
      throw new NullPointerException("Colors array cannot be null"); 
    if (paramCycleMethod == null)
      throw new NullPointerException("Cycle method cannot be null"); 
    if (paramColorSpaceType == null)
      throw new NullPointerException("Color space cannot be null"); 
    if (paramAffineTransform == null)
      throw new NullPointerException("Gradient transform cannot be null"); 
    if (paramArrayOfFloat.length != paramArrayOfColor.length)
      throw new IllegalArgumentException("Colors and fractions must have equal size"); 
    if (paramArrayOfColor.length < 2)
      throw new IllegalArgumentException("User must specify at least 2 colors"); 
    float f = -1.0F;
    for (float f1 : paramArrayOfFloat) {
      if (f1 < 0.0F || f1 > 1.0F)
        throw new IllegalArgumentException("Fraction values must be in the range 0 to 1: " + f1); 
      if (f1 <= f)
        throw new IllegalArgumentException("Keyframe fractions must be increasing: " + f1); 
      f = f1;
    } 
    boolean bool1 = false;
    boolean bool2 = false;
    int i = paramArrayOfFloat.length;
    byte b1 = 0;
    if (paramArrayOfFloat[0] != 0.0F) {
      bool1 = true;
      i++;
      b1++;
    } 
    if (paramArrayOfFloat[paramArrayOfFloat.length - 1] != 1.0F) {
      bool2 = true;
      i++;
    } 
    this.fractions = new float[i];
    System.arraycopy(paramArrayOfFloat, 0, this.fractions, b1, paramArrayOfFloat.length);
    this.colors = new Color[i];
    System.arraycopy(paramArrayOfColor, 0, this.colors, b1, paramArrayOfColor.length);
    if (bool1) {
      this.fractions[0] = 0.0F;
      this.colors[0] = paramArrayOfColor[0];
    } 
    if (bool2) {
      this.fractions[i - 1] = 1.0F;
      this.colors[i - 1] = paramArrayOfColor[paramArrayOfColor.length - 1];
    } 
    this.colorSpace = paramColorSpaceType;
    this.cycleMethod = paramCycleMethod;
    this.gradientTransform = new AffineTransform(paramAffineTransform);
    boolean bool3 = true;
    for (byte b2 = 0; b2 < paramArrayOfColor.length; b2++)
      bool3 = (bool3 && paramArrayOfColor[b2].getAlpha() == 255) ? 1 : 0; 
    this.transparency = bool3 ? 1 : 3;
  }
  
  public final float[] getFractions() { return Arrays.copyOf(this.fractions, this.fractions.length); }
  
  public final Color[] getColors() { return (Color[])Arrays.copyOf(this.colors, this.colors.length); }
  
  public final CycleMethod getCycleMethod() { return this.cycleMethod; }
  
  public final ColorSpaceType getColorSpace() { return this.colorSpace; }
  
  public final AffineTransform getTransform() { return new AffineTransform(this.gradientTransform); }
  
  public final int getTransparency() { return this.transparency; }
  
  public enum ColorSpaceType {
    SRGB, LINEAR_RGB;
  }
  
  public enum CycleMethod {
    NO_CYCLE, REFLECT, REPEAT;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MultipleGradientPaint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */