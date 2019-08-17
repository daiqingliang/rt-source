package com.sun.org.apache.xerces.internal.util;

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

public class XMLDocumentFilterImpl implements XMLDocumentFilter {
  private XMLDocumentHandler next;
  
  private XMLDocumentSource source;
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler) { this.next = paramXMLDocumentHandler; }
  
  public XMLDocumentHandler getDocumentHandler() { return this.next; }
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) { this.source = paramXMLDocumentSource; }
  
  public XMLDocumentSource getDocumentSource() { return this.source; }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException { this.next.characters(paramXMLString, paramAugmentations); }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException { this.next.comment(paramXMLString, paramAugmentations); }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException { this.next.doctypeDecl(paramString1, paramString2, paramString3, paramAugmentations); }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException { this.next.emptyElement(paramQName, paramXMLAttributes, paramAugmentations); }
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException { this.next.endCDATA(paramAugmentations); }
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException { this.next.endDocument(paramAugmentations); }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException { this.next.endElement(paramQName, paramAugmentations); }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException { this.next.endGeneralEntity(paramString, paramAugmentations); }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException { this.next.ignorableWhitespace(paramXMLString, paramAugmentations); }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException { this.next.processingInstruction(paramString, paramXMLString, paramAugmentations); }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException { this.next.startCDATA(paramAugmentations); }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException { this.next.startDocument(paramXMLLocator, paramString, paramNamespaceContext, paramAugmentations); }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException { this.next.startElement(paramQName, paramXMLAttributes, paramAugmentations); }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException { this.next.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations); }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException { this.next.textDecl(paramString1, paramString2, paramAugmentations); }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException { this.next.xmlDecl(paramString1, paramString2, paramString3, paramAugmentations); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\XMLDocumentFilterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */