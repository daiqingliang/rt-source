package com.sun.org.apache.xerces.internal.xni;

public interface XMLLocator {
  String getPublicId();
  
  String getLiteralSystemId();
  
  String getBaseSystemId();
  
  String getExpandedSystemId();
  
  int getLineNumber();
  
  int getColumnNumber();
  
  int getCharacterOffset();
  
  String getEncoding();
  
  String getXMLVersion();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\XMLLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */