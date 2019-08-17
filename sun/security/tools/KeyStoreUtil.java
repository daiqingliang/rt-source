package sun.security.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.Collator;
import java.util.Locale;
import java.util.ResourceBundle;

public class KeyStoreUtil {
  private static final String JKS = "jks";
  
  private static final Collator collator = Collator.getInstance();
  
  public static boolean isSelfSigned(X509Certificate paramX509Certificate) { return signedBy(paramX509Certificate, paramX509Certificate); }
  
  public static boolean signedBy(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2) {
    if (!paramX509Certificate2.getSubjectX500Principal().equals(paramX509Certificate1.getIssuerX500Principal()))
      return false; 
    try {
      paramX509Certificate1.verify(paramX509Certificate2.getPublicKey());
      return true;
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public static boolean isWindowsKeyStore(String paramString) { return (paramString != null && (paramString.equalsIgnoreCase("Windows-MY") || paramString.equalsIgnoreCase("Windows-ROOT"))); }
  
  public static String niceStoreTypeName(String paramString) { return paramString.equalsIgnoreCase("Windows-MY") ? "Windows-MY" : (paramString.equalsIgnoreCase("Windows-ROOT") ? "Windows-ROOT" : paramString.toUpperCase(Locale.ENGLISH)); }
  
  public static KeyStore getCacertsKeyStore() throws Exception {
    String str = File.separator;
    File file = new File(System.getProperty("java.home") + str + "lib" + str + "security" + str + "cacerts");
    if (!file.exists())
      return null; 
    KeyStore keyStore = null;
    try (FileInputStream null = new FileInputStream(file)) {
      keyStore = KeyStore.getInstance("jks");
      keyStore.load(fileInputStream, null);
    } 
    return keyStore;
  }
  
  public static char[] getPassWithModifier(String paramString1, String paramString2, ResourceBundle paramResourceBundle) {
    if (paramString1 == null)
      return paramString2.toCharArray(); 
    if (collator.compare(paramString1, "env") == 0) {
      String str = System.getenv(paramString2);
      if (str == null) {
        System.err.println(paramResourceBundle.getString("Cannot.find.environment.variable.") + paramString2);
        return null;
      } 
      return str.toCharArray();
    } 
    if (collator.compare(paramString1, "file") == 0)
      try {
        URL uRL = null;
        try {
          uRL = new URL(paramString2);
        } catch (MalformedURLException malformedURLException) {
          File file = new File(paramString2);
          if (file.exists()) {
            uRL = file.toURI().toURL();
          } else {
            System.err.println(paramResourceBundle.getString("Cannot.find.file.") + paramString2);
            return null;
          } 
        } 
        try (BufferedReader null = new BufferedReader(new InputStreamReader(uRL.openStream()))) {
          String str = bufferedReader.readLine();
          if (str == null)
            return new char[0]; 
          return str.toCharArray();
        } 
      } catch (IOException iOException) {
        System.err.println(iOException);
        return null;
      }  
    System.err.println(paramResourceBundle.getString("Unknown.password.type.") + paramString1);
    return null;
  }
  
  static  {
    collator.setStrength(0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\KeyStoreUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */