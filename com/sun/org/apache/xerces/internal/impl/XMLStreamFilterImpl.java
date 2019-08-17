package com.sun.org.apache.xerces.internal.impl;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamFilterImpl implements XMLStreamReader {
  private StreamFilter fStreamFilter = null;
  
  private XMLStreamReader fStreamReader = null;
  
  private int fCurrentEvent;
  
  private boolean fEventAccepted = false;
  
  private boolean fStreamAdvancedByHasNext = false;
  
  public XMLStreamFilterImpl(XMLStreamReader paramXMLStreamReader, StreamFilter paramStreamFilter) {
    this.fStreamReader = paramXMLStreamReader;
    this.fStreamFilter = paramStreamFilter;
    try {
      if (this.fStreamFilter.accept(this.fStreamReader)) {
        this.fEventAccepted = true;
      } else {
        findNextEvent();
      } 
    } catch (XMLStreamException xMLStreamException) {
      System.err.println("Error while creating a stream Filter" + xMLStreamException);
    } 
  }
  
  protected void setStreamFilter(StreamFilter paramStreamFilter) { this.fStreamFilter = paramStreamFilter; }
  
  public int next() throws XMLStreamException {
    if (this.fStreamAdvancedByHasNext && this.fEventAccepted) {
      this.fStreamAdvancedByHasNext = false;
      return this.fCurrentEvent;
    } 
    int i = findNextEvent();
    if (i != -1)
      return i; 
    throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more  items to return");
  }
  
  public int nextTag() throws XMLStreamException {
    if (this.fStreamAdvancedByHasNext && this.fEventAccepted && (this.fCurrentEvent == 1 || this.fCurrentEvent == 1)) {
      this.fStreamAdvancedByHasNext = false;
      return this.fCurrentEvent;
    } 
    int i = findNextTag();
    if (i != -1)
      return i; 
    throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more  items to return");
  }
  
  public boolean hasNext() throws XMLStreamException {
    if (this.fStreamReader.hasNext()) {
      if (!this.fEventAccepted) {
        if ((this.fCurrentEvent = findNextEvent()) == -1)
          return false; 
        this.fStreamAdvancedByHasNext = true;
      } 
      return true;
    } 
    return false;
  }
  
  private int findNextEvent() throws XMLStreamException {
    this.fStreamAdvancedByHasNext = false;
    while (this.fStreamReader.hasNext()) {
      this.fCurrentEvent = this.fStreamReader.next();
      if (this.fStreamFilter.accept(this.fStreamReader)) {
        this.fEventAccepted = true;
        return this.fCurrentEvent;
      } 
    } 
    return (this.fCurrentEvent == 8) ? this.fCurrentEvent : -1;
  }
  
  private int findNextTag() throws XMLStreamException {
    this.fStreamAdvancedByHasNext = false;
    while (this.fStreamReader.hasNext()) {
      this.fCurrentEvent = this.fStreamReader.nextTag();
      if (this.fStreamFilter.accept(this.fStreamReader)) {
        this.fEventAccepted = true;
        return this.fCurrentEvent;
      } 
    } 
    return (this.fCurrentEvent == 8) ? this.fCurrentEvent : -1;
  }
  
  public void close() throws XMLStreamException { this.fStreamReader.close(); }
  
  public int getAttributeCount() throws XMLStreamException { return this.fStreamReader.getAttributeCount(); }
  
  public QName getAttributeName(int paramInt) { return this.fStreamReader.getAttributeName(paramInt); }
  
  public String getAttributeNamespace(int paramInt) { return this.fStreamReader.getAttributeNamespace(paramInt); }
  
  public String getAttributePrefix(int paramInt) { return this.fStreamReader.getAttributePrefix(paramInt); }
  
  public String getAttributeType(int paramInt) { return this.fStreamReader.getAttributeType(paramInt); }
  
  public String getAttributeValue(int paramInt) { return this.fStreamReader.getAttributeValue(paramInt); }
  
  public String getAttributeValue(String paramString1, String paramString2) { return this.fStreamReader.getAttributeValue(paramString1, paramString2); }
  
  public String getCharacterEncodingScheme() { return this.fStreamReader.getCharacterEncodingScheme(); }
  
  public String getElementText() { return this.fStreamReader.getElementText(); }
  
  public String getEncoding() { return this.fStreamReader.getEncoding(); }
  
  public int getEventType() throws XMLStreamException { return this.fStreamReader.getEventType(); }
  
  public String getLocalName() { return this.fStreamReader.getLocalName(); }
  
  public Location getLocation() { return this.fStreamReader.getLocation(); }
  
  public QName getName() { return this.fStreamReader.getName(); }
  
  public NamespaceContext getNamespaceContext() { return this.fStreamReader.getNamespaceContext(); }
  
  public int getNamespaceCount() throws XMLStreamException { return this.fStreamReader.getNamespaceCount(); }
  
  public String getNamespacePrefix(int paramInt) { return this.fStreamReader.getNamespacePrefix(paramInt); }
  
  public String getNamespaceURI() { return this.fStreamReader.getNamespaceURI(); }
  
  public String getNamespaceURI(int paramInt) { return this.fStreamReader.getNamespaceURI(paramInt); }
  
  public String getNamespaceURI(String paramString) { return this.fStreamReader.getNamespaceURI(paramString); }
  
  public String getPIData() { return this.fStreamReader.getPIData(); }
  
  public String getPITarget() { return this.fStreamReader.getPITarget(); }
  
  public String getPrefix() { return this.fStreamReader.getPrefix(); }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return this.fStreamReader.getProperty(paramString); }
  
  public String getText() { return this.fStreamReader.getText(); }
  
  public char[] getTextCharacters() { return this.fStreamReader.getTextCharacters(); }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException { return this.fStreamReader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3); }
  
  public int getTextLength() throws XMLStreamException { return this.fStreamReader.getTextLength(); }
  
  public int getTextStart() throws XMLStreamException { return this.fStreamReader.getTextStart(); }
  
  public String getVersion() { return this.fStreamReader.getVersion(); }
  
  public boolean hasName() throws XMLStreamException { return this.fStreamReader.hasName(); }
  
  public boolean hasText() throws XMLStreamException { return this.fStreamReader.hasText(); }
  
  public boolean isAttributeSpecified(int paramInt) { return this.fStreamReader.isAttributeSpecified(paramInt); }
  
  public boolean isCharacters() throws XMLStreamException { return this.fStreamReader.isCharacters(); }
  
  public boolean isEndElement() throws XMLStreamException { return this.fStreamReader.isEndElement(); }
  
  public boolean isStandalone() throws XMLStreamException { return this.fStreamReader.isStandalone(); }
  
  public boolean isStartElement() throws XMLStreamException { return this.fStreamReader.isStartElement(); }
  
  public boolean isWhiteSpace() throws XMLStreamException { return this.fStreamReader.isWhiteSpace(); }
  
  public void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException { this.fStreamReader.require(paramInt, paramString1, paramString2); }
  
  public boolean standaloneSet() throws XMLStreamException { return this.fStreamReader.standaloneSet(); }
  
  public String getAttributeLocalName(int paramInt) { return this.fStreamReader.getAttributeLocalName(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLStreamFilterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */