package javax.swing.text;

import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;

public class Utilities {
  static JComponent getJComponent(View paramView) {
    if (paramView != null) {
      Container container = paramView.getContainer();
      if (container instanceof JComponent)
        return (JComponent)container; 
    } 
    return null;
  }
  
  public static final int drawTabbedText(Segment paramSegment, int paramInt1, int paramInt2, Graphics paramGraphics, TabExpander paramTabExpander, int paramInt3) { return drawTabbedText(null, paramSegment, paramInt1, paramInt2, paramGraphics, paramTabExpander, paramInt3); }
  
  static final int drawTabbedText(View paramView, Segment paramSegment, int paramInt1, int paramInt2, Graphics paramGraphics, TabExpander paramTabExpander, int paramInt3) { return drawTabbedText(paramView, paramSegment, paramInt1, paramInt2, paramGraphics, paramTabExpander, paramInt3, null); }
  
  static final int drawTabbedText(View paramView, Segment paramSegment, int paramInt1, int paramInt2, Graphics paramGraphics, TabExpander paramTabExpander, int paramInt3, int[] paramArrayOfInt) {
    JComponent jComponent = getJComponent(paramView);
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(jComponent, paramGraphics);
    int i = paramInt1;
    char[] arrayOfChar = paramSegment.array;
    int j = paramSegment.offset;
    byte b = 0;
    int k = paramSegment.offset;
    int m = 0;
    int n = -1;
    int i1 = 0;
    int i2 = 0;
    if (paramArrayOfInt != null) {
      int i5 = -paramInt3 + j;
      View view = null;
      if (paramView != null && (view = paramView.getParent()) != null)
        i5 += view.getStartOffset(); 
      m = paramArrayOfInt[0];
      n = paramArrayOfInt[1] + i5;
      i1 = paramArrayOfInt[2] + i5;
      i2 = paramArrayOfInt[3] + i5;
    } 
    int i3 = paramSegment.offset + paramSegment.count;
    for (int i4 = j; i4 < i3; i4++) {
      if (arrayOfChar[i4] == '\t' || ((m != 0 || i4 <= n) && arrayOfChar[i4] == ' ' && i1 <= i4 && i4 <= i2)) {
        if (b) {
          i = SwingUtilities2.drawChars(jComponent, paramGraphics, arrayOfChar, k, b, paramInt1, paramInt2);
          b = 0;
        } 
        k = i4 + 1;
        if (arrayOfChar[i4] == '\t') {
          if (paramTabExpander != null) {
            i = (int)paramTabExpander.nextTabStop(i, paramInt3 + i4 - j);
          } else {
            i += fontMetrics.charWidth(' ');
          } 
        } else if (arrayOfChar[i4] == ' ') {
          i += fontMetrics.charWidth(' ') + m;
          if (i4 <= n)
            i++; 
        } 
        paramInt1 = i;
      } else if (arrayOfChar[i4] == '\n' || arrayOfChar[i4] == '\r') {
        if (b > 0) {
          i = SwingUtilities2.drawChars(jComponent, paramGraphics, arrayOfChar, k, b, paramInt1, paramInt2);
          b = 0;
        } 
        k = i4 + 1;
        paramInt1 = i;
      } else {
        b++;
      } 
    } 
    if (b > 0)
      i = SwingUtilities2.drawChars(jComponent, paramGraphics, arrayOfChar, k, b, paramInt1, paramInt2); 
    return i;
  }
  
  public static final int getTabbedTextWidth(Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, TabExpander paramTabExpander, int paramInt2) { return getTabbedTextWidth(null, paramSegment, paramFontMetrics, paramInt1, paramTabExpander, paramInt2, null); }
  
  static final int getTabbedTextWidth(View paramView, Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, TabExpander paramTabExpander, int paramInt2, int[] paramArrayOfInt) {
    int i = paramInt1;
    char[] arrayOfChar = paramSegment.array;
    int j = paramSegment.offset;
    int k = paramSegment.offset + paramSegment.count;
    int m = 0;
    int n = 0;
    int i1 = -1;
    int i2 = 0;
    int i3 = 0;
    if (paramArrayOfInt != null) {
      int i5 = -paramInt2 + j;
      View view = null;
      if (paramView != null && (view = paramView.getParent()) != null)
        i5 += view.getStartOffset(); 
      n = paramArrayOfInt[0];
      i1 = paramArrayOfInt[1] + i5;
      i2 = paramArrayOfInt[2] + i5;
      i3 = paramArrayOfInt[3] + i5;
    } 
    for (int i4 = j; i4 < k; i4++) {
      if (arrayOfChar[i4] == '\t' || ((n != 0 || i4 <= i1) && arrayOfChar[i4] == ' ' && i2 <= i4 && i4 <= i3)) {
        i += paramFontMetrics.charsWidth(arrayOfChar, i4 - m, m);
        m = 0;
        if (arrayOfChar[i4] == '\t') {
          if (paramTabExpander != null) {
            i = (int)paramTabExpander.nextTabStop(i, paramInt2 + i4 - j);
          } else {
            i += paramFontMetrics.charWidth(' ');
          } 
        } else if (arrayOfChar[i4] == ' ') {
          i += paramFontMetrics.charWidth(' ') + n;
          if (i4 <= i1)
            i++; 
        } 
      } else if (arrayOfChar[i4] == '\n') {
        i += paramFontMetrics.charsWidth(arrayOfChar, i4 - m, m);
        m = 0;
      } else {
        m++;
      } 
    } 
    i += paramFontMetrics.charsWidth(arrayOfChar, k - m, m);
    return i - paramInt1;
  }
  
  public static final int getTabbedTextOffset(Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3) { return getTabbedTextOffset(paramSegment, paramFontMetrics, paramInt1, paramInt2, paramTabExpander, paramInt3, true); }
  
  static final int getTabbedTextOffset(View paramView, Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3, int[] paramArrayOfInt) { return getTabbedTextOffset(paramView, paramSegment, paramFontMetrics, paramInt1, paramInt2, paramTabExpander, paramInt3, true, paramArrayOfInt); }
  
  public static final int getTabbedTextOffset(Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3, boolean paramBoolean) { return getTabbedTextOffset(null, paramSegment, paramFontMetrics, paramInt1, paramInt2, paramTabExpander, paramInt3, paramBoolean, null); }
  
  static final int getTabbedTextOffset(View paramView, Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3, boolean paramBoolean, int[] paramArrayOfInt) {
    if (paramInt1 >= paramInt2)
      return 0; 
    int i = paramInt1;
    char[] arrayOfChar = paramSegment.array;
    int j = paramSegment.offset;
    int k = paramSegment.count;
    int m = 0;
    int n = -1;
    int i1 = 0;
    int i2 = 0;
    if (paramArrayOfInt != null) {
      int i5 = -paramInt3 + j;
      View view = null;
      if (paramView != null && (view = paramView.getParent()) != null)
        i5 += view.getStartOffset(); 
      m = paramArrayOfInt[0];
      n = paramArrayOfInt[1] + i5;
      i1 = paramArrayOfInt[2] + i5;
      i2 = paramArrayOfInt[3] + i5;
    } 
    int i3 = paramSegment.offset + paramSegment.count;
    for (int i4 = paramSegment.offset; i4 < i3; i4++) {
      if (arrayOfChar[i4] == '\t' || ((m != 0 || i4 <= n) && arrayOfChar[i4] == ' ' && i1 <= i4 && i4 <= i2)) {
        if (arrayOfChar[i4] == '\t') {
          if (paramTabExpander != null) {
            i = (int)paramTabExpander.nextTabStop(i, paramInt3 + i4 - j);
          } else {
            i += paramFontMetrics.charWidth(' ');
          } 
        } else if (arrayOfChar[i4] == ' ') {
          i += paramFontMetrics.charWidth(' ') + m;
          if (i4 <= n)
            i++; 
        } 
      } else {
        i += paramFontMetrics.charWidth(arrayOfChar[i4]);
      } 
      if (paramInt2 < i) {
        int i5;
        if (paramBoolean) {
          i5 = i4 + 1 - j;
          int i6 = paramFontMetrics.charsWidth(arrayOfChar, j, i5);
          int i7 = paramInt2 - paramInt1;
          if (i7 < i6)
            while (i5 > 0) {
              int i8 = (i5 > 1) ? paramFontMetrics.charsWidth(arrayOfChar, j, i5 - 1) : 0;
              if (i7 >= i8) {
                if (i7 - i8 < i6 - i7)
                  i5--; 
                break;
              } 
              i6 = i8;
              i5--;
            }  
        } else {
          for (i5 = i4 - j; i5 > 0 && paramFontMetrics.charsWidth(arrayOfChar, j, i5) > paramInt2 - paramInt1; i5--);
        } 
        return i5;
      } 
    } 
    return k;
  }
  
  public static final int getBreakLocation(Segment paramSegment, FontMetrics paramFontMetrics, int paramInt1, int paramInt2, TabExpander paramTabExpander, int paramInt3) {
    char[] arrayOfChar = paramSegment.array;
    int i = paramSegment.offset;
    int j = paramSegment.count;
    int k = getTabbedTextOffset(paramSegment, paramFontMetrics, paramInt1, paramInt2, paramTabExpander, paramInt3, false);
    if (k >= j - 1)
      return j; 
    for (int m = i + k; m >= i; m--) {
      char c = arrayOfChar[m];
      if (c < 'Ā') {
        if (Character.isWhitespace(c)) {
          k = m - i + 1;
          break;
        } 
      } else {
        BreakIterator breakIterator = BreakIterator.getLineInstance();
        breakIterator.setText(paramSegment);
        int n = breakIterator.preceding(m + 1);
        if (n > i)
          k = n - i; 
        break;
      } 
    } 
    return k;
  }
  
  public static final int getRowStart(JTextComponent paramJTextComponent, int paramInt) throws BadLocationException {
    Rectangle rectangle = paramJTextComponent.modelToView(paramInt);
    if (rectangle == null)
      return -1; 
    int i = paramInt;
    int j = rectangle.y;
    while (rectangle != null && j == rectangle.y) {
      if (rectangle.height != 0)
        paramInt = i; 
      rectangle = (--i >= 0) ? paramJTextComponent.modelToView(i) : null;
    } 
    return paramInt;
  }
  
  public static final int getRowEnd(JTextComponent paramJTextComponent, int paramInt) throws BadLocationException {
    Rectangle rectangle = paramJTextComponent.modelToView(paramInt);
    if (rectangle == null)
      return -1; 
    int i = paramJTextComponent.getDocument().getLength();
    int j = paramInt;
    int k = rectangle.y;
    while (rectangle != null && k == rectangle.y) {
      if (rectangle.height != 0)
        paramInt = j; 
      rectangle = (++j <= i) ? paramJTextComponent.modelToView(j) : null;
    } 
    return paramInt;
  }
  
  public static final int getPositionAbove(JTextComponent paramJTextComponent, int paramInt1, int paramInt2) throws BadLocationException {
    int i = getRowStart(paramJTextComponent, paramInt1) - 1;
    if (i < 0)
      return -1; 
    int j = Integer.MAX_VALUE;
    int k = 0;
    Rectangle rectangle = null;
    if (i >= 0) {
      rectangle = paramJTextComponent.modelToView(i);
      k = rectangle.y;
    } 
    while (rectangle != null && k == rectangle.y) {
      int m = Math.abs(rectangle.x - paramInt2);
      if (m < j) {
        paramInt1 = i;
        j = m;
      } 
      rectangle = (--i >= 0) ? paramJTextComponent.modelToView(i) : null;
    } 
    return paramInt1;
  }
  
  public static final int getPositionBelow(JTextComponent paramJTextComponent, int paramInt1, int paramInt2) throws BadLocationException {
    int i = getRowEnd(paramJTextComponent, paramInt1) + 1;
    if (i <= 0)
      return -1; 
    int j = Integer.MAX_VALUE;
    int k = paramJTextComponent.getDocument().getLength();
    int m = 0;
    Rectangle rectangle = null;
    if (i <= k) {
      rectangle = paramJTextComponent.modelToView(i);
      m = rectangle.y;
    } 
    while (rectangle != null && m == rectangle.y) {
      int n = Math.abs(paramInt2 - rectangle.x);
      if (n < j) {
        paramInt1 = i;
        j = n;
      } 
      rectangle = (++i <= k) ? paramJTextComponent.modelToView(i) : null;
    } 
    return paramInt1;
  }
  
  public static final int getWordStart(JTextComponent paramJTextComponent, int paramInt) throws BadLocationException {
    Document document = paramJTextComponent.getDocument();
    Element element = getParagraphElement(paramJTextComponent, paramInt);
    if (element == null)
      throw new BadLocationException("No word at " + paramInt, paramInt); 
    int i = element.getStartOffset();
    int j = Math.min(element.getEndOffset(), document.getLength());
    Segment segment = SegmentCache.getSharedSegment();
    document.getText(i, j - i, segment);
    if (segment.count > 0) {
      BreakIterator breakIterator = BreakIterator.getWordInstance(paramJTextComponent.getLocale());
      breakIterator.setText(segment);
      int k = segment.offset + paramInt - i;
      if (k >= breakIterator.last())
        k = breakIterator.last() - 1; 
      breakIterator.following(k);
      paramInt = i + breakIterator.previous() - segment.offset;
    } 
    SegmentCache.releaseSharedSegment(segment);
    return paramInt;
  }
  
  public static final int getWordEnd(JTextComponent paramJTextComponent, int paramInt) throws BadLocationException {
    Document document = paramJTextComponent.getDocument();
    Element element = getParagraphElement(paramJTextComponent, paramInt);
    if (element == null)
      throw new BadLocationException("No word at " + paramInt, paramInt); 
    int i = element.getStartOffset();
    int j = Math.min(element.getEndOffset(), document.getLength());
    Segment segment = SegmentCache.getSharedSegment();
    document.getText(i, j - i, segment);
    if (segment.count > 0) {
      BreakIterator breakIterator = BreakIterator.getWordInstance(paramJTextComponent.getLocale());
      breakIterator.setText(segment);
      int k = paramInt - i + segment.offset;
      if (k >= breakIterator.last())
        k = breakIterator.last() - 1; 
      paramInt = i + breakIterator.following(k) - segment.offset;
    } 
    SegmentCache.releaseSharedSegment(segment);
    return paramInt;
  }
  
  public static final int getNextWord(JTextComponent paramJTextComponent, int paramInt) throws BadLocationException {
    Element element = getParagraphElement(paramJTextComponent, paramInt);
    int i;
    for (i = getNextWordInParagraph(paramJTextComponent, element, paramInt, false); i == -1; i = getNextWordInParagraph(paramJTextComponent, element, paramInt, true)) {
      paramInt = element.getEndOffset();
      element = getParagraphElement(paramJTextComponent, paramInt);
    } 
    return i;
  }
  
  static int getNextWordInParagraph(JTextComponent paramJTextComponent, Element paramElement, int paramInt, boolean paramBoolean) throws BadLocationException {
    if (paramElement == null)
      throw new BadLocationException("No more words", paramInt); 
    Document document = paramElement.getDocument();
    int i = paramElement.getStartOffset();
    int j = Math.min(paramElement.getEndOffset(), document.getLength());
    if (paramInt >= j || paramInt < i)
      throw new BadLocationException("No more words", paramInt); 
    Segment segment = SegmentCache.getSharedSegment();
    document.getText(i, j - i, segment);
    BreakIterator breakIterator = BreakIterator.getWordInstance(paramJTextComponent.getLocale());
    breakIterator.setText(segment);
    if (paramBoolean && breakIterator.first() == segment.offset + paramInt - i && !Character.isWhitespace(segment.array[breakIterator.first()]))
      return paramInt; 
    int k = breakIterator.following(segment.offset + paramInt - i);
    if (k == -1 || k >= segment.offset + segment.count)
      return -1; 
    char c = segment.array[k];
    if (!Character.isWhitespace(c))
      return i + k - segment.offset; 
    k = breakIterator.next();
    if (k != -1) {
      paramInt = i + k - segment.offset;
      if (paramInt != j)
        return paramInt; 
    } 
    SegmentCache.releaseSharedSegment(segment);
    return -1;
  }
  
  public static final int getPreviousWord(JTextComponent paramJTextComponent, int paramInt) throws BadLocationException {
    Element element = getParagraphElement(paramJTextComponent, paramInt);
    int i;
    for (i = getPrevWordInParagraph(paramJTextComponent, element, paramInt); i == -1; i = getPrevWordInParagraph(paramJTextComponent, element, paramInt)) {
      paramInt = element.getStartOffset() - 1;
      element = getParagraphElement(paramJTextComponent, paramInt);
    } 
    return i;
  }
  
  static int getPrevWordInParagraph(JTextComponent paramJTextComponent, Element paramElement, int paramInt) throws BadLocationException {
    if (paramElement == null)
      throw new BadLocationException("No more words", paramInt); 
    Document document = paramElement.getDocument();
    int i = paramElement.getStartOffset();
    int j = paramElement.getEndOffset();
    if (paramInt > j || paramInt < i)
      throw new BadLocationException("No more words", paramInt); 
    Segment segment = SegmentCache.getSharedSegment();
    document.getText(i, j - i, segment);
    BreakIterator breakIterator = BreakIterator.getWordInstance(paramJTextComponent.getLocale());
    breakIterator.setText(segment);
    if (breakIterator.following(segment.offset + paramInt - i) == -1)
      breakIterator.last(); 
    int k = breakIterator.previous();
    if (k == segment.offset + paramInt - i)
      k = breakIterator.previous(); 
    if (k == -1)
      return -1; 
    char c = segment.array[k];
    if (!Character.isWhitespace(c))
      return i + k - segment.offset; 
    k = breakIterator.previous();
    if (k != -1)
      return i + k - segment.offset; 
    SegmentCache.releaseSharedSegment(segment);
    return -1;
  }
  
  public static final Element getParagraphElement(JTextComponent paramJTextComponent, int paramInt) {
    Document document = paramJTextComponent.getDocument();
    if (document instanceof StyledDocument)
      return ((StyledDocument)document).getParagraphElement(paramInt); 
    Element element1 = document.getDefaultRootElement();
    int i = element1.getElementIndex(paramInt);
    Element element2 = element1.getElement(i);
    return (paramInt >= element2.getStartOffset() && paramInt < element2.getEndOffset()) ? element2 : null;
  }
  
  static boolean isComposedTextElement(Document paramDocument, int paramInt) {
    Element element;
    for (element = paramDocument.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(paramInt)));
    return isComposedTextElement(element);
  }
  
  static boolean isComposedTextElement(Element paramElement) {
    AttributeSet attributeSet = paramElement.getAttributes();
    return isComposedTextAttributeDefined(attributeSet);
  }
  
  static boolean isComposedTextAttributeDefined(AttributeSet paramAttributeSet) { return (paramAttributeSet != null && paramAttributeSet.isDefined(StyleConstants.ComposedTextAttribute)); }
  
  static int drawComposedText(View paramView, AttributeSet paramAttributeSet, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws BadLocationException {
    Graphics2D graphics2D = (Graphics2D)paramGraphics;
    AttributedString attributedString = (AttributedString)paramAttributeSet.getAttribute(StyleConstants.ComposedTextAttribute);
    attributedString.addAttribute(TextAttribute.FONT, paramGraphics.getFont());
    if (paramInt3 >= paramInt4)
      return paramInt1; 
    AttributedCharacterIterator attributedCharacterIterator = attributedString.getIterator(null, paramInt3, paramInt4);
    return paramInt1 + (int)SwingUtilities2.drawString(getJComponent(paramView), graphics2D, attributedCharacterIterator, paramInt1, paramInt2);
  }
  
  static void paintComposedText(Graphics paramGraphics, Rectangle paramRectangle, GlyphView paramGlyphView) {
    if (paramGraphics instanceof Graphics2D) {
      Graphics2D graphics2D = (Graphics2D)paramGraphics;
      int i = paramGlyphView.getStartOffset();
      int j = paramGlyphView.getEndOffset();
      AttributeSet attributeSet = paramGlyphView.getElement().getAttributes();
      AttributedString attributedString = (AttributedString)attributeSet.getAttribute(StyleConstants.ComposedTextAttribute);
      int k = paramGlyphView.getElement().getStartOffset();
      int m = paramRectangle.y + paramRectangle.height - (int)paramGlyphView.getGlyphPainter().getDescent(paramGlyphView);
      int n = paramRectangle.x;
      attributedString.addAttribute(TextAttribute.FONT, paramGlyphView.getFont());
      attributedString.addAttribute(TextAttribute.FOREGROUND, paramGlyphView.getForeground());
      if (StyleConstants.isBold(paramGlyphView.getAttributes()))
        attributedString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD); 
      if (StyleConstants.isItalic(paramGlyphView.getAttributes()))
        attributedString.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE); 
      if (paramGlyphView.isUnderline())
        attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON); 
      if (paramGlyphView.isStrikeThrough())
        attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON); 
      if (paramGlyphView.isSuperscript())
        attributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER); 
      if (paramGlyphView.isSubscript())
        attributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB); 
      AttributedCharacterIterator attributedCharacterIterator = attributedString.getIterator(null, i - k, j - k);
      SwingUtilities2.drawString(getJComponent(paramGlyphView), graphics2D, attributedCharacterIterator, n, m);
    } 
  }
  
  static boolean isLeftToRight(Component paramComponent) { return paramComponent.getComponentOrientation().isLeftToRight(); }
  
  static int getNextVisualPositionFrom(View paramView, int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    int i;
    if (paramView.getViewCount() == 0)
      return paramInt1; 
    boolean bool = (paramInt2 == 1 || paramInt2 == 7) ? 1 : 0;
    if (paramInt1 == -1) {
      int j = bool ? (paramView.getViewCount() - 1) : 0;
      View view = paramView.getView(j);
      Shape shape = paramView.getChildAllocation(j, paramShape);
      i = view.getNextVisualPositionFrom(paramInt1, paramBias, shape, paramInt2, paramArrayOfBias);
      if (i == -1 && !bool && paramView.getViewCount() > 1) {
        view = paramView.getView(1);
        shape = paramView.getChildAllocation(1, paramShape);
        i = view.getNextVisualPositionFrom(-1, paramArrayOfBias[0], shape, paramInt2, paramArrayOfBias);
      } 
    } else {
      int k;
      int j = bool ? -1 : 1;
      if (paramBias == Position.Bias.Backward && paramInt1 > 0) {
        k = paramView.getViewIndex(paramInt1 - 1, Position.Bias.Forward);
      } else {
        k = paramView.getViewIndex(paramInt1, Position.Bias.Forward);
      } 
      View view = paramView.getView(k);
      Shape shape = paramView.getChildAllocation(k, paramShape);
      i = view.getNextVisualPositionFrom(paramInt1, paramBias, shape, paramInt2, paramArrayOfBias);
      if ((paramInt2 == 3 || paramInt2 == 7) && paramView instanceof CompositeView && ((CompositeView)paramView).flipEastAndWestAtEnds(paramInt1, paramBias))
        j *= -1; 
      k += j;
      if (i == -1 && k >= 0 && k < paramView.getViewCount()) {
        view = paramView.getView(k);
        shape = paramView.getChildAllocation(k, paramShape);
        i = view.getNextVisualPositionFrom(-1, paramBias, shape, paramInt2, paramArrayOfBias);
        if (i == paramInt1 && paramArrayOfBias[false] != paramBias)
          return getNextVisualPositionFrom(paramView, paramInt1, paramArrayOfBias[0], paramShape, paramInt2, paramArrayOfBias); 
      } else if (i != -1 && paramArrayOfBias[false] != paramBias && ((j == 1 && view.getEndOffset() == i) || (j == -1 && view.getStartOffset() == i)) && k >= 0 && k < paramView.getViewCount()) {
        view = paramView.getView(k);
        shape = paramView.getChildAllocation(k, paramShape);
        Position.Bias bias = paramArrayOfBias[0];
        int m = view.getNextVisualPositionFrom(-1, paramBias, shape, paramInt2, paramArrayOfBias);
        if (paramArrayOfBias[false] == paramBias) {
          i = m;
        } else {
          paramArrayOfBias[0] = bias;
        } 
      } 
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\Utilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */