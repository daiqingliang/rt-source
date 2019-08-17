package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public abstract class NodeImpl implements Node, NodeList, EventTarget, Cloneable, Serializable {
  public static final short TREE_POSITION_PRECEDING = 1;
  
  public static final short TREE_POSITION_FOLLOWING = 2;
  
  public static final short TREE_POSITION_ANCESTOR = 4;
  
  public static final short TREE_POSITION_DESCENDANT = 8;
  
  public static final short TREE_POSITION_EQUIVALENT = 16;
  
  public static final short TREE_POSITION_SAME_NODE = 32;
  
  public static final short TREE_POSITION_DISCONNECTED = 0;
  
  public static final short DOCUMENT_POSITION_DISCONNECTED = 1;
  
  public static final short DOCUMENT_POSITION_PRECEDING = 2;
  
  public static final short DOCUMENT_POSITION_FOLLOWING = 4;
  
  public static final short DOCUMENT_POSITION_CONTAINS = 8;
  
  public static final short DOCUMENT_POSITION_IS_CONTAINED = 16;
  
  public static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;
  
  static final long serialVersionUID = -6316591992167219696L;
  
  public static final short ELEMENT_DEFINITION_NODE = 21;
  
  protected NodeImpl ownerNode;
  
  protected short flags;
  
  protected static final short READONLY = 1;
  
  protected static final short SYNCDATA = 2;
  
  protected static final short SYNCCHILDREN = 4;
  
  protected static final short OWNED = 8;
  
  protected static final short FIRSTCHILD = 16;
  
  protected static final short SPECIFIED = 32;
  
  protected static final short IGNORABLEWS = 64;
  
  protected static final short HASSTRING = 128;
  
  protected static final short NORMALIZED = 256;
  
  protected static final short ID = 512;
  
  protected NodeImpl(CoreDocumentImpl paramCoreDocumentImpl) { this.ownerNode = paramCoreDocumentImpl; }
  
  public NodeImpl() {}
  
  public abstract short getNodeType();
  
  public abstract String getNodeName();
  
  public String getNodeValue() { return null; }
  
  public void setNodeValue(String paramString) throws DOMException {}
  
  public Node appendChild(Node paramNode) throws DOMException { return insertBefore(paramNode, null); }
  
  public Node cloneNode(boolean paramBoolean) {
    NodeImpl nodeImpl;
    if (needsSyncData())
      synchronizeData(); 
    try {
      nodeImpl = (NodeImpl)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new RuntimeException("**Internal Error**" + cloneNotSupportedException);
    } 
    nodeImpl.ownerNode = ownerDocument();
    nodeImpl.isOwned(false);
    nodeImpl.isReadOnly(false);
    ownerDocument().callUserDataHandlers(this, nodeImpl, (short)1);
    return nodeImpl;
  }
  
  public Document getOwnerDocument() { return isOwned() ? this.ownerNode.ownerDocument() : (Document)this.ownerNode; }
  
  CoreDocumentImpl ownerDocument() { return isOwned() ? this.ownerNode.ownerDocument() : (CoreDocumentImpl)this.ownerNode; }
  
  void setOwnerDocument(CoreDocumentImpl paramCoreDocumentImpl) {
    if (needsSyncData())
      synchronizeData(); 
    if (!isOwned())
      this.ownerNode = paramCoreDocumentImpl; 
  }
  
  protected int getNodeNumber() {
    CoreDocumentImpl coreDocumentImpl = (CoreDocumentImpl)getOwnerDocument();
    return coreDocumentImpl.getNodeNumber(this);
  }
  
  public Node getParentNode() { return null; }
  
  NodeImpl parentNode() { return null; }
  
  public Node getNextSibling() { return null; }
  
  public Node getPreviousSibling() { return null; }
  
  ChildNode previousSibling() { return null; }
  
  public NamedNodeMap getAttributes() { return null; }
  
  public boolean hasAttributes() { return false; }
  
  public boolean hasChildNodes() { return false; }
  
  public NodeList getChildNodes() { return this; }
  
  public Node getFirstChild() { return null; }
  
  public Node getLastChild() { return null; }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException { throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); }
  
  public Node removeChild(Node paramNode) throws DOMException { throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null)); }
  
  public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException { throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); }
  
  public int getLength() { return 0; }
  
  public Node item(int paramInt) { return null; }
  
  public void normalize() {}
  
  public boolean isSupported(String paramString1, String paramString2) { return ownerDocument().getImplementation().hasFeature(paramString1, paramString2); }
  
  public String getNamespaceURI() { return null; }
  
  public String getPrefix() { return null; }
  
  public void setPrefix(String paramString) throws DOMException { throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null)); }
  
  public String getLocalName() { return null; }
  
  public void addEventListener(String paramString, EventListener paramEventListener, boolean paramBoolean) { ownerDocument().addEventListener(this, paramString, paramEventListener, paramBoolean); }
  
  public void removeEventListener(String paramString, EventListener paramEventListener, boolean paramBoolean) { ownerDocument().removeEventListener(this, paramString, paramEventListener, paramBoolean); }
  
  public boolean dispatchEvent(Event paramEvent) { return ownerDocument().dispatchEvent(this, paramEvent); }
  
  public String getBaseURI() { return null; }
  
  public short compareTreePosition(Node paramNode) {
    if (this == paramNode)
      return 48; 
    short s1 = getNodeType();
    short s2 = paramNode.getNodeType();
    if (s1 == 6 || s1 == 12 || s2 == 6 || s2 == 12)
      return 0; 
    Node node2 = this;
    Node node3 = paramNode;
    byte b1 = 0;
    byte b2 = 0;
    NodeImpl nodeImpl = this;
    while (nodeImpl != null) {
      b1++;
      if (nodeImpl == paramNode)
        return 5; 
      node2 = nodeImpl;
      Node node = nodeImpl.getParentNode();
    } 
    Node node1;
    for (node1 = paramNode; node1 != null; node1 = node1.getParentNode()) {
      b2++;
      if (node1 == this)
        return 10; 
      node3 = node1;
    } 
    Node node4 = this;
    Node node5 = paramNode;
    short s3 = node2.getNodeType();
    short s4 = node3.getNodeType();
    if (s3 == 2)
      node4 = ((AttrImpl)node2).getOwnerElement(); 
    if (s4 == 2)
      node5 = ((AttrImpl)node3).getOwnerElement(); 
    if (s3 == 2 && s4 == 2 && node4 == node5)
      return 16; 
    if (s3 == 2) {
      b1 = 0;
      for (node1 = node4; node1 != null; node1 = node1.getParentNode()) {
        b1++;
        if (node1 == node5)
          return 1; 
        node2 = node1;
      } 
    } 
    if (s4 == 2) {
      b2 = 0;
      for (node1 = node5; node1 != null; node1 = node1.getParentNode()) {
        b2++;
        if (node1 == node4)
          return 2; 
        node3 = node1;
      } 
    } 
    if (node2 != node3)
      return 0; 
    if (b1 > b2) {
      for (byte b = 0; b < b1 - b2; b++)
        node4 = node4.getParentNode(); 
      if (node4 == node5)
        return 1; 
    } else {
      for (byte b = 0; b < b2 - b1; b++)
        node5 = node5.getParentNode(); 
      if (node5 == node4)
        return 2; 
    } 
    Node node6 = node4.getParentNode();
    for (Node node7 = node5.getParentNode(); node6 != node7; node7 = node7.getParentNode()) {
      node4 = node6;
      node5 = node7;
      node6 = node6.getParentNode();
    } 
    for (Node node8 = node6.getFirstChild(); node8 != null; node8 = node8.getNextSibling()) {
      if (node8 == node5)
        return 1; 
      if (node8 == node4)
        return 2; 
    } 
    return 0;
  }
  
  public short compareDocumentPosition(Node paramNode) {
    DocumentType documentType;
    Document document3;
    Node node4;
    Node node2;
    Document document2;
    Document document1;
    if (this == paramNode)
      return 0; 
    try {
      document1 = (NodeImpl)paramNode;
    } catch (ClassCastException null) {
      document2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, document2);
    } 
    if (getNodeType() == 9) {
      document1 = (Document)this;
    } else {
      document1 = getOwnerDocument();
    } 
    if (paramNode.getNodeType() == 9) {
      document2 = (Document)paramNode;
    } else {
      document2 = paramNode.getOwnerDocument();
    } 
    if (document1 != document2 && document1 != null && document2 != null) {
      int i = ((CoreDocumentImpl)document2).getNodeNumber();
      int j = ((CoreDocumentImpl)document1).getNodeNumber();
      return (i > j) ? 37 : 35;
    } 
    NodeImpl nodeImpl2 = this;
    Node node3 = paramNode;
    byte b1 = 0;
    byte b2 = 0;
    NodeImpl nodeImpl1 = this;
    while (nodeImpl1 != null) {
      b1++;
      if (nodeImpl1 == paramNode)
        return 10; 
      nodeImpl2 = nodeImpl1;
      Node node = nodeImpl1.getParentNode();
    } 
    Node node1;
    for (node1 = paramNode; node1 != null; node1 = node1.getParentNode()) {
      b2++;
      if (node1 == this)
        return 20; 
      node3 = node1;
    } 
    short s1 = nodeImpl2.getNodeType();
    short s2 = node3.getNodeType();
    NodeImpl nodeImpl3 = this;
    Node node5 = paramNode;
    switch (s1) {
      case 6:
      case 12:
        documentType = document1.getDoctype();
        if (documentType == node3)
          return 10; 
        switch (s2) {
          case 6:
          case 12:
            return (s1 != s2) ? ((s1 > s2) ? 2 : 4) : ((s1 == 12) ? (((NamedNodeMapImpl)documentType.getNotations()).precedes(node3, nodeImpl2) ? 34 : 36) : (((NamedNodeMapImpl)documentType.getEntities()).precedes(node3, nodeImpl2) ? 34 : 36));
        } 
        document3 = node2 = document1;
        break;
      case 10:
        if (node5 == document1)
          return 10; 
        if (document1 != null && document1 == document2)
          return 4; 
        break;
      case 2:
        node4 = ((AttrImpl)node2).getOwnerElement();
        if (s2 == 2) {
          node5 = ((AttrImpl)node3).getOwnerElement();
          if (node5 == node4)
            return ((NamedNodeMapImpl)node4.getAttributes()).precedes(paramNode, this) ? 34 : 36; 
        } 
        b1 = 0;
        for (node1 = node4; node1 != null; node1 = node1.getParentNode()) {
          b1++;
          if (node1 == node5)
            return 10; 
          node2 = node1;
        } 
        break;
    } 
    switch (s2) {
      case 6:
      case 12:
        documentType = document1.getDoctype();
        if (documentType == this)
          return 20; 
        node5 = node3 = document1;
        break;
      case 10:
        if (node4 == document2)
          return 20; 
        if (document2 != null && document1 == document2)
          return 2; 
        break;
      case 2:
        b2 = 0;
        node5 = ((AttrImpl)node3).getOwnerElement();
        for (node1 = node5; node1 != null; node1 = node1.getParentNode()) {
          b2++;
          if (node1 == node4)
            return 20; 
          node3 = node1;
        } 
        break;
    } 
    if (node2 != node3) {
      int i = ((NodeImpl)node2).getNodeNumber();
      int j = ((NodeImpl)node3).getNodeNumber();
      return (i > j) ? 37 : 35;
    } 
    if (b1 > b2) {
      for (byte b = 0; b < b1 - b2; b++)
        node4 = node4.getParentNode(); 
      if (node4 == node5)
        return 2; 
    } else {
      for (byte b = 0; b < b2 - b1; b++)
        node5 = node5.getParentNode(); 
      if (node5 == node4)
        return 4; 
    } 
    Node node6 = node4.getParentNode();
    for (Node node7 = node5.getParentNode(); node6 != node7; node7 = node7.getParentNode()) {
      node4 = node6;
      node5 = node7;
      node6 = node6.getParentNode();
    } 
    for (Node node8 = node6.getFirstChild(); node8 != null; node8 = node8.getNextSibling()) {
      if (node8 == node5)
        return 2; 
      if (node8 == node4)
        return 4; 
    } 
    return 0;
  }
  
  public String getTextContent() { return getNodeValue(); }
  
  void getTextContent(StringBuffer paramStringBuffer) throws DOMException {
    String str = getNodeValue();
    if (str != null)
      paramStringBuffer.append(str); 
  }
  
  public void setTextContent(String paramString) throws DOMException { setNodeValue(paramString); }
  
  public boolean isSameNode(Node paramNode) { return (this == paramNode); }
  
  public boolean isDefaultNamespace(String paramString) {
    NodeImpl nodeImpl2;
    String str2;
    String str1;
    short s = getNodeType();
    switch (s) {
      case 1:
        str1 = getNamespaceURI();
        str2 = getPrefix();
        if (str2 == null || str2.length() == 0)
          return (paramString == null) ? ((str1 == paramString)) : paramString.equals(str1); 
        if (hasAttributes()) {
          ElementImpl elementImpl = (ElementImpl)this;
          NodeImpl nodeImpl = (NodeImpl)elementImpl.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
          if (nodeImpl != null) {
            String str = nodeImpl.getNodeValue();
            return (paramString == null) ? ((str1 == str)) : paramString.equals(str);
          } 
        } 
        nodeImpl2 = (NodeImpl)getElementAncestor(this);
        return (nodeImpl2 != null) ? nodeImpl2.isDefaultNamespace(paramString) : 0;
      case 9:
        return ((NodeImpl)((Document)this).getDocumentElement()).isDefaultNamespace(paramString);
      case 6:
      case 10:
      case 11:
      case 12:
        return false;
      case 2:
        return (this.ownerNode.getNodeType() == 1) ? this.ownerNode.isDefaultNamespace(paramString) : 0;
    } 
    NodeImpl nodeImpl1 = (NodeImpl)getElementAncestor(this);
    return (nodeImpl1 != null) ? nodeImpl1.isDefaultNamespace(paramString) : 0;
  }
  
  public String lookupPrefix(String paramString) {
    String str;
    if (paramString == null)
      return null; 
    short s = getNodeType();
    switch (s) {
      case 1:
        str = getNamespaceURI();
        return lookupNamespacePrefix(paramString, (ElementImpl)this);
      case 9:
        return ((NodeImpl)((Document)this).getDocumentElement()).lookupPrefix(paramString);
      case 6:
      case 10:
      case 11:
      case 12:
        return null;
      case 2:
        return (this.ownerNode.getNodeType() == 1) ? this.ownerNode.lookupPrefix(paramString) : null;
    } 
    NodeImpl nodeImpl = (NodeImpl)getElementAncestor(this);
    return (nodeImpl != null) ? nodeImpl.lookupPrefix(paramString) : null;
  }
  
  public String lookupNamespaceURI(String paramString) {
    NodeImpl nodeImpl2;
    String str2;
    String str1;
    short s = getNodeType();
    switch (s) {
      case 1:
        str1 = getNamespaceURI();
        str2 = getPrefix();
        if (str1 != null) {
          if (paramString == null && str2 == paramString)
            return str1; 
          if (str2 != null && str2.equals(paramString))
            return str1; 
        } 
        if (hasAttributes()) {
          NamedNodeMap namedNodeMap = getAttributes();
          int i = namedNodeMap.getLength();
          for (byte b = 0; b < i; b++) {
            Node node = namedNodeMap.item(b);
            String str3 = node.getPrefix();
            String str4 = node.getNodeValue();
            str1 = node.getNamespaceURI();
            if (str1 != null && str1.equals("http://www.w3.org/2000/xmlns/")) {
              if (paramString == null && node.getNodeName().equals("xmlns"))
                return str4; 
              if (str3 != null && str3.equals("xmlns") && node.getLocalName().equals(paramString))
                return str4; 
            } 
          } 
        } 
        nodeImpl2 = (NodeImpl)getElementAncestor(this);
        return (nodeImpl2 != null) ? nodeImpl2.lookupNamespaceURI(paramString) : null;
      case 9:
        return ((NodeImpl)((Document)this).getDocumentElement()).lookupNamespaceURI(paramString);
      case 6:
      case 10:
      case 11:
      case 12:
        return null;
      case 2:
        return (this.ownerNode.getNodeType() == 1) ? this.ownerNode.lookupNamespaceURI(paramString) : null;
    } 
    NodeImpl nodeImpl1 = (NodeImpl)getElementAncestor(this);
    return (nodeImpl1 != null) ? nodeImpl1.lookupNamespaceURI(paramString) : null;
  }
  
  Node getElementAncestor(Node paramNode) throws DOMException {
    Node node = paramNode.getParentNode();
    if (node != null) {
      short s = node.getNodeType();
      return (s == 1) ? node : getElementAncestor(node);
    } 
    return null;
  }
  
  String lookupNamespacePrefix(String paramString, ElementImpl paramElementImpl) {
    String str1 = getNamespaceURI();
    String str2 = getPrefix();
    if (str1 != null && str1.equals(paramString) && str2 != null) {
      String str = paramElementImpl.lookupNamespaceURI(str2);
      if (str != null && str.equals(paramString))
        return str2; 
    } 
    if (hasAttributes()) {
      NamedNodeMap namedNodeMap = getAttributes();
      int i = namedNodeMap.getLength();
      for (byte b = 0; b < i; b++) {
        Node node = namedNodeMap.item(b);
        String str3 = node.getPrefix();
        String str4 = node.getNodeValue();
        str1 = node.getNamespaceURI();
        if (str1 != null && str1.equals("http://www.w3.org/2000/xmlns/") && (node.getNodeName().equals("xmlns") || (str3 != null && str3.equals("xmlns") && str4.equals(paramString)))) {
          String str5 = node.getLocalName();
          String str6 = paramElementImpl.lookupNamespaceURI(str5);
          if (str6 != null && str6.equals(paramString))
            return str5; 
        } 
      } 
    } 
    NodeImpl nodeImpl = (NodeImpl)getElementAncestor(this);
    return (nodeImpl != null) ? nodeImpl.lookupNamespacePrefix(paramString, paramElementImpl) : null;
  }
  
  public boolean isEqualNode(Node paramNode) {
    if (paramNode == this)
      return true; 
    if (paramNode.getNodeType() != getNodeType())
      return false; 
    if (getNodeName() == null) {
      if (paramNode.getNodeName() != null)
        return false; 
    } else if (!getNodeName().equals(paramNode.getNodeName())) {
      return false;
    } 
    if (getLocalName() == null) {
      if (paramNode.getLocalName() != null)
        return false; 
    } else if (!getLocalName().equals(paramNode.getLocalName())) {
      return false;
    } 
    if (getNamespaceURI() == null) {
      if (paramNode.getNamespaceURI() != null)
        return false; 
    } else if (!getNamespaceURI().equals(paramNode.getNamespaceURI())) {
      return false;
    } 
    if (getPrefix() == null) {
      if (paramNode.getPrefix() != null)
        return false; 
    } else if (!getPrefix().equals(paramNode.getPrefix())) {
      return false;
    } 
    if (getNodeValue() == null) {
      if (paramNode.getNodeValue() != null)
        return false; 
    } else if (!getNodeValue().equals(paramNode.getNodeValue())) {
      return false;
    } 
    return true;
  }
  
  public Object getFeature(String paramString1, String paramString2) { return isSupported(paramString1, paramString2) ? this : null; }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler) { return ownerDocument().setUserData(this, paramString, paramObject, paramUserDataHandler); }
  
  public Object getUserData(String paramString) { return ownerDocument().getUserData(this, paramString); }
  
  protected Map<String, ParentNode.UserDataRecord> getUserDataRecord() { return ownerDocument().getUserDataRecord(this); }
  
  public void setReadOnly(boolean paramBoolean1, boolean paramBoolean2) {
    if (needsSyncData())
      synchronizeData(); 
    isReadOnly(paramBoolean1);
  }
  
  public boolean getReadOnly() {
    if (needsSyncData())
      synchronizeData(); 
    return isReadOnly();
  }
  
  public void setUserData(Object paramObject) { ownerDocument().setUserData(this, paramObject); }
  
  public Object getUserData() { return ownerDocument().getUserData(this); }
  
  protected void changed() { ownerDocument().changed(); }
  
  protected int changes() { return ownerDocument().changes(); }
  
  protected void synchronizeData() { needsSyncData(false); }
  
  protected Node getContainer() { return null; }
  
  final boolean isReadOnly() { return ((this.flags & true) != 0); }
  
  final void isReadOnly(boolean paramBoolean) { this.flags = (short)(paramBoolean ? (this.flags | true) : (this.flags & 0xFFFFFFFE)); }
  
  final boolean needsSyncData() { return ((this.flags & 0x2) != 0); }
  
  final void needsSyncData(boolean paramBoolean) { this.flags = (short)(paramBoolean ? (this.flags | 0x2) : (this.flags & 0xFFFFFFFD)); }
  
  final boolean needsSyncChildren() { return ((this.flags & 0x4) != 0); }
  
  public final void needsSyncChildren(boolean paramBoolean) { this.flags = (short)(paramBoolean ? (this.flags | 0x4) : (this.flags & 0xFFFFFFFB)); }
  
  final boolean isOwned() { return ((this.flags & 0x8) != 0); }
  
  final void isOwned(boolean paramBoolean) { this.flags = (short)(paramBoolean ? (this.flags | 0x8) : (this.flags & 0xFFFFFFF7)); }
  
  final boolean isFirstChild() { return ((this.flags & 0x10) != 0); }
  
  final void isFirstChild(boolean paramBoolean) { this.flags = (short)(paramBoolean ? (this.flags | 0x10) : (this.flags & 0xFFFFFFEF)); }
  
  final boolean isSpecified() { return ((this.flags & 0x20) != 0); }
  
  final void isSpecified(boolean paramBoolean) { this.flags = (short)(paramBoolean ? (this.flags | 0x20) : (this.flags & 0xFFFFFFDF)); }
  
  final boolean internalIsIgnorableWhitespace() { return ((this.flags & 0x40) != 0); }
  
  final void isIgnorableWhitespace(boolean paramBoolean) { this.flags = (short)(paramBoolean ? (this.flags | 0x40) : (this.flags & 0xFFFFFFBF)); }
  
  final boolean hasStringValue() { return ((this.flags & 0x80) != 0); }
  
  final void hasStringValue(boolean paramBoolean) { this.flags = (short)(paramBoolean ? (this.flags | 0x80) : (this.flags & 0xFFFFFF7F)); }
  
  final boolean isNormalized() { return ((this.flags & 0x100) != 0); }
  
  final void isNormalized(boolean paramBoolean) {
    if (!paramBoolean && isNormalized() && this.ownerNode != null)
      this.ownerNode.isNormalized(false); 
    this.flags = (short)(paramBoolean ? (this.flags | 0x100) : (this.flags & 0xFFFFFEFF));
  }
  
  final boolean isIdAttribute() { return ((this.flags & 0x200) != 0); }
  
  final void isIdAttribute(boolean paramBoolean) { this.flags = (short)(paramBoolean ? (this.flags | 0x200) : (this.flags & 0xFFFFFDFF)); }
  
  public String toString() { return "[" + getNodeName() + ": " + getNodeValue() + "]"; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (needsSyncData())
      synchronizeData(); 
    paramObjectOutputStream.defaultWriteObject();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\NodeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */