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
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public class XMLFilterImpl implements XMLFilter, EntityResolver, DTDHandler, ContentHandler, ErrorHandler {
  private XMLReader parent = null;
  
  private Locator locator = null;
  
  private EntityResolver entityResolver = null;
  
  private DTDHandler dtdHandler = null;
  
  private ContentHandler contentHandler = null;
  
  private ErrorHandler errorHandler = null;
  
  public XMLFilterImpl() {}
  
  public XMLFilterImpl(XMLReader paramXMLReader) { setParent(paramXMLReader); }
  
  public void setParent(XMLReader paramXMLReader) { this.parent = paramXMLReader; }
  
  public XMLReader getParent() { return this.parent; }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (this.parent != null) {
      this.parent.setFeature(paramString, paramBoolean);
    } else {
      throw new SAXNotRecognizedException("Feature: " + paramString);
    } 
  }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (this.parent != null)
      return this.parent.getFeature(paramString); 
    throw new SAXNotRecognizedException("Feature: " + paramString);
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (this.parent != null) {
      this.parent.setProperty(paramString, paramObject);
    } else {
      throw new SAXNotRecognizedException("Property: " + paramString);
    } 
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (this.parent != null)
      return this.parent.getProperty(paramString); 
    throw new SAXNotRecognizedException("Property: " + paramString);
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver) { this.entityResolver = paramEntityResolver; }
  
  public EntityResolver getEntityResolver() { return this.entityResolver; }
  
  public void setDTDHandler(DTDHandler paramDTDHandler) { this.dtdHandler = paramDTDHandler; }
  
  public DTDHandler getDTDHandler() { return this.dtdHandler; }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this.contentHandler = paramContentHandler; }
  
  public ContentHandler getContentHandler() { return this.contentHandler; }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) { this.errorHandler = paramErrorHandler; }
  
  public ErrorHandler getErrorHandler() { return this.errorHandler; }
  
  public void parse(InputSource paramInputSource) throws SAXException, IOException {
    setupParse();
    this.parent.parse(paramInputSource);
  }
  
  public void parse(String paramString) throws SAXException, IOException { parse(new InputSource(paramString)); }
  
  public InputSource resolveEntity(String paramString1, String paramString2) throws SAXException, IOException { return (this.entityResolver != null) ? this.entityResolver.resolveEntity(paramString1, paramString2) : null; }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.dtdHandler != null)
      this.dtdHandler.notationDecl(paramString1, paramString2, paramString3); 
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {
    if (this.dtdHandler != null)
      this.dtdHandler.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4); 
  }
  
  public void setDocumentLocator(Locator paramLocator) {
    this.locator = paramLocator;
    if (this.contentHandler != null)
      this.contentHandler.setDocumentLocator(paramLocator); 
  }
  
  public void startDocument() {
    if (this.contentHandler != null)
      this.contentHandler.startDocument(); 
  }
  
  public void endDocument() {
    if (this.contentHandler != null)
      this.contentHandler.endDocument(); 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.startPrefixMapping(paramString1, paramString2); 
  }
  
  public void endPrefixMapping(String paramString) throws SAXException, IOException {
    if (this.contentHandler != null)
      this.contentHandler.endPrefixMapping(paramString); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.startElement(paramString1, paramString2, paramString3, paramAttributes); 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.endElement(paramString1, paramString2, paramString3); 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.characters(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.processingInstruction(paramString1, paramString2); 
  }
  
  public void skippedEntity(String paramString) throws SAXException, IOException {
    if (this.contentHandler != null)
      this.contentHandler.skippedEntity(paramString); 
  }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {
    if (this.errorHandler != null)
      this.errorHandler.warning(paramSAXParseException); 
  }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {
    if (this.errorHandler != null)
      this.errorHandler.error(paramSAXParseException); 
  }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException {
    if (this.errorHandler != null)
      this.errorHandler.fatalError(paramSAXParseException); 
  }
  
  private void setupParse() {
    if (this.parent == null)
      throw new NullPointerException("No parent for filter"); 
    this.parent.setEntityResolver(this);
    this.parent.setDTDHandler(this);
    this.parent.setContentHandler(this);
    this.parent.setErrorHandler(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\helpers\XMLFilterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */