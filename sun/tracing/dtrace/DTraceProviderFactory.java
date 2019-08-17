package sun.tracing.dtrace;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DTraceProviderFactory extends ProviderFactory {
  public <T extends Provider> T createProvider(Class<T> paramClass) {
    DTraceProvider dTraceProvider = new DTraceProvider(paramClass);
    Provider provider = dTraceProvider.newProxyInstance();
    dTraceProvider.setProxy(provider);
    dTraceProvider.init();
    new Activation(dTraceProvider.getModuleName(), new DTraceProvider[] { dTraceProvider });
    return (T)provider;
  }
  
  public Map<Class<? extends Provider>, Provider> createProviders(Set<Class<? extends Provider>> paramSet, String paramString) {
    HashMap hashMap = new HashMap();
    HashSet hashSet = new HashSet();
    for (Class clazz : paramSet) {
      DTraceProvider dTraceProvider = new DTraceProvider(clazz);
      hashSet.add(dTraceProvider);
      hashMap.put(clazz, dTraceProvider.newProxyInstance());
    } 
    new Activation(paramString, (DTraceProvider[])hashSet.toArray(new DTraceProvider[0]));
    return hashMap;
  }
  
  public static boolean isSupported() {
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        RuntimePermission runtimePermission = new RuntimePermission("com.sun.tracing.dtrace.createProvider");
        securityManager.checkPermission(runtimePermission);
      } 
      return JVM.isSupported();
    } catch (SecurityException securityException) {
      return false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\dtrace\DTraceProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */