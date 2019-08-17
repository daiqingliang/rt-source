package java.awt.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;
import sun.font.AttributeValues;
import sun.font.CoreMetrics;
import sun.font.FontResolver;
import sun.font.GraphicComponent;
import sun.font.LayoutPathImpl;
import sun.text.CodePointIterator;

public final class TextLayout implements Cloneable {
  private int characterCount;
  
  private boolean isVerticalLine = false;
  
  private byte baseline;
  
  private float[] baselineOffsets;
  
  private TextLine textLine;
  
  private TextLine.TextLineMetrics lineMetrics = null;
  
  private float visibleAdvance;
  
  private int hashCodeCache;
  
  private boolean cacheIsValid = false;
  
  private float justifyRatio;
  
  private static final float ALREADY_JUSTIFIED = -53.9F;
  
  private static float dx;
  
  private static float dy;
  
  private Rectangle2D naturalBounds = null;
  
  private Rectangle2D boundsRect = null;
  
  private boolean caretsInLigaturesAreAllowed = false;
  
  public static final CaretPolicy DEFAULT_CARET_POLICY = new CaretPolicy();
  
  public TextLayout(String paramString, Font paramFont, FontRenderContext paramFontRenderContext) {
    if (paramFont == null)
      throw new IllegalArgumentException("Null font passed to TextLayout constructor."); 
    if (paramString == null)
      throw new IllegalArgumentException("Null string passed to TextLayout constructor."); 
    if (paramString.length() == 0)
      throw new IllegalArgumentException("Zero length string passed to TextLayout constructor."); 
    Map map = null;
    if (paramFont.hasLayoutAttributes())
      map = paramFont.getAttributes(); 
    char[] arrayOfChar = paramString.toCharArray();
    if (sameBaselineUpTo(paramFont, arrayOfChar, 0, arrayOfChar.length) == arrayOfChar.length) {
      fastInit(arrayOfChar, paramFont, map, paramFontRenderContext);
    } else {
      AttributedString attributedString = (map == null) ? new AttributedString(paramString) : new AttributedString(paramString, map);
      attributedString.addAttribute(TextAttribute.FONT, paramFont);
      standardInit(attributedString.getIterator(), arrayOfChar, paramFontRenderContext);
    } 
  }
  
  public TextLayout(String paramString, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, FontRenderContext paramFontRenderContext) {
    if (paramString == null)
      throw new IllegalArgumentException("Null string passed to TextLayout constructor."); 
    if (paramMap == null)
      throw new IllegalArgumentException("Null map passed to TextLayout constructor."); 
    if (paramString.length() == 0)
      throw new IllegalArgumentException("Zero length string passed to TextLayout constructor."); 
    char[] arrayOfChar = paramString.toCharArray();
    Font font = singleFont(arrayOfChar, 0, arrayOfChar.length, paramMap);
    if (font != null) {
      fastInit(arrayOfChar, font, paramMap, paramFontRenderContext);
    } else {
      AttributedString attributedString = new AttributedString(paramString, paramMap);
      standardInit(attributedString.getIterator(), arrayOfChar, paramFontRenderContext);
    } 
  }
  
  private static Font singleFont(char[] paramArrayOfChar, int paramInt1, int paramInt2, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) {
    if (paramMap.get(TextAttribute.CHAR_REPLACEMENT) != null)
      return null; 
    Font font = null;
    try {
      font = (Font)paramMap.get(TextAttribute.FONT);
    } catch (ClassCastException classCastException) {}
    if (font == null)
      if (paramMap.get(TextAttribute.FAMILY) != null) {
        font = Font.getFont(paramMap);
        if (font.canDisplayUpTo(paramArrayOfChar, paramInt1, paramInt2) != -1)
          return null; 
      } else {
        FontResolver fontResolver = FontResolver.getInstance();
        CodePointIterator codePointIterator = CodePointIterator.create(paramArrayOfChar, paramInt1, paramInt2);
        int i = fontResolver.nextFontRunIndex(codePointIterator);
        if (codePointIterator.charIndex() == paramInt2)
          font = fontResolver.getFont(i, paramMap); 
      }  
    return (sameBaselineUpTo(font, paramArrayOfChar, paramInt1, paramInt2) != paramInt2) ? null : font;
  }
  
  public TextLayout(AttributedCharacterIterator paramAttributedCharacterIterator, FontRenderContext paramFontRenderContext) {
    if (paramAttributedCharacterIterator == null)
      throw new IllegalArgumentException("Null iterator passed to TextLayout constructor."); 
    int i = paramAttributedCharacterIterator.getBeginIndex();
    int j = paramAttributedCharacterIterator.getEndIndex();
    if (i == j)
      throw new IllegalArgumentException("Zero length iterator passed to TextLayout constructor."); 
    int k = j - i;
    paramAttributedCharacterIterator.first();
    char[] arrayOfChar = new char[k];
    byte b = 0;
    char c;
    for (c = paramAttributedCharacterIterator.first(); c != Character.MAX_VALUE; c = paramAttributedCharacterIterator.next())
      arrayOfChar[b++] = c; 
    paramAttributedCharacterIterator.first();
    if (paramAttributedCharacterIterator.getRunLimit() == j) {
      Map map = paramAttributedCharacterIterator.getAttributes();
      Font font = singleFont(arrayOfChar, 0, k, map);
      if (font != null) {
        fastInit(arrayOfChar, font, map, paramFontRenderContext);
        return;
      } 
    } 
    standardInit(paramAttributedCharacterIterator, arrayOfChar, paramFontRenderContext);
  }
  
  TextLayout(TextLine paramTextLine, byte paramByte, float[] paramArrayOfFloat, float paramFloat) {
    this.characterCount = paramTextLine.characterCount();
    this.baseline = paramByte;
    this.baselineOffsets = paramArrayOfFloat;
    this.textLine = paramTextLine;
    this.justifyRatio = paramFloat;
  }
  
  private void paragraphInit(byte paramByte, CoreMetrics paramCoreMetrics, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, char[] paramArrayOfChar) {
    this.baseline = paramByte;
    this.baselineOffsets = TextLine.getNormalizedOffsets(paramCoreMetrics.baselineOffsets, this.baseline);
    this.justifyRatio = AttributeValues.getJustification(paramMap);
    NumericShaper numericShaper = AttributeValues.getNumericShaping(paramMap);
    if (numericShaper != null)
      numericShaper.shape(paramArrayOfChar, 0, paramArrayOfChar.length); 
  }
  
  private void fastInit(char[] paramArrayOfChar, Font paramFont, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, FontRenderContext paramFontRenderContext) {
    this.isVerticalLine = false;
    LineMetrics lineMetrics1 = paramFont.getLineMetrics(paramArrayOfChar, 0, paramArrayOfChar.length, paramFontRenderContext);
    CoreMetrics coreMetrics = CoreMetrics.get(lineMetrics1);
    byte b = (byte)coreMetrics.baselineIndex;
    if (paramMap == null) {
      this.baseline = b;
      this.baselineOffsets = coreMetrics.baselineOffsets;
      this.justifyRatio = 1.0F;
    } else {
      paragraphInit(b, coreMetrics, paramMap, paramArrayOfChar);
    } 
    this.characterCount = paramArrayOfChar.length;
    this.textLine = TextLine.fastCreateTextLine(paramFontRenderContext, paramArrayOfChar, paramFont, coreMetrics, paramMap);
  }
  
  private void standardInit(AttributedCharacterIterator paramAttributedCharacterIterator, char[] paramArrayOfChar, FontRenderContext paramFontRenderContext) {
    this.characterCount = paramArrayOfChar.length;
    Map map = paramAttributedCharacterIterator.getAttributes();
    boolean bool = TextLine.advanceToFirstFont(paramAttributedCharacterIterator);
    if (bool) {
      Font font = TextLine.getFontAtCurrentPos(paramAttributedCharacterIterator);
      int i = paramAttributedCharacterIterator.getIndex() - paramAttributedCharacterIterator.getBeginIndex();
      LineMetrics lineMetrics1 = font.getLineMetrics(paramArrayOfChar, i, i + 1, paramFontRenderContext);
      CoreMetrics coreMetrics = CoreMetrics.get(lineMetrics1);
      paragraphInit((byte)coreMetrics.baselineIndex, coreMetrics, map, paramArrayOfChar);
    } else {
      GraphicAttribute graphicAttribute = (GraphicAttribute)map.get(TextAttribute.CHAR_REPLACEMENT);
      byte b = getBaselineFromGraphic(graphicAttribute);
      CoreMetrics coreMetrics = GraphicComponent.createCoreMetrics(graphicAttribute);
      paragraphInit(b, coreMetrics, map, paramArrayOfChar);
    } 
    this.textLine = TextLine.standardCreateTextLine(paramFontRenderContext, paramAttributedCharacterIterator, paramArrayOfChar, this.baselineOffsets);
  }
  
  private void ensureCache() {
    if (!this.cacheIsValid)
      buildCache(); 
  }
  
  private void buildCache() {
    this.lineMetrics = this.textLine.getMetrics();
    if (this.textLine.isDirectionLTR()) {
      int i;
      for (i = this.characterCount - 1; i != -1; i--) {
        int j = this.textLine.visualToLogical(i);
        if (!this.textLine.isCharSpace(j))
          break; 
      } 
      if (i == this.characterCount - 1) {
        this.visibleAdvance = this.lineMetrics.advance;
      } else if (i == -1) {
        this.visibleAdvance = 0.0F;
      } else {
        int j = this.textLine.visualToLogical(i);
        this.visibleAdvance = this.textLine.getCharLinePosition(j) + this.textLine.getCharAdvance(j);
      } 
    } else {
      byte b;
      for (b = 0; b != this.characterCount; b++) {
        int i = this.textLine.visualToLogical(b);
        if (!this.textLine.isCharSpace(i))
          break; 
      } 
      if (b == this.characterCount) {
        this.visibleAdvance = 0.0F;
      } else if (b == 0) {
        this.visibleAdvance = this.lineMetrics.advance;
      } else {
        int i = this.textLine.visualToLogical(b);
        float f = this.textLine.getCharLinePosition(i);
        this.visibleAdvance = this.lineMetrics.advance - f;
      } 
    } 
    this.naturalBounds = null;
    this.boundsRect = null;
    this.hashCodeCache = 0;
    this.cacheIsValid = true;
  }
  
  private Rectangle2D getNaturalBounds() {
    ensureCache();
    if (this.naturalBounds == null)
      this.naturalBounds = this.textLine.getItalicBounds(); 
    return this.naturalBounds;
  }
  
  protected Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  private void checkTextHit(TextHitInfo paramTextHitInfo) {
    if (paramTextHitInfo == null)
      throw new IllegalArgumentException("TextHitInfo is null."); 
    if (paramTextHitInfo.getInsertionIndex() < 0 || paramTextHitInfo.getInsertionIndex() > this.characterCount)
      throw new IllegalArgumentException("TextHitInfo is out of range"); 
  }
  
  public TextLayout getJustifiedLayout(float paramFloat) {
    if (paramFloat <= 0.0F)
      throw new IllegalArgumentException("justificationWidth <= 0 passed to TextLayout.getJustifiedLayout()"); 
    if (this.justifyRatio == -53.9F)
      throw new Error("Can't justify again."); 
    ensureCache();
    int i;
    for (i = this.characterCount; i > 0 && this.textLine.isCharWhitespace(i - 1); i--);
    TextLine textLine1 = this.textLine.getJustifiedLine(paramFloat, this.justifyRatio, 0, i);
    return (textLine1 != null) ? new TextLayout(textLine1, this.baseline, this.baselineOffsets, -53.9F) : this;
  }
  
  protected void handleJustify(float paramFloat) {}
  
  public byte getBaseline() { return this.baseline; }
  
  public float[] getBaselineOffsets() {
    float[] arrayOfFloat = new float[this.baselineOffsets.length];
    System.arraycopy(this.baselineOffsets, 0, arrayOfFloat, 0, arrayOfFloat.length);
    return arrayOfFloat;
  }
  
  public float getAdvance() {
    ensureCache();
    return this.lineMetrics.advance;
  }
  
  public float getVisibleAdvance() {
    ensureCache();
    return this.visibleAdvance;
  }
  
  public float getAscent() {
    ensureCache();
    return this.lineMetrics.ascent;
  }
  
  public float getDescent() {
    ensureCache();
    return this.lineMetrics.descent;
  }
  
  public float getLeading() {
    ensureCache();
    return this.lineMetrics.leading;
  }
  
  public Rectangle2D getBounds() {
    ensureCache();
    if (this.boundsRect == null) {
      Rectangle2D rectangle2D = this.textLine.getVisualBounds();
      if (dx != 0.0F || dy != 0.0F)
        rectangle2D.setRect(rectangle2D.getX() - dx, rectangle2D.getY() - dy, rectangle2D.getWidth(), rectangle2D.getHeight()); 
      this.boundsRect = rectangle2D;
    } 
    Rectangle2D.Float float = new Rectangle2D.Float();
    float.setRect(this.boundsRect);
    return float;
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) { return this.textLine.getPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2); }
  
  public boolean isLeftToRight() { return this.textLine.isDirectionLTR(); }
  
  public boolean isVertical() { return this.isVerticalLine; }
  
  public int getCharacterCount() { return this.characterCount; }
  
  private float[] getCaretInfo(int paramInt, Rectangle2D paramRectangle2D, float[] paramArrayOfFloat) {
    float f4;
    float f3;
    float f2;
    float f1;
    if (paramInt == 0 || paramInt == this.characterCount) {
      int i;
      float f7;
      if (paramInt == this.characterCount) {
        i = this.textLine.visualToLogical(this.characterCount - 1);
        f7 = this.textLine.getCharLinePosition(i) + this.textLine.getCharAdvance(i);
      } else {
        i = this.textLine.visualToLogical(paramInt);
        f7 = this.textLine.getCharLinePosition(i);
      } 
      float f8 = this.textLine.getCharAngle(i);
      float f9 = this.textLine.getCharShift(i);
      f7 += f8 * f9;
      f1 = f2 = f7 + f8 * this.textLine.getCharAscent(i);
      f3 = f4 = f7 - f8 * this.textLine.getCharDescent(i);
    } else {
      int i = this.textLine.visualToLogical(paramInt - 1);
      float f7 = this.textLine.getCharAngle(i);
      float f8 = this.textLine.getCharLinePosition(i) + this.textLine.getCharAdvance(i);
      if (f7 != 0.0F) {
        f8 += f7 * this.textLine.getCharShift(i);
        f1 = f8 + f7 * this.textLine.getCharAscent(i);
        f3 = f8 - f7 * this.textLine.getCharDescent(i);
      } else {
        f1 = f3 = f8;
      } 
      i = this.textLine.visualToLogical(paramInt);
      f7 = this.textLine.getCharAngle(i);
      f8 = this.textLine.getCharLinePosition(i);
      if (f7 != 0.0F) {
        f8 += f7 * this.textLine.getCharShift(i);
        f2 = f8 + f7 * this.textLine.getCharAscent(i);
        f4 = f8 - f7 * this.textLine.getCharDescent(i);
      } else {
        f2 = f4 = f8;
      } 
    } 
    float f5 = (f1 + f2) / 2.0F;
    float f6 = (f3 + f4) / 2.0F;
    if (paramArrayOfFloat == null)
      paramArrayOfFloat = new float[2]; 
    if (this.isVerticalLine) {
      paramArrayOfFloat[1] = (float)((f5 - f6) / paramRectangle2D.getWidth());
      paramArrayOfFloat[0] = (float)(f5 + paramArrayOfFloat[1] * paramRectangle2D.getX());
    } else {
      paramArrayOfFloat[1] = (float)((f5 - f6) / paramRectangle2D.getHeight());
      paramArrayOfFloat[0] = (float)(f6 + paramArrayOfFloat[1] * paramRectangle2D.getMaxY());
    } 
    return paramArrayOfFloat;
  }
  
  public float[] getCaretInfo(TextHitInfo paramTextHitInfo, Rectangle2D paramRectangle2D) {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    return getCaretInfoTestInternal(paramTextHitInfo, paramRectangle2D);
  }
  
  private float[] getCaretInfoTestInternal(TextHitInfo paramTextHitInfo, Rectangle2D paramRectangle2D) {
    double d4;
    double d3;
    double d2;
    double d1;
    ensureCache();
    checkTextHit(paramTextHitInfo);
    float[] arrayOfFloat = new float[6];
    getCaretInfo(hitToCaret(paramTextHitInfo), paramRectangle2D, arrayOfFloat);
    int i = paramTextHitInfo.getCharIndex();
    boolean bool1 = paramTextHitInfo.isLeadingEdge();
    boolean bool2 = this.textLine.isDirectionLTR();
    boolean bool = !isVertical() ? 1 : 0;
    if (i == -1 || i == this.characterCount) {
      TextLine.TextLineMetrics textLineMetrics = this.textLine.getMetrics();
      boolean bool3 = (bool2 == ((i == -1))) ? 1 : 0;
      double d = 0.0D;
      if (bool) {
        d1 = d3 = bool3 ? 0.0D : textLineMetrics.advance;
        d2 = -textLineMetrics.ascent;
        d4 = textLineMetrics.descent;
      } else {
        d2 = d4 = bool3 ? 0.0D : textLineMetrics.advance;
        d1 = textLineMetrics.descent;
        d3 = textLineMetrics.ascent;
      } 
    } else {
      CoreMetrics coreMetrics = this.textLine.getCoreMetricsAt(i);
      double d5 = coreMetrics.italicAngle;
      double d6 = this.textLine.getCharLinePosition(i, bool1);
      if (coreMetrics.baselineIndex < 0) {
        TextLine.TextLineMetrics textLineMetrics = this.textLine.getMetrics();
        if (bool) {
          d1 = d3 = d6;
          if (coreMetrics.baselineIndex == -1) {
            d2 = -textLineMetrics.ascent;
            d4 = d2 + coreMetrics.height;
          } else {
            d4 = textLineMetrics.descent;
            d2 = d4 - coreMetrics.height;
          } 
        } else {
          d2 = d4 = d6;
          d1 = textLineMetrics.descent;
          d3 = textLineMetrics.ascent;
        } 
      } else {
        float f = this.baselineOffsets[coreMetrics.baselineIndex];
        if (bool) {
          d6 += d5 * coreMetrics.ssOffset;
          d1 = d6 + d5 * coreMetrics.ascent;
          d3 = d6 - d5 * coreMetrics.descent;
          d2 = (f - coreMetrics.ascent);
          d4 = (f + coreMetrics.descent);
        } else {
          d6 -= d5 * coreMetrics.ssOffset;
          d2 = d6 + d5 * coreMetrics.ascent;
          d4 = d6 - d5 * coreMetrics.descent;
          d1 = (f + coreMetrics.ascent);
          d3 = (f + coreMetrics.descent);
        } 
      } 
    } 
    arrayOfFloat[2] = (float)d1;
    arrayOfFloat[3] = (float)d2;
    arrayOfFloat[4] = (float)d3;
    arrayOfFloat[5] = (float)d4;
    return arrayOfFloat;
  }
  
  public float[] getCaretInfo(TextHitInfo paramTextHitInfo) { return getCaretInfo(paramTextHitInfo, getNaturalBounds()); }
  
  private int hitToCaret(TextHitInfo paramTextHitInfo) {
    int i = paramTextHitInfo.getCharIndex();
    if (i < 0)
      return this.textLine.isDirectionLTR() ? 0 : this.characterCount; 
    if (i >= this.characterCount)
      return this.textLine.isDirectionLTR() ? this.characterCount : 0; 
    int j = this.textLine.logicalToVisual(i);
    if (paramTextHitInfo.isLeadingEdge() != this.textLine.isCharLTR(i))
      j++; 
    return j;
  }
  
  private TextHitInfo caretToHit(int paramInt) {
    if (paramInt == 0 || paramInt == this.characterCount)
      return (((paramInt == this.characterCount)) == this.textLine.isDirectionLTR()) ? TextHitInfo.leading(this.characterCount) : TextHitInfo.trailing(-1); 
    int i = this.textLine.visualToLogical(paramInt);
    boolean bool = this.textLine.isCharLTR(i);
    return bool ? TextHitInfo.leading(i) : TextHitInfo.trailing(i);
  }
  
  private boolean caretIsValid(int paramInt) {
    if (paramInt == this.characterCount || paramInt == 0)
      return true; 
    int i = this.textLine.visualToLogical(paramInt);
    if (!this.textLine.isCharLTR(i)) {
      i = this.textLine.visualToLogical(paramInt - 1);
      if (this.textLine.isCharLTR(i))
        return true; 
    } 
    return this.textLine.caretAtOffsetIsValid(i);
  }
  
  public TextHitInfo getNextRightHit(TextHitInfo paramTextHitInfo) {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    int i = hitToCaret(paramTextHitInfo);
    if (i == this.characterCount)
      return null; 
    do {
      i++;
    } while (!caretIsValid(i));
    return caretToHit(i);
  }
  
  public TextHitInfo getNextRightHit(int paramInt, CaretPolicy paramCaretPolicy) {
    if (paramInt < 0 || paramInt > this.characterCount)
      throw new IllegalArgumentException("Offset out of bounds in TextLayout.getNextRightHit()"); 
    if (paramCaretPolicy == null)
      throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getNextRightHit()"); 
    TextHitInfo textHitInfo1 = TextHitInfo.afterOffset(paramInt);
    TextHitInfo textHitInfo2 = textHitInfo1.getOtherHit();
    TextHitInfo textHitInfo3 = getNextRightHit(paramCaretPolicy.getStrongCaret(textHitInfo1, textHitInfo2, this));
    if (textHitInfo3 != null) {
      TextHitInfo textHitInfo = getVisualOtherHit(textHitInfo3);
      return paramCaretPolicy.getStrongCaret(textHitInfo, textHitInfo3, this);
    } 
    return null;
  }
  
  public TextHitInfo getNextRightHit(int paramInt) { return getNextRightHit(paramInt, DEFAULT_CARET_POLICY); }
  
  public TextHitInfo getNextLeftHit(TextHitInfo paramTextHitInfo) {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    int i = hitToCaret(paramTextHitInfo);
    if (i == 0)
      return null; 
    do {
      i--;
    } while (!caretIsValid(i));
    return caretToHit(i);
  }
  
  public TextHitInfo getNextLeftHit(int paramInt, CaretPolicy paramCaretPolicy) {
    if (paramCaretPolicy == null)
      throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getNextLeftHit()"); 
    if (paramInt < 0 || paramInt > this.characterCount)
      throw new IllegalArgumentException("Offset out of bounds in TextLayout.getNextLeftHit()"); 
    TextHitInfo textHitInfo1 = TextHitInfo.afterOffset(paramInt);
    TextHitInfo textHitInfo2 = textHitInfo1.getOtherHit();
    TextHitInfo textHitInfo3 = getNextLeftHit(paramCaretPolicy.getStrongCaret(textHitInfo1, textHitInfo2, this));
    if (textHitInfo3 != null) {
      TextHitInfo textHitInfo = getVisualOtherHit(textHitInfo3);
      return paramCaretPolicy.getStrongCaret(textHitInfo, textHitInfo3, this);
    } 
    return null;
  }
  
  public TextHitInfo getNextLeftHit(int paramInt) { return getNextLeftHit(paramInt, DEFAULT_CARET_POLICY); }
  
  public TextHitInfo getVisualOtherHit(TextHitInfo paramTextHitInfo) {
    boolean bool;
    int j;
    ensureCache();
    checkTextHit(paramTextHitInfo);
    int i = paramTextHitInfo.getCharIndex();
    if (i == -1 || i == this.characterCount) {
      int k;
      if (this.textLine.isDirectionLTR() == ((i == -1))) {
        k = 0;
      } else {
        k = this.characterCount - 1;
      } 
      j = this.textLine.visualToLogical(k);
      if (this.textLine.isDirectionLTR() == ((i == -1))) {
        bool = this.textLine.isCharLTR(j);
      } else {
        bool = !this.textLine.isCharLTR(j) ? 1 : 0;
      } 
    } else {
      boolean bool1;
      int k = this.textLine.logicalToVisual(i);
      if (this.textLine.isCharLTR(i) == paramTextHitInfo.isLeadingEdge()) {
        k--;
        bool1 = false;
      } else {
        k++;
        bool1 = true;
      } 
      if (k > -1 && k < this.characterCount) {
        j = this.textLine.visualToLogical(k);
        bool = (bool1 == this.textLine.isCharLTR(j)) ? 1 : 0;
      } else {
        j = (bool1 == this.textLine.isDirectionLTR()) ? this.characterCount : -1;
        bool = (j == this.characterCount) ? 1 : 0;
      } 
    } 
    return bool ? TextHitInfo.leading(j) : TextHitInfo.trailing(j);
  }
  
  private double[] getCaretPath(TextHitInfo paramTextHitInfo, Rectangle2D paramRectangle2D) {
    float[] arrayOfFloat = getCaretInfo(paramTextHitInfo, paramRectangle2D);
    return new double[] { arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5] };
  }
  
  private double[] getCaretPath(int paramInt, Rectangle2D paramRectangle2D, boolean paramBoolean) {
    double d6;
    double d5;
    double d4;
    double d3;
    float[] arrayOfFloat = getCaretInfo(paramInt, paramRectangle2D, null);
    double d1 = arrayOfFloat[0];
    double d2 = arrayOfFloat[1];
    double d7 = -3141.59D;
    double d8 = -2.7D;
    double d9 = paramRectangle2D.getX();
    double d10 = d9 + paramRectangle2D.getWidth();
    double d11 = paramRectangle2D.getY();
    double d12 = d11 + paramRectangle2D.getHeight();
    boolean bool = false;
    if (this.isVerticalLine) {
      if (d2 >= 0.0D) {
        d3 = d9;
        d5 = d10;
      } else {
        d5 = d9;
        d3 = d10;
      } 
      d4 = d1 + d3 * d2;
      d6 = d1 + d5 * d2;
      if (paramBoolean) {
        d4 = d6 = d11;
        bool = true;
        d4 = d11;
        d8 = d11;
        d7 = d5 + (d11 - d6) / d2;
        if (d6 > d12)
          d6 = d12; 
        if (d6 > d12) {
          d4 = d6 = d12;
          bool = true;
          d6 = d12;
          d8 = d12;
          d7 = d3 + (d12 - d5) / d2;
        } 
      } 
    } else {
      if (d2 >= 0.0D) {
        d4 = d12;
        d6 = d11;
      } else {
        d6 = d12;
        d4 = d11;
      } 
      d3 = d1 - d4 * d2;
      d5 = d1 - d6 * d2;
      if (paramBoolean) {
        d3 = d5 = d9;
        bool = true;
        d3 = d9;
        d7 = d9;
        d8 = d6 - (d9 - d5) / d2;
        if (d5 > d10)
          d5 = d10; 
        if (d5 > d10) {
          d3 = d5 = d10;
          bool = true;
          d5 = d10;
          d7 = d10;
          d8 = d4 - (d10 - d3) / d2;
        } 
      } 
    } 
    new double[6][0] = d3;
    new double[6][1] = d4;
    new double[6][2] = d7;
    new double[6][3] = d8;
    new double[6][4] = d5;
    new double[6][5] = d6;
    new double[4][0] = d3;
    new double[4][1] = d4;
    new double[4][2] = d5;
    new double[4][3] = d6;
    return bool ? new double[6] : new double[4];
  }
  
  private static GeneralPath pathToShape(double[] paramArrayOfDouble, boolean paramBoolean, LayoutPathImpl paramLayoutPathImpl) {
    GeneralPath generalPath = new GeneralPath(0, paramArrayOfDouble.length);
    generalPath.moveTo((float)paramArrayOfDouble[0], (float)paramArrayOfDouble[1]);
    for (byte b = 2; b < paramArrayOfDouble.length; b += 2)
      generalPath.lineTo((float)paramArrayOfDouble[b], (float)paramArrayOfDouble[b + 1]); 
    if (paramBoolean)
      generalPath.closePath(); 
    if (paramLayoutPathImpl != null)
      generalPath = (GeneralPath)paramLayoutPathImpl.mapShape(generalPath); 
    return generalPath;
  }
  
  public Shape getCaretShape(TextHitInfo paramTextHitInfo, Rectangle2D paramRectangle2D) {
    ensureCache();
    checkTextHit(paramTextHitInfo);
    if (paramRectangle2D == null)
      throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getCaret()"); 
    return pathToShape(getCaretPath(paramTextHitInfo, paramRectangle2D), false, this.textLine.getLayoutPath());
  }
  
  public Shape getCaretShape(TextHitInfo paramTextHitInfo) { return getCaretShape(paramTextHitInfo, getNaturalBounds()); }
  
  private final TextHitInfo getStrongHit(TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2) {
    byte b1 = getCharacterLevel(paramTextHitInfo1.getCharIndex());
    byte b2 = getCharacterLevel(paramTextHitInfo2.getCharIndex());
    return (b1 == b2) ? ((paramTextHitInfo2.isLeadingEdge() && !paramTextHitInfo1.isLeadingEdge()) ? paramTextHitInfo2 : paramTextHitInfo1) : ((b1 < b2) ? paramTextHitInfo1 : paramTextHitInfo2);
  }
  
  public byte getCharacterLevel(int paramInt) {
    if (paramInt < -1 || paramInt > this.characterCount)
      throw new IllegalArgumentException("Index is out of range in getCharacterLevel."); 
    ensureCache();
    return (paramInt == -1 || paramInt == this.characterCount) ? (byte)(this.textLine.isDirectionLTR() ? 0 : 1) : this.textLine.getCharLevel(paramInt);
  }
  
  public Shape[] getCaretShapes(int paramInt, Rectangle2D paramRectangle2D, CaretPolicy paramCaretPolicy) {
    ensureCache();
    if (paramInt < 0 || paramInt > this.characterCount)
      throw new IllegalArgumentException("Offset out of bounds in TextLayout.getCaretShapes()"); 
    if (paramRectangle2D == null)
      throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getCaretShapes()"); 
    if (paramCaretPolicy == null)
      throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getCaretShapes()"); 
    Shape[] arrayOfShape = new Shape[2];
    TextHitInfo textHitInfo1 = TextHitInfo.afterOffset(paramInt);
    int i = hitToCaret(textHitInfo1);
    LayoutPathImpl layoutPathImpl = this.textLine.getLayoutPath();
    GeneralPath generalPath = pathToShape(getCaretPath(textHitInfo1, paramRectangle2D), false, layoutPathImpl);
    TextHitInfo textHitInfo2 = textHitInfo1.getOtherHit();
    int j = hitToCaret(textHitInfo2);
    if (i == j) {
      arrayOfShape[0] = generalPath;
    } else {
      GeneralPath generalPath1 = pathToShape(getCaretPath(textHitInfo2, paramRectangle2D), false, layoutPathImpl);
      TextHitInfo textHitInfo = paramCaretPolicy.getStrongCaret(textHitInfo1, textHitInfo2, this);
      boolean bool = textHitInfo.equals(textHitInfo1);
      if (bool) {
        arrayOfShape[0] = generalPath;
        arrayOfShape[1] = generalPath1;
      } else {
        arrayOfShape[0] = generalPath1;
        arrayOfShape[1] = generalPath;
      } 
    } 
    return arrayOfShape;
  }
  
  public Shape[] getCaretShapes(int paramInt, Rectangle2D paramRectangle2D) { return getCaretShapes(paramInt, paramRectangle2D, DEFAULT_CARET_POLICY); }
  
  public Shape[] getCaretShapes(int paramInt) { return getCaretShapes(paramInt, getNaturalBounds(), DEFAULT_CARET_POLICY); }
  
  private GeneralPath boundingShape(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2) {
    byte b1;
    int i;
    boolean bool2;
    boolean bool1;
    GeneralPath generalPath = pathToShape(paramArrayOfDouble1, false, null);
    if (this.isVerticalLine) {
      bool1 = (((paramArrayOfDouble1[1] > paramArrayOfDouble1[paramArrayOfDouble1.length - 1]) ? 1 : 0) == ((paramArrayOfDouble2[1] > paramArrayOfDouble2[paramArrayOfDouble2.length - 1]) ? 1 : 0)) ? 1 : 0;
    } else {
      bool1 = (((paramArrayOfDouble1[0] > paramArrayOfDouble1[paramArrayOfDouble1.length - 2]) ? 1 : 0) == ((paramArrayOfDouble2[0] > paramArrayOfDouble2[paramArrayOfDouble2.length - 2]) ? 1 : 0)) ? 1 : 0;
    } 
    if (bool1) {
      bool2 = paramArrayOfDouble2.length - 2;
      i = -2;
      b1 = -2;
    } else {
      bool2 = false;
      i = paramArrayOfDouble2.length;
      b1 = 2;
    } 
    byte b2;
    for (b2 = bool2; b2 != i; b2 += b1)
      generalPath.lineTo((float)paramArrayOfDouble2[b2], (float)paramArrayOfDouble2[b2 + true]); 
    generalPath.closePath();
    return generalPath;
  }
  
  private GeneralPath caretBoundingShape(int paramInt1, int paramInt2, Rectangle2D paramRectangle2D) {
    if (paramInt1 > paramInt2) {
      int i = paramInt1;
      paramInt1 = paramInt2;
      paramInt2 = i;
    } 
    return boundingShape(getCaretPath(paramInt1, paramRectangle2D, true), getCaretPath(paramInt2, paramRectangle2D, true));
  }
  
  private GeneralPath leftShape(Rectangle2D paramRectangle2D) {
    double[] arrayOfDouble1;
    if (this.isVerticalLine) {
      arrayOfDouble1 = new double[] { paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getX() + paramRectangle2D.getWidth(), paramRectangle2D.getY() };
    } else {
      arrayOfDouble1 = new double[] { paramRectangle2D.getX(), paramRectangle2D.getY() + paramRectangle2D.getHeight(), paramRectangle2D.getX(), paramRectangle2D.getY() };
    } 
    double[] arrayOfDouble2 = getCaretPath(0, paramRectangle2D, true);
    return boundingShape(arrayOfDouble1, arrayOfDouble2);
  }
  
  private GeneralPath rightShape(Rectangle2D paramRectangle2D) {
    double[] arrayOfDouble1;
    if (this.isVerticalLine) {
      arrayOfDouble1 = new double[] { paramRectangle2D.getX(), paramRectangle2D.getY() + paramRectangle2D.getHeight(), paramRectangle2D.getX() + paramRectangle2D.getWidth(), paramRectangle2D.getY() + paramRectangle2D.getHeight() };
    } else {
      arrayOfDouble1 = new double[] { paramRectangle2D.getX() + paramRectangle2D.getWidth(), paramRectangle2D.getY() + paramRectangle2D.getHeight(), paramRectangle2D.getX() + paramRectangle2D.getWidth(), paramRectangle2D.getY() };
    } 
    double[] arrayOfDouble2 = getCaretPath(this.characterCount, paramRectangle2D, true);
    return boundingShape(arrayOfDouble2, arrayOfDouble1);
  }
  
  public int[] getLogicalRangesForVisualSelection(TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2) {
    ensureCache();
    checkTextHit(paramTextHitInfo1);
    checkTextHit(paramTextHitInfo2);
    boolean[] arrayOfBoolean = new boolean[this.characterCount];
    int i = hitToCaret(paramTextHitInfo1);
    int j = hitToCaret(paramTextHitInfo2);
    if (i > j) {
      int k = i;
      i = j;
      j = k;
    } 
    if (i < j)
      for (int k = i; k < j; k++)
        arrayOfBoolean[this.textLine.visualToLogical(k)] = true;  
    byte b1 = 0;
    boolean bool = false;
    for (byte b2 = 0; b2 < this.characterCount; b2++) {
      if (arrayOfBoolean[b2] != bool) {
        bool = !bool ? 1 : 0;
        if (bool)
          b1++; 
      } 
    } 
    int[] arrayOfInt = new int[b1 * 2];
    b1 = 0;
    bool = false;
    for (byte b3 = 0; b3 < this.characterCount; b3++) {
      if (arrayOfBoolean[b3] != bool) {
        arrayOfInt[b1++] = b3;
        bool = !bool ? 1 : 0;
      } 
    } 
    if (bool)
      arrayOfInt[b1++] = this.characterCount; 
    return arrayOfInt;
  }
  
  public Shape getVisualHighlightShape(TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2, Rectangle2D paramRectangle2D) {
    ensureCache();
    checkTextHit(paramTextHitInfo1);
    checkTextHit(paramTextHitInfo2);
    if (paramRectangle2D == null)
      throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getVisualHighlightShape()"); 
    GeneralPath generalPath = new GeneralPath(0);
    int i = hitToCaret(paramTextHitInfo1);
    int j = hitToCaret(paramTextHitInfo2);
    generalPath.append(caretBoundingShape(i, j, paramRectangle2D), false);
    if (i == 0 || j == 0) {
      GeneralPath generalPath1 = leftShape(paramRectangle2D);
      if (!generalPath1.getBounds().isEmpty())
        generalPath.append(generalPath1, false); 
    } 
    if (i == this.characterCount || j == this.characterCount) {
      GeneralPath generalPath1 = rightShape(paramRectangle2D);
      if (!generalPath1.getBounds().isEmpty())
        generalPath.append(generalPath1, false); 
    } 
    LayoutPathImpl layoutPathImpl = this.textLine.getLayoutPath();
    if (layoutPathImpl != null)
      generalPath = (GeneralPath)layoutPathImpl.mapShape(generalPath); 
    return generalPath;
  }
  
  public Shape getVisualHighlightShape(TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2) { return getVisualHighlightShape(paramTextHitInfo1, paramTextHitInfo2, getNaturalBounds()); }
  
  public Shape getLogicalHighlightShape(int paramInt1, int paramInt2, Rectangle2D paramRectangle2D) {
    if (paramRectangle2D == null)
      throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getLogicalHighlightShape()"); 
    ensureCache();
    if (paramInt1 > paramInt2) {
      int i = paramInt1;
      paramInt1 = paramInt2;
      paramInt2 = i;
    } 
    if (paramInt1 < 0 || paramInt2 > this.characterCount)
      throw new IllegalArgumentException("Range is invalid in TextLayout.getLogicalHighlightShape()"); 
    GeneralPath generalPath = new GeneralPath(0);
    int[] arrayOfInt = new int[10];
    byte b = 0;
    if (paramInt1 < paramInt2) {
      int i = paramInt1;
      do {
        arrayOfInt[b++] = hitToCaret(TextHitInfo.leading(i));
        boolean bool1 = this.textLine.isCharLTR(i);
        do {
        
        } while (++i < paramInt2 && this.textLine.isCharLTR(i) == bool1);
        int j = i;
        arrayOfInt[b++] = hitToCaret(TextHitInfo.trailing(j - 1));
        if (b != arrayOfInt.length)
          continue; 
        int[] arrayOfInt1 = new int[arrayOfInt.length + 10];
        System.arraycopy(arrayOfInt, 0, arrayOfInt1, 0, b);
        arrayOfInt = arrayOfInt1;
      } while (i < paramInt2);
    } else {
      b = 2;
      arrayOfInt[1] = hitToCaret(TextHitInfo.leading(paramInt1));
      arrayOfInt[0] = hitToCaret(TextHitInfo.leading(paramInt1));
    } 
    for (boolean bool = false; bool < b; bool += true)
      generalPath.append(caretBoundingShape(arrayOfInt[bool], arrayOfInt[bool + true], paramRectangle2D), false); 
    if (paramInt1 != paramInt2) {
      if ((this.textLine.isDirectionLTR() && paramInt1 == 0) || (!this.textLine.isDirectionLTR() && paramInt2 == this.characterCount)) {
        GeneralPath generalPath1 = leftShape(paramRectangle2D);
        if (!generalPath1.getBounds().isEmpty())
          generalPath.append(generalPath1, false); 
      } 
      if ((this.textLine.isDirectionLTR() && paramInt2 == this.characterCount) || (!this.textLine.isDirectionLTR() && paramInt1 == 0)) {
        GeneralPath generalPath1 = rightShape(paramRectangle2D);
        if (!generalPath1.getBounds().isEmpty())
          generalPath.append(generalPath1, false); 
      } 
    } 
    LayoutPathImpl layoutPathImpl = this.textLine.getLayoutPath();
    if (layoutPathImpl != null)
      generalPath = (GeneralPath)layoutPathImpl.mapShape(generalPath); 
    return generalPath;
  }
  
  public Shape getLogicalHighlightShape(int paramInt1, int paramInt2) { return getLogicalHighlightShape(paramInt1, paramInt2, getNaturalBounds()); }
  
  public Shape getBlackBoxBounds(int paramInt1, int paramInt2) {
    ensureCache();
    if (paramInt1 > paramInt2) {
      int i = paramInt1;
      paramInt1 = paramInt2;
      paramInt2 = i;
    } 
    if (paramInt1 < 0 || paramInt2 > this.characterCount)
      throw new IllegalArgumentException("Invalid range passed to TextLayout.getBlackBoxBounds()"); 
    GeneralPath generalPath = new GeneralPath(1);
    if (paramInt1 < this.characterCount)
      for (int i = paramInt1; i < paramInt2; i++) {
        Rectangle2D rectangle2D = this.textLine.getCharBounds(i);
        if (!rectangle2D.isEmpty())
          generalPath.append(rectangle2D, false); 
      }  
    if (dx != 0.0F || dy != 0.0F) {
      AffineTransform affineTransform = AffineTransform.getTranslateInstance(dx, dy);
      generalPath = (GeneralPath)affineTransform.createTransformedShape(generalPath);
    } 
    LayoutPathImpl layoutPathImpl = this.textLine.getLayoutPath();
    if (layoutPathImpl != null)
      generalPath = (GeneralPath)layoutPathImpl.mapShape(generalPath); 
    return generalPath;
  }
  
  private float caretToPointDistance(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2) {
    float f1 = this.isVerticalLine ? paramFloat2 : paramFloat1;
    float f2 = this.isVerticalLine ? -paramFloat1 : paramFloat2;
    return f1 - paramArrayOfFloat[0] + f2 * paramArrayOfFloat[1];
  }
  
  public TextHitInfo hitTestChar(float paramFloat1, float paramFloat2, Rectangle2D paramRectangle2D) {
    LayoutPathImpl layoutPathImpl = this.textLine.getLayoutPath();
    boolean bool = false;
    if (layoutPathImpl != null) {
      Point2D.Float float = new Point2D.Float(paramFloat1, paramFloat2);
      bool = layoutPathImpl.pointToPath(float, float);
      paramFloat1 = float.x;
      paramFloat2 = float.y;
    } 
    if (isVertical()) {
      if (paramFloat2 < paramRectangle2D.getMinY())
        return TextHitInfo.leading(0); 
      if (paramFloat2 >= paramRectangle2D.getMaxY())
        return TextHitInfo.trailing(this.characterCount - 1); 
    } else {
      if (paramFloat1 < paramRectangle2D.getMinX())
        return isLeftToRight() ? TextHitInfo.leading(0) : TextHitInfo.trailing(this.characterCount - 1); 
      if (paramFloat1 >= paramRectangle2D.getMaxX())
        return isLeftToRight() ? TextHitInfo.trailing(this.characterCount - 1) : TextHitInfo.leading(0); 
    } 
    double d = Double.MAX_VALUE;
    byte b1 = 0;
    int i = -1;
    CoreMetrics coreMetrics = null;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f4 = 0.0F;
    float f5 = 0.0F;
    float f6 = 0.0F;
    byte b2;
    for (b2 = 0; b2 < this.characterCount; b2++) {
      if (this.textLine.caretAtOffsetIsValid(b2)) {
        if (i == -1)
          i = b2; 
        CoreMetrics coreMetrics1 = this.textLine.getCoreMetricsAt(b2);
        if (coreMetrics1 != coreMetrics) {
          coreMetrics = coreMetrics1;
          if (coreMetrics1.baselineIndex == -1) {
            f4 = -((this.textLine.getMetrics()).ascent - coreMetrics1.ascent) + coreMetrics1.ssOffset;
          } else if (coreMetrics1.baselineIndex == -2) {
            f4 = (this.textLine.getMetrics()).descent - coreMetrics1.descent + coreMetrics1.ssOffset;
          } else {
            f4 = coreMetrics1.effectiveBaselineOffset(this.baselineOffsets) + coreMetrics1.ssOffset;
          } 
          float f = (coreMetrics1.descent - coreMetrics1.ascent) / 2.0F - f4;
          f5 = f * coreMetrics1.italicAngle;
          f4 += f;
          f6 = (f4 - paramFloat2) * (f4 - paramFloat2);
        } 
        float f7 = this.textLine.getCharXPosition(b2);
        float f8 = this.textLine.getCharAdvance(b2);
        float f9 = f8 / 2.0F;
        f7 += f9 - f5;
        double d1 = Math.sqrt((4.0F * (f7 - paramFloat1) * (f7 - paramFloat1) + f6));
        if (d1 < d) {
          d = d1;
          b1 = b2;
          i = -1;
          f1 = f7;
          f2 = f4;
          f3 = coreMetrics1.italicAngle;
        } 
      } 
    } 
    b2 = (paramFloat1 < f1 - (paramFloat2 - f2) * f3) ? 1 : 0;
    boolean bool1 = (this.textLine.isCharLTR(b1) == b2) ? 1 : 0;
    if (i == -1)
      i = this.characterCount; 
    return bool1 ? TextHitInfo.leading(b1) : TextHitInfo.trailing(i - 1);
  }
  
  public TextHitInfo hitTestChar(float paramFloat1, float paramFloat2) { return hitTestChar(paramFloat1, paramFloat2, getNaturalBounds()); }
  
  public int hashCode() {
    if (this.hashCodeCache == 0) {
      ensureCache();
      this.hashCodeCache = this.textLine.hashCode();
    } 
    return this.hashCodeCache;
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof TextLayout && equals((TextLayout)paramObject)); }
  
  public boolean equals(TextLayout paramTextLayout) {
    if (paramTextLayout == null)
      return false; 
    if (paramTextLayout == this)
      return true; 
    ensureCache();
    return this.textLine.equals(paramTextLayout.textLine);
  }
  
  public String toString() {
    ensureCache();
    return this.textLine.toString();
  }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) {
    if (paramGraphics2D == null)
      throw new IllegalArgumentException("Null Graphics2D passed to TextLayout.draw()"); 
    this.textLine.draw(paramGraphics2D, paramFloat1 - dx, paramFloat2 - dy);
  }
  
  TextLine getTextLineForTesting() { return this.textLine; }
  
  private static int sameBaselineUpTo(Font paramFont, char[] paramArrayOfChar, int paramInt1, int paramInt2) { return paramInt2; }
  
  static byte getBaselineFromGraphic(GraphicAttribute paramGraphicAttribute) {
    byte b = (byte)paramGraphicAttribute.getAlignment();
    return (b == -2 || b == -1) ? 0 : b;
  }
  
  public Shape getOutline(AffineTransform paramAffineTransform) {
    ensureCache();
    Shape shape = this.textLine.getOutline(paramAffineTransform);
    LayoutPathImpl layoutPathImpl = this.textLine.getLayoutPath();
    if (layoutPathImpl != null)
      shape = layoutPathImpl.mapShape(shape); 
    return shape;
  }
  
  public LayoutPath getLayoutPath() { return this.textLine.getLayoutPath(); }
  
  public void hitToPoint(TextHitInfo paramTextHitInfo, Point2D paramPoint2D) {
    boolean bool2;
    if (paramTextHitInfo == null || paramPoint2D == null)
      throw new NullPointerException(((paramTextHitInfo == null) ? "hit" : "point") + " can't be null"); 
    ensureCache();
    checkTextHit(paramTextHitInfo);
    float f1 = 0.0F;
    float f2 = 0.0F;
    int i = paramTextHitInfo.getCharIndex();
    boolean bool1 = paramTextHitInfo.isLeadingEdge();
    if (i == -1 || i == this.textLine.characterCount()) {
      bool2 = this.textLine.isDirectionLTR();
      f1 = (bool2 == ((i == -1))) ? 0.0F : this.lineMetrics.advance;
    } else {
      bool2 = this.textLine.isCharLTR(i);
      f1 = this.textLine.getCharLinePosition(i, bool1);
      f2 = this.textLine.getCharYPosition(i);
    } 
    paramPoint2D.setLocation(f1, f2);
    LayoutPathImpl layoutPathImpl = this.textLine.getLayoutPath();
    if (layoutPathImpl != null)
      layoutPathImpl.pathToPoint(paramPoint2D, (bool2 != bool1), paramPoint2D); 
  }
  
  public static class CaretPolicy {
    public TextHitInfo getStrongCaret(TextHitInfo param1TextHitInfo1, TextHitInfo param1TextHitInfo2, TextLayout param1TextLayout) { return param1TextLayout.getStrongHit(param1TextHitInfo1, param1TextHitInfo2); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\TextLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */