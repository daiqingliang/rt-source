package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class SynthMenuBarUI extends BasicMenuBarUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthMenuBarUI(); }
  
  protected void installDefaults() {
    if (this.menuBar.getLayout() == null || this.menuBar.getLayout() instanceof javax.swing.plaf.UIResource)
      this.menuBar.setLayout(new SynthMenuLayout(this.menuBar, 2)); 
    updateStyle(this.menuBar);
  }
  
  protected void installListeners() {
    super.installListeners();
    this.menuBar.addPropertyChangeListener(this);
  }
  
  private void updateStyle(JMenuBar paramJMenuBar) {
    SynthContext synthContext = getContext(paramJMenuBar, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle && synthStyle != null) {
      uninstallKeyboardActions();
      installKeyboardActions();
    } 
    synthContext.dispose();
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.menuBar, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.menuBar.removePropertyChangeListener(this);
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintMenuBarBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintMenuBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JMenuBar)paramPropertyChangeEvent.getSource()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthMenuBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */