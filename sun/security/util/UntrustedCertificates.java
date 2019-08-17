package sun.security.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;
import sun.security.x509.X509CertImpl;

public final class UntrustedCertificates {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private static final String ALGORITHM_KEY = "Algorithm";
  
  private static final Properties props = new Properties();
  
  private static final String algorithm;
  
  private static String stripColons(Object paramObject) {
    String str = (String)paramObject;
    char[] arrayOfChar = str.toCharArray();
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfChar.length; b2++) {
      if (arrayOfChar[b2] != ':') {
        if (b2 != b1)
          arrayOfChar[b1] = arrayOfChar[b2]; 
        b1++;
      } 
    } 
    return (b1 == arrayOfChar.length) ? str : new String(arrayOfChar, 0, b1);
  }
  
  public static boolean isUntrusted(X509Certificate paramX509Certificate) {
    String str;
    if (algorithm == null)
      return false; 
    if (paramX509Certificate instanceof X509CertImpl) {
      str = ((X509CertImpl)paramX509Certificate).getFingerprint(algorithm);
    } else {
      try {
        str = (new X509CertImpl(paramX509Certificate.getEncoded())).getFingerprint(algorithm);
      } catch (CertificateException certificateException) {
        return false;
      } 
    } 
    return props.containsKey(str);
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            File file = new File(System.getProperty("java.home"), "lib/security/blacklisted.certs");
            try (FileInputStream null = new FileInputStream(file)) {
              props.load(fileInputStream);
              for (Map.Entry entry : props.entrySet())
                entry.setValue(UntrustedCertificates.stripColons(entry.getValue())); 
            } catch (IOException iOException) {
              if (debug != null)
                debug.println("Error parsing blacklisted.certs"); 
            } 
            return null;
          }
        });
    algorithm = props.getProperty("Algorithm");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\UntrustedCertificates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */