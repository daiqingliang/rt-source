package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
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
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;

public class StAXEvent2SAX implements XMLReader, Locator {
  private final XMLEventReader staxEventReader;
  
  private ContentHandler _sax = null;
  
  private LexicalHandler _lex = null;
  
  private SAXImpl _saxImpl = null;
  
  private String version = null;
  
  private String encoding = null;
  
  public StAXEvent2SAX(XMLEventReader paramXMLEventReader) { this.staxEventReader = paramXMLEventReader; }
  
  public ContentHandler getContentHandler() { return this._sax; }
  
  public void setContentHandler(ContentHandler paramContentHandler) throws NullPointerException {
    this._sax = paramContentHandler;
    if (paramContentHandler instanceof LexicalHandler)
      this._lex = (LexicalHandler)paramContentHandler; 
    if (paramContentHandler instanceof SAXImpl)
      this._saxImpl = (SAXImpl)paramContentHandler; 
  }
  
  public void parse(InputSource paramInputSource) throws IOException, SAXException {
    try {
      bridge();
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void parse() throws IOException, SAXException, XMLStreamException { bridge(); }
  
  private void bridge() throws IOException, SAXException, XMLStreamException {
    try {
      byte b = 0;
      boolean bool = false;
      XMLEvent xMLEvent = this.staxEventReader.peek();
      if (!xMLEvent.isStartDocument() && !xMLEvent.isStartElement())
        throw new IllegalStateException(); 
      if (xMLEvent.getEventType() == 7) {
        bool = true;
        this.version = ((StartDocument)xMLEvent).getVersion();
        if (((StartDocument)xMLEvent).encodingSet())
          this.encoding = ((StartDocument)xMLEvent).getCharacterEncodingScheme(); 
        xMLEvent = this.staxEventReader.nextEvent();
        xMLEvent = this.staxEventReader.nextEvent();
      } 
      handleStartDocument(xMLEvent);
      while (xMLEvent.getEventType() != 1) {
        switch (xMLEvent.getEventType()) {
          case 4:
            handleCharacters(xMLEvent.asCharacters());
            break;
          case 3:
            handlePI((ProcessingInstruction)xMLEvent);
            break;
          case 5:
            handleComment();
            break;
          case 11:
            handleDTD();
            break;
          case 6:
            handleSpace();
            break;
          default:
            throw new InternalError("processing prolog event: " + xMLEvent);
        } 
        xMLEvent = this.staxEventReader.nextEvent();
      } 
      do {
        switch (xMLEvent.getEventType()) {
          case 1:
            b++;
            handleStartElement(xMLEvent.asStartElement());
            break;
          case 2:
            handleEndElement(xMLEvent.asEndElement());
            b--;
            break;
          case 4:
            handleCharacters(xMLEvent.asCharacters());
            break;
          case 9:
            handleEntityReference();
            break;
          case 3:
            handlePI((ProcessingInstruction)xMLEvent);
            break;
          case 5:
            handleComment();
            break;
          case 11:
            handleDTD();
            break;
          case 10:
            handleAttribute();
            break;
          case 13:
            handleNamespace();
            break;
          case 12:
            handleCDATA();
            break;
          case 15:
            handleEntityDecl();
            break;
          case 14:
            handleNotationDecl();
            break;
          case 6:
            handleSpace();
            break;
          default:
            throw new InternalError("processing event: " + xMLEvent);
        } 
        xMLEvent = this.staxEventReader.nextEvent();
      } while (b != 0);
      if (bool)
        while (xMLEvent.getEventType() != 8) {
          switch (xMLEvent.getEventType()) {
            case 4:
              handleCharacters(xMLEvent.asCharacters());
              break;
            case 3:
              handlePI((ProcessingInstruction)xMLEvent);
              break;
            case 5:
              handleComment();
              break;
            case 6:
              handleSpace();
              break;
            default:
              throw new InternalError("processing misc event after document element: " + xMLEvent);
          } 
          xMLEvent = this.staxEventReader.nextEvent();
        }  
      handleEndDocument();
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private void handleEndDocument() throws IOException, SAXException, XMLStreamException { this._sax.endDocument(); }
  
  private void handleStartDocument(final XMLEvent event) throws SAXException {
    this._sax.setDocumentLocator(new Locator2() {
          public int getColumnNumber() { return event.getLocation().getColumnNumber(); }
          
          public int getLineNumber() { return event.getLocation().getLineNumber(); }
          
          public String getPublicId() { return event.getLocation().getPublicId(); }
          
          public String getSystemId() { return event.getLocation().getSystemId(); }
          
          public String getXMLVersion() { return StAXEvent2SAX.this.version; }
          
          public String getEncoding() { return StAXEvent2SAX.this.encoding; }
        });
    this._sax.startDocument();
  }
  
  private void handlePI(ProcessingInstruction paramProcessingInstruction) throws XMLStreamException {
    try {
      this._sax.processingInstruction(paramProcessingInstruction.getTarget(), paramProcessingInstruction.getData());
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private void handleCharacters(Characters paramCharacters) throws XMLStreamException {
    try {
      this._sax.characters(paramCharacters.getData().toCharArray(), 0, paramCharacters.getData().length());
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private void handleEndElement(EndElement paramEndElement) throws XMLStreamException {
    QName qName = paramEndElement.getName();
    String str = "";
    if (qName.getPrefix() != null && qName.getPrefix().trim().length() != 0)
      str = qName.getPrefix() + ":"; 
    str = str + qName.getLocalPart();
    try {
      this._sax.endElement(qName.getNamespaceURI(), qName.getLocalPart(), str);
      Iterator iterator = paramEndElement.getNamespaces();
      while (iterator.hasNext()) {
        String str1 = (String)iterator.next();
        if (str1 == null)
          str1 = ""; 
        this._sax.endPrefixMapping(str1);
      } 
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private void handleStartElement(StartElement paramStartElement) throws XMLStreamException {
    try {
      String str2;
      Iterator iterator = paramStartElement.getNamespaces();
      while (iterator.hasNext()) {
        String str = ((Namespace)iterator.next()).getPrefix();
        if (str == null)
          str = ""; 
        this._sax.startPrefixMapping(str, paramStartElement.getNamespaceURI(str));
      } 
      QName qName = paramStartElement.getName();
      String str1 = qName.getPrefix();
      if (str1 == null || str1.length() == 0) {
        str2 = qName.getLocalPart();
      } else {
        str2 = str1 + ':' + qName.getLocalPart();
      } 
      Attributes attributes = getAttributes(paramStartElement);
      this._sax.startElement(qName.getNamespaceURI(), qName.getLocalPart(), str2, attributes);
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private Attributes getAttributes(StartElement paramStartElement) {
    AttributesImpl attributesImpl = new AttributesImpl();
    if (!paramStartElement.isStartElement())
      throw new InternalError("getAttributes() attempting to process: " + paramStartElement); 
    Iterator iterator = paramStartElement.getAttributes();
    while (iterator.hasNext()) {
      String str4;
      Attribute attribute = (Attribute)iterator.next();
      String str1 = attribute.getName().getNamespaceURI();
      if (str1 == null)
        str1 = ""; 
      String str2 = attribute.getName().getLocalPart();
      String str3 = attribute.getName().getPrefix();
      if (str3 == null || str3.length() == 0) {
        str4 = str2;
      } else {
        str4 = str3 + ':' + str2;
      } 
      String str5 = attribute.getDTDType();
      String str6 = attribute.getValue();
      attributesImpl.addAttribute(str1, str2, str4, str5, str6);
    } 
    return attributesImpl;
  }
  
  private void handleNamespace() throws IOException, SAXException, XMLStreamException {}
  
  private void handleAttribute() throws IOException, SAXException, XMLStreamException {}
  
  private void handleDTD() throws IOException, SAXException, XMLStreamException {}
  
  private void handleComment() throws IOException, SAXException, XMLStreamException {}
  
  private void handleEntityReference() throws IOException, SAXException, XMLStreamException {}
  
  private void handleSpace() throws IOException, SAXException, XMLStreamException {}
  
  private void handleNotationDecl() throws IOException, SAXException, XMLStreamException {}
  
  private void handleEntityDecl() throws IOException, SAXException, XMLStreamException {}
  
  private void handleCDATA() throws IOException, SAXException, XMLStreamException {}
  
  public DTDHandler getDTDHandler() { return null; }
  
  public ErrorHandler getErrorHandler() { return null; }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException { return false; }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {}
  
  public void parse(String paramString) throws IOException, SAXException { throw new IOException("This method is not yet implemented."); }
  
  public void setDTDHandler(DTDHandler paramDTDHandler) throws NullPointerException {}
  
  public void setEntityResolver(EntityResolver paramEntityResolver) throws NullPointerException {}
  
  public EntityResolver getEntityResolver() { return null; }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) throws NullPointerException {}
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {}
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException { return null; }
  
  public int getColumnNumber() { return 0; }
  
  public int getLineNumber() { return 0; }
  
  public String getPublicId() { return null; }
  
  public String getSystemId() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\StAXEvent2SAX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */