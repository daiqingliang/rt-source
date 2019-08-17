package sun.security.provider.certpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import sun.security.action.GetIntegerAction;
import sun.security.util.Cache;
import sun.security.util.Debug;
import sun.security.x509.AccessDescription;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.URIName;

class URICertStore extends CertStoreSpi {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private static final int CHECK_INTERVAL = 30000;
  
  private static final int CACHE_SIZE = 185;
  
  private final CertificateFactory factory;
  
  private Collection<X509Certificate> certs = Collections.emptySet();
  
  private X509CRL crl;
  
  private long lastChecked;
  
  private long lastModified;
  
  private URI uri;
  
  private boolean ldap = false;
  
  private CertStoreHelper ldapHelper;
  
  private CertStore ldapCertStore;
  
  private String ldapPath;
  
  private static final int DEFAULT_CRL_CONNECT_TIMEOUT = 15000;
  
  private static final int CRL_CONNECT_TIMEOUT = initializeTimeout();
  
  private static final Cache<URICertStoreParameters, CertStore> certStoreCache = Cache.newSoftMemoryCache(185);
  
  private static int initializeTimeout() {
    Integer integer = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.crl.timeout"));
    return (integer == null || integer.intValue() < 0) ? 15000 : (integer.intValue() * 1000);
  }
  
  URICertStore(CertStoreParameters paramCertStoreParameters) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof URICertStoreParameters))
      throw new InvalidAlgorithmParameterException("params must be instanceof URICertStoreParameters"); 
    this.uri = ((URICertStoreParameters)paramCertStoreParameters).uri;
    if (this.uri.getScheme().toLowerCase(Locale.ENGLISH).equals("ldap")) {
      this.ldap = true;
      this.ldapHelper = CertStoreHelper.getInstance("LDAP");
      this.ldapCertStore = this.ldapHelper.getCertStore(this.uri);
      this.ldapPath = this.uri.getPath();
      if (this.ldapPath.charAt(0) == '/')
        this.ldapPath = this.ldapPath.substring(1); 
    } 
    try {
      this.factory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {
      throw new RuntimeException();
    } 
  }
  
  static CertStore getInstance(URICertStoreParameters paramURICertStoreParameters) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    if (debug != null)
      debug.println("CertStore URI:" + paramURICertStoreParameters.uri); 
    CertStore certStore = (CertStore)certStoreCache.get(paramURICertStoreParameters);
    if (certStore == null) {
      certStore = new UCS(new URICertStore(paramURICertStoreParameters), null, "URI", paramURICertStoreParameters);
      certStoreCache.put(paramURICertStoreParameters, certStore);
    } else if (debug != null) {
      debug.println("URICertStore.getInstance: cache hit");
    } 
    return certStore;
  }
  
  static CertStore getInstance(AccessDescription paramAccessDescription) {
    if (!paramAccessDescription.getAccessMethod().equals(AccessDescription.Ad_CAISSUERS_Id))
      return null; 
    GeneralNameInterface generalNameInterface = paramAccessDescription.getAccessLocation().getName();
    if (!(generalNameInterface instanceof URIName))
      return null; 
    URI uRI = ((URIName)generalNameInterface).getURI();
    try {
      return getInstance(new URICertStoreParameters(uRI));
    } catch (Exception exception) {
      if (debug != null) {
        debug.println("exception creating CertStore: " + exception);
        exception.printStackTrace();
      } 
      return null;
    } 
  }
  
  public Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector) throws CertStoreException {
    if (this.ldap) {
      X509CertSelector x509CertSelector = (X509CertSelector)paramCertSelector;
      try {
        x509CertSelector = this.ldapHelper.wrap(x509CertSelector, x509CertSelector.getSubject(), this.ldapPath);
      } catch (IOException iOException) {
        throw new CertStoreException(iOException);
      } 
      return this.ldapCertStore.getCertificates(x509CertSelector);
    } 
    long l = System.currentTimeMillis();
    if (l - this.lastChecked < 30000L) {
      if (debug != null)
        debug.println("Returning certificates from cache"); 
      return getMatchingCerts(this.certs, paramCertSelector);
    } 
    this.lastChecked = l;
    try {
      URLConnection uRLConnection = this.uri.toURL().openConnection();
      if (this.lastModified != 0L)
        uRLConnection.setIfModifiedSince(this.lastModified); 
      long l1 = this.lastModified;
      try (InputStream null = uRLConnection.getInputStream()) {
        this.lastModified = uRLConnection.getLastModified();
        if (l1 != 0L) {
          if (l1 == this.lastModified) {
            if (debug != null)
              debug.println("Not modified, using cached copy"); 
            return getMatchingCerts(this.certs, paramCertSelector);
          } 
          if (uRLConnection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
            if (httpURLConnection.getResponseCode() == 304) {
              if (debug != null)
                debug.println("Not modified, using cached copy"); 
              return getMatchingCerts(this.certs, paramCertSelector);
            } 
          } 
        } 
        if (debug != null)
          debug.println("Downloading new certificates..."); 
        this.certs = this.factory.generateCertificates(inputStream);
      } 
      return getMatchingCerts(this.certs, paramCertSelector);
    } catch (IOException|CertificateException iOException) {
      if (debug != null) {
        debug.println("Exception fetching certificates:");
        iOException.printStackTrace();
      } 
      this.lastModified = 0L;
      this.certs = Collections.emptySet();
      return this.certs;
    } 
  }
  
  private static Collection<X509Certificate> getMatchingCerts(Collection<X509Certificate> paramCollection, CertSelector paramCertSelector) {
    if (paramCertSelector == null)
      return paramCollection; 
    ArrayList arrayList = new ArrayList(paramCollection.size());
    for (X509Certificate x509Certificate : paramCollection) {
      if (paramCertSelector.match(x509Certificate))
        arrayList.add(x509Certificate); 
    } 
    return arrayList;
  }
  
  public Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector) throws CertStoreException {
    if (this.ldap) {
      X509CRLSelector x509CRLSelector = (X509CRLSelector)paramCRLSelector;
      try {
        x509CRLSelector = this.ldapHelper.wrap(x509CRLSelector, null, this.ldapPath);
      } catch (IOException iOException) {
        throw new CertStoreException(iOException);
      } 
      try {
        return this.ldapCertStore.getCRLs(x509CRLSelector);
      } catch (CertStoreException certStoreException) {
        throw new PKIX.CertStoreTypeException("LDAP", certStoreException);
      } 
    } 
    long l = System.currentTimeMillis();
    if (l - this.lastChecked < 30000L) {
      if (debug != null)
        debug.println("Returning CRL from cache"); 
      return getMatchingCRLs(this.crl, paramCRLSelector);
    } 
    this.lastChecked = l;
    try {
      URLConnection uRLConnection = this.uri.toURL().openConnection();
      if (this.lastModified != 0L)
        uRLConnection.setIfModifiedSince(this.lastModified); 
      long l1 = this.lastModified;
      uRLConnection.setConnectTimeout(CRL_CONNECT_TIMEOUT);
      try (InputStream null = uRLConnection.getInputStream()) {
        this.lastModified = uRLConnection.getLastModified();
        if (l1 != 0L) {
          if (l1 == this.lastModified) {
            if (debug != null)
              debug.println("Not modified, using cached copy"); 
            return getMatchingCRLs(this.crl, paramCRLSelector);
          } 
          if (uRLConnection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
            if (httpURLConnection.getResponseCode() == 304) {
              if (debug != null)
                debug.println("Not modified, using cached copy"); 
              return getMatchingCRLs(this.crl, paramCRLSelector);
            } 
          } 
        } 
        if (debug != null)
          debug.println("Downloading new CRL..."); 
        this.crl = (X509CRL)this.factory.generateCRL(inputStream);
      } 
      return getMatchingCRLs(this.crl, paramCRLSelector);
    } catch (IOException|java.security.cert.CRLException iOException) {
      if (debug != null) {
        debug.println("Exception fetching CRL:");
        iOException.printStackTrace();
      } 
      this.lastModified = 0L;
      this.crl = null;
      throw new PKIX.CertStoreTypeException("URI", new CertStoreException(iOException));
    } 
  }
  
  private static Collection<X509CRL> getMatchingCRLs(X509CRL paramX509CRL, CRLSelector paramCRLSelector) { return (paramCRLSelector == null || (paramX509CRL != null && paramCRLSelector.match(paramX509CRL))) ? Collections.singletonList(paramX509CRL) : Collections.emptyList(); }
  
  private static class UCS extends CertStore {
    protected UCS(CertStoreSpi param1CertStoreSpi, Provider param1Provider, String param1String, CertStoreParameters param1CertStoreParameters) { super(param1CertStoreSpi, param1Provider, param1String, param1CertStoreParameters); }
  }
  
  static class URICertStoreParameters implements CertStoreParameters {
    private final URI uri;
    
    URICertStoreParameters(URI param1URI) { this.uri = param1URI; }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof URICertStoreParameters))
        return false; 
      URICertStoreParameters uRICertStoreParameters = (URICertStoreParameters)param1Object;
      return this.uri.equals(uRICertStoreParameters.uri);
    }
    
    public int hashCode() {
      if (this.hashCode == 0) {
        int i = 17;
        i = 37 * i + this.uri.hashCode();
        this.hashCode = i;
      } 
      return this.hashCode;
    }
    
    public Object clone() {
      try {
        return super.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\URICertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */