package javax.net.ssl;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public abstract class X509ExtendedTrustManager implements X509TrustManager {
  public abstract void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket) throws CertificateException;
  
  public abstract void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket) throws CertificateException;
  
  public abstract void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine) throws CertificateException;
  
  public abstract void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine) throws CertificateException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\X509ExtendedTrustManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */