package org.w3c.dom;

public interface Entity extends Node {
  String getPublicId();
  
  String getSystemId();
  
  String getNotationName();
  
  String getInputEncoding();
  
  String getXmlEncoding();
  
  String getXmlVersion();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\Entity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */