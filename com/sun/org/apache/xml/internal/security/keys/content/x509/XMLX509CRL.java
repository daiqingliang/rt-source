package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509CRL extends SignatureElementProxy implements XMLX509DataContent {
  public XMLX509CRL(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public XMLX509CRL(Document paramDocument, byte[] paramArrayOfByte) {
    super(paramDocument);
    addBase64Text(paramArrayOfByte);
  }
  
  public byte[] getCRLBytes() throws XMLSecurityException { return getBytesFromTextChild(); }
  
  public String getBaseLocalName() { return "X509CRL"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\x509\XMLX509CRL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */