package com.sun.security.auth.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.AuthProvider;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;
import jdk.Exported;
import sun.security.util.Password;

@Exported
public class KeyStoreLoginModule implements LoginModule {
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
        public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.AuthResources"); }
      });
  
  private static final int UNINITIALIZED = 0;
  
  private static final int INITIALIZED = 1;
  
  private static final int AUTHENTICATED = 2;
  
  private static final int LOGGED_IN = 3;
  
  private static final int PROTECTED_PATH = 0;
  
  private static final int TOKEN = 1;
  
  private static final int NORMAL = 2;
  
  private static final String NONE = "NONE";
  
  private static final String P11KEYSTORE = "PKCS11";
  
  private static final TextOutputCallback bannerCallback = new TextOutputCallback(0, rb.getString("Please.enter.keystore.information"));
  
  private final ConfirmationCallback confirmationCallback = new ConfirmationCallback(0, 2, 3);
  
  private Subject subject;
  
  private CallbackHandler callbackHandler;
  
  private Map<String, Object> sharedState;
  
  private Map<String, ?> options;
  
  private char[] keyStorePassword;
  
  private char[] privateKeyPassword;
  
  private KeyStore keyStore;
  
  private String keyStoreURL;
  
  private String keyStoreType;
  
  private String keyStoreProvider;
  
  private String keyStoreAlias;
  
  private String keyStorePasswordURL;
  
  private String privateKeyPasswordURL;
  
  private boolean debug;
  
  private X500Principal principal;
  
  private Certificate[] fromKeyStore;
  
  private CertPath certP = null;
  
  private X500PrivateCredential privateCredential;
  
  private int status = 0;
  
  private boolean nullStream = false;
  
  private boolean token = false;
  
  private boolean protectedPath = false;
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2) {
    this.subject = paramSubject;
    this.callbackHandler = paramCallbackHandler;
    this.sharedState = paramMap1;
    this.options = paramMap2;
    processOptions();
    this.status = 1;
  }
  
  private void processOptions() {
    this.keyStoreURL = (String)this.options.get("keyStoreURL");
    if (this.keyStoreURL == null) {
      this.keyStoreURL = "file:" + System.getProperty("user.home").replace(File.separatorChar, '/') + '/' + ".keystore";
    } else if ("NONE".equals(this.keyStoreURL)) {
      this.nullStream = true;
    } 
    this.keyStoreType = (String)this.options.get("keyStoreType");
    if (this.keyStoreType == null)
      this.keyStoreType = KeyStore.getDefaultType(); 
    if ("PKCS11".equalsIgnoreCase(this.keyStoreType))
      this.token = true; 
    this.keyStoreProvider = (String)this.options.get("keyStoreProvider");
    this.keyStoreAlias = (String)this.options.get("keyStoreAlias");
    this.keyStorePasswordURL = (String)this.options.get("keyStorePasswordURL");
    this.privateKeyPasswordURL = (String)this.options.get("privateKeyPasswordURL");
    this.protectedPath = "true".equalsIgnoreCase((String)this.options.get("protected"));
    this.debug = "true".equalsIgnoreCase((String)this.options.get("debug"));
    if (this.debug) {
      debugPrint(null);
      debugPrint("keyStoreURL=" + this.keyStoreURL);
      debugPrint("keyStoreType=" + this.keyStoreType);
      debugPrint("keyStoreProvider=" + this.keyStoreProvider);
      debugPrint("keyStoreAlias=" + this.keyStoreAlias);
      debugPrint("keyStorePasswordURL=" + this.keyStorePasswordURL);
      debugPrint("privateKeyPasswordURL=" + this.privateKeyPasswordURL);
      debugPrint("protectedPath=" + this.protectedPath);
      debugPrint(null);
    } 
  }
  
  public boolean login() throws LoginException {
    switch (this.status) {
      default:
        throw new LoginException("The login module is not initialized");
      case 1:
      case 2:
        if (this.token && !this.nullStream)
          throw new LoginException("if keyStoreType is PKCS11 then keyStoreURL must be NONE"); 
        if (this.token && this.privateKeyPasswordURL != null)
          throw new LoginException("if keyStoreType is PKCS11 then privateKeyPasswordURL must not be specified"); 
        if (this.protectedPath && (this.keyStorePasswordURL != null || this.privateKeyPasswordURL != null))
          throw new LoginException("if protected is true then keyStorePasswordURL and privateKeyPasswordURL must not be specified"); 
        if (this.protectedPath) {
          getAliasAndPasswords(0);
        } else if (this.token) {
          getAliasAndPasswords(1);
        } else {
          getAliasAndPasswords(2);
        } 
        try {
          getKeyStoreInfo();
        } finally {
          if (this.privateKeyPassword != null && this.privateKeyPassword != this.keyStorePassword) {
            Arrays.fill(this.privateKeyPassword, false);
            this.privateKeyPassword = null;
          } 
          if (this.keyStorePassword != null) {
            Arrays.fill(this.keyStorePassword, false);
            this.keyStorePassword = null;
          } 
        } 
        this.status = 2;
        return true;
      case 3:
        break;
    } 
    return true;
  }
  
  private void getAliasAndPasswords(int paramInt) throws LoginException {
    if (this.callbackHandler == null) {
      switch (paramInt) {
        case 0:
          checkAlias();
          break;
        case 1:
          checkAlias();
          checkStorePass();
          break;
        case 2:
          checkAlias();
          checkStorePass();
          checkKeyPass();
          break;
      } 
    } else {
      NameCallback nameCallback;
      if (this.keyStoreAlias == null || this.keyStoreAlias.length() == 0) {
        nameCallback = new NameCallback(rb.getString("Keystore.alias."));
      } else {
        nameCallback = new NameCallback(rb.getString("Keystore.alias."), this.keyStoreAlias);
      } 
      PasswordCallback passwordCallback1 = null;
      PasswordCallback passwordCallback2 = null;
      switch (paramInt) {
        case 2:
          passwordCallback2 = new PasswordCallback(rb.getString("Private.key.password.optional."), false);
        case 1:
          passwordCallback1 = new PasswordCallback(rb.getString("Keystore.password."), false);
          break;
      } 
      prompt(nameCallback, passwordCallback1, passwordCallback2);
    } 
    if (this.debug)
      debugPrint("alias=" + this.keyStoreAlias); 
  }
  
  private void checkAlias() {
    if (this.keyStoreAlias == null)
      throw new LoginException("Need to specify an alias option to use KeyStoreLoginModule non-interactively."); 
  }
  
  private void checkStorePass() {
    if (this.keyStorePasswordURL == null)
      throw new LoginException("Need to specify keyStorePasswordURL option to use KeyStoreLoginModule non-interactively."); 
    inputStream = null;
    try {
      inputStream = (new URL(this.keyStorePasswordURL)).openStream();
      this.keyStorePassword = Password.readPassword(inputStream);
    } catch (IOException iOException) {
      LoginException loginException = new LoginException("Problem accessing keystore password \"" + this.keyStorePasswordURL + "\"");
      loginException.initCause(iOException);
      throw loginException;
    } finally {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (IOException iOException) {
          LoginException loginException = new LoginException("Problem closing the keystore password stream");
          loginException.initCause(iOException);
          throw loginException;
        }  
    } 
  }
  
  private void checkKeyPass() {
    if (this.privateKeyPasswordURL == null) {
      this.privateKeyPassword = this.keyStorePassword;
    } else {
      inputStream = null;
      try {
        inputStream = (new URL(this.privateKeyPasswordURL)).openStream();
        this.privateKeyPassword = Password.readPassword(inputStream);
      } catch (IOException iOException) {
        LoginException loginException = new LoginException("Problem accessing private key password \"" + this.privateKeyPasswordURL + "\"");
        loginException.initCause(iOException);
        throw loginException;
      } finally {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (IOException iOException) {
            LoginException loginException = new LoginException("Problem closing the private key password stream");
            loginException.initCause(iOException);
            throw loginException;
          }  
      } 
    } 
  }
  
  private void prompt(NameCallback paramNameCallback, PasswordCallback paramPasswordCallback1, PasswordCallback paramPasswordCallback2) throws LoginException {
    if (paramPasswordCallback1 == null) {
      try {
        this.callbackHandler.handle(new Callback[] { bannerCallback, paramNameCallback, this.confirmationCallback });
      } catch (IOException iOException) {
        LoginException loginException = new LoginException("Problem retrieving keystore alias");
        loginException.initCause(iOException);
        throw loginException;
      } catch (UnsupportedCallbackException unsupportedCallbackException) {
        throw new LoginException("Error: " + unsupportedCallbackException.getCallback().toString() + " is not available to retrieve authentication  information from the user");
      } 
      int i = this.confirmationCallback.getSelectedIndex();
      if (i == 2)
        throw new LoginException("Login cancelled"); 
      saveAlias(paramNameCallback);
    } else if (paramPasswordCallback2 == null) {
      try {
        this.callbackHandler.handle(new Callback[] { bannerCallback, paramNameCallback, paramPasswordCallback1, this.confirmationCallback });
      } catch (IOException iOException) {
        LoginException loginException = new LoginException("Problem retrieving keystore alias and password");
        loginException.initCause(iOException);
        throw loginException;
      } catch (UnsupportedCallbackException unsupportedCallbackException) {
        throw new LoginException("Error: " + unsupportedCallbackException.getCallback().toString() + " is not available to retrieve authentication  information from the user");
      } 
      int i = this.confirmationCallback.getSelectedIndex();
      if (i == 2)
        throw new LoginException("Login cancelled"); 
      saveAlias(paramNameCallback);
      saveStorePass(paramPasswordCallback1);
    } else {
      try {
        this.callbackHandler.handle(new Callback[] { bannerCallback, paramNameCallback, paramPasswordCallback1, paramPasswordCallback2, this.confirmationCallback });
      } catch (IOException iOException) {
        LoginException loginException = new LoginException("Problem retrieving keystore alias and passwords");
        loginException.initCause(iOException);
        throw loginException;
      } catch (UnsupportedCallbackException unsupportedCallbackException) {
        throw new LoginException("Error: " + unsupportedCallbackException.getCallback().toString() + " is not available to retrieve authentication  information from the user");
      } 
      int i = this.confirmationCallback.getSelectedIndex();
      if (i == 2)
        throw new LoginException("Login cancelled"); 
      saveAlias(paramNameCallback);
      saveStorePass(paramPasswordCallback1);
      saveKeyPass(paramPasswordCallback2);
    } 
  }
  
  private void saveAlias(NameCallback paramNameCallback) { this.keyStoreAlias = paramNameCallback.getName(); }
  
  private void saveStorePass(PasswordCallback paramPasswordCallback) {
    this.keyStorePassword = paramPasswordCallback.getPassword();
    if (this.keyStorePassword == null)
      this.keyStorePassword = new char[0]; 
    paramPasswordCallback.clearPassword();
  }
  
  private void saveKeyPass(PasswordCallback paramPasswordCallback) {
    this.privateKeyPassword = paramPasswordCallback.getPassword();
    if (this.privateKeyPassword == null || this.privateKeyPassword.length == 0)
      this.privateKeyPassword = this.keyStorePassword; 
    paramPasswordCallback.clearPassword();
  }
  
  private void getKeyStoreInfo() {
    try {
      if (this.keyStoreProvider == null) {
        this.keyStore = KeyStore.getInstance(this.keyStoreType);
      } else {
        this.keyStore = KeyStore.getInstance(this.keyStoreType, this.keyStoreProvider);
      } 
    } catch (KeyStoreException keyStoreException) {
      LoginException loginException = new LoginException("The specified keystore type was not available");
      loginException.initCause(keyStoreException);
      throw loginException;
    } catch (NoSuchProviderException noSuchProviderException) {
      LoginException loginException = new LoginException("The specified keystore provider was not available");
      loginException.initCause(noSuchProviderException);
      throw loginException;
    } 
    inputStream = null;
    try {
      if (this.nullStream) {
        this.keyStore.load(null, this.keyStorePassword);
      } else {
        inputStream = (new URL(this.keyStoreURL)).openStream();
        this.keyStore.load(inputStream, this.keyStorePassword);
      } 
    } catch (MalformedURLException malformedURLException) {
      LoginException loginException = new LoginException("Incorrect keyStoreURL option");
      loginException.initCause(malformedURLException);
      throw loginException;
    } catch (GeneralSecurityException generalSecurityException) {
      LoginException loginException = new LoginException("Error initializing keystore");
      loginException.initCause(generalSecurityException);
      throw loginException;
    } catch (IOException iOException) {
      LoginException loginException = new LoginException("Error initializing keystore");
      loginException.initCause(iOException);
      throw loginException;
    } finally {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (IOException iOException) {
          LoginException loginException = new LoginException("Error initializing keystore");
          loginException.initCause(iOException);
          throw loginException;
        }  
    } 
    try {
      this.fromKeyStore = this.keyStore.getCertificateChain(this.keyStoreAlias);
      if (this.fromKeyStore == null || this.fromKeyStore.length == 0 || !(this.fromKeyStore[0] instanceof X509Certificate))
        throw new FailedLoginException("Unable to find X.509 certificate chain in keystore"); 
      LinkedList linkedList = new LinkedList();
      for (byte b = 0; b < this.fromKeyStore.length; b++)
        linkedList.add(this.fromKeyStore[b]); 
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      this.certP = certificateFactory.generateCertPath(linkedList);
    } catch (KeyStoreException keyStoreException) {
      LoginException loginException = new LoginException("Error using keystore");
      loginException.initCause(keyStoreException);
      throw loginException;
    } catch (CertificateException certificateException) {
      LoginException loginException = new LoginException("Error: X.509 Certificate type unavailable");
      loginException.initCause(certificateException);
      throw loginException;
    } 
    try {
      X509Certificate x509Certificate = (X509Certificate)this.fromKeyStore[0];
      this.principal = new X500Principal(x509Certificate.getSubjectDN().getName());
      Key key = this.keyStore.getKey(this.keyStoreAlias, this.privateKeyPassword);
      if (key == null || !(key instanceof PrivateKey))
        throw new FailedLoginException("Unable to recover key from keystore"); 
      this.privateCredential = new X500PrivateCredential(x509Certificate, (PrivateKey)key, this.keyStoreAlias);
    } catch (KeyStoreException keyStoreException) {
      LoginException loginException = new LoginException("Error using keystore");
      loginException.initCause(keyStoreException);
      throw loginException;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      LoginException loginException = new LoginException("Error using keystore");
      loginException.initCause(noSuchAlgorithmException);
      throw loginException;
    } catch (UnrecoverableKeyException unrecoverableKeyException) {
      FailedLoginException failedLoginException = new FailedLoginException("Unable to recover key from keystore");
      failedLoginException.initCause(unrecoverableKeyException);
      throw failedLoginException;
    } 
    if (this.debug)
      debugPrint("principal=" + this.principal + "\n certificate=" + this.privateCredential.getCertificate() + "\n alias =" + this.privateCredential.getAlias()); 
  }
  
  public boolean commit() throws LoginException {
    switch (this.status) {
      default:
        throw new LoginException("The login module is not initialized");
      case 1:
        logoutInternal();
        throw new LoginException("Authentication failed");
      case 2:
        if (commitInternal())
          return true; 
        logoutInternal();
        throw new LoginException("Unable to retrieve certificates");
      case 3:
        break;
    } 
    return true;
  }
  
  private boolean commitInternal() throws LoginException {
    if (this.subject.isReadOnly())
      throw new LoginException("Subject is set readonly"); 
    this.subject.getPrincipals().add(this.principal);
    this.subject.getPublicCredentials().add(this.certP);
    this.subject.getPrivateCredentials().add(this.privateCredential);
    this.status = 3;
    return true;
  }
  
  public boolean abort() throws LoginException {
    switch (this.status) {
      default:
        return false;
      case 1:
        return false;
      case 2:
        logoutInternal();
        return true;
      case 3:
        break;
    } 
    logoutInternal();
    return true;
  }
  
  public boolean logout() throws LoginException {
    if (this.debug)
      debugPrint("Entering logout " + this.status); 
    switch (this.status) {
      case 0:
        throw new LoginException("The login module is not initialized");
      default:
        return false;
      case 3:
        break;
    } 
    logoutInternal();
    return true;
  }
  
  private void logoutInternal() {
    if (this.debug)
      debugPrint("Entering logoutInternal"); 
    LoginException loginException = null;
    Provider provider = this.keyStore.getProvider();
    if (provider instanceof AuthProvider) {
      AuthProvider authProvider = (AuthProvider)provider;
      try {
        authProvider.logout();
        if (this.debug)
          debugPrint("logged out of KeyStore AuthProvider"); 
      } catch (LoginException loginException1) {
        loginException = loginException1;
      } 
    } 
    if (this.subject.isReadOnly()) {
      this.principal = null;
      this.certP = null;
      this.status = 1;
      for (Object object : this.subject.getPrivateCredentials()) {
        if (this.privateCredential.equals(object)) {
          this.privateCredential = null;
          try {
            ((Destroyable)object).destroy();
            if (this.debug)
              debugPrint("Destroyed private credential, " + object.getClass().getName()); 
            break;
          } catch (DestroyFailedException destroyFailedException) {
            LoginException loginException1 = new LoginException("Unable to destroy private credential, " + object.getClass().getName());
            loginException1.initCause(destroyFailedException);
            throw loginException1;
          } 
        } 
      } 
      throw new LoginException("Unable to remove Principal (X500Principal ) and public credential (certificatepath) from read-only Subject");
    } 
    if (this.principal != null) {
      this.subject.getPrincipals().remove(this.principal);
      this.principal = null;
    } 
    if (this.certP != null) {
      this.subject.getPublicCredentials().remove(this.certP);
      this.certP = null;
    } 
    if (this.privateCredential != null) {
      this.subject.getPrivateCredentials().remove(this.privateCredential);
      this.privateCredential = null;
    } 
    if (loginException != null)
      throw loginException; 
    this.status = 1;
  }
  
  private void debugPrint(String paramString) {
    if (paramString == null) {
      System.err.println();
    } else {
      System.err.println("Debug KeyStoreLoginModule: " + paramString);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\module\KeyStoreLoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */