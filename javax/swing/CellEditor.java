package javax.swing;

import java.util.EventObject;
import javax.swing.event.CellEditorListener;

public interface CellEditor {
  Object getCellEditorValue();
  
  boolean isCellEditable(EventObject paramEventObject);
  
  boolean shouldSelectCell(EventObject paramEventObject);
  
  boolean stopCellEditing();
  
  void cancelCellEditing();
  
  void addCellEditorListener(CellEditorListener paramCellEditorListener);
  
  void removeCellEditorListener(CellEditorListener paramCellEditorListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\CellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */