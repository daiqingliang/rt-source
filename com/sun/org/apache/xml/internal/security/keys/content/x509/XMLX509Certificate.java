package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509Certificate extends SignatureElementProxy implements XMLX509DataContent {
  public static final String JCA_CERT_ID = "X.509";
  
  public XMLX509Certificate(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public XMLX509Certificate(Document paramDocument, byte[] paramArrayOfByte) {
    super(paramDocument);
    addBase64Text(paramArrayOfByte);
  }
  
  public XMLX509Certificate(Document paramDocument, X509Certificate paramX509Certificate) throws XMLSecurityException {
    super(paramDocument);
    try {
      addBase64Text(paramX509Certificate.getEncoded());
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new XMLSecurityException("empty", certificateEncodingException);
    } 
  }
  
  public byte[] getCertificateBytes() throws XMLSecurityException { return getBytesFromTextChild(); }
  
  public X509Certificate getX509Certificate() throws XMLSecurityException {
    try {
      byte[] arrayOfByte = getCertificateBytes();
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      X509Certificate x509Certificate = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfByte));
      return (x509Certificate != null) ? x509Certificate : null;
    } catch (CertificateException certificateException) {
      throw new XMLSecurityException("empty", certificateException);
    } 
  }
  
  public PublicKey getPublicKey() throws XMLSecurityException {
    X509Certificate x509Certificate = getX509Certificate();
    return (x509Certificate != null) ? x509Certificate.getPublicKey() : null;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof XMLX509Certificate))
      return false; 
    XMLX509Certificate xMLX509Certificate = (XMLX509Certificate)paramObject;
    try {
      return Arrays.equals(xMLX509Certificate.getCertificateBytes(), getCertificateBytes());
    } catch (XMLSecurityException xMLSecurityException) {
      return false;
    } 
  }
  
  public int hashCode() {
    byte b = 17;
    try {
      byte[] arrayOfByte = getCertificateBytes();
      for (byte b1 = 0; b1 < arrayOfByte.length; b1++)
        b = 31 * b + arrayOfByte[b1]; 
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, xMLSecurityException.getMessage(), xMLSecurityException); 
    } 
    return b;
  }
  
  public String getBaseLocalName() { return "X509Certificate"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\x509\XMLX509Certificate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */