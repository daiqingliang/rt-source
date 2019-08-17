package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.security.auth.x500.X500Principal;
import sun.net.util.IPAddressUtil;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NameConstraintsExtension extends Extension implements CertAttrSet<String>, Cloneable {
  public static final String IDENT = "x509.info.extensions.NameConstraints";
  
  public static final String NAME = "NameConstraints";
  
  public static final String PERMITTED_SUBTREES = "permitted_subtrees";
  
  public static final String EXCLUDED_SUBTREES = "excluded_subtrees";
  
  private static final byte TAG_PERMITTED = 0;
  
  private static final byte TAG_EXCLUDED = 1;
  
  private GeneralSubtrees permitted = null;
  
  private GeneralSubtrees excluded = null;
  
  private boolean hasMin;
  
  private boolean hasMax;
  
  private boolean minMaxValid = false;
  
  private void calcMinMax() throws IOException {
    this.hasMin = false;
    this.hasMax = false;
    if (this.excluded != null)
      for (byte b = 0; b < this.excluded.size(); b++) {
        GeneralSubtree generalSubtree = this.excluded.get(b);
        if (generalSubtree.getMinimum() != 0)
          this.hasMin = true; 
        if (generalSubtree.getMaximum() != -1)
          this.hasMax = true; 
      }  
    if (this.permitted != null)
      for (byte b = 0; b < this.permitted.size(); b++) {
        GeneralSubtree generalSubtree = this.permitted.get(b);
        if (generalSubtree.getMinimum() != 0)
          this.hasMin = true; 
        if (generalSubtree.getMaximum() != -1)
          this.hasMax = true; 
      }  
    this.minMaxValid = true;
  }
  
  private void encodeThis() throws IOException {
    this.minMaxValid = false;
    if (this.permitted == null && this.excluded == null) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    if (this.permitted != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      this.permitted.encode(derOutputStream);
      derOutputStream2.writeImplicit(DerValue.createTag(-128, true, (byte)0), derOutputStream);
    } 
    if (this.excluded != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      this.excluded.encode(derOutputStream);
      derOutputStream2.writeImplicit(DerValue.createTag(-128, true, (byte)1), derOutputStream);
    } 
    derOutputStream1.write((byte)48, derOutputStream2);
    this.extensionValue = derOutputStream1.toByteArray();
  }
  
  public NameConstraintsExtension(GeneralSubtrees paramGeneralSubtrees1, GeneralSubtrees paramGeneralSubtrees2) throws IOException {
    this.permitted = paramGeneralSubtrees1;
    this.excluded = paramGeneralSubtrees2;
    this.extensionId = PKIXExtensions.NameConstraints_Id;
    this.critical = true;
    encodeThis();
  }
  
  public NameConstraintsExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.NameConstraints_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 48)
      throw new IOException("Invalid encoding for NameConstraintsExtension."); 
    if (derValue.data == null)
      return; 
    while (derValue.data.available() != 0) {
      DerValue derValue1 = derValue.data.getDerValue();
      if (derValue1.isContextSpecific((byte)0) && derValue1.isConstructed()) {
        if (this.permitted != null)
          throw new IOException("Duplicate permitted GeneralSubtrees in NameConstraintsExtension."); 
        derValue1.resetTag((byte)48);
        this.permitted = new GeneralSubtrees(derValue1);
        continue;
      } 
      if (derValue1.isContextSpecific((byte)1) && derValue1.isConstructed()) {
        if (this.excluded != null)
          throw new IOException("Duplicate excluded GeneralSubtrees in NameConstraintsExtension."); 
        derValue1.resetTag((byte)48);
        this.excluded = new GeneralSubtrees(derValue1);
        continue;
      } 
      throw new IOException("Invalid encoding of NameConstraintsExtension.");
    } 
    this.minMaxValid = false;
  }
  
  public String toString() { return super.toString() + "NameConstraints: [" + ((this.permitted == null) ? "" : ("\n    Permitted:" + this.permitted.toString())) + ((this.excluded == null) ? "" : ("\n    Excluded:" + this.excluded.toString())) + "   ]\n"; }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.NameConstraints_Id;
      this.critical = true;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("permitted_subtrees")) {
      if (!(paramObject instanceof GeneralSubtrees))
        throw new IOException("Attribute value should be of type GeneralSubtrees."); 
      this.permitted = (GeneralSubtrees)paramObject;
    } else if (paramString.equalsIgnoreCase("excluded_subtrees")) {
      if (!(paramObject instanceof GeneralSubtrees))
        throw new IOException("Attribute value should be of type GeneralSubtrees."); 
      this.excluded = (GeneralSubtrees)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
    } 
    encodeThis();
  }
  
  public GeneralSubtrees get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("permitted_subtrees"))
      return this.permitted; 
    if (paramString.equalsIgnoreCase("excluded_subtrees"))
      return this.excluded; 
    throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("permitted_subtrees")) {
      this.permitted = null;
    } else if (paramString.equalsIgnoreCase("excluded_subtrees")) {
      this.excluded = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("permitted_subtrees");
    attributeNameEnumeration.addElement("excluded_subtrees");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "NameConstraints"; }
  
  public void merge(NameConstraintsExtension paramNameConstraintsExtension) throws IOException {
    if (paramNameConstraintsExtension == null)
      return; 
    GeneralSubtrees generalSubtrees1 = paramNameConstraintsExtension.get("excluded_subtrees");
    if (this.excluded == null) {
      this.excluded = (generalSubtrees1 != null) ? (GeneralSubtrees)generalSubtrees1.clone() : null;
    } else if (generalSubtrees1 != null) {
      this.excluded.union(generalSubtrees1);
    } 
    GeneralSubtrees generalSubtrees2 = paramNameConstraintsExtension.get("permitted_subtrees");
    if (this.permitted == null) {
      this.permitted = (generalSubtrees2 != null) ? (GeneralSubtrees)generalSubtrees2.clone() : null;
    } else if (generalSubtrees2 != null) {
      generalSubtrees1 = this.permitted.intersect(generalSubtrees2);
      if (generalSubtrees1 != null)
        if (this.excluded != null) {
          this.excluded.union(generalSubtrees1);
        } else {
          this.excluded = (GeneralSubtrees)generalSubtrees1.clone();
        }  
    } 
    if (this.permitted != null)
      this.permitted.reduce(this.excluded); 
    encodeThis();
  }
  
  public boolean verify(X509Certificate paramX509Certificate) throws IOException {
    if (paramX509Certificate == null)
      throw new IOException("Certificate is null"); 
    if (!this.minMaxValid)
      calcMinMax(); 
    if (this.hasMin)
      throw new IOException("Non-zero minimum BaseDistance in name constraints not supported"); 
    if (this.hasMax)
      throw new IOException("Maximum BaseDistance in name constraints not supported"); 
    X500Principal x500Principal = paramX509Certificate.getSubjectX500Principal();
    X500Name x500Name = X500Name.asX500Name(x500Principal);
    if (!x500Name.isEmpty() && !verify(x500Name))
      return false; 
    GeneralNames generalNames = null;
    try {
      X509CertImpl x509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
      SubjectAlternativeNameExtension subjectAlternativeNameExtension = x509CertImpl.getSubjectAlternativeNameExtension();
      if (subjectAlternativeNameExtension != null)
        generalNames = subjectAlternativeNameExtension.get("subject_name"); 
    } catch (CertificateException certificateException) {
      throw new IOException("Unable to extract extensions from certificate: " + certificateException.getMessage());
    } 
    if (generalNames == null) {
      generalNames = new GeneralNames();
      for (AVA aVA : x500Name.allAvas()) {
        ObjectIdentifier objectIdentifier = aVA.getObjectIdentifier();
        if (objectIdentifier.equals(PKCS9Attribute.EMAIL_ADDRESS_OID)) {
          String str1 = aVA.getValueString();
          if (str1 != null)
            try {
              generalNames.add(new GeneralName(new RFC822Name(str1)));
            } catch (IOException iOException) {} 
        } 
      } 
    } 
    DerValue derValue = x500Name.findMostSpecificAttribute(X500Name.commonName_oid);
    String str = (derValue == null) ? null : derValue.getAsString();
    if (str != null)
      try {
        if (IPAddressUtil.isIPv4LiteralAddress(str) || IPAddressUtil.isIPv6LiteralAddress(str)) {
          if (!hasNameType(generalNames, 7))
            generalNames.add(new GeneralName(new IPAddressName(str))); 
        } else if (!hasNameType(generalNames, 2)) {
          generalNames.add(new GeneralName(new DNSName(str)));
        } 
      } catch (IOException iOException) {} 
    for (byte b = 0; b < generalNames.size(); b++) {
      GeneralNameInterface generalNameInterface = generalNames.get(b).getName();
      if (!verify(generalNameInterface))
        return false; 
    } 
    return true;
  }
  
  private static boolean hasNameType(GeneralNames paramGeneralNames, int paramInt) {
    for (GeneralName generalName : paramGeneralNames.names()) {
      if (generalName.getType() == paramInt)
        return true; 
    } 
    return false;
  }
  
  public boolean verify(GeneralNameInterface paramGeneralNameInterface) throws IOException {
    if (paramGeneralNameInterface == null)
      throw new IOException("name is null"); 
    if (this.excluded != null && this.excluded.size() > 0)
      for (byte b = 0; b < this.excluded.size(); b++) {
        GeneralSubtree generalSubtree = this.excluded.get(b);
        if (generalSubtree != null) {
          GeneralName generalName = generalSubtree.getName();
          if (generalName != null) {
            GeneralNameInterface generalNameInterface = generalName.getName();
            if (generalNameInterface != null)
              switch (generalNameInterface.constrains(paramGeneralNameInterface)) {
                case 0:
                case 1:
                  return false;
              }  
          } 
        } 
      }  
    if (this.permitted != null && this.permitted.size() > 0) {
      boolean bool = false;
      for (byte b = 0; b < this.permitted.size(); b++) {
        GeneralSubtree generalSubtree = this.permitted.get(b);
        if (generalSubtree != null) {
          GeneralName generalName = generalSubtree.getName();
          if (generalName != null) {
            GeneralNameInterface generalNameInterface = generalName.getName();
            if (generalNameInterface != null)
              switch (generalNameInterface.constrains(paramGeneralNameInterface)) {
                case 2:
                case 3:
                  bool = true;
                  break;
                case 0:
                case 1:
                  return true;
              }  
          } 
        } 
      } 
      if (bool)
        return false; 
    } 
    return true;
  }
  
  public Object clone() {
    try {
      NameConstraintsExtension nameConstraintsExtension = (NameConstraintsExtension)super.clone();
      if (this.permitted != null)
        nameConstraintsExtension.permitted = (GeneralSubtrees)this.permitted.clone(); 
      if (this.excluded != null)
        nameConstraintsExtension.excluded = (GeneralSubtrees)this.excluded.clone(); 
      return nameConstraintsExtension;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new RuntimeException("CloneNotSupportedException while cloning NameConstraintsException. This should never happen.");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\NameConstraintsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */