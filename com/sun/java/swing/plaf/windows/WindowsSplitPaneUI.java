package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class WindowsSplitPaneUI extends BasicSplitPaneUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsSplitPaneUI(); }
  
  public BasicSplitPaneDivider createDefaultDivider() { return new WindowsSplitPaneDivider(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsSplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */