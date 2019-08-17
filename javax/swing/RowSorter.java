package javax.swing;

import java.util.List;
import javax.swing.event.EventListenerList;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;

public abstract class RowSorter<M> extends Object {
  private EventListenerList listenerList = new EventListenerList();
  
  public abstract M getModel();
  
  public abstract void toggleSortOrder(int paramInt);
  
  public abstract int convertRowIndexToModel(int paramInt);
  
  public abstract int convertRowIndexToView(int paramInt);
  
  public abstract void setSortKeys(List<? extends SortKey> paramList);
  
  public abstract List<? extends SortKey> getSortKeys();
  
  public abstract int getViewRowCount();
  
  public abstract int getModelRowCount();
  
  public abstract void modelStructureChanged();
  
  public abstract void allRowsChanged();
  
  public abstract void rowsInserted(int paramInt1, int paramInt2);
  
  public abstract void rowsDeleted(int paramInt1, int paramInt2);
  
  public abstract void rowsUpdated(int paramInt1, int paramInt2);
  
  public abstract void rowsUpdated(int paramInt1, int paramInt2, int paramInt3);
  
  public void addRowSorterListener(RowSorterListener paramRowSorterListener) { this.listenerList.add(RowSorterListener.class, paramRowSorterListener); }
  
  public void removeRowSorterListener(RowSorterListener paramRowSorterListener) { this.listenerList.remove(RowSorterListener.class, paramRowSorterListener); }
  
  protected void fireSortOrderChanged() { fireRowSorterChanged(new RowSorterEvent(this)); }
  
  protected void fireRowSorterChanged(int[] paramArrayOfInt) { fireRowSorterChanged(new RowSorterEvent(this, RowSorterEvent.Type.SORTED, paramArrayOfInt)); }
  
  void fireRowSorterChanged(RowSorterEvent paramRowSorterEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == RowSorterListener.class)
        ((RowSorterListener)arrayOfObject[i + 1]).sorterChanged(paramRowSorterEvent); 
    } 
  }
  
  public static class SortKey {
    private int column;
    
    private SortOrder sortOrder;
    
    public SortKey(int param1Int, SortOrder param1SortOrder) {
      if (param1SortOrder == null)
        throw new IllegalArgumentException("sort order must be non-null"); 
      this.column = param1Int;
      this.sortOrder = param1SortOrder;
    }
    
    public final int getColumn() { return this.column; }
    
    public final SortOrder getSortOrder() { return this.sortOrder; }
    
    public int hashCode() {
      null = 17;
      null = 37 * null + this.column;
      return 37 * null + this.sortOrder.hashCode();
    }
    
    public boolean equals(Object param1Object) { return (param1Object == this) ? true : ((param1Object instanceof SortKey) ? ((((SortKey)param1Object).column == this.column && ((SortKey)param1Object).sortOrder == this.sortOrder)) : false); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\RowSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */