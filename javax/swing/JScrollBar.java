package javax.swing;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ScrollBarUI;

public class JScrollBar extends JComponent implements Adjustable, Accessible {
  private static final String uiClassID = "ScrollBarUI";
  
  private ChangeListener fwdAdjustmentEvents = new ModelListener(null);
  
  protected BoundedRangeModel model;
  
  protected int orientation;
  
  protected int unitIncrement;
  
  protected int blockIncrement;
  
  private void checkOrientation(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
        return;
    } 
    throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
  }
  
  public JScrollBar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    checkOrientation(paramInt1);
    this.unitIncrement = 1;
    this.blockIncrement = (paramInt3 == 0) ? 1 : paramInt3;
    this.orientation = paramInt1;
    this.model = new DefaultBoundedRangeModel(paramInt2, paramInt3, paramInt4, paramInt5);
    this.model.addChangeListener(this.fwdAdjustmentEvents);
    setRequestFocusEnabled(false);
    updateUI();
  }
  
  public JScrollBar(int paramInt) { this(paramInt, 0, 10, 0, 100); }
  
  public JScrollBar() { this(1); }
  
  public void setUI(ScrollBarUI paramScrollBarUI) { setUI(paramScrollBarUI); }
  
  public ScrollBarUI getUI() { return (ScrollBarUI)this.ui; }
  
  public void updateUI() { setUI((ScrollBarUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "ScrollBarUI"; }
  
  public int getOrientation() { return this.orientation; }
  
  public void setOrientation(int paramInt) {
    checkOrientation(paramInt);
    int i = this.orientation;
    this.orientation = paramInt;
    firePropertyChange("orientation", i, paramInt);
    if (i != paramInt && this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleState", (i == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL, (paramInt == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL); 
    if (paramInt != i)
      revalidate(); 
  }
  
  public BoundedRangeModel getModel() { return this.model; }
  
  public void setModel(BoundedRangeModel paramBoundedRangeModel) {
    Integer integer = null;
    BoundedRangeModel boundedRangeModel = this.model;
    if (this.model != null) {
      this.model.removeChangeListener(this.fwdAdjustmentEvents);
      integer = Integer.valueOf(this.model.getValue());
    } 
    this.model = paramBoundedRangeModel;
    if (this.model != null)
      this.model.addChangeListener(this.fwdAdjustmentEvents); 
    firePropertyChange("model", boundedRangeModel, this.model);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleValue", integer, new Integer(this.model.getValue())); 
  }
  
  public int getUnitIncrement(int paramInt) { return this.unitIncrement; }
  
  public void setUnitIncrement(int paramInt) {
    int i = this.unitIncrement;
    this.unitIncrement = paramInt;
    firePropertyChange("unitIncrement", i, paramInt);
  }
  
  public int getBlockIncrement(int paramInt) { return this.blockIncrement; }
  
  public void setBlockIncrement(int paramInt) {
    int i = this.blockIncrement;
    this.blockIncrement = paramInt;
    firePropertyChange("blockIncrement", i, paramInt);
  }
  
  public int getUnitIncrement() { return this.unitIncrement; }
  
  public int getBlockIncrement() { return this.blockIncrement; }
  
  public int getValue() { return getModel().getValue(); }
  
  public void setValue(int paramInt) {
    BoundedRangeModel boundedRangeModel = getModel();
    int i = boundedRangeModel.getValue();
    boundedRangeModel.setValue(paramInt);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(boundedRangeModel.getValue())); 
  }
  
  public int getVisibleAmount() { return getModel().getExtent(); }
  
  public void setVisibleAmount(int paramInt) { getModel().setExtent(paramInt); }
  
  public int getMinimum() { return getModel().getMinimum(); }
  
  public void setMinimum(int paramInt) { getModel().setMinimum(paramInt); }
  
  public int getMaximum() { return getModel().getMaximum(); }
  
  public void setMaximum(int paramInt) { getModel().setMaximum(paramInt); }
  
  public boolean getValueIsAdjusting() { return getModel().getValueIsAdjusting(); }
  
  public void setValueIsAdjusting(boolean paramBoolean) {
    BoundedRangeModel boundedRangeModel = getModel();
    boolean bool = boundedRangeModel.getValueIsAdjusting();
    boundedRangeModel.setValueIsAdjusting(paramBoolean);
    if (bool != paramBoolean && this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleState", bool ? AccessibleState.BUSY : null, paramBoolean ? AccessibleState.BUSY : null); 
  }
  
  public void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    BoundedRangeModel boundedRangeModel = getModel();
    int i = boundedRangeModel.getValue();
    boundedRangeModel.setRangeProperties(paramInt1, paramInt2, paramInt3, paramInt4, boundedRangeModel.getValueIsAdjusting());
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(boundedRangeModel.getValue())); 
  }
  
  public void addAdjustmentListener(AdjustmentListener paramAdjustmentListener) { this.listenerList.add(AdjustmentListener.class, paramAdjustmentListener); }
  
  public void removeAdjustmentListener(AdjustmentListener paramAdjustmentListener) { this.listenerList.remove(AdjustmentListener.class, paramAdjustmentListener); }
  
  public AdjustmentListener[] getAdjustmentListeners() { return (AdjustmentListener[])this.listenerList.getListeners(AdjustmentListener.class); }
  
  protected void fireAdjustmentValueChanged(int paramInt1, int paramInt2, int paramInt3) { fireAdjustmentValueChanged(paramInt1, paramInt2, paramInt3, getValueIsAdjusting()); }
  
  private void fireAdjustmentValueChanged(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    AdjustmentEvent adjustmentEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == AdjustmentListener.class) {
        if (adjustmentEvent == null)
          adjustmentEvent = new AdjustmentEvent(this, paramInt1, paramInt2, paramInt3, paramBoolean); 
        ((AdjustmentListener)arrayOfObject[i + 1]).adjustmentValueChanged(adjustmentEvent);
      } 
    } 
  }
  
  public Dimension getMinimumSize() {
    Dimension dimension = getPreferredSize();
    return (this.orientation == 1) ? new Dimension(dimension.width, 5) : new Dimension(5, dimension.height);
  }
  
  public Dimension getMaximumSize() {
    Dimension dimension = getPreferredSize();
    return (getOrientation() == 1) ? new Dimension(dimension.width, 32767) : new Dimension(32767, dimension.height);
  }
  
  public void setEnabled(boolean paramBoolean) {
    super.setEnabled(paramBoolean);
    Component[] arrayOfComponent = getComponents();
    for (Component component : arrayOfComponent)
      component.setEnabled(paramBoolean); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ScrollBarUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str = (this.orientation == 0) ? "HORIZONTAL" : "VERTICAL";
    return super.paramString() + ",blockIncrement=" + this.blockIncrement + ",orientation=" + str + ",unitIncrement=" + this.unitIncrement;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJScrollBar(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJScrollBar extends JComponent.AccessibleJComponent implements AccessibleValue {
    protected AccessibleJScrollBar() { super(JScrollBar.this); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (JScrollBar.this.getValueIsAdjusting())
        accessibleStateSet.add(AccessibleState.BUSY); 
      if (JScrollBar.this.getOrientation() == 1) {
        accessibleStateSet.add(AccessibleState.VERTICAL);
      } else {
        accessibleStateSet.add(AccessibleState.HORIZONTAL);
      } 
      return accessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.SCROLL_BAR; }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public Number getCurrentAccessibleValue() { return Integer.valueOf(JScrollBar.this.getValue()); }
    
    public boolean setCurrentAccessibleValue(Number param1Number) {
      if (param1Number == null)
        return false; 
      JScrollBar.this.setValue(param1Number.intValue());
      return true;
    }
    
    public Number getMinimumAccessibleValue() { return Integer.valueOf(JScrollBar.this.getMinimum()); }
    
    public Number getMaximumAccessibleValue() { return new Integer(JScrollBar.this.model.getMaximum() - JScrollBar.this.model.getExtent()); }
  }
  
  private class ModelListener implements ChangeListener, Serializable {
    private ModelListener() {}
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      Object object = param1ChangeEvent.getSource();
      if (object instanceof BoundedRangeModel) {
        char c = 'ə';
        byte b = 5;
        BoundedRangeModel boundedRangeModel = (BoundedRangeModel)object;
        int i = boundedRangeModel.getValue();
        boolean bool = boundedRangeModel.getValueIsAdjusting();
        JScrollBar.this.fireAdjustmentValueChanged(c, b, i, bool);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JScrollBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */