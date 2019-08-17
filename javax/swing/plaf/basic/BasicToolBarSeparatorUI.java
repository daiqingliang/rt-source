package javax.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

public class BasicToolBarSeparatorUI extends BasicSeparatorUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicToolBarSeparatorUI(); }
  
  protected void installDefaults(JSeparator paramJSeparator) {
    Dimension dimension = ((JToolBar.Separator)paramJSeparator).getSeparatorSize();
    if (dimension == null || dimension instanceof javax.swing.plaf.UIResource) {
      JToolBar.Separator separator = (JToolBar.Separator)paramJSeparator;
      dimension = (Dimension)UIManager.get("ToolBar.separatorSize");
      if (dimension != null) {
        if (separator.getOrientation() == 0)
          dimension = new Dimension(dimension.height, dimension.width); 
        separator.setSeparatorSize(dimension);
      } 
    } 
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {}
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension = ((JToolBar.Separator)paramJComponent).getSeparatorSize();
    return (dimension != null) ? dimension.getSize() : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicToolBarSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */