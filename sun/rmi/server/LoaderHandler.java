package sun.rmi.server;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.rmi.server.LogStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.reflect.misc.ReflectUtil;
import sun.rmi.runtime.Log;
import sun.security.action.GetPropertyAction;

public final class LoaderHandler {
  static final int logLevel = LogStream.parseLevel((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.loader.logLevel")));
  
  static final Log loaderLog = Log.getLog("sun.rmi.loader", "loader", logLevel);
  
  private static String codebaseProperty = null;
  
  private static URL[] codebaseURLs;
  
  private static final Map<ClassLoader, Void> codebaseLoaders;
  
  private static final HashMap<LoaderKey, LoaderEntry> loaderTable;
  
  private static final ReferenceQueue<Loader> refQueue;
  
  private static final Map<String, Object[]> pathToURLsCache;
  
  private static URL[] getDefaultCodebaseURLs() throws MalformedURLException {
    if (codebaseURLs == null)
      if (codebaseProperty != null) {
        codebaseURLs = pathToURLs(codebaseProperty);
      } else {
        codebaseURLs = new URL[0];
      }  
    return codebaseURLs;
  }
  
  public static Class<?> loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader) throws MalformedURLException, ClassNotFoundException {
    URL[] arrayOfURL;
    if (loaderLog.isLoggable(Log.BRIEF))
      loaderLog.log(Log.BRIEF, "name = \"" + paramString2 + "\", codebase = \"" + ((paramString1 != null) ? paramString1 : "") + "\"" + ((paramClassLoader != null) ? (", defaultLoader = " + paramClassLoader) : "")); 
    if (paramString1 != null) {
      arrayOfURL = pathToURLs(paramString1);
    } else {
      arrayOfURL = getDefaultCodebaseURLs();
    } 
    if (paramClassLoader != null)
      try {
        Class clazz = loadClassForName(paramString2, false, paramClassLoader);
        if (loaderLog.isLoggable(Log.VERBOSE))
          loaderLog.log(Log.VERBOSE, "class \"" + paramString2 + "\" found via defaultLoader, defined by " + clazz.getClassLoader()); 
        return clazz;
      } catch (ClassNotFoundException classNotFoundException) {} 
    return loadClass(arrayOfURL, paramString2);
  }
  
  public static String getClassAnnotation(Class<?> paramClass) {
    String str1 = paramClass.getName();
    int i = str1.length();
    if (i > 0 && str1.charAt(0) == '[') {
      byte b;
      for (b = 1; i > b && str1.charAt(b) == '['; b++);
      if (i > b && str1.charAt(b) != 'L')
        return null; 
    } 
    ClassLoader classLoader = paramClass.getClassLoader();
    if (classLoader == null || codebaseLoaders.containsKey(classLoader))
      return codebaseProperty; 
    String str2 = null;
    if (classLoader instanceof Loader) {
      str2 = ((Loader)classLoader).getClassAnnotation();
    } else if (classLoader instanceof URLClassLoader) {
      try {
        URL[] arrayOfURL = ((URLClassLoader)classLoader).getURLs();
        if (arrayOfURL != null) {
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null) {
            Permissions permissions = new Permissions();
            for (byte b = 0; b < arrayOfURL.length; b++) {
              Permission permission = arrayOfURL[b].openConnection().getPermission();
              if (permission != null && !permissions.implies(permission)) {
                securityManager.checkPermission(permission);
                permissions.add(permission);
              } 
            } 
          } 
          str2 = urlsToPath(arrayOfURL);
        } 
      } catch (SecurityException|IOException securityException) {}
    } 
    return (str2 != null) ? str2 : codebaseProperty;
  }
  
  public static ClassLoader getClassLoader(String paramString) throws MalformedURLException {
    URL[] arrayOfURL;
    ClassLoader classLoader = getRMIContextClassLoader();
    if (paramString != null) {
      arrayOfURL = pathToURLs(paramString);
    } else {
      arrayOfURL = getDefaultCodebaseURLs();
    } 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkPermission(new RuntimePermission("getClassLoader"));
    } else {
      return classLoader;
    } 
    Loader loader = lookupLoader(arrayOfURL, classLoader);
    if (loader != null)
      loader.checkPermissions(); 
    return loader;
  }
  
  public static Object getSecurityContext(ClassLoader paramClassLoader) {
    if (paramClassLoader instanceof Loader) {
      URL[] arrayOfURL = ((Loader)paramClassLoader).getURLs();
      if (arrayOfURL.length > 0)
        return arrayOfURL[0]; 
    } 
    return null;
  }
  
  public static void registerCodebaseLoader(ClassLoader paramClassLoader) { codebaseLoaders.put(paramClassLoader, null); }
  
  private static Class<?> loadClass(URL[] paramArrayOfURL, String paramString) throws ClassNotFoundException {
    ClassLoader classLoader = getRMIContextClassLoader();
    if (loaderLog.isLoggable(Log.VERBOSE))
      loaderLog.log(Log.VERBOSE, "(thread context class loader: " + classLoader + ")"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      try {
        Class clazz = Class.forName(paramString, false, classLoader);
        if (loaderLog.isLoggable(Log.VERBOSE))
          loaderLog.log(Log.VERBOSE, "class \"" + paramString + "\" found via thread context class loader (no security manager: codebase disabled), defined by " + clazz.getClassLoader()); 
        return clazz;
      } catch (ClassNotFoundException classNotFoundException) {
        if (loaderLog.isLoggable(Log.BRIEF))
          loaderLog.log(Log.BRIEF, "class \"" + paramString + "\" not found via thread context class loader (no security manager: codebase disabled)", classNotFoundException); 
        throw new ClassNotFoundException(classNotFoundException.getMessage() + " (no security manager: RMI class loader disabled)", classNotFoundException.getException());
      }  
    Loader loader = lookupLoader(paramArrayOfURL, classLoader);
    try {
      if (loader != null)
        loader.checkPermissions(); 
    } catch (SecurityException securityException) {
      try {
        Class clazz = loadClassForName(paramString, false, classLoader);
        if (loaderLog.isLoggable(Log.VERBOSE))
          loaderLog.log(Log.VERBOSE, "class \"" + paramString + "\" found via thread context class loader (access to codebase denied), defined by " + clazz.getClassLoader()); 
        return clazz;
      } catch (ClassNotFoundException classNotFoundException) {
        if (loaderLog.isLoggable(Log.BRIEF))
          loaderLog.log(Log.BRIEF, "class \"" + paramString + "\" not found via thread context class loader (access to codebase denied)", securityException); 
        throw new ClassNotFoundException("access to class loader denied", securityException);
      } 
    } 
    try {
      Class clazz = loadClassForName(paramString, false, loader);
      if (loaderLog.isLoggable(Log.VERBOSE))
        loaderLog.log(Log.VERBOSE, "class \"" + paramString + "\" found via codebase, defined by " + clazz.getClassLoader()); 
      return clazz;
    } catch (ClassNotFoundException classNotFoundException) {
      if (loaderLog.isLoggable(Log.BRIEF))
        loaderLog.log(Log.BRIEF, "class \"" + paramString + "\" not found via codebase", classNotFoundException); 
      throw classNotFoundException;
    } 
  }
  
  public static Class<?> loadProxyClass(String paramString, String[] paramArrayOfString, ClassLoader paramClassLoader) throws MalformedURLException, ClassNotFoundException {
    URL[] arrayOfURL;
    if (loaderLog.isLoggable(Log.BRIEF))
      loaderLog.log(Log.BRIEF, "interfaces = " + Arrays.asList(paramArrayOfString) + ", codebase = \"" + ((paramString != null) ? paramString : "") + "\"" + ((paramClassLoader != null) ? (", defaultLoader = " + paramClassLoader) : "")); 
    ClassLoader classLoader = getRMIContextClassLoader();
    if (loaderLog.isLoggable(Log.VERBOSE))
      loaderLog.log(Log.VERBOSE, "(thread context class loader: " + classLoader + ")"); 
    if (paramString != null) {
      arrayOfURL = pathToURLs(paramString);
    } else {
      arrayOfURL = getDefaultCodebaseURLs();
    } 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      try {
        Class clazz = loadProxyClass(paramArrayOfString, paramClassLoader, classLoader, false);
        if (loaderLog.isLoggable(Log.VERBOSE))
          loaderLog.log(Log.VERBOSE, "(no security manager: codebase disabled) proxy class defined by " + clazz.getClassLoader()); 
        return clazz;
      } catch (ClassNotFoundException classNotFoundException) {
        if (loaderLog.isLoggable(Log.BRIEF))
          loaderLog.log(Log.BRIEF, "(no security manager: codebase disabled) proxy class resolution failed", classNotFoundException); 
        throw new ClassNotFoundException(classNotFoundException.getMessage() + " (no security manager: RMI class loader disabled)", classNotFoundException.getException());
      }  
    Loader loader = lookupLoader(arrayOfURL, classLoader);
    try {
      if (loader != null)
        loader.checkPermissions(); 
    } catch (SecurityException securityException) {
      try {
        Class clazz = loadProxyClass(paramArrayOfString, paramClassLoader, classLoader, false);
        if (loaderLog.isLoggable(Log.VERBOSE))
          loaderLog.log(Log.VERBOSE, "(access to codebase denied) proxy class defined by " + clazz.getClassLoader()); 
        return clazz;
      } catch (ClassNotFoundException classNotFoundException) {
        if (loaderLog.isLoggable(Log.BRIEF))
          loaderLog.log(Log.BRIEF, "(access to codebase denied) proxy class resolution failed", securityException); 
        throw new ClassNotFoundException("access to class loader denied", securityException);
      } 
    } 
    try {
      Class clazz = loadProxyClass(paramArrayOfString, paramClassLoader, loader, true);
      if (loaderLog.isLoggable(Log.VERBOSE))
        loaderLog.log(Log.VERBOSE, "proxy class defined by " + clazz.getClassLoader()); 
      return clazz;
    } catch (ClassNotFoundException classNotFoundException) {
      if (loaderLog.isLoggable(Log.BRIEF))
        loaderLog.log(Log.BRIEF, "proxy class resolution failed", classNotFoundException); 
      throw classNotFoundException;
    } 
  }
  
  private static Class<?> loadProxyClass(String[] paramArrayOfString, ClassLoader paramClassLoader1, ClassLoader paramClassLoader2, boolean paramBoolean) throws ClassNotFoundException {
    ClassLoader classLoader = null;
    Class[] arrayOfClass = new Class[paramArrayOfString.length];
    boolean[] arrayOfBoolean = { false };
    if (paramClassLoader1 != null) {
      try {
        classLoader = loadProxyInterfaces(paramArrayOfString, paramClassLoader1, arrayOfClass, arrayOfBoolean);
        if (loaderLog.isLoggable(Log.VERBOSE)) {
          ClassLoader[] arrayOfClassLoader = new ClassLoader[arrayOfClass.length];
          for (byte b = 0; b < arrayOfClassLoader.length; b++)
            arrayOfClassLoader[b] = arrayOfClass[b].getClassLoader(); 
          loaderLog.log(Log.VERBOSE, "proxy interfaces found via defaultLoader, defined by " + Arrays.asList(arrayOfClassLoader));
        } 
      } catch (ClassNotFoundException classNotFoundException) {}
      if (!arrayOfBoolean[0]) {
        if (paramBoolean)
          try {
            return Proxy.getProxyClass(paramClassLoader2, arrayOfClass);
          } catch (IllegalArgumentException illegalArgumentException) {} 
        classLoader = paramClassLoader1;
      } 
      return loadProxyClass(classLoader, arrayOfClass);
    } 
    arrayOfBoolean[0] = false;
    classLoader = loadProxyInterfaces(paramArrayOfString, paramClassLoader2, arrayOfClass, arrayOfBoolean);
    if (loaderLog.isLoggable(Log.VERBOSE)) {
      ClassLoader[] arrayOfClassLoader = new ClassLoader[arrayOfClass.length];
      for (byte b = 0; b < arrayOfClassLoader.length; b++)
        arrayOfClassLoader[b] = arrayOfClass[b].getClassLoader(); 
      loaderLog.log(Log.VERBOSE, "proxy interfaces found via codebase, defined by " + Arrays.asList(arrayOfClassLoader));
    } 
    if (!arrayOfBoolean[0])
      classLoader = paramClassLoader2; 
    return loadProxyClass(classLoader, arrayOfClass);
  }
  
  private static Class<?> loadProxyClass(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass) throws ClassNotFoundException {
    try {
      return Proxy.getProxyClass(paramClassLoader, paramArrayOfClass);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ClassNotFoundException("error creating dynamic proxy class", illegalArgumentException);
    } 
  }
  
  private static ClassLoader loadProxyInterfaces(String[] paramArrayOfString, ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass, boolean[] paramArrayOfBoolean) throws ClassNotFoundException {
    ClassLoader classLoader = null;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      Class clazz = paramArrayOfClass[b] = loadClassForName(paramArrayOfString[b], false, paramClassLoader);
      if (!Modifier.isPublic(clazz.getModifiers())) {
        ClassLoader classLoader1 = clazz.getClassLoader();
        if (loaderLog.isLoggable(Log.VERBOSE))
          loaderLog.log(Log.VERBOSE, "non-public interface \"" + paramArrayOfString[b] + "\" defined by " + classLoader1); 
        if (!paramArrayOfBoolean[0]) {
          classLoader = classLoader1;
          paramArrayOfBoolean[0] = true;
        } else if (classLoader1 != classLoader) {
          throw new IllegalAccessError("non-public interfaces defined in different class loaders");
        } 
      } 
    } 
    return classLoader;
  }
  
  private static URL[] pathToURLs(String paramString) throws MalformedURLException {
    synchronized (pathToURLsCache) {
      Object[] arrayOfObject = (Object[])pathToURLsCache.get(paramString);
      if (arrayOfObject != null)
        return (URL[])arrayOfObject[0]; 
    } 
    StringTokenizer stringTokenizer = new StringTokenizer(paramString);
    URL[] arrayOfURL = new URL[stringTokenizer.countTokens()];
    for (byte b = 0; stringTokenizer.hasMoreTokens(); b++)
      arrayOfURL[b] = new URL(stringTokenizer.nextToken()); 
    synchronized (pathToURLsCache) {
      pathToURLsCache.put(paramString, new Object[] { arrayOfURL, new SoftReference(paramString) });
    } 
    return arrayOfURL;
  }
  
  private static String urlsToPath(URL[] paramArrayOfURL) {
    if (paramArrayOfURL.length == 0)
      return null; 
    if (paramArrayOfURL.length == 1)
      return paramArrayOfURL[0].toExternalForm(); 
    StringBuffer stringBuffer = new StringBuffer(paramArrayOfURL[0].toExternalForm());
    for (byte b = 1; b < paramArrayOfURL.length; b++) {
      stringBuffer.append(' ');
      stringBuffer.append(paramArrayOfURL[b].toExternalForm());
    } 
    return stringBuffer.toString();
  }
  
  private static ClassLoader getRMIContextClassLoader() { return Thread.currentThread().getContextClassLoader(); }
  
  private static Loader lookupLoader(final URL[] urls, final ClassLoader parent) {
    Loader loader;
    synchronized (LoaderHandler.class) {
      LoaderEntry loaderEntry;
      while ((loaderEntry = (LoaderEntry)refQueue.poll()) != null) {
        if (!loaderEntry.removed)
          loaderTable.remove(loaderEntry.key); 
      } 
      LoaderKey loaderKey = new LoaderKey(paramArrayOfURL, paramClassLoader);
      loaderEntry = (LoaderEntry)loaderTable.get(loaderKey);
      if (loaderEntry == null || (loader = (Loader)loaderEntry.get()) == null) {
        if (loaderEntry != null) {
          loaderTable.remove(loaderKey);
          loaderEntry.removed = true;
        } 
        AccessControlContext accessControlContext = getLoaderAccessControlContext(paramArrayOfURL);
        loader = (Loader)AccessController.doPrivileged(new PrivilegedAction<Loader>() {
              public LoaderHandler.Loader run() { return new LoaderHandler.Loader(urls, parent, null); }
            }accessControlContext);
        loaderEntry = new LoaderEntry(loaderKey, loader);
        loaderTable.put(loaderKey, loaderEntry);
      } 
    } 
    return loader;
  }
  
  private static AccessControlContext getLoaderAccessControlContext(URL[] paramArrayOfURL) {
    PermissionCollection permissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
          public PermissionCollection run() {
            CodeSource codeSource = new CodeSource(null, (Certificate[])null);
            Policy policy = Policy.getPolicy();
            return (policy != null) ? policy.getPermissions(codeSource) : new Permissions();
          }
        });
    permissionCollection.add(new RuntimePermission("createClassLoader"));
    permissionCollection.add(new PropertyPermission("java.*", "read"));
    addPermissionsForURLs(paramArrayOfURL, permissionCollection, true);
    ProtectionDomain protectionDomain = new ProtectionDomain(new CodeSource((paramArrayOfURL.length > 0) ? paramArrayOfURL[0] : null, (Certificate[])null), permissionCollection);
    return new AccessControlContext(new ProtectionDomain[] { protectionDomain });
  }
  
  private static void addPermissionsForURLs(URL[] paramArrayOfURL, PermissionCollection paramPermissionCollection, boolean paramBoolean) {
    for (byte b = 0; b < paramArrayOfURL.length; b++) {
      URL uRL = paramArrayOfURL[b];
      try {
        URLConnection uRLConnection = uRL.openConnection();
        Permission permission = uRLConnection.getPermission();
        if (permission != null)
          if (permission instanceof FilePermission) {
            String str = permission.getName();
            int i = str.lastIndexOf(File.separatorChar);
            if (i != -1) {
              str = str.substring(0, i + 1);
              if (str.endsWith(File.separator))
                str = str + "-"; 
              FilePermission filePermission = new FilePermission(str, "read");
              if (!paramPermissionCollection.implies(filePermission))
                paramPermissionCollection.add(filePermission); 
              paramPermissionCollection.add(new FilePermission(str, "read"));
            } else if (!paramPermissionCollection.implies(permission)) {
              paramPermissionCollection.add(permission);
            } 
          } else {
            if (!paramPermissionCollection.implies(permission))
              paramPermissionCollection.add(permission); 
            if (paramBoolean) {
              URL uRL1 = uRL;
              for (URLConnection uRLConnection1 = uRLConnection; uRLConnection1 instanceof JarURLConnection; uRLConnection1 = uRL1.openConnection())
                uRL1 = ((JarURLConnection)uRLConnection1).getJarFileURL(); 
              String str = uRL1.getHost();
              if (str != null && permission.implies(new SocketPermission(str, "resolve"))) {
                SocketPermission socketPermission = new SocketPermission(str, "connect,accept");
                if (!paramPermissionCollection.implies(socketPermission))
                  paramPermissionCollection.add(socketPermission); 
              } 
            } 
          }  
      } catch (IOException iOException) {}
    } 
  }
  
  private static Class<?> loadClassForName(String paramString, boolean paramBoolean, ClassLoader paramClassLoader) throws ClassNotFoundException {
    if (paramClassLoader == null)
      ReflectUtil.checkPackageAccess(paramString); 
    return Class.forName(paramString, paramBoolean, paramClassLoader);
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.codebase"));
    if (str != null && str.trim().length() > 0)
      codebaseProperty = str; 
    codebaseURLs = null;
    codebaseLoaders = Collections.synchronizedMap(new IdentityHashMap(5));
    for (ClassLoader classLoader = ClassLoader.getSystemClassLoader(); classLoader != null; classLoader = classLoader.getParent())
      codebaseLoaders.put(classLoader, null); 
    loaderTable = new HashMap(5);
    refQueue = new ReferenceQueue();
    pathToURLsCache = new WeakHashMap(5);
  }
  
  private static class Loader extends URLClassLoader {
    private ClassLoader parent;
    
    private String annotation;
    
    private Permissions permissions;
    
    private Loader(URL[] param1ArrayOfURL, ClassLoader param1ClassLoader) {
      super(param1ArrayOfURL, param1ClassLoader);
      this.parent = param1ClassLoader;
      this.permissions = new Permissions();
      LoaderHandler.addPermissionsForURLs(param1ArrayOfURL, this.permissions, false);
      this.annotation = LoaderHandler.urlsToPath(param1ArrayOfURL);
    }
    
    public String getClassAnnotation() { return this.annotation; }
    
    private void checkPermissions() {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        Enumeration enumeration = this.permissions.elements();
        while (enumeration.hasMoreElements())
          securityManager.checkPermission((Permission)enumeration.nextElement()); 
      } 
    }
    
    protected PermissionCollection getPermissions(CodeSource param1CodeSource) { return super.getPermissions(param1CodeSource); }
    
    public String toString() { return super.toString() + "[\"" + this.annotation + "\"]"; }
    
    protected Class<?> loadClass(String param1String, boolean param1Boolean) throws ClassNotFoundException {
      if (this.parent == null)
        ReflectUtil.checkPackageAccess(param1String); 
      return super.loadClass(param1String, param1Boolean);
    }
  }
  
  private static class LoaderEntry extends WeakReference<Loader> {
    public LoaderHandler.LoaderKey key;
    
    public boolean removed = false;
    
    public LoaderEntry(LoaderHandler.LoaderKey param1LoaderKey, LoaderHandler.Loader param1Loader) {
      super(param1Loader, refQueue);
      this.key = param1LoaderKey;
    }
  }
  
  private static class LoaderKey {
    private URL[] urls;
    
    private ClassLoader parent;
    
    private int hashValue;
    
    public LoaderKey(URL[] param1ArrayOfURL, ClassLoader param1ClassLoader) {
      this.urls = param1ArrayOfURL;
      this.parent = param1ClassLoader;
      if (param1ClassLoader != null)
        this.hashValue = param1ClassLoader.hashCode(); 
      for (byte b = 0; b < param1ArrayOfURL.length; b++)
        this.hashValue ^= param1ArrayOfURL[b].hashCode(); 
    }
    
    public int hashCode() { return this.hashValue; }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof LoaderKey) {
        LoaderKey loaderKey = (LoaderKey)param1Object;
        if (this.parent != loaderKey.parent)
          return false; 
        if (this.urls == loaderKey.urls)
          return true; 
        if (this.urls.length != loaderKey.urls.length)
          return false; 
        for (byte b = 0; b < this.urls.length; b++) {
          if (!this.urls[b].equals(loaderKey.urls[b]))
            return false; 
        } 
        return true;
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\LoaderHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */