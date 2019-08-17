package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.swing.plaf.ToolBarUI;
import javax.swing.plaf.UIResource;

public class JToolBar extends JComponent implements SwingConstants, Accessible {
  private static final String uiClassID = "ToolBarUI";
  
  private boolean paintBorder = true;
  
  private Insets margin = null;
  
  private boolean floatable = true;
  
  private int orientation = 0;
  
  public JToolBar() { this(0); }
  
  public JToolBar(int paramInt) { this(null, paramInt); }
  
  public JToolBar(String paramString) { this(paramString, 0); }
  
  public JToolBar(String paramString, int paramInt) {
    setName(paramString);
    checkOrientation(paramInt);
    this.orientation = paramInt;
    DefaultToolBarLayout defaultToolBarLayout = new DefaultToolBarLayout(paramInt);
    setLayout(defaultToolBarLayout);
    addPropertyChangeListener(defaultToolBarLayout);
    updateUI();
  }
  
  public ToolBarUI getUI() { return (ToolBarUI)this.ui; }
  
  public void setUI(ToolBarUI paramToolBarUI) { setUI(paramToolBarUI); }
  
  public void updateUI() {
    setUI((ToolBarUI)UIManager.getUI(this));
    if (getLayout() == null)
      setLayout(new DefaultToolBarLayout(getOrientation())); 
    invalidate();
  }
  
  public String getUIClassID() { return "ToolBarUI"; }
  
  public int getComponentIndex(Component paramComponent) {
    int i = getComponentCount();
    Component[] arrayOfComponent = getComponents();
    for (byte b = 0; b < i; b++) {
      Component component = arrayOfComponent[b];
      if (component == paramComponent)
        return b; 
    } 
    return -1;
  }
  
  public Component getComponentAtIndex(int paramInt) {
    int i = getComponentCount();
    if (paramInt >= 0 && paramInt < i) {
      Component[] arrayOfComponent = getComponents();
      return arrayOfComponent[paramInt];
    } 
    return null;
  }
  
  public void setMargin(Insets paramInsets) {
    Insets insets = this.margin;
    this.margin = paramInsets;
    firePropertyChange("margin", insets, paramInsets);
    revalidate();
    repaint();
  }
  
  public Insets getMargin() { return (this.margin == null) ? new Insets(0, 0, 0, 0) : this.margin; }
  
  public boolean isBorderPainted() { return this.paintBorder; }
  
  public void setBorderPainted(boolean paramBoolean) {
    if (this.paintBorder != paramBoolean) {
      boolean bool = this.paintBorder;
      this.paintBorder = paramBoolean;
      firePropertyChange("borderPainted", bool, paramBoolean);
      revalidate();
      repaint();
    } 
  }
  
  protected void paintBorder(Graphics paramGraphics) {
    if (isBorderPainted())
      super.paintBorder(paramGraphics); 
  }
  
  public boolean isFloatable() { return this.floatable; }
  
  public void setFloatable(boolean paramBoolean) {
    if (this.floatable != paramBoolean) {
      boolean bool = this.floatable;
      this.floatable = paramBoolean;
      firePropertyChange("floatable", bool, paramBoolean);
      revalidate();
      repaint();
    } 
  }
  
  public int getOrientation() { return this.orientation; }
  
  public void setOrientation(int paramInt) {
    checkOrientation(paramInt);
    if (this.orientation != paramInt) {
      int i = this.orientation;
      this.orientation = paramInt;
      firePropertyChange("orientation", i, paramInt);
      revalidate();
      repaint();
    } 
  }
  
  public void setRollover(boolean paramBoolean) { putClientProperty("JToolBar.isRollover", paramBoolean ? Boolean.TRUE : Boolean.FALSE); }
  
  public boolean isRollover() {
    Boolean bool = (Boolean)getClientProperty("JToolBar.isRollover");
    return (bool != null) ? bool.booleanValue() : 0;
  }
  
  private void checkOrientation(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
        return;
    } 
    throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
  }
  
  public void addSeparator() { addSeparator(null); }
  
  public void addSeparator(Dimension paramDimension) {
    Separator separator = new Separator(paramDimension);
    add(separator);
  }
  
  public JButton add(Action paramAction) {
    JButton jButton = createActionComponent(paramAction);
    jButton.setAction(paramAction);
    add(jButton);
    return jButton;
  }
  
  protected JButton createActionComponent(Action paramAction) {
    JButton jButton = new JButton() {
        protected PropertyChangeListener createActionPropertyChangeListener(Action param1Action) {
          PropertyChangeListener propertyChangeListener = JToolBar.this.createActionChangeListener(this);
          if (propertyChangeListener == null)
            propertyChangeListener = super.createActionPropertyChangeListener(param1Action); 
          return propertyChangeListener;
        }
      };
    if (paramAction != null && (paramAction.getValue("SmallIcon") != null || paramAction.getValue("SwingLargeIconKey") != null))
      jButton.setHideActionText(true); 
    jButton.setHorizontalTextPosition(0);
    jButton.setVerticalTextPosition(3);
    return jButton;
  }
  
  protected PropertyChangeListener createActionChangeListener(JButton paramJButton) { return null; }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    if (paramComponent instanceof Separator)
      if (getOrientation() == 1) {
        ((Separator)paramComponent).setOrientation(0);
      } else {
        ((Separator)paramComponent).setOrientation(1);
      }  
    super.addImpl(paramComponent, paramObject, paramInt);
    if (paramComponent instanceof JButton)
      ((JButton)paramComponent).setDefaultCapable(false); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ToolBarUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str1 = this.paintBorder ? "true" : "false";
    String str2 = (this.margin != null) ? this.margin.toString() : "";
    String str3 = this.floatable ? "true" : "false";
    String str4 = (this.orientation == 0) ? "HORIZONTAL" : "VERTICAL";
    return super.paramString() + ",floatable=" + str3 + ",margin=" + str2 + ",orientation=" + str4 + ",paintBorder=" + str1;
  }
  
  public void setLayout(LayoutManager paramLayoutManager) {
    LayoutManager layoutManager = getLayout();
    if (layoutManager instanceof PropertyChangeListener)
      removePropertyChangeListener((PropertyChangeListener)layoutManager); 
    super.setLayout(paramLayoutManager);
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJToolBar(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJToolBar extends JComponent.AccessibleJComponent {
    protected AccessibleJToolBar() { super(JToolBar.this); }
    
    public AccessibleStateSet getAccessibleStateSet() { return super.getAccessibleStateSet(); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.TOOL_BAR; }
  }
  
  private class DefaultToolBarLayout implements LayoutManager2, Serializable, PropertyChangeListener, UIResource {
    BoxLayout lm;
    
    DefaultToolBarLayout(int param1Int) {
      if (param1Int == 1) {
        this.lm = new BoxLayout(this$0, 3);
      } else {
        this.lm = new BoxLayout(this$0, 2);
      } 
    }
    
    public void addLayoutComponent(String param1String, Component param1Component) { this.lm.addLayoutComponent(param1String, param1Component); }
    
    public void addLayoutComponent(Component param1Component, Object param1Object) { this.lm.addLayoutComponent(param1Component, param1Object); }
    
    public void removeLayoutComponent(Component param1Component) { this.lm.removeLayoutComponent(param1Component); }
    
    public Dimension preferredLayoutSize(Container param1Container) { return this.lm.preferredLayoutSize(param1Container); }
    
    public Dimension minimumLayoutSize(Container param1Container) { return this.lm.minimumLayoutSize(param1Container); }
    
    public Dimension maximumLayoutSize(Container param1Container) { return this.lm.maximumLayoutSize(param1Container); }
    
    public void layoutContainer(Container param1Container) { this.lm.layoutContainer(param1Container); }
    
    public float getLayoutAlignmentX(Container param1Container) { return this.lm.getLayoutAlignmentX(param1Container); }
    
    public float getLayoutAlignmentY(Container param1Container) { return this.lm.getLayoutAlignmentY(param1Container); }
    
    public void invalidateLayout(Container param1Container) { this.lm.invalidateLayout(param1Container); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str.equals("orientation")) {
        int i = ((Integer)param1PropertyChangeEvent.getNewValue()).intValue();
        if (i == 1) {
          this.lm = new BoxLayout(JToolBar.this, 3);
        } else {
          this.lm = new BoxLayout(JToolBar.this, 2);
        } 
      } 
    }
  }
  
  public static class Separator extends JSeparator {
    private Dimension separatorSize;
    
    public Separator() { this(null); }
    
    public Separator(Dimension param1Dimension) {
      super(0);
      setSeparatorSize(param1Dimension);
    }
    
    public String getUIClassID() { return "ToolBarSeparatorUI"; }
    
    public void setSeparatorSize(Dimension param1Dimension) {
      if (param1Dimension != null) {
        this.separatorSize = param1Dimension;
      } else {
        updateUI();
      } 
      invalidate();
    }
    
    public Dimension getSeparatorSize() { return this.separatorSize; }
    
    public Dimension getMinimumSize() { return (this.separatorSize != null) ? this.separatorSize.getSize() : super.getMinimumSize(); }
    
    public Dimension getMaximumSize() { return (this.separatorSize != null) ? this.separatorSize.getSize() : super.getMaximumSize(); }
    
    public Dimension getPreferredSize() { return (this.separatorSize != null) ? this.separatorSize.getSize() : super.getPreferredSize(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JToolBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */