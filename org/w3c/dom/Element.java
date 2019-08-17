package org.w3c.dom;

public interface Element extends Node {
  String getTagName();
  
  String getAttribute(String paramString);
  
  void setAttribute(String paramString1, String paramString2) throws DOMException;
  
  void removeAttribute(String paramString) throws DOMException;
  
  Attr getAttributeNode(String paramString);
  
  Attr setAttributeNode(Attr paramAttr) throws DOMException;
  
  Attr removeAttributeNode(Attr paramAttr) throws DOMException;
  
  NodeList getElementsByTagName(String paramString);
  
  String getAttributeNS(String paramString1, String paramString2) throws DOMException;
  
  void setAttributeNS(String paramString1, String paramString2, String paramString3) throws DOMException;
  
  void removeAttributeNS(String paramString1, String paramString2) throws DOMException;
  
  Attr getAttributeNodeNS(String paramString1, String paramString2) throws DOMException;
  
  Attr setAttributeNodeNS(Attr paramAttr) throws DOMException;
  
  NodeList getElementsByTagNameNS(String paramString1, String paramString2) throws DOMException;
  
  boolean hasAttribute(String paramString);
  
  boolean hasAttributeNS(String paramString1, String paramString2) throws DOMException;
  
  TypeInfo getSchemaTypeInfo();
  
  void setIdAttribute(String paramString, boolean paramBoolean) throws DOMException;
  
  void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean) throws DOMException;
  
  void setIdAttributeNode(Attr paramAttr, boolean paramBoolean) throws DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */