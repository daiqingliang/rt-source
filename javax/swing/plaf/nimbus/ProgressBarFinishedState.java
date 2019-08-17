package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

class ProgressBarFinishedState extends State {
  ProgressBarFinishedState() { super("Finished"); }
  
  protected boolean isInState(JComponent paramJComponent) { return (paramJComponent instanceof JProgressBar && ((JProgressBar)paramJComponent).getPercentComplete() == 1.0D); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ProgressBarFinishedState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */