package com.sun.org.apache.xerces.internal.xni;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;

public interface XMLDTDHandler {
  public static final short CONDITIONAL_INCLUDE = 0;
  
  public static final short CONDITIONAL_IGNORE = 1;
  
  void startDTD(XMLLocator paramXMLLocator, Augmentations paramAugmentations) throws XNIException;
  
  void startParameterEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException;
  
  void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException;
  
  void endParameterEntity(String paramString, Augmentations paramAugmentations) throws XNIException;
  
  void startExternalSubset(XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException;
  
  void endExternalSubset(Augmentations paramAugmentations) throws XNIException;
  
  void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException;
  
  void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException;
  
  void elementDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException;
  
  void startAttlist(String paramString, Augmentations paramAugmentations) throws XNIException;
  
  void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException;
  
  void endAttlist(Augmentations paramAugmentations) throws XNIException;
  
  void internalEntityDecl(String paramString, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException;
  
  void externalEntityDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException;
  
  void unparsedEntityDecl(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException;
  
  void notationDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException;
  
  void startConditional(short paramShort, Augmentations paramAugmentations) throws XNIException;
  
  void ignoredCharacters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException;
  
  void endConditional(Augmentations paramAugmentations) throws XNIException;
  
  void endDTD(Augmentations paramAugmentations) throws XNIException;
  
  void setDTDSource(XMLDTDSource paramXMLDTDSource);
  
  XMLDTDSource getDTDSource();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\XMLDTDHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */