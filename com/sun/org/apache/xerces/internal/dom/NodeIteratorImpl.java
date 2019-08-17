package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class NodeIteratorImpl implements NodeIterator {
  private DocumentImpl fDocument;
  
  private Node fRoot;
  
  private int fWhatToShow = -1;
  
  private NodeFilter fNodeFilter;
  
  private boolean fDetach = false;
  
  private Node fCurrentNode;
  
  private boolean fForward = true;
  
  private boolean fEntityReferenceExpansion;
  
  public NodeIteratorImpl(DocumentImpl paramDocumentImpl, Node paramNode, int paramInt, NodeFilter paramNodeFilter, boolean paramBoolean) {
    this.fDocument = paramDocumentImpl;
    this.fRoot = paramNode;
    this.fCurrentNode = null;
    this.fWhatToShow = paramInt;
    this.fNodeFilter = paramNodeFilter;
    this.fEntityReferenceExpansion = paramBoolean;
  }
  
  public Node getRoot() { return this.fRoot; }
  
  public int getWhatToShow() { return this.fWhatToShow; }
  
  public NodeFilter getFilter() { return this.fNodeFilter; }
  
  public boolean getExpandEntityReferences() { return this.fEntityReferenceExpansion; }
  
  public Node nextNode() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    if (this.fRoot == null)
      return null; 
    Node node = this.fCurrentNode;
    boolean bool = false;
    while (!bool) {
      if (!this.fForward && node != null) {
        node = this.fCurrentNode;
      } else if (!this.fEntityReferenceExpansion && node != null && node.getNodeType() == 5) {
        node = nextNode(node, false);
      } else {
        node = nextNode(node, true);
      } 
      this.fForward = true;
      if (node == null)
        return null; 
      bool = acceptNode(node);
      if (bool) {
        this.fCurrentNode = node;
        return this.fCurrentNode;
      } 
    } 
    return null;
  }
  
  public Node previousNode() {
    if (this.fDetach)
      throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null)); 
    if (this.fRoot == null || this.fCurrentNode == null)
      return null; 
    Node node = this.fCurrentNode;
    boolean bool = false;
    while (!bool) {
      if (this.fForward && node != null) {
        node = this.fCurrentNode;
      } else {
        node = previousNode(node);
      } 
      this.fForward = false;
      if (node == null)
        return null; 
      bool = acceptNode(node);
      if (bool) {
        this.fCurrentNode = node;
        return this.fCurrentNode;
      } 
    } 
    return null;
  }
  
  boolean acceptNode(Node paramNode) { return (this.fNodeFilter == null) ? (((this.fWhatToShow & 1 << paramNode.getNodeType() - 1) != 0)) : (((this.fWhatToShow & 1 << paramNode.getNodeType() - 1) != 0 && this.fNodeFilter.acceptNode(paramNode) == 1)); }
  
  Node matchNodeOrParent(Node paramNode) {
    if (this.fCurrentNode == null)
      return null; 
    for (Node node = this.fCurrentNode; node != this.fRoot; node = node.getParentNode()) {
      if (paramNode == node)
        return node; 
    } 
    return null;
  }
  
  Node nextNode(Node paramNode, boolean paramBoolean) {
    if (paramNode == null)
      return this.fRoot; 
    if (paramBoolean && paramNode.hasChildNodes())
      return paramNode.getFirstChild(); 
    if (paramNode == this.fRoot)
      return null; 
    Node node1 = paramNode.getNextSibling();
    if (node1 != null)
      return node1; 
    for (Node node2 = paramNode.getParentNode(); node2 != null && node2 != this.fRoot; node2 = node2.getParentNode()) {
      node1 = node2.getNextSibling();
      if (node1 != null)
        return node1; 
    } 
    return null;
  }
  
  Node previousNode(Node paramNode) {
    if (paramNode == this.fRoot)
      return null; 
    Node node = paramNode.getPreviousSibling();
    if (node == null)
      return paramNode.getParentNode(); 
    if (node.hasChildNodes() && (this.fEntityReferenceExpansion || node == null || node.getNodeType() != 5))
      while (node.hasChildNodes())
        node = node.getLastChild();  
    return node;
  }
  
  public void removeNode(Node paramNode) {
    if (paramNode == null)
      return; 
    Node node = matchNodeOrParent(paramNode);
    if (node == null)
      return; 
    if (this.fForward) {
      this.fCurrentNode = previousNode(node);
    } else {
      Node node1 = nextNode(node, false);
      if (node1 != null) {
        this.fCurrentNode = node1;
      } else {
        this.fCurrentNode = previousNode(node);
        this.fForward = true;
      } 
    } 
  }
  
  public void detach() {
    this.fDetach = true;
    this.fDocument.removeNodeIterator(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\NodeIteratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */