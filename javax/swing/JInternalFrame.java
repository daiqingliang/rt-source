package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.DesktopIconUI;
import javax.swing.plaf.InternalFrameUI;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.swing.SwingUtilities2;

public class JInternalFrame extends JComponent implements Accessible, WindowConstants, RootPaneContainer {
  private static final String uiClassID = "InternalFrameUI";
  
  protected JRootPane rootPane;
  
  protected boolean rootPaneCheckingEnabled = false;
  
  protected boolean closable;
  
  protected boolean isClosed;
  
  protected boolean maximizable;
  
  protected boolean isMaximum;
  
  protected boolean iconable;
  
  protected boolean isIcon;
  
  protected boolean resizable;
  
  protected boolean isSelected;
  
  protected Icon frameIcon;
  
  protected String title;
  
  protected JDesktopIcon desktopIcon;
  
  private Cursor lastCursor;
  
  private boolean opened;
  
  private Rectangle normalBounds = null;
  
  private int defaultCloseOperation = 2;
  
  private Component lastFocusOwner;
  
  public static final String CONTENT_PANE_PROPERTY = "contentPane";
  
  public static final String MENU_BAR_PROPERTY = "JMenuBar";
  
  public static final String TITLE_PROPERTY = "title";
  
  public static final String LAYERED_PANE_PROPERTY = "layeredPane";
  
  public static final String ROOT_PANE_PROPERTY = "rootPane";
  
  public static final String GLASS_PANE_PROPERTY = "glassPane";
  
  public static final String FRAME_ICON_PROPERTY = "frameIcon";
  
  public static final String IS_SELECTED_PROPERTY = "selected";
  
  public static final String IS_CLOSED_PROPERTY = "closed";
  
  public static final String IS_MAXIMUM_PROPERTY = "maximum";
  
  public static final String IS_ICON_PROPERTY = "icon";
  
  private static final Object PROPERTY_CHANGE_LISTENER_KEY = new StringBuilder("InternalFramePropertyChangeListener");
  
  boolean isDragging = false;
  
  boolean danger = false;
  
  private static void addPropertyChangeListenerIfNecessary() {
    if (AppContext.getAppContext().get(PROPERTY_CHANGE_LISTENER_KEY) == null) {
      FocusPropertyChangeListener focusPropertyChangeListener = new FocusPropertyChangeListener(null);
      AppContext.getAppContext().put(PROPERTY_CHANGE_LISTENER_KEY, focusPropertyChangeListener);
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(focusPropertyChangeListener);
    } 
  }
  
  private static void updateLastFocusOwner(Component paramComponent) {
    if (paramComponent != null)
      for (Component component = paramComponent; component != null && !(component instanceof java.awt.Window); component = component.getParent()) {
        if (component instanceof JInternalFrame)
          ((JInternalFrame)component).setLastFocusOwner(paramComponent); 
      }  
  }
  
  public JInternalFrame() { this("", false, false, false, false); }
  
  public JInternalFrame(String paramString) { this(paramString, false, false, false, false); }
  
  public JInternalFrame(String paramString, boolean paramBoolean) { this(paramString, paramBoolean, false, false, false); }
  
  public JInternalFrame(String paramString, boolean paramBoolean1, boolean paramBoolean2) { this(paramString, paramBoolean1, paramBoolean2, false, false); }
  
  public JInternalFrame(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) { this(paramString, paramBoolean1, paramBoolean2, paramBoolean3, false); }
  
  public JInternalFrame(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) {
    setRootPane(createRootPane());
    setLayout(new BorderLayout());
    this.title = paramString;
    this.resizable = paramBoolean1;
    this.closable = paramBoolean2;
    this.maximizable = paramBoolean3;
    this.isMaximum = false;
    this.iconable = paramBoolean4;
    this.isIcon = false;
    setVisible(false);
    setRootPaneCheckingEnabled(true);
    this.desktopIcon = new JDesktopIcon(this);
    updateUI();
    SunToolkit.checkAndSetPolicy(this);
    addPropertyChangeListenerIfNecessary();
  }
  
  protected JRootPane createRootPane() { return new JRootPane(); }
  
  public InternalFrameUI getUI() { return (InternalFrameUI)this.ui; }
  
  public void setUI(InternalFrameUI paramInternalFrameUI) {
    bool = isRootPaneCheckingEnabled();
    try {
      setRootPaneCheckingEnabled(false);
      setUI(paramInternalFrameUI);
    } finally {
      setRootPaneCheckingEnabled(bool);
    } 
  }
  
  public void updateUI() {
    setUI((InternalFrameUI)UIManager.getUI(this));
    invalidate();
    if (this.desktopIcon != null)
      this.desktopIcon.updateUIWhenHidden(); 
  }
  
  void updateUIWhenHidden() {
    setUI((InternalFrameUI)UIManager.getUI(this));
    invalidate();
    Component[] arrayOfComponent = getComponents();
    if (arrayOfComponent != null)
      for (Component component : arrayOfComponent)
        SwingUtilities.updateComponentTreeUI(component);  
  }
  
  public String getUIClassID() { return "InternalFrameUI"; }
  
  protected boolean isRootPaneCheckingEnabled() { return this.rootPaneCheckingEnabled; }
  
  protected void setRootPaneCheckingEnabled(boolean paramBoolean) { this.rootPaneCheckingEnabled = paramBoolean; }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    if (isRootPaneCheckingEnabled()) {
      getContentPane().add(paramComponent, paramObject, paramInt);
    } else {
      super.addImpl(paramComponent, paramObject, paramInt);
    } 
  }
  
  public void remove(Component paramComponent) {
    int i = getComponentCount();
    super.remove(paramComponent);
    if (i == getComponentCount())
      getContentPane().remove(paramComponent); 
  }
  
  public void setLayout(LayoutManager paramLayoutManager) {
    if (isRootPaneCheckingEnabled()) {
      getContentPane().setLayout(paramLayoutManager);
    } else {
      super.setLayout(paramLayoutManager);
    } 
  }
  
  @Deprecated
  public JMenuBar getMenuBar() { return getRootPane().getMenuBar(); }
  
  public JMenuBar getJMenuBar() { return getRootPane().getJMenuBar(); }
  
  @Deprecated
  public void setMenuBar(JMenuBar paramJMenuBar) {
    JMenuBar jMenuBar = getMenuBar();
    getRootPane().setJMenuBar(paramJMenuBar);
    firePropertyChange("JMenuBar", jMenuBar, paramJMenuBar);
  }
  
  public void setJMenuBar(JMenuBar paramJMenuBar) {
    JMenuBar jMenuBar = getMenuBar();
    getRootPane().setJMenuBar(paramJMenuBar);
    firePropertyChange("JMenuBar", jMenuBar, paramJMenuBar);
  }
  
  public Container getContentPane() { return getRootPane().getContentPane(); }
  
  public void setContentPane(Container paramContainer) {
    Container container = getContentPane();
    getRootPane().setContentPane(paramContainer);
    firePropertyChange("contentPane", container, paramContainer);
  }
  
  public JLayeredPane getLayeredPane() { return getRootPane().getLayeredPane(); }
  
  public void setLayeredPane(JLayeredPane paramJLayeredPane) {
    JLayeredPane jLayeredPane = getLayeredPane();
    getRootPane().setLayeredPane(paramJLayeredPane);
    firePropertyChange("layeredPane", jLayeredPane, paramJLayeredPane);
  }
  
  public Component getGlassPane() { return getRootPane().getGlassPane(); }
  
  public void setGlassPane(Component paramComponent) {
    Component component = getGlassPane();
    getRootPane().setGlassPane(paramComponent);
    firePropertyChange("glassPane", component, paramComponent);
  }
  
  public JRootPane getRootPane() { return this.rootPane; }
  
  protected void setRootPane(JRootPane paramJRootPane) {
    if (this.rootPane != null)
      remove(this.rootPane); 
    JRootPane jRootPane = getRootPane();
    this.rootPane = paramJRootPane;
    if (this.rootPane != null) {
      bool = isRootPaneCheckingEnabled();
      try {
        setRootPaneCheckingEnabled(false);
        add(this.rootPane, "Center");
      } finally {
        setRootPaneCheckingEnabled(bool);
      } 
    } 
    firePropertyChange("rootPane", jRootPane, paramJRootPane);
  }
  
  public void setClosable(boolean paramBoolean) {
    Boolean bool1 = this.closable ? Boolean.TRUE : Boolean.FALSE;
    Boolean bool2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    this.closable = paramBoolean;
    firePropertyChange("closable", bool1, bool2);
  }
  
  public boolean isClosable() { return this.closable; }
  
  public boolean isClosed() { return this.isClosed; }
  
  public void setClosed(boolean paramBoolean) {
    if (this.isClosed == paramBoolean)
      return; 
    Boolean bool1 = this.isClosed ? Boolean.TRUE : Boolean.FALSE;
    Boolean bool2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    if (paramBoolean)
      fireInternalFrameEvent(25550); 
    fireVetoableChange("closed", bool1, bool2);
    this.isClosed = paramBoolean;
    if (this.isClosed)
      setVisible(false); 
    firePropertyChange("closed", bool1, bool2);
    if (this.isClosed) {
      dispose();
    } else if (!this.opened) {
    
    } 
  }
  
  public void setResizable(boolean paramBoolean) {
    Boolean bool1 = this.resizable ? Boolean.TRUE : Boolean.FALSE;
    Boolean bool2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    this.resizable = paramBoolean;
    firePropertyChange("resizable", bool1, bool2);
  }
  
  public boolean isResizable() { return this.isMaximum ? false : this.resizable; }
  
  public void setIconifiable(boolean paramBoolean) {
    Boolean bool1 = this.iconable ? Boolean.TRUE : Boolean.FALSE;
    Boolean bool2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    this.iconable = paramBoolean;
    firePropertyChange("iconable", bool1, bool2);
  }
  
  public boolean isIconifiable() { return this.iconable; }
  
  public boolean isIcon() { return this.isIcon; }
  
  public void setIcon(boolean paramBoolean) {
    if (this.isIcon == paramBoolean)
      return; 
    firePropertyChange("ancestor", null, getParent());
    Boolean bool1 = this.isIcon ? Boolean.TRUE : Boolean.FALSE;
    Boolean bool2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    fireVetoableChange("icon", bool1, bool2);
    this.isIcon = paramBoolean;
    firePropertyChange("icon", bool1, bool2);
    if (paramBoolean) {
      fireInternalFrameEvent(25552);
    } else {
      fireInternalFrameEvent(25553);
    } 
  }
  
  public void setMaximizable(boolean paramBoolean) {
    Boolean bool1 = this.maximizable ? Boolean.TRUE : Boolean.FALSE;
    Boolean bool2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    this.maximizable = paramBoolean;
    firePropertyChange("maximizable", bool1, bool2);
  }
  
  public boolean isMaximizable() { return this.maximizable; }
  
  public boolean isMaximum() { return this.isMaximum; }
  
  public void setMaximum(boolean paramBoolean) {
    if (this.isMaximum == paramBoolean)
      return; 
    Boolean bool1 = this.isMaximum ? Boolean.TRUE : Boolean.FALSE;
    Boolean bool2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    fireVetoableChange("maximum", bool1, bool2);
    this.isMaximum = paramBoolean;
    firePropertyChange("maximum", bool1, bool2);
  }
  
  public String getTitle() { return this.title; }
  
  public void setTitle(String paramString) {
    String str = this.title;
    this.title = paramString;
    firePropertyChange("title", str, paramString);
  }
  
  public void setSelected(boolean paramBoolean) {
    if (paramBoolean && this.isSelected) {
      restoreSubcomponentFocus();
      return;
    } 
    if (this.isSelected == paramBoolean || (paramBoolean && (this.isIcon ? !this.desktopIcon.isShowing() : !isShowing())))
      return; 
    Boolean bool1 = this.isSelected ? Boolean.TRUE : Boolean.FALSE;
    Boolean bool2 = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    fireVetoableChange("selected", bool1, bool2);
    if (paramBoolean)
      restoreSubcomponentFocus(); 
    this.isSelected = paramBoolean;
    firePropertyChange("selected", bool1, bool2);
    if (this.isSelected) {
      fireInternalFrameEvent(25554);
    } else {
      fireInternalFrameEvent(25555);
    } 
    repaint();
  }
  
  public boolean isSelected() { return this.isSelected; }
  
  public void setFrameIcon(Icon paramIcon) {
    Icon icon = this.frameIcon;
    this.frameIcon = paramIcon;
    firePropertyChange("frameIcon", icon, paramIcon);
  }
  
  public Icon getFrameIcon() { return this.frameIcon; }
  
  public void moveToFront() {
    if (isIcon()) {
      if (getDesktopIcon().getParent() instanceof JLayeredPane)
        ((JLayeredPane)getDesktopIcon().getParent()).moveToFront(getDesktopIcon()); 
    } else if (getParent() instanceof JLayeredPane) {
      ((JLayeredPane)getParent()).moveToFront(this);
    } 
  }
  
  public void moveToBack() {
    if (isIcon()) {
      if (getDesktopIcon().getParent() instanceof JLayeredPane)
        ((JLayeredPane)getDesktopIcon().getParent()).moveToBack(getDesktopIcon()); 
    } else if (getParent() instanceof JLayeredPane) {
      ((JLayeredPane)getParent()).moveToBack(this);
    } 
  }
  
  public Cursor getLastCursor() { return this.lastCursor; }
  
  public void setCursor(Cursor paramCursor) {
    if (paramCursor == null) {
      this.lastCursor = null;
      super.setCursor(paramCursor);
      return;
    } 
    int i = paramCursor.getType();
    if (i != 4 && i != 5 && i != 6 && i != 7 && i != 8 && i != 9 && i != 10 && i != 11)
      this.lastCursor = paramCursor; 
    super.setCursor(paramCursor);
  }
  
  public void setLayer(Integer paramInteger) {
    if (getParent() != null && getParent() instanceof JLayeredPane) {
      JLayeredPane jLayeredPane = (JLayeredPane)getParent();
      jLayeredPane.setLayer(this, paramInteger.intValue(), jLayeredPane.getPosition(this));
    } else {
      JLayeredPane.putLayer(this, paramInteger.intValue());
      if (getParent() != null)
        getParent().repaint(getX(), getY(), getWidth(), getHeight()); 
    } 
  }
  
  public void setLayer(int paramInt) { setLayer(Integer.valueOf(paramInt)); }
  
  public int getLayer() { return JLayeredPane.getLayer(this); }
  
  public JDesktopPane getDesktopPane() {
    Container container;
    for (container = getParent(); container != null && !(container instanceof JDesktopPane); container = container.getParent());
    if (container == null)
      for (container = getDesktopIcon().getParent(); container != null && !(container instanceof JDesktopPane); container = container.getParent()); 
    return (JDesktopPane)container;
  }
  
  public void setDesktopIcon(JDesktopIcon paramJDesktopIcon) {
    JDesktopIcon jDesktopIcon = getDesktopIcon();
    this.desktopIcon = paramJDesktopIcon;
    firePropertyChange("desktopIcon", jDesktopIcon, paramJDesktopIcon);
  }
  
  public JDesktopIcon getDesktopIcon() { return this.desktopIcon; }
  
  public Rectangle getNormalBounds() { return (this.normalBounds != null) ? this.normalBounds : getBounds(); }
  
  public void setNormalBounds(Rectangle paramRectangle) { this.normalBounds = paramRectangle; }
  
  public Component getFocusOwner() { return isSelected() ? this.lastFocusOwner : null; }
  
  public Component getMostRecentFocusOwner() {
    if (isSelected())
      return getFocusOwner(); 
    if (this.lastFocusOwner != null)
      return this.lastFocusOwner; 
    FocusTraversalPolicy focusTraversalPolicy = getFocusTraversalPolicy();
    if (focusTraversalPolicy instanceof InternalFrameFocusTraversalPolicy)
      return ((InternalFrameFocusTraversalPolicy)focusTraversalPolicy).getInitialComponent(this); 
    Component component = focusTraversalPolicy.getDefaultComponent(this);
    return (component != null) ? component : getContentPane();
  }
  
  public void restoreSubcomponentFocus() {
    if (isIcon()) {
      SwingUtilities2.compositeRequestFocus(getDesktopIcon());
    } else {
      Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
      if (component == null || !SwingUtilities.isDescendingFrom(component, this)) {
        setLastFocusOwner(getMostRecentFocusOwner());
        if (this.lastFocusOwner == null)
          setLastFocusOwner(getContentPane()); 
        this.lastFocusOwner.requestFocus();
      } 
    } 
  }
  
  private void setLastFocusOwner(Component paramComponent) { this.lastFocusOwner = paramComponent; }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    validate();
    repaint();
  }
  
  public void addInternalFrameListener(InternalFrameListener paramInternalFrameListener) {
    this.listenerList.add(InternalFrameListener.class, paramInternalFrameListener);
    enableEvents(0L);
  }
  
  public void removeInternalFrameListener(InternalFrameListener paramInternalFrameListener) { this.listenerList.remove(InternalFrameListener.class, paramInternalFrameListener); }
  
  public InternalFrameListener[] getInternalFrameListeners() { return (InternalFrameListener[])this.listenerList.getListeners(InternalFrameListener.class); }
  
  protected void fireInternalFrameEvent(int paramInt) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    InternalFrameEvent internalFrameEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == InternalFrameListener.class) {
        if (internalFrameEvent == null)
          internalFrameEvent = new InternalFrameEvent(this, paramInt); 
        switch (internalFrameEvent.getID()) {
          case 25549:
            ((InternalFrameListener)arrayOfObject[i + 1]).internalFrameOpened(internalFrameEvent);
            break;
          case 25550:
            ((InternalFrameListener)arrayOfObject[i + 1]).internalFrameClosing(internalFrameEvent);
            break;
          case 25551:
            ((InternalFrameListener)arrayOfObject[i + 1]).internalFrameClosed(internalFrameEvent);
            break;
          case 25552:
            ((InternalFrameListener)arrayOfObject[i + 1]).internalFrameIconified(internalFrameEvent);
            break;
          case 25553:
            ((InternalFrameListener)arrayOfObject[i + 1]).internalFrameDeiconified(internalFrameEvent);
            break;
          case 25554:
            ((InternalFrameListener)arrayOfObject[i + 1]).internalFrameActivated(internalFrameEvent);
            break;
          case 25555:
            ((InternalFrameListener)arrayOfObject[i + 1]).internalFrameDeactivated(internalFrameEvent);
            break;
        } 
      } 
    } 
  }
  
  public void doDefaultCloseAction() {
    fireInternalFrameEvent(25550);
    switch (this.defaultCloseOperation) {
      case 1:
        setVisible(false);
        if (isSelected())
          try {
            setSelected(false);
          } catch (PropertyVetoException propertyVetoException) {} 
        break;
      case 2:
        try {
          fireVetoableChange("closed", Boolean.FALSE, Boolean.TRUE);
          this.isClosed = true;
          setVisible(false);
          firePropertyChange("closed", Boolean.FALSE, Boolean.TRUE);
          dispose();
        } catch (PropertyVetoException propertyVetoException) {}
        break;
    } 
  }
  
  public void setDefaultCloseOperation(int paramInt) { this.defaultCloseOperation = paramInt; }
  
  public int getDefaultCloseOperation() { return this.defaultCloseOperation; }
  
  public void pack() {
    try {
      if (isIcon()) {
        setIcon(false);
      } else if (isMaximum()) {
        setMaximum(false);
      } 
    } catch (PropertyVetoException propertyVetoException) {
      return;
    } 
    setSize(getPreferredSize());
    validate();
  }
  
  public void show() {
    if (isVisible())
      return; 
    if (!this.opened) {
      fireInternalFrameEvent(25549);
      this.opened = true;
    } 
    getDesktopIcon().setVisible(true);
    toFront();
    super.show();
    if (this.isIcon)
      return; 
    if (!isSelected())
      try {
        setSelected(true);
      } catch (PropertyVetoException propertyVetoException) {} 
  }
  
  public void hide() {
    if (isIcon())
      getDesktopIcon().setVisible(false); 
    super.hide();
  }
  
  public void dispose() {
    if (isVisible())
      setVisible(false); 
    if (isSelected())
      try {
        setSelected(false);
      } catch (PropertyVetoException propertyVetoException) {} 
    if (!this.isClosed) {
      firePropertyChange("closed", Boolean.FALSE, Boolean.TRUE);
      this.isClosed = true;
    } 
    fireInternalFrameEvent(25551);
  }
  
  public void toFront() { moveToFront(); }
  
  public void toBack() { moveToBack(); }
  
  public final void setFocusCycleRoot(boolean paramBoolean) {}
  
  public final boolean isFocusCycleRoot() { return true; }
  
  public final Container getFocusCycleRootAncestor() { return null; }
  
  public final String getWarningString() { return null; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("InternalFrameUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null) {
        bool = isRootPaneCheckingEnabled();
        try {
          setRootPaneCheckingEnabled(false);
          this.ui.installUI(this);
        } finally {
          setRootPaneCheckingEnabled(bool);
        } 
      } 
    } 
  }
  
  void compWriteObjectNotify() {
    bool = isRootPaneCheckingEnabled();
    try {
      setRootPaneCheckingEnabled(false);
      super.compWriteObjectNotify();
    } finally {
      setRootPaneCheckingEnabled(bool);
    } 
  }
  
  protected String paramString() {
    String str15;
    String str1 = (this.rootPane != null) ? this.rootPane.toString() : "";
    String str2 = this.rootPaneCheckingEnabled ? "true" : "false";
    String str3 = this.closable ? "true" : "false";
    String str4 = this.isClosed ? "true" : "false";
    String str5 = this.maximizable ? "true" : "false";
    String str6 = this.isMaximum ? "true" : "false";
    String str7 = this.iconable ? "true" : "false";
    String str8 = this.isIcon ? "true" : "false";
    String str9 = this.resizable ? "true" : "false";
    String str10 = this.isSelected ? "true" : "false";
    String str11 = (this.frameIcon != null) ? this.frameIcon.toString() : "";
    String str12 = (this.title != null) ? this.title : "";
    String str13 = (this.desktopIcon != null) ? this.desktopIcon.toString() : "";
    String str14 = this.opened ? "true" : "false";
    if (this.defaultCloseOperation == 1) {
      str15 = "HIDE_ON_CLOSE";
    } else if (this.defaultCloseOperation == 2) {
      str15 = "DISPOSE_ON_CLOSE";
    } else if (this.defaultCloseOperation == 0) {
      str15 = "DO_NOTHING_ON_CLOSE";
    } else {
      str15 = "";
    } 
    return super.paramString() + ",closable=" + str3 + ",defaultCloseOperation=" + str15 + ",desktopIcon=" + str13 + ",frameIcon=" + str11 + ",iconable=" + str7 + ",isClosed=" + str4 + ",isIcon=" + str8 + ",isMaximum=" + str6 + ",isSelected=" + str10 + ",maximizable=" + str5 + ",opened=" + str14 + ",resizable=" + str9 + ",rootPane=" + str1 + ",rootPaneCheckingEnabled=" + str2 + ",title=" + str12;
  }
  
  protected void paintComponent(Graphics paramGraphics) {
    if (this.isDragging)
      this.danger = true; 
    super.paintComponent(paramGraphics);
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJInternalFrame(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJInternalFrame extends JComponent.AccessibleJComponent implements AccessibleValue {
    protected AccessibleJInternalFrame() { super(JInternalFrame.this); }
    
    public String getAccessibleName() {
      String str = this.accessibleName;
      if (str == null)
        str = (String)JInternalFrame.this.getClientProperty("AccessibleName"); 
      if (str == null)
        str = JInternalFrame.this.getTitle(); 
      return str;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.INTERNAL_FRAME; }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public Number getCurrentAccessibleValue() { return Integer.valueOf(JInternalFrame.this.getLayer()); }
    
    public boolean setCurrentAccessibleValue(Number param1Number) {
      if (param1Number == null)
        return false; 
      JInternalFrame.this.setLayer(new Integer(param1Number.intValue()));
      return true;
    }
    
    public Number getMinimumAccessibleValue() { return Integer.valueOf(-2147483648); }
    
    public Number getMaximumAccessibleValue() { return Integer.valueOf(2147483647); }
  }
  
  private static class FocusPropertyChangeListener implements PropertyChangeListener {
    private FocusPropertyChangeListener() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getPropertyName() == "permanentFocusOwner")
        JInternalFrame.updateLastFocusOwner((Component)param1PropertyChangeEvent.getNewValue()); 
    }
  }
  
  public static class JDesktopIcon extends JComponent implements Accessible {
    JInternalFrame internalFrame;
    
    public JDesktopIcon(JInternalFrame param1JInternalFrame) {
      setVisible(false);
      setInternalFrame(param1JInternalFrame);
      updateUI();
    }
    
    public DesktopIconUI getUI() { return (DesktopIconUI)this.ui; }
    
    public void setUI(DesktopIconUI param1DesktopIconUI) { setUI(param1DesktopIconUI); }
    
    public JInternalFrame getInternalFrame() { return this.internalFrame; }
    
    public void setInternalFrame(JInternalFrame param1JInternalFrame) { this.internalFrame = param1JInternalFrame; }
    
    public JDesktopPane getDesktopPane() { return (getInternalFrame() != null) ? getInternalFrame().getDesktopPane() : null; }
    
    public void updateUI() {
      boolean bool = (this.ui != null) ? 1 : 0;
      setUI((DesktopIconUI)UIManager.getUI(this));
      invalidate();
      Dimension dimension = getPreferredSize();
      setSize(dimension.width, dimension.height);
      if (this.internalFrame != null && this.internalFrame.getUI() != null)
        SwingUtilities.updateComponentTreeUI(this.internalFrame); 
    }
    
    void updateUIWhenHidden() {
      setUI((DesktopIconUI)UIManager.getUI(this));
      Dimension dimension = getPreferredSize();
      setSize(dimension.width, dimension.height);
      invalidate();
      Component[] arrayOfComponent = getComponents();
      if (arrayOfComponent != null)
        for (Component component : arrayOfComponent)
          SwingUtilities.updateComponentTreeUI(component);  
    }
    
    public String getUIClassID() { return "DesktopIconUI"; }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      param1ObjectOutputStream.defaultWriteObject();
      if (getUIClassID().equals("DesktopIconUI")) {
        byte b = JComponent.getWriteObjCounter(this);
        b = (byte)(b - 1);
        JComponent.setWriteObjCounter(this, b);
        if (b == 0 && this.ui != null)
          this.ui.installUI(this); 
      } 
    }
    
    public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null)
        this.accessibleContext = new AccessibleJDesktopIcon(); 
      return this.accessibleContext;
    }
    
    protected class AccessibleJDesktopIcon extends JComponent.AccessibleJComponent implements AccessibleValue {
      protected AccessibleJDesktopIcon() { super(JInternalFrame.JDesktopIcon.this); }
      
      public AccessibleRole getAccessibleRole() { return AccessibleRole.DESKTOP_ICON; }
      
      public AccessibleValue getAccessibleValue() { return this; }
      
      public Number getCurrentAccessibleValue() {
        AccessibleContext accessibleContext = JInternalFrame.JDesktopIcon.this.getInternalFrame().getAccessibleContext();
        AccessibleValue accessibleValue = accessibleContext.getAccessibleValue();
        return (accessibleValue != null) ? accessibleValue.getCurrentAccessibleValue() : null;
      }
      
      public boolean setCurrentAccessibleValue(Number param2Number) {
        if (param2Number == null)
          return false; 
        AccessibleContext accessibleContext = JInternalFrame.JDesktopIcon.this.getInternalFrame().getAccessibleContext();
        AccessibleValue accessibleValue = accessibleContext.getAccessibleValue();
        return (accessibleValue != null) ? accessibleValue.setCurrentAccessibleValue(param2Number) : 0;
      }
      
      public Number getMinimumAccessibleValue() {
        AccessibleContext accessibleContext = JInternalFrame.JDesktopIcon.this.getInternalFrame().getAccessibleContext();
        return (accessibleContext instanceof AccessibleValue) ? ((AccessibleValue)accessibleContext).getMinimumAccessibleValue() : null;
      }
      
      public Number getMaximumAccessibleValue() {
        AccessibleContext accessibleContext = JInternalFrame.JDesktopIcon.this.getInternalFrame().getAccessibleContext();
        return (accessibleContext instanceof AccessibleValue) ? ((AccessibleValue)accessibleContext).getMaximumAccessibleValue() : null;
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JInternalFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */