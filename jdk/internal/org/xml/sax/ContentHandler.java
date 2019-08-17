package jdk.internal.org.xml.sax;

public interface ContentHandler {
  void setDocumentLocator(Locator paramLocator);
  
  void startDocument() throws SAXException;
  
  void endDocument() throws SAXException;
  
  void startPrefixMapping(String paramString1, String paramString2) throws SAXException;
  
  void endPrefixMapping(String paramString) throws SAXException;
  
  void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException;
  
  void endElement(String paramString1, String paramString2, String paramString3) throws SAXException;
  
  void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException;
  
  void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException;
  
  void processingInstruction(String paramString1, String paramString2) throws SAXException;
  
  void skippedEntity(String paramString) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\xml\sax\ContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */