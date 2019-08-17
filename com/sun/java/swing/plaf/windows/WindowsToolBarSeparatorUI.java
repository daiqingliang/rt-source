package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

public class WindowsToolBarSeparatorUI extends BasicToolBarSeparatorUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsToolBarSeparatorUI(); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension = ((JToolBar.Separator)paramJComponent).getSeparatorSize();
    if (dimension != null) {
      dimension = dimension.getSize();
    } else {
      dimension = new Dimension(6, 6);
      XPStyle xPStyle = XPStyle.getXP();
      if (xPStyle != null) {
        boolean bool = (((JSeparator)paramJComponent).getOrientation() == 1) ? 1 : 0;
        TMSchema.Part part = bool ? TMSchema.Part.TP_SEPARATOR : TMSchema.Part.TP_SEPARATORVERT;
        XPStyle.Skin skin = xPStyle.getSkin(paramJComponent, part);
        dimension.width = skin.getWidth();
        dimension.height = skin.getHeight();
      } 
      if (((JSeparator)paramJComponent).getOrientation() == 1) {
        dimension.height = 0;
      } else {
        dimension.width = 0;
      } 
    } 
    return dimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    Dimension dimension = getPreferredSize(paramJComponent);
    return (((JSeparator)paramJComponent).getOrientation() == 1) ? new Dimension(dimension.width, 32767) : new Dimension(32767, dimension.height);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    boolean bool = (((JSeparator)paramJComponent).getOrientation() == 1) ? 1 : 0;
    Dimension dimension = paramJComponent.getSize();
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      TMSchema.Part part = bool ? TMSchema.Part.TP_SEPARATOR : TMSchema.Part.TP_SEPARATORVERT;
      XPStyle.Skin skin = xPStyle.getSkin(paramJComponent, part);
      int i = bool ? ((dimension.width - skin.getWidth()) / 2) : 0;
      byte b = bool ? 0 : ((dimension.height - skin.getHeight()) / 2);
      int j = bool ? skin.getWidth() : dimension.width;
      int k = bool ? dimension.height : skin.getHeight();
      skin.paintSkin(paramGraphics, i, b, j, k, null);
    } else {
      Color color1 = paramGraphics.getColor();
      UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
      Color color2 = uIDefaults.getColor("ToolBar.shadow");
      Color color3 = uIDefaults.getColor("ToolBar.highlight");
      if (bool) {
        int i = dimension.width / 2 - 1;
        paramGraphics.setColor(color2);
        paramGraphics.drawLine(i, 2, i, dimension.height - 2);
        paramGraphics.setColor(color3);
        paramGraphics.drawLine(i + 1, 2, i + 1, dimension.height - 2);
      } else {
        int i = dimension.height / 2 - 1;
        paramGraphics.setColor(color2);
        paramGraphics.drawLine(2, i, dimension.width - 2, i);
        paramGraphics.setColor(color3);
        paramGraphics.drawLine(2, i + 1, dimension.width - 2, i + 1);
      } 
      paramGraphics.setColor(color1);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsToolBarSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */