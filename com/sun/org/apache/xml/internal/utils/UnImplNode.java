package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class UnImplNode implements Node, Element, NodeList, Document {
  protected String fDocumentURI;
  
  protected String actualEncoding;
  
  private String xmlEncoding;
  
  private boolean xmlStandalone;
  
  private String xmlVersion;
  
  public void error(String paramString) {
    System.out.println("DOM ERROR! class: " + getClass().getName());
    throw new RuntimeException(XMLMessages.createXMLMessage(paramString, null));
  }
  
  public void error(String paramString, Object[] paramArrayOfObject) {
    System.out.println("DOM ERROR! class: " + getClass().getName());
    throw new RuntimeException(XMLMessages.createXMLMessage(paramString, paramArrayOfObject));
  }
  
  public Node appendChild(Node paramNode) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public boolean hasChildNodes() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return false;
  }
  
  public short getNodeType() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return 0;
  }
  
  public Node getParentNode() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public NodeList getChildNodes() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node getFirstChild() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node getLastChild() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node getNextSibling() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public int getLength() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return 0;
  }
  
  public Node item(int paramInt) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Document getOwnerDocument() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public String getTagName() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public String getNodeName() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public void normalize() { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public NodeList getElementsByTagName(String paramString) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Attr removeAttributeNode(Attr paramAttr) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Attr setAttributeNode(Attr paramAttr) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public boolean hasAttribute(String paramString) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return false;
  }
  
  public boolean hasAttributeNS(String paramString1, String paramString2) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return false;
  }
  
  public Attr getAttributeNode(String paramString) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public void removeAttribute(String paramString) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public void setAttribute(String paramString1, String paramString2) throws DOMException { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public String getAttribute(String paramString) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public boolean hasAttributes() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return false;
  }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Attr setAttributeNodeNS(Attr paramAttr) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Attr getAttributeNodeNS(String paramString1, String paramString2) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public void removeAttributeNS(String paramString1, String paramString2) throws DOMException { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public void setAttributeNS(String paramString1, String paramString2, String paramString3) throws DOMException { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public String getAttributeNS(String paramString1, String paramString2) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node getPreviousSibling() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node cloneNode(boolean paramBoolean) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public String getNodeValue() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public void setNodeValue(String paramString) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public void setValue(String paramString) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public Element getOwnerElement() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public boolean getSpecified() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return false;
  }
  
  public NamedNodeMap getAttributes() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node removeChild(Node paramNode) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public boolean isSupported(String paramString1, String paramString2) { return false; }
  
  public String getNamespaceURI() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public String getPrefix() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public void setPrefix(String paramString) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public String getLocalName() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public DocumentType getDoctype() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public DOMImplementation getImplementation() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Element getDocumentElement() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Element createElement(String paramString) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public DocumentFragment createDocumentFragment() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Text createTextNode(String paramString) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Comment createComment(String paramString) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public CDATASection createCDATASection(String paramString) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Attr createAttribute(String paramString) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public EntityReference createEntityReference(String paramString) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node importNode(Node paramNode, boolean paramBoolean) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Element createElementNS(String paramString1, String paramString2) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Attr createAttributeNS(String paramString1, String paramString2) {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Element getElementById(String paramString) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public void setData(String paramString) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public String substringData(int paramInt1, int paramInt2) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public void appendData(String paramString) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public void insertData(int paramInt, String paramString) throws DOMException { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public void deleteData(int paramInt1, int paramInt2) throws DOMException { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public void replaceData(int paramInt1, int paramInt2, String paramString) throws DOMException { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public Text splitText(int paramInt) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public Node adoptNode(Node paramNode) throws DOMException {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public String getInputEncoding() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public void setInputEncoding(String paramString) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public boolean getStandalone() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return false;
  }
  
  public void setStandalone(boolean paramBoolean) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public boolean getStrictErrorChecking() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return false;
  }
  
  public void setStrictErrorChecking(boolean paramBoolean) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public String getVersion() {
    error("ER_FUNCTION_NOT_SUPPORTED");
    return null;
  }
  
  public void setVersion(String paramString) { error("ER_FUNCTION_NOT_SUPPORTED"); }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler) { return getOwnerDocument().setUserData(paramString, paramObject, paramUserDataHandler); }
  
  public Object getUserData(String paramString) { return getOwnerDocument().getUserData(paramString); }
  
  public Object getFeature(String paramString1, String paramString2) { return isSupported(paramString1, paramString2) ? this : null; }
  
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
  
  public String lookupNamespaceURI(String paramString) {
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
        return null;
      case 6:
      case 10:
      case 11:
      case 12:
        return null;
      case 2:
        return (getOwnerElement().getNodeType() == 1) ? getOwnerElement().lookupNamespaceURI(paramString) : null;
    } 
    return null;
  }
  
  public boolean isDefaultNamespace(String paramString) { return false; }
  
  public String lookupPrefix(String paramString) {
    if (paramString == null)
      return null; 
    short s = getNodeType();
    switch (s) {
      case 6:
      case 10:
      case 11:
      case 12:
        return null;
      case 2:
        return (getOwnerElement().getNodeType() == 1) ? getOwnerElement().lookupPrefix(paramString) : null;
    } 
    return null;
  }
  
  public boolean isSameNode(Node paramNode) { return (this == paramNode); }
  
  public void setTextContent(String paramString) { setNodeValue(paramString); }
  
  public String getTextContent() { return getNodeValue(); }
  
  public short compareDocumentPosition(Node paramNode) throws DOMException { return 0; }
  
  public String getBaseURI() { return null; }
  
  public Node renameNode(Node paramNode, String paramString1, String paramString2) throws DOMException { return paramNode; }
  
  public void normalizeDocument() {}
  
  public DOMConfiguration getDomConfig() { return null; }
  
  public void setDocumentURI(String paramString) { this.fDocumentURI = paramString; }
  
  public String getDocumentURI() { return this.fDocumentURI; }
  
  public String getActualEncoding() { return this.actualEncoding; }
  
  public void setActualEncoding(String paramString) { this.actualEncoding = paramString; }
  
  public Text replaceWholeText(String paramString) { return null; }
  
  public String getWholeText() { return null; }
  
  public boolean isWhitespaceInElementContent() { return false; }
  
  public void setIdAttribute(boolean paramBoolean) {}
  
  public void setIdAttribute(String paramString, boolean paramBoolean) {}
  
  public void setIdAttributeNode(Attr paramAttr, boolean paramBoolean) {}
  
  public void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean) {}
  
  public TypeInfo getSchemaTypeInfo() { return null; }
  
  public boolean isId() { return false; }
  
  public String getXmlEncoding() { return this.xmlEncoding; }
  
  public void setXmlEncoding(String paramString) { this.xmlEncoding = paramString; }
  
  public boolean getXmlStandalone() { return this.xmlStandalone; }
  
  public void setXmlStandalone(boolean paramBoolean) { this.xmlStandalone = paramBoolean; }
  
  public String getXmlVersion() { return this.xmlVersion; }
  
  public void setXmlVersion(String paramString) { this.xmlVersion = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\UnImplNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */