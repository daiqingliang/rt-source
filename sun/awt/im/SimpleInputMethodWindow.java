package sun.awt.im;

import java.awt.Frame;
import java.awt.im.InputContext;

public class SimpleInputMethodWindow extends Frame implements InputMethodWindow {
  InputContext inputContext = null;
  
  private static final long serialVersionUID = 5093376647036461555L;
  
  public SimpleInputMethodWindow(String paramString, InputContext paramInputContext) {
    super(paramString);
    if (paramInputContext != null)
      this.inputContext = paramInputContext; 
    setFocusableWindowState(false);
  }
  
  public void setInputContext(InputContext paramInputContext) { this.inputContext = paramInputContext; }
  
  public InputContext getInputContext() { return (this.inputContext != null) ? this.inputContext : super.getInputContext(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\SimpleInputMethodWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */