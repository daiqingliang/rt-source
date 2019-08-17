package sun.java2d.pipe;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import sun.awt.image.PixelConverter;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;

public class BufferedPaints {
  public static final int MULTI_MAX_FRACTIONS = 12;
  
  static void setPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, Paint paramPaint, int paramInt) {
    if (paramSunGraphics2D.paintState <= 1) {
      setColor(paramRenderQueue, paramSunGraphics2D.pixel);
    } else {
      boolean bool = ((paramInt & 0x2) != 0);
      switch (paramSunGraphics2D.paintState) {
        case 2:
          setGradientPaint(paramRenderQueue, paramSunGraphics2D, (GradientPaint)paramPaint, bool);
          break;
        case 3:
          setLinearGradientPaint(paramRenderQueue, paramSunGraphics2D, (LinearGradientPaint)paramPaint, bool);
          break;
        case 4:
          setRadialGradientPaint(paramRenderQueue, paramSunGraphics2D, (RadialGradientPaint)paramPaint, bool);
          break;
        case 5:
          setTexturePaint(paramRenderQueue, paramSunGraphics2D, (TexturePaint)paramPaint, bool);
          break;
      } 
    } 
  }
  
  static void resetPaint(RenderQueue paramRenderQueue) {
    paramRenderQueue.ensureCapacity(4);
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    renderBuffer.putInt(100);
  }
  
  private static void setColor(RenderQueue paramRenderQueue, int paramInt) {
    paramRenderQueue.ensureCapacity(8);
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    renderBuffer.putInt(101);
    renderBuffer.putInt(paramInt);
  }
  
  private static void setGradientPaint(RenderQueue paramRenderQueue, AffineTransform paramAffineTransform, Color paramColor1, Color paramColor2, Point2D paramPoint2D1, Point2D paramPoint2D2, boolean paramBoolean1, boolean paramBoolean2) {
    double d6;
    double d5;
    double d4;
    PixelConverter pixelConverter = PixelConverter.ArgbPre.instance;
    int i = pixelConverter.rgbToPixel(paramColor1.getRGB(), null);
    int j = pixelConverter.rgbToPixel(paramColor2.getRGB(), null);
    double d1 = paramPoint2D1.getX();
    double d2 = paramPoint2D1.getY();
    paramAffineTransform.translate(d1, d2);
    d1 = paramPoint2D2.getX() - d1;
    d2 = paramPoint2D2.getY() - d2;
    double d3 = Math.sqrt(d1 * d1 + d2 * d2);
    paramAffineTransform.rotate(d1, d2);
    paramAffineTransform.scale(2.0D * d3, 1.0D);
    paramAffineTransform.translate(-0.25D, 0.0D);
    try {
      paramAffineTransform.invert();
      d4 = paramAffineTransform.getScaleX();
      d5 = paramAffineTransform.getShearX();
      d6 = paramAffineTransform.getTranslateX();
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      d4 = d5 = d6 = 0.0D;
    } 
    paramRenderQueue.ensureCapacityAndAlignment(44, 12);
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    renderBuffer.putInt(102);
    renderBuffer.putInt(paramBoolean2 ? 1 : 0);
    renderBuffer.putInt(paramBoolean1 ? 1 : 0);
    renderBuffer.putDouble(d4).putDouble(d5).putDouble(d6);
    renderBuffer.putInt(i).putInt(j);
  }
  
  private static void setGradientPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, GradientPaint paramGradientPaint, boolean paramBoolean) { setGradientPaint(paramRenderQueue, (AffineTransform)paramSunGraphics2D.transform.clone(), paramGradientPaint.getColor1(), paramGradientPaint.getColor2(), paramGradientPaint.getPoint1(), paramGradientPaint.getPoint2(), paramGradientPaint.isCyclic(), paramBoolean); }
  
  private static void setTexturePaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, TexturePaint paramTexturePaint, boolean paramBoolean) {
    double d6;
    double d5;
    double d4;
    double d3;
    double d2;
    double d1;
    BufferedImage bufferedImage = paramTexturePaint.getImage();
    SurfaceData surfaceData1 = paramSunGraphics2D.surfaceData;
    SurfaceData surfaceData2 = surfaceData1.getSourceSurfaceData(bufferedImage, 0, CompositeType.SrcOver, null);
    boolean bool = (paramSunGraphics2D.interpolationType != 1) ? 1 : 0;
    AffineTransform affineTransform = (AffineTransform)paramSunGraphics2D.transform.clone();
    Rectangle2D rectangle2D = paramTexturePaint.getAnchorRect();
    affineTransform.translate(rectangle2D.getX(), rectangle2D.getY());
    affineTransform.scale(rectangle2D.getWidth(), rectangle2D.getHeight());
    try {
      affineTransform.invert();
      d1 = affineTransform.getScaleX();
      d2 = affineTransform.getShearX();
      d3 = affineTransform.getTranslateX();
      d4 = affineTransform.getShearY();
      d5 = affineTransform.getScaleY();
      d6 = affineTransform.getTranslateY();
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      d1 = d2 = d3 = d4 = d5 = d6 = 0.0D;
    } 
    paramRenderQueue.ensureCapacityAndAlignment(68, 12);
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    renderBuffer.putInt(105);
    renderBuffer.putInt(paramBoolean ? 1 : 0);
    renderBuffer.putInt(bool ? 1 : 0);
    renderBuffer.putLong(surfaceData2.getNativeOps());
    renderBuffer.putDouble(d1).putDouble(d2).putDouble(d3);
    renderBuffer.putDouble(d4).putDouble(d5).putDouble(d6);
  }
  
  public static int convertSRGBtoLinearRGB(int paramInt) {
    float f2;
    float f1 = paramInt / 255.0F;
    if (f1 <= 0.04045F) {
      f2 = f1 / 12.92F;
    } else {
      f2 = (float)Math.pow((f1 + 0.055D) / 1.055D, 2.4D);
    } 
    return Math.round(f2 * 255.0F);
  }
  
  private static int colorToIntArgbPrePixel(Color paramColor, boolean paramBoolean) {
    int i = paramColor.getRGB();
    if (!paramBoolean && i >> 24 == -1)
      return i; 
    int j = i >>> 24;
    int k = i >> 16 & 0xFF;
    int m = i >> 8 & 0xFF;
    int n = i & 0xFF;
    if (paramBoolean) {
      k = convertSRGBtoLinearRGB(k);
      m = convertSRGBtoLinearRGB(m);
      n = convertSRGBtoLinearRGB(n);
    } 
    int i1 = j + (j >> 7);
    k = k * i1 >> 8;
    m = m * i1 >> 8;
    n = n * i1 >> 8;
    return j << 24 | k << 16 | m << 8 | n;
  }
  
  private static int[] convertToIntArgbPrePixels(Color[] paramArrayOfColor, boolean paramBoolean) {
    int[] arrayOfInt = new int[paramArrayOfColor.length];
    for (byte b = 0; b < paramArrayOfColor.length; b++)
      arrayOfInt[b] = colorToIntArgbPrePixel(paramArrayOfColor[b], paramBoolean); 
    return arrayOfInt;
  }
  
  private static void setLinearGradientPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, LinearGradientPaint paramLinearGradientPaint, boolean paramBoolean) {
    float f3;
    float f2;
    float f1;
    boolean bool = (paramLinearGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB);
    Color[] arrayOfColor = paramLinearGradientPaint.getColors();
    int i = arrayOfColor.length;
    Point2D point2D1 = paramLinearGradientPaint.getStartPoint();
    Point2D point2D2 = paramLinearGradientPaint.getEndPoint();
    AffineTransform affineTransform = paramLinearGradientPaint.getTransform();
    affineTransform.preConcatenate(paramSunGraphics2D.transform);
    if (!bool && i == 2 && paramLinearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT) {
      boolean bool1 = (paramLinearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.NO_CYCLE);
      setGradientPaint(paramRenderQueue, affineTransform, arrayOfColor[0], arrayOfColor[1], point2D1, point2D2, bool1, paramBoolean);
      return;
    } 
    int j = paramLinearGradientPaint.getCycleMethod().ordinal();
    float[] arrayOfFloat = paramLinearGradientPaint.getFractions();
    int[] arrayOfInt = convertToIntArgbPrePixels(arrayOfColor, bool);
    double d1 = point2D1.getX();
    double d2 = point2D1.getY();
    affineTransform.translate(d1, d2);
    d1 = point2D2.getX() - d1;
    d2 = point2D2.getY() - d2;
    double d3 = Math.sqrt(d1 * d1 + d2 * d2);
    affineTransform.rotate(d1, d2);
    affineTransform.scale(d3, 1.0D);
    try {
      affineTransform.invert();
      f1 = (float)affineTransform.getScaleX();
      f2 = (float)affineTransform.getShearX();
      f3 = (float)affineTransform.getTranslateX();
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      f1 = f2 = f3 = 0.0F;
    } 
    paramRenderQueue.ensureCapacity(32 + i * 4 * 2);
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    renderBuffer.putInt(103);
    renderBuffer.putInt(paramBoolean ? 1 : 0);
    renderBuffer.putInt(bool ? 1 : 0);
    renderBuffer.putInt(j);
    renderBuffer.putInt(i);
    renderBuffer.putFloat(f1);
    renderBuffer.putFloat(f2);
    renderBuffer.putFloat(f3);
    renderBuffer.put(arrayOfFloat);
    renderBuffer.put(arrayOfInt);
  }
  
  private static void setRadialGradientPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, RadialGradientPaint paramRadialGradientPaint, boolean paramBoolean) {
    boolean bool = (paramRadialGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB);
    int i = paramRadialGradientPaint.getCycleMethod().ordinal();
    float[] arrayOfFloat = paramRadialGradientPaint.getFractions();
    Color[] arrayOfColor = paramRadialGradientPaint.getColors();
    int j = arrayOfColor.length;
    int[] arrayOfInt = convertToIntArgbPrePixels(arrayOfColor, bool);
    Point2D point2D1 = paramRadialGradientPaint.getCenterPoint();
    Point2D point2D2 = paramRadialGradientPaint.getFocusPoint();
    float f = paramRadialGradientPaint.getRadius();
    double d1 = point2D1.getX();
    double d2 = point2D1.getY();
    double d3 = point2D2.getX();
    double d4 = point2D2.getY();
    AffineTransform affineTransform = paramRadialGradientPaint.getTransform();
    affineTransform.preConcatenate(paramSunGraphics2D.transform);
    point2D2 = affineTransform.transform(point2D2, point2D2);
    affineTransform.translate(d1, d2);
    affineTransform.rotate(d3 - d1, d4 - d2);
    affineTransform.scale(f, f);
    try {
      affineTransform.invert();
    } catch (Exception exception) {
      affineTransform.setToScale(0.0D, 0.0D);
    } 
    point2D2 = affineTransform.transform(point2D2, point2D2);
    d3 = Math.min(point2D2.getX(), 0.99D);
    paramRenderQueue.ensureCapacity(48 + j * 4 * 2);
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    renderBuffer.putInt(104);
    renderBuffer.putInt(paramBoolean ? 1 : 0);
    renderBuffer.putInt(bool ? 1 : 0);
    renderBuffer.putInt(j);
    renderBuffer.putInt(i);
    renderBuffer.putFloat((float)affineTransform.getScaleX());
    renderBuffer.putFloat((float)affineTransform.getShearX());
    renderBuffer.putFloat((float)affineTransform.getTranslateX());
    renderBuffer.putFloat((float)affineTransform.getShearY());
    renderBuffer.putFloat((float)affineTransform.getScaleY());
    renderBuffer.putFloat((float)affineTransform.getTranslateY());
    renderBuffer.putFloat((float)d3);
    renderBuffer.put(arrayOfFloat);
    renderBuffer.put(arrayOfInt);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\BufferedPaints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */