package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509SKI extends SignatureElementProxy implements XMLX509DataContent {
  private static Logger log = Logger.getLogger(XMLX509SKI.class.getName());
  
  public static final String SKI_OID = "2.5.29.14";
  
  public XMLX509SKI(Document paramDocument, byte[] paramArrayOfByte) {
    super(paramDocument);
    addBase64Text(paramArrayOfByte);
  }
  
  public XMLX509SKI(Document paramDocument, X509Certificate paramX509Certificate) throws XMLSecurityException {
    super(paramDocument);
    addBase64Text(getSKIBytesFromCert(paramX509Certificate));
  }
  
  public XMLX509SKI(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public byte[] getSKIBytes() throws XMLSecurityException { return getBytesFromTextChild(); }
  
  public static byte[] getSKIBytesFromCert(X509Certificate paramX509Certificate) throws XMLSecurityException {
    if (paramX509Certificate.getVersion() < 3) {
      Object[] arrayOfObject = { Integer.valueOf(paramX509Certificate.getVersion()) };
      throw new XMLSecurityException("certificate.noSki.lowVersion", arrayOfObject);
    } 
    byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.14");
    if (arrayOfByte1 == null)
      throw new XMLSecurityException("certificate.noSki.null"); 
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length - 4];
    System.arraycopy(arrayOfByte1, 4, arrayOfByte2, 0, arrayOfByte2.length);
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Base64 of SKI is " + Base64.encode(arrayOfByte2)); 
    return arrayOfByte2;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof XMLX509SKI))
      return false; 
    XMLX509SKI xMLX509SKI = (XMLX509SKI)paramObject;
    try {
      return Arrays.equals(xMLX509SKI.getSKIBytes(), getSKIBytes());
    } catch (XMLSecurityException xMLSecurityException) {
      return false;
    } 
  }
  
  public int hashCode() {
    byte b = 17;
    try {
      byte[] arrayOfByte = getSKIBytes();
      for (byte b1 = 0; b1 < arrayOfByte.length; b1++)
        b = 31 * b + arrayOfByte[b1]; 
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, xMLSecurityException.getMessage(), xMLSecurityException); 
    } 
    return b;
  }
  
  public String getBaseLocalName() { return "X509SKI"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\x509\XMLX509SKI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */