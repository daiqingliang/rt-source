package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.plaf.SplitPaneUI;

public class JSplitPane extends JComponent implements Accessible {
  private static final String uiClassID = "SplitPaneUI";
  
  public static final int VERTICAL_SPLIT = 0;
  
  public static final int HORIZONTAL_SPLIT = 1;
  
  public static final String LEFT = "left";
  
  public static final String RIGHT = "right";
  
  public static final String TOP = "top";
  
  public static final String BOTTOM = "bottom";
  
  public static final String DIVIDER = "divider";
  
  public static final String ORIENTATION_PROPERTY = "orientation";
  
  public static final String CONTINUOUS_LAYOUT_PROPERTY = "continuousLayout";
  
  public static final String DIVIDER_SIZE_PROPERTY = "dividerSize";
  
  public static final String ONE_TOUCH_EXPANDABLE_PROPERTY = "oneTouchExpandable";
  
  public static final String LAST_DIVIDER_LOCATION_PROPERTY = "lastDividerLocation";
  
  public static final String DIVIDER_LOCATION_PROPERTY = "dividerLocation";
  
  public static final String RESIZE_WEIGHT_PROPERTY = "resizeWeight";
  
  protected int orientation;
  
  protected boolean continuousLayout;
  
  protected Component leftComponent;
  
  protected Component rightComponent;
  
  protected int dividerSize;
  
  private boolean dividerSizeSet = false;
  
  protected boolean oneTouchExpandable;
  
  private boolean oneTouchExpandableSet;
  
  protected int lastDividerLocation;
  
  private double resizeWeight;
  
  private int dividerLocation = -1;
  
  public JSplitPane() { this(1, UIManager.getBoolean("SplitPane.continuousLayout"), new JButton(UIManager.getString("SplitPane.leftButtonText")), new JButton(UIManager.getString("SplitPane.rightButtonText"))); }
  
  @ConstructorProperties({"orientation"})
  public JSplitPane(int paramInt) { this(paramInt, UIManager.getBoolean("SplitPane.continuousLayout")); }
  
  public JSplitPane(int paramInt, boolean paramBoolean) { this(paramInt, paramBoolean, null, null); }
  
  public JSplitPane(int paramInt, Component paramComponent1, Component paramComponent2) { this(paramInt, UIManager.getBoolean("SplitPane.continuousLayout"), paramComponent1, paramComponent2); }
  
  public JSplitPane(int paramInt, boolean paramBoolean, Component paramComponent1, Component paramComponent2) {
    setLayout(null);
    setUIProperty("opaque", Boolean.TRUE);
    this.orientation = paramInt;
    if (this.orientation != 1 && this.orientation != 0)
      throw new IllegalArgumentException("cannot create JSplitPane, orientation must be one of JSplitPane.HORIZONTAL_SPLIT or JSplitPane.VERTICAL_SPLIT"); 
    this.continuousLayout = paramBoolean;
    if (paramComponent1 != null)
      setLeftComponent(paramComponent1); 
    if (paramComponent2 != null)
      setRightComponent(paramComponent2); 
    updateUI();
  }
  
  public void setUI(SplitPaneUI paramSplitPaneUI) {
    if ((SplitPaneUI)this.ui != paramSplitPaneUI) {
      setUI(paramSplitPaneUI);
      revalidate();
    } 
  }
  
  public SplitPaneUI getUI() { return (SplitPaneUI)this.ui; }
  
  public void updateUI() {
    setUI((SplitPaneUI)UIManager.getUI(this));
    revalidate();
  }
  
  public String getUIClassID() { return "SplitPaneUI"; }
  
  public void setDividerSize(int paramInt) {
    int i = this.dividerSize;
    this.dividerSizeSet = true;
    if (i != paramInt) {
      this.dividerSize = paramInt;
      firePropertyChange("dividerSize", i, paramInt);
    } 
  }
  
  public int getDividerSize() { return this.dividerSize; }
  
  public void setLeftComponent(Component paramComponent) {
    if (paramComponent == null) {
      if (this.leftComponent != null) {
        remove(this.leftComponent);
        this.leftComponent = null;
      } 
    } else {
      add(paramComponent, "left");
    } 
  }
  
  public Component getLeftComponent() { return this.leftComponent; }
  
  public void setTopComponent(Component paramComponent) { setLeftComponent(paramComponent); }
  
  public Component getTopComponent() { return this.leftComponent; }
  
  public void setRightComponent(Component paramComponent) {
    if (paramComponent == null) {
      if (this.rightComponent != null) {
        remove(this.rightComponent);
        this.rightComponent = null;
      } 
    } else {
      add(paramComponent, "right");
    } 
  }
  
  public Component getRightComponent() { return this.rightComponent; }
  
  public void setBottomComponent(Component paramComponent) { setRightComponent(paramComponent); }
  
  public Component getBottomComponent() { return this.rightComponent; }
  
  public void setOneTouchExpandable(boolean paramBoolean) {
    boolean bool = this.oneTouchExpandable;
    this.oneTouchExpandable = paramBoolean;
    this.oneTouchExpandableSet = true;
    firePropertyChange("oneTouchExpandable", bool, paramBoolean);
    repaint();
  }
  
  public boolean isOneTouchExpandable() { return this.oneTouchExpandable; }
  
  public void setLastDividerLocation(int paramInt) {
    int i = this.lastDividerLocation;
    this.lastDividerLocation = paramInt;
    firePropertyChange("lastDividerLocation", i, paramInt);
  }
  
  public int getLastDividerLocation() { return this.lastDividerLocation; }
  
  public void setOrientation(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("JSplitPane: orientation must be one of JSplitPane.VERTICAL_SPLIT or JSplitPane.HORIZONTAL_SPLIT"); 
    int i = this.orientation;
    this.orientation = paramInt;
    firePropertyChange("orientation", i, paramInt);
  }
  
  public int getOrientation() { return this.orientation; }
  
  public void setContinuousLayout(boolean paramBoolean) {
    boolean bool = this.continuousLayout;
    this.continuousLayout = paramBoolean;
    firePropertyChange("continuousLayout", bool, paramBoolean);
  }
  
  public boolean isContinuousLayout() { return this.continuousLayout; }
  
  public void setResizeWeight(double paramDouble) {
    if (paramDouble < 0.0D || paramDouble > 1.0D)
      throw new IllegalArgumentException("JSplitPane weight must be between 0 and 1"); 
    double d = this.resizeWeight;
    this.resizeWeight = paramDouble;
    firePropertyChange("resizeWeight", d, paramDouble);
  }
  
  public double getResizeWeight() { return this.resizeWeight; }
  
  public void resetToPreferredSizes() {
    SplitPaneUI splitPaneUI = getUI();
    if (splitPaneUI != null)
      splitPaneUI.resetToPreferredSizes(this); 
  }
  
  public void setDividerLocation(double paramDouble) {
    if (paramDouble < 0.0D || paramDouble > 1.0D)
      throw new IllegalArgumentException("proportional location must be between 0.0 and 1.0."); 
    if (getOrientation() == 0) {
      setDividerLocation((int)((getHeight() - getDividerSize()) * paramDouble));
    } else {
      setDividerLocation((int)((getWidth() - getDividerSize()) * paramDouble));
    } 
  }
  
  public void setDividerLocation(int paramInt) {
    int i = this.dividerLocation;
    this.dividerLocation = paramInt;
    SplitPaneUI splitPaneUI = getUI();
    if (splitPaneUI != null)
      splitPaneUI.setDividerLocation(this, paramInt); 
    firePropertyChange("dividerLocation", i, paramInt);
    setLastDividerLocation(i);
  }
  
  public int getDividerLocation() { return this.dividerLocation; }
  
  public int getMinimumDividerLocation() {
    SplitPaneUI splitPaneUI = getUI();
    return (splitPaneUI != null) ? splitPaneUI.getMinimumDividerLocation(this) : -1;
  }
  
  public int getMaximumDividerLocation() {
    SplitPaneUI splitPaneUI = getUI();
    return (splitPaneUI != null) ? splitPaneUI.getMaximumDividerLocation(this) : -1;
  }
  
  public void remove(Component paramComponent) {
    if (paramComponent == this.leftComponent) {
      this.leftComponent = null;
    } else if (paramComponent == this.rightComponent) {
      this.rightComponent = null;
    } 
    super.remove(paramComponent);
    revalidate();
    repaint();
  }
  
  public void remove(int paramInt) {
    Component component = getComponent(paramInt);
    if (component == this.leftComponent) {
      this.leftComponent = null;
    } else if (component == this.rightComponent) {
      this.rightComponent = null;
    } 
    super.remove(paramInt);
    revalidate();
    repaint();
  }
  
  public void removeAll() {
    this.leftComponent = this.rightComponent = null;
    super.removeAll();
    revalidate();
    repaint();
  }
  
  public boolean isValidateRoot() { return true; }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    if (paramObject != null && !(paramObject instanceof String))
      throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)"); 
    if (paramObject == null)
      if (getLeftComponent() == null) {
        paramObject = "left";
      } else if (getRightComponent() == null) {
        paramObject = "right";
      }  
    if (paramObject != null && (paramObject.equals("left") || paramObject.equals("top"))) {
      Component component = getLeftComponent();
      if (component != null)
        remove(component); 
      this.leftComponent = paramComponent;
      paramInt = -1;
    } else if (paramObject != null && (paramObject.equals("right") || paramObject.equals("bottom"))) {
      Component component = getRightComponent();
      if (component != null)
        remove(component); 
      this.rightComponent = paramComponent;
      paramInt = -1;
    } else if (paramObject != null && paramObject.equals("divider")) {
      paramInt = -1;
    } 
    super.addImpl(paramComponent, paramObject, paramInt);
    revalidate();
    repaint();
  }
  
  protected void paintChildren(Graphics paramGraphics) {
    super.paintChildren(paramGraphics);
    SplitPaneUI splitPaneUI = getUI();
    if (splitPaneUI != null) {
      Graphics graphics = paramGraphics.create();
      splitPaneUI.finishedPaintingChildren(this, graphics);
      graphics.dispose();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("SplitPaneUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  void setUIProperty(String paramString, Object paramObject) {
    if (paramString == "dividerSize") {
      if (!this.dividerSizeSet) {
        setDividerSize(((Number)paramObject).intValue());
        this.dividerSizeSet = false;
      } 
    } else if (paramString == "oneTouchExpandable") {
      if (!this.oneTouchExpandableSet) {
        setOneTouchExpandable(((Boolean)paramObject).booleanValue());
        this.oneTouchExpandableSet = false;
      } 
    } else {
      super.setUIProperty(paramString, paramObject);
    } 
  }
  
  protected String paramString() {
    String str1 = (this.orientation == 1) ? "HORIZONTAL_SPLIT" : "VERTICAL_SPLIT";
    String str2 = this.continuousLayout ? "true" : "false";
    String str3 = this.oneTouchExpandable ? "true" : "false";
    return super.paramString() + ",continuousLayout=" + str2 + ",dividerSize=" + this.dividerSize + ",lastDividerLocation=" + this.lastDividerLocation + ",oneTouchExpandable=" + str3 + ",orientation=" + str1;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJSplitPane(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJSplitPane extends JComponent.AccessibleJComponent implements AccessibleValue {
    protected AccessibleJSplitPane() { super(JSplitPane.this); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (JSplitPane.this.getOrientation() == 0) {
        accessibleStateSet.add(AccessibleState.VERTICAL);
      } else {
        accessibleStateSet.add(AccessibleState.HORIZONTAL);
      } 
      return accessibleStateSet;
    }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public Number getCurrentAccessibleValue() { return Integer.valueOf(JSplitPane.this.getDividerLocation()); }
    
    public boolean setCurrentAccessibleValue(Number param1Number) {
      if (param1Number == null)
        return false; 
      JSplitPane.this.setDividerLocation(param1Number.intValue());
      return true;
    }
    
    public Number getMinimumAccessibleValue() { return Integer.valueOf(JSplitPane.this.getUI().getMinimumDividerLocation(JSplitPane.this)); }
    
    public Number getMaximumAccessibleValue() { return Integer.valueOf(JSplitPane.this.getUI().getMaximumDividerLocation(JSplitPane.this)); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.SPLIT_PANE; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JSplitPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */