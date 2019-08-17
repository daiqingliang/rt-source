package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Signature11ElementProxy extends ElementProxy {
  protected Signature11ElementProxy() {}
  
  public Signature11ElementProxy(Document paramDocument) {
    if (paramDocument == null)
      throw new RuntimeException("Document is null"); 
    this.doc = paramDocument;
    this.constructionElement = XMLUtils.createElementInSignature11Space(this.doc, getBaseLocalName());
  }
  
  public Signature11ElementProxy(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public String getBaseNamespace() { return "http://www.w3.org/2009/xmldsig11#"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\Signature11ElementProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */