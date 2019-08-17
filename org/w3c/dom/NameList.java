package org.w3c.dom;

public interface NameList {
  String getName(int paramInt);
  
  String getNamespaceURI(int paramInt);
  
  int getLength();
  
  boolean contains(String paramString);
  
  boolean containsNS(String paramString1, String paramString2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\NameList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */