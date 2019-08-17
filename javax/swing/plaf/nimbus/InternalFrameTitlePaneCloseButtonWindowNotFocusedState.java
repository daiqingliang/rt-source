package javax.swing.plaf.nimbus;

import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

class InternalFrameTitlePaneCloseButtonWindowNotFocusedState extends State {
  InternalFrameTitlePaneCloseButtonWindowNotFocusedState() { super("WindowNotFocused"); }
  
  protected boolean isInState(JComponent paramJComponent) {
    Container container = paramJComponent;
    while (container.getParent() != null && !(container instanceof JInternalFrame))
      container = container.getParent(); 
    return (container instanceof JInternalFrame) ? (!((JInternalFrame)container).isSelected()) : false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\InternalFrameTitlePaneCloseButtonWindowNotFocusedState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */