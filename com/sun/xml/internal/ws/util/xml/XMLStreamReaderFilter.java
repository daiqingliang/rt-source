package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderFilter implements XMLStreamReaderFactory.RecycleAware, XMLStreamReader {
  protected XMLStreamReader reader;
  
  public XMLStreamReaderFilter(XMLStreamReader paramXMLStreamReader) { this.reader = paramXMLStreamReader; }
  
  public void onRecycled() {
    XMLStreamReaderFactory.recycle(this.reader);
    this.reader = null;
  }
  
  public int getAttributeCount() { return this.reader.getAttributeCount(); }
  
  public int getEventType() { return this.reader.getEventType(); }
  
  public int getNamespaceCount() { return this.reader.getNamespaceCount(); }
  
  public int getTextLength() { return this.reader.getTextLength(); }
  
  public int getTextStart() { return this.reader.getTextStart(); }
  
  public int next() { return this.reader.next(); }
  
  public int nextTag() { return this.reader.nextTag(); }
  
  public void close() { this.reader.close(); }
  
  public boolean hasName() { return this.reader.hasName(); }
  
  public boolean hasNext() { return this.reader.hasNext(); }
  
  public boolean hasText() { return this.reader.hasText(); }
  
  public boolean isCharacters() { return this.reader.isCharacters(); }
  
  public boolean isEndElement() { return this.reader.isEndElement(); }
  
  public boolean isStandalone() { return this.reader.isStandalone(); }
  
  public boolean isStartElement() { return this.reader.isStartElement(); }
  
  public boolean isWhiteSpace() { return this.reader.isWhiteSpace(); }
  
  public boolean standaloneSet() { return this.reader.standaloneSet(); }
  
  public char[] getTextCharacters() { return this.reader.getTextCharacters(); }
  
  public boolean isAttributeSpecified(int paramInt) { return this.reader.isAttributeSpecified(paramInt); }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException { return this.reader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3); }
  
  public String getCharacterEncodingScheme() { return this.reader.getCharacterEncodingScheme(); }
  
  public String getElementText() { return this.reader.getElementText(); }
  
  public String getEncoding() { return this.reader.getEncoding(); }
  
  public String getLocalName() { return this.reader.getLocalName(); }
  
  public String getNamespaceURI() { return this.reader.getNamespaceURI(); }
  
  public String getPIData() { return this.reader.getPIData(); }
  
  public String getPITarget() { return this.reader.getPITarget(); }
  
  public String getPrefix() { return this.reader.getPrefix(); }
  
  public String getText() { return this.reader.getText(); }
  
  public String getVersion() { return this.reader.getVersion(); }
  
  public String getAttributeLocalName(int paramInt) { return this.reader.getAttributeLocalName(paramInt); }
  
  public String getAttributeNamespace(int paramInt) { return this.reader.getAttributeNamespace(paramInt); }
  
  public String getAttributePrefix(int paramInt) { return this.reader.getAttributePrefix(paramInt); }
  
  public String getAttributeType(int paramInt) { return this.reader.getAttributeType(paramInt); }
  
  public String getAttributeValue(int paramInt) { return this.reader.getAttributeValue(paramInt); }
  
  public String getNamespacePrefix(int paramInt) { return this.reader.getNamespacePrefix(paramInt); }
  
  public String getNamespaceURI(int paramInt) { return this.reader.getNamespaceURI(paramInt); }
  
  public NamespaceContext getNamespaceContext() { return this.reader.getNamespaceContext(); }
  
  public QName getName() { return this.reader.getName(); }
  
  public QName getAttributeName(int paramInt) { return this.reader.getAttributeName(paramInt); }
  
  public Location getLocation() { return this.reader.getLocation(); }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return this.reader.getProperty(paramString); }
  
  public void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException { this.reader.require(paramInt, paramString1, paramString2); }
  
  public String getNamespaceURI(String paramString) { return this.reader.getNamespaceURI(paramString); }
  
  public String getAttributeValue(String paramString1, String paramString2) { return this.reader.getAttributeValue(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\XMLStreamReaderFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */