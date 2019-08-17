package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.RootPaneUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicRootPaneUI extends RootPaneUI implements PropertyChangeListener {
  private static RootPaneUI rootPaneUI = new BasicRootPaneUI();
  
  public static ComponentUI createUI(JComponent paramJComponent) { return rootPaneUI; }
  
  public void installUI(JComponent paramJComponent) {
    installDefaults((JRootPane)paramJComponent);
    installComponents((JRootPane)paramJComponent);
    installListeners((JRootPane)paramJComponent);
    installKeyboardActions((JRootPane)paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults((JRootPane)paramJComponent);
    uninstallComponents((JRootPane)paramJComponent);
    uninstallListeners((JRootPane)paramJComponent);
    uninstallKeyboardActions((JRootPane)paramJComponent);
  }
  
  protected void installDefaults(JRootPane paramJRootPane) { LookAndFeel.installProperty(paramJRootPane, "opaque", Boolean.FALSE); }
  
  protected void installComponents(JRootPane paramJRootPane) {}
  
  protected void installListeners(JRootPane paramJRootPane) { paramJRootPane.addPropertyChangeListener(this); }
  
  protected void installKeyboardActions(JRootPane paramJRootPane) {
    InputMap inputMap = getInputMap(2, paramJRootPane);
    SwingUtilities.replaceUIInputMap(paramJRootPane, 2, inputMap);
    inputMap = getInputMap(1, paramJRootPane);
    SwingUtilities.replaceUIInputMap(paramJRootPane, 1, inputMap);
    LazyActionMap.installLazyActionMap(paramJRootPane, BasicRootPaneUI.class, "RootPane.actionMap");
    updateDefaultButtonBindings(paramJRootPane);
  }
  
  protected void uninstallDefaults(JRootPane paramJRootPane) {}
  
  protected void uninstallComponents(JRootPane paramJRootPane) {}
  
  protected void uninstallListeners(JRootPane paramJRootPane) { paramJRootPane.removePropertyChangeListener(this); }
  
  protected void uninstallKeyboardActions(JRootPane paramJRootPane) {
    SwingUtilities.replaceUIInputMap(paramJRootPane, 2, null);
    SwingUtilities.replaceUIActionMap(paramJRootPane, null);
  }
  
  InputMap getInputMap(int paramInt, JComponent paramJComponent) { return (paramInt == 1) ? (InputMap)DefaultLookup.get(paramJComponent, this, "RootPane.ancestorInputMap") : ((paramInt == 2) ? createInputMap(paramInt, paramJComponent) : null); }
  
  ComponentInputMap createInputMap(int paramInt, JComponent paramJComponent) { return new RootPaneInputMap(paramJComponent); }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("press"));
    paramLazyActionMap.put(new Actions("release"));
    paramLazyActionMap.put(new Actions("postPopup"));
  }
  
  void updateDefaultButtonBindings(JRootPane paramJRootPane) {
    InputMap inputMap;
    for (inputMap = SwingUtilities.getUIInputMap(paramJRootPane, 2); inputMap != null && !(inputMap instanceof RootPaneInputMap); inputMap = inputMap.getParent());
    if (inputMap != null) {
      inputMap.clear();
      if (paramJRootPane.getDefaultButton() != null) {
        Object[] arrayOfObject = (Object[])DefaultLookup.get(paramJRootPane, this, "RootPane.defaultButtonWindowKeyBindings");
        if (arrayOfObject != null)
          LookAndFeel.loadKeyBindings(inputMap, arrayOfObject); 
      } 
    } 
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (paramPropertyChangeEvent.getPropertyName().equals("defaultButton")) {
      JRootPane jRootPane = (JRootPane)paramPropertyChangeEvent.getSource();
      updateDefaultButtonBindings(jRootPane);
      if (jRootPane.getClientProperty("temporaryDefaultButton") == null)
        jRootPane.putClientProperty("initialDefaultButton", paramPropertyChangeEvent.getNewValue()); 
    } 
  }
  
  static class Actions extends UIAction {
    public static final String PRESS = "press";
    
    public static final String RELEASE = "release";
    
    public static final String POST_POPUP = "postPopup";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JRootPane jRootPane = (JRootPane)param1ActionEvent.getSource();
      JButton jButton = jRootPane.getDefaultButton();
      String str = getName();
      if (str == "postPopup") {
        Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (component instanceof JComponent) {
          JComponent jComponent = (JComponent)component;
          JPopupMenu jPopupMenu = jComponent.getComponentPopupMenu();
          if (jPopupMenu != null) {
            Point point = jComponent.getPopupLocation(null);
            if (point == null) {
              Rectangle rectangle = jComponent.getVisibleRect();
              point = new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
            } 
            jPopupMenu.show(component, point.x, point.y);
          } 
        } 
      } else if (jButton != null && SwingUtilities.getRootPane(jButton) == jRootPane && str == "press") {
        jButton.doClick(20);
      } 
    }
    
    public boolean isEnabled(Object param1Object) {
      String str = getName();
      if (str == "postPopup") {
        MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
        if (arrayOfMenuElement != null && arrayOfMenuElement.length != 0)
          return false; 
        Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (component instanceof JComponent) {
          JComponent jComponent = (JComponent)component;
          return (jComponent.getComponentPopupMenu() != null);
        } 
        return false;
      } 
      if (param1Object != null && param1Object instanceof JRootPane) {
        JButton jButton = ((JRootPane)param1Object).getDefaultButton();
        return (jButton != null && jButton.getModel().isEnabled());
      } 
      return true;
    }
  }
  
  private static class RootPaneInputMap extends ComponentInputMapUIResource {
    public RootPaneInputMap(JComponent param1JComponent) { super(param1JComponent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicRootPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */