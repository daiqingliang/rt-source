package org.jcp.xml.dsig.internal.dom;

import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMX509IssuerSerial extends DOMStructure implements X509IssuerSerial {
  private final String issuerName;
  
  private final BigInteger serialNumber;
  
  public DOMX509IssuerSerial(String paramString, BigInteger paramBigInteger) {
    if (paramString == null)
      throw new NullPointerException("issuerName cannot be null"); 
    if (paramBigInteger == null)
      throw new NullPointerException("serialNumber cannot be null"); 
    new X500Principal(paramString);
    this.issuerName = paramString;
    this.serialNumber = paramBigInteger;
  }
  
  public DOMX509IssuerSerial(Element paramElement) throws MarshalException {
    Element element1 = DOMUtils.getFirstChildElement(paramElement, "X509IssuerName");
    Element element2 = DOMUtils.getNextSiblingElement(element1, "X509SerialNumber");
    this.issuerName = element1.getFirstChild().getNodeValue();
    this.serialNumber = new BigInteger(element2.getFirstChild().getNodeValue());
  }
  
  public String getIssuerName() { return this.issuerName; }
  
  public BigInteger getSerialNumber() { return this.serialNumber; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element1 = DOMUtils.createElement(document, "X509IssuerSerial", "http://www.w3.org/2000/09/xmldsig#", paramString);
    Element element2 = DOMUtils.createElement(document, "X509IssuerName", "http://www.w3.org/2000/09/xmldsig#", paramString);
    Element element3 = DOMUtils.createElement(document, "X509SerialNumber", "http://www.w3.org/2000/09/xmldsig#", paramString);
    element2.appendChild(document.createTextNode(this.issuerName));
    element3.appendChild(document.createTextNode(this.serialNumber.toString()));
    element1.appendChild(element2);
    element1.appendChild(element3);
    paramNode.appendChild(element1);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof X509IssuerSerial))
      return false; 
    X509IssuerSerial x509IssuerSerial = (X509IssuerSerial)paramObject;
    return (this.issuerName.equals(x509IssuerSerial.getIssuerName()) && this.serialNumber.equals(x509IssuerSerial.getSerialNumber()));
  }
  
  public int hashCode() {
    null = 17;
    null = 31 * null + this.issuerName.hashCode();
    return 31 * null + this.serialNumber.hashCode();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMX509IssuerSerial.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */