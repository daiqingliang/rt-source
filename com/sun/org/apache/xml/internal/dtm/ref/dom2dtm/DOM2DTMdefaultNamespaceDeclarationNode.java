package com.sun.org.apache.xml.internal.dtm.ref.dom2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class DOM2DTMdefaultNamespaceDeclarationNode implements Attr, TypeInfo {
  final String NOT_SUPPORTED_ERR = "Unsupported operation on pseudonode";
  
  Element pseudoparent;
  
  String prefix;
  
  String uri;
  
  String nodename;
  
  int handle;
  
  DOM2DTMdefaultNamespaceDeclarationNode(Element paramElement, String paramString1, String paramString2, int paramInt) {
    this.pseudoparent = paramElement;
    this.prefix = paramString1;
    this.uri = paramString2;
    this.handle = paramInt;
    this.nodename = "xmlns:" + paramString1;
  }
  
  public String getNodeName() { return this.nodename; }
  
  public String getName() { return this.nodename; }
  
  public String getNamespaceURI() { return "http://www.w3.org/2000/xmlns/"; }
  
  public String getPrefix() { return this.prefix; }
  
  public String getLocalName() { return this.prefix; }
  
  public String getNodeValue() { return this.uri; }
  
  public String getValue() { return this.uri; }
  
  public Element getOwnerElement() { return this.pseudoparent; }
  
  public boolean isSupported(String paramString1, String paramString2) { return false; }
  
  public boolean hasChildNodes() { return false; }
  
  public boolean hasAttributes() { return false; }
  
  public Node getParentNode() { return null; }
  
  public Node getFirstChild() { return null; }
  
  public Node getLastChild() { return null; }
  
  public Node getPreviousSibling() { return null; }
  
  public Node getNextSibling() { return null; }
  
  public boolean getSpecified() { return false; }
  
  public void normalize() {}
  
  public NodeList getChildNodes() { return null; }
  
  public NamedNodeMap getAttributes() { return null; }
  
  public short getNodeType() { return 2; }
  
  public void setNodeValue(String paramString) { throw new DTMException("Unsupported operation on pseudonode"); }
  
  public void setValue(String paramString) { throw new DTMException("Unsupported operation on pseudonode"); }
  
  public void setPrefix(String paramString) { throw new DTMException("Unsupported operation on pseudonode"); }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) { throw new DTMException("Unsupported operation on pseudonode"); }
  
  public Node replaceChild(Node paramNode1, Node paramNode2) { throw new DTMException("Unsupported operation on pseudonode"); }
  
  public Node appendChild(Node paramNode) { throw new DTMException("Unsupported operation on pseudonode"); }
  
  public Node removeChild(Node paramNode) { throw new DTMException("Unsupported operation on pseudonode"); }
  
  public Document getOwnerDocument() { return this.pseudoparent.getOwnerDocument(); }
  
  public Node cloneNode(boolean paramBoolean) { throw new DTMException("Unsupported operation on pseudonode"); }
  
  public int getHandleOfNode() { return this.handle; }
  
  public String getTypeName() { return null; }
  
  public String getTypeNamespace() { return null; }
  
  public boolean isDerivedFrom(String paramString1, String paramString2, int paramInt) { return false; }
  
  public TypeInfo getSchemaTypeInfo() { return this; }
  
  public boolean isId() { return false; }
  
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\dom2dtm\DOM2DTMdefaultNamespaceDeclarationNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */