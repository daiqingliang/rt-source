package javax.swing.plaf.basic;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ViewportUI;

public class BasicViewportUI extends ViewportUI {
  private static ViewportUI viewportUI;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    if (viewportUI == null)
      viewportUI = new BasicViewportUI(); 
    return viewportUI;
  }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    installDefaults(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults(paramJComponent);
    super.uninstallUI(paramJComponent);
  }
  
  protected void installDefaults(JComponent paramJComponent) {
    LookAndFeel.installColorsAndFont(paramJComponent, "Viewport.background", "Viewport.foreground", "Viewport.font");
    LookAndFeel.installProperty(paramJComponent, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults(JComponent paramJComponent) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicViewportUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */