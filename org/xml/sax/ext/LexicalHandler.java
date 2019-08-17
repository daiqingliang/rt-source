package org.xml.sax.ext;

import org.xml.sax.SAXException;

public interface LexicalHandler {
  void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException;
  
  void endDTD() throws SAXException;
  
  void startEntity(String paramString) throws SAXException;
  
  void endEntity(String paramString) throws SAXException;
  
  void startCDATA() throws SAXException;
  
  void endCDATA() throws SAXException;
  
  void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\ext\LexicalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */