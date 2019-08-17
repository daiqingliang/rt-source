package java.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.security.auth.Destroyable;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import sun.security.util.Debug;

public class KeyStore {
  private static final Debug pdebug;
  
  private static final boolean skipDebug = ((pdebug = Debug.getInstance("provider", "Provider")).isOn("engine=") && !Debug.isOn("keystore"));
  
  private static final String KEYSTORE_TYPE = "keystore.type";
  
  private String type;
  
  private Provider provider;
  
  private KeyStoreSpi keyStoreSpi;
  
  private boolean initialized = false;
  
  protected KeyStore(KeyStoreSpi paramKeyStoreSpi, Provider paramProvider, String paramString) {
    this.keyStoreSpi = paramKeyStoreSpi;
    this.provider = paramProvider;
    this.type = paramString;
    if (!skipDebug && pdebug != null)
      pdebug.println("KeyStore." + paramString.toUpperCase() + " type from: " + this.provider.getName()); 
  }
  
  public static KeyStore getInstance(String paramString) throws KeyStoreException {
    try {
      Object[] arrayOfObject = Security.getImpl(paramString, "KeyStore", (String)null);
      return new KeyStore((KeyStoreSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new KeyStoreException(paramString + " not found", noSuchAlgorithmException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new KeyStoreException(paramString + " not found", noSuchProviderException);
    } 
  }
  
  public static KeyStore getInstance(String paramString1, String paramString2) throws KeyStoreException, NoSuchProviderException {
    if (paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException("missing provider"); 
    try {
      Object[] arrayOfObject = Security.getImpl(paramString1, "KeyStore", paramString2);
      return new KeyStore((KeyStoreSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new KeyStoreException(paramString1 + " not found", noSuchAlgorithmException);
    } 
  }
  
  public static KeyStore getInstance(String paramString, Provider paramProvider) throws KeyStoreException {
    if (paramProvider == null)
      throw new IllegalArgumentException("missing provider"); 
    try {
      Object[] arrayOfObject = Security.getImpl(paramString, "KeyStore", paramProvider);
      return new KeyStore((KeyStoreSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new KeyStoreException(paramString + " not found", noSuchAlgorithmException);
    } 
  }
  
  public static final String getDefaultType() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty("keystore.type"); }
        });
    if (str == null)
      str = "jks"; 
    return str;
  }
  
  public final Provider getProvider() { return this.provider; }
  
  public final String getType() { return this.type; }
  
  public final Key getKey(String paramString, char[] paramArrayOfChar) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetKey(paramString, paramArrayOfChar);
  }
  
  public final Certificate[] getCertificateChain(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetCertificateChain(paramString);
  }
  
  public final Certificate getCertificate(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetCertificate(paramString);
  }
  
  public final Date getCreationDate(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetCreationDate(paramString);
  }
  
  public final void setKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    if (paramKey instanceof PrivateKey && (paramArrayOfCertificate == null || paramArrayOfCertificate.length == 0))
      throw new IllegalArgumentException("Private key must be accompanied by certificate chain"); 
    this.keyStoreSpi.engineSetKeyEntry(paramString, paramKey, paramArrayOfChar, paramArrayOfCertificate);
  }
  
  public final void setKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineSetKeyEntry(paramString, paramArrayOfByte, paramArrayOfCertificate);
  }
  
  public final void setCertificateEntry(String paramString, Certificate paramCertificate) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineSetCertificateEntry(paramString, paramCertificate);
  }
  
  public final void deleteEntry(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineDeleteEntry(paramString);
  }
  
  public final Enumeration<String> aliases() throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineAliases();
  }
  
  public final boolean containsAlias(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineContainsAlias(paramString);
  }
  
  public final int size() throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineSize();
  }
  
  public final boolean isKeyEntry(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineIsKeyEntry(paramString);
  }
  
  public final boolean isCertificateEntry(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineIsCertificateEntry(paramString);
  }
  
  public final String getCertificateAlias(Certificate paramCertificate) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetCertificateAlias(paramCertificate);
  }
  
  public final void store(OutputStream paramOutputStream, char[] paramArrayOfChar) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineStore(paramOutputStream, paramArrayOfChar);
  }
  
  public final void store(LoadStoreParameter paramLoadStoreParameter) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineStore(paramLoadStoreParameter);
  }
  
  public final void load(InputStream paramInputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException {
    this.keyStoreSpi.engineLoad(paramInputStream, paramArrayOfChar);
    this.initialized = true;
  }
  
  public final void load(LoadStoreParameter paramLoadStoreParameter) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    this.keyStoreSpi.engineLoad(paramLoadStoreParameter);
    this.initialized = true;
  }
  
  public final Entry getEntry(String paramString, ProtectionParameter paramProtectionParameter) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
    if (paramString == null)
      throw new NullPointerException("invalid null input"); 
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetEntry(paramString, paramProtectionParameter);
  }
  
  public final void setEntry(String paramString, Entry paramEntry, ProtectionParameter paramProtectionParameter) throws KeyStoreException {
    if (paramString == null || paramEntry == null)
      throw new NullPointerException("invalid null input"); 
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineSetEntry(paramString, paramEntry, paramProtectionParameter);
  }
  
  public final boolean entryInstanceOf(String paramString, Class<? extends Entry> paramClass) throws KeyStoreException {
    if (paramString == null || paramClass == null)
      throw new NullPointerException("invalid null input"); 
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineEntryInstanceOf(paramString, paramClass);
  }
  
  public static abstract class Builder {
    static final int MAX_CALLBACK_TRIES = 3;
    
    public abstract KeyStore getKeyStore() throws KeyStoreException;
    
    public abstract KeyStore.ProtectionParameter getProtectionParameter(String param1String) throws KeyStoreException;
    
    public static Builder newInstance(final KeyStore keyStore, final KeyStore.ProtectionParameter protectionParameter) {
      if (param1KeyStore == null || param1ProtectionParameter == null)
        throw new NullPointerException(); 
      if (!param1KeyStore.initialized)
        throw new IllegalArgumentException("KeyStore not initialized"); 
      return new Builder() {
          public KeyStore getKeyStore() throws KeyStoreException {
            this.getCalled = true;
            return keyStore;
          }
          
          public KeyStore.ProtectionParameter getProtectionParameter(String param2String) throws KeyStoreException {
            if (param2String == null)
              throw new NullPointerException(); 
            if (!this.getCalled)
              throw new IllegalStateException("getKeyStore() must be called first"); 
            return protectionParameter;
          }
        };
    }
    
    public static Builder newInstance(String param1String, Provider param1Provider, File param1File, KeyStore.ProtectionParameter param1ProtectionParameter) {
      if (param1String == null || param1File == null || param1ProtectionParameter == null)
        throw new NullPointerException(); 
      if (!(param1ProtectionParameter instanceof KeyStore.PasswordProtection) && !(param1ProtectionParameter instanceof KeyStore.CallbackHandlerProtection))
        throw new IllegalArgumentException("Protection must be PasswordProtection or CallbackHandlerProtection"); 
      if (!param1File.isFile())
        throw new IllegalArgumentException("File does not exist or it does not refer to a normal file: " + param1File); 
      return new FileBuilder(param1String, param1Provider, param1File, param1ProtectionParameter, AccessController.getContext());
    }
    
    public static Builder newInstance(final String type, final Provider provider, final KeyStore.ProtectionParameter protection) {
      if (param1String == null || param1ProtectionParameter == null)
        throw new NullPointerException(); 
      final AccessControlContext context = AccessController.getContext();
      return new Builder() {
          private IOException oldException;
          
          private final PrivilegedExceptionAction<KeyStore> action = new PrivilegedExceptionAction<KeyStore>() {
              public KeyStore run() throws KeyStoreException {
                KeyStore keyStore;
                if (provider == null) {
                  keyStore = KeyStore.getInstance(type);
                } else {
                  keyStore = KeyStore.getInstance(type, provider);
                } 
                KeyStore.SimpleLoadStoreParameter simpleLoadStoreParameter = new KeyStore.SimpleLoadStoreParameter(protection);
                if (!(protection instanceof KeyStore.CallbackHandlerProtection)) {
                  keyStore.load(simpleLoadStoreParameter);
                } else {
                  byte b = 0;
                  while (true) {
                    b++;
                    try {
                      keyStore.load(simpleLoadStoreParameter);
                      break;
                    } catch (IOException iOException) {
                      if (iOException.getCause() instanceof UnrecoverableKeyException) {
                        if (b < 3)
                          continue; 
                        KeyStore.Builder.null.this.oldException = iOException;
                      } 
                      throw iOException;
                    } 
                  } 
                } 
                KeyStore.Builder.null.this.getCalled = true;
                return keyStore;
              }
            };
          
          public KeyStore getKeyStore() throws KeyStoreException {
            if (this.oldException != null)
              throw new KeyStoreException("Previous KeyStore instantiation failed", this.oldException); 
            try {
              return (KeyStore)AccessController.doPrivileged(this.action, context);
            } catch (PrivilegedActionException privilegedActionException) {
              Throwable throwable = privilegedActionException.getCause();
              throw new KeyStoreException("KeyStore instantiation failed", throwable);
            } 
          }
          
          public KeyStore.ProtectionParameter getProtectionParameter(String param2String) throws KeyStoreException {
            if (param2String == null)
              throw new NullPointerException(); 
            if (!this.getCalled)
              throw new IllegalStateException("getKeyStore() must be called first"); 
            return protection;
          }
        };
    }
    
    private static final class FileBuilder extends Builder {
      private final String type;
      
      private final Provider provider;
      
      private final File file;
      
      private KeyStore.ProtectionParameter protection;
      
      private KeyStore.ProtectionParameter keyProtection;
      
      private final AccessControlContext context;
      
      private KeyStore keyStore;
      
      private Throwable oldException;
      
      FileBuilder(String param2String, Provider param2Provider, File param2File, KeyStore.ProtectionParameter param2ProtectionParameter, AccessControlContext param2AccessControlContext) {
        this.type = param2String;
        this.provider = param2Provider;
        this.file = param2File;
        this.protection = param2ProtectionParameter;
        this.context = param2AccessControlContext;
      }
      
      public KeyStore getKeyStore() throws KeyStoreException {
        if (this.keyStore != null)
          return this.keyStore; 
        if (this.oldException != null)
          throw new KeyStoreException("Previous KeyStore instantiation failed", this.oldException); 
        PrivilegedExceptionAction<KeyStore> privilegedExceptionAction = new PrivilegedExceptionAction<KeyStore>() {
            public KeyStore run() throws KeyStoreException {
              if (!(KeyStore.Builder.FileBuilder.this.protection instanceof KeyStore.CallbackHandlerProtection))
                return run0(); 
              byte b = 0;
              while (true) {
                b++;
                try {
                  return run0();
                } catch (IOException iOException) {
                  if (b < 3 && iOException.getCause() instanceof UnrecoverableKeyException)
                    continue; 
                  break;
                } 
              } 
              throw iOException;
            }
            
            public KeyStore run0() throws KeyStoreException {
              KeyStore keyStore;
              if (KeyStore.Builder.FileBuilder.this.provider == null) {
                keyStore = KeyStore.getInstance(KeyStore.Builder.FileBuilder.this.type);
              } else {
                keyStore = KeyStore.getInstance(KeyStore.Builder.FileBuilder.this.type, KeyStore.Builder.FileBuilder.this.provider);
              } 
              fileInputStream = null;
              char[] arrayOfChar = null;
              try {
                fileInputStream = new FileInputStream(KeyStore.Builder.FileBuilder.this.file);
                if (KeyStore.Builder.FileBuilder.this.protection instanceof KeyStore.PasswordProtection) {
                  arrayOfChar = ((KeyStore.PasswordProtection)KeyStore.Builder.FileBuilder.this.protection).getPassword();
                  KeyStore.Builder.FileBuilder.this.keyProtection = KeyStore.Builder.FileBuilder.this.protection;
                } else {
                  CallbackHandler callbackHandler = ((KeyStore.CallbackHandlerProtection)KeyStore.Builder.FileBuilder.this.protection).getCallbackHandler();
                  PasswordCallback passwordCallback = new PasswordCallback("Password for keystore " + KeyStore.Builder.FileBuilder.this.file.getName(), false);
                  callbackHandler.handle(new Callback[] { passwordCallback });
                  arrayOfChar = passwordCallback.getPassword();
                  if (arrayOfChar == null)
                    throw new KeyStoreException("No password provided"); 
                  passwordCallback.clearPassword();
                  KeyStore.Builder.FileBuilder.this.keyProtection = new KeyStore.PasswordProtection(arrayOfChar);
                } 
                keyStore.load(fileInputStream, arrayOfChar);
                return keyStore;
              } finally {
                if (fileInputStream != null)
                  fileInputStream.close(); 
              } 
            }
          };
        try {
          this.keyStore = (KeyStore)AccessController.doPrivileged(privilegedExceptionAction, this.context);
          return this.keyStore;
        } catch (PrivilegedActionException privilegedActionException) {
          this.oldException = privilegedActionException.getCause();
          throw new KeyStoreException("KeyStore instantiation failed", this.oldException);
        } 
      }
      
      public KeyStore.ProtectionParameter getProtectionParameter(String param2String) throws KeyStoreException {
        if (param2String == null)
          throw new NullPointerException(); 
        if (this.keyStore == null)
          throw new IllegalStateException("getKeyStore() must be called first"); 
        return this.keyProtection;
      }
    }
  }
  
  public static class CallbackHandlerProtection implements ProtectionParameter {
    private final CallbackHandler handler;
    
    public CallbackHandlerProtection(CallbackHandler param1CallbackHandler) {
      if (param1CallbackHandler == null)
        throw new NullPointerException("handler must not be null"); 
      this.handler = param1CallbackHandler;
    }
    
    public CallbackHandler getCallbackHandler() { return this.handler; }
  }
  
  public static interface Entry {
    default Set<Attribute> getAttributes() { return Collections.emptySet(); }
    
    public static interface Attribute {
      String getName();
      
      String getValue();
    }
  }
  
  public static interface LoadStoreParameter {
    KeyStore.ProtectionParameter getProtectionParameter();
  }
  
  public static class PasswordProtection implements ProtectionParameter, Destroyable {
    private final char[] password;
    
    private final String protectionAlgorithm;
    
    private final AlgorithmParameterSpec protectionParameters;
    
    public PasswordProtection(char[] param1ArrayOfChar) {
      this.password = (param1ArrayOfChar == null) ? null : (char[])param1ArrayOfChar.clone();
      this.protectionAlgorithm = null;
      this.protectionParameters = null;
    }
    
    public PasswordProtection(char[] param1ArrayOfChar, String param1String, AlgorithmParameterSpec param1AlgorithmParameterSpec) {
      if (param1String == null)
        throw new NullPointerException("invalid null input"); 
      this.password = (param1ArrayOfChar == null) ? null : (char[])param1ArrayOfChar.clone();
      this.protectionAlgorithm = param1String;
      this.protectionParameters = param1AlgorithmParameterSpec;
    }
    
    public String getProtectionAlgorithm() { return this.protectionAlgorithm; }
    
    public AlgorithmParameterSpec getProtectionParameters() { return this.protectionParameters; }
    
    public char[] getPassword() {
      if (this.destroyed)
        throw new IllegalStateException("password has been cleared"); 
      return this.password;
    }
    
    public void destroy() {
      this.destroyed = true;
      if (this.password != null)
        Arrays.fill(this.password, ' '); 
    }
    
    public boolean isDestroyed() { return this.destroyed; }
  }
  
  public static final class PrivateKeyEntry implements Entry {
    private final PrivateKey privKey;
    
    private final Certificate[] chain;
    
    private final Set<KeyStore.Entry.Attribute> attributes;
    
    public PrivateKeyEntry(PrivateKey param1PrivateKey, Certificate[] param1ArrayOfCertificate) { this(param1PrivateKey, param1ArrayOfCertificate, Collections.emptySet()); }
    
    public PrivateKeyEntry(PrivateKey param1PrivateKey, Certificate[] param1ArrayOfCertificate, Set<KeyStore.Entry.Attribute> param1Set) {
      if (param1PrivateKey == null || param1ArrayOfCertificate == null || param1Set == null)
        throw new NullPointerException("invalid null input"); 
      if (param1ArrayOfCertificate.length == 0)
        throw new IllegalArgumentException("invalid zero-length input chain"); 
      Certificate[] arrayOfCertificate = (Certificate[])param1ArrayOfCertificate.clone();
      String str = arrayOfCertificate[0].getType();
      for (byte b = 1; b < arrayOfCertificate.length; b++) {
        if (!str.equals(arrayOfCertificate[b].getType()))
          throw new IllegalArgumentException("chain does not contain certificates of the same type"); 
      } 
      if (!param1PrivateKey.getAlgorithm().equals(arrayOfCertificate[0].getPublicKey().getAlgorithm()))
        throw new IllegalArgumentException("private key algorithm does not match algorithm of public key in end entity certificate (at index 0)"); 
      this.privKey = param1PrivateKey;
      if (arrayOfCertificate[0] instanceof java.security.cert.X509Certificate && !(arrayOfCertificate instanceof java.security.cert.X509Certificate[])) {
        this.chain = new java.security.cert.X509Certificate[arrayOfCertificate.length];
        System.arraycopy(arrayOfCertificate, 0, this.chain, 0, arrayOfCertificate.length);
      } else {
        this.chain = arrayOfCertificate;
      } 
      this.attributes = Collections.unmodifiableSet(new HashSet(param1Set));
    }
    
    public PrivateKey getPrivateKey() { return this.privKey; }
    
    public Certificate[] getCertificateChain() { return (Certificate[])this.chain.clone(); }
    
    public Certificate getCertificate() { return this.chain[0]; }
    
    public Set<KeyStore.Entry.Attribute> getAttributes() { return this.attributes; }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Private key entry and certificate chain with " + this.chain.length + " elements:\r\n");
      for (Certificate certificate : this.chain) {
        stringBuilder.append(certificate);
        stringBuilder.append("\r\n");
      } 
      return stringBuilder.toString();
    }
  }
  
  public static interface ProtectionParameter {}
  
  public static final class SecretKeyEntry implements Entry {
    private final SecretKey sKey;
    
    private final Set<KeyStore.Entry.Attribute> attributes;
    
    public SecretKeyEntry(SecretKey param1SecretKey) {
      if (param1SecretKey == null)
        throw new NullPointerException("invalid null input"); 
      this.sKey = param1SecretKey;
      this.attributes = Collections.emptySet();
    }
    
    public SecretKeyEntry(SecretKey param1SecretKey, Set<KeyStore.Entry.Attribute> param1Set) {
      if (param1SecretKey == null || param1Set == null)
        throw new NullPointerException("invalid null input"); 
      this.sKey = param1SecretKey;
      this.attributes = Collections.unmodifiableSet(new HashSet(param1Set));
    }
    
    public SecretKey getSecretKey() { return this.sKey; }
    
    public Set<KeyStore.Entry.Attribute> getAttributes() { return this.attributes; }
    
    public String toString() { return "Secret key entry with algorithm " + this.sKey.getAlgorithm(); }
  }
  
  static class SimpleLoadStoreParameter implements LoadStoreParameter {
    private final KeyStore.ProtectionParameter protection;
    
    SimpleLoadStoreParameter(KeyStore.ProtectionParameter param1ProtectionParameter) { this.protection = param1ProtectionParameter; }
    
    public KeyStore.ProtectionParameter getProtectionParameter() { return this.protection; }
  }
  
  public static final class TrustedCertificateEntry implements Entry {
    private final Certificate cert;
    
    private final Set<KeyStore.Entry.Attribute> attributes;
    
    public TrustedCertificateEntry(Certificate param1Certificate) {
      if (param1Certificate == null)
        throw new NullPointerException("invalid null input"); 
      this.cert = param1Certificate;
      this.attributes = Collections.emptySet();
    }
    
    public TrustedCertificateEntry(Certificate param1Certificate, Set<KeyStore.Entry.Attribute> param1Set) {
      if (param1Certificate == null || param1Set == null)
        throw new NullPointerException("invalid null input"); 
      this.cert = param1Certificate;
      this.attributes = Collections.unmodifiableSet(new HashSet(param1Set));
    }
    
    public Certificate getTrustedCertificate() { return this.cert; }
    
    public Set<KeyStore.Entry.Attribute> getAttributes() { return this.attributes; }
    
    public String toString() { return "Trusted certificate entry:\r\n" + this.cert.toString(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\KeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */