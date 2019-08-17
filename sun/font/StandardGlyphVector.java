package sun.font;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import java.text.CharacterIterator;
import sun.java2d.loops.FontInfo;

public class StandardGlyphVector extends GlyphVector {
  private Font font;
  
  private FontRenderContext frc;
  
  private int[] glyphs;
  
  private int[] userGlyphs;
  
  private float[] positions;
  
  private int[] charIndices;
  
  private int flags;
  
  private static final int UNINITIALIZED_FLAGS = -1;
  
  private GlyphTransformInfo gti;
  
  private AffineTransform ftx;
  
  private AffineTransform dtx;
  
  private AffineTransform invdtx;
  
  private AffineTransform frctx;
  
  private Font2D font2D;
  
  private SoftReference fsref;
  
  private SoftReference lbcacheRef;
  
  private SoftReference vbcacheRef;
  
  public static final int FLAG_USES_VERTICAL_BASELINE = 128;
  
  public static final int FLAG_USES_VERTICAL_METRICS = 256;
  
  public static final int FLAG_USES_ALTERNATE_ORIENTATION = 512;
  
  public StandardGlyphVector(Font paramFont, String paramString, FontRenderContext paramFontRenderContext) { init(paramFont, paramString.toCharArray(), 0, paramString.length(), paramFontRenderContext, -1); }
  
  public StandardGlyphVector(Font paramFont, char[] paramArrayOfChar, FontRenderContext paramFontRenderContext) { init(paramFont, paramArrayOfChar, 0, paramArrayOfChar.length, paramFontRenderContext, -1); }
  
  public StandardGlyphVector(Font paramFont, char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext) { init(paramFont, paramArrayOfChar, paramInt1, paramInt2, paramFontRenderContext, -1); }
  
  private float getTracking(Font paramFont) {
    if (paramFont.hasLayoutAttributes()) {
      AttributeValues attributeValues = ((AttributeMap)paramFont.getAttributes()).getValues();
      return attributeValues.getTracking();
    } 
    return 0.0F;
  }
  
  public StandardGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, int[] paramArrayOfInt1, float[] paramArrayOfFloat, int[] paramArrayOfInt2, int paramInt) {
    initGlyphVector(paramFont, paramFontRenderContext, paramArrayOfInt1, paramArrayOfFloat, paramArrayOfInt2, paramInt);
    float f = getTracking(paramFont);
    if (f != 0.0F) {
      f *= paramFont.getSize2D();
      Point2D.Float float = new Point2D.Float(f, 0.0F);
      if (paramFont.isTransformed()) {
        AffineTransform affineTransform = paramFont.getTransform();
        affineTransform.deltaTransform(float, float);
      } 
      Font2D font2D1 = FontUtilities.getFont2D(paramFont);
      FontStrike fontStrike = font2D1.getStrike(paramFont, paramFontRenderContext);
      float[] arrayOfFloat = { float.x, float.y };
      for (int i = 0; i < arrayOfFloat.length; i++) {
        float f1 = arrayOfFloat[i];
        if (f1 != 0.0F) {
          float f2 = 0.0F;
          boolean bool = i;
          byte b = 0;
          while (b < paramArrayOfInt1.length) {
            if (fontStrike.getGlyphAdvance(paramArrayOfInt1[b++]) != 0.0F) {
              paramArrayOfFloat[bool] = paramArrayOfFloat[bool] + f2;
              f2 += f1;
            } 
            bool += true;
          } 
          paramArrayOfFloat[paramArrayOfFloat.length - 2 + i] = paramArrayOfFloat[paramArrayOfFloat.length - 2 + i] + f2;
        } 
      } 
    } 
  }
  
  public void initGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, int[] paramArrayOfInt1, float[] paramArrayOfFloat, int[] paramArrayOfInt2, int paramInt) {
    this.font = paramFont;
    this.frc = paramFontRenderContext;
    this.glyphs = paramArrayOfInt1;
    this.userGlyphs = paramArrayOfInt1;
    this.positions = paramArrayOfFloat;
    this.charIndices = paramArrayOfInt2;
    this.flags = paramInt;
    initFontData();
  }
  
  public StandardGlyphVector(Font paramFont, CharacterIterator paramCharacterIterator, FontRenderContext paramFontRenderContext) {
    int i = paramCharacterIterator.getBeginIndex();
    char[] arrayOfChar = new char[paramCharacterIterator.getEndIndex() - i];
    char c;
    for (c = paramCharacterIterator.first(); c != Character.MAX_VALUE; c = paramCharacterIterator.next())
      arrayOfChar[paramCharacterIterator.getIndex() - i] = c; 
    init(paramFont, arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext, -1);
  }
  
  public StandardGlyphVector(Font paramFont, int[] paramArrayOfInt, FontRenderContext paramFontRenderContext) {
    this.font = paramFont;
    this.frc = paramFontRenderContext;
    this.flags = -1;
    initFontData();
    this.userGlyphs = paramArrayOfInt;
    this.glyphs = getValidatedGlyphs(this.userGlyphs);
  }
  
  public static StandardGlyphVector getStandardGV(GlyphVector paramGlyphVector, FontInfo paramFontInfo) {
    if (paramFontInfo.aaHint == 2) {
      Object object = paramGlyphVector.getFontRenderContext().getAntiAliasingHint();
      if (object != RenderingHints.VALUE_TEXT_ANTIALIAS_ON && object != RenderingHints.VALUE_TEXT_ANTIALIAS_GASP) {
        FontRenderContext fontRenderContext = paramGlyphVector.getFontRenderContext();
        fontRenderContext = new FontRenderContext(fontRenderContext.getTransform(), RenderingHints.VALUE_TEXT_ANTIALIAS_ON, fontRenderContext.getFractionalMetricsHint());
        return new StandardGlyphVector(paramGlyphVector, fontRenderContext);
      } 
    } 
    return (paramGlyphVector instanceof StandardGlyphVector) ? (StandardGlyphVector)paramGlyphVector : new StandardGlyphVector(paramGlyphVector, paramGlyphVector.getFontRenderContext());
  }
  
  public Font getFont() { return this.font; }
  
  public FontRenderContext getFontRenderContext() { return this.frc; }
  
  public void performDefaultLayout() {
    this.positions = null;
    if (getTracking(this.font) == 0.0F)
      clearFlags(2); 
  }
  
  public int getNumGlyphs() { return this.glyphs.length; }
  
  public int getGlyphCode(int paramInt) { return this.userGlyphs[paramInt]; }
  
  public int[] getGlyphCodes(int paramInt1, int paramInt2, int[] paramArrayOfInt) {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("count = " + paramInt2); 
    if (paramInt1 < 0)
      throw new IndexOutOfBoundsException("start = " + paramInt1); 
    if (paramInt1 > this.glyphs.length - paramInt2)
      throw new IndexOutOfBoundsException("start + count = " + (paramInt1 + paramInt2)); 
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[paramInt2]; 
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfInt[i] = this.userGlyphs[i + paramInt1]; 
    return paramArrayOfInt;
  }
  
  public int getGlyphCharIndex(int paramInt) {
    if (paramInt < 0 && paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("" + paramInt); 
    return (this.charIndices == null) ? (((getLayoutFlags() & 0x4) != 0) ? (this.glyphs.length - 1 - paramInt) : paramInt) : this.charIndices[paramInt];
  }
  
  public int[] getGlyphCharIndices(int paramInt1, int paramInt2, int[] paramArrayOfInt) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > this.glyphs.length - paramInt1)
      throw new IndexOutOfBoundsException("" + paramInt1 + ", " + paramInt2); 
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[paramInt2]; 
    if (this.charIndices == null) {
      if ((getLayoutFlags() & 0x4) != 0) {
        byte b = 0;
        for (int i = this.glyphs.length - 1 - paramInt1; b < paramInt2; i--) {
          paramArrayOfInt[b] = i;
          b++;
        } 
      } else {
        byte b = 0;
        for (int i = paramInt1; b < paramInt2; i++) {
          paramArrayOfInt[b] = i;
          b++;
        } 
      } 
    } else {
      for (int i = 0; i < paramInt2; i++)
        paramArrayOfInt[i] = this.charIndices[i + paramInt1]; 
    } 
    return paramArrayOfInt;
  }
  
  public Rectangle2D getLogicalBounds() {
    setFRCTX();
    initPositions();
    LineMetrics lineMetrics = this.font.getLineMetrics("", this.frc);
    float f1 = 0.0F;
    float f2 = -lineMetrics.getAscent();
    float f3 = 0.0F;
    float f4 = lineMetrics.getDescent() + lineMetrics.getLeading();
    if (this.glyphs.length > 0)
      f3 = this.positions[this.positions.length - 2]; 
    return new Rectangle2D.Float(f1, f2, f3 - f1, f4 - f2);
  }
  
  public Rectangle2D getVisualBounds() {
    Rectangle2D rectangle2D = null;
    for (byte b = 0; b < this.glyphs.length; b++) {
      Rectangle2D rectangle2D1 = getGlyphVisualBounds(b).getBounds2D();
      if (!rectangle2D1.isEmpty())
        if (rectangle2D == null) {
          rectangle2D = rectangle2D1;
        } else {
          Rectangle2D.union(rectangle2D, rectangle2D1, rectangle2D);
        }  
    } 
    if (rectangle2D == null)
      rectangle2D = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F); 
    return rectangle2D;
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) { return getGlyphsPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2, 0, this.glyphs.length); }
  
  public Shape getOutline() { return getGlyphsOutline(0, this.glyphs.length, 0.0F, 0.0F); }
  
  public Shape getOutline(float paramFloat1, float paramFloat2) { return getGlyphsOutline(0, this.glyphs.length, paramFloat1, paramFloat2); }
  
  public Shape getGlyphOutline(int paramInt) { return getGlyphsOutline(paramInt, 1, 0.0F, 0.0F); }
  
  public Shape getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2) { return getGlyphsOutline(paramInt, 1, paramFloat1, paramFloat2); }
  
  public Point2D getGlyphPosition(int paramInt) {
    initPositions();
    paramInt *= 2;
    return new Point2D.Float(this.positions[paramInt], this.positions[paramInt + 1]);
  }
  
  public void setGlyphPosition(int paramInt, Point2D paramPoint2D) {
    initPositions();
    int i = paramInt << 1;
    this.positions[i] = (float)paramPoint2D.getX();
    this.positions[i + 1] = (float)paramPoint2D.getY();
    clearCaches(paramInt);
    addFlags(2);
  }
  
  public AffineTransform getGlyphTransform(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    return (this.gti != null) ? this.gti.getGlyphTransform(paramInt) : null;
  }
  
  public void setGlyphTransform(int paramInt, AffineTransform paramAffineTransform) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    if (this.gti == null) {
      if (paramAffineTransform == null || paramAffineTransform.isIdentity())
        return; 
      this.gti = new GlyphTransformInfo(this);
    } 
    this.gti.setGlyphTransform(paramInt, paramAffineTransform);
    if (this.gti.transformCount() == 0)
      this.gti = null; 
  }
  
  public int getLayoutFlags() {
    if (this.flags == -1) {
      this.flags = 0;
      if (this.charIndices != null && this.glyphs.length > 1) {
        boolean bool1 = true;
        boolean bool2 = true;
        int i = this.charIndices.length;
        for (byte b = 0; b < this.charIndices.length && (bool1 || bool2); b++) {
          int j = this.charIndices[b];
          bool1 = (bool1 && j == b) ? 1 : 0;
          bool2 = (bool2 && j == --i) ? 1 : 0;
        } 
        if (bool2)
          this.flags |= 0x4; 
        if (!bool2 && !bool1)
          this.flags |= 0x8; 
      } 
    } 
    return this.flags;
  }
  
  public float[] getGlyphPositions(int paramInt1, int paramInt2, float[] paramArrayOfFloat) {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("count = " + paramInt2); 
    if (paramInt1 < 0)
      throw new IndexOutOfBoundsException("start = " + paramInt1); 
    if (paramInt1 > this.glyphs.length + 1 - paramInt2)
      throw new IndexOutOfBoundsException("start + count = " + (paramInt1 + paramInt2)); 
    return internalGetGlyphPositions(paramInt1, paramInt2, 0, paramArrayOfFloat);
  }
  
  public Shape getGlyphLogicalBounds(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    Shape[] arrayOfShape;
    if (this.lbcacheRef == null || (arrayOfShape = (Shape[])this.lbcacheRef.get()) == null) {
      arrayOfShape = new Shape[this.glyphs.length];
      this.lbcacheRef = new SoftReference(arrayOfShape);
    } 
    Shape shape = arrayOfShape[paramInt];
    if (shape == null) {
      setFRCTX();
      initPositions();
      ADL aDL = new ADL();
      GlyphStrike glyphStrike = getGlyphStrike(paramInt);
      glyphStrike.getADL(aDL);
      Point2D.Float float = glyphStrike.strike.getGlyphMetrics(this.glyphs[paramInt]);
      float f1 = float.x;
      float f2 = float.y;
      float f3 = aDL.descentX + aDL.leadingX + aDL.ascentX;
      float f4 = aDL.descentY + aDL.leadingY + aDL.ascentY;
      float f5 = this.positions[paramInt * 2] + glyphStrike.dx - aDL.ascentX;
      float f6 = this.positions[paramInt * 2 + 1] + glyphStrike.dy - aDL.ascentY;
      GeneralPath generalPath = new GeneralPath();
      generalPath.moveTo(f5, f6);
      generalPath.lineTo(f5 + f1, f6 + f2);
      generalPath.lineTo(f5 + f1 + f3, f6 + f2 + f4);
      generalPath.lineTo(f5 + f3, f6 + f4);
      generalPath.closePath();
      shape = new DelegatingShape(generalPath);
      arrayOfShape[paramInt] = shape;
    } 
    return shape;
  }
  
  public Shape getGlyphVisualBounds(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    Shape[] arrayOfShape;
    if (this.vbcacheRef == null || (arrayOfShape = (Shape[])this.vbcacheRef.get()) == null) {
      arrayOfShape = new Shape[this.glyphs.length];
      this.vbcacheRef = new SoftReference(arrayOfShape);
    } 
    Shape shape = arrayOfShape[paramInt];
    if (shape == null) {
      shape = new DelegatingShape(getGlyphOutlineBounds(paramInt));
      arrayOfShape[paramInt] = shape;
    } 
    return shape;
  }
  
  public Rectangle getGlyphPixelBounds(int paramInt, FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) { return getGlyphsPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2, paramInt, 1); }
  
  public GlyphMetrics getGlyphMetrics(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    Rectangle2D rectangle2D = getGlyphVisualBounds(paramInt).getBounds2D();
    Point2D point2D = getGlyphPosition(paramInt);
    rectangle2D.setRect(rectangle2D.getMinX() - point2D.getX(), rectangle2D.getMinY() - point2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    Point2D.Float float = (getGlyphStrike(paramInt)).strike.getGlyphMetrics(this.glyphs[paramInt]);
    return new GlyphMetrics(true, float.x, float.y, rectangle2D, (byte)0);
  }
  
  public GlyphJustificationInfo getGlyphJustificationInfo(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    return null;
  }
  
  public boolean equals(GlyphVector paramGlyphVector) {
    if (this == paramGlyphVector)
      return true; 
    if (paramGlyphVector == null)
      return false; 
    try {
      StandardGlyphVector standardGlyphVector = (StandardGlyphVector)paramGlyphVector;
      if (this.glyphs.length != standardGlyphVector.glyphs.length)
        return false; 
      byte b;
      for (b = 0; b < this.glyphs.length; b++) {
        if (this.glyphs[b] != standardGlyphVector.glyphs[b])
          return false; 
      } 
      if (!this.font.equals(standardGlyphVector.font))
        return false; 
      if (!this.frc.equals(standardGlyphVector.frc))
        return false; 
      if (((standardGlyphVector.positions == null) ? 1 : 0) != ((this.positions == null) ? 1 : 0))
        if (this.positions == null) {
          initPositions();
        } else {
          standardGlyphVector.initPositions();
        }  
      if (this.positions != null)
        for (b = 0; b < this.positions.length; b++) {
          if (this.positions[b] != standardGlyphVector.positions[b])
            return false; 
        }  
      return (this.gti == null) ? ((standardGlyphVector.gti == null)) : this.gti.equals(standardGlyphVector.gti);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public int hashCode() { return this.font.hashCode() ^ this.glyphs.length; }
  
  public boolean equals(Object paramObject) {
    try {
      return equals((GlyphVector)paramObject);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public StandardGlyphVector copy() { return (StandardGlyphVector)clone(); }
  
  public Object clone() {
    try {
      StandardGlyphVector standardGlyphVector = (StandardGlyphVector)super.clone();
      standardGlyphVector.clearCaches();
      if (this.positions != null)
        standardGlyphVector.positions = (float[])this.positions.clone(); 
      if (this.gti != null)
        standardGlyphVector.gti = new GlyphTransformInfo(standardGlyphVector, this.gti); 
      return standardGlyphVector;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return this;
    } 
  }
  
  public void setGlyphPositions(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 < 0)
      throw new IllegalArgumentException("count = " + paramInt3); 
    initPositions();
    int i = paramInt2 * 2;
    int j = i + paramInt3 * 2;
    for (int k = paramInt1; i < j; k++) {
      this.positions[i] = paramArrayOfFloat[k];
      i++;
    } 
    clearCaches();
    addFlags(2);
  }
  
  public void setGlyphPositions(float[] paramArrayOfFloat) {
    int i = this.glyphs.length * 2 + 2;
    if (paramArrayOfFloat.length != i)
      throw new IllegalArgumentException("srcPositions.length != " + i); 
    this.positions = (float[])paramArrayOfFloat.clone();
    clearCaches();
    addFlags(2);
  }
  
  public float[] getGlyphPositions(float[] paramArrayOfFloat) { return internalGetGlyphPositions(0, this.glyphs.length + 1, 0, paramArrayOfFloat); }
  
  public AffineTransform[] getGlyphTransforms(int paramInt1, int paramInt2, AffineTransform[] paramArrayOfAffineTransform) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > this.glyphs.length)
      throw new IllegalArgumentException("start: " + paramInt1 + " count: " + paramInt2); 
    if (this.gti == null)
      return null; 
    if (paramArrayOfAffineTransform == null)
      paramArrayOfAffineTransform = new AffineTransform[paramInt2]; 
    byte b = 0;
    while (b < paramInt2) {
      paramArrayOfAffineTransform[b] = this.gti.getGlyphTransform(paramInt1);
      b++;
      paramInt1++;
    } 
    return paramArrayOfAffineTransform;
  }
  
  public AffineTransform[] getGlyphTransforms() { return getGlyphTransforms(0, this.glyphs.length, null); }
  
  public void setGlyphTransforms(AffineTransform[] paramArrayOfAffineTransform, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt2;
    int j = paramInt2 + paramInt3;
    while (i < j) {
      setGlyphTransform(i, paramArrayOfAffineTransform[paramInt1 + i]);
      i++;
    } 
  }
  
  public void setGlyphTransforms(AffineTransform[] paramArrayOfAffineTransform) { setGlyphTransforms(paramArrayOfAffineTransform, 0, 0, this.glyphs.length); }
  
  public float[] getGlyphInfo() {
    setFRCTX();
    initPositions();
    float[] arrayOfFloat = new float[this.glyphs.length * 8];
    byte b = 0;
    for (boolean bool = false; b < this.glyphs.length; bool += true) {
      float f1 = this.positions[b * 2];
      float f2 = this.positions[b * 2 + 1];
      arrayOfFloat[bool] = f1;
      arrayOfFloat[bool + true] = f2;
      int i = this.glyphs[b];
      GlyphStrike glyphStrike = getGlyphStrike(b);
      Point2D.Float float = glyphStrike.strike.getGlyphMetrics(i);
      arrayOfFloat[bool + 2] = float.x;
      arrayOfFloat[bool + 3] = float.y;
      Rectangle2D rectangle2D = getGlyphVisualBounds(b).getBounds2D();
      arrayOfFloat[bool + 4] = (float)rectangle2D.getMinX();
      arrayOfFloat[bool + 5] = (float)rectangle2D.getMinY();
      arrayOfFloat[bool + 6] = (float)rectangle2D.getWidth();
      arrayOfFloat[bool + 7] = (float)rectangle2D.getHeight();
      b++;
    } 
    return arrayOfFloat;
  }
  
  public void pixellate(FontRenderContext paramFontRenderContext, Point2D paramPoint2D, Point paramPoint) {
    if (paramFontRenderContext == null)
      paramFontRenderContext = this.frc; 
    AffineTransform affineTransform = paramFontRenderContext.getTransform();
    affineTransform.transform(paramPoint2D, paramPoint2D);
    paramPoint.x = (int)paramPoint2D.getX();
    paramPoint.y = (int)paramPoint2D.getY();
    paramPoint2D.setLocation(paramPoint.x, paramPoint.y);
    try {
      affineTransform.inverseTransform(paramPoint2D, paramPoint2D);
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      throw new IllegalArgumentException("must be able to invert frc transform");
    } 
  }
  
  boolean needsPositions(double[] paramArrayOfDouble) { return (this.gti != null || (getLayoutFlags() & 0x2) != 0 || !matchTX(paramArrayOfDouble, this.frctx)); }
  
  Object setupGlyphImages(long[] paramArrayOfLong, float[] paramArrayOfFloat, double[] paramArrayOfDouble) {
    initPositions();
    setRenderTransform(paramArrayOfDouble);
    if (this.gti != null)
      return this.gti.setupGlyphImages(paramArrayOfLong, paramArrayOfFloat, this.dtx); 
    GlyphStrike glyphStrike = getDefaultStrike();
    glyphStrike.strike.getGlyphImagePtrs(this.glyphs, paramArrayOfLong, this.glyphs.length);
    if (paramArrayOfFloat != null)
      if (this.dtx.isIdentity()) {
        System.arraycopy(this.positions, 0, paramArrayOfFloat, 0, this.glyphs.length * 2);
      } else {
        this.dtx.transform(this.positions, 0, paramArrayOfFloat, 0, this.glyphs.length);
      }  
    return glyphStrike;
  }
  
  private static boolean matchTX(double[] paramArrayOfDouble, AffineTransform paramAffineTransform) { return (paramArrayOfDouble[0] == paramAffineTransform.getScaleX() && paramArrayOfDouble[1] == paramAffineTransform.getShearY() && paramArrayOfDouble[2] == paramAffineTransform.getShearX() && paramArrayOfDouble[3] == paramAffineTransform.getScaleY()); }
  
  private static AffineTransform getNonTranslateTX(AffineTransform paramAffineTransform) {
    if (paramAffineTransform.getTranslateX() != 0.0D || paramAffineTransform.getTranslateY() != 0.0D)
      paramAffineTransform = new AffineTransform(paramAffineTransform.getScaleX(), paramAffineTransform.getShearY(), paramAffineTransform.getShearX(), paramAffineTransform.getScaleY(), 0.0D, 0.0D); 
    return paramAffineTransform;
  }
  
  private static boolean equalNonTranslateTX(AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2) { return (paramAffineTransform1.getScaleX() == paramAffineTransform2.getScaleX() && paramAffineTransform1.getShearY() == paramAffineTransform2.getShearY() && paramAffineTransform1.getShearX() == paramAffineTransform2.getShearX() && paramAffineTransform1.getScaleY() == paramAffineTransform2.getScaleY()); }
  
  private void setRenderTransform(double[] paramArrayOfDouble) {
    assert paramArrayOfDouble.length == 4;
    if (!matchTX(paramArrayOfDouble, this.dtx))
      resetDTX(new AffineTransform(paramArrayOfDouble)); 
  }
  
  private final void setDTX(AffineTransform paramAffineTransform) {
    if (!equalNonTranslateTX(this.dtx, paramAffineTransform))
      resetDTX(getNonTranslateTX(paramAffineTransform)); 
  }
  
  private final void setFRCTX() {
    if (!equalNonTranslateTX(this.frctx, this.dtx))
      resetDTX(getNonTranslateTX(this.frctx)); 
  }
  
  private final void resetDTX(AffineTransform paramAffineTransform) {
    this.fsref = null;
    this.dtx = paramAffineTransform;
    this.invdtx = null;
    if (!this.dtx.isIdentity())
      try {
        this.invdtx = this.dtx.createInverse();
      } catch (NoninvertibleTransformException noninvertibleTransformException) {} 
    if (this.gti != null)
      this.gti.strikesRef = null; 
  }
  
  private StandardGlyphVector(GlyphVector paramGlyphVector, FontRenderContext paramFontRenderContext) {
    this.font = paramGlyphVector.getFont();
    this.frc = paramFontRenderContext;
    initFontData();
    int i = paramGlyphVector.getNumGlyphs();
    this.userGlyphs = paramGlyphVector.getGlyphCodes(0, i, null);
    if (paramGlyphVector instanceof StandardGlyphVector) {
      this.glyphs = this.userGlyphs;
    } else {
      this.glyphs = getValidatedGlyphs(this.userGlyphs);
    } 
    this.flags = paramGlyphVector.getLayoutFlags() & 0xF;
    if ((this.flags & 0x2) != 0)
      this.positions = paramGlyphVector.getGlyphPositions(0, i + 1, null); 
    if ((this.flags & 0x8) != 0)
      this.charIndices = paramGlyphVector.getGlyphCharIndices(0, i, null); 
    if ((this.flags & true) != 0) {
      AffineTransform[] arrayOfAffineTransform = new AffineTransform[i];
      for (byte b = 0; b < i; b++)
        arrayOfAffineTransform[b] = paramGlyphVector.getGlyphTransform(b); 
      setGlyphTransforms(arrayOfAffineTransform);
    } 
  }
  
  int[] getValidatedGlyphs(int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length;
    int[] arrayOfInt = new int[i];
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfInt[b] == 65534 || paramArrayOfInt[b] == 65535) {
        arrayOfInt[b] = paramArrayOfInt[b];
      } else {
        arrayOfInt[b] = this.font2D.getValidatedGlyphCode(paramArrayOfInt[b]);
      } 
    } 
    return arrayOfInt;
  }
  
  private void init(Font paramFont, char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext, int paramInt3) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length)
      throw new ArrayIndexOutOfBoundsException("start or count out of bounds"); 
    this.font = paramFont;
    this.frc = paramFontRenderContext;
    this.flags = paramInt3;
    if (getTracking(paramFont) != 0.0F)
      addFlags(2); 
    if (paramInt1 != 0) {
      char[] arrayOfChar = new char[paramInt2];
      System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, paramInt2);
      paramArrayOfChar = arrayOfChar;
    } 
    initFontData();
    this.glyphs = new int[paramInt2];
    this.userGlyphs = this.glyphs;
    this.font2D.getMapper().charsToGlyphs(paramInt2, paramArrayOfChar, this.glyphs);
  }
  
  private void initFontData() {
    this.font2D = FontUtilities.getFont2D(this.font);
    if (this.font2D instanceof FontSubstitution)
      this.font2D = ((FontSubstitution)this.font2D).getCompositeFont2D(); 
    float f = this.font.getSize2D();
    if (this.font.isTransformed()) {
      this.ftx = this.font.getTransform();
      if (this.ftx.getTranslateX() != 0.0D || this.ftx.getTranslateY() != 0.0D)
        addFlags(2); 
      this.ftx.setTransform(this.ftx.getScaleX(), this.ftx.getShearY(), this.ftx.getShearX(), this.ftx.getScaleY(), 0.0D, 0.0D);
      this.ftx.scale(f, f);
    } else {
      this.ftx = AffineTransform.getScaleInstance(f, f);
    } 
    this.frctx = this.frc.getTransform();
    resetDTX(getNonTranslateTX(this.frctx));
  }
  
  private float[] internalGetGlyphPositions(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat) {
    if (paramArrayOfFloat == null)
      paramArrayOfFloat = new float[paramInt3 + paramInt2 * 2]; 
    initPositions();
    int i = paramInt3;
    int j = paramInt3 + paramInt2 * 2;
    for (int k = paramInt1 * 2; i < j; k++) {
      paramArrayOfFloat[i] = this.positions[k];
      i++;
    } 
    return paramArrayOfFloat;
  }
  
  private Rectangle2D getGlyphOutlineBounds(int paramInt) {
    setFRCTX();
    initPositions();
    return getGlyphStrike(paramInt).getGlyphOutlineBounds(this.glyphs[paramInt], this.positions[paramInt * 2], this.positions[paramInt * 2 + 1]);
  }
  
  private Shape getGlyphsOutline(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2) {
    setFRCTX();
    initPositions();
    GeneralPath generalPath = new GeneralPath(1);
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1 * 2; i < j; k += 2) {
      float f1 = paramFloat1 + this.positions[k];
      float f2 = paramFloat2 + this.positions[k + 1];
      getGlyphStrike(i).appendGlyphOutline(this.glyphs[i], generalPath, f1, f2);
      i++;
    } 
    return generalPath;
  }
  
  private Rectangle getGlyphsPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2) {
    initPositions();
    AffineTransform affineTransform = null;
    if (paramFontRenderContext == null || paramFontRenderContext.equals(this.frc)) {
      affineTransform = this.frctx;
    } else {
      affineTransform = paramFontRenderContext.getTransform();
    } 
    setDTX(affineTransform);
    if (this.gti != null)
      return this.gti.getGlyphsPixelBounds(affineTransform, paramFloat1, paramFloat2, paramInt1, paramInt2); 
    FontStrike fontStrike = (getDefaultStrike()).strike;
    Rectangle rectangle1 = null;
    Rectangle rectangle2 = new Rectangle();
    Point2D.Float float = new Point2D.Float();
    int i = paramInt1 * 2;
    while (--paramInt2 >= 0) {
      float.x = paramFloat1 + this.positions[i++];
      float.y = paramFloat2 + this.positions[i++];
      affineTransform.transform(float, float);
      fontStrike.getGlyphImageBounds(this.glyphs[paramInt1++], float, rectangle2);
      if (!rectangle2.isEmpty()) {
        if (rectangle1 == null) {
          rectangle1 = new Rectangle(rectangle2);
          continue;
        } 
        rectangle1.add(rectangle2);
      } 
    } 
    return (rectangle1 != null) ? rectangle1 : rectangle2;
  }
  
  private void clearCaches(int paramInt) {
    if (this.lbcacheRef != null) {
      Shape[] arrayOfShape = (Shape[])this.lbcacheRef.get();
      if (arrayOfShape != null)
        arrayOfShape[paramInt] = null; 
    } 
    if (this.vbcacheRef != null) {
      Shape[] arrayOfShape = (Shape[])this.vbcacheRef.get();
      if (arrayOfShape != null)
        arrayOfShape[paramInt] = null; 
    } 
  }
  
  private void clearCaches() {
    this.lbcacheRef = null;
    this.vbcacheRef = null;
  }
  
  private void initPositions() {
    if (this.positions == null) {
      setFRCTX();
      this.positions = new float[this.glyphs.length * 2 + 2];
      Point2D.Float float1 = null;
      float f = getTracking(this.font);
      if (f != 0.0F) {
        f *= this.font.getSize2D();
        float1 = new Point2D.Float(f, 0.0F);
      } 
      Point2D.Float float2 = new Point2D.Float(0.0F, 0.0F);
      if (this.font.isTransformed()) {
        AffineTransform affineTransform = this.font.getTransform();
        affineTransform.transform(float2, float2);
        this.positions[0] = float2.x;
        this.positions[1] = float2.y;
        if (float1 != null)
          affineTransform.deltaTransform(float1, float1); 
      } 
      byte b1 = 0;
      for (byte b2 = 2; b1 < this.glyphs.length; b2 += 2) {
        getGlyphStrike(b1).addDefaultGlyphAdvance(this.glyphs[b1], float2);
        if (float1 != null) {
          float2.x += float1.x;
          float2.y += float1.y;
        } 
        this.positions[b2] = float2.x;
        this.positions[b2 + 1] = float2.y;
        b1++;
      } 
    } 
  }
  
  private void addFlags(int paramInt) { this.flags = getLayoutFlags() | paramInt; }
  
  private void clearFlags(int paramInt) { this.flags = getLayoutFlags() & (paramInt ^ 0xFFFFFFFF); }
  
  private GlyphStrike getGlyphStrike(int paramInt) { return (this.gti == null) ? getDefaultStrike() : this.gti.getStrike(paramInt); }
  
  private GlyphStrike getDefaultStrike() {
    GlyphStrike glyphStrike = null;
    if (this.fsref != null)
      glyphStrike = (GlyphStrike)this.fsref.get(); 
    if (glyphStrike == null) {
      glyphStrike = GlyphStrike.create(this, this.dtx, null);
      this.fsref = new SoftReference(glyphStrike);
    } 
    return glyphStrike;
  }
  
  public String toString() { return appendString(null).toString(); }
  
  StringBuffer appendString(StringBuffer paramStringBuffer) {
    if (paramStringBuffer == null)
      paramStringBuffer = new StringBuffer(); 
    try {
      paramStringBuffer.append("SGV{font: ");
      paramStringBuffer.append(this.font.toString());
      paramStringBuffer.append(", frc: ");
      paramStringBuffer.append(this.frc.toString());
      paramStringBuffer.append(", glyphs: (");
      paramStringBuffer.append(this.glyphs.length);
      paramStringBuffer.append(")[");
      byte b;
      for (b = 0; b < this.glyphs.length; b++) {
        if (b)
          paramStringBuffer.append(", "); 
        paramStringBuffer.append(Integer.toHexString(this.glyphs[b]));
      } 
      paramStringBuffer.append("]");
      if (this.positions != null) {
        paramStringBuffer.append(", positions: (");
        paramStringBuffer.append(this.positions.length);
        paramStringBuffer.append(")[");
        for (b = 0; b < this.positions.length; b += 2) {
          if (b > 0)
            paramStringBuffer.append(", "); 
          paramStringBuffer.append(this.positions[b]);
          paramStringBuffer.append("@");
          paramStringBuffer.append(this.positions[b + 1]);
        } 
        paramStringBuffer.append("]");
      } 
      if (this.charIndices != null) {
        paramStringBuffer.append(", indices: (");
        paramStringBuffer.append(this.charIndices.length);
        paramStringBuffer.append(")[");
        for (b = 0; b < this.charIndices.length; b++) {
          if (b > 0)
            paramStringBuffer.append(", "); 
          paramStringBuffer.append(this.charIndices[b]);
        } 
        paramStringBuffer.append("]");
      } 
      paramStringBuffer.append(", flags:");
      if (getLayoutFlags() == 0) {
        paramStringBuffer.append(" default");
      } else {
        if ((this.flags & true) != 0)
          paramStringBuffer.append(" tx"); 
        if ((this.flags & 0x2) != 0)
          paramStringBuffer.append(" pos"); 
        if ((this.flags & 0x4) != 0)
          paramStringBuffer.append(" rtl"); 
        if ((this.flags & 0x8) != 0)
          paramStringBuffer.append(" complex"); 
      } 
    } catch (Exception exception) {
      paramStringBuffer.append(" " + exception.getMessage());
    } 
    paramStringBuffer.append("}");
    return paramStringBuffer;
  }
  
  static class ADL {
    public float ascentX;
    
    public float ascentY;
    
    public float descentX;
    
    public float descentY;
    
    public float leadingX;
    
    public float leadingY;
    
    public String toString() { return toStringBuffer(null).toString(); }
    
    protected StringBuffer toStringBuffer(StringBuffer param1StringBuffer) {
      if (param1StringBuffer == null)
        param1StringBuffer = new StringBuffer(); 
      param1StringBuffer.append("ax: ");
      param1StringBuffer.append(this.ascentX);
      param1StringBuffer.append(" ay: ");
      param1StringBuffer.append(this.ascentY);
      param1StringBuffer.append(" dx: ");
      param1StringBuffer.append(this.descentX);
      param1StringBuffer.append(" dy: ");
      param1StringBuffer.append(this.descentY);
      param1StringBuffer.append(" lx: ");
      param1StringBuffer.append(this.leadingX);
      param1StringBuffer.append(" ly: ");
      param1StringBuffer.append(this.leadingY);
      return param1StringBuffer;
    }
  }
  
  public static final class GlyphStrike {
    StandardGlyphVector sgv;
    
    FontStrike strike;
    
    float dx;
    
    float dy;
    
    static GlyphStrike create(StandardGlyphVector param1StandardGlyphVector, AffineTransform param1AffineTransform1, AffineTransform param1AffineTransform2) {
      float f1 = 0.0F;
      float f2 = 0.0F;
      AffineTransform affineTransform = param1StandardGlyphVector.ftx;
      if (!param1AffineTransform1.isIdentity() || param1AffineTransform2 != null) {
        affineTransform = new AffineTransform(param1StandardGlyphVector.ftx);
        if (param1AffineTransform2 != null) {
          affineTransform.preConcatenate(param1AffineTransform2);
          f1 = (float)affineTransform.getTranslateX();
          f2 = (float)affineTransform.getTranslateY();
        } 
        if (!param1AffineTransform1.isIdentity())
          affineTransform.preConcatenate(param1AffineTransform1); 
      } 
      int i = 1;
      Object object = param1StandardGlyphVector.frc.getAntiAliasingHint();
      if (object == RenderingHints.VALUE_TEXT_ANTIALIAS_GASP && !affineTransform.isIdentity() && (affineTransform.getType() & 0xFFFFFFFE) != 0) {
        double d = affineTransform.getShearX();
        if (d != 0.0D) {
          double d1 = affineTransform.getScaleY();
          i = (int)Math.sqrt(d * d + d1 * d1);
        } else {
          i = (int)Math.abs(affineTransform.getScaleY());
        } 
      } 
      int j = FontStrikeDesc.getAAHintIntVal(object, param1StandardGlyphVector.font2D, i);
      int k = FontStrikeDesc.getFMHintIntVal(param1StandardGlyphVector.frc.getFractionalMetricsHint());
      FontStrikeDesc fontStrikeDesc = new FontStrikeDesc(param1AffineTransform1, affineTransform, param1StandardGlyphVector.font.getStyle(), j, k);
      Font2D font2D = param1StandardGlyphVector.font2D;
      if (font2D instanceof FontSubstitution)
        font2D = ((FontSubstitution)font2D).getCompositeFont2D(); 
      FontStrike fontStrike = font2D.handle.font2D.getStrike(fontStrikeDesc);
      return new GlyphStrike(param1StandardGlyphVector, fontStrike, f1, f2);
    }
    
    private GlyphStrike(StandardGlyphVector param1StandardGlyphVector, FontStrike param1FontStrike, float param1Float1, float param1Float2) {
      this.sgv = param1StandardGlyphVector;
      this.strike = param1FontStrike;
      this.dx = param1Float1;
      this.dy = param1Float2;
    }
    
    void getADL(StandardGlyphVector.ADL param1ADL) {
      StrikeMetrics strikeMetrics = this.strike.getFontMetrics();
      Point2D.Float float = null;
      if (this.sgv.font.isTransformed()) {
        float = new Point2D.Float();
        float.x = (float)this.sgv.font.getTransform().getTranslateX();
        float.y = (float)this.sgv.font.getTransform().getTranslateY();
      } 
      param1ADL.ascentX = -strikeMetrics.ascentX;
      param1ADL.ascentY = -strikeMetrics.ascentY;
      param1ADL.descentX = strikeMetrics.descentX;
      param1ADL.descentY = strikeMetrics.descentY;
      param1ADL.leadingX = strikeMetrics.leadingX;
      param1ADL.leadingY = strikeMetrics.leadingY;
    }
    
    void getGlyphPosition(int param1Int1, int param1Int2, float[] param1ArrayOfFloat1, float[] param1ArrayOfFloat2) {
      param1ArrayOfFloat2[param1Int2] = param1ArrayOfFloat1[param1Int2] + this.dx;
      param1ArrayOfFloat2[++param1Int2] = param1ArrayOfFloat1[param1Int2] + this.dy;
    }
    
    void addDefaultGlyphAdvance(int param1Int, Point2D.Float param1Float) {
      Point2D.Float float = this.strike.getGlyphMetrics(param1Int);
      param1Float.x += float.x + this.dx;
      param1Float.y += float.y + this.dy;
    }
    
    Rectangle2D getGlyphOutlineBounds(int param1Int, float param1Float1, float param1Float2) {
      Rectangle2D rectangle2D = null;
      if (this.sgv.invdtx == null) {
        rectangle2D = new Rectangle2D.Float();
        rectangle2D.setRect(this.strike.getGlyphOutlineBounds(param1Int));
      } else {
        GeneralPath generalPath = this.strike.getGlyphOutline(param1Int, 0.0F, 0.0F);
        generalPath.transform(this.sgv.invdtx);
        rectangle2D = generalPath.getBounds2D();
      } 
      if (!rectangle2D.isEmpty())
        rectangle2D.setRect(rectangle2D.getMinX() + param1Float1 + this.dx, rectangle2D.getMinY() + param1Float2 + this.dy, rectangle2D.getWidth(), rectangle2D.getHeight()); 
      return rectangle2D;
    }
    
    void appendGlyphOutline(int param1Int, GeneralPath param1GeneralPath, float param1Float1, float param1Float2) {
      GeneralPath generalPath = null;
      if (this.sgv.invdtx == null) {
        generalPath = this.strike.getGlyphOutline(param1Int, param1Float1 + this.dx, param1Float2 + this.dy);
      } else {
        generalPath = this.strike.getGlyphOutline(param1Int, 0.0F, 0.0F);
        generalPath.transform(this.sgv.invdtx);
        generalPath.transform(AffineTransform.getTranslateInstance((param1Float1 + this.dx), (param1Float2 + this.dy)));
      } 
      PathIterator pathIterator = generalPath.getPathIterator(null);
      param1GeneralPath.append(pathIterator, false);
    }
  }
  
  static final class GlyphTransformInfo {
    StandardGlyphVector sgv;
    
    int[] indices;
    
    double[] transforms;
    
    SoftReference strikesRef;
    
    boolean haveAllStrikes;
    
    GlyphTransformInfo(StandardGlyphVector param1StandardGlyphVector) { this.sgv = param1StandardGlyphVector; }
    
    GlyphTransformInfo(StandardGlyphVector param1StandardGlyphVector, GlyphTransformInfo param1GlyphTransformInfo) {
      this.sgv = param1StandardGlyphVector;
      this.indices = (param1GlyphTransformInfo.indices == null) ? null : (int[])param1GlyphTransformInfo.indices.clone();
      this.transforms = (param1GlyphTransformInfo.transforms == null) ? null : (double[])param1GlyphTransformInfo.transforms.clone();
      this.strikesRef = null;
    }
    
    public boolean equals(GlyphTransformInfo param1GlyphTransformInfo) {
      if (param1GlyphTransformInfo == null)
        return false; 
      if (param1GlyphTransformInfo == this)
        return true; 
      if (this.indices.length != param1GlyphTransformInfo.indices.length)
        return false; 
      if (this.transforms.length != param1GlyphTransformInfo.transforms.length)
        return false; 
      for (byte b = 0; b < this.indices.length; b++) {
        int i = this.indices[b];
        int j = param1GlyphTransformInfo.indices[b];
        if (((i == 0) ? 1 : 0) != ((j == 0) ? 1 : 0))
          return false; 
        if (i != 0) {
          i *= 6;
          j *= 6;
          for (byte b1 = 6; b1 > 0; b1--) {
            if (this.indices[--i] != param1GlyphTransformInfo.indices[--j])
              return false; 
          } 
        } 
      } 
      return true;
    }
    
    void setGlyphTransform(int param1Int, AffineTransform param1AffineTransform) {
      double[] arrayOfDouble = new double[6];
      boolean bool = true;
      arrayOfDouble[3] = 1.0D;
      arrayOfDouble[0] = 1.0D;
      bool = false;
      param1AffineTransform.getMatrix(arrayOfDouble);
      if (this.indices == null) {
        if (bool)
          return; 
        this.indices = new int[this.sgv.glyphs.length];
        this.indices[param1Int] = 1;
        this.transforms = arrayOfDouble;
      } else {
        boolean bool1 = false;
        int i = -1;
        if (bool) {
          i = 0;
        } else {
          bool1 = true;
          byte b = 0;
          while (b < this.transforms.length) {
            for (byte b1 = 0; b1 < 6; b1++) {
              if (this.transforms[b + b1] != arrayOfDouble[b1]) {
                b += 6;
                continue;
              } 
            } 
            bool1 = false;
          } 
          i = b / 6 + 1;
        } 
        int j = this.indices[param1Int];
        if (i != j) {
          boolean bool2 = false;
          if (j != 0) {
            bool2 = true;
            for (byte b = 0; b < this.indices.length; b++) {
              if (this.indices[b] == j && b != param1Int) {
                bool2 = false;
                break;
              } 
            } 
          } 
          if (bool2 && bool1) {
            i = j;
            System.arraycopy(arrayOfDouble, 0, this.transforms, (i - 1) * 6, 6);
          } else if (bool2) {
            if (this.transforms.length == 6) {
              this.indices = null;
              this.transforms = null;
              this.sgv.clearCaches(param1Int);
              this.sgv.clearFlags(1);
              this.strikesRef = null;
              return;
            } 
            double[] arrayOfDouble1 = new double[this.transforms.length - 6];
            System.arraycopy(this.transforms, 0, arrayOfDouble1, 0, (j - 1) * 6);
            System.arraycopy(this.transforms, j * 6, arrayOfDouble1, (j - 1) * 6, this.transforms.length - j * 6);
            this.transforms = arrayOfDouble1;
            for (byte b = 0; b < this.indices.length; b++) {
              if (this.indices[b] > j)
                this.indices[b] = this.indices[b] - 1; 
            } 
            if (i > j)
              i--; 
          } else if (bool1) {
            double[] arrayOfDouble1 = new double[this.transforms.length + 6];
            System.arraycopy(this.transforms, 0, arrayOfDouble1, 0, this.transforms.length);
            System.arraycopy(arrayOfDouble, 0, arrayOfDouble1, this.transforms.length, 6);
            this.transforms = arrayOfDouble1;
          } 
          this.indices[param1Int] = i;
        } 
      } 
      this.sgv.clearCaches(param1Int);
      this.sgv.addFlags(1);
      this.strikesRef = null;
    }
    
    AffineTransform getGlyphTransform(int param1Int) {
      int i = this.indices[param1Int];
      if (i == 0)
        return null; 
      int j = (i - 1) * 6;
      return new AffineTransform(this.transforms[j + 0], this.transforms[j + 1], this.transforms[j + 2], this.transforms[j + 3], this.transforms[j + 4], this.transforms[j + 5]);
    }
    
    int transformCount() { return (this.transforms == null) ? 0 : (this.transforms.length / 6); }
    
    Object setupGlyphImages(long[] param1ArrayOfLong, float[] param1ArrayOfFloat, AffineTransform param1AffineTransform) {
      int i = this.sgv.glyphs.length;
      StandardGlyphVector.GlyphStrike[] arrayOfGlyphStrike = getAllStrikes();
      for (byte b = 0; b < i; b++) {
        StandardGlyphVector.GlyphStrike glyphStrike = arrayOfGlyphStrike[this.indices[b]];
        int j = this.sgv.glyphs[b];
        param1ArrayOfLong[b] = glyphStrike.strike.getGlyphImagePtr(j);
        glyphStrike.getGlyphPosition(j, b * 2, this.sgv.positions, param1ArrayOfFloat);
      } 
      param1AffineTransform.transform(param1ArrayOfFloat, 0, param1ArrayOfFloat, 0, i);
      return arrayOfGlyphStrike;
    }
    
    Rectangle getGlyphsPixelBounds(AffineTransform param1AffineTransform, float param1Float1, float param1Float2, int param1Int1, int param1Int2) {
      Rectangle rectangle1 = null;
      Rectangle rectangle2 = new Rectangle();
      Point2D.Float float = new Point2D.Float();
      int i = param1Int1 * 2;
      while (--param1Int2 >= 0) {
        StandardGlyphVector.GlyphStrike glyphStrike = getStrike(param1Int1);
        float.x = param1Float1 + this.sgv.positions[i++] + glyphStrike.dx;
        float.y = param1Float2 + this.sgv.positions[i++] + glyphStrike.dy;
        param1AffineTransform.transform(float, float);
        glyphStrike.strike.getGlyphImageBounds(this.sgv.glyphs[param1Int1++], float, rectangle2);
        if (!rectangle2.isEmpty()) {
          if (rectangle1 == null) {
            rectangle1 = new Rectangle(rectangle2);
            continue;
          } 
          rectangle1.add(rectangle2);
        } 
      } 
      return (rectangle1 != null) ? rectangle1 : rectangle2;
    }
    
    StandardGlyphVector.GlyphStrike getStrike(int param1Int) {
      if (this.indices != null) {
        StandardGlyphVector.GlyphStrike[] arrayOfGlyphStrike = getStrikeArray();
        return getStrikeAtIndex(arrayOfGlyphStrike, this.indices[param1Int]);
      } 
      return this.sgv.getDefaultStrike();
    }
    
    private StandardGlyphVector.GlyphStrike[] getAllStrikes() {
      if (this.indices == null)
        return null; 
      StandardGlyphVector.GlyphStrike[] arrayOfGlyphStrike = getStrikeArray();
      if (!this.haveAllStrikes) {
        for (byte b = 0; b < arrayOfGlyphStrike.length; b++)
          getStrikeAtIndex(arrayOfGlyphStrike, b); 
        this.haveAllStrikes = true;
      } 
      return arrayOfGlyphStrike;
    }
    
    private StandardGlyphVector.GlyphStrike[] getStrikeArray() {
      StandardGlyphVector.GlyphStrike[] arrayOfGlyphStrike = null;
      if (this.strikesRef != null)
        arrayOfGlyphStrike = (GlyphStrike[])this.strikesRef.get(); 
      if (arrayOfGlyphStrike == null) {
        this.haveAllStrikes = false;
        arrayOfGlyphStrike = new StandardGlyphVector.GlyphStrike[transformCount() + 1];
        this.strikesRef = new SoftReference(arrayOfGlyphStrike);
      } 
      return arrayOfGlyphStrike;
    }
    
    private StandardGlyphVector.GlyphStrike getStrikeAtIndex(StandardGlyphVector.GlyphStrike[] param1ArrayOfGlyphStrike, int param1Int) {
      StandardGlyphVector.GlyphStrike glyphStrike = param1ArrayOfGlyphStrike[param1Int];
      if (glyphStrike == null) {
        if (param1Int == 0) {
          glyphStrike = this.sgv.getDefaultStrike();
        } else {
          int i = (param1Int - 1) * 6;
          AffineTransform affineTransform = new AffineTransform(this.transforms[i], this.transforms[i + 1], this.transforms[i + 2], this.transforms[i + 3], this.transforms[i + 4], this.transforms[i + 5]);
          glyphStrike = StandardGlyphVector.GlyphStrike.create(this.sgv, this.sgv.dtx, affineTransform);
        } 
        param1ArrayOfGlyphStrike[param1Int] = glyphStrike;
      } 
      return glyphStrike;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\StandardGlyphVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */