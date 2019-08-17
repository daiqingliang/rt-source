package com.sun.org.apache.xerces.internal.dom;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class AttributeMap extends NamedNodeMapImpl {
  static final long serialVersionUID = 8872606282138665383L;
  
  protected AttributeMap(ElementImpl paramElementImpl, NamedNodeMapImpl paramNamedNodeMapImpl) {
    super(paramElementImpl);
    if (paramNamedNodeMapImpl != null) {
      cloneContent(paramNamedNodeMapImpl);
      if (this.nodes != null)
        hasDefaults(true); 
    } 
  }
  
  public Node setNamedItem(Node paramNode) throws DOMException {
    boolean bool = (this.ownerNode.ownerDocument()).errorChecking;
    if (bool) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramNode.getOwnerDocument() != this.ownerNode.ownerDocument()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
        throw new DOMException((short)4, str);
      } 
      if (paramNode.getNodeType() != 2) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
        throw new DOMException((short)3, str);
      } 
    } 
    AttrImpl attrImpl1 = (AttrImpl)paramNode;
    if (attrImpl1.isOwned()) {
      if (bool && attrImpl1.getOwnerElement() != this.ownerNode) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null);
        throw new DOMException((short)10, str);
      } 
      return paramNode;
    } 
    attrImpl1.ownerNode = this.ownerNode;
    attrImpl1.isOwned(true);
    int i = findNamePoint(attrImpl1.getNodeName(), 0);
    AttrImpl attrImpl2 = null;
    if (i >= 0) {
      attrImpl2 = (AttrImpl)this.nodes.get(i);
      this.nodes.set(i, paramNode);
      attrImpl2.ownerNode = this.ownerNode.ownerDocument();
      attrImpl2.isOwned(false);
      attrImpl2.isSpecified(true);
    } else {
      i = -1 - i;
      if (null == this.nodes)
        this.nodes = new ArrayList(5); 
      this.nodes.add(i, paramNode);
    } 
    this.ownerNode.ownerDocument().setAttrNode(attrImpl1, attrImpl2);
    if (!attrImpl1.isNormalized())
      this.ownerNode.isNormalized(false); 
    return attrImpl2;
  }
  
  public Node setNamedItemNS(Node paramNode) throws DOMException {
    boolean bool = (this.ownerNode.ownerDocument()).errorChecking;
    if (bool) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramNode.getOwnerDocument() != this.ownerNode.ownerDocument()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
        throw new DOMException((short)4, str);
      } 
      if (paramNode.getNodeType() != 2) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
        throw new DOMException((short)3, str);
      } 
    } 
    AttrImpl attrImpl1 = (AttrImpl)paramNode;
    if (attrImpl1.isOwned()) {
      if (bool && attrImpl1.getOwnerElement() != this.ownerNode) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null);
        throw new DOMException((short)10, str);
      } 
      return paramNode;
    } 
    attrImpl1.ownerNode = this.ownerNode;
    attrImpl1.isOwned(true);
    int i = findNamePoint(attrImpl1.getNamespaceURI(), attrImpl1.getLocalName());
    AttrImpl attrImpl2 = null;
    if (i >= 0) {
      attrImpl2 = (AttrImpl)this.nodes.get(i);
      this.nodes.set(i, paramNode);
      attrImpl2.ownerNode = this.ownerNode.ownerDocument();
      attrImpl2.isOwned(false);
      attrImpl2.isSpecified(true);
    } else {
      i = findNamePoint(paramNode.getNodeName(), 0);
      if (i >= 0) {
        attrImpl2 = (AttrImpl)this.nodes.get(i);
        this.nodes.add(i, paramNode);
      } else {
        i = -1 - i;
        if (null == this.nodes)
          this.nodes = new ArrayList(5); 
        this.nodes.add(i, paramNode);
      } 
    } 
    this.ownerNode.ownerDocument().setAttrNode(attrImpl1, attrImpl2);
    if (!attrImpl1.isNormalized())
      this.ownerNode.isNormalized(false); 
    return attrImpl2;
  }
  
  public Node removeNamedItem(String paramString) throws DOMException { return internalRemoveNamedItem(paramString, true); }
  
  Node safeRemoveNamedItem(String paramString) throws DOMException { return internalRemoveNamedItem(paramString, false); }
  
  protected Node removeItem(Node paramNode, boolean paramBoolean) throws DOMException {
    byte b = -1;
    if (this.nodes != null) {
      int i = this.nodes.size();
      for (byte b1 = 0; b1 < i; b1++) {
        if (this.nodes.get(b1) == paramNode) {
          b = b1;
          break;
        } 
      } 
    } 
    if (b < 0) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
      throw new DOMException((short)8, str);
    } 
    return remove((AttrImpl)paramNode, b, paramBoolean);
  }
  
  protected final Node internalRemoveNamedItem(String paramString, boolean paramBoolean) {
    if (isReadOnly()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str);
    } 
    int i = findNamePoint(paramString, 0);
    if (i < 0) {
      if (paramBoolean) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
        throw new DOMException((short)8, str);
      } 
      return null;
    } 
    return remove((AttrImpl)this.nodes.get(i), i, true);
  }
  
  private final Node remove(AttrImpl paramAttrImpl, int paramInt, boolean paramBoolean) {
    CoreDocumentImpl coreDocumentImpl = this.ownerNode.ownerDocument();
    String str = paramAttrImpl.getNodeName();
    if (paramAttrImpl.isIdAttribute())
      coreDocumentImpl.removeIdentifier(paramAttrImpl.getValue()); 
    if (hasDefaults() && paramBoolean) {
      NamedNodeMapImpl namedNodeMapImpl = ((ElementImpl)this.ownerNode).getDefaultAttributes();
      Node node;
      if (namedNodeMapImpl != null && (node = namedNodeMapImpl.getNamedItem(str)) != null && findNamePoint(str, paramInt + 1) < 0) {
        NodeImpl nodeImpl = (NodeImpl)node.cloneNode(true);
        if (node.getLocalName() != null)
          ((AttrNSImpl)nodeImpl).namespaceURI = paramAttrImpl.getNamespaceURI(); 
        nodeImpl.ownerNode = this.ownerNode;
        nodeImpl.isOwned(true);
        nodeImpl.isSpecified(false);
        this.nodes.set(paramInt, nodeImpl);
        if (paramAttrImpl.isIdAttribute())
          coreDocumentImpl.putIdentifier(nodeImpl.getNodeValue(), (ElementImpl)this.ownerNode); 
      } else {
        this.nodes.remove(paramInt);
      } 
    } else {
      this.nodes.remove(paramInt);
    } 
    paramAttrImpl.ownerNode = coreDocumentImpl;
    paramAttrImpl.isOwned(false);
    paramAttrImpl.isSpecified(true);
    paramAttrImpl.isIdAttribute(false);
    coreDocumentImpl.removedAttrNode(paramAttrImpl, this.ownerNode, str);
    return paramAttrImpl;
  }
  
  public Node removeNamedItemNS(String paramString1, String paramString2) throws DOMException { return internalRemoveNamedItemNS(paramString1, paramString2, true); }
  
  Node safeRemoveNamedItemNS(String paramString1, String paramString2) throws DOMException { return internalRemoveNamedItemNS(paramString1, paramString2, false); }
  
  protected final Node internalRemoveNamedItemNS(String paramString1, String paramString2, boolean paramBoolean) {
    CoreDocumentImpl coreDocumentImpl = this.ownerNode.ownerDocument();
    if (coreDocumentImpl.errorChecking && isReadOnly()) {
      String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str1);
    } 
    int i = findNamePoint(paramString1, paramString2);
    if (i < 0) {
      if (paramBoolean) {
        String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
        throw new DOMException((short)8, str1);
      } 
      return null;
    } 
    AttrImpl attrImpl = (AttrImpl)this.nodes.get(i);
    if (attrImpl.isIdAttribute())
      coreDocumentImpl.removeIdentifier(attrImpl.getValue()); 
    String str = attrImpl.getNodeName();
    if (hasDefaults()) {
      NamedNodeMapImpl namedNodeMapImpl = ((ElementImpl)this.ownerNode).getDefaultAttributes();
      Node node;
      if (namedNodeMapImpl != null && (node = namedNodeMapImpl.getNamedItem(str)) != null) {
        int j = findNamePoint(str, 0);
        if (j >= 0 && findNamePoint(str, j + 1) < 0) {
          NodeImpl nodeImpl = (NodeImpl)node.cloneNode(true);
          nodeImpl.ownerNode = this.ownerNode;
          if (node.getLocalName() != null)
            ((AttrNSImpl)nodeImpl).namespaceURI = paramString1; 
          nodeImpl.isOwned(true);
          nodeImpl.isSpecified(false);
          this.nodes.set(i, nodeImpl);
          if (nodeImpl.isIdAttribute())
            coreDocumentImpl.putIdentifier(nodeImpl.getNodeValue(), (ElementImpl)this.ownerNode); 
        } else {
          this.nodes.remove(i);
        } 
      } else {
        this.nodes.remove(i);
      } 
    } else {
      this.nodes.remove(i);
    } 
    attrImpl.ownerNode = coreDocumentImpl;
    attrImpl.isOwned(false);
    attrImpl.isSpecified(true);
    attrImpl.isIdAttribute(false);
    coreDocumentImpl.removedAttrNode(attrImpl, this.ownerNode, paramString2);
    return attrImpl;
  }
  
  public NamedNodeMapImpl cloneMap(NodeImpl paramNodeImpl) {
    AttributeMap attributeMap = new AttributeMap((ElementImpl)paramNodeImpl, null);
    attributeMap.hasDefaults(hasDefaults());
    attributeMap.cloneContent(this);
    return attributeMap;
  }
  
  protected void cloneContent(NamedNodeMapImpl paramNamedNodeMapImpl) {
    List list = paramNamedNodeMapImpl.nodes;
    if (list != null) {
      int i = list.size();
      if (i != 0) {
        if (this.nodes == null) {
          this.nodes = new ArrayList(i);
        } else {
          this.nodes.clear();
        } 
        for (byte b = 0; b < i; b++) {
          NodeImpl nodeImpl1 = (NodeImpl)list.get(b);
          NodeImpl nodeImpl2 = (NodeImpl)nodeImpl1.cloneNode(true);
          nodeImpl2.isSpecified(nodeImpl1.isSpecified());
          this.nodes.add(nodeImpl2);
          nodeImpl2.ownerNode = this.ownerNode;
          nodeImpl2.isOwned(true);
        } 
      } 
    } 
  }
  
  void moveSpecifiedAttributes(AttributeMap paramAttributeMap) {
    int i = (paramAttributeMap.nodes != null) ? paramAttributeMap.nodes.size() : 0;
    for (int j = i - 1; j >= 0; j--) {
      AttrImpl attrImpl = (AttrImpl)paramAttributeMap.nodes.get(j);
      if (attrImpl.isSpecified()) {
        paramAttributeMap.remove(attrImpl, j, false);
        if (attrImpl.getLocalName() != null) {
          setNamedItem(attrImpl);
        } else {
          setNamedItemNS(attrImpl);
        } 
      } 
    } 
  }
  
  protected void reconcileDefaults(NamedNodeMapImpl paramNamedNodeMapImpl) {
    int i = (this.nodes != null) ? this.nodes.size() : 0;
    int j;
    for (j = i - 1; j >= 0; j--) {
      AttrImpl attrImpl = (AttrImpl)this.nodes.get(j);
      if (!attrImpl.isSpecified())
        remove(attrImpl, j, false); 
    } 
    if (paramNamedNodeMapImpl == null)
      return; 
    if (this.nodes == null || this.nodes.size() == 0) {
      cloneContent(paramNamedNodeMapImpl);
    } else {
      j = paramNamedNodeMapImpl.nodes.size();
      for (byte b = 0; b < j; b++) {
        AttrImpl attrImpl = (AttrImpl)paramNamedNodeMapImpl.nodes.get(b);
        int k = findNamePoint(attrImpl.getNodeName(), 0);
        if (k < 0) {
          k = -1 - k;
          NodeImpl nodeImpl = (NodeImpl)attrImpl.cloneNode(true);
          nodeImpl.ownerNode = this.ownerNode;
          nodeImpl.isOwned(true);
          nodeImpl.isSpecified(false);
          this.nodes.add(k, nodeImpl);
        } 
      } 
    } 
  }
  
  protected final int addItem(Node paramNode) {
    AttrImpl attrImpl = (AttrImpl)paramNode;
    attrImpl.ownerNode = this.ownerNode;
    attrImpl.isOwned(true);
    int i = findNamePoint(attrImpl.getNamespaceURI(), attrImpl.getLocalName());
    if (i >= 0) {
      this.nodes.set(i, paramNode);
    } else {
      i = findNamePoint(attrImpl.getNodeName(), 0);
      if (i >= 0) {
        this.nodes.add(i, paramNode);
      } else {
        i = -1 - i;
        if (null == this.nodes)
          this.nodes = new ArrayList(5); 
        this.nodes.add(i, paramNode);
      } 
    } 
    this.ownerNode.ownerDocument().setAttrNode(attrImpl, null);
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\AttributeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */