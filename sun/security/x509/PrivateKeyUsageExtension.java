package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PrivateKeyUsageExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.PrivateKeyUsage";
  
  public static final String NAME = "PrivateKeyUsage";
  
  public static final String NOT_BEFORE = "not_before";
  
  public static final String NOT_AFTER = "not_after";
  
  private static final byte TAG_BEFORE = 0;
  
  private static final byte TAG_AFTER = 1;
  
  private Date notBefore = null;
  
  private Date notAfter = null;
  
  private void encodeThis() throws IOException {
    if (this.notBefore == null && this.notAfter == null) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    if (this.notBefore != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putGeneralizedTime(this.notBefore);
      derOutputStream2.writeImplicit(DerValue.createTag(-128, false, (byte)0), derOutputStream);
    } 
    if (this.notAfter != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putGeneralizedTime(this.notAfter);
      derOutputStream2.writeImplicit(DerValue.createTag(-128, false, (byte)1), derOutputStream);
    } 
    derOutputStream1.write((byte)48, derOutputStream2);
    this.extensionValue = derOutputStream1.toByteArray();
  }
  
  public PrivateKeyUsageExtension(Date paramDate1, Date paramDate2) throws IOException {
    this.notBefore = paramDate1;
    this.notAfter = paramDate2;
    this.extensionId = PKIXExtensions.PrivateKeyUsage_Id;
    this.critical = false;
    encodeThis();
  }
  
  public PrivateKeyUsageExtension(Boolean paramBoolean, Object paramObject) throws CertificateException, IOException {
    this.extensionId = PKIXExtensions.PrivateKeyUsage_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerInputStream derInputStream = new DerInputStream(this.extensionValue);
    DerValue[] arrayOfDerValue = derInputStream.getSequence(2);
    for (byte b = 0; b < arrayOfDerValue.length; b++) {
      DerValue derValue = arrayOfDerValue[b];
      if (derValue.isContextSpecific((byte)0) && !derValue.isConstructed()) {
        if (this.notBefore != null)
          throw new CertificateParsingException("Duplicate notBefore in PrivateKeyUsage."); 
        derValue.resetTag((byte)24);
        derInputStream = new DerInputStream(derValue.toByteArray());
        this.notBefore = derInputStream.getGeneralizedTime();
      } else if (derValue.isContextSpecific((byte)1) && !derValue.isConstructed()) {
        if (this.notAfter != null)
          throw new CertificateParsingException("Duplicate notAfter in PrivateKeyUsage."); 
        derValue.resetTag((byte)24);
        derInputStream = new DerInputStream(derValue.toByteArray());
        this.notAfter = derInputStream.getGeneralizedTime();
      } else {
        throw new IOException("Invalid encoding of PrivateKeyUsageExtension");
      } 
    } 
  }
  
  public String toString() { return super.toString() + "PrivateKeyUsage: [\n" + ((this.notBefore == null) ? "" : ("From: " + this.notBefore.toString() + ", ")) + ((this.notAfter == null) ? "" : ("To: " + this.notAfter.toString())) + "]\n"; }
  
  public void valid() throws IOException {
    Date date = new Date();
    valid(date);
  }
  
  public void valid(Date paramDate) throws CertificateNotYetValidException, CertificateExpiredException {
    Objects.requireNonNull(paramDate);
    if (this.notBefore != null && this.notBefore.after(paramDate))
      throw new CertificateNotYetValidException("NotBefore: " + this.notBefore.toString()); 
    if (this.notAfter != null && this.notAfter.before(paramDate))
      throw new CertificateExpiredException("NotAfter: " + this.notAfter.toString()); 
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.PrivateKeyUsage_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws CertificateException, IOException {
    if (!(paramObject instanceof Date))
      throw new CertificateException("Attribute must be of type Date."); 
    if (paramString.equalsIgnoreCase("not_before")) {
      this.notBefore = (Date)paramObject;
    } else if (paramString.equalsIgnoreCase("not_after")) {
      this.notAfter = (Date)paramObject;
    } else {
      throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
    } 
    encodeThis();
  }
  
  public Date get(String paramString) throws CertificateException {
    if (paramString.equalsIgnoreCase("not_before"))
      return new Date(this.notBefore.getTime()); 
    if (paramString.equalsIgnoreCase("not_after"))
      return new Date(this.notAfter.getTime()); 
    throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
  }
  
  public void delete(String paramString) throws CertificateException, IOException {
    if (paramString.equalsIgnoreCase("not_before")) {
      this.notBefore = null;
    } else if (paramString.equalsIgnoreCase("not_after")) {
      this.notAfter = null;
    } else {
      throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("not_before");
    attributeNameEnumeration.addElement("not_after");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "PrivateKeyUsage"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\PrivateKeyUsageExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */