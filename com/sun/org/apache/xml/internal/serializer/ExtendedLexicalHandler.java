package com.sun.org.apache.xml.internal.serializer;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

interface ExtendedLexicalHandler extends LexicalHandler {
  void comment(String paramString) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ExtendedLexicalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */