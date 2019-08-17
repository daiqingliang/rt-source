package sun.awt.im;

import java.awt.im.InputContext;
import javax.swing.JFrame;

public class InputMethodJFrame extends JFrame implements InputMethodWindow {
  InputContext inputContext = null;
  
  private static final long serialVersionUID = -4705856747771842549L;
  
  public InputMethodJFrame(String paramString, InputContext paramInputContext) {
    super(paramString);
    if (JFrame.isDefaultLookAndFeelDecorated()) {
      setUndecorated(true);
      getRootPane().setWindowDecorationStyle(0);
    } 
    if (paramInputContext != null)
      this.inputContext = paramInputContext; 
    setFocusableWindowState(false);
  }
  
  public void setInputContext(InputContext paramInputContext) { this.inputContext = paramInputContext; }
  
  public InputContext getInputContext() { return (this.inputContext != null) ? this.inputContext : super.getInputContext(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\InputMethodJFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */