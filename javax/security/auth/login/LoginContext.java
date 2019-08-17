package javax.security.auth.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import sun.security.util.Debug;
import sun.security.util.PendingException;
import sun.security.util.ResourcesMgr;

public class LoginContext {
  private static final String INIT_METHOD = "initialize";
  
  private static final String LOGIN_METHOD = "login";
  
  private static final String COMMIT_METHOD = "commit";
  
  private static final String ABORT_METHOD = "abort";
  
  private static final String LOGOUT_METHOD = "logout";
  
  private static final String OTHER = "other";
  
  private static final String DEFAULT_HANDLER = "auth.login.defaultCallbackHandler";
  
  private Subject subject = null;
  
  private boolean subjectProvided = false;
  
  private boolean loginSucceeded = false;
  
  private CallbackHandler callbackHandler;
  
  private Map<String, ?> state = new HashMap();
  
  private Configuration config;
  
  private AccessControlContext creatorAcc = null;
  
  private ModuleInfo[] moduleStack;
  
  private ClassLoader contextClassLoader = null;
  
  private static final Class<?>[] PARAMS = new Class[0];
  
  private int moduleIndex = 0;
  
  private LoginException firstError = null;
  
  private LoginException firstRequiredError = null;
  
  private boolean success = false;
  
  private static final Debug debug = Debug.getInstance("logincontext", "\t[LoginContext]");
  
  private void init(String paramString) throws LoginException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && this.creatorAcc == null)
      securityManager.checkPermission(new AuthPermission("createLoginContext." + paramString)); 
    if (paramString == null)
      throw new LoginException(ResourcesMgr.getString("Invalid.null.input.name")); 
    if (this.config == null)
      this.config = (Configuration)AccessController.doPrivileged(new PrivilegedAction<Configuration>() {
            public Configuration run() { return Configuration.getConfiguration(); }
          }); 
    AppConfigurationEntry[] arrayOfAppConfigurationEntry = this.config.getAppConfigurationEntry(paramString);
    if (arrayOfAppConfigurationEntry == null) {
      if (securityManager != null && this.creatorAcc == null)
        securityManager.checkPermission(new AuthPermission("createLoginContext.other")); 
      arrayOfAppConfigurationEntry = this.config.getAppConfigurationEntry("other");
      if (arrayOfAppConfigurationEntry == null) {
        MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("No.LoginModules.configured.for.name"));
        Object[] arrayOfObject = { paramString };
        throw new LoginException(messageFormat.format(arrayOfObject));
      } 
    } 
    this.moduleStack = new ModuleInfo[arrayOfAppConfigurationEntry.length];
    for (byte b = 0; b < arrayOfAppConfigurationEntry.length; b++)
      this.moduleStack[b] = new ModuleInfo(new AppConfigurationEntry(arrayOfAppConfigurationEntry[b].getLoginModuleName(), arrayOfAppConfigurationEntry[b].getControlFlag(), arrayOfAppConfigurationEntry[b].getOptions()), null); 
    this.contextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null)
              classLoader = ClassLoader.getSystemClassLoader(); 
            return classLoader;
          }
        });
  }
  
  private void loadDefaultCallbackHandler() throws LoginException {
    try {
      final ClassLoader finalLoader = this.contextClassLoader;
      this.callbackHandler = (CallbackHandler)AccessController.doPrivileged(new PrivilegedExceptionAction<CallbackHandler>() {
            public CallbackHandler run() throws Exception {
              String str = Security.getProperty("auth.login.defaultCallbackHandler");
              if (str == null || str.length() == 0)
                return null; 
              Class clazz = Class.forName(str, true, finalLoader).asSubclass(CallbackHandler.class);
              return (CallbackHandler)clazz.newInstance();
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new LoginException(privilegedActionException.getException().toString());
    } 
    if (this.callbackHandler != null && this.creatorAcc == null)
      this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), this.callbackHandler); 
  }
  
  public LoginContext(String paramString) throws LoginException {
    init(paramString);
    loadDefaultCallbackHandler();
  }
  
  public LoginContext(String paramString, Subject paramSubject) throws LoginException {
    init(paramString);
    if (paramSubject == null)
      throw new LoginException(ResourcesMgr.getString("invalid.null.Subject.provided")); 
    this.subject = paramSubject;
    this.subjectProvided = true;
    loadDefaultCallbackHandler();
  }
  
  public LoginContext(String paramString, CallbackHandler paramCallbackHandler) throws LoginException {
    init(paramString);
    if (paramCallbackHandler == null)
      throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided")); 
    this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), paramCallbackHandler);
  }
  
  public LoginContext(String paramString, Subject paramSubject, CallbackHandler paramCallbackHandler) throws LoginException {
    this(paramString, paramSubject);
    if (paramCallbackHandler == null)
      throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided")); 
    this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), paramCallbackHandler);
  }
  
  public LoginContext(String paramString, Subject paramSubject, CallbackHandler paramCallbackHandler, Configuration paramConfiguration) throws LoginException {
    this.config = paramConfiguration;
    if (paramConfiguration != null)
      this.creatorAcc = AccessController.getContext(); 
    init(paramString);
    if (paramSubject != null) {
      this.subject = paramSubject;
      this.subjectProvided = true;
    } 
    if (paramCallbackHandler == null) {
      loadDefaultCallbackHandler();
    } else if (this.creatorAcc == null) {
      this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), paramCallbackHandler);
    } else {
      this.callbackHandler = paramCallbackHandler;
    } 
  }
  
  public void login() throws LoginException {
    this.loginSucceeded = false;
    if (this.subject == null)
      this.subject = new Subject(); 
    try {
      invokePriv("login");
      invokePriv("commit");
      this.loginSucceeded = true;
    } catch (LoginException loginException) {
      try {
        invokePriv("abort");
      } catch (LoginException loginException1) {
        throw loginException;
      } 
      throw loginException;
    } 
  }
  
  public void logout() throws LoginException {
    if (this.subject == null)
      throw new LoginException(ResourcesMgr.getString("null.subject.logout.called.before.login")); 
    invokePriv("logout");
  }
  
  public Subject getSubject() { return (!this.loginSucceeded && !this.subjectProvided) ? null : this.subject; }
  
  private void clearState() throws LoginException {
    this.moduleIndex = 0;
    this.firstError = null;
    this.firstRequiredError = null;
    this.success = false;
  }
  
  private void throwException(LoginException paramLoginException1, LoginException paramLoginException2) throws LoginException {
    clearState();
    LoginException loginException = (paramLoginException1 != null) ? paramLoginException1 : paramLoginException2;
    throw loginException;
  }
  
  private void invokePriv(final String methodName) throws LoginException {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws LoginException {
              LoginContext.this.invoke(methodName);
              return null;
            }
          },  this.creatorAcc);
    } catch (PrivilegedActionException privilegedActionException) {
      throw (LoginException)privilegedActionException.getException();
    } 
  }
  
  private void invoke(String paramString) throws LoginException {
    int i = this.moduleIndex;
    while (i < this.moduleStack.length) {
      try {
        byte b = 0;
        Method[] arrayOfMethod = null;
        if ((this.moduleStack[i]).module != null) {
          arrayOfMethod = (this.moduleStack[i]).module.getClass().getMethods();
        } else {
          Class clazz = Class.forName((this.moduleStack[i]).entry.getLoginModuleName(), true, this.contextClassLoader);
          Constructor constructor = clazz.getConstructor(PARAMS);
          Object[] arrayOfObject1 = new Object[0];
          (this.moduleStack[i]).module = constructor.newInstance(arrayOfObject1);
          arrayOfMethod = (this.moduleStack[i]).module.getClass().getMethods();
          for (b = 0; b < arrayOfMethod.length && !arrayOfMethod[b].getName().equals("initialize"); b++);
          Object[] arrayOfObject2 = { this.subject, this.callbackHandler, this.state, (this.moduleStack[i]).entry.getOptions() };
          arrayOfMethod[b].invoke((this.moduleStack[i]).module, arrayOfObject2);
        } 
        for (b = 0; b < arrayOfMethod.length && !arrayOfMethod[b].getName().equals(paramString); b++);
        Object[] arrayOfObject = new Object[0];
        boolean bool = ((Boolean)arrayOfMethod[b].invoke((this.moduleStack[i]).module, arrayOfObject)).booleanValue();
        if (bool == true) {
          if (!paramString.equals("abort") && !paramString.equals("logout") && (this.moduleStack[i]).entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT && this.firstRequiredError == null) {
            clearState();
            if (debug != null)
              debug.println(paramString + " SUFFICIENT success"); 
            return;
          } 
          if (debug != null)
            debug.println(paramString + " success"); 
          this.success = true;
        } else if (debug != null) {
          debug.println(paramString + " ignored");
        } 
      } catch (NoSuchMethodException noSuchMethodException) {
        MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor"));
        Object[] arrayOfObject = { (this.moduleStack[i]).entry.getLoginModuleName() };
        throwException(null, new LoginException(messageFormat.format(arrayOfObject)));
      } catch (InstantiationException instantiationException) {
        throwException(null, new LoginException(ResourcesMgr.getString("unable.to.instantiate.LoginModule.") + instantiationException.getMessage()));
      } catch (ClassNotFoundException classNotFoundException) {
        throwException(null, new LoginException(ResourcesMgr.getString("unable.to.find.LoginModule.class.") + classNotFoundException.getMessage()));
      } catch (IllegalAccessException illegalAccessException) {
        throwException(null, new LoginException(ResourcesMgr.getString("unable.to.access.LoginModule.") + illegalAccessException.getMessage()));
      } catch (InvocationTargetException invocationTargetException) {
        LoginException loginException;
        if (invocationTargetException.getCause() instanceof PendingException && paramString.equals("login"))
          throw (PendingException)invocationTargetException.getCause(); 
        if (invocationTargetException.getCause() instanceof LoginException) {
          loginException = (LoginException)invocationTargetException.getCause();
        } else if (invocationTargetException.getCause() instanceof SecurityException) {
          loginException = new LoginException("Security Exception");
          loginException.initCause(new SecurityException());
          if (debug != null) {
            debug.println("original security exception with detail msg replaced by new exception with empty detail msg");
            debug.println("original security exception: " + invocationTargetException.getCause().toString());
          } 
        } else {
          StringWriter stringWriter = new StringWriter();
          invocationTargetException.getCause().printStackTrace(new PrintWriter(stringWriter));
          stringWriter.flush();
          loginException = new LoginException(stringWriter.toString());
        } 
        if ((this.moduleStack[i]).entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
          if (debug != null)
            debug.println(paramString + " REQUISITE failure"); 
          if (paramString.equals("abort") || paramString.equals("logout")) {
            if (this.firstRequiredError == null)
              this.firstRequiredError = loginException; 
          } else {
            throwException(this.firstRequiredError, loginException);
          } 
        } else if ((this.moduleStack[i]).entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED) {
          if (debug != null)
            debug.println(paramString + " REQUIRED failure"); 
          if (this.firstRequiredError == null)
            this.firstRequiredError = loginException; 
        } else {
          if (debug != null)
            debug.println(paramString + " OPTIONAL failure"); 
          if (this.firstError == null)
            this.firstError = loginException; 
        } 
      } 
      i++;
      this.moduleIndex++;
    } 
    if (this.firstRequiredError != null) {
      throwException(this.firstRequiredError, null);
    } else if (!this.success && this.firstError != null) {
      throwException(this.firstError, null);
    } else if (!this.success) {
      throwException(new LoginException(ResourcesMgr.getString("Login.Failure.all.modules.ignored")), null);
    } else {
      clearState();
      return;
    } 
  }
  
  private static class ModuleInfo {
    AppConfigurationEntry entry;
    
    Object module;
    
    ModuleInfo(AppConfigurationEntry param1AppConfigurationEntry, Object param1Object) {
      this.entry = param1AppConfigurationEntry;
      this.module = param1Object;
    }
  }
  
  private static class SecureCallbackHandler implements CallbackHandler {
    private final AccessControlContext acc;
    
    private final CallbackHandler ch;
    
    SecureCallbackHandler(AccessControlContext param1AccessControlContext, CallbackHandler param1CallbackHandler) {
      this.acc = param1AccessControlContext;
      this.ch = param1CallbackHandler;
    }
    
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
              public Void run() throws LoginException {
                LoginContext.SecureCallbackHandler.this.ch.handle(callbacks);
                return null;
              }
            },  this.acc);
      } catch (PrivilegedActionException privilegedActionException) {
        if (privilegedActionException.getException() instanceof IOException)
          throw (IOException)privilegedActionException.getException(); 
        throw (UnsupportedCallbackException)privilegedActionException.getException();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\login\LoginContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */