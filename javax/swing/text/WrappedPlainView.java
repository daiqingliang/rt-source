package javax.swing.text;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.lang.ref.SoftReference;
import javax.swing.event.DocumentEvent;

public class WrappedPlainView extends BoxView implements TabExpander {
  FontMetrics metrics;
  
  Segment lineBuffer;
  
  boolean widthChanging;
  
  int tabBase;
  
  int tabSize;
  
  boolean wordWrap;
  
  int sel0;
  
  int sel1;
  
  Color unselected;
  
  Color selected;
  
  public WrappedPlainView(Element paramElement) { this(paramElement, false); }
  
  public WrappedPlainView(Element paramElement, boolean paramBoolean) {
    super(paramElement, 1);
    this.wordWrap = paramBoolean;
  }
  
  protected int getTabSize() {
    Integer integer = (Integer)getDocument().getProperty("tabSize");
    return (integer != null) ? integer.intValue() : 8;
  }
  
  protected void drawLine(int paramInt1, int paramInt2, Graphics paramGraphics, int paramInt3, int paramInt4) {
    Element element1 = getElement();
    Element element2 = element1.getElement(element1.getElementIndex(paramInt1));
    try {
      if (element2.isLeaf()) {
        drawText(element2, paramInt1, paramInt2, paramGraphics, paramInt3, paramInt4);
      } else {
        int i = element2.getElementIndex(paramInt1);
        int j = element2.getElementIndex(paramInt2);
        while (i <= j) {
          Element element = element2.getElement(i);
          int k = Math.max(element.getStartOffset(), paramInt1);
          int m = Math.min(element.getEndOffset(), paramInt2);
          paramInt3 = drawText(element, k, m, paramGraphics, paramInt3, paramInt4);
          i++;
        } 
      } 
    } catch (BadLocationException badLocationException) {
      throw new StateInvariantError("Can't render: " + paramInt1 + "," + paramInt2);
    } 
  }
  
  private int drawText(Element paramElement, int paramInt1, int paramInt2, Graphics paramGraphics, int paramInt3, int paramInt4) throws BadLocationException {
    paramInt2 = Math.min(getDocument().getLength(), paramInt2);
    AttributeSet attributeSet = paramElement.getAttributes();
    if (Utilities.isComposedTextAttributeDefined(attributeSet)) {
      paramGraphics.setColor(this.unselected);
      paramInt3 = Utilities.drawComposedText(this, attributeSet, paramGraphics, paramInt3, paramInt4, paramInt1 - paramElement.getStartOffset(), paramInt2 - paramElement.getStartOffset());
    } else if (this.sel0 == this.sel1 || this.selected == this.unselected) {
      paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, paramInt1, paramInt2);
    } else if (paramInt1 >= this.sel0 && paramInt1 <= this.sel1 && paramInt2 >= this.sel0 && paramInt2 <= this.sel1) {
      paramInt3 = drawSelectedText(paramGraphics, paramInt3, paramInt4, paramInt1, paramInt2);
    } else if (this.sel0 >= paramInt1 && this.sel0 <= paramInt2) {
      if (this.sel1 >= paramInt1 && this.sel1 <= paramInt2) {
        paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, paramInt1, this.sel0);
        paramInt3 = drawSelectedText(paramGraphics, paramInt3, paramInt4, this.sel0, this.sel1);
        paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, this.sel1, paramInt2);
      } else {
        paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, paramInt1, this.sel0);
        paramInt3 = drawSelectedText(paramGraphics, paramInt3, paramInt4, this.sel0, paramInt2);
      } 
    } else if (this.sel1 >= paramInt1 && this.sel1 <= paramInt2) {
      paramInt3 = drawSelectedText(paramGraphics, paramInt3, paramInt4, paramInt1, this.sel1);
      paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, this.sel1, paramInt2);
    } else {
      paramInt3 = drawUnselectedText(paramGraphics, paramInt3, paramInt4, paramInt1, paramInt2);
    } 
    return paramInt3;
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
  
  protected int calculateBreakPosition(int paramInt1, int paramInt2) {
    int i;
    Segment segment = SegmentCache.getSharedSegment();
    loadText(segment, paramInt1, paramInt2);
    int j = getWidth();
    if (this.wordWrap) {
      i = paramInt1 + Utilities.getBreakLocation(segment, this.metrics, this.tabBase, this.tabBase + j, this, paramInt1);
    } else {
      i = paramInt1 + Utilities.getTabbedTextOffset(segment, this.metrics, this.tabBase, this.tabBase + j, this, paramInt1, false);
    } 
    SegmentCache.releaseSharedSegment(segment);
    return i;
  }
  
  protected void loadChildren(ViewFactory paramViewFactory) {
    Element element = getElement();
    int i = element.getElementCount();
    if (i > 0) {
      View[] arrayOfView = new View[i];
      for (byte b = 0; b < i; b++)
        arrayOfView[b] = new WrappedLine(element.getElement(b)); 
      replace(0, 0, arrayOfView);
    } 
  }
  
  void updateChildren(DocumentEvent paramDocumentEvent, Shape paramShape) {
    Element element = getElement();
    DocumentEvent.ElementChange elementChange = paramDocumentEvent.getChange(element);
    if (elementChange != null) {
      Element[] arrayOfElement1 = elementChange.getChildrenRemoved();
      Element[] arrayOfElement2 = elementChange.getChildrenAdded();
      View[] arrayOfView = new View[arrayOfElement2.length];
      for (byte b = 0; b < arrayOfElement2.length; b++)
        arrayOfView[b] = new WrappedLine(arrayOfElement2[b]); 
      replace(elementChange.getIndex(), arrayOfElement1.length, arrayOfView);
      if (paramShape != null) {
        preferenceChanged(null, true, true);
        getContainer().repaint();
      } 
    } 
    updateMetrics();
  }
  
  final void loadText(Segment paramSegment, int paramInt1, int paramInt2) {
    try {
      Document document = getDocument();
      document.getText(paramInt1, paramInt2 - paramInt1, paramSegment);
    } catch (BadLocationException badLocationException) {
      throw new StateInvariantError("Can't get line text");
    } 
  }
  
  final void updateMetrics() {
    Container container = getContainer();
    Font font = container.getFont();
    this.metrics = container.getFontMetrics(font);
    this.tabSize = getTabSize() * this.metrics.charWidth('m');
  }
  
  public float nextTabStop(float paramFloat, int paramInt) {
    if (this.tabSize == 0)
      return paramFloat; 
    int i = ((int)paramFloat - this.tabBase) / this.tabSize;
    return (this.tabBase + (i + 1) * this.tabSize);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Rectangle rectangle = (Rectangle)paramShape;
    this.tabBase = rectangle.x;
    JTextComponent jTextComponent = (JTextComponent)getContainer();
    this.sel0 = jTextComponent.getSelectionStart();
    this.sel1 = jTextComponent.getSelectionEnd();
    this.unselected = jTextComponent.isEnabled() ? jTextComponent.getForeground() : jTextComponent.getDisabledTextColor();
    Caret caret = jTextComponent.getCaret();
    this.selected = (caret.isSelectionVisible() && jTextComponent.getHighlighter() != null) ? jTextComponent.getSelectedTextColor() : this.unselected;
    paramGraphics.setFont(jTextComponent.getFont());
    super.paint(paramGraphics, paramShape);
  }
  
  public void setSize(float paramFloat1, float paramFloat2) {
    updateMetrics();
    if ((int)paramFloat1 != getWidth()) {
      preferenceChanged(null, true, true);
      this.widthChanging = true;
    } 
    super.setSize(paramFloat1, paramFloat2);
    this.widthChanging = false;
  }
  
  public float getPreferredSpan(int paramInt) {
    updateMetrics();
    return super.getPreferredSpan(paramInt);
  }
  
  public float getMinimumSpan(int paramInt) {
    updateMetrics();
    return super.getMinimumSpan(paramInt);
  }
  
  public float getMaximumSpan(int paramInt) {
    updateMetrics();
    return super.getMaximumSpan(paramInt);
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    updateChildren(paramDocumentEvent, paramShape);
    Rectangle rectangle = (paramShape != null && isAllocationValid()) ? getInsideAllocation(paramShape) : null;
    int i = paramDocumentEvent.getOffset();
    View view = getViewAtPosition(i, rectangle);
    if (view != null)
      view.insertUpdate(paramDocumentEvent, rectangle, paramViewFactory); 
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    updateChildren(paramDocumentEvent, paramShape);
    Rectangle rectangle = (paramShape != null && isAllocationValid()) ? getInsideAllocation(paramShape) : null;
    int i = paramDocumentEvent.getOffset();
    View view = getViewAtPosition(i, rectangle);
    if (view != null)
      view.removeUpdate(paramDocumentEvent, rectangle, paramViewFactory); 
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) { updateChildren(paramDocumentEvent, paramShape); }
  
  class WrappedLine extends View {
    int lineCount = -1;
    
    SoftReference<int[]> lineCache = null;
    
    WrappedLine(Element param1Element) { super(param1Element); }
    
    public float getPreferredSpan(int param1Int) {
      float f;
      switch (param1Int) {
        case 0:
          f = WrappedPlainView.this.getWidth();
          return (f == 2.14748365E9F) ? 100.0F : f;
        case 1:
          if (this.lineCount < 0 || WrappedPlainView.this.widthChanging)
            breakLines(getStartOffset()); 
          return (this.lineCount * WrappedPlainView.this.metrics.getHeight());
      } 
      throw new IllegalArgumentException("Invalid axis: " + param1Int);
    }
    
    public void paint(Graphics param1Graphics, Shape param1Shape) {
      Rectangle rectangle = (Rectangle)param1Shape;
      int i = rectangle.y + WrappedPlainView.this.metrics.getAscent();
      int j = rectangle.x;
      JTextComponent jTextComponent = (JTextComponent)getContainer();
      Highlighter highlighter = jTextComponent.getHighlighter();
      LayeredHighlighter layeredHighlighter = (highlighter instanceof LayeredHighlighter) ? (LayeredHighlighter)highlighter : null;
      int k = getStartOffset();
      int m = getEndOffset();
      int n = k;
      int[] arrayOfInt = getLineEnds();
      for (byte b = 0; b < this.lineCount; b++) {
        int i1 = (arrayOfInt == null) ? m : (k + arrayOfInt[b]);
        if (layeredHighlighter != null) {
          int i2 = (i1 == m) ? (i1 - 1) : i1;
          layeredHighlighter.paintLayeredHighlights(param1Graphics, n, i2, param1Shape, jTextComponent, this);
        } 
        WrappedPlainView.this.drawLine(n, i1, param1Graphics, j, i);
        n = i1;
        i += WrappedPlainView.this.metrics.getHeight();
      } 
    }
    
    public Shape modelToView(int param1Int, Shape param1Shape, Position.Bias param1Bias) throws BadLocationException {
      Rectangle rectangle = param1Shape.getBounds();
      rectangle.height = WrappedPlainView.this.metrics.getHeight();
      rectangle.width = 1;
      int i = getStartOffset();
      if (param1Int < i || param1Int > getEndOffset())
        throw new BadLocationException("Position out of range", param1Int); 
      int j = (param1Bias == Position.Bias.Forward) ? param1Int : Math.max(i, param1Int - 1);
      int k = 0;
      int[] arrayOfInt = getLineEnds();
      if (arrayOfInt != null) {
        k = findLine(j - i);
        if (k > 0)
          i += arrayOfInt[k - 1]; 
        rectangle.y += rectangle.height * k;
      } 
      if (param1Int > i) {
        Segment segment = SegmentCache.getSharedSegment();
        WrappedPlainView.this.loadText(segment, i, param1Int);
        rectangle.x += Utilities.getTabbedTextWidth(segment, WrappedPlainView.this.metrics, rectangle.x, WrappedPlainView.this, i);
        SegmentCache.releaseSharedSegment(segment);
      } 
      return rectangle;
    }
    
    public int viewToModel(float param1Float1, float param1Float2, Shape param1Shape, Position.Bias[] param1ArrayOfBias) {
      int n;
      param1ArrayOfBias[0] = Position.Bias.Forward;
      Rectangle rectangle = (Rectangle)param1Shape;
      int i = (int)param1Float1;
      int j = (int)param1Float2;
      if (j < rectangle.y)
        return getStartOffset(); 
      if (j > rectangle.y + rectangle.height)
        return getEndOffset() - 1; 
      rectangle.height = WrappedPlainView.this.metrics.getHeight();
      int k = (rectangle.height > 0) ? ((j - rectangle.y) / rectangle.height) : (this.lineCount - 1);
      if (k >= this.lineCount)
        return getEndOffset() - 1; 
      int m = getStartOffset();
      if (this.lineCount == 1) {
        n = getEndOffset();
      } else {
        int[] arrayOfInt = getLineEnds();
        n = m + arrayOfInt[k];
        if (k > 0)
          m += arrayOfInt[k - 1]; 
      } 
      if (i < rectangle.x)
        return m; 
      if (i > rectangle.x + rectangle.width)
        return n - 1; 
      Segment segment = SegmentCache.getSharedSegment();
      WrappedPlainView.this.loadText(segment, m, n);
      int i1 = Utilities.getTabbedTextOffset(segment, WrappedPlainView.this.metrics, rectangle.x, i, WrappedPlainView.this, m);
      SegmentCache.releaseSharedSegment(segment);
      return Math.min(m + i1, n - 1);
    }
    
    public void insertUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) { update(param1DocumentEvent, param1Shape); }
    
    public void removeUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) { update(param1DocumentEvent, param1Shape); }
    
    private void update(DocumentEvent param1DocumentEvent, Shape param1Shape) {
      int i = this.lineCount;
      breakLines(param1DocumentEvent.getOffset());
      if (i != this.lineCount) {
        WrappedPlainView.this.preferenceChanged(this, false, true);
        getContainer().repaint();
      } else if (param1Shape != null) {
        Container container = getContainer();
        Rectangle rectangle = (Rectangle)param1Shape;
        container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
    }
    
    final int[] getLineEnds() {
      if (this.lineCache == null)
        return null; 
      int[] arrayOfInt = (int[])this.lineCache.get();
      return (arrayOfInt == null) ? breakLines(getStartOffset()) : arrayOfInt;
    }
    
    final int[] breakLines(int param1Int) {
      int[] arrayOfInt1 = (this.lineCache == null) ? null : (int[])this.lineCache.get();
      int[] arrayOfInt2 = arrayOfInt1;
      int i = getStartOffset();
      int j = 0;
      if (arrayOfInt1 != null) {
        j = findLine(param1Int - i);
        if (j > 0)
          j--; 
      } 
      int k = (j == 0) ? i : (i + arrayOfInt1[j - 1]);
      int m = getEndOffset();
      while (k < m) {
        int n = WrappedPlainView.this.calculateBreakPosition(k, m);
        k = (n == k) ? ++n : n;
        if (j == 0 && k >= m) {
          this.lineCache = null;
          arrayOfInt1 = null;
          j = 1;
          break;
        } 
        if (arrayOfInt1 == null || j >= arrayOfInt1.length) {
          double d = (m - i) / (k - i);
          int i1 = (int)Math.ceil((j + 1) * d);
          i1 = Math.max(i1, j + 2);
          int[] arrayOfInt = new int[i1];
          if (arrayOfInt1 != null)
            System.arraycopy(arrayOfInt1, 0, arrayOfInt, 0, j); 
          arrayOfInt1 = arrayOfInt;
        } 
        arrayOfInt1[j++] = k - i;
      } 
      this.lineCount = j;
      if (this.lineCount > 1) {
        int n = this.lineCount + this.lineCount / 3;
        if (arrayOfInt1.length > n) {
          int[] arrayOfInt = new int[n];
          System.arraycopy(arrayOfInt1, 0, arrayOfInt, 0, this.lineCount);
          arrayOfInt1 = arrayOfInt;
        } 
      } 
      if (arrayOfInt1 != null && arrayOfInt1 != arrayOfInt2)
        this.lineCache = new SoftReference(arrayOfInt1); 
      return arrayOfInt1;
    }
    
    private int findLine(int param1Int) {
      int[] arrayOfInt = (int[])this.lineCache.get();
      return (param1Int < arrayOfInt[0]) ? 0 : ((param1Int > arrayOfInt[this.lineCount - 1]) ? this.lineCount : findLine(arrayOfInt, param1Int, 0, this.lineCount - 1));
    }
    
    private int findLine(int[] param1ArrayOfInt, int param1Int1, int param1Int2, int param1Int3) {
      if (param1Int3 - param1Int2 <= 1)
        return param1Int3; 
      int i = (param1Int3 + param1Int2) / 2;
      return (param1Int1 < param1ArrayOfInt[i]) ? findLine(param1ArrayOfInt, param1Int1, param1Int2, i) : findLine(param1ArrayOfInt, param1Int1, i, param1Int3);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\WrappedPlainView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */