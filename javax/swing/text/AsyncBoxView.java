package javax.swing.text;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;

public class AsyncBoxView extends View {
  int axis;
  
  List<ChildState> stats = new ArrayList();
  
  float majorSpan;
  
  boolean estimatedMajorSpan;
  
  float minorSpan;
  
  protected ChildLocator locator;
  
  float topInset;
  
  float bottomInset;
  
  float leftInset;
  
  float rightInset;
  
  ChildState minRequest;
  
  ChildState prefRequest;
  
  boolean majorChanged;
  
  boolean minorChanged;
  
  Runnable flushTask;
  
  ChildState changing;
  
  public AsyncBoxView(Element paramElement, int paramInt) {
    super(paramElement);
    this.axis = paramInt;
    this.locator = new ChildLocator();
    this.flushTask = new FlushTask();
    this.minorSpan = 32767.0F;
    this.estimatedMajorSpan = false;
  }
  
  public int getMajorAxis() { return this.axis; }
  
  public int getMinorAxis() { return (this.axis == 0) ? 1 : 0; }
  
  public float getTopInset() { return this.topInset; }
  
  public void setTopInset(float paramFloat) { this.topInset = paramFloat; }
  
  public float getBottomInset() { return this.bottomInset; }
  
  public void setBottomInset(float paramFloat) { this.bottomInset = paramFloat; }
  
  public float getLeftInset() { return this.leftInset; }
  
  public void setLeftInset(float paramFloat) { this.leftInset = paramFloat; }
  
  public float getRightInset() { return this.rightInset; }
  
  public void setRightInset(float paramFloat) { this.rightInset = paramFloat; }
  
  protected float getInsetSpan(int paramInt) { return (paramInt == 0) ? (getLeftInset() + getRightInset()) : (getTopInset() + getBottomInset()); }
  
  protected void setEstimatedMajorSpan(boolean paramBoolean) { this.estimatedMajorSpan = paramBoolean; }
  
  protected boolean getEstimatedMajorSpan() { return this.estimatedMajorSpan; }
  
  protected ChildState getChildState(int paramInt) {
    synchronized (this.stats) {
      if (paramInt >= 0 && paramInt < this.stats.size())
        return (ChildState)this.stats.get(paramInt); 
      return null;
    } 
  }
  
  protected LayoutQueue getLayoutQueue() { return LayoutQueue.getDefaultQueue(); }
  
  protected ChildState createChildState(View paramView) { return new ChildState(paramView); }
  
  protected void majorRequirementChange(ChildState paramChildState, float paramFloat) {
    if (!this.estimatedMajorSpan)
      this.majorSpan += paramFloat; 
    this.majorChanged = true;
  }
  
  protected void minorRequirementChange(ChildState paramChildState) { this.minorChanged = true; }
  
  protected void flushRequirementChanges() {
    abstractDocument = (AbstractDocument)getDocument();
    try {
      abstractDocument.readLock();
      View view = null;
      boolean bool1 = false;
      boolean bool2 = false;
      synchronized (this) {
        synchronized (this.stats) {
          int i = getViewCount();
          if (i > 0 && (this.minorChanged || this.estimatedMajorSpan)) {
            LayoutQueue layoutQueue = getLayoutQueue();
            ChildState childState1 = getChildState(0);
            ChildState childState2 = getChildState(0);
            float f = 0.0F;
            for (byte b = 1; b < i; b++) {
              ChildState childState = getChildState(b);
              if (this.minorChanged) {
                if (childState.min > childState1.min)
                  childState1 = childState; 
                if (childState.pref > childState2.pref)
                  childState2 = childState; 
              } 
              if (this.estimatedMajorSpan)
                f += childState.getMajorSpan(); 
            } 
            if (this.minorChanged) {
              this.minRequest = childState1;
              this.prefRequest = childState2;
            } 
            if (this.estimatedMajorSpan) {
              this.majorSpan = f;
              this.estimatedMajorSpan = false;
              this.majorChanged = true;
            } 
          } 
        } 
        if (this.majorChanged || this.minorChanged) {
          view = getParent();
          if (view != null)
            if (this.axis == 0) {
              bool1 = this.majorChanged;
              bool2 = this.minorChanged;
            } else {
              bool2 = this.majorChanged;
              bool1 = this.minorChanged;
            }  
          this.majorChanged = false;
          this.minorChanged = false;
        } 
      } 
      if (view != null) {
        view.preferenceChanged(this, bool1, bool2);
        Container container = getContainer();
        if (container != null)
          container.repaint(); 
      } 
    } finally {
      abstractDocument.readUnlock();
    } 
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView) {
    synchronized (this.stats) {
      for (byte b = 0; b < paramInt2; b++) {
        ChildState childState = (ChildState)this.stats.remove(paramInt1);
        float f = childState.getMajorSpan();
        childState.getChildView().setParent(null);
        if (f != 0.0F)
          majorRequirementChange(childState, -f); 
      } 
      LayoutQueue layoutQueue = getLayoutQueue();
      if (paramArrayOfView != null)
        for (int i = 0; i < paramArrayOfView.length; i++) {
          ChildState childState = createChildState(paramArrayOfView[i]);
          this.stats.add(paramInt1 + i, childState);
          layoutQueue.addTask(childState);
        }  
      layoutQueue.addTask(this.flushTask);
    } 
  }
  
  protected void loadChildren(ViewFactory paramViewFactory) {
    Element element = getElement();
    int i = element.getElementCount();
    if (i > 0) {
      View[] arrayOfView = new View[i];
      for (byte b = 0; b < i; b++)
        arrayOfView[b] = paramViewFactory.create(element.getElement(b)); 
      replace(0, 0, arrayOfView);
    } 
  }
  
  protected int getViewIndexAtPosition(int paramInt, Position.Bias paramBias) {
    boolean bool = (paramBias == Position.Bias.Backward) ? 1 : 0;
    paramInt = bool ? Math.max(0, paramInt - 1) : paramInt;
    Element element = getElement();
    return element.getElementIndex(paramInt);
  }
  
  protected void updateLayout(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape) {
    if (paramElementChange != null) {
      int i = Math.max(paramElementChange.getIndex() - 1, 0);
      ChildState childState = getChildState(i);
      this.locator.childChanged(childState);
    } 
  }
  
  public void setParent(View paramView) {
    super.setParent(paramView);
    if (paramView != null && getViewCount() == 0) {
      ViewFactory viewFactory = getViewFactory();
      loadChildren(viewFactory);
    } 
  }
  
  public void preferenceChanged(View paramView, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramView == null) {
      getParent().preferenceChanged(this, paramBoolean1, paramBoolean2);
    } else {
      if (this.changing != null) {
        View view = this.changing.getChildView();
        if (view == paramView) {
          this.changing.preferenceChanged(paramBoolean1, paramBoolean2);
          return;
        } 
      } 
      int i = getViewIndex(paramView.getStartOffset(), Position.Bias.Forward);
      ChildState childState = getChildState(i);
      childState.preferenceChanged(paramBoolean1, paramBoolean2);
      LayoutQueue layoutQueue = getLayoutQueue();
      layoutQueue.addTask(childState);
      layoutQueue.addTask(this.flushTask);
    } 
  }
  
  public void setSize(float paramFloat1, float paramFloat2) {
    setSpanOnAxis(0, paramFloat1);
    setSpanOnAxis(1, paramFloat2);
  }
  
  float getSpanOnAxis(int paramInt) { return (paramInt == getMajorAxis()) ? this.majorSpan : this.minorSpan; }
  
  void setSpanOnAxis(int paramInt, float paramFloat) {
    float f = getInsetSpan(paramInt);
    if (paramInt == getMinorAxis()) {
      float f1 = paramFloat - f;
      if (f1 != this.minorSpan) {
        this.minorSpan = f1;
        int i = getViewCount();
        if (i != 0) {
          LayoutQueue layoutQueue = getLayoutQueue();
          for (byte b = 0; b < i; b++) {
            ChildState childState;
            childState.childSizeValid = false;
            layoutQueue.addTask(childState);
          } 
          layoutQueue.addTask(this.flushTask);
        } 
      } 
    } else if (this.estimatedMajorSpan) {
      this.majorSpan = paramFloat - f;
    } 
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    synchronized (this.locator) {
      this.locator.setAllocation(paramShape);
      this.locator.paintChildren(paramGraphics);
    } 
  }
  
  public float getPreferredSpan(int paramInt) {
    float f = getInsetSpan(paramInt);
    if (paramInt == this.axis)
      return this.majorSpan + f; 
    if (this.prefRequest != null) {
      View view = this.prefRequest.getChildView();
      return view.getPreferredSpan(paramInt) + f;
    } 
    return f + 30.0F;
  }
  
  public float getMinimumSpan(int paramInt) {
    if (paramInt == this.axis)
      return getPreferredSpan(paramInt); 
    if (this.minRequest != null) {
      View view = this.minRequest.getChildView();
      return view.getMinimumSpan(paramInt);
    } 
    return (paramInt == 0) ? (getLeftInset() + getRightInset() + 5.0F) : (getTopInset() + getBottomInset() + 5.0F);
  }
  
  public float getMaximumSpan(int paramInt) { return (paramInt == this.axis) ? getPreferredSpan(paramInt) : 2.14748365E9F; }
  
  public int getViewCount() {
    synchronized (this.stats) {
      return this.stats.size();
    } 
  }
  
  public View getView(int paramInt) {
    ChildState childState = getChildState(paramInt);
    return (childState != null) ? childState.getChildView() : null;
  }
  
  public Shape getChildAllocation(int paramInt, Shape paramShape) { return this.locator.getChildAllocation(paramInt, paramShape); }
  
  public int getViewIndex(int paramInt, Position.Bias paramBias) { return getViewIndexAtPosition(paramInt, paramBias); }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException {
    int i = getViewIndex(paramInt, paramBias);
    Shape shape = this.locator.getChildAllocation(i, paramShape);
    ChildState childState = getChildState(i);
    synchronized (childState) {
      View view = childState.getChildView();
      return view.modelToView(paramInt, shape, paramBias);
    } 
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    Shape shape;
    int j;
    int i;
    synchronized (this.locator) {
      j = this.locator.getViewIndexAtPoint(paramFloat1, paramFloat2, paramShape);
      shape = this.locator.getChildAllocation(j, paramShape);
    } 
    ChildState childState = getChildState(j);
    synchronized (childState) {
      View view = childState.getChildView();
      i = view.viewToModel(paramFloat1, paramFloat2, shape, paramArrayOfBias);
    } 
    return i;
  }
  
  public int getNextVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    if (paramInt1 < -1)
      throw new BadLocationException("invalid position", paramInt1); 
    return Utilities.getNextVisualPositionFrom(this, paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
  }
  
  public class ChildLocator {
    protected AsyncBoxView.ChildState lastValidOffset;
    
    protected Rectangle lastAlloc = new Rectangle();
    
    protected Rectangle childAlloc = new Rectangle();
    
    public void childChanged(AsyncBoxView.ChildState param1ChildState) {
      if (this.lastValidOffset == null) {
        this.lastValidOffset = param1ChildState;
      } else if (param1ChildState.getChildView().getStartOffset() < this.lastValidOffset.getChildView().getStartOffset()) {
        this.lastValidOffset = param1ChildState;
      } 
    }
    
    public void paintChildren(Graphics param1Graphics) {
      Rectangle rectangle = param1Graphics.getClipBounds();
      float f1 = (AsyncBoxView.this.axis == 0) ? (rectangle.x - this.lastAlloc.x) : (rectangle.y - this.lastAlloc.y);
      int i = getViewIndexAtVisualOffset(f1);
      int j = AsyncBoxView.this.getViewCount();
      float f2 = AsyncBoxView.this.getChildState(i).getMajorOffset();
      int k = i;
      while (k < j) {
        AsyncBoxView.ChildState childState = AsyncBoxView.this.getChildState(k);
        childState.setMajorOffset(f2);
        Shape shape = getChildAllocation(k);
        if (intersectsClip(shape, rectangle)) {
          synchronized (childState) {
            View view = childState.getChildView();
            view.paint(param1Graphics, shape);
          } 
          f2 += childState.getMajorSpan();
          k++;
        } 
      } 
    }
    
    public Shape getChildAllocation(int param1Int, Shape param1Shape) {
      if (param1Shape == null)
        return null; 
      setAllocation(param1Shape);
      AsyncBoxView.ChildState childState = AsyncBoxView.this.getChildState(param1Int);
      if (this.lastValidOffset == null)
        this.lastValidOffset = AsyncBoxView.this.getChildState(0); 
      if (childState.getChildView().getStartOffset() > this.lastValidOffset.getChildView().getStartOffset())
        updateChildOffsetsToIndex(param1Int); 
      return getChildAllocation(param1Int);
    }
    
    public int getViewIndexAtPoint(float param1Float1, float param1Float2, Shape param1Shape) {
      setAllocation(param1Shape);
      float f = (AsyncBoxView.this.axis == 0) ? (param1Float1 - this.lastAlloc.x) : (param1Float2 - this.lastAlloc.y);
      return getViewIndexAtVisualOffset(f);
    }
    
    protected Shape getChildAllocation(int param1Int) {
      AsyncBoxView.ChildState childState = AsyncBoxView.this.getChildState(param1Int);
      if (!childState.isLayoutValid())
        childState.run(); 
      if (AsyncBoxView.this.axis == 0) {
        this.lastAlloc.x += (int)childState.getMajorOffset();
        this.lastAlloc.y += (int)childState.getMinorOffset();
        this.childAlloc.width = (int)childState.getMajorSpan();
        this.childAlloc.height = (int)childState.getMinorSpan();
      } else {
        this.lastAlloc.y += (int)childState.getMajorOffset();
        this.lastAlloc.x += (int)childState.getMinorOffset();
        this.childAlloc.height = (int)childState.getMajorSpan();
        this.childAlloc.width = (int)childState.getMinorSpan();
      } 
      this.childAlloc.x += (int)AsyncBoxView.this.getLeftInset();
      this.childAlloc.y += (int)AsyncBoxView.this.getRightInset();
      return this.childAlloc;
    }
    
    protected void setAllocation(Shape param1Shape) {
      if (param1Shape instanceof Rectangle) {
        this.lastAlloc.setBounds((Rectangle)param1Shape);
      } else {
        this.lastAlloc.setBounds(param1Shape.getBounds());
      } 
      AsyncBoxView.this.setSize(this.lastAlloc.width, this.lastAlloc.height);
    }
    
    protected int getViewIndexAtVisualOffset(float param1Float) {
      int i = AsyncBoxView.this.getViewCount();
      if (i > 0) {
        boolean bool = (this.lastValidOffset != null) ? 1 : 0;
        if (this.lastValidOffset == null)
          this.lastValidOffset = AsyncBoxView.this.getChildState(0); 
        if (param1Float > AsyncBoxView.this.majorSpan) {
          if (!bool)
            return 0; 
          int j = this.lastValidOffset.getChildView().getStartOffset();
          return AsyncBoxView.this.getViewIndex(j, Position.Bias.Forward);
        } 
        if (param1Float > this.lastValidOffset.getMajorOffset())
          return updateChildOffsets(param1Float); 
        float f = 0.0F;
        for (byte b = 0; b < i; b++) {
          AsyncBoxView.ChildState childState = AsyncBoxView.this.getChildState(b);
          float f1 = f + childState.getMajorSpan();
          if (param1Float < f1)
            return b; 
          f = f1;
        } 
      } 
      return i - 1;
    }
    
    int updateChildOffsets(float param1Float) {
      int i = AsyncBoxView.this.getViewCount();
      int j = i - 1;
      int k = this.lastValidOffset.getChildView().getStartOffset();
      int m = AsyncBoxView.this.getViewIndex(k, Position.Bias.Forward);
      float f1 = this.lastValidOffset.getMajorOffset();
      float f2 = f1;
      for (int n = m; n < i; n++) {
        AsyncBoxView.ChildState childState = AsyncBoxView.this.getChildState(n);
        childState.setMajorOffset(f2);
        f2 += childState.getMajorSpan();
        if (param1Float < f2) {
          j = n;
          this.lastValidOffset = childState;
          break;
        } 
      } 
      return j;
    }
    
    void updateChildOffsetsToIndex(int param1Int) {
      int i = this.lastValidOffset.getChildView().getStartOffset();
      int j = AsyncBoxView.this.getViewIndex(i, Position.Bias.Forward);
      float f = this.lastValidOffset.getMajorOffset();
      for (int k = j; k <= param1Int; k++) {
        AsyncBoxView.ChildState childState = AsyncBoxView.this.getChildState(k);
        childState.setMajorOffset(f);
        f += childState.getMajorSpan();
      } 
    }
    
    boolean intersectsClip(Shape param1Shape, Rectangle param1Rectangle) {
      Rectangle rectangle = (param1Shape instanceof Rectangle) ? (Rectangle)param1Shape : param1Shape.getBounds();
      return rectangle.intersects(param1Rectangle) ? this.lastAlloc.intersects(rectangle) : 0;
    }
  }
  
  public class ChildState implements Runnable {
    private float min;
    
    private float pref;
    
    private float max;
    
    private boolean minorValid;
    
    private float span;
    
    private float offset;
    
    private boolean majorValid;
    
    private View child;
    
    private boolean childSizeValid;
    
    public ChildState(View param1View) {
      this.child = param1View;
      this.minorValid = false;
      this.majorValid = false;
      this.childSizeValid = false;
      this.child.setParent(this$0);
    }
    
    public View getChildView() { return this.child; }
    
    public void run() {
      abstractDocument = (AbstractDocument)AsyncBoxView.this.getDocument();
      try {
        abstractDocument.readLock();
        if (this.minorValid && this.majorValid && this.childSizeValid)
          return; 
        if (this.child.getParent() == AsyncBoxView.this) {
          synchronized (AsyncBoxView.this) {
            AsyncBoxView.this.changing = this;
          } 
          updateChild();
          synchronized (AsyncBoxView.this) {
            AsyncBoxView.this.changing = null;
          } 
          updateChild();
        } 
      } finally {
        abstractDocument.readUnlock();
      } 
    }
    
    void updateChild() {
      boolean bool1 = false;
      synchronized (this) {
        if (!this.minorValid) {
          int i = AsyncBoxView.this.getMinorAxis();
          this.min = this.child.getMinimumSpan(i);
          this.pref = this.child.getPreferredSpan(i);
          this.max = this.child.getMaximumSpan(i);
          this.minorValid = true;
          bool1 = true;
        } 
      } 
      if (bool1)
        AsyncBoxView.this.minorRequirementChange(this); 
      boolean bool2 = false;
      float f = 0.0F;
      synchronized (this) {
        if (!this.majorValid) {
          float f1 = this.span;
          this.span = this.child.getPreferredSpan(AsyncBoxView.this.axis);
          f = this.span - f1;
          this.majorValid = true;
          bool2 = true;
        } 
      } 
      if (bool2) {
        AsyncBoxView.this.majorRequirementChange(this, f);
        AsyncBoxView.this.locator.childChanged(this);
      } 
      synchronized (this) {
        if (!this.childSizeValid) {
          float f2;
          float f1;
          if (AsyncBoxView.this.axis == 0) {
            f1 = this.span;
            f2 = getMinorSpan();
          } else {
            f1 = getMinorSpan();
            f2 = this.span;
          } 
          this.childSizeValid = true;
          this.child.setSize(f1, f2);
        } 
      } 
    }
    
    public float getMinorSpan() { return (this.max < AsyncBoxView.this.minorSpan) ? this.max : Math.max(this.min, AsyncBoxView.this.minorSpan); }
    
    public float getMinorOffset() {
      if (this.max < AsyncBoxView.this.minorSpan) {
        float f = this.child.getAlignment(AsyncBoxView.this.getMinorAxis());
        return (AsyncBoxView.this.minorSpan - this.max) * f;
      } 
      return 0.0F;
    }
    
    public float getMajorSpan() { return this.span; }
    
    public float getMajorOffset() { return this.offset; }
    
    public void setMajorOffset(float param1Float) { this.offset = param1Float; }
    
    public void preferenceChanged(boolean param1Boolean1, boolean param1Boolean2) {
      if (AsyncBoxView.this.axis == 0) {
        if (param1Boolean1)
          this.majorValid = false; 
        if (param1Boolean2)
          this.minorValid = false; 
      } else {
        if (param1Boolean1)
          this.minorValid = false; 
        if (param1Boolean2)
          this.majorValid = false; 
      } 
      this.childSizeValid = false;
    }
    
    public boolean isLayoutValid() { return (this.minorValid && this.majorValid && this.childSizeValid); }
  }
  
  class FlushTask implements Runnable {
    public void run() { AsyncBoxView.this.flushRequirementChanges(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\AsyncBoxView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */