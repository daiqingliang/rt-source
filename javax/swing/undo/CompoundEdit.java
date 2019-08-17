package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;

public class CompoundEdit extends AbstractUndoableEdit {
  boolean inProgress = true;
  
  protected Vector<UndoableEdit> edits = new Vector();
  
  public void undo() {
    super.undo();
    int i = this.edits.size();
    while (i-- > 0) {
      UndoableEdit undoableEdit = (UndoableEdit)this.edits.elementAt(i);
      undoableEdit.undo();
    } 
  }
  
  public void redo() {
    super.redo();
    Enumeration enumeration = this.edits.elements();
    while (enumeration.hasMoreElements())
      ((UndoableEdit)enumeration.nextElement()).redo(); 
  }
  
  protected UndoableEdit lastEdit() {
    int i = this.edits.size();
    return (i > 0) ? (UndoableEdit)this.edits.elementAt(i - 1) : null;
  }
  
  public void die() {
    int i = this.edits.size();
    for (int j = i - 1; j >= 0; j--) {
      UndoableEdit undoableEdit = (UndoableEdit)this.edits.elementAt(j);
      undoableEdit.die();
    } 
    super.die();
  }
  
  public boolean addEdit(UndoableEdit paramUndoableEdit) {
    if (!this.inProgress)
      return false; 
    UndoableEdit undoableEdit = lastEdit();
    if (undoableEdit == null) {
      this.edits.addElement(paramUndoableEdit);
    } else if (!undoableEdit.addEdit(paramUndoableEdit)) {
      if (paramUndoableEdit.replaceEdit(undoableEdit))
        this.edits.removeElementAt(this.edits.size() - 1); 
      this.edits.addElement(paramUndoableEdit);
    } 
    return true;
  }
  
  public void end() { this.inProgress = false; }
  
  public boolean canUndo() { return (!isInProgress() && super.canUndo()); }
  
  public boolean canRedo() { return (!isInProgress() && super.canRedo()); }
  
  public boolean isInProgress() { return this.inProgress; }
  
  public boolean isSignificant() {
    Enumeration enumeration = this.edits.elements();
    while (enumeration.hasMoreElements()) {
      if (((UndoableEdit)enumeration.nextElement()).isSignificant())
        return true; 
    } 
    return false;
  }
  
  public String getPresentationName() {
    UndoableEdit undoableEdit = lastEdit();
    return (undoableEdit != null) ? undoableEdit.getPresentationName() : super.getPresentationName();
  }
  
  public String getUndoPresentationName() {
    UndoableEdit undoableEdit = lastEdit();
    return (undoableEdit != null) ? undoableEdit.getUndoPresentationName() : super.getUndoPresentationName();
  }
  
  public String getRedoPresentationName() {
    UndoableEdit undoableEdit = lastEdit();
    return (undoableEdit != null) ? undoableEdit.getRedoPresentationName() : super.getRedoPresentationName();
  }
  
  public String toString() { return super.toString() + " inProgress: " + this.inProgress + " edits: " + this.edits; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swin\\undo\CompoundEdit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */