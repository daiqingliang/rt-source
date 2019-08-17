package javax.xml.transform.stax;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;

public class StAXSource implements Source {
  public static final String FEATURE = "http://javax.xml.transform.stax.StAXSource/feature";
  
  private XMLEventReader xmlEventReader = null;
  
  private XMLStreamReader xmlStreamReader = null;
  
  private String systemId = null;
  
  public StAXSource(XMLEventReader paramXMLEventReader) throws XMLStreamException {
    if (paramXMLEventReader == null)
      throw new IllegalArgumentException("StAXSource(XMLEventReader) with XMLEventReader == null"); 
    XMLEvent xMLEvent = paramXMLEventReader.peek();
    int i = xMLEvent.getEventType();
    if (i != 7 && i != 1)
      throw new IllegalStateException("StAXSource(XMLEventReader) with XMLEventReader not in XMLStreamConstants.START_DOCUMENT or XMLStreamConstants.START_ELEMENT state"); 
    this.xmlEventReader = paramXMLEventReader;
    this.systemId = xMLEvent.getLocation().getSystemId();
  }
  
  public StAXSource(XMLStreamReader paramXMLStreamReader) {
    if (paramXMLStreamReader == null)
      throw new IllegalArgumentException("StAXSource(XMLStreamReader) with XMLStreamReader == null"); 
    int i = paramXMLStreamReader.getEventType();
    if (i != 7 && i != 1)
      throw new IllegalStateException("StAXSource(XMLStreamReader) with XMLStreamReadernot in XMLStreamConstants.START_DOCUMENT or XMLStreamConstants.START_ELEMENT state"); 
    this.xmlStreamReader = paramXMLStreamReader;
    this.systemId = paramXMLStreamReader.getLocation().getSystemId();
  }
  
  public XMLEventReader getXMLEventReader() { return this.xmlEventReader; }
  
  public XMLStreamReader getXMLStreamReader() { return this.xmlStreamReader; }
  
  public void setSystemId(String paramString) { throw new UnsupportedOperationException("StAXSource#setSystemId(systemId) cannot set the system identifier for a StAXSource"); }
  
  public String getSystemId() { return this.systemId; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\stax\StAXSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */