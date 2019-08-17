package sun.awt.windows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.security.AccessController;
import java.util.Arrays;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.BytePackedRaster;
import sun.font.CompositeFont;
import sun.font.Font2D;
import sun.font.FontUtilities;
import sun.font.PhysicalFont;
import sun.font.TrueTypeFont;
import sun.print.PathGraphics;
import sun.print.ProxyGraphics2D;
import sun.security.action.GetPropertyAction;

final class WPathGraphics extends PathGraphics {
  private static final int DEFAULT_USER_RES = 72;
  
  private static final float MIN_DEVICE_LINEWIDTH = 1.2F;
  
  private static final float MAX_THINLINE_INCHES = 0.014F;
  
  private static boolean useGDITextLayout = true;
  
  private static boolean preferGDITextLayout = false;
  
  WPathGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt, boolean paramBoolean) { super(paramGraphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, paramBoolean); }
  
  public Graphics create() { return new WPathGraphics((Graphics2D)getDelegate().create(), getPrinterJob(), getPrintable(), getPageFormat(), getPageIndex(), canDoRedraws()); }
  
  public void draw(Shape paramShape) {
    Stroke stroke = getStroke();
    if (stroke instanceof BasicStroke) {
      BasicStroke basicStroke2 = null;
      BasicStroke basicStroke1 = (BasicStroke)stroke;
      float f2 = basicStroke1.getLineWidth();
      Point2D.Float float = new Point2D.Float(f2, f2);
      AffineTransform affineTransform = getTransform();
      affineTransform.deltaTransform(float, float);
      float f1 = Math.min(Math.abs(float.x), Math.abs(float.y));
      if (f1 < 1.2F) {
        Point2D.Float float1 = new Point2D.Float(1.2F, 1.2F);
        try {
          AffineTransform affineTransform1 = affineTransform.createInverse();
          affineTransform1.deltaTransform(float1, float1);
          float f = Math.max(Math.abs(float1.x), Math.abs(float1.y));
          basicStroke2 = new BasicStroke(f, basicStroke1.getEndCap(), basicStroke1.getLineJoin(), basicStroke1.getMiterLimit(), basicStroke1.getDashArray(), basicStroke1.getDashPhase());
          setStroke(basicStroke2);
        } catch (NoninvertibleTransformException noninvertibleTransformException) {}
      } 
      super.draw(paramShape);
      if (basicStroke2 != null)
        setStroke(basicStroke1); 
    } else {
      super.draw(paramShape);
    } 
  }
  
  public void drawString(String paramString, int paramInt1, int paramInt2) { drawString(paramString, paramInt1, paramInt2); }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2) { drawString(paramString, paramFloat1, paramFloat2, getFont(), getFontRenderContext(), 0.0F); }
  
  protected int platformFontCount(Font paramFont, String paramString) {
    AffineTransform affineTransform1 = getTransform();
    AffineTransform affineTransform2 = new AffineTransform(affineTransform1);
    affineTransform2.concatenate(getFont().getTransform());
    int i = affineTransform2.getType();
    boolean bool = (i != 32 && (i & 0x40) == 0) ? 1 : 0;
    if (!bool)
      return 0; 
    Font2D font2D = FontUtilities.getFont2D(paramFont);
    return (font2D instanceof CompositeFont || font2D instanceof TrueTypeFont) ? 1 : 0;
  }
  
  private static boolean isXP() {
    String str = System.getProperty("os.version");
    if (str != null) {
      Float float = Float.valueOf(str);
      return (float.floatValue() >= 5.1F);
    } 
    return false;
  }
  
  private boolean strNeedsTextLayout(String paramString, Font paramFont) {
    char[] arrayOfChar = paramString.toCharArray();
    boolean bool = FontUtilities.isComplexText(arrayOfChar, 0, arrayOfChar.length);
    return !bool ? false : (!useGDITextLayout ? true : (!(preferGDITextLayout || (isXP() && FontUtilities.textLayoutIsCompatible(paramFont)))));
  }
  
  private int getAngle(Point2D.Double paramDouble) {
    double d = Math.toDegrees(Math.atan2(paramDouble.y, paramDouble.x));
    if (d < 0.0D)
      d += 360.0D; 
    if (d != 0.0D)
      d = 360.0D - d; 
    return (int)Math.round(d * 10.0D);
  }
  
  private float getAwScale(double paramDouble1, double paramDouble2) {
    float f = (float)(paramDouble1 / paramDouble2);
    if (f > 0.999F && f < 1.001F)
      f = 1.0F; 
    return f;
  }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3) {
    if (paramString.length() == 0)
      return; 
    if (WPrinterJob.shapeTextProp) {
      super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
      return;
    } 
    boolean bool = strNeedsTextLayout(paramString, paramFont);
    if ((paramFont.hasLayoutAttributes() || bool) && !this.printingGlyphVector) {
      TextLayout textLayout = new TextLayout(paramString, paramFont, paramFontRenderContext);
      textLayout.draw(this, paramFloat1, paramFloat2);
      return;
    } 
    if (bool) {
      super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
      return;
    } 
    AffineTransform affineTransform1 = getTransform();
    AffineTransform affineTransform2 = new AffineTransform(affineTransform1);
    affineTransform2.concatenate(paramFont.getTransform());
    int i = affineTransform2.getType();
    boolean bool1 = (i != 32 && (i & 0x40) == 0) ? 1 : 0;
    WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
    try {
      wPrinterJob.setTextColor((Color)getPaint());
    } catch (ClassCastException classCastException) {
      bool1 = false;
    } 
    if (!bool1) {
      super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
      return;
    } 
    Point2D.Float float1 = new Point2D.Float(paramFloat1, paramFloat2);
    Point2D.Float float2 = new Point2D.Float();
    if (paramFont.isTransformed()) {
      AffineTransform affineTransform = paramFont.getTransform();
      float f4 = (float)affineTransform.getTranslateX();
      float f5 = (float)affineTransform.getTranslateY();
      if (Math.abs(f4) < 1.0E-5D)
        f4 = 0.0F; 
      if (Math.abs(f5) < 1.0E-5D)
        f5 = 0.0F; 
      float1.x += f4;
      float1.y += f5;
    } 
    affineTransform1.transform(float1, float2);
    if (getClip() != null)
      deviceClip(getClip().getPathIterator(affineTransform1)); 
    float f1 = paramFont.getSize2D();
    double d1 = wPrinterJob.getXRes();
    double d2 = wPrinterJob.getYRes();
    double d3 = d2 / 72.0D;
    int j = getPageFormat().getOrientation();
    if (j == 0 || j == 2) {
      double d = d1;
      d1 = d2;
      d2 = d;
    } 
    double d4 = d1 / 72.0D;
    double d5 = d2 / 72.0D;
    affineTransform2.scale(1.0D / d4, 1.0D / d5);
    Point2D.Double double1 = new Point2D.Double(0.0D, 1.0D);
    affineTransform2.deltaTransform(double1, double1);
    double d6 = Math.sqrt(double1.x * double1.x + double1.y * double1.y);
    float f2 = (float)(f1 * d6 * d3);
    Point2D.Double double2 = new Point2D.Double(1.0D, 0.0D);
    affineTransform2.deltaTransform(double2, double2);
    double d7 = Math.sqrt(double2.x * double2.x + double2.y * double2.y);
    float f3 = getAwScale(d7, d6);
    int k = getAngle(double2);
    double2 = new Point2D.Double(1.0D, 0.0D);
    affineTransform1.deltaTransform(double2, double2);
    double d8 = Math.sqrt(double2.x * double2.x + double2.y * double2.y);
    double1 = new Point2D.Double(0.0D, 1.0D);
    affineTransform1.deltaTransform(double1, double1);
    double d9 = Math.sqrt(double1.x * double1.x + double1.y * double1.y);
    Font2D font2D = FontUtilities.getFont2D(paramFont);
    if (font2D instanceof TrueTypeFont) {
      textOut(paramString, paramFont, (TrueTypeFont)font2D, paramFontRenderContext, f2, k, f3, d8, d9, paramFloat1, paramFloat2, float2.x, float2.y, paramFloat3);
    } else if (font2D instanceof CompositeFont) {
      CompositeFont compositeFont = (CompositeFont)font2D;
      float f4 = paramFloat1;
      float f5 = paramFloat2;
      float f6 = float2.x;
      float f7 = float2.y;
      char[] arrayOfChar = paramString.toCharArray();
      int m = arrayOfChar.length;
      int[] arrayOfInt = new int[m];
      compositeFont.getMapper().charsToGlyphs(m, arrayOfChar, arrayOfInt);
      byte b1 = 0;
      byte b2 = 0;
      int n = 0;
      while (b2 < m) {
        b1 = b2;
        n = arrayOfInt[b1] >>> 24;
        while (b2 < m && arrayOfInt[b2] >>> 24 == n)
          b2++; 
        String str = new String(arrayOfChar, b1, b2 - b1);
        PhysicalFont physicalFont = compositeFont.getSlotFont(n);
        textOut(str, paramFont, physicalFont, paramFontRenderContext, f2, k, f3, d8, d9, f4, f5, f6, f7, 0.0F);
        Rectangle2D rectangle2D = paramFont.getStringBounds(str, paramFontRenderContext);
        float f = (float)rectangle2D.getWidth();
        f4 += f;
        float1.x += f;
        affineTransform1.transform(float1, float2);
        f6 = float2.x;
        f7 = float2.y;
      } 
    } else {
      super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
    } 
  }
  
  protected boolean printGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {
    if ((paramGlyphVector.getLayoutFlags() & true) != 0)
      return false; 
    if (paramGlyphVector.getNumGlyphs() == 0)
      return true; 
    AffineTransform affineTransform1 = getTransform();
    AffineTransform affineTransform2 = new AffineTransform(affineTransform1);
    Font font = paramGlyphVector.getFont();
    affineTransform2.concatenate(font.getTransform());
    int i = affineTransform2.getType();
    boolean bool = (i != 32 && (i & 0x40) == 0) ? 1 : 0;
    WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
    try {
      wPrinterJob.setTextColor((Color)getPaint());
    } catch (ClassCastException classCastException) {
      bool = false;
    } 
    if (WPrinterJob.shapeTextProp || !bool)
      return false; 
    Point2D.Float float1 = new Point2D.Float(paramFloat1, paramFloat2);
    Point2D point2D = paramGlyphVector.getGlyphPosition(0);
    float1.x += (float)point2D.getX();
    float1.y += (float)point2D.getY();
    Point2D.Float float2 = new Point2D.Float();
    if (font.isTransformed()) {
      AffineTransform affineTransform = font.getTransform();
      float f4 = (float)affineTransform.getTranslateX();
      float f5 = (float)affineTransform.getTranslateY();
      if (Math.abs(f4) < 1.0E-5D)
        f4 = 0.0F; 
      if (Math.abs(f5) < 1.0E-5D)
        f5 = 0.0F; 
      float1.x += f4;
      float1.y += f5;
    } 
    affineTransform1.transform(float1, float2);
    if (getClip() != null)
      deviceClip(getClip().getPathIterator(affineTransform1)); 
    float f1 = font.getSize2D();
    double d1 = wPrinterJob.getXRes();
    double d2 = wPrinterJob.getYRes();
    double d3 = d2 / 72.0D;
    int j = getPageFormat().getOrientation();
    if (j == 0 || j == 2) {
      double d = d1;
      d1 = d2;
      d2 = d;
    } 
    double d4 = d1 / 72.0D;
    double d5 = d2 / 72.0D;
    affineTransform2.scale(1.0D / d4, 1.0D / d5);
    Point2D.Double double1 = new Point2D.Double(0.0D, 1.0D);
    affineTransform2.deltaTransform(double1, double1);
    double d6 = Math.sqrt(double1.x * double1.x + double1.y * double1.y);
    float f2 = (float)(f1 * d6 * d3);
    Point2D.Double double2 = new Point2D.Double(1.0D, 0.0D);
    affineTransform2.deltaTransform(double2, double2);
    double d7 = Math.sqrt(double2.x * double2.x + double2.y * double2.y);
    float f3 = getAwScale(d7, d6);
    int k = getAngle(double2);
    double2 = new Point2D.Double(1.0D, 0.0D);
    affineTransform1.deltaTransform(double2, double2);
    double d8 = Math.sqrt(double2.x * double2.x + double2.y * double2.y);
    double1 = new Point2D.Double(0.0D, 1.0D);
    affineTransform1.deltaTransform(double1, double1);
    double d9 = Math.sqrt(double1.x * double1.x + double1.y * double1.y);
    int m = paramGlyphVector.getNumGlyphs();
    int[] arrayOfInt = paramGlyphVector.getGlyphCodes(0, m, null);
    float[] arrayOfFloat1 = paramGlyphVector.getGlyphPositions(0, m, null);
    int n = 0;
    int i1;
    for (i1 = 0; i1 < m; i1++) {
      if ((arrayOfInt[i1] & 0xFFFF) >= 65534)
        n++; 
    } 
    if (n > 0) {
      i1 = m - n;
      int[] arrayOfInt1 = new int[i1];
      float[] arrayOfFloat = new float[i1 * 2];
      byte b1 = 0;
      for (byte b2 = 0; b2 < m; b2++) {
        if ((arrayOfInt[b2] & 0xFFFF) < 65534) {
          arrayOfInt1[b1] = arrayOfInt[b2];
          arrayOfFloat[b1 * 2] = arrayOfFloat1[b2 * 2];
          arrayOfFloat[b1 * 2 + 1] = arrayOfFloat1[b2 * 2 + 1];
          b1++;
        } 
      } 
      m = i1;
      arrayOfInt = arrayOfInt1;
      arrayOfFloat1 = arrayOfFloat;
    } 
    AffineTransform affineTransform3 = AffineTransform.getScaleInstance(d8, d9);
    float[] arrayOfFloat2 = new float[arrayOfFloat1.length];
    affineTransform3.transform(arrayOfFloat1, 0, arrayOfFloat2, 0, arrayOfFloat1.length / 2);
    Font2D font2D = FontUtilities.getFont2D(font);
    if (font2D instanceof TrueTypeFont) {
      String str = font2D.getFamilyName(null);
      int i2 = font.getStyle() | font2D.getStyle();
      if (!wPrinterJob.setFont(str, f2, i2, k, f3))
        return false; 
      wPrinterJob.glyphsOut(arrayOfInt, float2.x, float2.y, arrayOfFloat2);
    } else if (font2D instanceof CompositeFont) {
      CompositeFont compositeFont = (CompositeFont)font2D;
      float f4 = paramFloat1;
      float f5 = paramFloat2;
      float f6 = float2.x;
      float f7 = float2.y;
      byte b1 = 0;
      byte b2 = 0;
      int i2 = 0;
      while (b2 < m) {
        b1 = b2;
        i2 = arrayOfInt[b1] >>> 24;
        while (b2 < m && arrayOfInt[b2] >>> 24 == i2)
          b2++; 
        PhysicalFont physicalFont = compositeFont.getSlotFont(i2);
        if (!(physicalFont instanceof TrueTypeFont))
          return false; 
        String str = physicalFont.getFamilyName(null);
        int i3 = font.getStyle() | physicalFont.getStyle();
        if (!wPrinterJob.setFont(str, f2, i3, k, f3))
          return false; 
        int[] arrayOfInt1 = Arrays.copyOfRange(arrayOfInt, b1, b2);
        float[] arrayOfFloat = Arrays.copyOfRange(arrayOfFloat2, b1 * 2, b2 * 2);
        if (b1 != 0) {
          Point2D.Float float = new Point2D.Float(paramFloat1 + arrayOfFloat1[b1 * 2], paramFloat2 + arrayOfFloat1[b1 * 2 + 1]);
          affineTransform1.transform(float, float);
          f6 = float.x;
          f7 = float.y;
        } 
        wPrinterJob.glyphsOut(arrayOfInt1, f6, f7, arrayOfFloat);
      } 
    } else {
      return false;
    } 
    return true;
  }
  
  private void textOut(String paramString, Font paramFont, PhysicalFont paramPhysicalFont, FontRenderContext paramFontRenderContext, float paramFloat1, int paramInt, float paramFloat2, double paramDouble1, double paramDouble2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7) {
    String str = paramPhysicalFont.getFamilyName(null);
    int i = paramFont.getStyle() | paramPhysicalFont.getStyle();
    WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
    boolean bool = wPrinterJob.setFont(str, paramFloat1, i, paramInt, paramFloat2);
    if (!bool) {
      super.drawString(paramString, paramFloat3, paramFloat4, paramFont, paramFontRenderContext, paramFloat7);
      return;
    } 
    float[] arrayOfFloat = null;
    if (!okGDIMetrics(paramString, paramFont, paramFontRenderContext, paramDouble1)) {
      paramString = wPrinterJob.removeControlChars(paramString);
      char[] arrayOfChar = paramString.toCharArray();
      int j = arrayOfChar.length;
      GlyphVector glyphVector = null;
      if (!FontUtilities.isComplexText(arrayOfChar, 0, j))
        glyphVector = paramFont.createGlyphVector(paramFontRenderContext, paramString); 
      if (glyphVector == null) {
        super.drawString(paramString, paramFloat3, paramFloat4, paramFont, paramFontRenderContext, paramFloat7);
        return;
      } 
      arrayOfFloat = glyphVector.getGlyphPositions(0, j, null);
      Point2D point2D = glyphVector.getGlyphPosition(glyphVector.getNumGlyphs());
      AffineTransform affineTransform = AffineTransform.getScaleInstance(paramDouble1, paramDouble2);
      float[] arrayOfFloat1 = new float[arrayOfFloat.length];
      affineTransform.transform(arrayOfFloat, 0, arrayOfFloat1, 0, arrayOfFloat.length / 2);
      arrayOfFloat = arrayOfFloat1;
    } 
    wPrinterJob.textOut(paramString, paramFloat5, paramFloat6, arrayOfFloat);
  }
  
  private boolean okGDIMetrics(String paramString, Font paramFont, FontRenderContext paramFontRenderContext, double paramDouble) {
    Rectangle2D rectangle2D = paramFont.getStringBounds(paramString, paramFontRenderContext);
    double d = rectangle2D.getWidth();
    d = Math.round(d * paramDouble);
    int i = ((WPrinterJob)getPrinterJob()).getGDIAdvance(paramString);
    if (d > 0.0D && i > 0) {
      double d1 = Math.abs(i - d);
      double d2 = i / d;
      if (d2 < 1.0D)
        d2 = 1.0D / d2; 
      return (d1 <= 1.0D || d2 < 1.01D);
    } 
    return true;
  }
  
  protected boolean drawImageToPlatform(Image paramImage, AffineTransform paramAffineTransform, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    BufferedImage bufferedImage = getBufferedImage(paramImage);
    if (bufferedImage == null)
      return true; 
    WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
    AffineTransform affineTransform = getTransform();
    if (paramAffineTransform == null)
      paramAffineTransform = new AffineTransform(); 
    affineTransform.concatenate(paramAffineTransform);
    double[] arrayOfDouble = new double[6];
    affineTransform.getMatrix(arrayOfDouble);
    Point2D.Float float1 = new Point2D.Float(1.0F, 0.0F);
    Point2D.Float float2 = new Point2D.Float(0.0F, 1.0F);
    affineTransform.deltaTransform(float1, float1);
    affineTransform.deltaTransform(float2, float2);
    Point2D.Float float3 = new Point2D.Float(0.0F, 0.0F);
    double d1 = float1.distance(float3);
    double d2 = float2.distance(float3);
    double d3 = wPrinterJob.getXRes();
    double d4 = wPrinterJob.getYRes();
    double d5 = d3 / 72.0D;
    double d6 = d4 / 72.0D;
    int i = affineTransform.getType();
    boolean bool = ((i & 0x30) != 0) ? 1 : 0;
    if (bool) {
      if (d1 > d5)
        d1 = d5; 
      if (d2 > d6)
        d2 = d6; 
    } 
    if (d1 != 0.0D && d2 != 0.0D) {
      AffineTransform affineTransform1 = new AffineTransform(arrayOfDouble[0] / d1, arrayOfDouble[1] / d2, arrayOfDouble[2] / d1, arrayOfDouble[3] / d2, arrayOfDouble[4] / d1, arrayOfDouble[5] / d2);
      Rectangle2D.Float float = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
      Shape shape = affineTransform1.createTransformedShape(float);
      Rectangle2D rectangle2D = shape.getBounds2D();
      rectangle2D.setRect(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth() + 0.001D, rectangle2D.getHeight() + 0.001D);
      int j = (int)rectangle2D.getWidth();
      int k = (int)rectangle2D.getHeight();
      if (j > 0 && k > 0) {
        byte[] arrayOfByte;
        boolean bool1 = true;
        if (!paramBoolean && hasTransparentPixels(bufferedImage)) {
          bool1 = false;
          if (isBitmaskTransparency(bufferedImage))
            if (paramColor == null) {
              if (drawBitmaskImage(bufferedImage, paramAffineTransform, paramColor, paramInt1, paramInt2, paramInt3, paramInt4))
                return true; 
            } else if (paramColor.getTransparency() == 1) {
              bool1 = true;
            }  
          if (!canDoRedraws())
            bool1 = true; 
        } else {
          paramColor = null;
        } 
        if ((paramInt1 + paramInt3 > bufferedImage.getWidth(null) || paramInt2 + paramInt4 > bufferedImage.getHeight(null)) && canDoRedraws())
          bool1 = false; 
        if (!bool1) {
          affineTransform.getMatrix(arrayOfDouble);
          AffineTransform affineTransform2 = new AffineTransform(arrayOfDouble[0] / d5, arrayOfDouble[1] / d6, arrayOfDouble[2] / d5, arrayOfDouble[3] / d6, arrayOfDouble[4] / d5, arrayOfDouble[5] / d6);
          Rectangle2D.Float float5 = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
          Shape shape2 = affineTransform.createTransformedShape(float5);
          Rectangle2D rectangle2D1 = shape2.getBounds2D();
          rectangle2D1.setRect(rectangle2D1.getX(), rectangle2D1.getY(), rectangle2D1.getWidth() + 0.001D, rectangle2D1.getHeight() + 0.001D);
          int i4 = (int)rectangle2D1.getWidth();
          int i5 = (int)rectangle2D1.getHeight();
          int i6 = i4 * i5 * 3;
          int i7 = 8388608;
          double d7 = (d3 < d4) ? d3 : d4;
          int i8 = (int)d7;
          double d8 = 1.0D;
          double d9 = i4 / j;
          double d10 = i5 / k;
          double d11 = (d9 > d10) ? d10 : d9;
          int i9 = (int)(i8 / d11);
          if (i9 < 72)
            i9 = 72; 
          while (i6 > i7 && i8 > i9) {
            d8 *= 2.0D;
            i8 /= 2;
            i6 /= 4;
          } 
          if (i8 < i9)
            d8 = d7 / i9; 
          rectangle2D1.setRect(rectangle2D1.getX() / d8, rectangle2D1.getY() / d8, rectangle2D1.getWidth() / d8, rectangle2D1.getHeight() / d8);
          wPrinterJob.saveState(getTransform(), getClip(), rectangle2D1, d8, d8);
          return true;
        } 
        int m = 5;
        IndexColorModel indexColorModel = null;
        ColorModel colorModel = bufferedImage.getColorModel();
        int n = bufferedImage.getType();
        if (colorModel instanceof IndexColorModel && colorModel.getPixelSize() <= 8 && (n == 12 || n == 13)) {
          indexColorModel = (IndexColorModel)colorModel;
          m = n;
          if (n == 12 && colorModel.getPixelSize() == 2) {
            int[] arrayOfInt = new int[16];
            indexColorModel.getRGBs(arrayOfInt);
            boolean bool3 = (indexColorModel.getTransparency() != 1);
            int i4 = indexColorModel.getTransparentPixel();
            indexColorModel = new IndexColorModel(4, 16, arrayOfInt, 0, bool3, i4, 0);
          } 
        } 
        int i1 = (int)rectangle2D.getWidth();
        int i2 = (int)rectangle2D.getHeight();
        BufferedImage bufferedImage1 = null;
        boolean bool2 = true;
        if (bool2) {
          if (indexColorModel == null) {
            bufferedImage1 = new BufferedImage(i1, i2, m);
          } else {
            bufferedImage1 = new BufferedImage(i1, i2, m, indexColorModel);
          } 
          Graphics2D graphics2D = bufferedImage1.createGraphics();
          graphics2D.clipRect(0, 0, bufferedImage1.getWidth(), bufferedImage1.getHeight());
          graphics2D.translate(-rectangle2D.getX(), -rectangle2D.getY());
          graphics2D.transform(affineTransform1);
          if (paramColor == null)
            paramColor = Color.white; 
          graphics2D.drawImage(bufferedImage, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramColor, null);
          graphics2D.dispose();
        } else {
          bufferedImage1 = bufferedImage;
        } 
        Rectangle2D.Float float4 = new Rectangle2D.Float((float)(rectangle2D.getX() * d1), (float)(rectangle2D.getY() * d2), (float)(rectangle2D.getWidth() * d1), (float)(rectangle2D.getHeight() * d2));
        WritableRaster writableRaster = bufferedImage1.getRaster();
        if (writableRaster instanceof ByteComponentRaster) {
          arrayOfByte = ((ByteComponentRaster)writableRaster).getDataStorage();
        } else if (writableRaster instanceof BytePackedRaster) {
          arrayOfByte = ((BytePackedRaster)writableRaster).getDataStorage();
        } else {
          return false;
        } 
        int i3 = 24;
        SampleModel sampleModel = bufferedImage1.getSampleModel();
        if (sampleModel instanceof ComponentSampleModel) {
          ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
          i3 = componentSampleModel.getPixelStride() * 8;
        } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
          MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
          i3 = multiPixelPackedSampleModel.getPixelBitStride();
        } else if (indexColorModel != null) {
          int i4 = bufferedImage1.getWidth();
          int i5 = bufferedImage1.getHeight();
          if (i4 > 0 && i5 > 0)
            i3 = arrayOfByte.length * 8 / i4 / i5; 
        } 
        Shape shape1 = getClip();
        clip(paramAffineTransform.createTransformedShape(float));
        deviceClip(getClip().getPathIterator(getTransform()));
        wPrinterJob.drawDIBImage(arrayOfByte, float4.x, float4.y, (float)Math.rint(float4.width + 0.5D), (float)Math.rint(float4.height + 0.5D), 0.0F, 0.0F, bufferedImage1.getWidth(), bufferedImage1.getHeight(), i3, indexColorModel);
        setClip(shape1);
      } 
    } 
    return true;
  }
  
  public void redrawRegion(Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2, Shape paramShape, AffineTransform paramAffineTransform) throws PrinterException {
    WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
    Printable printable = getPrintable();
    PageFormat pageFormat = getPageFormat();
    int i = getPageIndex();
    BufferedImage bufferedImage = new BufferedImage((int)paramRectangle2D.getWidth(), (int)paramRectangle2D.getHeight(), 5);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    ProxyGraphics2D proxyGraphics2D = new ProxyGraphics2D(graphics2D, wPrinterJob);
    proxyGraphics2D.setColor(Color.white);
    proxyGraphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    proxyGraphics2D.clipRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    proxyGraphics2D.translate(-paramRectangle2D.getX(), -paramRectangle2D.getY());
    float f1 = (float)(wPrinterJob.getXRes() / paramDouble1);
    float f2 = (float)(wPrinterJob.getYRes() / paramDouble2);
    proxyGraphics2D.scale((f1 / 72.0F), (f2 / 72.0F));
    proxyGraphics2D.translate(-wPrinterJob.getPhysicalPrintableX(pageFormat.getPaper()) / wPrinterJob.getXRes() * 72.0D, -wPrinterJob.getPhysicalPrintableY(pageFormat.getPaper()) / wPrinterJob.getYRes() * 72.0D);
    proxyGraphics2D.transform(new AffineTransform(getPageFormat().getMatrix()));
    proxyGraphics2D.setPaint(Color.black);
    printable.print(proxyGraphics2D, pageFormat, i);
    graphics2D.dispose();
    if (paramShape != null)
      deviceClip(paramShape.getPathIterator(paramAffineTransform)); 
    Rectangle2D.Float float = new Rectangle2D.Float((float)(paramRectangle2D.getX() * paramDouble1), (float)(paramRectangle2D.getY() * paramDouble2), (float)(paramRectangle2D.getWidth() * paramDouble1), (float)(paramRectangle2D.getHeight() * paramDouble2));
    ByteComponentRaster byteComponentRaster = (ByteComponentRaster)bufferedImage.getRaster();
    wPrinterJob.drawImage3ByteBGR(byteComponentRaster.getDataStorage(), float.x, float.y, float.width, float.height, 0.0F, 0.0F, bufferedImage.getWidth(), bufferedImage.getHeight());
  }
  
  protected void deviceFill(PathIterator paramPathIterator, Color paramColor) {
    WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
    convertToWPath(paramPathIterator);
    wPrinterJob.selectSolidBrush(paramColor);
    wPrinterJob.fillPath();
  }
  
  protected void deviceClip(PathIterator paramPathIterator) {
    WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
    convertToWPath(paramPathIterator);
    wPrinterJob.selectClipPath();
  }
  
  protected void deviceFrameRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) {
    AffineTransform affineTransform = getTransform();
    int i = affineTransform.getType();
    boolean bool = ((i & 0x30) != 0) ? 1 : 0;
    if (bool) {
      draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
      return;
    } 
    Stroke stroke = getStroke();
    if (stroke instanceof BasicStroke) {
      BasicStroke basicStroke = (BasicStroke)stroke;
      int j = basicStroke.getEndCap();
      int k = basicStroke.getLineJoin();
      if (j == 2 && k == 0 && basicStroke.getMiterLimit() == 10.0F) {
        float f1 = basicStroke.getLineWidth();
        Point2D.Float float1 = new Point2D.Float(f1, f1);
        affineTransform.deltaTransform(float1, float1);
        float f2 = Math.min(Math.abs(float1.x), Math.abs(float1.y));
        Point2D.Float float2 = new Point2D.Float(paramInt1, paramInt2);
        affineTransform.transform(float2, float2);
        Point2D.Float float3 = new Point2D.Float((paramInt1 + paramInt3), (paramInt2 + paramInt4));
        affineTransform.transform(float3, float3);
        float f3 = (float)(float3.getX() - float2.getX());
        float f4 = (float)(float3.getY() - float2.getY());
        WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
        if (wPrinterJob.selectStylePen(j, k, f2, paramColor) == true) {
          wPrinterJob.frameRect((float)float2.getX(), (float)float2.getY(), f3, f4);
        } else {
          double d = Math.min(wPrinterJob.getXRes(), wPrinterJob.getYRes());
          if (f2 / d < 0.014000000432133675D) {
            wPrinterJob.selectPen(f2, paramColor);
            wPrinterJob.frameRect((float)float2.getX(), (float)float2.getY(), f3, f4);
          } else {
            draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
          } 
        } 
      } else {
        draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
      } 
    } 
  }
  
  protected void deviceFillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) {
    AffineTransform affineTransform = getTransform();
    int i = affineTransform.getType();
    boolean bool = ((i & 0x30) != 0) ? 1 : 0;
    if (bool) {
      fill(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
      return;
    } 
    Point2D.Float float1 = new Point2D.Float(paramInt1, paramInt2);
    affineTransform.transform(float1, float1);
    Point2D.Float float2 = new Point2D.Float((paramInt1 + paramInt3), (paramInt2 + paramInt4));
    affineTransform.transform(float2, float2);
    float f1 = (float)(float2.getX() - float1.getX());
    float f2 = (float)(float2.getY() - float1.getY());
    WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
    wPrinterJob.fillRect((float)float1.getX(), (float)float1.getY(), f1, f2, paramColor);
  }
  
  protected void deviceDrawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) {
    Stroke stroke = getStroke();
    if (stroke instanceof BasicStroke) {
      BasicStroke basicStroke = (BasicStroke)stroke;
      if (basicStroke.getDashArray() != null) {
        draw(new Line2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
        return;
      } 
      float f1 = basicStroke.getLineWidth();
      Point2D.Float float1 = new Point2D.Float(f1, f1);
      AffineTransform affineTransform = getTransform();
      affineTransform.deltaTransform(float1, float1);
      float f2 = Math.min(Math.abs(float1.x), Math.abs(float1.y));
      Point2D.Float float2 = new Point2D.Float(paramInt1, paramInt2);
      affineTransform.transform(float2, float2);
      Point2D.Float float3 = new Point2D.Float(paramInt3, paramInt4);
      affineTransform.transform(float3, float3);
      int i = basicStroke.getEndCap();
      int j = basicStroke.getLineJoin();
      if (float3.getX() == float2.getX() && float3.getY() == float2.getY())
        i = 1; 
      WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
      if (wPrinterJob.selectStylePen(i, j, f2, paramColor)) {
        wPrinterJob.moveTo((float)float2.getX(), (float)float2.getY());
        wPrinterJob.lineTo((float)float3.getX(), (float)float3.getY());
      } else {
        double d = Math.min(wPrinterJob.getXRes(), wPrinterJob.getYRes());
        if (i == 1 || ((paramInt1 == paramInt3 || paramInt2 == paramInt4) && f2 / d < 0.014000000432133675D)) {
          wPrinterJob.selectPen(f2, paramColor);
          wPrinterJob.moveTo((float)float2.getX(), (float)float2.getY());
          wPrinterJob.lineTo((float)float3.getX(), (float)float3.getY());
        } else {
          draw(new Line2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
        } 
      } 
    } 
  }
  
  private void convertToWPath(PathIterator paramPathIterator) {
    byte b;
    float[] arrayOfFloat = new float[6];
    WPrinterJob wPrinterJob = (WPrinterJob)getPrinterJob();
    if (paramPathIterator.getWindingRule() == 0) {
      b = 1;
    } else {
      b = 2;
    } 
    wPrinterJob.setPolyFillMode(b);
    wPrinterJob.beginPath();
    while (!paramPathIterator.isDone()) {
      float f4;
      float f3;
      float f2;
      float f1;
      int k;
      int j;
      int i = paramPathIterator.currentSegment(arrayOfFloat);
      switch (i) {
        case 0:
          wPrinterJob.moveTo(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 1:
          wPrinterJob.lineTo(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 2:
          j = wPrinterJob.getPenX();
          k = wPrinterJob.getPenY();
          f1 = j + (arrayOfFloat[0] - j) * 2.0F / 3.0F;
          f2 = k + (arrayOfFloat[1] - k) * 2.0F / 3.0F;
          f3 = arrayOfFloat[2] - (arrayOfFloat[2] - arrayOfFloat[0]) * 2.0F / 3.0F;
          f4 = arrayOfFloat[3] - (arrayOfFloat[3] - arrayOfFloat[1]) * 2.0F / 3.0F;
          wPrinterJob.polyBezierTo(f1, f2, f3, f4, arrayOfFloat[2], arrayOfFloat[3]);
          break;
        case 3:
          wPrinterJob.polyBezierTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
          break;
        case 4:
          wPrinterJob.closeFigure();
          break;
      } 
      paramPathIterator.next();
    } 
    wPrinterJob.endPath();
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.enableGDITextLayout"));
    if (str != null) {
      useGDITextLayout = Boolean.getBoolean(str);
      if (!useGDITextLayout && str.equalsIgnoreCase("prefer")) {
        useGDITextLayout = true;
        preferGDITextLayout = true;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WPathGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */