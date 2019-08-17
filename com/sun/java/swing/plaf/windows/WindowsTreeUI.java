package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class WindowsTreeUI extends BasicTreeUI {
  protected static final int HALF_SIZE = 4;
  
  protected static final int SIZE = 9;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsTreeUI(); }
  
  protected void ensureRowsAreVisible(int paramInt1, int paramInt2) {
    if (this.tree != null && paramInt1 >= 0 && paramInt2 < getRowCount(this.tree)) {
      Rectangle rectangle = this.tree.getVisibleRect();
      if (paramInt1 == paramInt2) {
        Rectangle rectangle1 = getPathBounds(this.tree, getPathForRow(this.tree, paramInt1));
        if (rectangle1 != null) {
          rectangle1.x = rectangle.x;
          rectangle1.width = rectangle.width;
          this.tree.scrollRectToVisible(rectangle1);
        } 
      } else {
        Rectangle rectangle1 = getPathBounds(this.tree, getPathForRow(this.tree, paramInt1));
        if (rectangle1 != null) {
          Rectangle rectangle2 = rectangle1;
          int i = rectangle1.y;
          int j = i + rectangle.height;
          for (int k = paramInt1 + 1; k <= paramInt2; k++) {
            rectangle2 = getPathBounds(this.tree, getPathForRow(this.tree, k));
            if (rectangle2 != null && rectangle2.y + rectangle2.height > j)
              k = paramInt2; 
          } 
          if (rectangle2 == null)
            return; 
          this.tree.scrollRectToVisible(new Rectangle(rectangle.x, i, 1, rectangle2.y + rectangle2.height - i));
        } 
      } 
    } 
  }
  
  protected TreeCellRenderer createDefaultCellRenderer() { return new WindowsTreeCellRenderer(); }
  
  public static class CollapsedIcon extends ExpandedIcon {
    public static Icon createCollapsedIcon() { return new CollapsedIcon(); }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      XPStyle.Skin skin = getSkin(param1Component);
      if (skin != null) {
        skin.paintSkin(param1Graphics, param1Int1, param1Int2, TMSchema.State.CLOSED);
      } else {
        super.paintIcon(param1Component, param1Graphics, param1Int1, param1Int2);
        param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 2, param1Int1 + 4, param1Int2 + 6);
      } 
    }
  }
  
  public static class ExpandedIcon implements Icon, Serializable {
    public static Icon createExpandedIcon() { return new ExpandedIcon(); }
    
    XPStyle.Skin getSkin(Component param1Component) {
      XPStyle xPStyle = XPStyle.getXP();
      return (xPStyle != null) ? xPStyle.getSkin(param1Component, TMSchema.Part.TVP_GLYPH) : null;
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      XPStyle.Skin skin = getSkin(param1Component);
      if (skin != null) {
        skin.paintSkin(param1Graphics, param1Int1, param1Int2, TMSchema.State.OPENED);
        return;
      } 
      Color color = param1Component.getBackground();
      if (color != null) {
        param1Graphics.setColor(color);
      } else {
        param1Graphics.setColor(Color.white);
      } 
      param1Graphics.fillRect(param1Int1, param1Int2, 8, 8);
      param1Graphics.setColor(Color.gray);
      param1Graphics.drawRect(param1Int1, param1Int2, 8, 8);
      param1Graphics.setColor(Color.black);
      param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 4, param1Int1 + 6, param1Int2 + 4);
    }
    
    public int getIconWidth() {
      XPStyle.Skin skin = getSkin(null);
      return (skin != null) ? skin.getWidth() : 9;
    }
    
    public int getIconHeight() {
      XPStyle.Skin skin = getSkin(null);
      return (skin != null) ? skin.getHeight() : 9;
    }
  }
  
  public class WindowsTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree param1JTree, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3, int param1Int, boolean param1Boolean4) {
      super.getTreeCellRendererComponent(param1JTree, param1Object, param1Boolean1, param1Boolean2, param1Boolean3, param1Int, param1Boolean4);
      if (!param1JTree.isEnabled()) {
        setEnabled(false);
        if (param1Boolean3) {
          setDisabledIcon(getLeafIcon());
        } else if (param1Boolean1) {
          setDisabledIcon(getOpenIcon());
        } else {
          setDisabledIcon(getClosedIcon());
        } 
      } else {
        setEnabled(true);
        if (param1Boolean3) {
          setIcon(getLeafIcon());
        } else if (param1Boolean1) {
          setIcon(getOpenIcon());
        } else {
          setIcon(getClosedIcon());
        } 
      } 
      return this;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */