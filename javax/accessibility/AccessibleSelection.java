package javax.accessibility;

public interface AccessibleSelection {
  int getAccessibleSelectionCount();
  
  Accessible getAccessibleSelection(int paramInt);
  
  boolean isAccessibleChildSelected(int paramInt);
  
  void addAccessibleSelection(int paramInt);
  
  void removeAccessibleSelection(int paramInt);
  
  void clearAccessibleSelection();
  
  void selectAllAccessibleSelection();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */