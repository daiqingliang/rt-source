package java.security;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DomainLoadStoreParameter implements KeyStore.LoadStoreParameter {
  private final URI configuration;
  
  private final Map<String, KeyStore.ProtectionParameter> protectionParams;
  
  public DomainLoadStoreParameter(URI paramURI, Map<String, KeyStore.ProtectionParameter> paramMap) {
    if (paramURI == null || paramMap == null)
      throw new NullPointerException("invalid null input"); 
    this.configuration = paramURI;
    this.protectionParams = Collections.unmodifiableMap(new HashMap(paramMap));
  }
  
  public URI getConfiguration() { return this.configuration; }
  
  public Map<String, KeyStore.ProtectionParameter> getProtectionParams() { return this.protectionParams; }
  
  public KeyStore.ProtectionParameter getProtectionParameter() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\DomainLoadStoreParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */