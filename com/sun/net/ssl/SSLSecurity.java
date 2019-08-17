package com.sun.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;

final class SSLSecurity {
  private static Provider.Service getService(String paramString1, String paramString2) {
    ProviderList providerList = Providers.getProviderList();
    for (Provider provider : providerList.providers()) {
      Provider.Service service = provider.getService(paramString1, paramString2);
      if (service != null)
        return service; 
    } 
    return null;
  }
  
  private static Object[] getImpl1(String paramString1, String paramString2, Provider.Service paramService) throws NoSuchAlgorithmException {
    Class clazz;
    Provider provider = paramService.getProvider();
    String str = paramService.getClassName();
    try {
      ClassLoader classLoader = provider.getClass().getClassLoader();
      if (classLoader == null) {
        clazz = Class.forName(str);
      } else {
        clazz = classLoader.loadClass(str);
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoSuchAlgorithmException("Class " + str + " configured for " + paramString2 + " not found: " + classNotFoundException.getMessage());
    } catch (SecurityException securityException) {
      throw new NoSuchAlgorithmException("Class " + str + " configured for " + paramString2 + " cannot be accessed: " + securityException.getMessage());
    } 
    try {
      Object object = null;
      Class clazz1;
      if ((clazz1 = Class.forName("javax.net.ssl." + paramString2 + "Spi")) != null && checkSuperclass(clazz, clazz1)) {
        if (paramString2.equals("SSLContext")) {
          object = new SSLContextSpiWrapper(paramString1, provider);
        } else if (paramString2.equals("TrustManagerFactory")) {
          TrustManagerFactorySpiWrapper trustManagerFactorySpiWrapper = new TrustManagerFactorySpiWrapper(paramString1, provider);
        } else if (paramString2.equals("KeyManagerFactory")) {
          KeyManagerFactorySpiWrapper keyManagerFactorySpiWrapper = new KeyManagerFactorySpiWrapper(paramString1, provider);
        } else {
          throw new IllegalStateException("Class " + clazz.getName() + " unknown engineType wrapper:" + paramString2);
        } 
      } else {
        Class clazz2;
        if ((clazz2 = Class.forName("com.sun.net.ssl." + paramString2 + "Spi")) != null && checkSuperclass(clazz, clazz2))
          object = paramService.newInstance(null); 
      } 
      if (object != null)
        return new Object[] { object, provider }; 
      throw new NoSuchAlgorithmException("Couldn't locate correct object or wrapper: " + paramString2 + " " + paramString1);
    } catch (ClassNotFoundException classNotFoundException) {
      IllegalStateException illegalStateException = new IllegalStateException("Engine Class Not Found for " + paramString2);
      illegalStateException.initCause(classNotFoundException);
      throw illegalStateException;
    } 
  }
  
  static Object[] getImpl(String paramString1, String paramString2, String paramString3) throws NoSuchAlgorithmException, NoSuchProviderException {
    Provider.Service service;
    if (paramString3 != null) {
      ProviderList providerList = Providers.getProviderList();
      Provider provider = providerList.getProvider(paramString3);
      if (provider == null)
        throw new NoSuchProviderException("No such provider: " + paramString3); 
      service = provider.getService(paramString2, paramString1);
    } else {
      service = getService(paramString2, paramString1);
    } 
    if (service == null)
      throw new NoSuchAlgorithmException("Algorithm " + paramString1 + " not available"); 
    return getImpl1(paramString1, paramString2, service);
  }
  
  static Object[] getImpl(String paramString1, String paramString2, Provider paramProvider) throws NoSuchAlgorithmException {
    Provider.Service service = paramProvider.getService(paramString2, paramString1);
    if (service == null)
      throw new NoSuchAlgorithmException("No such algorithm: " + paramString1); 
    return getImpl1(paramString1, paramString2, service);
  }
  
  private static boolean checkSuperclass(Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1 == null || paramClass2 == null)
      return false; 
    while (!paramClass1.equals(paramClass2)) {
      paramClass1 = paramClass1.getSuperclass();
      if (paramClass1 == null)
        return false; 
    } 
    return true;
  }
  
  static Object[] truncateArray(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) {
    for (byte b = 0; b < paramArrayOfObject2.length; b++)
      paramArrayOfObject2[b] = paramArrayOfObject1[b]; 
    return paramArrayOfObject2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\SSLSecurity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */