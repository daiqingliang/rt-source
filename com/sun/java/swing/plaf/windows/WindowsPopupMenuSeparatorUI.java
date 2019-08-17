package com.sun.java.swing.plaf.windows;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

public class WindowsPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsPopupMenuSeparatorUI(); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    Dimension dimension = paramJComponent.getSize();
    XPStyle xPStyle = XPStyle.getXP();
    if (WindowsMenuItemUI.isVistaPainting(xPStyle)) {
      int i = 1;
      Container container = paramJComponent.getParent();
      if (container instanceof JComponent) {
        Object object = ((JComponent)container).getClientProperty(WindowsPopupMenuUI.GUTTER_OFFSET_KEY);
        if (object instanceof Integer) {
          i = ((Integer)object).intValue() - paramJComponent.getX();
          i += WindowsPopupMenuUI.getGutterWidth();
        } 
      } 
      XPStyle.Skin skin = xPStyle.getSkin(paramJComponent, TMSchema.Part.MP_POPUPSEPARATOR);
      int j = skin.getHeight();
      int k = (dimension.height - j) / 2;
      skin.paintSkin(paramGraphics, i, k, dimension.width - i - 1, j, TMSchema.State.NORMAL);
    } else {
      int i = dimension.height / 2;
      paramGraphics.setColor(paramJComponent.getForeground());
      paramGraphics.drawLine(1, i - 1, dimension.width - 2, i - 1);
      paramGraphics.setColor(paramJComponent.getBackground());
      paramGraphics.drawLine(1, i, dimension.width - 2, i);
    } 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    int i = 0;
    Font font = paramJComponent.getFont();
    if (font != null)
      i = paramJComponent.getFontMetrics(font).getHeight(); 
    return new Dimension(0, i / 2 + 2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsPopupMenuSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */