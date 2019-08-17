package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Algorithm extends SignatureElementProxy {
  public Algorithm(Document paramDocument, String paramString) {
    super(paramDocument);
    setAlgorithmURI(paramString);
  }
  
  public Algorithm(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public String getAlgorithmURI() { return this.constructionElement.getAttributeNS(null, "Algorithm"); }
  
  protected void setAlgorithmURI(String paramString) {
    if (paramString != null)
      this.constructionElement.setAttributeNS(null, "Algorithm", paramString); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\Algorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */