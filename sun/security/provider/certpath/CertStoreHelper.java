package sun.security.provider.certpath;

import java.io.IOException;
import java.net.URI;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Cache;

public abstract class CertStoreHelper {
  private static final int NUM_TYPES = 2;
  
  private static final Map<String, String> classMap = new HashMap(2);
  
  private static Cache<String, CertStoreHelper> cache;
  
  public static CertStoreHelper getInstance(final String type) throws NoSuchAlgorithmException {
    CertStoreHelper certStoreHelper = (CertStoreHelper)cache.get(paramString);
    if (certStoreHelper != null)
      return certStoreHelper; 
    final String cl = (String)classMap.get(paramString);
    if (str == null)
      throw new NoSuchAlgorithmException(paramString + " not available"); 
    try {
      return (CertStoreHelper)AccessController.doPrivileged(new PrivilegedExceptionAction<CertStoreHelper>() {
            public CertStoreHelper run() throws ClassNotFoundException {
              try {
                Class clazz = Class.forName(cl, true, null);
                CertStoreHelper certStoreHelper;
                cache.put(type, certStoreHelper);
                return certStoreHelper;
              } catch (InstantiationException|IllegalAccessException instantiationException) {
                throw new AssertionError(instantiationException);
              } 
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new NoSuchAlgorithmException(paramString + " not available", privilegedActionException.getException());
    } 
  }
  
  static boolean isCausedByNetworkIssue(String paramString, CertStoreException paramCertStoreException) {
    Throwable throwable;
    switch (paramString) {
      case "LDAP":
      case "SSLServer":
        try {
          CertStoreHelper certStoreHelper = getInstance(paramString);
          return certStoreHelper.isCausedByNetworkIssue(paramCertStoreException);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          return false;
        } 
      case "URI":
        throwable = paramCertStoreException.getCause();
        return (throwable != null && throwable instanceof IOException);
    } 
    return false;
  }
  
  public abstract CertStore getCertStore(URI paramURI) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
  
  public abstract X509CertSelector wrap(X509CertSelector paramX509CertSelector, X500Principal paramX500Principal, String paramString) throws IOException;
  
  public abstract X509CRLSelector wrap(X509CRLSelector paramX509CRLSelector, Collection<X500Principal> paramCollection, String paramString) throws IOException;
  
  public abstract boolean isCausedByNetworkIssue(CertStoreException paramCertStoreException);
  
  static  {
    classMap.put("LDAP", "sun.security.provider.certpath.ldap.LDAPCertStoreHelper");
    classMap.put("SSLServer", "sun.security.provider.certpath.ssl.SSLServerCertStoreHelper");
    cache = Cache.newSoftMemoryCache(2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\CertStoreHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */