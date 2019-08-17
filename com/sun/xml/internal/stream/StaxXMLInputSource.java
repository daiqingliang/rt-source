package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;

public class StaxXMLInputSource {
  XMLStreamReader fStreamReader;
  
  XMLEventReader fEventReader;
  
  XMLInputSource fInputSource;
  
  boolean fHasResolver = false;
  
  public StaxXMLInputSource(XMLStreamReader paramXMLStreamReader) { this.fStreamReader = paramXMLStreamReader; }
  
  public StaxXMLInputSource(XMLEventReader paramXMLEventReader) { this.fEventReader = paramXMLEventReader; }
  
  public StaxXMLInputSource(XMLInputSource paramXMLInputSource) { this.fInputSource = paramXMLInputSource; }
  
  public StaxXMLInputSource(XMLInputSource paramXMLInputSource, boolean paramBoolean) {
    this.fInputSource = paramXMLInputSource;
    this.fHasResolver = paramBoolean;
  }
  
  public XMLStreamReader getXMLStreamReader() { return this.fStreamReader; }
  
  public XMLEventReader getXMLEventReader() { return this.fEventReader; }
  
  public XMLInputSource getXMLInputSource() { return this.fInputSource; }
  
  public boolean hasXMLStreamOrXMLEventReader() { return !(this.fStreamReader == null && this.fEventReader == null); }
  
  public boolean hasResolver() { return this.fHasResolver; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\StaxXMLInputSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */