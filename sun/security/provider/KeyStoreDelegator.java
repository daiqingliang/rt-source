package sun.security.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import sun.security.util.Debug;

class KeyStoreDelegator extends KeyStoreSpi {
  private static final String KEYSTORE_TYPE_COMPAT = "keystore.type.compat";
  
  private static final Debug debug = Debug.getInstance("keystore");
  
  private final String primaryType;
  
  private final String secondaryType;
  
  private final Class<? extends KeyStoreSpi> primaryKeyStore;
  
  private final Class<? extends KeyStoreSpi> secondaryKeyStore;
  
  private String type;
  
  private KeyStoreSpi keystore;
  
  private boolean compatModeEnabled = true;
  
  public KeyStoreDelegator(String paramString1, Class<? extends KeyStoreSpi> paramClass1, String paramString2, Class<? extends KeyStoreSpi> paramClass2) {
    this.compatModeEnabled = "true".equalsIgnoreCase((String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() { return Security.getProperty("keystore.type.compat"); }
          }));
    if (this.compatModeEnabled) {
      this.primaryType = paramString1;
      this.secondaryType = paramString2;
      this.primaryKeyStore = paramClass1;
      this.secondaryKeyStore = paramClass2;
    } else {
      this.primaryType = paramString1;
      this.secondaryType = null;
      this.primaryKeyStore = paramClass1;
      this.secondaryKeyStore = null;
      if (debug != null)
        debug.println("WARNING: compatibility mode disabled for " + paramString1 + " and " + paramString2 + " keystore types"); 
    } 
  }
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar) throws NoSuchAlgorithmException, UnrecoverableKeyException { return this.keystore.engineGetKey(paramString, paramArrayOfChar); }
  
  public Certificate[] engineGetCertificateChain(String paramString) { return this.keystore.engineGetCertificateChain(paramString); }
  
  public Certificate engineGetCertificate(String paramString) { return this.keystore.engineGetCertificate(paramString); }
  
  public Date engineGetCreationDate(String paramString) { return this.keystore.engineGetCreationDate(paramString); }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate) throws KeyStoreException { this.keystore.engineSetKeyEntry(paramString, paramKey, paramArrayOfChar, paramArrayOfCertificate); }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate) throws KeyStoreException { this.keystore.engineSetKeyEntry(paramString, paramArrayOfByte, paramArrayOfCertificate); }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate) throws KeyStoreException { this.keystore.engineSetCertificateEntry(paramString, paramCertificate); }
  
  public void engineDeleteEntry(String paramString) throws KeyStoreException { this.keystore.engineDeleteEntry(paramString); }
  
  public Enumeration<String> engineAliases() { return this.keystore.engineAliases(); }
  
  public boolean engineContainsAlias(String paramString) { return this.keystore.engineContainsAlias(paramString); }
  
  public int engineSize() { return this.keystore.engineSize(); }
  
  public boolean engineIsKeyEntry(String paramString) { return this.keystore.engineIsKeyEntry(paramString); }
  
  public boolean engineIsCertificateEntry(String paramString) { return this.keystore.engineIsCertificateEntry(paramString); }
  
  public String engineGetCertificateAlias(Certificate paramCertificate) { return this.keystore.engineGetCertificateAlias(paramCertificate); }
  
  public KeyStore.Entry engineGetEntry(String paramString, KeyStore.ProtectionParameter paramProtectionParameter) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException { return this.keystore.engineGetEntry(paramString, paramProtectionParameter); }
  
  public void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter) throws KeyStoreException { this.keystore.engineSetEntry(paramString, paramEntry, paramProtectionParameter); }
  
  public boolean engineEntryInstanceOf(String paramString, Class<? extends KeyStore.Entry> paramClass) { return this.keystore.engineEntryInstanceOf(paramString, paramClass); }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException {
    if (debug != null)
      debug.println("Storing keystore in " + this.type + " format"); 
    this.keystore.engineStore(paramOutputStream, paramArrayOfChar);
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException {
    if (paramInputStream == null || !this.compatModeEnabled) {
      try {
        this.keystore = (KeyStoreSpi)this.primaryKeyStore.newInstance();
      } catch (InstantiationException|IllegalAccessException instantiationException) {}
      this.type = this.primaryType;
      if (debug != null && paramInputStream == null)
        debug.println("Creating a new keystore in " + this.type + " format"); 
      this.keystore.engineLoad(paramInputStream, paramArrayOfChar);
    } else {
      BufferedInputStream bufferedInputStream = new BufferedInputStream(paramInputStream);
      bufferedInputStream.mark(2147483647);
      try {
        this.keystore = (KeyStoreSpi)this.primaryKeyStore.newInstance();
        this.type = this.primaryType;
        this.keystore.engineLoad(bufferedInputStream, paramArrayOfChar);
      } catch (Exception exception) {
        if (exception instanceof IOException && exception.getCause() instanceof UnrecoverableKeyException)
          throw (IOException)exception; 
        try {
          this.keystore = (KeyStoreSpi)this.secondaryKeyStore.newInstance();
          this.type = this.secondaryType;
          bufferedInputStream.reset();
          this.keystore.engineLoad(bufferedInputStream, paramArrayOfChar);
          if (debug != null)
            debug.println("WARNING: switching from " + this.primaryType + " to " + this.secondaryType + " keystore file format has altered the keystore security level"); 
        } catch (InstantiationException|IllegalAccessException instantiationException) {
        
        } catch (IOException|NoSuchAlgorithmException|CertificateException iOException) {
          if (iOException instanceof IOException && iOException.getCause() instanceof UnrecoverableKeyException)
            throw (IOException)iOException; 
          if (exception instanceof IOException)
            throw (IOException)exception; 
          if (exception instanceof CertificateException)
            throw (CertificateException)exception; 
          if (exception instanceof NoSuchAlgorithmException)
            throw (NoSuchAlgorithmException)exception; 
        } 
      } 
      if (debug != null)
        debug.println("Loaded a keystore in " + this.type + " format"); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\KeyStoreDelegator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */