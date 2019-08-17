package org.w3c.dom;

public interface Text extends CharacterData {
  Text splitText(int paramInt) throws DOMException;
  
  boolean isElementContentWhitespace();
  
  String getWholeText();
  
  Text replaceWholeText(String paramString) throws DOMException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\Text.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */