package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Caret;

public class WindowsTextAreaUI extends BasicTextAreaUI {
  protected Caret createCaret() { return new WindowsTextUI.WindowsCaret(); }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsTextAreaUI(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTextAreaUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */