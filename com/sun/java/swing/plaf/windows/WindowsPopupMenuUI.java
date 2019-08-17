package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.SwingUtilities2;

public class WindowsPopupMenuUI extends BasicPopupMenuUI {
  static MnemonicListener mnemonicListener = null;
  
  static final Object GUTTER_OFFSET_KEY = new StringUIClientPropertyKey("GUTTER_OFFSET_KEY");
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsPopupMenuUI(); }
  
  public void installListeners() {
    super.installListeners();
    if (!UIManager.getBoolean("Button.showMnemonics") && mnemonicListener == null) {
      mnemonicListener = new MnemonicListener();
      MenuSelectionManager.defaultManager().addChangeListener(mnemonicListener);
    } 
  }
  
  public Popup getPopup(JPopupMenu paramJPopupMenu, int paramInt1, int paramInt2) {
    PopupFactory popupFactory = PopupFactory.getSharedInstance();
    return popupFactory.getPopup(paramJPopupMenu.getInvoker(), paramJPopupMenu, paramInt1, paramInt2);
  }
  
  static int getTextOffset(JComponent paramJComponent) {
    int i = -1;
    Object object = paramJComponent.getClientProperty(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET);
    if (object instanceof Integer) {
      i = ((Integer)object).intValue();
      int j = 0;
      Component component = paramJComponent.getComponent(0);
      if (component != null)
        j = component.getX(); 
      i += j;
    } 
    return i;
  }
  
  static int getSpanBeforeGutter() { return 3; }
  
  static int getSpanAfterGutter() { return 3; }
  
  static int getGutterWidth() {
    int i = 2;
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      XPStyle.Skin skin = xPStyle.getSkin(null, TMSchema.Part.MP_POPUPGUTTER);
      i = skin.getWidth();
    } 
    return i;
  }
  
  private static boolean isLeftToRight(JComponent paramJComponent) {
    boolean bool = true;
    for (int i = paramJComponent.getComponentCount() - 1; i >= 0 && bool; i--)
      bool = paramJComponent.getComponent(i).getComponentOrientation().isLeftToRight(); 
    return bool;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    XPStyle xPStyle = XPStyle.getXP();
    if (WindowsMenuItemUI.isVistaPainting(xPStyle)) {
      XPStyle.Skin skin = xPStyle.getSkin(paramJComponent, TMSchema.Part.MP_POPUPBACKGROUND);
      skin.paintSkin(paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), TMSchema.State.NORMAL);
      int i = getTextOffset(paramJComponent);
      if (i >= 0 && isLeftToRight(paramJComponent)) {
        skin = xPStyle.getSkin(paramJComponent, TMSchema.Part.MP_POPUPGUTTER);
        int j = getGutterWidth();
        int k = i - getSpanAfterGutter() - j;
        paramJComponent.putClientProperty(GUTTER_OFFSET_KEY, Integer.valueOf(k));
        Insets insets = paramJComponent.getInsets();
        skin.paintSkin(paramGraphics, k, insets.top, j, paramJComponent.getHeight() - insets.bottom - insets.top, TMSchema.State.NORMAL);
      } else if (paramJComponent.getClientProperty(GUTTER_OFFSET_KEY) != null) {
        paramJComponent.putClientProperty(GUTTER_OFFSET_KEY, null);
      } 
    } else {
      super.paint(paramGraphics, paramJComponent);
    } 
  }
  
  static class MnemonicListener implements ChangeListener {
    JRootPane repaintRoot = null;
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      MenuSelectionManager menuSelectionManager = (MenuSelectionManager)param1ChangeEvent.getSource();
      MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
      if (arrayOfMenuElement.length == 0) {
        if (!WindowsLookAndFeel.isMnemonicHidden()) {
          WindowsLookAndFeel.setMnemonicHidden(true);
          if (this.repaintRoot != null) {
            Window window = SwingUtilities.getWindowAncestor(this.repaintRoot);
            WindowsGraphicsUtils.repaintMnemonicsInWindow(window);
          } 
        } 
      } else {
        Component component = (Component)arrayOfMenuElement[0];
        if (component instanceof JPopupMenu)
          component = ((JPopupMenu)component).getInvoker(); 
        this.repaintRoot = SwingUtilities.getRootPane(component);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsPopupMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */