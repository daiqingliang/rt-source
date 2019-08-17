package sun.net.www.protocol.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public final class DefaultHostnameVerifier implements HostnameVerifier {
  public boolean verify(String paramString, SSLSession paramSSLSession) { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\https\DefaultHostnameVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */