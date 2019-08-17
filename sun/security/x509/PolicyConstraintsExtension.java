package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PolicyConstraintsExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.PolicyConstraints";
  
  public static final String NAME = "PolicyConstraints";
  
  public static final String REQUIRE = "require";
  
  public static final String INHIBIT = "inhibit";
  
  private static final byte TAG_REQUIRE = 0;
  
  private static final byte TAG_INHIBIT = 1;
  
  private int require = -1;
  
  private int inhibit = -1;
  
  private void encodeThis() throws IOException {
    if (this.require == -1 && this.inhibit == -1) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    if (this.require != -1) {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putInteger(this.require);
      derOutputStream1.writeImplicit(DerValue.createTag(-128, false, (byte)0), derOutputStream);
    } 
    if (this.inhibit != -1) {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putInteger(this.inhibit);
      derOutputStream1.writeImplicit(DerValue.createTag(-128, false, (byte)1), derOutputStream);
    } 
    derOutputStream2.write((byte)48, derOutputStream1);
    this.extensionValue = derOutputStream2.toByteArray();
  }
  
  public PolicyConstraintsExtension(int paramInt1, int paramInt2) throws IOException { this(Boolean.FALSE, paramInt1, paramInt2); }
  
  public PolicyConstraintsExtension(Boolean paramBoolean, int paramInt1, int paramInt2) throws IOException {
    this.require = paramInt1;
    this.inhibit = paramInt2;
    this.extensionId = PKIXExtensions.PolicyConstraints_Id;
    this.critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public PolicyConstraintsExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.PolicyConstraints_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 48)
      throw new IOException("Sequence tag missing for PolicyConstraint."); 
    DerInputStream derInputStream = derValue.data;
    while (derInputStream != null && derInputStream.available() != 0) {
      DerValue derValue1 = derInputStream.getDerValue();
      if (derValue1.isContextSpecific((byte)0) && !derValue1.isConstructed()) {
        if (this.require != -1)
          throw new IOException("Duplicate requireExplicitPolicyfound in the PolicyConstraintsExtension"); 
        derValue1.resetTag((byte)2);
        this.require = derValue1.getInteger();
        continue;
      } 
      if (derValue1.isContextSpecific((byte)1) && !derValue1.isConstructed()) {
        if (this.inhibit != -1)
          throw new IOException("Duplicate inhibitPolicyMappingfound in the PolicyConstraintsExtension"); 
        derValue1.resetTag((byte)2);
        this.inhibit = derValue1.getInteger();
        continue;
      } 
      throw new IOException("Invalid encoding of PolicyConstraint");
    } 
  }
  
  public String toString() {
    null = super.toString() + "PolicyConstraints: [  Require: ";
    if (this.require == -1) {
      null = null + "unspecified;";
    } else {
      null = null + this.require + ";";
    } 
    null = null + "\tInhibit: ";
    if (this.inhibit == -1) {
      null = null + "unspecified";
    } else {
      null = null + this.inhibit;
    } 
    return null + " ]\n";
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.PolicyConstraints_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof Integer))
      throw new IOException("Attribute value should be of type Integer."); 
    if (paramString.equalsIgnoreCase("require")) {
      this.require = ((Integer)paramObject).intValue();
    } else if (paramString.equalsIgnoreCase("inhibit")) {
      this.inhibit = ((Integer)paramObject).intValue();
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:PolicyConstraints.");
    } 
    encodeThis();
  }
  
  public Integer get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("require"))
      return new Integer(this.require); 
    if (paramString.equalsIgnoreCase("inhibit"))
      return new Integer(this.inhibit); 
    throw new IOException("Attribute name not recognized by CertAttrSet:PolicyConstraints.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("require")) {
      this.require = -1;
    } else if (paramString.equalsIgnoreCase("inhibit")) {
      this.inhibit = -1;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:PolicyConstraints.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("require");
    attributeNameEnumeration.addElement("inhibit");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "PolicyConstraints"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\PolicyConstraintsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */