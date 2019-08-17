package com.sun.org.apache.xpath.internal.domapi;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.xpath.XPathNamespace;

class XPathNamespaceImpl implements XPathNamespace {
  private final Node m_attributeNode;
  
  private String textContent;
  
  XPathNamespaceImpl(Node paramNode) { this.m_attributeNode = paramNode; }
  
  public Element getOwnerElement() { return ((Attr)this.m_attributeNode).getOwnerElement(); }
  
  public String getNodeName() { return "#namespace"; }
  
  public String getNodeValue() { return this.m_attributeNode.getNodeValue(); }
  
  public void setNodeValue(String paramString) throws DOMException {}
  
  public short getNodeType() { return 13; }
  
  public Node getParentNode() { return this.m_attributeNode.getParentNode(); }
  
  public NodeList getChildNodes() { return this.m_attributeNode.getChildNodes(); }
  
  public Node getFirstChild() { return this.m_attributeNode.getFirstChild(); }
  
  public Node getLastChild() { return this.m_attributeNode.getLastChild(); }
  
  public Node getPreviousSibling() { return this.m_attributeNode.getPreviousSibling(); }
  
  public Node getNextSibling() { return this.m_attributeNode.getNextSibling(); }
  
  public NamedNodeMap getAttributes() { return this.m_attributeNode.getAttributes(); }
  
  public Document getOwnerDocument() { return this.m_attributeNode.getOwnerDocument(); }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException { return null; }
  
  public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException { return null; }
  
  public Node removeChild(Node paramNode) throws DOMException { return null; }
  
  public Node appendChild(Node paramNode) throws DOMException { return null; }
  
  public boolean hasChildNodes() { return false; }
  
  public Node cloneNode(boolean paramBoolean) { throw new DOMException((short)9, null); }
  
  public void normalize() { this.m_attributeNode.normalize(); }
  
  public boolean isSupported(String paramString1, String paramString2) { return this.m_attributeNode.isSupported(paramString1, paramString2); }
  
  public String getNamespaceURI() { return this.m_attributeNode.getNodeValue(); }
  
  public String getPrefix() { return this.m_attributeNode.getPrefix(); }
  
  public void setPrefix(String paramString) throws DOMException {}
  
  public String getLocalName() { return this.m_attributeNode.getPrefix(); }
  
  public boolean hasAttributes() { return this.m_attributeNode.hasAttributes(); }
  
  public String getBaseURI() { return null; }
  
  public short compareDocumentPosition(Node paramNode) throws DOMException { return 0; }
  
  public String getTextContent() { return this.textContent; }
  
  public void setTextContent(String paramString) throws DOMException { this.textContent = paramString; }
  
  public boolean isSameNode(Node paramNode) { return false; }
  
  public String lookupPrefix(String paramString) { return ""; }
  
  public boolean isDefaultNamespace(String paramString) { return false; }
  
  public String lookupNamespaceURI(String paramString) { return null; }
  
  public boolean isEqualNode(Node paramNode) { return false; }
  
  public Object getFeature(String paramString1, String paramString2) { return null; }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler) { return null; }
  
  public Object getUserData(String paramString) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathNamespaceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */