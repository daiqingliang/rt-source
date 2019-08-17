package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

class TeeXMLDocumentFilterImpl implements XMLDocumentFilter {
  private XMLDocumentHandler next;
  
  private XMLDocumentHandler side;
  
  private XMLDocumentSource source;
  
  public XMLDocumentHandler getSide() { return this.side; }
  
  public void setSide(XMLDocumentHandler paramXMLDocumentHandler) { this.side = paramXMLDocumentHandler; }
  
  public XMLDocumentSource getDocumentSource() { return this.source; }
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) { this.source = paramXMLDocumentSource; }
  
  public XMLDocumentHandler getDocumentHandler() { return this.next; }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler) { this.next = paramXMLDocumentHandler; }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    this.side.characters(paramXMLString, paramAugmentations);
    this.next.characters(paramXMLString, paramAugmentations);
  }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    this.side.comment(paramXMLString, paramAugmentations);
    this.next.comment(paramXMLString, paramAugmentations);
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    this.side.doctypeDecl(paramString1, paramString2, paramString3, paramAugmentations);
    this.next.doctypeDecl(paramString1, paramString2, paramString3, paramAugmentations);
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    this.side.emptyElement(paramQName, paramXMLAttributes, paramAugmentations);
    this.next.emptyElement(paramQName, paramXMLAttributes, paramAugmentations);
  }
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {
    this.side.endCDATA(paramAugmentations);
    this.next.endCDATA(paramAugmentations);
  }
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {
    this.side.endDocument(paramAugmentations);
    this.next.endDocument(paramAugmentations);
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    this.side.endElement(paramQName, paramAugmentations);
    this.next.endElement(paramQName, paramAugmentations);
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {
    this.side.endGeneralEntity(paramString, paramAugmentations);
    this.next.endGeneralEntity(paramString, paramAugmentations);
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    this.side.ignorableWhitespace(paramXMLString, paramAugmentations);
    this.next.ignorableWhitespace(paramXMLString, paramAugmentations);
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    this.side.processingInstruction(paramString, paramXMLString, paramAugmentations);
    this.next.processingInstruction(paramString, paramXMLString, paramAugmentations);
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {
    this.side.startCDATA(paramAugmentations);
    this.next.startCDATA(paramAugmentations);
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {
    this.side.startDocument(paramXMLLocator, paramString, paramNamespaceContext, paramAugmentations);
    this.next.startDocument(paramXMLLocator, paramString, paramNamespaceContext, paramAugmentations);
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    this.side.startElement(paramQName, paramXMLAttributes, paramAugmentations);
    this.next.startElement(paramQName, paramXMLAttributes, paramAugmentations);
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    this.side.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    this.next.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {
    this.side.textDecl(paramString1, paramString2, paramAugmentations);
    this.next.textDecl(paramString1, paramString2, paramAugmentations);
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    this.side.xmlDecl(paramString1, paramString2, paramString3, paramAugmentations);
    this.next.xmlDecl(paramString1, paramString2, paramString3, paramAugmentations);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\TeeXMLDocumentFilterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */