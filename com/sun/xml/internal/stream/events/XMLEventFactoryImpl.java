package com.sun.xml.internal.stream.events;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

public class XMLEventFactoryImpl extends XMLEventFactory {
  Location location = null;
  
  public Attribute createAttribute(String paramString1, String paramString2) {
    AttributeImpl attributeImpl = new AttributeImpl(paramString1, paramString2);
    if (this.location != null)
      attributeImpl.setLocation(this.location); 
    return attributeImpl;
  }
  
  public Attribute createAttribute(QName paramQName, String paramString) { return createAttribute(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramString); }
  
  public Attribute createAttribute(String paramString1, String paramString2, String paramString3, String paramString4) {
    AttributeImpl attributeImpl = new AttributeImpl(paramString1, paramString2, paramString3, paramString4, null);
    if (this.location != null)
      attributeImpl.setLocation(this.location); 
    return attributeImpl;
  }
  
  public Characters createCData(String paramString) {
    CharacterEvent characterEvent = new CharacterEvent(paramString, true);
    if (this.location != null)
      characterEvent.setLocation(this.location); 
    return characterEvent;
  }
  
  public Characters createCharacters(String paramString) {
    CharacterEvent characterEvent = new CharacterEvent(paramString);
    if (this.location != null)
      characterEvent.setLocation(this.location); 
    return characterEvent;
  }
  
  public Comment createComment(String paramString) {
    CommentEvent commentEvent = new CommentEvent(paramString);
    if (this.location != null)
      commentEvent.setLocation(this.location); 
    return commentEvent;
  }
  
  public DTD createDTD(String paramString) {
    DTDEvent dTDEvent = new DTDEvent(paramString);
    if (this.location != null)
      dTDEvent.setLocation(this.location); 
    return dTDEvent;
  }
  
  public EndDocument createEndDocument() {
    EndDocumentEvent endDocumentEvent = new EndDocumentEvent();
    if (this.location != null)
      endDocumentEvent.setLocation(this.location); 
    return endDocumentEvent;
  }
  
  public EndElement createEndElement(QName paramQName, Iterator paramIterator) { return createEndElement(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  public EndElement createEndElement(String paramString1, String paramString2, String paramString3) {
    EndElementEvent endElementEvent = new EndElementEvent(paramString1, paramString2, paramString3);
    if (this.location != null)
      endElementEvent.setLocation(this.location); 
    return endElementEvent;
  }
  
  public EndElement createEndElement(String paramString1, String paramString2, String paramString3, Iterator paramIterator) {
    EndElementEvent endElementEvent = new EndElementEvent(paramString1, paramString2, paramString3);
    if (paramIterator != null)
      while (paramIterator.hasNext())
        endElementEvent.addNamespace((Namespace)paramIterator.next());  
    if (this.location != null)
      endElementEvent.setLocation(this.location); 
    return endElementEvent;
  }
  
  public EntityReference createEntityReference(String paramString, EntityDeclaration paramEntityDeclaration) {
    EntityReferenceEvent entityReferenceEvent = new EntityReferenceEvent(paramString, paramEntityDeclaration);
    if (this.location != null)
      entityReferenceEvent.setLocation(this.location); 
    return entityReferenceEvent;
  }
  
  public Characters createIgnorableSpace(String paramString) {
    CharacterEvent characterEvent = new CharacterEvent(paramString, false, true);
    if (this.location != null)
      characterEvent.setLocation(this.location); 
    return characterEvent;
  }
  
  public Namespace createNamespace(String paramString) {
    NamespaceImpl namespaceImpl = new NamespaceImpl(paramString);
    if (this.location != null)
      namespaceImpl.setLocation(this.location); 
    return namespaceImpl;
  }
  
  public Namespace createNamespace(String paramString1, String paramString2) {
    NamespaceImpl namespaceImpl = new NamespaceImpl(paramString1, paramString2);
    if (this.location != null)
      namespaceImpl.setLocation(this.location); 
    return namespaceImpl;
  }
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) {
    ProcessingInstructionEvent processingInstructionEvent = new ProcessingInstructionEvent(paramString1, paramString2);
    if (this.location != null)
      processingInstructionEvent.setLocation(this.location); 
    return processingInstructionEvent;
  }
  
  public Characters createSpace(String paramString) {
    CharacterEvent characterEvent = new CharacterEvent(paramString);
    if (this.location != null)
      characterEvent.setLocation(this.location); 
    return characterEvent;
  }
  
  public StartDocument createStartDocument() {
    StartDocumentEvent startDocumentEvent = new StartDocumentEvent();
    if (this.location != null)
      startDocumentEvent.setLocation(this.location); 
    return startDocumentEvent;
  }
  
  public StartDocument createStartDocument(String paramString) {
    StartDocumentEvent startDocumentEvent = new StartDocumentEvent(paramString);
    if (this.location != null)
      startDocumentEvent.setLocation(this.location); 
    return startDocumentEvent;
  }
  
  public StartDocument createStartDocument(String paramString1, String paramString2) {
    StartDocumentEvent startDocumentEvent = new StartDocumentEvent(paramString1, paramString2);
    if (this.location != null)
      startDocumentEvent.setLocation(this.location); 
    return startDocumentEvent;
  }
  
  public StartDocument createStartDocument(String paramString1, String paramString2, boolean paramBoolean) {
    StartDocumentEvent startDocumentEvent = new StartDocumentEvent(paramString1, paramString2, paramBoolean);
    if (this.location != null)
      startDocumentEvent.setLocation(this.location); 
    return startDocumentEvent;
  }
  
  public StartElement createStartElement(QName paramQName, Iterator paramIterator1, Iterator paramIterator2) { return createStartElement(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramIterator1, paramIterator2); }
  
  public StartElement createStartElement(String paramString1, String paramString2, String paramString3) {
    StartElementEvent startElementEvent = new StartElementEvent(paramString1, paramString2, paramString3);
    if (this.location != null)
      startElementEvent.setLocation(this.location); 
    return startElementEvent;
  }
  
  public StartElement createStartElement(String paramString1, String paramString2, String paramString3, Iterator paramIterator1, Iterator paramIterator2) { return createStartElement(paramString1, paramString2, paramString3, paramIterator1, paramIterator2, null); }
  
  public StartElement createStartElement(String paramString1, String paramString2, String paramString3, Iterator paramIterator1, Iterator paramIterator2, NamespaceContext paramNamespaceContext) {
    StartElementEvent startElementEvent = new StartElementEvent(paramString1, paramString2, paramString3);
    startElementEvent.addAttributes(paramIterator1);
    startElementEvent.addNamespaceAttributes(paramIterator2);
    startElementEvent.setNamespaceContext(paramNamespaceContext);
    if (this.location != null)
      startElementEvent.setLocation(this.location); 
    return startElementEvent;
  }
  
  public void setLocation(Location paramLocation) { this.location = paramLocation; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\XMLEventFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */