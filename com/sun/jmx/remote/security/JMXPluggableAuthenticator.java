package com.sun.jmx.remote.security;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXAuthenticator;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public final class JMXPluggableAuthenticator implements JMXAuthenticator {
  private LoginContext loginContext;
  
  private String username;
  
  private String password;
  
  private static final String LOGIN_CONFIG_PROP = "jmx.remote.x.login.config";
  
  private static final String LOGIN_CONFIG_NAME = "JMXPluggableAuthenticator";
  
  private static final String PASSWORD_FILE_PROP = "jmx.remote.x.password.file";
  
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXPluggableAuthenticator");
  
  public JMXPluggableAuthenticator(Map<?, ?> paramMap) {
    String str1 = null;
    String str2 = null;
    if (paramMap != null) {
      str1 = (String)paramMap.get("jmx.remote.x.login.config");
      str2 = (String)paramMap.get("jmx.remote.x.password.file");
    } 
    try {
      if (str1 != null) {
        this.loginContext = new LoginContext(str1, new JMXCallbackHandler(this, null));
      } else {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          securityManager.checkPermission(new AuthPermission("createLoginContext.JMXPluggableAuthenticator")); 
        final String pf = str2;
        try {
          this.loginContext = (LoginContext)AccessController.doPrivileged(new PrivilegedExceptionAction<LoginContext>() {
                public LoginContext run() throws LoginException { return new LoginContext("JMXPluggableAuthenticator", null, new JMXPluggableAuthenticator.JMXCallbackHandler(JMXPluggableAuthenticator.this, null), new JMXPluggableAuthenticator.FileLoginConfig(pf)); }
              });
        } catch (PrivilegedActionException privilegedActionException) {
          throw (LoginException)privilegedActionException.getException();
        } 
      } 
    } catch (LoginException loginException) {
      authenticationFailure("authenticate", loginException);
    } catch (SecurityException securityException) {
      authenticationFailure("authenticate", securityException);
    } 
  }
  
  public Subject authenticate(Object paramObject) {
    if (!(paramObject instanceof String[])) {
      if (paramObject == null)
        authenticationFailure("authenticate", "Credentials required"); 
      String str = "Credentials should be String[] instead of " + paramObject.getClass().getName();
      authenticationFailure("authenticate", str);
    } 
    String[] arrayOfString = (String[])paramObject;
    if (arrayOfString.length != 2) {
      String str = "Credentials should have 2 elements not " + arrayOfString.length;
      authenticationFailure("authenticate", str);
    } 
    this.username = arrayOfString[0];
    this.password = arrayOfString[1];
    if (this.username == null || this.password == null)
      authenticationFailure("authenticate", "Username or password is null"); 
    try {
      this.loginContext.login();
      final Subject subject = this.loginContext.getSubject();
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              subject.setReadOnly();
              return null;
            }
          });
      return subject;
    } catch (LoginException loginException) {
      authenticationFailure("authenticate", loginException);
      return null;
    } 
  }
  
  private static void authenticationFailure(String paramString1, String paramString2) throws SecurityException {
    String str = "Authentication failed! " + paramString2;
    SecurityException securityException = new SecurityException(str);
    logException(paramString1, str, securityException);
    throw securityException;
  }
  
  private static void authenticationFailure(String paramString, Exception paramException) throws SecurityException {
    SecurityException securityException;
    String str;
    if (paramException instanceof SecurityException) {
      str = paramException.getMessage();
      securityException = (SecurityException)paramException;
    } else {
      str = "Authentication failed! " + paramException.getMessage();
      SecurityException securityException1 = new SecurityException(str);
      EnvHelp.initCause(securityException1, paramException);
      securityException = securityException1;
    } 
    logException(paramString, str, securityException);
    throw securityException;
  }
  
  private static void logException(String paramString1, String paramString2, Exception paramException) {
    if (logger.traceOn())
      logger.trace(paramString1, paramString2); 
    if (logger.debugOn())
      logger.debug(paramString1, paramException); 
  }
  
  private static class FileLoginConfig extends Configuration {
    private AppConfigurationEntry[] entries;
    
    private static final String FILE_LOGIN_MODULE = FileLoginModule.class.getName();
    
    private static final String PASSWORD_FILE_OPTION = "passwordFile";
    
    public FileLoginConfig(String param1String) {
      if (param1String != null) {
        map = new HashMap(1);
        map.put("passwordFile", param1String);
      } else {
        map = Collections.emptyMap();
      } 
      this.entries = new AppConfigurationEntry[] { new AppConfigurationEntry(FILE_LOGIN_MODULE, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, map) };
    }
    
    public AppConfigurationEntry[] getAppConfigurationEntry(String param1String) { return param1String.equals("JMXPluggableAuthenticator") ? this.entries : null; }
    
    public void refresh() {}
  }
  
  private final class JMXCallbackHandler implements CallbackHandler {
    private JMXCallbackHandler() {}
    
    public void handle(Callback[] param1ArrayOfCallback) throws IOException, UnsupportedCallbackException {
      for (byte b = 0; b < param1ArrayOfCallback.length; b++) {
        if (param1ArrayOfCallback[b] instanceof NameCallback) {
          ((NameCallback)param1ArrayOfCallback[b]).setName(JMXPluggableAuthenticator.this.username);
        } else if (param1ArrayOfCallback[b] instanceof PasswordCallback) {
          ((PasswordCallback)param1ArrayOfCallback[b]).setPassword(JMXPluggableAuthenticator.this.password.toCharArray());
        } else {
          throw new UnsupportedCallbackException(param1ArrayOfCallback[b], "Unrecognized Callback");
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\security\JMXPluggableAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */