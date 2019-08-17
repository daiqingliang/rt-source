package sun.security.provider.certpath.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

public final class SSLServerCertStore extends CertStoreSpi {
  private final URI uri;
  
  private static final GetChainTrustManager trustManager;
  
  private static final SSLSocketFactory socketFactory;
  
  private static final HostnameVerifier hostnameVerifier;
  
  SSLServerCertStore(URI paramURI) throws InvalidAlgorithmParameterException {
    super(null);
    this.uri = paramURI;
  }
  
  public Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector) throws CertStoreException {
    try {
      URLConnection uRLConnection = this.uri.toURL().openConnection();
      if (uRLConnection instanceof HttpsURLConnection) {
        if (socketFactory == null)
          throw new CertStoreException("No initialized SSLSocketFactory"); 
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection)uRLConnection;
        httpsURLConnection.setSSLSocketFactory(socketFactory);
        httpsURLConnection.setHostnameVerifier(hostnameVerifier);
        synchronized (trustManager) {
          try {
            httpsURLConnection.connect();
            return getMatchingCerts(trustManager.serverChain, paramCertSelector);
          } catch (IOException iOException) {
            if (trustManager.exchangedServerCerts)
              return getMatchingCerts(trustManager.serverChain, paramCertSelector); 
            throw iOException;
          } finally {
            trustManager.cleanup();
          } 
        } 
      } 
    } catch (IOException iOException) {
      throw new CertStoreException(iOException);
    } 
    return Collections.emptySet();
  }
  
  private static List<X509Certificate> getMatchingCerts(List<X509Certificate> paramList, CertSelector paramCertSelector) {
    if (paramCertSelector == null)
      return paramList; 
    ArrayList arrayList = new ArrayList(paramList.size());
    for (X509Certificate x509Certificate : paramList) {
      if (paramCertSelector.match(x509Certificate))
        arrayList.add(x509Certificate); 
    } 
    return arrayList;
  }
  
  public Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector) throws CertStoreException { throw new UnsupportedOperationException(); }
  
  static CertStore getInstance(URI paramURI) throws InvalidAlgorithmParameterException { return new CS(new SSLServerCertStore(paramURI), null, "SSLServer", null); }
  
  static  {
    Object object;
    trustManager = new GetChainTrustManager(null);
    hostnameVerifier = new HostnameVerifier() {
        public boolean verify(String param1String, SSLSession param1SSLSession) { return true; }
      };
    try {
      SSLContext sSLContext = SSLContext.getInstance("SSL");
      sSLContext.init(null, new TrustManager[] { trustManager }, null);
      object = sSLContext.getSocketFactory();
    } catch (GeneralSecurityException generalSecurityException) {
      object = null;
    } 
    socketFactory = object;
  }
  
  private static class CS extends CertStore {
    protected CS(CertStoreSpi param1CertStoreSpi, Provider param1Provider, String param1String, CertStoreParameters param1CertStoreParameters) { super(param1CertStoreSpi, param1Provider, param1String, param1CertStoreParameters); }
  }
  
  private static class GetChainTrustManager extends X509ExtendedTrustManager {
    private List<X509Certificate> serverChain = Collections.emptyList();
    
    private boolean exchangedServerCerts = false;
    
    private GetChainTrustManager() {}
    
    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    
    public void checkClientTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String) throws CertificateException { throw new UnsupportedOperationException(); }
    
    public void checkClientTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String, Socket param1Socket) throws CertificateException { throw new UnsupportedOperationException(); }
    
    public void checkClientTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String, SSLEngine param1SSLEngine) throws CertificateException { throw new UnsupportedOperationException(); }
    
    public void checkServerTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String) throws CertificateException {
      this.exchangedServerCerts = true;
      this.serverChain = (param1ArrayOfX509Certificate == null) ? Collections.emptyList() : Arrays.asList(param1ArrayOfX509Certificate);
    }
    
    public void checkServerTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String, Socket param1Socket) throws CertificateException { checkServerTrusted(param1ArrayOfX509Certificate, param1String); }
    
    public void checkServerTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String, SSLEngine param1SSLEngine) throws CertificateException { checkServerTrusted(param1ArrayOfX509Certificate, param1String); }
    
    void cleanup() {
      this.exchangedServerCerts = false;
      this.serverChain = Collections.emptyList();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\ssl\SSLServerCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */