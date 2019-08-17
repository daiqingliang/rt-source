package javax.security.auth.x500;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.security.auth.Destroyable;

public final class X500PrivateCredential implements Destroyable {
  private X509Certificate cert;
  
  private PrivateKey key;
  
  private String alias;
  
  public X500PrivateCredential(X509Certificate paramX509Certificate, PrivateKey paramPrivateKey) {
    if (paramX509Certificate == null || paramPrivateKey == null)
      throw new IllegalArgumentException(); 
    this.cert = paramX509Certificate;
    this.key = paramPrivateKey;
    this.alias = null;
  }
  
  public X500PrivateCredential(X509Certificate paramX509Certificate, PrivateKey paramPrivateKey, String paramString) {
    if (paramX509Certificate == null || paramPrivateKey == null || paramString == null)
      throw new IllegalArgumentException(); 
    this.cert = paramX509Certificate;
    this.key = paramPrivateKey;
    this.alias = paramString;
  }
  
  public X509Certificate getCertificate() { return this.cert; }
  
  public PrivateKey getPrivateKey() { return this.key; }
  
  public String getAlias() { return this.alias; }
  
  public void destroy() {
    this.cert = null;
    this.key = null;
    this.alias = null;
  }
  
  public boolean isDestroyed() { return (this.cert == null && this.key == null && this.alias == null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\x500\X500PrivateCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */