package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class TreeWalkerImpl implements TreeWalker {
  private boolean fEntityReferenceExpansion = false;
  
  int fWhatToShow = -1;
  
  NodeFilter fNodeFilter;
  
  Node fCurrentNode;
  
  Node fRoot;
  
  public TreeWalkerImpl(Node paramNode, int paramInt, NodeFilter paramNodeFilter, boolean paramBoolean) {
    this.fCurrentNode = paramNode;
    this.fRoot = paramNode;
    this.fWhatToShow = paramInt;
    this.fNodeFilter = paramNodeFilter;
    this.fEntityReferenceExpansion = paramBoolean;
  }
  
  public Node getRoot() { return this.fRoot; }
  
  public int getWhatToShow() { return this.fWhatToShow; }
  
  public void setWhatShow(int paramInt) { this.fWhatToShow = paramInt; }
  
  public NodeFilter getFilter() { return this.fNodeFilter; }
  
  public boolean getExpandEntityReferences() { return this.fEntityReferenceExpansion; }
  
  public Node getCurrentNode() { return this.fCurrentNode; }
  
  public void setCurrentNode(Node paramNode) {
    if (paramNode == null) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, str);
    } 
    this.fCurrentNode = paramNode;
  }
  
  public Node parentNode() {
    if (this.fCurrentNode == null)
      return null; 
    Node node = getParentNode(this.fCurrentNode);
    if (node != null)
      this.fCurrentNode = node; 
    return node;
  }
  
  public Node firstChild() {
    if (this.fCurrentNode == null)
      return null; 
    Node node = getFirstChild(this.fCurrentNode);
    if (node != null)
      this.fCurrentNode = node; 
    return node;
  }
  
  public Node lastChild() {
    if (this.fCurrentNode == null)
      return null; 
    Node node = getLastChild(this.fCurrentNode);
    if (node != null)
      this.fCurrentNode = node; 
    return node;
  }
  
  public Node previousSibling() {
    if (this.fCurrentNode == null)
      return null; 
    Node node = getPreviousSibling(this.fCurrentNode);
    if (node != null)
      this.fCurrentNode = node; 
    return node;
  }
  
  public Node nextSibling() {
    if (this.fCurrentNode == null)
      return null; 
    Node node = getNextSibling(this.fCurrentNode);
    if (node != null)
      this.fCurrentNode = node; 
    return node;
  }
  
  public Node previousNode() {
    if (this.fCurrentNode == null)
      return null; 
    Node node1 = getPreviousSibling(this.fCurrentNode);
    if (node1 == null) {
      node1 = getParentNode(this.fCurrentNode);
      if (node1 != null) {
        this.fCurrentNode = node1;
        return this.fCurrentNode;
      } 
      return null;
    } 
    Node node2 = getLastChild(node1);
    Node node3 = node2;
    while (node2 != null) {
      node3 = node2;
      node2 = getLastChild(node3);
    } 
    node2 = node3;
    if (node2 != null) {
      this.fCurrentNode = node2;
      return this.fCurrentNode;
    } 
    if (node1 != null) {
      this.fCurrentNode = node1;
      return this.fCurrentNode;
    } 
    return null;
  }
  
  public Node nextNode() {
    if (this.fCurrentNode == null)
      return null; 
    Node node1 = getFirstChild(this.fCurrentNode);
    if (node1 != null) {
      this.fCurrentNode = node1;
      return node1;
    } 
    node1 = getNextSibling(this.fCurrentNode);
    if (node1 != null) {
      this.fCurrentNode = node1;
      return node1;
    } 
    for (Node node2 = getParentNode(this.fCurrentNode); node2 != null; node2 = getParentNode(node2)) {
      node1 = getNextSibling(node2);
      if (node1 != null) {
        this.fCurrentNode = node1;
        return node1;
      } 
    } 
    return null;
  }
  
  Node getParentNode(Node paramNode) {
    if (paramNode == null || paramNode == this.fRoot)
      return null; 
    Node node = paramNode.getParentNode();
    if (node == null)
      return null; 
    short s = acceptNode(node);
    return (s == 1) ? node : getParentNode(node);
  }
  
  Node getNextSibling(Node paramNode) { return getNextSibling(paramNode, this.fRoot); }
  
  Node getNextSibling(Node paramNode1, Node paramNode2) {
    if (paramNode1 == null || paramNode1 == paramNode2)
      return null; 
    Node node = paramNode1.getNextSibling();
    if (node == null) {
      node = paramNode1.getParentNode();
      if (node == null || node == paramNode2)
        return null; 
      short s1 = acceptNode(node);
      return (s1 == 3) ? getNextSibling(node, paramNode2) : null;
    } 
    short s = acceptNode(node);
    if (s == 1)
      return node; 
    if (s == 3) {
      Node node1 = getFirstChild(node);
      return (node1 == null) ? getNextSibling(node, paramNode2) : node1;
    } 
    return getNextSibling(node, paramNode2);
  }
  
  Node getPreviousSibling(Node paramNode) { return getPreviousSibling(paramNode, this.fRoot); }
  
  Node getPreviousSibling(Node paramNode1, Node paramNode2) {
    if (paramNode1 == null || paramNode1 == paramNode2)
      return null; 
    Node node = paramNode1.getPreviousSibling();
    if (node == null) {
      node = paramNode1.getParentNode();
      if (node == null || node == paramNode2)
        return null; 
      short s1 = acceptNode(node);
      return (s1 == 3) ? getPreviousSibling(node, paramNode2) : null;
    } 
    short s = acceptNode(node);
    if (s == 1)
      return node; 
    if (s == 3) {
      Node node1 = getLastChild(node);
      return (node1 == null) ? getPreviousSibling(node, paramNode2) : node1;
    } 
    return getPreviousSibling(node, paramNode2);
  }
  
  Node getFirstChild(Node paramNode) {
    if (paramNode == null)
      return null; 
    if (!this.fEntityReferenceExpansion && paramNode.getNodeType() == 5)
      return null; 
    Node node = paramNode.getFirstChild();
    if (node == null)
      return null; 
    short s = acceptNode(node);
    if (s == 1)
      return node; 
    if (s == 3 && node.hasChildNodes()) {
      Node node1 = getFirstChild(node);
      return (node1 == null) ? getNextSibling(node, paramNode) : node1;
    } 
    return getNextSibling(node, paramNode);
  }
  
  Node getLastChild(Node paramNode) {
    if (paramNode == null)
      return null; 
    if (!this.fEntityReferenceExpansion && paramNode.getNodeType() == 5)
      return null; 
    Node node = paramNode.getLastChild();
    if (node == null)
      return null; 
    short s = acceptNode(node);
    if (s == 1)
      return node; 
    if (s == 3 && node.hasChildNodes()) {
      Node node1 = getLastChild(node);
      return (node1 == null) ? getPreviousSibling(node, paramNode) : node1;
    } 
    return getPreviousSibling(node, paramNode);
  }
  
  short acceptNode(Node paramNode) { return (this.fNodeFilter == null) ? (((this.fWhatToShow & 1 << paramNode.getNodeType() - 1) != 0) ? 1 : 3) : (((this.fWhatToShow & 1 << paramNode.getNodeType() - 1) != 0) ? this.fNodeFilter.acceptNode(paramNode) : 3); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\TreeWalkerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */