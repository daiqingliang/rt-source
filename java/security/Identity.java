package java.security;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Vector;

@Deprecated
public abstract class Identity implements Principal, Serializable {
  private static final long serialVersionUID = 3609922007826600659L;
  
  private String name;
  
  private PublicKey publicKey;
  
  String info = "No further information available.";
  
  IdentityScope scope;
  
  Vector<Certificate> certificates;
  
  protected Identity() { this("restoring..."); }
  
  public Identity(String paramString, IdentityScope paramIdentityScope) throws KeyManagementException {
    this(paramString);
    if (paramIdentityScope != null)
      paramIdentityScope.addIdentity(this); 
    this.scope = paramIdentityScope;
  }
  
  public Identity(String paramString) { this.name = paramString; }
  
  public final String getName() { return this.name; }
  
  public final IdentityScope getScope() { return this.scope; }
  
  public PublicKey getPublicKey() { return this.publicKey; }
  
  public void setPublicKey(PublicKey paramPublicKey) throws KeyManagementException {
    check("setIdentityPublicKey");
    this.publicKey = paramPublicKey;
    this.certificates = new Vector();
  }
  
  public void setInfo(String paramString) {
    check("setIdentityInfo");
    this.info = paramString;
  }
  
  public String getInfo() { return this.info; }
  
  public void addCertificate(Certificate paramCertificate) throws KeyManagementException {
    check("addIdentityCertificate");
    if (this.certificates == null)
      this.certificates = new Vector(); 
    if (this.publicKey != null) {
      if (!keyEquals(this.publicKey, paramCertificate.getPublicKey()))
        throw new KeyManagementException("public key different from cert public key"); 
    } else {
      this.publicKey = paramCertificate.getPublicKey();
    } 
    this.certificates.addElement(paramCertificate);
  }
  
  private boolean keyEquals(PublicKey paramPublicKey1, PublicKey paramPublicKey2) {
    String str1 = paramPublicKey1.getFormat();
    String str2 = paramPublicKey2.getFormat();
    return (((str1 == null) ? 1 : 0) ^ ((str2 == null) ? 1 : 0)) ? false : ((str1 != null && str2 != null && !str1.equalsIgnoreCase(str2)) ? false : Arrays.equals(paramPublicKey1.getEncoded(), paramPublicKey2.getEncoded()));
  }
  
  public void removeCertificate(Certificate paramCertificate) throws KeyManagementException {
    check("removeIdentityCertificate");
    if (this.certificates != null)
      this.certificates.removeElement(paramCertificate); 
  }
  
  public Certificate[] certificates() {
    if (this.certificates == null)
      return new Certificate[0]; 
    int i = this.certificates.size();
    Certificate[] arrayOfCertificate = new Certificate[i];
    this.certificates.copyInto(arrayOfCertificate);
    return arrayOfCertificate;
  }
  
  public final boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof Identity) {
      Identity identity = (Identity)paramObject;
      return fullName().equals(identity.fullName()) ? true : identityEquals(identity);
    } 
    return false;
  }
  
  protected boolean identityEquals(Identity paramIdentity) { return !this.name.equalsIgnoreCase(paramIdentity.name) ? false : ((((this.publicKey == null) ? 1 : 0) ^ ((paramIdentity.publicKey == null) ? 1 : 0)) ? false : (!(this.publicKey != null && paramIdentity.publicKey != null && !this.publicKey.equals(paramIdentity.publicKey)))); }
  
  String fullName() {
    String str = this.name;
    if (this.scope != null)
      str = str + "." + this.scope.getName(); 
    return str;
  }
  
  public String toString() {
    check("printIdentity");
    String str = this.name;
    if (this.scope != null)
      str = str + "[" + this.scope.getName() + "]"; 
    return str;
  }
  
  public String toString(boolean paramBoolean) {
    String str = toString();
    if (paramBoolean) {
      str = str + "\n";
      str = str + printKeys();
      str = str + "\n" + printCertificates();
      if (this.info != null) {
        str = str + "\n\t" + this.info;
      } else {
        str = str + "\n\tno additional information available.";
      } 
    } 
    return str;
  }
  
  String printKeys() {
    String str = "";
    if (this.publicKey != null) {
      str = "\tpublic key initialized";
    } else {
      str = "\tno public key";
    } 
    return str;
  }
  
  String printCertificates() {
    String str = "";
    if (this.certificates == null)
      return "\tno certificates"; 
    str = str + "\tcertificates: \n";
    byte b = 1;
    for (Certificate certificate : this.certificates) {
      str = str + "\tcertificate " + b++ + "\tfor  : " + certificate.getPrincipal() + "\n";
      str = str + "\t\t\tfrom : " + certificate.getGuarantor() + "\n";
    } 
    return str;
  }
  
  public int hashCode() { return this.name.hashCode(); }
  
  private static void check(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSecurityAccess(paramString); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Identity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */