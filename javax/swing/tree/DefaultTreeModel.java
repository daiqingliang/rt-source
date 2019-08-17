package javax.swing.tree;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class DefaultTreeModel implements Serializable, TreeModel {
  protected TreeNode root;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  protected boolean asksAllowsChildren;
  
  @ConstructorProperties({"root"})
  public DefaultTreeModel(TreeNode paramTreeNode) { this(paramTreeNode, false); }
  
  public DefaultTreeModel(TreeNode paramTreeNode, boolean paramBoolean) {
    this.root = paramTreeNode;
    this.asksAllowsChildren = paramBoolean;
  }
  
  public void setAsksAllowsChildren(boolean paramBoolean) { this.asksAllowsChildren = paramBoolean; }
  
  public boolean asksAllowsChildren() { return this.asksAllowsChildren; }
  
  public void setRoot(TreeNode paramTreeNode) {
    TreeNode treeNode = this.root;
    this.root = paramTreeNode;
    if (paramTreeNode == null && treeNode != null) {
      fireTreeStructureChanged(this, null);
    } else {
      nodeStructureChanged(paramTreeNode);
    } 
  }
  
  public Object getRoot() { return this.root; }
  
  public int getIndexOfChild(Object paramObject1, Object paramObject2) { return (paramObject1 == null || paramObject2 == null) ? -1 : ((TreeNode)paramObject1).getIndex((TreeNode)paramObject2); }
  
  public Object getChild(Object paramObject, int paramInt) { return ((TreeNode)paramObject).getChildAt(paramInt); }
  
  public int getChildCount(Object paramObject) { return ((TreeNode)paramObject).getChildCount(); }
  
  public boolean isLeaf(Object paramObject) { return this.asksAllowsChildren ? (!((TreeNode)paramObject).getAllowsChildren()) : ((TreeNode)paramObject).isLeaf(); }
  
  public void reload() { reload(this.root); }
  
  public void valueForPathChanged(TreePath paramTreePath, Object paramObject) {
    MutableTreeNode mutableTreeNode = (MutableTreeNode)paramTreePath.getLastPathComponent();
    mutableTreeNode.setUserObject(paramObject);
    nodeChanged(mutableTreeNode);
  }
  
  public void insertNodeInto(MutableTreeNode paramMutableTreeNode1, MutableTreeNode paramMutableTreeNode2, int paramInt) {
    paramMutableTreeNode2.insert(paramMutableTreeNode1, paramInt);
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = paramInt;
    nodesWereInserted(paramMutableTreeNode2, arrayOfInt);
  }
  
  public void removeNodeFromParent(MutableTreeNode paramMutableTreeNode) {
    MutableTreeNode mutableTreeNode = (MutableTreeNode)paramMutableTreeNode.getParent();
    if (mutableTreeNode == null)
      throw new IllegalArgumentException("node does not have a parent."); 
    int[] arrayOfInt = new int[1];
    Object[] arrayOfObject = new Object[1];
    arrayOfInt[0] = mutableTreeNode.getIndex(paramMutableTreeNode);
    mutableTreeNode.remove(arrayOfInt[0]);
    arrayOfObject[0] = paramMutableTreeNode;
    nodesWereRemoved(mutableTreeNode, arrayOfInt, arrayOfObject);
  }
  
  public void nodeChanged(TreeNode paramTreeNode) {
    if (this.listenerList != null && paramTreeNode != null) {
      TreeNode treeNode = paramTreeNode.getParent();
      if (treeNode != null) {
        int i = treeNode.getIndex(paramTreeNode);
        if (i != -1) {
          int[] arrayOfInt = new int[1];
          arrayOfInt[0] = i;
          nodesChanged(treeNode, arrayOfInt);
        } 
      } else if (paramTreeNode == getRoot()) {
        nodesChanged(paramTreeNode, null);
      } 
    } 
  }
  
  public void reload(TreeNode paramTreeNode) {
    if (paramTreeNode != null)
      fireTreeStructureChanged(this, getPathToRoot(paramTreeNode), null, null); 
  }
  
  public void nodesWereInserted(TreeNode paramTreeNode, int[] paramArrayOfInt) {
    if (this.listenerList != null && paramTreeNode != null && paramArrayOfInt != null && paramArrayOfInt.length > 0) {
      int i = paramArrayOfInt.length;
      Object[] arrayOfObject = new Object[i];
      for (byte b = 0; b < i; b++)
        arrayOfObject[b] = paramTreeNode.getChildAt(paramArrayOfInt[b]); 
      fireTreeNodesInserted(this, getPathToRoot(paramTreeNode), paramArrayOfInt, arrayOfObject);
    } 
  }
  
  public void nodesWereRemoved(TreeNode paramTreeNode, int[] paramArrayOfInt, Object[] paramArrayOfObject) {
    if (paramTreeNode != null && paramArrayOfInt != null)
      fireTreeNodesRemoved(this, getPathToRoot(paramTreeNode), paramArrayOfInt, paramArrayOfObject); 
  }
  
  public void nodesChanged(TreeNode paramTreeNode, int[] paramArrayOfInt) {
    if (paramTreeNode != null)
      if (paramArrayOfInt != null) {
        int i = paramArrayOfInt.length;
        if (i > 0) {
          Object[] arrayOfObject = new Object[i];
          for (byte b = 0; b < i; b++)
            arrayOfObject[b] = paramTreeNode.getChildAt(paramArrayOfInt[b]); 
          fireTreeNodesChanged(this, getPathToRoot(paramTreeNode), paramArrayOfInt, arrayOfObject);
        } 
      } else if (paramTreeNode == getRoot()) {
        fireTreeNodesChanged(this, getPathToRoot(paramTreeNode), null, null);
      }  
  }
  
  public void nodeStructureChanged(TreeNode paramTreeNode) {
    if (paramTreeNode != null)
      fireTreeStructureChanged(this, getPathToRoot(paramTreeNode), null, null); 
  }
  
  public TreeNode[] getPathToRoot(TreeNode paramTreeNode) { return getPathToRoot(paramTreeNode, 0); }
  
  protected TreeNode[] getPathToRoot(TreeNode paramTreeNode, int paramInt) {
    TreeNode[] arrayOfTreeNode;
    if (paramTreeNode == null) {
      if (paramInt == 0)
        return null; 
      arrayOfTreeNode = new TreeNode[paramInt];
    } else {
      paramInt++;
      if (paramTreeNode == this.root) {
        arrayOfTreeNode = new TreeNode[paramInt];
      } else {
        arrayOfTreeNode = getPathToRoot(paramTreeNode.getParent(), paramInt);
      } 
      arrayOfTreeNode[arrayOfTreeNode.length - paramInt] = paramTreeNode;
    } 
    return arrayOfTreeNode;
  }
  
  public void addTreeModelListener(TreeModelListener paramTreeModelListener) { this.listenerList.add(TreeModelListener.class, paramTreeModelListener); }
  
  public void removeTreeModelListener(TreeModelListener paramTreeModelListener) { this.listenerList.remove(TreeModelListener.class, paramTreeModelListener); }
  
  public TreeModelListener[] getTreeModelListeners() { return (TreeModelListener[])this.listenerList.getListeners(TreeModelListener.class); }
  
  protected void fireTreeNodesChanged(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeModelEvent treeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class) {
        if (treeModelEvent == null)
          treeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2); 
        ((TreeModelListener)arrayOfObject[i + 1]).treeNodesChanged(treeModelEvent);
      } 
    } 
  }
  
  protected void fireTreeNodesInserted(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeModelEvent treeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class) {
        if (treeModelEvent == null)
          treeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2); 
        ((TreeModelListener)arrayOfObject[i + 1]).treeNodesInserted(treeModelEvent);
      } 
    } 
  }
  
  protected void fireTreeNodesRemoved(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeModelEvent treeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class) {
        if (treeModelEvent == null)
          treeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2); 
        ((TreeModelListener)arrayOfObject[i + 1]).treeNodesRemoved(treeModelEvent);
      } 
    } 
  }
  
  protected void fireTreeStructureChanged(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeModelEvent treeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class) {
        if (treeModelEvent == null)
          treeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2); 
        ((TreeModelListener)arrayOfObject[i + 1]).treeStructureChanged(treeModelEvent);
      } 
    } 
  }
  
  private void fireTreeStructureChanged(Object paramObject, TreePath paramTreePath) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeModelEvent treeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class) {
        if (treeModelEvent == null)
          treeModelEvent = new TreeModelEvent(paramObject, paramTreePath); 
        ((TreeModelListener)arrayOfObject[i + 1]).treeStructureChanged(treeModelEvent);
      } 
    } 
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Vector vector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if (this.root != null && this.root instanceof Serializable) {
      vector.addElement("root");
      vector.addElement(this.root);
    } 
    paramObjectOutputStream.writeObject(vector);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Vector vector = (Vector)paramObjectInputStream.readObject();
    byte b = 0;
    int i = vector.size();
    if (b < i && vector.elementAt(b).equals("root")) {
      this.root = (TreeNode)vector.elementAt(++b);
      b++;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\DefaultTreeModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */