package javax.swing.text.html;

import java.io.Serializable;
import javax.swing.DefaultComboBoxModel;

class OptionComboBoxModel<E> extends DefaultComboBoxModel<E> implements Serializable {
  private Option selectedOption = null;
  
  public void setInitialSelection(Option paramOption) { this.selectedOption = paramOption; }
  
  public Option getInitialSelection() { return this.selectedOption; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\OptionComboBoxModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */