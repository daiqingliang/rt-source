package javax.swing.plaf.nimbus;

import javax.swing.JComboBox;
import javax.swing.JComponent;

class ComboBoxEditableState extends State {
  ComboBoxEditableState() { super("Editable"); }
  
  protected boolean isInState(JComponent paramJComponent) { return (paramJComponent instanceof JComboBox && ((JComboBox)paramJComponent).isEditable()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ComboBoxEditableState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */