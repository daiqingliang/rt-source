package sun.net.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.SecureCacheResponse;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;
import sun.net.www.http.HttpClient;
import sun.net.www.protocol.http.Handler;
import sun.net.www.protocol.http.HttpURLConnection;

public abstract class AbstractDelegateHttpsURLConnection extends HttpURLConnection {
  protected AbstractDelegateHttpsURLConnection(URL paramURL, Handler paramHandler) throws IOException { this(paramURL, null, paramHandler); }
  
  protected AbstractDelegateHttpsURLConnection(URL paramURL, Proxy paramProxy, Handler paramHandler) throws IOException { super(paramURL, paramProxy, paramHandler); }
  
  protected abstract SSLSocketFactory getSSLSocketFactory();
  
  protected abstract HostnameVerifier getHostnameVerifier();
  
  public void setNewClient(URL paramURL) throws IOException { setNewClient(paramURL, false); }
  
  public void setNewClient(URL paramURL, boolean paramBoolean) throws IOException {
    this.http = HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramBoolean, this);
    ((HttpsClient)this.http).afterConnect();
  }
  
  public void setProxiedClient(URL paramURL, String paramString, int paramInt) throws IOException { setProxiedClient(paramURL, paramString, paramInt, false); }
  
  public void setProxiedClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean) throws IOException {
    proxiedConnect(paramURL, paramString, paramInt, paramBoolean);
    if (!this.http.isCachedConnection())
      doTunneling(); 
    ((HttpsClient)this.http).afterConnect();
  }
  
  protected void proxiedConnect(URL paramURL, String paramString, int paramInt, boolean paramBoolean) throws IOException {
    if (this.connected)
      return; 
    this.http = HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramString, paramInt, paramBoolean, this);
    this.connected = true;
  }
  
  public boolean isConnected() { return this.connected; }
  
  public void setConnected(boolean paramBoolean) { this.connected = paramBoolean; }
  
  public void connect() throws IOException {
    if (this.connected)
      return; 
    plainConnect();
    if (this.cachedResponse != null)
      return; 
    if (!this.http.isCachedConnection() && this.http.needsTunneling())
      doTunneling(); 
    ((HttpsClient)this.http).afterConnect();
  }
  
  protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt) throws IOException { return HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramProxy, true, paramInt, this); }
  
  protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt, boolean paramBoolean) throws IOException { return HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramProxy, paramBoolean, paramInt, this); }
  
  public String getCipherSuite() {
    if (this.cachedResponse != null)
      return ((SecureCacheResponse)this.cachedResponse).getCipherSuite(); 
    if (this.http == null)
      throw new IllegalStateException("connection not yet open"); 
    return ((HttpsClient)this.http).getCipherSuite();
  }
  
  public Certificate[] getLocalCertificates() {
    if (this.cachedResponse != null) {
      List list = ((SecureCacheResponse)this.cachedResponse).getLocalCertificateChain();
      return (list == null) ? null : (Certificate[])list.toArray(new Certificate[0]);
    } 
    if (this.http == null)
      throw new IllegalStateException("connection not yet open"); 
    return ((HttpsClient)this.http).getLocalCertificates();
  }
  
  public Certificate[] getServerCertificates() {
    if (this.cachedResponse != null) {
      List list = ((SecureCacheResponse)this.cachedResponse).getServerCertificateChain();
      return (list == null) ? null : (Certificate[])list.toArray(new Certificate[0]);
    } 
    if (this.http == null)
      throw new IllegalStateException("connection not yet open"); 
    return ((HttpsClient)this.http).getServerCertificates();
  }
  
  public X509Certificate[] getServerCertificateChain() throws SSLPeerUnverifiedException {
    if (this.cachedResponse != null)
      throw new UnsupportedOperationException("this method is not supported when using cache"); 
    if (this.http == null)
      throw new IllegalStateException("connection not yet open"); 
    return ((HttpsClient)this.http).getServerCertificateChain();
  }
  
  Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
    if (this.cachedResponse != null)
      return ((SecureCacheResponse)this.cachedResponse).getPeerPrincipal(); 
    if (this.http == null)
      throw new IllegalStateException("connection not yet open"); 
    return ((HttpsClient)this.http).getPeerPrincipal();
  }
  
  Principal getLocalPrincipal() throws SSLPeerUnverifiedException {
    if (this.cachedResponse != null)
      return ((SecureCacheResponse)this.cachedResponse).getLocalPrincipal(); 
    if (this.http == null)
      throw new IllegalStateException("connection not yet open"); 
    return ((HttpsClient)this.http).getLocalPrincipal();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\https\AbstractDelegateHttpsURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */