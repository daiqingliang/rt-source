package javax.swing.event;

import java.util.EventListener;

public interface TreeExpansionListener extends EventListener {
  void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent);
  
  void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\TreeExpansionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */