package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JToolBar;

class ToolBarEastState extends State {
  ToolBarEastState() { super("East"); }
  
  protected boolean isInState(JComponent paramJComponent) { return (paramJComponent instanceof JToolBar && NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)paramJComponent) == "East"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ToolBarEastState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */