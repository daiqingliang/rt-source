package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeCellRenderer;

public class MotifTreeUI extends BasicTreeUI {
  static final int HALF_SIZE = 7;
  
  static final int SIZE = 14;
  
  public void installUI(JComponent paramJComponent) { super.installUI(paramJComponent); }
  
  protected void paintVerticalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3) {
    if (this.tree.getComponentOrientation().isLeftToRight()) {
      paramGraphics.fillRect(paramInt1, paramInt2, 2, paramInt3 - paramInt2 + 2);
    } else {
      paramGraphics.fillRect(paramInt1 - 1, paramInt2, 2, paramInt3 - paramInt2 + 2);
    } 
  }
  
  protected void paintHorizontalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3) { paramGraphics.fillRect(paramInt2, paramInt1, paramInt3 - paramInt2 + 1, 2); }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifTreeUI(); }
  
  public TreeCellRenderer createDefaultCellRenderer() { return new MotifTreeCellRenderer(); }
  
  public static class MotifCollapsedIcon extends MotifExpandedIcon {
    public static Icon createCollapsedIcon() { return new MotifCollapsedIcon(); }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      super.paintIcon(param1Component, param1Graphics, param1Int1, param1Int2);
      param1Graphics.drawLine(param1Int1 + 7 - 1, param1Int2 + 3, param1Int1 + 7 - 1, param1Int2 + 10);
      param1Graphics.drawLine(param1Int1 + 7, param1Int2 + 3, param1Int1 + 7, param1Int2 + 10);
    }
  }
  
  public static class MotifExpandedIcon implements Icon, Serializable {
    static Color bg;
    
    static Color fg;
    
    static Color highlight;
    
    static Color shadow;
    
    public MotifExpandedIcon() {
      bg = UIManager.getColor("Tree.iconBackground");
      fg = UIManager.getColor("Tree.iconForeground");
      highlight = UIManager.getColor("Tree.iconHighlight");
      shadow = UIManager.getColor("Tree.iconShadow");
    }
    
    public static Icon createExpandedIcon() { return new MotifExpandedIcon(); }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.setColor(highlight);
      param1Graphics.drawLine(param1Int1, param1Int2, param1Int1 + 14 - 1, param1Int2);
      param1Graphics.drawLine(param1Int1, param1Int2 + 1, param1Int1, param1Int2 + 14 - 1);
      param1Graphics.setColor(shadow);
      param1Graphics.drawLine(param1Int1 + 14 - 1, param1Int2 + 1, param1Int1 + 14 - 1, param1Int2 + 14 - 1);
      param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 14 - 1, param1Int1 + 14 - 1, param1Int2 + 14 - 1);
      param1Graphics.setColor(bg);
      param1Graphics.fillRect(param1Int1 + 1, param1Int2 + 1, 12, 12);
      param1Graphics.setColor(fg);
      param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 7 - 1, param1Int1 + 14 - 4, param1Int2 + 7 - 1);
      param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 7, param1Int1 + 14 - 4, param1Int2 + 7);
    }
    
    public int getIconWidth() { return 14; }
    
    public int getIconHeight() { return 14; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */