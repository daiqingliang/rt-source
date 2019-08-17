package javax.swing.tree;

import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;
import javax.swing.event.TreeModelEvent;
import sun.swing.SwingUtilities2;

public class FixedHeightLayoutCache extends AbstractLayoutCache {
  private FHTreeStateNode root;
  
  private int rowCount;
  
  private Rectangle boundsBuffer = new Rectangle();
  
  private Hashtable<TreePath, FHTreeStateNode> treePathMapping = new Hashtable();
  
  private SearchInfo info = new SearchInfo(null);
  
  private Stack<Stack<TreePath>> tempStacks = new Stack();
  
  public FixedHeightLayoutCache() { setRowHeight(1); }
  
  public void setModel(TreeModel paramTreeModel) {
    super.setModel(paramTreeModel);
    rebuild(false);
  }
  
  public void setRootVisible(boolean paramBoolean) {
    if (isRootVisible() != paramBoolean) {
      super.setRootVisible(paramBoolean);
      if (this.root != null) {
        if (paramBoolean) {
          this.rowCount++;
          this.root.adjustRowBy(1);
        } else {
          this.rowCount--;
          this.root.adjustRowBy(-1);
        } 
        visibleNodesChanged();
      } 
    } 
  }
  
  public void setRowHeight(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("FixedHeightLayoutCache only supports row heights greater than 0"); 
    if (getRowHeight() != paramInt) {
      super.setRowHeight(paramInt);
      visibleNodesChanged();
    } 
  }
  
  public int getRowCount() { return this.rowCount; }
  
  public void invalidatePathBounds(TreePath paramTreePath) {}
  
  public void invalidateSizes() { visibleNodesChanged(); }
  
  public boolean isExpanded(TreePath paramTreePath) {
    if (paramTreePath != null) {
      FHTreeStateNode fHTreeStateNode = getNodeForPath(paramTreePath, true, false);
      return (fHTreeStateNode != null && fHTreeStateNode.isExpanded());
    } 
    return false;
  }
  
  public Rectangle getBounds(TreePath paramTreePath, Rectangle paramRectangle) {
    if (paramTreePath == null)
      return null; 
    FHTreeStateNode fHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (fHTreeStateNode != null)
      return getBounds(fHTreeStateNode, -1, paramRectangle); 
    TreePath treePath = paramTreePath.getParentPath();
    fHTreeStateNode = getNodeForPath(treePath, true, false);
    if (fHTreeStateNode != null && fHTreeStateNode.isExpanded()) {
      int i = this.treeModel.getIndexOfChild(treePath.getLastPathComponent(), paramTreePath.getLastPathComponent());
      if (i != -1)
        return getBounds(fHTreeStateNode, i, paramRectangle); 
    } 
    return null;
  }
  
  public TreePath getPathForRow(int paramInt) { return (paramInt >= 0 && paramInt < getRowCount() && this.root.getPathForRow(paramInt, getRowCount(), this.info)) ? this.info.getPath() : null; }
  
  public int getRowForPath(TreePath paramTreePath) {
    if (paramTreePath == null || this.root == null)
      return -1; 
    FHTreeStateNode fHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (fHTreeStateNode != null)
      return fHTreeStateNode.getRow(); 
    TreePath treePath = paramTreePath.getParentPath();
    fHTreeStateNode = getNodeForPath(treePath, true, false);
    return (fHTreeStateNode != null && fHTreeStateNode.isExpanded()) ? fHTreeStateNode.getRowToModelIndex(this.treeModel.getIndexOfChild(treePath.getLastPathComponent(), paramTreePath.getLastPathComponent())) : -1;
  }
  
  public TreePath getPathClosestTo(int paramInt1, int paramInt2) {
    if (getRowCount() == 0)
      return null; 
    int i = getRowContainingYLocation(paramInt2);
    return getPathForRow(i);
  }
  
  public int getVisibleChildCount(TreePath paramTreePath) {
    FHTreeStateNode fHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    return (fHTreeStateNode == null) ? 0 : fHTreeStateNode.getTotalChildCount();
  }
  
  public Enumeration<TreePath> getVisiblePathsFrom(TreePath paramTreePath) {
    if (paramTreePath == null)
      return null; 
    FHTreeStateNode fHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (fHTreeStateNode != null)
      return new VisibleFHTreeStateNodeEnumeration(fHTreeStateNode); 
    TreePath treePath = paramTreePath.getParentPath();
    fHTreeStateNode = getNodeForPath(treePath, true, false);
    return (fHTreeStateNode != null && fHTreeStateNode.isExpanded()) ? new VisibleFHTreeStateNodeEnumeration(fHTreeStateNode, this.treeModel.getIndexOfChild(treePath.getLastPathComponent(), paramTreePath.getLastPathComponent())) : null;
  }
  
  public void setExpandedState(TreePath paramTreePath, boolean paramBoolean) {
    if (paramBoolean) {
      ensurePathIsExpanded(paramTreePath, true);
    } else if (paramTreePath != null) {
      TreePath treePath = paramTreePath.getParentPath();
      if (treePath != null) {
        FHTreeStateNode fHTreeStateNode1 = getNodeForPath(treePath, false, true);
        if (fHTreeStateNode1 != null)
          fHTreeStateNode1.makeVisible(); 
      } 
      FHTreeStateNode fHTreeStateNode = getNodeForPath(paramTreePath, true, false);
      if (fHTreeStateNode != null)
        fHTreeStateNode.collapse(true); 
    } 
  }
  
  public boolean getExpandedState(TreePath paramTreePath) {
    FHTreeStateNode fHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    return (fHTreeStateNode != null) ? ((fHTreeStateNode.isVisible() && fHTreeStateNode.isExpanded())) : false;
  }
  
  public void treeNodesChanged(TreeModelEvent paramTreeModelEvent) {
    if (paramTreeModelEvent != null) {
      FHTreeStateNode fHTreeStateNode = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      if (fHTreeStateNode != null) {
        int i;
        if (arrayOfInt != null && (i = arrayOfInt.length) > 0) {
          Object object = fHTreeStateNode.getUserObject();
          for (byte b = 0; b < i; b++) {
            FHTreeStateNode fHTreeStateNode1 = fHTreeStateNode.getChildAtModelIndex(arrayOfInt[b]);
            if (fHTreeStateNode1 != null)
              fHTreeStateNode1.setUserObject(this.treeModel.getChild(object, arrayOfInt[b])); 
          } 
          if (fHTreeStateNode.isVisible() && fHTreeStateNode.isExpanded())
            visibleNodesChanged(); 
        } else if (fHTreeStateNode == this.root && fHTreeStateNode.isVisible() && fHTreeStateNode.isExpanded()) {
          visibleNodesChanged();
        } 
      } 
    } 
  }
  
  public void treeNodesInserted(TreeModelEvent paramTreeModelEvent) {
    if (paramTreeModelEvent != null) {
      FHTreeStateNode fHTreeStateNode = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      int i;
      if (fHTreeStateNode != null && arrayOfInt != null && (i = arrayOfInt.length) > 0) {
        boolean bool = (fHTreeStateNode.isVisible() && fHTreeStateNode.isExpanded());
        for (byte b = 0; b < i; b++)
          fHTreeStateNode.childInsertedAtModelIndex(arrayOfInt[b], bool); 
        if (bool && this.treeSelectionModel != null)
          this.treeSelectionModel.resetRowSelection(); 
        if (fHTreeStateNode.isVisible())
          visibleNodesChanged(); 
      } 
    } 
  }
  
  public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent) {
    if (paramTreeModelEvent != null) {
      TreePath treePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
      FHTreeStateNode fHTreeStateNode = getNodeForPath(treePath, false, false);
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      int i;
      if (fHTreeStateNode != null && arrayOfInt != null && (i = arrayOfInt.length) > 0) {
        Object[] arrayOfObject = paramTreeModelEvent.getChildren();
        boolean bool = (fHTreeStateNode.isVisible() && fHTreeStateNode.isExpanded());
        for (int j = i - 1; j >= 0; j--)
          fHTreeStateNode.removeChildAtModelIndex(arrayOfInt[j], bool); 
        if (bool) {
          if (this.treeSelectionModel != null)
            this.treeSelectionModel.resetRowSelection(); 
          if (this.treeModel.getChildCount(fHTreeStateNode.getUserObject()) == 0 && fHTreeStateNode.isLeaf())
            fHTreeStateNode.collapse(false); 
          visibleNodesChanged();
        } else if (fHTreeStateNode.isVisible()) {
          visibleNodesChanged();
        } 
      } 
    } 
  }
  
  public void treeStructureChanged(TreeModelEvent paramTreeModelEvent) {
    if (paramTreeModelEvent != null) {
      TreePath treePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
      FHTreeStateNode fHTreeStateNode = getNodeForPath(treePath, false, false);
      if (fHTreeStateNode == this.root || (fHTreeStateNode == null && ((treePath == null && this.treeModel != null && this.treeModel.getRoot() == null) || (treePath != null && treePath.getPathCount() <= 1)))) {
        rebuild(true);
      } else if (fHTreeStateNode != null) {
        FHTreeStateNode fHTreeStateNode1 = (FHTreeStateNode)fHTreeStateNode.getParent();
        boolean bool1 = fHTreeStateNode.isExpanded();
        boolean bool2 = fHTreeStateNode.isVisible();
        int i = fHTreeStateNode1.getIndex(fHTreeStateNode);
        fHTreeStateNode.collapse(false);
        fHTreeStateNode1.remove(i);
        if (bool2 && bool1) {
          int j = fHTreeStateNode.getRow();
          fHTreeStateNode1.resetChildrenRowsFrom(j, i, fHTreeStateNode.getChildIndex());
          fHTreeStateNode = getNodeForPath(treePath, false, true);
          fHTreeStateNode.expand();
        } 
        if (this.treeSelectionModel != null && bool2 && bool1)
          this.treeSelectionModel.resetRowSelection(); 
        if (bool2)
          visibleNodesChanged(); 
      } 
    } 
  }
  
  private void visibleNodesChanged() {}
  
  private Rectangle getBounds(FHTreeStateNode paramFHTreeStateNode, int paramInt, Rectangle paramRectangle) {
    Object object;
    int j;
    int i;
    boolean bool;
    if (paramInt == -1) {
      j = paramFHTreeStateNode.getRow();
      object = paramFHTreeStateNode.getUserObject();
      bool = paramFHTreeStateNode.isExpanded();
      i = paramFHTreeStateNode.getLevel();
    } else {
      j = paramFHTreeStateNode.getRowToModelIndex(paramInt);
      object = this.treeModel.getChild(paramFHTreeStateNode.getUserObject(), paramInt);
      bool = false;
      i = paramFHTreeStateNode.getLevel() + 1;
    } 
    Rectangle rectangle = getNodeDimensions(object, j, i, bool, this.boundsBuffer);
    if (rectangle == null)
      return null; 
    if (paramRectangle == null)
      paramRectangle = new Rectangle(); 
    paramRectangle.x = rectangle.x;
    paramRectangle.height = getRowHeight();
    paramRectangle.y = j * paramRectangle.height;
    paramRectangle.width = rectangle.width;
    return paramRectangle;
  }
  
  private void adjustRowCountBy(int paramInt) { this.rowCount += paramInt; }
  
  private void addMapping(FHTreeStateNode paramFHTreeStateNode) { this.treePathMapping.put(paramFHTreeStateNode.getTreePath(), paramFHTreeStateNode); }
  
  private void removeMapping(FHTreeStateNode paramFHTreeStateNode) { this.treePathMapping.remove(paramFHTreeStateNode.getTreePath()); }
  
  private FHTreeStateNode getMapping(TreePath paramTreePath) { return (FHTreeStateNode)this.treePathMapping.get(paramTreePath); }
  
  private void rebuild(boolean paramBoolean) {
    this.treePathMapping.clear();
    Object object;
    if (this.treeModel != null && (object = this.treeModel.getRoot()) != null) {
      this.root = createNodeForValue(object, 0);
      this.root.path = new TreePath(object);
      addMapping(this.root);
      if (isRootVisible()) {
        this.rowCount = 1;
        this.root.row = 0;
      } else {
        this.rowCount = 0;
        this.root.row = -1;
      } 
      this.root.expand();
    } else {
      this.root = null;
      this.rowCount = 0;
    } 
    if (paramBoolean && this.treeSelectionModel != null)
      this.treeSelectionModel.clearSelection(); 
    visibleNodesChanged();
  }
  
  private int getRowContainingYLocation(int paramInt) { return (getRowCount() == 0) ? -1 : Math.max(0, Math.min(getRowCount() - 1, paramInt / getRowHeight())); }
  
  private boolean ensurePathIsExpanded(TreePath paramTreePath, boolean paramBoolean) {
    if (paramTreePath != null) {
      if (this.treeModel.isLeaf(paramTreePath.getLastPathComponent())) {
        paramTreePath = paramTreePath.getParentPath();
        paramBoolean = true;
      } 
      if (paramTreePath != null) {
        FHTreeStateNode fHTreeStateNode = getNodeForPath(paramTreePath, false, true);
        if (fHTreeStateNode != null) {
          fHTreeStateNode.makeVisible();
          if (paramBoolean)
            fHTreeStateNode.expand(); 
          return true;
        } 
      } 
    } 
    return false;
  }
  
  private FHTreeStateNode createNodeForValue(Object paramObject, int paramInt) { return new FHTreeStateNode(paramObject, paramInt, -1); }
  
  private FHTreeStateNode getNodeForPath(TreePath paramTreePath, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramTreePath != null) {
      FHTreeStateNode fHTreeStateNode = getMapping(paramTreePath);
      if (fHTreeStateNode != null)
        return (paramBoolean1 && !fHTreeStateNode.isVisible()) ? null : fHTreeStateNode; 
      if (paramBoolean1)
        return null; 
      if (this.tempStacks.size() == 0) {
        stack = new Stack();
      } else {
        stack = (Stack)this.tempStacks.pop();
      } 
      try {
        stack.push(paramTreePath);
        paramTreePath = paramTreePath.getParentPath();
        fHTreeStateNode = null;
        while (paramTreePath != null) {
          fHTreeStateNode = getMapping(paramTreePath);
          if (fHTreeStateNode != null) {
            while (fHTreeStateNode != null && stack.size() > 0) {
              paramTreePath = (TreePath)stack.pop();
              fHTreeStateNode = fHTreeStateNode.createChildFor(paramTreePath.getLastPathComponent());
            } 
            return fHTreeStateNode;
          } 
          stack.push(paramTreePath);
          paramTreePath = paramTreePath.getParentPath();
        } 
      } finally {
        stack.removeAllElements();
        this.tempStacks.push(stack);
      } 
      return null;
    } 
    return null;
  }
  
  private class FHTreeStateNode extends DefaultMutableTreeNode {
    protected boolean isExpanded;
    
    protected int childIndex;
    
    protected int childCount;
    
    protected int row;
    
    protected TreePath path;
    
    public FHTreeStateNode(Object param1Object, int param1Int1, int param1Int2) {
      super(param1Object);
      this.childIndex = param1Int1;
      this.row = param1Int2;
    }
    
    public void setParent(MutableTreeNode param1MutableTreeNode) {
      super.setParent(param1MutableTreeNode);
      if (param1MutableTreeNode != null) {
        this.path = ((FHTreeStateNode)param1MutableTreeNode).getTreePath().pathByAddingChild(getUserObject());
        FixedHeightLayoutCache.this.addMapping(this);
      } 
    }
    
    public void remove(int param1Int) {
      FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getChildAt(param1Int);
      fHTreeStateNode.removeFromMapping();
      super.remove(param1Int);
    }
    
    public void setUserObject(Object param1Object) {
      super.setUserObject(param1Object);
      if (this.path != null) {
        FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getParent();
        if (fHTreeStateNode != null) {
          resetChildrenPaths(fHTreeStateNode.getTreePath());
        } else {
          resetChildrenPaths(null);
        } 
      } 
    }
    
    public int getChildIndex() { return this.childIndex; }
    
    public TreePath getTreePath() { return this.path; }
    
    public FHTreeStateNode getChildAtModelIndex(int param1Int) {
      for (int i = getChildCount() - 1; i >= 0; i--) {
        if (((FHTreeStateNode)getChildAt(i)).childIndex == param1Int)
          return (FHTreeStateNode)getChildAt(i); 
      } 
      return null;
    }
    
    public boolean isVisible() {
      FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getParent();
      return (fHTreeStateNode == null) ? true : ((fHTreeStateNode.isExpanded() && fHTreeStateNode.isVisible()));
    }
    
    public int getRow() { return this.row; }
    
    public int getRowToModelIndex(int param1Int) {
      int i = getRow() + 1;
      int j = i;
      byte b = 0;
      int k = getChildCount();
      while (b < k) {
        FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getChildAt(b);
        if (fHTreeStateNode.childIndex >= param1Int)
          return (fHTreeStateNode.childIndex == param1Int) ? fHTreeStateNode.row : ((b == 0) ? (getRow() + 1 + param1Int) : (fHTreeStateNode.row - fHTreeStateNode.childIndex - param1Int)); 
        b++;
      } 
      return getRow() + 1 + getTotalChildCount() - this.childCount - param1Int;
    }
    
    public int getTotalChildCount() {
      if (isExpanded()) {
        FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getParent();
        int i;
        if (fHTreeStateNode != null && (i = fHTreeStateNode.getIndex(this)) + 1 < fHTreeStateNode.getChildCount()) {
          FHTreeStateNode fHTreeStateNode1 = (FHTreeStateNode)fHTreeStateNode.getChildAt(i + 1);
          return fHTreeStateNode1.row - this.row - fHTreeStateNode1.childIndex - this.childIndex;
        } 
        int j = this.childCount;
        for (int k = getChildCount() - 1; k >= 0; k--)
          j += ((FHTreeStateNode)getChildAt(k)).getTotalChildCount(); 
        return j;
      } 
      return 0;
    }
    
    public boolean isExpanded() { return this.isExpanded; }
    
    public int getVisibleLevel() { return FixedHeightLayoutCache.this.isRootVisible() ? getLevel() : (getLevel() - 1); }
    
    protected void resetChildrenPaths(TreePath param1TreePath) {
      FixedHeightLayoutCache.this.removeMapping(this);
      if (param1TreePath == null) {
        this.path = new TreePath(getUserObject());
      } else {
        this.path = param1TreePath.pathByAddingChild(getUserObject());
      } 
      FixedHeightLayoutCache.this.addMapping(this);
      for (int i = getChildCount() - 1; i >= 0; i--)
        ((FHTreeStateNode)getChildAt(i)).resetChildrenPaths(this.path); 
    }
    
    protected void removeFromMapping() {
      if (this.path != null) {
        FixedHeightLayoutCache.this.removeMapping(this);
        for (int i = getChildCount() - 1; i >= 0; i--)
          ((FHTreeStateNode)getChildAt(i)).removeFromMapping(); 
      } 
    }
    
    protected FHTreeStateNode createChildFor(Object param1Object) {
      byte b;
      int i = FixedHeightLayoutCache.this.treeModel.getIndexOfChild(getUserObject(), param1Object);
      if (i < 0)
        return null; 
      FHTreeStateNode fHTreeStateNode = FixedHeightLayoutCache.this.createNodeForValue(param1Object, i);
      if (isVisible()) {
        b = getRowToModelIndex(i);
      } else {
        b = -1;
      } 
      fHTreeStateNode.row = b;
      byte b1 = 0;
      int j = getChildCount();
      while (b1 < j) {
        FHTreeStateNode fHTreeStateNode1 = (FHTreeStateNode)getChildAt(b1);
        if (fHTreeStateNode1.childIndex > i) {
          insert(fHTreeStateNode, b1);
          return fHTreeStateNode;
        } 
        b1++;
      } 
      add(fHTreeStateNode);
      return fHTreeStateNode;
    }
    
    protected void adjustRowBy(int param1Int) {
      this.row += param1Int;
      if (this.isExpanded)
        for (int i = getChildCount() - 1; i >= 0; i--)
          ((FHTreeStateNode)getChildAt(i)).adjustRowBy(param1Int);  
    }
    
    protected void adjustRowBy(int param1Int1, int param1Int2) {
      if (this.isExpanded)
        for (int i = getChildCount() - 1; i >= param1Int2; i--)
          ((FHTreeStateNode)getChildAt(i)).adjustRowBy(param1Int1);  
      FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getParent();
      if (fHTreeStateNode != null)
        fHTreeStateNode.adjustRowBy(param1Int1, fHTreeStateNode.getIndex(this) + 1); 
    }
    
    protected void didExpand() {
      int i = setRowAndChildren(this.row);
      FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getParent();
      int j = i - this.row - 1;
      if (fHTreeStateNode != null)
        fHTreeStateNode.adjustRowBy(j, fHTreeStateNode.getIndex(this) + 1); 
      FixedHeightLayoutCache.this.adjustRowCountBy(j);
    }
    
    protected int setRowAndChildren(int param1Int) {
      this.row = param1Int;
      if (!isExpanded())
        return this.row + 1; 
      int i = this.row + 1;
      int j = 0;
      int k = getChildCount();
      for (byte b = 0; b < k; b++) {
        FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getChildAt(b);
        i += fHTreeStateNode.childIndex - j;
        j = fHTreeStateNode.childIndex + 1;
        if (fHTreeStateNode.isExpanded) {
          i = fHTreeStateNode.setRowAndChildren(i);
        } else {
          fHTreeStateNode.row = i++;
        } 
      } 
      return i + this.childCount - j;
    }
    
    protected void resetChildrenRowsFrom(int param1Int1, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      int k = getChildCount();
      for (int m = param1Int2; m < k; m++) {
        FHTreeStateNode fHTreeStateNode1 = (FHTreeStateNode)getChildAt(m);
        i += fHTreeStateNode1.childIndex - j;
        j = fHTreeStateNode1.childIndex + 1;
        if (fHTreeStateNode1.isExpanded) {
          i = fHTreeStateNode1.setRowAndChildren(i);
        } else {
          fHTreeStateNode1.row = i++;
        } 
      } 
      i += this.childCount - j;
      FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getParent();
      if (fHTreeStateNode != null) {
        fHTreeStateNode.resetChildrenRowsFrom(i, fHTreeStateNode.getIndex(this) + 1, this.childIndex + 1);
      } else {
        FixedHeightLayoutCache.this.rowCount = i;
      } 
    }
    
    protected void makeVisible() {
      FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getParent();
      if (fHTreeStateNode != null)
        fHTreeStateNode.expandParentAndReceiver(); 
    }
    
    protected void expandParentAndReceiver() {
      FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getParent();
      if (fHTreeStateNode != null)
        fHTreeStateNode.expandParentAndReceiver(); 
      expand();
    }
    
    protected void expand() {
      if (!this.isExpanded && !isLeaf()) {
        boolean bool = isVisible();
        this.isExpanded = true;
        this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(getUserObject());
        if (bool)
          didExpand(); 
        if (bool && FixedHeightLayoutCache.this.treeSelectionModel != null)
          FixedHeightLayoutCache.this.treeSelectionModel.resetRowSelection(); 
      } 
    }
    
    protected void collapse(boolean param1Boolean) {
      if (this.isExpanded) {
        if (isVisible() && param1Boolean) {
          int i = getTotalChildCount();
          this.isExpanded = false;
          FixedHeightLayoutCache.this.adjustRowCountBy(-i);
          adjustRowBy(-i, 0);
        } else {
          this.isExpanded = false;
        } 
        if (param1Boolean && isVisible() && FixedHeightLayoutCache.this.treeSelectionModel != null)
          FixedHeightLayoutCache.this.treeSelectionModel.resetRowSelection(); 
      } 
    }
    
    public boolean isLeaf() {
      TreeModel treeModel = FixedHeightLayoutCache.this.getModel();
      return (treeModel != null) ? treeModel.isLeaf(getUserObject()) : 1;
    }
    
    protected void addNode(FHTreeStateNode param1FHTreeStateNode) {
      boolean bool = false;
      int i = param1FHTreeStateNode.getChildIndex();
      int j = 0;
      int k = getChildCount();
      while (j < k) {
        if (((FHTreeStateNode)getChildAt(j)).getChildIndex() > i) {
          bool = true;
          insert(param1FHTreeStateNode, j);
          j = k;
        } 
        j++;
      } 
      if (!bool)
        add(param1FHTreeStateNode); 
    }
    
    protected void removeChildAtModelIndex(int param1Int, boolean param1Boolean) {
      FHTreeStateNode fHTreeStateNode = getChildAtModelIndex(param1Int);
      if (fHTreeStateNode != null) {
        int i = fHTreeStateNode.getRow();
        int j = getIndex(fHTreeStateNode);
        fHTreeStateNode.collapse(false);
        remove(j);
        adjustChildIndexs(j, -1);
        this.childCount--;
        if (param1Boolean)
          resetChildrenRowsFrom(i, j, param1Int); 
      } else {
        int i = getChildCount();
        for (byte b = 0; b < i; b++) {
          FHTreeStateNode fHTreeStateNode1 = (FHTreeStateNode)getChildAt(b);
          if (fHTreeStateNode1.childIndex >= param1Int) {
            if (param1Boolean) {
              adjustRowBy(-1, b);
              FixedHeightLayoutCache.this.adjustRowCountBy(-1);
            } 
            while (b < i) {
              ((FHTreeStateNode)getChildAt(b)).childIndex--;
              b++;
            } 
            this.childCount--;
            return;
          } 
        } 
        if (param1Boolean) {
          adjustRowBy(-1, i);
          FixedHeightLayoutCache.this.adjustRowCountBy(-1);
        } 
        this.childCount--;
      } 
    }
    
    protected void adjustChildIndexs(int param1Int1, int param1Int2) {
      int i = param1Int1;
      int j = getChildCount();
      while (i < j) {
        ((FHTreeStateNode)getChildAt(i)).childIndex += param1Int2;
        i++;
      } 
    }
    
    protected void childInsertedAtModelIndex(int param1Int, boolean param1Boolean) {
      int i = getChildCount();
      for (byte b = 0; b < i; b++) {
        FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getChildAt(b);
        if (fHTreeStateNode.childIndex >= param1Int) {
          if (param1Boolean) {
            adjustRowBy(1, b);
            FixedHeightLayoutCache.this.adjustRowCountBy(1);
          } 
          while (b < i) {
            ((FHTreeStateNode)getChildAt(b)).childIndex++;
            b++;
          } 
          this.childCount++;
          return;
        } 
      } 
      if (param1Boolean) {
        adjustRowBy(1, i);
        FixedHeightLayoutCache.this.adjustRowCountBy(1);
      } 
      this.childCount++;
    }
    
    protected boolean getPathForRow(int param1Int1, int param1Int2, FixedHeightLayoutCache.SearchInfo param1SearchInfo) {
      if (this.row == param1Int1) {
        param1SearchInfo.node = this;
        param1SearchInfo.isNodeParentNode = false;
        param1SearchInfo.childIndex = this.childIndex;
        return true;
      } 
      FHTreeStateNode fHTreeStateNode = null;
      int i = 0;
      int j = getChildCount();
      while (i < j) {
        FHTreeStateNode fHTreeStateNode1 = (FHTreeStateNode)getChildAt(i);
        if (fHTreeStateNode1.row > param1Int1) {
          if (i == 0) {
            param1SearchInfo.node = this;
            param1SearchInfo.isNodeParentNode = true;
            param1SearchInfo.childIndex = param1Int1 - this.row - 1;
            return true;
          } 
          int k = 1 + fHTreeStateNode1.row - fHTreeStateNode1.childIndex - fHTreeStateNode.childIndex;
          if (param1Int1 < k)
            return fHTreeStateNode.getPathForRow(param1Int1, k, param1SearchInfo); 
          param1SearchInfo.node = this;
          param1SearchInfo.isNodeParentNode = true;
          param1SearchInfo.childIndex = param1Int1 - k + fHTreeStateNode.childIndex + 1;
          return true;
        } 
        fHTreeStateNode = fHTreeStateNode1;
        i++;
      } 
      if (fHTreeStateNode != null) {
        i = param1Int2 - this.childCount - fHTreeStateNode.childIndex + 1;
        if (param1Int1 < i)
          return fHTreeStateNode.getPathForRow(param1Int1, i, param1SearchInfo); 
        param1SearchInfo.node = this;
        param1SearchInfo.isNodeParentNode = true;
        param1SearchInfo.childIndex = param1Int1 - i + fHTreeStateNode.childIndex + 1;
        return true;
      } 
      i = param1Int1 - this.row - 1;
      if (i >= this.childCount)
        return false; 
      param1SearchInfo.node = this;
      param1SearchInfo.isNodeParentNode = true;
      param1SearchInfo.childIndex = i;
      return true;
    }
    
    protected int getCountTo(int param1Int) {
      int i = param1Int + 1;
      int j = 0;
      int k = getChildCount();
      while (j < k) {
        FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getChildAt(j);
        if (fHTreeStateNode.childIndex >= param1Int) {
          j = k;
        } else {
          i += fHTreeStateNode.getTotalChildCount();
        } 
        j++;
      } 
      return (this.parent != null) ? (i + ((FHTreeStateNode)getParent()).getCountTo(this.childIndex)) : (!FixedHeightLayoutCache.this.isRootVisible() ? (i - 1) : i);
    }
    
    protected int getNumExpandedChildrenTo(int param1Int) {
      int i = param1Int;
      byte b = 0;
      int j = getChildCount();
      while (b < j) {
        FHTreeStateNode fHTreeStateNode = (FHTreeStateNode)getChildAt(b);
        if (fHTreeStateNode.childIndex >= param1Int)
          return i; 
        i += fHTreeStateNode.getTotalChildCount();
        b++;
      } 
      return i;
    }
    
    protected void didAdjustTree() {}
  }
  
  private class SearchInfo {
    protected FixedHeightLayoutCache.FHTreeStateNode node;
    
    protected boolean isNodeParentNode;
    
    protected int childIndex;
    
    private SearchInfo() {}
    
    protected TreePath getPath() { return (this.node == null) ? null : (this.isNodeParentNode ? this.node.getTreePath().pathByAddingChild(FixedHeightLayoutCache.this.treeModel.getChild(this.node.getUserObject(), this.childIndex)) : this.node.path); }
  }
  
  private class VisibleFHTreeStateNodeEnumeration extends Object implements Enumeration<TreePath> {
    protected FixedHeightLayoutCache.FHTreeStateNode parent;
    
    protected int nextIndex;
    
    protected int childCount;
    
    protected VisibleFHTreeStateNodeEnumeration(FixedHeightLayoutCache this$0, FixedHeightLayoutCache.FHTreeStateNode param1FHTreeStateNode) { this(param1FHTreeStateNode, -1); }
    
    protected VisibleFHTreeStateNodeEnumeration(FixedHeightLayoutCache.FHTreeStateNode param1FHTreeStateNode, int param1Int) {
      this.parent = param1FHTreeStateNode;
      this.nextIndex = param1Int;
      this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(this.parent.getUserObject());
    }
    
    public boolean hasMoreElements() { return (this.parent != null); }
    
    public TreePath nextElement() {
      TreePath treePath;
      if (!hasMoreElements())
        throw new NoSuchElementException("No more visible paths"); 
      if (this.nextIndex == -1) {
        treePath = this.parent.getTreePath();
      } else {
        FixedHeightLayoutCache.FHTreeStateNode fHTreeStateNode = this.parent.getChildAtModelIndex(this.nextIndex);
        if (fHTreeStateNode == null) {
          treePath = this.parent.getTreePath().pathByAddingChild(FixedHeightLayoutCache.this.treeModel.getChild(this.parent.getUserObject(), this.nextIndex));
        } else {
          treePath = fHTreeStateNode.getTreePath();
        } 
      } 
      updateNextObject();
      return treePath;
    }
    
    protected void updateNextObject() {
      if (!updateNextIndex())
        findNextValidParent(); 
    }
    
    protected boolean findNextValidParent() {
      if (this.parent == FixedHeightLayoutCache.this.root) {
        this.parent = null;
        return false;
      } 
      while (this.parent != null) {
        FixedHeightLayoutCache.FHTreeStateNode fHTreeStateNode = (FixedHeightLayoutCache.FHTreeStateNode)this.parent.getParent();
        if (fHTreeStateNode != null) {
          this.nextIndex = this.parent.childIndex;
          this.parent = fHTreeStateNode;
          this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(this.parent.getUserObject());
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
      FixedHeightLayoutCache.FHTreeStateNode fHTreeStateNode = this.parent.getChildAtModelIndex(this.nextIndex);
      if (fHTreeStateNode != null && fHTreeStateNode.isExpanded()) {
        this.parent = fHTreeStateNode;
        this.nextIndex = -1;
        this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(fHTreeStateNode.getUserObject());
      } 
      return true;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\FixedHeightLayoutCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */