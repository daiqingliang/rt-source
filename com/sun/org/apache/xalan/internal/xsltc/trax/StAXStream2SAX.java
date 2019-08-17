package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
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

public class StAXStream2SAX implements XMLReader, Locator {
  private final XMLStreamReader staxStreamReader;
  
  private ContentHandler _sax = null;
  
  private LexicalHandler _lex = null;
  
  private SAXImpl _saxImpl = null;
  
  public StAXStream2SAX(XMLStreamReader paramXMLStreamReader) { this.staxStreamReader = paramXMLStreamReader; }
  
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
  
  public void parse(String paramString) throws IOException, SAXException { throw new IOException("This method is not yet implemented."); }
  
  public void bridge() throws IOException, SAXException, XMLStreamException {
    try {
      byte b = 0;
      int i = this.staxStreamReader.getEventType();
      if (i == 7)
        i = this.staxStreamReader.next(); 
      if (i != 1) {
        i = this.staxStreamReader.nextTag();
        if (i != 1)
          throw new IllegalStateException("The current event is not START_ELEMENT\n but" + i); 
      } 
      handleStartDocument();
      do {
        switch (i) {
          case 1:
            b++;
            handleStartElement();
            break;
          case 2:
            handleEndElement();
            b--;
            break;
          case 4:
            handleCharacters();
            break;
          case 9:
            handleEntityReference();
            break;
          case 3:
            handlePI();
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
            throw new InternalError("processing event: " + i);
        } 
        i = this.staxStreamReader.next();
      } while (b != 0);
      handleEndDocument();
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private void handleEndDocument() throws IOException, SAXException, XMLStreamException { this._sax.endDocument(); }
  
  private void handleStartDocument() throws IOException, SAXException, XMLStreamException {
    this._sax.setDocumentLocator(new Locator2() {
          public int getColumnNumber() { return StAXStream2SAX.this.staxStreamReader.getLocation().getColumnNumber(); }
          
          public int getLineNumber() { return StAXStream2SAX.this.staxStreamReader.getLocation().getLineNumber(); }
          
          public String getPublicId() { return StAXStream2SAX.this.staxStreamReader.getLocation().getPublicId(); }
          
          public String getSystemId() { return StAXStream2SAX.this.staxStreamReader.getLocation().getSystemId(); }
          
          public String getXMLVersion() { return StAXStream2SAX.this.staxStreamReader.getVersion(); }
          
          public String getEncoding() { return StAXStream2SAX.this.staxStreamReader.getEncoding(); }
        });
    this._sax.startDocument();
  }
  
  private void handlePI() throws IOException, SAXException, XMLStreamException {
    try {
      this._sax.processingInstruction(this.staxStreamReader.getPITarget(), this.staxStreamReader.getPIData());
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private void handleCharacters() throws IOException, SAXException, XMLStreamException {
    int i = this.staxStreamReader.getTextLength();
    char[] arrayOfChar = new char[i];
    this.staxStreamReader.getTextCharacters(0, arrayOfChar, 0, i);
    try {
      this._sax.characters(arrayOfChar, 0, arrayOfChar.length);
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private void handleEndElement() throws IOException, SAXException, XMLStreamException {
    QName qName = this.staxStreamReader.getName();
    try {
      String str = "";
      if (qName.getPrefix() != null && qName.getPrefix().trim().length() != 0)
        str = qName.getPrefix() + ":"; 
      str = str + qName.getLocalPart();
      this._sax.endElement(qName.getNamespaceURI(), qName.getLocalPart(), str);
      int i = this.staxStreamReader.getNamespaceCount();
      for (int j = i - 1; j >= 0; j--) {
        String str1 = this.staxStreamReader.getNamespacePrefix(j);
        if (str1 == null)
          str1 = ""; 
        this._sax.endPrefixMapping(str1);
      } 
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private void handleStartElement() throws IOException, SAXException, XMLStreamException {
    try {
      String str2;
      int i = this.staxStreamReader.getNamespaceCount();
      for (byte b = 0; b < i; b++) {
        String str = this.staxStreamReader.getNamespacePrefix(b);
        if (str == null)
          str = ""; 
        this._sax.startPrefixMapping(str, this.staxStreamReader.getNamespaceURI(b));
      } 
      QName qName = this.staxStreamReader.getName();
      String str1 = qName.getPrefix();
      if (str1 == null || str1.length() == 0) {
        str2 = qName.getLocalPart();
      } else {
        str2 = str1 + ':' + qName.getLocalPart();
      } 
      Attributes attributes = getAttributes();
      this._sax.startElement(qName.getNamespaceURI(), qName.getLocalPart(), str2, attributes);
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  private Attributes getAttributes() {
    AttributesImpl attributesImpl = new AttributesImpl();
    int i = this.staxStreamReader.getEventType();
    if (i != 10 && i != 1)
      throw new InternalError("getAttributes() attempting to process: " + i); 
    for (byte b = 0; b < this.staxStreamReader.getAttributeCount(); b++) {
      String str4;
      String str1 = this.staxStreamReader.getAttributeNamespace(b);
      if (str1 == null)
        str1 = ""; 
      String str2 = this.staxStreamReader.getAttributeLocalName(b);
      String str3 = this.staxStreamReader.getAttributePrefix(b);
      if (str3 == null || str3.length() == 0) {
        str4 = str2;
      } else {
        str4 = str3 + ':' + str2;
      } 
      String str5 = this.staxStreamReader.getAttributeType(b);
      String str6 = this.staxStreamReader.getAttributeValue(b);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\StAXStream2SAX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */