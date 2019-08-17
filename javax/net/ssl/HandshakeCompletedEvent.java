package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.EventObject;
import javax.security.auth.x500.X500Principal;
import javax.security.cert.X509Certificate;

public class HandshakeCompletedEvent extends EventObject {
  private static final long serialVersionUID = 7914963744257769778L;
  
  private SSLSession session;
  
  public HandshakeCompletedEvent(SSLSocket paramSSLSocket, SSLSession paramSSLSession) {
    super(paramSSLSocket);
    this.session = paramSSLSession;
  }
  
  public SSLSession getSession() { return this.session; }
  
  public String getCipherSuite() { return this.session.getCipherSuite(); }
  
  public Certificate[] getLocalCertificates() { return this.session.getLocalCertificates(); }
  
  public Certificate[] getPeerCertificates() { return this.session.getPeerCertificates(); }
  
  public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException { return this.session.getPeerCertificateChain(); }
  
  public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
    X500Principal x500Principal;
    try {
      x500Principal = this.session.getPeerPrincipal();
    } catch (AbstractMethodError abstractMethodError) {
      Certificate[] arrayOfCertificate = getPeerCertificates();
      x500Principal = ((X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
    } 
    return x500Principal;
  }
  
  public Principal getLocalPrincipal() throws SSLPeerUnverifiedException {
    X500Principal x500Principal;
    try {
      x500Principal = this.session.getLocalPrincipal();
    } catch (AbstractMethodError abstractMethodError) {
      x500Principal = null;
      Certificate[] arrayOfCertificate = getLocalCertificates();
      if (arrayOfCertificate != null)
        x500Principal = ((X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal(); 
    } 
    return x500Principal;
  }
  
  public SSLSocket getSocket() { return (SSLSocket)getSource(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\HandshakeCompletedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */