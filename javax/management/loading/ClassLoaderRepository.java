package javax.management.loading;

public interface ClassLoaderRepository {
  Class<?> loadClass(String paramString) throws ClassNotFoundException;
  
  Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException;
  
  Class<?> loadClassBefore(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\loading\ClassLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */