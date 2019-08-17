package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class InhibitAnyPolicyExtension extends Extension implements CertAttrSet<String> {
  private static final Debug debug = Debug.getInstance("certpath");
  
  public static final String IDENT = "x509.info.extensions.InhibitAnyPolicy";
  
  public static ObjectIdentifier AnyPolicy_Id;
  
  public static final String NAME = "InhibitAnyPolicy";
  
  public static final String SKIP_CERTS = "skip_certs";
  
  private int skipCerts = Integer.MAX_VALUE;
  
  private void encodeThis() throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putInteger(this.skipCerts);
    this.extensionValue = derOutputStream.toByteArray();
  }
  
  public InhibitAnyPolicyExtension(int paramInt) throws IOException {
    if (paramInt < -1)
      throw new IOException("Invalid value for skipCerts"); 
    if (paramInt == -1) {
      this.skipCerts = Integer.MAX_VALUE;
    } else {
      this.skipCerts = paramInt;
    } 
    this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
    this.critical = true;
    encodeThis();
  }
  
  public InhibitAnyPolicyExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
    if (!paramBoolean.booleanValue())
      throw new IOException("Criticality cannot be false for InhibitAnyPolicy"); 
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 2)
      throw new IOException("Invalid encoding of InhibitAnyPolicy: data not integer"); 
    if (derValue.data == null)
      throw new IOException("Invalid encoding of InhibitAnyPolicy: null data"); 
    int i = derValue.getInteger();
    if (i < -1)
      throw new IOException("Invalid value for skipCerts"); 
    if (i == -1) {
      this.skipCerts = Integer.MAX_VALUE;
    } else {
      this.skipCerts = i;
    } 
  }
  
  public String toString() { return super.toString() + "InhibitAnyPolicy: " + this.skipCerts + "\n"; }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
      this.critical = true;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("skip_certs")) {
      if (!(paramObject instanceof Integer))
        throw new IOException("Attribute value should be of type Integer."); 
      int i = ((Integer)paramObject).intValue();
      if (i < -1)
        throw new IOException("Invalid value for skipCerts"); 
      if (i == -1) {
        this.skipCerts = Integer.MAX_VALUE;
      } else {
        this.skipCerts = i;
      } 
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
    } 
    encodeThis();
  }
  
  public Integer get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("skip_certs"))
      return new Integer(this.skipCerts); 
    throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("skip_certs"))
      throw new IOException("Attribute skip_certs may not be deleted."); 
    throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("skip_certs");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "InhibitAnyPolicy"; }
  
  static  {
    try {
      AnyPolicy_Id = new ObjectIdentifier("2.5.29.32.0");
    } catch (IOException iOException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\InhibitAnyPolicyExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */