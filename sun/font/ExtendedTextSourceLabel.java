package sun.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;

class ExtendedTextSourceLabel extends ExtendedTextLabel implements Decoration.Label {
  TextSource source;
  
  private Decoration decorator;
  
  private Font font;
  
  private AffineTransform baseTX;
  
  private CoreMetrics cm;
  
  Rectangle2D lb;
  
  Rectangle2D ab;
  
  Rectangle2D vb;
  
  Rectangle2D ib;
  
  StandardGlyphVector gv;
  
  float[] charinfo;
  
  private static final int posx = 0;
  
  private static final int posy = 1;
  
  private static final int advx = 2;
  
  private static final int advy = 3;
  
  private static final int visx = 4;
  
  private static final int visy = 5;
  
  private static final int visw = 6;
  
  private static final int vish = 7;
  
  private static final int numvals = 8;
  
  public ExtendedTextSourceLabel(TextSource paramTextSource, Decoration paramDecoration) {
    this.source = paramTextSource;
    this.decorator = paramDecoration;
    finishInit();
  }
  
  public ExtendedTextSourceLabel(TextSource paramTextSource, ExtendedTextSourceLabel paramExtendedTextSourceLabel, int paramInt) {
    this.source = paramTextSource;
    this.decorator = paramExtendedTextSourceLabel.decorator;
    finishInit();
  }
  
  private void finishInit() {
    this.font = this.source.getFont();
    Map map = this.font.getAttributes();
    this.baseTX = AttributeValues.getBaselineTransform(map);
    if (this.baseTX == null) {
      this.cm = this.source.getCoreMetrics();
    } else {
      AffineTransform affineTransform = AttributeValues.getCharTransform(map);
      if (affineTransform == null)
        affineTransform = new AffineTransform(); 
      this.font = this.font.deriveFont(affineTransform);
      LineMetrics lineMetrics = this.font.getLineMetrics(this.source.getChars(), this.source.getStart(), this.source.getStart() + this.source.getLength(), this.source.getFRC());
      this.cm = CoreMetrics.get(lineMetrics);
    } 
  }
  
  public Rectangle2D getLogicalBounds() { return getLogicalBounds(0.0F, 0.0F); }
  
  public Rectangle2D getLogicalBounds(float paramFloat1, float paramFloat2) {
    if (this.lb == null)
      this.lb = createLogicalBounds(); 
    return new Rectangle2D.Float((float)(this.lb.getX() + paramFloat1), (float)(this.lb.getY() + paramFloat2), (float)this.lb.getWidth(), (float)this.lb.getHeight());
  }
  
  public float getAdvance() {
    if (this.lb == null)
      this.lb = createLogicalBounds(); 
    return (float)this.lb.getWidth();
  }
  
  public Rectangle2D getVisualBounds(float paramFloat1, float paramFloat2) {
    if (this.vb == null)
      this.vb = this.decorator.getVisualBounds(this); 
    return new Rectangle2D.Float((float)(this.vb.getX() + paramFloat1), (float)(this.vb.getY() + paramFloat2), (float)this.vb.getWidth(), (float)this.vb.getHeight());
  }
  
  public Rectangle2D getAlignBounds(float paramFloat1, float paramFloat2) {
    if (this.ab == null)
      this.ab = createAlignBounds(); 
    return new Rectangle2D.Float((float)(this.ab.getX() + paramFloat1), (float)(this.ab.getY() + paramFloat2), (float)this.ab.getWidth(), (float)this.ab.getHeight());
  }
  
  public Rectangle2D getItalicBounds(float paramFloat1, float paramFloat2) {
    if (this.ib == null)
      this.ib = createItalicBounds(); 
    return new Rectangle2D.Float((float)(this.ib.getX() + paramFloat1), (float)(this.ib.getY() + paramFloat2), (float)this.ib.getWidth(), (float)this.ib.getHeight());
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) { return getGV().getPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2); }
  
  public boolean isSimple() { return (this.decorator == Decoration.getPlainDecoration() && this.baseTX == null); }
  
  public AffineTransform getBaselineTransform() { return this.baseTX; }
  
  public Shape handleGetOutline(float paramFloat1, float paramFloat2) { return getGV().getOutline(paramFloat1, paramFloat2); }
  
  public Shape getOutline(float paramFloat1, float paramFloat2) { return this.decorator.getOutline(this, paramFloat1, paramFloat2); }
  
  public void handleDraw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) { paramGraphics2D.drawGlyphVector(getGV(), paramFloat1, paramFloat2); }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) { this.decorator.drawTextAndDecorations(this, paramGraphics2D, paramFloat1, paramFloat2); }
  
  protected Rectangle2D createLogicalBounds() { return getGV().getLogicalBounds(); }
  
  public Rectangle2D handleGetVisualBounds() { return getGV().getVisualBounds(); }
  
  protected Rectangle2D createAlignBounds() {
    float[] arrayOfFloat = getCharinfo();
    float f1 = 0.0F;
    float f2 = -this.cm.ascent;
    float f3 = 0.0F;
    float f4 = this.cm.ascent + this.cm.descent;
    if (this.charinfo == null || this.charinfo.length == 0)
      return new Rectangle2D.Float(f1, f2, f3, f4); 
    boolean bool = ((this.source.getLayoutFlags() & 0x8) == 0) ? 1 : 0;
    int i = arrayOfFloat.length - 8;
    if (bool)
      while (i > 0 && arrayOfFloat[i + 6] == 0.0F)
        i -= 8;  
    if (i >= 0) {
      boolean bool1;
      for (bool1 = false; bool1 < i && (arrayOfFloat[bool1 + 2] == 0.0F || (!bool && arrayOfFloat[bool1 + 6] == 0.0F)); bool1 += true);
      f1 = Math.max(0.0F, arrayOfFloat[bool1 + false]);
      f3 = arrayOfFloat[i + 0] + arrayOfFloat[i + 2] - f1;
    } 
    return new Rectangle2D.Float(f1, f2, f3, f4);
  }
  
  public Rectangle2D createItalicBounds() {
    float f1 = this.cm.italicAngle;
    Rectangle2D rectangle2D = getLogicalBounds();
    float f2 = (float)rectangle2D.getMinX();
    float f3 = -this.cm.ascent;
    float f4 = (float)rectangle2D.getMaxX();
    float f5 = this.cm.descent;
    if (f1 != 0.0F)
      if (f1 > 0.0F) {
        f2 -= f1 * (f5 - this.cm.ssOffset);
        f4 -= f1 * (f3 - this.cm.ssOffset);
      } else {
        f2 -= f1 * (f3 - this.cm.ssOffset);
        f4 -= f1 * (f5 - this.cm.ssOffset);
      }  
    return new Rectangle2D.Float(f2, f3, f4 - f2, f5 - f3);
  }
  
  private final StandardGlyphVector getGV() {
    if (this.gv == null)
      this.gv = createGV(); 
    return this.gv;
  }
  
  protected StandardGlyphVector createGV() {
    FontRenderContext fontRenderContext = this.source.getFRC();
    int i = this.source.getLayoutFlags();
    char[] arrayOfChar = this.source.getChars();
    int j = this.source.getStart();
    int k = this.source.getLength();
    GlyphLayout glyphLayout = GlyphLayout.get(null);
    this.gv = glyphLayout.layout(this.font, fontRenderContext, arrayOfChar, j, k, i, null);
    GlyphLayout.done(glyphLayout);
    return this.gv;
  }
  
  public int getNumCharacters() { return this.source.getLength(); }
  
  public CoreMetrics getCoreMetrics() { return this.cm; }
  
  public float getCharX(int paramInt) {
    validate(paramInt);
    float[] arrayOfFloat = getCharinfo();
    int i = l2v(paramInt) * 8 + 0;
    return (arrayOfFloat == null || i >= arrayOfFloat.length) ? 0.0F : arrayOfFloat[i];
  }
  
  public float getCharY(int paramInt) {
    validate(paramInt);
    float[] arrayOfFloat = getCharinfo();
    int i = l2v(paramInt) * 8 + 1;
    return (arrayOfFloat == null || i >= arrayOfFloat.length) ? 0.0F : arrayOfFloat[i];
  }
  
  public float getCharAdvance(int paramInt) {
    validate(paramInt);
    float[] arrayOfFloat = getCharinfo();
    int i = l2v(paramInt) * 8 + 2;
    return (arrayOfFloat == null || i >= arrayOfFloat.length) ? 0.0F : arrayOfFloat[i];
  }
  
  public Rectangle2D handleGetCharVisualBounds(int paramInt) {
    validate(paramInt);
    float[] arrayOfFloat = getCharinfo();
    paramInt = l2v(paramInt) * 8;
    return (arrayOfFloat == null || paramInt + 7 >= arrayOfFloat.length) ? new Rectangle2D.Float() : new Rectangle2D.Float(arrayOfFloat[paramInt + 4], arrayOfFloat[paramInt + 5], arrayOfFloat[paramInt + 6], arrayOfFloat[paramInt + 7]);
  }
  
  public Rectangle2D getCharVisualBounds(int paramInt, float paramFloat1, float paramFloat2) {
    Rectangle2D rectangle2D = this.decorator.getCharVisualBounds(this, paramInt);
    if (paramFloat1 != 0.0F || paramFloat2 != 0.0F)
      rectangle2D.setRect(rectangle2D.getX() + paramFloat1, rectangle2D.getY() + paramFloat2, rectangle2D.getWidth(), rectangle2D.getHeight()); 
    return rectangle2D;
  }
  
  private void validate(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("index " + paramInt + " < 0"); 
    if (paramInt >= this.source.getLength())
      throw new IllegalArgumentException("index " + paramInt + " < " + this.source.getLength()); 
  }
  
  public int logicalToVisual(int paramInt) {
    validate(paramInt);
    return l2v(paramInt);
  }
  
  public int visualToLogical(int paramInt) {
    validate(paramInt);
    return v2l(paramInt);
  }
  
  public int getLineBreakIndex(int paramInt, float paramFloat) {
    float[] arrayOfFloat = getCharinfo();
    int i = this.source.getLength();
    paramInt--;
    while (paramFloat >= 0.0F && ++paramInt < i) {
      int j = l2v(paramInt) * 8 + 2;
      if (j >= arrayOfFloat.length)
        break; 
      float f = arrayOfFloat[j];
      paramFloat -= f;
    } 
    return paramInt;
  }
  
  public float getAdvanceBetween(int paramInt1, int paramInt2) {
    float f = 0.0F;
    float[] arrayOfFloat = getCharinfo();
    paramInt1--;
    while (++paramInt1 < paramInt2) {
      int i = l2v(paramInt1) * 8 + 2;
      if (i >= arrayOfFloat.length)
        break; 
      f += arrayOfFloat[i];
    } 
    return f;
  }
  
  public boolean caretAtOffsetIsValid(int paramInt) {
    if (paramInt == 0 || paramInt == this.source.getLength())
      return true; 
    char c = this.source.getChars()[this.source.getStart() + paramInt];
    if (c == '\t' || c == '\n' || c == '\r')
      return true; 
    int i = l2v(paramInt);
    int j = i * 8 + 2;
    float[] arrayOfFloat = getCharinfo();
    return (arrayOfFloat == null || j >= arrayOfFloat.length) ? false : ((arrayOfFloat[j] != 0.0F));
  }
  
  private final float[] getCharinfo() {
    if (this.charinfo == null)
      this.charinfo = createCharinfo(); 
    return this.charinfo;
  }
  
  protected float[] createCharinfo() {
    StandardGlyphVector standardGlyphVector = getGV();
    float[] arrayOfFloat = null;
    try {
      arrayOfFloat = standardGlyphVector.getGlyphInfo();
    } catch (Exception exception) {
      System.out.println(this.source);
    } 
    int i = standardGlyphVector.getNumGlyphs();
    if (i == 0)
      return arrayOfFloat; 
    int[] arrayOfInt = standardGlyphVector.getGlyphCharIndices(0, i, null);
    boolean bool1 = false;
    if (bool1) {
      System.err.println("number of glyphs: " + i);
      for (byte b = 0; b < i; b++)
        System.err.println("g: " + b + ", x: " + arrayOfFloat[b * 8 + 0] + ", a: " + arrayOfFloat[b * 8 + 2] + ", n: " + arrayOfInt[b]); 
    } 
    int j = arrayOfInt[0];
    int k = j;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = i;
    int i5 = 8;
    int i6 = 1;
    boolean bool2 = ((this.source.getLayoutFlags() & true) == 0) ? 1 : 0;
    if (!bool2) {
      j = arrayOfInt[i - 1];
      k = j;
      m = 0;
      n = arrayOfFloat.length - 8;
      i1 = 0;
      i2 = arrayOfFloat.length - 8;
      i3 = i - 1;
      i4 = -1;
      i5 = -8;
      i6 = -1;
    } 
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f4 = 0.0F;
    float f5 = 0.0F;
    float f6 = 0.0F;
    float f7 = 0.0F;
    boolean bool3 = false;
    while (i3 != i4) {
      boolean bool = false;
      byte b = 0;
      j = arrayOfInt[i3];
      k = j;
      i3 += i6;
      for (i2 += i5; i3 != i4 && (arrayOfFloat[i2 + 2] == 0.0F || j != m || arrayOfInt[i3] <= k || k - j > b); i2 += i5) {
        if (!bool) {
          int i7 = i2 - i5;
          f1 = arrayOfFloat[i7 + 0];
          f2 = f1 + arrayOfFloat[i7 + 2];
          f3 = arrayOfFloat[i7 + 4];
          f4 = arrayOfFloat[i7 + 5];
          f5 = f3 + arrayOfFloat[i7 + 6];
          f6 = f4 + arrayOfFloat[i7 + 7];
          bool = true;
        } 
        b++;
        float f8 = arrayOfFloat[i2 + 2];
        if (f8 != 0.0F) {
          float f = arrayOfFloat[i2 + 0];
          f1 = Math.min(f1, f);
          f2 = Math.max(f2, f + f8);
        } 
        float f9 = arrayOfFloat[i2 + 6];
        if (f9 != 0.0F) {
          float f10 = arrayOfFloat[i2 + 4];
          float f11 = arrayOfFloat[i2 + 5];
          f3 = Math.min(f3, f10);
          f4 = Math.min(f4, f11);
          f5 = Math.max(f5, f10 + f9);
          f6 = Math.max(f6, f11 + arrayOfFloat[i2 + 7]);
        } 
        j = Math.min(j, arrayOfInt[i3]);
        k = Math.max(k, arrayOfInt[i3]);
        i3 += i6;
      } 
      if (bool1)
        System.out.println("minIndex = " + j + ", maxIndex = " + k); 
      m = k + 1;
      arrayOfFloat[n + 1] = f7;
      arrayOfFloat[n + 3] = 0.0F;
      if (bool) {
        arrayOfFloat[n + 0] = f1;
        arrayOfFloat[n + 2] = f2 - f1;
        arrayOfFloat[n + 4] = f3;
        arrayOfFloat[n + 5] = f4;
        arrayOfFloat[n + 6] = f5 - f3;
        arrayOfFloat[n + 7] = f6 - f4;
        if (k - j < b)
          bool3 = true; 
        if (j < k) {
          if (!bool2)
            f2 = f1; 
          f5 -= f3;
          f6 -= f4;
          int i7 = j;
          int i8 = n / 8;
          while (j < k) {
            j++;
            i1 += i6;
            n += i5;
            if ((n < 0 || n >= arrayOfFloat.length) && bool1)
              System.out.println("minIndex = " + i7 + ", maxIndex = " + k + ", cp = " + i8); 
            arrayOfFloat[n + 0] = f2;
            arrayOfFloat[n + 1] = f7;
            arrayOfFloat[n + 2] = 0.0F;
            arrayOfFloat[n + 3] = 0.0F;
            arrayOfFloat[n + 4] = f3;
            arrayOfFloat[n + 5] = f4;
            arrayOfFloat[n + 6] = f5;
            arrayOfFloat[n + 7] = f6;
          } 
        } 
        bool = false;
      } else if (bool3) {
        int i7 = i2 - i5;
        arrayOfFloat[n + 0] = arrayOfFloat[i7 + 0];
        arrayOfFloat[n + 2] = arrayOfFloat[i7 + 2];
        arrayOfFloat[n + 4] = arrayOfFloat[i7 + 4];
        arrayOfFloat[n + 5] = arrayOfFloat[i7 + 5];
        arrayOfFloat[n + 6] = arrayOfFloat[i7 + 6];
        arrayOfFloat[n + 7] = arrayOfFloat[i7 + 7];
      } 
      n += i5;
      i1 += i6;
    } 
    if (bool3 && !bool2) {
      n -= i5;
      System.arraycopy(arrayOfFloat, n, arrayOfFloat, 0, arrayOfFloat.length - n);
    } 
    if (bool1) {
      char[] arrayOfChar = this.source.getChars();
      int i7 = this.source.getStart();
      int i8 = this.source.getLength();
      System.out.println("char info for " + i8 + " characters");
      byte b = 0;
      while (b < i8 * 8)
        System.out.println(" ch: " + Integer.toHexString(arrayOfChar[i7 + v2l(b / 8)]) + " x: " + arrayOfFloat[b++] + " y: " + arrayOfFloat[b++] + " xa: " + arrayOfFloat[b++] + " ya: " + arrayOfFloat[b++] + " l: " + arrayOfFloat[b++] + " t: " + arrayOfFloat[b++] + " w: " + arrayOfFloat[b++] + " h: " + arrayOfFloat[b++]); 
    } 
    return arrayOfFloat;
  }
  
  protected int l2v(int paramInt) { return ((this.source.getLayoutFlags() & true) == 0) ? paramInt : (this.source.getLength() - 1 - paramInt); }
  
  protected int v2l(int paramInt) { return ((this.source.getLayoutFlags() & true) == 0) ? paramInt : (this.source.getLength() - 1 - paramInt); }
  
  public TextLineComponent getSubset(int paramInt1, int paramInt2, int paramInt3) { return new ExtendedTextSourceLabel(this.source.getSubSource(paramInt1, paramInt2 - paramInt1, paramInt3), this.decorator); }
  
  public String toString() {
    this.source;
    return this.source.toString(false);
  }
  
  public int getNumJustificationInfos() { return getGV().getNumGlyphs(); }
  
  public void getJustificationInfos(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2, int paramInt3) {
    StandardGlyphVector standardGlyphVector = getGV();
    float[] arrayOfFloat = getCharinfo();
    float f = standardGlyphVector.getFont().getSize2D();
    GlyphJustificationInfo glyphJustificationInfo1 = new GlyphJustificationInfo(0.0F, false, 3, 0.0F, 0.0F, false, 3, 0.0F, 0.0F);
    GlyphJustificationInfo glyphJustificationInfo2 = new GlyphJustificationInfo(f, true, 1, 0.0F, f, true, 1, 0.0F, f / 4.0F);
    GlyphJustificationInfo glyphJustificationInfo3 = new GlyphJustificationInfo(f, true, 2, f, f, false, 3, 0.0F, 0.0F);
    char[] arrayOfChar = this.source.getChars();
    int i = this.source.getStart();
    int j = standardGlyphVector.getNumGlyphs();
    int k = 0;
    int m = j;
    boolean bool = ((this.source.getLayoutFlags() & true) == 0) ? 1 : 0;
    if (paramInt2 != 0 || paramInt3 != this.source.getLength())
      if (bool) {
        k = paramInt2;
        m = paramInt3;
      } else {
        k = j - paramInt3;
        m = j - paramInt2;
      }  
    for (int n = 0; n < j; n++) {
      GlyphJustificationInfo glyphJustificationInfo = null;
      if (n >= k && n < m)
        if (arrayOfFloat[n * 8 + 2] == 0.0F) {
          glyphJustificationInfo = glyphJustificationInfo1;
        } else {
          int i1 = v2l(n);
          char c = arrayOfChar[i + i1];
          if (Character.isWhitespace(c)) {
            glyphJustificationInfo = glyphJustificationInfo2;
          } else if ((c >= '一' && c < 'ꀀ') || (c >= '가' && c < 'ힰ') || (c >= '豈' && c < 'ﬀ')) {
            glyphJustificationInfo = glyphJustificationInfo3;
          } else {
            glyphJustificationInfo = glyphJustificationInfo1;
          } 
        }  
      paramArrayOfGlyphJustificationInfo[paramInt1 + n] = glyphJustificationInfo;
    } 
  }
  
  public TextLineComponent applyJustificationDeltas(float[] paramArrayOfFloat, int paramInt, boolean[] paramArrayOfBoolean) {
    float[] arrayOfFloat1 = (float[])getCharinfo().clone();
    paramArrayOfBoolean[0] = false;
    StandardGlyphVector standardGlyphVector = (StandardGlyphVector)getGV().clone();
    float[] arrayOfFloat2 = standardGlyphVector.getGlyphPositions(null);
    int i = standardGlyphVector.getNumGlyphs();
    char[] arrayOfChar = this.source.getChars();
    int j = this.source.getStart();
    float f = 0.0F;
    for (byte b = 0; b < i; b++) {
      if (Character.isWhitespace(arrayOfChar[j + v2l(b)])) {
        arrayOfFloat2[b * 2] = arrayOfFloat2[b * 2] + f;
        float f1 = paramArrayOfFloat[paramInt + b * 2] + paramArrayOfFloat[paramInt + b * 2 + 1];
        arrayOfFloat1[b * 8 + 0] = arrayOfFloat1[b * 8 + 0] + f;
        arrayOfFloat1[b * 8 + 4] = arrayOfFloat1[b * 8 + 4] + f;
        arrayOfFloat1[b * 8 + 2] = arrayOfFloat1[b * 8 + 2] + f1;
        f += f1;
      } else {
        f += paramArrayOfFloat[paramInt + b * 2];
        arrayOfFloat2[b * 2] = arrayOfFloat2[b * 2] + f;
        arrayOfFloat1[b * 8 + 0] = arrayOfFloat1[b * 8 + 0] + f;
        arrayOfFloat1[b * 8 + 4] = arrayOfFloat1[b * 8 + 4] + f;
        f += paramArrayOfFloat[paramInt + b * 2 + 1];
      } 
    } 
    arrayOfFloat2[i * 2] = arrayOfFloat2[i * 2] + f;
    standardGlyphVector.setGlyphPositions(arrayOfFloat2);
    ExtendedTextSourceLabel extendedTextSourceLabel = new ExtendedTextSourceLabel(this.source, this.decorator);
    extendedTextSourceLabel.gv = standardGlyphVector;
    extendedTextSourceLabel.charinfo = arrayOfFloat1;
    return extendedTextSourceLabel;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\ExtendedTextSourceLabel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */