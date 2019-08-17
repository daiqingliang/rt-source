package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class StAXFilteredEvent implements XMLEventReader {
  private XMLEventReader eventReader;
  
  private EventFilter _filter;
  
  public StAXFilteredEvent() {}
  
  public StAXFilteredEvent(XMLEventReader paramXMLEventReader, EventFilter paramEventFilter) throws XMLStreamException {
    this.eventReader = paramXMLEventReader;
    this._filter = paramEventFilter;
  }
  
  public void setEventReader(XMLEventReader paramXMLEventReader) { this.eventReader = paramXMLEventReader; }
  
  public void setFilter(EventFilter paramEventFilter) { this._filter = paramEventFilter; }
  
  public Object next() {
    try {
      return nextEvent();
    } catch (XMLStreamException xMLStreamException) {
      return null;
    } 
  }
  
  public XMLEvent nextEvent() throws XMLStreamException { return hasNext() ? this.eventReader.nextEvent() : null; }
  
  public String getElementText() throws XMLStreamException {
    StringBuffer stringBuffer = new StringBuffer();
    XMLEvent xMLEvent = nextEvent();
    if (!xMLEvent.isStartElement())
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTART_ELEMENT")); 
    while (hasNext()) {
      xMLEvent = nextEvent();
      if (xMLEvent.isStartElement())
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly")); 
      if (xMLEvent.isCharacters())
        stringBuffer.append(((Characters)xMLEvent).getData()); 
      if (xMLEvent.isEndElement())
        return stringBuffer.toString(); 
    } 
    throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.END_ELEMENTnotFound"));
  }
  
  public XMLEvent nextTag() throws XMLStreamException {
    while (hasNext()) {
      XMLEvent xMLEvent = nextEvent();
      if (xMLEvent.isStartElement() || xMLEvent.isEndElement())
        return xMLEvent; 
    } 
    throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.startOrEndNotFound"));
  }
  
  public boolean hasNext() {
    try {
      while (this.eventReader.hasNext()) {
        if (this._filter.accept(this.eventReader.peek()))
          return true; 
        this.eventReader.nextEvent();
      } 
      return false;
    } catch (XMLStreamException xMLStreamException) {
      return false;
    } 
  }
  
  public void remove() { throw new UnsupportedOperationException(); }
  
  public XMLEvent peek() throws XMLStreamException { return hasNext() ? this.eventReader.peek() : null; }
  
  public void close() { this.eventReader.close(); }
  
  public Object getProperty(String paramString) { return this.eventReader.getProperty(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StAXFilteredEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */