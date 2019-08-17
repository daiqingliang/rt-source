package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

public class SynthPanelUI extends BasicPanelUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthPanelUI(); }
  
  public void installUI(JComponent paramJComponent) {
    JPanel jPanel = (JPanel)paramJComponent;
    super.installUI(paramJComponent);
    installListeners(jPanel);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    JPanel jPanel = (JPanel)paramJComponent;
    uninstallListeners(jPanel);
    super.uninstallUI(paramJComponent);
  }
  
  protected void installListeners(JPanel paramJPanel) { paramJPanel.addPropertyChangeListener(this); }
  
  protected void uninstallListeners(JPanel paramJPanel) { paramJPanel.removePropertyChangeListener(this); }
  
  protected void installDefaults(JPanel paramJPanel) { updateStyle(paramJPanel); }
  
  protected void uninstallDefaults(JPanel paramJPanel) {
    SynthContext synthContext = getContext(paramJPanel, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  private void updateStyle(JPanel paramJPanel) {
    SynthContext synthContext = getContext(paramJPanel, 1);
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintPanelBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintPanelBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JPanel)paramPropertyChangeEvent.getSource()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthPanelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */