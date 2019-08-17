package com.sun.org.apache.xerces.internal.impl.xs.opti;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

public class DefaultXMLDocumentHandler implements XMLDocumentHandler, XMLDTDHandler, XMLDTDContentModelHandler {
  private XMLDocumentSource fDocumentSource;
  
  private XMLDTDSource fDTDSource;
  
  private XMLDTDContentModelSource fCMSource;
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {}
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void startPrefixMapping(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {}
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {}
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {}
  
  public void endPrefixMapping(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {}
  
  public void startDTD(XMLLocator paramXMLLocator, Augmentations paramAugmentations) throws XNIException {}
  
  public void startParameterEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void endParameterEntity(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void startExternalSubset(XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {}
  
  public void endExternalSubset(Augmentations paramAugmentations) throws XNIException {}
  
  public void elementDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void startAttlist(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void endAttlist(Augmentations paramAugmentations) throws XNIException {}
  
  public void internalEntityDecl(String paramString, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void externalEntityDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {}
  
  public void unparsedEntityDecl(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void notationDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {}
  
  public void startConditional(short paramShort, Augmentations paramAugmentations) throws XNIException {}
  
  public void ignoredCharacters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void endConditional(Augmentations paramAugmentations) throws XNIException {}
  
  public void endDTD(Augmentations paramAugmentations) throws XNIException {}
  
  public void startContentModel(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void any(Augmentations paramAugmentations) throws XNIException {}
  
  public void empty(Augmentations paramAugmentations) throws XNIException {}
  
  public void startGroup(Augmentations paramAugmentations) throws XNIException {}
  
  public void pcdata(Augmentations paramAugmentations) throws XNIException {}
  
  public void element(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void separator(short paramShort, Augmentations paramAugmentations) throws XNIException {}
  
  public void occurrence(short paramShort, Augmentations paramAugmentations) throws XNIException {}
  
  public void endGroup(Augmentations paramAugmentations) throws XNIException {}
  
  public void endContentModel(Augmentations paramAugmentations) throws XNIException {}
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) { this.fDocumentSource = paramXMLDocumentSource; }
  
  public XMLDocumentSource getDocumentSource() { return this.fDocumentSource; }
  
  public void setDTDSource(XMLDTDSource paramXMLDTDSource) { this.fDTDSource = paramXMLDTDSource; }
  
  public XMLDTDSource getDTDSource() { return this.fDTDSource; }
  
  public void setDTDContentModelSource(XMLDTDContentModelSource paramXMLDTDContentModelSource) { this.fCMSource = paramXMLDTDContentModelSource; }
  
  public XMLDTDContentModelSource getDTDContentModelSource() { return this.fCMSource; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\DefaultXMLDocumentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */