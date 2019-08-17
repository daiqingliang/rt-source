package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class SynthTabbedPaneUI extends BasicTabbedPaneUI implements PropertyChangeListener, SynthUI {
  private int tabOverlap = 0;
  
  private boolean extendTabsToBase = false;
  
  private SynthContext tabAreaContext;
  
  private SynthContext tabContext;
  
  private SynthContext tabContentContext;
  
  private SynthStyle style;
  
  private SynthStyle tabStyle;
  
  private SynthStyle tabAreaStyle;
  
  private SynthStyle tabContentStyle;
  
  private Rectangle textRect = new Rectangle();
  
  private Rectangle iconRect = new Rectangle();
  
  private Rectangle tabAreaBounds = new Rectangle();
  
  private boolean tabAreaStatesMatchSelectedTab = false;
  
  private boolean nudgeSelectedLabel = true;
  
  private boolean selectedTabIsPressed = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthTabbedPaneUI(); }
  
  private boolean scrollableTabLayoutEnabled() { return (this.tabPane.getTabLayoutPolicy() == 1); }
  
  protected void installDefaults() { updateStyle(this.tabPane); }
  
  private void updateStyle(JTabbedPane paramJTabbedPane) {
    SynthContext synthContext = getContext(paramJTabbedPane, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      this.tabRunOverlay = this.style.getInt(synthContext, "TabbedPane.tabRunOverlay", 0);
      this.tabOverlap = this.style.getInt(synthContext, "TabbedPane.tabOverlap", 0);
      this.extendTabsToBase = this.style.getBoolean(synthContext, "TabbedPane.extendTabsToBase", false);
      this.textIconGap = this.style.getInt(synthContext, "TabbedPane.textIconGap", 0);
      this.selectedTabPadInsets = (Insets)this.style.get(synthContext, "TabbedPane.selectedTabPadInsets");
      if (this.selectedTabPadInsets == null)
        this.selectedTabPadInsets = new Insets(0, 0, 0, 0); 
      this.tabAreaStatesMatchSelectedTab = this.style.getBoolean(synthContext, "TabbedPane.tabAreaStatesMatchSelectedTab", false);
      this.nudgeSelectedLabel = this.style.getBoolean(synthContext, "TabbedPane.nudgeSelectedLabel", true);
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
    if (this.tabContext != null)
      this.tabContext.dispose(); 
    this.tabContext = getContext(paramJTabbedPane, Region.TABBED_PANE_TAB, 1);
    this.tabStyle = SynthLookAndFeel.updateStyle(this.tabContext, this);
    this.tabInsets = this.tabStyle.getInsets(this.tabContext, null);
    if (this.tabAreaContext != null)
      this.tabAreaContext.dispose(); 
    this.tabAreaContext = getContext(paramJTabbedPane, Region.TABBED_PANE_TAB_AREA, 1);
    this.tabAreaStyle = SynthLookAndFeel.updateStyle(this.tabAreaContext, this);
    this.tabAreaInsets = this.tabAreaStyle.getInsets(this.tabAreaContext, null);
    if (this.tabContentContext != null)
      this.tabContentContext.dispose(); 
    this.tabContentContext = getContext(paramJTabbedPane, Region.TABBED_PANE_CONTENT, 1);
    this.tabContentStyle = SynthLookAndFeel.updateStyle(this.tabContentContext, this);
    this.contentBorderInsets = this.tabContentStyle.getInsets(this.tabContentContext, null);
  }
  
  protected void installListeners() {
    super.installListeners();
    this.tabPane.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.tabPane.removePropertyChangeListener(this);
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.tabPane, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    this.tabStyle.uninstallDefaults(this.tabContext);
    this.tabContext.dispose();
    this.tabContext = null;
    this.tabStyle = null;
    this.tabAreaStyle.uninstallDefaults(this.tabAreaContext);
    this.tabAreaContext.dispose();
    this.tabAreaContext = null;
    this.tabAreaStyle = null;
    this.tabContentStyle.uninstallDefaults(this.tabContentContext);
    this.tabContentContext.dispose();
    this.tabContentContext = null;
    this.tabContentStyle = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt) {
    SynthStyle synthStyle = null;
    if (paramRegion == Region.TABBED_PANE_TAB) {
      synthStyle = this.tabStyle;
    } else if (paramRegion == Region.TABBED_PANE_TAB_AREA) {
      synthStyle = this.tabAreaStyle;
    } else if (paramRegion == Region.TABBED_PANE_CONTENT) {
      synthStyle = this.tabContentStyle;
    } 
    return SynthContext.getContext(paramJComponent, paramRegion, synthStyle, paramInt);
  }
  
  protected JButton createScrollButton(int paramInt) {
    if (UIManager.getBoolean("TabbedPane.useBasicArrows")) {
      JButton jButton = super.createScrollButton(paramInt);
      jButton.setBorder(BorderFactory.createEmptyBorder());
      return jButton;
    } 
    return new SynthScrollableTabButton(paramInt);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle(this.tabPane); 
  }
  
  protected MouseListener createMouseListener() {
    final MouseListener delegate = super.createMouseListener();
    final MouseMotionListener delegate2 = (MouseMotionListener)mouseListener;
    return new MouseListener() {
        public void mouseClicked(MouseEvent param1MouseEvent) { delegate.mouseClicked(param1MouseEvent); }
        
        public void mouseEntered(MouseEvent param1MouseEvent) { delegate.mouseEntered(param1MouseEvent); }
        
        public void mouseExited(MouseEvent param1MouseEvent) { delegate.mouseExited(param1MouseEvent); }
        
        public void mousePressed(MouseEvent param1MouseEvent) {
          if (!SynthTabbedPaneUI.this.tabPane.isEnabled())
            return; 
          int i = SynthTabbedPaneUI.this.tabForCoordinate(SynthTabbedPaneUI.this.tabPane, param1MouseEvent.getX(), param1MouseEvent.getY());
          if (i >= 0 && SynthTabbedPaneUI.this.tabPane.isEnabledAt(i) && i == SynthTabbedPaneUI.this.tabPane.getSelectedIndex()) {
            SynthTabbedPaneUI.this.selectedTabIsPressed = true;
            SynthTabbedPaneUI.this.tabPane.repaint();
          } 
          delegate.mousePressed(param1MouseEvent);
        }
        
        public void mouseReleased(MouseEvent param1MouseEvent) {
          if (SynthTabbedPaneUI.this.selectedTabIsPressed) {
            SynthTabbedPaneUI.this.selectedTabIsPressed = false;
            SynthTabbedPaneUI.this.tabPane.repaint();
          } 
          delegate.mouseReleased(param1MouseEvent);
          delegate2.mouseMoved(param1MouseEvent);
        }
      };
  }
  
  protected int getTabLabelShiftX(int paramInt1, int paramInt2, boolean paramBoolean) { return this.nudgeSelectedLabel ? super.getTabLabelShiftX(paramInt1, paramInt2, paramBoolean) : 0; }
  
  protected int getTabLabelShiftY(int paramInt1, int paramInt2, boolean paramBoolean) { return this.nudgeSelectedLabel ? super.getTabLabelShiftY(paramInt1, paramInt2, paramBoolean) : 0; }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintTabbedPaneBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected int getBaseline(int paramInt) {
    if (this.tabPane.getTabComponentAt(paramInt) != null || getTextViewForTab(paramInt) != null)
      return super.getBaseline(paramInt); 
    String str = this.tabPane.getTitleAt(paramInt);
    Font font = this.tabContext.getStyle().getFont(this.tabContext);
    FontMetrics fontMetrics = getFontMetrics(font);
    Icon icon = getIconForTab(paramInt);
    this.textRect.setBounds(0, 0, 0, 0);
    this.iconRect.setBounds(0, 0, 0, 0);
    this.calcRect.setBounds(0, 0, 32767, this.maxTabHeight);
    this.tabContext.getStyle().getGraphicsUtils(this.tabContext).layoutText(this.tabContext, fontMetrics, str, icon, 0, 0, 10, 0, this.calcRect, this.iconRect, this.textRect, this.textIconGap);
    return this.textRect.y + fontMetrics.getAscent() + getBaselineOffset();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintTabbedPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    int i = this.tabPane.getSelectedIndex();
    int j = this.tabPane.getTabPlacement();
    ensureCurrentLayout();
    if (!scrollableTabLayoutEnabled()) {
      int i2;
      Insets insets = this.tabPane.getInsets();
      int k = insets.left;
      int m = insets.top;
      int n = this.tabPane.getWidth() - insets.left - insets.right;
      int i1 = this.tabPane.getHeight() - insets.top - insets.bottom;
      switch (j) {
        case 2:
          n = calculateTabAreaWidth(j, this.runCount, this.maxTabWidth);
          break;
        case 4:
          i2 = calculateTabAreaWidth(j, this.runCount, this.maxTabWidth);
          k = k + n - i2;
          n = i2;
          break;
        case 3:
          i2 = calculateTabAreaHeight(j, this.runCount, this.maxTabHeight);
          m = m + i1 - i2;
          i1 = i2;
          break;
        default:
          i1 = calculateTabAreaHeight(j, this.runCount, this.maxTabHeight);
          break;
      } 
      this.tabAreaBounds.setBounds(k, m, n, i1);
      if (paramGraphics.getClipBounds().intersects(this.tabAreaBounds))
        paintTabArea(this.tabAreaContext, paramGraphics, j, i, this.tabAreaBounds); 
    } 
    paintContentBorder(this.tabContentContext, paramGraphics, j, i);
  }
  
  protected void paintTabArea(Graphics paramGraphics, int paramInt1, int paramInt2) {
    Insets insets = this.tabPane.getInsets();
    int i = insets.left;
    int j = insets.top;
    int k = this.tabPane.getWidth() - insets.left - insets.right;
    int m = this.tabPane.getHeight() - insets.top - insets.bottom;
    paintTabArea(this.tabAreaContext, paramGraphics, paramInt1, paramInt2, new Rectangle(i, j, k, m));
  }
  
  private void paintTabArea(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, Rectangle paramRectangle) {
    Rectangle rectangle = paramGraphics.getClipBounds();
    if (this.tabAreaStatesMatchSelectedTab && paramInt2 >= 0) {
      updateTabContext(paramInt2, true, this.selectedTabIsPressed, (getRolloverTab() == paramInt2), (getFocusIndex() == paramInt2));
      paramSynthContext.setComponentState(this.tabContext.getComponentState());
    } else {
      paramSynthContext.setComponentState(1);
    } 
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    paramSynthContext.getPainter().paintTabbedPaneTabAreaBackground(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, paramInt1);
    paramSynthContext.getPainter().paintTabbedPaneTabAreaBorder(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, paramInt1);
    int i = this.tabPane.getTabCount();
    this.iconRect.setBounds(0, 0, 0, 0);
    this.textRect.setBounds(0, 0, 0, 0);
    for (int j = this.runCount - 1; j >= 0; j--) {
      int k = this.tabRuns[j];
      int m = this.tabRuns[(j == this.runCount - 1) ? 0 : (j + 1)];
      int n = (m != 0) ? (m - 1) : (i - 1);
      for (int i1 = k; i1 <= n; i1++) {
        if (this.rects[i1].intersects(rectangle) && paramInt2 != i1)
          paintTab(this.tabContext, paramGraphics, paramInt1, this.rects, i1, this.iconRect, this.textRect); 
      } 
    } 
    if (paramInt2 >= 0 && this.rects[paramInt2].intersects(rectangle))
      paintTab(this.tabContext, paramGraphics, paramInt1, this.rects, paramInt2, this.iconRect, this.textRect); 
  }
  
  protected void setRolloverTab(int paramInt) {
    int i = getRolloverTab();
    super.setRolloverTab(paramInt);
    Rectangle rectangle = null;
    if (i != paramInt && this.tabAreaStatesMatchSelectedTab) {
      this.tabPane.repaint();
    } else {
      if (i >= 0 && i < this.tabPane.getTabCount()) {
        rectangle = getTabBounds(this.tabPane, i);
        if (rectangle != null)
          this.tabPane.repaint(rectangle); 
      } 
      if (paramInt >= 0) {
        rectangle = getTabBounds(this.tabPane, paramInt);
        if (rectangle != null)
          this.tabPane.repaint(rectangle); 
      } 
    } 
  }
  
  private void paintTab(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2) {
    Rectangle rectangle = paramArrayOfRectangle[paramInt2];
    int i = this.tabPane.getSelectedIndex();
    boolean bool = (i == paramInt2);
    updateTabContext(paramInt2, bool, (bool && this.selectedTabIsPressed), (getRolloverTab() == paramInt2), (getFocusIndex() == paramInt2));
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, rectangle);
    int j = rectangle.x;
    int k = rectangle.y;
    int m = rectangle.height;
    int n = rectangle.width;
    int i1 = this.tabPane.getTabPlacement();
    if (this.extendTabsToBase && this.runCount > 1 && i >= 0) {
      int i5;
      int i4;
      int i3;
      int i2;
      Rectangle rectangle1 = paramArrayOfRectangle[i];
      switch (i1) {
        case 1:
          i2 = rectangle1.y + rectangle1.height;
          m = i2 - rectangle.y;
          break;
        case 2:
          i3 = rectangle1.x + rectangle1.width;
          n = i3 - rectangle.x;
          break;
        case 3:
          i4 = rectangle1.y;
          m = rectangle.y + rectangle.height - i4;
          k = i4;
          break;
        case 4:
          i5 = rectangle1.x;
          n = rectangle.x + rectangle.width - i5;
          j = i5;
          break;
      } 
    } 
    this.tabContext.getPainter().paintTabbedPaneTabBackground(this.tabContext, paramGraphics, j, k, n, m, paramInt2, i1);
    this.tabContext.getPainter().paintTabbedPaneTabBorder(this.tabContext, paramGraphics, j, k, n, m, paramInt2, i1);
    if (this.tabPane.getTabComponentAt(paramInt2) == null) {
      String str = this.tabPane.getTitleAt(paramInt2);
      Font font = paramSynthContext.getStyle().getFont(paramSynthContext);
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.tabPane, paramGraphics, font);
      Icon icon = getIconForTab(paramInt2);
      layoutLabel(paramSynthContext, paramInt1, fontMetrics, paramInt2, str, icon, rectangle, paramRectangle1, paramRectangle2, bool);
      paintText(paramSynthContext, paramGraphics, paramInt1, font, fontMetrics, paramInt2, str, paramRectangle2, bool);
      paintIcon(paramGraphics, paramInt1, paramInt2, icon, paramRectangle1, bool);
    } 
  }
  
  private void layoutLabel(SynthContext paramSynthContext, int paramInt1, FontMetrics paramFontMetrics, int paramInt2, String paramString, Icon paramIcon, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, boolean paramBoolean) {
    View view = getTextViewForTab(paramInt2);
    if (view != null)
      this.tabPane.putClientProperty("html", view); 
    paramRectangle3.x = paramRectangle3.y = paramRectangle2.x = paramRectangle2.y = 0;
    paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).layoutText(paramSynthContext, paramFontMetrics, paramString, paramIcon, 0, 0, 10, 0, paramRectangle1, paramRectangle2, paramRectangle3, this.textIconGap);
    this.tabPane.putClientProperty("html", null);
    int i = getTabLabelShiftX(paramInt1, paramInt2, paramBoolean);
    int j = getTabLabelShiftY(paramInt1, paramInt2, paramBoolean);
    paramRectangle2.x += i;
    paramRectangle2.y += j;
    paramRectangle3.x += i;
    paramRectangle3.y += j;
  }
  
  private void paintText(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, Font paramFont, FontMetrics paramFontMetrics, int paramInt2, String paramString, Rectangle paramRectangle, boolean paramBoolean) {
    paramGraphics.setFont(paramFont);
    View view = getTextViewForTab(paramInt2);
    if (view != null) {
      view.paint(paramGraphics, paramRectangle);
    } else {
      int i = this.tabPane.getDisplayedMnemonicIndexAt(paramInt2);
      paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
      paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, paramString, paramRectangle, i);
    } 
  }
  
  private void paintContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2) {
    int i = this.tabPane.getWidth();
    int j = this.tabPane.getHeight();
    Insets insets = this.tabPane.getInsets();
    int k = insets.left;
    int m = insets.top;
    int n = i - insets.right - insets.left;
    int i1 = j - insets.top - insets.bottom;
    switch (paramInt1) {
      case 2:
        k += calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth);
        n -= k - insets.left;
        break;
      case 4:
        n -= calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth);
        break;
      case 3:
        i1 -= calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight);
        break;
      default:
        m += calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight);
        i1 -= m - insets.top;
        break;
    } 
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, new Rectangle(k, m, n, i1));
    paramSynthContext.getPainter().paintTabbedPaneContentBackground(paramSynthContext, paramGraphics, k, m, n, i1);
    paramSynthContext.getPainter().paintTabbedPaneContentBorder(paramSynthContext, paramGraphics, k, m, n, i1);
  }
  
  private void ensureCurrentLayout() {
    if (!this.tabPane.isValid())
      this.tabPane.validate(); 
    if (!this.tabPane.isValid()) {
      BasicTabbedPaneUI.TabbedPaneLayout tabbedPaneLayout = (BasicTabbedPaneUI.TabbedPaneLayout)this.tabPane.getLayout();
      tabbedPaneLayout.calculateLayoutInfo();
    } 
  }
  
  protected int calculateMaxTabHeight(int paramInt) {
    FontMetrics fontMetrics = getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext));
    int i = this.tabPane.getTabCount();
    int j = 0;
    int k = fontMetrics.getHeight();
    for (byte b = 0; b < i; b++)
      j = Math.max(calculateTabHeight(paramInt, b, k), j); 
    return j;
  }
  
  protected int calculateTabWidth(int paramInt1, int paramInt2, FontMetrics paramFontMetrics) {
    Icon icon = getIconForTab(paramInt2);
    Insets insets = getTabInsets(paramInt1, paramInt2);
    int i = insets.left + insets.right;
    Component component = this.tabPane.getTabComponentAt(paramInt2);
    if (component != null) {
      i += (component.getPreferredSize()).width;
    } else {
      if (icon != null)
        i += icon.getIconWidth() + this.textIconGap; 
      View view = getTextViewForTab(paramInt2);
      if (view != null) {
        i += (int)view.getPreferredSpan(0);
      } else {
        String str = this.tabPane.getTitleAt(paramInt2);
        i += this.tabContext.getStyle().getGraphicsUtils(this.tabContext).computeStringWidth(this.tabContext, paramFontMetrics.getFont(), paramFontMetrics, str);
      } 
    } 
    return i;
  }
  
  protected int calculateMaxTabWidth(int paramInt) {
    FontMetrics fontMetrics = getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext));
    int i = this.tabPane.getTabCount();
    int j = 0;
    for (byte b = 0; b < i; b++)
      j = Math.max(calculateTabWidth(paramInt, b, fontMetrics), j); 
    return j;
  }
  
  protected Insets getTabInsets(int paramInt1, int paramInt2) {
    updateTabContext(paramInt2, false, false, false, (getFocusIndex() == paramInt2));
    return this.tabInsets;
  }
  
  protected FontMetrics getFontMetrics() { return getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext)); }
  
  private FontMetrics getFontMetrics(Font paramFont) { return this.tabPane.getFontMetrics(paramFont); }
  
  private void updateTabContext(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) {
    int i = 0;
    if (!this.tabPane.isEnabled() || !this.tabPane.isEnabledAt(paramInt)) {
      i |= 0x8;
      if (paramBoolean1)
        i |= 0x200; 
    } else if (paramBoolean1) {
      i |= 0x201;
      if (paramBoolean3 && UIManager.getBoolean("TabbedPane.isTabRollover"))
        i |= 0x2; 
    } else if (paramBoolean3) {
      i |= 0x3;
    } else {
      i = SynthLookAndFeel.getComponentState(this.tabPane);
      i &= 0xFFFFFEFF;
    } 
    if (paramBoolean4 && this.tabPane.hasFocus())
      i |= 0x100; 
    if (paramBoolean2)
      i |= 0x4; 
    this.tabContext.setComponentState(i);
  }
  
  protected LayoutManager createLayoutManager() { return (this.tabPane.getTabLayoutPolicy() == 1) ? super.createLayoutManager() : new BasicTabbedPaneUI.TabbedPaneLayout() {
        public void calculateLayoutInfo() {
          super.calculateLayoutInfo();
          if (SynthTabbedPaneUI.this.tabOverlap != 0) {
            int i = SynthTabbedPaneUI.this.tabPane.getTabCount();
            boolean bool = SynthTabbedPaneUI.this.tabPane.getComponentOrientation().isLeftToRight();
            for (int j = SynthTabbedPaneUI.this.runCount - 1; j >= 0; j--) {
              int k = SynthTabbedPaneUI.this.tabRuns[j];
              int m = SynthTabbedPaneUI.this.tabRuns[(j == SynthTabbedPaneUI.this.runCount - 1) ? 0 : (j + 1)];
              int n = (m != 0) ? (m - 1) : (i - 1);
              for (int i1 = k + 1; i1 <= n; i1++) {
                int i2 = 0;
                int i3 = 0;
                switch (SynthTabbedPaneUI.this.tabPane.getTabPlacement()) {
                  case 1:
                  case 3:
                    i2 = bool ? SynthTabbedPaneUI.this.tabOverlap : -SynthTabbedPaneUI.this.tabOverlap;
                    break;
                  case 2:
                  case 4:
                    i3 = SynthTabbedPaneUI.this.tabOverlap;
                    break;
                } 
                (this.this$0.rects[i1]).x += i2;
                (this.this$0.rects[i1]).y += i3;
                (this.this$0.rects[i1]).width += Math.abs(i2);
                (this.this$0.rects[i1]).height += Math.abs(i3);
              } 
            } 
          } 
        }
      }; }
  
  private class SynthScrollableTabButton extends SynthArrowButton implements UIResource {
    public SynthScrollableTabButton(int param1Int) {
      super(param1Int);
      setName("TabbedPane.button");
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */