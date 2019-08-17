package javax.swing;

import java.awt.ItemSelectable;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.event.ChangeListener;

public interface ButtonModel extends ItemSelectable {
  boolean isArmed();
  
  boolean isSelected();
  
  boolean isEnabled();
  
  boolean isPressed();
  
  boolean isRollover();
  
  void setArmed(boolean paramBoolean);
  
  void setSelected(boolean paramBoolean);
  
  void setEnabled(boolean paramBoolean);
  
  void setPressed(boolean paramBoolean);
  
  void setRollover(boolean paramBoolean);
  
  void setMnemonic(int paramInt);
  
  int getMnemonic();
  
  void setActionCommand(String paramString);
  
  String getActionCommand();
  
  void setGroup(ButtonGroup paramButtonGroup);
  
  void addActionListener(ActionListener paramActionListener);
  
  void removeActionListener(ActionListener paramActionListener);
  
  void addItemListener(ItemListener paramItemListener);
  
  void removeItemListener(ItemListener paramItemListener);
  
  void addChangeListener(ChangeListener paramChangeListener);
  
  void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ButtonModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */