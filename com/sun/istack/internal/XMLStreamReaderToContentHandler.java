package com.sun.istack.internal;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLStreamReaderToContentHandler {
  private final XMLStreamReader staxStreamReader;
  
  private final ContentHandler saxHandler;
  
  private final boolean eagerQuit;
  
  private final boolean fragment;
  
  private final String[] inscopeNamespaces;
  
  public XMLStreamReaderToContentHandler(XMLStreamReader paramXMLStreamReader, ContentHandler paramContentHandler, boolean paramBoolean1, boolean paramBoolean2) { this(paramXMLStreamReader, paramContentHandler, paramBoolean1, paramBoolean2, new String[0]); }
  
  public XMLStreamReaderToContentHandler(XMLStreamReader paramXMLStreamReader, ContentHandler paramContentHandler, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString) {
    this.staxStreamReader = paramXMLStreamReader;
    this.saxHandler = paramContentHandler;
    this.eagerQuit = paramBoolean1;
    this.fragment = paramBoolean2;
    this.inscopeNamespaces = paramArrayOfString;
    assert paramArrayOfString.length % 2 == 0;
  }
  
  public void bridge() throws XMLStreamException {
    try {
      byte b = 0;
      int i = this.staxStreamReader.getEventType();
      if (i == 7)
        while (!this.staxStreamReader.isStartElement())
          i = this.staxStreamReader.next();  
      if (i != 1)
        throw new IllegalStateException("The current event is not START_ELEMENT\n but " + i); 
      handleStartDocument();
      boolean bool;
      for (bool = false; bool < this.inscopeNamespaces.length; bool += true)
        this.saxHandler.startPrefixMapping(this.inscopeNamespaces[bool], this.inscopeNamespaces[bool + true]); 
      do {
        switch (i) {
          case 1:
            b++;
            handleStartElement();
            break;
          case 2:
            handleEndElement();
            if (--b == 0 && this.eagerQuit)
              break; 
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
      for (bool = false; bool < this.inscopeNamespaces.length; bool += true)
        this.saxHandler.endPrefixMapping(this.inscopeNamespaces[bool]); 
      handleEndDocument();
    } catch (SAXException sAXException) {
      throw new XMLStreamException2(sAXException);
    } 
  }
  
  private void handleEndDocument() throws XMLStreamException {
    if (this.fragment)
      return; 
    this.saxHandler.endDocument();
  }
  
  private void handleStartDocument() throws XMLStreamException {
    if (this.fragment)
      return; 
    this.saxHandler.setDocumentLocator(new Locator() {
          public int getColumnNumber() { return XMLStreamReaderToContentHandler.this.staxStreamReader.getLocation().getColumnNumber(); }
          
          public int getLineNumber() { return XMLStreamReaderToContentHandler.this.staxStreamReader.getLocation().getLineNumber(); }
          
          public String getPublicId() { return XMLStreamReaderToContentHandler.this.staxStreamReader.getLocation().getPublicId(); }
          
          public String getSystemId() { return XMLStreamReaderToContentHandler.this.staxStreamReader.getLocation().getSystemId(); }
        });
    this.saxHandler.startDocument();
  }
  
  private void handlePI() throws XMLStreamException {
    try {
      this.saxHandler.processingInstruction(this.staxStreamReader.getPITarget(), this.staxStreamReader.getPIData());
    } catch (SAXException sAXException) {
      throw new XMLStreamException2(sAXException);
    } 
  }
  
  private void handleCharacters() throws XMLStreamException {
    try {
      this.saxHandler.characters(this.staxStreamReader.getTextCharacters(), this.staxStreamReader.getTextStart(), this.staxStreamReader.getTextLength());
    } catch (SAXException sAXException) {
      throw new XMLStreamException2(sAXException);
    } 
  }
  
  private void handleEndElement() throws XMLStreamException {
    QName qName = this.staxStreamReader.getName();
    try {
      String str1 = qName.getPrefix();
      String str2 = (str1 == null || str1.length() == 0) ? qName.getLocalPart() : (str1 + ':' + qName.getLocalPart());
      this.saxHandler.endElement(qName.getNamespaceURI(), qName.getLocalPart(), str2);
      int i = this.staxStreamReader.getNamespaceCount();
      for (int j = i - 1; j >= 0; j--) {
        String str = this.staxStreamReader.getNamespacePrefix(j);
        if (str == null)
          str = ""; 
        this.saxHandler.endPrefixMapping(str);
      } 
    } catch (SAXException sAXException) {
      throw new XMLStreamException2(sAXException);
    } 
  }
  
  private void handleStartElement() throws XMLStreamException {
    try {
      String str2;
      int i = this.staxStreamReader.getNamespaceCount();
      for (byte b = 0; b < i; b++)
        this.saxHandler.startPrefixMapping(fixNull(this.staxStreamReader.getNamespacePrefix(b)), fixNull(this.staxStreamReader.getNamespaceURI(b))); 
      QName qName = this.staxStreamReader.getName();
      String str1 = qName.getPrefix();
      if (str1 == null || str1.length() == 0) {
        str2 = qName.getLocalPart();
      } else {
        str2 = str1 + ':' + qName.getLocalPart();
      } 
      Attributes attributes = getAttributes();
      this.saxHandler.startElement(qName.getNamespaceURI(), qName.getLocalPart(), str2, attributes);
    } catch (SAXException sAXException) {
      throw new XMLStreamException2(sAXException);
    } 
  }
  
  private static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
  
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
  
  private void handleNamespace() throws XMLStreamException {}
  
  private void handleAttribute() throws XMLStreamException {}
  
  private void handleDTD() throws XMLStreamException {}
  
  private void handleComment() throws XMLStreamException {}
  
  private void handleEntityReference() throws XMLStreamException {}
  
  private void handleSpace() throws XMLStreamException {}
  
  private void handleNotationDecl() throws XMLStreamException {}
  
  private void handleEntityDecl() throws XMLStreamException {}
  
  private void handleCDATA() throws XMLStreamException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\XMLStreamReaderToContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */