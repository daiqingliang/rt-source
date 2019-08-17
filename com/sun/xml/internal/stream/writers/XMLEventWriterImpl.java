package com.sun.xml.internal.stream.writers;

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

public class XMLEventWriterImpl implements XMLEventWriter {
  private XMLStreamWriter fStreamWriter;
  
  private static final boolean DEBUG = false;
  
  public XMLEventWriterImpl(XMLStreamWriter paramXMLStreamWriter) { this.fStreamWriter = paramXMLStreamWriter; }
  
  public void add(XMLEventReader paramXMLEventReader) throws XMLStreamException {
    if (paramXMLEventReader == null)
      throw new XMLStreamException("Event reader shouldn't be null"); 
    while (paramXMLEventReader.hasNext())
      add(paramXMLEventReader.nextEvent()); 
  }
  
  public void add(XMLEvent paramXMLEvent) throws XMLStreamException {
    Iterator iterator2;
    Iterator iterator1;
    QName qName;
    Characters characters1;
    Namespace namespace;
    Attribute attribute;
    StartElement startElement;
    DTD dTD;
    StartDocument startDocument;
    Comment comment;
    ProcessingInstruction processingInstruction;
    Characters characters2;
    EntityReference entityReference;
    int i = paramXMLEvent.getEventType();
    switch (i) {
      case 11:
        dTD = (DTD)paramXMLEvent;
        this.fStreamWriter.writeDTD(dTD.getDocumentTypeDeclaration());
        break;
      case 7:
        startDocument = (StartDocument)paramXMLEvent;
        try {
          this.fStreamWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
        } catch (XMLStreamException xMLStreamException) {
          this.fStreamWriter.writeStartDocument(startDocument.getVersion());
        } 
        break;
      case 1:
        startElement = paramXMLEvent.asStartElement();
        qName = startElement.getName();
        this.fStreamWriter.writeStartElement(qName.getPrefix(), qName.getLocalPart(), qName.getNamespaceURI());
        iterator1 = startElement.getNamespaces();
        while (iterator1.hasNext()) {
          Namespace namespace1 = (Namespace)iterator1.next();
          this.fStreamWriter.writeNamespace(namespace1.getPrefix(), namespace1.getNamespaceURI());
        } 
        iterator2 = startElement.getAttributes();
        while (iterator2.hasNext()) {
          Attribute attribute1 = (Attribute)iterator2.next();
          QName qName1 = attribute1.getName();
          this.fStreamWriter.writeAttribute(qName1.getPrefix(), qName1.getNamespaceURI(), qName1.getLocalPart(), attribute1.getValue());
        } 
        break;
      case 13:
        namespace = (Namespace)paramXMLEvent;
        this.fStreamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
        break;
      case 5:
        comment = (Comment)paramXMLEvent;
        this.fStreamWriter.writeComment(comment.getText());
        break;
      case 3:
        processingInstruction = (ProcessingInstruction)paramXMLEvent;
        this.fStreamWriter.writeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
        break;
      case 4:
        characters2 = paramXMLEvent.asCharacters();
        if (characters2.isCData()) {
          this.fStreamWriter.writeCData(characters2.getData());
          break;
        } 
        this.fStreamWriter.writeCharacters(characters2.getData());
        break;
      case 9:
        entityReference = (EntityReference)paramXMLEvent;
        this.fStreamWriter.writeEntityRef(entityReference.getName());
        break;
      case 10:
        attribute = (Attribute)paramXMLEvent;
        qName = attribute.getName();
        this.fStreamWriter.writeAttribute(qName.getPrefix(), qName.getNamespaceURI(), qName.getLocalPart(), attribute.getValue());
        break;
      case 12:
        characters1 = (Characters)paramXMLEvent;
        if (characters1.isCData())
          this.fStreamWriter.writeCData(characters1.getData()); 
        break;
      case 2:
        this.fStreamWriter.writeEndElement();
        break;
      case 8:
        this.fStreamWriter.writeEndDocument();
        break;
    } 
  }
  
  public void close() throws XMLStreamException { this.fStreamWriter.close(); }
  
  public void flush() throws XMLStreamException { this.fStreamWriter.flush(); }
  
  public NamespaceContext getNamespaceContext() { return this.fStreamWriter.getNamespaceContext(); }
  
  public String getPrefix(String paramString) throws XMLStreamException { return this.fStreamWriter.getPrefix(paramString); }
  
  public void setDefaultNamespace(String paramString) throws XMLStreamException { this.fStreamWriter.setDefaultNamespace(paramString); }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) throws XMLStreamException { this.fStreamWriter.setNamespaceContext(paramNamespaceContext); }
  
  public void setPrefix(String paramString1, String paramString2) throws XMLStreamException { this.fStreamWriter.setPrefix(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\writers\XMLEventWriterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */