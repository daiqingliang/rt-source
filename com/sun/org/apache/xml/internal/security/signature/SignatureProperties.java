package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureProperties extends SignatureElementProxy {
  public SignatureProperties(Document paramDocument) {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public SignatureProperties(Element paramElement, String paramString) throws XMLSecurityException {
    super(paramElement, paramString);
    Attr attr = paramElement.getAttributeNodeNS(null, "Id");
    if (attr != null)
      paramElement.setIdAttributeNode(attr, true); 
    int i = getLength();
    for (byte b = 0; b < i; b++) {
      Element element = XMLUtils.selectDsNode(this.constructionElement, "SignatureProperty", b);
      Attr attr1 = element.getAttributeNodeNS(null, "Id");
      if (attr1 != null)
        element.setIdAttributeNode(attr1, true); 
    } 
  }
  
  public int getLength() {
    Element[] arrayOfElement = XMLUtils.selectDsNodes(this.constructionElement, "SignatureProperty");
    return arrayOfElement.length;
  }
  
  public SignatureProperty item(int paramInt) throws XMLSignatureException {
    try {
      Element element = XMLUtils.selectDsNode(this.constructionElement, "SignatureProperty", paramInt);
      return (element == null) ? null : new SignatureProperty(element, this.baseURI);
    } catch (XMLSecurityException xMLSecurityException) {
      throw new XMLSignatureException("empty", xMLSecurityException);
    } 
  }
  
  public void setId(String paramString) {
    if (paramString != null) {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    } 
  }
  
  public String getId() { return this.constructionElement.getAttributeNS(null, "Id"); }
  
  public void addSignatureProperty(SignatureProperty paramSignatureProperty) {
    this.constructionElement.appendChild(paramSignatureProperty.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public String getBaseLocalName() { return "SignatureProperties"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\SignatureProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */