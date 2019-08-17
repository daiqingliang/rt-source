package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.Caret;

public class WindowsEditorPaneUI extends BasicEditorPaneUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsEditorPaneUI(); }
  
  protected Caret createCaret() { return new WindowsTextUI.WindowsCaret(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsEditorPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */