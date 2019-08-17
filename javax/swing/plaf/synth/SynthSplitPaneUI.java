package javax.swing.plaf.synth;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SynthSplitPaneUI extends BasicSplitPaneUI implements PropertyChangeListener, SynthUI {
  private static Set<KeyStroke> managingFocusForwardTraversalKeys;
  
  private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
  
  private SynthStyle style;
  
  private SynthStyle dividerStyle;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthSplitPaneUI(); }
  
  protected void installDefaults() {
    updateStyle(this.splitPane);
    setOrientation(this.splitPane.getOrientation());
    setContinuousLayout(this.splitPane.isContinuousLayout());
    resetLayoutManager();
    if (this.nonContinuousLayoutDivider == null) {
      setNonContinuousLayoutDivider(createDefaultNonContinuousLayoutDivider(), true);
    } else {
      setNonContinuousLayoutDivider(this.nonContinuousLayoutDivider, true);
    } 
    if (managingFocusForwardTraversalKeys == null) {
      managingFocusForwardTraversalKeys = new HashSet();
      managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 0));
    } 
    this.splitPane.setFocusTraversalKeys(0, managingFocusForwardTraversalKeys);
    if (managingFocusBackwardTraversalKeys == null) {
      managingFocusBackwardTraversalKeys = new HashSet();
      managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 1));
    } 
    this.splitPane.setFocusTraversalKeys(1, managingFocusBackwardTraversalKeys);
  }
  
  private void updateStyle(JSplitPane paramJSplitPane) {
    SynthContext synthContext = getContext(paramJSplitPane, Region.SPLIT_PANE_DIVIDER, 1);
    SynthStyle synthStyle1 = this.dividerStyle;
    this.dividerStyle = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
    synthContext = getContext(paramJSplitPane, 1);
    SynthStyle synthStyle2 = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle2) {
      Object object = this.style.get(synthContext, "SplitPane.size");
      if (object == null)
        object = Integer.valueOf(6); 
      LookAndFeel.installProperty(paramJSplitPane, "dividerSize", object);
      object = this.style.get(synthContext, "SplitPane.oneTouchExpandable");
      if (object != null)
        LookAndFeel.installProperty(paramJSplitPane, "oneTouchExpandable", object); 
      if (this.divider != null) {
        paramJSplitPane.remove(this.divider);
        this.divider.setDividerSize(paramJSplitPane.getDividerSize());
      } 
      if (synthStyle2 != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    if (this.style != synthStyle2 || this.dividerStyle != synthStyle1) {
      if (this.divider != null)
        paramJSplitPane.remove(this.divider); 
      this.divider = createDefaultDivider();
      this.divider.setBasicSplitPaneUI(this);
      paramJSplitPane.add(this.divider, "divider");
    } 
    synthContext.dispose();
  }
  
  protected void installListeners() {
    super.installListeners();
    this.splitPane.addPropertyChangeListener(this);
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.splitPane, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    synthContext = getContext(this.splitPane, Region.SPLIT_PANE_DIVIDER, 1);
    this.dividerStyle.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.dividerStyle = null;
    super.uninstallDefaults();
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.splitPane.removePropertyChangeListener(this);
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  SynthContext getContext(JComponent paramJComponent, Region paramRegion) { return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion)); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt) { return (paramRegion == Region.SPLIT_PANE_DIVIDER) ? SynthContext.getContext(paramJComponent, paramRegion, this.dividerStyle, paramInt) : SynthContext.getContext(paramJComponent, paramRegion, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion) {
    int i = SynthLookAndFeel.getComponentState(paramJComponent);
    if (this.divider.isMouseOver())
      i |= 0x2; 
    return i;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JSplitPane)paramPropertyChangeEvent.getSource()); 
  }
  
  public BasicSplitPaneDivider createDefaultDivider() {
    SynthSplitPaneDivider synthSplitPaneDivider = new SynthSplitPaneDivider(this);
    synthSplitPaneDivider.setDividerSize(this.splitPane.getDividerSize());
    return synthSplitPaneDivider;
  }
  
  protected Component createDefaultNonContinuousLayoutDivider() { return new Canvas() {
        public void paint(Graphics param1Graphics) { SynthSplitPaneUI.this.paintDragDivider(param1Graphics, 0, 0, getWidth(), getHeight()); }
      }; }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintSplitPaneBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) { super.paint(paramGraphics, this.splitPane); }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintSplitPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  private void paintDragDivider(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    SynthContext synthContext = getContext(this.splitPane, Region.SPLIT_PANE_DIVIDER);
    synthContext.setComponentState((synthContext.getComponentState() | 0x2) ^ 0x2 | 0x4);
    Shape shape = paramGraphics.getClip();
    paramGraphics.clipRect(paramInt1, paramInt2, paramInt3, paramInt4);
    synthContext.getPainter().paintSplitPaneDragDivider(synthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, this.splitPane.getOrientation());
    paramGraphics.setClip(shape);
    synthContext.dispose();
  }
  
  public void finishedPaintingChildren(JSplitPane paramJSplitPane, Graphics paramGraphics) {
    if (paramJSplitPane == this.splitPane && getLastDragLocation() != -1 && !isContinuousLayout() && !this.draggingHW)
      if (paramJSplitPane.getOrientation() == 1) {
        paintDragDivider(paramGraphics, getLastDragLocation(), 0, this.dividerSize - 1, this.splitPane.getHeight() - 1);
      } else {
        paintDragDivider(paramGraphics, 0, getLastDragLocation(), this.splitPane.getWidth() - 1, this.dividerSize - 1);
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthSplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */