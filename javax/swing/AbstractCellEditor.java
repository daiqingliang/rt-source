package javax.swing;

import java.io.Serializable;
import java.util.EventObject;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

public abstract class AbstractCellEditor implements CellEditor, Serializable {
  protected EventListenerList listenerList = new EventListenerList();
  
  protected ChangeEvent changeEvent = null;
  
  public boolean isCellEditable(EventObject paramEventObject) { return true; }
  
  public boolean shouldSelectCell(EventObject paramEventObject) { return true; }
  
  public boolean stopCellEditing() {
    fireEditingStopped();
    return true;
  }
  
  public void cancelCellEditing() { fireEditingCanceled(); }
  
  public void addCellEditorListener(CellEditorListener paramCellEditorListener) { this.listenerList.add(CellEditorListener.class, paramCellEditorListener); }
  
  public void removeCellEditorListener(CellEditorListener paramCellEditorListener) { this.listenerList.remove(CellEditorListener.class, paramCellEditorListener); }
  
  public CellEditorListener[] getCellEditorListeners() { return (CellEditorListener[])this.listenerList.getListeners(CellEditorListener.class); }
  
  protected void fireEditingStopped() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == CellEditorListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((CellEditorListener)arrayOfObject[i + 1]).editingStopped(this.changeEvent);
      } 
    } 
  }
  
  protected void fireEditingCanceled() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == CellEditorListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((CellEditorListener)arrayOfObject[i + 1]).editingCanceled(this.changeEvent);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\AbstractCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */