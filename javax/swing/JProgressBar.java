package javax.swing;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.NumberFormat;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ProgressBarUI;

public class JProgressBar extends JComponent implements SwingConstants, Accessible {
  private static final String uiClassID = "ProgressBarUI";
  
  protected int orientation;
  
  protected boolean paintBorder;
  
  protected BoundedRangeModel model;
  
  protected String progressString;
  
  protected boolean paintString;
  
  private static final int defaultMinimum = 0;
  
  private static final int defaultMaximum = 100;
  
  private static final int defaultOrientation = 0;
  
  protected ChangeEvent changeEvent = null;
  
  protected ChangeListener changeListener = null;
  
  private Format format;
  
  private boolean indeterminate;
  
  public JProgressBar() { this(0); }
  
  public JProgressBar(int paramInt) { this(paramInt, 0, 100); }
  
  public JProgressBar(int paramInt1, int paramInt2) { this(0, paramInt1, paramInt2); }
  
  public JProgressBar(int paramInt1, int paramInt2, int paramInt3) {
    setModel(new DefaultBoundedRangeModel(paramInt2, 0, paramInt2, paramInt3));
    updateUI();
    setOrientation(paramInt1);
    setBorderPainted(true);
    setStringPainted(false);
    setString(null);
    setIndeterminate(false);
  }
  
  public JProgressBar(BoundedRangeModel paramBoundedRangeModel) {
    setModel(paramBoundedRangeModel);
    updateUI();
    setOrientation(0);
    setBorderPainted(true);
    setStringPainted(false);
    setString(null);
    setIndeterminate(false);
  }
  
  public int getOrientation() { return this.orientation; }
  
  public void setOrientation(int paramInt) {
    if (this.orientation != paramInt) {
      int i;
      switch (paramInt) {
        case 0:
        case 1:
          i = this.orientation;
          this.orientation = paramInt;
          firePropertyChange("orientation", i, paramInt);
          if (this.accessibleContext != null)
            this.accessibleContext.firePropertyChange("AccessibleState", (i == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL, (this.orientation == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL); 
          break;
        default:
          throw new IllegalArgumentException(paramInt + " is not a legal orientation");
      } 
      revalidate();
    } 
  }
  
  public boolean isStringPainted() { return this.paintString; }
  
  public void setStringPainted(boolean paramBoolean) {
    boolean bool = this.paintString;
    this.paintString = paramBoolean;
    firePropertyChange("stringPainted", bool, this.paintString);
    if (this.paintString != bool) {
      revalidate();
      repaint();
    } 
  }
  
  public String getString() {
    if (this.progressString != null)
      return this.progressString; 
    if (this.format == null)
      this.format = NumberFormat.getPercentInstance(); 
    return this.format.format(new Double(getPercentComplete()));
  }
  
  public void setString(String paramString) {
    String str = this.progressString;
    this.progressString = paramString;
    firePropertyChange("string", str, this.progressString);
    if (this.progressString == null || str == null || !this.progressString.equals(str))
      repaint(); 
  }
  
  public double getPercentComplete() {
    long l = (this.model.getMaximum() - this.model.getMinimum());
    double d = this.model.getValue();
    return (d - this.model.getMinimum()) / l;
  }
  
  public boolean isBorderPainted() { return this.paintBorder; }
  
  public void setBorderPainted(boolean paramBoolean) {
    boolean bool = this.paintBorder;
    this.paintBorder = paramBoolean;
    firePropertyChange("borderPainted", bool, this.paintBorder);
    if (this.paintBorder != bool)
      repaint(); 
  }
  
  protected void paintBorder(Graphics paramGraphics) {
    if (isBorderPainted())
      super.paintBorder(paramGraphics); 
  }
  
  public ProgressBarUI getUI() { return (ProgressBarUI)this.ui; }
  
  public void setUI(ProgressBarUI paramProgressBarUI) { setUI(paramProgressBarUI); }
  
  public void updateUI() { setUI((ProgressBarUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "ProgressBarUI"; }
  
  protected ChangeListener createChangeListener() { return new ModelListener(null); }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.listenerList.add(ChangeListener.class, paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.listenerList.remove(ChangeListener.class, paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
  
  protected void fireStateChanged() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((ChangeListener)arrayOfObject[i + 1]).stateChanged(this.changeEvent);
      } 
    } 
  }
  
  public BoundedRangeModel getModel() { return this.model; }
  
  public void setModel(BoundedRangeModel paramBoundedRangeModel) {
    BoundedRangeModel boundedRangeModel = getModel();
    if (paramBoundedRangeModel != boundedRangeModel) {
      if (boundedRangeModel != null) {
        boundedRangeModel.removeChangeListener(this.changeListener);
        this.changeListener = null;
      } 
      this.model = paramBoundedRangeModel;
      if (paramBoundedRangeModel != null) {
        this.changeListener = createChangeListener();
        paramBoundedRangeModel.addChangeListener(this.changeListener);
      } 
      if (this.accessibleContext != null)
        this.accessibleContext.firePropertyChange("AccessibleValue", (boundedRangeModel == null) ? null : Integer.valueOf(boundedRangeModel.getValue()), (paramBoundedRangeModel == null) ? null : Integer.valueOf(paramBoundedRangeModel.getValue())); 
      if (this.model != null)
        this.model.setExtent(0); 
      repaint();
    } 
  }
  
  public int getValue() { return getModel().getValue(); }
  
  public int getMinimum() { return getModel().getMinimum(); }
  
  public int getMaximum() { return getModel().getMaximum(); }
  
  public void setValue(int paramInt) {
    BoundedRangeModel boundedRangeModel = getModel();
    int i = boundedRangeModel.getValue();
    boundedRangeModel.setValue(paramInt);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(boundedRangeModel.getValue())); 
  }
  
  public void setMinimum(int paramInt) { getModel().setMinimum(paramInt); }
  
  public void setMaximum(int paramInt) { getModel().setMaximum(paramInt); }
  
  public void setIndeterminate(boolean paramBoolean) {
    boolean bool = this.indeterminate;
    this.indeterminate = paramBoolean;
    firePropertyChange("indeterminate", bool, this.indeterminate);
  }
  
  public boolean isIndeterminate() { return this.indeterminate; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ProgressBarUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str1 = (this.orientation == 0) ? "HORIZONTAL" : "VERTICAL";
    String str2 = this.paintBorder ? "true" : "false";
    String str3 = (this.progressString != null) ? this.progressString : "";
    String str4 = this.paintString ? "true" : "false";
    String str5 = this.indeterminate ? "true" : "false";
    return super.paramString() + ",orientation=" + str1 + ",paintBorder=" + str2 + ",paintString=" + str4 + ",progressString=" + str3 + ",indeterminateString=" + str5;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJProgressBar(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJProgressBar extends JComponent.AccessibleJComponent implements AccessibleValue {
    protected AccessibleJProgressBar() { super(JProgressBar.this); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (JProgressBar.this.getModel().getValueIsAdjusting())
        accessibleStateSet.add(AccessibleState.BUSY); 
      if (JProgressBar.this.getOrientation() == 1) {
        accessibleStateSet.add(AccessibleState.VERTICAL);
      } else {
        accessibleStateSet.add(AccessibleState.HORIZONTAL);
      } 
      return accessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.PROGRESS_BAR; }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public Number getCurrentAccessibleValue() { return Integer.valueOf(JProgressBar.this.getValue()); }
    
    public boolean setCurrentAccessibleValue(Number param1Number) {
      if (param1Number == null)
        return false; 
      JProgressBar.this.setValue(param1Number.intValue());
      return true;
    }
    
    public Number getMinimumAccessibleValue() { return Integer.valueOf(JProgressBar.this.getMinimum()); }
    
    public Number getMaximumAccessibleValue() { return Integer.valueOf(JProgressBar.this.model.getMaximum() - JProgressBar.this.model.getExtent()); }
  }
  
  private class ModelListener implements ChangeListener, Serializable {
    private ModelListener() {}
    
    public void stateChanged(ChangeEvent param1ChangeEvent) { JProgressBar.this.fireStateChanged(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JProgressBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */