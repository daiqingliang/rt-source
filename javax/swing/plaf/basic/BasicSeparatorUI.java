package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SeparatorUI;

public class BasicSeparatorUI extends SeparatorUI {
  protected Color shadow;
  
  protected Color highlight;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicSeparatorUI(); }
  
  public void installUI(JComponent paramJComponent) {
    installDefaults((JSeparator)paramJComponent);
    installListeners((JSeparator)paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults((JSeparator)paramJComponent);
    uninstallListeners((JSeparator)paramJComponent);
  }
  
  protected void installDefaults(JSeparator paramJSeparator) {
    LookAndFeel.installColors(paramJSeparator, "Separator.background", "Separator.foreground");
    LookAndFeel.installProperty(paramJSeparator, "opaque", Boolean.FALSE);
  }
  
  protected void uninstallDefaults(JSeparator paramJSeparator) {}
  
  protected void installListeners(JSeparator paramJSeparator) {}
  
  protected void uninstallListeners(JSeparator paramJSeparator) {}
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    Dimension dimension = paramJComponent.getSize();
    if (((JSeparator)paramJComponent).getOrientation() == 1) {
      paramGraphics.setColor(paramJComponent.getForeground());
      paramGraphics.drawLine(0, 0, 0, dimension.height);
      paramGraphics.setColor(paramJComponent.getBackground());
      paramGraphics.drawLine(1, 0, 1, dimension.height);
    } else {
      paramGraphics.setColor(paramJComponent.getForeground());
      paramGraphics.drawLine(0, 0, dimension.width, 0);
      paramGraphics.setColor(paramJComponent.getBackground());
      paramGraphics.drawLine(0, 1, dimension.width, 1);
    } 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return (((JSeparator)paramJComponent).getOrientation() == 1) ? new Dimension(2, 0) : new Dimension(0, 2); }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return null; }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */