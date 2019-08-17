package javax.swing.tree;

import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;
import javax.swing.event.TreeModelEvent;
import sun.swing.SwingUtilities2;

public class VariableHeightLayoutCache extends AbstractLayoutCache {
  private Vector<Object> visibleNodes = new Vector();
  
  private boolean updateNodeSizes;
  
  private TreeStateNode root;
  
  private Rectangle boundsBuffer = new Rectangle();
  
  private Hashtable<TreePath, TreeStateNode> treePathMapping = new Hashtable();
  
  private Stack<Stack<TreePath>> tempStacks = new Stack();
  
  public void setModel(TreeModel paramTreeModel) {
    super.setModel(paramTreeModel);
    rebuild(false);
  }
  
  public void setRootVisible(boolean paramBoolean) {
    if (isRootVisible() != paramBoolean && this.root != null) {
      if (paramBoolean) {
        this.root.updatePreferredSize(0);
        this.visibleNodes.insertElementAt(this.root, 0);
      } else if (this.visibleNodes.size() > 0) {
        this.visibleNodes.removeElementAt(0);
        if (this.treeSelectionModel != null)
          this.treeSelectionModel.removeSelectionPath(this.root.getTreePath()); 
      } 
      if (this.treeSelectionModel != null)
        this.treeSelectionModel.resetRowSelection(); 
      if (getRowCount() > 0)
        getNode(0).setYOrigin(0); 
      updateYLocationsFrom(0);
      visibleNodesChanged();
    } 
    super.setRootVisible(paramBoolean);
  }
  
  public void setRowHeight(int paramInt) {
    if (paramInt != getRowHeight()) {
      super.setRowHeight(paramInt);
      invalidateSizes();
      visibleNodesChanged();
    } 
  }
  
  public void setNodeDimensions(AbstractLayoutCache.NodeDimensions paramNodeDimensions) {
    super.setNodeDimensions(paramNodeDimensions);
    invalidateSizes();
    visibleNodesChanged();
  }
  
  public void setExpandedState(TreePath paramTreePath, boolean paramBoolean) {
    if (paramTreePath != null)
      if (paramBoolean) {
        ensurePathIsExpanded(paramTreePath, true);
      } else {
        TreeStateNode treeStateNode = getNodeForPath(paramTreePath, false, true);
        if (treeStateNode != null) {
          treeStateNode.makeVisible();
          treeStateNode.collapse();
        } 
      }  
  }
  
  public boolean getExpandedState(TreePath paramTreePath) {
    TreeStateNode treeStateNode = getNodeForPath(paramTreePath, true, false);
    return (treeStateNode != null) ? ((treeStateNode.isVisible() && treeStateNode.isExpanded())) : false;
  }
  
  public Rectangle getBounds(TreePath paramTreePath, Rectangle paramRectangle) {
    TreeStateNode treeStateNode = getNodeForPath(paramTreePath, true, false);
    if (treeStateNode != null) {
      if (this.updateNodeSizes)
        updateNodeSizes(false); 
      return treeStateNode.getNodeBounds(paramRectangle);
    } 
    return null;
  }
  
  public TreePath getPathForRow(int paramInt) { return (paramInt >= 0 && paramInt < getRowCount()) ? getNode(paramInt).getTreePath() : null; }
  
  public int getRowForPath(TreePath paramTreePath) {
    if (paramTreePath == null)
      return -1; 
    TreeStateNode treeStateNode = getNodeForPath(paramTreePath, true, false);
    return (treeStateNode != null) ? treeStateNode.getRow() : -1;
  }
  
  public int getRowCount() { return this.visibleNodes.size(); }
  
  public void invalidatePathBounds(TreePath paramTreePath) {
    TreeStateNode treeStateNode = getNodeForPath(paramTreePath, true, false);
    if (treeStateNode != null) {
      treeStateNode.markSizeInvalid();
      if (treeStateNode.isVisible())
        updateYLocationsFrom(treeStateNode.getRow()); 
    } 
  }
  
  public int getPreferredHeight() {
    int i = getRowCount();
    if (i > 0) {
      TreeStateNode treeStateNode = getNode(i - 1);
      return treeStateNode.getYOrigin() + treeStateNode.getPreferredHeight();
    } 
    return 0;
  }
  
  public int getPreferredWidth(Rectangle paramRectangle) {
    if (this.updateNodeSizes)
      updateNodeSizes(false); 
    return getMaxNodeWidth();
  }
  
  public TreePath getPathClosestTo(int paramInt1, int paramInt2) {
    if (getRowCount() == 0)
      return null; 
    if (this.updateNodeSizes)
      updateNodeSizes(false); 
    int i = getRowContainingYLocation(paramInt2);
    return getNode(i).getTreePath();
  }
  
  public Enumeration<TreePath> getVisiblePathsFrom(TreePath paramTreePath) {
    TreeStateNode treeStateNode = getNodeForPath(paramTreePath, true, false);
    return (treeStateNode != null) ? new VisibleTreeStateNodeEnumeration(treeStateNode) : null;
  }
  
  public int getVisibleChildCount(TreePath paramTreePath) {
    TreeStateNode treeStateNode = getNodeForPath(paramTreePath, true, false);
    return (treeStateNode != null) ? treeStateNode.getVisibleChildCount() : 0;
  }
  
  public void invalidateSizes() {
    if (this.root != null)
      this.root.deepMarkSizeInvalid(); 
    if (!isFixedRowHeight() && this.visibleNodes.size() > 0)
      updateNodeSizes(true); 
  }
  
  public boolean isExpanded(TreePath paramTreePath) {
    if (paramTreePath != null) {
      TreeStateNode treeStateNode = getNodeForPath(paramTreePath, true, false);
      return (treeStateNode != null && treeStateNode.isExpanded());
    } 
    return false;
  }
  
  public void treeNodesChanged(TreeModelEvent paramTreeModelEvent) {
    if (paramTreeModelEvent != null) {
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      TreeStateNode treeStateNode = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      if (treeStateNode != null) {
        Object object = treeStateNode.getValue();
        treeStateNode.updatePreferredSize();
        if (treeStateNode.hasBeenExpanded() && arrayOfInt != null) {
          for (byte b = 0; b < arrayOfInt.length; b++) {
            TreeStateNode treeStateNode1 = (TreeStateNode)treeStateNode.getChildAt(arrayOfInt[b]);
            treeStateNode1.setUserObject(this.treeModel.getChild(object, arrayOfInt[b]));
            treeStateNode1.updatePreferredSize();
          } 
        } else if (treeStateNode == this.root) {
          treeStateNode.updatePreferredSize();
        } 
        if (!isFixedRowHeight()) {
          int i = treeStateNode.getRow();
          if (i != -1)
            updateYLocationsFrom(i); 
        } 
        visibleNodesChanged();
      } 
    } 
  }
  
  public void treeNodesInserted(TreeModelEvent paramTreeModelEvent) {
    if (paramTreeModelEvent != null) {
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      TreeStateNode treeStateNode = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      if (treeStateNode != null && arrayOfInt != null && arrayOfInt.length > 0)
        if (treeStateNode.hasBeenExpanded()) {
          int i = treeStateNode.getChildCount();
          Object object = treeStateNode.getValue();
          boolean bool = ((treeStateNode == this.root && !this.rootVisible) || (treeStateNode.getRow() != -1 && treeStateNode.isExpanded())) ? 1 : 0;
          for (byte b = 0; b < arrayOfInt.length; b++)
            TreeStateNode treeStateNode1 = createNodeAt(treeStateNode, arrayOfInt[b]); 
          if (i == 0)
            treeStateNode.updatePreferredSize(); 
          if (this.treeSelectionModel != null)
            this.treeSelectionModel.resetRowSelection(); 
          if (!isFixedRowHeight() && (bool || (i == 0 && treeStateNode.isVisible()))) {
            if (treeStateNode == this.root) {
              updateYLocationsFrom(0);
            } else {
              updateYLocationsFrom(treeStateNode.getRow());
            } 
            visibleNodesChanged();
          } else if (bool) {
            visibleNodesChanged();
          } 
        } else if (this.treeModel.getChildCount(treeStateNode.getValue()) - arrayOfInt.length == 0) {
          treeStateNode.updatePreferredSize();
          if (!isFixedRowHeight() && treeStateNode.isVisible())
            updateYLocationsFrom(treeStateNode.getRow()); 
        }  
    } 
  }
  
  public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent) {
    if (paramTreeModelEvent != null) {
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      TreeStateNode treeStateNode = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      if (treeStateNode != null && arrayOfInt != null && arrayOfInt.length > 0)
        if (treeStateNode.hasBeenExpanded()) {
          boolean bool = ((treeStateNode == this.root && !this.rootVisible) || (treeStateNode.getRow() != -1 && treeStateNode.isExpanded())) ? 1 : 0;
          for (int i = arrayOfInt.length - 1; i >= 0; i--) {
            TreeStateNode treeStateNode1 = (TreeStateNode)treeStateNode.getChildAt(arrayOfInt[i]);
            if (treeStateNode1.isExpanded())
              treeStateNode1.collapse(false); 
            if (bool) {
              int j = treeStateNode1.getRow();
              if (j != -1)
                this.visibleNodes.removeElementAt(j); 
            } 
            treeStateNode.remove(arrayOfInt[i]);
          } 
          if (treeStateNode.getChildCount() == 0) {
            treeStateNode.updatePreferredSize();
            if (treeStateNode.isExpanded() && treeStateNode.isLeaf())
              treeStateNode.collapse(false); 
          } 
          if (this.treeSelectionModel != null)
            this.treeSelectionModel.resetRowSelection(); 
          if (!isFixedRowHeight() && (bool || (treeStateNode.getChildCount() == 0 && treeStateNode.isVisible()))) {
            if (treeStateNode == this.root) {
              if (getRowCount() > 0)
                getNode(0).setYOrigin(0); 
              updateYLocationsFrom(0);
            } else {
              updateYLocationsFrom(treeStateNode.getRow());
            } 
            visibleNodesChanged();
          } else if (bool) {
            visibleNodesChanged();
          } 
        } else if (this.treeModel.getChildCount(treeStateNode.getValue()) == 0) {
          treeStateNode.updatePreferredSize();
          if (!isFixedRowHeight() && treeStateNode.isVisible())
            updateYLocationsFrom(treeStateNode.getRow()); 
        }  
    } 
  }
  
  public void treeStructureChanged(TreeModelEvent paramTreeModelEvent) {
    if (paramTreeModelEvent != null) {
      TreePath treePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
      TreeStateNode treeStateNode = getNodeForPath(treePath, false, false);
      if (treeStateNode == this.root || (treeStateNode == null && ((treePath == null && this.treeModel != null && this.treeModel.getRoot() == null) || (treePath != null && treePath.getPathCount() == 1)))) {
        rebuild(true);
      } else if (treeStateNode != null) {
        boolean bool = treeStateNode.isExpanded();
        boolean bool1 = (treeStateNode.getRow() != -1) ? 1 : 0;
        TreeStateNode treeStateNode2 = (TreeStateNode)treeStateNode.getParent();
        int i = treeStateNode2.getIndex(treeStateNode);
        if (bool1 && bool)
          treeStateNode.collapse(false); 
        if (bool1)
          this.visibleNodes.removeElement(treeStateNode); 
        treeStateNode.removeFromParent();
        createNodeAt(treeStateNode2, i);
        TreeStateNode treeStateNode1 = (TreeStateNode)treeStateNode2.getChildAt(i);
        if (bool1 && bool)
          treeStateNode1.expand(false); 
        int j = treeStateNode1.getRow();
        if (!isFixedRowHeight() && bool1) {
          if (j == 0) {
            updateYLocationsFrom(j);
          } else {
            updateYLocationsFrom(j - 1);
          } 
          visibleNodesChanged();
        } else if (bool1) {
          visibleNodesChanged();
        } 
      } 
    } 
  }
  
  private void visibleNodesChanged() {}
  
  private void addMapping(TreeStateNode paramTreeStateNode) { this.treePathMapping.put(paramTreeStateNode.getTreePath(), paramTreeStateNode); }
  
  private void removeMapping(TreeStateNode paramTreeStateNode) { this.treePathMapping.remove(paramTreeStateNode.getTreePath()); }
  
  private TreeStateNode getMapping(TreePath paramTreePath) { return (TreeStateNode)this.treePathMapping.get(paramTreePath); }
  
  private Rectangle getBounds(int paramInt, Rectangle paramRectangle) {
    if (this.updateNodeSizes)
      updateNodeSizes(false); 
    return (paramInt >= 0 && paramInt < getRowCount()) ? getNode(paramInt).getNodeBounds(paramRectangle) : null;
  }
  
  private void rebuild(boolean paramBoolean) {
    this.treePathMapping.clear();
    Object object;
    if (this.treeModel != null && (object = this.treeModel.getRoot()) != null) {
      this.root = createNodeForValue(object);
      this.root.path = new TreePath(object);
      addMapping(this.root);
      this.root.updatePreferredSize(0);
      this.visibleNodes.removeAllElements();
      if (isRootVisible())
        this.visibleNodes.addElement(this.root); 
      if (!this.root.isExpanded()) {
        this.root.expand();
      } else {
        Enumeration enumeration = this.root.children();
        while (enumeration.hasMoreElements())
          this.visibleNodes.addElement(enumeration.nextElement()); 
        if (!isFixedRowHeight())
          updateYLocationsFrom(0); 
      } 
    } else {
      this.visibleNodes.removeAllElements();
      this.root = null;
    } 
    if (paramBoolean && this.treeSelectionModel != null)
      this.treeSelectionModel.clearSelection(); 
    visibleNodesChanged();
  }
  
  private TreeStateNode createNodeAt(TreeStateNode paramTreeStateNode, int paramInt) {
    Object object = this.treeModel.getChild(paramTreeStateNode.getValue(), paramInt);
    TreeStateNode treeStateNode = createNodeForValue(object);
    paramTreeStateNode.insert(treeStateNode, paramInt);
    treeStateNode.updatePreferredSize(-1);
    boolean bool = (paramTreeStateNode == this.root) ? 1 : 0;
    if (treeStateNode != null && paramTreeStateNode.isExpanded() && (paramTreeStateNode.getRow() != -1 || bool)) {
      int i;
      if (paramInt == 0) {
        if (bool && !isRootVisible()) {
          i = 0;
        } else {
          i = paramTreeStateNode.getRow() + 1;
        } 
      } else if (paramInt == paramTreeStateNode.getChildCount()) {
        i = paramTreeStateNode.getLastVisibleNode().getRow() + 1;
      } else {
        TreeStateNode treeStateNode1 = (TreeStateNode)paramTreeStateNode.getChildAt(paramInt - 1);
        i = treeStateNode1.getLastVisibleNode().getRow() + 1;
      } 
      this.visibleNodes.insertElementAt(treeStateNode, i);
    } 
    return treeStateNode;
  }
  
  private TreeStateNode getNodeForPath(TreePath paramTreePath, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramTreePath != null) {
      TreeStateNode treeStateNode = getMapping(paramTreePath);
      if (treeStateNode != null)
        return (paramBoolean1 && !treeStateNode.isVisible()) ? null : treeStateNode; 
      if (this.tempStacks.size() == 0) {
        stack = new Stack();
      } else {
        stack = (Stack)this.tempStacks.pop();
      } 
      try {
        stack.push(paramTreePath);
        paramTreePath = paramTreePath.getParentPath();
        treeStateNode = null;
        while (paramTreePath != null) {
          treeStateNode = getMapping(paramTreePath);
          if (treeStateNode != null) {
            while (treeStateNode != null && stack.size() > 0) {
              paramTreePath = (TreePath)stack.pop();
              treeStateNode.getLoadedChildren(paramBoolean2);
              int i = this.treeModel.getIndexOfChild(treeStateNode.getUserObject(), paramTreePath.getLastPathComponent());
              if (i == -1 || i >= treeStateNode.getChildCount() || (paramBoolean1 && !treeStateNode.isVisible())) {
                treeStateNode = null;
                continue;
              } 
              treeStateNode = (TreeStateNode)treeStateNode.getChildAt(i);
            } 
            return treeStateNode;
          } 
          stack.push(paramTreePath);
          paramTreePath = paramTreePath.getParentPath();
        } 
      } finally {
        stack.removeAllElements();
        this.tempStacks.push(stack);
      } 
    } 
    return null;
  }
  
  private void updateYLocationsFrom(int paramInt) {
    if (paramInt >= 0 && paramInt < getRowCount()) {
      TreeStateNode treeStateNode = getNode(paramInt);
      int k = treeStateNode.getYOrigin() + treeStateNode.getPreferredHeight();
      int i = paramInt + 1;
      int j = this.visibleNodes.size();
      while (i < j) {
        treeStateNode = (TreeStateNode)this.visibleNodes.elementAt(i);
        treeStateNode.setYOrigin(k);
        k += treeStateNode.getPreferredHeight();
        i++;
      } 
    } 
  }
  
  private void updateNodeSizes(boolean paramBoolean) {
    this.updateNodeSizes = false;
    byte b = 0;
    int i = b;
    int j = this.visibleNodes.size();
    while (b < j) {
      TreeStateNode treeStateNode = (TreeStateNode)this.visibleNodes.elementAt(b);
      treeStateNode.setYOrigin(i);
      if (paramBoolean || !treeStateNode.hasValidSize())
        treeStateNode.updatePreferredSize(b); 
      i += treeStateNode.getPreferredHeight();
      b++;
    } 
  }
  
  private int getRowContainingYLocation(int paramInt) {
    if (isFixedRowHeight())
      return (getRowCount() == 0) ? -1 : Math.max(0, Math.min(getRowCount() - 1, paramInt / getRowHeight())); 
    int i;
    if ((i = getRowCount()) <= 0)
      return -1; 
    int k = 0;
    int j = k;
    while (k < i) {
      j = (i - k) / 2 + k;
      TreeStateNode treeStateNode = (TreeStateNode)this.visibleNodes.elementAt(j);
      int n = treeStateNode.getYOrigin();
      int m = n + treeStateNode.getPreferredHeight();
      if (paramInt < n) {
        i = j - 1;
        continue;
      } 
      if (paramInt >= m)
        k = j + 1; 
    } 
    if (k == i) {
      j = k;
      if (j >= getRowCount())
        j = getRowCount() - 1; 
    } 
    return j;
  }
  
  private void ensurePathIsExpanded(TreePath paramTreePath, boolean paramBoolean) {
    if (paramTreePath != null) {
      if (this.treeModel.isLeaf(paramTreePath.getLastPathComponent())) {
        paramTreePath = paramTreePath.getParentPath();
        paramBoolean = true;
      } 
      if (paramTreePath != null) {
        TreeStateNode treeStateNode = getNodeForPath(paramTreePath, false, true);
        if (treeStateNode != null) {
          treeStateNode.makeVisible();
          if (paramBoolean)
            treeStateNode.expand(); 
        } 
      } 
    } 
  }
  
  private TreeStateNode getNode(int paramInt) { return (TreeStateNode)this.visibleNodes.elementAt(paramInt); }
  
  private int getMaxNodeWidth() {
    int i = 0;
    for (int j = getRowCount() - 1; j >= 0; j--) {
      TreeStateNode treeStateNode = getNode(j);
      int k = treeStateNode.getPreferredWidth() + treeStateNode.getXOrigin();
      if (k > i)
        i = k; 
    } 
    return i;
  }
  
  private TreeStateNode createNodeForValue(Object paramObject) { return new TreeStateNode(paramObject); }
  
  private class TreeStateNode extends DefaultMutableTreeNode {
    protected int preferredWidth;
    
    protected int preferredHeight;
    
    protected int xOrigin;
    
    protected int yOrigin;
    
    protected boolean expanded;
    
    protected boolean hasBeenExpanded;
    
    protected TreePath path;
    
    public TreeStateNode(Object param1Object) { super(param1Object); }
    
    public void setParent(MutableTreeNode param1MutableTreeNode) {
      super.setParent(param1MutableTreeNode);
      if (param1MutableTreeNode != null) {
        this.path = ((TreeStateNode)param1MutableTreeNode).getTreePath().pathByAddingChild(getUserObject());
        VariableHeightLayoutCache.this.addMapping(this);
      } 
    }
    
    public void remove(int param1Int) {
      TreeStateNode treeStateNode = (TreeStateNode)getChildAt(param1Int);
      treeStateNode.removeFromMapping();
      super.remove(param1Int);
    }
    
    public void setUserObject(Object param1Object) {
      super.setUserObject(param1Object);
      if (this.path != null) {
        TreeStateNode treeStateNode = (TreeStateNode)getParent();
        if (treeStateNode != null) {
          resetChildrenPaths(treeStateNode.getTreePath());
        } else {
          resetChildrenPaths(null);
        } 
      } 
    }
    
    public Enumeration children() { return !isExpanded() ? DefaultMutableTreeNode.EMPTY_ENUMERATION : super.children(); }
    
    public boolean isLeaf() { return VariableHeightLayoutCache.this.getModel().isLeaf(getValue()); }
    
    public Rectangle getNodeBounds(Rectangle param1Rectangle) {
      if (param1Rectangle == null) {
        param1Rectangle = new Rectangle(getXOrigin(), getYOrigin(), getPreferredWidth(), getPreferredHeight());
      } else {
        param1Rectangle.x = getXOrigin();
        param1Rectangle.y = getYOrigin();
        param1Rectangle.width = getPreferredWidth();
        param1Rectangle.height = getPreferredHeight();
      } 
      return param1Rectangle;
    }
    
    public int getXOrigin() {
      if (!hasValidSize())
        updatePreferredSize(getRow()); 
      return this.xOrigin;
    }
    
    public int getYOrigin() {
      if (VariableHeightLayoutCache.this.isFixedRowHeight()) {
        int i = getRow();
        return (i == -1) ? -1 : (VariableHeightLayoutCache.this.getRowHeight() * i);
      } 
      return this.yOrigin;
    }
    
    public int getPreferredHeight() {
      if (VariableHeightLayoutCache.this.isFixedRowHeight())
        return VariableHeightLayoutCache.this.getRowHeight(); 
      if (!hasValidSize())
        updatePreferredSize(getRow()); 
      return this.preferredHeight;
    }
    
    public int getPreferredWidth() {
      if (!hasValidSize())
        updatePreferredSize(getRow()); 
      return this.preferredWidth;
    }
    
    public boolean hasValidSize() { return (this.preferredHeight != 0); }
    
    public int getRow() { return VariableHeightLayoutCache.this.visibleNodes.indexOf(this); }
    
    public boolean hasBeenExpanded() { return this.hasBeenExpanded; }
    
    public boolean isExpanded() { return this.expanded; }
    
    public TreeStateNode getLastVisibleNode() {
      TreeStateNode treeStateNode;
      for (treeStateNode = this; treeStateNode.isExpanded() && treeStateNode.getChildCount() > 0; treeStateNode = (TreeStateNode)treeStateNode.getLastChild());
      return treeStateNode;
    }
    
    public boolean isVisible() {
      if (this == VariableHeightLayoutCache.this.root)
        return true; 
      TreeStateNode treeStateNode = (TreeStateNode)getParent();
      return (treeStateNode != null && treeStateNode.isExpanded() && treeStateNode.isVisible());
    }
    
    public int getModelChildCount() { return this.hasBeenExpanded ? getChildCount() : VariableHeightLayoutCache.this.getModel().getChildCount(getValue()); }
    
    public int getVisibleChildCount() {
      int i = 0;
      if (isExpanded()) {
        int j = getChildCount();
        i += j;
        for (byte b = 0; b < j; b++)
          i += ((TreeStateNode)getChildAt(b)).getVisibleChildCount(); 
      } 
      return i;
    }
    
    public void toggleExpanded() {
      if (isExpanded()) {
        collapse();
      } else {
        expand();
      } 
    }
    
    public void makeVisible() {
      TreeStateNode treeStateNode = (TreeStateNode)getParent();
      if (treeStateNode != null)
        treeStateNode.expandParentAndReceiver(); 
    }
    
    public void expand() { expand(true); }
    
    public void collapse() { collapse(true); }
    
    public Object getValue() { return getUserObject(); }
    
    public TreePath getTreePath() { return this.path; }
    
    protected void resetChildrenPaths(TreePath param1TreePath) {
      VariableHeightLayoutCache.this.removeMapping(this);
      if (param1TreePath == null) {
        this.path = new TreePath(getUserObject());
      } else {
        this.path = param1TreePath.pathByAddingChild(getUserObject());
      } 
      VariableHeightLayoutCache.this.addMapping(this);
      for (int i = getChildCount() - 1; i >= 0; i--)
        ((TreeStateNode)getChildAt(i)).resetChildrenPaths(this.path); 
    }
    
    protected void setYOrigin(int param1Int) { this.yOrigin = param1Int; }
    
    protected void shiftYOriginBy(int param1Int) { this.yOrigin += param1Int; }
    
    protected void updatePreferredSize() { updatePreferredSize(getRow()); }
    
    protected void updatePreferredSize(int param1Int) {
      Rectangle rectangle = VariableHeightLayoutCache.this.getNodeDimensions(getUserObject(), param1Int, getLevel(), isExpanded(), VariableHeightLayoutCache.this.boundsBuffer);
      if (rectangle == null) {
        this.xOrigin = 0;
        this.preferredWidth = this.preferredHeight = 0;
        VariableHeightLayoutCache.this.updateNodeSizes = true;
      } else if (rectangle.height == 0) {
        this.xOrigin = 0;
        this.preferredWidth = this.preferredHeight = 0;
        VariableHeightLayoutCache.this.updateNodeSizes = true;
      } else {
        this.xOrigin = rectangle.x;
        this.preferredWidth = rectangle.width;
        if (VariableHeightLayoutCache.this.isFixedRowHeight()) {
          this.preferredHeight = VariableHeightLayoutCache.this.getRowHeight();
        } else {
          this.preferredHeight = rectangle.height;
        } 
      } 
    }
    
    protected void markSizeInvalid() { this.preferredHeight = 0; }
    
    protected void deepMarkSizeInvalid() {
      markSizeInvalid();
      for (int i = getChildCount() - 1; i >= 0; i--)
        ((TreeStateNode)getChildAt(i)).deepMarkSizeInvalid(); 
    }
    
    protected Enumeration getLoadedChildren(boolean param1Boolean) {
      if (!param1Boolean || this.hasBeenExpanded)
        return super.children(); 
      Object object = getValue();
      TreeModel treeModel = VariableHeightLayoutCache.this.getModel();
      int i = treeModel.getChildCount(object);
      this.hasBeenExpanded = true;
      int j = getRow();
      if (j == -1) {
        for (byte b = 0; b < i; b++) {
          TreeStateNode treeStateNode = VariableHeightLayoutCache.this.createNodeForValue(treeModel.getChild(object, b));
          add(treeStateNode);
          treeStateNode.updatePreferredSize(-1);
        } 
      } else {
        j++;
        for (byte b = 0; b < i; b++) {
          TreeStateNode treeStateNode = VariableHeightLayoutCache.this.createNodeForValue(treeModel.getChild(object, b));
          add(treeStateNode);
          treeStateNode.updatePreferredSize(j++);
        } 
      } 
      return super.children();
    }
    
    protected void didAdjustTree() {}
    
    protected void expandParentAndReceiver() {
      TreeStateNode treeStateNode = (TreeStateNode)getParent();
      if (treeStateNode != null)
        treeStateNode.expandParentAndReceiver(); 
      expand();
    }
    
    protected void expand(boolean param1Boolean) {
      if (!isExpanded() && !isLeaf()) {
        int m;
        boolean bool = VariableHeightLayoutCache.this.isFixedRowHeight();
        int i = getPreferredHeight();
        int j = getRow();
        this.expanded = true;
        updatePreferredSize(j);
        if (!this.hasBeenExpanded) {
          Object object = getValue();
          TreeModel treeModel = VariableHeightLayoutCache.this.getModel();
          int n = treeModel.getChildCount(object);
          this.hasBeenExpanded = true;
          if (j == -1) {
            for (byte b = 0; b < n; b++) {
              TreeStateNode treeStateNode = VariableHeightLayoutCache.this.createNodeForValue(treeModel.getChild(object, b));
              add(treeStateNode);
              treeStateNode.updatePreferredSize(-1);
            } 
          } else {
            int i1 = j + 1;
            for (byte b = 0; b < n; b++) {
              TreeStateNode treeStateNode = VariableHeightLayoutCache.this.createNodeForValue(treeModel.getChild(object, b));
              add(treeStateNode);
              treeStateNode.updatePreferredSize(i1);
            } 
          } 
        } 
        int k = j;
        Enumeration enumeration = preorderEnumeration();
        enumeration.nextElement();
        if (bool) {
          m = 0;
        } else if (this == VariableHeightLayoutCache.this.root && !VariableHeightLayoutCache.this.isRootVisible()) {
          m = 0;
        } else {
          m = getYOrigin() + getPreferredHeight();
        } 
        if (!bool) {
          while (enumeration.hasMoreElements()) {
            TreeStateNode treeStateNode = (TreeStateNode)enumeration.nextElement();
            if (!VariableHeightLayoutCache.this.updateNodeSizes && !treeStateNode.hasValidSize())
              treeStateNode.updatePreferredSize(k + 1); 
            treeStateNode.setYOrigin(m);
            m += treeStateNode.getPreferredHeight();
            VariableHeightLayoutCache.this.visibleNodes.insertElementAt(treeStateNode, ++k);
          } 
        } else {
          while (enumeration.hasMoreElements()) {
            TreeStateNode treeStateNode = (TreeStateNode)enumeration.nextElement();
            VariableHeightLayoutCache.this.visibleNodes.insertElementAt(treeStateNode, ++k);
          } 
        } 
        if (param1Boolean && (j != k || getPreferredHeight() != i)) {
          if (!bool && ++k < VariableHeightLayoutCache.this.getRowCount()) {
            int i1 = m - getYOrigin() + getPreferredHeight() + getPreferredHeight() - i;
            for (int n = VariableHeightLayoutCache.this.visibleNodes.size() - 1; n >= k; n--)
              ((TreeStateNode)VariableHeightLayoutCache.this.visibleNodes.elementAt(n)).shiftYOriginBy(i1); 
          } 
          didAdjustTree();
          VariableHeightLayoutCache.this.visibleNodesChanged();
        } 
        if (VariableHeightLayoutCache.this.treeSelectionModel != null)
          VariableHeightLayoutCache.this.treeSelectionModel.resetRowSelection(); 
      } 
    }
    
    protected void collapse(boolean param1Boolean) {
      if (isExpanded()) {
        int j;
        Enumeration enumeration = preorderEnumeration();
        enumeration.nextElement();
        int i = 0;
        boolean bool = VariableHeightLayoutCache.this.isFixedRowHeight();
        if (bool) {
          j = 0;
        } else {
          j = getPreferredHeight() + getYOrigin();
        } 
        int k = getPreferredHeight();
        int m = j;
        int n = getRow();
        if (!bool) {
          while (enumeration.hasMoreElements()) {
            TreeStateNode treeStateNode = (TreeStateNode)enumeration.nextElement();
            if (treeStateNode.isVisible()) {
              i++;
              j = treeStateNode.getYOrigin() + treeStateNode.getPreferredHeight();
            } 
          } 
        } else {
          while (enumeration.hasMoreElements()) {
            TreeStateNode treeStateNode = (TreeStateNode)enumeration.nextElement();
            if (treeStateNode.isVisible())
              i++; 
          } 
        } 
        int i1;
        for (i1 = i + n; i1 > n; i1--)
          VariableHeightLayoutCache.this.visibleNodes.removeElementAt(i1); 
        this.expanded = false;
        if (n == -1) {
          markSizeInvalid();
        } else if (param1Boolean) {
          updatePreferredSize(n);
        } 
        if (n != -1 && param1Boolean && (i > 0 || k != getPreferredHeight())) {
          m += getPreferredHeight() - k;
          if (!bool && n + 1 < VariableHeightLayoutCache.this.getRowCount() && m != j) {
            int i3 = m - j;
            i1 = n + 1;
            int i2 = VariableHeightLayoutCache.this.visibleNodes.size();
            while (i1 < i2) {
              ((TreeStateNode)VariableHeightLayoutCache.this.visibleNodes.elementAt(i1)).shiftYOriginBy(i3);
              i1++;
            } 
          } 
          didAdjustTree();
          VariableHeightLayoutCache.this.visibleNodesChanged();
        } 
        if (VariableHeightLayoutCache.this.treeSelectionModel != null && i > 0 && n != -1)
          VariableHeightLayoutCache.this.treeSelectionModel.resetRowSelection(); 
      } 
    }
    
    protected void removeFromMapping() {
      if (this.path != null) {
        VariableHeightLayoutCache.this.removeMapping(this);
        for (int i = getChildCount() - 1; i >= 0; i--)
          ((TreeStateNode)getChildAt(i)).removeFromMapping(); 
      } 
    }
  }
  
  private class VisibleTreeStateNodeEnumeration extends Object implements Enumeration<TreePath> {
    protected VariableHeightLayoutCache.TreeStateNode parent;
    
    protected int nextIndex;
    
    protected int childCount;
    
    protected VisibleTreeStateNodeEnumeration(VariableHeightLayoutCache this$0, VariableHeightLayoutCache.TreeStateNode param1TreeStateNode) { this(param1TreeStateNode, -1); }
    
    protected VisibleTreeStateNodeEnumeration(VariableHeightLayoutCache.TreeStateNode param1TreeStateNode, int param1Int) {
      this.parent = param1TreeStateNode;
      this.nextIndex = param1Int;
      this.childCount = this.parent.getChildCount();
    }
    
    public boolean hasMoreElements() { return (this.parent != null); }
    
    public TreePath nextElement() {
      TreePath treePath;
      if (!hasMoreElements())
        throw new NoSuchElementException("No more visible paths"); 
      if (this.nextIndex == -1) {
        treePath = this.parent.getTreePath();
      } else {
        VariableHeightLayoutCache.TreeStateNode treeStateNode = (VariableHeightLayoutCache.TreeStateNode)this.parent.getChildAt(this.nextIndex);
        treePath = treeStateNode.getTreePath();
      } 
      updateNextObject();
      return treePath;
    }
    
    protected void updateNextObject() {
      if (!updateNextIndex())
        findNextValidParent(); 
    }
    
    protected boolean findNextValidParent() {
      if (this.parent == VariableHeightLayoutCache.this.root) {
        this.parent = null;
        return false;
      } 
      while (this.parent != null) {
        VariableHeightLayoutCache.TreeStateNode treeStateNode = (VariableHeightLayoutCache.TreeStateNode)this.parent.getParent();
        if (treeStateNode != null) {
          this.nextIndex = treeStateNode.getIndex(this.parent);
          this.parent = treeStateNode;
          this.childCount = this.parent.getChildCount();
          if (updateNextIndex())
            return true; 
          continue;
        } 
        this.parent = null;
      } 
      return false;
    }
    
    protected boolean updateNextIndex() {
      if (this.nextIndex == -1 && !this.parent.isExpanded())
        return false; 
      if (this.childCount == 0)
        return false; 
      if (++this.nextIndex >= this.childCount)
        return false; 
      VariableHeightLayoutCache.TreeStateNode treeStateNode = (VariableHeightLayoutCache.TreeStateNode)this.parent.getChildAt(this.nextIndex);
      if (treeStateNode != null && treeStateNode.isExpanded()) {
        this.parent = treeStateNode;
        this.nextIndex = -1;
        this.childCount = treeStateNode.getChildCount();
      } 
      return true;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\VariableHeightLayoutCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */