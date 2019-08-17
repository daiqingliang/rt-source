package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificatePoliciesExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.CertificatePolicies";
  
  public static final String NAME = "CertificatePolicies";
  
  public static final String POLICIES = "policies";
  
  private List<PolicyInformation> certPolicies;
  
  private void encodeThis() throws IOException {
    if (this.certPolicies == null || this.certPolicies.isEmpty()) {
      this.extensionValue = null;
    } else {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      DerOutputStream derOutputStream2 = new DerOutputStream();
      for (PolicyInformation policyInformation : this.certPolicies)
        policyInformation.encode(derOutputStream2); 
      derOutputStream1.write((byte)48, derOutputStream2);
      this.extensionValue = derOutputStream1.toByteArray();
    } 
  }
  
  public CertificatePoliciesExtension(List<PolicyInformation> paramList) throws IOException { this(Boolean.FALSE, paramList); }
  
  public CertificatePoliciesExtension(Boolean paramBoolean, List<PolicyInformation> paramList) throws IOException {
    this.certPolicies = paramList;
    this.extensionId = PKIXExtensions.CertificatePolicies_Id;
    this.critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public CertificatePoliciesExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.CertificatePolicies_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 48)
      throw new IOException("Invalid encoding for CertificatePoliciesExtension."); 
    this.certPolicies = new ArrayList();
    while (derValue.data.available() != 0) {
      DerValue derValue1 = derValue.data.getDerValue();
      PolicyInformation policyInformation = new PolicyInformation(derValue1);
      this.certPolicies.add(policyInformation);
    } 
  }
  
  public String toString() {
    if (this.certPolicies == null)
      return ""; 
    StringBuilder stringBuilder = new StringBuilder(super.toString());
    stringBuilder.append("CertificatePolicies [\n");
    for (PolicyInformation policyInformation : this.certPolicies)
      stringBuilder.append(policyInformation.toString()); 
    stringBuilder.append("]\n");
    return stringBuilder.toString();
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.CertificatePolicies_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("policies")) {
      if (!(paramObject instanceof List))
        throw new IOException("Attribute value should be of type List."); 
      this.certPolicies = (List)paramObject;
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
    } 
    encodeThis();
  }
  
  public List<PolicyInformation> get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("policies"))
      return this.certPolicies; 
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("policies")) {
      this.certPolicies = null;
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("policies");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "CertificatePolicies"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificatePoliciesExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */