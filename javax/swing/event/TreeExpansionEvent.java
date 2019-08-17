package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeExpansionEvent extends EventObject {
  protected TreePath path;
  
  public TreeExpansionEvent(Object paramObject, TreePath paramTreePath) {
    super(paramObject);
    this.path = paramTreePath;
  }
  
  public TreePath getPath() { return this.path; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\TreeExpansionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */