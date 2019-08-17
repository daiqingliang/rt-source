package javax.swing.event;

import java.util.EventObject;
import javax.swing.RowSorter;

public class RowSorterEvent extends EventObject {
  private Type type;
  
  private int[] oldViewToModel;
  
  public RowSorterEvent(RowSorter paramRowSorter) { this(paramRowSorter, Type.SORT_ORDER_CHANGED, null); }
  
  public RowSorterEvent(RowSorter paramRowSorter, Type paramType, int[] paramArrayOfInt) {
    super(paramRowSorter);
    if (paramType == null)
      throw new IllegalArgumentException("type must be non-null"); 
    this.type = paramType;
    this.oldViewToModel = paramArrayOfInt;
  }
  
  public RowSorter getSource() { return (RowSorter)super.getSource(); }
  
  public Type getType() { return this.type; }
  
  public int convertPreviousRowIndexToModel(int paramInt) { return (this.oldViewToModel != null && paramInt >= 0 && paramInt < this.oldViewToModel.length) ? this.oldViewToModel[paramInt] : -1; }
  
  public int getPreviousRowCount() { return (this.oldViewToModel == null) ? 0 : this.oldViewToModel.length; }
  
  public enum Type {
    SORT_ORDER_CHANGED, SORTED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\RowSorterEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */