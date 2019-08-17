package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

public class MotifOptionPaneUI extends BasicOptionPaneUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifOptionPaneUI(); }
  
  protected Container createButtonArea() {
    Container container = super.createButtonArea();
    if (container != null && container.getLayout() instanceof BasicOptionPaneUI.ButtonAreaLayout)
      ((BasicOptionPaneUI.ButtonAreaLayout)container.getLayout()).setCentersChildren(false); 
    return container;
  }
  
  public Dimension getMinimumOptionPaneSize() { return null; }
  
  protected Container createSeparator() { return new JPanel() {
        public Dimension getPreferredSize() { return new Dimension(10, 2); }
        
        public void paint(Graphics param1Graphics) {
          int i = getWidth();
          param1Graphics.setColor(Color.darkGray);
          param1Graphics.drawLine(0, 0, i, 0);
          param1Graphics.setColor(Color.white);
          param1Graphics.drawLine(0, 1, i, 1);
        }
      }; }
  
  protected void addIcon(Container paramContainer) {
    Icon icon = getIcon();
    if (icon != null) {
      JLabel jLabel = new JLabel(icon);
      jLabel.setVerticalAlignment(0);
      paramContainer.add(jLabel, "West");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifOptionPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */