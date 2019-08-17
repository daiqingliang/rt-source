package com.sun.xml.internal.org.jvnet.fastinfoset.stax;

import javax.xml.stream.XMLStreamException;

public interface FastInfosetStreamReader {
  int peekNext() throws XMLStreamException;
  
  int accessNamespaceCount() throws XMLStreamException;
  
  String accessLocalName();
  
  String accessNamespaceURI();
  
  String accessPrefix();
  
  char[] accessTextCharacters();
  
  int accessTextStart() throws XMLStreamException;
  
  int accessTextLength() throws XMLStreamException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\stax\FastInfosetStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */