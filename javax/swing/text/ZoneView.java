package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.event.DocumentEvent;

public class ZoneView extends BoxView {
  int maxZoneSize = 8192;
  
  int maxZonesLoaded = 3;
  
  Vector<View> loadedZones = new Vector();
  
  public ZoneView(Element paramElement, int paramInt) { super(paramElement, paramInt); }
  
  public int getMaximumZoneSize() { return this.maxZoneSize; }
  
  public void setMaximumZoneSize(int paramInt) { this.maxZoneSize = paramInt; }
  
  public int getMaxZonesLoaded() { return this.maxZonesLoaded; }
  
  public void setMaxZonesLoaded(int paramInt) {
    if (paramInt < 1)
      throw new IllegalArgumentException("ZoneView.setMaxZonesLoaded must be greater than 0."); 
    this.maxZonesLoaded = paramInt;
    unloadOldZones();
  }
  
  protected void zoneWasLoaded(View paramView) {
    this.loadedZones.addElement(paramView);
    unloadOldZones();
  }
  
  void unloadOldZones() {
    while (this.loadedZones.size() > getMaxZonesLoaded()) {
      View view = (View)this.loadedZones.elementAt(0);
      this.loadedZones.removeElementAt(0);
      unloadZone(view);
    } 
  }
  
  protected void unloadZone(View paramView) { paramView.removeAll(); }
  
  protected boolean isZoneLoaded(View paramView) { return (paramView.getViewCount() > 0); }
  
  protected View createZone(int paramInt1, int paramInt2) {
    Zone zone;
    Document document = getDocument();
    try {
      zone = new Zone(getElement(), document.createPosition(paramInt1), document.createPosition(paramInt2));
    } catch (BadLocationException badLocationException) {
      throw new StateInvariantError(badLocationException.getMessage());
    } 
    return zone;
  }
  
  protected void loadChildren(ViewFactory paramViewFactory) {
    Document document = getDocument();
    int i = getStartOffset();
    int j = getEndOffset();
    append(createZone(i, j));
    handleInsert(i, j - i);
  }
  
  protected int getViewIndexAtPosition(int paramInt) {
    int i = getViewCount();
    if (paramInt == getEndOffset())
      return i - 1; 
    for (byte b = 0; b < i; b++) {
      View view = getView(b);
      if (paramInt >= view.getStartOffset() && paramInt < view.getEndOffset())
        return b; 
    } 
    return -1;
  }
  
  void handleInsert(int paramInt1, int paramInt2) {
    int i = getViewIndex(paramInt1, Position.Bias.Forward);
    View view = getView(i);
    int j = view.getStartOffset();
    int k = view.getEndOffset();
    if (k - j > this.maxZoneSize)
      splitZone(i, j, k); 
  }
  
  void handleRemove(int paramInt1, int paramInt2) {}
  
  void splitZone(int paramInt1, int paramInt2, int paramInt3) {
    Element element = getElement();
    Document document = element.getDocument();
    Vector vector = new Vector();
    int i = paramInt2;
    do {
      paramInt2 = i;
      i = Math.min(getDesiredZoneEnd(paramInt2), paramInt3);
      vector.addElement(createZone(paramInt2, i));
    } while (i < paramInt3);
    View view = getView(paramInt1);
    View[] arrayOfView = new View[vector.size()];
    vector.copyInto(arrayOfView);
    replace(paramInt1, 1, arrayOfView);
  }
  
  int getDesiredZoneEnd(int paramInt) {
    Element element1 = getElement();
    int i = element1.getElementIndex(paramInt + this.maxZoneSize / 2);
    Element element2 = element1.getElement(i);
    int j = element2.getStartOffset();
    int k = element2.getEndOffset();
    return (k - paramInt > this.maxZoneSize && j > paramInt) ? j : k;
  }
  
  protected boolean updateChildren(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, ViewFactory paramViewFactory) { return false; }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    handleInsert(paramDocumentEvent.getOffset(), paramDocumentEvent.getLength());
    super.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    handleRemove(paramDocumentEvent.getOffset(), paramDocumentEvent.getLength());
    super.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  class Zone extends AsyncBoxView {
    private Position start;
    
    private Position end;
    
    public Zone(Element param1Element, Position param1Position1, Position param1Position2) {
      super(param1Element, this$0.getAxis());
      this.start = param1Position1;
      this.end = param1Position2;
    }
    
    public void load() {
      if (!isLoaded()) {
        setEstimatedMajorSpan(true);
        Element element = getElement();
        ViewFactory viewFactory = getViewFactory();
        int i = element.getElementIndex(getStartOffset());
        int j = element.getElementIndex(getEndOffset());
        View[] arrayOfView = new View[j - i + 1];
        for (int k = i; k <= j; k++)
          arrayOfView[k - i] = viewFactory.create(element.getElement(k)); 
        replace(0, 0, arrayOfView);
        ZoneView.this.zoneWasLoaded(this);
      } 
    }
    
    public void unload() {
      setEstimatedMajorSpan(true);
      removeAll();
    }
    
    public boolean isLoaded() { return (getViewCount() != 0); }
    
    protected void loadChildren(ViewFactory param1ViewFactory) {
      setEstimatedMajorSpan(true);
      Element element = getElement();
      int i = element.getElementIndex(getStartOffset());
      int j = element.getElementIndex(getEndOffset());
      int k = j - i;
      View view = param1ViewFactory.create(element.getElement(i));
      view.setParent(this);
      float f1 = view.getPreferredSpan(0);
      float f2 = view.getPreferredSpan(1);
      if (getMajorAxis() == 0) {
        f1 *= k;
      } else {
        f2 += k;
      } 
      setSize(f1, f2);
    }
    
    protected void flushRequirementChanges() {
      if (isLoaded())
        super.flushRequirementChanges(); 
    }
    
    public int getViewIndex(int param1Int, Position.Bias param1Bias) {
      boolean bool = (param1Bias == Position.Bias.Backward) ? 1 : 0;
      param1Int = bool ? Math.max(0, param1Int - 1) : param1Int;
      Element element = getElement();
      int i = element.getElementIndex(param1Int);
      int j = element.getElementIndex(getStartOffset());
      return i - j;
    }
    
    protected boolean updateChildren(DocumentEvent.ElementChange param1ElementChange, DocumentEvent param1DocumentEvent, ViewFactory param1ViewFactory) {
      Element[] arrayOfElement1 = param1ElementChange.getChildrenRemoved();
      Element[] arrayOfElement2 = param1ElementChange.getChildrenAdded();
      Element element = getElement();
      int i = element.getElementIndex(getStartOffset());
      int j = element.getElementIndex(getEndOffset() - 1);
      int k = param1ElementChange.getIndex();
      if (k >= i && k <= j) {
        int m = k - i;
        int n = Math.min(j - i + 1, arrayOfElement2.length);
        int i1 = Math.min(j - i + 1, arrayOfElement1.length);
        View[] arrayOfView = new View[n];
        for (byte b = 0; b < n; b++)
          arrayOfView[b] = param1ViewFactory.create(arrayOfElement2[b]); 
        replace(m, i1, arrayOfView);
      } 
      return true;
    }
    
    public AttributeSet getAttributes() { return ZoneView.this.getAttributes(); }
    
    public void paint(Graphics param1Graphics, Shape param1Shape) {
      load();
      super.paint(param1Graphics, param1Shape);
    }
    
    public int viewToModel(float param1Float1, float param1Float2, Shape param1Shape, Position.Bias[] param1ArrayOfBias) {
      load();
      return super.viewToModel(param1Float1, param1Float2, param1Shape, param1ArrayOfBias);
    }
    
    public Shape modelToView(int param1Int, Shape param1Shape, Position.Bias param1Bias) throws BadLocationException {
      load();
      return super.modelToView(param1Int, param1Shape, param1Bias);
    }
    
    public int getStartOffset() { return this.start.getOffset(); }
    
    public int getEndOffset() { return this.end.getOffset(); }
    
    public void insertUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      if (isLoaded())
        super.insertUpdate(param1DocumentEvent, param1Shape, param1ViewFactory); 
    }
    
    public void removeUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      if (isLoaded())
        super.removeUpdate(param1DocumentEvent, param1Shape, param1ViewFactory); 
    }
    
    public void changedUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      if (isLoaded())
        super.changedUpdate(param1DocumentEvent, param1Shape, param1ViewFactory); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\ZoneView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */