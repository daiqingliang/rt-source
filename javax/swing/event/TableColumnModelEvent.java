package javax.swing.event;

import java.util.EventObject;
import javax.swing.table.TableColumnModel;

public class TableColumnModelEvent extends EventObject {
  protected int fromIndex;
  
  protected int toIndex;
  
  public TableColumnModelEvent(TableColumnModel paramTableColumnModel, int paramInt1, int paramInt2) {
    super(paramTableColumnModel);
    this.fromIndex = paramInt1;
    this.toIndex = paramInt2;
  }
  
  public int getFromIndex() { return this.fromIndex; }
  
  public int getToIndex() { return this.toIndex; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\TableColumnModelEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */