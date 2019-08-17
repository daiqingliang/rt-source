package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class MotifTabbedPaneUI extends BasicTabbedPaneUI {
  protected Color unselectedTabBackground;
  
  protected Color unselectedTabForeground;
  
  protected Color unselectedTabShadow;
  
  protected Color unselectedTabHighlight;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifTabbedPaneUI(); }
  
  protected void installDefaults() {
    super.installDefaults();
    this.unselectedTabBackground = UIManager.getColor("TabbedPane.unselectedTabBackground");
    this.unselectedTabForeground = UIManager.getColor("TabbedPane.unselectedTabForeground");
    this.unselectedTabShadow = UIManager.getColor("TabbedPane.unselectedTabShadow");
    this.unselectedTabHighlight = UIManager.getColor("TabbedPane.unselectedTabHighlight");
  }
  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    this.unselectedTabBackground = null;
    this.unselectedTabForeground = null;
    this.unselectedTabShadow = null;
    this.unselectedTabHighlight = null;
  }
  
  protected void paintContentBorderTopEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.lightHighlight);
    if (paramInt1 != 1 || paramInt2 < 0 || rectangle.x < paramInt3 || rectangle.x > paramInt3 + paramInt5) {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
    } else {
      paramGraphics.drawLine(paramInt3, paramInt4, rectangle.x - 1, paramInt4);
      if (rectangle.x + rectangle.width < paramInt3 + paramInt5 - 2)
        paramGraphics.drawLine(rectangle.x + rectangle.width, paramInt4, paramInt3 + paramInt5 - 2, paramInt4); 
    } 
  }
  
  protected void paintContentBorderBottomEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.shadow);
    if (paramInt1 != 3 || paramInt2 < 0 || rectangle.x < paramInt3 || rectangle.x > paramInt3 + paramInt5) {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    } else {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 1, rectangle.x - 1, paramInt4 + paramInt6 - 1);
      if (rectangle.x + rectangle.width < paramInt3 + paramInt5 - 2)
        paramGraphics.drawLine(rectangle.x + rectangle.width, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 1); 
    } 
  }
  
  protected void paintContentBorderRightEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.shadow);
    if (paramInt1 != 4 || paramInt2 < 0 || rectangle.y < paramInt4 || rectangle.y > paramInt4 + paramInt6) {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    } else {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 1, paramInt3 + paramInt5 - 1, rectangle.y - 1);
      if (rectangle.y + rectangle.height < paramInt4 + paramInt6 - 2)
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, rectangle.y + rectangle.height, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2); 
    } 
  }
  
  protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
    paramGraphics.setColor(paramBoolean ? this.tabPane.getBackgroundAt(paramInt2) : this.unselectedTabBackground);
    switch (paramInt1) {
      case 2:
        paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 1, paramInt6 - 2);
        return;
      case 4:
        paramGraphics.fillRect(paramInt3, paramInt4 + 1, paramInt5 - 1, paramInt6 - 2);
        return;
      case 3:
        paramGraphics.fillRect(paramInt3 + 1, paramInt4, paramInt5 - 2, paramInt6 - 3);
        paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 3, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 3);
        paramGraphics.drawLine(paramInt3 + 3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 4, paramInt4 + paramInt6 - 2);
        return;
    } 
    paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 3, paramInt5 - 2, paramInt6 - 3);
    paramGraphics.drawLine(paramInt3 + 2, paramInt4 + 2, paramInt3 + paramInt5 - 3, paramInt4 + 2);
    paramGraphics.drawLine(paramInt3 + 3, paramInt4 + 1, paramInt3 + paramInt5 - 4, paramInt4 + 1);
  }
  
  protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
    paramGraphics.setColor(paramBoolean ? this.lightHighlight : this.unselectedTabHighlight);
    switch (paramInt1) {
      case 2:
        paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 3);
        paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 2);
        paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + 2, paramInt4 + 1);
        paramGraphics.drawLine(paramInt3 + 3, paramInt4, paramInt3 + paramInt5 - 1, paramInt4);
        paramGraphics.setColor(paramBoolean ? this.shadow : this.unselectedTabShadow);
        paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 3, paramInt3 + 1, paramInt4 + paramInt6 - 2);
        paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + 2, paramInt4 + paramInt6 - 1);
        paramGraphics.drawLine(paramInt3 + 3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
        return;
      case 4:
        paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
        paramGraphics.setColor(paramBoolean ? this.shadow : this.unselectedTabShadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4 + 1);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 2);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
        paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
        return;
      case 3:
        paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 3);
        paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 3, paramInt3 + 1, paramInt4 + paramInt6 - 2);
        paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + 2, paramInt4 + paramInt6 - 1);
        paramGraphics.setColor(paramBoolean ? this.shadow : this.unselectedTabShadow);
        paramGraphics.drawLine(paramInt3 + 3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 4, paramInt4 + paramInt6 - 1);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
        return;
    } 
    paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 1);
    paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 2);
    paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + 2, paramInt4 + 1);
    paramGraphics.drawLine(paramInt3 + 3, paramInt4, paramInt3 + paramInt5 - 4, paramInt4);
    paramGraphics.setColor(paramBoolean ? this.shadow : this.unselectedTabShadow);
    paramGraphics.drawLine(paramInt3 + paramInt5 - 3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4 + 1);
    paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 2);
    paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
  }
  
  protected void paintFocusIndicator(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2, boolean paramBoolean) {
    Rectangle rectangle = paramArrayOfRectangle[paramInt2];
    if (this.tabPane.hasFocus() && paramBoolean) {
      int m;
      int k;
      int j;
      int i;
      paramGraphics.setColor(this.focus);
      switch (paramInt1) {
        case 2:
          i = rectangle.x + 3;
          j = rectangle.y + 3;
          k = rectangle.width - 6;
          m = rectangle.height - 7;
          break;
        case 4:
          i = rectangle.x + 2;
          j = rectangle.y + 3;
          k = rectangle.width - 6;
          m = rectangle.height - 7;
          break;
        case 3:
          i = rectangle.x + 3;
          j = rectangle.y + 2;
          k = rectangle.width - 7;
          m = rectangle.height - 6;
          break;
        default:
          i = rectangle.x + 3;
          j = rectangle.y + 3;
          k = rectangle.width - 7;
          m = rectangle.height - 6;
          break;
      } 
      paramGraphics.drawRect(i, j, k, m);
    } 
  }
  
  protected int getTabRunIndent(int paramInt1, int paramInt2) { return paramInt2 * 3; }
  
  protected int getTabRunOverlay(int paramInt) {
    this.tabRunOverlay = (paramInt == 2 || paramInt == 4) ? (int)Math.round(this.maxTabWidth * 0.1D) : (int)Math.round(this.maxTabHeight * 0.22D);
    switch (paramInt) {
      case 2:
        if (this.tabRunOverlay > this.tabInsets.right - 2)
          this.tabRunOverlay = this.tabInsets.right - 2; 
        break;
      case 4:
        if (this.tabRunOverlay > this.tabInsets.left - 2)
          this.tabRunOverlay = this.tabInsets.left - 2; 
        break;
      case 1:
        if (this.tabRunOverlay > this.tabInsets.bottom - 2)
          this.tabRunOverlay = this.tabInsets.bottom - 2; 
        break;
      case 3:
        if (this.tabRunOverlay > this.tabInsets.top - 2)
          this.tabRunOverlay = this.tabInsets.top - 2; 
        break;
    } 
    return this.tabRunOverlay;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */