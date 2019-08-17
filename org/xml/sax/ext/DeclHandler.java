package org.xml.sax.ext;

import org.xml.sax.SAXException;

public interface DeclHandler {
  void elementDecl(String paramString1, String paramString2) throws SAXException;
  
  void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException;
  
  void internalEntityDecl(String paramString1, String paramString2) throws SAXException;
  
  void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\ext\DeclHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */