package javax.swing.plaf.metal;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class MetalSplitPaneUI extends BasicSplitPaneUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalSplitPaneUI(); }
  
  public BasicSplitPaneDivider createDefaultDivider() { return new MetalSplitPaneDivider(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalSplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */