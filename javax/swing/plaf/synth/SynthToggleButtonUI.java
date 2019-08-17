package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class SynthToggleButtonUI extends SynthButtonUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthToggleButtonUI(); }
  
  protected String getPropertyPrefix() { return "ToggleButton."; }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent) {
    if (((AbstractButton)paramJComponent).isContentAreaFilled()) {
      byte b1 = 0;
      byte b2 = 0;
      int i = paramJComponent.getWidth();
      int j = paramJComponent.getHeight();
      SynthPainter synthPainter = paramSynthContext.getPainter();
      synthPainter.paintToggleButtonBackground(paramSynthContext, paramGraphics, b1, b2, i, j);
    } 
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintToggleButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthToggleButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */