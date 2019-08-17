package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

class SplitPaneVerticalState extends State {
  SplitPaneVerticalState() { super("Vertical"); }
  
  protected boolean isInState(JComponent paramJComponent) { return (paramJComponent instanceof JSplitPane && ((JSplitPane)paramJComponent).getOrientation() == 1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\SplitPaneVerticalState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */