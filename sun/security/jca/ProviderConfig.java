package sun.security.jca;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.ProviderException;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;

final class ProviderConfig {
  private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
  
  private static final String P11_SOL_NAME = "sun.security.pkcs11.SunPKCS11";
  
  private static final String P11_SOL_ARG = "${java.home}/lib/security/sunpkcs11-solaris.cfg";
  
  private static final int MAX_LOAD_TRIES = 30;
  
  private static final Class[] CL_STRING = { String.class };
  
  private final String className;
  
  private final String argument;
  
  private int tries;
  
  private boolean isLoading;
  
  ProviderConfig(String paramString1, String paramString2) {
    if (paramString1.equals("sun.security.pkcs11.SunPKCS11") && paramString2.equals("${java.home}/lib/security/sunpkcs11-solaris.cfg"))
      checkSunPKCS11Solaris(); 
    this.className = paramString1;
    this.argument = expand(paramString2);
  }
  
  ProviderConfig(String paramString) { this(paramString, ""); }
  
  ProviderConfig(Provider paramProvider) {
    this.className = paramProvider.getClass().getName();
    this.argument = "";
    this.provider = paramProvider;
  }
  
  private void checkSunPKCS11Solaris() {
    Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            File file = new File("/usr/lib/libpkcs11.so");
            return !file.exists() ? Boolean.FALSE : ("false".equalsIgnoreCase(System.getProperty("sun.security.pkcs11.enable-solaris")) ? Boolean.FALSE : Boolean.TRUE);
          }
        });
    if (bool == Boolean.FALSE)
      this.tries = 30; 
  }
  
  private boolean hasArgument() { return (this.argument.length() != 0); }
  
  private boolean shouldLoad() { return (this.tries < 30); }
  
  private void disableLoad() { this.tries = 30; }
  
  boolean isLoaded() { return (this.provider != null); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ProviderConfig))
      return false; 
    ProviderConfig providerConfig = (ProviderConfig)paramObject;
    return (this.className.equals(providerConfig.className) && this.argument.equals(providerConfig.argument));
  }
  
  public int hashCode() { return this.className.hashCode() + this.argument.hashCode(); }
  
  public String toString() { return hasArgument() ? (this.className + "('" + this.argument + "')") : this.className; }
  
  Provider getProvider() {
    Provider provider1 = this.provider;
    if (provider1 != null)
      return provider1; 
    if (!shouldLoad())
      return null; 
    if (this.isLoading) {
      if (debug != null) {
        debug.println("Recursion loading provider: " + this);
        (new Exception("Call trace")).printStackTrace();
      } 
      return null;
    } 
    try {
      this.isLoading = true;
      this.tries++;
      provider1 = doLoadProvider();
    } finally {
      this.isLoading = false;
    } 
    this.provider = provider1;
    return provider1;
  }
  
  private Provider doLoadProvider() { return (Provider)AccessController.doPrivileged(new PrivilegedAction<Provider>() {
          public Provider run() {
            if (debug != null)
              debug.println("Loading provider: " + ProviderConfig.this); 
            try {
              Object object;
              Class clazz;
              ClassLoader classLoader = ClassLoader.getSystemClassLoader();
              if (classLoader != null) {
                clazz = classLoader.loadClass(ProviderConfig.this.className);
              } else {
                clazz = Class.forName(ProviderConfig.this.className);
              } 
              if (!ProviderConfig.this.hasArgument()) {
                object = clazz.newInstance();
              } else {
                Constructor constructor = clazz.getConstructor(CL_STRING);
                object = constructor.newInstance(new Object[] { ProviderConfig.access$400(ProviderConfig.this) });
              } 
              if (object instanceof Provider) {
                if (debug != null)
                  debug.println("Loaded provider " + object); 
                return (Provider)object;
              } 
              if (debug != null)
                debug.println(ProviderConfig.this.className + " is not a provider"); 
              ProviderConfig.this.disableLoad();
              return null;
            } catch (Exception exception1) {
              Exception exception2;
              if (exception1 instanceof InvocationTargetException) {
                exception2 = ((InvocationTargetException)exception1).getCause();
              } else {
                exception2 = exception1;
              } 
              if (debug != null) {
                debug.println("Error loading provider " + ProviderConfig.this);
                exception2.printStackTrace();
              } 
              if (exception2 instanceof ProviderException)
                throw (ProviderException)exception2; 
              if (exception2 instanceof UnsupportedOperationException)
                ProviderConfig.this.disableLoad(); 
              return null;
            } catch (ExceptionInInitializerError exceptionInInitializerError) {
              if (debug != null) {
                debug.println("Error loading provider " + ProviderConfig.this);
                exceptionInInitializerError.printStackTrace();
              } 
              ProviderConfig.this.disableLoad();
              return null;
            } 
          }
        }); }
  
  private static String expand(final String value) { return !paramString.contains("${") ? paramString : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            try {
              return PropertyExpander.expand(value);
            } catch (GeneralSecurityException generalSecurityException) {
              throw new ProviderException(generalSecurityException);
            } 
          }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jca\ProviderConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */