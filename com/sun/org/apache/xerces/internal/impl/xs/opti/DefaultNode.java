package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class DefaultNode implements Node {
  public String getNodeName() { return null; }
  
  public String getNodeValue() { return null; }
  
  public short getNodeType() { return -1; }
  
  public Node getParentNode() { return null; }
  
  public NodeList getChildNodes() { return null; }
  
  public Node getFirstChild() { return null; }
  
  public Node getLastChild() { return null; }
  
  public Node getPreviousSibling() { return null; }
  
  public Node getNextSibling() { return null; }
  
  public NamedNodeMap getAttributes() { return null; }
  
  public Document getOwnerDocument() { return null; }
  
  public boolean hasChildNodes() { return false; }
  
  public Node cloneNode(boolean paramBoolean) { return null; }
  
  public void normalize() {}
  
  public boolean isSupported(String paramString1, String paramString2) { return false; }
  
  public String getNamespaceURI() { return null; }
  
  public String getPrefix() { return null; }
  
  public String getLocalName() { return null; }
  
  public String getBaseURI() { return null; }
  
  public boolean hasAttributes() { return false; }
  
  public void setNodeValue(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Node removeChild(Node paramNode) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Node appendChild(Node paramNode) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void setPrefix(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public short compareDocumentPosition(Node paramNode) { throw new DOMException((short)9, "Method not supported"); }
  
  public String getTextContent() { throw new DOMException((short)9, "Method not supported"); }
  
  public void setTextContent(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public boolean isSameNode(Node paramNode) { throw new DOMException((short)9, "Method not supported"); }
  
  public String lookupPrefix(String paramString) { throw new DOMException((short)9, "Method not supported"); }
  
  public boolean isDefaultNamespace(String paramString) { throw new DOMException((short)9, "Method not supported"); }
  
  public String lookupNamespaceURI(String paramString) { throw new DOMException((short)9, "Method not supported"); }
  
  public boolean isEqualNode(Node paramNode) { throw new DOMException((short)9, "Method not supported"); }
  
  public Object getFeature(String paramString1, String paramString2) { return null; }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler) { throw new DOMException((short)9, "Method not supported"); }
  
  public Object getUserData(String paramString) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\DefaultNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */