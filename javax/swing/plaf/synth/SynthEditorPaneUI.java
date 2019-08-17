package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;

public class SynthEditorPaneUI extends BasicEditorPaneUI implements SynthUI {
  private SynthStyle style;
  
  private Boolean localTrue = Boolean.TRUE;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthEditorPaneUI(); }
  
  protected void installDefaults() {
    super.installDefaults();
    JTextComponent jTextComponent = getComponent();
    Object object = jTextComponent.getClientProperty("JEditorPane.honorDisplayProperties");
    if (object == null)
      jTextComponent.putClientProperty("JEditorPane.honorDisplayProperties", this.localTrue); 
    updateStyle(getComponent());
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(getComponent(), 1);
    JTextComponent jTextComponent = getComponent();
    jTextComponent.putClientProperty("caretAspectRatio", null);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    Object object = jTextComponent.getClientProperty("JEditorPane.honorDisplayProperties");
    if (object == this.localTrue)
      jTextComponent.putClientProperty("JEditorPane.honorDisplayProperties", Boolean.FALSE); 
    super.uninstallDefaults();
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JTextComponent)paramPropertyChangeEvent.getSource()); 
    super.propertyChange(paramPropertyChangeEvent);
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
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    paintBackground(synthContext, paramGraphics, paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) { paint(paramGraphics, getComponent()); }
  
  protected void paintBackground(Graphics paramGraphics) {}
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent) { paramSynthContext.getPainter().paintEditorPaneBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight()); }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintEditorPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthEditorPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */