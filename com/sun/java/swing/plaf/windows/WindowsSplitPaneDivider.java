package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class WindowsSplitPaneDivider extends BasicSplitPaneDivider {
  public WindowsSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI) { super(paramBasicSplitPaneUI); }
  
  public void paint(Graphics paramGraphics) {
    Color color = this.splitPane.hasFocus() ? UIManager.getColor("SplitPane.shadow") : getBackground();
    Dimension dimension = getSize();
    if (color != null) {
      paramGraphics.setColor(color);
      paramGraphics.fillRect(0, 0, dimension.width, dimension.height);
    } 
    super.paint(paramGraphics);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */