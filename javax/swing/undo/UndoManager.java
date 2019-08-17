package javax.swing.undo;

import java.util.Vector;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

public class UndoManager extends CompoundEdit implements UndoableEditListener {
  int indexOfNextAdd = 0;
  
  int limit = 100;
  
  public UndoManager() { this.edits.ensureCapacity(this.limit); }
  
  public int getLimit() { return this.limit; }
  
  public void discardAllEdits() {
    for (UndoableEdit undoableEdit : this.edits)
      undoableEdit.die(); 
    this.edits = new Vector();
    this.indexOfNextAdd = 0;
  }
  
  protected void trimForLimit() {
    if (this.limit >= 0) {
      int i = this.edits.size();
      if (i > this.limit) {
        int j = this.limit / 2;
        int k = this.indexOfNextAdd - 1 - j;
        int m = this.indexOfNextAdd - 1 + j;
        if (m - k + 1 > this.limit)
          k++; 
        if (k < 0) {
          m -= k;
          k = 0;
        } 
        if (m >= i) {
          int n = i - m - 1;
          m += n;
          k += n;
        } 
        trimEdits(m + 1, i - 1);
        trimEdits(0, k - 1);
      } 
    } 
  }
  
  protected void trimEdits(int paramInt1, int paramInt2) {
    if (paramInt1 <= paramInt2) {
      for (int i = paramInt2; paramInt1 <= i; i--) {
        UndoableEdit undoableEdit = (UndoableEdit)this.edits.elementAt(i);
        undoableEdit.die();
        this.edits.removeElementAt(i);
      } 
      if (this.indexOfNextAdd > paramInt2) {
        this.indexOfNextAdd -= paramInt2 - paramInt1 + 1;
      } else if (this.indexOfNextAdd >= paramInt1) {
        this.indexOfNextAdd = paramInt1;
      } 
    } 
  }
  
  public void setLimit(int paramInt) {
    if (!this.inProgress)
      throw new RuntimeException("Attempt to call UndoManager.setLimit() after UndoManager.end() has been called"); 
    this.limit = paramInt;
    trimForLimit();
  }
  
  protected UndoableEdit editToBeUndone() {
    int i = this.indexOfNextAdd;
    while (i > 0) {
      UndoableEdit undoableEdit = (UndoableEdit)this.edits.elementAt(--i);
      if (undoableEdit.isSignificant())
        return undoableEdit; 
    } 
    return null;
  }
  
  protected UndoableEdit editToBeRedone() {
    int i = this.edits.size();
    int j = this.indexOfNextAdd;
    while (j < i) {
      UndoableEdit undoableEdit = (UndoableEdit)this.edits.elementAt(j++);
      if (undoableEdit.isSignificant())
        return undoableEdit; 
    } 
    return null;
  }
  
  protected void undoTo(UndoableEdit paramUndoableEdit) throws CannotUndoException {
    for (boolean bool = false; !bool; bool = (undoableEdit == paramUndoableEdit) ? 1 : 0) {
      UndoableEdit undoableEdit = (UndoableEdit)this.edits.elementAt(--this.indexOfNextAdd);
      undoableEdit.undo();
    } 
  }
  
  protected void redoTo(UndoableEdit paramUndoableEdit) throws CannotUndoException {
    for (boolean bool = false; !bool; bool = (undoableEdit == paramUndoableEdit) ? 1 : 0) {
      UndoableEdit undoableEdit = (UndoableEdit)this.edits.elementAt(this.indexOfNextAdd++);
      undoableEdit.redo();
    } 
  }
  
  public void undoOrRedo() {
    if (this.indexOfNextAdd == this.edits.size()) {
      undo();
    } else {
      redo();
    } 
  }
  
  public boolean canUndoOrRedo() { return (this.indexOfNextAdd == this.edits.size()) ? canUndo() : canRedo(); }
  
  public void undo() {
    if (this.inProgress) {
      UndoableEdit undoableEdit = editToBeUndone();
      if (undoableEdit == null)
        throw new CannotUndoException(); 
      undoTo(undoableEdit);
    } else {
      super.undo();
    } 
  }
  
  public boolean canUndo() {
    if (this.inProgress) {
      UndoableEdit undoableEdit = editToBeUndone();
      return (undoableEdit != null && undoableEdit.canUndo());
    } 
    return super.canUndo();
  }
  
  public void redo() {
    if (this.inProgress) {
      UndoableEdit undoableEdit = editToBeRedone();
      if (undoableEdit == null)
        throw new CannotRedoException(); 
      redoTo(undoableEdit);
    } else {
      super.redo();
    } 
  }
  
  public boolean canRedo() {
    if (this.inProgress) {
      UndoableEdit undoableEdit = editToBeRedone();
      return (undoableEdit != null && undoableEdit.canRedo());
    } 
    return super.canRedo();
  }
  
  public boolean addEdit(UndoableEdit paramUndoableEdit) {
    trimEdits(this.indexOfNextAdd, this.edits.size() - 1);
    boolean bool = super.addEdit(paramUndoableEdit);
    if (this.inProgress)
      bool = true; 
    this.indexOfNextAdd = this.edits.size();
    trimForLimit();
    return bool;
  }
  
  public void end() {
    super.end();
    trimEdits(this.indexOfNextAdd, this.edits.size() - 1);
  }
  
  public String getUndoOrRedoPresentationName() { return (this.indexOfNextAdd == this.edits.size()) ? getUndoPresentationName() : getRedoPresentationName(); }
  
  public String getUndoPresentationName() { return this.inProgress ? (canUndo() ? editToBeUndone().getUndoPresentationName() : UIManager.getString("AbstractUndoableEdit.undoText")) : super.getUndoPresentationName(); }
  
  public String getRedoPresentationName() { return this.inProgress ? (canRedo() ? editToBeRedone().getRedoPresentationName() : UIManager.getString("AbstractUndoableEdit.redoText")) : super.getRedoPresentationName(); }
  
  public void undoableEditHappened(UndoableEditEvent paramUndoableEditEvent) { addEdit(paramUndoableEditEvent.getEdit()); }
  
  public String toString() { return super.toString() + " limit: " + this.limit + " indexOfNextAdd: " + this.indexOfNextAdd; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swin\\undo\UndoManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */