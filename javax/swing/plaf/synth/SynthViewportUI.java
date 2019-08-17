package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ViewportUI;

public class SynthViewportUI extends ViewportUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthViewportUI(); }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    installDefaults(paramJComponent);
    installListeners(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    super.uninstallUI(paramJComponent);
    uninstallListeners(paramJComponent);
    uninstallDefaults(paramJComponent);
  }
  
  protected void installDefaults(JComponent paramJComponent) { updateStyle(paramJComponent); }
  
  private void updateStyle(JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent, 1);
    SynthStyle synthStyle1 = SynthLookAndFeel.getStyle(synthContext.getComponent(), synthContext.getRegion());
    SynthStyle synthStyle2 = synthContext.getStyle();
    if (synthStyle1 != synthStyle2) {
      if (synthStyle2 != null)
        synthStyle2.uninstallDefaults(synthContext); 
      synthContext.setStyle(synthStyle1);
      synthStyle1.installDefaults(synthContext);
    } 
    this.style = synthStyle1;
    synthContext.dispose();
  }
  
  protected void installListeners(JComponent paramJComponent) { paramJComponent.addPropertyChangeListener(this); }
  
  protected void uninstallListeners(JComponent paramJComponent) { paramJComponent.removePropertyChangeListener(this); }
  
  protected void uninstallDefaults(JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private Region getRegion(JComponent paramJComponent) { return SynthLookAndFeel.getRegion(paramJComponent); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintViewportBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JComponent)paramPropertyChangeEvent.getSource()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthViewportUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */