package com.sun.org.apache.xml.internal.serializer;

import java.util.Vector;

interface XSLOutputAttributes {
  String getDoctypePublic();
  
  String getDoctypeSystem();
  
  String getEncoding();
  
  boolean getIndent();
  
  int getIndentAmount();
  
  String getMediaType();
  
  boolean getOmitXMLDeclaration();
  
  String getStandalone();
  
  String getVersion();
  
  void setCdataSectionElements(Vector paramVector);
  
  void setDoctype(String paramString1, String paramString2);
  
  void setDoctypePublic(String paramString);
  
  void setDoctypeSystem(String paramString);
  
  void setEncoding(String paramString);
  
  void setIndent(boolean paramBoolean);
  
  void setMediaType(String paramString);
  
  void setOmitXMLDeclaration(boolean paramBoolean);
  
  void setStandalone(String paramString);
  
  void setVersion(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\XSLOutputAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */