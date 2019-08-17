package javax.swing.table;

import java.util.Enumeration;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableColumnModelListener;

public interface TableColumnModel {
  void addColumn(TableColumn paramTableColumn);
  
  void removeColumn(TableColumn paramTableColumn);
  
  void moveColumn(int paramInt1, int paramInt2);
  
  void setColumnMargin(int paramInt);
  
  int getColumnCount();
  
  Enumeration<TableColumn> getColumns();
  
  int getColumnIndex(Object paramObject);
  
  TableColumn getColumn(int paramInt);
  
  int getColumnMargin();
  
  int getColumnIndexAtX(int paramInt);
  
  int getTotalColumnWidth();
  
  void setColumnSelectionAllowed(boolean paramBoolean);
  
  boolean getColumnSelectionAllowed();
  
  int[] getSelectedColumns();
  
  int getSelectedColumnCount();
  
  void setSelectionModel(ListSelectionModel paramListSelectionModel);
  
  ListSelectionModel getSelectionModel();
  
  void addColumnModelListener(TableColumnModelListener paramTableColumnModelListener);
  
  void removeColumnModelListener(TableColumnModelListener paramTableColumnModelListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\table\TableColumnModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */