package sun.security.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import sun.net.www.ParseUtil;

public class PolicyUtil {
  private static final String P11KEYSTORE = "PKCS11";
  
  private static final String NONE = "NONE";
  
  public static InputStream getInputStream(URL paramURL) throws IOException {
    if ("file".equals(paramURL.getProtocol())) {
      String str = paramURL.getFile().replace('/', File.separatorChar);
      str = ParseUtil.decode(str);
      return new FileInputStream(str);
    } 
    return paramURL.openStream();
  }
  
  public static KeyStore getKeyStore(URL paramURL, String paramString1, String paramString2, String paramString3, String paramString4, Debug paramDebug) throws KeyStoreException, MalformedURLException, IOException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
    if (paramString1 == null)
      throw new IllegalArgumentException("null KeyStore name"); 
    arrayOfChar = null;
    try {
      KeyStore keyStore;
      if (paramString2 == null)
        paramString2 = KeyStore.getDefaultType(); 
      if ("PKCS11".equalsIgnoreCase(paramString2) && !"NONE".equals(paramString1))
        throw new IllegalArgumentException("Invalid value (" + paramString1 + ") for keystore URL.  If the keystore type is \"" + "PKCS11" + "\", the keystore url must be \"" + "NONE" + "\""); 
      if (paramString3 != null) {
        keyStore = KeyStore.getInstance(paramString2, paramString3);
      } else {
        keyStore = KeyStore.getInstance(paramString2);
      } 
      if (paramString4 != null) {
        URL uRL1;
        try {
          uRL1 = new URL(paramString4);
        } catch (MalformedURLException malformedURLException) {
          if (paramURL == null)
            throw malformedURLException; 
          uRL1 = new URL(paramURL, paramString4);
        } 
        if (paramDebug != null)
          paramDebug.println("reading password" + uRL1); 
        inputStream = null;
        try {
          inputStream = uRL1.openStream();
          arrayOfChar = Password.readPassword(inputStream);
        } finally {
          if (inputStream != null)
            inputStream.close(); 
        } 
      } 
      if ("NONE".equals(paramString1)) {
        keyStore.load(null, arrayOfChar);
        return keyStore;
      } 
      URL uRL = null;
      try {
        uRL = new URL(paramString1);
      } catch (MalformedURLException malformedURLException) {
        if (paramURL == null)
          throw malformedURLException; 
        uRL = new URL(paramURL, paramString1);
      } 
      if (paramDebug != null)
        paramDebug.println("reading keystore" + uRL); 
      bufferedInputStream = null;
      try {
        bufferedInputStream = new BufferedInputStream(getInputStream(uRL));
        keyStore.load(bufferedInputStream, arrayOfChar);
      } finally {
        bufferedInputStream.close();
      } 
      return keyStore;
    } finally {
      if (arrayOfChar != null)
        Arrays.fill(arrayOfChar, ' '); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\PolicyUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */