package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

final class DOMResultAugmentor implements DOMDocumentHandler {
  private DOMValidatorHelper fDOMValidatorHelper;
  
  private Document fDocument;
  
  private CoreDocumentImpl fDocumentImpl;
  
  private boolean fStorePSVI;
  
  private boolean fIgnoreChars;
  
  private final QName fAttributeQName = new QName();
  
  public DOMResultAugmentor(DOMValidatorHelper paramDOMValidatorHelper) { this.fDOMValidatorHelper = paramDOMValidatorHelper; }
  
  public void setDOMResult(DOMResult paramDOMResult) {
    this.fIgnoreChars = false;
    if (paramDOMResult != null) {
      Node node = paramDOMResult.getNode();
      this.fDocument = (node.getNodeType() == 9) ? (Document)node : node.getOwnerDocument();
      this.fDocumentImpl = (this.fDocument instanceof CoreDocumentImpl) ? (CoreDocumentImpl)this.fDocument : null;
      this.fStorePSVI = this.fDocument instanceof com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
      return;
    } 
    this.fDocument = null;
    this.fDocumentImpl = null;
    this.fStorePSVI = false;
  }
  
  public void doctypeDecl(DocumentType paramDocumentType) throws XNIException {}
  
  public void characters(Text paramText) throws XNIException {}
  
  public void cdata(CDATASection paramCDATASection) throws XNIException {}
  
  public void comment(Comment paramComment) throws XNIException {}
  
  public void processingInstruction(ProcessingInstruction paramProcessingInstruction) throws XNIException {}
  
  public void setIgnoringCharacters(boolean paramBoolean) { this.fIgnoreChars = paramBoolean; }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {}
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    Element element = (Element)this.fDOMValidatorHelper.getCurrentElement();
    NamedNodeMap namedNodeMap = element.getAttributes();
    int i = namedNodeMap.getLength();
    if (this.fDocumentImpl != null)
      for (byte b = 0; b < i; b++) {
        AttrImpl attrImpl = (AttrImpl)namedNodeMap.item(b);
        AttributePSVI attributePSVI = (AttributePSVI)paramXMLAttributes.getAugmentations(b).getItem("ATTRIBUTE_PSVI");
        if (attributePSVI != null && processAttributePSVI(attrImpl, attributePSVI))
          ((ElementImpl)element).setIdAttributeNode(attrImpl, true); 
      }  
    int j = paramXMLAttributes.getLength();
    if (j > i)
      if (this.fDocumentImpl == null) {
        for (int k = i; k < j; k++) {
          paramXMLAttributes.getName(k, this.fAttributeQName);
          element.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, paramXMLAttributes.getValue(k));
        } 
      } else {
        for (int k = i; k < j; k++) {
          paramXMLAttributes.getName(k, this.fAttributeQName);
          AttrImpl attrImpl = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
          attrImpl.setValue(paramXMLAttributes.getValue(k));
          AttributePSVI attributePSVI = (AttributePSVI)paramXMLAttributes.getAugmentations(k).getItem("ATTRIBUTE_PSVI");
          if (attributePSVI != null && processAttributePSVI(attrImpl, attributePSVI))
            ((ElementImpl)element).setIdAttributeNode(attrImpl, true); 
          attrImpl.setSpecified(false);
          element.setAttributeNode(attrImpl);
        } 
      }  
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    startElement(paramQName, paramXMLAttributes, paramAugmentations);
    endElement(paramQName, paramAugmentations);
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (!this.fIgnoreChars) {
      Element element = (Element)this.fDOMValidatorHelper.getCurrentElement();
      element.appendChild(this.fDocument.createTextNode(paramXMLString.toString()));
    } 
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException { characters(paramXMLString, paramAugmentations); }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    Node node = this.fDOMValidatorHelper.getCurrentElement();
    if (paramAugmentations != null && this.fDocumentImpl != null) {
      ElementPSVI elementPSVI = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
      if (elementPSVI != null) {
        if (this.fStorePSVI)
          ((PSVIElementNSImpl)node).setPSVI(elementPSVI); 
        XSTypeDefinition xSTypeDefinition = elementPSVI.getMemberTypeDefinition();
        if (xSTypeDefinition == null)
          xSTypeDefinition = elementPSVI.getTypeDefinition(); 
        ((ElementNSImpl)node).setType(xSTypeDefinition);
      } 
    } 
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {}
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) {}
  
  public XMLDocumentSource getDocumentSource() { return null; }
  
  private boolean processAttributePSVI(AttrImpl paramAttrImpl, AttributePSVI paramAttributePSVI) {
    if (this.fStorePSVI)
      ((PSVIAttrNSImpl)paramAttrImpl).setPSVI(paramAttributePSVI); 
    XSTypeDefinition xSTypeDefinition = paramAttributePSVI.getMemberTypeDefinition();
    if (xSTypeDefinition == null) {
      xSTypeDefinition = paramAttributePSVI.getTypeDefinition();
      if (xSTypeDefinition != null) {
        paramAttrImpl.setType(xSTypeDefinition);
        return ((XSSimpleType)xSTypeDefinition).isIDType();
      } 
    } else {
      paramAttrImpl.setType(xSTypeDefinition);
      return ((XSSimpleType)xSTypeDefinition).isIDType();
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\DOMResultAugmentor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */