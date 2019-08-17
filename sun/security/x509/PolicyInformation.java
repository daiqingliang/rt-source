package sun.security.x509;

import java.io.IOException;
import java.security.cert.PolicyQualifierInfo;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PolicyInformation {
  public static final String NAME = "PolicyInformation";
  
  public static final String ID = "id";
  
  public static final String QUALIFIERS = "qualifiers";
  
  private CertificatePolicyId policyIdentifier;
  
  private Set<PolicyQualifierInfo> policyQualifiers;
  
  public PolicyInformation(CertificatePolicyId paramCertificatePolicyId, Set<PolicyQualifierInfo> paramSet) throws IOException {
    if (paramSet == null)
      throw new NullPointerException("policyQualifiers is null"); 
    this.policyQualifiers = new LinkedHashSet(paramSet);
    this.policyIdentifier = paramCertificatePolicyId;
  }
  
  public PolicyInformation(DerValue paramDerValue) throws IOException {
    if (paramDerValue.tag != 48)
      throw new IOException("Invalid encoding of PolicyInformation"); 
    this.policyIdentifier = new CertificatePolicyId(paramDerValue.data.getDerValue());
    if (paramDerValue.data.available() != 0) {
      this.policyQualifiers = new LinkedHashSet();
      DerValue derValue = paramDerValue.data.getDerValue();
      if (derValue.tag != 48)
        throw new IOException("Invalid encoding of PolicyInformation"); 
      if (derValue.data.available() == 0)
        throw new IOException("No data available in policyQualifiers"); 
      while (derValue.data.available() != 0)
        this.policyQualifiers.add(new PolicyQualifierInfo(derValue.data.getDerValue().toByteArray())); 
    } else {
      this.policyQualifiers = Collections.emptySet();
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof PolicyInformation))
      return false; 
    PolicyInformation policyInformation = (PolicyInformation)paramObject;
    return !this.policyIdentifier.equals(policyInformation.getPolicyIdentifier()) ? false : this.policyQualifiers.equals(policyInformation.getPolicyQualifiers());
  }
  
  public int hashCode() {
    null = 37 + this.policyIdentifier.hashCode();
    return 37 * null + this.policyQualifiers.hashCode();
  }
  
  public CertificatePolicyId getPolicyIdentifier() { return this.policyIdentifier; }
  
  public Set<PolicyQualifierInfo> getPolicyQualifiers() { return this.policyQualifiers; }
  
  public Object get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("id"))
      return this.policyIdentifier; 
    if (paramString.equalsIgnoreCase("qualifiers"))
      return this.policyQualifiers; 
    throw new IOException("Attribute name [" + paramString + "] not recognized by PolicyInformation.");
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("id")) {
      if (paramObject instanceof CertificatePolicyId) {
        this.policyIdentifier = (CertificatePolicyId)paramObject;
      } else {
        throw new IOException("Attribute value must be instance of CertificatePolicyId.");
      } 
    } else if (paramString.equalsIgnoreCase("qualifiers")) {
      if (this.policyIdentifier == null)
        throw new IOException("Attribute must have a CertificatePolicyIdentifier value before PolicyQualifierInfo can be set."); 
      if (paramObject instanceof Set) {
        for (Object object : (Set)paramObject) {
          if (!(object instanceof PolicyQualifierInfo))
            throw new IOException("Attribute value must be aSet of PolicyQualifierInfo objects."); 
        } 
        this.policyQualifiers = (Set)paramObject;
      } else {
        throw new IOException("Attribute value must be of type Set.");
      } 
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by PolicyInformation");
    } 
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("qualifiers")) {
      this.policyQualifiers = Collections.emptySet();
    } else {
      if (paramString.equalsIgnoreCase("id"))
        throw new IOException("Attribute ID may not be deleted from PolicyInformation."); 
      throw new IOException("Attribute name [" + paramString + "] not recognized by PolicyInformation.");
    } 
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("id");
    attributeNameEnumeration.addElement("qualifiers");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "PolicyInformation"; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("  [" + this.policyIdentifier.toString());
    stringBuilder.append(this.policyQualifiers + "  ]\n");
    return stringBuilder.toString();
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    this.policyIdentifier.encode(derOutputStream);
    if (!this.policyQualifiers.isEmpty()) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      for (PolicyQualifierInfo policyQualifierInfo : this.policyQualifiers)
        derOutputStream1.write(policyQualifierInfo.getEncoded()); 
      derOutputStream.write((byte)48, derOutputStream1);
    } 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\PolicyInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */