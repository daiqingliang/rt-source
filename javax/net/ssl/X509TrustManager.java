package javax.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface X509TrustManager extends TrustManager {
  void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException;
  
  void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException;
  
  X509Certificate[] getAcceptedIssuers();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\X509TrustManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */