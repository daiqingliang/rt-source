package javax.swing.text;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;

public abstract class View implements SwingConstants {
  public static final int BadBreakWeight = 0;
  
  public static final int GoodBreakWeight = 1000;
  
  public static final int ExcellentBreakWeight = 2000;
  
  public static final int ForcedBreakWeight = 3000;
  
  public static final int X_AXIS = 0;
  
  public static final int Y_AXIS = 1;
  
  static final Position.Bias[] sharedBiasReturn = new Position.Bias[1];
  
  private View parent;
  
  private Element elem;
  
  int firstUpdateIndex;
  
  int lastUpdateIndex;
  
  public View(Element paramElement) { this.elem = paramElement; }
  
  public View getParent() { return this.parent; }
  
  public boolean isVisible() { return true; }
  
  public abstract float getPreferredSpan(int paramInt);
  
  public float getMinimumSpan(int paramInt) {
    int i = getResizeWeight(paramInt);
    return (i == 0) ? getPreferredSpan(paramInt) : 0.0F;
  }
  
  public float getMaximumSpan(int paramInt) {
    int i = getResizeWeight(paramInt);
    return (i == 0) ? getPreferredSpan(paramInt) : 2.14748365E9F;
  }
  
  public void preferenceChanged(View paramView, boolean paramBoolean1, boolean paramBoolean2) {
    View view = getParent();
    if (view != null)
      view.preferenceChanged(this, paramBoolean1, paramBoolean2); 
  }
  
  public float getAlignment(int paramInt) { return 0.5F; }
  
  public abstract void paint(Graphics paramGraphics, Shape paramShape);
  
  public void setParent(View paramView) {
    if (paramView == null)
      for (byte b = 0; b < getViewCount(); b++) {
        if (getView(b).getParent() == this)
          getView(b).setParent(null); 
      }  
    this.parent = paramView;
  }
  
  public int getViewCount() { return 0; }
  
  public View getView(int paramInt) { return null; }
  
  public void removeAll() { replace(0, getViewCount(), null); }
  
  public void remove(int paramInt) { replace(paramInt, 1, null); }
  
  public void insert(int paramInt, View paramView) {
    View[] arrayOfView = new View[1];
    arrayOfView[0] = paramView;
    replace(paramInt, 0, arrayOfView);
  }
  
  public void append(View paramView) {
    View[] arrayOfView = new View[1];
    arrayOfView[0] = paramView;
    replace(getViewCount(), 0, arrayOfView);
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView) {}
  
  public int getViewIndex(int paramInt, Position.Bias paramBias) { return -1; }
  
  public Shape getChildAllocation(int paramInt, Shape paramShape) { return null; }
  
  public int getNextVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    if (paramInt1 < -1)
      throw new BadLocationException("Invalid position", paramInt1); 
    paramArrayOfBias[0] = Position.Bias.Forward;
    switch (paramInt2) {
      case 1:
      case 5:
        if (paramInt1 == -1) {
          paramInt1 = (paramInt2 == 1) ? Math.max(0, getEndOffset() - 1) : getStartOffset();
        } else {
          int i;
          Object object;
          JTextComponent jTextComponent = (JTextComponent)getContainer();
          Caret caret = (jTextComponent != null) ? jTextComponent.getCaret() : null;
          if (caret != null) {
            object = caret.getMagicCaretPosition();
          } else {
            object = null;
          } 
          if (object == null) {
            Rectangle rectangle = jTextComponent.modelToView(paramInt1);
            i = (rectangle == null) ? 0 : rectangle.x;
          } else {
            i = object.x;
          } 
          if (paramInt2 == 1) {
            paramInt1 = Utilities.getPositionAbove(jTextComponent, paramInt1, i);
          } else {
            paramInt1 = Utilities.getPositionBelow(jTextComponent, paramInt1, i);
          } 
        } 
        return paramInt1;
      case 7:
        if (paramInt1 == -1) {
          paramInt1 = Math.max(0, getEndOffset() - 1);
        } else {
          paramInt1 = Math.max(0, paramInt1 - 1);
        } 
        return paramInt1;
      case 3:
        if (paramInt1 == -1) {
          paramInt1 = getStartOffset();
        } else {
          paramInt1 = Math.min(paramInt1 + 1, getDocument().getLength());
        } 
        return paramInt1;
    } 
    throw new IllegalArgumentException("Bad direction: " + paramInt2);
  }
  
  public abstract Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException;
  
  public Shape modelToView(int paramInt1, Position.Bias paramBias1, int paramInt2, Position.Bias paramBias2, Shape paramShape) throws BadLocationException {
    Shape shape2;
    Shape shape1 = modelToView(paramInt1, paramShape, paramBias1);
    if (paramInt2 == getEndOffset()) {
      try {
        shape2 = modelToView(paramInt2, paramShape, paramBias2);
      } catch (BadLocationException badLocationException) {
        shape2 = null;
      } 
      if (shape2 == null) {
        Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
        shape2 = new Rectangle(rectangle.x + rectangle.width - 1, rectangle.y, 1, rectangle.height);
      } 
    } else {
      shape2 = modelToView(paramInt2, paramShape, paramBias2);
    } 
    Rectangle rectangle1 = shape1.getBounds();
    Rectangle rectangle2 = (shape2 instanceof Rectangle) ? (Rectangle)shape2 : shape2.getBounds();
    if (rectangle1.y != rectangle2.y) {
      Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      rectangle1.x = rectangle.x;
      rectangle1.width = rectangle.width;
    } 
    rectangle1.add(rectangle2);
    return rectangle1;
  }
  
  public abstract int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias);
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    if (getViewCount() > 0) {
      Element element = getElement();
      DocumentEvent.ElementChange elementChange = paramDocumentEvent.getChange(element);
      if (elementChange != null && !updateChildren(elementChange, paramDocumentEvent, paramViewFactory))
        elementChange = null; 
      forwardUpdate(elementChange, paramDocumentEvent, paramShape, paramViewFactory);
      updateLayout(elementChange, paramDocumentEvent, paramShape);
    } 
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    if (getViewCount() > 0) {
      Element element = getElement();
      DocumentEvent.ElementChange elementChange = paramDocumentEvent.getChange(element);
      if (elementChange != null && !updateChildren(elementChange, paramDocumentEvent, paramViewFactory))
        elementChange = null; 
      forwardUpdate(elementChange, paramDocumentEvent, paramShape, paramViewFactory);
      updateLayout(elementChange, paramDocumentEvent, paramShape);
    } 
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    if (getViewCount() > 0) {
      Element element = getElement();
      DocumentEvent.ElementChange elementChange = paramDocumentEvent.getChange(element);
      if (elementChange != null && !updateChildren(elementChange, paramDocumentEvent, paramViewFactory))
        elementChange = null; 
      forwardUpdate(elementChange, paramDocumentEvent, paramShape, paramViewFactory);
      updateLayout(elementChange, paramDocumentEvent, paramShape);
    } 
  }
  
  public Document getDocument() { return this.elem.getDocument(); }
  
  public int getStartOffset() { return this.elem.getStartOffset(); }
  
  public int getEndOffset() { return this.elem.getEndOffset(); }
  
  public Element getElement() { return this.elem; }
  
  public Graphics getGraphics() {
    Container container = getContainer();
    return container.getGraphics();
  }
  
  public AttributeSet getAttributes() { return this.elem.getAttributes(); }
  
  public View breakView(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2) { return this; }
  
  public View createFragment(int paramInt1, int paramInt2) { return this; }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2) { return (paramFloat2 > getPreferredSpan(paramInt)) ? 1000 : 0; }
  
  public int getResizeWeight(int paramInt) { return 0; }
  
  public void setSize(float paramFloat1, float paramFloat2) {}
  
  public Container getContainer() {
    View view = getParent();
    return (view != null) ? view.getContainer() : null;
  }
  
  public ViewFactory getViewFactory() {
    View view = getParent();
    return (view != null) ? view.getViewFactory() : null;
  }
  
  public String getToolTipText(float paramFloat1, float paramFloat2, Shape paramShape) {
    int i = getViewIndex(paramFloat1, paramFloat2, paramShape);
    if (i >= 0) {
      paramShape = getChildAllocation(i, paramShape);
      Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      if (rectangle.contains(paramFloat1, paramFloat2))
        return getView(i).getToolTipText(paramFloat1, paramFloat2, paramShape); 
    } 
    return null;
  }
  
  public int getViewIndex(float paramFloat1, float paramFloat2, Shape paramShape) {
    for (int i = getViewCount() - 1; i >= 0; i--) {
      Shape shape = getChildAllocation(i, paramShape);
      if (shape != null) {
        Rectangle rectangle = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
        if (rectangle.contains(paramFloat1, paramFloat2))
          return i; 
      } 
    } 
    return -1;
  }
  
  protected boolean updateChildren(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, ViewFactory paramViewFactory) {
    Element[] arrayOfElement1 = paramElementChange.getChildrenRemoved();
    Element[] arrayOfElement2 = paramElementChange.getChildrenAdded();
    View[] arrayOfView = null;
    if (arrayOfElement2 != null) {
      arrayOfView = new View[arrayOfElement2.length];
      for (byte b = 0; b < arrayOfElement2.length; b++)
        arrayOfView[b] = paramViewFactory.create(arrayOfElement2[b]); 
    } 
    int i = 0;
    int j = paramElementChange.getIndex();
    if (arrayOfElement1 != null)
      i = arrayOfElement1.length; 
    replace(j, i, arrayOfView);
    return true;
  }
  
  protected void forwardUpdate(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    calculateUpdateIndexes(paramDocumentEvent);
    int i = this.lastUpdateIndex + 1;
    int j = i;
    Element[] arrayOfElement = (paramElementChange != null) ? paramElementChange.getChildrenAdded() : null;
    if (arrayOfElement != null && arrayOfElement.length > 0) {
      i = paramElementChange.getIndex();
      j = i + arrayOfElement.length - 1;
    } 
    for (int k = this.firstUpdateIndex; k <= this.lastUpdateIndex; k++) {
      if (k < i || k > j) {
        View view = getView(k);
        if (view != null) {
          Shape shape = getChildAllocation(k, paramShape);
          forwardUpdateToView(view, paramDocumentEvent, shape, paramViewFactory);
        } 
      } 
    } 
  }
  
  void calculateUpdateIndexes(DocumentEvent paramDocumentEvent) {
    int i = paramDocumentEvent.getOffset();
    this.firstUpdateIndex = getViewIndex(i, Position.Bias.Forward);
    if (this.firstUpdateIndex == -1 && paramDocumentEvent.getType() == DocumentEvent.EventType.REMOVE && i >= getEndOffset())
      this.firstUpdateIndex = getViewCount() - 1; 
    this.lastUpdateIndex = this.firstUpdateIndex;
    View view = (this.firstUpdateIndex >= 0) ? getView(this.firstUpdateIndex) : null;
    if (view != null && view.getStartOffset() == i && i > 0)
      this.firstUpdateIndex = Math.max(this.firstUpdateIndex - 1, 0); 
    if (paramDocumentEvent.getType() != DocumentEvent.EventType.REMOVE) {
      this.lastUpdateIndex = getViewIndex(i + paramDocumentEvent.getLength(), Position.Bias.Forward);
      if (this.lastUpdateIndex < 0)
        this.lastUpdateIndex = getViewCount() - 1; 
    } 
    this.firstUpdateIndex = Math.max(this.firstUpdateIndex, 0);
  }
  
  void updateAfterChange() {}
  
  protected void forwardUpdateToView(View paramView, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    DocumentEvent.EventType eventType = paramDocumentEvent.getType();
    if (eventType == DocumentEvent.EventType.INSERT) {
      paramView.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    } else if (eventType == DocumentEvent.EventType.REMOVE) {
      paramView.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    } else {
      paramView.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    } 
  }
  
  protected void updateLayout(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape) {
    if (paramElementChange != null && paramShape != null) {
      preferenceChanged(null, true, true);
      Container container = getContainer();
      if (container != null)
        container.repaint(); 
    } 
  }
  
  @Deprecated
  public Shape modelToView(int paramInt, Shape paramShape) { return modelToView(paramInt, paramShape, Position.Bias.Forward); }
  
  @Deprecated
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape) {
    sharedBiasReturn[0] = Position.Bias.Forward;
    return viewToModel(paramFloat1, paramFloat2, paramShape, sharedBiasReturn);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\View.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */