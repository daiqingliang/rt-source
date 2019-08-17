package com.sun.net.ssl.internal.www.protocol.https;

import com.sun.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import sun.net.www.protocol.http.Handler;
import sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection;

public class DelegateHttpsURLConnection extends AbstractDelegateHttpsURLConnection {
  public HttpsURLConnection httpsURLConnection;
  
  DelegateHttpsURLConnection(URL paramURL, Handler paramHandler, HttpsURLConnection paramHttpsURLConnection) throws IOException { this(paramURL, null, paramHandler, paramHttpsURLConnection); }
  
  DelegateHttpsURLConnection(URL paramURL, Proxy paramProxy, Handler paramHandler, HttpsURLConnection paramHttpsURLConnection) throws IOException {
    super(paramURL, paramProxy, paramHandler);
    this.httpsURLConnection = paramHttpsURLConnection;
  }
  
  protected SSLSocketFactory getSSLSocketFactory() { return this.httpsURLConnection.getSSLSocketFactory(); }
  
  protected HostnameVerifier getHostnameVerifier() { return new VerifierWrapper(this.httpsURLConnection.getHostnameVerifier()); }
  
  protected void dispose() throws Throwable { finalize(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\internal\www\protocol\https\DelegateHttpsURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */