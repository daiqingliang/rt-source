package com.sun.xml.internal.fastinfoset.stax.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXParserWrapper implements XMLStreamReader {
  private XMLStreamReader _reader;
  
  public StAXParserWrapper() {}
  
  public StAXParserWrapper(XMLStreamReader paramXMLStreamReader) { this._reader = paramXMLStreamReader; }
  
  public void setReader(XMLStreamReader paramXMLStreamReader) { this._reader = paramXMLStreamReader; }
  
  public XMLStreamReader getReader() { return this._reader; }
  
  public int next() throws XMLStreamException { return this._reader.next(); }
  
  public int nextTag() throws XMLStreamException { return this._reader.nextTag(); }
  
  public String getElementText() throws XMLStreamException { return this._reader.getElementText(); }
  
  public void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException { this._reader.require(paramInt, paramString1, paramString2); }
  
  public boolean hasNext() throws XMLStreamException { return this._reader.hasNext(); }
  
  public void close() { this._reader.close(); }
  
  public String getNamespaceURI(String paramString) { return this._reader.getNamespaceURI(paramString); }
  
  public NamespaceContext getNamespaceContext() { return this._reader.getNamespaceContext(); }
  
  public boolean isStartElement() throws XMLStreamException { return this._reader.isStartElement(); }
  
  public boolean isEndElement() throws XMLStreamException { return this._reader.isEndElement(); }
  
  public boolean isCharacters() throws XMLStreamException { return this._reader.isCharacters(); }
  
  public boolean isWhiteSpace() throws XMLStreamException { return this._reader.isWhiteSpace(); }
  
  public QName getAttributeName(int paramInt) { return this._reader.getAttributeName(paramInt); }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException { return this._reader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3); }
  
  public String getAttributeValue(String paramString1, String paramString2) { return this._reader.getAttributeValue(paramString1, paramString2); }
  
  public int getAttributeCount() throws XMLStreamException { return this._reader.getAttributeCount(); }
  
  public String getAttributePrefix(int paramInt) { return this._reader.getAttributePrefix(paramInt); }
  
  public String getAttributeNamespace(int paramInt) { return this._reader.getAttributeNamespace(paramInt); }
  
  public String getAttributeLocalName(int paramInt) { return this._reader.getAttributeLocalName(paramInt); }
  
  public String getAttributeType(int paramInt) { return this._reader.getAttributeType(paramInt); }
  
  public String getAttributeValue(int paramInt) { return this._reader.getAttributeValue(paramInt); }
  
  public boolean isAttributeSpecified(int paramInt) { return this._reader.isAttributeSpecified(paramInt); }
  
  public int getNamespaceCount() throws XMLStreamException { return this._reader.getNamespaceCount(); }
  
  public String getNamespacePrefix(int paramInt) { return this._reader.getNamespacePrefix(paramInt); }
  
  public String getNamespaceURI(int paramInt) { return this._reader.getNamespaceURI(paramInt); }
  
  public int getEventType() throws XMLStreamException { return this._reader.getEventType(); }
  
  public String getText() throws XMLStreamException { return this._reader.getText(); }
  
  public char[] getTextCharacters() { return this._reader.getTextCharacters(); }
  
  public int getTextStart() throws XMLStreamException { return this._reader.getTextStart(); }
  
  public int getTextLength() throws XMLStreamException { return this._reader.getTextLength(); }
  
  public String getEncoding() throws XMLStreamException { return this._reader.getEncoding(); }
  
  public boolean hasText() throws XMLStreamException { return this._reader.hasText(); }
  
  public Location getLocation() { return this._reader.getLocation(); }
  
  public QName getName() { return this._reader.getName(); }
  
  public String getLocalName() throws XMLStreamException { return this._reader.getLocalName(); }
  
  public boolean hasName() throws XMLStreamException { return this._reader.hasName(); }
  
  public String getNamespaceURI() throws XMLStreamException { return this._reader.getNamespaceURI(); }
  
  public String getPrefix() throws XMLStreamException { return this._reader.getPrefix(); }
  
  public String getVersion() throws XMLStreamException { return this._reader.getVersion(); }
  
  public boolean isStandalone() throws XMLStreamException { return this._reader.isStandalone(); }
  
  public boolean standaloneSet() throws XMLStreamException { return this._reader.standaloneSet(); }
  
  public String getCharacterEncodingScheme() throws XMLStreamException { return this._reader.getCharacterEncodingScheme(); }
  
  public String getPITarget() throws XMLStreamException { return this._reader.getPITarget(); }
  
  public String getPIData() throws XMLStreamException { return this._reader.getPIData(); }
  
  public Object getProperty(String paramString) { return this._reader.getProperty(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\sta\\util\StAXParserWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */