package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class MetalPopupMenuSeparatorUI extends MetalSeparatorUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalPopupMenuSeparatorUI(); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    Dimension dimension = paramJComponent.getSize();
    paramGraphics.setColor(paramJComponent.getForeground());
    paramGraphics.drawLine(0, 1, dimension.width, 1);
    paramGraphics.setColor(paramJComponent.getBackground());
    paramGraphics.drawLine(0, 2, dimension.width, 2);
    paramGraphics.drawLine(0, 0, 0, 0);
    paramGraphics.drawLine(0, 3, 0, 3);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return new Dimension(0, 4); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalPopupMenuSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */