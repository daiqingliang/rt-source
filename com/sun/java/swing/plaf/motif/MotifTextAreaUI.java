package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Caret;

public class MotifTextAreaUI extends BasicTextAreaUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifTextAreaUI(); }
  
  protected Caret createCaret() { return MotifTextUI.createCaret(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifTextAreaUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */