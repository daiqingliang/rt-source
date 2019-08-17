package javax.swing;

import javax.swing.event.ListSelectionListener;

public interface ListSelectionModel {
  public static final int SINGLE_SELECTION = 0;
  
  public static final int SINGLE_INTERVAL_SELECTION = 1;
  
  public static final int MULTIPLE_INTERVAL_SELECTION = 2;
  
  void setSelectionInterval(int paramInt1, int paramInt2);
  
  void addSelectionInterval(int paramInt1, int paramInt2);
  
  void removeSelectionInterval(int paramInt1, int paramInt2);
  
  int getMinSelectionIndex();
  
  int getMaxSelectionIndex();
  
  boolean isSelectedIndex(int paramInt);
  
  int getAnchorSelectionIndex();
  
  void setAnchorSelectionIndex(int paramInt);
  
  int getLeadSelectionIndex();
  
  void setLeadSelectionIndex(int paramInt);
  
  void clearSelection();
  
  boolean isSelectionEmpty();
  
  void insertIndexInterval(int paramInt1, int paramInt2, boolean paramBoolean);
  
  void removeIndexInterval(int paramInt1, int paramInt2);
  
  void setValueIsAdjusting(boolean paramBoolean);
  
  boolean getValueIsAdjusting();
  
  void setSelectionMode(int paramInt);
  
  int getSelectionMode();
  
  void addListSelectionListener(ListSelectionListener paramListSelectionListener);
  
  void removeListSelectionListener(ListSelectionListener paramListSelectionListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ListSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */