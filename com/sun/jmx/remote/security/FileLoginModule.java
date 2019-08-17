package com.sun.jmx.remote.security;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import javax.management.remote.JMXPrincipal;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class FileLoginModule implements LoginModule {
  private static final String DEFAULT_PASSWORD_FILE_NAME = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + File.separatorChar + "lib" + File.separatorChar + "management" + File.separatorChar + "jmxremote.password";
  
  private static final String USERNAME_KEY = "javax.security.auth.login.name";
  
  private static final String PASSWORD_KEY = "javax.security.auth.login.password";
  
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "FileLoginModule");
  
  private boolean useFirstPass = false;
  
  private boolean tryFirstPass = false;
  
  private boolean storePass = false;
  
  private boolean clearPass = false;
  
  private boolean succeeded = false;
  
  private boolean commitSucceeded = false;
  
  private String username;
  
  private char[] password;
  
  private JMXPrincipal user;
  
  private Subject subject;
  
  private CallbackHandler callbackHandler;
  
  private Map<String, Object> sharedState;
  
  private Map<String, ?> options;
  
  private String passwordFile;
  
  private String passwordFileDisplayName;
  
  private boolean userSuppliedPasswordFile;
  
  private boolean hasJavaHomePermission;
  
  private Properties userCredentials;
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2) {
    this.subject = paramSubject;
    this.callbackHandler = paramCallbackHandler;
    this.sharedState = (Map)Util.cast(paramMap1);
    this.options = paramMap2;
    this.tryFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("tryFirstPass"));
    this.useFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("useFirstPass"));
    this.storePass = "true".equalsIgnoreCase((String)paramMap2.get("storePass"));
    this.clearPass = "true".equalsIgnoreCase((String)paramMap2.get("clearPass"));
    this.passwordFile = (String)paramMap2.get("passwordFile");
    this.passwordFileDisplayName = this.passwordFile;
    this.userSuppliedPasswordFile = true;
    if (this.passwordFile == null) {
      this.passwordFile = DEFAULT_PASSWORD_FILE_NAME;
      this.userSuppliedPasswordFile = false;
      try {
        System.getProperty("java.home");
        this.hasJavaHomePermission = true;
        this.passwordFileDisplayName = this.passwordFile;
      } catch (SecurityException securityException) {
        this.hasJavaHomePermission = false;
        this.passwordFileDisplayName = "jmxremote.password";
      } 
    } 
  }
  
  public boolean login() throws LoginException {
    try {
      loadPasswordFile();
    } catch (IOException iOException) {
      LoginException loginException = new LoginException("Error: unable to load the password file: " + this.passwordFileDisplayName);
      throw (LoginException)EnvHelp.initCause(loginException, iOException);
    } 
    if (this.userCredentials == null)
      throw new LoginException("Error: unable to locate the users' credentials."); 
    if (logger.debugOn())
      logger.debug("login", "Using password file: " + this.passwordFileDisplayName); 
    if (this.tryFirstPass) {
      try {
        attemptAuthentication(true);
        this.succeeded = true;
        if (logger.debugOn())
          logger.debug("login", "Authentication using cached password has succeeded"); 
        return true;
      } catch (LoginException loginException) {
        cleanState();
        logger.debug("login", "Authentication using cached password has failed");
      } 
    } else if (this.useFirstPass) {
      try {
        attemptAuthentication(true);
        this.succeeded = true;
        if (logger.debugOn())
          logger.debug("login", "Authentication using cached password has succeeded"); 
        return true;
      } catch (LoginException loginException) {
        cleanState();
        logger.debug("login", "Authentication using cached password has failed");
        throw loginException;
      } 
    } 
    if (logger.debugOn())
      logger.debug("login", "Acquiring password"); 
    try {
      attemptAuthentication(false);
      this.succeeded = true;
      if (logger.debugOn())
        logger.debug("login", "Authentication has succeeded"); 
      return true;
    } catch (LoginException loginException) {
      cleanState();
      logger.debug("login", "Authentication has failed");
      throw loginException;
    } 
  }
  
  public boolean commit() throws LoginException {
    if (!this.succeeded)
      return false; 
    if (this.subject.isReadOnly()) {
      cleanState();
      throw new LoginException("Subject is read-only");
    } 
    if (!this.subject.getPrincipals().contains(this.user))
      this.subject.getPrincipals().add(this.user); 
    if (logger.debugOn())
      logger.debug("commit", "Authentication has completed successfully"); 
    cleanState();
    this.commitSucceeded = true;
    return true;
  }
  
  public boolean abort() throws LoginException {
    if (logger.debugOn())
      logger.debug("abort", "Authentication has not completed successfully"); 
    if (!this.succeeded)
      return false; 
    if (this.succeeded == true && !this.commitSucceeded) {
      this.succeeded = false;
      cleanState();
      this.user = null;
    } else {
      logout();
    } 
    return true;
  }
  
  public boolean logout() throws LoginException {
    if (this.subject.isReadOnly()) {
      cleanState();
      throw new LoginException("Subject is read-only");
    } 
    this.subject.getPrincipals().remove(this.user);
    cleanState();
    this.succeeded = false;
    this.commitSucceeded = false;
    this.user = null;
    if (logger.debugOn())
      logger.debug("logout", "Subject is being logged out"); 
    return true;
  }
  
  private void attemptAuthentication(boolean paramBoolean) throws LoginException {
    getUsernamePassword(paramBoolean);
    String str;
    if ((str = this.userCredentials.getProperty(this.username)) == null || !str.equals(new String(this.password))) {
      if (logger.debugOn())
        logger.debug("login", "Invalid username or password"); 
      throw new FailedLoginException("Invalid username or password");
    } 
    if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
      this.sharedState.put("javax.security.auth.login.name", this.username);
      this.sharedState.put("javax.security.auth.login.password", this.password);
    } 
    this.user = new JMXPrincipal(this.username);
    if (logger.debugOn())
      logger.debug("login", "User '" + this.username + "' successfully validated"); 
  }
  
  private void loadPasswordFile() {
    try {
      fileInputStream = new FileInputStream(this.passwordFile);
    } catch (SecurityException securityException) {
      if (this.userSuppliedPasswordFile || this.hasJavaHomePermission)
        throw securityException; 
      FilePermission filePermission = new FilePermission(this.passwordFileDisplayName, "read");
      AccessControlException accessControlException = new AccessControlException("access denied " + filePermission.toString());
      accessControlException.setStackTrace(securityException.getStackTrace());
      throw accessControlException;
    } 
    try {
      bufferedInputStream = new BufferedInputStream(fileInputStream);
      try {
        this.userCredentials = new Properties();
        this.userCredentials.load(bufferedInputStream);
      } finally {
        bufferedInputStream.close();
      } 
    } finally {
      fileInputStream.close();
    } 
  }
  
  private void getUsernamePassword(boolean paramBoolean) throws LoginException {
    if (paramBoolean) {
      this.username = (String)this.sharedState.get("javax.security.auth.login.name");
      this.password = (char[])this.sharedState.get("javax.security.auth.login.password");
      return;
    } 
    if (this.callbackHandler == null)
      throw new LoginException("Error: no CallbackHandler available to garner authentication information from the user"); 
    Callback[] arrayOfCallback = new Callback[2];
    arrayOfCallback[0] = new NameCallback("username");
    arrayOfCallback[1] = new PasswordCallback("password", false);
    try {
      this.callbackHandler.handle(arrayOfCallback);
      this.username = ((NameCallback)arrayOfCallback[0]).getName();
      char[] arrayOfChar = ((PasswordCallback)arrayOfCallback[1]).getPassword();
      this.password = new char[arrayOfChar.length];
      System.arraycopy(arrayOfChar, 0, this.password, 0, arrayOfChar.length);
      ((PasswordCallback)arrayOfCallback[1]).clearPassword();
    } catch (IOException iOException) {
      LoginException loginException = new LoginException(iOException.toString());
      throw (LoginException)EnvHelp.initCause(loginException, iOException);
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      LoginException loginException = new LoginException("Error: " + unsupportedCallbackException.getCallback().toString() + " not available to garner authentication information from the user");
      throw (LoginException)EnvHelp.initCause(loginException, unsupportedCallbackException);
    } 
  }
  
  private void cleanState() {
    this.username = null;
    if (this.password != null) {
      Arrays.fill(this.password, ' ');
      this.password = null;
    } 
    if (this.clearPass) {
      this.sharedState.remove("javax.security.auth.login.name");
      this.sharedState.remove("javax.security.auth.login.password");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\security\FileLoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */