package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.DOM2Helper;
import com.sun.org.apache.xpath.internal.axes.ContextNodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class NodeSet implements NodeList, NodeIterator, Cloneable, ContextNodeList {
  protected int m_next = 0;
  
  protected boolean m_mutable = true;
  
  protected boolean m_cacheNodes = true;
  
  private int m_last = 0;
  
  private int m_blocksize = 32;
  
  Node[] m_map;
  
  protected int m_firstFree = 0;
  
  private int m_mapSize = 0;
  
  public NodeSet() {}
  
  public NodeSet(int paramInt) {}
  
  public NodeSet(NodeList paramNodeList) {
    this(32);
    addNodes(paramNodeList);
  }
  
  public NodeSet(NodeSet paramNodeSet) {
    this(32);
    addNodes(paramNodeSet);
  }
  
  public NodeSet(NodeIterator paramNodeIterator) {
    this(32);
    addNodes(paramNodeIterator);
  }
  
  public NodeSet(Node paramNode) {
    this(32);
    addNode(paramNode);
  }
  
  public Node getRoot() { return null; }
  
  public NodeIterator cloneWithReset() throws CloneNotSupportedException {
    NodeSet nodeSet = (NodeSet)clone();
    nodeSet.reset();
    return nodeSet;
  }
  
  public void reset() { this.m_next = 0; }
  
  public int getWhatToShow() { return -17; }
  
  public NodeFilter getFilter() { return null; }
  
  public boolean getExpandEntityReferences() { return true; }
  
  public Node nextNode() {
    if (this.m_next < size()) {
      Node node = elementAt(this.m_next);
      this.m_next++;
      return node;
    } 
    return null;
  }
  
  public Node previousNode() {
    if (!this.m_cacheNodes)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_CANNOT_ITERATE", null)); 
    if (this.m_next - 1 > 0) {
      this.m_next--;
      return elementAt(this.m_next);
    } 
    return null;
  }
  
  public void detach() {}
  
  public boolean isFresh() { return (this.m_next == 0); }
  
  public void runTo(int paramInt) {
    if (!this.m_cacheNodes)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_CANNOT_INDEX", null)); 
    if (paramInt >= 0 && this.m_next < this.m_firstFree) {
      this.m_next = paramInt;
    } else {
      this.m_next = this.m_firstFree - 1;
    } 
  }
  
  public Node item(int paramInt) {
    runTo(paramInt);
    return elementAt(paramInt);
  }
  
  public int getLength() {
    runTo(-1);
    return size();
  }
  
  public void addNode(Node paramNode) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    addElement(paramNode);
  }
  
  public void insertNode(Node paramNode, int paramInt) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    insertElementAt(paramNode, paramInt);
  }
  
  public void removeNode(Node paramNode) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    removeElement(paramNode);
  }
  
  public void addNodes(NodeList paramNodeList) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    if (null != paramNodeList) {
      int i = paramNodeList.getLength();
      for (byte b = 0; b < i; b++) {
        Node node = paramNodeList.item(b);
        if (null != node)
          addElement(node); 
      } 
    } 
  }
  
  public void addNodes(NodeSet paramNodeSet) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    addNodes(paramNodeSet);
  }
  
  public void addNodes(NodeIterator paramNodeIterator) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    if (null != paramNodeIterator) {
      Node node;
      while (null != (node = paramNodeIterator.nextNode()))
        addElement(node); 
    } 
  }
  
  public void addNodesInDocOrder(NodeList paramNodeList, XPathContext paramXPathContext) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    int i = paramNodeList.getLength();
    for (byte b = 0; b < i; b++) {
      Node node = paramNodeList.item(b);
      if (null != node)
        addNodeInDocOrder(node, paramXPathContext); 
    } 
  }
  
  public void addNodesInDocOrder(NodeIterator paramNodeIterator, XPathContext paramXPathContext) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    Node node;
    while (null != (node = paramNodeIterator.nextNode()))
      addNodeInDocOrder(node, paramXPathContext); 
  }
  
  private boolean addNodesInDocOrder(int paramInt1, int paramInt2, int paramInt3, NodeList paramNodeList, XPathContext paramXPathContext) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    boolean bool = false;
    Node node = paramNodeList.item(paramInt3);
    int i;
    for (i = paramInt2; i >= paramInt1; i--) {
      Node node1 = elementAt(i);
      if (node1 == node) {
        i = -2;
        break;
      } 
      if (!DOM2Helper.isNodeAfter(node, node1)) {
        insertElementAt(node, i + 1);
        if (--paramInt3 > 0) {
          boolean bool1 = addNodesInDocOrder(0, i, paramInt3, paramNodeList, paramXPathContext);
          if (!bool1)
            addNodesInDocOrder(i, size() - 1, paramInt3, paramNodeList, paramXPathContext); 
        } 
        break;
      } 
    } 
    if (i == -1)
      insertElementAt(node, 0); 
    return bool;
  }
  
  public int addNodeInDocOrder(Node paramNode, boolean paramBoolean, XPathContext paramXPathContext) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    int i = -1;
    if (paramBoolean) {
      int j = size();
      int k;
      for (k = j - 1; k >= 0; k--) {
        Node node = elementAt(k);
        if (node == paramNode) {
          k = -2;
          break;
        } 
        if (!DOM2Helper.isNodeAfter(paramNode, node))
          break; 
      } 
      if (k != -2) {
        i = k + 1;
        insertElementAt(paramNode, i);
      } 
    } else {
      i = size();
      boolean bool = false;
      for (byte b = 0; b < i; b++) {
        if (item(b).equals(paramNode)) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        addElement(paramNode); 
    } 
    return i;
  }
  
  public int addNodeInDocOrder(Node paramNode, XPathContext paramXPathContext) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    return addNodeInDocOrder(paramNode, true, paramXPathContext);
  }
  
  public int getCurrentPos() { return this.m_next; }
  
  public void setCurrentPos(int paramInt) {
    if (!this.m_cacheNodes)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_CANNOT_INDEX", null)); 
    this.m_next = paramInt;
  }
  
  public Node getCurrentNode() {
    if (!this.m_cacheNodes)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_CANNOT_INDEX", null)); 
    int i = this.m_next;
    Node node = (this.m_next < this.m_firstFree) ? elementAt(this.m_next) : null;
    this.m_next = i;
    return node;
  }
  
  public boolean getShouldCacheNodes() { return this.m_cacheNodes; }
  
  public void setShouldCacheNodes(boolean paramBoolean) {
    if (!isFresh())
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_CANNOT_CALL_SETSHOULDCACHENODE", null)); 
    this.m_cacheNodes = paramBoolean;
    this.m_mutable = true;
  }
  
  public int getLast() { return this.m_last; }
  
  public void setLast(int paramInt) { this.m_last = paramInt; }
  
  public Object clone() throws CloneNotSupportedException {
    NodeSet nodeSet = (NodeSet)super.clone();
    if (null != this.m_map && this.m_map == nodeSet.m_map) {
      nodeSet.m_map = new Node[this.m_map.length];
      System.arraycopy(this.m_map, 0, nodeSet.m_map, 0, this.m_map.length);
    } 
    return nodeSet;
  }
  
  public int size() { return this.m_firstFree; }
  
  public void addElement(Node paramNode) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    if (this.m_firstFree + 1 >= this.m_mapSize)
      if (null == this.m_map) {
        this.m_map = new Node[this.m_blocksize];
        this.m_mapSize = this.m_blocksize;
      } else {
        this.m_mapSize += this.m_blocksize;
        Node[] arrayOfNode = new Node[this.m_mapSize];
        System.arraycopy(this.m_map, 0, arrayOfNode, 0, this.m_firstFree + 1);
        this.m_map = arrayOfNode;
      }  
    this.m_map[this.m_firstFree] = paramNode;
    this.m_firstFree++;
  }
  
  public final void push(Node paramNode) {
    int i = this.m_firstFree;
    if (i + 1 >= this.m_mapSize)
      if (null == this.m_map) {
        this.m_map = new Node[this.m_blocksize];
        this.m_mapSize = this.m_blocksize;
      } else {
        this.m_mapSize += this.m_blocksize;
        Node[] arrayOfNode = new Node[this.m_mapSize];
        System.arraycopy(this.m_map, 0, arrayOfNode, 0, i + 1);
        this.m_map = arrayOfNode;
      }  
    this.m_map[i] = paramNode;
    this.m_firstFree = ++i;
  }
  
  public final Node pop() {
    this.m_firstFree--;
    Node node = this.m_map[this.m_firstFree];
    this.m_map[this.m_firstFree] = null;
    return node;
  }
  
  public final Node popAndTop() {
    this.m_firstFree--;
    this.m_map[this.m_firstFree] = null;
    return (this.m_firstFree == 0) ? null : this.m_map[this.m_firstFree - 1];
  }
  
  public final void popQuick() {
    this.m_firstFree--;
    this.m_map[this.m_firstFree] = null;
  }
  
  public final Node peepOrNull() { return (null != this.m_map && this.m_firstFree > 0) ? this.m_map[this.m_firstFree - 1] : null; }
  
  public final void pushPair(Node paramNode1, Node paramNode2) {
    if (null == this.m_map) {
      this.m_map = new Node[this.m_blocksize];
      this.m_mapSize = this.m_blocksize;
    } else if (this.m_firstFree + 2 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      Node[] arrayOfNode = new Node[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfNode, 0, this.m_firstFree);
      this.m_map = arrayOfNode;
    } 
    this.m_map[this.m_firstFree] = paramNode1;
    this.m_map[this.m_firstFree + 1] = paramNode2;
    this.m_firstFree += 2;
  }
  
  public final void popPair() {
    this.m_firstFree -= 2;
    this.m_map[this.m_firstFree] = null;
    this.m_map[this.m_firstFree + 1] = null;
  }
  
  public final void setTail(Node paramNode) { this.m_map[this.m_firstFree - 1] = paramNode; }
  
  public final void setTailSub1(Node paramNode) { this.m_map[this.m_firstFree - 2] = paramNode; }
  
  public final Node peepTail() { return this.m_map[this.m_firstFree - 1]; }
  
  public final Node peepTailSub1() { return this.m_map[this.m_firstFree - 2]; }
  
  public void insertElementAt(Node paramNode, int paramInt) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    if (null == this.m_map) {
      this.m_map = new Node[this.m_blocksize];
      this.m_mapSize = this.m_blocksize;
    } else if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      Node[] arrayOfNode = new Node[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfNode, 0, this.m_firstFree + 1);
      this.m_map = arrayOfNode;
    } 
    if (paramInt <= this.m_firstFree - 1)
      System.arraycopy(this.m_map, paramInt, this.m_map, paramInt + 1, this.m_firstFree - paramInt); 
    this.m_map[paramInt] = paramNode;
    this.m_firstFree++;
  }
  
  public void appendNodes(NodeSet paramNodeSet) {
    int i = paramNodeSet.size();
    if (null == this.m_map) {
      this.m_mapSize = i + this.m_blocksize;
      this.m_map = new Node[this.m_mapSize];
    } else if (this.m_firstFree + i >= this.m_mapSize) {
      this.m_mapSize += i + this.m_blocksize;
      Node[] arrayOfNode = new Node[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfNode, 0, this.m_firstFree + i);
      this.m_map = arrayOfNode;
    } 
    System.arraycopy(paramNodeSet.m_map, 0, this.m_map, this.m_firstFree, i);
    this.m_firstFree += i;
  }
  
  public void removeAllElements() {
    if (null == this.m_map)
      return; 
    for (byte b = 0; b < this.m_firstFree; b++)
      this.m_map[b] = null; 
    this.m_firstFree = 0;
  }
  
  public boolean removeElement(Node paramNode) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    if (null == this.m_map)
      return false; 
    for (int i = 0; i < this.m_firstFree; i++) {
      Node node = this.m_map[i];
      if (null != node && node.equals(paramNode)) {
        if (i < this.m_firstFree - 1)
          System.arraycopy(this.m_map, i + true, this.m_map, i, this.m_firstFree - i - 1); 
        this.m_firstFree--;
        this.m_map[this.m_firstFree] = null;
        return true;
      } 
    } 
    return false;
  }
  
  public void removeElementAt(int paramInt) {
    if (null == this.m_map)
      return; 
    if (paramInt >= this.m_firstFree)
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + this.m_firstFree); 
    if (paramInt < 0)
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    if (paramInt < this.m_firstFree - 1)
      System.arraycopy(this.m_map, paramInt + 1, this.m_map, paramInt, this.m_firstFree - paramInt - 1); 
    this.m_firstFree--;
    this.m_map[this.m_firstFree] = null;
  }
  
  public void setElementAt(Node paramNode, int paramInt) {
    if (!this.m_mutable)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null)); 
    if (null == this.m_map) {
      this.m_map = new Node[this.m_blocksize];
      this.m_mapSize = this.m_blocksize;
    } 
    this.m_map[paramInt] = paramNode;
  }
  
  public Node elementAt(int paramInt) { return (null == this.m_map) ? null : this.m_map[paramInt]; }
  
  public boolean contains(Node paramNode) {
    runTo(-1);
    if (null == this.m_map)
      return false; 
    for (byte b = 0; b < this.m_firstFree; b++) {
      Node node = this.m_map[b];
      if (null != node && node.equals(paramNode))
        return true; 
    } 
    return false;
  }
  
  public int indexOf(Node paramNode, int paramInt) {
    runTo(-1);
    if (null == this.m_map)
      return -1; 
    for (int i = paramInt; i < this.m_firstFree; i++) {
      Node node = this.m_map[i];
      if (null != node && node.equals(paramNode))
        return i; 
    } 
    return -1;
  }
  
  public int indexOf(Node paramNode) {
    runTo(-1);
    if (null == this.m_map)
      return -1; 
    for (byte b = 0; b < this.m_firstFree; b++) {
      Node node = this.m_map[b];
      if (null != node && node.equals(paramNode))
        return b; 
    } 
    return -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\NodeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */