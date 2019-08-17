package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ObjectContainer extends SignatureElementProxy {
  public ObjectContainer(Document paramDocument) { super(paramDocument); }
  
  public ObjectContainer(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public void setId(String paramString) {
    if (paramString != null) {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    } 
  }
  
  public String getId() { return this.constructionElement.getAttributeNS(null, "Id"); }
  
  public void setMimeType(String paramString) {
    if (paramString != null)
      this.constructionElement.setAttributeNS(null, "MimeType", paramString); 
  }
  
  public String getMimeType() { return this.constructionElement.getAttributeNS(null, "MimeType"); }
  
  public void setEncoding(String paramString) {
    if (paramString != null)
      this.constructionElement.setAttributeNS(null, "Encoding", paramString); 
  }
  
  public String getEncoding() { return this.constructionElement.getAttributeNS(null, "Encoding"); }
  
  public Node appendChild(Node paramNode) { return this.constructionElement.appendChild(paramNode); }
  
  public String getBaseLocalName() { return "Object"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\ObjectContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */