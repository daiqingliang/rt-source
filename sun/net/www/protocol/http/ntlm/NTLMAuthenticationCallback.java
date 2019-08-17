package sun.net.www.protocol.http.ntlm;

import java.net.URL;

public abstract class NTLMAuthenticationCallback {
  public static void setNTLMAuthenticationCallback(NTLMAuthenticationCallback paramNTLMAuthenticationCallback) { callback = paramNTLMAuthenticationCallback; }
  
  public static NTLMAuthenticationCallback getNTLMAuthenticationCallback() { return callback; }
  
  public abstract boolean isTrustedSite(URL paramURL);
  
  static class DefaultNTLMAuthenticationCallback extends NTLMAuthenticationCallback {
    public boolean isTrustedSite(URL param1URL) { return true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\ntlm\NTLMAuthenticationCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */