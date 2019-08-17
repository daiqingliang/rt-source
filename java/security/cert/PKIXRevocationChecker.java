package java.security.cert;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class PKIXRevocationChecker extends PKIXCertPathChecker {
  private URI ocspResponder;
  
  private X509Certificate ocspResponderCert;
  
  private List<Extension> ocspExtensions = Collections.emptyList();
  
  private Map<X509Certificate, byte[]> ocspResponses = Collections.emptyMap();
  
  private Set<Option> options = Collections.emptySet();
  
  public void setOcspResponder(URI paramURI) { this.ocspResponder = paramURI; }
  
  public URI getOcspResponder() { return this.ocspResponder; }
  
  public void setOcspResponderCert(X509Certificate paramX509Certificate) { this.ocspResponderCert = paramX509Certificate; }
  
  public X509Certificate getOcspResponderCert() { return this.ocspResponderCert; }
  
  public void setOcspExtensions(List<Extension> paramList) { this.ocspExtensions = (paramList == null) ? Collections.emptyList() : new ArrayList(paramList); }
  
  public List<Extension> getOcspExtensions() { return Collections.unmodifiableList(this.ocspExtensions); }
  
  public void setOcspResponses(Map<X509Certificate, byte[]> paramMap) {
    if (paramMap == null) {
      this.ocspResponses = Collections.emptyMap();
    } else {
      HashMap hashMap = new HashMap(paramMap.size());
      for (Map.Entry entry : paramMap.entrySet())
        hashMap.put(entry.getKey(), ((byte[])entry.getValue()).clone()); 
      this.ocspResponses = hashMap;
    } 
  }
  
  public Map<X509Certificate, byte[]> getOcspResponses() {
    HashMap hashMap = new HashMap(this.ocspResponses.size());
    for (Map.Entry entry : this.ocspResponses.entrySet())
      hashMap.put(entry.getKey(), ((byte[])entry.getValue()).clone()); 
    return hashMap;
  }
  
  public void setOptions(Set<Option> paramSet) { this.options = (paramSet == null) ? Collections.emptySet() : new HashSet(paramSet); }
  
  public Set<Option> getOptions() { return Collections.unmodifiableSet(this.options); }
  
  public abstract List<CertPathValidatorException> getSoftFailExceptions();
  
  public PKIXRevocationChecker clone() {
    PKIXRevocationChecker pKIXRevocationChecker = (PKIXRevocationChecker)super.clone();
    pKIXRevocationChecker.ocspExtensions = new ArrayList(this.ocspExtensions);
    pKIXRevocationChecker.ocspResponses = new HashMap(this.ocspResponses);
    for (Map.Entry entry : pKIXRevocationChecker.ocspResponses.entrySet()) {
      byte[] arrayOfByte = (byte[])entry.getValue();
      entry.setValue(arrayOfByte.clone());
    } 
    pKIXRevocationChecker.options = new HashSet(this.options);
    return pKIXRevocationChecker;
  }
  
  public enum Option {
    ONLY_END_ENTITY, PREFER_CRLS, NO_FALLBACK, SOFT_FAIL;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\PKIXRevocationChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */