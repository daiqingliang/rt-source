package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StAXEventWriter implements XMLEventWriter {
  private XMLStreamWriter _streamWriter;
  
  public StAXEventWriter(XMLStreamWriter paramXMLStreamWriter) { this._streamWriter = paramXMLStreamWriter; }
  
  public void flush() throws XMLStreamException { this._streamWriter.flush(); }
  
  public void close() throws XMLStreamException { this._streamWriter.close(); }
  
  public void add(XMLEventReader paramXMLEventReader) throws XMLStreamException {
    if (paramXMLEventReader == null)
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.nullEventReader")); 
    while (paramXMLEventReader.hasNext())
      add(paramXMLEventReader.nextEvent()); 
  }
  
  public void add(XMLEvent paramXMLEvent) throws XMLStreamException {
    Iterator iterator2;
    Iterator iterator1;
    QName qName;
    StartDocument startDocument;
    Characters characters1;
    Namespace namespace;
    StartElement startElement;
    Comment comment;
    DTD dTD;
    Characters characters2;
    EntityReference entityReference;
    Attribute attribute;
    ProcessingInstruction processingInstruction;
    int i = paramXMLEvent.getEventType();
    switch (i) {
      case 11:
        dTD = (DTD)paramXMLEvent;
        this._streamWriter.writeDTD(dTD.getDocumentTypeDeclaration());
        return;
      case 7:
        startDocument = (StartDocument)paramXMLEvent;
        this._streamWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
        return;
      case 1:
        startElement = paramXMLEvent.asStartElement();
        qName = startElement.getName();
        this._streamWriter.writeStartElement(qName.getPrefix(), qName.getLocalPart(), qName.getNamespaceURI());
        iterator1 = startElement.getNamespaces();
        while (iterator1.hasNext()) {
          Namespace namespace1 = (Namespace)iterator1.next();
          this._streamWriter.writeNamespace(namespace1.getPrefix(), namespace1.getNamespaceURI());
        } 
        iterator2 = startElement.getAttributes();
        while (iterator2.hasNext()) {
          Attribute attribute1 = (Attribute)iterator2.next();
          QName qName1 = attribute1.getName();
          this._streamWriter.writeAttribute(qName1.getPrefix(), qName1.getNamespaceURI(), qName1.getLocalPart(), attribute1.getValue());
        } 
        return;
      case 13:
        namespace = (Namespace)paramXMLEvent;
        this._streamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
        return;
      case 5:
        comment = (Comment)paramXMLEvent;
        this._streamWriter.writeComment(comment.getText());
        return;
      case 3:
        processingInstruction = (ProcessingInstruction)paramXMLEvent;
        this._streamWriter.writeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
        return;
      case 4:
        characters2 = paramXMLEvent.asCharacters();
        if (characters2.isCData()) {
          this._streamWriter.writeCData(characters2.getData());
        } else {
          this._streamWriter.writeCharacters(characters2.getData());
        } 
        return;
      case 9:
        entityReference = (EntityReference)paramXMLEvent;
        this._streamWriter.writeEntityRef(entityReference.getName());
        return;
      case 10:
        attribute = (Attribute)paramXMLEvent;
        qName = attribute.getName();
        this._streamWriter.writeAttribute(qName.getPrefix(), qName.getNamespaceURI(), qName.getLocalPart(), attribute.getValue());
        return;
      case 12:
        characters1 = (Characters)paramXMLEvent;
        if (characters1.isCData())
          this._streamWriter.writeCData(characters1.getData()); 
        return;
      case 2:
        this._streamWriter.writeEndElement();
        return;
      case 8:
        this._streamWriter.writeEndDocument();
        return;
    } 
    throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.eventTypeNotSupported", new Object[] { Util.getEventTypeString(i) }));
  }
  
  public String getPrefix(String paramString) throws XMLStreamException { return this._streamWriter.getPrefix(paramString); }
  
  public NamespaceContext getNamespaceContext() { return this._streamWriter.getNamespaceContext(); }
  
  public void setDefaultNamespace(String paramString) throws XMLStreamException { this._streamWriter.setDefaultNamespace(paramString); }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) throws XMLStreamException { this._streamWriter.setNamespaceContext(paramNamespaceContext); }
  
  public void setPrefix(String paramString1, String paramString2) throws XMLStreamException { this._streamWriter.setPrefix(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StAXEventWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */