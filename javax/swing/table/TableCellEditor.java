package javax.swing.table;

import java.awt.Component;
import javax.swing.CellEditor;
import javax.swing.JTable;

public interface TableCellEditor extends CellEditor {
  Component getTableCellEditorComponent(JTable paramJTable, Object paramObject, boolean paramBoolean, int paramInt1, int paramInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\table\TableCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */