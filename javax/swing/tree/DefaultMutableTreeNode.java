package javax.swing.tree;

import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

public class DefaultMutableTreeNode implements Cloneable, MutableTreeNode, Serializable {
  private static final long serialVersionUID = -4298474751201349152L;
  
  public static final Enumeration<TreeNode> EMPTY_ENUMERATION = Collections.emptyEnumeration();
  
  protected MutableTreeNode parent = null;
  
  protected Vector children;
  
  protected Object userObject;
  
  protected boolean allowsChildren;
  
  public DefaultMutableTreeNode() { this(null); }
  
  public DefaultMutableTreeNode(Object paramObject) { this(paramObject, true); }
  
  public DefaultMutableTreeNode(Object paramObject, boolean paramBoolean) {
    this.allowsChildren = paramBoolean;
    this.userObject = paramObject;
  }
  
  public void insert(MutableTreeNode paramMutableTreeNode, int paramInt) {
    if (!this.allowsChildren)
      throw new IllegalStateException("node does not allow children"); 
    if (paramMutableTreeNode == null)
      throw new IllegalArgumentException("new child is null"); 
    if (isNodeAncestor(paramMutableTreeNode))
      throw new IllegalArgumentException("new child is an ancestor"); 
    MutableTreeNode mutableTreeNode = (MutableTreeNode)paramMutableTreeNode.getParent();
    if (mutableTreeNode != null)
      mutableTreeNode.remove(paramMutableTreeNode); 
    paramMutableTreeNode.setParent(this);
    if (this.children == null)
      this.children = new Vector(); 
    this.children.insertElementAt(paramMutableTreeNode, paramInt);
  }
  
  public void remove(int paramInt) {
    MutableTreeNode mutableTreeNode = (MutableTreeNode)getChildAt(paramInt);
    this.children.removeElementAt(paramInt);
    mutableTreeNode.setParent(null);
  }
  
  @Transient
  public void setParent(MutableTreeNode paramMutableTreeNode) { this.parent = paramMutableTreeNode; }
  
  public TreeNode getParent() { return this.parent; }
  
  public TreeNode getChildAt(int paramInt) {
    if (this.children == null)
      throw new ArrayIndexOutOfBoundsException("node has no children"); 
    return (TreeNode)this.children.elementAt(paramInt);
  }
  
  public int getChildCount() { return (this.children == null) ? 0 : this.children.size(); }
  
  public int getIndex(TreeNode paramTreeNode) {
    if (paramTreeNode == null)
      throw new IllegalArgumentException("argument is null"); 
    return !isNodeChild(paramTreeNode) ? -1 : this.children.indexOf(paramTreeNode);
  }
  
  public Enumeration children() { return (this.children == null) ? EMPTY_ENUMERATION : this.children.elements(); }
  
  public void setAllowsChildren(boolean paramBoolean) {
    if (paramBoolean != this.allowsChildren) {
      this.allowsChildren = paramBoolean;
      if (!this.allowsChildren)
        removeAllChildren(); 
    } 
  }
  
  public boolean getAllowsChildren() { return this.allowsChildren; }
  
  public void setUserObject(Object paramObject) { this.userObject = paramObject; }
  
  public Object getUserObject() { return this.userObject; }
  
  public void removeFromParent() {
    MutableTreeNode mutableTreeNode = (MutableTreeNode)getParent();
    if (mutableTreeNode != null)
      mutableTreeNode.remove(this); 
  }
  
  public void remove(MutableTreeNode paramMutableTreeNode) {
    if (paramMutableTreeNode == null)
      throw new IllegalArgumentException("argument is null"); 
    if (!isNodeChild(paramMutableTreeNode))
      throw new IllegalArgumentException("argument is not a child"); 
    remove(getIndex(paramMutableTreeNode));
  }
  
  public void removeAllChildren() {
    for (int i = getChildCount() - 1; i >= 0; i--)
      remove(i); 
  }
  
  public void add(MutableTreeNode paramMutableTreeNode) {
    if (paramMutableTreeNode != null && paramMutableTreeNode.getParent() == this) {
      insert(paramMutableTreeNode, getChildCount() - 1);
    } else {
      insert(paramMutableTreeNode, getChildCount());
    } 
  }
  
  public boolean isNodeAncestor(TreeNode paramTreeNode) {
    if (paramTreeNode == null)
      return false; 
    DefaultMutableTreeNode defaultMutableTreeNode = this;
    TreeNode treeNode;
    do {
      if (defaultMutableTreeNode == paramTreeNode)
        return true; 
    } while ((treeNode = defaultMutableTreeNode.getParent()) != null);
    return false;
  }
  
  public boolean isNodeDescendant(DefaultMutableTreeNode paramDefaultMutableTreeNode) { return (paramDefaultMutableTreeNode == null) ? false : paramDefaultMutableTreeNode.isNodeAncestor(this); }
  
  public TreeNode getSharedAncestor(DefaultMutableTreeNode paramDefaultMutableTreeNode) {
    TreeNode treeNode2;
    TreeNode treeNode1;
    int k;
    if (paramDefaultMutableTreeNode == this)
      return this; 
    if (paramDefaultMutableTreeNode == null)
      return null; 
    int i = getLevel();
    int j = paramDefaultMutableTreeNode.getLevel();
    if (j > i) {
      k = j - i;
      treeNode1 = paramDefaultMutableTreeNode;
      treeNode2 = this;
    } else {
      k = i - j;
      treeNode1 = this;
      treeNode2 = paramDefaultMutableTreeNode;
    } 
    while (k > 0) {
      treeNode1 = treeNode1.getParent();
      k--;
    } 
    do {
      if (treeNode1 == treeNode2)
        return treeNode1; 
      treeNode1 = treeNode1.getParent();
      treeNode2 = treeNode2.getParent();
    } while (treeNode1 != null);
    if (treeNode1 != null || treeNode2 != null)
      throw new Error("nodes should be null"); 
    return null;
  }
  
  public boolean isNodeRelated(DefaultMutableTreeNode paramDefaultMutableTreeNode) { return (paramDefaultMutableTreeNode != null && getRoot() == paramDefaultMutableTreeNode.getRoot()); }
  
  public int getDepth() {
    Object object = null;
    Enumeration enumeration = breadthFirstEnumeration();
    while (enumeration.hasMoreElements())
      object = enumeration.nextElement(); 
    if (object == null)
      throw new Error("nodes should be null"); 
    return ((DefaultMutableTreeNode)object).getLevel() - getLevel();
  }
  
  public int getLevel() {
    byte b = 0;
    DefaultMutableTreeNode defaultMutableTreeNode = this;
    TreeNode treeNode;
    while ((treeNode = defaultMutableTreeNode.getParent()) != null)
      b++; 
    return b;
  }
  
  public TreeNode[] getPath() { return getPathToRoot(this, 0); }
  
  protected TreeNode[] getPathToRoot(TreeNode paramTreeNode, int paramInt) {
    TreeNode[] arrayOfTreeNode;
    if (paramTreeNode == null) {
      if (paramInt == 0)
        return null; 
      arrayOfTreeNode = new TreeNode[paramInt];
    } else {
      arrayOfTreeNode = getPathToRoot(paramTreeNode.getParent(), ++paramInt);
      arrayOfTreeNode[arrayOfTreeNode.length - paramInt] = paramTreeNode;
    } 
    return arrayOfTreeNode;
  }
  
  public Object[] getUserObjectPath() {
    TreeNode[] arrayOfTreeNode = getPath();
    Object[] arrayOfObject = new Object[arrayOfTreeNode.length];
    for (byte b = 0; b < arrayOfTreeNode.length; b++)
      arrayOfObject[b] = ((DefaultMutableTreeNode)arrayOfTreeNode[b]).getUserObject(); 
    return arrayOfObject;
  }
  
  public TreeNode getRoot() {
    DefaultMutableTreeNode defaultMutableTreeNode;
    TreeNode treeNode = this;
    do {
      defaultMutableTreeNode = treeNode;
      treeNode = treeNode.getParent();
    } while (treeNode != null);
    return defaultMutableTreeNode;
  }
  
  public boolean isRoot() { return (getParent() == null); }
  
  public DefaultMutableTreeNode getNextNode() {
    if (getChildCount() == 0) {
      DefaultMutableTreeNode defaultMutableTreeNode = getNextSibling();
      if (defaultMutableTreeNode == null)
        for (DefaultMutableTreeNode defaultMutableTreeNode1 = (DefaultMutableTreeNode)getParent();; defaultMutableTreeNode1 = (DefaultMutableTreeNode)defaultMutableTreeNode1.getParent()) {
          if (defaultMutableTreeNode1 == null)
            return null; 
          defaultMutableTreeNode = defaultMutableTreeNode1.getNextSibling();
          if (defaultMutableTreeNode != null)
            return defaultMutableTreeNode; 
        }  
      return defaultMutableTreeNode;
    } 
    return (DefaultMutableTreeNode)getChildAt(0);
  }
  
  public DefaultMutableTreeNode getPreviousNode() {
    DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    if (defaultMutableTreeNode2 == null)
      return null; 
    DefaultMutableTreeNode defaultMutableTreeNode1 = getPreviousSibling();
    return (defaultMutableTreeNode1 != null) ? ((defaultMutableTreeNode1.getChildCount() == 0) ? defaultMutableTreeNode1 : defaultMutableTreeNode1.getLastLeaf()) : defaultMutableTreeNode2;
  }
  
  public Enumeration preorderEnumeration() { return new PreorderEnumeration(this); }
  
  public Enumeration postorderEnumeration() { return new PostorderEnumeration(this); }
  
  public Enumeration breadthFirstEnumeration() { return new BreadthFirstEnumeration(this); }
  
  public Enumeration depthFirstEnumeration() { return postorderEnumeration(); }
  
  public Enumeration pathFromAncestorEnumeration(TreeNode paramTreeNode) { return new PathBetweenNodesEnumeration(paramTreeNode, this); }
  
  public boolean isNodeChild(TreeNode paramTreeNode) {
    boolean bool;
    if (paramTreeNode == null) {
      bool = false;
    } else if (getChildCount() == 0) {
      bool = false;
    } else {
      bool = (paramTreeNode.getParent() == this);
    } 
    return bool;
  }
  
  public TreeNode getFirstChild() {
    if (getChildCount() == 0)
      throw new NoSuchElementException("node has no children"); 
    return getChildAt(0);
  }
  
  public TreeNode getLastChild() {
    if (getChildCount() == 0)
      throw new NoSuchElementException("node has no children"); 
    return getChildAt(getChildCount() - 1);
  }
  
  public TreeNode getChildAfter(TreeNode paramTreeNode) {
    if (paramTreeNode == null)
      throw new IllegalArgumentException("argument is null"); 
    int i = getIndex(paramTreeNode);
    if (i == -1)
      throw new IllegalArgumentException("node is not a child"); 
    return (i < getChildCount() - 1) ? getChildAt(i + 1) : null;
  }
  
  public TreeNode getChildBefore(TreeNode paramTreeNode) {
    if (paramTreeNode == null)
      throw new IllegalArgumentException("argument is null"); 
    int i = getIndex(paramTreeNode);
    if (i == -1)
      throw new IllegalArgumentException("argument is not a child"); 
    return (i > 0) ? getChildAt(i - 1) : null;
  }
  
  public boolean isNodeSibling(TreeNode paramTreeNode) {
    boolean bool;
    if (paramTreeNode == null) {
      bool = false;
    } else if (paramTreeNode == this) {
      bool = true;
    } else {
      TreeNode treeNode = getParent();
      bool = (treeNode != null && treeNode == paramTreeNode.getParent());
      if (bool && !((DefaultMutableTreeNode)getParent()).isNodeChild(paramTreeNode))
        throw new Error("sibling has different parent"); 
    } 
    return bool;
  }
  
  public int getSiblingCount() {
    TreeNode treeNode = getParent();
    return (treeNode == null) ? 1 : treeNode.getChildCount();
  }
  
  public DefaultMutableTreeNode getNextSibling() {
    DefaultMutableTreeNode defaultMutableTreeNode1;
    DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    if (defaultMutableTreeNode2 == null) {
      defaultMutableTreeNode1 = null;
    } else {
      defaultMutableTreeNode1 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getChildAfter(this);
    } 
    if (defaultMutableTreeNode1 != null && !isNodeSibling(defaultMutableTreeNode1))
      throw new Error("child of parent is not a sibling"); 
    return defaultMutableTreeNode1;
  }
  
  public DefaultMutableTreeNode getPreviousSibling() {
    DefaultMutableTreeNode defaultMutableTreeNode1;
    DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    if (defaultMutableTreeNode2 == null) {
      defaultMutableTreeNode1 = null;
    } else {
      defaultMutableTreeNode1 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getChildBefore(this);
    } 
    if (defaultMutableTreeNode1 != null && !isNodeSibling(defaultMutableTreeNode1))
      throw new Error("child of parent is not a sibling"); 
    return defaultMutableTreeNode1;
  }
  
  public boolean isLeaf() { return (getChildCount() == 0); }
  
  public DefaultMutableTreeNode getFirstLeaf() {
    DefaultMutableTreeNode defaultMutableTreeNode;
    for (defaultMutableTreeNode = this; !defaultMutableTreeNode.isLeaf(); defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode.getFirstChild());
    return defaultMutableTreeNode;
  }
  
  public DefaultMutableTreeNode getLastLeaf() {
    DefaultMutableTreeNode defaultMutableTreeNode;
    for (defaultMutableTreeNode = this; !defaultMutableTreeNode.isLeaf(); defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode.getLastChild());
    return defaultMutableTreeNode;
  }
  
  public DefaultMutableTreeNode getNextLeaf() {
    DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    if (defaultMutableTreeNode2 == null)
      return null; 
    DefaultMutableTreeNode defaultMutableTreeNode1 = getNextSibling();
    return (defaultMutableTreeNode1 != null) ? defaultMutableTreeNode1.getFirstLeaf() : defaultMutableTreeNode2.getNextLeaf();
  }
  
  public DefaultMutableTreeNode getPreviousLeaf() {
    DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    if (defaultMutableTreeNode2 == null)
      return null; 
    DefaultMutableTreeNode defaultMutableTreeNode1 = getPreviousSibling();
    return (defaultMutableTreeNode1 != null) ? defaultMutableTreeNode1.getLastLeaf() : defaultMutableTreeNode2.getPreviousLeaf();
  }
  
  public int getLeafCount() {
    byte b = 0;
    Enumeration enumeration = breadthFirstEnumeration();
    while (enumeration.hasMoreElements()) {
      TreeNode treeNode = (TreeNode)enumeration.nextElement();
      if (treeNode.isLeaf())
        b++; 
    } 
    if (b < 1)
      throw new Error("tree has zero leaves"); 
    return b;
  }
  
  public String toString() { return (this.userObject == null) ? "" : this.userObject.toString(); }
  
  public Object clone() {
    DefaultMutableTreeNode defaultMutableTreeNode;
    try {
      defaultMutableTreeNode = (DefaultMutableTreeNode)super.clone();
      defaultMutableTreeNode.children = null;
      defaultMutableTreeNode.parent = null;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new Error(cloneNotSupportedException.toString());
    } 
    return defaultMutableTreeNode;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Object[] arrayOfObject;
    paramObjectOutputStream.defaultWriteObject();
    if (this.userObject != null && this.userObject instanceof Serializable) {
      arrayOfObject = new Object[2];
      arrayOfObject[0] = "userObject";
      arrayOfObject[1] = this.userObject;
    } else {
      arrayOfObject = new Object[0];
    } 
    paramObjectOutputStream.writeObject(arrayOfObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
    if (arrayOfObject.length > 0 && arrayOfObject[0].equals("userObject"))
      this.userObject = arrayOfObject[1]; 
  }
  
  final class BreadthFirstEnumeration extends Object implements Enumeration<TreeNode> {
    protected Queue queue;
    
    public BreadthFirstEnumeration(TreeNode param1TreeNode) {
      Vector vector = new Vector(1);
      vector.addElement(param1TreeNode);
      this.queue = new Queue();
      this.queue.enqueue(vector.elements());
    }
    
    public boolean hasMoreElements() { return (!this.queue.isEmpty() && ((Enumeration)this.queue.firstObject()).hasMoreElements()); }
    
    public TreeNode nextElement() {
      Enumeration enumeration1 = (Enumeration)this.queue.firstObject();
      TreeNode treeNode = (TreeNode)enumeration1.nextElement();
      Enumeration enumeration2 = treeNode.children();
      if (!enumeration1.hasMoreElements())
        this.queue.dequeue(); 
      if (enumeration2.hasMoreElements())
        this.queue.enqueue(enumeration2); 
      return treeNode;
    }
    
    final class Queue {
      QNode head;
      
      QNode tail;
      
      public void enqueue(Object param2Object) {
        if (this.head == null) {
          this.head = this.tail = new QNode(param2Object, null);
        } else {
          this.tail.next = new QNode(param2Object, null);
          this.tail = this.tail.next;
        } 
      }
      
      public Object dequeue() {
        if (this.head == null)
          throw new NoSuchElementException("No more elements"); 
        Object object = this.head.object;
        QNode qNode = this.head;
        this.head = this.head.next;
        if (this.head == null) {
          this.tail = null;
        } else {
          qNode.next = null;
        } 
        return object;
      }
      
      public Object firstObject() {
        if (this.head == null)
          throw new NoSuchElementException("No more elements"); 
        return this.head.object;
      }
      
      public boolean isEmpty() { return (this.head == null); }
      
      final class QNode {
        public Object object;
        
        public QNode next;
        
        public QNode(Object param3Object, QNode param3QNode) {
          this.object = param3Object;
          this.next = param3QNode;
        }
      }
    }
  }
  
  final class PathBetweenNodesEnumeration extends Object implements Enumeration<TreeNode> {
    protected Stack<TreeNode> stack;
    
    public PathBetweenNodesEnumeration(TreeNode param1TreeNode1, TreeNode param1TreeNode2) {
      if (param1TreeNode1 == null || param1TreeNode2 == null)
        throw new IllegalArgumentException("argument is null"); 
      this.stack = new Stack();
      this.stack.push(param1TreeNode2);
      TreeNode treeNode = param1TreeNode2;
      while (treeNode != param1TreeNode1) {
        treeNode = treeNode.getParent();
        if (treeNode == null && param1TreeNode2 != param1TreeNode1)
          throw new IllegalArgumentException("node " + param1TreeNode1 + " is not an ancestor of " + param1TreeNode2); 
        this.stack.push(treeNode);
      } 
    }
    
    public boolean hasMoreElements() { return (this.stack.size() > 0); }
    
    public TreeNode nextElement() {
      try {
        return (TreeNode)this.stack.pop();
      } catch (EmptyStackException emptyStackException) {
        throw new NoSuchElementException("No more elements");
      } 
    }
  }
  
  final class PostorderEnumeration extends Object implements Enumeration<TreeNode> {
    protected TreeNode root;
    
    protected Enumeration<TreeNode> children;
    
    protected Enumeration<TreeNode> subtree;
    
    public PostorderEnumeration(TreeNode param1TreeNode) {
      this.root = param1TreeNode;
      this.children = this.root.children();
      this.subtree = DefaultMutableTreeNode.EMPTY_ENUMERATION;
    }
    
    public boolean hasMoreElements() { return (this.root != null); }
    
    public TreeNode nextElement() {
      TreeNode treeNode;
      if (this.subtree.hasMoreElements()) {
        treeNode = (TreeNode)this.subtree.nextElement();
      } else if (this.children.hasMoreElements()) {
        this.subtree = new PostorderEnumeration(DefaultMutableTreeNode.this, (TreeNode)this.children.nextElement());
        treeNode = (TreeNode)this.subtree.nextElement();
      } else {
        treeNode = this.root;
        this.root = null;
      } 
      return treeNode;
    }
  }
  
  private final class PreorderEnumeration extends Object implements Enumeration<TreeNode> {
    private final Stack<Enumeration> stack = new Stack();
    
    public PreorderEnumeration(TreeNode param1TreeNode) {
      Vector vector = new Vector(1);
      vector.addElement(param1TreeNode);
      this.stack.push(vector.elements());
    }
    
    public boolean hasMoreElements() { return (!this.stack.empty() && ((Enumeration)this.stack.peek()).hasMoreElements()); }
    
    public TreeNode nextElement() {
      Enumeration enumeration1 = (Enumeration)this.stack.peek();
      TreeNode treeNode = (TreeNode)enumeration1.nextElement();
      Enumeration enumeration2 = treeNode.children();
      if (!enumeration1.hasMoreElements())
        this.stack.pop(); 
      if (enumeration2.hasMoreElements())
        this.stack.push(enumeration2); 
      return treeNode;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\DefaultMutableTreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */