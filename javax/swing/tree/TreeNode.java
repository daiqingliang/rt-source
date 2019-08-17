package javax.swing.tree;

import java.util.Enumeration;

public interface TreeNode {
  TreeNode getChildAt(int paramInt);
  
  int getChildCount();
  
  TreeNode getParent();
  
  int getIndex(TreeNode paramTreeNode);
  
  boolean getAllowsChildren();
  
  boolean isLeaf();
  
  Enumeration children();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\TreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */