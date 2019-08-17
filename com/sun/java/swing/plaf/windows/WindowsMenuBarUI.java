package com.sun.java.swing.plaf.windows;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class WindowsMenuBarUI extends BasicMenuBarUI {
  private WindowListener windowListener = null;
  
  private HierarchyListener hierarchyListener = null;
  
  private Window window = null;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsMenuBarUI(); }
  
  protected void uninstallListeners() {
    uninstallWindowListener();
    if (this.hierarchyListener != null) {
      this.menuBar.removeHierarchyListener(this.hierarchyListener);
      this.hierarchyListener = null;
    } 
    super.uninstallListeners();
  }
  
  private void installWindowListener() {
    if (this.windowListener == null) {
      Container container = this.menuBar.getTopLevelAncestor();
      if (container instanceof Window) {
        this.window = (Window)container;
        this.windowListener = new WindowAdapter() {
            public void windowActivated(WindowEvent param1WindowEvent) { WindowsMenuBarUI.this.menuBar.repaint(); }
            
            public void windowDeactivated(WindowEvent param1WindowEvent) { WindowsMenuBarUI.this.menuBar.repaint(); }
          };
        ((Window)container).addWindowListener(this.windowListener);
      } 
    } 
  }
  
  private void uninstallWindowListener() {
    if (this.windowListener != null && this.window != null)
      this.window.removeWindowListener(this.windowListener); 
    this.window = null;
    this.windowListener = null;
  }
  
  protected void installListeners() {
    if (WindowsLookAndFeel.isOnVista()) {
      installWindowListener();
      this.hierarchyListener = new HierarchyListener() {
          public void hierarchyChanged(HierarchyEvent param1HierarchyEvent) {
            if ((param1HierarchyEvent.getChangeFlags() & 0x2L) != 0L)
              if (WindowsMenuBarUI.this.menuBar.isDisplayable()) {
                WindowsMenuBarUI.this.installWindowListener();
              } else {
                WindowsMenuBarUI.this.uninstallWindowListener();
              }  
          }
        };
      this.menuBar.addHierarchyListener(this.hierarchyListener);
    } 
    super.installListeners();
  }
  
  protected void installKeyboardActions() {
    super.installKeyboardActions();
    ActionMap actionMap = SwingUtilities.getUIActionMap(this.menuBar);
    if (actionMap == null) {
      actionMap = new ActionMapUIResource();
      SwingUtilities.replaceUIActionMap(this.menuBar, actionMap);
    } 
    actionMap.put("takeFocus", new TakeFocus(null));
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    XPStyle xPStyle = XPStyle.getXP();
    if (WindowsMenuItemUI.isVistaPainting(xPStyle)) {
      XPStyle.Skin skin = xPStyle.getSkin(paramJComponent, TMSchema.Part.MP_BARBACKGROUND);
      int i = paramJComponent.getWidth();
      int j = paramJComponent.getHeight();
      TMSchema.State state = isActive(paramJComponent) ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
      skin.paintSkin(paramGraphics, 0, 0, i, j, state);
    } else {
      super.paint(paramGraphics, paramJComponent);
    } 
  }
  
  static boolean isActive(JComponent paramJComponent) {
    JRootPane jRootPane = paramJComponent.getRootPane();
    if (jRootPane != null) {
      Container container = jRootPane.getParent();
      if (container instanceof Window)
        return ((Window)container).isActive(); 
    } 
    return true;
  }
  
  private static class TakeFocus extends AbstractAction {
    private TakeFocus() {}
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JMenuBar jMenuBar = (JMenuBar)param1ActionEvent.getSource();
      JMenu jMenu = jMenuBar.getMenu(0);
      if (jMenu != null) {
        MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
        MenuElement[] arrayOfMenuElement = new MenuElement[2];
        arrayOfMenuElement[0] = jMenuBar;
        arrayOfMenuElement[1] = jMenu;
        menuSelectionManager.setSelectedPath(arrayOfMenuElement);
        WindowsLookAndFeel.setMnemonicHidden(false);
        WindowsLookAndFeel.repaintRootPane(jMenuBar);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsMenuBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */