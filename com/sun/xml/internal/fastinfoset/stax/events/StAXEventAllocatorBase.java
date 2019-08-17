package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;

public class StAXEventAllocatorBase implements XMLEventAllocator {
  XMLEventFactory factory;
  
  public StAXEventAllocatorBase() {
    if (System.getProperty("javax.xml.stream.XMLEventFactory") == null)
      System.setProperty("javax.xml.stream.XMLEventFactory", "com.sun.xml.internal.fastinfoset.stax.factory.StAXEventFactory"); 
    this.factory = XMLEventFactory.newInstance();
  }
  
  public XMLEventAllocator newInstance() { return new StAXEventAllocatorBase(); }
  
  public XMLEvent allocate(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    if (paramXMLStreamReader == null)
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.nullReader")); 
    return getXMLEvent(paramXMLStreamReader);
  }
  
  public void allocate(XMLStreamReader paramXMLStreamReader, XMLEventConsumer paramXMLEventConsumer) throws XMLStreamException { paramXMLEventConsumer.add(getXMLEvent(paramXMLStreamReader)); }
  
  XMLEvent getXMLEvent(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    EndDocumentEvent endDocumentEvent2;
    EndElementEvent endElementEvent2;
    StartElementEvent startElementEvent2;
    StartDocumentEvent startDocumentEvent2;
    StartDocumentEvent startDocumentEvent1;
    Characters characters2;
    Comment comment;
    EndElementEvent endElementEvent1;
    ProcessingInstruction processingInstruction;
    EntityReference entityReference;
    DTD dTD;
    EndDocumentEvent endDocumentEvent1;
    Characters characters1;
    StartElementEvent startElementEvent1 = null;
    int i = paramXMLStreamReader.getEventType();
    this.factory.setLocation(paramXMLStreamReader.getLocation());
    switch (i) {
      case 1:
        startElementEvent2 = (StartElementEvent)this.factory.createStartElement(paramXMLStreamReader.getPrefix(), paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName());
        addAttributes(startElementEvent2, paramXMLStreamReader);
        addNamespaces(startElementEvent2, paramXMLStreamReader);
        startElementEvent1 = startElementEvent2;
        break;
      case 2:
        endElementEvent2 = (EndElementEvent)this.factory.createEndElement(paramXMLStreamReader.getPrefix(), paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName());
        addNamespaces(endElementEvent2, paramXMLStreamReader);
        endElementEvent1 = endElementEvent2;
        break;
      case 3:
        processingInstruction = this.factory.createProcessingInstruction(paramXMLStreamReader.getPITarget(), paramXMLStreamReader.getPIData());
        break;
      case 4:
        if (paramXMLStreamReader.isWhiteSpace()) {
          Characters characters = this.factory.createSpace(paramXMLStreamReader.getText());
          break;
        } 
        characters2 = this.factory.createCharacters(paramXMLStreamReader.getText());
        break;
      case 5:
        comment = this.factory.createComment(paramXMLStreamReader.getText());
        break;
      case 7:
        startDocumentEvent2 = (StartDocumentEvent)this.factory.createStartDocument(paramXMLStreamReader.getVersion(), paramXMLStreamReader.getEncoding(), paramXMLStreamReader.isStandalone());
        if (paramXMLStreamReader.getCharacterEncodingScheme() != null) {
          startDocumentEvent2.setDeclaredEncoding(true);
        } else {
          startDocumentEvent2.setDeclaredEncoding(false);
        } 
        startDocumentEvent1 = startDocumentEvent2;
        break;
      case 8:
        endDocumentEvent2 = new EndDocumentEvent();
        endDocumentEvent1 = endDocumentEvent2;
        break;
      case 9:
        entityReference = this.factory.createEntityReference(paramXMLStreamReader.getLocalName(), new EntityDeclarationImpl(paramXMLStreamReader.getLocalName(), paramXMLStreamReader.getText()));
        break;
      case 10:
        entityReference = null;
        break;
      case 11:
        dTD = this.factory.createDTD(paramXMLStreamReader.getText());
        break;
      case 12:
        characters1 = this.factory.createCData(paramXMLStreamReader.getText());
        break;
      case 6:
        characters1 = this.factory.createSpace(paramXMLStreamReader.getText());
        break;
    } 
    return characters1;
  }
  
  protected void addAttributes(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader) {
    AttributeBase attributeBase = null;
    for (byte b = 0; b < paramXMLStreamReader.getAttributeCount(); b++) {
      attributeBase = (AttributeBase)this.factory.createAttribute(paramXMLStreamReader.getAttributeName(b), paramXMLStreamReader.getAttributeValue(b));
      attributeBase.setAttributeType(paramXMLStreamReader.getAttributeType(b));
      attributeBase.setSpecified(paramXMLStreamReader.isAttributeSpecified(b));
      paramStartElementEvent.addAttribute(attributeBase);
    } 
  }
  
  protected void addNamespaces(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader) {
    Namespace namespace = null;
    for (byte b = 0; b < paramXMLStreamReader.getNamespaceCount(); b++) {
      namespace = this.factory.createNamespace(paramXMLStreamReader.getNamespacePrefix(b), paramXMLStreamReader.getNamespaceURI(b));
      paramStartElementEvent.addNamespace(namespace);
    } 
  }
  
  protected void addNamespaces(EndElementEvent paramEndElementEvent, XMLStreamReader paramXMLStreamReader) {
    Namespace namespace = null;
    for (byte b = 0; b < paramXMLStreamReader.getNamespaceCount(); b++) {
      namespace = this.factory.createNamespace(paramXMLStreamReader.getNamespacePrefix(b), paramXMLStreamReader.getNamespaceURI(b));
      paramEndElementEvent.addNamespace(namespace);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StAXEventAllocatorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */