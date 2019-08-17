package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

class InternalFrameWindowFocusedState extends State {
  InternalFrameWindowFocusedState() { super("WindowFocused"); }
  
  protected boolean isInState(JComponent paramJComponent) { return (paramJComponent instanceof JInternalFrame && ((JInternalFrame)paramJComponent).isSelected()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\InternalFrameWindowFocusedState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */