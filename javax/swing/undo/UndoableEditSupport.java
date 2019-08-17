package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

public class UndoableEditSupport {
  protected int updateLevel;
  
  protected CompoundEdit compoundEdit;
  
  protected Vector<UndoableEditListener> listeners;
  
  protected Object realSource;
  
  public UndoableEditSupport() { this(null); }
  
  public UndoableEditSupport(Object paramObject) {
    this.realSource = (paramObject == null) ? this : paramObject;
    this.updateLevel = 0;
    this.compoundEdit = null;
    this.listeners = new Vector();
  }
  
  public void addUndoableEditListener(UndoableEditListener paramUndoableEditListener) { this.listeners.addElement(paramUndoableEditListener); }
  
  public void removeUndoableEditListener(UndoableEditListener paramUndoableEditListener) { this.listeners.removeElement(paramUndoableEditListener); }
  
  public UndoableEditListener[] getUndoableEditListeners() { return (UndoableEditListener[])this.listeners.toArray(new UndoableEditListener[0]); }
  
  protected void _postEdit(UndoableEdit paramUndoableEdit) {
    UndoableEditEvent undoableEditEvent = new UndoableEditEvent(this.realSource, paramUndoableEdit);
    Enumeration enumeration = ((Vector)this.listeners.clone()).elements();
    while (enumeration.hasMoreElements())
      ((UndoableEditListener)enumeration.nextElement()).undoableEditHappened(undoableEditEvent); 
  }
  
  public void postEdit(UndoableEdit paramUndoableEdit) {
    if (this.updateLevel == 0) {
      _postEdit(paramUndoableEdit);
    } else {
      this.compoundEdit.addEdit(paramUndoableEdit);
    } 
  }
  
  public int getUpdateLevel() { return this.updateLevel; }
  
  public void beginUpdate() {
    if (this.updateLevel == 0)
      this.compoundEdit = createCompoundEdit(); 
    this.updateLevel++;
  }
  
  protected CompoundEdit createCompoundEdit() { return new CompoundEdit(); }
  
  public void endUpdate() {
    this.updateLevel--;
    if (this.updateLevel == 0) {
      this.compoundEdit.end();
      _postEdit(this.compoundEdit);
      this.compoundEdit = null;
    } 
  }
  
  public String toString() { return super.toString() + " updateLevel: " + this.updateLevel + " listeners: " + this.listeners + " compoundEdit: " + this.compoundEdit; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swin\\undo\UndoableEditSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */