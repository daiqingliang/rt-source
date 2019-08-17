package javax.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import javax.swing.plaf.basic.BasicComboPopup;
import sun.awt.SunToolkit;
import sun.security.action.GetPropertyAction;

public class JPopupMenu extends JComponent implements Accessible, MenuElement {
  private static final String uiClassID = "PopupMenuUI";
  
  private static final Object defaultLWPopupEnabledKey = new StringBuffer("JPopupMenu.defaultLWPopupEnabledKey");
  
  static boolean popupPostionFixDisabled = false;
  
  Component invoker;
  
  Popup popup;
  
  Frame frame;
  
  private int desiredLocationX;
  
  private int desiredLocationY;
  
  private String label = null;
  
  private boolean paintBorder = true;
  
  private Insets margin = null;
  
  private boolean lightWeightPopup = true;
  
  private SingleSelectionModel selectionModel;
  
  private static final Object classLock;
  
  private static final boolean TRACE = false;
  
  private static final boolean VERBOSE = false;
  
  private static final boolean DEBUG = false;
  
  public static void setDefaultLightWeightPopupEnabled(boolean paramBoolean) { SwingUtilities.appContextPut(defaultLWPopupEnabledKey, Boolean.valueOf(paramBoolean)); }
  
  public static boolean getDefaultLightWeightPopupEnabled() {
    Boolean bool = (Boolean)SwingUtilities.appContextGet(defaultLWPopupEnabledKey);
    if (bool == null) {
      SwingUtilities.appContextPut(defaultLWPopupEnabledKey, Boolean.TRUE);
      return true;
    } 
    return bool.booleanValue();
  }
  
  public JPopupMenu() { this(null); }
  
  public JPopupMenu(String paramString) {
    this.label = paramString;
    this.lightWeightPopup = getDefaultLightWeightPopupEnabled();
    setSelectionModel(new DefaultSingleSelectionModel());
    enableEvents(16L);
    setFocusTraversalKeysEnabled(false);
    updateUI();
  }
  
  public PopupMenuUI getUI() { return (PopupMenuUI)this.ui; }
  
  public void setUI(PopupMenuUI paramPopupMenuUI) { setUI(paramPopupMenuUI); }
  
  public void updateUI() { setUI((PopupMenuUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "PopupMenuUI"; }
  
  protected void processFocusEvent(FocusEvent paramFocusEvent) { super.processFocusEvent(paramFocusEvent); }
  
  protected void processKeyEvent(KeyEvent paramKeyEvent) {
    MenuSelectionManager.defaultManager().processKeyEvent(paramKeyEvent);
    if (paramKeyEvent.isConsumed())
      return; 
    super.processKeyEvent(paramKeyEvent);
  }
  
  public SingleSelectionModel getSelectionModel() { return this.selectionModel; }
  
  public void setSelectionModel(SingleSelectionModel paramSingleSelectionModel) { this.selectionModel = paramSingleSelectionModel; }
  
  public JMenuItem add(JMenuItem paramJMenuItem) {
    add(paramJMenuItem);
    return paramJMenuItem;
  }
  
  public JMenuItem add(String paramString) { return add(new JMenuItem(paramString)); }
  
  public JMenuItem add(Action paramAction) {
    JMenuItem jMenuItem = createActionComponent(paramAction);
    jMenuItem.setAction(paramAction);
    add(jMenuItem);
    return jMenuItem;
  }
  
  Point adjustPopupLocationToFitScreen(int paramInt1, int paramInt2) {
    Rectangle rectangle;
    Point point = new Point(paramInt1, paramInt2);
    if (popupPostionFixDisabled == true || GraphicsEnvironment.isHeadless())
      return point; 
    GraphicsConfiguration graphicsConfiguration = getCurrentGraphicsConfiguration(point);
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (graphicsConfiguration != null) {
      rectangle = graphicsConfiguration.getBounds();
    } else {
      rectangle = new Rectangle(toolkit.getScreenSize());
    } 
    Dimension dimension = getPreferredSize();
    long l1 = point.x + dimension.width;
    long l2 = point.y + dimension.height;
    int i = rectangle.width;
    int j = rectangle.height;
    if (!canPopupOverlapTaskBar()) {
      Insets insets = toolkit.getScreenInsets(graphicsConfiguration);
      rectangle.x += insets.left;
      rectangle.y += insets.top;
      i -= insets.left + insets.right;
      j -= insets.top + insets.bottom;
    } 
    int k = rectangle.x + i;
    int m = rectangle.y + j;
    if (l1 > k)
      point.x = k - dimension.width; 
    if (l2 > m)
      point.y = m - dimension.height; 
    if (point.x < rectangle.x)
      point.x = rectangle.x; 
    if (point.y < rectangle.y)
      point.y = rectangle.y; 
    return point;
  }
  
  private GraphicsConfiguration getCurrentGraphicsConfiguration(Point paramPoint) {
    GraphicsConfiguration graphicsConfiguration = null;
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] arrayOfGraphicsDevice = graphicsEnvironment.getScreenDevices();
    for (byte b = 0; b < arrayOfGraphicsDevice.length; b++) {
      if (arrayOfGraphicsDevice[b].getType() == 0) {
        GraphicsConfiguration graphicsConfiguration1 = arrayOfGraphicsDevice[b].getDefaultConfiguration();
        if (graphicsConfiguration1.getBounds().contains(paramPoint)) {
          graphicsConfiguration = graphicsConfiguration1;
          break;
        } 
      } 
    } 
    if (graphicsConfiguration == null && getInvoker() != null)
      graphicsConfiguration = getInvoker().getGraphicsConfiguration(); 
    return graphicsConfiguration;
  }
  
  static boolean canPopupOverlapTaskBar() {
    boolean bool = true;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (toolkit instanceof SunToolkit)
      bool = ((SunToolkit)toolkit).canPopupOverlapTaskBar(); 
    return bool;
  }
  
  protected JMenuItem createActionComponent(Action paramAction) {
    JMenuItem jMenuItem = new JMenuItem() {
        protected PropertyChangeListener createActionPropertyChangeListener(Action param1Action) {
          PropertyChangeListener propertyChangeListener = JPopupMenu.this.createActionChangeListener(this);
          if (propertyChangeListener == null)
            propertyChangeListener = super.createActionPropertyChangeListener(param1Action); 
          return propertyChangeListener;
        }
      };
    jMenuItem.setHorizontalTextPosition(11);
    jMenuItem.setVerticalTextPosition(0);
    return jMenuItem;
  }
  
  protected PropertyChangeListener createActionChangeListener(JMenuItem paramJMenuItem) { return paramJMenuItem.createActionPropertyChangeListener0(paramJMenuItem.getAction()); }
  
  public void remove(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("index less than zero."); 
    if (paramInt > getComponentCount() - 1)
      throw new IllegalArgumentException("index greater than the number of items."); 
    super.remove(paramInt);
  }
  
  public void setLightWeightPopupEnabled(boolean paramBoolean) { this.lightWeightPopup = paramBoolean; }
  
  public boolean isLightWeightPopupEnabled() { return this.lightWeightPopup; }
  
  public String getLabel() { return this.label; }
  
  public void setLabel(String paramString) {
    String str = this.label;
    this.label = paramString;
    firePropertyChange("label", str, paramString);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", str, paramString); 
    invalidate();
    repaint();
  }
  
  public void addSeparator() { add(new Separator()); }
  
  public void insert(Action paramAction, int paramInt) {
    JMenuItem jMenuItem = createActionComponent(paramAction);
    jMenuItem.setAction(paramAction);
    insert(jMenuItem, paramInt);
  }
  
  public void insert(Component paramComponent, int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("index less than zero."); 
    int i = getComponentCount();
    Vector vector = new Vector();
    for (int j = paramInt; j < i; j++) {
      vector.addElement(getComponent(paramInt));
      remove(paramInt);
    } 
    add(paramComponent);
    for (Component component : vector)
      add(component); 
  }
  
  public void addPopupMenuListener(PopupMenuListener paramPopupMenuListener) { this.listenerList.add(PopupMenuListener.class, paramPopupMenuListener); }
  
  public void removePopupMenuListener(PopupMenuListener paramPopupMenuListener) { this.listenerList.remove(PopupMenuListener.class, paramPopupMenuListener); }
  
  public PopupMenuListener[] getPopupMenuListeners() { return (PopupMenuListener[])this.listenerList.getListeners(PopupMenuListener.class); }
  
  public void addMenuKeyListener(MenuKeyListener paramMenuKeyListener) { this.listenerList.add(MenuKeyListener.class, paramMenuKeyListener); }
  
  public void removeMenuKeyListener(MenuKeyListener paramMenuKeyListener) { this.listenerList.remove(MenuKeyListener.class, paramMenuKeyListener); }
  
  public MenuKeyListener[] getMenuKeyListeners() { return (MenuKeyListener[])this.listenerList.getListeners(MenuKeyListener.class); }
  
  protected void firePopupMenuWillBecomeVisible() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    PopupMenuEvent popupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class) {
        if (popupMenuEvent == null)
          popupMenuEvent = new PopupMenuEvent(this); 
        ((PopupMenuListener)arrayOfObject[i + 1]).popupMenuWillBecomeVisible(popupMenuEvent);
      } 
    } 
  }
  
  protected void firePopupMenuWillBecomeInvisible() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    PopupMenuEvent popupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class) {
        if (popupMenuEvent == null)
          popupMenuEvent = new PopupMenuEvent(this); 
        ((PopupMenuListener)arrayOfObject[i + 1]).popupMenuWillBecomeInvisible(popupMenuEvent);
      } 
    } 
  }
  
  protected void firePopupMenuCanceled() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    PopupMenuEvent popupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class) {
        if (popupMenuEvent == null)
          popupMenuEvent = new PopupMenuEvent(this); 
        ((PopupMenuListener)arrayOfObject[i + 1]).popupMenuCanceled(popupMenuEvent);
      } 
    } 
  }
  
  boolean alwaysOnTop() { return true; }
  
  public void pack() {
    if (this.popup != null) {
      Dimension dimension = getPreferredSize();
      if (dimension == null || dimension.width != getWidth() || dimension.height != getHeight()) {
        showPopup();
      } else {
        validate();
      } 
    } 
  }
  
  public void setVisible(boolean paramBoolean) {
    if (paramBoolean == isVisible())
      return; 
    if (!paramBoolean) {
      Boolean bool = (Boolean)getClientProperty("JPopupMenu.firePopupMenuCanceled");
      if (bool != null && bool == Boolean.TRUE) {
        putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.FALSE);
        firePopupMenuCanceled();
      } 
      getSelectionModel().clearSelection();
    } else if (isPopupMenu()) {
      MenuElement[] arrayOfMenuElement = new MenuElement[1];
      arrayOfMenuElement[0] = this;
      MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
    } 
    if (paramBoolean) {
      firePopupMenuWillBecomeVisible();
      showPopup();
      firePropertyChange("visible", Boolean.FALSE, Boolean.TRUE);
    } else if (this.popup != null) {
      firePopupMenuWillBecomeInvisible();
      this.popup.hide();
      this.popup = null;
      firePropertyChange("visible", Boolean.TRUE, Boolean.FALSE);
      if (isPopupMenu())
        MenuSelectionManager.defaultManager().clearSelectedPath(); 
    } 
  }
  
  private void showPopup() {
    Popup popup1 = this.popup;
    if (popup1 != null)
      popup1.hide(); 
    PopupFactory popupFactory = PopupFactory.getSharedInstance();
    if (isLightWeightPopupEnabled()) {
      popupFactory.setPopupType(0);
    } else {
      popupFactory.setPopupType(2);
    } 
    Point point = adjustPopupLocationToFitScreen(this.desiredLocationX, this.desiredLocationY);
    this.desiredLocationX = point.x;
    this.desiredLocationY = point.y;
    Popup popup2 = getUI().getPopup(this, this.desiredLocationX, this.desiredLocationY);
    popupFactory.setPopupType(0);
    this.popup = popup2;
    popup2.show();
  }
  
  public boolean isVisible() { return (this.popup != null); }
  
  public void setLocation(int paramInt1, int paramInt2) {
    int i = this.desiredLocationX;
    int j = this.desiredLocationY;
    this.desiredLocationX = paramInt1;
    this.desiredLocationY = paramInt2;
    if (this.popup != null && (paramInt1 != i || paramInt2 != j))
      showPopup(); 
  }
  
  private boolean isPopupMenu() { return (this.invoker != null && !(this.invoker instanceof JMenu)); }
  
  public Component getInvoker() { return this.invoker; }
  
  public void setInvoker(Component paramComponent) {
    Component component = this.invoker;
    this.invoker = paramComponent;
    if (component != this.invoker && this.ui != null) {
      this.ui.uninstallUI(this);
      this.ui.installUI(this);
    } 
    invalidate();
  }
  
  public void show(Component paramComponent, int paramInt1, int paramInt2) {
    setInvoker(paramComponent);
    Frame frame1 = getFrame(paramComponent);
    if (frame1 != this.frame && frame1 != null) {
      this.frame = frame1;
      if (this.popup != null)
        setVisible(false); 
    } 
    if (paramComponent != null) {
      Point point = paramComponent.getLocationOnScreen();
      long l1 = point.x + paramInt1;
      long l2 = point.y + paramInt2;
      if (l1 > 2147483647L)
        l1 = 2147483647L; 
      if (l1 < -2147483648L)
        l1 = -2147483648L; 
      if (l2 > 2147483647L)
        l2 = 2147483647L; 
      if (l2 < -2147483648L)
        l2 = -2147483648L; 
      setLocation((int)l1, (int)l2);
    } else {
      setLocation(paramInt1, paramInt2);
    } 
    setVisible(true);
  }
  
  JPopupMenu getRootPopupMenu() {
    JPopupMenu jPopupMenu;
    for (jPopupMenu = this; jPopupMenu != null && jPopupMenu.isPopupMenu() != true && jPopupMenu.getInvoker() != null && jPopupMenu.getInvoker().getParent() != null && jPopupMenu.getInvoker().getParent() instanceof JPopupMenu; jPopupMenu = (JPopupMenu)jPopupMenu.getInvoker().getParent());
    return jPopupMenu;
  }
  
  @Deprecated
  public Component getComponentAtIndex(int paramInt) { return getComponent(paramInt); }
  
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
  
  public void setPopupSize(Dimension paramDimension) {
    Dimension dimension = getPreferredSize();
    setPreferredSize(paramDimension);
    if (this.popup != null) {
      Dimension dimension1 = getPreferredSize();
      if (!dimension.equals(dimension1))
        showPopup(); 
    } 
  }
  
  public void setPopupSize(int paramInt1, int paramInt2) { setPopupSize(new Dimension(paramInt1, paramInt2)); }
  
  public void setSelected(Component paramComponent) {
    SingleSelectionModel singleSelectionModel = getSelectionModel();
    int i = getComponentIndex(paramComponent);
    singleSelectionModel.setSelectedIndex(i);
  }
  
  public boolean isBorderPainted() { return this.paintBorder; }
  
  public void setBorderPainted(boolean paramBoolean) {
    this.paintBorder = paramBoolean;
    repaint();
  }
  
  protected void paintBorder(Graphics paramGraphics) {
    if (isBorderPainted())
      super.paintBorder(paramGraphics); 
  }
  
  public Insets getMargin() { return (this.margin == null) ? new Insets(0, 0, 0, 0) : this.margin; }
  
  boolean isSubPopupMenu(JPopupMenu paramJPopupMenu) {
    int i = getComponentCount();
    Component[] arrayOfComponent = getComponents();
    for (byte b = 0; b < i; b++) {
      Component component = arrayOfComponent[b];
      if (component instanceof JMenu) {
        JMenu jMenu = (JMenu)component;
        JPopupMenu jPopupMenu = jMenu.getPopupMenu();
        if (jPopupMenu == paramJPopupMenu)
          return true; 
        if (jPopupMenu.isSubPopupMenu(paramJPopupMenu))
          return true; 
      } 
    } 
    return false;
  }
  
  private static Frame getFrame(Component paramComponent) {
    Component component;
    for (component = paramComponent; !(component instanceof Frame) && component != null; component = component.getParent());
    return (Frame)component;
  }
  
  protected String paramString() {
    String str1 = (this.label != null) ? this.label : "";
    String str2 = this.paintBorder ? "true" : "false";
    String str3 = (this.margin != null) ? this.margin.toString() : "";
    String str4 = isLightWeightPopupEnabled() ? "true" : "false";
    return super.paramString() + ",desiredLocationX=" + this.desiredLocationX + ",desiredLocationY=" + this.desiredLocationY + ",label=" + str1 + ",lightWeightPopupEnabled=" + str4 + ",margin=" + str3 + ",paintBorder=" + str2;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJPopupMenu(); 
    return this.accessibleContext;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Vector vector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if (this.invoker != null && this.invoker instanceof java.io.Serializable) {
      vector.addElement("invoker");
      vector.addElement(this.invoker);
    } 
    if (this.popup != null && this.popup instanceof java.io.Serializable) {
      vector.addElement("popup");
      vector.addElement(this.popup);
    } 
    paramObjectOutputStream.writeObject(vector);
    if (getUIClassID().equals("PopupMenuUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Vector vector = (Vector)paramObjectInputStream.readObject();
    byte b = 0;
    int i = vector.size();
    if (b < i && vector.elementAt(b).equals("invoker")) {
      this.invoker = (Component)vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("popup")) {
      this.popup = (Popup)vector.elementAt(++b);
      b++;
    } 
  }
  
  public void processMouseEvent(MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {}
  
  public void processKeyEvent(KeyEvent paramKeyEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {
    MenuKeyEvent menuKeyEvent = new MenuKeyEvent(paramKeyEvent.getComponent(), paramKeyEvent.getID(), paramKeyEvent.getWhen(), paramKeyEvent.getModifiers(), paramKeyEvent.getKeyCode(), paramKeyEvent.getKeyChar(), paramArrayOfMenuElement, paramMenuSelectionManager);
    processMenuKeyEvent(menuKeyEvent);
    if (menuKeyEvent.isConsumed())
      paramKeyEvent.consume(); 
  }
  
  private void processMenuKeyEvent(MenuKeyEvent paramMenuKeyEvent) {
    switch (paramMenuKeyEvent.getID()) {
      case 401:
        fireMenuKeyPressed(paramMenuKeyEvent);
        break;
      case 402:
        fireMenuKeyReleased(paramMenuKeyEvent);
        break;
      case 400:
        fireMenuKeyTyped(paramMenuKeyEvent);
        break;
    } 
  }
  
  private void fireMenuKeyPressed(MenuKeyEvent paramMenuKeyEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuKeyListener.class)
        ((MenuKeyListener)arrayOfObject[i + 1]).menuKeyPressed(paramMenuKeyEvent); 
    } 
  }
  
  private void fireMenuKeyReleased(MenuKeyEvent paramMenuKeyEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuKeyListener.class)
        ((MenuKeyListener)arrayOfObject[i + 1]).menuKeyReleased(paramMenuKeyEvent); 
    } 
  }
  
  private void fireMenuKeyTyped(MenuKeyEvent paramMenuKeyEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuKeyListener.class)
        ((MenuKeyListener)arrayOfObject[i + 1]).menuKeyTyped(paramMenuKeyEvent); 
    } 
  }
  
  public void menuSelectionChanged(boolean paramBoolean) {
    if (this.invoker instanceof JMenu) {
      JMenu jMenu = (JMenu)this.invoker;
      if (paramBoolean) {
        jMenu.setPopupMenuVisible(true);
      } else {
        jMenu.setPopupMenuVisible(false);
      } 
    } 
    if (isPopupMenu() && !paramBoolean)
      setVisible(false); 
  }
  
  public MenuElement[] getSubElements() {
    Vector vector = new Vector();
    int i = getComponentCount();
    byte b;
    for (b = 0; b < i; b++) {
      Component component = getComponent(b);
      if (component instanceof MenuElement)
        vector.addElement((MenuElement)component); 
    } 
    MenuElement[] arrayOfMenuElement = new MenuElement[vector.size()];
    b = 0;
    i = vector.size();
    while (b < i) {
      arrayOfMenuElement[b] = (MenuElement)vector.elementAt(b);
      b++;
    } 
    return arrayOfMenuElement;
  }
  
  public Component getComponent() { return this; }
  
  public boolean isPopupTrigger(MouseEvent paramMouseEvent) { return getUI().isPopupTrigger(paramMouseEvent); }
  
  static  {
    popupPostionFixDisabled = ((String)AccessController.doPrivileged(new GetPropertyAction("javax.swing.adjustPopupLocationToFit", ""))).equals("false");
    classLock = new Object();
  }
  
  protected class AccessibleJPopupMenu extends JComponent.AccessibleJComponent implements PropertyChangeListener {
    protected AccessibleJPopupMenu() {
      super(JPopupMenu.this);
      this$0.addPropertyChangeListener(this);
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.POPUP_MENU; }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "visible")
        if (param1PropertyChangeEvent.getOldValue() == Boolean.FALSE && param1PropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          handlePopupIsVisibleEvent(true);
        } else if (param1PropertyChangeEvent.getOldValue() == Boolean.TRUE && param1PropertyChangeEvent.getNewValue() == Boolean.FALSE) {
          handlePopupIsVisibleEvent(false);
        }  
    }
    
    private void handlePopupIsVisibleEvent(boolean param1Boolean) {
      if (param1Boolean) {
        firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
        fireActiveDescendant();
      } else {
        firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
      } 
    }
    
    private void fireActiveDescendant() {
      if (JPopupMenu.this instanceof BasicComboPopup) {
        JList jList = ((BasicComboPopup)JPopupMenu.this).getList();
        if (jList == null)
          return; 
        AccessibleContext accessibleContext1 = jList.getAccessibleContext();
        AccessibleSelection accessibleSelection = accessibleContext1.getAccessibleSelection();
        if (accessibleSelection == null)
          return; 
        Accessible accessible = accessibleSelection.getAccessibleSelection(0);
        if (accessible == null)
          return; 
        AccessibleContext accessibleContext2 = accessible.getAccessibleContext();
        if (accessibleContext2 != null && JPopupMenu.this.invoker != null) {
          AccessibleContext accessibleContext = JPopupMenu.this.invoker.getAccessibleContext();
          if (accessibleContext != null)
            accessibleContext.firePropertyChange("AccessibleActiveDescendant", null, accessibleContext2); 
        } 
      } 
    }
  }
  
  public static class Separator extends JSeparator {
    public Separator() { super(0); }
    
    public String getUIClassID() { return "PopupMenuSeparatorUI"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JPopupMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */