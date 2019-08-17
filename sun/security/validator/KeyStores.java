package sun.security.validator;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class KeyStores {
  public static Set<X509Certificate> getTrustedCerts(KeyStore paramKeyStore) {
    HashSet hashSet = new HashSet();
    try {
      Enumeration enumeration = paramKeyStore.aliases();
      while (enumeration.hasMoreElements()) {
        String str = (String)enumeration.nextElement();
        if (paramKeyStore.isCertificateEntry(str)) {
          Certificate certificate = paramKeyStore.getCertificate(str);
          if (certificate instanceof X509Certificate)
            hashSet.add((X509Certificate)certificate); 
          continue;
        } 
        if (paramKeyStore.isKeyEntry(str)) {
          Certificate[] arrayOfCertificate = paramKeyStore.getCertificateChain(str);
          if (arrayOfCertificate != null && arrayOfCertificate.length > 0 && arrayOfCertificate[0] instanceof X509Certificate)
            hashSet.add((X509Certificate)arrayOfCertificate[0]); 
        } 
      } 
    } catch (KeyStoreException keyStoreException) {}
    return hashSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\validator\KeyStores.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */