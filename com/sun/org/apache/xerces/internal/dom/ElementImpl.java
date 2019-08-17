package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

public class ElementImpl extends ParentNode implements Element, TypeInfo {
  static final long serialVersionUID = 3717253516652722278L;
  
  protected String name;
  
  protected AttributeMap attributes;
  
  public ElementImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) {
    super(paramCoreDocumentImpl);
    this.name = paramString;
    needsSyncData(true);
  }
  
  protected ElementImpl() {}
  
  void rename(String paramString) {
    if (needsSyncData())
      synchronizeData(); 
    this.name = paramString;
    reconcileDefaultAttributes();
  }
  
  public short getNodeType() { return 1; }
  
  public String getNodeName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.name;
  }
  
  public NamedNodeMap getAttributes() {
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes == null)
      this.attributes = new AttributeMap(this, null); 
    return this.attributes;
  }
  
  public Node cloneNode(boolean paramBoolean) {
    ElementImpl elementImpl = (ElementImpl)super.cloneNode(paramBoolean);
    if (this.attributes != null)
      elementImpl.attributes = (AttributeMap)this.attributes.cloneMap(elementImpl); 
    return elementImpl;
  }
  
  public String getBaseURI() {
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes != null) {
      Attr attr = (Attr)this.attributes.getNamedItem("xml:base");
      if (attr != null) {
        String str1 = attr.getNodeValue();
        if (str1.length() != 0) {
          try {
            str1 = (new URI(str1)).toString();
          } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
            String str2 = (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null;
            if (str2 != null) {
              try {
                str1 = (new URI(new URI(str2), str1)).toString();
              } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException1) {
                return null;
              } 
              return str1;
            } 
            return null;
          } 
          return str1;
        } 
      } 
    } 
    String str = (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null;
    if (str != null)
      try {
        return (new URI(str)).toString();
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
        return null;
      }  
    return null;
  }
  
  void setOwnerDocument(CoreDocumentImpl paramCoreDocumentImpl) {
    super.setOwnerDocument(paramCoreDocumentImpl);
    if (this.attributes != null)
      this.attributes.setOwnerDocument(paramCoreDocumentImpl); 
  }
  
  public String getAttribute(String paramString) {
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes == null)
      return ""; 
    Attr attr = (Attr)this.attributes.getNamedItem(paramString);
    return (attr == null) ? "" : attr.getValue();
  }
  
  public Attr getAttributeNode(String paramString) {
    if (needsSyncData())
      synchronizeData(); 
    return (this.attributes == null) ? null : (Attr)this.attributes.getNamedItem(paramString);
  }
  
  public NodeList getElementsByTagName(String paramString) { return new DeepNodeListImpl(this, paramString); }
  
  public String getTagName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.name;
  }
  
  public void normalize() {
    if (isNormalized())
      return; 
    if (needsSyncChildren())
      synchronizeChildren(); 
    for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode1) {
      ChildNode childNode1 = childNode.nextSibling;
      if (childNode.getNodeType() == 3) {
        if (childNode1 != null && childNode1.getNodeType() == 3) {
          ((Text)childNode).appendData(childNode1.getNodeValue());
          removeChild(childNode1);
          childNode1 = childNode;
        } else if (childNode.getNodeValue() == null || childNode.getNodeValue().length() == 0) {
          removeChild(childNode);
        } 
      } else if (childNode.getNodeType() == 1) {
        childNode.normalize();
      } 
    } 
    if (this.attributes != null)
      for (byte b = 0; b < this.attributes.getLength(); b++) {
        Node node = this.attributes.item(b);
        node.normalize();
      }  
    isNormalized(true);
  }
  
  public void removeAttribute(String paramString) {
    if (this.ownerDocument.errorChecking && isReadOnly()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str);
    } 
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes == null)
      return; 
    this.attributes.safeRemoveNamedItem(paramString);
  }
  
  public Attr removeAttributeNode(Attr paramAttr) throws DOMException {
    if (this.ownerDocument.errorChecking && isReadOnly()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str);
    } 
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes == null) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
      throw new DOMException((short)8, str);
    } 
    return (Attr)this.attributes.removeItem(paramAttr, true);
  }
  
  public void setAttribute(String paramString1, String paramString2) {
    if (this.ownerDocument.errorChecking && isReadOnly()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str);
    } 
    if (needsSyncData())
      synchronizeData(); 
    Attr attr = getAttributeNode(paramString1);
    if (attr == null) {
      attr = getOwnerDocument().createAttribute(paramString1);
      if (this.attributes == null)
        this.attributes = new AttributeMap(this, null); 
      attr.setNodeValue(paramString2);
      this.attributes.setNamedItem(attr);
    } else {
      attr.setNodeValue(paramString2);
    } 
  }
  
  public Attr setAttributeNode(Attr paramAttr) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    if (this.ownerDocument.errorChecking) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramAttr.getOwnerDocument() != this.ownerDocument) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
        throw new DOMException((short)4, str);
      } 
    } 
    if (this.attributes == null)
      this.attributes = new AttributeMap(this, null); 
    return (Attr)this.attributes.setNamedItem(paramAttr);
  }
  
  public String getAttributeNS(String paramString1, String paramString2) {
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes == null)
      return ""; 
    Attr attr = (Attr)this.attributes.getNamedItemNS(paramString1, paramString2);
    return (attr == null) ? "" : attr.getValue();
  }
  
  public void setAttributeNS(String paramString1, String paramString2, String paramString3) {
    String str2;
    String str1;
    if (this.ownerDocument.errorChecking && isReadOnly()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str);
    } 
    if (needsSyncData())
      synchronizeData(); 
    int i = paramString2.indexOf(':');
    if (i < 0) {
      str1 = null;
      str2 = paramString2;
    } else {
      str1 = paramString2.substring(0, i);
      str2 = paramString2.substring(i + 1);
    } 
    Attr attr = getAttributeNodeNS(paramString1, str2);
    if (attr == null) {
      attr = getOwnerDocument().createAttributeNS(paramString1, paramString2);
      if (this.attributes == null)
        this.attributes = new AttributeMap(this, null); 
      attr.setNodeValue(paramString3);
      this.attributes.setNamedItemNS(attr);
    } else {
      if (attr instanceof AttrNSImpl) {
        String str3 = ((AttrNSImpl)attr).name;
        String str4 = (str1 != null) ? (str1 + ":" + str2) : str2;
        ((AttrNSImpl)attr).name = str4;
        if (!str4.equals(str3)) {
          attr = (Attr)this.attributes.removeItem(attr, false);
          this.attributes.addItem(attr);
        } 
      } else {
        attr = new AttrNSImpl((CoreDocumentImpl)getOwnerDocument(), paramString1, paramString2, str2);
        this.attributes.setNamedItemNS(attr);
      } 
      attr.setNodeValue(paramString3);
    } 
  }
  
  public void removeAttributeNS(String paramString1, String paramString2) {
    if (this.ownerDocument.errorChecking && isReadOnly()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str);
    } 
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes == null)
      return; 
    this.attributes.safeRemoveNamedItemNS(paramString1, paramString2);
  }
  
  public Attr getAttributeNodeNS(String paramString1, String paramString2) {
    if (needsSyncData())
      synchronizeData(); 
    return (this.attributes == null) ? null : (Attr)this.attributes.getNamedItemNS(paramString1, paramString2);
  }
  
  public Attr setAttributeNodeNS(Attr paramAttr) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    if (this.ownerDocument.errorChecking) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramAttr.getOwnerDocument() != this.ownerDocument) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
        throw new DOMException((short)4, str);
      } 
    } 
    if (this.attributes == null)
      this.attributes = new AttributeMap(this, null); 
    return (Attr)this.attributes.setNamedItemNS(paramAttr);
  }
  
  protected int setXercesAttributeNode(Attr paramAttr) {
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes == null)
      this.attributes = new AttributeMap(this, null); 
    return this.attributes.addItem(paramAttr);
  }
  
  protected int getXercesAttribute(String paramString1, String paramString2) {
    if (needsSyncData())
      synchronizeData(); 
    return (this.attributes == null) ? -1 : this.attributes.getNamedItemIndex(paramString1, paramString2);
  }
  
  public boolean hasAttributes() {
    if (needsSyncData())
      synchronizeData(); 
    return (this.attributes != null && this.attributes.getLength() != 0);
  }
  
  public boolean hasAttribute(String paramString) { return (getAttributeNode(paramString) != null); }
  
  public boolean hasAttributeNS(String paramString1, String paramString2) { return (getAttributeNodeNS(paramString1, paramString2) != null); }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2) { return new DeepNodeListImpl(this, paramString1, paramString2); }
  
  public boolean isEqualNode(Node paramNode) {
    if (!super.isEqualNode(paramNode))
      return false; 
    boolean bool = hasAttributes();
    if (bool != ((Element)paramNode).hasAttributes())
      return false; 
    if (bool) {
      NamedNodeMap namedNodeMap1 = getAttributes();
      NamedNodeMap namedNodeMap2 = ((Element)paramNode).getAttributes();
      int i = namedNodeMap1.getLength();
      if (i != namedNodeMap2.getLength())
        return false; 
      for (byte b = 0; b < i; b++) {
        Node node = namedNodeMap1.item(b);
        if (node.getLocalName() == null) {
          Node node1 = namedNodeMap2.getNamedItem(node.getNodeName());
          if (node1 == null || !((NodeImpl)node).isEqualNode(node1))
            return false; 
        } else {
          Node node1 = namedNodeMap2.getNamedItemNS(node.getNamespaceURI(), node.getLocalName());
          if (node1 == null || !((NodeImpl)node).isEqualNode(node1))
            return false; 
        } 
      } 
    } 
    return true;
  }
  
  public void setIdAttributeNode(Attr paramAttr, boolean paramBoolean) {
    if (needsSyncData())
      synchronizeData(); 
    if (this.ownerDocument.errorChecking) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramAttr.getOwnerElement() != this) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
        throw new DOMException((short)8, str);
      } 
    } 
    ((AttrImpl)paramAttr).isIdAttribute(paramBoolean);
    if (!paramBoolean) {
      this.ownerDocument.removeIdentifier(paramAttr.getValue());
    } else {
      this.ownerDocument.putIdentifier(paramAttr.getValue(), this);
    } 
  }
  
  public void setIdAttribute(String paramString, boolean paramBoolean) {
    if (needsSyncData())
      synchronizeData(); 
    Attr attr = getAttributeNode(paramString);
    if (attr == null) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
      throw new DOMException((short)8, str);
    } 
    if (this.ownerDocument.errorChecking) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (attr.getOwnerElement() != this) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
        throw new DOMException((short)8, str);
      } 
    } 
    ((AttrImpl)attr).isIdAttribute(paramBoolean);
    if (!paramBoolean) {
      this.ownerDocument.removeIdentifier(attr.getValue());
    } else {
      this.ownerDocument.putIdentifier(attr.getValue(), this);
    } 
  }
  
  public void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean) {
    if (needsSyncData())
      synchronizeData(); 
    if (paramString1 != null)
      paramString1 = (paramString1.length() == 0) ? null : paramString1; 
    Attr attr = getAttributeNodeNS(paramString1, paramString2);
    if (attr == null) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
      throw new DOMException((short)8, str);
    } 
    if (this.ownerDocument.errorChecking) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (attr.getOwnerElement() != this) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
        throw new DOMException((short)8, str);
      } 
    } 
    ((AttrImpl)attr).isIdAttribute(paramBoolean);
    if (!paramBoolean) {
      this.ownerDocument.removeIdentifier(attr.getValue());
    } else {
      this.ownerDocument.putIdentifier(attr.getValue(), this);
    } 
  }
  
  public String getTypeName() { return null; }
  
  public String getTypeNamespace() { return null; }
  
  public boolean isDerivedFrom(String paramString1, String paramString2, int paramInt) { return false; }
  
  public TypeInfo getSchemaTypeInfo() {
    if (needsSyncData())
      synchronizeData(); 
    return this;
  }
  
  public void setReadOnly(boolean paramBoolean1, boolean paramBoolean2) {
    super.setReadOnly(paramBoolean1, paramBoolean2);
    if (this.attributes != null)
      this.attributes.setReadOnly(paramBoolean1, true); 
  }
  
  protected void synchronizeData() {
    needsSyncData(false);
    boolean bool = this.ownerDocument.getMutationEvents();
    this.ownerDocument.setMutationEvents(false);
    setupDefaultAttributes();
    this.ownerDocument.setMutationEvents(bool);
  }
  
  void moveSpecifiedAttributes(ElementImpl paramElementImpl) {
    if (needsSyncData())
      synchronizeData(); 
    if (paramElementImpl.hasAttributes()) {
      if (this.attributes == null)
        this.attributes = new AttributeMap(this, null); 
      this.attributes.moveSpecifiedAttributes(paramElementImpl.attributes);
    } 
  }
  
  protected void setupDefaultAttributes() {
    NamedNodeMapImpl namedNodeMapImpl = getDefaultAttributes();
    if (namedNodeMapImpl != null)
      this.attributes = new AttributeMap(this, namedNodeMapImpl); 
  }
  
  protected void reconcileDefaultAttributes() {
    if (this.attributes != null) {
      NamedNodeMapImpl namedNodeMapImpl = getDefaultAttributes();
      this.attributes.reconcileDefaults(namedNodeMapImpl);
    } 
  }
  
  protected NamedNodeMapImpl getDefaultAttributes() {
    DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)this.ownerDocument.getDoctype();
    if (documentTypeImpl == null)
      return null; 
    ElementDefinitionImpl elementDefinitionImpl = (ElementDefinitionImpl)documentTypeImpl.getElements().getNamedItem(getNodeName());
    return (elementDefinitionImpl == null) ? null : (NamedNodeMapImpl)elementDefinitionImpl.getAttributes();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\ElementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */