package javax.xml.stream.util;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class EventReaderDelegate implements XMLEventReader {
  private XMLEventReader reader;
  
  public EventReaderDelegate() {}
  
  public EventReaderDelegate(XMLEventReader paramXMLEventReader) { this.reader = paramXMLEventReader; }
  
  public void setParent(XMLEventReader paramXMLEventReader) { this.reader = paramXMLEventReader; }
  
  public XMLEventReader getParent() { return this.reader; }
  
  public XMLEvent nextEvent() throws XMLStreamException { return this.reader.nextEvent(); }
  
  public Object next() { return this.reader.next(); }
  
  public boolean hasNext() { return this.reader.hasNext(); }
  
  public XMLEvent peek() throws XMLStreamException { return this.reader.peek(); }
  
  public void close() { this.reader.close(); }
  
  public String getElementText() throws XMLStreamException { return this.reader.getElementText(); }
  
  public XMLEvent nextTag() throws XMLStreamException { return this.reader.nextTag(); }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return this.reader.getProperty(paramString); }
  
  public void remove() { this.reader.remove(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\strea\\util\EventReaderDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */