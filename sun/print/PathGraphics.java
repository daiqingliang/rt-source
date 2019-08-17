package sun.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.ref.SoftReference;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Map;
import sun.awt.image.SunWritableRaster;
import sun.awt.image.ToolkitImage;
import sun.font.CompositeFont;
import sun.font.Font2D;
import sun.font.Font2DHandle;
import sun.font.FontUtilities;
import sun.font.PhysicalFont;

public abstract class PathGraphics extends ProxyGraphics2D {
  private Printable mPainter;
  
  private PageFormat mPageFormat;
  
  private int mPageIndex;
  
  private boolean mCanRedraw;
  
  protected boolean printingGlyphVector;
  
  protected static SoftReference<Hashtable<Font2DHandle, Object>> fontMapRef = new SoftReference(null);
  
  protected PathGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt, boolean paramBoolean) {
    super(paramGraphics2D, paramPrinterJob);
    this.mPainter = paramPrintable;
    this.mPageFormat = paramPageFormat;
    this.mPageIndex = paramInt;
    this.mCanRedraw = paramBoolean;
  }
  
  protected Printable getPrintable() { return this.mPainter; }
  
  protected PageFormat getPageFormat() { return this.mPageFormat; }
  
  protected int getPageIndex() { return this.mPageIndex; }
  
  public boolean canDoRedraws() { return this.mCanRedraw; }
  
  public abstract void redrawRegion(Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2, Shape paramShape, AffineTransform paramAffineTransform) throws PrinterException;
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Paint paint = getPaint();
    try {
      AffineTransform affineTransform = getTransform();
      if (getClip() != null)
        deviceClip(getClip().getPathIterator(affineTransform)); 
      deviceDrawLine(paramInt1, paramInt2, paramInt3, paramInt4, (Color)paint);
    } catch (ClassCastException classCastException) {
      throw new IllegalArgumentException("Expected a Color instance");
    } 
  }
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Paint paint = getPaint();
    try {
      AffineTransform affineTransform = getTransform();
      if (getClip() != null)
        deviceClip(getClip().getPathIterator(affineTransform)); 
      deviceFrameRect(paramInt1, paramInt2, paramInt3, paramInt4, (Color)paint);
    } catch (ClassCastException classCastException) {
      throw new IllegalArgumentException("Expected a Color instance");
    } 
  }
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Paint paint = getPaint();
    try {
      AffineTransform affineTransform = getTransform();
      if (getClip() != null)
        deviceClip(getClip().getPathIterator(affineTransform)); 
      deviceFillRect(paramInt1, paramInt2, paramInt3, paramInt4, (Color)paint);
    } catch (ClassCastException classCastException) {
      throw new IllegalArgumentException("Expected a Color instance");
    } 
  }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { fill(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4), getBackground()); }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { draw(new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)); }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { fill(new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)); }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { draw(new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { fill(new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { draw(new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0)); }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { fill(new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2)); }
  
  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    if (paramInt > 0) {
      float f1 = paramArrayOfInt1[0];
      float f2 = paramArrayOfInt2[0];
      for (byte b = 1; b < paramInt; b++) {
        float f3 = paramArrayOfInt1[b];
        float f4 = paramArrayOfInt2[b];
        draw(new Line2D.Float(f1, f2, f3, f4));
        f1 = f3;
        f2 = f4;
      } 
    } 
  }
  
  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { draw(new Polygon(paramArrayOfInt1, paramArrayOfInt2, paramInt)); }
  
  public void drawPolygon(Polygon paramPolygon) { draw(paramPolygon); }
  
  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { fill(new Polygon(paramArrayOfInt1, paramArrayOfInt2, paramInt)); }
  
  public void fillPolygon(Polygon paramPolygon) { fill(paramPolygon); }
  
  public void drawString(String paramString, int paramInt1, int paramInt2) { drawString(paramString, paramInt1, paramInt2); }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2) {
    if (paramString.length() == 0)
      return; 
    TextLayout textLayout = new TextLayout(paramString, getFont(), getFontRenderContext());
    textLayout.draw(this, paramFloat1, paramFloat2);
  }
  
  protected void drawString(String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3) {
    TextLayout textLayout = new TextLayout(paramString, paramFont, paramFontRenderContext);
    Shape shape = textLayout.getOutline(AffineTransform.getTranslateInstance(paramFloat1, paramFloat2));
    fill(shape);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2) { drawString(paramAttributedCharacterIterator, paramInt1, paramInt2); }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2) {
    if (paramAttributedCharacterIterator == null)
      throw new NullPointerException("attributedcharacteriterator is null"); 
    TextLayout textLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
    textLayout.draw(this, paramFloat1, paramFloat2);
  }
  
  public void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {
    if (this.printingGlyphVector) {
      assert !this.printingGlyphVector;
      fill(paramGlyphVector.getOutline(paramFloat1, paramFloat2));
      return;
    } 
    try {
      this.printingGlyphVector = true;
      if (RasterPrinterJob.shapeTextProp || !printedSimpleGlyphVector(paramGlyphVector, paramFloat1, paramFloat2))
        fill(paramGlyphVector.getOutline(paramFloat1, paramFloat2)); 
    } finally {
      this.printingGlyphVector = false;
    } 
  }
  
  protected int platformFontCount(Font paramFont, String paramString) { return 0; }
  
  protected boolean printGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) { return false; }
  
  boolean printedSimpleGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {
    Hashtable hashtable;
    int i = paramGlyphVector.getLayoutFlags();
    if (i != 0 && i != 2)
      return printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2); 
    Font font = paramGlyphVector.getFont();
    Font2D font2D = FontUtilities.getFont2D(font);
    if (font2D.handle.font2D != font2D)
      return false; 
    synchronized (PathGraphics.class) {
      hashtable = (Hashtable)fontMapRef.get();
      if (hashtable == null) {
        hashtable = new Hashtable();
        fontMapRef = new SoftReference(hashtable);
      } 
    } 
    int j = paramGlyphVector.getNumGlyphs();
    int[] arrayOfInt1 = paramGlyphVector.getGlyphCodes(0, j, null);
    char[] arrayOfChar1 = null;
    char[][] arrayOfChar = (char[][])null;
    CompositeFont compositeFont = null;
    synchronized (hashtable) {
      if (font2D instanceof CompositeFont) {
        compositeFont = (CompositeFont)font2D;
        int m = compositeFont.getNumSlots();
        arrayOfChar = (char[][])hashtable.get(font2D.handle);
        if (arrayOfChar == null) {
          arrayOfChar = new char[m][];
          hashtable.put(font2D.handle, arrayOfChar);
        } 
        for (byte b = 0; b < j; b++) {
          int n = arrayOfInt1[b] >>> 24;
          if (n >= m)
            return false; 
          if (arrayOfChar[n] == null) {
            PhysicalFont physicalFont = compositeFont.getSlotFont(n);
            char[] arrayOfChar3 = (char[])hashtable.get(physicalFont.handle);
            if (arrayOfChar3 == null)
              arrayOfChar3 = getGlyphToCharMapForFont(physicalFont); 
            arrayOfChar[n] = arrayOfChar3;
          } 
        } 
      } else {
        arrayOfChar1 = (char[])hashtable.get(font2D.handle);
        if (arrayOfChar1 == null) {
          arrayOfChar1 = getGlyphToCharMapForFont(font2D);
          hashtable.put(font2D.handle, arrayOfChar1);
        } 
      } 
    } 
    char[] arrayOfChar2 = new char[j];
    if (compositeFont != null) {
      for (byte b = 0; b < j; b++) {
        char c;
        int m = arrayOfInt1[b];
        char[] arrayOfChar3 = arrayOfChar[m >>> 24];
        m &= 0xFFFFFF;
        if (arrayOfChar3 == null)
          return false; 
        if (m == 65535) {
          c = '\n';
        } else {
          if (m < 0 || m >= arrayOfChar3.length)
            return false; 
          c = arrayOfChar3[m];
        } 
        if (c != Character.MAX_VALUE) {
          arrayOfChar2[b] = c;
        } else {
          return false;
        } 
      } 
    } else {
      for (byte b = 0; b < j; b++) {
        char c;
        int m = arrayOfInt1[b];
        if (m == 65535) {
          c = '\n';
        } else {
          if (m < 0 || m >= arrayOfChar1.length)
            return false; 
          c = arrayOfChar1[m];
        } 
        if (c != Character.MAX_VALUE) {
          arrayOfChar2[b] = c;
        } else {
          return false;
        } 
      } 
    } 
    FontRenderContext fontRenderContext1 = paramGlyphVector.getFontRenderContext();
    GlyphVector glyphVector = font.createGlyphVector(fontRenderContext1, arrayOfChar2);
    if (glyphVector.getNumGlyphs() != j)
      return printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2); 
    int[] arrayOfInt2 = glyphVector.getGlyphCodes(0, j, null);
    for (byte b1 = 0; b1 < j; b1++) {
      if (arrayOfInt1[b1] != arrayOfInt2[b1])
        return printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2); 
    } 
    FontRenderContext fontRenderContext2 = getFontRenderContext();
    boolean bool = fontRenderContext1.equals(fontRenderContext2);
    if (!bool && fontRenderContext1.usesFractionalMetrics() == fontRenderContext2.usesFractionalMetrics()) {
      AffineTransform affineTransform1 = fontRenderContext1.getTransform();
      AffineTransform affineTransform2 = getTransform();
      double[] arrayOfDouble1 = new double[4];
      double[] arrayOfDouble2 = new double[4];
      affineTransform1.getMatrix(arrayOfDouble1);
      affineTransform2.getMatrix(arrayOfDouble2);
      bool = true;
      for (byte b = 0; b < 4; b++) {
        if (arrayOfDouble1[b] != arrayOfDouble2[b]) {
          bool = false;
          break;
        } 
      } 
    } 
    String str = new String(arrayOfChar2, 0, j);
    int k = platformFontCount(font, str);
    if (k == 0)
      return false; 
    float[] arrayOfFloat = paramGlyphVector.getGlyphPositions(0, j, null);
    boolean bool1 = ((i & 0x2) == 0 || samePositions(glyphVector, arrayOfInt2, arrayOfInt1, arrayOfFloat)) ? 1 : 0;
    Point2D point2D = paramGlyphVector.getGlyphPosition(j);
    float f = (float)point2D.getX();
    boolean bool2 = false;
    if (font.hasLayoutAttributes() && this.printingGlyphVector && bool1) {
      Map map = font.getAttributes();
      Object object = map.get(TextAttribute.TRACKING);
      boolean bool3 = (object != null && object instanceof Number && ((Number)object).floatValue() != 0.0F) ? 1 : 0;
      if (bool3) {
        bool1 = false;
      } else {
        Rectangle2D rectangle2D = font.getStringBounds(str, fontRenderContext1);
        float f1 = (float)rectangle2D.getWidth();
        if (Math.abs(f1 - f) > 1.0E-5D)
          bool2 = true; 
      } 
    } 
    if (bool && bool1 && !bool2) {
      drawString(str, paramFloat1, paramFloat2, font, fontRenderContext1, 0.0F);
      return true;
    } 
    if (k == 1 && canDrawStringToWidth() && bool1) {
      drawString(str, paramFloat1, paramFloat2, font, fontRenderContext1, f);
      return true;
    } 
    if (FontUtilities.isComplexText(arrayOfChar2, 0, arrayOfChar2.length))
      return printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2); 
    if (j > 10 && printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2))
      return true; 
    for (byte b2 = 0; b2 < j; b2++) {
      String str1 = new String(arrayOfChar2, b2, 1);
      drawString(str1, paramFloat1 + arrayOfFloat[b2 * 2], paramFloat2 + arrayOfFloat[b2 * 2 + 1], font, fontRenderContext1, 0.0F);
    } 
    return true;
  }
  
  private boolean samePositions(GlyphVector paramGlyphVector, int[] paramArrayOfInt1, int[] paramArrayOfInt2, float[] paramArrayOfFloat) {
    int i = paramGlyphVector.getNumGlyphs();
    float[] arrayOfFloat = paramGlyphVector.getGlyphPositions(0, i, null);
    if (i != paramArrayOfInt1.length || paramArrayOfInt2.length != paramArrayOfInt1.length || paramArrayOfFloat.length != arrayOfFloat.length)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfInt1[b] != paramArrayOfInt2[b] || arrayOfFloat[b] != paramArrayOfFloat[b])
        return false; 
    } 
    return true;
  }
  
  protected boolean canDrawStringToWidth() { return false; }
  
  private static char[] getGlyphToCharMapForFont(Font2D paramFont2D) {
    int i = paramFont2D.getNumGlyphs();
    int j = paramFont2D.getMissingGlyphCode();
    char[] arrayOfChar = new char[i];
    char c;
    for (c = Character.MIN_VALUE; c < i; c++)
      arrayOfChar[c] = Character.MAX_VALUE; 
    for (c = Character.MIN_VALUE; c < '￿'; c = (char)(c + 1)) {
      if (c < '?' || c > '?') {
        int k = paramFont2D.charToGlyph(c);
        if (k != j && k >= 0 && k < i && arrayOfChar[k] == Character.MAX_VALUE)
          arrayOfChar[k] = c; 
      } 
    } 
    return arrayOfChar;
  }
  
  public void draw(Shape paramShape) { fill(getStroke().createStrokedShape(paramShape)); }
  
  public void fill(Shape paramShape) {
    Paint paint = getPaint();
    try {
      fill(paramShape, (Color)paint);
    } catch (ClassCastException classCastException) {
      throw new IllegalArgumentException("Expected a Color instance");
    } 
  }
  
  public void fill(Shape paramShape, Color paramColor) {
    AffineTransform affineTransform = getTransform();
    if (getClip() != null)
      deviceClip(getClip().getPathIterator(affineTransform)); 
    deviceFill(paramShape.getPathIterator(affineTransform), paramColor);
  }
  
  protected abstract void deviceFill(PathIterator paramPathIterator, Color paramColor);
  
  protected abstract void deviceClip(PathIterator paramPathIterator);
  
  protected abstract void deviceFrameRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor);
  
  protected abstract void deviceDrawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor);
  
  protected abstract void deviceFillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor);
  
  protected BufferedImage getBufferedImage(Image paramImage) { return (paramImage instanceof BufferedImage) ? (BufferedImage)paramImage : ((paramImage instanceof ToolkitImage) ? ((ToolkitImage)paramImage).getBufferedImage() : ((paramImage instanceof VolatileImage) ? ((VolatileImage)paramImage).getSnapshot() : null)); }
  
  protected boolean hasTransparentPixels(BufferedImage paramBufferedImage) {
    ColorModel colorModel = paramBufferedImage.getColorModel();
    boolean bool = (colorModel == null) ? true : ((colorModel.getTransparency() != 1));
    if (bool && paramBufferedImage != null && (paramBufferedImage.getType() == 2 || paramBufferedImage.getType() == 3)) {
      DataBuffer dataBuffer = paramBufferedImage.getRaster().getDataBuffer();
      SampleModel sampleModel = paramBufferedImage.getRaster().getSampleModel();
      if (dataBuffer instanceof DataBufferInt && sampleModel instanceof SinglePixelPackedSampleModel) {
        SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
        int[] arrayOfInt = SunWritableRaster.stealData((DataBufferInt)dataBuffer, 0);
        int i = paramBufferedImage.getMinX();
        int j = paramBufferedImage.getMinY();
        int k = paramBufferedImage.getWidth();
        int m = paramBufferedImage.getHeight();
        int n = singlePixelPackedSampleModel.getScanlineStride();
        boolean bool1 = false;
        for (int i1 = j; i1 < j + m; i1++) {
          int i2 = i1 * n;
          for (int i3 = i; i3 < i + k; i3++) {
            if ((arrayOfInt[i2 + i3] & 0xFF000000) != -16777216) {
              bool1 = true;
              break;
            } 
          } 
          if (bool1)
            break; 
        } 
        if (!bool1)
          bool = false; 
      } 
    } 
    return bool;
  }
  
  protected boolean isBitmaskTransparency(BufferedImage paramBufferedImage) {
    ColorModel colorModel = paramBufferedImage.getColorModel();
    return (colorModel != null && colorModel.getTransparency() == 2);
  }
  
  protected boolean drawBitmaskImage(BufferedImage paramBufferedImage, AffineTransform paramAffineTransform, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i1;
    int n;
    int m;
    ColorModel colorModel = paramBufferedImage.getColorModel();
    if (!(colorModel instanceof IndexColorModel))
      return false; 
    IndexColorModel indexColorModel = (IndexColorModel)colorModel;
    if (colorModel.getTransparency() != 2)
      return false; 
    if (paramColor != null && paramColor.getAlpha() < 128)
      return false; 
    if ((paramAffineTransform.getType() & 0xFFFFFFF4) != 0)
      return false; 
    if ((getTransform().getType() & 0xFFFFFFF4) != 0)
      return false; 
    BufferedImage bufferedImage = null;
    WritableRaster writableRaster = paramBufferedImage.getRaster();
    int i = indexColorModel.getTransparentPixel();
    byte[] arrayOfByte = new byte[indexColorModel.getMapSize()];
    indexColorModel.getAlphas(arrayOfByte);
    if (i >= 0)
      arrayOfByte[i] = 0; 
    int j = writableRaster.getWidth();
    int k = writableRaster.getHeight();
    if (paramInt1 > j || paramInt2 > k)
      return false; 
    if (paramInt1 + paramInt3 > j) {
      m = j;
      i1 = m - paramInt1;
    } else {
      m = paramInt1 + paramInt3;
      i1 = paramInt3;
    } 
    if (paramInt2 + paramInt4 > k) {
      n = k;
      int i3 = n - paramInt2;
    } else {
      n = paramInt2 + paramInt4;
      int i3 = paramInt4;
    } 
    int[] arrayOfInt = new int[i1];
    for (int i2 = paramInt2; i2 < n; i2++) {
      int i3 = -1;
      writableRaster.getPixels(paramInt1, i2, i1, 1, arrayOfInt);
      for (int i4 = paramInt1; i4 < m; i4++) {
        if (arrayOfByte[arrayOfInt[i4 - paramInt1]] == 0) {
          if (i3 >= 0) {
            bufferedImage = paramBufferedImage.getSubimage(i3, i2, i4 - i3, 1);
            paramAffineTransform.translate(i3, i2);
            drawImageToPlatform(bufferedImage, paramAffineTransform, paramColor, 0, 0, i4 - i3, 1, true);
            paramAffineTransform.translate(-i3, -i2);
            i3 = -1;
          } 
        } else if (i3 < 0) {
          i3 = i4;
        } 
      } 
      if (i3 >= 0) {
        bufferedImage = paramBufferedImage.getSubimage(i3, i2, m - i3, 1);
        paramAffineTransform.translate(i3, i2);
        drawImageToPlatform(bufferedImage, paramAffineTransform, paramColor, 0, 0, m - i3, 1, true);
        paramAffineTransform.translate(-i3, -i2);
      } 
    } 
    return true;
  }
  
  protected abstract boolean drawImageToPlatform(Image paramImage, AffineTransform paramAffineTransform, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) { return drawImage(paramImage, paramInt1, paramInt2, null, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver) { return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, null, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) {
    boolean bool;
    if (paramImage == null)
      return true; 
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (i < 0 || j < 0) {
      bool = false;
    } else {
      bool = drawImage(paramImage, paramInt1, paramInt2, i, j, paramColor, paramImageObserver);
    } 
    return bool;
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) {
    boolean bool;
    if (paramImage == null)
      return true; 
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (i < 0 || j < 0) {
      bool = false;
    } else {
      bool = drawImage(paramImage, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, 0, 0, i, j, paramImageObserver);
    } 
    return bool;
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver) { return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, null, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) {
    if (paramImage == null)
      return true; 
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (i < 0 || j < 0)
      return true; 
    int k = paramInt7 - paramInt5;
    int m = paramInt8 - paramInt6;
    float f1 = (paramInt3 - paramInt1) / k;
    float f2 = (paramInt4 - paramInt2) / m;
    AffineTransform affineTransform = new AffineTransform(f1, 0.0F, 0.0F, f2, paramInt1 - paramInt5 * f1, paramInt2 - paramInt6 * f2);
    int n = 0;
    if (paramInt7 < paramInt5) {
      n = paramInt5;
      paramInt5 = paramInt7;
      paramInt7 = n;
    } 
    if (paramInt8 < paramInt6) {
      n = paramInt6;
      paramInt6 = paramInt8;
      paramInt8 = n;
    } 
    if (paramInt5 < 0) {
      paramInt5 = 0;
    } else if (paramInt5 > i) {
      paramInt5 = i;
    } 
    if (paramInt7 < 0) {
      paramInt7 = 0;
    } else if (paramInt7 > i) {
      paramInt7 = i;
    } 
    if (paramInt6 < 0) {
      paramInt6 = 0;
    } else if (paramInt6 > j) {
      paramInt6 = j;
    } 
    if (paramInt8 < 0) {
      paramInt8 = 0;
    } else if (paramInt8 > j) {
      paramInt8 = j;
    } 
    k = paramInt7 - paramInt5;
    m = paramInt8 - paramInt6;
    return (k <= 0 || m <= 0) ? true : drawImageToPlatform(paramImage, affineTransform, paramColor, paramInt5, paramInt6, k, m, false);
  }
  
  public boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver) {
    boolean bool;
    if (paramImage == null)
      return true; 
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (i < 0 || j < 0) {
      bool = false;
    } else {
      bool = drawImageToPlatform(paramImage, paramAffineTransform, null, 0, 0, i, j, false);
    } 
    return bool;
  }
  
  public void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2) {
    if (paramBufferedImage == null)
      return; 
    int i = paramBufferedImage.getWidth(null);
    int j = paramBufferedImage.getHeight(null);
    if (paramBufferedImageOp != null)
      paramBufferedImage = paramBufferedImageOp.filter(paramBufferedImage, null); 
    if (i <= 0 || j <= 0)
      return; 
    AffineTransform affineTransform = new AffineTransform(1.0F, 0.0F, 0.0F, 1.0F, paramInt1, paramInt2);
    drawImageToPlatform(paramBufferedImage, affineTransform, null, 0, 0, i, j, false);
  }
  
  public void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform) {
    if (paramRenderedImage == null)
      return; 
    BufferedImage bufferedImage = null;
    int i = paramRenderedImage.getWidth();
    int j = paramRenderedImage.getHeight();
    if (i <= 0 || j <= 0)
      return; 
    if (paramRenderedImage instanceof BufferedImage) {
      bufferedImage = (BufferedImage)paramRenderedImage;
    } else {
      bufferedImage = new BufferedImage(i, j, 2);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      graphics2D.drawRenderedImage(paramRenderedImage, paramAffineTransform);
    } 
    drawImageToPlatform(bufferedImage, paramAffineTransform, null, 0, 0, i, j, false);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PathGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */