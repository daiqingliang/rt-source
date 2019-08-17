package javax.swing.undo;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class StateEdit extends AbstractUndoableEdit {
  protected static final String RCSID = "$Id: StateEdit.java,v 1.6 1997/10/01 20:05:51 sandipc Exp $";
  
  protected StateEditable object;
  
  protected Hashtable<Object, Object> preState;
  
  protected Hashtable<Object, Object> postState;
  
  protected String undoRedoName;
  
  public StateEdit(StateEditable paramStateEditable) { init(paramStateEditable, null); }
  
  public StateEdit(StateEditable paramStateEditable, String paramString) { init(paramStateEditable, paramString); }
  
  protected void init(StateEditable paramStateEditable, String paramString) {
    this.object = paramStateEditable;
    this.preState = new Hashtable(11);
    this.object.storeState(this.preState);
    this.postState = null;
    this.undoRedoName = paramString;
  }
  
  public void end() {
    this.postState = new Hashtable(11);
    this.object.storeState(this.postState);
    removeRedundantState();
  }
  
  public void undo() {
    super.undo();
    this.object.restoreState(this.preState);
  }
  
  public void redo() {
    super.redo();
    this.object.restoreState(this.postState);
  }
  
  public String getPresentationName() { return this.undoRedoName; }
  
  protected void removeRedundantState() {
    Vector vector = new Vector();
    Enumeration enumeration = this.preState.keys();
    while (enumeration.hasMoreElements()) {
      Object object1 = enumeration.nextElement();
      if (this.postState.containsKey(object1) && this.postState.get(object1).equals(this.preState.get(object1)))
        vector.addElement(object1); 
    } 
    for (int i = vector.size() - 1; i >= 0; i--) {
      Object object1 = vector.elementAt(i);
      this.preState.remove(object1);
      this.postState.remove(object1);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swin\\undo\StateEdit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */