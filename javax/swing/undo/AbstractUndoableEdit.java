package javax.swing.undo;

import java.io.Serializable;
import javax.swing.UIManager;

public class AbstractUndoableEdit implements UndoableEdit, Serializable {
  protected static final String UndoName = "Undo";
  
  protected static final String RedoName = "Redo";
  
  boolean hasBeenDone = true;
  
  boolean alive = true;
  
  public void die() { this.alive = false; }
  
  public void undo() {
    if (!canUndo())
      throw new CannotUndoException(); 
    this.hasBeenDone = false;
  }
  
  public boolean canUndo() { return (this.alive && this.hasBeenDone); }
  
  public void redo() {
    if (!canRedo())
      throw new CannotRedoException(); 
    this.hasBeenDone = true;
  }
  
  public boolean canRedo() { return (this.alive && !this.hasBeenDone); }
  
  public boolean addEdit(UndoableEdit paramUndoableEdit) { return false; }
  
  public boolean replaceEdit(UndoableEdit paramUndoableEdit) { return false; }
  
  public boolean isSignificant() { return true; }
  
  public String getPresentationName() { return ""; }
  
  public String getUndoPresentationName() {
    String str = getPresentationName();
    if (!"".equals(str)) {
      str = UIManager.getString("AbstractUndoableEdit.undoText") + " " + str;
    } else {
      str = UIManager.getString("AbstractUndoableEdit.undoText");
    } 
    return str;
  }
  
  public String getRedoPresentationName() {
    String str = getPresentationName();
    if (!"".equals(str)) {
      str = UIManager.getString("AbstractUndoableEdit.redoText") + " " + str;
    } else {
      str = UIManager.getString("AbstractUndoableEdit.redoText");
    } 
    return str;
  }
  
  public String toString() { return super.toString() + " hasBeenDone: " + this.hasBeenDone + " alive: " + this.alive; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swin\\undo\AbstractUndoableEdit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */