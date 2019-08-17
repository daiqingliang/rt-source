package javax.swing.plaf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public abstract class ComponentUI {
  public void installUI(JComponent paramJComponent) {}
  
  public void uninstallUI(JComponent paramJComponent) {}
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {}
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    if (paramJComponent.isOpaque()) {
      paramGraphics.setColor(paramJComponent.getBackground());
      paramGraphics.fillRect(0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    } 
    paint(paramGraphics, paramJComponent);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return null; }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return getPreferredSize(paramJComponent); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return getPreferredSize(paramJComponent); }
  
  public boolean contains(JComponent paramJComponent, int paramInt1, int paramInt2) { return paramJComponent.inside(paramInt1, paramInt2); }
  
  public static ComponentUI createUI(JComponent paramJComponent) { throw new Error("ComponentUI.createUI not implemented."); }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    if (paramJComponent == null)
      throw new NullPointerException("Component must be non-null"); 
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Width and height must be >= 0"); 
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    if (paramJComponent == null)
      throw new NullPointerException("Component must be non-null"); 
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  public int getAccessibleChildrenCount(JComponent paramJComponent) { return SwingUtilities.getAccessibleChildrenCount(paramJComponent); }
  
  public Accessible getAccessibleChild(JComponent paramJComponent, int paramInt) { return SwingUtilities.getAccessibleChild(paramJComponent, paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\ComponentUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */