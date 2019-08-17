package java.lang.reflect;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.WeakCache;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import sun.misc.ProxyGenerator;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class Proxy implements Serializable {
  private static final long serialVersionUID = -2222568056686623797L;
  
  private static final Class<?>[] constructorParams = { InvocationHandler.class };
  
  private static final WeakCache<ClassLoader, Class<?>[], Class<?>> proxyClassCache = new WeakCache(new KeyFactory(null), new ProxyClassFactory(null));
  
  protected InvocationHandler h;
  
  private static final Object key0 = new Object();
  
  private Proxy() {}
  
  protected Proxy(InvocationHandler paramInvocationHandler) {
    Objects.requireNonNull(paramInvocationHandler);
    this.h = paramInvocationHandler;
  }
  
  @CallerSensitive
  public static Class<?> getProxyClass(ClassLoader paramClassLoader, Class<?>... paramVarArgs) throws IllegalArgumentException {
    Class[] arrayOfClass = (Class[])paramVarArgs.clone();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      checkProxyAccess(Reflection.getCallerClass(), paramClassLoader, arrayOfClass); 
    return getProxyClass0(paramClassLoader, arrayOfClass);
  }
  
  private static void checkProxyAccess(Class<?> paramClass, ClassLoader paramClassLoader, Class<?>... paramVarArgs) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      ClassLoader classLoader = paramClass.getClassLoader();
      if (VM.isSystemDomainLoader(paramClassLoader) && !VM.isSystemDomainLoader(classLoader))
        securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION); 
      ReflectUtil.checkProxyPackageAccess(classLoader, paramVarArgs);
    } 
  }
  
  private static Class<?> getProxyClass0(ClassLoader paramClassLoader, Class<?>... paramVarArgs) throws IllegalArgumentException {
    if (paramVarArgs.length > 65535)
      throw new IllegalArgumentException("interface limit exceeded"); 
    return (Class)proxyClassCache.get(paramClassLoader, paramVarArgs);
  }
  
  @CallerSensitive
  public static Object newProxyInstance(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass, InvocationHandler paramInvocationHandler) throws IllegalArgumentException {
    Objects.requireNonNull(paramInvocationHandler);
    Class[] arrayOfClass = (Class[])paramArrayOfClass.clone();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      checkProxyAccess(Reflection.getCallerClass(), paramClassLoader, arrayOfClass); 
    Class clazz = getProxyClass0(paramClassLoader, arrayOfClass);
    try {
      if (securityManager != null)
        checkNewProxyPermission(Reflection.getCallerClass(), clazz); 
      final Constructor cons = clazz.getConstructor(constructorParams);
      InvocationHandler invocationHandler = paramInvocationHandler;
      if (!Modifier.isPublic(clazz.getModifiers()))
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                cons.setAccessible(true);
                return null;
              }
            }); 
      return constructor.newInstance(new Object[] { paramInvocationHandler });
    } catch (IllegalAccessException|InstantiationException illegalAccessException) {
      throw new InternalError(illegalAccessException.toString(), illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      throw new InternalError(throwable.toString(), throwable);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new InternalError(noSuchMethodException.toString(), noSuchMethodException);
    } 
  }
  
  private static void checkNewProxyPermission(Class<?> paramClass1, Class<?> paramClass2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && ReflectUtil.isNonPublicProxyClass(paramClass2)) {
      ClassLoader classLoader1 = paramClass1.getClassLoader();
      ClassLoader classLoader2 = paramClass2.getClassLoader();
      int i = paramClass2.getName().lastIndexOf('.');
      String str1 = (i == -1) ? "" : paramClass2.getName().substring(0, i);
      i = paramClass1.getName().lastIndexOf('.');
      String str2 = (i == -1) ? "" : paramClass1.getName().substring(0, i);
      if (classLoader2 != classLoader1 || !str1.equals(str2))
        securityManager.checkPermission(new ReflectPermission("newProxyInPackage." + str1)); 
    } 
  }
  
  public static boolean isProxyClass(Class<?> paramClass) { return (Proxy.class.isAssignableFrom(paramClass) && proxyClassCache.containsValue(paramClass)); }
  
  @CallerSensitive
  public static InvocationHandler getInvocationHandler(Object paramObject) throws IllegalArgumentException {
    if (!isProxyClass(paramObject.getClass()))
      throw new IllegalArgumentException("not a proxy instance"); 
    Proxy proxy = (Proxy)paramObject;
    InvocationHandler invocationHandler = proxy.h;
    if (System.getSecurityManager() != null) {
      Class clazz1 = invocationHandler.getClass();
      Class clazz2 = Reflection.getCallerClass();
      if (ReflectUtil.needsPackageAccessCheck(clazz2.getClassLoader(), clazz1.getClassLoader()))
        ReflectUtil.checkPackageAccess(clazz1); 
    } 
    return invocationHandler;
  }
  
  private static native Class<?> defineClass0(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static final class Key1 extends WeakReference<Class<?>> {
    private final int hash;
    
    Key1(Class<?> param1Class) {
      super(param1Class);
      this.hash = param1Class.hashCode();
    }
    
    public int hashCode() { return this.hash; }
    
    public boolean equals(Object param1Object) {
      Class clazz;
      return (this == param1Object || (param1Object != null && param1Object.getClass() == Key1.class && (clazz = (Class)get()) != null && clazz == ((Key1)param1Object).get()));
    }
  }
  
  private static final class Key2 extends WeakReference<Class<?>> {
    private final int hash;
    
    private final WeakReference<Class<?>> ref2;
    
    Key2(Class<?> param1Class1, Class<?> param1Class2) {
      super(param1Class1);
      this.hash = 31 * param1Class1.hashCode() + param1Class2.hashCode();
      this.ref2 = new WeakReference(param1Class2);
    }
    
    public int hashCode() { return this.hash; }
    
    public boolean equals(Object param1Object) {
      Class clazz1;
      Class clazz2;
      return (this == param1Object || (param1Object != null && param1Object.getClass() == Key2.class && (clazz1 = (Class)get()) != null && clazz1 == ((Key2)param1Object).get() && (clazz2 = (Class)this.ref2.get()) != null && clazz2 == ((Key2)param1Object).ref2.get()));
    }
  }
  
  private static final class KeyFactory extends Object implements BiFunction<ClassLoader, Class<?>[], Object> {
    private KeyFactory() {}
    
    public Object apply(ClassLoader param1ClassLoader, Class<?>[] param1ArrayOfClass) {
      switch (param1ArrayOfClass.length) {
        case 1:
          return new Proxy.Key1(param1ArrayOfClass[0]);
        case 2:
          return new Proxy.Key2(param1ArrayOfClass[0], param1ArrayOfClass[1]);
        case 0:
          return key0;
      } 
      return new Proxy.KeyX(param1ArrayOfClass);
    }
  }
  
  private static final class KeyX {
    private final int hash;
    
    private final WeakReference<Class<?>>[] refs;
    
    KeyX(Class<?>[] param1ArrayOfClass) {
      this.hash = Arrays.hashCode(param1ArrayOfClass);
      this.refs = (WeakReference[])new WeakReference[param1ArrayOfClass.length];
      for (byte b = 0; b < param1ArrayOfClass.length; b++)
        this.refs[b] = new WeakReference(param1ArrayOfClass[b]); 
    }
    
    public int hashCode() { return this.hash; }
    
    public boolean equals(Object param1Object) { return (this == param1Object || (param1Object != null && param1Object.getClass() == KeyX.class && equals(this.refs, ((KeyX)param1Object).refs))); }
    
    private static boolean equals(WeakReference<Class<?>>[] param1ArrayOfWeakReference1, WeakReference<Class<?>>[] param1ArrayOfWeakReference2) {
      if (param1ArrayOfWeakReference1.length != param1ArrayOfWeakReference2.length)
        return false; 
      for (byte b = 0; b < param1ArrayOfWeakReference1.length; b++) {
        Class clazz = (Class)param1ArrayOfWeakReference1[b].get();
        if (clazz == null || clazz != param1ArrayOfWeakReference2[b].get())
          return false; 
      } 
      return true;
    }
  }
  
  private static final class ProxyClassFactory extends Object implements BiFunction<ClassLoader, Class<?>[], Class<?>> {
    private static final String proxyClassNamePrefix = "$Proxy";
    
    private static final AtomicLong nextUniqueNumber = new AtomicLong();
    
    private ProxyClassFactory() {}
    
    public Class<?> apply(ClassLoader param1ClassLoader, Class<?>[] param1ArrayOfClass) throws IllegalArgumentException {
      IdentityHashMap identityHashMap = new IdentityHashMap(param1ArrayOfClass.length);
      for (Class<?> clazz1 : param1ArrayOfClass) {
        Class clazz2 = null;
        try {
          clazz2 = Class.forName(clazz1.getName(), false, param1ClassLoader);
        } catch (ClassNotFoundException classNotFoundException) {}
        if (clazz2 != clazz1)
          throw new IllegalArgumentException(clazz1 + " is not visible from class loader"); 
        if (!clazz2.isInterface())
          throw new IllegalArgumentException(clazz2.getName() + " is not an interface"); 
        if (identityHashMap.put(clazz2, Boolean.TRUE) != null)
          throw new IllegalArgumentException("repeated interface: " + clazz2.getName()); 
      } 
      String str1 = null;
      byte b = 17;
      for (Class<?> clazz : param1ArrayOfClass) {
        int i = clazz.getModifiers();
        if (!Modifier.isPublic(i)) {
          b = 16;
          String str3 = clazz.getName();
          int j = str3.lastIndexOf('.');
          String str4 = (j == -1) ? "" : str3.substring(0, j + 1);
          if (str1 == null) {
            str1 = str4;
          } else if (!str4.equals(str1)) {
            throw new IllegalArgumentException("non-public interfaces from different packages");
          } 
        } 
      } 
      if (str1 == null)
        str1 = "com.sun.proxy."; 
      long l = nextUniqueNumber.getAndIncrement();
      String str2 = str1 + "$Proxy" + l;
      byte[] arrayOfByte = ProxyGenerator.generateProxyClass(str2, param1ArrayOfClass, b);
      try {
        return Proxy.defineClass0(param1ClassLoader, str2, arrayOfByte, 0, arrayOfByte.length);
      } catch (ClassFormatError classFormatError) {
        throw new IllegalArgumentException(classFormatError.toString());
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\Proxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */