package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.KeyEventPostProcessor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public class WindowsRootPaneUI extends BasicRootPaneUI {
  private static final WindowsRootPaneUI windowsRootPaneUI = new WindowsRootPaneUI();
  
  static final AltProcessor altProcessor = new AltProcessor();
  
  public static ComponentUI createUI(JComponent paramJComponent) { return windowsRootPaneUI; }
  
  static class AltProcessor implements KeyEventPostProcessor {
    static boolean altKeyPressed = false;
    
    static boolean menuCanceledOnPress = false;
    
    static JRootPane root = null;
    
    static Window winAncestor = null;
    
    void altPressed(KeyEvent param1KeyEvent) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
      if (arrayOfMenuElement.length > 0 && !(arrayOfMenuElement[0] instanceof javax.swing.plaf.basic.ComboPopup)) {
        menuSelectionManager.clearSelectedPath();
        menuCanceledOnPress = true;
        param1KeyEvent.consume();
      } else if (arrayOfMenuElement.length > 0) {
        menuCanceledOnPress = false;
        WindowsLookAndFeel.setMnemonicHidden(false);
        WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
        param1KeyEvent.consume();
      } else {
        menuCanceledOnPress = false;
        WindowsLookAndFeel.setMnemonicHidden(false);
        WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
        JMenuBar jMenuBar = (root != null) ? root.getJMenuBar() : null;
        if (jMenuBar == null && winAncestor instanceof JFrame)
          jMenuBar = ((JFrame)winAncestor).getJMenuBar(); 
        JMenu jMenu = (jMenuBar != null) ? jMenuBar.getMenu(0) : null;
        if (jMenu != null)
          param1KeyEvent.consume(); 
      } 
    }
    
    void altReleased(KeyEvent param1KeyEvent) {
      if (menuCanceledOnPress) {
        WindowsLookAndFeel.setMnemonicHidden(true);
        WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
        return;
      } 
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      if (menuSelectionManager.getSelectedPath().length == 0) {
        JMenuBar jMenuBar = (root != null) ? root.getJMenuBar() : null;
        if (jMenuBar == null && winAncestor instanceof JFrame)
          jMenuBar = ((JFrame)winAncestor).getJMenuBar(); 
        JMenu jMenu = (jMenuBar != null) ? jMenuBar.getMenu(0) : null;
        boolean bool = false;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        if (toolkit instanceof SunToolkit) {
          Component component = AWTAccessor.getKeyEventAccessor().getOriginalSource(param1KeyEvent);
          bool = (SunToolkit.getContainingWindow(component) != winAncestor || param1KeyEvent.getWhen() <= ((SunToolkit)toolkit).getWindowDeactivationTime(winAncestor)) ? 1 : 0;
        } 
        if (jMenu != null && !bool) {
          MenuElement[] arrayOfMenuElement = new MenuElement[2];
          arrayOfMenuElement[0] = jMenuBar;
          arrayOfMenuElement[1] = jMenu;
          menuSelectionManager.setSelectedPath(arrayOfMenuElement);
        } else if (!WindowsLookAndFeel.isMnemonicHidden()) {
          WindowsLookAndFeel.setMnemonicHidden(true);
          WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
        } 
      } else if (menuSelectionManager.getSelectedPath()[0] instanceof javax.swing.plaf.basic.ComboPopup) {
        WindowsLookAndFeel.setMnemonicHidden(true);
        WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
      } 
    }
    
    public boolean postProcessKeyEvent(KeyEvent param1KeyEvent) {
      if (param1KeyEvent.isConsumed() && param1KeyEvent.getKeyCode() != 18) {
        altKeyPressed = false;
        return false;
      } 
      if (param1KeyEvent.getKeyCode() == 18) {
        root = SwingUtilities.getRootPane(param1KeyEvent.getComponent());
        winAncestor = (root == null) ? null : SwingUtilities.getWindowAncestor(root);
        if (param1KeyEvent.getID() == 401) {
          if (!altKeyPressed)
            altPressed(param1KeyEvent); 
          altKeyPressed = true;
          return true;
        } 
        if (param1KeyEvent.getID() == 402) {
          if (altKeyPressed) {
            altReleased(param1KeyEvent);
          } else {
            MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
            MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
            if (arrayOfMenuElement.length <= 0) {
              WindowsLookAndFeel.setMnemonicHidden(true);
              WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
            } 
          } 
          altKeyPressed = false;
        } 
        root = null;
        winAncestor = null;
      } else {
        altKeyPressed = false;
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsRootPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */