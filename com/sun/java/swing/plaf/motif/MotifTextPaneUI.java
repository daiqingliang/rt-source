package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.Caret;

public class MotifTextPaneUI extends BasicTextPaneUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifTextPaneUI(); }
  
  protected Caret createCaret() { return MotifTextUI.createCaret(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifTextPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */