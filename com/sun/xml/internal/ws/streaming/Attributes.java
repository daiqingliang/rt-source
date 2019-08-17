package com.sun.xml.internal.ws.streaming;

import javax.xml.namespace.QName;

public interface Attributes {
  int getLength();
  
  boolean isNamespaceDeclaration(int paramInt);
  
  QName getName(int paramInt);
  
  String getURI(int paramInt);
  
  String getLocalName(int paramInt);
  
  String getPrefix(int paramInt);
  
  String getValue(int paramInt);
  
  int getIndex(QName paramQName);
  
  int getIndex(String paramString1, String paramString2);
  
  int getIndex(String paramString);
  
  String getValue(QName paramQName);
  
  String getValue(String paramString1, String paramString2);
  
  String getValue(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\streaming\Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */