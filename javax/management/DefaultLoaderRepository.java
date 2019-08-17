package javax.management;

import javax.management.loading.DefaultLoaderRepository;

@Deprecated
public class DefaultLoaderRepository {
  public static Class<?> loadClass(String paramString) throws ClassNotFoundException { return DefaultLoaderRepository.loadClass(paramString); }
  
  public static Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException { return DefaultLoaderRepository.loadClassWithout(paramClassLoader, paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\DefaultLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */