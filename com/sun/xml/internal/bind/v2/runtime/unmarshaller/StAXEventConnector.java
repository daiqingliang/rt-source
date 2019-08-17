package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

final class StAXEventConnector extends StAXConnector {
  private final XMLEventReader staxEventReader;
  
  private XMLEvent event;
  
  private final AttributesImpl attrs = new AttributesImpl();
  
  private final StringBuilder buffer = new StringBuilder();
  
  private boolean seenText;
  
  public StAXEventConnector(XMLEventReader paramXMLEventReader, XmlVisitor paramXmlVisitor) {
    super(paramXmlVisitor);
    this.staxEventReader = paramXMLEventReader;
  }
  
  public void bridge() throws XMLStreamException {
    try {
      byte b = 0;
      this.event = this.staxEventReader.peek();
      if (!this.event.isStartDocument() && !this.event.isStartElement())
        throw new IllegalStateException(); 
      do {
        this.event = this.staxEventReader.nextEvent();
      } while (!this.event.isStartElement());
      handleStartDocument(this.event.asStartElement().getNamespaceContext());
      while (true) {
        switch (this.event.getEventType()) {
          case 1:
            handleStartElement(this.event.asStartElement());
            b++;
            break;
          case 2:
            b--;
            handleEndElement(this.event.asEndElement());
            if (b == 0)
              break; 
            break;
          case 4:
          case 6:
          case 12:
            handleCharacters(this.event.asCharacters());
            break;
        } 
        this.event = this.staxEventReader.nextEvent();
      } 
      handleEndDocument();
      this.event = null;
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  protected Location getCurrentLocation() { return this.event.getLocation(); }
  
  protected String getCurrentQName() {
    QName qName;
    if (this.event.isEndElement()) {
      qName = this.event.asEndElement().getName();
    } else {
      qName = this.event.asStartElement().getName();
    } 
    return getQName(qName.getPrefix(), qName.getLocalPart());
  }
  
  private void handleCharacters(Characters paramCharacters) throws SAXException, XMLStreamException {
    XMLEvent xMLEvent;
    if (!this.predictor.expectText())
      return; 
    this.seenText = true;
    while (true) {
      xMLEvent = this.staxEventReader.peek();
      if (!isIgnorable(xMLEvent))
        break; 
      this.staxEventReader.nextEvent();
    } 
    if (isTag(xMLEvent)) {
      this.visitor.text(paramCharacters.getData());
      return;
    } 
    this.buffer.append(paramCharacters.getData());
    while (true) {
      xMLEvent = this.staxEventReader.peek();
      if (!isIgnorable(xMLEvent)) {
        if (isTag(xMLEvent)) {
          this.visitor.text(this.buffer);
          this.buffer.setLength(0);
          return;
        } 
        this.buffer.append(xMLEvent.asCharacters().getData());
        this.staxEventReader.nextEvent();
        continue;
      } 
      this.staxEventReader.nextEvent();
    } 
  }
  
  private boolean isTag(XMLEvent paramXMLEvent) {
    int i = paramXMLEvent.getEventType();
    return (i == 1 || i == 2);
  }
  
  private boolean isIgnorable(XMLEvent paramXMLEvent) {
    int i = paramXMLEvent.getEventType();
    return (i == 5 || i == 3);
  }
  
  private void handleEndElement(EndElement paramEndElement) throws SAXException {
    if (!this.seenText && this.predictor.expectText())
      this.visitor.text(""); 
    QName qName = paramEndElement.getName();
    this.tagName.uri = fixNull(qName.getNamespaceURI());
    this.tagName.local = qName.getLocalPart();
    this.visitor.endElement(this.tagName);
    Iterator iterator = paramEndElement.getNamespaces();
    while (iterator.hasNext()) {
      String str = fixNull(((Namespace)iterator.next()).getPrefix());
      this.visitor.endPrefixMapping(str);
    } 
    this.seenText = false;
  }
  
  private void handleStartElement(StartElement paramStartElement) throws SAXException {
    Iterator iterator = paramStartElement.getNamespaces();
    while (iterator.hasNext()) {
      Namespace namespace = (Namespace)iterator.next();
      this.visitor.startPrefixMapping(fixNull(namespace.getPrefix()), fixNull(namespace.getNamespaceURI()));
    } 
    QName qName = paramStartElement.getName();
    this.tagName.uri = fixNull(qName.getNamespaceURI());
    String str = qName.getLocalPart();
    this.tagName.uri = fixNull(qName.getNamespaceURI());
    this.tagName.local = str;
    this.tagName.atts = getAttributes(paramStartElement);
    this.visitor.startElement(this.tagName);
    this.seenText = false;
  }
  
  private Attributes getAttributes(StartElement paramStartElement) {
    this.attrs.clear();
    Iterator iterator = paramStartElement.getAttributes();
    while (iterator.hasNext()) {
      String str4;
      Attribute attribute = (Attribute)iterator.next();
      QName qName = attribute.getName();
      String str1 = fixNull(qName.getNamespaceURI());
      String str2 = qName.getLocalPart();
      String str3 = qName.getPrefix();
      if (str3 == null || str3.length() == 0) {
        str4 = str2;
      } else {
        str4 = str3 + ':' + str2;
      } 
      String str5 = attribute.getDTDType();
      String str6 = attribute.getValue();
      this.attrs.addAttribute(str1, str2, str4, str5, str6);
    } 
    return this.attrs;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\StAXEventConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */