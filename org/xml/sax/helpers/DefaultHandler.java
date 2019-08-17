package org.xml.sax.helpers;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultHandler implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler {
  public InputSource resolveEntity(String paramString1, String paramString2) throws IOException, SAXException { return null; }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {}
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void startDocument() {}
  
  public void endDocument() {}
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {}
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {}
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {}
  
  public void skippedEntity(String paramString) throws SAXException {}
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {}
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {}
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\helpers\DefaultHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */