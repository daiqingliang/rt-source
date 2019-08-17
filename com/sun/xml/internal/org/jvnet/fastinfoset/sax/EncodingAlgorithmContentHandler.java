package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface EncodingAlgorithmContentHandler {
  void octets(String paramString, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) throws SAXException;
  
  void object(String paramString, int paramInt, Object paramObject) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\EncodingAlgorithmContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */