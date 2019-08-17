package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class SynthScrollPaneUI extends BasicScrollPaneUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  private boolean viewportViewHasFocus = false;
  
  private ViewportViewFocusHandler viewportViewFocusHandler;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthScrollPaneUI(); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintScrollPaneBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    Border border = this.scrollpane.getViewportBorder();
    if (border != null) {
      Rectangle rectangle = this.scrollpane.getViewportBorderBounds();
      border.paintBorder(this.scrollpane, paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintScrollPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  protected void installDefaults(JScrollPane paramJScrollPane) { updateStyle(paramJScrollPane); }
  
  private void updateStyle(JScrollPane paramJScrollPane) {
    SynthContext synthContext = getContext(paramJScrollPane, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      Border border = this.scrollpane.getViewportBorder();
      if (border == null || border instanceof UIResource)
        this.scrollpane.setViewportBorder(new ViewportBorder(synthContext)); 
      if (synthStyle != null) {
        uninstallKeyboardActions(paramJScrollPane);
        installKeyboardActions(paramJScrollPane);
      } 
    } 
    synthContext.dispose();
  }
  
  protected void installListeners(JScrollPane paramJScrollPane) {
    super.installListeners(paramJScrollPane);
    paramJScrollPane.addPropertyChangeListener(this);
    if (UIManager.getBoolean("ScrollPane.useChildTextComponentFocus")) {
      this.viewportViewFocusHandler = new ViewportViewFocusHandler(null);
      paramJScrollPane.getViewport().addContainerListener(this.viewportViewFocusHandler);
      Component component = paramJScrollPane.getViewport().getView();
      if (component instanceof javax.swing.text.JTextComponent)
        component.addFocusListener(this.viewportViewFocusHandler); 
    } 
  }
  
  protected void uninstallDefaults(JScrollPane paramJScrollPane) {
    SynthContext synthContext = getContext(paramJScrollPane, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    if (this.scrollpane.getViewportBorder() instanceof UIResource)
      this.scrollpane.setViewportBorder(null); 
  }
  
  protected void uninstallListeners(JComponent paramJComponent) {
    super.uninstallListeners(paramJComponent);
    paramJComponent.removePropertyChangeListener(this);
    if (this.viewportViewFocusHandler != null) {
      JViewport jViewport = ((JScrollPane)paramJComponent).getViewport();
      jViewport.removeContainerListener(this.viewportViewFocusHandler);
      if (jViewport.getView() != null)
        jViewport.getView().removeFocusListener(this.viewportViewFocusHandler); 
      this.viewportViewFocusHandler = null;
    } 
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) {
    int i = SynthLookAndFeel.getComponentState(paramJComponent);
    if (this.viewportViewFocusHandler != null && this.viewportViewHasFocus)
      i |= 0x100; 
    return i;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle(this.scrollpane); 
  }
  
  private class ViewportBorder extends AbstractBorder implements UIResource {
    private Insets insets;
    
    ViewportBorder(SynthContext param1SynthContext) {
      this.insets = (Insets)param1SynthContext.getStyle().get(param1SynthContext, "ScrollPane.viewportBorderInsets");
      if (this.insets == null)
        this.insets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS; 
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      JComponent jComponent = (JComponent)param1Component;
      SynthContext synthContext = SynthScrollPaneUI.this.getContext(jComponent);
      SynthStyle synthStyle = synthContext.getStyle();
      if (synthStyle == null) {
        assert false : "SynthBorder is being used outside after the  UI has been uninstalled";
        return;
      } 
      synthContext.getPainter().paintViewportBorder(synthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      synthContext.dispose();
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      if (param1Insets == null)
        return new Insets(this.insets.top, this.insets.left, this.insets.bottom, this.insets.right); 
      param1Insets.top = this.insets.top;
      param1Insets.bottom = this.insets.bottom;
      param1Insets.left = this.insets.left;
      param1Insets.right = this.insets.left;
      return param1Insets;
    }
    
    public boolean isBorderOpaque() { return false; }
  }
  
  private class ViewportViewFocusHandler implements ContainerListener, FocusListener {
    private ViewportViewFocusHandler() {}
    
    public void componentAdded(ContainerEvent param1ContainerEvent) {
      if (param1ContainerEvent.getChild() instanceof javax.swing.text.JTextComponent) {
        param1ContainerEvent.getChild().addFocusListener(this);
        SynthScrollPaneUI.this.viewportViewHasFocus = param1ContainerEvent.getChild().isFocusOwner();
        SynthScrollPaneUI.this.scrollpane.repaint();
      } 
    }
    
    public void componentRemoved(ContainerEvent param1ContainerEvent) {
      if (param1ContainerEvent.getChild() instanceof javax.swing.text.JTextComponent)
        param1ContainerEvent.getChild().removeFocusListener(this); 
    }
    
    public void focusGained(FocusEvent param1FocusEvent) {
      SynthScrollPaneUI.this.viewportViewHasFocus = true;
      SynthScrollPaneUI.this.scrollpane.repaint();
    }
    
    public void focusLost(FocusEvent param1FocusEvent) {
      SynthScrollPaneUI.this.viewportViewHasFocus = false;
      SynthScrollPaneUI.this.scrollpane.repaint();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthScrollPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */