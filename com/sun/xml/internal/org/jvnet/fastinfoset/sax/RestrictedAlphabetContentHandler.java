package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface RestrictedAlphabetContentHandler {
  void numericCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException;
  
  void dateTimeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException;
  
  void alphabetCharacters(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\RestrictedAlphabetContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */