package javax.swing;

import javax.swing.event.ChangeListener;

public interface SingleSelectionModel {
  int getSelectedIndex();
  
  void setSelectedIndex(int paramInt);
  
  void clearSelection();
  
  boolean isSelected();
  
  void addChangeListener(ChangeListener paramChangeListener);
  
  void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SingleSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */