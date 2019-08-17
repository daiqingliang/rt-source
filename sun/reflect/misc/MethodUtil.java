package sun.reflect.misc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;
import sun.misc.IOUtils;

public final class MethodUtil extends SecureClassLoader {
  private static final String MISC_PKG = "sun.reflect.misc.";
  
  private static final String TRAMPOLINE = "sun.reflect.misc.Trampoline";
  
  private static final Method bounce = getTrampoline();
  
  public static Method getMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass) throws NoSuchMethodException {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getMethod(paramString, paramArrayOfClass);
  }
  
  public static Method[] getMethods(Class<?> paramClass) {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getMethods();
  }
  
  public static Method[] getPublicMethods(Class<?> paramClass) {
    if (System.getSecurityManager() == null)
      return paramClass.getMethods(); 
    HashMap hashMap = new HashMap();
    while (paramClass != null) {
      boolean bool = getInternalPublicMethods(paramClass, hashMap);
      if (bool)
        break; 
      getInterfaceMethods(paramClass, hashMap);
      paramClass = paramClass.getSuperclass();
    } 
    return (Method[])hashMap.values().toArray(new Method[hashMap.size()]);
  }
  
  private static void getInterfaceMethods(Class<?> paramClass, Map<Signature, Method> paramMap) {
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      Class clazz = arrayOfClass[b];
      boolean bool = getInternalPublicMethods(clazz, paramMap);
      if (!bool)
        getInterfaceMethods(clazz, paramMap); 
    } 
  }
  
  private static boolean getInternalPublicMethods(Class<?> paramClass, Map<Signature, Method> paramMap) {
    Method[] arrayOfMethod = null;
    try {
      if (!Modifier.isPublic(paramClass.getModifiers()))
        return false; 
      if (!ReflectUtil.isPackageAccessible(paramClass))
        return false; 
      arrayOfMethod = paramClass.getMethods();
    } catch (SecurityException securityException) {
      return false;
    } 
    boolean bool = true;
    byte b;
    for (b = 0; b < arrayOfMethod.length; b++) {
      Class clazz = arrayOfMethod[b].getDeclaringClass();
      if (!Modifier.isPublic(clazz.getModifiers())) {
        bool = false;
        break;
      } 
    } 
    if (bool) {
      for (b = 0; b < arrayOfMethod.length; b++)
        addMethod(paramMap, arrayOfMethod[b]); 
    } else {
      for (b = 0; b < arrayOfMethod.length; b++) {
        Class clazz = arrayOfMethod[b].getDeclaringClass();
        if (paramClass.equals(clazz))
          addMethod(paramMap, arrayOfMethod[b]); 
      } 
    } 
    return bool;
  }
  
  private static void addMethod(Map<Signature, Method> paramMap, Method paramMethod) {
    Signature signature = new Signature(paramMethod);
    if (!paramMap.containsKey(signature)) {
      paramMap.put(signature, paramMethod);
    } else if (!paramMethod.getDeclaringClass().isInterface()) {
      Method method = (Method)paramMap.get(signature);
      if (method.getDeclaringClass().isInterface())
        paramMap.put(signature, paramMethod); 
    } 
  }
  
  public static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject) throws InvocationTargetException, IllegalAccessException {
    try {
      return bounce.invoke(null, new Object[] { paramMethod, paramObject, paramArrayOfObject });
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (throwable instanceof InvocationTargetException)
        throw (InvocationTargetException)throwable; 
      if (throwable instanceof IllegalAccessException)
        throw (IllegalAccessException)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new Error("Unexpected invocation error", throwable);
    } catch (IllegalAccessException illegalAccessException) {
      throw new Error("Unexpected invocation error", illegalAccessException);
    } 
  }
  
  private static Method getTrampoline() {
    try {
      return (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() {
              Class clazz = MethodUtil.getTrampolineClass();
              Class[] arrayOfClass = { Method.class, Object.class, Object[].class };
              Method method = clazz.getDeclaredMethod("invoke", arrayOfClass);
              method.setAccessible(true);
              return method;
            }
          });
    } catch (Exception exception) {
      throw new InternalError("bouncer cannot be found", exception);
    } 
  }
  
  protected Class<?> loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
    ReflectUtil.checkPackageAccess(paramString);
    Class clazz = findLoadedClass(paramString);
    if (clazz == null) {
      try {
        clazz = findClass(paramString);
      } catch (ClassNotFoundException classNotFoundException) {}
      if (clazz == null)
        clazz = getParent().loadClass(paramString); 
    } 
    if (paramBoolean)
      resolveClass(clazz); 
    return clazz;
  }
  
  protected Class<?> findClass(String paramString) throws ClassNotFoundException {
    if (!paramString.startsWith("sun.reflect.misc."))
      throw new ClassNotFoundException(paramString); 
    String str = paramString.replace('.', '/').concat(".class");
    URL uRL = getResource(str);
    if (uRL != null)
      try {
        return defineClass(paramString, uRL);
      } catch (IOException iOException) {
        throw new ClassNotFoundException(paramString, iOException);
      }  
    throw new ClassNotFoundException(paramString);
  }
  
  private Class<?> defineClass(String paramString, URL paramURL) throws IOException {
    byte[] arrayOfByte = getBytes(paramURL);
    CodeSource codeSource = new CodeSource(null, (Certificate[])null);
    if (!paramString.equals("sun.reflect.misc.Trampoline"))
      throw new IOException("MethodUtil: bad name " + paramString); 
    return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length, codeSource);
  }
  
  private static byte[] getBytes(URL paramURL) throws IOException {
    byte[] arrayOfByte;
    URLConnection uRLConnection = paramURL.openConnection();
    if (uRLConnection instanceof HttpURLConnection) {
      HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
      int j = httpURLConnection.getResponseCode();
      if (j >= 400)
        throw new IOException("open HTTP connection failed."); 
    } 
    int i = uRLConnection.getContentLength();
    bufferedInputStream = new BufferedInputStream(uRLConnection.getInputStream());
    try {
      arrayOfByte = IOUtils.readFully(bufferedInputStream, i, true);
    } finally {
      bufferedInputStream.close();
    } 
    return arrayOfByte;
  }
  
  protected PermissionCollection getPermissions(CodeSource paramCodeSource) {
    PermissionCollection permissionCollection = super.getPermissions(paramCodeSource);
    permissionCollection.add(new AllPermission());
    return permissionCollection;
  }
  
  private static Class<?> getTrampolineClass() {
    try {
      return Class.forName("sun.reflect.misc.Trampoline", true, new MethodUtil());
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } 
  }
  
  private static class Signature {
    private String methodName;
    
    private Class<?>[] argClasses;
    
    Signature(Method param1Method) {
      this.methodName = param1Method.getName();
      this.argClasses = param1Method.getParameterTypes();
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      Signature signature = (Signature)param1Object;
      if (!this.methodName.equals(signature.methodName))
        return false; 
      if (this.argClasses.length != signature.argClasses.length)
        return false; 
      for (byte b = 0; b < this.argClasses.length; b++) {
        if (this.argClasses[b] != signature.argClasses[b])
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      if (this.hashCode == 0) {
        int i = 17;
        i = 37 * i + this.methodName.hashCode();
        if (this.argClasses != null)
          for (byte b = 0; b < this.argClasses.length; b++)
            i = 37 * i + ((this.argClasses[b] == null) ? 0 : this.argClasses[b].hashCode());  
        this.hashCode = i;
      } 
      return this.hashCode;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\misc\MethodUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */