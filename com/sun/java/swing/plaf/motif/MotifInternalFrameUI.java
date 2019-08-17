package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class MotifInternalFrameUI extends BasicInternalFrameUI {
  Color color;
  
  Color highlight;
  
  Color shadow;
  
  MotifInternalFrameTitlePane titlePane;
  
  @Deprecated
  protected KeyStroke closeMenuKey;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifInternalFrameUI((JInternalFrame)paramJComponent); }
  
  public MotifInternalFrameUI(JInternalFrame paramJInternalFrame) { super(paramJInternalFrame); }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    setColors((JInternalFrame)paramJComponent);
  }
  
  protected void installDefaults() {
    Border border = this.frame.getBorder();
    this.frame.setLayout(this.internalFrameLayout = createLayoutManager());
    if (border == null || border instanceof javax.swing.plaf.UIResource)
      this.frame.setBorder(new MotifBorders.InternalFrameBorder(this.frame)); 
  }
  
  protected void installKeyboardActions() {
    super.installKeyboardActions();
    this.closeMenuKey = KeyStroke.getKeyStroke(27, 0);
  }
  
  protected void uninstallDefaults() {
    LookAndFeel.uninstallBorder(this.frame);
    this.frame.setLayout(null);
    this.internalFrameLayout = null;
  }
  
  private JInternalFrame getFrame() { return this.frame; }
  
  public JComponent createNorthPane(JInternalFrame paramJInternalFrame) {
    this.titlePane = new MotifInternalFrameTitlePane(paramJInternalFrame);
    return this.titlePane;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return Toolkit.getDefaultToolkit().getScreenSize(); }
  
  protected void uninstallKeyboardActions() {
    super.uninstallKeyboardActions();
    if (isKeyBindingRegistered()) {
      JInternalFrame.JDesktopIcon jDesktopIcon = this.frame.getDesktopIcon();
      SwingUtilities.replaceUIActionMap(jDesktopIcon, null);
      SwingUtilities.replaceUIInputMap(jDesktopIcon, 2, null);
    } 
  }
  
  protected void setupMenuOpenKey() {
    super.setupMenuOpenKey();
    ActionMap actionMap = SwingUtilities.getUIActionMap(this.frame);
    if (actionMap != null)
      actionMap.put("showSystemMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent param1ActionEvent) { MotifInternalFrameUI.this.titlePane.showSystemMenu(); }
            
            public boolean isEnabled() { return MotifInternalFrameUI.this.isKeyBindingActive(); }
          }); 
  }
  
  protected void setupMenuCloseKey() {
    ActionMap actionMap1 = SwingUtilities.getUIActionMap(this.frame);
    if (actionMap1 != null)
      actionMap1.put("hideSystemMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent param1ActionEvent) { MotifInternalFrameUI.this.titlePane.hideSystemMenu(); }
            
            public boolean isEnabled() { return MotifInternalFrameUI.this.isKeyBindingActive(); }
          }); 
    JInternalFrame.JDesktopIcon jDesktopIcon = this.frame.getDesktopIcon();
    InputMap inputMap = SwingUtilities.getUIInputMap(jDesktopIcon, 2);
    if (inputMap == null) {
      Object[] arrayOfObject = (Object[])UIManager.get("DesktopIcon.windowBindings");
      if (arrayOfObject != null) {
        inputMap = LookAndFeel.makeComponentInputMap(jDesktopIcon, arrayOfObject);
        SwingUtilities.replaceUIInputMap(jDesktopIcon, 2, inputMap);
      } 
    } 
    ActionMap actionMap2 = SwingUtilities.getUIActionMap(jDesktopIcon);
    if (actionMap2 == null) {
      actionMap2 = new ActionMapUIResource();
      actionMap2.put("hideSystemMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent param1ActionEvent) {
              JInternalFrame.JDesktopIcon jDesktopIcon = MotifInternalFrameUI.this.getFrame().getDesktopIcon();
              MotifDesktopIconUI motifDesktopIconUI = (MotifDesktopIconUI)jDesktopIcon.getUI();
              motifDesktopIconUI.hideSystemMenu();
            }
            
            public boolean isEnabled() { return MotifInternalFrameUI.this.isKeyBindingActive(); }
          });
      SwingUtilities.replaceUIActionMap(jDesktopIcon, actionMap2);
    } 
  }
  
  protected void activateFrame(JInternalFrame paramJInternalFrame) {
    super.activateFrame(paramJInternalFrame);
    setColors(paramJInternalFrame);
  }
  
  protected void deactivateFrame(JInternalFrame paramJInternalFrame) {
    setColors(paramJInternalFrame);
    super.deactivateFrame(paramJInternalFrame);
  }
  
  void setColors(JInternalFrame paramJInternalFrame) {
    if (paramJInternalFrame.isSelected()) {
      this.color = UIManager.getColor("InternalFrame.activeTitleBackground");
    } else {
      this.color = UIManager.getColor("InternalFrame.inactiveTitleBackground");
    } 
    this.highlight = this.color.brighter();
    this.shadow = this.color.darker().darker();
    this.titlePane.setColors(this.color, this.highlight, this.shadow);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifInternalFrameUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */