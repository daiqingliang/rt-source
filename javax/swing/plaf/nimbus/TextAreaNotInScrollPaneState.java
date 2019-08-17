package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class TextAreaNotInScrollPaneState extends State {
  TextAreaNotInScrollPaneState() { super("NotInScrollPane"); }
  
  protected boolean isInState(JComponent paramJComponent) { return !(paramJComponent.getParent() instanceof javax.swing.JViewport); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\TextAreaNotInScrollPaneState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */