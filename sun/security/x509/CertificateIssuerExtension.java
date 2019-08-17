package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateIssuerExtension extends Extension implements CertAttrSet<String> {
  public static final String NAME = "CertificateIssuer";
  
  public static final String ISSUER = "issuer";
  
  private GeneralNames names;
  
  private void encodeThis() throws IOException {
    if (this.names == null || this.names.isEmpty()) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream = new DerOutputStream();
    this.names.encode(derOutputStream);
    this.extensionValue = derOutputStream.toByteArray();
  }
  
  public CertificateIssuerExtension(GeneralNames paramGeneralNames) throws IOException {
    this.names = paramGeneralNames;
    encodeThis();
  }
  
  public CertificateIssuerExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    this.names = new GeneralNames(derValue);
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("issuer")) {
      if (!(paramObject instanceof GeneralNames))
        throw new IOException("Attribute value must be of type GeneralNames"); 
      this.names = (GeneralNames)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuer");
    } 
    encodeThis();
  }
  
  public GeneralNames get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("issuer"))
      return this.names; 
    throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuer");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("issuer")) {
      this.names = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuer");
    } 
    encodeThis();
  }
  
  public String toString() { return super.toString() + "Certificate Issuer [\n" + String.valueOf(this.names) + "]\n"; }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.CertificateIssuer_Id;
      this.critical = true;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("issuer");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "CertificateIssuer"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificateIssuerExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */