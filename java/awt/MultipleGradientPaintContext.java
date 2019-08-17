package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

abstract class MultipleGradientPaintContext implements PaintContext {
  protected ColorModel model;
  
  private static ColorModel xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255);
  
  protected static ColorModel cachedModel;
  
  protected static WeakReference<Raster> cached;
  
  protected Raster saved;
  
  protected MultipleGradientPaint.CycleMethod cycleMethod;
  
  protected MultipleGradientPaint.ColorSpaceType colorSpace;
  
  protected float a00;
  
  protected float a01;
  
  protected float a10;
  
  protected float a11;
  
  protected float a02;
  
  protected float a12;
  
  protected boolean isSimpleLookup;
  
  protected int fastGradientArraySize;
  
  protected int[] gradient;
  
  private int[][] gradients;
  
  private float[] normalizedIntervals;
  
  private float[] fractions;
  
  private int transparencyTest;
  
  private static final int[] SRGBtoLinearRGB = new int[256];
  
  private static final int[] LinearRGBtoSRGB = new int[256];
  
  protected static final int GRADIENT_SIZE = 256;
  
  protected static final int GRADIENT_SIZE_INDEX = 255;
  
  private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;
  
  protected MultipleGradientPaintContext(MultipleGradientPaint paramMultipleGradientPaint, ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod, MultipleGradientPaint.ColorSpaceType paramColorSpaceType) {
    if (paramRectangle == null)
      throw new NullPointerException("Device bounds cannot be null"); 
    if (paramRectangle2D == null)
      throw new NullPointerException("User bounds cannot be null"); 
    if (paramAffineTransform == null)
      throw new NullPointerException("Transform cannot be null"); 
    if (paramRenderingHints == null)
      throw new NullPointerException("RenderingHints cannot be null"); 
    try {
      paramAffineTransform.invert();
      affineTransform = paramAffineTransform;
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      affineTransform = new AffineTransform();
    } 
    double[] arrayOfDouble = new double[6];
    affineTransform.getMatrix(arrayOfDouble);
    this.a00 = (float)arrayOfDouble[0];
    this.a10 = (float)arrayOfDouble[1];
    this.a01 = (float)arrayOfDouble[2];
    this.a11 = (float)arrayOfDouble[3];
    this.a02 = (float)arrayOfDouble[4];
    this.a12 = (float)arrayOfDouble[5];
    this.cycleMethod = paramCycleMethod;
    this.colorSpace = paramColorSpaceType;
    this.fractions = paramArrayOfFloat;
    int[] arrayOfInt = (paramMultipleGradientPaint.gradient != null) ? (int[])paramMultipleGradientPaint.gradient.get() : null;
    int[][] arrayOfInt1 = (paramMultipleGradientPaint.gradients != null) ? (int[][])paramMultipleGradientPaint.gradients.get() : (int[][])null;
    if (arrayOfInt == null && arrayOfInt1 == null) {
      calculateLookupData(paramArrayOfColor);
      paramMultipleGradientPaint.model = this.model;
      paramMultipleGradientPaint.normalizedIntervals = this.normalizedIntervals;
      paramMultipleGradientPaint.isSimpleLookup = this.isSimpleLookup;
      if (this.isSimpleLookup) {
        paramMultipleGradientPaint.fastGradientArraySize = this.fastGradientArraySize;
        paramMultipleGradientPaint.gradient = new SoftReference(this.gradient);
      } else {
        paramMultipleGradientPaint.gradients = new SoftReference(this.gradients);
      } 
    } else {
      this.model = paramMultipleGradientPaint.model;
      this.normalizedIntervals = paramMultipleGradientPaint.normalizedIntervals;
      this.isSimpleLookup = paramMultipleGradientPaint.isSimpleLookup;
      this.gradient = arrayOfInt;
      this.fastGradientArraySize = paramMultipleGradientPaint.fastGradientArraySize;
      this.gradients = arrayOfInt1;
    } 
  }
  
  private void calculateLookupData(Color[] paramArrayOfColor) {
    Color[] arrayOfColor;
    if (this.colorSpace == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) {
      arrayOfColor = new Color[paramArrayOfColor.length];
      for (byte b = 0; b < paramArrayOfColor.length; b++) {
        int j = paramArrayOfColor[b].getRGB();
        int k = j >>> 24;
        int m = SRGBtoLinearRGB[j >> 16 & 0xFF];
        int n = SRGBtoLinearRGB[j >> 8 & 0xFF];
        int i1 = SRGBtoLinearRGB[j & 0xFF];
        arrayOfColor[b] = new Color(m, n, i1, k);
      } 
    } else {
      arrayOfColor = paramArrayOfColor;
    } 
    this.normalizedIntervals = new float[this.fractions.length - 1];
    for (byte b1 = 0; b1 < this.normalizedIntervals.length; b1++)
      this.normalizedIntervals[b1] = this.fractions[b1 + true] - this.fractions[b1]; 
    this.transparencyTest = -16777216;
    this.gradients = new int[this.normalizedIntervals.length][];
    float f = 1.0F;
    int i;
    for (i = 0; i < this.normalizedIntervals.length; i++)
      f = (f > this.normalizedIntervals[i]) ? this.normalizedIntervals[i] : f; 
    i = 0;
    for (byte b2 = 0; b2 < this.normalizedIntervals.length; b2++)
      i = (int)(i + this.normalizedIntervals[b2] / f * 256.0F); 
    if (i > 5000) {
      calculateMultipleArrayGradient(arrayOfColor);
    } else {
      calculateSingleArrayGradient(arrayOfColor, f);
    } 
    if (this.transparencyTest >>> 24 == 255) {
      this.model = xrgbmodel;
    } else {
      this.model = ColorModel.getRGBdefault();
    } 
  }
  
  private void calculateSingleArrayGradient(Color[] paramArrayOfColor, float paramFloat) {
    this.isSimpleLookup = true;
    int i = 1;
    int j;
    for (j = 0; j < this.gradients.length; j++) {
      int n = (int)(this.normalizedIntervals[j] / paramFloat * 255.0F);
      i += n;
      this.gradients[j] = new int[n];
      int k = paramArrayOfColor[j].getRGB();
      int m = paramArrayOfColor[j + true].getRGB();
      interpolate(k, m, this.gradients[j]);
      this.transparencyTest &= k;
      this.transparencyTest &= m;
    } 
    this.gradient = new int[i];
    j = 0;
    byte b;
    for (b = 0; b < this.gradients.length; b++) {
      System.arraycopy(this.gradients[b], 0, this.gradient, j, this.gradients[b].length);
      j += this.gradients[b].length;
    } 
    this.gradient[this.gradient.length - 1] = paramArrayOfColor[paramArrayOfColor.length - 1].getRGB();
    if (this.colorSpace == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB)
      for (b = 0; b < this.gradient.length; b++)
        this.gradient[b] = convertEntireColorLinearRGBtoSRGB(this.gradient[b]);  
    this.fastGradientArraySize = this.gradient.length - 1;
  }
  
  private void calculateMultipleArrayGradient(Color[] paramArrayOfColor) {
    this.isSimpleLookup = false;
    byte b;
    for (b = 0; b < this.gradients.length; b++) {
      this.gradients[b] = new int[256];
      int i = paramArrayOfColor[b].getRGB();
      int j = paramArrayOfColor[b + true].getRGB();
      interpolate(i, j, this.gradients[b]);
      this.transparencyTest &= i;
      this.transparencyTest &= j;
    } 
    if (this.colorSpace == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB)
      for (b = 0; b < this.gradients.length; b++) {
        for (byte b1 = 0; b1 < this.gradients[b].length; b1++)
          this.gradients[b][b1] = convertEntireColorLinearRGBtoSRGB(this.gradients[b][b1]); 
      }  
  }
  
  private void interpolate(int paramInt1, int paramInt2, int[] paramArrayOfInt) {
    float f = 1.0F / paramArrayOfInt.length;
    int i = paramInt1 >> 24 & 0xFF;
    int j = paramInt1 >> 16 & 0xFF;
    int k = paramInt1 >> 8 & 0xFF;
    int m = paramInt1 & 0xFF;
    int n = (paramInt2 >> 24 & 0xFF) - i;
    int i1 = (paramInt2 >> 16 & 0xFF) - j;
    int i2 = (paramInt2 >> 8 & 0xFF) - k;
    int i3 = (paramInt2 & 0xFF) - m;
    for (int i4 = 0; i4 < paramArrayOfInt.length; i4++)
      paramArrayOfInt[i4] = (int)((i + (i4 * n) * f) + 0.5D) << 24 | (int)((j + (i4 * i1) * f) + 0.5D) << 16 | (int)((k + (i4 * i2) * f) + 0.5D) << 8 | (int)((m + (i4 * i3) * f) + 0.5D); 
  }
  
  private int convertEntireColorLinearRGBtoSRGB(int paramInt) {
    int i = paramInt >> 24 & 0xFF;
    int j = paramInt >> 16 & 0xFF;
    int k = paramInt >> 8 & 0xFF;
    int m = paramInt & 0xFF;
    j = LinearRGBtoSRGB[j];
    k = LinearRGBtoSRGB[k];
    m = LinearRGBtoSRGB[m];
    return i << 24 | j << 16 | k << 8 | m;
  }
  
  protected final int indexIntoGradientsArrays(float paramFloat) {
    if (this.cycleMethod == MultipleGradientPaint.CycleMethod.NO_CYCLE) {
      if (paramFloat > 1.0F) {
        paramFloat = 1.0F;
      } else if (paramFloat < 0.0F) {
        paramFloat = 0.0F;
      } 
    } else if (this.cycleMethod == MultipleGradientPaint.CycleMethod.REPEAT) {
      paramFloat -= (int)paramFloat;
      if (paramFloat < 0.0F)
        paramFloat++; 
    } else {
      if (paramFloat < 0.0F)
        paramFloat = -paramFloat; 
      int i = (int)paramFloat;
      paramFloat -= i;
      if ((i & true) == 1)
        paramFloat = 1.0F - paramFloat; 
    } 
    if (this.isSimpleLookup)
      return this.gradient[(int)(paramFloat * this.fastGradientArraySize)]; 
    for (byte b = 0; b < this.gradients.length; b++) {
      if (paramFloat < this.fractions[b + true]) {
        float f = paramFloat - this.fractions[b];
        int i = (int)(f / this.normalizedIntervals[b] * 255.0F);
        return this.gradients[b][i];
      } 
    } 
    return this.gradients[this.gradients.length - 1][255];
  }
  
  private static int convertSRGBtoLinearRGB(int paramInt) {
    float f2;
    float f1 = paramInt / 255.0F;
    if (f1 <= 0.04045F) {
      f2 = f1 / 12.92F;
    } else {
      f2 = (float)Math.pow((f1 + 0.055D) / 1.055D, 2.4D);
    } 
    return Math.round(f2 * 255.0F);
  }
  
  private static int convertLinearRGBtoSRGB(int paramInt) {
    float f2;
    float f1 = paramInt / 255.0F;
    if (f1 <= 0.0031308D) {
      f2 = f1 * 12.92F;
    } else {
      f2 = 1.055F * (float)Math.pow(f1, 0.4166666666666667D) - 0.055F;
    } 
    return Math.round(f2 * 255.0F);
  }
  
  public final Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Raster raster = this.saved;
    if (raster == null || raster.getWidth() < paramInt3 || raster.getHeight() < paramInt4) {
      raster = getCachedRaster(this.model, paramInt3, paramInt4);
      this.saved = raster;
    } 
    DataBufferInt dataBufferInt = (DataBufferInt)raster.getDataBuffer();
    int[] arrayOfInt = dataBufferInt.getData(0);
    int i = dataBufferInt.getOffset();
    int j = ((SinglePixelPackedSampleModel)raster.getSampleModel()).getScanlineStride();
    int k = j - paramInt3;
    fillRaster(arrayOfInt, i, k, paramInt1, paramInt2, paramInt3, paramInt4);
    return raster;
  }
  
  protected abstract void fillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  private static Raster getCachedRaster(ColorModel paramColorModel, int paramInt1, int paramInt2) {
    if (paramColorModel == cachedModel && cached != null) {
      Raster raster = (Raster)cached.get();
      if (raster != null && raster.getWidth() >= paramInt1 && raster.getHeight() >= paramInt2) {
        cached = null;
        return raster;
      } 
    } 
    return paramColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
  }
  
  private static void putCachedRaster(ColorModel paramColorModel, Raster paramRaster) {
    if (cached != null) {
      Raster raster = (Raster)cached.get();
      if (raster != null) {
        int i = raster.getWidth();
        int j = raster.getHeight();
        int k = paramRaster.getWidth();
        int m = paramRaster.getHeight();
        if (i >= k && j >= m)
          return; 
        if (i * j >= k * m)
          return; 
      } 
    } 
    cachedModel = paramColorModel;
    cached = new WeakReference(paramRaster);
  }
  
  public final void dispose() {
    if (this.saved != null) {
      putCachedRaster(this.model, this.saved);
      this.saved = null;
    } 
  }
  
  public final ColorModel getColorModel() { return this.model; }
  
  static  {
    for (byte b = 0; b < 'Ā'; b++) {
      SRGBtoLinearRGB[b] = convertSRGBtoLinearRGB(b);
      LinearRGBtoSRGB[b] = convertLinearRGBtoSRGB(b);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MultipleGradientPaintContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */