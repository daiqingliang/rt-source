package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicColorChooserUI;

public class SynthColorChooserUI extends BasicColorChooserUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthColorChooserUI(); }
  
  protected AbstractColorChooserPanel[] createDefaultChoosers() {
    SynthContext synthContext = getContext(this.chooser, 1);
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = (AbstractColorChooserPanel[])synthContext.getStyle().get(synthContext, "ColorChooser.panels");
    synthContext.dispose();
    if (arrayOfAbstractColorChooserPanel == null)
      arrayOfAbstractColorChooserPanel = ColorChooserComponentFactory.getDefaultChooserPanels(); 
    return arrayOfAbstractColorChooserPanel;
  }
  
  protected void installDefaults() {
    super.installDefaults();
    updateStyle(this.chooser);
  }
  
  private void updateStyle(JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent, 1);
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.chooser, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    super.uninstallDefaults();
  }
  
  protected void installListeners() {
    super.installListeners();
    this.chooser.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners() {
    this.chooser.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintColorChooserBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintColorChooserBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JColorChooser)paramPropertyChangeEvent.getSource()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthColorChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */