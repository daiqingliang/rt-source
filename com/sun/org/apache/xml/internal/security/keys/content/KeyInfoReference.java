package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyInfoReference extends Signature11ElementProxy implements KeyInfoContent {
  public KeyInfoReference(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public KeyInfoReference(Document paramDocument, String paramString) {
    super(paramDocument);
    this.constructionElement.setAttributeNS(null, "URI", paramString);
  }
  
  public Attr getURIAttr() { return this.constructionElement.getAttributeNodeNS(null, "URI"); }
  
  public String getURI() { return getURIAttr().getNodeValue(); }
  
  public void setId(String paramString) {
    if (paramString != null) {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    } else {
      this.constructionElement.removeAttributeNS(null, "Id");
    } 
  }
  
  public String getId() { return this.constructionElement.getAttributeNS(null, "Id"); }
  
  public String getBaseLocalName() { return "KeyInfoReference"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\KeyInfoReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */