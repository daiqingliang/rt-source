package com.sun.org.apache.xerces.internal.xni;

public interface XMLDocumentFragmentHandler {
  void startDocumentFragment(XMLLocator paramXMLLocator, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException;
  
  void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException;
  
  void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException;
  
  void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException;
  
  void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException;
  
  void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException;
  
  void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException;
  
  void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException;
  
  void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException;
  
  void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException;
  
  void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException;
  
  void startCDATA(Augmentations paramAugmentations) throws XNIException;
  
  void endCDATA(Augmentations paramAugmentations) throws XNIException;
  
  void endDocumentFragment(Augmentations paramAugmentations) throws XNIException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\XMLDocumentFragmentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */