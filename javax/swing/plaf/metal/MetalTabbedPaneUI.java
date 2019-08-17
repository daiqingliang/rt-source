package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class MetalTabbedPaneUI extends BasicTabbedPaneUI {
  protected int minTabWidth = 40;
  
  private Color unselectedBackground;
  
  protected Color tabAreaBackground;
  
  protected Color selectColor;
  
  protected Color selectHighlight;
  
  private boolean tabsOpaque = true;
  
  private boolean ocean;
  
  private Color oceanSelectedBorderColor;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalTabbedPaneUI(); }
  
  protected LayoutManager createLayoutManager() { return (this.tabPane.getTabLayoutPolicy() == 1) ? super.createLayoutManager() : new TabbedPaneLayout(); }
  
  protected void installDefaults() {
    super.installDefaults();
    this.tabAreaBackground = UIManager.getColor("TabbedPane.tabAreaBackground");
    this.selectColor = UIManager.getColor("TabbedPane.selected");
    this.selectHighlight = UIManager.getColor("TabbedPane.selectHighlight");
    this.tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
    this.unselectedBackground = UIManager.getColor("TabbedPane.unselectedBackground");
    this.ocean = MetalLookAndFeel.usingOcean();
    if (this.ocean)
      this.oceanSelectedBorderColor = UIManager.getColor("TabbedPane.borderHightlightColor"); 
  }
  
  protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
    int i = paramInt4 + paramInt6 - 1;
    int j = paramInt3 + paramInt5 - 1;
    switch (paramInt1) {
      case 2:
        paintLeftTabBorder(paramInt2, paramGraphics, paramInt3, paramInt4, paramInt5, paramInt6, i, j, paramBoolean);
        return;
      case 3:
        paintBottomTabBorder(paramInt2, paramGraphics, paramInt3, paramInt4, paramInt5, paramInt6, i, j, paramBoolean);
        return;
      case 4:
        paintRightTabBorder(paramInt2, paramGraphics, paramInt3, paramInt4, paramInt5, paramInt6, i, j, paramBoolean);
        return;
    } 
    paintTopTabBorder(paramInt2, paramGraphics, paramInt3, paramInt4, paramInt5, paramInt6, i, j, paramBoolean);
  }
  
  protected void paintTopTabBorder(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean) {
    int i = getRunForTab(this.tabPane.getTabCount(), paramInt1);
    int j = lastTabInRun(this.tabPane.getTabCount(), i);
    int k = this.tabRuns[i];
    boolean bool = MetalUtils.isLeftToRight(this.tabPane);
    int m = this.tabPane.getSelectedIndex();
    int n = paramInt5 - 1;
    int i1 = paramInt4 - 1;
    if (shouldFillGap(i, paramInt1, paramInt2, paramInt3)) {
      paramGraphics.translate(paramInt2, paramInt3);
      if (bool) {
        paramGraphics.setColor(getColorForGap(i, paramInt2, paramInt3 + 1));
        paramGraphics.fillRect(1, 0, 5, 3);
        paramGraphics.fillRect(1, 3, 2, 2);
      } else {
        paramGraphics.setColor(getColorForGap(i, paramInt2 + paramInt4 - 1, paramInt3 + 1));
        paramGraphics.fillRect(i1 - 5, 0, 5, 3);
        paramGraphics.fillRect(i1 - 2, 3, 2, 2);
      } 
      paramGraphics.translate(-paramInt2, -paramInt3);
    } 
    paramGraphics.translate(paramInt2, paramInt3);
    if (this.ocean && paramBoolean) {
      paramGraphics.setColor(this.oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(this.darkShadow);
    } 
    if (bool) {
      paramGraphics.drawLine(1, 5, 6, 0);
      paramGraphics.drawLine(6, 0, i1, 0);
      if (paramInt1 == j)
        paramGraphics.drawLine(i1, 1, i1, n); 
      if (this.ocean && paramInt1 - 1 == m && i == getRunForTab(this.tabPane.getTabCount(), m))
        paramGraphics.setColor(this.oceanSelectedBorderColor); 
      if (paramInt1 != this.tabRuns[this.runCount - 1]) {
        if (this.ocean && paramBoolean) {
          paramGraphics.drawLine(0, 6, 0, n);
          paramGraphics.setColor(this.darkShadow);
          paramGraphics.drawLine(0, 0, 0, 5);
        } else {
          paramGraphics.drawLine(0, 0, 0, n);
        } 
      } else {
        paramGraphics.drawLine(0, 6, 0, n);
      } 
    } else {
      paramGraphics.drawLine(i1 - 1, 5, i1 - 6, 0);
      paramGraphics.drawLine(i1 - 6, 0, 0, 0);
      if (paramInt1 == j)
        paramGraphics.drawLine(0, 1, 0, n); 
      if (this.ocean && paramInt1 - 1 == m && i == getRunForTab(this.tabPane.getTabCount(), m)) {
        paramGraphics.setColor(this.oceanSelectedBorderColor);
        paramGraphics.drawLine(i1, 0, i1, n);
      } else if (this.ocean && paramBoolean) {
        paramGraphics.drawLine(i1, 6, i1, n);
        if (paramInt1 != 0) {
          paramGraphics.setColor(this.darkShadow);
          paramGraphics.drawLine(i1, 0, i1, 5);
        } 
      } else if (paramInt1 != this.tabRuns[this.runCount - 1]) {
        paramGraphics.drawLine(i1, 0, i1, n);
      } else {
        paramGraphics.drawLine(i1, 6, i1, n);
      } 
    } 
    paramGraphics.setColor(paramBoolean ? this.selectHighlight : this.highlight);
    if (bool) {
      paramGraphics.drawLine(1, 6, 6, 1);
      paramGraphics.drawLine(6, 1, (paramInt1 == j) ? (i1 - 1) : i1, 1);
      paramGraphics.drawLine(1, 6, 1, n);
      if (paramInt1 == k && paramInt1 != this.tabRuns[this.runCount - 1]) {
        if (this.tabPane.getSelectedIndex() == this.tabRuns[i + 1]) {
          paramGraphics.setColor(this.selectHighlight);
        } else {
          paramGraphics.setColor(this.highlight);
        } 
        paramGraphics.drawLine(1, 0, 1, 4);
      } 
    } else {
      paramGraphics.drawLine(i1 - 1, 6, i1 - 6, 1);
      paramGraphics.drawLine(i1 - 6, 1, 1, 1);
      if (paramInt1 == j) {
        paramGraphics.drawLine(1, 1, 1, n);
      } else {
        paramGraphics.drawLine(0, 1, 0, n);
      } 
    } 
    paramGraphics.translate(-paramInt2, -paramInt3);
  }
  
  protected boolean shouldFillGap(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    boolean bool = false;
    if (!this.tabsOpaque)
      return false; 
    if (paramInt1 == this.runCount - 2) {
      Rectangle rectangle1 = getTabBounds(this.tabPane, this.tabPane.getTabCount() - 1);
      Rectangle rectangle2 = getTabBounds(this.tabPane, paramInt2);
      if (MetalUtils.isLeftToRight(this.tabPane)) {
        int i = rectangle1.x + rectangle1.width - 1;
        if (i > rectangle2.x + 2)
          return true; 
      } else {
        int i = rectangle1.x;
        int j = rectangle2.x + rectangle2.width - 1;
        if (i < j - 2)
          return true; 
      } 
    } else {
      bool = (paramInt1 != this.runCount - 1);
    } 
    return bool;
  }
  
  protected Color getColorForGap(int paramInt1, int paramInt2, int paramInt3) {
    int i = this.tabPane.getSelectedIndex();
    int j = this.tabRuns[paramInt1 + 1];
    int k = lastTabInRun(this.tabPane.getTabCount(), paramInt1 + 1);
    byte b = -1;
    for (int m = j; m <= k; m++) {
      Rectangle rectangle = getTabBounds(this.tabPane, m);
      int n = rectangle.x;
      int i1 = rectangle.x + rectangle.width - 1;
      if (MetalUtils.isLeftToRight(this.tabPane)) {
        if (n <= paramInt2 && i1 - 4 > paramInt2)
          return (i == m) ? this.selectColor : getUnselectedBackgroundAt(m); 
      } else if (n + 4 < paramInt2 && i1 >= paramInt2) {
        return (i == m) ? this.selectColor : getUnselectedBackgroundAt(m);
      } 
    } 
    return this.tabPane.getBackground();
  }
  
  protected void paintLeftTabBorder(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean) {
    int i = this.tabPane.getTabCount();
    int j = getRunForTab(i, paramInt1);
    int k = lastTabInRun(i, j);
    int m = this.tabRuns[j];
    paramGraphics.translate(paramInt2, paramInt3);
    int n = paramInt5 - 1;
    int i1 = paramInt4 - 1;
    if (paramInt1 != m && this.tabsOpaque) {
      paramGraphics.setColor((this.tabPane.getSelectedIndex() == paramInt1 - 1) ? this.selectColor : getUnselectedBackgroundAt(paramInt1 - 1));
      paramGraphics.fillRect(2, 0, 4, 3);
      paramGraphics.drawLine(2, 3, 2, 3);
    } 
    if (this.ocean) {
      paramGraphics.setColor(paramBoolean ? this.selectHighlight : MetalLookAndFeel.getWhite());
    } else {
      paramGraphics.setColor(paramBoolean ? this.selectHighlight : this.highlight);
    } 
    paramGraphics.drawLine(1, 6, 6, 1);
    paramGraphics.drawLine(1, 6, 1, n);
    paramGraphics.drawLine(6, 1, i1, 1);
    if (paramInt1 != m) {
      if (this.tabPane.getSelectedIndex() == paramInt1 - 1) {
        paramGraphics.setColor(this.selectHighlight);
      } else {
        paramGraphics.setColor(this.ocean ? MetalLookAndFeel.getWhite() : this.highlight);
      } 
      paramGraphics.drawLine(1, 0, 1, 4);
    } 
    if (this.ocean) {
      if (paramBoolean) {
        paramGraphics.setColor(this.oceanSelectedBorderColor);
      } else {
        paramGraphics.setColor(this.darkShadow);
      } 
    } else {
      paramGraphics.setColor(this.darkShadow);
    } 
    paramGraphics.drawLine(1, 5, 6, 0);
    paramGraphics.drawLine(6, 0, i1, 0);
    if (paramInt1 == k)
      paramGraphics.drawLine(0, n, i1, n); 
    if (this.ocean) {
      if (this.tabPane.getSelectedIndex() == paramInt1 - 1) {
        paramGraphics.drawLine(0, 5, 0, n);
        paramGraphics.setColor(this.oceanSelectedBorderColor);
        paramGraphics.drawLine(0, 0, 0, 5);
      } else if (paramBoolean) {
        paramGraphics.drawLine(0, 6, 0, n);
        if (paramInt1 != 0) {
          paramGraphics.setColor(this.darkShadow);
          paramGraphics.drawLine(0, 0, 0, 5);
        } 
      } else if (paramInt1 != m) {
        paramGraphics.drawLine(0, 0, 0, n);
      } else {
        paramGraphics.drawLine(0, 6, 0, n);
      } 
    } else if (paramInt1 != m) {
      paramGraphics.drawLine(0, 0, 0, n);
    } else {
      paramGraphics.drawLine(0, 6, 0, n);
    } 
    paramGraphics.translate(-paramInt2, -paramInt3);
  }
  
  protected void paintBottomTabBorder(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean) {
    int i = this.tabPane.getTabCount();
    int j = getRunForTab(i, paramInt1);
    int k = lastTabInRun(i, j);
    int m = this.tabRuns[j];
    boolean bool = MetalUtils.isLeftToRight(this.tabPane);
    int n = paramInt5 - 1;
    int i1 = paramInt4 - 1;
    if (shouldFillGap(j, paramInt1, paramInt2, paramInt3)) {
      paramGraphics.translate(paramInt2, paramInt3);
      if (bool) {
        paramGraphics.setColor(getColorForGap(j, paramInt2, paramInt3));
        paramGraphics.fillRect(1, n - 4, 3, 5);
        paramGraphics.fillRect(4, n - 1, 2, 2);
      } else {
        paramGraphics.setColor(getColorForGap(j, paramInt2 + paramInt4 - 1, paramInt3));
        paramGraphics.fillRect(i1 - 3, n - 3, 3, 4);
        paramGraphics.fillRect(i1 - 5, n - 1, 2, 2);
        paramGraphics.drawLine(i1 - 1, n - 4, i1 - 1, n - 4);
      } 
      paramGraphics.translate(-paramInt2, -paramInt3);
    } 
    paramGraphics.translate(paramInt2, paramInt3);
    if (this.ocean && paramBoolean) {
      paramGraphics.setColor(this.oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(this.darkShadow);
    } 
    if (bool) {
      paramGraphics.drawLine(1, n - 5, 6, n);
      paramGraphics.drawLine(6, n, i1, n);
      if (paramInt1 == k)
        paramGraphics.drawLine(i1, 0, i1, n); 
      if (this.ocean && paramBoolean) {
        paramGraphics.drawLine(0, 0, 0, n - 6);
        if ((j == 0 && paramInt1 != 0) || (j > 0 && paramInt1 != this.tabRuns[j - 1])) {
          paramGraphics.setColor(this.darkShadow);
          paramGraphics.drawLine(0, n - 5, 0, n);
        } 
      } else {
        if (this.ocean && paramInt1 == this.tabPane.getSelectedIndex() + 1)
          paramGraphics.setColor(this.oceanSelectedBorderColor); 
        if (paramInt1 != this.tabRuns[this.runCount - 1]) {
          paramGraphics.drawLine(0, 0, 0, n);
        } else {
          paramGraphics.drawLine(0, 0, 0, n - 6);
        } 
      } 
    } else {
      paramGraphics.drawLine(i1 - 1, n - 5, i1 - 6, n);
      paramGraphics.drawLine(i1 - 6, n, 0, n);
      if (paramInt1 == k)
        paramGraphics.drawLine(0, 0, 0, n); 
      if (this.ocean && paramInt1 == this.tabPane.getSelectedIndex() + 1) {
        paramGraphics.setColor(this.oceanSelectedBorderColor);
        paramGraphics.drawLine(i1, 0, i1, n);
      } else if (this.ocean && paramBoolean) {
        paramGraphics.drawLine(i1, 0, i1, n - 6);
        if (paramInt1 != m) {
          paramGraphics.setColor(this.darkShadow);
          paramGraphics.drawLine(i1, n - 5, i1, n);
        } 
      } else if (paramInt1 != this.tabRuns[this.runCount - 1]) {
        paramGraphics.drawLine(i1, 0, i1, n);
      } else {
        paramGraphics.drawLine(i1, 0, i1, n - 6);
      } 
    } 
    paramGraphics.setColor(paramBoolean ? this.selectHighlight : this.highlight);
    if (bool) {
      paramGraphics.drawLine(1, n - 6, 6, n - 1);
      paramGraphics.drawLine(1, 0, 1, n - 6);
      if (paramInt1 == m && paramInt1 != this.tabRuns[this.runCount - 1]) {
        if (this.tabPane.getSelectedIndex() == this.tabRuns[j + 1]) {
          paramGraphics.setColor(this.selectHighlight);
        } else {
          paramGraphics.setColor(this.highlight);
        } 
        paramGraphics.drawLine(1, n - 4, 1, n);
      } 
    } else if (paramInt1 == k) {
      paramGraphics.drawLine(1, 0, 1, n - 1);
    } else {
      paramGraphics.drawLine(0, 0, 0, n - 1);
    } 
    paramGraphics.translate(-paramInt2, -paramInt3);
  }
  
  protected void paintRightTabBorder(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean) {
    int i = this.tabPane.getTabCount();
    int j = getRunForTab(i, paramInt1);
    int k = lastTabInRun(i, j);
    int m = this.tabRuns[j];
    paramGraphics.translate(paramInt2, paramInt3);
    int n = paramInt5 - 1;
    int i1 = paramInt4 - 1;
    if (paramInt1 != m && this.tabsOpaque) {
      paramGraphics.setColor((this.tabPane.getSelectedIndex() == paramInt1 - 1) ? this.selectColor : getUnselectedBackgroundAt(paramInt1 - 1));
      paramGraphics.fillRect(i1 - 5, 0, 5, 3);
      paramGraphics.fillRect(i1 - 2, 3, 2, 2);
    } 
    paramGraphics.setColor(paramBoolean ? this.selectHighlight : this.highlight);
    paramGraphics.drawLine(i1 - 6, 1, i1 - 1, 6);
    paramGraphics.drawLine(0, 1, i1 - 6, 1);
    if (!paramBoolean)
      paramGraphics.drawLine(0, 1, 0, n); 
    if (this.ocean && paramBoolean) {
      paramGraphics.setColor(this.oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(this.darkShadow);
    } 
    if (paramInt1 == k)
      paramGraphics.drawLine(0, n, i1, n); 
    if (this.ocean && this.tabPane.getSelectedIndex() == paramInt1 - 1)
      paramGraphics.setColor(this.oceanSelectedBorderColor); 
    paramGraphics.drawLine(i1 - 6, 0, i1, 6);
    paramGraphics.drawLine(0, 0, i1 - 6, 0);
    if (this.ocean && paramBoolean) {
      paramGraphics.drawLine(i1, 6, i1, n);
      if (paramInt1 != m) {
        paramGraphics.setColor(this.darkShadow);
        paramGraphics.drawLine(i1, 0, i1, 5);
      } 
    } else if (this.ocean && this.tabPane.getSelectedIndex() == paramInt1 - 1) {
      paramGraphics.setColor(this.oceanSelectedBorderColor);
      paramGraphics.drawLine(i1, 0, i1, 6);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(i1, 6, i1, n);
    } else if (paramInt1 != m) {
      paramGraphics.drawLine(i1, 0, i1, n);
    } else {
      paramGraphics.drawLine(i1, 6, i1, n);
    } 
    paramGraphics.translate(-paramInt2, -paramInt3);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    if (paramJComponent.isOpaque()) {
      paramGraphics.setColor(this.tabAreaBackground);
      paramGraphics.fillRect(0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    } 
    paint(paramGraphics, paramJComponent);
  }
  
  protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
    int i = paramInt6 / 2;
    if (paramBoolean) {
      paramGraphics.setColor(this.selectColor);
    } else {
      paramGraphics.setColor(getUnselectedBackgroundAt(paramInt2));
    } 
    if (MetalUtils.isLeftToRight(this.tabPane)) {
      switch (paramInt1) {
        case 2:
          paramGraphics.fillRect(paramInt3 + 5, paramInt4 + 1, paramInt5 - 5, paramInt6 - 1);
          paramGraphics.fillRect(paramInt3 + 2, paramInt4 + 4, 3, paramInt6 - 4);
          return;
        case 3:
          paramGraphics.fillRect(paramInt3 + 2, paramInt4, paramInt5 - 2, paramInt6 - 4);
          paramGraphics.fillRect(paramInt3 + 5, paramInt4 + paramInt6 - 1 - 3, paramInt5 - 5, 3);
          return;
        case 4:
          paramGraphics.fillRect(paramInt3, paramInt4 + 2, paramInt5 - 4, paramInt6 - 2);
          paramGraphics.fillRect(paramInt3 + paramInt5 - 1 - 3, paramInt4 + 5, 3, paramInt6 - 5);
          return;
      } 
      paramGraphics.fillRect(paramInt3 + 4, paramInt4 + 2, paramInt5 - 1 - 3, paramInt6 - 1 - 1);
      paramGraphics.fillRect(paramInt3 + 2, paramInt4 + 5, 2, paramInt6 - 5);
    } else {
      switch (paramInt1) {
        case 2:
          paramGraphics.fillRect(paramInt3 + 5, paramInt4 + 1, paramInt5 - 5, paramInt6 - 1);
          paramGraphics.fillRect(paramInt3 + 2, paramInt4 + 4, 3, paramInt6 - 4);
          return;
        case 3:
          paramGraphics.fillRect(paramInt3, paramInt4, paramInt5 - 5, paramInt6 - 1);
          paramGraphics.fillRect(paramInt3 + paramInt5 - 1 - 4, paramInt4, 4, paramInt6 - 5);
          paramGraphics.fillRect(paramInt3 + paramInt5 - 1 - 4, paramInt4 + paramInt6 - 1 - 4, 2, 2);
          return;
        case 4:
          paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 5, paramInt6 - 1);
          paramGraphics.fillRect(paramInt3 + paramInt5 - 1 - 3, paramInt4 + 5, 3, paramInt6 - 5);
          return;
      } 
      paramGraphics.fillRect(paramInt3, paramInt4 + 2, paramInt5 - 1 - 3, paramInt6 - 1 - 1);
      paramGraphics.fillRect(paramInt3 + paramInt5 - 1 - 3, paramInt4 + 5, 3, paramInt6 - 3);
    } 
  }
  
  protected int getTabLabelShiftX(int paramInt1, int paramInt2, boolean paramBoolean) { return 0; }
  
  protected int getTabLabelShiftY(int paramInt1, int paramInt2, boolean paramBoolean) { return 0; }
  
  protected int getBaselineOffset() { return 0; }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    int i = this.tabPane.getTabPlacement();
    Insets insets = paramJComponent.getInsets();
    Dimension dimension = paramJComponent.getSize();
    if (this.tabPane.isOpaque()) {
      int k;
      int j;
      Color color = paramJComponent.getBackground();
      if (color instanceof javax.swing.plaf.UIResource && this.tabAreaBackground != null) {
        paramGraphics.setColor(this.tabAreaBackground);
      } else {
        paramGraphics.setColor(color);
      } 
      switch (i) {
        case 2:
          paramGraphics.fillRect(insets.left, insets.top, calculateTabAreaWidth(i, this.runCount, this.maxTabWidth), dimension.height - insets.bottom - insets.top);
          break;
        case 3:
          j = calculateTabAreaHeight(i, this.runCount, this.maxTabHeight);
          paramGraphics.fillRect(insets.left, dimension.height - insets.bottom - j, dimension.width - insets.left - insets.right, j);
          break;
        case 4:
          k = calculateTabAreaWidth(i, this.runCount, this.maxTabWidth);
          paramGraphics.fillRect(dimension.width - insets.right - k, insets.top, k, dimension.height - insets.top - insets.bottom);
          break;
        default:
          paramGraphics.fillRect(insets.left, insets.top, dimension.width - insets.right - insets.left, calculateTabAreaHeight(i, this.runCount, this.maxTabHeight));
          paintHighlightBelowTab();
          break;
      } 
    } 
    super.paint(paramGraphics, paramJComponent);
  }
  
  protected void paintHighlightBelowTab() {}
  
  protected void paintFocusIndicator(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2, boolean paramBoolean) {
    if (this.tabPane.hasFocus() && paramBoolean) {
      Rectangle rectangle = paramArrayOfRectangle[paramInt2];
      boolean bool1 = isLastInRun(paramInt2);
      paramGraphics.setColor(this.focus);
      paramGraphics.translate(rectangle.x, rectangle.y);
      int i = rectangle.width - 1;
      int j = rectangle.height - 1;
      boolean bool2 = MetalUtils.isLeftToRight(this.tabPane);
      switch (paramInt1) {
        case 4:
          paramGraphics.drawLine(i - 6, 2, i - 2, 6);
          paramGraphics.drawLine(1, 2, i - 6, 2);
          paramGraphics.drawLine(i - 2, 6, i - 2, j);
          paramGraphics.drawLine(1, 2, 1, j);
          paramGraphics.drawLine(1, j, i - 2, j);
          break;
        case 3:
          if (bool2) {
            paramGraphics.drawLine(2, j - 6, 6, j - 2);
            paramGraphics.drawLine(6, j - 2, i, j - 2);
            paramGraphics.drawLine(2, 0, 2, j - 6);
            paramGraphics.drawLine(2, 0, i, 0);
            paramGraphics.drawLine(i, 0, i, j - 2);
            break;
          } 
          paramGraphics.drawLine(i - 2, j - 6, i - 6, j - 2);
          paramGraphics.drawLine(i - 2, 0, i - 2, j - 6);
          if (bool1) {
            paramGraphics.drawLine(2, j - 2, i - 6, j - 2);
            paramGraphics.drawLine(2, 0, i - 2, 0);
            paramGraphics.drawLine(2, 0, 2, j - 2);
            break;
          } 
          paramGraphics.drawLine(1, j - 2, i - 6, j - 2);
          paramGraphics.drawLine(1, 0, i - 2, 0);
          paramGraphics.drawLine(1, 0, 1, j - 2);
          break;
        case 2:
          paramGraphics.drawLine(2, 6, 6, 2);
          paramGraphics.drawLine(2, 6, 2, j - 1);
          paramGraphics.drawLine(6, 2, i, 2);
          paramGraphics.drawLine(i, 2, i, j - 1);
          paramGraphics.drawLine(2, j - 1, i, j - 1);
          break;
        default:
          if (bool2) {
            paramGraphics.drawLine(2, 6, 6, 2);
            paramGraphics.drawLine(2, 6, 2, j - 1);
            paramGraphics.drawLine(6, 2, i, 2);
            paramGraphics.drawLine(i, 2, i, j - 1);
            paramGraphics.drawLine(2, j - 1, i, j - 1);
            break;
          } 
          paramGraphics.drawLine(i - 2, 6, i - 6, 2);
          paramGraphics.drawLine(i - 2, 6, i - 2, j - 1);
          if (bool1) {
            paramGraphics.drawLine(i - 6, 2, 2, 2);
            paramGraphics.drawLine(2, 2, 2, j - 1);
            paramGraphics.drawLine(i - 2, j - 1, 2, j - 1);
            break;
          } 
          paramGraphics.drawLine(i - 6, 2, 1, 2);
          paramGraphics.drawLine(1, 2, 1, j - 1);
          paramGraphics.drawLine(i - 2, j - 1, 1, j - 1);
          break;
      } 
      paramGraphics.translate(-rectangle.x, -rectangle.y);
    } 
  }
  
  protected void paintContentBorderTopEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    boolean bool = MetalUtils.isLeftToRight(this.tabPane);
    int i = paramInt3 + paramInt5 - 1;
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    if (this.ocean) {
      paramGraphics.setColor(this.oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(this.selectHighlight);
    } 
    if (paramInt1 != 1 || paramInt2 < 0 || rectangle.y + rectangle.height + 1 < paramInt4 || rectangle.x < paramInt3 || rectangle.x > paramInt3 + paramInt5) {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      if (this.ocean && paramInt1 == 1) {
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        paramGraphics.drawLine(paramInt3, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
      } 
    } else {
      boolean bool1 = isLastInRun(paramInt2);
      if (bool || bool1) {
        paramGraphics.drawLine(paramInt3, paramInt4, rectangle.x + 1, paramInt4);
      } else {
        paramGraphics.drawLine(paramInt3, paramInt4, rectangle.x, paramInt4);
      } 
      if (rectangle.x + rectangle.width < i - 1) {
        if (bool && !bool1) {
          paramGraphics.drawLine(rectangle.x + rectangle.width, paramInt4, i - 1, paramInt4);
        } else {
          paramGraphics.drawLine(rectangle.x + rectangle.width - 1, paramInt4, i - 1, paramInt4);
        } 
      } else {
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      } 
      if (this.ocean) {
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        if (bool || bool1) {
          paramGraphics.drawLine(paramInt3, paramInt4 + 1, rectangle.x + 1, paramInt4 + 1);
        } else {
          paramGraphics.drawLine(paramInt3, paramInt4 + 1, rectangle.x, paramInt4 + 1);
        } 
        if (rectangle.x + rectangle.width < i - 1) {
          if (bool && !bool1) {
            paramGraphics.drawLine(rectangle.x + rectangle.width, paramInt4 + 1, i - 1, paramInt4 + 1);
          } else {
            paramGraphics.drawLine(rectangle.x + rectangle.width - 1, paramInt4 + 1, i - 1, paramInt4 + 1);
          } 
        } else {
          paramGraphics.setColor(this.shadow);
          paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
        } 
      } 
    } 
  }
  
  protected void paintContentBorderBottomEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    boolean bool = MetalUtils.isLeftToRight(this.tabPane);
    int i = paramInt4 + paramInt6 - 1;
    int j = paramInt3 + paramInt5 - 1;
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.darkShadow);
    if (paramInt1 != 3 || paramInt2 < 0 || rectangle.y - 1 > paramInt6 || rectangle.x < paramInt3 || rectangle.x > paramInt3 + paramInt5) {
      if (this.ocean && paramInt1 == 3)
        paramGraphics.setColor(this.oceanSelectedBorderColor); 
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    } else {
      boolean bool1 = isLastInRun(paramInt2);
      if (this.ocean)
        paramGraphics.setColor(this.oceanSelectedBorderColor); 
      if (bool || bool1) {
        paramGraphics.drawLine(paramInt3, i, rectangle.x, i);
      } else {
        paramGraphics.drawLine(paramInt3, i, rectangle.x - 1, i);
      } 
      if (rectangle.x + rectangle.width < paramInt3 + paramInt5 - 2)
        if (bool && !bool1) {
          paramGraphics.drawLine(rectangle.x + rectangle.width, i, j, i);
        } else {
          paramGraphics.drawLine(rectangle.x + rectangle.width - 1, i, j, i);
        }  
    } 
  }
  
  protected void paintContentBorderLeftEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    if (this.ocean) {
      paramGraphics.setColor(this.oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(this.selectHighlight);
    } 
    if (paramInt1 != 2 || paramInt2 < 0 || rectangle.x + rectangle.width + 1 < paramInt3 || rectangle.y < paramInt4 || rectangle.y > paramInt4 + paramInt6) {
      paramGraphics.drawLine(paramInt3, paramInt4 + 1, paramInt3, paramInt4 + paramInt6 - 2);
      if (this.ocean && paramInt1 == 2) {
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        paramGraphics.drawLine(paramInt3 + 1, paramInt4, paramInt3 + 1, paramInt4 + paramInt6 - 2);
      } 
    } else {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, rectangle.y + 1);
      if (rectangle.y + rectangle.height < paramInt4 + paramInt6 - 2)
        paramGraphics.drawLine(paramInt3, rectangle.y + rectangle.height + 1, paramInt3, paramInt4 + paramInt6 + 2); 
      if (this.ocean) {
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, rectangle.y + 1);
        if (rectangle.y + rectangle.height < paramInt4 + paramInt6 - 2)
          paramGraphics.drawLine(paramInt3 + 1, rectangle.y + rectangle.height + 1, paramInt3 + 1, paramInt4 + paramInt6 + 2); 
      } 
    } 
  }
  
  protected void paintContentBorderRightEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.darkShadow);
    if (paramInt1 != 4 || paramInt2 < 0 || rectangle.x - 1 > paramInt5 || rectangle.y < paramInt4 || rectangle.y > paramInt4 + paramInt6) {
      if (this.ocean && paramInt1 == 4)
        paramGraphics.setColor(this.oceanSelectedBorderColor); 
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    } else {
      if (this.ocean)
        paramGraphics.setColor(this.oceanSelectedBorderColor); 
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, rectangle.y);
      if (rectangle.y + rectangle.height < paramInt4 + paramInt6 - 2)
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, rectangle.y + rectangle.height, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2); 
    } 
  }
  
  protected int calculateMaxTabHeight(int paramInt) {
    FontMetrics fontMetrics = getFontMetrics();
    int i = fontMetrics.getHeight();
    boolean bool = false;
    for (byte b = 0; b < this.tabPane.getTabCount(); b++) {
      Icon icon = this.tabPane.getIconAt(b);
      if (icon != null && icon.getIconHeight() > i) {
        bool = true;
        break;
      } 
    } 
    return super.calculateMaxTabHeight(paramInt) - (bool ? (this.tabInsets.top + this.tabInsets.bottom) : 0);
  }
  
  protected int getTabRunOverlay(int paramInt) {
    if (paramInt == 2 || paramInt == 4) {
      int i = calculateMaxTabHeight(paramInt);
      return i / 2;
    } 
    return 0;
  }
  
  protected boolean shouldRotateTabRuns(int paramInt1, int paramInt2) { return false; }
  
  protected boolean shouldPadTabRun(int paramInt1, int paramInt2) { return (this.runCount > 1 && paramInt2 < this.runCount - 1); }
  
  private boolean isLastInRun(int paramInt) {
    int i = getRunForTab(this.tabPane.getTabCount(), paramInt);
    int j = lastTabInRun(this.tabPane.getTabCount(), i);
    return (paramInt == j);
  }
  
  private Color getUnselectedBackgroundAt(int paramInt) {
    Color color = this.tabPane.getBackgroundAt(paramInt);
    return (color instanceof javax.swing.plaf.UIResource && this.unselectedBackground != null) ? this.unselectedBackground : color;
  }
  
  int getRolloverTabIndex() { return getRolloverTab(); }
  
  public class TabbedPaneLayout extends BasicTabbedPaneUI.TabbedPaneLayout {
    public TabbedPaneLayout() { super(MetalTabbedPaneUI.this); }
    
    protected void normalizeTabRuns(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (param1Int1 == 1 || param1Int1 == 3)
        super.normalizeTabRuns(param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    protected void rotateTabRuns(int param1Int1, int param1Int2) {}
    
    protected void padSelectedTab(int param1Int1, int param1Int2) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */