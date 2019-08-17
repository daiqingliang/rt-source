package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.UIManager;
import sun.swing.SwingUtilities2;

public class WindowsGraphicsUtils {
  public static void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString, int paramInt) {
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(paramAbstractButton, paramGraphics);
    int i = paramAbstractButton.getDisplayedMnemonicIndex();
    if (WindowsLookAndFeel.isMnemonicHidden() == true)
      i = -1; 
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null && !(paramAbstractButton instanceof javax.swing.JMenuItem)) {
      paintXPText(paramAbstractButton, paramGraphics, paramRectangle.x + paramInt, paramRectangle.y + fontMetrics.getAscent() + paramInt, paramString, i);
    } else {
      paintClassicText(paramAbstractButton, paramGraphics, paramRectangle.x + paramInt, paramRectangle.y + fontMetrics.getAscent() + paramInt, paramString, i);
    } 
  }
  
  static void paintClassicText(AbstractButton paramAbstractButton, Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3) {
    ButtonModel buttonModel = paramAbstractButton.getModel();
    Color color = paramAbstractButton.getForeground();
    if (buttonModel.isEnabled()) {
      if ((!(paramAbstractButton instanceof javax.swing.JMenuItem) || !buttonModel.isArmed()) && (!(paramAbstractButton instanceof javax.swing.JMenu) || (!buttonModel.isSelected() && !buttonModel.isRollover())))
        paramGraphics.setColor(paramAbstractButton.getForeground()); 
      SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
    } else {
      color = UIManager.getColor("Button.shadow");
      Color color1 = UIManager.getColor("Button.disabledShadow");
      if (buttonModel.isArmed()) {
        color = UIManager.getColor("Button.disabledForeground");
      } else {
        if (color1 == null)
          color1 = paramAbstractButton.getBackground().darker(); 
        paramGraphics.setColor(color1);
        SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1 + 1, paramInt2 + 1);
      } 
      if (color == null)
        color = paramAbstractButton.getBackground().brighter(); 
      paramGraphics.setColor(color);
      SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
    } 
  }
  
  static void paintXPText(AbstractButton paramAbstractButton, Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3) {
    TMSchema.Part part = WindowsButtonUI.getXPButtonType(paramAbstractButton);
    TMSchema.State state = WindowsButtonUI.getXPButtonState(paramAbstractButton);
    paintXPText(paramAbstractButton, part, state, paramGraphics, paramInt1, paramInt2, paramString, paramInt3);
  }
  
  static void paintXPText(AbstractButton paramAbstractButton, TMSchema.Part paramPart, TMSchema.State paramState, Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle == null)
      return; 
    Color color = paramAbstractButton.getForeground();
    if (color instanceof javax.swing.plaf.UIResource) {
      color = xPStyle.getColor(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTCOLOR, paramAbstractButton.getForeground());
      if (paramPart == TMSchema.Part.TP_BUTTON && paramState == TMSchema.State.DISABLED) {
        Color color1 = xPStyle.getColor(paramAbstractButton, paramPart, TMSchema.State.NORMAL, TMSchema.Prop.TEXTCOLOR, paramAbstractButton.getForeground());
        if (color.equals(color1))
          color = xPStyle.getColor(paramAbstractButton, TMSchema.Part.BP_PUSHBUTTON, paramState, TMSchema.Prop.TEXTCOLOR, color); 
      } 
      TMSchema.TypeEnum typeEnum = xPStyle.getTypeEnum(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTSHADOWTYPE);
      if (typeEnum == TMSchema.TypeEnum.TST_SINGLE || typeEnum == TMSchema.TypeEnum.TST_CONTINUOUS) {
        Color color1 = xPStyle.getColor(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTSHADOWCOLOR, Color.black);
        Point point = xPStyle.getPoint(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTSHADOWOFFSET);
        if (point != null) {
          paramGraphics.setColor(color1);
          SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1 + point.x, paramInt2 + point.y);
        } 
      } 
    } 
    paramGraphics.setColor(color);
    SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
  }
  
  static boolean isLeftToRight(Component paramComponent) { return paramComponent.getComponentOrientation().isLeftToRight(); }
  
  static void repaintMnemonicsInWindow(Window paramWindow) {
    if (paramWindow == null || !paramWindow.isShowing())
      return; 
    Window[] arrayOfWindow = paramWindow.getOwnedWindows();
    for (byte b = 0; b < arrayOfWindow.length; b++)
      repaintMnemonicsInWindow(arrayOfWindow[b]); 
    repaintMnemonicsInContainer(paramWindow);
  }
  
  static void repaintMnemonicsInContainer(Container paramContainer) {
    for (byte b = 0; b < paramContainer.getComponentCount(); b++) {
      Component component = paramContainer.getComponent(b);
      if (component != null && component.isVisible())
        if (component instanceof AbstractButton && ((AbstractButton)component).getMnemonic() != 0) {
          component.repaint();
        } else if (component instanceof JLabel && ((JLabel)component).getDisplayedMnemonic() != 0) {
          component.repaint();
        } else if (component instanceof Container) {
          repaintMnemonicsInContainer((Container)component);
        }  
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsGraphicsUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */