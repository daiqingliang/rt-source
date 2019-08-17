package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.security.auth.x500.X500Principal;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateIssuerName extends Object implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.issuer";
  
  public static final String NAME = "issuer";
  
  public static final String DN_NAME = "dname";
  
  public static final String DN_PRINCIPAL = "x500principal";
  
  private X500Name dnName;
  
  private X500Principal dnPrincipal;
  
  public CertificateIssuerName(X500Name paramX500Name) { this.dnName = paramX500Name; }
  
  public CertificateIssuerName(DerInputStream paramDerInputStream) throws IOException { this.dnName = new X500Name(paramDerInputStream); }
  
  public CertificateIssuerName(InputStream paramInputStream) throws IOException {
    DerValue derValue = new DerValue(paramInputStream);
    this.dnName = new X500Name(derValue);
  }
  
  public String toString() { return (this.dnName == null) ? "" : this.dnName.toString(); }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    this.dnName.encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof X500Name))
      throw new IOException("Attribute must be of type X500Name."); 
    if (paramString.equalsIgnoreCase("dname")) {
      this.dnName = (X500Name)paramObject;
      this.dnPrincipal = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuerName.");
    } 
  }
  
  public Object get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("dname"))
      return this.dnName; 
    if (paramString.equalsIgnoreCase("x500principal")) {
      if (this.dnPrincipal == null && this.dnName != null)
        this.dnPrincipal = this.dnName.asX500Principal(); 
      return this.dnPrincipal;
    } 
    throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuerName.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("dname")) {
      this.dnName = null;
      this.dnPrincipal = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuerName.");
    } 
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("dname");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "issuer"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificateIssuerName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */