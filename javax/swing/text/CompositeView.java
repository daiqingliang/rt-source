package javax.swing.text;

import java.awt.Rectangle;
import java.awt.Shape;

public abstract class CompositeView extends View {
  private static View[] ZERO = new View[0];
  
  private View[] children = new View[1];
  
  private int nchildren = 0;
  
  private short left;
  
  private short right;
  
  private short top;
  
  private short bottom;
  
  private Rectangle childAlloc = new Rectangle();
  
  public CompositeView(Element paramElement) { super(paramElement); }
  
  protected void loadChildren(ViewFactory paramViewFactory) {
    if (paramViewFactory == null)
      return; 
    Element element = getElement();
    int i = element.getElementCount();
    if (i > 0) {
      View[] arrayOfView = new View[i];
      for (byte b = 0; b < i; b++)
        arrayOfView[b] = paramViewFactory.create(element.getElement(b)); 
      replace(0, 0, arrayOfView);
    } 
  }
  
  public void setParent(View paramView) {
    super.setParent(paramView);
    if (paramView != null && this.nchildren == 0) {
      ViewFactory viewFactory = getViewFactory();
      loadChildren(viewFactory);
    } 
  }
  
  public int getViewCount() { return this.nchildren; }
  
  public View getView(int paramInt) { return this.children[paramInt]; }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView) {
    if (paramArrayOfView == null)
      paramArrayOfView = ZERO; 
    int i;
    for (i = paramInt1; i < paramInt1 + paramInt2; i++) {
      if (this.children[i].getParent() == this)
        this.children[i].setParent(null); 
      this.children[i] = null;
    } 
    i = paramArrayOfView.length - paramInt2;
    int j = paramInt1 + paramInt2;
    int k = this.nchildren - j;
    int m = j + i;
    if (this.nchildren + i >= this.children.length) {
      int n = Math.max(2 * this.children.length, this.nchildren + i);
      View[] arrayOfView = new View[n];
      System.arraycopy(this.children, 0, arrayOfView, 0, paramInt1);
      System.arraycopy(paramArrayOfView, 0, arrayOfView, paramInt1, paramArrayOfView.length);
      System.arraycopy(this.children, j, arrayOfView, m, k);
      this.children = arrayOfView;
    } else {
      System.arraycopy(this.children, j, this.children, m, k);
      System.arraycopy(paramArrayOfView, 0, this.children, paramInt1, paramArrayOfView.length);
    } 
    this.nchildren += i;
    for (byte b = 0; b < paramArrayOfView.length; b++)
      paramArrayOfView[b].setParent(this); 
  }
  
  public Shape getChildAllocation(int paramInt, Shape paramShape) {
    Rectangle rectangle = getInsideAllocation(paramShape);
    childAllocation(paramInt, rectangle);
    return rectangle;
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException {
    boolean bool = (paramBias == Position.Bias.Backward) ? 1 : 0;
    int i = bool ? Math.max(0, paramInt - 1) : paramInt;
    if (bool && i < getStartOffset())
      return null; 
    int j = getViewIndexAtPosition(i);
    if (j != -1 && j < getViewCount()) {
      View view = getView(j);
      if (view != null && i >= view.getStartOffset() && i < view.getEndOffset()) {
        Shape shape1 = getChildAllocation(j, paramShape);
        if (shape1 == null)
          return null; 
        Shape shape2 = view.modelToView(paramInt, shape1, paramBias);
        if (shape2 == null && view.getEndOffset() == paramInt && ++j < getViewCount()) {
          view = getView(j);
          shape2 = view.modelToView(paramInt, getChildAllocation(j, paramShape), paramBias);
        } 
        return shape2;
      } 
    } 
    throw new BadLocationException("Position not represented by view", paramInt);
  }
  
  public Shape modelToView(int paramInt1, Position.Bias paramBias1, int paramInt2, Position.Bias paramBias2, Shape paramShape) throws BadLocationException {
    if (paramInt1 == getStartOffset() && paramInt2 == getEndOffset())
      return paramShape; 
    Rectangle rectangle1 = getInsideAllocation(paramShape);
    Rectangle rectangle2 = new Rectangle(rectangle1);
    View view1 = getViewAtPosition((paramBias1 == Position.Bias.Backward) ? Math.max(0, paramInt1 - 1) : paramInt1, rectangle2);
    Rectangle rectangle3 = new Rectangle(rectangle1);
    View view2 = getViewAtPosition((paramBias2 == Position.Bias.Backward) ? Math.max(0, paramInt2 - 1) : paramInt2, rectangle3);
    if (view1 == view2)
      return (view1 == null) ? paramShape : view1.modelToView(paramInt1, paramBias1, paramInt2, paramBias2, rectangle2); 
    int i = getViewCount();
    for (byte b = 0; b < i; b++) {
      View view;
      if ((view = getView(b)) == view1 || view == view2) {
        Rectangle rectangle4;
        View view3;
        Rectangle rectangle5 = new Rectangle();
        if (view == view1) {
          rectangle4 = view1.modelToView(paramInt1, paramBias1, view1.getEndOffset(), Position.Bias.Backward, rectangle2).getBounds();
          view3 = view2;
        } else {
          rectangle4 = view2.modelToView(view2.getStartOffset(), Position.Bias.Forward, paramInt2, paramBias2, rectangle3).getBounds();
          view3 = view1;
        } 
        while (++b < i && (view = getView(b)) != view3) {
          rectangle5.setBounds(rectangle1);
          childAllocation(b, rectangle5);
          rectangle4.add(rectangle5);
        } 
        if (view3 != null) {
          Shape shape;
          if (view3 == view2) {
            shape = view2.modelToView(view2.getStartOffset(), Position.Bias.Forward, paramInt2, paramBias2, rectangle3);
          } else {
            shape = view1.modelToView(paramInt1, paramBias1, view1.getEndOffset(), Position.Bias.Backward, rectangle2);
          } 
          if (shape instanceof Rectangle) {
            rectangle4.add((Rectangle)shape);
          } else {
            rectangle4.add(shape.getBounds());
          } 
        } 
        return rectangle4;
      } 
    } 
    throw new BadLocationException("Position not represented by view", paramInt1);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    Rectangle rectangle = getInsideAllocation(paramShape);
    if (isBefore((int)paramFloat1, (int)paramFloat2, rectangle)) {
      int i = -1;
      try {
        i = getNextVisualPositionFrom(-1, Position.Bias.Forward, paramShape, 3, paramArrayOfBias);
      } catch (BadLocationException badLocationException) {
      
      } catch (IllegalArgumentException illegalArgumentException) {}
      if (i == -1) {
        i = getStartOffset();
        paramArrayOfBias[0] = Position.Bias.Forward;
      } 
      return i;
    } 
    if (isAfter((int)paramFloat1, (int)paramFloat2, rectangle)) {
      int i = -1;
      try {
        i = getNextVisualPositionFrom(-1, Position.Bias.Forward, paramShape, 7, paramArrayOfBias);
      } catch (BadLocationException badLocationException) {
      
      } catch (IllegalArgumentException illegalArgumentException) {}
      if (i == -1) {
        i = getEndOffset() - 1;
        paramArrayOfBias[0] = Position.Bias.Forward;
      } 
      return i;
    } 
    View view = getViewAtPoint((int)paramFloat1, (int)paramFloat2, rectangle);
    return (view != null) ? view.viewToModel(paramFloat1, paramFloat2, rectangle, paramArrayOfBias) : -1;
  }
  
  public int getNextVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    if (paramInt1 < -1)
      throw new BadLocationException("invalid position", paramInt1); 
    Rectangle rectangle = getInsideAllocation(paramShape);
    switch (paramInt2) {
      case 1:
        return getNextNorthSouthVisualPositionFrom(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
      case 5:
        return getNextNorthSouthVisualPositionFrom(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
      case 3:
        return getNextEastWestVisualPositionFrom(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
      case 7:
        return getNextEastWestVisualPositionFrom(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
    } 
    throw new IllegalArgumentException("Bad direction: " + paramInt2);
  }
  
  public int getViewIndex(int paramInt, Position.Bias paramBias) {
    if (paramBias == Position.Bias.Backward)
      paramInt--; 
    return (paramInt >= getStartOffset() && paramInt < getEndOffset()) ? getViewIndexAtPosition(paramInt) : -1;
  }
  
  protected abstract boolean isBefore(int paramInt1, int paramInt2, Rectangle paramRectangle);
  
  protected abstract boolean isAfter(int paramInt1, int paramInt2, Rectangle paramRectangle);
  
  protected abstract View getViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle);
  
  protected abstract void childAllocation(int paramInt, Rectangle paramRectangle);
  
  protected View getViewAtPosition(int paramInt, Rectangle paramRectangle) {
    int i = getViewIndexAtPosition(paramInt);
    if (i >= 0 && i < getViewCount()) {
      View view = getView(i);
      if (paramRectangle != null)
        childAllocation(i, paramRectangle); 
      return view;
    } 
    return null;
  }
  
  protected int getViewIndexAtPosition(int paramInt) {
    Element element = getElement();
    return element.getElementIndex(paramInt);
  }
  
  protected Rectangle getInsideAllocation(Shape paramShape) {
    if (paramShape != null) {
      Rectangle rectangle;
      if (paramShape instanceof Rectangle) {
        rectangle = (Rectangle)paramShape;
      } else {
        rectangle = paramShape.getBounds();
      } 
      this.childAlloc.setBounds(rectangle);
      this.childAlloc.x += getLeftInset();
      this.childAlloc.y += getTopInset();
      this.childAlloc.width -= getLeftInset() + getRightInset();
      this.childAlloc.height -= getTopInset() + getBottomInset();
      return this.childAlloc;
    } 
    return null;
  }
  
  protected void setParagraphInsets(AttributeSet paramAttributeSet) {
    this.top = (short)(int)StyleConstants.getSpaceAbove(paramAttributeSet);
    this.left = (short)(int)StyleConstants.getLeftIndent(paramAttributeSet);
    this.bottom = (short)(int)StyleConstants.getSpaceBelow(paramAttributeSet);
    this.right = (short)(int)StyleConstants.getRightIndent(paramAttributeSet);
  }
  
  protected void setInsets(short paramShort1, short paramShort2, short paramShort3, short paramShort4) {
    this.top = paramShort1;
    this.left = paramShort2;
    this.right = paramShort4;
    this.bottom = paramShort3;
  }
  
  protected short getLeftInset() { return this.left; }
  
  protected short getRightInset() { return this.right; }
  
  protected short getTopInset() { return this.top; }
  
  protected short getBottomInset() { return this.bottom; }
  
  protected int getNextNorthSouthVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException { return Utilities.getNextVisualPositionFrom(this, paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias); }
  
  protected int getNextEastWestVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException { return Utilities.getNextVisualPositionFrom(this, paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias); }
  
  protected boolean flipEastAndWestAtEnds(int paramInt, Position.Bias paramBias) { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\CompositeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */