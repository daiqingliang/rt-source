package com.sun.xml.internal.stream;

import java.util.NoSuchElementException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;

public class EventFilterSupport extends EventReaderDelegate {
  EventFilter fEventFilter;
  
  public EventFilterSupport(XMLEventReader paramXMLEventReader, EventFilter paramEventFilter) {
    setParent(paramXMLEventReader);
    this.fEventFilter = paramEventFilter;
  }
  
  public Object next() {
    try {
      return nextEvent();
    } catch (XMLStreamException xMLStreamException) {
      throw new NoSuchElementException();
    } 
  }
  
  public boolean hasNext() {
    try {
      return (peek() != null);
    } catch (XMLStreamException xMLStreamException) {
      return false;
    } 
  }
  
  public XMLEvent nextEvent() throws XMLStreamException {
    if (super.hasNext()) {
      XMLEvent xMLEvent = super.nextEvent();
      return this.fEventFilter.accept(xMLEvent) ? xMLEvent : nextEvent();
    } 
    throw new NoSuchElementException();
  }
  
  public XMLEvent nextTag() throws XMLStreamException {
    if (super.hasNext()) {
      XMLEvent xMLEvent = super.nextTag();
      return this.fEventFilter.accept(xMLEvent) ? xMLEvent : nextTag();
    } 
    throw new NoSuchElementException();
  }
  
  public XMLEvent peek() throws XMLStreamException {
    while (true) {
      XMLEvent xMLEvent = super.peek();
      if (xMLEvent == null)
        return null; 
      if (this.fEventFilter.accept(xMLEvent))
        return xMLEvent; 
      super.next();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\EventFilterSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */