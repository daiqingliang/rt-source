package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public abstract class ParentNode extends ChildNode {
  static final long serialVersionUID = 2815829867152120872L;
  
  protected CoreDocumentImpl ownerDocument;
  
  protected ChildNode firstChild = null;
  
  protected NodeListCache fNodeListCache = null;
  
  protected ParentNode(CoreDocumentImpl paramCoreDocumentImpl) {
    super(paramCoreDocumentImpl);
    this.ownerDocument = paramCoreDocumentImpl;
  }
  
  public ParentNode() {}
  
  public Node cloneNode(boolean paramBoolean) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    ParentNode parentNode = (ParentNode)super.cloneNode(paramBoolean);
    parentNode.ownerDocument = this.ownerDocument;
    parentNode.firstChild = null;
    parentNode.fNodeListCache = null;
    if (paramBoolean)
      for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling)
        parentNode.appendChild(childNode.cloneNode(true));  
    return parentNode;
  }
  
  public Document getOwnerDocument() { return this.ownerDocument; }
  
  CoreDocumentImpl ownerDocument() { return this.ownerDocument; }
  
  void setOwnerDocument(CoreDocumentImpl paramCoreDocumentImpl) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling)
      childNode.setOwnerDocument(paramCoreDocumentImpl); 
    super.setOwnerDocument(paramCoreDocumentImpl);
    this.ownerDocument = paramCoreDocumentImpl;
  }
  
  public boolean hasChildNodes() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return (this.firstChild != null);
  }
  
  public NodeList getChildNodes() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this;
  }
  
  public Node getFirstChild() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this.firstChild;
  }
  
  public Node getLastChild() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return lastChild();
  }
  
  final ChildNode lastChild() { return (this.firstChild != null) ? this.firstChild.previousSibling : null; }
  
  final void lastChild(ChildNode paramChildNode) {
    if (this.firstChild != null)
      this.firstChild.previousSibling = paramChildNode; 
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException { return internalInsertBefore(paramNode1, paramNode2, false); }
  
  Node internalInsertBefore(Node paramNode1, Node paramNode2, boolean paramBoolean) throws DOMException {
    boolean bool = this.ownerDocument.errorChecking;
    if (paramNode1.getNodeType() == 11) {
      if (bool)
        for (Node node = paramNode1.getFirstChild(); node != null; node = node.getNextSibling()) {
          if (!this.ownerDocument.isKidOK(this, node))
            throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); 
        }  
      while (paramNode1.hasChildNodes())
        insertBefore(paramNode1.getFirstChild(), paramNode2); 
      return paramNode1;
    } 
    if (paramNode1 == paramNode2) {
      paramNode2 = paramNode2.getNextSibling();
      removeChild(paramNode1);
      insertBefore(paramNode1, paramNode2);
      return paramNode1;
    } 
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (bool) {
      if (isReadOnly())
        throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
      if (paramNode1.getOwnerDocument() != this.ownerDocument && paramNode1 != this.ownerDocument)
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
      if (!this.ownerDocument.isKidOK(this, paramNode1))
        throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); 
      if (paramNode2 != null && paramNode2.getParentNode() != this)
        throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null)); 
      if (this.ownerDocument.ancestorChecking) {
        boolean bool1 = true;
        ParentNode parentNode = this;
        while (bool1 && parentNode != null) {
          bool1 = (paramNode1 != parentNode) ? 1 : 0;
          NodeImpl nodeImpl1 = parentNode.parentNode();
        } 
        if (!bool1)
          throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); 
      } 
    } 
    this.ownerDocument.insertingNode(this, paramBoolean);
    ChildNode childNode1 = (ChildNode)paramNode1;
    NodeImpl nodeImpl = childNode1.parentNode();
    if (nodeImpl != null)
      nodeImpl.removeChild(childNode1); 
    ChildNode childNode2 = (ChildNode)paramNode2;
    childNode1.ownerNode = this;
    childNode1.isOwned(true);
    if (this.firstChild == null) {
      this.firstChild = childNode1;
      childNode1.isFirstChild(true);
      childNode1.previousSibling = childNode1;
    } else if (childNode2 == null) {
      ChildNode childNode = this.firstChild.previousSibling;
      childNode.nextSibling = childNode1;
      childNode1.previousSibling = childNode;
      this.firstChild.previousSibling = childNode1;
    } else if (paramNode2 == this.firstChild) {
      this.firstChild.isFirstChild(false);
      childNode1.nextSibling = this.firstChild;
      childNode1.previousSibling = this.firstChild.previousSibling;
      this.firstChild.previousSibling = childNode1;
      this.firstChild = childNode1;
      childNode1.isFirstChild(true);
    } else {
      ChildNode childNode = childNode2.previousSibling;
      childNode1.nextSibling = childNode2;
      childNode.nextSibling = childNode1;
      childNode2.previousSibling = childNode1;
      childNode1.previousSibling = childNode;
    } 
    changed();
    if (this.fNodeListCache != null) {
      if (this.fNodeListCache.fLength != -1)
        this.fNodeListCache.fLength++; 
      if (this.fNodeListCache.fChildIndex != -1)
        if (this.fNodeListCache.fChild == childNode2) {
          this.fNodeListCache.fChild = childNode1;
        } else {
          this.fNodeListCache.fChildIndex = -1;
        }  
    } 
    this.ownerDocument.insertedNode(this, childNode1, paramBoolean);
    checkNormalizationAfterInsert(childNode1);
    return paramNode1;
  }
  
  public Node removeChild(Node paramNode) throws DOMException { return internalRemoveChild(paramNode, false); }
  
  Node internalRemoveChild(Node paramNode, boolean paramBoolean) throws DOMException {
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    if (coreDocumentImpl.errorChecking) {
      if (isReadOnly())
        throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
      if (paramNode != null && paramNode.getParentNode() != this)
        throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null)); 
    } 
    ChildNode childNode1 = (ChildNode)paramNode;
    coreDocumentImpl.removingNode(this, childNode1, paramBoolean);
    if (this.fNodeListCache != null) {
      if (this.fNodeListCache.fLength != -1)
        this.fNodeListCache.fLength--; 
      if (this.fNodeListCache.fChildIndex != -1)
        if (this.fNodeListCache.fChild == childNode1) {
          this.fNodeListCache.fChildIndex--;
          this.fNodeListCache.fChild = childNode1.previousSibling();
        } else {
          this.fNodeListCache.fChildIndex = -1;
        }  
    } 
    if (childNode1 == this.firstChild) {
      childNode1.isFirstChild(false);
      this.firstChild = childNode1.nextSibling;
      if (this.firstChild != null) {
        this.firstChild.isFirstChild(true);
        this.firstChild.previousSibling = childNode1.previousSibling;
      } 
    } else {
      ChildNode childNode3 = childNode1.previousSibling;
      ChildNode childNode4 = childNode1.nextSibling;
      childNode3.nextSibling = childNode4;
      if (childNode4 == null) {
        this.firstChild.previousSibling = childNode3;
      } else {
        childNode4.previousSibling = childNode3;
      } 
    } 
    ChildNode childNode2 = childNode1.previousSibling();
    childNode1.ownerNode = coreDocumentImpl;
    childNode1.isOwned(false);
    childNode1.nextSibling = null;
    childNode1.previousSibling = null;
    changed();
    coreDocumentImpl.removedNode(this, paramBoolean);
    checkNormalizationAfterRemove(childNode2);
    return childNode1;
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException {
    this.ownerDocument.replacingNode(this);
    internalInsertBefore(paramNode1, paramNode2, true);
    if (paramNode1 != paramNode2)
      internalRemoveChild(paramNode2, true); 
    this.ownerDocument.replacedNode(this);
    return paramNode2;
  }
  
  public String getTextContent() throws DOMException {
    Node node = getFirstChild();
    if (node != null) {
      Node node1 = node.getNextSibling();
      if (node1 == null)
        return hasTextContent(node) ? ((NodeImpl)node).getTextContent() : ""; 
      if (this.fBufferStr == null) {
        this.fBufferStr = new StringBuffer();
      } else {
        this.fBufferStr.setLength(0);
      } 
      getTextContent(this.fBufferStr);
      return this.fBufferStr.toString();
    } 
    return "";
  }
  
  void getTextContent(StringBuffer paramStringBuffer) throws DOMException {
    for (Node node = getFirstChild(); node != null; node = node.getNextSibling()) {
      if (hasTextContent(node))
        ((NodeImpl)node).getTextContent(paramStringBuffer); 
    } 
  }
  
  final boolean hasTextContent(Node paramNode) { return (paramNode.getNodeType() != 8 && paramNode.getNodeType() != 7 && (paramNode.getNodeType() != 3 || !((TextImpl)paramNode).isIgnorableWhitespace())); }
  
  public void setTextContent(String paramString) throws DOMException {
    Node node;
    while ((node = getFirstChild()) != null)
      removeChild(node); 
    if (paramString != null && paramString.length() != 0)
      appendChild(ownerDocument().createTextNode(paramString)); 
  }
  
  private int nodeListGetLength() {
    if (this.fNodeListCache == null) {
      if (this.firstChild == null)
        return 0; 
      if (this.firstChild == lastChild())
        return 1; 
      this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
    } 
    if (this.fNodeListCache.fLength == -1) {
      ChildNode childNode;
      byte b;
      if (this.fNodeListCache.fChildIndex != -1 && this.fNodeListCache.fChild != null) {
        b = this.fNodeListCache.fChildIndex;
        childNode = this.fNodeListCache.fChild;
      } else {
        childNode = this.firstChild;
        b = 0;
      } 
      while (childNode != null) {
        b++;
        childNode = childNode.nextSibling;
      } 
      this.fNodeListCache.fLength = b;
    } 
    return this.fNodeListCache.fLength;
  }
  
  public int getLength() { return nodeListGetLength(); }
  
  private Node nodeListItem(int paramInt) {
    if (this.fNodeListCache == null) {
      if (this.firstChild == lastChild())
        return (paramInt == 0) ? this.firstChild : null; 
      this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
    } 
    int i = this.fNodeListCache.fChildIndex;
    ChildNode childNode = this.fNodeListCache.fChild;
    boolean bool = true;
    if (i != -1 && childNode != null) {
      bool = false;
      if (i < paramInt) {
        while (i < paramInt && childNode != null) {
          i++;
          childNode = childNode.nextSibling;
        } 
      } else if (i > paramInt) {
        while (i > paramInt && childNode != null) {
          i--;
          childNode = childNode.previousSibling();
        } 
      } 
    } else {
      if (paramInt < 0)
        return null; 
      childNode = this.firstChild;
      for (i = 0; i < paramInt && childNode != null; i++)
        childNode = childNode.nextSibling; 
    } 
    if (!bool && (childNode == this.firstChild || childNode == lastChild())) {
      this.fNodeListCache.fChildIndex = -1;
      this.fNodeListCache.fChild = null;
      this.ownerDocument.freeNodeListCache(this.fNodeListCache);
    } else {
      this.fNodeListCache.fChildIndex = i;
      this.fNodeListCache.fChild = childNode;
    } 
    return childNode;
  }
  
  public Node item(int paramInt) { return nodeListItem(paramInt); }
  
  protected final NodeList getChildNodesUnoptimized() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return new NodeList() {
        public int getLength() { return ParentNode.this.nodeListGetLength(); }
        
        public Node item(int param1Int) { return ParentNode.this.nodeListItem(param1Int); }
      };
  }
  
  public void normalize() {
    if (isNormalized())
      return; 
    if (needsSyncChildren())
      synchronizeChildren(); 
    for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling)
      childNode.normalize(); 
    isNormalized(true);
  }
  
  public boolean isEqualNode(Node paramNode) {
    if (!super.isEqualNode(paramNode))
      return false; 
    Node node1 = getFirstChild();
    Node node2;
    for (node2 = paramNode.getFirstChild(); node1 != null && node2 != null; node2 = node2.getNextSibling()) {
      if (!((NodeImpl)node1).isEqualNode(node2))
        return false; 
      node1 = node1.getNextSibling();
    } 
    return !(node1 != node2);
  }
  
  public void setReadOnly(boolean paramBoolean1, boolean paramBoolean2) {
    super.setReadOnly(paramBoolean1, paramBoolean2);
    if (paramBoolean2) {
      if (needsSyncChildren())
        synchronizeChildren(); 
      for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling) {
        if (childNode.getNodeType() != 5)
          childNode.setReadOnly(paramBoolean1, true); 
      } 
    } 
  }
  
  protected void synchronizeChildren() { needsSyncChildren(false); }
  
  void checkNormalizationAfterInsert(ChildNode paramChildNode) {
    if (paramChildNode.getNodeType() == 3) {
      ChildNode childNode1 = paramChildNode.previousSibling();
      ChildNode childNode2 = paramChildNode.nextSibling;
      if ((childNode1 != null && childNode1.getNodeType() == 3) || (childNode2 != null && childNode2.getNodeType() == 3))
        isNormalized(false); 
    } else if (!paramChildNode.isNormalized()) {
      isNormalized(false);
    } 
  }
  
  void checkNormalizationAfterRemove(ChildNode paramChildNode) {
    if (paramChildNode != null && paramChildNode.getNodeType() == 3) {
      ChildNode childNode = paramChildNode.nextSibling;
      if (childNode != null && childNode.getNodeType() == 3)
        isNormalized(false); 
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (needsSyncChildren())
      synchronizeChildren(); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    needsSyncChildren(false);
  }
  
  protected class UserDataRecord implements Serializable {
    private static final long serialVersionUID = 3258126977134310455L;
    
    Object fData;
    
    UserDataHandler fHandler;
    
    UserDataRecord(Object param1Object, UserDataHandler param1UserDataHandler) {
      this.fData = param1Object;
      this.fHandler = param1UserDataHandler;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\ParentNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */