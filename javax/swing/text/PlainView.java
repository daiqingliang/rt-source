package javax.swing.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;

public class PlainView extends View implements TabExpander {
  protected FontMetrics metrics;
  
  Element longLine;
  
  Font font;
  
  Segment lineBuffer;
  
  int tabSize;
  
  int tabBase;
  
  int sel0;
  
  int sel1;
  
  Color unselected;
  
  Color selected;
  
  int firstLineOffset;
  
  public PlainView(Element paramElement) { super(paramElement); }
  
  protected int getTabSize() {
    Integer integer = (Integer)getDocument().getProperty("tabSize");
    return (integer != null) ? integer.intValue() : 8;
  }
  
  protected void drawLine(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3) {
    Element element = getElement().getElement(paramInt1);
    try {
      if (element.isLeaf()) {
        drawElement(paramInt1, element, paramGraphics, paramInt2, paramInt3);
      } else {
        int i = element.getElementCount();
        for (byte b = 0; b < i; b++) {
          Element element1 = element.getElement(b);
          paramInt2 = drawElement(paramInt1, element1, paramGraphics, paramInt2, paramInt3);
        } 
      } 
    } catch (BadLocationException badLocationException) {
      throw new StateInvariantError("Can't render line: " + paramInt1);
    } 
  }
  
  private int drawElement(int paramInt1, Element paramElement, Graphics paramGraphics, int paramInt2, int paramInt3) throws BadLocationException {
    int i = paramElement.getStartOffset();
    int j = paramElement.getEndOffset();
    j = Math.min(getDocument().getLength(), j);
    if (paramInt1 == 0)
      paramInt2 += this.firstLineOffset; 
    AttributeSet attributeSet = paramElement.getAttributes();
    if (Utilities.isComposedTextAttributeDefined(attributeSet)) {
      paramGraphics.setColor(this.unselected);
      paramInt2 = Utilities.drawComposedText(this, attributeSet, paramGraphics, paramInt2, paramInt3, i - paramElement.getStartOffset(), j - paramElement.getStartOffset());
    } else if (this.sel0 == this.sel1 || this.selected == this.unselected) {
      paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, i, j);
    } else if (i >= this.sel0 && i <= this.sel1 && j >= this.sel0 && j <= this.sel1) {
      paramInt2 = drawSelectedText(paramGraphics, paramInt2, paramInt3, i, j);
    } else if (this.sel0 >= i && this.sel0 <= j) {
      if (this.sel1 >= i && this.sel1 <= j) {
        paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, i, this.sel0);
        paramInt2 = drawSelectedText(paramGraphics, paramInt2, paramInt3, this.sel0, this.sel1);
        paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, this.sel1, j);
      } else {
        paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, i, this.sel0);
        paramInt2 = drawSelectedText(paramGraphics, paramInt2, paramInt3, this.sel0, j);
      } 
    } else if (this.sel1 >= i && this.sel1 <= j) {
      paramInt2 = drawSelectedText(paramGraphics, paramInt2, paramInt3, i, this.sel1);
      paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, this.sel1, j);
    } else {
      paramInt2 = drawUnselectedText(paramGraphics, paramInt2, paramInt3, i, j);
    } 
    return paramInt2;
  }
  
  protected int drawUnselectedText(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws BadLocationException {
    paramGraphics.setColor(this.unselected);
    Document document = getDocument();
    Segment segment = SegmentCache.getSharedSegment();
    document.getText(paramInt3, paramInt4 - paramInt3, segment);
    int i = Utilities.drawTabbedText(this, segment, paramInt1, paramInt2, paramGraphics, this, paramInt3);
    SegmentCache.releaseSharedSegment(segment);
    return i;
  }
  
  protected int drawSelectedText(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws BadLocationException {
    paramGraphics.setColor(this.selected);
    Document document = getDocument();
    Segment segment = SegmentCache.getSharedSegment();
    document.getText(paramInt3, paramInt4 - paramInt3, segment);
    int i = Utilities.drawTabbedText(this, segment, paramInt1, paramInt2, paramGraphics, this, paramInt3);
    SegmentCache.releaseSharedSegment(segment);
    return i;
  }
  
  protected final Segment getLineBuffer() {
    if (this.lineBuffer == null)
      this.lineBuffer = new Segment(); 
    return this.lineBuffer;
  }
  
  protected void updateMetrics() {
    Container container = getContainer();
    Font font1 = container.getFont();
    if (this.font != font1) {
      calculateLongestLine();
      this.tabSize = getTabSize() * this.metrics.charWidth('m');
    } 
  }
  
  public float getPreferredSpan(int paramInt) {
    updateMetrics();
    switch (paramInt) {
      case 0:
        return getLineWidth(this.longLine);
      case 1:
        return (getElement().getElementCount() * this.metrics.getHeight());
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    byte b3;
    byte b2;
    byte b1;
    Shape shape = paramShape;
    paramShape = adjustPaintRegion(paramShape);
    Rectangle rectangle1 = (Rectangle)paramShape;
    this.tabBase = rectangle1.x;
    JTextComponent jTextComponent = (JTextComponent)getContainer();
    Highlighter highlighter = jTextComponent.getHighlighter();
    paramGraphics.setFont(jTextComponent.getFont());
    this.sel0 = jTextComponent.getSelectionStart();
    this.sel1 = jTextComponent.getSelectionEnd();
    this.unselected = jTextComponent.isEnabled() ? jTextComponent.getForeground() : jTextComponent.getDisabledTextColor();
    Caret caret = jTextComponent.getCaret();
    this.selected = (caret.isSelectionVisible() && highlighter != null) ? jTextComponent.getSelectedTextColor() : this.unselected;
    updateMetrics();
    Rectangle rectangle2 = paramGraphics.getClipBounds();
    int i = this.metrics.getHeight();
    int j = rectangle1.y + rectangle1.height - rectangle2.y + rectangle2.height;
    int k = rectangle2.y - rectangle1.y;
    if (i > 0) {
      b1 = Math.max(0, j / i);
      b2 = Math.max(0, k / i);
      b3 = rectangle1.height / i;
      if (rectangle1.height % i != 0)
        b3++; 
    } else {
      b1 = b2 = b3 = 0;
    } 
    Rectangle rectangle3 = lineToRect(paramShape, b2);
    int m = rectangle3.y + this.metrics.getAscent();
    int n = rectangle3.x;
    Element element = getElement();
    int i1 = element.getElementCount();
    int i2 = Math.min(i1, b3 - b1);
    i1--;
    LayeredHighlighter layeredHighlighter = (highlighter instanceof LayeredHighlighter) ? (LayeredHighlighter)highlighter : null;
    for (byte b4 = b2; b4 < i2; b4++) {
      if (layeredHighlighter != null) {
        Element element1 = element.getElement(b4);
        if (b4 == i1) {
          layeredHighlighter.paintLayeredHighlights(paramGraphics, element1.getStartOffset(), element1.getEndOffset(), shape, jTextComponent, this);
        } else {
          layeredHighlighter.paintLayeredHighlights(paramGraphics, element1.getStartOffset(), element1.getEndOffset() - 1, shape, jTextComponent, this);
        } 
      } 
      drawLine(b4, paramGraphics, n, m);
      m += i;
      if (b4 == 0)
        n -= this.firstLineOffset; 
    } 
  }
  
  Shape adjustPaintRegion(Shape paramShape) { return paramShape; }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException {
    Document document = getDocument();
    Element element1 = getElement();
    int i = element1.getElementIndex(paramInt);
    if (i < 0)
      return lineToRect(paramShape, 0); 
    Rectangle rectangle = lineToRect(paramShape, i);
    this.tabBase = rectangle.x;
    Element element2 = element1.getElement(i);
    int j = element2.getStartOffset();
    Segment segment = SegmentCache.getSharedSegment();
    document.getText(j, paramInt - j, segment);
    int k = Utilities.getTabbedTextWidth(segment, this.metrics, this.tabBase, this, j);
    SegmentCache.releaseSharedSegment(segment);
    rectangle.x += k;
    rectangle.width = 1;
    rectangle.height = this.metrics.getHeight();
    return rectangle;
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    paramArrayOfBias[0] = Position.Bias.Forward;
    Rectangle rectangle = paramShape.getBounds();
    Document document = getDocument();
    int i = (int)paramFloat1;
    int j = (int)paramFloat2;
    if (j < rectangle.y)
      return getStartOffset(); 
    if (j > rectangle.y + rectangle.height)
      return getEndOffset() - 1; 
    Element element1 = document.getDefaultRootElement();
    int k = this.metrics.getHeight();
    int m = (k > 0) ? Math.abs((j - rectangle.y) / k) : (element1.getElementCount() - 1);
    if (m >= element1.getElementCount())
      return getEndOffset() - 1; 
    Element element2 = element1.getElement(m);
    boolean bool = false;
    if (m == 0) {
      rectangle.x += this.firstLineOffset;
      rectangle.width -= this.firstLineOffset;
    } 
    if (i < rectangle.x)
      return element2.getStartOffset(); 
    if (i > rectangle.x + rectangle.width)
      return element2.getEndOffset() - 1; 
    try {
      int n = element2.getStartOffset();
      int i1 = element2.getEndOffset() - 1;
      Segment segment = SegmentCache.getSharedSegment();
      document.getText(n, i1 - n, segment);
      this.tabBase = rectangle.x;
      int i2 = n + Utilities.getTabbedTextOffset(segment, this.metrics, this.tabBase, i, this, n);
      SegmentCache.releaseSharedSegment(segment);
      return i2;
    } catch (BadLocationException badLocationException) {
      return -1;
    } 
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) { updateDamage(paramDocumentEvent, paramShape, paramViewFactory); }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) { updateDamage(paramDocumentEvent, paramShape, paramViewFactory); }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) { updateDamage(paramDocumentEvent, paramShape, paramViewFactory); }
  
  public void setSize(float paramFloat1, float paramFloat2) {
    super.setSize(paramFloat1, paramFloat2);
    updateMetrics();
  }
  
  public float nextTabStop(float paramFloat, int paramInt) {
    if (this.tabSize == 0)
      return paramFloat; 
    int i = ((int)paramFloat - this.tabBase) / this.tabSize;
    return (this.tabBase + (i + 1) * this.tabSize);
  }
  
  protected void updateDamage(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    Container container = getContainer();
    updateMetrics();
    Element element = getElement();
    DocumentEvent.ElementChange elementChange = paramDocumentEvent.getChange(element);
    Element[] arrayOfElement1 = (elementChange != null) ? elementChange.getChildrenAdded() : null;
    Element[] arrayOfElement2 = (elementChange != null) ? elementChange.getChildrenRemoved() : null;
    if ((arrayOfElement1 != null && arrayOfElement1.length > 0) || (arrayOfElement2 != null && arrayOfElement2.length > 0)) {
      if (arrayOfElement1 != null) {
        int i = getLineWidth(this.longLine);
        for (byte b = 0; b < arrayOfElement1.length; b++) {
          int j = getLineWidth(arrayOfElement1[b]);
          if (j > i) {
            i = j;
            this.longLine = arrayOfElement1[b];
          } 
        } 
      } 
      if (arrayOfElement2 != null)
        for (byte b = 0; b < arrayOfElement2.length; b++) {
          if (arrayOfElement2[b] == this.longLine) {
            calculateLongestLine();
            break;
          } 
        }  
      preferenceChanged(null, true, true);
      container.repaint();
    } else {
      Element element1 = getElement();
      int i = element1.getElementIndex(paramDocumentEvent.getOffset());
      damageLineRange(i, i, paramShape, container);
      if (paramDocumentEvent.getType() == DocumentEvent.EventType.INSERT) {
        int j = getLineWidth(this.longLine);
        Element element2 = element1.getElement(i);
        if (element2 == this.longLine) {
          preferenceChanged(null, true, false);
        } else if (getLineWidth(element2) > j) {
          this.longLine = element2;
          preferenceChanged(null, true, false);
        } 
      } else if (paramDocumentEvent.getType() == DocumentEvent.EventType.REMOVE && element1.getElement(i) == this.longLine) {
        calculateLongestLine();
        preferenceChanged(null, true, false);
      } 
    } 
  }
  
  protected void damageLineRange(int paramInt1, int paramInt2, Shape paramShape, Component paramComponent) {
    if (paramShape != null) {
      Rectangle rectangle1 = lineToRect(paramShape, paramInt1);
      Rectangle rectangle2 = lineToRect(paramShape, paramInt2);
      if (rectangle1 != null && rectangle2 != null) {
        Rectangle rectangle = rectangle1.union(rectangle2);
        paramComponent.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } else {
        paramComponent.repaint();
      } 
    } 
  }
  
  protected Rectangle lineToRect(Shape paramShape, int paramInt) {
    Rectangle rectangle = null;
    updateMetrics();
    if (this.metrics != null) {
      Rectangle rectangle1 = paramShape.getBounds();
      if (paramInt == 0) {
        rectangle1.x += this.firstLineOffset;
        rectangle1.width -= this.firstLineOffset;
      } 
      rectangle = new Rectangle(rectangle1.x, rectangle1.y + paramInt * this.metrics.getHeight(), rectangle1.width, this.metrics.getHeight());
    } 
    return rectangle;
  }
  
  private void calculateLongestLine() {
    Container container = getContainer();
    this.font = container.getFont();
    this.metrics = container.getFontMetrics(this.font);
    Document document = getDocument();
    Element element = getElement();
    int i = element.getElementCount();
    int j = -1;
    for (byte b = 0; b < i; b++) {
      Element element1 = element.getElement(b);
      int k = getLineWidth(element1);
      if (k > j) {
        j = k;
        this.longLine = element1;
      } 
    } 
  }
  
  private int getLineWidth(Element paramElement) {
    byte b;
    if (paramElement == null)
      return 0; 
    int i = paramElement.getStartOffset();
    int j = paramElement.getEndOffset();
    Segment segment = SegmentCache.getSharedSegment();
    try {
      paramElement.getDocument().getText(i, j - i, segment);
      b = Utilities.getTabbedTextWidth(segment, this.metrics, this.tabBase, this, i);
    } catch (BadLocationException badLocationException) {
      b = 0;
    } 
    SegmentCache.releaseSharedSegment(segment);
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\PlainView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */