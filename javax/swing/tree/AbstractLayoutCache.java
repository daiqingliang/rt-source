package javax.swing.tree;

import java.awt.Rectangle;
import java.util.Enumeration;
import javax.swing.event.TreeModelEvent;

public abstract class AbstractLayoutCache implements RowMapper {
  protected NodeDimensions nodeDimensions;
  
  protected TreeModel treeModel;
  
  protected TreeSelectionModel treeSelectionModel;
  
  protected boolean rootVisible;
  
  protected int rowHeight;
  
  public void setNodeDimensions(NodeDimensions paramNodeDimensions) { this.nodeDimensions = paramNodeDimensions; }
  
  public NodeDimensions getNodeDimensions() { return this.nodeDimensions; }
  
  public void setModel(TreeModel paramTreeModel) { this.treeModel = paramTreeModel; }
  
  public TreeModel getModel() { return this.treeModel; }
  
  public void setRootVisible(boolean paramBoolean) { this.rootVisible = paramBoolean; }
  
  public boolean isRootVisible() { return this.rootVisible; }
  
  public void setRowHeight(int paramInt) { this.rowHeight = paramInt; }
  
  public int getRowHeight() { return this.rowHeight; }
  
  public void setSelectionModel(TreeSelectionModel paramTreeSelectionModel) {
    if (this.treeSelectionModel != null)
      this.treeSelectionModel.setRowMapper(null); 
    this.treeSelectionModel = paramTreeSelectionModel;
    if (this.treeSelectionModel != null)
      this.treeSelectionModel.setRowMapper(this); 
  }
  
  public TreeSelectionModel getSelectionModel() { return this.treeSelectionModel; }
  
  public int getPreferredHeight() {
    int i = getRowCount();
    if (i > 0) {
      Rectangle rectangle = getBounds(getPathForRow(i - 1), null);
      if (rectangle != null)
        return rectangle.y + rectangle.height; 
    } 
    return 0;
  }
  
  public int getPreferredWidth(Rectangle paramRectangle) {
    int i = getRowCount();
    if (i > 0) {
      int j;
      TreePath treePath;
      if (paramRectangle == null) {
        treePath = getPathForRow(0);
        j = Integer.MAX_VALUE;
      } else {
        treePath = getPathClosestTo(paramRectangle.x, paramRectangle.y);
        j = paramRectangle.height + paramRectangle.y;
      } 
      Enumeration enumeration = getVisiblePathsFrom(treePath);
      if (enumeration != null && enumeration.hasMoreElements()) {
        int k;
        Rectangle rectangle = getBounds((TreePath)enumeration.nextElement(), null);
        if (rectangle != null) {
          k = rectangle.x + rectangle.width;
          if (rectangle.y >= j)
            return k; 
        } else {
          k = 0;
        } 
        while (rectangle != null && enumeration.hasMoreElements()) {
          rectangle = getBounds((TreePath)enumeration.nextElement(), rectangle);
          if (rectangle != null && rectangle.y < j) {
            k = Math.max(k, rectangle.x + rectangle.width);
            continue;
          } 
          rectangle = null;
        } 
        return k;
      } 
    } 
    return 0;
  }
  
  public abstract boolean isExpanded(TreePath paramTreePath);
  
  public abstract Rectangle getBounds(TreePath paramTreePath, Rectangle paramRectangle);
  
  public abstract TreePath getPathForRow(int paramInt);
  
  public abstract int getRowForPath(TreePath paramTreePath);
  
  public abstract TreePath getPathClosestTo(int paramInt1, int paramInt2);
  
  public abstract Enumeration<TreePath> getVisiblePathsFrom(TreePath paramTreePath);
  
  public abstract int getVisibleChildCount(TreePath paramTreePath);
  
  public abstract void setExpandedState(TreePath paramTreePath, boolean paramBoolean);
  
  public abstract boolean getExpandedState(TreePath paramTreePath);
  
  public abstract int getRowCount();
  
  public abstract void invalidateSizes();
  
  public abstract void invalidatePathBounds(TreePath paramTreePath);
  
  public abstract void treeNodesChanged(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeNodesInserted(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeNodesRemoved(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeStructureChanged(TreeModelEvent paramTreeModelEvent);
  
  public int[] getRowsForPaths(TreePath[] paramArrayOfTreePath) {
    if (paramArrayOfTreePath == null)
      return null; 
    int i = paramArrayOfTreePath.length;
    int[] arrayOfInt = new int[i];
    for (byte b = 0; b < i; b++)
      arrayOfInt[b] = getRowForPath(paramArrayOfTreePath[b]); 
    return arrayOfInt;
  }
  
  protected Rectangle getNodeDimensions(Object paramObject, int paramInt1, int paramInt2, boolean paramBoolean, Rectangle paramRectangle) {
    NodeDimensions nodeDimensions1 = getNodeDimensions();
    return (nodeDimensions1 != null) ? nodeDimensions1.getNodeDimensions(paramObject, paramInt1, paramInt2, paramBoolean, paramRectangle) : null;
  }
  
  protected boolean isFixedRowHeight() { return (this.rowHeight > 0); }
  
  public static abstract class NodeDimensions {
    public abstract Rectangle getNodeDimensions(Object param1Object, int param1Int1, int param1Int2, boolean param1Boolean, Rectangle param1Rectangle);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\AbstractLayoutCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */