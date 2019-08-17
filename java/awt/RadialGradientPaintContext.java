package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

final class RadialGradientPaintContext extends MultipleGradientPaintContext {
  private boolean isSimpleFocus = false;
  
  private boolean isNonCyclic = false;
  
  private float radius;
  
  private float centerX;
  
  private float centerY;
  
  private float focusX;
  
  private float focusY;
  
  private float radiusSq;
  
  private float constA;
  
  private float constB;
  
  private float gDeltaDelta;
  
  private float trivial;
  
  private static final float SCALEBACK = 0.99F;
  
  private static final int SQRT_LUT_SIZE = 2048;
  
  private static float[] sqrtLut = new float[2049];
  
  RadialGradientPaintContext(RadialGradientPaint paramRadialGradientPaint, ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod, MultipleGradientPaint.ColorSpaceType paramColorSpaceType) {
    super(paramRadialGradientPaint, paramColorModel, paramRectangle, paramRectangle2D, paramAffineTransform, paramRenderingHints, paramArrayOfFloat, paramArrayOfColor, paramCycleMethod, paramColorSpaceType);
    this.centerX = paramFloat1;
    this.centerY = paramFloat2;
    this.focusX = paramFloat4;
    this.focusY = paramFloat5;
    this.radius = paramFloat3;
    this.isSimpleFocus = (this.focusX == this.centerX && this.focusY == this.centerY);
    this.isNonCyclic = (paramCycleMethod == MultipleGradientPaint.CycleMethod.NO_CYCLE);
    this.radiusSq = this.radius * this.radius;
    float f1 = this.focusX - this.centerX;
    float f2 = this.focusY - this.centerY;
    double d = (f1 * f1 + f2 * f2);
    if (d > (this.radiusSq * 0.99F)) {
      float f = (float)Math.sqrt((this.radiusSq * 0.99F) / d);
      f1 *= f;
      f2 *= f;
      this.focusX = this.centerX + f1;
      this.focusY = this.centerY + f2;
    } 
    this.trivial = (float)Math.sqrt((this.radiusSq - f1 * f1));
    this.constA = this.a02 - this.centerX;
    this.constB = this.a12 - this.centerY;
    this.gDeltaDelta = 2.0F * (this.a00 * this.a00 + this.a10 * this.a10) / this.radiusSq;
  }
  
  protected void fillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (this.isSimpleFocus && this.isNonCyclic && this.isSimpleLookup) {
      simpleNonCyclicFillRaster(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } else {
      cyclicCircularGradientFillRaster(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } 
  }
  
  private void simpleNonCyclicFillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    float f1 = this.a00 * paramInt3 + this.a01 * paramInt4 + this.constA;
    float f2 = this.a10 * paramInt3 + this.a11 * paramInt4 + this.constB;
    float f3 = this.gDeltaDelta;
    paramInt2 += paramInt5;
    int i = this.gradient[this.fastGradientArraySize];
    for (byte b = 0; b < paramInt6; b++) {
      float f4 = (f1 * f1 + f2 * f2) / this.radiusSq;
      float f5 = 2.0F * (this.a00 * f1 + this.a10 * f2) / this.radiusSq + f3 / 2.0F;
      int j;
      for (j = 0; j < paramInt5 && f4 >= 1.0F; j++) {
        paramArrayOfInt[paramInt1 + j] = i;
        f4 += f5;
        f5 += f3;
      } 
      while (j < paramInt5 && f4 < 1.0F) {
        int k;
        if (f4 <= 0.0F) {
          k = 0;
        } else {
          float f6 = f4 * 2048.0F;
          int m = (int)f6;
          float f7 = sqrtLut[m];
          float f8 = sqrtLut[m + 1] - f7;
          f6 = f7 + (f6 - m) * f8;
          k = (int)(f6 * this.fastGradientArraySize);
        } 
        paramArrayOfInt[paramInt1 + j] = this.gradient[k];
        f4 += f5;
        f5 += f3;
        j++;
      } 
      while (j < paramInt5) {
        paramArrayOfInt[paramInt1 + j] = i;
        j++;
      } 
      paramInt1 += paramInt2;
      f1 += this.a01;
      f2 += this.a11;
    } 
  }
  
  private void cyclicCircularGradientFillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    double d = (-this.radiusSq + this.centerX * this.centerX + this.centerY * this.centerY);
    float f1 = this.a00 * paramInt3 + this.a01 * paramInt4 + this.a02;
    float f2 = this.a10 * paramInt3 + this.a11 * paramInt4 + this.a12;
    float f3 = 2.0F * this.centerY;
    float f4 = -2.0F * this.centerX;
    int i = paramInt1;
    int j = paramInt5 + paramInt2;
    for (byte b = 0; b < paramInt6; b++) {
      float f5 = this.a01 * b + f1;
      float f6 = this.a11 * b + f2;
      for (int k = 0; k < paramInt5; k++) {
        double d2;
        double d1;
        if (f5 == this.focusX) {
          d1 = this.focusX;
          d2 = this.centerY;
          d2 += ((f6 > this.focusY) ? this.trivial : -this.trivial);
        } else {
          double d6 = ((f6 - this.focusY) / (f5 - this.focusX));
          double d7 = f6 - d6 * f5;
          double d3 = d6 * d6 + 1.0D;
          double d4 = f4 + -2.0D * d6 * (this.centerY - d7);
          double d5 = d + d7 * (d7 - f3);
          float f = (float)Math.sqrt(d4 * d4 - 4.0D * d3 * d5);
          d1 = -d4;
          d1 += ((f5 < this.focusX) ? -f : f);
          d1 /= 2.0D * d3;
          d2 = d6 * d1 + d7;
        } 
        float f10 = f5 - this.focusX;
        f10 *= f10;
        float f11 = f6 - this.focusY;
        f11 *= f11;
        float f8 = f10 + f11;
        f10 = (float)d1 - this.focusX;
        f10 *= f10;
        f11 = (float)d2 - this.focusY;
        f11 *= f11;
        float f9 = f10 + f11;
        float f7 = (float)Math.sqrt((f8 / f9));
        paramArrayOfInt[i + k] = indexIntoGradientsArrays(f7);
        f5 += this.a00;
        f6 += this.a10;
      } 
      i += j;
    } 
  }
  
  static  {
    for (byte b = 0; b < sqrtLut.length; b++)
      sqrtLut[b] = (float)Math.sqrt((b / 2048.0F)); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\RadialGradientPaintContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */