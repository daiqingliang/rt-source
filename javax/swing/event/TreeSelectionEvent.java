package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeSelectionEvent extends EventObject {
  protected TreePath[] paths;
  
  protected boolean[] areNew;
  
  protected TreePath oldLeadSelectionPath;
  
  protected TreePath newLeadSelectionPath;
  
  public TreeSelectionEvent(Object paramObject, TreePath[] paramArrayOfTreePath, boolean[] paramArrayOfBoolean, TreePath paramTreePath1, TreePath paramTreePath2) {
    super(paramObject);
    this.paths = paramArrayOfTreePath;
    this.areNew = paramArrayOfBoolean;
    this.oldLeadSelectionPath = paramTreePath1;
    this.newLeadSelectionPath = paramTreePath2;
  }
  
  public TreeSelectionEvent(Object paramObject, TreePath paramTreePath1, boolean paramBoolean, TreePath paramTreePath2, TreePath paramTreePath3) {
    super(paramObject);
    this.paths = new TreePath[1];
    this.paths[0] = paramTreePath1;
    this.areNew = new boolean[1];
    this.areNew[0] = paramBoolean;
    this.oldLeadSelectionPath = paramTreePath2;
    this.newLeadSelectionPath = paramTreePath3;
  }
  
  public TreePath[] getPaths() {
    int i = this.paths.length;
    TreePath[] arrayOfTreePath = new TreePath[i];
    System.arraycopy(this.paths, 0, arrayOfTreePath, 0, i);
    return arrayOfTreePath;
  }
  
  public TreePath getPath() { return this.paths[0]; }
  
  public boolean isAddedPath() { return this.areNew[0]; }
  
  public boolean isAddedPath(TreePath paramTreePath) {
    for (int i = this.paths.length - 1; i >= 0; i--) {
      if (this.paths[i].equals(paramTreePath))
        return this.areNew[i]; 
    } 
    throw new IllegalArgumentException("path is not a path identified by the TreeSelectionEvent");
  }
  
  public boolean isAddedPath(int paramInt) {
    if (this.paths == null || paramInt < 0 || paramInt >= this.paths.length)
      throw new IllegalArgumentException("index is beyond range of added paths identified by TreeSelectionEvent"); 
    return this.areNew[paramInt];
  }
  
  public TreePath getOldLeadSelectionPath() { return this.oldLeadSelectionPath; }
  
  public TreePath getNewLeadSelectionPath() { return this.newLeadSelectionPath; }
  
  public Object cloneWithSource(Object paramObject) { return new TreeSelectionEvent(paramObject, this.paths, this.areNew, this.oldLeadSelectionPath, this.newLeadSelectionPath); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\TreeSelectionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */