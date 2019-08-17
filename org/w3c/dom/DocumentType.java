package org.w3c.dom;

public interface DocumentType extends Node {
  String getName();
  
  NamedNodeMap getEntities();
  
  NamedNodeMap getNotations();
  
  String getPublicId();
  
  String getSystemId();
  
  String getInternalSubset();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\DocumentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */