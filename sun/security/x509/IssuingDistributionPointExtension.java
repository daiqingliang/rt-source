package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class IssuingDistributionPointExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.IssuingDistributionPoint";
  
  public static final String NAME = "IssuingDistributionPoint";
  
  public static final String POINT = "point";
  
  public static final String REASONS = "reasons";
  
  public static final String ONLY_USER_CERTS = "only_user_certs";
  
  public static final String ONLY_CA_CERTS = "only_ca_certs";
  
  public static final String ONLY_ATTRIBUTE_CERTS = "only_attribute_certs";
  
  public static final String INDIRECT_CRL = "indirect_crl";
  
  private DistributionPointName distributionPoint = null;
  
  private ReasonFlags revocationReasons = null;
  
  private boolean hasOnlyUserCerts = false;
  
  private boolean hasOnlyCACerts = false;
  
  private boolean hasOnlyAttributeCerts = false;
  
  private boolean isIndirectCRL = false;
  
  private static final byte TAG_DISTRIBUTION_POINT = 0;
  
  private static final byte TAG_ONLY_USER_CERTS = 1;
  
  private static final byte TAG_ONLY_CA_CERTS = 2;
  
  private static final byte TAG_ONLY_SOME_REASONS = 3;
  
  private static final byte TAG_INDIRECT_CRL = 4;
  
  private static final byte TAG_ONLY_ATTRIBUTE_CERTS = 5;
  
  public IssuingDistributionPointExtension(DistributionPointName paramDistributionPointName, ReasonFlags paramReasonFlags, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) throws IOException {
    if ((paramBoolean1 && (paramBoolean2 || paramBoolean3)) || (paramBoolean2 && (paramBoolean1 || paramBoolean3)) || (paramBoolean3 && (paramBoolean1 || paramBoolean2)))
      throw new IllegalArgumentException("Only one of hasOnlyUserCerts, hasOnlyCACerts, hasOnlyAttributeCerts may be set to true"); 
    this.extensionId = PKIXExtensions.IssuingDistributionPoint_Id;
    this.critical = true;
    this.distributionPoint = paramDistributionPointName;
    this.revocationReasons = paramReasonFlags;
    this.hasOnlyUserCerts = paramBoolean1;
    this.hasOnlyCACerts = paramBoolean2;
    this.hasOnlyAttributeCerts = paramBoolean3;
    this.isIndirectCRL = paramBoolean4;
    encodeThis();
  }
  
  public IssuingDistributionPointExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.IssuingDistributionPoint_Id;
    this.critical = paramBoolean.booleanValue();
    if (!(paramObject instanceof byte[]))
      throw new IOException("Illegal argument type"); 
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 48)
      throw new IOException("Invalid encoding for IssuingDistributionPointExtension."); 
    if (derValue.data == null || derValue.data.available() == 0)
      return; 
    DerInputStream derInputStream = derValue.data;
    while (derInputStream != null && derInputStream.available() != 0) {
      DerValue derValue1 = derInputStream.getDerValue();
      if (derValue1.isContextSpecific((byte)0) && derValue1.isConstructed()) {
        this.distributionPoint = new DistributionPointName(derValue1.data.getDerValue());
        continue;
      } 
      if (derValue1.isContextSpecific((byte)1) && !derValue1.isConstructed()) {
        derValue1.resetTag((byte)1);
        this.hasOnlyUserCerts = derValue1.getBoolean();
        continue;
      } 
      if (derValue1.isContextSpecific((byte)2) && !derValue1.isConstructed()) {
        derValue1.resetTag((byte)1);
        this.hasOnlyCACerts = derValue1.getBoolean();
        continue;
      } 
      if (derValue1.isContextSpecific((byte)3) && !derValue1.isConstructed()) {
        this.revocationReasons = new ReasonFlags(derValue1);
        continue;
      } 
      if (derValue1.isContextSpecific((byte)4) && !derValue1.isConstructed()) {
        derValue1.resetTag((byte)1);
        this.isIndirectCRL = derValue1.getBoolean();
        continue;
      } 
      if (derValue1.isContextSpecific((byte)5) && !derValue1.isConstructed()) {
        derValue1.resetTag((byte)1);
        this.hasOnlyAttributeCerts = derValue1.getBoolean();
        continue;
      } 
      throw new IOException("Invalid encoding of IssuingDistributionPoint");
    } 
  }
  
  public String getName() { return "IssuingDistributionPoint"; }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.IssuingDistributionPoint_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("point")) {
      if (!(paramObject instanceof DistributionPointName))
        throw new IOException("Attribute value should be of type DistributionPointName."); 
      this.distributionPoint = (DistributionPointName)paramObject;
    } else if (paramString.equalsIgnoreCase("reasons")) {
      if (!(paramObject instanceof ReasonFlags))
        throw new IOException("Attribute value should be of type ReasonFlags."); 
      this.revocationReasons = (ReasonFlags)paramObject;
    } else if (paramString.equalsIgnoreCase("indirect_crl")) {
      if (!(paramObject instanceof Boolean))
        throw new IOException("Attribute value should be of type Boolean."); 
      this.isIndirectCRL = ((Boolean)paramObject).booleanValue();
    } else if (paramString.equalsIgnoreCase("only_user_certs")) {
      if (!(paramObject instanceof Boolean))
        throw new IOException("Attribute value should be of type Boolean."); 
      this.hasOnlyUserCerts = ((Boolean)paramObject).booleanValue();
    } else if (paramString.equalsIgnoreCase("only_ca_certs")) {
      if (!(paramObject instanceof Boolean))
        throw new IOException("Attribute value should be of type Boolean."); 
      this.hasOnlyCACerts = ((Boolean)paramObject).booleanValue();
    } else if (paramString.equalsIgnoreCase("only_attribute_certs")) {
      if (!(paramObject instanceof Boolean))
        throw new IOException("Attribute value should be of type Boolean."); 
      this.hasOnlyAttributeCerts = ((Boolean)paramObject).booleanValue();
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:IssuingDistributionPointExtension.");
    } 
    encodeThis();
  }
  
  public Object get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("point"))
      return this.distributionPoint; 
    if (paramString.equalsIgnoreCase("indirect_crl"))
      return Boolean.valueOf(this.isIndirectCRL); 
    if (paramString.equalsIgnoreCase("reasons"))
      return this.revocationReasons; 
    if (paramString.equalsIgnoreCase("only_user_certs"))
      return Boolean.valueOf(this.hasOnlyUserCerts); 
    if (paramString.equalsIgnoreCase("only_ca_certs"))
      return Boolean.valueOf(this.hasOnlyCACerts); 
    if (paramString.equalsIgnoreCase("only_attribute_certs"))
      return Boolean.valueOf(this.hasOnlyAttributeCerts); 
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:IssuingDistributionPointExtension.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("point")) {
      this.distributionPoint = null;
    } else if (paramString.equalsIgnoreCase("indirect_crl")) {
      this.isIndirectCRL = false;
    } else if (paramString.equalsIgnoreCase("reasons")) {
      this.revocationReasons = null;
    } else if (paramString.equalsIgnoreCase("only_user_certs")) {
      this.hasOnlyUserCerts = false;
    } else if (paramString.equalsIgnoreCase("only_ca_certs")) {
      this.hasOnlyCACerts = false;
    } else if (paramString.equalsIgnoreCase("only_attribute_certs")) {
      this.hasOnlyAttributeCerts = false;
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:IssuingDistributionPointExtension.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("point");
    attributeNameEnumeration.addElement("reasons");
    attributeNameEnumeration.addElement("only_user_certs");
    attributeNameEnumeration.addElement("only_ca_certs");
    attributeNameEnumeration.addElement("only_attribute_certs");
    attributeNameEnumeration.addElement("indirect_crl");
    return attributeNameEnumeration.elements();
  }
  
  private void encodeThis() throws IOException {
    if (this.distributionPoint == null && this.revocationReasons == null && !this.hasOnlyUserCerts && !this.hasOnlyCACerts && !this.hasOnlyAttributeCerts && !this.isIndirectCRL) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    if (this.distributionPoint != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      this.distributionPoint.encode(derOutputStream);
      derOutputStream1.writeImplicit(DerValue.createTag(-128, true, (byte)0), derOutputStream);
    } 
    if (this.hasOnlyUserCerts) {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putBoolean(this.hasOnlyUserCerts);
      derOutputStream1.writeImplicit(DerValue.createTag(-128, false, (byte)1), derOutputStream);
    } 
    if (this.hasOnlyCACerts) {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putBoolean(this.hasOnlyCACerts);
      derOutputStream1.writeImplicit(DerValue.createTag(-128, false, (byte)2), derOutputStream);
    } 
    if (this.revocationReasons != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      this.revocationReasons.encode(derOutputStream);
      derOutputStream1.writeImplicit(DerValue.createTag(-128, false, (byte)3), derOutputStream);
    } 
    if (this.isIndirectCRL) {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putBoolean(this.isIndirectCRL);
      derOutputStream1.writeImplicit(DerValue.createTag(-128, false, (byte)4), derOutputStream);
    } 
    if (this.hasOnlyAttributeCerts) {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putBoolean(this.hasOnlyAttributeCerts);
      derOutputStream1.writeImplicit(DerValue.createTag(-128, false, (byte)5), derOutputStream);
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    this.extensionValue = derOutputStream2.toByteArray();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(super.toString());
    stringBuilder.append("IssuingDistributionPoint [\n  ");
    if (this.distributionPoint != null)
      stringBuilder.append(this.distributionPoint); 
    if (this.revocationReasons != null)
      stringBuilder.append(this.revocationReasons); 
    stringBuilder.append(this.hasOnlyUserCerts ? "  Only contains user certs: true" : "  Only contains user certs: false").append("\n");
    stringBuilder.append(this.hasOnlyCACerts ? "  Only contains CA certs: true" : "  Only contains CA certs: false").append("\n");
    stringBuilder.append(this.hasOnlyAttributeCerts ? "  Only contains attribute certs: true" : "  Only contains attribute certs: false").append("\n");
    stringBuilder.append(this.isIndirectCRL ? "  Indirect CRL: true" : "  Indirect CRL: false").append("\n");
    stringBuilder.append("]\n");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\IssuingDistributionPointExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */