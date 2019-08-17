package java.awt.font;

import java.awt.Font;
import java.text.AttributedCharacterIterator;
import java.text.Bidi;
import java.text.BreakIterator;
import java.util.Hashtable;
import java.util.Map;
import sun.font.AttributeValues;
import sun.font.BidiUtils;
import sun.font.TextLabelFactory;
import sun.font.TextLineComponent;

public final class TextMeasurer implements Cloneable {
  private static float EST_LINES = 2.1F;
  
  private FontRenderContext fFrc;
  
  private int fStart;
  
  private char[] fChars;
  
  private Bidi fBidi;
  
  private byte[] fLevels;
  
  private TextLineComponent[] fComponents;
  
  private int fComponentStart;
  
  private int fComponentLimit;
  
  private boolean haveLayoutWindow;
  
  private BreakIterator fLineBreak = null;
  
  private CharArrayIterator charIter = null;
  
  int layoutCount = 0;
  
  int layoutCharCount = 0;
  
  private StyledParagraph fParagraph;
  
  private boolean fIsDirectionLTR;
  
  private byte fBaseline;
  
  private float[] fBaselineOffsets;
  
  private float fJustifyRatio = 1.0F;
  
  private int formattedChars = 0;
  
  private static boolean wantStats = false;
  
  private boolean collectStats = false;
  
  public TextMeasurer(AttributedCharacterIterator paramAttributedCharacterIterator, FontRenderContext paramFontRenderContext) {
    this.fFrc = paramFontRenderContext;
    initAll(paramAttributedCharacterIterator);
  }
  
  protected Object clone() {
    TextMeasurer textMeasurer;
    try {
      textMeasurer = (TextMeasurer)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new Error();
    } 
    if (this.fComponents != null)
      textMeasurer.fComponents = (TextLineComponent[])this.fComponents.clone(); 
    return textMeasurer;
  }
  
  private void invalidateComponents() {
    this.fComponentStart = this.fComponentLimit = this.fChars.length;
    this.fComponents = null;
    this.haveLayoutWindow = false;
  }
  
  private void initAll(AttributedCharacterIterator paramAttributedCharacterIterator) {
    this.fStart = paramAttributedCharacterIterator.getBeginIndex();
    this.fChars = new char[paramAttributedCharacterIterator.getEndIndex() - this.fStart];
    byte b = 0;
    for (char c = paramAttributedCharacterIterator.first(); c != Character.MAX_VALUE; c = paramAttributedCharacterIterator.next())
      this.fChars[b++] = c; 
    paramAttributedCharacterIterator.first();
    this.fBidi = new Bidi(paramAttributedCharacterIterator);
    if (this.fBidi.isLeftToRight())
      this.fBidi = null; 
    paramAttributedCharacterIterator.first();
    Map map = paramAttributedCharacterIterator.getAttributes();
    NumericShaper numericShaper = AttributeValues.getNumericShaping(map);
    if (numericShaper != null)
      numericShaper.shape(this.fChars, 0, this.fChars.length); 
    this.fParagraph = new StyledParagraph(paramAttributedCharacterIterator, this.fChars);
    this.fJustifyRatio = AttributeValues.getJustification(map);
    boolean bool = TextLine.advanceToFirstFont(paramAttributedCharacterIterator);
    if (bool) {
      Font font = TextLine.getFontAtCurrentPos(paramAttributedCharacterIterator);
      int i = paramAttributedCharacterIterator.getIndex() - paramAttributedCharacterIterator.getBeginIndex();
      LineMetrics lineMetrics = font.getLineMetrics(this.fChars, i, i + 1, this.fFrc);
      this.fBaseline = (byte)lineMetrics.getBaselineIndex();
      this.fBaselineOffsets = lineMetrics.getBaselineOffsets();
    } else {
      GraphicAttribute graphicAttribute = (GraphicAttribute)map.get(TextAttribute.CHAR_REPLACEMENT);
      this.fBaseline = TextLayout.getBaselineFromGraphic(graphicAttribute);
      Hashtable hashtable = new Hashtable(5, 0.9F);
      Font font = new Font(hashtable);
      LineMetrics lineMetrics = font.getLineMetrics(" ", 0, 1, this.fFrc);
      this.fBaselineOffsets = lineMetrics.getBaselineOffsets();
    } 
    this.fBaselineOffsets = TextLine.getNormalizedOffsets(this.fBaselineOffsets, this.fBaseline);
    invalidateComponents();
  }
  
  private void generateComponents(int paramInt1, int paramInt2) {
    if (this.collectStats)
      this.formattedChars += paramInt2 - paramInt1; 
    byte b = 0;
    TextLabelFactory textLabelFactory = new TextLabelFactory(this.fFrc, this.fChars, this.fBidi, b);
    int[] arrayOfInt = null;
    if (this.fBidi != null) {
      this.fLevels = BidiUtils.getLevels(this.fBidi);
      int[] arrayOfInt1 = BidiUtils.createVisualToLogicalMap(this.fLevels);
      arrayOfInt = BidiUtils.createInverseMap(arrayOfInt1);
      this.fIsDirectionLTR = this.fBidi.baseIsLeftToRight();
    } else {
      this.fLevels = null;
      this.fIsDirectionLTR = true;
    } 
    try {
      this.fComponents = TextLine.getComponents(this.fParagraph, this.fChars, paramInt1, paramInt2, arrayOfInt, this.fLevels, textLabelFactory);
    } catch (IllegalArgumentException illegalArgumentException) {
      System.out.println("startingAt=" + paramInt1 + "; endingAt=" + paramInt2);
      System.out.println("fComponentLimit=" + this.fComponentLimit);
      throw illegalArgumentException;
    } 
    this.fComponentStart = paramInt1;
    this.fComponentLimit = paramInt2;
  }
  
  private int calcLineBreak(int paramInt, float paramFloat) {
    int i = paramInt;
    float f = paramFloat;
    int j = this.fComponentStart;
    byte b;
    for (b = 0; b < this.fComponents.length; b++) {
      int k = j + this.fComponents[b].getNumCharacters();
      if (k > i)
        break; 
      j = k;
    } 
    while (b < this.fComponents.length) {
      TextLineComponent textLineComponent = this.fComponents[b];
      int k = textLineComponent.getNumCharacters();
      int m = textLineComponent.getLineBreakIndex(i - j, f);
      if (m == k && b < this.fComponents.length) {
        f -= textLineComponent.getAdvanceBetween(i - j, m);
        j += k;
        i = j;
      } else {
        return j + m;
      } 
      b++;
    } 
    if (this.fComponentLimit < this.fChars.length) {
      generateComponents(paramInt, this.fChars.length);
      return calcLineBreak(paramInt, paramFloat);
    } 
    return this.fChars.length;
  }
  
  private int trailingCdWhitespaceStart(int paramInt1, int paramInt2) {
    if (this.fLevels != null) {
      byte b = (byte)(this.fIsDirectionLTR ? 0 : 1);
      int i = paramInt2;
      while (--i >= paramInt1) {
        if (this.fLevels[i] % 2 == b || Character.getDirectionality(this.fChars[i]) != 12)
          return ++i; 
      } 
    } 
    return paramInt1;
  }
  
  private TextLineComponent[] makeComponentsOnRange(int paramInt1, int paramInt2) {
    byte b3;
    int i = trailingCdWhitespaceStart(paramInt1, paramInt2);
    int j = this.fComponentStart;
    byte b1;
    for (b1 = 0; b1 < this.fComponents.length; b1++) {
      int i1 = j + this.fComponents[b1].getNumCharacters();
      if (i1 > paramInt1)
        break; 
      j = i1;
    } 
    boolean bool = false;
    int k = j;
    int m = b1;
    int n = 1;
    while (n) {
      b3 = k + this.fComponents[m].getNumCharacters();
      if (i > Math.max(k, paramInt1) && i < Math.min(b3, paramInt2))
        bool = true; 
      if (b3 >= paramInt2) {
        n = 0;
      } else {
        k = b3;
      } 
      m++;
    } 
    byte b2 = m - b1;
    if (bool)
      b2++; 
    TextLineComponent[] arrayOfTextLineComponent = new TextLineComponent[b2];
    k = 0;
    m = paramInt1;
    n = i;
    if (n == paramInt1) {
      b3 = this.fIsDirectionLTR ? 0 : 1;
      n = paramInt2;
    } else {
      b3 = 2;
    } 
    while (m < paramInt2) {
      int i1 = this.fComponents[b1].getNumCharacters();
      int i2 = j + i1;
      int i3 = Math.max(m, j);
      int i4 = Math.min(n, i2);
      arrayOfTextLineComponent[k++] = this.fComponents[b1].getSubset(i3 - j, i4 - j, b3);
      m += i4 - i3;
      if (m == n) {
        n = paramInt2;
        b3 = this.fIsDirectionLTR ? 0 : 1;
      } 
      if (m == i2) {
        b1++;
        j = i2;
      } 
    } 
    return arrayOfTextLineComponent;
  }
  
  private TextLine makeTextLineOnRange(int paramInt1, int paramInt2) {
    int[] arrayOfInt = null;
    byte[] arrayOfByte = null;
    if (this.fBidi != null) {
      Bidi bidi = this.fBidi.createLineBidi(paramInt1, paramInt2);
      arrayOfByte = BidiUtils.getLevels(bidi);
      int[] arrayOfInt1 = BidiUtils.createVisualToLogicalMap(arrayOfByte);
      arrayOfInt = BidiUtils.createInverseMap(arrayOfInt1);
    } 
    TextLineComponent[] arrayOfTextLineComponent = makeComponentsOnRange(paramInt1, paramInt2);
    return new TextLine(this.fFrc, arrayOfTextLineComponent, this.fBaselineOffsets, this.fChars, paramInt1, paramInt2, arrayOfInt, arrayOfByte, this.fIsDirectionLTR);
  }
  
  private void ensureComponents(int paramInt1, int paramInt2) {
    if (paramInt1 < this.fComponentStart || paramInt2 > this.fComponentLimit)
      generateComponents(paramInt1, paramInt2); 
  }
  
  private void makeLayoutWindow(int paramInt) {
    int i = paramInt;
    int j = this.fChars.length;
    if (this.layoutCount > 0 && !this.haveLayoutWindow) {
      float f = Math.max(this.layoutCharCount / this.layoutCount, 1);
      j = Math.min(paramInt + (int)(f * EST_LINES), this.fChars.length);
    } 
    if (paramInt > 0 || j < this.fChars.length) {
      if (this.charIter == null) {
        this.charIter = new CharArrayIterator(this.fChars);
      } else {
        this.charIter.reset(this.fChars);
      } 
      if (this.fLineBreak == null)
        this.fLineBreak = BreakIterator.getLineInstance(); 
      this.fLineBreak.setText(this.charIter);
      if (paramInt > 0 && !this.fLineBreak.isBoundary(paramInt))
        i = this.fLineBreak.preceding(paramInt); 
      if (j < this.fChars.length && !this.fLineBreak.isBoundary(j))
        j = this.fLineBreak.following(j); 
    } 
    ensureComponents(i, j);
    this.haveLayoutWindow = true;
  }
  
  public int getLineBreakIndex(int paramInt, float paramFloat) {
    int i = paramInt - this.fStart;
    if (!this.haveLayoutWindow || i < this.fComponentStart || i >= this.fComponentLimit)
      makeLayoutWindow(i); 
    return calcLineBreak(i, paramFloat) + this.fStart;
  }
  
  public float getAdvanceBetween(int paramInt1, int paramInt2) {
    int i = paramInt1 - this.fStart;
    int j = paramInt2 - this.fStart;
    ensureComponents(i, j);
    TextLine textLine = makeTextLineOnRange(i, j);
    return (textLine.getMetrics()).advance;
  }
  
  public TextLayout getLayout(int paramInt1, int paramInt2) {
    int i = paramInt1 - this.fStart;
    int j = paramInt2 - this.fStart;
    ensureComponents(i, j);
    TextLine textLine = makeTextLineOnRange(i, j);
    if (j < this.fChars.length) {
      this.layoutCharCount += paramInt2 - paramInt1;
      this.layoutCount++;
    } 
    return new TextLayout(textLine, this.fBaseline, this.fBaselineOffsets, this.fJustifyRatio);
  }
  
  private void printStats() {
    System.out.println("formattedChars: " + this.formattedChars);
    this.collectStats = false;
  }
  
  public void insertChar(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt) {
    if (this.collectStats)
      printStats(); 
    if (wantStats)
      this.collectStats = true; 
    this.fStart = paramAttributedCharacterIterator.getBeginIndex();
    int i = paramAttributedCharacterIterator.getEndIndex();
    if (i - this.fStart != this.fChars.length + 1)
      initAll(paramAttributedCharacterIterator); 
    char[] arrayOfChar = new char[i - this.fStart];
    int j = paramInt - this.fStart;
    System.arraycopy(this.fChars, 0, arrayOfChar, 0, j);
    char c = paramAttributedCharacterIterator.setIndex(paramInt);
    arrayOfChar[j] = c;
    System.arraycopy(this.fChars, j, arrayOfChar, j + 1, i - paramInt - 1);
    this.fChars = arrayOfChar;
    if (this.fBidi != null || Bidi.requiresBidi(arrayOfChar, j, j + 1) || paramAttributedCharacterIterator.getAttribute(TextAttribute.BIDI_EMBEDDING) != null) {
      this.fBidi = new Bidi(paramAttributedCharacterIterator);
      if (this.fBidi.isLeftToRight())
        this.fBidi = null; 
    } 
    this.fParagraph = StyledParagraph.insertChar(paramAttributedCharacterIterator, this.fChars, paramInt, this.fParagraph);
    invalidateComponents();
  }
  
  public void deleteChar(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt) {
    this.fStart = paramAttributedCharacterIterator.getBeginIndex();
    int i = paramAttributedCharacterIterator.getEndIndex();
    if (i - this.fStart != this.fChars.length - 1)
      initAll(paramAttributedCharacterIterator); 
    char[] arrayOfChar = new char[i - this.fStart];
    int j = paramInt - this.fStart;
    System.arraycopy(this.fChars, 0, arrayOfChar, 0, paramInt - this.fStart);
    System.arraycopy(this.fChars, j + 1, arrayOfChar, j, i - paramInt);
    this.fChars = arrayOfChar;
    if (this.fBidi != null) {
      this.fBidi = new Bidi(paramAttributedCharacterIterator);
      if (this.fBidi.isLeftToRight())
        this.fBidi = null; 
    } 
    this.fParagraph = StyledParagraph.deleteChar(paramAttributedCharacterIterator, this.fChars, paramInt, this.fParagraph);
    invalidateComponents();
  }
  
  char[] getChars() { return this.fChars; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\TextMeasurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */