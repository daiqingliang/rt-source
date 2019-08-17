package javax.swing.tree;

public interface MutableTreeNode extends TreeNode {
  void insert(MutableTreeNode paramMutableTreeNode, int paramInt);
  
  void remove(int paramInt);
  
  void remove(MutableTreeNode paramMutableTreeNode);
  
  void setUserObject(Object paramObject);
  
  void removeFromParent();
  
  void setParent(MutableTreeNode paramMutableTreeNode);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\MutableTreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */