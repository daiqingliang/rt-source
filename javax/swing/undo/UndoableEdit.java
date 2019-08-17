package javax.swing.undo;

public interface UndoableEdit {
  void undo() throws CannotUndoException;
  
  boolean canUndo();
  
  void redo() throws CannotUndoException;
  
  boolean canRedo();
  
  void die() throws CannotUndoException;
  
  boolean addEdit(UndoableEdit paramUndoableEdit);
  
  boolean replaceEdit(UndoableEdit paramUndoableEdit);
  
  boolean isSignificant();
  
  String getPresentationName();
  
  String getUndoPresentationName();
  
  String getRedoPresentationName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swin\\undo\UndoableEdit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */