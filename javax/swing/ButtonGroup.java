package javax.swing;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class ButtonGroup implements Serializable {
  protected Vector<AbstractButton> buttons = new Vector();
  
  ButtonModel selection = null;
  
  public void add(AbstractButton paramAbstractButton) {
    if (paramAbstractButton == null)
      return; 
    this.buttons.addElement(paramAbstractButton);
    if (paramAbstractButton.isSelected())
      if (this.selection == null) {
        this.selection = paramAbstractButton.getModel();
      } else {
        paramAbstractButton.setSelected(false);
      }  
    paramAbstractButton.getModel().setGroup(this);
  }
  
  public void remove(AbstractButton paramAbstractButton) {
    if (paramAbstractButton == null)
      return; 
    this.buttons.removeElement(paramAbstractButton);
    if (paramAbstractButton.getModel() == this.selection)
      this.selection = null; 
    paramAbstractButton.getModel().setGroup(null);
  }
  
  public void clearSelection() {
    if (this.selection != null) {
      ButtonModel buttonModel = this.selection;
      this.selection = null;
      buttonModel.setSelected(false);
    } 
  }
  
  public Enumeration<AbstractButton> getElements() { return this.buttons.elements(); }
  
  public ButtonModel getSelection() { return this.selection; }
  
  public void setSelected(ButtonModel paramButtonModel, boolean paramBoolean) {
    if (paramBoolean && paramButtonModel != null && paramButtonModel != this.selection) {
      ButtonModel buttonModel = this.selection;
      this.selection = paramButtonModel;
      if (buttonModel != null)
        buttonModel.setSelected(false); 
      paramButtonModel.setSelected(true);
    } 
  }
  
  public boolean isSelected(ButtonModel paramButtonModel) { return (paramButtonModel == this.selection); }
  
  public int getButtonCount() { return (this.buttons == null) ? 0 : this.buttons.size(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ButtonGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */