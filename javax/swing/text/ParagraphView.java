package javax.swing.text;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;

public class ParagraphView extends FlowView implements TabExpander {
  private int justification;
  
  private float lineSpacing;
  
  protected int firstLineIndent = 0;
  
  private int tabBase;
  
  static Class i18nStrategy;
  
  static char[] tabChars = new char[1];
  
  static char[] tabDecimalChars;
  
  public ParagraphView(Element paramElement) {
    super(paramElement, 1);
    setPropertiesFromAttributes();
    Document document = paramElement.getDocument();
    Object object = document.getProperty("i18n");
    if (object != null && object.equals(Boolean.TRUE))
      try {
        if (i18nStrategy == null) {
          String str = "javax.swing.text.TextLayoutStrategy";
          ClassLoader classLoader = getClass().getClassLoader();
          if (classLoader != null) {
            i18nStrategy = classLoader.loadClass(str);
          } else {
            i18nStrategy = Class.forName(str);
          } 
        } 
        Object object1 = i18nStrategy.newInstance();
        if (object1 instanceof FlowView.FlowStrategy)
          this.strategy = (FlowView.FlowStrategy)object1; 
      } catch (Throwable throwable) {
        throw new StateInvariantError("ParagraphView: Can't create i18n strategy: " + throwable.getMessage());
      }  
  }
  
  protected void setJustification(int paramInt) { this.justification = paramInt; }
  
  protected void setLineSpacing(float paramFloat) { this.lineSpacing = paramFloat; }
  
  protected void setFirstLineIndent(float paramFloat) { this.firstLineIndent = (int)paramFloat; }
  
  protected void setPropertiesFromAttributes() {
    AttributeSet attributeSet = getAttributes();
    if (attributeSet != null) {
      int i;
      setParagraphInsets(attributeSet);
      Integer integer = (Integer)attributeSet.getAttribute(StyleConstants.Alignment);
      if (integer == null) {
        Document document = getElement().getDocument();
        Object object = document.getProperty(TextAttribute.RUN_DIRECTION);
        if (object != null && object.equals(TextAttribute.RUN_DIRECTION_RTL)) {
          i = 2;
        } else {
          i = 0;
        } 
      } else {
        i = integer.intValue();
      } 
      setJustification(i);
      setLineSpacing(StyleConstants.getLineSpacing(attributeSet));
      setFirstLineIndent(StyleConstants.getFirstLineIndent(attributeSet));
    } 
  }
  
  protected int getLayoutViewCount() { return this.layoutPool.getViewCount(); }
  
  protected View getLayoutView(int paramInt) { return this.layoutPool.getView(paramInt); }
  
  protected int getNextNorthSouthVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    int j;
    int i;
    if (paramInt1 == -1) {
      i = (paramInt2 == 1) ? (getViewCount() - 1) : 0;
    } else {
      if (paramBias == Position.Bias.Backward && paramInt1 > 0) {
        i = getViewIndexAtPosition(paramInt1 - 1);
      } else {
        i = getViewIndexAtPosition(paramInt1);
      } 
      if (paramInt2 == 1) {
        if (i == 0)
          return -1; 
        i--;
      } else if (++i >= getViewCount()) {
        return -1;
      } 
    } 
    JTextComponent jTextComponent = (JTextComponent)getContainer();
    Caret caret = jTextComponent.getCaret();
    Point point = (caret != null) ? caret.getMagicCaretPosition() : null;
    if (point == null) {
      Object object;
      try {
        object = jTextComponent.getUI().modelToView(jTextComponent, paramInt1, paramBias);
      } catch (BadLocationException badLocationException) {
        object = null;
      } 
      if (object == null) {
        j = 0;
      } else {
        j = (object.getBounds()).x;
      } 
    } else {
      j = point.x;
    } 
    return getClosestPositionTo(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias, i, j);
  }
  
  protected int getClosestPositionTo(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias, int paramInt3, int paramInt4) throws BadLocationException {
    JTextComponent jTextComponent = (JTextComponent)getContainer();
    Document document = getDocument();
    View view = getView(paramInt3);
    int i = -1;
    paramArrayOfBias[0] = Position.Bias.Forward;
    byte b = 0;
    int j = view.getViewCount();
    while (b < j) {
      View view1 = view.getView(b);
      int k = view1.getStartOffset();
      boolean bool = AbstractDocument.isLeftToRight(document, k, k + 1);
      if (bool) {
        i = k;
        int m = view1.getEndOffset();
        while (i < m) {
          float f = (jTextComponent.modelToView(i).getBounds()).x;
          if (f >= paramInt4) {
            while (++i < m && (jTextComponent.modelToView(i).getBounds()).x == f);
            return --i;
          } 
          i++;
        } 
        i--;
      } else {
        for (i = view1.getEndOffset() - 1; i >= k; i--) {
          float f = (jTextComponent.modelToView(i).getBounds()).x;
          if (f >= paramInt4) {
            while (--i >= k && (jTextComponent.modelToView(i).getBounds()).x == f);
            return ++i;
          } 
        } 
        i++;
      } 
      b++;
    } 
    return (i == -1) ? getStartOffset() : i;
  }
  
  protected boolean flipEastAndWestAtEnds(int paramInt, Position.Bias paramBias) {
    Document document = getDocument();
    paramInt = getStartOffset();
    return !AbstractDocument.isLeftToRight(document, paramInt, paramInt + 1);
  }
  
  public int getFlowSpan(int paramInt) {
    View view = getView(paramInt);
    int i = 0;
    if (view instanceof Row) {
      Row row = (Row)view;
      i = row.getLeftInset() + row.getRightInset();
    } 
    return (this.layoutSpan == Integer.MAX_VALUE) ? this.layoutSpan : (this.layoutSpan - i);
  }
  
  public int getFlowStart(int paramInt) {
    View view = getView(paramInt);
    int i = 0;
    if (view instanceof Row) {
      Row row = (Row)view;
      i = row.getLeftInset();
    } 
    return this.tabBase + i;
  }
  
  protected View createRow() { return new Row(getElement()); }
  
  public float nextTabStop(float paramFloat, int paramInt) {
    int j;
    if (this.justification != 0)
      return paramFloat + 10.0F; 
    paramFloat -= this.tabBase;
    TabSet tabSet = getTabSet();
    if (tabSet == null)
      return (this.tabBase + ((int)paramFloat / 72 + 1) * 72); 
    TabStop tabStop = tabSet.getTabAfter(paramFloat + 0.01F);
    if (tabStop == null)
      return this.tabBase + paramFloat + 5.0F; 
    int i = tabStop.getAlignment();
    switch (i) {
      default:
        return this.tabBase + tabStop.getPosition();
      case 5:
        return this.tabBase + tabStop.getPosition();
      case 1:
      case 2:
        j = findOffsetToCharactersInString(tabChars, paramInt + 1);
        break;
      case 4:
        j = findOffsetToCharactersInString(tabDecimalChars, paramInt + 1);
        break;
    } 
    if (j == -1)
      j = getEndOffset(); 
    float f = getPartialSize(paramInt + 1, j);
    switch (i) {
      case 1:
      case 4:
        return this.tabBase + Math.max(paramFloat, tabStop.getPosition() - f);
      case 2:
        return this.tabBase + Math.max(paramFloat, tabStop.getPosition() - f / 2.0F);
    } 
    return paramFloat;
  }
  
  protected TabSet getTabSet() { return StyleConstants.getTabSet(getElement().getAttributes()); }
  
  protected float getPartialSize(int paramInt1, int paramInt2) {
    float f = 0.0F;
    int j = getViewCount();
    int i = getElement().getElementIndex(paramInt1);
    j = this.layoutPool.getViewCount();
    while (paramInt1 < paramInt2 && i < j) {
      View view = this.layoutPool.getView(i++);
      int k = view.getEndOffset();
      int m = Math.min(paramInt2, k);
      if (view instanceof TabableView) {
        f += ((TabableView)view).getPartialSpan(paramInt1, m);
      } else if (paramInt1 == view.getStartOffset() && m == view.getEndOffset()) {
        f += view.getPreferredSpan(0);
      } else {
        return 0.0F;
      } 
      paramInt1 = k;
    } 
    return f;
  }
  
  protected int findOffsetToCharactersInString(char[] paramArrayOfChar, int paramInt) {
    int i = paramArrayOfChar.length;
    int j = getEndOffset();
    Segment segment = new Segment();
    try {
      getDocument().getText(paramInt, j - paramInt, segment);
    } catch (BadLocationException badLocationException) {
      return -1;
    } 
    int k = segment.offset;
    int m = segment.offset + segment.count;
    while (k < m) {
      char c = segment.array[k];
      for (byte b = 0; b < i; b++) {
        if (c == paramArrayOfChar[b])
          return k - segment.offset + paramInt; 
      } 
      k++;
    } 
    return -1;
  }
  
  protected float getTabBase() { return this.tabBase; }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    this.tabBase = rectangle.x + getLeftInset();
    super.paint(paramGraphics, paramShape);
    if (this.firstLineIndent < 0) {
      Shape shape = getChildAllocation(0, paramShape);
      if (shape != null && shape.intersects(rectangle)) {
        int i = rectangle.x + getLeftInset() + this.firstLineIndent;
        int j = rectangle.y + getTopInset();
        Rectangle rectangle1 = paramGraphics.getClipBounds();
        this.tempRect.x = i + getOffset(0, 0);
        this.tempRect.y = j + getOffset(1, 0);
        this.tempRect.width = getSpan(0, 0) - this.firstLineIndent;
        this.tempRect.height = getSpan(1, 0);
        if (this.tempRect.intersects(rectangle1)) {
          this.tempRect.x -= this.firstLineIndent;
          paintChild(paramGraphics, this.tempRect, 0);
        } 
      } 
    } 
  }
  
  public float getAlignment(int paramInt) {
    float f;
    switch (paramInt) {
      case 1:
        f = 0.5F;
        if (getViewCount() != 0) {
          int i = (int)getPreferredSpan(1);
          View view = getView(0);
          int j = (int)view.getPreferredSpan(1);
          f = (i != 0) ? ((j / 2) / i) : 0.0F;
        } 
        return f;
      case 0:
        return 0.5F;
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public View breakView(int paramInt, float paramFloat, Shape paramShape) {
    if (paramInt == 1) {
      if (paramShape != null) {
        Rectangle rectangle = paramShape.getBounds();
        setSize(rectangle.width, rectangle.height);
      } 
      return this;
    } 
    return this;
  }
  
  public int getBreakWeight(int paramInt, float paramFloat) { return (paramInt == 1) ? 0 : 0; }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    paramSizeRequirements = super.calculateMinorAxisRequirements(paramInt, paramSizeRequirements);
    float f1 = 0.0F;
    float f2 = 0.0F;
    int i = getLayoutViewCount();
    for (byte b = 0; b < i; b++) {
      View view = getLayoutView(b);
      float f = view.getMinimumSpan(paramInt);
      if (view.getBreakWeight(paramInt, 0.0F, view.getMaximumSpan(paramInt)) > 0) {
        int j = view.getStartOffset();
        int k = view.getEndOffset();
        float f3 = findEdgeSpan(view, paramInt, j, j, k);
        float f4 = findEdgeSpan(view, paramInt, k, j, k);
        f2 += f3;
        f1 = Math.max(f1, Math.max(f, f2));
        f2 = f4;
      } else {
        f2 += f;
        f1 = Math.max(f1, f2);
      } 
    } 
    paramSizeRequirements.minimum = Math.max(paramSizeRequirements.minimum, (int)f1);
    paramSizeRequirements.preferred = Math.max(paramSizeRequirements.minimum, paramSizeRequirements.preferred);
    paramSizeRequirements.maximum = Math.max(paramSizeRequirements.preferred, paramSizeRequirements.maximum);
    return paramSizeRequirements;
  }
  
  private float findEdgeSpan(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt4 - paramInt3;
    if (i <= 1)
      return paramView.getMinimumSpan(paramInt1); 
    int j = paramInt3 + i / 2;
    boolean bool1 = (j > paramInt2) ? 1 : 0;
    View view = bool1 ? paramView.createFragment(paramInt2, j) : paramView.createFragment(j, paramInt2);
    boolean bool2 = (view.getBreakWeight(paramInt1, 0.0F, view.getMaximumSpan(paramInt1)) > 0) ? 1 : 0;
    if (bool2 == bool1) {
      paramInt4 = j;
    } else {
      paramInt3 = j;
    } 
    return findEdgeSpan(view, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    setPropertiesFromAttributes();
    layoutChanged(0);
    layoutChanged(1);
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  static  {
    tabChars[0] = '\t';
    tabDecimalChars = new char[2];
    tabDecimalChars[0] = '\t';
    tabDecimalChars[1] = '.';
  }
  
  class Row extends BoxView {
    static final int SPACE_ADDON = 0;
    
    static final int SPACE_ADDON_LEFTOVER_END = 1;
    
    static final int START_JUSTIFIABLE = 2;
    
    static final int END_JUSTIFIABLE = 3;
    
    int[] justificationData = null;
    
    Row(Element param1Element) { super(param1Element, 0); }
    
    protected void loadChildren(ViewFactory param1ViewFactory) {}
    
    public AttributeSet getAttributes() {
      View view = getParent();
      return (view != null) ? view.getAttributes() : null;
    }
    
    public float getAlignment(int param1Int) {
      if (param1Int == 0) {
        float f;
        switch (ParagraphView.this.justification) {
          case 0:
            return 0.0F;
          case 2:
            return 1.0F;
          case 1:
            return 0.5F;
          case 3:
            f = 0.5F;
            if (isJustifiableDocument())
              f = 0.0F; 
            return f;
        } 
      } 
      return super.getAlignment(param1Int);
    }
    
    public Shape modelToView(int param1Int, Shape param1Shape, Position.Bias param1Bias) throws BadLocationException {
      Rectangle rectangle = param1Shape.getBounds();
      View view = getViewAtPosition(param1Int, rectangle);
      if (view != null && !view.getElement().isLeaf())
        return super.modelToView(param1Int, param1Shape, param1Bias); 
      rectangle = param1Shape.getBounds();
      int i = rectangle.height;
      int j = rectangle.y;
      Shape shape = super.modelToView(param1Int, param1Shape, param1Bias);
      rectangle = shape.getBounds();
      rectangle.height = i;
      rectangle.y = j;
      return rectangle;
    }
    
    public int getStartOffset() {
      int i = Integer.MAX_VALUE;
      int j = getViewCount();
      for (byte b = 0; b < j; b++) {
        View view = getView(b);
        i = Math.min(i, view.getStartOffset());
      } 
      return i;
    }
    
    public int getEndOffset() {
      int i = 0;
      int j = getViewCount();
      for (byte b = 0; b < j; b++) {
        View view = getView(b);
        i = Math.max(i, view.getEndOffset());
      } 
      return i;
    }
    
    protected void layoutMinorAxis(int param1Int1, int param1Int2, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2) { baselineLayout(param1Int1, param1Int2, param1ArrayOfInt1, param1ArrayOfInt2); }
    
    protected SizeRequirements calculateMinorAxisRequirements(int param1Int, SizeRequirements param1SizeRequirements) { return baselineRequirements(param1Int, param1SizeRequirements); }
    
    private boolean isLastRow() {
      View view;
      return ((view = getParent()) == null || this == view.getView(view.getViewCount() - true));
    }
    
    private boolean isBrokenRow() {
      boolean bool = false;
      int i = getViewCount();
      if (i > 0) {
        View view = getView(i - 1);
        if (view.getBreakWeight(0, 0.0F, 0.0F) >= 3000)
          bool = true; 
      } 
      return bool;
    }
    
    private boolean isJustifiableDocument() { return !Boolean.TRUE.equals(getDocument().getProperty("i18n")); }
    
    private boolean isJustifyEnabled() {
      null = (ParagraphView.this.justification == 3);
      null = (null && isJustifiableDocument());
      null = (null && !isLastRow());
      return (null && !isBrokenRow());
    }
    
    protected SizeRequirements calculateMajorAxisRequirements(int param1Int, SizeRequirements param1SizeRequirements) {
      int[] arrayOfInt = this.justificationData;
      this.justificationData = null;
      SizeRequirements sizeRequirements = super.calculateMajorAxisRequirements(param1Int, param1SizeRequirements);
      if (isJustifyEnabled())
        this.justificationData = arrayOfInt; 
      return sizeRequirements;
    }
    
    protected void layoutMajorAxis(int param1Int1, int param1Int2, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2) {
      int[] arrayOfInt1 = this.justificationData;
      this.justificationData = null;
      super.layoutMajorAxis(param1Int1, param1Int2, param1ArrayOfInt1, param1ArrayOfInt2);
      if (!isJustifyEnabled())
        return; 
      int i = 0;
      for (int i8 : param1ArrayOfInt2)
        i += i8; 
      if (i == param1Int1)
        return; 
      int j = 0;
      int k = -1;
      int m = -1;
      int n = 0;
      int i1 = getStartOffset();
      int i2 = getEndOffset();
      int[] arrayOfInt2 = new int[i2 - i1];
      Arrays.fill(arrayOfInt2, 0);
      int i3;
      for (i3 = getViewCount() - 1; i3 >= 0; i3--) {
        View view = getView(i3);
        if (view instanceof GlyphView) {
          GlyphView.JustificationInfo justificationInfo = ((GlyphView)view).getJustificationInfo(i1);
          int i8 = view.getStartOffset();
          int i9 = i8 - i1;
          for (int i10 = 0; i10 < justificationInfo.spaceMap.length(); i10++) {
            if (justificationInfo.spaceMap.get(i10))
              arrayOfInt2[i10 + i9] = 1; 
          } 
          if (k > 0)
            if (justificationInfo.end >= 0) {
              j += justificationInfo.trailingSpaces;
            } else {
              n += justificationInfo.trailingSpaces;
            }  
          if (justificationInfo.start >= 0) {
            k = justificationInfo.start + i8;
            j += n;
          } 
          if (justificationInfo.end >= 0 && m < 0)
            m = justificationInfo.end + i8; 
          j += justificationInfo.contentSpaces;
          n = justificationInfo.leadingSpaces;
          if (justificationInfo.hasTab)
            break; 
        } 
      } 
      if (j <= 0)
        return; 
      i3 = param1Int1 - i;
      int i4 = (j > 0) ? (i3 / j) : 0;
      int i5 = -1;
      int i6 = k - i1;
      int i7 = i3 - i4 * j;
      while (i7 > 0) {
        i5 = i6;
        i7 -= arrayOfInt2[i6];
        i6++;
      } 
      if (i4 > 0 || i5 >= 0) {
        this.justificationData = (arrayOfInt1 != null) ? arrayOfInt1 : new int[4];
        this.justificationData[0] = i4;
        this.justificationData[1] = i5;
        this.justificationData[2] = k - i1;
        this.justificationData[3] = m - i1;
        super.layoutMajorAxis(param1Int1, param1Int2, param1ArrayOfInt1, param1ArrayOfInt2);
      } 
    }
    
    public float getMaximumSpan(int param1Int) {
      float f;
      if (0 == param1Int && isJustifyEnabled()) {
        f = Float.MAX_VALUE;
      } else {
        f = super.getMaximumSpan(param1Int);
      } 
      return f;
    }
    
    protected int getViewIndexAtPosition(int param1Int) {
      if (param1Int < getStartOffset() || param1Int >= getEndOffset())
        return -1; 
      for (int i = getViewCount() - 1; i >= 0; i--) {
        View view = getView(i);
        if (param1Int >= view.getStartOffset() && param1Int < view.getEndOffset())
          return i; 
      } 
      return -1;
    }
    
    protected short getLeftInset() {
      int i = 0;
      View view;
      if ((view = getParent()) != null && this == view.getView(false))
        i = ParagraphView.this.firstLineIndent; 
      return (short)(super.getLeftInset() + i);
    }
    
    protected short getBottomInset() { return (short)(int)(super.getBottomInset() + ((this.minorRequest != null) ? this.minorRequest.preferred : false) * ParagraphView.this.lineSpacing); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\ParagraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */