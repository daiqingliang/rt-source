package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateX509Key extends Object implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.key";
  
  public static final String NAME = "key";
  
  public static final String KEY = "value";
  
  private PublicKey key;
  
  public CertificateX509Key(PublicKey paramPublicKey) { this.key = paramPublicKey; }
  
  public CertificateX509Key(DerInputStream paramDerInputStream) throws IOException {
    DerValue derValue = paramDerInputStream.getDerValue();
    this.key = X509Key.parse(derValue);
  }
  
  public CertificateX509Key(InputStream paramInputStream) throws IOException {
    DerValue derValue = new DerValue(paramInputStream);
    this.key = X509Key.parse(derValue);
  }
  
  public String toString() { return (this.key == null) ? "" : this.key.toString(); }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.write(this.key.getEncoded());
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("value")) {
      this.key = (PublicKey)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateX509Key.");
    } 
  }
  
  public PublicKey get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("value"))
      return this.key; 
    throw new IOException("Attribute name not recognized by CertAttrSet: CertificateX509Key.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("value")) {
      this.key = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateX509Key.");
    } 
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("value");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "key"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificateX509Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */