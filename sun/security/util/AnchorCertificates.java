package sun.security.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.PrivilegedAction;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import sun.security.x509.X509CertImpl;

public class AnchorCertificates {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private static final String HASH = "SHA-256";
  
  private static Set<String> certs = Collections.emptySet();
  
  public static boolean contains(X509Certificate paramX509Certificate) {
    String str = X509CertImpl.getFingerprint("SHA-256", paramX509Certificate);
    boolean bool = certs.contains(str);
    if (bool && debug != null)
      debug.println("AnchorCertificate.contains: matched " + paramX509Certificate.getSubjectDN()); 
    return bool;
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            File file = new File(System.getProperty("java.home"), "lib/security/cacerts");
            try {
              KeyStore keyStore = KeyStore.getInstance("JKS");
              try (FileInputStream null = new FileInputStream(file)) {
                keyStore.load(fileInputStream, null);
                certs = new HashSet();
                enumeration = keyStore.aliases();
                while (enumeration.hasMoreElements()) {
                  String str = (String)enumeration.nextElement();
                  if (str.contains(" [jdk")) {
                    X509Certificate x509Certificate = (X509Certificate)keyStore.getCertificate(str);
                    certs.add(X509CertImpl.getFingerprint("SHA-256", x509Certificate));
                  } 
                } 
              } 
            } catch (Exception exception) {
              if (debug != null)
                debug.println("Error parsing cacerts"); 
              exception.printStackTrace();
            } 
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\AnchorCertificates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */