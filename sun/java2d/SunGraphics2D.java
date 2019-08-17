package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import sun.awt.ConstrainableGraphics;
import sun.awt.SunHints;
import sun.awt.image.MultiResolutionImage;
import sun.awt.image.MultiResolutionToolkitImage;
import sun.awt.image.SurfaceManager;
import sun.awt.image.ToolkitImage;
import sun.font.FontDesignMetrics;
import sun.font.FontUtilities;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.FontInfo;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderingEngine;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.ShapeSpanIterator;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.ValidatePipe;
import sun.misc.PerformanceLogger;

public final class SunGraphics2D extends Graphics2D implements ConstrainableGraphics, Cloneable, DestSurfaceProvider {
  public static final int PAINT_CUSTOM = 6;
  
  public static final int PAINT_TEXTURE = 5;
  
  public static final int PAINT_RAD_GRADIENT = 4;
  
  public static final int PAINT_LIN_GRADIENT = 3;
  
  public static final int PAINT_GRADIENT = 2;
  
  public static final int PAINT_ALPHACOLOR = 1;
  
  public static final int PAINT_OPAQUECOLOR = 0;
  
  public static final int COMP_CUSTOM = 3;
  
  public static final int COMP_XOR = 2;
  
  public static final int COMP_ALPHA = 1;
  
  public static final int COMP_ISCOPY = 0;
  
  public static final int STROKE_CUSTOM = 3;
  
  public static final int STROKE_WIDE = 2;
  
  public static final int STROKE_THINDASHED = 1;
  
  public static final int STROKE_THIN = 0;
  
  public static final int TRANSFORM_GENERIC = 4;
  
  public static final int TRANSFORM_TRANSLATESCALE = 3;
  
  public static final int TRANSFORM_ANY_TRANSLATE = 2;
  
  public static final int TRANSFORM_INT_TRANSLATE = 1;
  
  public static final int TRANSFORM_ISIDENT = 0;
  
  public static final int CLIP_SHAPE = 2;
  
  public static final int CLIP_RECTANGULAR = 1;
  
  public static final int CLIP_DEVICE = 0;
  
  public int eargb;
  
  public int pixel;
  
  public SurfaceData surfaceData;
  
  public PixelDrawPipe drawpipe;
  
  public PixelFillPipe fillpipe;
  
  public DrawImagePipe imagepipe;
  
  public ShapeDrawPipe shapepipe;
  
  public TextPipe textpipe;
  
  public MaskFill alphafill;
  
  public RenderLoops loops;
  
  public CompositeType imageComp;
  
  public int paintState;
  
  public int compositeState;
  
  public int strokeState;
  
  public int transformState;
  
  public int clipState;
  
  public Color foregroundColor;
  
  public Color backgroundColor;
  
  public AffineTransform transform;
  
  public int transX;
  
  public int transY;
  
  protected static final Stroke defaultStroke = new BasicStroke();
  
  protected static final Composite defaultComposite = AlphaComposite.SrcOver;
  
  private static final Font defaultFont = new Font("Dialog", 0, 12);
  
  public Paint paint;
  
  public Stroke stroke;
  
  public Composite composite;
  
  protected Font font;
  
  protected FontMetrics fontMetrics;
  
  public int renderHint;
  
  public int antialiasHint;
  
  public int textAntialiasHint;
  
  protected int fractionalMetricsHint;
  
  public int lcdTextContrast;
  
  private static int lcdTextContrastDefaultValue = 140;
  
  private int interpolationHint;
  
  public int strokeHint;
  
  public int interpolationType;
  
  public RenderingHints hints;
  
  public Region constrainClip;
  
  public int constrainX;
  
  public int constrainY;
  
  public Region clipRegion;
  
  public Shape usrClip;
  
  protected Region devClip;
  
  private final int devScale;
  
  private int resolutionVariantHint;
  
  private boolean validFontInfo;
  
  private FontInfo fontInfo;
  
  private FontInfo glyphVectorFontInfo;
  
  private FontRenderContext glyphVectorFRC;
  
  private static final int slowTextTransformMask = 120;
  
  protected static ValidatePipe invalidpipe;
  
  private static final double[] IDENT_MATRIX;
  
  private static final AffineTransform IDENT_ATX;
  
  private static final int MINALLOCATED = 8;
  
  private static final int TEXTARRSIZE = 17;
  
  private static double[][] textTxArr;
  
  private static AffineTransform[] textAtArr;
  
  static final int NON_UNIFORM_SCALE_MASK = 36;
  
  public static final double MinPenSizeAA;
  
  public static final double MinPenSizeAASquared;
  
  public static final double MinPenSizeSquared = 1.000000001D;
  
  static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;
  
  Blit lastCAblit;
  
  Composite lastCAcomp;
  
  private FontRenderContext cachedFRC;
  
  public SunGraphics2D(SurfaceData paramSurfaceData, Color paramColor1, Color paramColor2, Font paramFont) {
    this.surfaceData = paramSurfaceData;
    this.foregroundColor = paramColor1;
    this.backgroundColor = paramColor2;
    this.transform = new AffineTransform();
    this.stroke = defaultStroke;
    this.composite = defaultComposite;
    this.paint = this.foregroundColor;
    this.imageComp = CompositeType.SrcOverNoEa;
    this.renderHint = 0;
    this.antialiasHint = 1;
    this.textAntialiasHint = 0;
    this.fractionalMetricsHint = 1;
    this.lcdTextContrast = lcdTextContrastDefaultValue;
    this.interpolationHint = -1;
    this.strokeHint = 0;
    this.resolutionVariantHint = 0;
    this.interpolationType = 1;
    validateColor();
    this.devScale = paramSurfaceData.getDefaultScale();
    if (this.devScale != 1) {
      this.transform.setToScale(this.devScale, this.devScale);
      invalidateTransform();
    } 
    this.font = paramFont;
    if (this.font == null)
      this.font = defaultFont; 
    setDevClip(paramSurfaceData.getBounds());
    invalidatePipe();
  }
  
  protected Object clone() {
    try {
      SunGraphics2D sunGraphics2D = (SunGraphics2D)super.clone();
      sunGraphics2D.transform = new AffineTransform(this.transform);
      if (this.hints != null)
        sunGraphics2D.hints = (RenderingHints)this.hints.clone(); 
      if (this.fontInfo != null)
        if (this.validFontInfo) {
          sunGraphics2D.fontInfo = (FontInfo)this.fontInfo.clone();
        } else {
          sunGraphics2D.fontInfo = null;
        }  
      if (this.glyphVectorFontInfo != null) {
        sunGraphics2D.glyphVectorFontInfo = (FontInfo)this.glyphVectorFontInfo.clone();
        sunGraphics2D.glyphVectorFRC = this.glyphVectorFRC;
      } 
      return sunGraphics2D;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public Graphics create() { return (Graphics)clone(); }
  
  public void setDevClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Region region = this.constrainClip;
    if (region == null) {
      this.devClip = Region.getInstanceXYWH(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      this.devClip = region.getIntersectionXYWH(paramInt1, paramInt2, paramInt3, paramInt4);
    } 
    validateCompClip();
  }
  
  public void setDevClip(Rectangle paramRectangle) { setDevClip(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  public void constrain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Region paramRegion) {
    if ((paramInt1 | paramInt2) != 0)
      translate(paramInt1, paramInt2); 
    if (this.transformState > 3) {
      clipRect(0, 0, paramInt3, paramInt4);
      return;
    } 
    double d1 = this.transform.getScaleX();
    double d2 = this.transform.getScaleY();
    paramInt1 = this.constrainX = (int)this.transform.getTranslateX();
    paramInt2 = this.constrainY = (int)this.transform.getTranslateY();
    paramInt3 = Region.dimAdd(paramInt1, Region.clipScale(paramInt3, d1));
    paramInt4 = Region.dimAdd(paramInt2, Region.clipScale(paramInt4, d2));
    Region region = this.constrainClip;
    if (region == null) {
      region = Region.getInstanceXYXY(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      region = region.getIntersectionXYXY(paramInt1, paramInt2, paramInt3, paramInt4);
    } 
    if (paramRegion != null) {
      paramRegion = paramRegion.getScaledRegion(d1, d2);
      paramRegion = paramRegion.getTranslatedRegion(paramInt1, paramInt2);
      region = region.getIntersection(paramRegion);
    } 
    if (region == this.constrainClip)
      return; 
    this.constrainClip = region;
    if (!this.devClip.isInsideQuickCheck(region)) {
      this.devClip = this.devClip.getIntersection(region);
      validateCompClip();
    } 
  }
  
  public void constrain(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { constrain(paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  protected void invalidatePipe() {
    this.drawpipe = invalidpipe;
    this.fillpipe = invalidpipe;
    this.shapepipe = invalidpipe;
    this.textpipe = invalidpipe;
    this.imagepipe = invalidpipe;
    this.loops = null;
  }
  
  public void validatePipe() {
    if (!this.surfaceData.isValid())
      throw new InvalidPipeException("attempt to validate Pipe with invalid SurfaceData"); 
    this.surfaceData.validatePipe(this);
  }
  
  Shape intersectShapes(Shape paramShape1, Shape paramShape2, boolean paramBoolean1, boolean paramBoolean2) { return (paramShape1 instanceof Rectangle && paramShape2 instanceof Rectangle) ? ((Rectangle)paramShape1).intersection((Rectangle)paramShape2) : ((paramShape1 instanceof Rectangle2D) ? intersectRectShape((Rectangle2D)paramShape1, paramShape2, paramBoolean1, paramBoolean2) : ((paramShape2 instanceof Rectangle2D) ? intersectRectShape((Rectangle2D)paramShape2, paramShape1, paramBoolean2, paramBoolean1) : intersectByArea(paramShape1, paramShape2, paramBoolean1, paramBoolean2))); }
  
  Shape intersectRectShape(Rectangle2D paramRectangle2D, Shape paramShape, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramShape instanceof Rectangle2D) {
      Rectangle2D.Float float;
      Rectangle2D rectangle2D = (Rectangle2D)paramShape;
      if (!paramBoolean1) {
        float = paramRectangle2D;
      } else if (!paramBoolean2) {
        float = rectangle2D;
      } else {
        float = new Rectangle2D.Float();
      } 
      double d1 = Math.max(paramRectangle2D.getX(), rectangle2D.getX());
      double d2 = Math.min(paramRectangle2D.getX() + paramRectangle2D.getWidth(), rectangle2D.getX() + rectangle2D.getWidth());
      double d3 = Math.max(paramRectangle2D.getY(), rectangle2D.getY());
      double d4 = Math.min(paramRectangle2D.getY() + paramRectangle2D.getHeight(), rectangle2D.getY() + rectangle2D.getHeight());
      if (d2 - d1 < 0.0D || d4 - d3 < 0.0D) {
        float.setFrameFromDiagonal(0.0D, 0.0D, 0.0D, 0.0D);
      } else {
        float.setFrameFromDiagonal(d1, d3, d2, d4);
      } 
      return float;
    } 
    if (paramRectangle2D.contains(paramShape.getBounds2D())) {
      if (paramBoolean2)
        paramShape = cloneShape(paramShape); 
      return paramShape;
    } 
    return intersectByArea(paramRectangle2D, paramShape, paramBoolean1, paramBoolean2);
  }
  
  protected static Shape cloneShape(Shape paramShape) { return new GeneralPath(paramShape); }
  
  Shape intersectByArea(Shape paramShape1, Shape paramShape2, boolean paramBoolean1, boolean paramBoolean2) {
    Area area2;
    Area area1;
    if (!paramBoolean1 && paramShape1 instanceof Area) {
      area1 = (Area)paramShape1;
    } else if (!paramBoolean2 && paramShape2 instanceof Area) {
      area1 = (Area)paramShape2;
      paramShape2 = paramShape1;
    } else {
      area1 = new Area(paramShape1);
    } 
    if (paramShape2 instanceof Area) {
      area2 = (Area)paramShape2;
    } else {
      area2 = new Area(paramShape2);
    } 
    area1.intersect(area2);
    return area1.isRectangular() ? area1.getBounds() : area1;
  }
  
  public Region getCompClip() {
    if (!this.surfaceData.isValid())
      revalidateAll(); 
    return this.clipRegion;
  }
  
  public Font getFont() {
    if (this.font == null)
      this.font = defaultFont; 
    return this.font;
  }
  
  public FontInfo checkFontInfo(FontInfo paramFontInfo, Font paramFont, FontRenderContext paramFontRenderContext) {
    int j;
    AffineTransform affineTransform1;
    if (paramFontInfo == null)
      paramFontInfo = new FontInfo(); 
    float f = paramFont.getSize2D();
    AffineTransform affineTransform2 = null;
    if (paramFont.isTransformed()) {
      affineTransform2 = paramFont.getTransform();
      affineTransform2.scale(f, f);
      int k = affineTransform2.getType();
      paramFontInfo.originX = (float)affineTransform2.getTranslateX();
      paramFontInfo.originY = (float)affineTransform2.getTranslateY();
      affineTransform2.translate(-paramFontInfo.originX, -paramFontInfo.originY);
      if (this.transformState >= 3) {
        this.transform.getMatrix(paramFontInfo.devTx = new double[4]);
        affineTransform1 = new AffineTransform(paramFontInfo.devTx);
        affineTransform2.preConcatenate(affineTransform1);
      } else {
        paramFontInfo.devTx = IDENT_MATRIX;
        affineTransform1 = IDENT_ATX;
      } 
      affineTransform2.getMatrix(paramFontInfo.glyphTx = new double[4]);
      double d1 = affineTransform2.getShearX();
      double d2 = affineTransform2.getScaleY();
      if (d1 != 0.0D)
        d2 = Math.sqrt(d1 * d1 + d2 * d2); 
      paramFontInfo.pixelHeight = (int)(Math.abs(d2) + 0.5D);
    } else {
      boolean bool = false;
      paramFontInfo.originX = paramFontInfo.originY = 0.0F;
      if (this.transformState >= 3) {
        this.transform.getMatrix(paramFontInfo.devTx = new double[4]);
        affineTransform1 = new AffineTransform(paramFontInfo.devTx);
        paramFontInfo.glyphTx = new double[4];
        for (byte b = 0; b < 4; b++)
          paramFontInfo.glyphTx[b] = paramFontInfo.devTx[b] * f; 
        affineTransform2 = new AffineTransform(paramFontInfo.glyphTx);
        double d1 = this.transform.getShearX();
        double d2 = this.transform.getScaleY();
        if (d1 != 0.0D)
          d2 = Math.sqrt(d1 * d1 + d2 * d2); 
        paramFontInfo.pixelHeight = (int)(Math.abs(d2 * f) + 0.5D);
      } else {
        int k = (int)f;
        if (f == k && k >= 8 && k < 17) {
          paramFontInfo.glyphTx = textTxArr[k];
          affineTransform2 = textAtArr[k];
          paramFontInfo.pixelHeight = k;
        } else {
          paramFontInfo.pixelHeight = (int)(f + 0.5D);
        } 
        if (affineTransform2 == null) {
          paramFontInfo.glyphTx = new double[] { f, 0.0D, 0.0D, f };
          affineTransform2 = new AffineTransform(paramFontInfo.glyphTx);
        } 
        paramFontInfo.devTx = IDENT_MATRIX;
        affineTransform1 = IDENT_ATX;
      } 
    } 
    paramFontInfo.font2D = FontUtilities.getFont2D(paramFont);
    int i = this.fractionalMetricsHint;
    if (i == 0)
      i = 1; 
    paramFontInfo.lcdSubPixPos = false;
    if (paramFontRenderContext == null) {
      j = this.textAntialiasHint;
    } else {
      j = ((SunHints.Value)paramFontRenderContext.getAntiAliasingHint()).getIndex();
    } 
    if (j == 0) {
      if (this.antialiasHint == 2) {
        j = 2;
      } else {
        j = 1;
      } 
    } else if (j == 3) {
      if (paramFontInfo.font2D.useAAForPtSize(paramFontInfo.pixelHeight)) {
        j = 2;
      } else {
        j = 1;
      } 
    } else if (j >= 4) {
      if (!this.surfaceData.canRenderLCDText(this)) {
        j = 2;
      } else {
        paramFontInfo.lcdRGBOrder = true;
        if (j == 5) {
          j = 4;
          paramFontInfo.lcdRGBOrder = false;
        } else if (j == 7) {
          j = 6;
          paramFontInfo.lcdRGBOrder = false;
        } 
        paramFontInfo.lcdSubPixPos = (i == 2 && j == 4);
      } 
    } 
    paramFontInfo.aaHint = j;
    paramFontInfo.fontStrike = paramFontInfo.font2D.getStrike(paramFont, affineTransform1, affineTransform2, j, i);
    return paramFontInfo;
  }
  
  public static boolean isRotated(double[] paramArrayOfDouble) { return !(paramArrayOfDouble[0] == paramArrayOfDouble[3] && paramArrayOfDouble[1] == 0.0D && paramArrayOfDouble[2] == 0.0D && paramArrayOfDouble[0] > 0.0D); }
  
  public void setFont(Font paramFont) {
    if (paramFont != null && paramFont != this.font) {
      if (this.textAntialiasHint == 3 && this.textpipe != invalidpipe && (this.transformState > 2 || paramFont.isTransformed() || this.fontInfo == null || ((this.fontInfo.aaHint == 2)) != FontUtilities.getFont2D(paramFont).useAAForPtSize(paramFont.getSize())))
        this.textpipe = invalidpipe; 
      this.font = paramFont;
      this.fontMetrics = null;
      this.validFontInfo = false;
    } 
  }
  
  public FontInfo getFontInfo() {
    if (!this.validFontInfo) {
      this.fontInfo = checkFontInfo(this.fontInfo, this.font, null);
      this.validFontInfo = true;
    } 
    return this.fontInfo;
  }
  
  public FontInfo getGVFontInfo(Font paramFont, FontRenderContext paramFontRenderContext) {
    if (this.glyphVectorFontInfo != null && this.glyphVectorFontInfo.font == paramFont && this.glyphVectorFRC == paramFontRenderContext)
      return this.glyphVectorFontInfo; 
    this.glyphVectorFRC = paramFontRenderContext;
    return this.glyphVectorFontInfo = checkFontInfo(this.glyphVectorFontInfo, paramFont, paramFontRenderContext);
  }
  
  public FontMetrics getFontMetrics() { return (this.fontMetrics != null) ? this.fontMetrics : (this.fontMetrics = FontDesignMetrics.getMetrics(this.font, getFontRenderContext())); }
  
  public FontMetrics getFontMetrics(Font paramFont) {
    if (this.fontMetrics != null && paramFont == this.font)
      return this.fontMetrics; 
    FontDesignMetrics fontDesignMetrics = FontDesignMetrics.getMetrics(paramFont, getFontRenderContext());
    if (this.font == paramFont)
      this.fontMetrics = fontDesignMetrics; 
    return fontDesignMetrics;
  }
  
  public boolean hit(Rectangle paramRectangle, Shape paramShape, boolean paramBoolean) {
    if (paramBoolean)
      paramShape = this.stroke.createStrokedShape(paramShape); 
    paramShape = transformShape(paramShape);
    if ((this.constrainX | this.constrainY) != 0) {
      paramRectangle = new Rectangle(paramRectangle);
      paramRectangle.translate(this.constrainX, this.constrainY);
    } 
    return paramShape.intersects(paramRectangle);
  }
  
  public ColorModel getDeviceColorModel() { return this.surfaceData.getColorModel(); }
  
  public GraphicsConfiguration getDeviceConfiguration() { return this.surfaceData.getDeviceConfiguration(); }
  
  public final SurfaceData getSurfaceData() { return this.surfaceData; }
  
  public void setComposite(Composite paramComposite) {
    CompositeType compositeType;
    byte b;
    if (this.composite == paramComposite)
      return; 
    if (paramComposite instanceof AlphaComposite) {
      AlphaComposite alphaComposite = (AlphaComposite)paramComposite;
      compositeType = CompositeType.forAlphaComposite(alphaComposite);
      if (compositeType == CompositeType.SrcOverNoEa) {
        if (this.paintState == 0 || (this.paintState > 1 && this.paint.getTransparency() == 1)) {
          b = 0;
        } else {
          b = 1;
        } 
      } else if (compositeType == CompositeType.SrcNoEa || compositeType == CompositeType.Src || compositeType == CompositeType.Clear) {
        b = 0;
      } else if (this.surfaceData.getTransparency() == 1 && compositeType == CompositeType.SrcIn) {
        b = 0;
      } else {
        b = 1;
      } 
    } else if (paramComposite instanceof XORComposite) {
      b = 2;
      compositeType = CompositeType.Xor;
    } else {
      if (paramComposite == null)
        throw new IllegalArgumentException("null Composite"); 
      this.surfaceData.checkCustomComposite();
      b = 3;
      compositeType = CompositeType.General;
    } 
    if (this.compositeState != b || this.imageComp != compositeType) {
      this.compositeState = b;
      this.imageComp = compositeType;
      invalidatePipe();
      this.validFontInfo = false;
    } 
    this.composite = paramComposite;
    if (this.paintState <= 1)
      validateColor(); 
  }
  
  public void setPaint(Paint paramPaint) {
    if (paramPaint instanceof Color) {
      setColor((Color)paramPaint);
      return;
    } 
    if (paramPaint == null || this.paint == paramPaint)
      return; 
    this.paint = paramPaint;
    if (this.imageComp == CompositeType.SrcOverNoEa)
      if (paramPaint.getTransparency() == 1) {
        if (this.compositeState != 0)
          this.compositeState = 0; 
      } else if (this.compositeState == 0) {
        this.compositeState = 1;
      }  
    Class clazz = paramPaint.getClass();
    if (clazz == java.awt.GradientPaint.class) {
      this.paintState = 2;
    } else if (clazz == java.awt.LinearGradientPaint.class) {
      this.paintState = 3;
    } else if (clazz == java.awt.RadialGradientPaint.class) {
      this.paintState = 4;
    } else if (clazz == java.awt.TexturePaint.class) {
      this.paintState = 5;
    } else {
      this.paintState = 6;
    } 
    this.validFontInfo = false;
    invalidatePipe();
  }
  
  private void validateBasicStroke(BasicStroke paramBasicStroke) {
    boolean bool = (this.antialiasHint == 2) ? 1 : 0;
    if (this.transformState < 3) {
      if (bool) {
        if (paramBasicStroke.getLineWidth() <= MinPenSizeAA) {
          if (paramBasicStroke.getDashArray() == null) {
            this.strokeState = 0;
          } else {
            this.strokeState = 1;
          } 
        } else {
          this.strokeState = 2;
        } 
      } else if (paramBasicStroke == defaultStroke) {
        this.strokeState = 0;
      } else if (paramBasicStroke.getLineWidth() <= 1.0F) {
        if (paramBasicStroke.getDashArray() == null) {
          this.strokeState = 0;
        } else {
          this.strokeState = 1;
        } 
      } else {
        this.strokeState = 2;
      } 
    } else {
      double d;
      if ((this.transform.getType() & 0x24) == 0) {
        d = Math.abs(this.transform.getDeterminant());
      } else {
        double d1 = this.transform.getScaleX();
        double d2 = this.transform.getShearX();
        double d3 = this.transform.getShearY();
        double d4 = this.transform.getScaleY();
        double d5 = d1 * d1 + d3 * d3;
        double d6 = 2.0D * (d1 * d2 + d3 * d4);
        double d7 = d2 * d2 + d4 * d4;
        double d8 = Math.sqrt(d6 * d6 + (d5 - d7) * (d5 - d7));
        d = (d5 + d7 + d8) / 2.0D;
      } 
      if (paramBasicStroke != defaultStroke)
        d *= (paramBasicStroke.getLineWidth() * paramBasicStroke.getLineWidth()); 
      if (d <= (bool ? MinPenSizeAASquared : 1.000000001D)) {
        if (paramBasicStroke.getDashArray() == null) {
          this.strokeState = 0;
        } else {
          this.strokeState = 1;
        } 
      } else {
        this.strokeState = 2;
      } 
    } 
  }
  
  public void setStroke(Stroke paramStroke) {
    if (paramStroke == null)
      throw new IllegalArgumentException("null Stroke"); 
    int i = this.strokeState;
    this.stroke = paramStroke;
    if (paramStroke instanceof BasicStroke) {
      validateBasicStroke((BasicStroke)paramStroke);
    } else {
      this.strokeState = 3;
    } 
    if (this.strokeState != i)
      invalidatePipe(); 
  }
  
  public void setRenderingHint(RenderingHints.Key paramKey, Object paramObject) {
    if (!paramKey.isCompatibleValue(paramObject))
      throw new IllegalArgumentException(paramObject + " is not compatible with " + paramKey); 
    if (paramKey instanceof SunHints.Key) {
      int i;
      byte b1;
      byte b2 = 0;
      boolean bool = true;
      SunHints.Key key = (SunHints.Key)paramKey;
      if (key == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) {
        i = ((Integer)paramObject).intValue();
      } else {
        i = ((SunHints.Value)paramObject).getIndex();
      } 
      switch (key.getIndex()) {
        case 0:
          b1 = (this.renderHint != i) ? 1 : 0;
          if (b1) {
            this.renderHint = i;
            if (this.interpolationHint == -1)
              this.interpolationType = (i == 2) ? 2 : 1; 
          } 
          break;
        case 1:
          b1 = (this.antialiasHint != i) ? 1 : 0;
          this.antialiasHint = i;
          if (b1) {
            b2 = (this.textAntialiasHint == 0) ? 1 : 0;
            if (this.strokeState != 3)
              validateBasicStroke((BasicStroke)this.stroke); 
          } 
          break;
        case 2:
          b1 = (this.textAntialiasHint != i) ? 1 : 0;
          b2 = b1;
          this.textAntialiasHint = i;
          break;
        case 3:
          b1 = (this.fractionalMetricsHint != i) ? 1 : 0;
          b2 = b1;
          this.fractionalMetricsHint = i;
          break;
        case 100:
          b1 = 0;
          this.lcdTextContrast = i;
          break;
        case 5:
          this.interpolationHint = i;
          switch (i) {
            case 2:
              i = 3;
              break;
            case 1:
              i = 2;
              break;
            default:
              i = 1;
              break;
          } 
          b1 = (this.interpolationType != i) ? 1 : 0;
          this.interpolationType = i;
          break;
        case 8:
          b1 = (this.strokeHint != i) ? 1 : 0;
          this.strokeHint = i;
          break;
        case 9:
          b1 = (this.resolutionVariantHint != i) ? 1 : 0;
          this.resolutionVariantHint = i;
          break;
        default:
          bool = false;
          b1 = 0;
          break;
      } 
      if (bool) {
        if (b1 != 0) {
          invalidatePipe();
          if (b2 != 0) {
            this.fontMetrics = null;
            this.cachedFRC = null;
            this.validFontInfo = false;
            this.glyphVectorFontInfo = null;
          } 
        } 
        if (this.hints != null)
          this.hints.put(paramKey, paramObject); 
        return;
      } 
    } 
    if (this.hints == null)
      this.hints = makeHints(null); 
    this.hints.put(paramKey, paramObject);
  }
  
  public Object getRenderingHint(RenderingHints.Key paramKey) {
    if (this.hints != null)
      return this.hints.get(paramKey); 
    if (!(paramKey instanceof SunHints.Key))
      return null; 
    int i = ((SunHints.Key)paramKey).getIndex();
    switch (i) {
      case 0:
        return SunHints.Value.get(0, this.renderHint);
      case 1:
        return SunHints.Value.get(1, this.antialiasHint);
      case 2:
        return SunHints.Value.get(2, this.textAntialiasHint);
      case 3:
        return SunHints.Value.get(3, this.fractionalMetricsHint);
      case 100:
        return new Integer(this.lcdTextContrast);
      case 5:
        switch (this.interpolationHint) {
          case 0:
            return SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
          case 1:
            return SunHints.VALUE_INTERPOLATION_BILINEAR;
          case 2:
            return SunHints.VALUE_INTERPOLATION_BICUBIC;
        } 
        return null;
      case 8:
        return SunHints.Value.get(8, this.strokeHint);
      case 9:
        return SunHints.Value.get(9, this.resolutionVariantHint);
    } 
    return null;
  }
  
  public void setRenderingHints(Map<?, ?> paramMap) {
    this.hints = null;
    this.renderHint = 0;
    this.antialiasHint = 1;
    this.textAntialiasHint = 0;
    this.fractionalMetricsHint = 1;
    this.lcdTextContrast = lcdTextContrastDefaultValue;
    this.interpolationHint = -1;
    this.interpolationType = 1;
    boolean bool = false;
    for (Object object : paramMap.keySet()) {
      if (object == SunHints.KEY_RENDERING || object == SunHints.KEY_ANTIALIASING || object == SunHints.KEY_TEXT_ANTIALIASING || object == SunHints.KEY_FRACTIONALMETRICS || object == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST || object == SunHints.KEY_STROKE_CONTROL || object == SunHints.KEY_INTERPOLATION) {
        setRenderingHint((RenderingHints.Key)object, paramMap.get(object));
        continue;
      } 
      bool = true;
    } 
    if (bool)
      this.hints = makeHints(paramMap); 
    invalidatePipe();
  }
  
  public void addRenderingHints(Map<?, ?> paramMap) {
    boolean bool = false;
    for (Object object : paramMap.keySet()) {
      if (object == SunHints.KEY_RENDERING || object == SunHints.KEY_ANTIALIASING || object == SunHints.KEY_TEXT_ANTIALIASING || object == SunHints.KEY_FRACTIONALMETRICS || object == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST || object == SunHints.KEY_STROKE_CONTROL || object == SunHints.KEY_INTERPOLATION) {
        setRenderingHint((RenderingHints.Key)object, paramMap.get(object));
        continue;
      } 
      bool = true;
    } 
    if (bool)
      if (this.hints == null) {
        this.hints = makeHints(paramMap);
      } else {
        this.hints.putAll(paramMap);
      }  
  }
  
  public RenderingHints getRenderingHints() { return (this.hints == null) ? makeHints(null) : (RenderingHints)this.hints.clone(); }
  
  RenderingHints makeHints(Map paramMap) {
    Object object;
    RenderingHints renderingHints = new RenderingHints(paramMap);
    renderingHints.put(SunHints.KEY_RENDERING, SunHints.Value.get(0, this.renderHint));
    renderingHints.put(SunHints.KEY_ANTIALIASING, SunHints.Value.get(1, this.antialiasHint));
    renderingHints.put(SunHints.KEY_TEXT_ANTIALIASING, SunHints.Value.get(2, this.textAntialiasHint));
    renderingHints.put(SunHints.KEY_FRACTIONALMETRICS, SunHints.Value.get(3, this.fractionalMetricsHint));
    renderingHints.put(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, Integer.valueOf(this.lcdTextContrast));
    switch (this.interpolationHint) {
      case 0:
        object = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        break;
      case 1:
        object = SunHints.VALUE_INTERPOLATION_BILINEAR;
        break;
      case 2:
        object = SunHints.VALUE_INTERPOLATION_BICUBIC;
        break;
      default:
        object = null;
        break;
    } 
    if (object != null)
      renderingHints.put(SunHints.KEY_INTERPOLATION, object); 
    renderingHints.put(SunHints.KEY_STROKE_CONTROL, SunHints.Value.get(8, this.strokeHint));
    return renderingHints;
  }
  
  public void translate(double paramDouble1, double paramDouble2) {
    this.transform.translate(paramDouble1, paramDouble2);
    invalidateTransform();
  }
  
  public void rotate(double paramDouble) {
    this.transform.rotate(paramDouble);
    invalidateTransform();
  }
  
  public void rotate(double paramDouble1, double paramDouble2, double paramDouble3) {
    this.transform.rotate(paramDouble1, paramDouble2, paramDouble3);
    invalidateTransform();
  }
  
  public void scale(double paramDouble1, double paramDouble2) {
    this.transform.scale(paramDouble1, paramDouble2);
    invalidateTransform();
  }
  
  public void shear(double paramDouble1, double paramDouble2) {
    this.transform.shear(paramDouble1, paramDouble2);
    invalidateTransform();
  }
  
  public void transform(AffineTransform paramAffineTransform) {
    this.transform.concatenate(paramAffineTransform);
    invalidateTransform();
  }
  
  public void translate(int paramInt1, int paramInt2) {
    this.transform.translate(paramInt1, paramInt2);
    if (this.transformState <= 1) {
      this.transX += paramInt1;
      this.transY += paramInt2;
      this.transformState = ((this.transX | this.transY) == 0) ? 0 : 1;
    } else {
      invalidateTransform();
    } 
  }
  
  public void setTransform(AffineTransform paramAffineTransform) {
    if ((this.constrainX | this.constrainY) == 0 && this.devScale == 1) {
      this.transform.setTransform(paramAffineTransform);
    } else {
      this.transform.setTransform(this.devScale, 0.0D, 0.0D, this.devScale, this.constrainX, this.constrainY);
      this.transform.concatenate(paramAffineTransform);
    } 
    invalidateTransform();
  }
  
  protected void invalidateTransform() {
    int i = this.transform.getType();
    int j = this.transformState;
    if (i == 0) {
      this.transformState = 0;
      this.transX = this.transY = 0;
    } else if (i == 1) {
      double d1 = this.transform.getTranslateX();
      double d2 = this.transform.getTranslateY();
      this.transX = (int)Math.floor(d1 + 0.5D);
      this.transY = (int)Math.floor(d2 + 0.5D);
      if (d1 == this.transX && d2 == this.transY) {
        this.transformState = 1;
      } else {
        this.transformState = 2;
      } 
    } else if ((i & 0x78) == 0) {
      this.transformState = 3;
      this.transX = this.transY = 0;
    } else {
      this.transformState = 4;
      this.transX = this.transY = 0;
    } 
    if (this.transformState >= 3 || j >= 3) {
      this.cachedFRC = null;
      this.validFontInfo = false;
      this.fontMetrics = null;
      this.glyphVectorFontInfo = null;
      if (this.transformState != j)
        invalidatePipe(); 
    } 
    if (this.strokeState != 3)
      validateBasicStroke((BasicStroke)this.stroke); 
  }
  
  public AffineTransform getTransform() {
    if ((this.constrainX | this.constrainY) == 0 && this.devScale == 1)
      return new AffineTransform(this.transform); 
    double d = 1.0D / this.devScale;
    AffineTransform affineTransform = new AffineTransform(d, 0.0D, 0.0D, d, -this.constrainX * d, -this.constrainY * d);
    affineTransform.concatenate(this.transform);
    return affineTransform;
  }
  
  public AffineTransform cloneTransform() { return new AffineTransform(this.transform); }
  
  public Paint getPaint() { return this.paint; }
  
  public Composite getComposite() { return this.composite; }
  
  public Color getColor() { return this.foregroundColor; }
  
  final void validateColor() {
    int i;
    if (this.imageComp == CompositeType.Clear) {
      i = 0;
    } else {
      i = this.foregroundColor.getRGB();
      if (this.compositeState <= 1 && this.imageComp != CompositeType.SrcNoEa && this.imageComp != CompositeType.SrcOverNoEa) {
        AlphaComposite alphaComposite = (AlphaComposite)this.composite;
        int j = Math.round(alphaComposite.getAlpha() * (i >>> 24));
        i = i & 0xFFFFFF | j << 24;
      } 
    } 
    this.eargb = i;
    this.pixel = this.surfaceData.pixelFor(i);
  }
  
  public void setColor(Color paramColor) {
    if (paramColor == null || paramColor == this.paint)
      return; 
    this.paint = this.foregroundColor = paramColor;
    validateColor();
    if (this.eargb >> 24 == -1) {
      if (this.paintState == 0)
        return; 
      this.paintState = 0;
      if (this.imageComp == CompositeType.SrcOverNoEa)
        this.compositeState = 0; 
    } else {
      if (this.paintState == 1)
        return; 
      this.paintState = 1;
      if (this.imageComp == CompositeType.SrcOverNoEa)
        this.compositeState = 1; 
    } 
    this.validFontInfo = false;
    invalidatePipe();
  }
  
  public void setBackground(Color paramColor) { this.backgroundColor = paramColor; }
  
  public Color getBackground() { return this.backgroundColor; }
  
  public Stroke getStroke() { return this.stroke; }
  
  public Rectangle getClipBounds() { return (this.clipState == 0) ? null : getClipBounds(new Rectangle()); }
  
  public Rectangle getClipBounds(Rectangle paramRectangle) {
    if (this.clipState != 0) {
      if (this.transformState <= 1) {
        if (this.usrClip instanceof Rectangle) {
          paramRectangle.setBounds((Rectangle)this.usrClip);
        } else {
          paramRectangle.setFrame(this.usrClip.getBounds2D());
        } 
        paramRectangle.translate(-this.transX, -this.transY);
      } else {
        paramRectangle.setFrame(getClip().getBounds2D());
      } 
    } else if (paramRectangle == null) {
      throw new NullPointerException("null rectangle parameter");
    } 
    return paramRectangle;
  }
  
  public boolean hitClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt3 <= 0 || paramInt4 <= 0)
      return false; 
    if (this.transformState > 1) {
      double[] arrayOfDouble = { paramInt1, paramInt2, (paramInt1 + paramInt3), paramInt2, paramInt1, (paramInt2 + paramInt4), (paramInt1 + paramInt3), (paramInt2 + paramInt4) };
      this.transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
      paramInt1 = (int)Math.floor(Math.min(Math.min(arrayOfDouble[0], arrayOfDouble[2]), Math.min(arrayOfDouble[4], arrayOfDouble[6])));
      paramInt2 = (int)Math.floor(Math.min(Math.min(arrayOfDouble[1], arrayOfDouble[3]), Math.min(arrayOfDouble[5], arrayOfDouble[7])));
      paramInt3 = (int)Math.ceil(Math.max(Math.max(arrayOfDouble[0], arrayOfDouble[2]), Math.max(arrayOfDouble[4], arrayOfDouble[6])));
      paramInt4 = (int)Math.ceil(Math.max(Math.max(arrayOfDouble[1], arrayOfDouble[3]), Math.max(arrayOfDouble[5], arrayOfDouble[7])));
    } else {
      paramInt1 += this.transX;
      paramInt2 += this.transY;
      paramInt3 += paramInt1;
      paramInt4 += paramInt2;
    } 
    try {
      if (!getCompClip().intersectsQuickCheckXYXY(paramInt1, paramInt2, paramInt3, paramInt4))
        return false; 
    } catch (InvalidPipeException invalidPipeException) {
      return false;
    } 
    return true;
  }
  
  protected void validateCompClip() {
    int i = this.clipState;
    if (this.usrClip == null) {
      this.clipState = 0;
      this.clipRegion = this.devClip;
    } else if (this.usrClip instanceof Rectangle2D) {
      this.clipState = 1;
      if (this.usrClip instanceof Rectangle) {
        this.clipRegion = this.devClip.getIntersection((Rectangle)this.usrClip);
      } else {
        this.clipRegion = this.devClip.getIntersection(this.usrClip.getBounds());
      } 
    } else {
      PathIterator pathIterator = this.usrClip.getPathIterator(null);
      int[] arrayOfInt = new int[4];
      shapeSpanIterator = LoopPipe.getFillSSI(this);
      try {
        shapeSpanIterator.setOutputArea(this.devClip);
        shapeSpanIterator.appendPath(pathIterator);
        shapeSpanIterator.getPathBox(arrayOfInt);
        Region region = Region.getInstance(arrayOfInt);
        region.appendSpans(shapeSpanIterator);
        this.clipRegion = region;
        this.clipState = region.isRectangular() ? 1 : 2;
      } finally {
        shapeSpanIterator.dispose();
      } 
    } 
    if (i != this.clipState && (this.clipState == 2 || i == 2)) {
      this.validFontInfo = false;
      invalidatePipe();
    } 
  }
  
  protected Shape transformShape(Shape paramShape) { return (paramShape == null) ? null : ((this.transformState > 1) ? transformShape(this.transform, paramShape) : transformShape(this.transX, this.transY, paramShape)); }
  
  public Shape untransformShape(Shape paramShape) {
    if (paramShape == null)
      return null; 
    if (this.transformState > 1)
      try {
        return transformShape(this.transform.createInverse(), paramShape);
      } catch (NoninvertibleTransformException noninvertibleTransformException) {
        return null;
      }  
    return transformShape(-this.transX, -this.transY, paramShape);
  }
  
  protected static Shape transformShape(int paramInt1, int paramInt2, Shape paramShape) {
    if (paramShape == null)
      return null; 
    if (paramShape instanceof Rectangle) {
      Rectangle rectangle = paramShape.getBounds();
      rectangle.translate(paramInt1, paramInt2);
      return rectangle;
    } 
    if (paramShape instanceof Rectangle2D) {
      Rectangle2D rectangle2D = (Rectangle2D)paramShape;
      return new Rectangle2D.Double(rectangle2D.getX() + paramInt1, rectangle2D.getY() + paramInt2, rectangle2D.getWidth(), rectangle2D.getHeight());
    } 
    if (paramInt1 == 0 && paramInt2 == 0)
      return cloneShape(paramShape); 
    AffineTransform affineTransform = AffineTransform.getTranslateInstance(paramInt1, paramInt2);
    return affineTransform.createTransformedShape(paramShape);
  }
  
  protected static Shape transformShape(AffineTransform paramAffineTransform, Shape paramShape) {
    if (paramShape == null)
      return null; 
    if (paramShape instanceof Rectangle2D && (paramAffineTransform.getType() & 0x30) == 0) {
      Rectangle2D rectangle2D = (Rectangle2D)paramShape;
      double[] arrayOfDouble = new double[4];
      arrayOfDouble[0] = rectangle2D.getX();
      arrayOfDouble[1] = rectangle2D.getY();
      arrayOfDouble[2] = arrayOfDouble[0] + rectangle2D.getWidth();
      arrayOfDouble[3] = arrayOfDouble[1] + rectangle2D.getHeight();
      paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 2);
      fixRectangleOrientation(arrayOfDouble, rectangle2D);
      return new Rectangle2D.Double(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2] - arrayOfDouble[0], arrayOfDouble[3] - arrayOfDouble[1]);
    } 
    return paramAffineTransform.isIdentity() ? cloneShape(paramShape) : paramAffineTransform.createTransformedShape(paramShape);
  }
  
  private static void fixRectangleOrientation(double[] paramArrayOfDouble, Rectangle2D paramRectangle2D) {
    if (((paramRectangle2D.getWidth() > 0.0D) ? 1 : 0) != ((paramArrayOfDouble[2] - paramArrayOfDouble[0] > 0.0D) ? 1 : 0)) {
      double d = paramArrayOfDouble[0];
      paramArrayOfDouble[0] = paramArrayOfDouble[2];
      paramArrayOfDouble[2] = d;
    } 
    if (((paramRectangle2D.getHeight() > 0.0D) ? 1 : 0) != ((paramArrayOfDouble[3] - paramArrayOfDouble[1] > 0.0D) ? 1 : 0)) {
      double d = paramArrayOfDouble[1];
      paramArrayOfDouble[1] = paramArrayOfDouble[3];
      paramArrayOfDouble[3] = d;
    } 
  }
  
  public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { clip(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { setClip(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public Shape getClip() { return untransformShape(this.usrClip); }
  
  public void setClip(Shape paramShape) {
    this.usrClip = transformShape(paramShape);
    validateCompClip();
  }
  
  public void clip(Shape paramShape) {
    paramShape = transformShape(paramShape);
    if (this.usrClip != null)
      paramShape = intersectShapes(this.usrClip, paramShape, true, true); 
    this.usrClip = paramShape;
    validateCompClip();
  }
  
  public void setPaintMode() { setComposite(AlphaComposite.SrcOver); }
  
  public void setXORMode(Color paramColor) {
    if (paramColor == null)
      throw new IllegalArgumentException("null XORColor"); 
    setComposite(new XORComposite(paramColor, this.surfaceData));
  }
  
  public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      doCopyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        doCopyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  private void doCopyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (paramInt3 <= 0 || paramInt4 <= 0)
      return; 
    SurfaceData surfaceData1 = this.surfaceData;
    if (surfaceData1.copyArea(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6))
      return; 
    if (this.transformState > 3)
      throw new InternalError("transformed copyArea not implemented yet"); 
    Region region = getCompClip();
    Composite composite1 = this.composite;
    if (this.lastCAcomp != composite1) {
      SurfaceType surfaceType = surfaceData1.getSurfaceType();
      CompositeType compositeType = this.imageComp;
      if (CompositeType.SrcOverNoEa.equals(compositeType) && surfaceData1.getTransparency() == 1)
        compositeType = CompositeType.SrcNoEa; 
      this.lastCAblit = Blit.locate(surfaceType, compositeType, surfaceType);
      this.lastCAcomp = composite1;
    } 
    double[] arrayOfDouble = { paramInt1, paramInt2, (paramInt1 + paramInt3), (paramInt2 + paramInt4), (paramInt1 + paramInt5), (paramInt2 + paramInt6) };
    this.transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 3);
    paramInt1 = (int)Math.ceil(arrayOfDouble[0] - 0.5D);
    paramInt2 = (int)Math.ceil(arrayOfDouble[1] - 0.5D);
    paramInt3 = (int)Math.ceil(arrayOfDouble[2] - 0.5D) - paramInt1;
    paramInt4 = (int)Math.ceil(arrayOfDouble[3] - 0.5D) - paramInt2;
    paramInt5 = (int)Math.ceil(arrayOfDouble[4] - 0.5D) - paramInt1;
    paramInt6 = (int)Math.ceil(arrayOfDouble[5] - 0.5D) - paramInt2;
    if (paramInt3 < 0) {
      paramInt3 *= -1;
      paramInt1 -= paramInt3;
    } 
    if (paramInt4 < 0) {
      paramInt4 *= -1;
      paramInt2 -= paramInt4;
    } 
    Blit blit = this.lastCAblit;
    if (paramInt6 == 0 && paramInt5 > 0 && paramInt5 < paramInt3) {
      while (paramInt3 > 0) {
        int i = Math.min(paramInt3, paramInt5);
        paramInt3 -= i;
        int j = paramInt1 + paramInt3;
        blit.Blit(surfaceData1, surfaceData1, composite1, region, j, paramInt2, j + paramInt5, paramInt2 + paramInt6, i, paramInt4);
      } 
      return;
    } 
    if (paramInt6 > 0 && paramInt6 < paramInt4 && paramInt5 > -paramInt3 && paramInt5 < paramInt3) {
      while (paramInt4 > 0) {
        int i = Math.min(paramInt4, paramInt6);
        paramInt4 -= i;
        int j = paramInt2 + paramInt4;
        blit.Blit(surfaceData1, surfaceData1, composite1, region, paramInt1, j, paramInt1 + paramInt5, j + paramInt6, paramInt3, i);
      } 
      return;
    } 
    blit.Blit(surfaceData1, surfaceData1, composite1, region, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4);
  }
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      this.drawpipe.drawLine(this, paramInt1, paramInt2, paramInt3, paramInt4);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.drawpipe.drawLine(this, paramInt1, paramInt2, paramInt3, paramInt4);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      this.drawpipe.drawRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.drawpipe.drawRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      this.fillpipe.fillRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.fillpipe.fillRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      this.drawpipe.drawOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.drawpipe.drawOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      this.fillpipe.fillOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.fillpipe.fillOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      this.drawpipe.drawArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.drawpipe.drawArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      this.fillpipe.fillArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.fillpipe.fillArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    try {
      this.drawpipe.drawPolyline(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.drawpipe.drawPolyline(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    try {
      this.drawpipe.drawPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.drawpipe.drawPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    try {
      this.fillpipe.fillPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.fillpipe.fillPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      this.drawpipe.drawRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.drawpipe.drawRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      this.fillpipe.fillRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.fillpipe.fillRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  private void revalidateAll() {
    this.surfaceData = this.surfaceData.getReplacement();
    if (this.surfaceData == null)
      this.surfaceData = NullSurfaceData.theInstance; 
    invalidatePipe();
    setDevClip(this.surfaceData.getBounds());
    if (this.paintState <= 1)
      validateColor(); 
    if (this.composite instanceof XORComposite) {
      Color color = ((XORComposite)this.composite).getXorColor();
      setComposite(new XORComposite(color, this.surfaceData));
    } 
    validatePipe();
  }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Composite composite1 = this.composite;
    Paint paint1 = this.paint;
    setComposite(AlphaComposite.Src);
    setColor(getBackground());
    fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
    setPaint(paint1);
    setComposite(composite1);
  }
  
  public void draw(Shape paramShape) {
    try {
      this.shapepipe.draw(this, paramShape);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.shapepipe.draw(this, paramShape);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void fill(Shape paramShape) {
    try {
      this.shapepipe.fill(this, paramShape);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.shapepipe.fill(this, paramShape);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  private static boolean isIntegerTranslation(AffineTransform paramAffineTransform) {
    if (paramAffineTransform.isIdentity())
      return true; 
    if (paramAffineTransform.getType() == 1) {
      double d1 = paramAffineTransform.getTranslateX();
      double d2 = paramAffineTransform.getTranslateY();
      return (d1 == (int)d1 && d2 == (int)d2);
    } 
    return false;
  }
  
  private static int getTileIndex(int paramInt1, int paramInt2, int paramInt3) {
    paramInt1 -= paramInt2;
    if (paramInt1 < 0)
      paramInt1 += 1 - paramInt3; 
    return paramInt1 / paramInt3;
  }
  
  private static Rectangle getImageRegion(RenderedImage paramRenderedImage, Region paramRegion, AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2, int paramInt1, int paramInt2) {
    Rectangle rectangle1 = new Rectangle(paramRenderedImage.getMinX(), paramRenderedImage.getMinY(), paramRenderedImage.getWidth(), paramRenderedImage.getHeight());
    Rectangle rectangle2 = null;
    try {
      double[] arrayOfDouble = new double[8];
      arrayOfDouble[2] = paramRegion.getLoX();
      arrayOfDouble[0] = paramRegion.getLoX();
      arrayOfDouble[6] = paramRegion.getHiX();
      arrayOfDouble[4] = paramRegion.getHiX();
      arrayOfDouble[5] = paramRegion.getLoY();
      arrayOfDouble[1] = paramRegion.getLoY();
      arrayOfDouble[7] = paramRegion.getHiY();
      arrayOfDouble[3] = paramRegion.getHiY();
      paramAffineTransform1.inverseTransform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
      paramAffineTransform2.inverseTransform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
      double d2 = arrayOfDouble[0];
      double d1 = d2;
      double d4 = arrayOfDouble[1];
      double d3 = d4;
      int i = 2;
      while (i < 8) {
        double d = arrayOfDouble[i++];
        if (d < d1) {
          d1 = d;
        } else if (d > d2) {
          d2 = d;
        } 
        d = arrayOfDouble[i++];
        if (d < d3) {
          d3 = d;
          continue;
        } 
        if (d > d4)
          d4 = d; 
      } 
      i = (int)d1 - paramInt1;
      int j = (int)(d2 - d1 + (2 * paramInt1));
      int k = (int)d3 - paramInt2;
      int m = (int)(d4 - d3 + (2 * paramInt2));
      Rectangle rectangle = new Rectangle(i, k, j, m);
      rectangle2 = rectangle.intersection(rectangle1);
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      rectangle2 = rectangle1;
    } 
    return rectangle2;
  }
  
  public void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform) {
    Region region;
    if (paramRenderedImage == null)
      return; 
    if (paramRenderedImage instanceof BufferedImage) {
      BufferedImage bufferedImage1 = (BufferedImage)paramRenderedImage;
      drawImage(bufferedImage1, paramAffineTransform, null);
      return;
    } 
    boolean bool = (this.transformState <= 1 && isIntegerTranslation(paramAffineTransform)) ? 1 : 0;
    byte b = bool ? 0 : 3;
    try {
      region = getCompClip();
    } catch (InvalidPipeException invalidPipeException) {
      return;
    } 
    Rectangle rectangle = getImageRegion(paramRenderedImage, region, this.transform, paramAffineTransform, b, b);
    if (rectangle.width <= 0 || rectangle.height <= 0)
      return; 
    if (bool) {
      drawTranslatedRenderedImage(paramRenderedImage, rectangle, (int)paramAffineTransform.getTranslateX(), (int)paramAffineTransform.getTranslateY());
      return;
    } 
    Raster raster;
    WritableRaster writableRaster = (raster = paramRenderedImage.getData(rectangle)).createWritableRaster(raster.getSampleModel(), raster.getDataBuffer(), null);
    int i = raster.getMinX();
    int j = raster.getMinY();
    int k = raster.getWidth();
    int m = raster.getHeight();
    int n = i - raster.getSampleModelTranslateX();
    int i1 = j - raster.getSampleModelTranslateY();
    if (n != 0 || i1 != 0 || k != writableRaster.getWidth() || m != writableRaster.getHeight())
      writableRaster = writableRaster.createWritableChild(n, i1, k, m, 0, 0, null); 
    AffineTransform affineTransform = (AffineTransform)paramAffineTransform.clone();
    affineTransform.translate(i, j);
    ColorModel colorModel = paramRenderedImage.getColorModel();
    BufferedImage bufferedImage = new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
    drawImage(bufferedImage, affineTransform, null);
  }
  
  private boolean clipTo(Rectangle paramRectangle1, Rectangle paramRectangle2) {
    int i = Math.max(paramRectangle1.x, paramRectangle2.x);
    int j = Math.min(paramRectangle1.x + paramRectangle1.width, paramRectangle2.x + paramRectangle2.width);
    int k = Math.max(paramRectangle1.y, paramRectangle2.y);
    int m = Math.min(paramRectangle1.y + paramRectangle1.height, paramRectangle2.y + paramRectangle2.height);
    if (j - i < 0 || m - k < 0) {
      paramRectangle1.width = -1;
      paramRectangle1.height = -1;
      return false;
    } 
    paramRectangle1.x = i;
    paramRectangle1.y = k;
    paramRectangle1.width = j - i;
    paramRectangle1.height = m - k;
    return true;
  }
  
  private void drawTranslatedRenderedImage(RenderedImage paramRenderedImage, Rectangle paramRectangle, int paramInt1, int paramInt2) {
    int i = paramRenderedImage.getTileGridXOffset();
    int j = paramRenderedImage.getTileGridYOffset();
    int k = paramRenderedImage.getTileWidth();
    int m = paramRenderedImage.getTileHeight();
    int n = getTileIndex(paramRectangle.x, i, k);
    int i1 = getTileIndex(paramRectangle.y, j, m);
    int i2 = getTileIndex(paramRectangle.x + paramRectangle.width - 1, i, k);
    int i3 = getTileIndex(paramRectangle.y + paramRectangle.height - 1, j, m);
    ColorModel colorModel = paramRenderedImage.getColorModel();
    Rectangle rectangle = new Rectangle();
    for (int i4 = i1; i4 <= i3; i4++) {
      for (int i5 = n; i5 <= i2; i5++) {
        Raster raster = paramRenderedImage.getTile(i5, i4);
        rectangle.x = i5 * k + i;
        rectangle.y = i4 * m + j;
        rectangle.width = k;
        rectangle.height = m;
        clipTo(rectangle, paramRectangle);
        WritableRaster writableRaster = null;
        if (raster instanceof WritableRaster) {
          writableRaster = (WritableRaster)raster;
        } else {
          writableRaster = Raster.createWritableRaster(raster.getSampleModel(), raster.getDataBuffer(), null);
        } 
        writableRaster = writableRaster.createWritableChild(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 0, 0, null);
        BufferedImage bufferedImage = new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
        copyImage(bufferedImage, rectangle.x + paramInt1, rectangle.y + paramInt2, 0, 0, rectangle.width, rectangle.height, null, null);
      } 
    } 
  }
  
  public void drawRenderableImage(RenderableImage paramRenderableImage, AffineTransform paramAffineTransform) {
    AffineTransform affineTransform3;
    if (paramRenderableImage == null)
      return; 
    AffineTransform affineTransform1 = this.transform;
    AffineTransform affineTransform2 = new AffineTransform(paramAffineTransform);
    affineTransform2.concatenate(affineTransform1);
    RenderContext renderContext = new RenderContext(affineTransform2);
    try {
      affineTransform3 = affineTransform1.createInverse();
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      renderContext = new RenderContext(affineTransform1);
      affineTransform3 = new AffineTransform();
    } 
    RenderedImage renderedImage = paramRenderableImage.createRendering(renderContext);
    drawRenderedImage(renderedImage, affineTransform3);
  }
  
  protected Rectangle transformBounds(Rectangle paramRectangle, AffineTransform paramAffineTransform) {
    if (paramAffineTransform.isIdentity())
      return paramRectangle; 
    Shape shape = transformShape(paramAffineTransform, paramRectangle);
    return shape.getBounds();
  }
  
  public void drawString(String paramString, int paramInt1, int paramInt2) {
    if (paramString == null)
      throw new NullPointerException("String is null"); 
    if (this.font.hasLayoutAttributes()) {
      if (paramString.length() == 0)
        return; 
      (new TextLayout(paramString, this.font, getFontRenderContext())).draw(this, paramInt1, paramInt2);
      return;
    } 
    try {
      this.textpipe.drawString(this, paramString, paramInt1, paramInt2);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.textpipe.drawString(this, paramString, paramInt1, paramInt2);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2) {
    if (paramString == null)
      throw new NullPointerException("String is null"); 
    if (this.font.hasLayoutAttributes()) {
      if (paramString.length() == 0)
        return; 
      (new TextLayout(paramString, this.font, getFontRenderContext())).draw(this, paramFloat1, paramFloat2);
      return;
    } 
    try {
      this.textpipe.drawString(this, paramString, paramFloat1, paramFloat2);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.textpipe.drawString(this, paramString, paramFloat1, paramFloat2);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2) {
    if (paramAttributedCharacterIterator == null)
      throw new NullPointerException("AttributedCharacterIterator is null"); 
    if (paramAttributedCharacterIterator.getBeginIndex() == paramAttributedCharacterIterator.getEndIndex())
      return; 
    TextLayout textLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
    textLayout.draw(this, paramInt1, paramInt2);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2) {
    if (paramAttributedCharacterIterator == null)
      throw new NullPointerException("AttributedCharacterIterator is null"); 
    if (paramAttributedCharacterIterator.getBeginIndex() == paramAttributedCharacterIterator.getEndIndex())
      return; 
    TextLayout textLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
    textLayout.draw(this, paramFloat1, paramFloat2);
  }
  
  public void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {
    if (paramGlyphVector == null)
      throw new NullPointerException("GlyphVector is null"); 
    try {
      this.textpipe.drawGlyphVector(this, paramGlyphVector, paramFloat1, paramFloat2);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.textpipe.drawGlyphVector(this, paramGlyphVector, paramFloat1, paramFloat2);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramArrayOfChar == null)
      throw new NullPointerException("char data is null"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length)
      throw new ArrayIndexOutOfBoundsException("bad offset/length"); 
    if (this.font.hasLayoutAttributes()) {
      if (paramArrayOfChar.length == 0)
        return; 
      (new TextLayout(new String(paramArrayOfChar, paramInt1, paramInt2), this.font, getFontRenderContext())).draw(this, paramInt3, paramInt4);
      return;
    } 
    try {
      this.textpipe.drawChars(this, paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.textpipe.drawChars(this, paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramArrayOfByte == null)
      throw new NullPointerException("byte data is null"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length)
      throw new ArrayIndexOutOfBoundsException("bad offset/length"); 
    char[] arrayOfChar = new char[paramInt2];
    i = paramInt2;
    while (i-- > 0)
      arrayOfChar[i] = (char)(paramArrayOfByte[i + paramInt1] & 0xFF); 
    if (this.font.hasLayoutAttributes()) {
      if (paramArrayOfByte.length == 0)
        return; 
      (new TextLayout(new String(arrayOfChar), this.font, getFontRenderContext())).draw(this, paramInt3, paramInt4);
      return;
    } 
    try {
      this.textpipe.drawChars(this, arrayOfChar, 0, paramInt2, paramInt3, paramInt4);
    } catch (InvalidPipeException i) {
      InvalidPipeException invalidPipeException;
      try {
        revalidateAll();
        this.textpipe.drawChars(this, arrayOfChar, 0, paramInt2, paramInt3, paramInt4);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  private boolean isHiDPIImage(Image paramImage) { return (SurfaceManager.getImageScale(paramImage) != 1 || (this.resolutionVariantHint != 1 && paramImage instanceof MultiResolutionImage)); }
  
  private boolean drawHiDPIImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) {
    if (SurfaceManager.getImageScale(paramImage) != 1) {
      int i = SurfaceManager.getImageScale(paramImage);
      paramInt5 = Region.clipScale(paramInt5, i);
      paramInt7 = Region.clipScale(paramInt7, i);
      paramInt6 = Region.clipScale(paramInt6, i);
      paramInt8 = Region.clipScale(paramInt8, i);
    } else if (paramImage instanceof MultiResolutionImage) {
      int i = paramImage.getWidth(paramImageObserver);
      int j = paramImage.getHeight(paramImageObserver);
      Image image = getResolutionVariant((MultiResolutionImage)paramImage, i, j, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8);
      if (image != paramImage && image != null) {
        ImageObserver imageObserver = MultiResolutionToolkitImage.getResolutionVariantObserver(paramImage, paramImageObserver, i, j, -1, -1);
        int k = image.getWidth(imageObserver);
        int m = image.getHeight(imageObserver);
        if (0 < i && 0 < j && 0 < k && 0 < m) {
          float f1 = k / i;
          float f2 = m / j;
          paramInt5 = Region.clipScale(paramInt5, f1);
          paramInt6 = Region.clipScale(paramInt6, f2);
          paramInt7 = Region.clipScale(paramInt7, f1);
          paramInt8 = Region.clipScale(paramInt8, f2);
          paramImageObserver = imageObserver;
          paramImage = image;
        } 
      } 
    } 
    try {
      return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
      } catch (InvalidPipeException invalidPipeException1) {
        return false;
      } 
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  private Image getResolutionVariant(MultiResolutionImage paramMultiResolutionImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10) {
    double d2;
    double d1;
    if (paramInt1 <= 0 || paramInt2 <= 0)
      return null; 
    int i = paramInt9 - paramInt7;
    int j = paramInt10 - paramInt8;
    if (i == 0 || j == 0)
      return null; 
    int k = this.transform.getType();
    int m = paramInt5 - paramInt3;
    int n = paramInt6 - paramInt4;
    if ((k & 0xFFFFFFBE) == 0) {
      d1 = m;
      d2 = n;
    } else if ((k & 0xFFFFFFB8) == 0) {
      d1 = m * this.transform.getScaleX();
      d2 = n * this.transform.getScaleY();
    } else {
      d1 = m * Math.hypot(this.transform.getScaleX(), this.transform.getShearY());
      d2 = n * Math.hypot(this.transform.getShearX(), this.transform.getScaleY());
    } 
    int i1 = (int)Math.abs(paramInt1 * d1 / i);
    int i2 = (int)Math.abs(paramInt2 * d2 / j);
    Image image = paramMultiResolutionImage.getResolutionVariant(i1, i2);
    return (image instanceof ToolkitImage && ((ToolkitImage)image).hasError()) ? null : image;
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver) { return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, null, paramImageObserver); }
  
  public boolean copyImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver) {
    try {
      return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor, paramImageObserver);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor, paramImageObserver);
      } catch (InvalidPipeException invalidPipeException1) {
        return false;
      } 
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) {
    if (paramImage == null)
      return true; 
    if (paramInt3 == 0 || paramInt4 == 0)
      return true; 
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (isHiDPIImage(paramImage))
      return drawHiDPIImage(paramImage, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, 0, 0, i, j, paramColor, paramImageObserver); 
    if (paramInt3 == i && paramInt4 == j)
      return copyImage(paramImage, paramInt1, paramInt2, 0, 0, paramInt3, paramInt4, paramColor, paramImageObserver); 
    try {
      return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
      } catch (InvalidPipeException invalidPipeException1) {
        return false;
      } 
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) { return drawImage(paramImage, paramInt1, paramInt2, null, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) {
    if (paramImage == null)
      return true; 
    if (isHiDPIImage(paramImage)) {
      int i = paramImage.getWidth(null);
      int j = paramImage.getHeight(null);
      return drawHiDPIImage(paramImage, paramInt1, paramInt2, paramInt1 + i, paramInt2 + j, 0, 0, i, j, paramColor, paramImageObserver);
    } 
    try {
      return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
      } catch (InvalidPipeException invalidPipeException1) {
        return false;
      } 
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver) { return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, null, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) {
    if (paramImage == null)
      return true; 
    if (paramInt1 == paramInt3 || paramInt2 == paramInt4 || paramInt5 == paramInt7 || paramInt6 == paramInt8)
      return true; 
    if (isHiDPIImage(paramImage))
      return drawHiDPIImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver); 
    if (paramInt7 - paramInt5 == paramInt3 - paramInt1 && paramInt8 - paramInt6 == paramInt4 - paramInt2) {
      int i1;
      int n;
      int m;
      int k;
      int j;
      int i;
      if (paramInt7 > paramInt5) {
        n = paramInt7 - paramInt5;
        i = paramInt5;
        k = paramInt1;
      } else {
        n = paramInt5 - paramInt7;
        i = paramInt7;
        k = paramInt3;
      } 
      if (paramInt8 > paramInt6) {
        i1 = paramInt8 - paramInt6;
        j = paramInt6;
        m = paramInt2;
      } else {
        i1 = paramInt6 - paramInt8;
        j = paramInt8;
        m = paramInt4;
      } 
      return copyImage(paramImage, k, m, i, j, n, i1, paramColor, paramImageObserver);
    } 
    try {
      return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
      } catch (InvalidPipeException invalidPipeException1) {
        return false;
      } 
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver) {
    if (paramImage == null)
      return true; 
    if (paramAffineTransform == null || paramAffineTransform.isIdentity())
      return drawImage(paramImage, 0, 0, null, paramImageObserver); 
    if (isHiDPIImage(paramImage)) {
      int i = paramImage.getWidth(null);
      int j = paramImage.getHeight(null);
      AffineTransform affineTransform = new AffineTransform(this.transform);
      transform(paramAffineTransform);
      boolean bool = drawHiDPIImage(paramImage, 0, 0, i, j, 0, 0, i, j, null, paramImageObserver);
      this.transform.setTransform(affineTransform);
      invalidateTransform();
      return bool;
    } 
    try {
      return this.imagepipe.transformImage(this, paramImage, paramAffineTransform, paramImageObserver);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        return this.imagepipe.transformImage(this, paramImage, paramAffineTransform, paramImageObserver);
      } catch (InvalidPipeException invalidPipeException1) {
        return false;
      } 
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2) {
    if (paramBufferedImage == null)
      return; 
    try {
      this.imagepipe.transformImage(this, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2);
    } catch (InvalidPipeException invalidPipeException) {
      try {
        revalidateAll();
        this.imagepipe.transformImage(this, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2);
      } catch (InvalidPipeException invalidPipeException1) {}
    } finally {
      this.surfaceData.markDirty();
    } 
  }
  
  public FontRenderContext getFontRenderContext() {
    if (this.cachedFRC == null) {
      int i = this.textAntialiasHint;
      if (i == 0 && this.antialiasHint == 2)
        i = 2; 
      AffineTransform affineTransform = null;
      if (this.transformState >= 3)
        if (this.transform.getTranslateX() == 0.0D && this.transform.getTranslateY() == 0.0D) {
          affineTransform = this.transform;
        } else {
          affineTransform = new AffineTransform(this.transform.getScaleX(), this.transform.getShearY(), this.transform.getShearX(), this.transform.getScaleY(), 0.0D, 0.0D);
        }  
      this.cachedFRC = new FontRenderContext(affineTransform, SunHints.Value.get(2, i), SunHints.Value.get(3, this.fractionalMetricsHint));
    } 
    return this.cachedFRC;
  }
  
  public void dispose() {
    this.surfaceData = NullSurfaceData.theInstance;
    invalidatePipe();
  }
  
  public void finalize() {}
  
  public Object getDestination() { return this.surfaceData.getDestination(); }
  
  public Surface getDestSurface() { return this.surfaceData; }
  
  static  {
    if (PerformanceLogger.loggingEnabled())
      PerformanceLogger.setTime("SunGraphics2D static initialization"); 
    invalidpipe = new ValidatePipe();
    IDENT_MATRIX = new double[] { 1.0D, 0.0D, 0.0D, 1.0D };
    IDENT_ATX = new AffineTransform();
    textTxArr = new double[17][];
    textAtArr = new AffineTransform[17];
    for (byte b = 8; b < 17; b++) {
      new double[4][0] = b;
      new double[4][1] = 0.0D;
      new double[4][2] = 0.0D;
      new double[4][3] = b;
      textTxArr[b] = new double[4];
      textAtArr[b] = new AffineTransform(textTxArr[b]);
    } 
    MinPenSizeAA = RenderingEngine.getInstance().getMinimumAAPenSize();
    MinPenSizeAASquared = MinPenSizeAA * MinPenSizeAA;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\SunGraphics2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */