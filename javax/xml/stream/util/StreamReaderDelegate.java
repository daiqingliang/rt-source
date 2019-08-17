package javax.xml.stream.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StreamReaderDelegate implements XMLStreamReader {
  private XMLStreamReader reader;
  
  public StreamReaderDelegate() {}
  
  public StreamReaderDelegate(XMLStreamReader paramXMLStreamReader) { this.reader = paramXMLStreamReader; }
  
  public void setParent(XMLStreamReader paramXMLStreamReader) { this.reader = paramXMLStreamReader; }
  
  public XMLStreamReader getParent() { return this.reader; }
  
  public int next() throws XMLStreamException { return this.reader.next(); }
  
  public int nextTag() throws XMLStreamException { return this.reader.nextTag(); }
  
  public String getElementText() throws XMLStreamException { return this.reader.getElementText(); }
  
  public void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException { this.reader.require(paramInt, paramString1, paramString2); }
  
  public boolean hasNext() throws XMLStreamException { return this.reader.hasNext(); }
  
  public void close() { this.reader.close(); }
  
  public String getNamespaceURI(String paramString) { return this.reader.getNamespaceURI(paramString); }
  
  public NamespaceContext getNamespaceContext() { return this.reader.getNamespaceContext(); }
  
  public boolean isStartElement() throws XMLStreamException { return this.reader.isStartElement(); }
  
  public boolean isEndElement() throws XMLStreamException { return this.reader.isEndElement(); }
  
  public boolean isCharacters() throws XMLStreamException { return this.reader.isCharacters(); }
  
  public boolean isWhiteSpace() throws XMLStreamException { return this.reader.isWhiteSpace(); }
  
  public String getAttributeValue(String paramString1, String paramString2) { return this.reader.getAttributeValue(paramString1, paramString2); }
  
  public int getAttributeCount() throws XMLStreamException { return this.reader.getAttributeCount(); }
  
  public QName getAttributeName(int paramInt) { return this.reader.getAttributeName(paramInt); }
  
  public String getAttributePrefix(int paramInt) { return this.reader.getAttributePrefix(paramInt); }
  
  public String getAttributeNamespace(int paramInt) { return this.reader.getAttributeNamespace(paramInt); }
  
  public String getAttributeLocalName(int paramInt) { return this.reader.getAttributeLocalName(paramInt); }
  
  public String getAttributeType(int paramInt) { return this.reader.getAttributeType(paramInt); }
  
  public String getAttributeValue(int paramInt) { return this.reader.getAttributeValue(paramInt); }
  
  public boolean isAttributeSpecified(int paramInt) { return this.reader.isAttributeSpecified(paramInt); }
  
  public int getNamespaceCount() throws XMLStreamException { return this.reader.getNamespaceCount(); }
  
  public String getNamespacePrefix(int paramInt) { return this.reader.getNamespacePrefix(paramInt); }
  
  public String getNamespaceURI(int paramInt) { return this.reader.getNamespaceURI(paramInt); }
  
  public int getEventType() throws XMLStreamException { return this.reader.getEventType(); }
  
  public String getText() throws XMLStreamException { return this.reader.getText(); }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException { return this.reader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3); }
  
  public char[] getTextCharacters() { return this.reader.getTextCharacters(); }
  
  public int getTextStart() throws XMLStreamException { return this.reader.getTextStart(); }
  
  public int getTextLength() throws XMLStreamException { return this.reader.getTextLength(); }
  
  public String getEncoding() throws XMLStreamException { return this.reader.getEncoding(); }
  
  public boolean hasText() throws XMLStreamException { return this.reader.hasText(); }
  
  public Location getLocation() { return this.reader.getLocation(); }
  
  public QName getName() { return this.reader.getName(); }
  
  public String getLocalName() throws XMLStreamException { return this.reader.getLocalName(); }
  
  public boolean hasName() throws XMLStreamException { return this.reader.hasName(); }
  
  public String getNamespaceURI() throws XMLStreamException { return this.reader.getNamespaceURI(); }
  
  public String getPrefix() throws XMLStreamException { return this.reader.getPrefix(); }
  
  public String getVersion() throws XMLStreamException { return this.reader.getVersion(); }
  
  public boolean isStandalone() throws XMLStreamException { return this.reader.isStandalone(); }
  
  public boolean standaloneSet() throws XMLStreamException { return this.reader.standaloneSet(); }
  
  public String getCharacterEncodingScheme() throws XMLStreamException { return this.reader.getCharacterEncodingScheme(); }
  
  public String getPITarget() throws XMLStreamException { return this.reader.getPITarget(); }
  
  public String getPIData() throws XMLStreamException { return this.reader.getPIData(); }
  
  public Object getProperty(String paramString) { return this.reader.getProperty(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\strea\\util\StreamReaderDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */