package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

public class SynthTextAreaUI extends BasicTextAreaUI implements SynthUI {
  private Handler handler = new Handler(null);
  
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthTextAreaUI(); }
  
  protected void installDefaults() {
    super.installDefaults();
    updateStyle(getComponent());
    getComponent().addFocusListener(this.handler);
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(getComponent(), 1);
    getComponent().putClientProperty("caretAspectRatio", null);
    getComponent().removeFocusListener(this.handler);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    super.uninstallDefaults();
  }
  
  private void updateStyle(JTextComponent paramJTextComponent) {
    SynthContext synthContext = getContext(paramJTextComponent, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      SynthTextFieldUI.updateStyle(paramJTextComponent, synthContext, getPropertyPrefix());
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintTextAreaBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) { paint(paramGraphics, getComponent()); }
  
  protected void paintBackground(Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintTextAreaBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JTextComponent)paramPropertyChangeEvent.getSource()); 
    super.propertyChange(paramPropertyChangeEvent);
  }
  
  private final class Handler implements FocusListener {
    private Handler() {}
    
    public void focusGained(FocusEvent param1FocusEvent) { SynthTextAreaUI.this.getComponent().repaint(); }
    
    public void focusLost(FocusEvent param1FocusEvent) { SynthTextAreaUI.this.getComponent().repaint(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthTextAreaUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */