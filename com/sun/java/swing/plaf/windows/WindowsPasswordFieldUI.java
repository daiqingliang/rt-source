package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.Caret;

public class WindowsPasswordFieldUI extends BasicPasswordFieldUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsPasswordFieldUI(); }
  
  protected Caret createCaret() { return new WindowsTextUI.WindowsCaret(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsPasswordFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */