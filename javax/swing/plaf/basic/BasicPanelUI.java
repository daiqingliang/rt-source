package javax.swing.plaf.basic;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

public class BasicPanelUI extends PanelUI {
  private static PanelUI panelUI;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    if (panelUI == null)
      panelUI = new BasicPanelUI(); 
    return panelUI;
  }
  
  public void installUI(JComponent paramJComponent) {
    JPanel jPanel = (JPanel)paramJComponent;
    super.installUI(jPanel);
    installDefaults(jPanel);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    JPanel jPanel = (JPanel)paramJComponent;
    uninstallDefaults(jPanel);
    super.uninstallUI(paramJComponent);
  }
  
  protected void installDefaults(JPanel paramJPanel) {
    LookAndFeel.installColorsAndFont(paramJPanel, "Panel.background", "Panel.foreground", "Panel.font");
    LookAndFeel.installBorder(paramJPanel, "Panel.border");
    LookAndFeel.installProperty(paramJPanel, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults(JPanel paramJPanel) { LookAndFeel.uninstallBorder(paramJPanel); }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    Border border = paramJComponent.getBorder();
    return (border instanceof AbstractBorder) ? ((AbstractBorder)border).getBaseline(paramJComponent, paramInt1, paramInt2) : -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    Border border = paramJComponent.getBorder();
    return (border instanceof AbstractBorder) ? ((AbstractBorder)border).getBaselineResizeBehavior(paramJComponent) : Component.BaselineResizeBehavior.OTHER;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicPanelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */