package com.sun.xml.internal.stream.events;

import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;

public class XMLEventAllocatorImpl implements XMLEventAllocator {
  public XMLEvent allocate(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    if (paramXMLStreamReader == null)
      throw new XMLStreamException("Reader cannot be null"); 
    return getXMLEvent(paramXMLStreamReader);
  }
  
  public void allocate(XMLStreamReader paramXMLStreamReader, XMLEventConsumer paramXMLEventConsumer) throws XMLStreamException {
    XMLEvent xMLEvent = getXMLEvent(paramXMLStreamReader);
    if (xMLEvent != null)
      paramXMLEventConsumer.add(xMLEvent); 
  }
  
  public XMLEventAllocator newInstance() { return new XMLEventAllocatorImpl(); }
  
  XMLEvent getXMLEvent(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    List list2;
    List list1;
    DTDEvent dTDEvent2;
    CharacterEvent characterEvent3;
    EndDocumentEvent endDocumentEvent2;
    EndElementEvent endElementEvent2;
    ProcessingInstructionEvent processingInstructionEvent2;
    CommentEvent commentEvent2;
    StartDocumentEvent startDocumentEvent2;
    StartElementEvent startElementEvent2;
    EntityReferenceEvent entityReferenceEvent2;
    CharacterEvent characterEvent4;
    StartDocumentEvent startDocumentEvent1;
    CharacterEvent characterEvent1;
    EndDocumentEvent endDocumentEvent1;
    ProcessingInstructionEvent processingInstructionEvent1;
    CharacterEvent characterEvent2;
    DTDEvent dTDEvent1;
    CommentEvent commentEvent1;
    EndElementEvent endElementEvent1;
    EntityReferenceEvent entityReferenceEvent1;
    StartElementEvent startElementEvent1 = null;
    int i = paramXMLStreamReader.getEventType();
    switch (i) {
      case 1:
        startElementEvent2 = new StartElementEvent(getQName(paramXMLStreamReader));
        fillAttributes(startElementEvent2, paramXMLStreamReader);
        if (((Boolean)paramXMLStreamReader.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue()) {
          fillNamespaceAttributes(startElementEvent2, paramXMLStreamReader);
          setNamespaceContext(startElementEvent2, paramXMLStreamReader);
        } 
        startElementEvent2.setLocation(paramXMLStreamReader.getLocation());
        startElementEvent1 = startElementEvent2;
        break;
      case 2:
        endElementEvent2 = new EndElementEvent(getQName(paramXMLStreamReader));
        endElementEvent2.setLocation(paramXMLStreamReader.getLocation());
        if (((Boolean)paramXMLStreamReader.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue())
          fillNamespaceAttributes(endElementEvent2, paramXMLStreamReader); 
        endElementEvent1 = endElementEvent2;
        break;
      case 3:
        processingInstructionEvent2 = new ProcessingInstructionEvent(paramXMLStreamReader.getPITarget(), paramXMLStreamReader.getPIData());
        processingInstructionEvent2.setLocation(paramXMLStreamReader.getLocation());
        processingInstructionEvent1 = processingInstructionEvent2;
        break;
      case 4:
        characterEvent4 = new CharacterEvent(paramXMLStreamReader.getText());
        characterEvent4.setLocation(paramXMLStreamReader.getLocation());
        characterEvent2 = characterEvent4;
        break;
      case 5:
        commentEvent2 = new CommentEvent(paramXMLStreamReader.getText());
        commentEvent2.setLocation(paramXMLStreamReader.getLocation());
        commentEvent1 = commentEvent2;
        break;
      case 7:
        startDocumentEvent2 = new StartDocumentEvent();
        startDocumentEvent2.setVersion(paramXMLStreamReader.getVersion());
        startDocumentEvent2.setEncoding(paramXMLStreamReader.getEncoding());
        if (paramXMLStreamReader.getCharacterEncodingScheme() != null) {
          startDocumentEvent2.setDeclaredEncoding(true);
        } else {
          startDocumentEvent2.setDeclaredEncoding(false);
        } 
        startDocumentEvent2.setStandalone(paramXMLStreamReader.isStandalone());
        startDocumentEvent2.setLocation(paramXMLStreamReader.getLocation());
        startDocumentEvent1 = startDocumentEvent2;
        break;
      case 8:
        endDocumentEvent2 = new EndDocumentEvent();
        endDocumentEvent2.setLocation(paramXMLStreamReader.getLocation());
        endDocumentEvent1 = endDocumentEvent2;
        break;
      case 9:
        entityReferenceEvent2 = new EntityReferenceEvent(paramXMLStreamReader.getLocalName(), new EntityDeclarationImpl(paramXMLStreamReader.getLocalName(), paramXMLStreamReader.getText()));
        entityReferenceEvent2.setLocation(paramXMLStreamReader.getLocation());
        entityReferenceEvent1 = entityReferenceEvent2;
        break;
      case 10:
        entityReferenceEvent1 = null;
        break;
      case 11:
        dTDEvent2 = new DTDEvent(paramXMLStreamReader.getText());
        dTDEvent2.setLocation(paramXMLStreamReader.getLocation());
        list1 = (List)paramXMLStreamReader.getProperty("javax.xml.stream.entities");
        if (list1 != null && list1.size() != 0)
          dTDEvent2.setEntities(list1); 
        list2 = (List)paramXMLStreamReader.getProperty("javax.xml.stream.notations");
        if (list2 != null && list2.size() != 0)
          dTDEvent2.setNotations(list2); 
        dTDEvent1 = dTDEvent2;
        break;
      case 12:
        characterEvent3 = new CharacterEvent(paramXMLStreamReader.getText(), true);
        characterEvent3.setLocation(paramXMLStreamReader.getLocation());
        characterEvent1 = characterEvent3;
        break;
      case 6:
        characterEvent3 = new CharacterEvent(paramXMLStreamReader.getText(), false, true);
        characterEvent3.setLocation(paramXMLStreamReader.getLocation());
        characterEvent1 = characterEvent3;
        break;
    } 
    return characterEvent1;
  }
  
  protected XMLEvent getNextEvent(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    paramXMLStreamReader.next();
    return getXMLEvent(paramXMLStreamReader);
  }
  
  protected void fillAttributes(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader) {
    int i = paramXMLStreamReader.getAttributeCount();
    QName qName = null;
    AttributeImpl attributeImpl = null;
    Object object = null;
    for (byte b = 0; b < i; b++) {
      qName = paramXMLStreamReader.getAttributeName(b);
      attributeImpl = new AttributeImpl();
      attributeImpl.setName(qName);
      attributeImpl.setAttributeType(paramXMLStreamReader.getAttributeType(b));
      attributeImpl.setSpecified(paramXMLStreamReader.isAttributeSpecified(b));
      attributeImpl.setValue(paramXMLStreamReader.getAttributeValue(b));
      paramStartElementEvent.addAttribute(attributeImpl);
    } 
  }
  
  protected void fillNamespaceAttributes(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader) {
    int i = paramXMLStreamReader.getNamespaceCount();
    String str1 = null;
    String str2 = null;
    NamespaceImpl namespaceImpl = null;
    for (byte b = 0; b < i; b++) {
      str1 = paramXMLStreamReader.getNamespaceURI(b);
      str2 = paramXMLStreamReader.getNamespacePrefix(b);
      if (str2 == null)
        str2 = ""; 
      namespaceImpl = new NamespaceImpl(str2, str1);
      paramStartElementEvent.addNamespaceAttribute(namespaceImpl);
    } 
  }
  
  protected void fillNamespaceAttributes(EndElementEvent paramEndElementEvent, XMLStreamReader paramXMLStreamReader) {
    int i = paramXMLStreamReader.getNamespaceCount();
    String str1 = null;
    String str2 = null;
    NamespaceImpl namespaceImpl = null;
    for (byte b = 0; b < i; b++) {
      str1 = paramXMLStreamReader.getNamespaceURI(b);
      str2 = paramXMLStreamReader.getNamespacePrefix(b);
      if (str2 == null)
        str2 = ""; 
      namespaceImpl = new NamespaceImpl(str2, str1);
      paramEndElementEvent.addNamespace(namespaceImpl);
    } 
  }
  
  private void setNamespaceContext(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader) {
    NamespaceContextWrapper namespaceContextWrapper = (NamespaceContextWrapper)paramXMLStreamReader.getNamespaceContext();
    NamespaceSupport namespaceSupport = new NamespaceSupport(namespaceContextWrapper.getNamespaceContext());
    paramStartElementEvent.setNamespaceContext(new NamespaceContextWrapper(namespaceSupport));
  }
  
  private QName getQName(XMLStreamReader paramXMLStreamReader) { return new QName(paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName(), paramXMLStreamReader.getPrefix()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\XMLEventAllocatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */