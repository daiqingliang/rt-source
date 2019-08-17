package org.w3c.dom;

public interface Document extends Node {
  DocumentType getDoctype();
  
  DOMImplementation getImplementation();
  
  Element getDocumentElement();
  
  Element createElement(String paramString) throws DOMException;
  
  DocumentFragment createDocumentFragment();
  
  Text createTextNode(String paramString);
  
  Comment createComment(String paramString);
  
  CDATASection createCDATASection(String paramString) throws DOMException;
  
  ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) throws DOMException;
  
  Attr createAttribute(String paramString) throws DOMException;
  
  EntityReference createEntityReference(String paramString) throws DOMException;
  
  NodeList getElementsByTagName(String paramString);
  
  Node importNode(Node paramNode, boolean paramBoolean) throws DOMException;
  
  Element createElementNS(String paramString1, String paramString2) throws DOMException;
  
  Attr createAttributeNS(String paramString1, String paramString2) throws DOMException;
  
  NodeList getElementsByTagNameNS(String paramString1, String paramString2);
  
  Element getElementById(String paramString) throws DOMException;
  
  String getInputEncoding();
  
  String getXmlEncoding();
  
  boolean getXmlStandalone();
  
  void setXmlStandalone(boolean paramBoolean) throws DOMException;
  
  String getXmlVersion();
  
  void setXmlVersion(String paramString) throws DOMException;
  
  boolean getStrictErrorChecking();
  
  void setStrictErrorChecking(boolean paramBoolean) throws DOMException;
  
  String getDocumentURI();
  
  void setDocumentURI(String paramString) throws DOMException;
  
  Node adoptNode(Node paramNode) throws DOMException;
  
  DOMConfiguration getDomConfig();
  
  void normalizeDocument();
  
  Node renameNode(Node paramNode, String paramString1, String paramString2) throws DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\Document.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */