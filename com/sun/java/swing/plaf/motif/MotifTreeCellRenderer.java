package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MotifTreeCellRenderer extends DefaultTreeCellRenderer {
  static final int LEAF_SIZE = 13;
  
  static final Icon LEAF_ICON = new IconUIResource(new TreeLeafIcon());
  
  public static Icon loadLeafIcon() { return LEAF_ICON; }
  
  public static class TreeLeafIcon implements Icon, Serializable {
    Color bg = UIManager.getColor("Tree.iconBackground");
    
    Color shadow = UIManager.getColor("Tree.iconShadow");
    
    Color highlight = UIManager.getColor("Tree.iconHighlight");
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.setColor(this.bg);
      param1Int2 -= 3;
      param1Graphics.fillRect(param1Int1 + 4, param1Int2 + 7, 5, 5);
      param1Graphics.drawLine(param1Int1 + 6, param1Int2 + 6, param1Int1 + 6, param1Int2 + 6);
      param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 9, param1Int1 + 3, param1Int2 + 9);
      param1Graphics.drawLine(param1Int1 + 6, param1Int2 + 12, param1Int1 + 6, param1Int2 + 12);
      param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 9, param1Int1 + 9, param1Int2 + 9);
      param1Graphics.setColor(this.highlight);
      param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 9, param1Int1 + 5, param1Int2 + 6);
      param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 10, param1Int1 + 5, param1Int2 + 12);
      param1Graphics.setColor(this.shadow);
      param1Graphics.drawLine(param1Int1 + 6, param1Int2 + 13, param1Int1 + 10, param1Int2 + 9);
      param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 8, param1Int1 + 7, param1Int2 + 6);
    }
    
    public int getIconWidth() { return 13; }
    
    public int getIconHeight() { return 13; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifTreeCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */