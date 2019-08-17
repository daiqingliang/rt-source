package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StreamReaderBufferCreator extends StreamBufferCreator {
  private int _eventType;
  
  private boolean _storeInScopeNamespacesOnElementFragment;
  
  private Map<String, Integer> _inScopePrefixes;
  
  public StreamReaderBufferCreator() {}
  
  public StreamReaderBufferCreator(MutableXMLStreamBuffer paramMutableXMLStreamBuffer) { setBuffer(paramMutableXMLStreamBuffer); }
  
  public MutableXMLStreamBuffer create(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    if (this._buffer == null)
      createBuffer(); 
    store(paramXMLStreamReader);
    return getXMLStreamBuffer();
  }
  
  public MutableXMLStreamBuffer createElementFragment(XMLStreamReader paramXMLStreamReader, boolean paramBoolean) throws XMLStreamException {
    if (this._buffer == null)
      createBuffer(); 
    if (!paramXMLStreamReader.hasNext())
      return this._buffer; 
    this._storeInScopeNamespacesOnElementFragment = paramBoolean;
    this._eventType = paramXMLStreamReader.getEventType();
    if (this._eventType != 1)
      do {
        this._eventType = paramXMLStreamReader.next();
      } while (this._eventType != 1 && this._eventType != 8); 
    if (paramBoolean)
      this._inScopePrefixes = new HashMap(); 
    storeElementAndChildren(paramXMLStreamReader);
    return getXMLStreamBuffer();
  }
  
  private void store(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    if (!paramXMLStreamReader.hasNext())
      return; 
    this._eventType = paramXMLStreamReader.getEventType();
    switch (this._eventType) {
      case 7:
        storeDocumentAndChildren(paramXMLStreamReader);
        break;
      case 1:
        storeElementAndChildren(paramXMLStreamReader);
        break;
      default:
        throw new XMLStreamException("XMLStreamReader not positioned at a document or element");
    } 
    increaseTreeCount();
  }
  
  private void storeDocumentAndChildren(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    storeStructure(16);
    this._eventType = paramXMLStreamReader.next();
    while (this._eventType != 8) {
      switch (this._eventType) {
        case 1:
          storeElementAndChildren(paramXMLStreamReader);
          continue;
        case 5:
          storeComment(paramXMLStreamReader);
          break;
        case 3:
          storeProcessingInstruction(paramXMLStreamReader);
          break;
      } 
      this._eventType = paramXMLStreamReader.next();
    } 
    storeStructure(144);
  }
  
  private void storeElementAndChildren(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    if (paramXMLStreamReader instanceof XMLStreamReaderEx) {
      storeElementAndChildrenEx((XMLStreamReaderEx)paramXMLStreamReader);
    } else {
      storeElementAndChildrenNoEx(paramXMLStreamReader);
    } 
  }
  
  private void storeElementAndChildrenEx(XMLStreamReaderEx paramXMLStreamReaderEx) throws XMLStreamException {
    byte b = 1;
    if (this._storeInScopeNamespacesOnElementFragment) {
      storeElementWithInScopeNamespaces(paramXMLStreamReaderEx);
    } else {
      storeElement(paramXMLStreamReaderEx);
    } 
    while (b) {
      CharSequence charSequence;
      this._eventType = paramXMLStreamReaderEx.next();
      switch (this._eventType) {
        case 1:
          b++;
          storeElement(paramXMLStreamReaderEx);
        case 2:
          b--;
          storeStructure(144);
        case 13:
          storeNamespaceAttributes(paramXMLStreamReaderEx);
        case 10:
          storeAttributes(paramXMLStreamReaderEx);
        case 4:
        case 6:
        case 12:
          charSequence = paramXMLStreamReaderEx.getPCDATA();
          if (charSequence instanceof com.sun.xml.internal.org.jvnet.staxex.Base64Data) {
            storeStructure(92);
            storeContentObject(charSequence);
            continue;
          } 
          storeContentCharacters(80, paramXMLStreamReaderEx.getTextCharacters(), paramXMLStreamReaderEx.getTextStart(), paramXMLStreamReaderEx.getTextLength());
        case 5:
          storeComment(paramXMLStreamReaderEx);
        case 3:
          storeProcessingInstruction(paramXMLStreamReaderEx);
      } 
    } 
    this._eventType = paramXMLStreamReaderEx.next();
  }
  
  private void storeElementAndChildrenNoEx(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    byte b = 1;
    if (this._storeInScopeNamespacesOnElementFragment) {
      storeElementWithInScopeNamespaces(paramXMLStreamReader);
    } else {
      storeElement(paramXMLStreamReader);
    } 
    while (b) {
      this._eventType = paramXMLStreamReader.next();
      switch (this._eventType) {
        case 1:
          b++;
          storeElement(paramXMLStreamReader);
        case 2:
          b--;
          storeStructure(144);
        case 13:
          storeNamespaceAttributes(paramXMLStreamReader);
        case 10:
          storeAttributes(paramXMLStreamReader);
        case 4:
        case 6:
        case 12:
          storeContentCharacters(80, paramXMLStreamReader.getTextCharacters(), paramXMLStreamReader.getTextStart(), paramXMLStreamReader.getTextLength());
        case 5:
          storeComment(paramXMLStreamReader);
        case 3:
          storeProcessingInstruction(paramXMLStreamReader);
      } 
    } 
    this._eventType = paramXMLStreamReader.next();
  }
  
  private void storeElementWithInScopeNamespaces(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    storeQualifiedName(32, paramXMLStreamReader.getPrefix(), paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName());
    if (paramXMLStreamReader.getNamespaceCount() > 0)
      storeNamespaceAttributes(paramXMLStreamReader); 
    if (paramXMLStreamReader.getAttributeCount() > 0)
      storeAttributes(paramXMLStreamReader); 
  }
  
  private void storeElement(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    storeQualifiedName(32, paramXMLStreamReader.getPrefix(), paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName());
    if (paramXMLStreamReader.getNamespaceCount() > 0)
      storeNamespaceAttributes(paramXMLStreamReader); 
    if (paramXMLStreamReader.getAttributeCount() > 0)
      storeAttributes(paramXMLStreamReader); 
  }
  
  public void storeElement(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    storeQualifiedName(32, paramString3, paramString1, paramString2);
    storeNamespaceAttributes(paramArrayOfString);
  }
  
  public void storeEndElement() { storeStructure(144); }
  
  private void storeNamespaceAttributes(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    int i = paramXMLStreamReader.getNamespaceCount();
    for (byte b = 0; b < i; b++)
      storeNamespaceAttribute(paramXMLStreamReader.getNamespacePrefix(b), paramXMLStreamReader.getNamespaceURI(b)); 
  }
  
  private void storeNamespaceAttributes(String[] paramArrayOfString) {
    for (byte b = 0; b < paramArrayOfString.length; b += 2)
      storeNamespaceAttribute(paramArrayOfString[b], paramArrayOfString[b + true]); 
  }
  
  private void storeAttributes(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    int i = paramXMLStreamReader.getAttributeCount();
    for (byte b = 0; b < i; b++)
      storeAttribute(paramXMLStreamReader.getAttributePrefix(b), paramXMLStreamReader.getAttributeNamespace(b), paramXMLStreamReader.getAttributeLocalName(b), paramXMLStreamReader.getAttributeType(b), paramXMLStreamReader.getAttributeValue(b)); 
  }
  
  private void storeComment(XMLStreamReader paramXMLStreamReader) throws XMLStreamException { storeContentCharacters(96, paramXMLStreamReader.getTextCharacters(), paramXMLStreamReader.getTextStart(), paramXMLStreamReader.getTextLength()); }
  
  private void storeProcessingInstruction(XMLStreamReader paramXMLStreamReader) throws XMLStreamException { storeProcessingInstruction(paramXMLStreamReader.getPITarget(), paramXMLStreamReader.getPIData()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\stax\StreamReaderBufferCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */