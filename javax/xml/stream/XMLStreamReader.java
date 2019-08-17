package javax.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public interface XMLStreamReader extends XMLStreamConstants {
  Object getProperty(String paramString) throws IllegalArgumentException;
  
  int next() throws XMLStreamException;
  
  void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException;
  
  String getElementText() throws XMLStreamException;
  
  int nextTag() throws XMLStreamException;
  
  boolean hasNext() throws XMLStreamException;
  
  void close() throws XMLStreamException;
  
  String getNamespaceURI(String paramString);
  
  boolean isStartElement() throws XMLStreamException;
  
  boolean isEndElement() throws XMLStreamException;
  
  boolean isCharacters() throws XMLStreamException;
  
  boolean isWhiteSpace() throws XMLStreamException;
  
  String getAttributeValue(String paramString1, String paramString2);
  
  int getAttributeCount() throws XMLStreamException;
  
  QName getAttributeName(int paramInt);
  
  String getAttributeNamespace(int paramInt);
  
  String getAttributeLocalName(int paramInt);
  
  String getAttributePrefix(int paramInt);
  
  String getAttributeType(int paramInt);
  
  String getAttributeValue(int paramInt);
  
  boolean isAttributeSpecified(int paramInt);
  
  int getNamespaceCount() throws XMLStreamException;
  
  String getNamespacePrefix(int paramInt);
  
  String getNamespaceURI(int paramInt);
  
  NamespaceContext getNamespaceContext();
  
  int getEventType() throws XMLStreamException;
  
  String getText() throws XMLStreamException;
  
  char[] getTextCharacters();
  
  int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException;
  
  int getTextStart() throws XMLStreamException;
  
  int getTextLength() throws XMLStreamException;
  
  String getEncoding() throws XMLStreamException;
  
  boolean hasText() throws XMLStreamException;
  
  Location getLocation();
  
  QName getName();
  
  String getLocalName() throws XMLStreamException;
  
  boolean hasName() throws XMLStreamException;
  
  String getNamespaceURI() throws XMLStreamException;
  
  String getPrefix() throws XMLStreamException;
  
  String getVersion() throws XMLStreamException;
  
  boolean isStandalone() throws XMLStreamException;
  
  boolean standaloneSet() throws XMLStreamException;
  
  String getCharacterEncodingScheme() throws XMLStreamException;
  
  String getPITarget() throws XMLStreamException;
  
  String getPIData() throws XMLStreamException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\XMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */