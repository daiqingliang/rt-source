package com.sun.jmx.remote.util;

import javax.management.loading.ClassLoaderRepository;

public class ClassLoaderWithRepository extends ClassLoader {
  private ClassLoaderRepository repository;
  
  private ClassLoader cl2;
  
  public ClassLoaderWithRepository(ClassLoaderRepository paramClassLoaderRepository, ClassLoader paramClassLoader) {
    if (paramClassLoaderRepository == null)
      throw new IllegalArgumentException("Null ClassLoaderRepository object."); 
    this.repository = paramClassLoaderRepository;
    this.cl2 = paramClassLoader;
  }
  
  protected Class<?> findClass(String paramString) throws ClassNotFoundException {
    Class clazz;
    try {
      clazz = this.repository.loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      if (this.cl2 != null)
        return this.cl2.loadClass(paramString); 
      throw classNotFoundException;
    } 
    if (!clazz.getName().equals(paramString)) {
      if (this.cl2 != null)
        return this.cl2.loadClass(paramString); 
      throw new ClassNotFoundException(paramString);
    } 
    return clazz;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remot\\util\ClassLoaderWithRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */