package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateAlgorithmId extends Object implements CertAttrSet<String> {
  private AlgorithmId algId;
  
  public static final String IDENT = "x509.info.algorithmID";
  
  public static final String NAME = "algorithmID";
  
  public static final String ALGORITHM = "algorithm";
  
  public CertificateAlgorithmId(AlgorithmId paramAlgorithmId) { this.algId = paramAlgorithmId; }
  
  public CertificateAlgorithmId(DerInputStream paramDerInputStream) throws IOException {
    DerValue derValue = paramDerInputStream.getDerValue();
    this.algId = AlgorithmId.parse(derValue);
  }
  
  public CertificateAlgorithmId(InputStream paramInputStream) throws IOException {
    DerValue derValue = new DerValue(paramInputStream);
    this.algId = AlgorithmId.parse(derValue);
  }
  
  public String toString() { return (this.algId == null) ? "" : (this.algId.toString() + ", OID = " + this.algId.getOID().toString() + "\n"); }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    this.algId.encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof AlgorithmId))
      throw new IOException("Attribute must be of type AlgorithmId."); 
    if (paramString.equalsIgnoreCase("algorithm")) {
      this.algId = (AlgorithmId)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateAlgorithmId.");
    } 
  }
  
  public AlgorithmId get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("algorithm"))
      return this.algId; 
    throw new IOException("Attribute name not recognized by CertAttrSet:CertificateAlgorithmId.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("algorithm")) {
      this.algId = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateAlgorithmId.");
    } 
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("algorithm");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "algorithmID"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificateAlgorithmId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */