package org.w3c.dom;

public interface NamedNodeMap {
  Node getNamedItem(String paramString);
  
  Node setNamedItem(Node paramNode) throws DOMException;
  
  Node removeNamedItem(String paramString);
  
  Node item(int paramInt);
  
  int getLength();
  
  Node getNamedItemNS(String paramString1, String paramString2) throws DOMException;
  
  Node setNamedItemNS(Node paramNode) throws DOMException;
  
  Node removeNamedItemNS(String paramString1, String paramString2) throws DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\NamedNodeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */