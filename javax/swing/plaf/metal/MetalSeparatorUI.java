package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

public class MetalSeparatorUI extends BasicSeparatorUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalSeparatorUI(); }
  
  protected void installDefaults(JSeparator paramJSeparator) { LookAndFeel.installColors(paramJSeparator, "Separator.background", "Separator.foreground"); }
  
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */