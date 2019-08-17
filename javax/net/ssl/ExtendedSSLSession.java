package javax.net.ssl;

import java.util.List;

public abstract class ExtendedSSLSession implements SSLSession {
  public abstract String[] getLocalSupportedSignatureAlgorithms();
  
  public abstract String[] getPeerSupportedSignatureAlgorithms();
  
  public List<SNIServerName> getRequestedServerNames() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\ExtendedSSLSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */