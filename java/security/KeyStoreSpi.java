package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import javax.crypto.SecretKey;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public abstract class KeyStoreSpi {
  public abstract Key engineGetKey(String paramString, char[] paramArrayOfChar) throws NoSuchAlgorithmException, UnrecoverableKeyException;
  
  public abstract Certificate[] engineGetCertificateChain(String paramString);
  
  public abstract Certificate engineGetCertificate(String paramString);
  
  public abstract Date engineGetCreationDate(String paramString);
  
  public abstract void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate) throws KeyStoreException;
  
  public abstract void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate) throws KeyStoreException;
  
  public abstract void engineSetCertificateEntry(String paramString, Certificate paramCertificate) throws KeyStoreException;
  
  public abstract void engineDeleteEntry(String paramString) throws KeyStoreException;
  
  public abstract Enumeration<String> engineAliases();
  
  public abstract boolean engineContainsAlias(String paramString);
  
  public abstract int engineSize();
  
  public abstract boolean engineIsKeyEntry(String paramString);
  
  public abstract boolean engineIsCertificateEntry(String paramString);
  
  public abstract String engineGetCertificateAlias(Certificate paramCertificate);
  
  public abstract void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException;
  
  public void engineStore(KeyStore.LoadStoreParameter paramLoadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException { throw new UnsupportedOperationException(); }
  
  public abstract void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException;
  
  public void engineLoad(KeyStore.LoadStoreParameter paramLoadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
    if (paramLoadStoreParameter == null) {
      engineLoad((InputStream)null, (char[])null);
      return;
    } 
    if (paramLoadStoreParameter instanceof KeyStore.SimpleLoadStoreParameter) {
      char[] arrayOfChar;
      KeyStore.ProtectionParameter protectionParameter = paramLoadStoreParameter.getProtectionParameter();
      if (protectionParameter instanceof KeyStore.PasswordProtection) {
        arrayOfChar = ((KeyStore.PasswordProtection)protectionParameter).getPassword();
      } else if (protectionParameter instanceof KeyStore.CallbackHandlerProtection) {
        CallbackHandler callbackHandler = ((KeyStore.CallbackHandlerProtection)protectionParameter).getCallbackHandler();
        PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);
        try {
          callbackHandler.handle(new Callback[] { passwordCallback });
        } catch (UnsupportedCallbackException unsupportedCallbackException) {
          throw new NoSuchAlgorithmException("Could not obtain password", unsupportedCallbackException);
        } 
        arrayOfChar = passwordCallback.getPassword();
        passwordCallback.clearPassword();
        if (arrayOfChar == null)
          throw new NoSuchAlgorithmException("No password provided"); 
      } else {
        throw new NoSuchAlgorithmException("ProtectionParameter must be PasswordProtection or CallbackHandlerProtection");
      } 
      engineLoad(null, arrayOfChar);
      return;
    } 
    throw new UnsupportedOperationException();
  }
  
  public KeyStore.Entry engineGetEntry(String paramString, KeyStore.ProtectionParameter paramProtectionParameter) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
    if (!engineContainsAlias(paramString))
      return null; 
    if (paramProtectionParameter == null) {
      if (engineIsCertificateEntry(paramString))
        return new KeyStore.TrustedCertificateEntry(engineGetCertificate(paramString)); 
      throw new UnrecoverableKeyException("requested entry requires a password");
    } 
    if (paramProtectionParameter instanceof KeyStore.PasswordProtection) {
      if (engineIsCertificateEntry(paramString))
        throw new UnsupportedOperationException("trusted certificate entries are not password-protected"); 
      if (engineIsKeyEntry(paramString)) {
        KeyStore.PasswordProtection passwordProtection = (KeyStore.PasswordProtection)paramProtectionParameter;
        char[] arrayOfChar = passwordProtection.getPassword();
        Key key = engineGetKey(paramString, arrayOfChar);
        if (key instanceof PrivateKey) {
          Certificate[] arrayOfCertificate = engineGetCertificateChain(paramString);
          return new KeyStore.PrivateKeyEntry((PrivateKey)key, arrayOfCertificate);
        } 
        if (key instanceof SecretKey)
          return new KeyStore.SecretKeyEntry((SecretKey)key); 
      } 
    } 
    throw new UnsupportedOperationException();
  }
  
  public void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter) throws KeyStoreException {
    if (paramProtectionParameter != null && !(paramProtectionParameter instanceof KeyStore.PasswordProtection))
      throw new KeyStoreException("unsupported protection parameter"); 
    KeyStore.PasswordProtection passwordProtection = null;
    if (paramProtectionParameter != null)
      passwordProtection = (KeyStore.PasswordProtection)paramProtectionParameter; 
    if (paramEntry instanceof KeyStore.TrustedCertificateEntry) {
      if (paramProtectionParameter != null && passwordProtection.getPassword() != null)
        throw new KeyStoreException("trusted certificate entries are not password-protected"); 
      KeyStore.TrustedCertificateEntry trustedCertificateEntry = (KeyStore.TrustedCertificateEntry)paramEntry;
      engineSetCertificateEntry(paramString, trustedCertificateEntry.getTrustedCertificate());
      return;
    } 
    if (paramEntry instanceof KeyStore.PrivateKeyEntry) {
      if (passwordProtection == null || passwordProtection.getPassword() == null)
        throw new KeyStoreException("non-null password required to create PrivateKeyEntry"); 
      engineSetKeyEntry(paramString, ((KeyStore.PrivateKeyEntry)paramEntry).getPrivateKey(), passwordProtection.getPassword(), ((KeyStore.PrivateKeyEntry)paramEntry).getCertificateChain());
      return;
    } 
    if (paramEntry instanceof KeyStore.SecretKeyEntry) {
      if (passwordProtection == null || passwordProtection.getPassword() == null)
        throw new KeyStoreException("non-null password required to create SecretKeyEntry"); 
      engineSetKeyEntry(paramString, ((KeyStore.SecretKeyEntry)paramEntry).getSecretKey(), passwordProtection.getPassword(), (Certificate[])null);
      return;
    } 
    throw new KeyStoreException("unsupported entry type: " + paramEntry.getClass().getName());
  }
  
  public boolean engineEntryInstanceOf(String paramString, Class<? extends KeyStore.Entry> paramClass) { return (paramClass == KeyStore.TrustedCertificateEntry.class) ? engineIsCertificateEntry(paramString) : ((paramClass == KeyStore.PrivateKeyEntry.class) ? ((engineIsKeyEntry(paramString) && engineGetCertificate(paramString) != null) ? 1 : 0) : ((paramClass == KeyStore.SecretKeyEntry.class) ? ((engineIsKeyEntry(paramString) && engineGetCertificate(paramString) == null) ? 1 : 0) : 0)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\KeyStoreSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */