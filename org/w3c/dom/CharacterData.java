package org.w3c.dom;

public interface CharacterData extends Node {
  String getData() throws DOMException;
  
  void setData(String paramString) throws DOMException;
  
  int getLength();
  
  String substringData(int paramInt1, int paramInt2) throws DOMException;
  
  void appendData(String paramString) throws DOMException;
  
  void insertData(int paramInt, String paramString) throws DOMException;
  
  void deleteData(int paramInt1, int paramInt2) throws DOMException;
  
  void replaceData(int paramInt1, int paramInt2, String paramString) throws DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\CharacterData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */