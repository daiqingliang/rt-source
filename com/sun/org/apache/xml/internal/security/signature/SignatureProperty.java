package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SignatureProperty extends SignatureElementProxy {
  public SignatureProperty(Document paramDocument, String paramString) { this(paramDocument, paramString, null); }
  
  public SignatureProperty(Document paramDocument, String paramString1, String paramString2) {
    super(paramDocument);
    setTarget(paramString1);
    setId(paramString2);
  }
  
  public SignatureProperty(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public void setId(String paramString) {
    if (paramString != null) {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    } 
  }
  
  public String getId() { return this.constructionElement.getAttributeNS(null, "Id"); }
  
  public void setTarget(String paramString) {
    if (paramString != null)
      this.constructionElement.setAttributeNS(null, "Target", paramString); 
  }
  
  public String getTarget() { return this.constructionElement.getAttributeNS(null, "Target"); }
  
  public Node appendChild(Node paramNode) { return this.constructionElement.appendChild(paramNode); }
  
  public String getBaseLocalName() { return "SignatureProperty"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\SignatureProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */