package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JToolBar;

class ToolBarSouthState extends State {
  ToolBarSouthState() { super("South"); }
  
  protected boolean isInState(JComponent paramJComponent) { return (paramJComponent instanceof JToolBar && NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)paramJComponent) == "South"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ToolBarSouthState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */