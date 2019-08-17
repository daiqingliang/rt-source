package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

public class StAXEventReader implements XMLEventReader {
  protected XMLStreamReader _streamReader;
  
  protected XMLEventAllocator _eventAllocator;
  
  private XMLEvent _currentEvent;
  
  private XMLEvent[] events = new XMLEvent[3];
  
  private int size = 3;
  
  private int currentIndex = 0;
  
  private boolean hasEvent = false;
  
  public StAXEventReader(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    this._streamReader = paramXMLStreamReader;
    this._eventAllocator = (XMLEventAllocator)paramXMLStreamReader.getProperty("javax.xml.stream.allocator");
    if (this._eventAllocator == null)
      this._eventAllocator = new StAXEventAllocatorBase(); 
    if (this._streamReader.hasNext()) {
      this._streamReader.next();
      this._currentEvent = this._eventAllocator.allocate(this._streamReader);
      this.events[0] = this._currentEvent;
      this.hasEvent = true;
    } else {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.noElement"));
    } 
  }
  
  public boolean hasNext() { return this.hasEvent; }
  
  public XMLEvent nextEvent() throws XMLStreamException {
    XMLEvent xMLEvent1 = null;
    XMLEvent xMLEvent2 = null;
    if (this.hasEvent) {
      xMLEvent1 = this.events[this.currentIndex];
      this.events[this.currentIndex] = null;
      if (this._streamReader.hasNext()) {
        this._streamReader.next();
        xMLEvent2 = this._eventAllocator.allocate(this._streamReader);
        if (++this.currentIndex == this.size)
          this.currentIndex = 0; 
        this.events[this.currentIndex] = xMLEvent2;
        this.hasEvent = true;
      } else {
        this._currentEvent = null;
        this.hasEvent = false;
      } 
      return xMLEvent1;
    } 
    throw new NoSuchElementException();
  }
  
  public void remove() { throw new UnsupportedOperationException(); }
  
  public void close() { this._streamReader.close(); }
  
  public String getElementText() throws XMLStreamException {
    if (!this.hasEvent)
      throw new NoSuchElementException(); 
    if (!this._currentEvent.isStartElement()) {
      StAXDocumentParser stAXDocumentParser = (StAXDocumentParser)this._streamReader;
      return stAXDocumentParser.getElementText(true);
    } 
    return this._streamReader.getElementText();
  }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return this._streamReader.getProperty(paramString); }
  
  public XMLEvent nextTag() throws XMLStreamException {
    if (!this.hasEvent)
      throw new NoSuchElementException(); 
    StAXDocumentParser stAXDocumentParser = (StAXDocumentParser)this._streamReader;
    stAXDocumentParser.nextTag(true);
    return this._eventAllocator.allocate(this._streamReader);
  }
  
  public Object next() {
    try {
      return nextEvent();
    } catch (XMLStreamException xMLStreamException) {
      return null;
    } 
  }
  
  public XMLEvent peek() throws XMLStreamException {
    if (!this.hasEvent)
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.noElement")); 
    this._currentEvent = this.events[this.currentIndex];
    return this._currentEvent;
  }
  
  public void setAllocator(XMLEventAllocator paramXMLEventAllocator) {
    if (paramXMLEventAllocator == null)
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullXMLEventAllocator")); 
    this._eventAllocator = paramXMLEventAllocator;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StAXEventReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */