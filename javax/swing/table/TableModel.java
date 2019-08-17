package javax.swing.table;

import javax.swing.event.TableModelListener;

public interface TableModel {
  int getRowCount();
  
  int getColumnCount();
  
  String getColumnName(int paramInt);
  
  Class<?> getColumnClass(int paramInt);
  
  boolean isCellEditable(int paramInt1, int paramInt2);
  
  Object getValueAt(int paramInt1, int paramInt2);
  
  void setValueAt(Object paramObject, int paramInt1, int paramInt2);
  
  void addTableModelListener(TableModelListener paramTableModelListener);
  
  void removeTableModelListener(TableModelListener paramTableModelListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\table\TableModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */