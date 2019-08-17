package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class WindowsTabbedPaneUI extends BasicTabbedPaneUI {
  private static Set<KeyStroke> managingFocusForwardTraversalKeys;
  
  private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
  
  private boolean contentOpaque = true;
  
  protected void installDefaults() {
    super.installDefaults();
    this.contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
    if (managingFocusForwardTraversalKeys == null) {
      managingFocusForwardTraversalKeys = new HashSet();
      managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 0));
    } 
    this.tabPane.setFocusTraversalKeys(0, managingFocusForwardTraversalKeys);
    if (managingFocusBackwardTraversalKeys == null) {
      managingFocusBackwardTraversalKeys = new HashSet();
      managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 1));
    } 
    this.tabPane.setFocusTraversalKeys(1, managingFocusBackwardTraversalKeys);
  }
  
  protected void uninstallDefaults() {
    this.tabPane.setFocusTraversalKeys(0, null);
    this.tabPane.setFocusTraversalKeys(1, null);
    super.uninstallDefaults();
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsTabbedPaneUI(); }
  
  protected void setRolloverTab(int paramInt) {
    if (XPStyle.getXP() != null) {
      int i = getRolloverTab();
      super.setRolloverTab(paramInt);
      Rectangle rectangle1 = null;
      Rectangle rectangle2 = null;
      if (i >= 0 && i < this.tabPane.getTabCount())
        rectangle1 = getTabBounds(this.tabPane, i); 
      if (paramInt >= 0)
        rectangle2 = getTabBounds(this.tabPane, paramInt); 
      if (rectangle1 != null) {
        if (rectangle2 != null) {
          this.tabPane.repaint(rectangle1.union(rectangle2));
        } else {
          this.tabPane.repaint(rectangle1);
        } 
      } else if (rectangle2 != null) {
        this.tabPane.repaint(rectangle2);
      } 
    } 
  }
  
  protected void paintContentBorder(Graphics paramGraphics, int paramInt1, int paramInt2) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null && (this.contentOpaque || this.tabPane.isOpaque())) {
      XPStyle.Skin skin = xPStyle.getSkin(this.tabPane, TMSchema.Part.TABP_PANE);
      if (skin != null) {
        Insets insets1 = this.tabPane.getInsets();
        Insets insets2 = UIManager.getInsets("TabbedPane.tabAreaInsets");
        int i = insets1.left;
        int j = insets1.top;
        int k = this.tabPane.getWidth() - insets1.right - insets1.left;
        int m = this.tabPane.getHeight() - insets1.top - insets1.bottom;
        if (paramInt1 == 2 || paramInt1 == 4) {
          int n = calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth);
          if (paramInt1 == 2)
            i += n - insets2.bottom; 
          k -= n - insets2.bottom;
        } else {
          int n = calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight);
          if (paramInt1 == 1)
            j += n - insets2.bottom; 
          m -= n - insets2.bottom;
        } 
        paintRotatedSkin(paramGraphics, skin, paramInt1, i, j, k, m, null);
        return;
      } 
    } 
    super.paintContentBorder(paramGraphics, paramInt1, paramInt2);
  }
  
  protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
    if (XPStyle.getXP() == null)
      super.paintTabBackground(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean); 
  }
  
  protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      TMSchema.Part part;
      int i = this.tabPane.getTabCount();
      int j = getRunForTab(i, paramInt2);
      if (this.tabRuns[j] == paramInt2) {
        part = TMSchema.Part.TABP_TABITEMLEFTEDGE;
      } else if (i > 1 && lastTabInRun(i, j) == paramInt2) {
        part = TMSchema.Part.TABP_TABITEMRIGHTEDGE;
        if (paramBoolean)
          if (paramInt1 == 1 || paramInt1 == 3) {
            paramInt5++;
          } else {
            paramInt6++;
          }  
      } else {
        part = TMSchema.Part.TABP_TABITEM;
      } 
      TMSchema.State state = TMSchema.State.NORMAL;
      if (paramBoolean) {
        state = TMSchema.State.SELECTED;
      } else if (paramInt2 == getRolloverTab()) {
        state = TMSchema.State.HOT;
      } 
      paintRotatedSkin(paramGraphics, xPStyle.getSkin(this.tabPane, part), paramInt1, paramInt3, paramInt4, paramInt5, paramInt6, state);
    } else {
      super.paintTabBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean);
    } 
  }
  
  private void paintRotatedSkin(Graphics paramGraphics, XPStyle.Skin paramSkin, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, TMSchema.State paramState) {
    Graphics2D graphics2D = (Graphics2D)paramGraphics.create();
    graphics2D.translate(paramInt2, paramInt3);
    switch (paramInt1) {
      case 4:
        graphics2D.translate(paramInt4, 0);
        graphics2D.rotate(Math.toRadians(90.0D));
        paramSkin.paintSkin(graphics2D, 0, 0, paramInt5, paramInt4, paramState);
        break;
      case 2:
        graphics2D.scale(-1.0D, 1.0D);
        graphics2D.rotate(Math.toRadians(90.0D));
        paramSkin.paintSkin(graphics2D, 0, 0, paramInt5, paramInt4, paramState);
        break;
      case 3:
        graphics2D.translate(0, paramInt5);
        graphics2D.scale(-1.0D, 1.0D);
        graphics2D.rotate(Math.toRadians(180.0D));
        paramSkin.paintSkin(graphics2D, 0, 0, paramInt4, paramInt5, paramState);
        break;
      default:
        paramSkin.paintSkin(graphics2D, 0, 0, paramInt4, paramInt5, paramState);
        break;
    } 
    graphics2D.dispose();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */