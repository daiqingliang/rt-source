package javax.swing.tree;

import java.beans.PropertyChangeListener;
import javax.swing.event.TreeSelectionListener;

public interface TreeSelectionModel {
  public static final int SINGLE_TREE_SELECTION = 1;
  
  public static final int CONTIGUOUS_TREE_SELECTION = 2;
  
  public static final int DISCONTIGUOUS_TREE_SELECTION = 4;
  
  void setSelectionMode(int paramInt);
  
  int getSelectionMode();
  
  void setSelectionPath(TreePath paramTreePath);
  
  void setSelectionPaths(TreePath[] paramArrayOfTreePath);
  
  void addSelectionPath(TreePath paramTreePath);
  
  void addSelectionPaths(TreePath[] paramArrayOfTreePath);
  
  void removeSelectionPath(TreePath paramTreePath);
  
  void removeSelectionPaths(TreePath[] paramArrayOfTreePath);
  
  TreePath getSelectionPath();
  
  TreePath[] getSelectionPaths();
  
  int getSelectionCount();
  
  boolean isPathSelected(TreePath paramTreePath);
  
  boolean isSelectionEmpty();
  
  void clearSelection();
  
  void setRowMapper(RowMapper paramRowMapper);
  
  RowMapper getRowMapper();
  
  int[] getSelectionRows();
  
  int getMinSelectionRow();
  
  int getMaxSelectionRow();
  
  boolean isRowSelected(int paramInt);
  
  void resetRowSelection();
  
  int getLeadSelectionRow();
  
  TreePath getLeadSelectionPath();
  
  void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  void addTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener);
  
  void removeTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\TreeSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */