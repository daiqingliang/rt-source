package javax.swing.event;

import java.util.EventListener;

public interface TreeModelListener extends EventListener {
  void treeNodesChanged(TreeModelEvent paramTreeModelEvent);
  
  void treeNodesInserted(TreeModelEvent paramTreeModelEvent);
  
  void treeNodesRemoved(TreeModelEvent paramTreeModelEvent);
  
  void treeStructureChanged(TreeModelEvent paramTreeModelEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\TreeModelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */