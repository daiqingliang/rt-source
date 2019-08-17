package javax.security.auth.login;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;
import javax.security.auth.AuthPermission;
import sun.security.jca.GetInstance;

public abstract class Configuration {
  private static Configuration configuration;
  
  private final AccessControlContext acc = AccessController.getContext();
  
  private static void checkPermission(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new AuthPermission("createLoginConfiguration." + paramString)); 
  }
  
  public static Configuration getConfiguration() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new AuthPermission("getLoginConfiguration")); 
    synchronized (Configuration.class) {
      if (configuration == null) {
        String str = null;
        str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
              public String run() { return Security.getProperty("login.configuration.provider"); }
            });
        if (str == null)
          str = "sun.security.provider.ConfigFile"; 
        try {
          final String finalClass = str;
          final Configuration untrustedImpl = (Configuration)AccessController.doPrivileged(new PrivilegedExceptionAction<Configuration>() {
                public Configuration run() {
                  Class clazz = Class.forName(finalClass, false, Thread.currentThread().getContextClassLoader()).asSubclass(Configuration.class);
                  return (Configuration)clazz.newInstance();
                }
              });
          AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() {
                  Configuration.setConfiguration(untrustedImpl);
                  return null;
                }
              },  (AccessControlContext)Objects.requireNonNull(configuration1.acc));
        } catch (PrivilegedActionException privilegedActionException) {
          Exception exception = privilegedActionException.getException();
          if (exception instanceof InstantiationException)
            throw (SecurityException)(new SecurityException("Configuration error:" + exception.getCause().getMessage() + "\n")).initCause(exception.getCause()); 
          throw (SecurityException)(new SecurityException("Configuration error: " + exception.toString() + "\n")).initCause(exception);
        } 
      } 
      return configuration;
    } 
  }
  
  public static void setConfiguration(Configuration paramConfiguration) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new AuthPermission("setLoginConfiguration")); 
    configuration = paramConfiguration;
  }
  
  public static Configuration getInstance(String paramString, Parameters paramParameters) throws NoSuchAlgorithmException {
    checkPermission(paramString);
    try {
      GetInstance.Instance instance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, paramString, paramParameters);
      return new ConfigDelegate((ConfigurationSpi)instance.impl, instance.provider, paramString, paramParameters, null);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return handleException(noSuchAlgorithmException);
    } 
  }
  
  public static Configuration getInstance(String paramString1, Parameters paramParameters, String paramString2) throws NoSuchProviderException, NoSuchAlgorithmException {
    if (paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException("missing provider"); 
    checkPermission(paramString1);
    try {
      GetInstance.Instance instance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, paramString1, paramParameters, paramString2);
      return new ConfigDelegate((ConfigurationSpi)instance.impl, instance.provider, paramString1, paramParameters, null);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return handleException(noSuchAlgorithmException);
    } 
  }
  
  public static Configuration getInstance(String paramString, Parameters paramParameters, Provider paramProvider) throws NoSuchAlgorithmException {
    if (paramProvider == null)
      throw new IllegalArgumentException("missing provider"); 
    checkPermission(paramString);
    try {
      GetInstance.Instance instance = GetInstance.getInstance("Configuration", ConfigurationSpi.class, paramString, paramParameters, paramProvider);
      return new ConfigDelegate((ConfigurationSpi)instance.impl, instance.provider, paramString, paramParameters, null);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return handleException(noSuchAlgorithmException);
    } 
  }
  
  private static Configuration handleException(NoSuchAlgorithmException paramNoSuchAlgorithmException) throws NoSuchAlgorithmException {
    Throwable throwable = paramNoSuchAlgorithmException.getCause();
    if (throwable instanceof IllegalArgumentException)
      throw (IllegalArgumentException)throwable; 
    throw paramNoSuchAlgorithmException;
  }
  
  public Provider getProvider() { return null; }
  
  public String getType() { return null; }
  
  public Parameters getParameters() { return null; }
  
  public abstract AppConfigurationEntry[] getAppConfigurationEntry(String paramString);
  
  public void refresh() {}
  
  private static class ConfigDelegate extends Configuration {
    private ConfigurationSpi spi;
    
    private Provider p;
    
    private String type;
    
    private Configuration.Parameters params;
    
    private ConfigDelegate(ConfigurationSpi param1ConfigurationSpi, Provider param1Provider, String param1String, Configuration.Parameters param1Parameters) {
      this.spi = param1ConfigurationSpi;
      this.p = param1Provider;
      this.type = param1String;
      this.params = param1Parameters;
    }
    
    public String getType() { return this.type; }
    
    public Configuration.Parameters getParameters() { return this.params; }
    
    public Provider getProvider() { return this.p; }
    
    public AppConfigurationEntry[] getAppConfigurationEntry(String param1String) { return this.spi.engineGetAppConfigurationEntry(param1String); }
    
    public void refresh() { this.spi.engineRefresh(); }
  }
  
  public static interface Parameters {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\login\Configuration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */