package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DeepNodeListImpl implements NodeList {
  protected NodeImpl rootNode;
  
  protected String tagName;
  
  protected int changes = 0;
  
  protected Vector nodes;
  
  protected String nsName;
  
  protected boolean enableNS = false;
  
  public DeepNodeListImpl(NodeImpl paramNodeImpl, String paramString) {
    this.rootNode = paramNodeImpl;
    this.tagName = paramString;
    this.nodes = new Vector();
  }
  
  public DeepNodeListImpl(NodeImpl paramNodeImpl, String paramString1, String paramString2) {
    this(paramNodeImpl, paramString2);
    this.nsName = (paramString1 != null && !paramString1.equals("")) ? paramString1 : null;
    this.enableNS = true;
  }
  
  public int getLength() {
    item(2147483647);
    return this.nodes.size();
  }
  
  public Node item(int paramInt) {
    Node node;
    if (this.rootNode.changes() != this.changes) {
      this.nodes = new Vector();
      this.changes = this.rootNode.changes();
    } 
    if (paramInt < this.nodes.size())
      return (Node)this.nodes.elementAt(paramInt); 
    if (this.nodes.size() == 0) {
      node = this.rootNode;
    } else {
      node = (NodeImpl)this.nodes.lastElement();
    } 
    while (node != null && paramInt >= this.nodes.size()) {
      node = nextMatchingElementAfter(node);
      if (node != null)
        this.nodes.addElement(node); 
    } 
    return node;
  }
  
  protected Node nextMatchingElementAfter(Node paramNode) {
    while (paramNode != null) {
      if (paramNode.hasChildNodes()) {
        paramNode = paramNode.getFirstChild();
      } else {
        Node node;
        if (paramNode != this.rootNode && null != (node = paramNode.getNextSibling())) {
          paramNode = node;
        } else {
          node = null;
          while (paramNode != this.rootNode) {
            node = paramNode.getNextSibling();
            if (node != null)
              break; 
            paramNode = paramNode.getParentNode();
          } 
          paramNode = node;
        } 
      } 
      if (paramNode != this.rootNode && paramNode != null && paramNode.getNodeType() == 1) {
        if (!this.enableNS) {
          if (this.tagName.equals("*") || ((ElementImpl)paramNode).getTagName().equals(this.tagName))
            return paramNode; 
          continue;
        } 
        if (this.tagName.equals("*")) {
          if (this.nsName != null && this.nsName.equals("*"))
            return paramNode; 
          ElementImpl elementImpl1 = (ElementImpl)paramNode;
          if ((this.nsName == null && elementImpl1.getNamespaceURI() == null) || (this.nsName != null && this.nsName.equals(elementImpl1.getNamespaceURI())))
            return paramNode; 
          continue;
        } 
        ElementImpl elementImpl = (ElementImpl)paramNode;
        if (elementImpl.getLocalName() != null && elementImpl.getLocalName().equals(this.tagName)) {
          if (this.nsName != null && this.nsName.equals("*"))
            return paramNode; 
          if ((this.nsName == null && elementImpl.getNamespaceURI() == null) || (this.nsName != null && this.nsName.equals(elementImpl.getNamespaceURI())))
            return paramNode; 
        } 
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeepNodeListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */