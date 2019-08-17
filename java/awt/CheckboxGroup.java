package java.awt;

import java.io.Serializable;

public class CheckboxGroup implements Serializable {
  Checkbox selectedCheckbox = null;
  
  private static final long serialVersionUID = 3729780091441768983L;
  
  public Checkbox getSelectedCheckbox() { return getCurrent(); }
  
  @Deprecated
  public Checkbox getCurrent() { return this.selectedCheckbox; }
  
  public void setSelectedCheckbox(Checkbox paramCheckbox) { setCurrent(paramCheckbox); }
  
  @Deprecated
  public void setCurrent(Checkbox paramCheckbox) {
    if (paramCheckbox != null && paramCheckbox.group != this)
      return; 
    Checkbox checkbox = this.selectedCheckbox;
    this.selectedCheckbox = paramCheckbox;
    if (checkbox != null && checkbox != paramCheckbox && checkbox.group == this)
      checkbox.setState(false); 
    if (paramCheckbox != null && checkbox != paramCheckbox && !paramCheckbox.getState())
      paramCheckbox.setStateInternal(true); 
  }
  
  public String toString() { return getClass().getName() + "[selectedCheckbox=" + this.selectedCheckbox + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\CheckboxGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */