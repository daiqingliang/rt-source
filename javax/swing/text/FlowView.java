package javax.swing.text;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;

public abstract class FlowView extends BoxView {
  protected int layoutSpan = Integer.MAX_VALUE;
  
  protected View layoutPool;
  
  protected FlowStrategy strategy = new FlowStrategy();
  
  public FlowView(Element paramElement, int paramInt) { super(paramElement, paramInt); }
  
  public int getFlowAxis() { return (getAxis() == 1) ? 0 : 1; }
  
  public int getFlowSpan(int paramInt) { return this.layoutSpan; }
  
  public int getFlowStart(int paramInt) { return 0; }
  
  protected abstract View createRow();
  
  protected void loadChildren(ViewFactory paramViewFactory) {
    if (this.layoutPool == null)
      this.layoutPool = new LogicalView(getElement()); 
    this.layoutPool.setParent(this);
    this.strategy.insertUpdate(this, null, null);
  }
  
  protected int getViewIndexAtPosition(int paramInt) {
    if (paramInt >= getStartOffset() && paramInt < getEndOffset())
      for (byte b = 0; b < getViewCount(); b++) {
        View view = getView(b);
        if (paramInt >= view.getStartOffset() && paramInt < view.getEndOffset())
          return b; 
      }  
    return -1;
  }
  
  protected void layout(int paramInt1, int paramInt2) {
    int j;
    int i = getFlowAxis();
    if (i == 0) {
      j = paramInt1;
    } else {
      j = paramInt2;
    } 
    if (this.layoutSpan != j) {
      layoutChanged(i);
      layoutChanged(getAxis());
      this.layoutSpan = j;
    } 
    if (!isLayoutValid(i)) {
      int k = getAxis();
      int m = (k == 0) ? getWidth() : getHeight();
      this.strategy.layout(this);
      int n = (int)getPreferredSpan(k);
      if (m != n) {
        View view = getParent();
        if (view != null)
          view.preferenceChanged(this, (k == 0), (k == 1)); 
        Container container = getContainer();
        if (container != null)
          container.repaint(); 
      } 
    } 
    super.layout(paramInt1, paramInt2);
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    if (paramSizeRequirements == null)
      paramSizeRequirements = new SizeRequirements(); 
    float f1 = this.layoutPool.getPreferredSpan(paramInt);
    float f2 = this.layoutPool.getMinimumSpan(paramInt);
    paramSizeRequirements.minimum = (int)f2;
    paramSizeRequirements.preferred = Math.max(paramSizeRequirements.minimum, (int)f1);
    paramSizeRequirements.maximum = Integer.MAX_VALUE;
    paramSizeRequirements.alignment = 0.5F;
    return paramSizeRequirements;
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    this.layoutPool.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    this.strategy.insertUpdate(this, paramDocumentEvent, getInsideAllocation(paramShape));
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    this.layoutPool.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    this.strategy.removeUpdate(this, paramDocumentEvent, getInsideAllocation(paramShape));
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    this.layoutPool.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    this.strategy.changedUpdate(this, paramDocumentEvent, getInsideAllocation(paramShape));
  }
  
  public void setParent(View paramView) {
    super.setParent(paramView);
    if (paramView == null && this.layoutPool != null)
      this.layoutPool.setParent(null); 
  }
  
  public static class FlowStrategy {
    Position damageStart = null;
    
    Vector<View> viewBuffer;
    
    void addDamage(FlowView param1FlowView, int param1Int) {
      if (param1Int >= param1FlowView.getStartOffset() && param1Int < param1FlowView.getEndOffset() && (this.damageStart == null || param1Int < this.damageStart.getOffset()))
        try {
          this.damageStart = param1FlowView.getDocument().createPosition(param1Int);
        } catch (BadLocationException badLocationException) {
          assert false;
        }  
    }
    
    void unsetDamage() { this.damageStart = null; }
    
    public void insertUpdate(FlowView param1FlowView, DocumentEvent param1DocumentEvent, Rectangle param1Rectangle) {
      if (param1DocumentEvent != null)
        addDamage(param1FlowView, param1DocumentEvent.getOffset()); 
      if (param1Rectangle != null) {
        Container container = param1FlowView.getContainer();
        if (container != null)
          container.repaint(param1Rectangle.x, param1Rectangle.y, param1Rectangle.width, param1Rectangle.height); 
      } else {
        param1FlowView.preferenceChanged(null, true, true);
      } 
    }
    
    public void removeUpdate(FlowView param1FlowView, DocumentEvent param1DocumentEvent, Rectangle param1Rectangle) {
      addDamage(param1FlowView, param1DocumentEvent.getOffset());
      if (param1Rectangle != null) {
        Container container = param1FlowView.getContainer();
        if (container != null)
          container.repaint(param1Rectangle.x, param1Rectangle.y, param1Rectangle.width, param1Rectangle.height); 
      } else {
        param1FlowView.preferenceChanged(null, true, true);
      } 
    }
    
    public void changedUpdate(FlowView param1FlowView, DocumentEvent param1DocumentEvent, Rectangle param1Rectangle) {
      addDamage(param1FlowView, param1DocumentEvent.getOffset());
      if (param1Rectangle != null) {
        Container container = param1FlowView.getContainer();
        if (container != null)
          container.repaint(param1Rectangle.x, param1Rectangle.y, param1Rectangle.width, param1Rectangle.height); 
      } else {
        param1FlowView.preferenceChanged(null, true, true);
      } 
    }
    
    protected View getLogicalView(FlowView param1FlowView) { return param1FlowView.layoutPool; }
    
    public void layout(FlowView param1FlowView) {
      int j;
      int i;
      View view = getLogicalView(param1FlowView);
      int k = param1FlowView.getEndOffset();
      if (param1FlowView.majorAllocValid) {
        if (this.damageStart == null)
          return; 
        for (int n = this.damageStart.getOffset(); (i = param1FlowView.getViewIndexAtPosition(n)) < 0; n--);
        if (i > 0)
          i--; 
        j = param1FlowView.getView(i).getStartOffset();
      } else {
        i = 0;
        j = param1FlowView.getStartOffset();
      } 
      reparentViews(view, j);
      this.viewBuffer = new Vector(10, 10);
      int m = param1FlowView.getViewCount();
      while (j < k) {
        if (i >= m) {
          View view1 = param1FlowView.createRow();
          param1FlowView.append(view1);
        } else {
          View view1 = param1FlowView.getView(i);
        } 
        j = layoutRow(param1FlowView, i, j);
        i++;
      } 
      this.viewBuffer = null;
      if (i < m)
        param1FlowView.replace(i, m - i, null); 
      unsetDamage();
    }
    
    protected int layoutRow(FlowView param1FlowView, int param1Int1, int param1Int2) {
      View view = param1FlowView.getView(param1Int1);
      float f1 = param1FlowView.getFlowStart(param1Int1);
      float f2 = param1FlowView.getFlowSpan(param1Int1);
      int i = param1FlowView.getEndOffset();
      TabExpander tabExpander = (param1FlowView instanceof TabExpander) ? (TabExpander)param1FlowView : null;
      int j = param1FlowView.getFlowAxis();
      int k = 0;
      float f3 = 0.0F;
      float f4 = 0.0F;
      byte b1 = -1;
      byte b2 = 0;
      this.viewBuffer.clear();
      while (param1Int2 < i && f2 >= 0.0F) {
        float f;
        View view1 = createView(param1FlowView, param1Int2, (int)f2, param1Int1);
        if (view1 == null)
          break; 
        int m = view1.getBreakWeight(j, f1, f2);
        if (m >= 3000) {
          View view2 = view1.breakView(j, param1Int2, f1, f2);
          if (view2 != null) {
            this.viewBuffer.add(view2);
            break;
          } 
          if (!b2)
            this.viewBuffer.add(view1); 
          break;
        } 
        if (m >= k && m > 0) {
          k = m;
          f3 = f1;
          f4 = f2;
          b1 = b2;
        } 
        if (j == 0 && view1 instanceof TabableView) {
          f = ((TabableView)view1).getTabbedSpan(f1, tabExpander);
        } else {
          f = view1.getPreferredSpan(j);
        } 
        if (f > f2 && b1 >= 0) {
          if (b1 < b2)
            view1 = (View)this.viewBuffer.get(b1); 
          for (byte b = b2 - 1; b >= b1; b--)
            this.viewBuffer.remove(b); 
          view1 = view1.breakView(j, view1.getStartOffset(), f3, f4);
        } 
        f2 -= f;
        f1 += f;
        this.viewBuffer.add(view1);
        param1Int2 = view1.getEndOffset();
        b2++;
      } 
      View[] arrayOfView = new View[this.viewBuffer.size()];
      this.viewBuffer.toArray(arrayOfView);
      view.replace(0, view.getViewCount(), arrayOfView);
      return (arrayOfView.length > 0) ? view.getEndOffset() : param1Int2;
    }
    
    protected void adjustRow(FlowView param1FlowView, int param1Int1, int param1Int2, int param1Int3) {
      int i = param1FlowView.getFlowAxis();
      View view1 = param1FlowView.getView(param1Int1);
      int j = view1.getViewCount();
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = -1;
      int i2;
      for (i2 = 0; i2 < j; i2++) {
        View view = view1.getView(i2);
        int i5 = param1Int2 - k;
        int i6 = view.getBreakWeight(i, (param1Int3 + k), i5);
        if (i6 >= m && i6 > 0) {
          m = i6;
          i1 = i2;
          n = k;
          if (i6 >= 3000)
            break; 
        } 
        k = (int)(k + view.getPreferredSpan(i));
      } 
      if (i1 < 0)
        return; 
      i2 = param1Int2 - n;
      View view2 = view1.getView(i1);
      view2 = view2.breakView(i, view2.getStartOffset(), (param1Int3 + n), i2);
      View[] arrayOfView = new View[1];
      arrayOfView[0] = view2;
      View view3 = getLogicalView(param1FlowView);
      int i3 = view1.getView(i1).getStartOffset();
      int i4 = view1.getEndOffset();
      for (byte b = 0; b < view3.getViewCount(); b++) {
        View view = view3.getView(b);
        if (view.getEndOffset() > i4)
          break; 
        if (view.getStartOffset() >= i3)
          view.setParent(view3); 
      } 
      view1.replace(i1, j - i1, arrayOfView);
    }
    
    void reparentViews(View param1View, int param1Int) {
      int i = param1View.getViewIndex(param1Int, Position.Bias.Forward);
      if (i >= 0)
        for (int j = i; j < param1View.getViewCount(); j++)
          param1View.getView(j).setParent(param1View);  
    }
    
    protected View createView(FlowView param1FlowView, int param1Int1, int param1Int2, int param1Int3) {
      View view = getLogicalView(param1FlowView);
      int i = view.getViewIndex(param1Int1, Position.Bias.Forward);
      null = view.getView(i);
      return (param1Int1 == null.getStartOffset()) ? null : null.createFragment(param1Int1, null.getEndOffset());
    }
  }
  
  static class LogicalView extends CompositeView {
    LogicalView(Element param1Element) { super(param1Element); }
    
    protected int getViewIndexAtPosition(int param1Int) {
      Element element = getElement();
      return element.isLeaf() ? 0 : super.getViewIndexAtPosition(param1Int);
    }
    
    protected void loadChildren(ViewFactory param1ViewFactory) {
      Element element = getElement();
      if (element.isLeaf()) {
        LabelView labelView = new LabelView(element);
        append(labelView);
      } else {
        super.loadChildren(param1ViewFactory);
      } 
    }
    
    public AttributeSet getAttributes() {
      View view = getParent();
      return (view != null) ? view.getAttributes() : null;
    }
    
    public float getPreferredSpan(int param1Int) {
      null = 0.0F;
      float f = 0.0F;
      int i = getViewCount();
      for (byte b = 0; b < i; b++) {
        View view = getView(b);
        f += view.getPreferredSpan(param1Int);
        if (view.getBreakWeight(param1Int, 0.0F, 2.14748365E9F) >= 3000) {
          null = Math.max(null, f);
          f = 0.0F;
        } 
      } 
      return Math.max(null, f);
    }
    
    public float getMinimumSpan(int param1Int) {
      null = 0.0F;
      float f = 0.0F;
      boolean bool = false;
      int i = getViewCount();
      for (byte b = 0; b < i; b++) {
        View view = getView(b);
        if (view.getBreakWeight(param1Int, 0.0F, 2.14748365E9F) == 0) {
          f += view.getPreferredSpan(param1Int);
          bool = true;
        } else if (bool) {
          null = Math.max(f, null);
          bool = false;
          f = 0.0F;
        } 
        if (view instanceof ComponentView)
          null = Math.max(null, view.getMinimumSpan(param1Int)); 
      } 
      return Math.max(null, f);
    }
    
    protected void forwardUpdateToView(View param1View, DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      View view = param1View.getParent();
      param1View.setParent(this);
      super.forwardUpdateToView(param1View, param1DocumentEvent, param1Shape, param1ViewFactory);
      param1View.setParent(view);
    }
    
    protected void forwardUpdate(DocumentEvent.ElementChange param1ElementChange, DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      super.forwardUpdate(param1ElementChange, param1DocumentEvent, param1Shape, param1ViewFactory);
      DocumentEvent.EventType eventType = param1DocumentEvent.getType();
      if (eventType == DocumentEvent.EventType.INSERT || eventType == DocumentEvent.EventType.REMOVE) {
        this.firstUpdateIndex = Math.min(this.lastUpdateIndex + 1, getViewCount() - 1);
        this.lastUpdateIndex = Math.max(getViewCount() - 1, 0);
        for (int i = this.firstUpdateIndex; i <= this.lastUpdateIndex; i++) {
          View view = getView(i);
          if (view != null)
            view.updateAfterChange(); 
        } 
      } 
    }
    
    public void paint(Graphics param1Graphics, Shape param1Shape) {}
    
    protected boolean isBefore(int param1Int1, int param1Int2, Rectangle param1Rectangle) { return false; }
    
    protected boolean isAfter(int param1Int1, int param1Int2, Rectangle param1Rectangle) { return false; }
    
    protected View getViewAtPoint(int param1Int1, int param1Int2, Rectangle param1Rectangle) { return null; }
    
    protected void childAllocation(int param1Int, Rectangle param1Rectangle) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\FlowView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */