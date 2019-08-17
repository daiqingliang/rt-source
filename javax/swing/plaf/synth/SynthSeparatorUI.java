package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.SeparatorUI;

public class SynthSeparatorUI extends SeparatorUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthSeparatorUI(); }
  
  public void installUI(JComponent paramJComponent) {
    installDefaults((JSeparator)paramJComponent);
    installListeners((JSeparator)paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallListeners((JSeparator)paramJComponent);
    uninstallDefaults((JSeparator)paramJComponent);
  }
  
  public void installDefaults(JSeparator paramJSeparator) { updateStyle(paramJSeparator); }
  
  private void updateStyle(JSeparator paramJSeparator) {
    SynthContext synthContext = getContext(paramJSeparator, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle && paramJSeparator instanceof JToolBar.Separator) {
      Dimension dimension = ((JToolBar.Separator)paramJSeparator).getSeparatorSize();
      if (dimension == null || dimension instanceof javax.swing.plaf.UIResource) {
        dimension = (DimensionUIResource)this.style.get(synthContext, "ToolBar.separatorSize");
        if (dimension == null)
          dimension = new DimensionUIResource(10, 10); 
        ((JToolBar.Separator)paramJSeparator).setSeparatorSize(dimension);
      } 
    } 
    synthContext.dispose();
  }
  
  public void uninstallDefaults(JSeparator paramJSeparator) {
    SynthContext synthContext = getContext(paramJSeparator, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  public void installListeners(JSeparator paramJSeparator) { paramJSeparator.addPropertyChangeListener(this); }
  
  public void uninstallListeners(JSeparator paramJSeparator) { paramJSeparator.removePropertyChangeListener(this); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    JSeparator jSeparator = (JSeparator)synthContext.getComponent();
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintSeparatorBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), jSeparator.getOrientation());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    JSeparator jSeparator = (JSeparator)paramSynthContext.getComponent();
    paramSynthContext.getPainter().paintSeparatorForeground(paramSynthContext, paramGraphics, 0, 0, jSeparator.getWidth(), jSeparator.getHeight(), jSeparator.getOrientation());
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    JSeparator jSeparator = (JSeparator)paramSynthContext.getComponent();
    paramSynthContext.getPainter().paintSeparatorBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, jSeparator.getOrientation());
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension;
    SynthContext synthContext = getContext(paramJComponent);
    int i = this.style.getInt(synthContext, "Separator.thickness", 2);
    Insets insets = paramJComponent.getInsets();
    if (((JSeparator)paramJComponent).getOrientation() == 1) {
      dimension = new Dimension(insets.left + insets.right + i, insets.top + insets.bottom);
    } else {
      dimension = new Dimension(insets.left + insets.right, insets.top + insets.bottom + i);
    } 
    synthContext.dispose();
    return dimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return getPreferredSize(paramJComponent); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return new Dimension(32767, 32767); }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JSeparator)paramPropertyChangeEvent.getSource()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */