package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

public class AttrImpl extends NodeImpl implements Attr, TypeInfo {
  static final long serialVersionUID = 7277707688218972102L;
  
  static final String DTD_URI = "http://www.w3.org/TR/REC-xml";
  
  protected Object value = null;
  
  protected String name;
  
  Object type;
  
  protected TextImpl textNode = null;
  
  protected AttrImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) {
    super(paramCoreDocumentImpl);
    this.name = paramString;
    isSpecified(true);
    hasStringValue(true);
  }
  
  protected AttrImpl() {}
  
  void rename(String paramString) {
    if (needsSyncData())
      synchronizeData(); 
    this.name = paramString;
  }
  
  protected void makeChildNode() {
    if (hasStringValue()) {
      if (this.value != null) {
        TextImpl textImpl = (TextImpl)ownerDocument().createTextNode((String)this.value);
        this.value = textImpl;
        textImpl.isFirstChild(true);
        textImpl.previousSibling = textImpl;
        textImpl.ownerNode = this;
        textImpl.isOwned(true);
      } 
      hasStringValue(false);
    } 
  }
  
  void setOwnerDocument(CoreDocumentImpl paramCoreDocumentImpl) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    super.setOwnerDocument(paramCoreDocumentImpl);
    if (!hasStringValue())
      for (ChildNode childNode = (ChildNode)this.value; childNode != null; childNode = childNode.nextSibling)
        childNode.setOwnerDocument(paramCoreDocumentImpl);  
  }
  
  public void setIdAttribute(boolean paramBoolean) {
    if (needsSyncData())
      synchronizeData(); 
    isIdAttribute(paramBoolean);
  }
  
  public boolean isId() { return isIdAttribute(); }
  
  public Node cloneNode(boolean paramBoolean) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    AttrImpl attrImpl = (AttrImpl)super.cloneNode(paramBoolean);
    if (!attrImpl.hasStringValue()) {
      attrImpl.value = null;
      for (Node node = (Node)this.value; node != null; node = node.getNextSibling())
        attrImpl.appendChild(node.cloneNode(true)); 
    } 
    attrImpl.isSpecified(true);
    return attrImpl;
  }
  
  public short getNodeType() { return 2; }
  
  public String getNodeName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.name;
  }
  
  public void setNodeValue(String paramString) { setValue(paramString); }
  
  public String getTypeName() { return (String)this.type; }
  
  public String getTypeNamespace() { return (this.type != null) ? "http://www.w3.org/TR/REC-xml" : null; }
  
  public TypeInfo getSchemaTypeInfo() { return this; }
  
  public String getNodeValue() { return getValue(); }
  
  public String getName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.name;
  }
  
  public void setValue(String paramString) {
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    if (coreDocumentImpl.errorChecking && isReadOnly()) {
      String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str1);
    } 
    Element element = getOwnerElement();
    String str = "";
    if (needsSyncData())
      synchronizeData(); 
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (this.value != null) {
      if (coreDocumentImpl.getMutationEvents()) {
        if (hasStringValue()) {
          str = (String)this.value;
          if (this.textNode == null) {
            this.textNode = (TextImpl)coreDocumentImpl.createTextNode((String)this.value);
          } else {
            this.textNode.data = (String)this.value;
          } 
          this.value = this.textNode;
          this.textNode.isFirstChild(true);
          this.textNode.previousSibling = this.textNode;
          this.textNode.ownerNode = this;
          this.textNode.isOwned(true);
          hasStringValue(false);
          internalRemoveChild(this.textNode, true);
        } else {
          str = getValue();
          while (this.value != null)
            internalRemoveChild((Node)this.value, true); 
        } 
      } else {
        if (hasStringValue()) {
          str = (String)this.value;
        } else {
          str = getValue();
          ChildNode childNode = (ChildNode)this.value;
          childNode.previousSibling = null;
          childNode.isFirstChild(false);
          childNode.ownerNode = coreDocumentImpl;
        } 
        this.value = null;
        needsSyncChildren(false);
      } 
      if (isIdAttribute() && element != null)
        coreDocumentImpl.removeIdentifier(str); 
    } 
    isSpecified(true);
    if (coreDocumentImpl.getMutationEvents()) {
      internalInsertBefore(coreDocumentImpl.createTextNode(paramString), null, true);
      hasStringValue(false);
      coreDocumentImpl.modifiedAttrValue(this, str);
    } else {
      this.value = paramString;
      hasStringValue(true);
      changed();
    } 
    if (isIdAttribute() && element != null)
      coreDocumentImpl.putIdentifier(paramString, element); 
  }
  
  public String getValue() {
    if (needsSyncData())
      synchronizeData(); 
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (this.value == null)
      return ""; 
    if (hasStringValue())
      return (String)this.value; 
    ChildNode childNode1 = (ChildNode)this.value;
    String str = null;
    if (childNode1.getNodeType() == 5) {
      str = ((EntityReferenceImpl)childNode1).getEntityRefValue();
    } else {
      str = childNode1.getNodeValue();
    } 
    ChildNode childNode2 = childNode1.nextSibling;
    if (childNode2 == null || str == null)
      return (str == null) ? "" : str; 
    StringBuffer stringBuffer = new StringBuffer(str);
    while (childNode2 != null) {
      if (childNode2.getNodeType() == 5) {
        str = ((EntityReferenceImpl)childNode2).getEntityRefValue();
        if (str == null)
          return ""; 
        stringBuffer.append(str);
      } else {
        stringBuffer.append(childNode2.getNodeValue());
      } 
      childNode2 = childNode2.nextSibling;
    } 
    return stringBuffer.toString();
  }
  
  public boolean getSpecified() {
    if (needsSyncData())
      synchronizeData(); 
    return isSpecified();
  }
  
  public Element getElement() { return (Element)(isOwned() ? this.ownerNode : null); }
  
  public Element getOwnerElement() { return (Element)(isOwned() ? this.ownerNode : null); }
  
  public void normalize() {
    if (isNormalized() || hasStringValue())
      return; 
    ChildNode childNode2 = (ChildNode)this.value;
    ChildNode childNode1 = childNode2;
    while (childNode1 != null) {
      Node node2 = childNode1.getNextSibling();
      if (childNode1.getNodeType() == 3)
        if (node2 != null && node2.getNodeType() == 3) {
          ((Text)childNode1).appendData(node2.getNodeValue());
          removeChild(node2);
          node2 = childNode1;
        } else if (childNode1.getNodeValue() == null || childNode1.getNodeValue().length() == 0) {
          removeChild(childNode1);
        }  
      Node node1 = node2;
    } 
    isNormalized(true);
  }
  
  public void setSpecified(boolean paramBoolean) {
    if (needsSyncData())
      synchronizeData(); 
    isSpecified(paramBoolean);
  }
  
  public void setType(Object paramObject) { this.type = paramObject; }
  
  public String toString() { return getName() + "=\"" + getValue() + "\""; }
  
  public boolean hasChildNodes() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return (this.value != null);
  }
  
  public NodeList getChildNodes() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this;
  }
  
  public Node getFirstChild() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    makeChildNode();
    return (Node)this.value;
  }
  
  public Node getLastChild() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return lastChild();
  }
  
  final ChildNode lastChild() {
    makeChildNode();
    return (this.value != null) ? ((ChildNode)this.value).previousSibling : null;
  }
  
  final void lastChild(ChildNode paramChildNode) {
    if (this.value != null)
      ((ChildNode)this.value).previousSibling = paramChildNode; 
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException { return internalInsertBefore(paramNode1, paramNode2, false); }
  
  Node internalInsertBefore(Node paramNode1, Node paramNode2, boolean paramBoolean) throws DOMException {
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    boolean bool = coreDocumentImpl.errorChecking;
    if (paramNode1.getNodeType() == 11) {
      if (bool)
        for (Node node = paramNode1.getFirstChild(); node != null; node = node.getNextSibling()) {
          if (!coreDocumentImpl.isKidOK(this, node)) {
            String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
            throw new DOMException((short)3, str);
          } 
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
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramNode1.getOwnerDocument() != coreDocumentImpl) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
        throw new DOMException((short)4, str);
      } 
      if (!coreDocumentImpl.isKidOK(this, paramNode1)) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
        throw new DOMException((short)3, str);
      } 
      if (paramNode2 != null && paramNode2.getParentNode() != this) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
        throw new DOMException((short)8, str);
      } 
      boolean bool1 = true;
      AttrImpl attrImpl = this;
      while (bool1 && attrImpl != null) {
        bool1 = (paramNode1 != attrImpl) ? 1 : 0;
        NodeImpl nodeImpl1 = attrImpl.parentNode();
      } 
      if (!bool1) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
        throw new DOMException((short)3, str);
      } 
    } 
    makeChildNode();
    coreDocumentImpl.insertingNode(this, paramBoolean);
    ChildNode childNode1 = (ChildNode)paramNode1;
    NodeImpl nodeImpl = childNode1.parentNode();
    if (nodeImpl != null)
      nodeImpl.removeChild(childNode1); 
    ChildNode childNode2 = (ChildNode)paramNode2;
    childNode1.ownerNode = this;
    childNode1.isOwned(true);
    ChildNode childNode3 = (ChildNode)this.value;
    if (childNode3 == null) {
      this.value = childNode1;
      childNode1.isFirstChild(true);
      childNode1.previousSibling = childNode1;
    } else if (childNode2 == null) {
      ChildNode childNode = childNode3.previousSibling;
      childNode.nextSibling = childNode1;
      childNode1.previousSibling = childNode;
      childNode3.previousSibling = childNode1;
    } else if (paramNode2 == childNode3) {
      childNode3.isFirstChild(false);
      childNode1.nextSibling = childNode3;
      childNode1.previousSibling = childNode3.previousSibling;
      childNode3.previousSibling = childNode1;
      this.value = childNode1;
      childNode1.isFirstChild(true);
    } else {
      ChildNode childNode = childNode2.previousSibling;
      childNode1.nextSibling = childNode2;
      childNode.nextSibling = childNode1;
      childNode2.previousSibling = childNode1;
      childNode1.previousSibling = childNode;
    } 
    changed();
    coreDocumentImpl.insertedNode(this, childNode1, paramBoolean);
    checkNormalizationAfterInsert(childNode1);
    return paramNode1;
  }
  
  public Node removeChild(Node paramNode) throws DOMException {
    if (hasStringValue()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
      throw new DOMException((short)8, str);
    } 
    return internalRemoveChild(paramNode, false);
  }
  
  Node internalRemoveChild(Node paramNode, boolean paramBoolean) throws DOMException {
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    if (coreDocumentImpl.errorChecking) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramNode != null && paramNode.getParentNode() != this) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
        throw new DOMException((short)8, str);
      } 
    } 
    ChildNode childNode1 = (ChildNode)paramNode;
    coreDocumentImpl.removingNode(this, childNode1, paramBoolean);
    if (childNode1 == this.value) {
      childNode1.isFirstChild(false);
      this.value = childNode1.nextSibling;
      ChildNode childNode = (ChildNode)this.value;
      if (childNode != null) {
        childNode.isFirstChild(true);
        childNode.previousSibling = childNode1.previousSibling;
      } 
    } else {
      ChildNode childNode3 = childNode1.previousSibling;
      ChildNode childNode4 = childNode1.nextSibling;
      childNode3.nextSibling = childNode4;
      if (childNode4 == null) {
        ChildNode childNode = (ChildNode)this.value;
        childNode.previousSibling = childNode3;
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
    makeChildNode();
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    coreDocumentImpl.replacingNode(this);
    internalInsertBefore(paramNode1, paramNode2, true);
    if (paramNode1 != paramNode2)
      internalRemoveChild(paramNode2, true); 
    coreDocumentImpl.replacedNode(this);
    return paramNode2;
  }
  
  public int getLength() {
    if (hasStringValue())
      return 1; 
    ChildNode childNode = (ChildNode)this.value;
    byte b = 0;
    while (childNode != null) {
      b++;
      childNode = childNode.nextSibling;
    } 
    return b;
  }
  
  public Node item(int paramInt) {
    if (hasStringValue()) {
      if (paramInt != 0 || this.value == null)
        return null; 
      makeChildNode();
      return (Node)this.value;
    } 
    if (paramInt < 0)
      return null; 
    ChildNode childNode = (ChildNode)this.value;
    for (byte b = 0; b < paramInt && childNode != null; b++)
      childNode = childNode.nextSibling; 
    return childNode;
  }
  
  public boolean isEqualNode(Node paramNode) { return super.isEqualNode(paramNode); }
  
  public boolean isDerivedFrom(String paramString1, String paramString2, int paramInt) { return false; }
  
  public void setReadOnly(boolean paramBoolean1, boolean paramBoolean2) {
    super.setReadOnly(paramBoolean1, paramBoolean2);
    if (paramBoolean2) {
      if (needsSyncChildren())
        synchronizeChildren(); 
      if (hasStringValue())
        return; 
      for (ChildNode childNode = (ChildNode)this.value; childNode != null; childNode = childNode.nextSibling) {
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\AttrImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */