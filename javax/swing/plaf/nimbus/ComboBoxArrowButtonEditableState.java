package javax.swing.plaf.nimbus;

import java.awt.Container;
import javax.swing.JComboBox;
import javax.swing.JComponent;

class ComboBoxArrowButtonEditableState extends State {
  ComboBoxArrowButtonEditableState() { super("Editable"); }
  
  protected boolean isInState(JComponent paramJComponent) {
    Container container = paramJComponent.getParent();
    return (container instanceof JComboBox && ((JComboBox)container).isEditable());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ComboBoxArrowButtonEditableState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */