package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class SubjectKeyIdentifierExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.SubjectKeyIdentifier";
  
  public static final String NAME = "SubjectKeyIdentifier";
  
  public static final String KEY_ID = "key_id";
  
  private KeyIdentifier id = null;
  
  private void encodeThis() throws IOException {
    if (this.id == null) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream = new DerOutputStream();
    this.id.encode(derOutputStream);
    this.extensionValue = derOutputStream.toByteArray();
  }
  
  public SubjectKeyIdentifierExtension(byte[] paramArrayOfByte) throws IOException {
    this.id = new KeyIdentifier(paramArrayOfByte);
    this.extensionId = PKIXExtensions.SubjectKey_Id;
    this.critical = false;
    encodeThis();
  }
  
  public SubjectKeyIdentifierExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.SubjectKey_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    this.id = new KeyIdentifier(derValue);
  }
  
  public String toString() { return super.toString() + "SubjectKeyIdentifier [\n" + String.valueOf(this.id) + "]\n"; }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.SubjectKey_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("key_id")) {
      if (!(paramObject instanceof KeyIdentifier))
        throw new IOException("Attribute value should be of type KeyIdentifier."); 
      this.id = (KeyIdentifier)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:SubjectKeyIdentifierExtension.");
    } 
    encodeThis();
  }
  
  public KeyIdentifier get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("key_id"))
      return this.id; 
    throw new IOException("Attribute name not recognized by CertAttrSet:SubjectKeyIdentifierExtension.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("key_id")) {
      this.id = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:SubjectKeyIdentifierExtension.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("key_id");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "SubjectKeyIdentifier"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\SubjectKeyIdentifierExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */