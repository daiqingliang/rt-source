package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleStateSet;
import javax.swing.plaf.MenuBarUI;

public class JMenuBar extends JComponent implements Accessible, MenuElement {
  private static final String uiClassID = "MenuBarUI";
  
  private SingleSelectionModel selectionModel;
  
  private boolean paintBorder = true;
  
  private Insets margin = null;
  
  private static final boolean TRACE = false;
  
  private static final boolean VERBOSE = false;
  
  private static final boolean DEBUG = false;
  
  public JMenuBar() {
    setFocusTraversalKeysEnabled(false);
    setSelectionModel(new DefaultSingleSelectionModel());
    updateUI();
  }
  
  public MenuBarUI getUI() { return (MenuBarUI)this.ui; }
  
  public void setUI(MenuBarUI paramMenuBarUI) { setUI(paramMenuBarUI); }
  
  public void updateUI() { setUI((MenuBarUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "MenuBarUI"; }
  
  public SingleSelectionModel getSelectionModel() { return this.selectionModel; }
  
  public void setSelectionModel(SingleSelectionModel paramSingleSelectionModel) {
    SingleSelectionModel singleSelectionModel = this.selectionModel;
    this.selectionModel = paramSingleSelectionModel;
    firePropertyChange("selectionModel", singleSelectionModel, this.selectionModel);
  }
  
  public JMenu add(JMenu paramJMenu) {
    add(paramJMenu);
    return paramJMenu;
  }
  
  public JMenu getMenu(int paramInt) {
    Component component = getComponentAtIndex(paramInt);
    return (component instanceof JMenu) ? (JMenu)component : null;
  }
  
  public int getMenuCount() { return getComponentCount(); }
  
  public void setHelpMenu(JMenu paramJMenu) { throw new Error("setHelpMenu() not yet implemented."); }
  
  @Transient
  public JMenu getHelpMenu() { throw new Error("getHelpMenu() not yet implemented."); }
  
  @Deprecated
  public Component getComponentAtIndex(int paramInt) { return (paramInt < 0 || paramInt >= getComponentCount()) ? null : getComponent(paramInt); }
  
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
  
  public void setSelected(Component paramComponent) {
    SingleSelectionModel singleSelectionModel = getSelectionModel();
    int i = getComponentIndex(paramComponent);
    singleSelectionModel.setSelectedIndex(i);
  }
  
  public boolean isSelected() { return this.selectionModel.isSelected(); }
  
  public boolean isBorderPainted() { return this.paintBorder; }
  
  public void setBorderPainted(boolean paramBoolean) {
    boolean bool = this.paintBorder;
    this.paintBorder = paramBoolean;
    firePropertyChange("borderPainted", bool, this.paintBorder);
    if (paramBoolean != bool) {
      revalidate();
      repaint();
    } 
  }
  
  protected void paintBorder(Graphics paramGraphics) {
    if (isBorderPainted())
      super.paintBorder(paramGraphics); 
  }
  
  public void setMargin(Insets paramInsets) {
    Insets insets = this.margin;
    this.margin = paramInsets;
    firePropertyChange("margin", insets, paramInsets);
    if (insets == null || !insets.equals(paramInsets)) {
      revalidate();
      repaint();
    } 
  }
  
  public Insets getMargin() { return (this.margin == null) ? new Insets(0, 0, 0, 0) : this.margin; }
  
  public void processMouseEvent(MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {}
  
  public void processKeyEvent(KeyEvent paramKeyEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {}
  
  public void menuSelectionChanged(boolean paramBoolean) {}
  
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
  
  protected String paramString() {
    String str1 = this.paintBorder ? "true" : "false";
    String str2 = (this.margin != null) ? this.margin.toString() : "";
    return super.paramString() + ",margin=" + str2 + ",paintBorder=" + str1;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJMenuBar(); 
    return this.accessibleContext;
  }
  
  protected boolean processKeyBinding(KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean) {
    boolean bool = super.processKeyBinding(paramKeyStroke, paramKeyEvent, paramInt, paramBoolean);
    if (!bool) {
      MenuElement[] arrayOfMenuElement = getSubElements();
      for (MenuElement menuElement : arrayOfMenuElement) {
        if (processBindingForKeyStrokeRecursive(menuElement, paramKeyStroke, paramKeyEvent, paramInt, paramBoolean))
          return true; 
      } 
    } 
    return bool;
  }
  
  static boolean processBindingForKeyStrokeRecursive(MenuElement paramMenuElement, KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean) {
    if (paramMenuElement == null)
      return false; 
    Component component = paramMenuElement.getComponent();
    if ((!component.isVisible() && !(component instanceof JPopupMenu)) || !component.isEnabled())
      return false; 
    if (component != null && component instanceof JComponent && ((JComponent)component).processKeyBinding(paramKeyStroke, paramKeyEvent, paramInt, paramBoolean))
      return true; 
    MenuElement[] arrayOfMenuElement = paramMenuElement.getSubElements();
    for (MenuElement menuElement : arrayOfMenuElement) {
      if (processBindingForKeyStrokeRecursive(menuElement, paramKeyStroke, paramKeyEvent, paramInt, paramBoolean))
        return true; 
    } 
    return false;
  }
  
  public void addNotify() {
    super.addNotify();
    KeyboardManager.getCurrentManager().registerMenuBar(this);
  }
  
  public void removeNotify() {
    super.removeNotify();
    KeyboardManager.getCurrentManager().unregisterMenuBar(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("MenuBarUI")) {
      byte b1 = JComponent.getWriteObjCounter(this);
      b1 = (byte)(b1 - 1);
      JComponent.setWriteObjCounter(this, b1);
      if (b1 == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
    Object[] arrayOfObject = new Object[4];
    byte b = 0;
    if (this.selectionModel instanceof java.io.Serializable) {
      arrayOfObject[b++] = "selectionModel";
      arrayOfObject[b++] = this.selectionModel;
    } 
    paramObjectOutputStream.writeObject(arrayOfObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
    for (boolean bool = false; bool < arrayOfObject.length && arrayOfObject[bool] != null; bool += true) {
      if (arrayOfObject[bool].equals("selectionModel"))
        this.selectionModel = (SingleSelectionModel)arrayOfObject[bool + true]; 
    } 
  }
  
  protected class AccessibleJMenuBar extends JComponent.AccessibleJComponent implements AccessibleSelection {
    protected AccessibleJMenuBar() { super(JMenuBar.this); }
    
    public AccessibleStateSet getAccessibleStateSet() { return super.getAccessibleStateSet(); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.MENU_BAR; }
    
    public AccessibleSelection getAccessibleSelection() { return this; }
    
    public int getAccessibleSelectionCount() { return JMenuBar.this.isSelected() ? 1 : 0; }
    
    public Accessible getAccessibleSelection(int param1Int) {
      if (JMenuBar.this.isSelected()) {
        if (param1Int != 0)
          return null; 
        int i = JMenuBar.this.getSelectionModel().getSelectedIndex();
        if (JMenuBar.this.getComponentAtIndex(i) instanceof Accessible)
          return (Accessible)JMenuBar.this.getComponentAtIndex(i); 
      } 
      return null;
    }
    
    public boolean isAccessibleChildSelected(int param1Int) { return (param1Int == JMenuBar.this.getSelectionModel().getSelectedIndex()); }
    
    public void addAccessibleSelection(int param1Int) {
      int i = JMenuBar.this.getSelectionModel().getSelectedIndex();
      if (param1Int == i)
        return; 
      if (i >= 0 && i < JMenuBar.this.getMenuCount()) {
        JMenu jMenu1 = JMenuBar.this.getMenu(i);
        if (jMenu1 != null)
          MenuSelectionManager.defaultManager().setSelectedPath(null); 
      } 
      JMenuBar.this.getSelectionModel().setSelectedIndex(param1Int);
      JMenu jMenu = JMenuBar.this.getMenu(param1Int);
      if (jMenu != null) {
        MenuElement[] arrayOfMenuElement = new MenuElement[3];
        arrayOfMenuElement[0] = JMenuBar.this;
        arrayOfMenuElement[1] = jMenu;
        arrayOfMenuElement[2] = jMenu.getPopupMenu();
        MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
      } 
    }
    
    public void removeAccessibleSelection(int param1Int) {
      if (param1Int >= 0 && param1Int < JMenuBar.this.getMenuCount()) {
        JMenu jMenu = JMenuBar.this.getMenu(param1Int);
        if (jMenu != null)
          MenuSelectionManager.defaultManager().setSelectedPath(null); 
        JMenuBar.this.getSelectionModel().setSelectedIndex(-1);
      } 
    }
    
    public void clearAccessibleSelection() {
      int i = JMenuBar.this.getSelectionModel().getSelectedIndex();
      if (i >= 0 && i < JMenuBar.this.getMenuCount()) {
        JMenu jMenu = JMenuBar.this.getMenu(i);
        if (jMenu != null)
          MenuSelectionManager.defaultManager().setSelectedPath(null); 
      } 
      JMenuBar.this.getSelectionModel().setSelectedIndex(-1);
    }
    
    public void selectAllAccessibleSelection() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JMenuBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */