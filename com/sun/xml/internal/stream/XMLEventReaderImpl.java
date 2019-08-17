package com.sun.xml.internal.stream;

import com.sun.xml.internal.stream.events.XMLEventAllocatorImpl;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

public class XMLEventReaderImpl implements XMLEventReader {
  protected XMLStreamReader fXMLReader;
  
  protected XMLEventAllocator fXMLEventAllocator;
  
  private XMLEvent fPeekedEvent;
  
  private XMLEvent fLastEvent;
  
  public XMLEventReaderImpl(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    this.fXMLReader = paramXMLStreamReader;
    this.fXMLEventAllocator = (XMLEventAllocator)paramXMLStreamReader.getProperty("javax.xml.stream.allocator");
    if (this.fXMLEventAllocator == null)
      this.fXMLEventAllocator = new XMLEventAllocatorImpl(); 
    this.fPeekedEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
  }
  
  public boolean hasNext() {
    if (this.fPeekedEvent != null)
      return true; 
    boolean bool = false;
    try {
      bool = this.fXMLReader.hasNext();
    } catch (XMLStreamException xMLStreamException) {
      return false;
    } 
    return bool;
  }
  
  public XMLEvent nextEvent() throws XMLStreamException {
    if (this.fPeekedEvent != null) {
      this.fLastEvent = this.fPeekedEvent;
      this.fPeekedEvent = null;
      return this.fLastEvent;
    } 
    if (this.fXMLReader.hasNext()) {
      this.fXMLReader.next();
      return this.fLastEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
    } 
    this.fLastEvent = null;
    throw new NoSuchElementException();
  }
  
  public void remove() { throw new UnsupportedOperationException(); }
  
  public void close() { this.fXMLReader.close(); }
  
  public String getElementText() throws XMLStreamException {
    if (this.fLastEvent.getEventType() != 1)
      throw new XMLStreamException("parser must be on START_ELEMENT to read next text", this.fLastEvent.getLocation()); 
    String str = null;
    if (this.fPeekedEvent != null) {
      XMLEvent xMLEvent = this.fPeekedEvent;
      this.fPeekedEvent = null;
      int i = xMLEvent.getEventType();
      if (i == 4 || i == 6 || i == 12) {
        str = xMLEvent.asCharacters().getData();
      } else if (i == 9) {
        str = ((EntityReference)xMLEvent).getDeclaration().getReplacementText();
      } else if (i != 5 && i != 3) {
        if (i == 1)
          throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", xMLEvent.getLocation()); 
        if (i == 2)
          return ""; 
      } 
      StringBuffer stringBuffer = new StringBuffer();
      if (str != null && str.length() > 0)
        stringBuffer.append(str); 
      for (xMLEvent = nextEvent(); xMLEvent.getEventType() != 2; xMLEvent = nextEvent()) {
        if (i == 4 || i == 6 || i == 12) {
          str = xMLEvent.asCharacters().getData();
        } else if (i == 9) {
          str = ((EntityReference)xMLEvent).getDeclaration().getReplacementText();
        } else if (i != 5 && i != 3) {
          if (i == 8)
            throw new XMLStreamException("unexpected end of document when reading element text content"); 
          if (i == 1)
            throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", xMLEvent.getLocation()); 
          throw new XMLStreamException("Unexpected event type " + i, xMLEvent.getLocation());
        } 
        if (str != null && str.length() > 0)
          stringBuffer.append(str); 
      } 
      return stringBuffer.toString();
    } 
    str = this.fXMLReader.getElementText();
    this.fLastEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
    return str;
  }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return this.fXMLReader.getProperty(paramString); }
  
  public XMLEvent nextTag() throws XMLStreamException {
    if (this.fPeekedEvent != null) {
      XMLEvent xMLEvent = this.fPeekedEvent;
      this.fPeekedEvent = null;
      int i = xMLEvent.getEventType();
      if ((xMLEvent.isCharacters() && xMLEvent.asCharacters().isWhiteSpace()) || i == 3 || i == 5 || i == 7) {
        xMLEvent = nextEvent();
        i = xMLEvent.getEventType();
      } 
      while ((xMLEvent.isCharacters() && xMLEvent.asCharacters().isWhiteSpace()) || i == 3 || i == 5) {
        xMLEvent = nextEvent();
        i = xMLEvent.getEventType();
      } 
      if (i != 1 && i != 2)
        throw new XMLStreamException("expected start or end tag", xMLEvent.getLocation()); 
      return xMLEvent;
    } 
    this.fXMLReader.nextTag();
    return this.fLastEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
  }
  
  public Object next() {
    XMLEvent xMLEvent = null;
    try {
      xMLEvent = nextEvent();
    } catch (XMLStreamException xMLStreamException) {
      this.fLastEvent = null;
      NoSuchElementException noSuchElementException = new NoSuchElementException(xMLStreamException.getMessage());
      noSuchElementException.initCause(xMLStreamException.getCause());
      throw noSuchElementException;
    } 
    return xMLEvent;
  }
  
  public XMLEvent peek() throws XMLStreamException {
    if (this.fPeekedEvent != null)
      return this.fPeekedEvent; 
    if (hasNext()) {
      this.fXMLReader.next();
      this.fPeekedEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
      return this.fPeekedEvent;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\XMLEventReaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */