package javax.swing.event;

import java.util.EventObject;
import javax.swing.undo.UndoableEdit;

public class UndoableEditEvent extends EventObject {
  private UndoableEdit myEdit;
  
  public UndoableEditEvent(Object paramObject, UndoableEdit paramUndoableEdit) {
    super(paramObject);
    this.myEdit = paramUndoableEdit;
  }
  
  public UndoableEdit getEdit() { return this.myEdit; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\UndoableEditEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */