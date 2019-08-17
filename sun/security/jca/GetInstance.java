package sun.security.jca;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.List;

public class GetInstance {
  public static Provider.Service getService(String paramString1, String paramString2) throws NoSuchAlgorithmException {
    ProviderList providerList = Providers.getProviderList();
    Provider.Service service = providerList.getService(paramString1, paramString2);
    if (service == null)
      throw new NoSuchAlgorithmException(paramString2 + " " + paramString1 + " not available"); 
    return service;
  }
  
  public static Provider.Service getService(String paramString1, String paramString2, String paramString3) throws NoSuchAlgorithmException, NoSuchProviderException {
    if (paramString3 == null || paramString3.length() == 0)
      throw new IllegalArgumentException("missing provider"); 
    Provider provider = Providers.getProviderList().getProvider(paramString3);
    if (provider == null)
      throw new NoSuchProviderException("no such provider: " + paramString3); 
    Provider.Service service = provider.getService(paramString1, paramString2);
    if (service == null)
      throw new NoSuchAlgorithmException("no such algorithm: " + paramString2 + " for provider " + paramString3); 
    return service;
  }
  
  public static Provider.Service getService(String paramString1, String paramString2, Provider paramProvider) throws NoSuchAlgorithmException {
    if (paramProvider == null)
      throw new IllegalArgumentException("missing provider"); 
    Provider.Service service = paramProvider.getService(paramString1, paramString2);
    if (service == null)
      throw new NoSuchAlgorithmException("no such algorithm: " + paramString2 + " for provider " + paramProvider.getName()); 
    return service;
  }
  
  public static List<Provider.Service> getServices(String paramString1, String paramString2) {
    ProviderList providerList = Providers.getProviderList();
    return providerList.getServices(paramString1, paramString2);
  }
  
  @Deprecated
  public static List<Provider.Service> getServices(String paramString, List<String> paramList) {
    ProviderList providerList = Providers.getProviderList();
    return providerList.getServices(paramString, paramList);
  }
  
  public static List<Provider.Service> getServices(List<ServiceId> paramList) {
    ProviderList providerList = Providers.getProviderList();
    return providerList.getServices(paramList);
  }
  
  public static Instance getInstance(String paramString1, Class<?> paramClass, String paramString2) throws NoSuchAlgorithmException {
    ProviderList providerList = Providers.getProviderList();
    Provider.Service service = providerList.getService(paramString1, paramString2);
    if (service == null)
      throw new NoSuchAlgorithmException(paramString2 + " " + paramString1 + " not available"); 
    try {
      return getInstance(service, paramClass);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException2) {
      NoSuchAlgorithmException noSuchAlgorithmException1 = noSuchAlgorithmException2;
      for (Provider.Service service1 : providerList.getServices(paramString1, paramString2)) {
        if (service1 == service)
          continue; 
        try {
          return getInstance(service1, paramClass);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          noSuchAlgorithmException1 = noSuchAlgorithmException;
        } 
      } 
      throw noSuchAlgorithmException1;
    } 
  }
  
  public static Instance getInstance(String paramString1, Class<?> paramClass, String paramString2, Object paramObject) throws NoSuchAlgorithmException {
    List list = getServices(paramString1, paramString2);
    NoSuchAlgorithmException noSuchAlgorithmException = null;
    for (Provider.Service service : list) {
      try {
        return getInstance(service, paramClass, paramObject);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException1) {
        noSuchAlgorithmException = noSuchAlgorithmException1;
      } 
    } 
    if (noSuchAlgorithmException != null)
      throw noSuchAlgorithmException; 
    throw new NoSuchAlgorithmException(paramString2 + " " + paramString1 + " not available");
  }
  
  public static Instance getInstance(String paramString1, Class<?> paramClass, String paramString2, String paramString3) throws NoSuchAlgorithmException, NoSuchProviderException { return getInstance(getService(paramString1, paramString2, paramString3), paramClass); }
  
  public static Instance getInstance(String paramString1, Class<?> paramClass, String paramString2, Object paramObject, String paramString3) throws NoSuchAlgorithmException, NoSuchProviderException { return getInstance(getService(paramString1, paramString2, paramString3), paramClass, paramObject); }
  
  public static Instance getInstance(String paramString1, Class<?> paramClass, String paramString2, Provider paramProvider) throws NoSuchAlgorithmException { return getInstance(getService(paramString1, paramString2, paramProvider), paramClass); }
  
  public static Instance getInstance(String paramString1, Class<?> paramClass, String paramString2, Object paramObject, Provider paramProvider) throws NoSuchAlgorithmException { return getInstance(getService(paramString1, paramString2, paramProvider), paramClass, paramObject); }
  
  public static Instance getInstance(Provider.Service paramService, Class<?> paramClass) throws NoSuchAlgorithmException {
    Object object = paramService.newInstance(null);
    checkSuperClass(paramService, object.getClass(), paramClass);
    return new Instance(paramService.getProvider(), object, null);
  }
  
  public static Instance getInstance(Provider.Service paramService, Class<?> paramClass, Object paramObject) throws NoSuchAlgorithmException {
    Object object = paramService.newInstance(paramObject);
    checkSuperClass(paramService, object.getClass(), paramClass);
    return new Instance(paramService.getProvider(), object, null);
  }
  
  public static void checkSuperClass(Provider.Service paramService, Class<?> paramClass1, Class<?> paramClass2) throws NoSuchAlgorithmException {
    if (paramClass2 == null)
      return; 
    if (!paramClass2.isAssignableFrom(paramClass1))
      throw new NoSuchAlgorithmException("class configured for " + paramService.getType() + ": " + paramService.getClassName() + " not a " + paramService.getType()); 
  }
  
  public static final class Instance {
    public final Provider provider;
    
    public final Object impl;
    
    private Instance(Provider param1Provider, Object param1Object) {
      this.provider = param1Provider;
      this.impl = param1Object;
    }
    
    public Object[] toArray() { return new Object[] { this.impl, this.provider }; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jca\GetInstance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */