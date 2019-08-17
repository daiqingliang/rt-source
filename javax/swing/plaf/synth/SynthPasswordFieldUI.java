package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Element;
import javax.swing.text.PasswordView;
import javax.swing.text.View;

public class SynthPasswordFieldUI extends SynthTextFieldUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthPasswordFieldUI(); }
  
  protected String getPropertyPrefix() { return "PasswordField"; }
  
  public View create(Element paramElement) { return new PasswordView(paramElement); }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent) { paramSynthContext.getPainter().paintPasswordFieldBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight()); }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintPasswordFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  protected void installKeyboardActions() {
    super.installKeyboardActions();
    ActionMap actionMap = SwingUtilities.getUIActionMap(getComponent());
    if (actionMap != null && actionMap.get("select-word") != null) {
      Action action = actionMap.get("select-line");
      if (action != null)
        actionMap.put("select-word", action); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthPasswordFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */