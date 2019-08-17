package sun.util.locale.provider;

import java.lang.reflect.Method;
import java.util.spi.LocaleServiceProvider;

public class HostLocaleProviderAdapter extends AuxLocaleProviderAdapter {
  public LocaleProviderAdapter.Type getAdapterType() { return LocaleProviderAdapter.Type.HOST; }
  
  protected <P extends LocaleServiceProvider> P findInstalledProvider(Class<P> paramClass) {
    try {
      Method method = HostLocaleProviderAdapterImpl.class.getMethod("get" + paramClass.getSimpleName(), (Class[])null);
      return (P)(LocaleServiceProvider)method.invoke(null, (Object[])null);
    } catch (NoSuchMethodException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException noSuchMethodException) {
      LocaleServiceProviderPool.config(HostLocaleProviderAdapter.class, noSuchMethodException.toString());
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\HostLocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */