package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import sun.swing.plaf.synth.SynthIcon;

public class SynthToolBarUI extends BasicToolBarUI implements PropertyChangeListener, SynthUI {
  private Icon handleIcon = null;
  
  private Rectangle contentRect = new Rectangle();
  
  private SynthStyle style;
  
  private SynthStyle contentStyle;
  
  private SynthStyle dragWindowStyle;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthToolBarUI(); }
  
  protected void installDefaults() {
    this.toolBar.setLayout(createLayout());
    updateStyle(this.toolBar);
  }
  
  protected void installListeners() {
    super.installListeners();
    this.toolBar.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.toolBar.removePropertyChangeListener(this);
  }
  
  private void updateStyle(JToolBar paramJToolBar) {
    SynthContext synthContext = getContext(paramJToolBar, Region.TOOL_BAR_CONTENT, null, 1);
    this.contentStyle = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
    synthContext = getContext(paramJToolBar, Region.TOOL_BAR_DRAG_WINDOW, null, 1);
    this.dragWindowStyle = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
    synthContext = getContext(paramJToolBar, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (synthStyle != this.style) {
      this.handleIcon = this.style.getIcon(synthContext, "ToolBar.handleIcon");
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.toolBar, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    this.handleIcon = null;
    synthContext = getContext(this.toolBar, Region.TOOL_BAR_CONTENT, this.contentStyle, 1);
    this.contentStyle.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.contentStyle = null;
    synthContext = getContext(this.toolBar, Region.TOOL_BAR_DRAG_WINDOW, this.dragWindowStyle, 1);
    this.dragWindowStyle.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.dragWindowStyle = null;
    this.toolBar.setLayout(null);
  }
  
  protected void installComponents() {}
  
  protected void uninstallComponents() {}
  
  protected LayoutManager createLayout() { return new SynthToolBarLayoutManager(); }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle) { return SynthContext.getContext(paramJComponent, paramRegion, paramSynthStyle, getComponentState(paramJComponent, paramRegion)); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle, int paramInt) { return SynthContext.getContext(paramJComponent, paramRegion, paramSynthStyle, paramInt); }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintToolBarBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), this.toolBar.getOrientation());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintToolBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, this.toolBar.getOrientation()); }
  
  protected void setBorderToNonRollover(Component paramComponent) {}
  
  protected void setBorderToRollover(Component paramComponent) {}
  
  protected void setBorderToNormal(Component paramComponent) {}
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    if (this.handleIcon != null && this.toolBar.isFloatable()) {
      byte b = this.toolBar.getComponentOrientation().isLeftToRight() ? 0 : (this.toolBar.getWidth() - SynthIcon.getIconWidth(this.handleIcon, paramSynthContext));
      SynthIcon.paintIcon(this.handleIcon, paramSynthContext, paramGraphics, b, 0, SynthIcon.getIconWidth(this.handleIcon, paramSynthContext), SynthIcon.getIconHeight(this.handleIcon, paramSynthContext));
    } 
    SynthContext synthContext = getContext(this.toolBar, Region.TOOL_BAR_CONTENT, this.contentStyle);
    paintContent(synthContext, paramGraphics, this.contentRect);
    synthContext.dispose();
  }
  
  protected void paintContent(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle) {
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    paramSynthContext.getPainter().paintToolBarContentBackground(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, this.toolBar.getOrientation());
    paramSynthContext.getPainter().paintToolBarContentBorder(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, this.toolBar.getOrientation());
  }
  
  protected void paintDragWindow(Graphics paramGraphics) {
    int i = this.dragWindow.getWidth();
    int j = this.dragWindow.getHeight();
    SynthContext synthContext = getContext(this.toolBar, Region.TOOL_BAR_DRAG_WINDOW, this.dragWindowStyle);
    SynthLookAndFeel.updateSubregion(synthContext, paramGraphics, new Rectangle(0, 0, i, j));
    synthContext.getPainter().paintToolBarDragWindowBackground(synthContext, paramGraphics, 0, 0, i, j, this.dragWindow.getOrientation());
    synthContext.getPainter().paintToolBarDragWindowBorder(synthContext, paramGraphics, 0, 0, i, j, this.dragWindow.getOrientation());
    synthContext.dispose();
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JToolBar)paramPropertyChangeEvent.getSource()); 
  }
  
  class SynthToolBarLayoutManager implements LayoutManager {
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public Dimension minimumLayoutSize(Container param1Container) {
      JToolBar jToolBar = (JToolBar)param1Container;
      Insets insets = jToolBar.getInsets();
      Dimension dimension = new Dimension();
      SynthContext synthContext = SynthToolBarUI.this.getContext(jToolBar);
      if (jToolBar.getOrientation() == 0) {
        dimension.width = jToolBar.isFloatable() ? SynthIcon.getIconWidth(SynthToolBarUI.this.handleIcon, synthContext) : 0;
        for (byte b = 0; b < jToolBar.getComponentCount(); b++) {
          Component component = jToolBar.getComponent(b);
          if (component.isVisible()) {
            Dimension dimension1 = component.getMinimumSize();
            dimension.width += dimension1.width;
            dimension.height = Math.max(dimension.height, dimension1.height);
          } 
        } 
      } else {
        dimension.height = jToolBar.isFloatable() ? SynthIcon.getIconHeight(SynthToolBarUI.this.handleIcon, synthContext) : 0;
        for (byte b = 0; b < jToolBar.getComponentCount(); b++) {
          Component component = jToolBar.getComponent(b);
          if (component.isVisible()) {
            Dimension dimension1 = component.getMinimumSize();
            dimension.width = Math.max(dimension.width, dimension1.width);
            dimension.height += dimension1.height;
          } 
        } 
      } 
      dimension.width += insets.left + insets.right;
      dimension.height += insets.top + insets.bottom;
      synthContext.dispose();
      return dimension;
    }
    
    public Dimension preferredLayoutSize(Container param1Container) {
      JToolBar jToolBar = (JToolBar)param1Container;
      Insets insets = jToolBar.getInsets();
      Dimension dimension = new Dimension();
      SynthContext synthContext = SynthToolBarUI.this.getContext(jToolBar);
      if (jToolBar.getOrientation() == 0) {
        dimension.width = jToolBar.isFloatable() ? SynthIcon.getIconWidth(SynthToolBarUI.this.handleIcon, synthContext) : 0;
        for (byte b = 0; b < jToolBar.getComponentCount(); b++) {
          Component component = jToolBar.getComponent(b);
          if (component.isVisible()) {
            Dimension dimension1 = component.getPreferredSize();
            dimension.width += dimension1.width;
            dimension.height = Math.max(dimension.height, dimension1.height);
          } 
        } 
      } else {
        dimension.height = jToolBar.isFloatable() ? SynthIcon.getIconHeight(SynthToolBarUI.this.handleIcon, synthContext) : 0;
        for (byte b = 0; b < jToolBar.getComponentCount(); b++) {
          Component component = jToolBar.getComponent(b);
          if (component.isVisible()) {
            Dimension dimension1 = component.getPreferredSize();
            dimension.width = Math.max(dimension.width, dimension1.width);
            dimension.height += dimension1.height;
          } 
        } 
      } 
      dimension.width += insets.left + insets.right;
      dimension.height += insets.top + insets.bottom;
      synthContext.dispose();
      return dimension;
    }
    
    public void layoutContainer(Container param1Container) {
      JToolBar jToolBar = (JToolBar)param1Container;
      Insets insets = jToolBar.getInsets();
      boolean bool = jToolBar.getComponentOrientation().isLeftToRight();
      SynthContext synthContext = SynthToolBarUI.this.getContext(jToolBar);
      int i = 0;
      int j;
      for (j = 0; j < jToolBar.getComponentCount(); j++) {
        if (isGlue(jToolBar.getComponent(j)))
          i++; 
      } 
      if (jToolBar.getOrientation() == 0) {
        j = jToolBar.isFloatable() ? SynthIcon.getIconWidth(SynthToolBarUI.this.handleIcon, synthContext) : 0;
        this.this$0.contentRect.x = bool ? j : 0;
        this.this$0.contentRect.y = 0;
        this.this$0.contentRect.width = jToolBar.getWidth() - j;
        this.this$0.contentRect.height = jToolBar.getHeight();
        int k = bool ? (j + insets.left) : (jToolBar.getWidth() - j - insets.right);
        int m = insets.top;
        int n = jToolBar.getHeight() - insets.top - insets.bottom;
        int i1 = 0;
        if (i > 0) {
          int i2 = (minimumLayoutSize(param1Container)).width;
          i1 = (jToolBar.getWidth() - i2) / i;
          if (i1 < 0)
            i1 = 0; 
        } 
        for (byte b = 0; b < jToolBar.getComponentCount(); b++) {
          Component component = jToolBar.getComponent(b);
          if (component.isVisible()) {
            int i3;
            int i2;
            Dimension dimension = component.getPreferredSize();
            if (dimension.height >= n || component instanceof javax.swing.JSeparator) {
              i2 = m;
              i3 = n;
            } else {
              i2 = m + n / 2 - dimension.height / 2;
              i3 = dimension.height;
            } 
            if (isGlue(component))
              dimension.width += i1; 
            component.setBounds(bool ? k : (k - dimension.width), i2, dimension.width, i3);
            k = bool ? (k + dimension.width) : (k - dimension.width);
          } 
        } 
      } else {
        j = jToolBar.isFloatable() ? SynthIcon.getIconHeight(SynthToolBarUI.this.handleIcon, synthContext) : 0;
        this.this$0.contentRect.x = 0;
        this.this$0.contentRect.y = j;
        this.this$0.contentRect.width = jToolBar.getWidth();
        this.this$0.contentRect.height = jToolBar.getHeight() - j;
        int k = insets.left;
        int m = jToolBar.getWidth() - insets.left - insets.right;
        int n = j + insets.top;
        int i1 = 0;
        if (i > 0) {
          int i2 = (minimumLayoutSize(param1Container)).height;
          i1 = (jToolBar.getHeight() - i2) / i;
          if (i1 < 0)
            i1 = 0; 
        } 
        for (byte b = 0; b < jToolBar.getComponentCount(); b++) {
          Component component = jToolBar.getComponent(b);
          if (component.isVisible()) {
            int i3;
            int i2;
            Dimension dimension = component.getPreferredSize();
            if (dimension.width >= m || component instanceof javax.swing.JSeparator) {
              i2 = k;
              i3 = m;
            } else {
              i2 = k + m / 2 - dimension.width / 2;
              i3 = dimension.width;
            } 
            if (isGlue(component))
              dimension.height += i1; 
            component.setBounds(i2, n, i3, dimension.height);
            n += dimension.height;
          } 
        } 
      } 
      synthContext.dispose();
    }
    
    private boolean isGlue(Component param1Component) {
      if (param1Component.isVisible() && param1Component instanceof Box.Filler) {
        Box.Filler filler = (Box.Filler)param1Component;
        Dimension dimension1 = filler.getMinimumSize();
        Dimension dimension2 = filler.getPreferredSize();
        return (dimension1.width == 0 && dimension1.height == 0 && dimension2.width == 0 && dimension2.height == 0);
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthToolBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */