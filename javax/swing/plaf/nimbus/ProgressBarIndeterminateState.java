package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

class ProgressBarIndeterminateState extends State {
  ProgressBarIndeterminateState() { super("Indeterminate"); }
  
  protected boolean isInState(JComponent paramJComponent) { return (paramJComponent instanceof JProgressBar && ((JProgressBar)paramJComponent).isIndeterminate()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ProgressBarIndeterminateState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */