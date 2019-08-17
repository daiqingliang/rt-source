package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyValue extends SignatureElementProxy implements KeyInfoContent {
  public KeyValue(Document paramDocument, DSAKeyValue paramDSAKeyValue) {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
    this.constructionElement.appendChild(paramDSAKeyValue.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public KeyValue(Document paramDocument, RSAKeyValue paramRSAKeyValue) {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
    this.constructionElement.appendChild(paramRSAKeyValue.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public KeyValue(Document paramDocument, Element paramElement) {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
    this.constructionElement.appendChild(paramElement);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public KeyValue(Document paramDocument, PublicKey paramPublicKey) {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
    if (paramPublicKey instanceof java.security.interfaces.DSAPublicKey) {
      DSAKeyValue dSAKeyValue = new DSAKeyValue(this.doc, paramPublicKey);
      this.constructionElement.appendChild(dSAKeyValue.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
    } else if (paramPublicKey instanceof java.security.interfaces.RSAPublicKey) {
      RSAKeyValue rSAKeyValue = new RSAKeyValue(this.doc, paramPublicKey);
      this.constructionElement.appendChild(rSAKeyValue.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
    } 
  }
  
  public KeyValue(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public PublicKey getPublicKey() throws XMLSecurityException {
    Element element1 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "RSAKeyValue", 0);
    if (element1 != null) {
      RSAKeyValue rSAKeyValue = new RSAKeyValue(element1, this.baseURI);
      return rSAKeyValue.getPublicKey();
    } 
    Element element2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "DSAKeyValue", 0);
    if (element2 != null) {
      DSAKeyValue dSAKeyValue = new DSAKeyValue(element2, this.baseURI);
      return dSAKeyValue.getPublicKey();
    } 
    return null;
  }
  
  public String getBaseLocalName() { return "KeyValue"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\KeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */