package javax.swing.event;

import java.util.EventObject;
import javax.swing.table.TableModel;

public class TableModelEvent extends EventObject {
  public static final int INSERT = 1;
  
  public static final int UPDATE = 0;
  
  public static final int DELETE = -1;
  
  public static final int HEADER_ROW = -1;
  
  public static final int ALL_COLUMNS = -1;
  
  protected int type;
  
  protected int firstRow;
  
  protected int lastRow;
  
  protected int column;
  
  public TableModelEvent(TableModel paramTableModel) { this(paramTableModel, 0, 2147483647, -1, 0); }
  
  public TableModelEvent(TableModel paramTableModel, int paramInt) { this(paramTableModel, paramInt, paramInt, -1, 0); }
  
  public TableModelEvent(TableModel paramTableModel, int paramInt1, int paramInt2) { this(paramTableModel, paramInt1, paramInt2, -1, 0); }
  
  public TableModelEvent(TableModel paramTableModel, int paramInt1, int paramInt2, int paramInt3) { this(paramTableModel, paramInt1, paramInt2, paramInt3, 0); }
  
  public TableModelEvent(TableModel paramTableModel, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super(paramTableModel);
    this.firstRow = paramInt1;
    this.lastRow = paramInt2;
    this.column = paramInt3;
    this.type = paramInt4;
  }
  
  public int getFirstRow() { return this.firstRow; }
  
  public int getLastRow() { return this.lastRow; }
  
  public int getColumn() { return this.column; }
  
  public int getType() { return this.type; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\TableModelEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */