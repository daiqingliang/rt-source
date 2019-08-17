package com.sun.jmx.mbeanserver;

import javax.management.loading.ClassLoaderRepository;

final class SecureClassLoaderRepository implements ClassLoaderRepository {
  private final ClassLoaderRepository clr;
  
  public SecureClassLoaderRepository(ClassLoaderRepository paramClassLoaderRepository) { this.clr = paramClassLoaderRepository; }
  
  public final Class<?> loadClass(String paramString) throws ClassNotFoundException { return this.clr.loadClass(paramString); }
  
  public final Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException { return this.clr.loadClassWithout(paramClassLoader, paramString); }
  
  public final Class<?> loadClassBefore(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException { return this.clr.loadClassBefore(paramClassLoader, paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\SecureClassLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */