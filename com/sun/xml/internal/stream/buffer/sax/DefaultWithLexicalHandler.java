package com.sun.xml.internal.stream.buffer.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultWithLexicalHandler extends DefaultHandler implements LexicalHandler {
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void endDTD() {}
  
  public void startEntity(String paramString) throws SAXException {}
  
  public void endEntity(String paramString) throws SAXException {}
  
  public void startCDATA() {}
  
  public void endCDATA() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\sax\DefaultWithLexicalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */