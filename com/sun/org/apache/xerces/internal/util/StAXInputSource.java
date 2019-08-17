package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class StAXInputSource extends XMLInputSource {
  private final XMLStreamReader fStreamReader;
  
  private final XMLEventReader fEventReader;
  
  private final boolean fConsumeRemainingContent;
  
  public StAXInputSource(XMLStreamReader paramXMLStreamReader) { this(paramXMLStreamReader, false); }
  
  public StAXInputSource(XMLStreamReader paramXMLStreamReader, boolean paramBoolean) {
    super(null, paramXMLStreamReader.getLocation().getSystemId(), null);
    if (paramXMLStreamReader == null)
      throw new IllegalArgumentException("XMLStreamReader parameter cannot be null."); 
    this.fStreamReader = paramXMLStreamReader;
    this.fEventReader = null;
    this.fConsumeRemainingContent = paramBoolean;
  }
  
  public StAXInputSource(XMLEventReader paramXMLEventReader) { this(paramXMLEventReader, false); }
  
  public StAXInputSource(XMLEventReader paramXMLEventReader, boolean paramBoolean) {
    super(null, getEventReaderSystemId(paramXMLEventReader), null);
    if (paramXMLEventReader == null)
      throw new IllegalArgumentException("XMLEventReader parameter cannot be null."); 
    this.fStreamReader = null;
    this.fEventReader = paramXMLEventReader;
    this.fConsumeRemainingContent = paramBoolean;
  }
  
  public XMLStreamReader getXMLStreamReader() { return this.fStreamReader; }
  
  public XMLEventReader getXMLEventReader() { return this.fEventReader; }
  
  public boolean shouldConsumeRemainingContent() { return this.fConsumeRemainingContent; }
  
  public void setSystemId(String paramString) { throw new UnsupportedOperationException("Cannot set the system ID on a StAXInputSource"); }
  
  private static String getEventReaderSystemId(XMLEventReader paramXMLEventReader) {
    try {
      if (paramXMLEventReader != null)
        return paramXMLEventReader.peek().getLocation().getSystemId(); 
    } catch (XMLStreamException xMLStreamException) {}
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\StAXInputSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */