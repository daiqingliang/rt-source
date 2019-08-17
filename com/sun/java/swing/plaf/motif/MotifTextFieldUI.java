package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;

public class MotifTextFieldUI extends BasicTextFieldUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifTextFieldUI(); }
  
  protected Caret createCaret() { return MotifTextUI.createCaret(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */