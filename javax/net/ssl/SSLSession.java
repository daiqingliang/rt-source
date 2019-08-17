package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import javax.security.cert.X509Certificate;

public interface SSLSession {
  byte[] getId();
  
  SSLSessionContext getSessionContext();
  
  long getCreationTime();
  
  long getLastAccessedTime();
  
  void invalidate();
  
  boolean isValid();
  
  void putValue(String paramString, Object paramObject);
  
  Object getValue(String paramString);
  
  void removeValue(String paramString);
  
  String[] getValueNames();
  
  Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException;
  
  Certificate[] getLocalCertificates() throws SSLPeerUnverifiedException;
  
  X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException;
  
  Principal getPeerPrincipal() throws SSLPeerUnverifiedException;
  
  Principal getLocalPrincipal() throws SSLPeerUnverifiedException;
  
  String getCipherSuite();
  
  String getProtocol();
  
  String getPeerHost();
  
  int getPeerPort();
  
  int getPacketBufferSize();
  
  int getApplicationBufferSize();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */