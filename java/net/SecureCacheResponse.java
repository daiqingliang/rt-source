package java.net;

import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;

public abstract class SecureCacheResponse extends CacheResponse {
  public abstract String getCipherSuite();
  
  public abstract List<Certificate> getLocalCertificateChain();
  
  public abstract List<Certificate> getServerCertificateChain();
  
  public abstract Principal getPeerPrincipal() throws SSLPeerUnverifiedException;
  
  public abstract Principal getLocalPrincipal() throws SSLPeerUnverifiedException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\SecureCacheResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */