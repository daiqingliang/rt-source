package org.w3c.dom;

public interface Node {
  public static final short ELEMENT_NODE = 1;
  
  public static final short ATTRIBUTE_NODE = 2;
  
  public static final short TEXT_NODE = 3;
  
  public static final short CDATA_SECTION_NODE = 4;
  
  public static final short ENTITY_REFERENCE_NODE = 5;
  
  public static final short ENTITY_NODE = 6;
  
  public static final short PROCESSING_INSTRUCTION_NODE = 7;
  
  public static final short COMMENT_NODE = 8;
  
  public static final short DOCUMENT_NODE = 9;
  
  public static final short DOCUMENT_TYPE_NODE = 10;
  
  public static final short DOCUMENT_FRAGMENT_NODE = 11;
  
  public static final short NOTATION_NODE = 12;
  
  public static final short DOCUMENT_POSITION_DISCONNECTED = 1;
  
  public static final short DOCUMENT_POSITION_PRECEDING = 2;
  
  public static final short DOCUMENT_POSITION_FOLLOWING = 4;
  
  public static final short DOCUMENT_POSITION_CONTAINS = 8;
  
  public static final short DOCUMENT_POSITION_CONTAINED_BY = 16;
  
  public static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;
  
  String getNodeName();
  
  String getNodeValue();
  
  void setNodeValue(String paramString) throws DOMException;
  
  short getNodeType();
  
  Node getParentNode();
  
  NodeList getChildNodes();
  
  Node getFirstChild();
  
  Node getLastChild();
  
  Node getPreviousSibling();
  
  Node getNextSibling();
  
  NamedNodeMap getAttributes();
  
  Document getOwnerDocument();
  
  Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException;
  
  Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException;
  
  Node removeChild(Node paramNode) throws DOMException;
  
  Node appendChild(Node paramNode) throws DOMException;
  
  boolean hasChildNodes();
  
  Node cloneNode(boolean paramBoolean);
  
  void normalize();
  
  boolean isSupported(String paramString1, String paramString2);
  
  String getNamespaceURI();
  
  String getPrefix();
  
  void setPrefix(String paramString) throws DOMException;
  
  String getLocalName();
  
  boolean hasAttributes();
  
  String getBaseURI();
  
  short compareDocumentPosition(Node paramNode) throws DOMException;
  
  String getTextContent();
  
  void setTextContent(String paramString) throws DOMException;
  
  boolean isSameNode(Node paramNode);
  
  String lookupPrefix(String paramString);
  
  boolean isDefaultNamespace(String paramString);
  
  String lookupNamespaceURI(String paramString);
  
  boolean isEqualNode(Node paramNode);
  
  Object getFeature(String paramString1, String paramString2);
  
  Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler);
  
  Object getUserData(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */