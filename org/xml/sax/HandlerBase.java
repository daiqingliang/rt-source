package org.xml.sax;

public class HandlerBase implements EntityResolver, DTDHandler, DocumentHandler, ErrorHandler {
  public InputSource resolveEntity(String paramString1, String paramString2) throws SAXException { return null; }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) {}
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void startDocument() {}
  
  public void endDocument() {}
  
  public void startElement(String paramString, AttributeList paramAttributeList) throws SAXException {}
  
  public void endElement(String paramString) throws SAXException {}
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {}
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {}
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {}
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\HandlerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */