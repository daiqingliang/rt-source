package javax.swing.text;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.text.BreakIterator;
import java.util.BitSet;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import sun.swing.SwingUtilities2;

public class GlyphView extends View implements TabableView, Cloneable {
  private byte[] selections = null;
  
  int offset = 0;
  
  int length = 0;
  
  boolean impliedCR;
  
  boolean skipWidth;
  
  TabExpander expander;
  
  private float minimumSpan = -1.0F;
  
  private int[] breakSpots = null;
  
  int x;
  
  GlyphPainter painter;
  
  static GlyphPainter defaultPainter;
  
  private JustificationInfo justificationInfo = null;
  
  public GlyphView(Element paramElement) {
    super(paramElement);
    Element element = paramElement.getParentElement();
    AttributeSet attributeSet = paramElement.getAttributes();
    this.impliedCR = (attributeSet != null && attributeSet.getAttribute("CR") != null && element != null && element.getElementCount() > 1);
    this.skipWidth = paramElement.getName().equals("br");
  }
  
  protected final Object clone() {
    Object object;
    try {
      object = super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      object = null;
    } 
    return object;
  }
  
  public GlyphPainter getGlyphPainter() { return this.painter; }
  
  public void setGlyphPainter(GlyphPainter paramGlyphPainter) { this.painter = paramGlyphPainter; }
  
  public Segment getText(int paramInt1, int paramInt2) {
    Segment segment = SegmentCache.getSharedSegment();
    try {
      Document document = getDocument();
      document.getText(paramInt1, paramInt2 - paramInt1, segment);
    } catch (BadLocationException badLocationException) {
      throw new StateInvariantError("GlyphView: Stale view: " + badLocationException);
    } 
    return segment;
  }
  
  public Color getBackground() {
    Document document = getDocument();
    if (document instanceof StyledDocument) {
      AttributeSet attributeSet = getAttributes();
      if (attributeSet.isDefined(StyleConstants.Background))
        return ((StyledDocument)document).getBackground(attributeSet); 
    } 
    return null;
  }
  
  public Color getForeground() {
    Document document = getDocument();
    if (document instanceof StyledDocument) {
      AttributeSet attributeSet = getAttributes();
      return ((StyledDocument)document).getForeground(attributeSet);
    } 
    Container container = getContainer();
    return (container != null) ? container.getForeground() : null;
  }
  
  public Font getFont() {
    Document document = getDocument();
    if (document instanceof StyledDocument) {
      AttributeSet attributeSet = getAttributes();
      return ((StyledDocument)document).getFont(attributeSet);
    } 
    Container container = getContainer();
    return (container != null) ? container.getFont() : null;
  }
  
  public boolean isUnderline() {
    AttributeSet attributeSet = getAttributes();
    return StyleConstants.isUnderline(attributeSet);
  }
  
  public boolean isStrikeThrough() {
    AttributeSet attributeSet = getAttributes();
    return StyleConstants.isStrikeThrough(attributeSet);
  }
  
  public boolean isSubscript() {
    AttributeSet attributeSet = getAttributes();
    return StyleConstants.isSubscript(attributeSet);
  }
  
  public boolean isSuperscript() {
    AttributeSet attributeSet = getAttributes();
    return StyleConstants.isSuperscript(attributeSet);
  }
  
  public TabExpander getTabExpander() { return this.expander; }
  
  protected void checkPainter() {
    if (this.painter == null) {
      if (defaultPainter == null) {
        String str = "javax.swing.text.GlyphPainter1";
        try {
          Class clazz;
          ClassLoader classLoader = getClass().getClassLoader();
          if (classLoader != null) {
            clazz = classLoader.loadClass(str);
          } else {
            clazz = Class.forName(str);
          } 
          Object object = clazz.newInstance();
          if (object instanceof GlyphPainter)
            defaultPainter = (GlyphPainter)object; 
        } catch (Throwable throwable) {
          throw new StateInvariantError("GlyphView: Can't load glyph painter: " + str);
        } 
      } 
      setGlyphPainter(defaultPainter.getPainter(this, getStartOffset(), getEndOffset()));
    } 
  }
  
  public float getTabbedSpan(float paramFloat, TabExpander paramTabExpander) {
    checkPainter();
    TabExpander tabExpander = this.expander;
    this.expander = paramTabExpander;
    if (this.expander != tabExpander)
      preferenceChanged(null, true, false); 
    this.x = (int)paramFloat;
    int i = getStartOffset();
    int j = getEndOffset();
    return this.painter.getSpan(this, i, j, this.expander, paramFloat);
  }
  
  public float getPartialSpan(int paramInt1, int paramInt2) {
    checkPainter();
    return this.painter.getSpan(this, paramInt1, paramInt2, this.expander, this.x);
  }
  
  public int getStartOffset() {
    Element element = getElement();
    return (this.length > 0) ? (element.getStartOffset() + this.offset) : element.getStartOffset();
  }
  
  public int getEndOffset() {
    Element element = getElement();
    return (this.length > 0) ? (element.getStartOffset() + this.offset + this.length) : element.getEndOffset();
  }
  
  private void initSelections(int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1 + 1;
    if (this.selections == null || i > this.selections.length) {
      this.selections = new byte[i];
      return;
    } 
    byte b = 0;
    while (b < i)
      this.selections[b++] = 0; 
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    checkPainter();
    boolean bool = false;
    Container container = getContainer();
    int i = getStartOffset();
    int j = getEndOffset();
    Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    Color color1 = getBackground();
    Color color2 = getForeground();
    if (container != null && !container.isEnabled())
      color2 = (container instanceof JTextComponent) ? ((JTextComponent)container).getDisabledTextColor() : UIManager.getColor("textInactiveText"); 
    if (color1 != null) {
      paramGraphics.setColor(color1);
      paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
    if (container instanceof JTextComponent) {
      JTextComponent jTextComponent = (JTextComponent)container;
      Highlighter highlighter = jTextComponent.getHighlighter();
      if (highlighter instanceof LayeredHighlighter)
        ((LayeredHighlighter)highlighter).paintLayeredHighlights(paramGraphics, i, j, paramShape, jTextComponent, this); 
    } 
    if (Utilities.isComposedTextElement(getElement())) {
      Utilities.paintComposedText(paramGraphics, paramShape.getBounds(), this);
      bool = true;
    } else if (container instanceof JTextComponent) {
      JTextComponent jTextComponent = (JTextComponent)container;
      Color color = jTextComponent.getSelectedTextColor();
      if (jTextComponent.getHighlighter() != null && color != null && !color.equals(color2)) {
        Highlighter.Highlight[] arrayOfHighlight = jTextComponent.getHighlighter().getHighlights();
        if (arrayOfHighlight.length != 0) {
          boolean bool1 = false;
          byte b = 0;
          int k;
          for (k = 0; k < arrayOfHighlight.length; k++) {
            Highlighter.Highlight highlight = arrayOfHighlight[k];
            int m = highlight.getStartOffset();
            int n = highlight.getEndOffset();
            if (m <= j && n >= i && SwingUtilities2.useSelectedTextColor(highlight, jTextComponent)) {
              if (m <= i && n >= j) {
                paintTextUsingColor(paramGraphics, paramShape, color, i, j);
                bool = true;
                break;
              } 
              if (!bool1) {
                initSelections(i, j);
                bool1 = true;
              } 
              m = Math.max(i, m);
              n = Math.min(j, n);
              paintTextUsingColor(paramGraphics, paramShape, color, m, n);
              this.selections[m - i] = (byte)(this.selections[m - i] + 1);
              this.selections[n - i] = (byte)(this.selections[n - i] - 1);
              b++;
            } 
          } 
          if (!bool && b > 0) {
            k = -1;
            int m = 0;
            int n = j - i;
            while (k++ < n) {
              while (k < n && this.selections[k] == 0)
                k++; 
              if (m != k)
                paintTextUsingColor(paramGraphics, paramShape, color2, i + m, i + k); 
              byte b1 = 0;
              while (k < n && b1 += this.selections[k] != 0)
                k++; 
              m = k;
            } 
            bool = true;
          } 
        } 
      } 
    } 
    if (!bool)
      paintTextUsingColor(paramGraphics, paramShape, color2, i, j); 
  }
  
  final void paintTextUsingColor(Graphics paramGraphics, Shape paramShape, Color paramColor, int paramInt1, int paramInt2) {
    paramGraphics.setColor(paramColor);
    this.painter.paint(this, paramGraphics, paramShape, paramInt1, paramInt2);
    boolean bool1 = isUnderline();
    boolean bool2 = isStrikeThrough();
    if (bool1 || bool2) {
      Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      View view = getParent();
      if (view != null && view.getEndOffset() == paramInt2) {
        Segment segment = getText(paramInt1, paramInt2);
        while (Character.isWhitespace(segment.last())) {
          paramInt2--;
          segment.count--;
        } 
        SegmentCache.releaseSharedSegment(segment);
      } 
      int i = rectangle.x;
      int j = getStartOffset();
      if (j != paramInt1)
        i += (int)this.painter.getSpan(this, j, paramInt1, getTabExpander(), i); 
      int k = i + (int)this.painter.getSpan(this, paramInt1, paramInt2, getTabExpander(), i);
      int m = rectangle.y + (int)(this.painter.getHeight(this) - this.painter.getDescent(this));
      if (bool1) {
        int n = m + 1;
        paramGraphics.drawLine(i, n, k, n);
      } 
      if (bool2) {
        int n = m - (int)(this.painter.getAscent(this) * 0.3F);
        paramGraphics.drawLine(i, n, k, n);
      } 
    } 
  }
  
  public float getMinimumSpan(int paramInt) {
    switch (paramInt) {
      case 0:
        if (this.minimumSpan < 0.0F) {
          this.minimumSpan = 0.0F;
          int i = getStartOffset();
          for (int j = getEndOffset(); j > i; j = k - 1) {
            int k = getBreakSpot(i, j);
            if (k == -1)
              k = i; 
            this.minimumSpan = Math.max(this.minimumSpan, getPartialSpan(k, j));
          } 
        } 
        return this.minimumSpan;
      case 1:
        return super.getMinimumSpan(paramInt);
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getPreferredSpan(int paramInt) {
    float f;
    if (this.impliedCR)
      return 0.0F; 
    checkPainter();
    int i = getStartOffset();
    int j = getEndOffset();
    switch (paramInt) {
      case 0:
        return this.skipWidth ? 0.0F : this.painter.getSpan(this, i, j, this.expander, this.x);
      case 1:
        f = this.painter.getHeight(this);
        if (isSuperscript())
          f += f / 3.0F; 
        return f;
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getAlignment(int paramInt) {
    checkPainter();
    if (paramInt == 1) {
      float f4;
      boolean bool1 = isSuperscript();
      boolean bool2 = isSubscript();
      float f1 = this.painter.getHeight(this);
      float f2 = this.painter.getDescent(this);
      float f3 = this.painter.getAscent(this);
      if (bool1) {
        f4 = 1.0F;
      } else if (bool2) {
        f4 = (f1 > 0.0F) ? ((f1 - f2 + f3 / 2.0F) / f1) : 0.0F;
      } else {
        f4 = (f1 > 0.0F) ? ((f1 - f2) / f1) : 0.0F;
      } 
      return f4;
    } 
    return super.getAlignment(paramInt);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException {
    checkPainter();
    return this.painter.modelToView(this, paramInt, paramBias, paramShape);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    checkPainter();
    return this.painter.viewToModel(this, paramFloat1, paramFloat2, paramShape, paramArrayOfBias);
  }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2) {
    if (paramInt == 0) {
      checkPainter();
      int i = getStartOffset();
      int j = this.painter.getBoundedPosition(this, i, paramFloat1, paramFloat2);
      return (j == i) ? 0 : ((getBreakSpot(i, j) != -1) ? 2000 : 1000);
    } 
    return super.getBreakWeight(paramInt, paramFloat1, paramFloat2);
  }
  
  public View breakView(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2) {
    if (paramInt1 == 0) {
      checkPainter();
      int i = this.painter.getBoundedPosition(this, paramInt2, paramFloat1, paramFloat2);
      int j = getBreakSpot(paramInt2, i);
      if (j != -1)
        i = j; 
      if (paramInt2 == getStartOffset() && i == getEndOffset())
        return this; 
      GlyphView glyphView = (GlyphView)createFragment(paramInt2, i);
      glyphView.x = (int)paramFloat1;
      return glyphView;
    } 
    return this;
  }
  
  private int getBreakSpot(int paramInt1, int paramInt2) {
    if (this.breakSpots == null) {
      int j = getStartOffset();
      int k = getEndOffset();
      int[] arrayOfInt = new int[k + 1 - j];
      byte b1 = 0;
      Element element = getElement().getParentElement();
      int m = (element == null) ? j : element.getStartOffset();
      int n = (element == null) ? k : element.getEndOffset();
      Segment segment = getText(m, n);
      segment.first();
      BreakIterator breakIterator = getBreaker();
      breakIterator.setText(segment);
      int i1 = k + ((n > k) ? 1 : 0);
      while (true) {
        i1 = breakIterator.preceding(segment.offset + i1 - m) + m - segment.offset;
        if (i1 > j) {
          arrayOfInt[b1++] = i1;
          continue;
        } 
        break;
      } 
      SegmentCache.releaseSharedSegment(segment);
      this.breakSpots = new int[b1];
      System.arraycopy(arrayOfInt, 0, this.breakSpots, 0, b1);
    } 
    int i = -1;
    for (byte b = 0; b < this.breakSpots.length; b++) {
      int j = this.breakSpots[b];
      if (j <= paramInt2) {
        if (j > paramInt1)
          i = j; 
        break;
      } 
    } 
    return i;
  }
  
  private BreakIterator getBreaker() {
    Document document = getDocument();
    if (document != null && Boolean.TRUE.equals(document.getProperty(AbstractDocument.MultiByteProperty))) {
      Container container = getContainer();
      Locale locale = (container == null) ? Locale.getDefault() : container.getLocale();
      return BreakIterator.getLineInstance(locale);
    } 
    return new WhitespaceBasedBreakIterator();
  }
  
  public View createFragment(int paramInt1, int paramInt2) {
    checkPainter();
    Element element = getElement();
    GlyphView glyphView = (GlyphView)clone();
    glyphView.offset = paramInt1 - element.getStartOffset();
    glyphView.length = paramInt2 - paramInt1;
    glyphView.painter = this.painter.getPainter(glyphView, paramInt1, paramInt2);
    glyphView.justificationInfo = null;
    return glyphView;
  }
  
  public int getNextVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    if (paramInt1 < -1)
      throw new BadLocationException("invalid position", paramInt1); 
    return this.painter.getNextVisualPositionFrom(this, paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    this.justificationInfo = null;
    this.breakSpots = null;
    this.minimumSpan = -1.0F;
    syncCR();
    preferenceChanged(null, true, false);
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    this.justificationInfo = null;
    this.breakSpots = null;
    this.minimumSpan = -1.0F;
    syncCR();
    preferenceChanged(null, true, false);
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    this.minimumSpan = -1.0F;
    syncCR();
    preferenceChanged(null, true, true);
  }
  
  private void syncCR() {
    if (this.impliedCR) {
      Element element = getElement().getParentElement();
      this.impliedCR = (element != null && element.getElementCount() > 1);
    } 
  }
  
  void updateAfterChange() { this.breakSpots = null; }
  
  JustificationInfo getJustificationInfo(int paramInt) {
    if (this.justificationInfo != null)
      return this.justificationInfo; 
    int i = getStartOffset();
    int j = getEndOffset();
    Segment segment = getText(i, j);
    int k = segment.offset;
    int m = segment.offset + segment.count - 1;
    int n = m + 1;
    int i1 = k - 1;
    int i2 = k - 1;
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    boolean bool = false;
    BitSet bitSet = new BitSet(j - i + 1);
    int i3 = m;
    int i4 = 0;
    while (i3 >= k) {
      if (' ' == segment.array[i3]) {
        bitSet.set(i3 - k);
        if (!i4) {
          b1++;
        } else if (i4 == 1) {
          i4 = 2;
          b3 = 1;
        } else if (i4 == 2) {
          b3++;
        } 
      } else {
        if ('\t' == segment.array[i3]) {
          bool = true;
          break;
        } 
        if (i4 == 0) {
          if ('\n' != segment.array[i3] && '\r' != segment.array[i3]) {
            i4 = 1;
            i1 = i3;
          } 
        } else if (i4 != 1 && i4 == 2) {
          b2 += b3;
          b3 = 0;
        } 
        n = i3;
      } 
      i3--;
    } 
    SegmentCache.releaseSharedSegment(segment);
    i3 = -1;
    if (n < m)
      i3 = n - k; 
    i4 = -1;
    if (i1 > k)
      i4 = i1 - k; 
    this.justificationInfo = new JustificationInfo(i3, i4, b3, b2, b1, bool, bitSet);
    return this.justificationInfo;
  }
  
  public static abstract class GlyphPainter {
    public abstract float getSpan(GlyphView param1GlyphView, int param1Int1, int param1Int2, TabExpander param1TabExpander, float param1Float);
    
    public abstract float getHeight(GlyphView param1GlyphView);
    
    public abstract float getAscent(GlyphView param1GlyphView);
    
    public abstract float getDescent(GlyphView param1GlyphView);
    
    public abstract void paint(GlyphView param1GlyphView, Graphics param1Graphics, Shape param1Shape, int param1Int1, int param1Int2);
    
    public abstract Shape modelToView(GlyphView param1GlyphView, int param1Int, Position.Bias param1Bias, Shape param1Shape) throws BadLocationException;
    
    public abstract int viewToModel(GlyphView param1GlyphView, float param1Float1, float param1Float2, Shape param1Shape, Position.Bias[] param1ArrayOfBias);
    
    public abstract int getBoundedPosition(GlyphView param1GlyphView, int param1Int, float param1Float1, float param1Float2);
    
    public GlyphPainter getPainter(GlyphView param1GlyphView, int param1Int1, int param1Int2) { return this; }
    
    public int getNextVisualPositionFrom(GlyphView param1GlyphView, int param1Int1, Position.Bias param1Bias, Shape param1Shape, int param1Int2, Position.Bias[] param1ArrayOfBias) throws BadLocationException {
      Container container;
      int i = param1GlyphView.getStartOffset();
      int j = param1GlyphView.getEndOffset();
      switch (param1Int2) {
        case 1:
        case 5:
          if (param1Int1 != -1)
            return -1; 
          container = param1GlyphView.getContainer();
          if (container instanceof JTextComponent) {
            Caret caret = ((JTextComponent)container).getCaret();
            Point point = (caret != null) ? caret.getMagicCaretPosition() : null;
            if (point == null) {
              param1ArrayOfBias[0] = Position.Bias.Forward;
              return i;
            } 
            return param1GlyphView.viewToModel(point.x, 0.0F, param1Shape, param1ArrayOfBias);
          } 
          return param1Int1;
        case 3:
          if (i == param1GlyphView.getDocument().getLength()) {
            if (param1Int1 == -1) {
              param1ArrayOfBias[0] = Position.Bias.Forward;
              return i;
            } 
            return -1;
          } 
          if (param1Int1 == -1) {
            param1ArrayOfBias[0] = Position.Bias.Forward;
            return i;
          } 
          if (param1Int1 == j)
            return -1; 
          if (++param1Int1 == j)
            return -1; 
          param1ArrayOfBias[0] = Position.Bias.Forward;
          return param1Int1;
        case 7:
          if (i == param1GlyphView.getDocument().getLength()) {
            if (param1Int1 == -1) {
              param1ArrayOfBias[0] = Position.Bias.Forward;
              return i;
            } 
            return -1;
          } 
          if (param1Int1 == -1) {
            param1ArrayOfBias[0] = Position.Bias.Forward;
            return j - 1;
          } 
          if (param1Int1 == i)
            return -1; 
          param1ArrayOfBias[0] = Position.Bias.Forward;
          return param1Int1 - 1;
      } 
      throw new IllegalArgumentException("Bad direction: " + param1Int2);
    }
  }
  
  static class JustificationInfo {
    final int start;
    
    final int end;
    
    final int leadingSpaces;
    
    final int contentSpaces;
    
    final int trailingSpaces;
    
    final boolean hasTab;
    
    final BitSet spaceMap;
    
    JustificationInfo(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, boolean param1Boolean, BitSet param1BitSet) {
      this.start = param1Int1;
      this.end = param1Int2;
      this.leadingSpaces = param1Int3;
      this.contentSpaces = param1Int4;
      this.trailingSpaces = param1Int5;
      this.hasTab = param1Boolean;
      this.spaceMap = param1BitSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\GlyphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */