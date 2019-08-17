package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;

public abstract class DTDParser extends XMLGrammarParser implements XMLDTDHandler, XMLDTDContentModelHandler {
  protected XMLDTDScanner fDTDScanner;
  
  public DTDParser(SymbolTable paramSymbolTable) { super(paramSymbolTable); }
  
  public DTDGrammar getDTDGrammar() { return null; }
  
  public void startEntity(String paramString1, String paramString2, String paramString3, String paramString4) throws XNIException {}
  
  public void textDecl(String paramString1, String paramString2) throws XNIException {}
  
  public void startDTD(XMLLocator paramXMLLocator, Augmentations paramAugmentations) throws XNIException {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
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
  
  public void endConditional(Augmentations paramAugmentations) throws XNIException {}
  
  public void endDTD(Augmentations paramAugmentations) throws XNIException {}
  
  public void endEntity(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void startContentModel(String paramString, short paramShort) throws XNIException {}
  
  public void mixedElement(String paramString) throws XNIException {}
  
  public void childrenStartGroup() throws XNIException {}
  
  public void childrenElement(String paramString) throws XNIException {}
  
  public void childrenSeparator(short paramShort) throws XNIException {}
  
  public void childrenOccurrence(short paramShort) throws XNIException {}
  
  public void childrenEndGroup() throws XNIException {}
  
  public void endContentModel() throws XNIException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\DTDParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */