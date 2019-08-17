package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;
import sun.swing.SwingUtilities2;

public class JTabbedPane extends JComponent implements Serializable, Accessible, SwingConstants {
  public static final int WRAP_TAB_LAYOUT = 0;
  
  public static final int SCROLL_TAB_LAYOUT = 1;
  
  private static final String uiClassID = "TabbedPaneUI";
  
  protected int tabPlacement = 1;
  
  private int tabLayoutPolicy;
  
  protected SingleSelectionModel model;
  
  private boolean haveRegistered;
  
  protected ChangeListener changeListener = null;
  
  private final List<Page> pages;
  
  private Component visComp = null;
  
  protected ChangeEvent changeEvent = null;
  
  public JTabbedPane() { this(1, 0); }
  
  public JTabbedPane(int paramInt) { this(paramInt, 0); }
  
  public JTabbedPane(int paramInt1, int paramInt2) {
    setTabPlacement(paramInt1);
    setTabLayoutPolicy(paramInt2);
    this.pages = new ArrayList(1);
    setModel(new DefaultSingleSelectionModel());
    updateUI();
  }
  
  public TabbedPaneUI getUI() { return (TabbedPaneUI)this.ui; }
  
  public void setUI(TabbedPaneUI paramTabbedPaneUI) {
    setUI(paramTabbedPaneUI);
    for (byte b = 0; b < getTabCount(); b++) {
      Icon icon = ((Page)this.pages.get(b)).disabledIcon;
      if (icon instanceof javax.swing.plaf.UIResource)
        setDisabledIconAt(b, null); 
    } 
  }
  
  public void updateUI() { setUI((TabbedPaneUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "TabbedPaneUI"; }
  
  protected ChangeListener createChangeListener() { return new ModelListener(); }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.listenerList.add(ChangeListener.class, paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.listenerList.remove(ChangeListener.class, paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
  
  protected void fireStateChanged() {
    int i = getSelectedIndex();
    if (i < 0) {
      if (this.visComp != null && this.visComp.isVisible())
        this.visComp.setVisible(false); 
      this.visComp = null;
    } else {
      Component component = getComponentAt(i);
      if (component != null && component != this.visComp) {
        boolean bool = false;
        if (this.visComp != null) {
          bool = (SwingUtilities.findFocusOwner(this.visComp) != null) ? 1 : 0;
          if (this.visComp.isVisible())
            this.visComp.setVisible(false); 
        } 
        if (!component.isVisible())
          component.setVisible(true); 
        if (bool)
          SwingUtilities2.tabbedPaneChangeFocusTo(component); 
        this.visComp = component;
      } 
    } 
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int j = arrayOfObject.length - 2; j >= 0; j -= 2) {
      if (arrayOfObject[j] == ChangeListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((ChangeListener)arrayOfObject[j + 1]).stateChanged(this.changeEvent);
      } 
    } 
  }
  
  public SingleSelectionModel getModel() { return this.model; }
  
  public void setModel(SingleSelectionModel paramSingleSelectionModel) {
    SingleSelectionModel singleSelectionModel = getModel();
    if (singleSelectionModel != null) {
      singleSelectionModel.removeChangeListener(this.changeListener);
      this.changeListener = null;
    } 
    this.model = paramSingleSelectionModel;
    if (paramSingleSelectionModel != null) {
      this.changeListener = createChangeListener();
      paramSingleSelectionModel.addChangeListener(this.changeListener);
    } 
    firePropertyChange("model", singleSelectionModel, paramSingleSelectionModel);
    repaint();
  }
  
  public int getTabPlacement() { return this.tabPlacement; }
  
  public void setTabPlacement(int paramInt) {
    if (paramInt != 1 && paramInt != 2 && paramInt != 3 && paramInt != 4)
      throw new IllegalArgumentException("illegal tab placement: must be TOP, BOTTOM, LEFT, or RIGHT"); 
    if (this.tabPlacement != paramInt) {
      int i = this.tabPlacement;
      this.tabPlacement = paramInt;
      firePropertyChange("tabPlacement", i, paramInt);
      revalidate();
      repaint();
    } 
  }
  
  public int getTabLayoutPolicy() { return this.tabLayoutPolicy; }
  
  public void setTabLayoutPolicy(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("illegal tab layout policy: must be WRAP_TAB_LAYOUT or SCROLL_TAB_LAYOUT"); 
    if (this.tabLayoutPolicy != paramInt) {
      int i = this.tabLayoutPolicy;
      this.tabLayoutPolicy = paramInt;
      firePropertyChange("tabLayoutPolicy", i, paramInt);
      revalidate();
      repaint();
    } 
  }
  
  @Transient
  public int getSelectedIndex() { return this.model.getSelectedIndex(); }
  
  public void setSelectedIndex(int paramInt) {
    if (paramInt != -1)
      checkIndex(paramInt); 
    setSelectedIndexImpl(paramInt, true);
  }
  
  private void setSelectedIndexImpl(int paramInt, boolean paramBoolean) {
    int i = this.model.getSelectedIndex();
    Page page1 = null;
    Page page2 = null;
    String str = null;
    paramBoolean = (paramBoolean && i != paramInt);
    if (paramBoolean) {
      if (this.accessibleContext != null)
        str = this.accessibleContext.getAccessibleName(); 
      if (i >= 0)
        page1 = (Page)this.pages.get(i); 
      if (paramInt >= 0)
        page2 = (Page)this.pages.get(paramInt); 
    } 
    this.model.setSelectedIndex(paramInt);
    if (paramBoolean)
      changeAccessibleSelection(page1, str, page2); 
  }
  
  private void changeAccessibleSelection(Page paramPage1, String paramString, Page paramPage2) {
    if (this.accessibleContext == null)
      return; 
    if (paramPage1 != null)
      paramPage1.firePropertyChange("AccessibleState", AccessibleState.SELECTED, null); 
    if (paramPage2 != null)
      paramPage2.firePropertyChange("AccessibleState", null, AccessibleState.SELECTED); 
    this.accessibleContext.firePropertyChange("AccessibleName", paramString, this.accessibleContext.getAccessibleName());
  }
  
  @Transient
  public Component getSelectedComponent() {
    int i = getSelectedIndex();
    return (i == -1) ? null : getComponentAt(i);
  }
  
  public void setSelectedComponent(Component paramComponent) {
    int i = indexOfComponent(paramComponent);
    if (i != -1) {
      setSelectedIndex(i);
    } else {
      throw new IllegalArgumentException("component not found in tabbed pane");
    } 
  }
  
  public void insertTab(String paramString1, Icon paramIcon, Component paramComponent, String paramString2, int paramInt) {
    int i = paramInt;
    int j = indexOfComponent(paramComponent);
    if (paramComponent != null && j != -1) {
      removeTabAt(j);
      if (i > j)
        i--; 
    } 
    int k = getSelectedIndex();
    this.pages.add(i, new Page(this, (paramString1 != null) ? paramString1 : "", paramIcon, null, paramComponent, paramString2));
    if (paramComponent != null) {
      addImpl(paramComponent, null, -1);
      paramComponent.setVisible(false);
    } else {
      firePropertyChange("indexForNullComponent", -1, paramInt);
    } 
    if (this.pages.size() == 1)
      setSelectedIndex(0); 
    if (k >= i)
      setSelectedIndexImpl(k + 1, false); 
    if (!this.haveRegistered && paramString2 != null) {
      ToolTipManager.sharedInstance().registerComponent(this);
      this.haveRegistered = true;
    } 
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", null, paramComponent); 
    revalidate();
    repaint();
  }
  
  public void addTab(String paramString1, Icon paramIcon, Component paramComponent, String paramString2) { insertTab(paramString1, paramIcon, paramComponent, paramString2, this.pages.size()); }
  
  public void addTab(String paramString, Icon paramIcon, Component paramComponent) { insertTab(paramString, paramIcon, paramComponent, null, this.pages.size()); }
  
  public void addTab(String paramString, Component paramComponent) { insertTab(paramString, null, paramComponent, null, this.pages.size()); }
  
  public Component add(Component paramComponent) {
    if (!(paramComponent instanceof javax.swing.plaf.UIResource)) {
      addTab(paramComponent.getName(), paramComponent);
    } else {
      super.add(paramComponent);
    } 
    return paramComponent;
  }
  
  public Component add(String paramString, Component paramComponent) {
    if (!(paramComponent instanceof javax.swing.plaf.UIResource)) {
      addTab(paramString, paramComponent);
    } else {
      super.add(paramString, paramComponent);
    } 
    return paramComponent;
  }
  
  public Component add(Component paramComponent, int paramInt) {
    if (!(paramComponent instanceof javax.swing.plaf.UIResource)) {
      insertTab(paramComponent.getName(), null, paramComponent, null, (paramInt == -1) ? getTabCount() : paramInt);
    } else {
      super.add(paramComponent, paramInt);
    } 
    return paramComponent;
  }
  
  public void add(Component paramComponent, Object paramObject) {
    if (!(paramComponent instanceof javax.swing.plaf.UIResource)) {
      if (paramObject instanceof String) {
        addTab((String)paramObject, paramComponent);
      } else if (paramObject instanceof Icon) {
        addTab(null, (Icon)paramObject, paramComponent);
      } else {
        add(paramComponent);
      } 
    } else {
      super.add(paramComponent, paramObject);
    } 
  }
  
  public void add(Component paramComponent, Object paramObject, int paramInt) {
    if (!(paramComponent instanceof javax.swing.plaf.UIResource)) {
      Icon icon = (paramObject instanceof Icon) ? (Icon)paramObject : null;
      String str = (paramObject instanceof String) ? (String)paramObject : null;
      insertTab(str, icon, paramComponent, null, (paramInt == -1) ? getTabCount() : paramInt);
    } else {
      super.add(paramComponent, paramObject, paramInt);
    } 
  }
  
  public void removeTabAt(int paramInt) {
    checkIndex(paramInt);
    Component component = getComponentAt(paramInt);
    boolean bool = false;
    int i = getSelectedIndex();
    String str = null;
    if (component == this.visComp) {
      bool = (SwingUtilities.findFocusOwner(this.visComp) != null) ? 1 : 0;
      this.visComp = null;
    } 
    if (this.accessibleContext != null) {
      if (paramInt == i) {
        ((Page)this.pages.get(paramInt)).firePropertyChange("AccessibleState", AccessibleState.SELECTED, null);
        str = this.accessibleContext.getAccessibleName();
      } 
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", component, null);
    } 
    setTabComponentAt(paramInt, null);
    this.pages.remove(paramInt);
    putClientProperty("__index_to_remove__", Integer.valueOf(paramInt));
    if (i > paramInt) {
      setSelectedIndexImpl(i - 1, false);
    } else if (i >= getTabCount()) {
      setSelectedIndexImpl(i - 1, false);
      Page page = (i != 0) ? (Page)this.pages.get(i - 1) : null;
      changeAccessibleSelection(null, str, page);
    } else if (paramInt == i) {
      fireStateChanged();
      changeAccessibleSelection(null, str, (Page)this.pages.get(paramInt));
    } 
    if (component != null) {
      Component[] arrayOfComponent = getComponents();
      int j = arrayOfComponent.length;
      while (--j >= 0) {
        if (arrayOfComponent[j] == component) {
          super.remove(j);
          component.setVisible(true);
          break;
        } 
      } 
    } 
    if (bool)
      SwingUtilities2.tabbedPaneChangeFocusTo(getSelectedComponent()); 
    revalidate();
    repaint();
  }
  
  public void remove(Component paramComponent) {
    int i = indexOfComponent(paramComponent);
    if (i != -1) {
      removeTabAt(i);
    } else {
      Component[] arrayOfComponent = getComponents();
      for (byte b = 0; b < arrayOfComponent.length; b++) {
        if (paramComponent == arrayOfComponent[b]) {
          super.remove(b);
          break;
        } 
      } 
    } 
  }
  
  public void remove(int paramInt) { removeTabAt(paramInt); }
  
  public void removeAll() {
    setSelectedIndexImpl(-1, true);
    int i = getTabCount();
    while (i-- > 0)
      removeTabAt(i); 
  }
  
  public int getTabCount() { return this.pages.size(); }
  
  public int getTabRunCount() { return (this.ui != null) ? ((TabbedPaneUI)this.ui).getTabRunCount(this) : 0; }
  
  public String getTitleAt(int paramInt) { return ((Page)this.pages.get(paramInt)).title; }
  
  public Icon getIconAt(int paramInt) { return ((Page)this.pages.get(paramInt)).icon; }
  
  public Icon getDisabledIconAt(int paramInt) {
    Page page = (Page)this.pages.get(paramInt);
    if (page.disabledIcon == null)
      page.disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, page.icon); 
    return page.disabledIcon;
  }
  
  public String getToolTipTextAt(int paramInt) { return ((Page)this.pages.get(paramInt)).tip; }
  
  public Color getBackgroundAt(int paramInt) { return ((Page)this.pages.get(paramInt)).getBackground(); }
  
  public Color getForegroundAt(int paramInt) { return ((Page)this.pages.get(paramInt)).getForeground(); }
  
  public boolean isEnabledAt(int paramInt) { return ((Page)this.pages.get(paramInt)).isEnabled(); }
  
  public Component getComponentAt(int paramInt) { return ((Page)this.pages.get(paramInt)).component; }
  
  public int getMnemonicAt(int paramInt) {
    checkIndex(paramInt);
    Page page = (Page)this.pages.get(paramInt);
    return page.getMnemonic();
  }
  
  public int getDisplayedMnemonicIndexAt(int paramInt) {
    checkIndex(paramInt);
    Page page = (Page)this.pages.get(paramInt);
    return page.getDisplayedMnemonicIndex();
  }
  
  public Rectangle getBoundsAt(int paramInt) {
    checkIndex(paramInt);
    return (this.ui != null) ? ((TabbedPaneUI)this.ui).getTabBounds(this, paramInt) : null;
  }
  
  public void setTitleAt(int paramInt, String paramString) {
    Page page = (Page)this.pages.get(paramInt);
    String str = page.title;
    page.title = paramString;
    if (str != paramString)
      firePropertyChange("indexForTitle", -1, paramInt); 
    page.updateDisplayedMnemonicIndex();
    if (str != paramString && this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", str, paramString); 
    if (paramString == null || str == null || !paramString.equals(str)) {
      revalidate();
      repaint();
    } 
  }
  
  public void setIconAt(int paramInt, Icon paramIcon) {
    Page page = (Page)this.pages.get(paramInt);
    Icon icon = page.icon;
    if (paramIcon != icon) {
      page.icon = paramIcon;
      if (page.disabledIcon instanceof javax.swing.plaf.UIResource)
        page.disabledIcon = null; 
      if (this.accessibleContext != null)
        this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon, paramIcon); 
      revalidate();
      repaint();
    } 
  }
  
  public void setDisabledIconAt(int paramInt, Icon paramIcon) {
    Icon icon = ((Page)this.pages.get(paramInt)).disabledIcon;
    ((Page)this.pages.get(paramInt)).disabledIcon = paramIcon;
    if (paramIcon != icon && !isEnabledAt(paramInt)) {
      revalidate();
      repaint();
    } 
  }
  
  public void setToolTipTextAt(int paramInt, String paramString) {
    String str = ((Page)this.pages.get(paramInt)).tip;
    ((Page)this.pages.get(paramInt)).tip = paramString;
    if (str != paramString && this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", str, paramString); 
    if (!this.haveRegistered && paramString != null) {
      ToolTipManager.sharedInstance().registerComponent(this);
      this.haveRegistered = true;
    } 
  }
  
  public void setBackgroundAt(int paramInt, Color paramColor) {
    Color color = ((Page)this.pages.get(paramInt)).background;
    ((Page)this.pages.get(paramInt)).setBackground(paramColor);
    if (paramColor == null || color == null || !paramColor.equals(color)) {
      Rectangle rectangle = getBoundsAt(paramInt);
      if (rectangle != null)
        repaint(rectangle); 
    } 
  }
  
  public void setForegroundAt(int paramInt, Color paramColor) {
    Color color = ((Page)this.pages.get(paramInt)).foreground;
    ((Page)this.pages.get(paramInt)).setForeground(paramColor);
    if (paramColor == null || color == null || !paramColor.equals(color)) {
      Rectangle rectangle = getBoundsAt(paramInt);
      if (rectangle != null)
        repaint(rectangle); 
    } 
  }
  
  public void setEnabledAt(int paramInt, boolean paramBoolean) {
    boolean bool = ((Page)this.pages.get(paramInt)).isEnabled();
    ((Page)this.pages.get(paramInt)).setEnabled(paramBoolean);
    if (paramBoolean != bool) {
      revalidate();
      repaint();
    } 
  }
  
  public void setComponentAt(int paramInt, Component paramComponent) {
    Page page = (Page)this.pages.get(paramInt);
    if (paramComponent != page.component) {
      boolean bool = false;
      if (page.component != null) {
        bool = (SwingUtilities.findFocusOwner(page.component) != null) ? 1 : 0;
        synchronized (getTreeLock()) {
          int i = getComponentCount();
          Component[] arrayOfComponent = getComponents();
          for (byte b = 0; b < i; b++) {
            if (arrayOfComponent[b] == page.component)
              super.remove(b); 
          } 
        } 
      } 
      page.component = paramComponent;
      boolean bool1 = (getSelectedIndex() == paramInt);
      if (bool1)
        this.visComp = paramComponent; 
      if (paramComponent != null) {
        paramComponent.setVisible(bool1);
        addImpl(paramComponent, null, -1);
        if (bool)
          SwingUtilities2.tabbedPaneChangeFocusTo(paramComponent); 
      } else {
        repaint();
      } 
      revalidate();
    } 
  }
  
  public void setDisplayedMnemonicIndexAt(int paramInt1, int paramInt2) {
    checkIndex(paramInt1);
    Page page = (Page)this.pages.get(paramInt1);
    page.setDisplayedMnemonicIndex(paramInt2);
  }
  
  public void setMnemonicAt(int paramInt1, int paramInt2) {
    checkIndex(paramInt1);
    Page page = (Page)this.pages.get(paramInt1);
    page.setMnemonic(paramInt2);
    firePropertyChange("mnemonicAt", null, null);
  }
  
  public int indexOfTab(String paramString) {
    for (byte b = 0; b < getTabCount(); b++) {
      if (getTitleAt(b).equals((paramString == null) ? "" : paramString))
        return b; 
    } 
    return -1;
  }
  
  public int indexOfTab(Icon paramIcon) {
    for (byte b = 0; b < getTabCount(); b++) {
      Icon icon = getIconAt(b);
      if ((icon != null && icon.equals(paramIcon)) || (icon == null && icon == paramIcon))
        return b; 
    } 
    return -1;
  }
  
  public int indexOfComponent(Component paramComponent) {
    for (byte b = 0; b < getTabCount(); b++) {
      Component component = getComponentAt(b);
      if ((component != null && component.equals(paramComponent)) || (component == null && component == paramComponent))
        return b; 
    } 
    return -1;
  }
  
  public int indexAtLocation(int paramInt1, int paramInt2) { return (this.ui != null) ? ((TabbedPaneUI)this.ui).tabForCoordinate(this, paramInt1, paramInt2) : -1; }
  
  public String getToolTipText(MouseEvent paramMouseEvent) {
    if (this.ui != null) {
      int i = ((TabbedPaneUI)this.ui).tabForCoordinate(this, paramMouseEvent.getX(), paramMouseEvent.getY());
      if (i != -1)
        return ((Page)this.pages.get(i)).tip; 
    } 
    return super.getToolTipText(paramMouseEvent);
  }
  
  private void checkIndex(int paramInt) {
    if (paramInt < 0 || paramInt >= this.pages.size())
      throw new IndexOutOfBoundsException("Index: " + paramInt + ", Tab count: " + this.pages.size()); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("TabbedPaneUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  void compWriteObjectNotify() {
    super.compWriteObjectNotify();
    if (getToolTipText() == null && this.haveRegistered)
      ToolTipManager.sharedInstance().unregisterComponent(this); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.ui != null && getUIClassID().equals("TabbedPaneUI"))
      this.ui.installUI(this); 
    if (getToolTipText() == null && this.haveRegistered)
      ToolTipManager.sharedInstance().registerComponent(this); 
  }
  
  protected String paramString() {
    String str1;
    if (this.tabPlacement == 1) {
      str1 = "TOP";
    } else if (this.tabPlacement == 3) {
      str1 = "BOTTOM";
    } else if (this.tabPlacement == 2) {
      str1 = "LEFT";
    } else if (this.tabPlacement == 4) {
      str1 = "RIGHT";
    } else {
      str1 = "";
    } 
    String str2 = this.haveRegistered ? "true" : "false";
    return super.paramString() + ",haveRegistered=" + str2 + ",tabPlacement=" + str1;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleJTabbedPane();
      int i = getTabCount();
      for (byte b = 0; b < i; b++)
        ((Page)this.pages.get(b)).initAccessibleContext(); 
    } 
    return this.accessibleContext;
  }
  
  public void setTabComponentAt(int paramInt, Component paramComponent) {
    if (paramComponent != null && indexOfComponent(paramComponent) != -1)
      throw new IllegalArgumentException("Component is already added to this JTabbedPane"); 
    Component component = getTabComponentAt(paramInt);
    if (paramComponent != component) {
      int i = indexOfTabComponent(paramComponent);
      if (i != -1)
        setTabComponentAt(i, null); 
      ((Page)this.pages.get(paramInt)).tabComponent = paramComponent;
      firePropertyChange("indexForTabComponent", -1, paramInt);
    } 
  }
  
  public Component getTabComponentAt(int paramInt) { return ((Page)this.pages.get(paramInt)).tabComponent; }
  
  public int indexOfTabComponent(Component paramComponent) {
    for (byte b = 0; b < getTabCount(); b++) {
      Component component = getTabComponentAt(b);
      if (component == paramComponent)
        return b; 
    } 
    return -1;
  }
  
  protected class AccessibleJTabbedPane extends JComponent.AccessibleJComponent implements AccessibleSelection, ChangeListener {
    public String getAccessibleName() {
      if (this.accessibleName != null)
        return this.accessibleName; 
      String str = (String)JTabbedPane.this.getClientProperty("AccessibleName");
      if (str != null)
        return str; 
      int i = JTabbedPane.this.getSelectedIndex();
      return (i >= 0) ? ((JTabbedPane.Page)JTabbedPane.this.pages.get(i)).getAccessibleName() : super.getAccessibleName();
    }
    
    public AccessibleJTabbedPane() {
      super(JTabbedPane.this);
      JTabbedPane.this.model.addChangeListener(this);
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      Object object = param1ChangeEvent.getSource();
      firePropertyChange("AccessibleSelection", null, object);
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.PAGE_TAB_LIST; }
    
    public int getAccessibleChildrenCount() { return JTabbedPane.this.getTabCount(); }
    
    public Accessible getAccessibleChild(int param1Int) { return (param1Int < 0 || param1Int >= JTabbedPane.this.getTabCount()) ? null : (Accessible)JTabbedPane.this.pages.get(param1Int); }
    
    public AccessibleSelection getAccessibleSelection() { return this; }
    
    public Accessible getAccessibleAt(Point param1Point) {
      int i = ((TabbedPaneUI)JTabbedPane.this.ui).tabForCoordinate(JTabbedPane.this, param1Point.x, param1Point.y);
      if (i == -1)
        i = JTabbedPane.this.getSelectedIndex(); 
      return getAccessibleChild(i);
    }
    
    public int getAccessibleSelectionCount() { return 1; }
    
    public Accessible getAccessibleSelection(int param1Int) {
      int i = JTabbedPane.this.getSelectedIndex();
      return (i == -1) ? null : (Accessible)JTabbedPane.this.pages.get(i);
    }
    
    public boolean isAccessibleChildSelected(int param1Int) { return (param1Int == JTabbedPane.this.getSelectedIndex()); }
    
    public void addAccessibleSelection(int param1Int) { JTabbedPane.this.setSelectedIndex(param1Int); }
    
    public void removeAccessibleSelection(int param1Int) {}
    
    public void clearAccessibleSelection() {}
    
    public void selectAllAccessibleSelection() {}
  }
  
  protected class ModelListener implements ChangeListener, Serializable {
    public void stateChanged(ChangeEvent param1ChangeEvent) { JTabbedPane.this.fireStateChanged(); }
  }
  
  private class Page extends AccessibleContext implements Serializable, Accessible, AccessibleComponent {
    String title;
    
    Color background;
    
    Color foreground;
    
    Icon icon;
    
    Icon disabledIcon;
    
    JTabbedPane parent;
    
    Component component;
    
    String tip;
    
    boolean enabled = true;
    
    boolean needsUIUpdate;
    
    int mnemonic = -1;
    
    int mnemonicIndex = -1;
    
    Component tabComponent;
    
    Page(JTabbedPane param1JTabbedPane1, String param1String1, Icon param1Icon1, Icon param1Icon2, Component param1Component, String param1String2) {
      this.title = param1String1;
      this.icon = param1Icon1;
      this.disabledIcon = param1Icon2;
      this.parent = param1JTabbedPane1;
      setAccessibleParent(param1JTabbedPane1);
      this.component = param1Component;
      this.tip = param1String2;
      initAccessibleContext();
    }
    
    void initAccessibleContext() {
      if (JTabbedPane.this.accessibleContext != null && this.component instanceof Accessible) {
        AccessibleContext accessibleContext = this.component.getAccessibleContext();
        if (accessibleContext != null)
          accessibleContext.setAccessibleParent(this); 
      } 
    }
    
    void setMnemonic(int param1Int) {
      this.mnemonic = param1Int;
      updateDisplayedMnemonicIndex();
    }
    
    int getMnemonic() { return this.mnemonic; }
    
    void setDisplayedMnemonicIndex(int param1Int) {
      if (this.mnemonicIndex != param1Int) {
        if (param1Int != -1 && (this.title == null || param1Int < 0 || param1Int >= this.title.length()))
          throw new IllegalArgumentException("Invalid mnemonic index: " + param1Int); 
        this.mnemonicIndex = param1Int;
        JTabbedPane.this.firePropertyChange("displayedMnemonicIndexAt", null, null);
      } 
    }
    
    int getDisplayedMnemonicIndex() { return this.mnemonicIndex; }
    
    void updateDisplayedMnemonicIndex() { setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(this.title, this.mnemonic)); }
    
    public AccessibleContext getAccessibleContext() { return this; }
    
    public String getAccessibleName() { return (this.accessibleName != null) ? this.accessibleName : ((this.title != null) ? this.title : null); }
    
    public String getAccessibleDescription() { return (this.accessibleDescription != null) ? this.accessibleDescription : ((this.tip != null) ? this.tip : null); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.PAGE_TAB; }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = this.parent.getAccessibleContext().getAccessibleStateSet();
      accessibleStateSet.add(AccessibleState.SELECTABLE);
      int i = this.parent.indexOfTab(this.title);
      if (i == this.parent.getSelectedIndex())
        accessibleStateSet.add(AccessibleState.SELECTED); 
      return accessibleStateSet;
    }
    
    public int getAccessibleIndexInParent() { return this.parent.indexOfTab(this.title); }
    
    public int getAccessibleChildrenCount() { return (this.component instanceof Accessible) ? 1 : 0; }
    
    public Accessible getAccessibleChild(int param1Int) { return (this.component instanceof Accessible) ? (Accessible)this.component : null; }
    
    public Locale getLocale() { return this.parent.getLocale(); }
    
    public AccessibleComponent getAccessibleComponent() { return this; }
    
    public Color getBackground() { return (this.background != null) ? this.background : this.parent.getBackground(); }
    
    public void setBackground(Color param1Color) { this.background = param1Color; }
    
    public Color getForeground() { return (this.foreground != null) ? this.foreground : this.parent.getForeground(); }
    
    public void setForeground(Color param1Color) { this.foreground = param1Color; }
    
    public Cursor getCursor() { return this.parent.getCursor(); }
    
    public void setCursor(Cursor param1Cursor) { this.parent.setCursor(param1Cursor); }
    
    public Font getFont() { return this.parent.getFont(); }
    
    public void setFont(Font param1Font) { this.parent.setFont(param1Font); }
    
    public FontMetrics getFontMetrics(Font param1Font) { return this.parent.getFontMetrics(param1Font); }
    
    public boolean isEnabled() { return this.enabled; }
    
    public void setEnabled(boolean param1Boolean) { this.enabled = param1Boolean; }
    
    public boolean isVisible() { return this.parent.isVisible(); }
    
    public void setVisible(boolean param1Boolean) { this.parent.setVisible(param1Boolean); }
    
    public boolean isShowing() { return this.parent.isShowing(); }
    
    public boolean contains(Point param1Point) {
      Rectangle rectangle = getBounds();
      return rectangle.contains(param1Point);
    }
    
    public Point getLocationOnScreen() {
      Point point1 = this.parent.getLocationOnScreen();
      Point point2 = getLocation();
      point2.translate(point1.x, point1.y);
      return point2;
    }
    
    public Point getLocation() {
      Rectangle rectangle = getBounds();
      return new Point(rectangle.x, rectangle.y);
    }
    
    public void setLocation(Point param1Point) {}
    
    public Rectangle getBounds() { return this.parent.getUI().getTabBounds(this.parent, this.parent.indexOfTab(this.title)); }
    
    public void setBounds(Rectangle param1Rectangle) {}
    
    public Dimension getSize() {
      Rectangle rectangle = getBounds();
      return new Dimension(rectangle.width, rectangle.height);
    }
    
    public void setSize(Dimension param1Dimension) {}
    
    public Accessible getAccessibleAt(Point param1Point) { return (this.component instanceof Accessible) ? (Accessible)this.component : null; }
    
    public boolean isFocusTraversable() { return false; }
    
    public void requestFocus() {}
    
    public void addFocusListener(FocusListener param1FocusListener) {}
    
    public void removeFocusListener(FocusListener param1FocusListener) {}
    
    public AccessibleIcon[] getAccessibleIcon() {
      AccessibleIcon accessibleIcon = null;
      if (this.enabled && this.icon instanceof ImageIcon) {
        AccessibleContext accessibleContext = ((ImageIcon)this.icon).getAccessibleContext();
        accessibleIcon = (AccessibleIcon)accessibleContext;
      } else if (!this.enabled && this.disabledIcon instanceof ImageIcon) {
        AccessibleContext accessibleContext = ((ImageIcon)this.disabledIcon).getAccessibleContext();
        accessibleIcon = (AccessibleIcon)accessibleContext;
      } 
      if (accessibleIcon != null) {
        AccessibleIcon[] arrayOfAccessibleIcon = new AccessibleIcon[1];
        arrayOfAccessibleIcon[0] = accessibleIcon;
        return arrayOfAccessibleIcon;
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JTabbedPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */