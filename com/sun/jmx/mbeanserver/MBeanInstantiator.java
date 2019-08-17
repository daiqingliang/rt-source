package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.logging.Level;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import sun.reflect.misc.ConstructorUtil;
import sun.reflect.misc.ReflectUtil;

public class MBeanInstantiator {
  private final ModifiableClassLoaderRepository clr;
  
  private static final Map<String, Class<?>> primitiveClasses = Util.newMap();
  
  MBeanInstantiator(ModifiableClassLoaderRepository paramModifiableClassLoaderRepository) { this.clr = paramModifiableClassLoaderRepository; }
  
  public void testCreation(Class<?> paramClass) throws NotCompliantMBeanException { Introspector.testCreation(paramClass); }
  
  public Class<?> findClassWithDefaultLoaderRepository(String paramString) throws ReflectionException {
    Class clazz;
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation"); 
    ReflectUtil.checkPackageAccess(paramString);
    try {
      if (this.clr == null)
        throw new ClassNotFoundException(paramString); 
      clazz = this.clr.loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new ReflectionException(classNotFoundException, "The MBean class could not be loaded by the default loader repository");
    } 
    return clazz;
  }
  
  public Class<?> findClass(String paramString, ClassLoader paramClassLoader) throws ReflectionException { return loadClass(paramString, paramClassLoader); }
  
  public Class<?> findClass(String paramString, ObjectName paramObjectName) throws ReflectionException, InstanceNotFoundException {
    if (paramObjectName == null)
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null loader passed in parameter"); 
    ClassLoader classLoader = null;
    synchronized (this) {
      classLoader = getClassLoader(paramObjectName);
    } 
    if (classLoader == null)
      throw new InstanceNotFoundException("The loader named " + paramObjectName + " is not registered in the MBeanServer"); 
    return findClass(paramString, classLoader);
  }
  
  public Class<?>[] findSignatureClasses(String[] paramArrayOfString, ClassLoader paramClassLoader) throws ReflectionException {
    if (paramArrayOfString == null)
      return null; 
    ClassLoader classLoader = paramClassLoader;
    int i = paramArrayOfString.length;
    Class[] arrayOfClass = new Class[i];
    if (i == 0)
      return arrayOfClass; 
    try {
      for (byte b = 0; b < i; b++) {
        Class clazz = (Class)primitiveClasses.get(paramArrayOfString[b]);
        if (clazz != null) {
          arrayOfClass[b] = clazz;
        } else {
          ReflectUtil.checkPackageAccess(paramArrayOfString[b]);
          if (classLoader != null) {
            arrayOfClass[b] = Class.forName(paramArrayOfString[b], false, classLoader);
          } else {
            arrayOfClass[b] = findClass(paramArrayOfString[b], getClass().getClassLoader());
          } 
        } 
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "The parameter class could not be found", classNotFoundException); 
      throw new ReflectionException(classNotFoundException, "The parameter class could not be found");
    } catch (RuntimeException runtimeException) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "Unexpected exception", runtimeException); 
      throw runtimeException;
    } 
    return arrayOfClass;
  }
  
  public Object instantiate(Class<?> paramClass) throws ReflectionException, MBeanException {
    Object object;
    checkMBeanPermission(paramClass, null, null, "instantiate");
    Constructor constructor = findConstructor(paramClass, null);
    if (constructor == null)
      throw new ReflectionException(new NoSuchMethodException("No such constructor")); 
    try {
      ReflectUtil.checkPackageAccess(paramClass);
      ensureClassAccess(paramClass);
      object = constructor.newInstance(new Object[0]);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      if (throwable instanceof RuntimeException)
        throw new RuntimeMBeanException((RuntimeException)throwable, "RuntimeException thrown in the MBean's empty constructor"); 
      if (throwable instanceof Error)
        throw new RuntimeErrorException((Error)throwable, "Error thrown in the MBean's empty constructor"); 
      throw new MBeanException((Exception)throwable, "Exception thrown in the MBean's empty constructor");
    } catch (NoSuchMethodError noSuchMethodError) {
      throw new ReflectionException(new NoSuchMethodException("No constructor"), "No such constructor");
    } catch (InstantiationException instantiationException) {
      throw new ReflectionException(instantiationException, "Exception thrown trying to invoke the MBean's empty constructor");
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionException(illegalAccessException, "Exception thrown trying to invoke the MBean's empty constructor");
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ReflectionException(illegalArgumentException, "Exception thrown trying to invoke the MBean's empty constructor");
    } 
    return object;
  }
  
  public Object instantiate(Class<?> paramClass, Object[] paramArrayOfObject, String[] paramArrayOfString, ClassLoader paramClassLoader) throws ReflectionException, MBeanException {
    Object object;
    Class[] arrayOfClass;
    checkMBeanPermission(paramClass, null, null, "instantiate");
    try {
      ClassLoader classLoader = paramClass.getClassLoader();
      arrayOfClass = (paramArrayOfString == null) ? null : findSignatureClasses(paramArrayOfString, classLoader);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ReflectionException(illegalArgumentException, "The constructor parameter classes could not be loaded");
    } 
    Constructor constructor = findConstructor(paramClass, arrayOfClass);
    if (constructor == null)
      throw new ReflectionException(new NoSuchMethodException("No such constructor")); 
    try {
      ReflectUtil.checkPackageAccess(paramClass);
      ensureClassAccess(paramClass);
      object = constructor.newInstance(paramArrayOfObject);
    } catch (NoSuchMethodError noSuchMethodError) {
      throw new ReflectionException(new NoSuchMethodException("No such constructor found"), "No such constructor");
    } catch (InstantiationException instantiationException) {
      throw new ReflectionException(instantiationException, "Exception thrown trying to invoke the MBean's constructor");
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionException(illegalAccessException, "Exception thrown trying to invoke the MBean's constructor");
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      if (throwable instanceof RuntimeException)
        throw new RuntimeMBeanException((RuntimeException)throwable, "RuntimeException thrown in the MBean's constructor"); 
      if (throwable instanceof Error)
        throw new RuntimeErrorException((Error)throwable, "Error thrown in the MBean's constructor"); 
      throw new MBeanException((Exception)throwable, "Exception thrown in the MBean's constructor");
    } 
    return object;
  }
  
  public ObjectInputStream deserialize(ClassLoader paramClassLoader, byte[] paramArrayOfByte) throws OperationsException {
    ObjectInputStreamWithLoader objectInputStreamWithLoader;
    if (paramArrayOfByte == null)
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter"); 
    if (paramArrayOfByte.length == 0)
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter"); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    try {
      objectInputStreamWithLoader = new ObjectInputStreamWithLoader(byteArrayInputStream, paramClassLoader);
    } catch (IOException iOException) {
      throw new OperationsException("An IOException occurred trying to de-serialize the data");
    } 
    return objectInputStreamWithLoader;
  }
  
  public ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte, ClassLoader paramClassLoader) throws InstanceNotFoundException, OperationsException, ReflectionException {
    ObjectInputStreamWithLoader objectInputStreamWithLoader;
    Class clazz;
    if (paramArrayOfByte == null)
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter"); 
    if (paramArrayOfByte.length == 0)
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter"); 
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter"); 
    ReflectUtil.checkPackageAccess(paramString);
    if (paramObjectName == null) {
      clazz = findClass(paramString, paramClassLoader);
    } else {
      try {
        ClassLoader classLoader = null;
        classLoader = getClassLoader(paramObjectName);
        if (classLoader == null)
          throw new ClassNotFoundException(paramString); 
        clazz = Class.forName(paramString, false, classLoader);
      } catch (ClassNotFoundException classNotFoundException) {
        throw new ReflectionException(classNotFoundException, "The MBean class could not be loaded by the " + paramObjectName.toString() + " class loader");
      } 
    } 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    try {
      objectInputStreamWithLoader = new ObjectInputStreamWithLoader(byteArrayInputStream, clazz.getClassLoader());
    } catch (IOException iOException) {
      throw new OperationsException("An IOException occurred trying to de-serialize the data");
    } 
    return objectInputStreamWithLoader;
  }
  
  public Object instantiate(String paramString) throws ReflectionException, MBeanException { return instantiate(paramString, (Object[])null, (String[])null, null); }
  
  public Object instantiate(String paramString, ObjectName paramObjectName, ClassLoader paramClassLoader) throws ReflectionException, MBeanException, InstanceNotFoundException { return instantiate(paramString, paramObjectName, (Object[])null, (String[])null, paramClassLoader); }
  
  public Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString, ClassLoader paramClassLoader) throws ReflectionException, MBeanException {
    Class clazz = findClassWithDefaultLoaderRepository(paramString);
    return instantiate(clazz, paramArrayOfObject, paramArrayOfString, paramClassLoader);
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString, ClassLoader paramClassLoader) throws ReflectionException, MBeanException, InstanceNotFoundException {
    Class clazz;
    if (paramObjectName == null) {
      clazz = findClass(paramString, paramClassLoader);
    } else {
      clazz = findClass(paramString, paramObjectName);
    } 
    return instantiate(clazz, paramArrayOfObject, paramArrayOfString, paramClassLoader);
  }
  
  public ModifiableClassLoaderRepository getClassLoaderRepository() {
    checkMBeanPermission((String)null, null, null, "getClassLoaderRepository");
    return this.clr;
  }
  
  static Class<?> loadClass(String paramString, ClassLoader paramClassLoader) throws ReflectionException {
    Class clazz;
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation"); 
    ReflectUtil.checkPackageAccess(paramString);
    try {
      if (paramClassLoader == null)
        paramClassLoader = MBeanInstantiator.class.getClassLoader(); 
      if (paramClassLoader != null) {
        clazz = Class.forName(paramString, false, paramClassLoader);
      } else {
        clazz = Class.forName(paramString);
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      throw new ReflectionException(classNotFoundException, "The MBean class could not be loaded");
    } 
    return clazz;
  }
  
  static Class<?>[] loadSignatureClasses(String[] paramArrayOfString, ClassLoader paramClassLoader) throws ReflectionException {
    if (paramArrayOfString == null)
      return null; 
    ClassLoader classLoader = (paramClassLoader == null) ? MBeanInstantiator.class.getClassLoader() : paramClassLoader;
    int i = paramArrayOfString.length;
    Class[] arrayOfClass = new Class[i];
    if (i == 0)
      return arrayOfClass; 
    try {
      for (byte b = 0; b < i; b++) {
        Class clazz = (Class)primitiveClasses.get(paramArrayOfString[b]);
        if (clazz != null) {
          arrayOfClass[b] = clazz;
        } else {
          ReflectUtil.checkPackageAccess(paramArrayOfString[b]);
          arrayOfClass[b] = Class.forName(paramArrayOfString[b], false, classLoader);
        } 
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "The parameter class could not be found", classNotFoundException); 
      throw new ReflectionException(classNotFoundException, "The parameter class could not be found");
    } catch (RuntimeException runtimeException) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "Unexpected exception", runtimeException); 
      throw runtimeException;
    } 
    return arrayOfClass;
  }
  
  private Constructor<?> findConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass) {
    try {
      return ConstructorUtil.getConstructor(paramClass, paramArrayOfClass);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  private static void checkMBeanPermission(Class<?> paramClass, String paramString1, ObjectName paramObjectName, String paramString2) {
    if (paramClass != null)
      checkMBeanPermission(paramClass.getName(), paramString1, paramObjectName, paramString2); 
  }
  
  private static void checkMBeanPermission(String paramString1, String paramString2, ObjectName paramObjectName, String paramString3) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      MBeanPermission mBeanPermission = new MBeanPermission(paramString1, paramString2, paramObjectName, paramString3);
      securityManager.checkPermission(mBeanPermission);
    } 
  }
  
  private static void ensureClassAccess(Class paramClass) throws IllegalAccessException {
    int i = paramClass.getModifiers();
    if (!Modifier.isPublic(i))
      throw new IllegalAccessException("Class is not public and can't be instantiated"); 
  }
  
  private ClassLoader getClassLoader(final ObjectName name) {
    if (this.clr == null)
      return null; 
    Permissions permissions = new Permissions();
    permissions.add(new MBeanPermission("*", null, paramObjectName, "getClassLoader"));
    ProtectionDomain protectionDomain = new ProtectionDomain(null, permissions);
    ProtectionDomain[] arrayOfProtectionDomain = { protectionDomain };
    AccessControlContext accessControlContext = new AccessControlContext(arrayOfProtectionDomain);
    return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return MBeanInstantiator.this.clr.getClassLoader(name); }
        },  accessControlContext);
  }
  
  static  {
    for (Class clazz : new Class[] { byte.class, short.class, int.class, long.class, float.class, double.class, char.class, boolean.class })
      primitiveClasses.put(clazz.getName(), clazz); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MBeanInstantiator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */