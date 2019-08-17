package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import sun.misc.CompoundEnumeration;
import sun.misc.Launcher;
import sun.misc.PerfCounter;
import sun.misc.Resource;
import sun.misc.URLClassPath;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public abstract class ClassLoader {
  private final ClassLoader parent;
  
  private final ConcurrentHashMap<String, Object> parallelLockMap;
  
  private final Map<String, Certificate[]> package2certs;
  
  private static final Certificate[] nocerts;
  
  private final Vector<Class<?>> classes = new Vector();
  
  private final ProtectionDomain defaultDomain = new ProtectionDomain(new CodeSource(null, (Certificate[])null), null, this, null);
  
  private final Set<ProtectionDomain> domains;
  
  private final HashMap<String, Package> packages = new HashMap();
  
  private static ClassLoader scl;
  
  private static boolean sclSet;
  
  private static Vector<String> loadedLibraryNames;
  
  private static Vector<NativeLibrary> systemNativeLibraries;
  
  private Vector<NativeLibrary> nativeLibraries = new Vector();
  
  private static Stack<NativeLibrary> nativeLibraryContext;
  
  private static String[] usr_paths;
  
  private static String[] sys_paths;
  
  final Object assertionLock;
  
  private boolean defaultAssertionStatus = false;
  
  private Map<String, Boolean> packageAssertionStatus = null;
  
  Map<String, Boolean> classAssertionStatus = null;
  
  private static native void registerNatives();
  
  void addClass(Class<?> paramClass) { this.classes.addElement(paramClass); }
  
  private static Void checkCreateClassLoader() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkCreateClassLoader(); 
    return null;
  }
  
  private ClassLoader(Void paramVoid, ClassLoader paramClassLoader) {
    this.parent = paramClassLoader;
    if (ParallelLoaders.isRegistered(getClass())) {
      this.parallelLockMap = new ConcurrentHashMap();
      this.package2certs = new ConcurrentHashMap();
      this.domains = Collections.synchronizedSet(new HashSet());
      this.assertionLock = new Object();
    } else {
      this.parallelLockMap = null;
      this.package2certs = new Hashtable();
      this.domains = new HashSet();
      this.assertionLock = this;
    } 
  }
  
  protected ClassLoader(ClassLoader paramClassLoader) { this(checkCreateClassLoader(), paramClassLoader); }
  
  protected ClassLoader() { this(checkCreateClassLoader(), getSystemClassLoader()); }
  
  public Class<?> loadClass(String paramString) throws ClassNotFoundException { return loadClass(paramString, false); }
  
  protected Class<?> loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
    synchronized (getClassLoadingLock(paramString)) {
      Class clazz = findLoadedClass(paramString);
      if (clazz == null) {
        long l = System.nanoTime();
        try {
          if (this.parent != null) {
            clazz = this.parent.loadClass(paramString, false);
          } else {
            clazz = findBootstrapClassOrNull(paramString);
          } 
        } catch (ClassNotFoundException classNotFoundException) {}
        if (clazz == null) {
          long l1 = System.nanoTime();
          clazz = findClass(paramString);
          PerfCounter.getParentDelegationTime().addTime(l1 - l);
          PerfCounter.getFindClassTime().addElapsedTimeFrom(l1);
          PerfCounter.getFindClasses().increment();
        } 
      } 
      if (paramBoolean)
        resolveClass(clazz); 
      return clazz;
    } 
  }
  
  protected Object getClassLoadingLock(String paramString) {
    Object object = this;
    if (this.parallelLockMap != null) {
      Object object1 = new Object();
      object = this.parallelLockMap.putIfAbsent(paramString, object1);
      if (object == null)
        object = object1; 
    } 
    return object;
  }
  
  private Class<?> loadClassInternal(String paramString) throws ClassNotFoundException {
    if (this.parallelLockMap == null)
      synchronized (this) {
        return loadClass(paramString);
      }  
    return loadClass(paramString);
  }
  
  private void checkPackageAccess(Class<?> paramClass, ProtectionDomain paramProtectionDomain) {
    final SecurityManager sm = System.getSecurityManager();
    if (securityManager != null) {
      if (ReflectUtil.isNonPublicProxyClass(paramClass)) {
        for (Class clazz : paramClass.getInterfaces())
          checkPackageAccess(clazz, paramProtectionDomain); 
        return;
      } 
      final String name = paramClass.getName();
      final int i = str.lastIndexOf('.');
      if (i != -1)
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                sm.checkPackageAccess(name.substring(0, i));
                return null;
              }
            }new AccessControlContext(new ProtectionDomain[] { paramProtectionDomain })); 
    } 
    this.domains.add(paramProtectionDomain);
  }
  
  protected Class<?> findClass(String paramString) throws ClassNotFoundException { throw new ClassNotFoundException(paramString); }
  
  @Deprecated
  protected final Class<?> defineClass(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws ClassFormatError { return defineClass(null, paramArrayOfByte, paramInt1, paramInt2, null); }
  
  protected final Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws ClassFormatError { return defineClass(paramString, paramArrayOfByte, paramInt1, paramInt2, null); }
  
  private ProtectionDomain preDefineClass(String paramString, ProtectionDomain paramProtectionDomain) {
    if (!checkName(paramString))
      throw new NoClassDefFoundError("IllegalName: " + paramString); 
    if (paramString != null && paramString.startsWith("java."))
      throw new SecurityException("Prohibited package name: " + paramString.substring(0, paramString.lastIndexOf('.'))); 
    if (paramProtectionDomain == null)
      paramProtectionDomain = this.defaultDomain; 
    if (paramString != null)
      checkCerts(paramString, paramProtectionDomain.getCodeSource()); 
    return paramProtectionDomain;
  }
  
  private String defineClassSourceLocation(ProtectionDomain paramProtectionDomain) {
    CodeSource codeSource = paramProtectionDomain.getCodeSource();
    String str = null;
    if (codeSource != null && codeSource.getLocation() != null)
      str = codeSource.getLocation().toString(); 
    return str;
  }
  
  private void postDefineClass(Class<?> paramClass, ProtectionDomain paramProtectionDomain) {
    if (paramProtectionDomain.getCodeSource() != null) {
      Certificate[] arrayOfCertificate = paramProtectionDomain.getCodeSource().getCertificates();
      if (arrayOfCertificate != null)
        setSigners(paramClass, arrayOfCertificate); 
    } 
  }
  
  protected final Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain) throws ClassFormatError {
    paramProtectionDomain = preDefineClass(paramString, paramProtectionDomain);
    String str = defineClassSourceLocation(paramProtectionDomain);
    Class clazz = defineClass1(paramString, paramArrayOfByte, paramInt1, paramInt2, paramProtectionDomain, str);
    postDefineClass(clazz, paramProtectionDomain);
    return clazz;
  }
  
  protected final Class<?> defineClass(String paramString, ByteBuffer paramByteBuffer, ProtectionDomain paramProtectionDomain) throws ClassFormatError {
    int i = paramByteBuffer.remaining();
    if (!paramByteBuffer.isDirect()) {
      if (paramByteBuffer.hasArray())
        return defineClass(paramString, paramByteBuffer.array(), paramByteBuffer.position() + paramByteBuffer.arrayOffset(), i, paramProtectionDomain); 
      byte[] arrayOfByte = new byte[i];
      paramByteBuffer.get(arrayOfByte);
      return defineClass(paramString, arrayOfByte, 0, i, paramProtectionDomain);
    } 
    paramProtectionDomain = preDefineClass(paramString, paramProtectionDomain);
    String str = defineClassSourceLocation(paramProtectionDomain);
    Class clazz = defineClass2(paramString, paramByteBuffer, paramByteBuffer.position(), i, paramProtectionDomain, str);
    postDefineClass(clazz, paramProtectionDomain);
    return clazz;
  }
  
  private native Class<?> defineClass0(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain) throws ClassFormatError;
  
  private native Class<?> defineClass1(String paramString1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain, String paramString2);
  
  private native Class<?> defineClass2(String paramString1, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain, String paramString2);
  
  private boolean checkName(String paramString) { return (paramString == null || paramString.length() == 0) ? true : (!(paramString.indexOf('/') != -1 || (!VM.allowArraySyntax() && paramString.charAt(0) == '['))); }
  
  private void checkCerts(String paramString, CodeSource paramCodeSource) {
    int i = paramString.lastIndexOf('.');
    String str = (i == -1) ? "" : paramString.substring(0, i);
    Certificate[] arrayOfCertificate1 = null;
    if (paramCodeSource != null)
      arrayOfCertificate1 = paramCodeSource.getCertificates(); 
    Certificate[] arrayOfCertificate2 = null;
    if (this.parallelLockMap == null) {
      synchronized (this) {
        arrayOfCertificate2 = (Certificate[])this.package2certs.get(str);
        if (arrayOfCertificate2 == null)
          this.package2certs.put(str, (arrayOfCertificate1 == null) ? nocerts : arrayOfCertificate1); 
      } 
    } else {
      arrayOfCertificate2 = (Certificate[])((ConcurrentHashMap)this.package2certs).putIfAbsent(str, (arrayOfCertificate1 == null) ? nocerts : arrayOfCertificate1);
    } 
    if (arrayOfCertificate2 != null && !compareCerts(arrayOfCertificate2, arrayOfCertificate1))
      throw new SecurityException("class \"" + paramString + "\"'s signer information does not match signer information of other classes in the same package"); 
  }
  
  private boolean compareCerts(Certificate[] paramArrayOfCertificate1, Certificate[] paramArrayOfCertificate2) {
    if (paramArrayOfCertificate2 == null || paramArrayOfCertificate2.length == 0)
      return (paramArrayOfCertificate1.length == 0); 
    if (paramArrayOfCertificate2.length != paramArrayOfCertificate1.length)
      return false; 
    byte b;
    for (b = 0; b < paramArrayOfCertificate2.length; b++) {
      boolean bool = false;
      for (byte b1 = 0; b1 < paramArrayOfCertificate1.length; b1++) {
        if (paramArrayOfCertificate2[b].equals(paramArrayOfCertificate1[b1])) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        return false; 
    } 
    for (b = 0; b < paramArrayOfCertificate1.length; b++) {
      boolean bool = false;
      for (byte b1 = 0; b1 < paramArrayOfCertificate2.length; b1++) {
        if (paramArrayOfCertificate1[b].equals(paramArrayOfCertificate2[b1])) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  protected final void resolveClass(Class<?> paramClass) { resolveClass0(paramClass); }
  
  private native void resolveClass0(Class<?> paramClass);
  
  protected final Class<?> findSystemClass(String paramString) throws ClassNotFoundException {
    ClassLoader classLoader = getSystemClassLoader();
    if (classLoader == null) {
      if (!checkName(paramString))
        throw new ClassNotFoundException(paramString); 
      Class clazz = findBootstrapClass(paramString);
      if (clazz == null)
        throw new ClassNotFoundException(paramString); 
      return clazz;
    } 
    return classLoader.loadClass(paramString);
  }
  
  private Class<?> findBootstrapClassOrNull(String paramString) throws ClassNotFoundException { return !checkName(paramString) ? null : findBootstrapClass(paramString); }
  
  private native Class<?> findBootstrapClass(String paramString) throws ClassNotFoundException;
  
  protected final Class<?> findLoadedClass(String paramString) throws ClassNotFoundException { return !checkName(paramString) ? null : findLoadedClass0(paramString); }
  
  private final native Class<?> findLoadedClass0(String paramString) throws ClassNotFoundException;
  
  protected final void setSigners(Class<?> paramClass, Object[] paramArrayOfObject) { paramClass.setSigners(paramArrayOfObject); }
  
  public URL getResource(String paramString) {
    URL uRL;
    if (this.parent != null) {
      uRL = this.parent.getResource(paramString);
    } else {
      uRL = getBootstrapResource(paramString);
    } 
    if (uRL == null)
      uRL = findResource(paramString); 
    return uRL;
  }
  
  public Enumeration<URL> getResources(String paramString) throws IOException {
    Enumeration[] arrayOfEnumeration = (Enumeration[])new Enumeration[2];
    if (this.parent != null) {
      arrayOfEnumeration[0] = this.parent.getResources(paramString);
    } else {
      arrayOfEnumeration[0] = getBootstrapResources(paramString);
    } 
    arrayOfEnumeration[1] = findResources(paramString);
    return new CompoundEnumeration(arrayOfEnumeration);
  }
  
  protected URL findResource(String paramString) { return null; }
  
  protected Enumeration<URL> findResources(String paramString) throws IOException { return Collections.emptyEnumeration(); }
  
  @CallerSensitive
  protected static boolean registerAsParallelCapable() {
    Class clazz = Reflection.getCallerClass().asSubclass(ClassLoader.class);
    return ParallelLoaders.register(clazz);
  }
  
  public static URL getSystemResource(String paramString) {
    ClassLoader classLoader = getSystemClassLoader();
    return (classLoader == null) ? getBootstrapResource(paramString) : classLoader.getResource(paramString);
  }
  
  public static Enumeration<URL> getSystemResources(String paramString) throws IOException {
    ClassLoader classLoader = getSystemClassLoader();
    return (classLoader == null) ? getBootstrapResources(paramString) : classLoader.getResources(paramString);
  }
  
  private static URL getBootstrapResource(String paramString) {
    URLClassPath uRLClassPath = getBootstrapClassPath();
    Resource resource = uRLClassPath.getResource(paramString);
    return (resource != null) ? resource.getURL() : null;
  }
  
  private static Enumeration<URL> getBootstrapResources(String paramString) throws IOException {
    final Enumeration e = getBootstrapClassPath().getResources(paramString);
    return new Enumeration<URL>() {
        public URL nextElement() { return ((Resource)e.nextElement()).getURL(); }
        
        public boolean hasMoreElements() { return e.hasMoreElements(); }
      };
  }
  
  static URLClassPath getBootstrapClassPath() { return Launcher.getBootstrapClassPath(); }
  
  public InputStream getResourceAsStream(String paramString) {
    URL uRL = getResource(paramString);
    try {
      return (uRL != null) ? uRL.openStream() : null;
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public static InputStream getSystemResourceAsStream(String paramString) {
    URL uRL = getSystemResource(paramString);
    try {
      return (uRL != null) ? uRL.openStream() : null;
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  @CallerSensitive
  public final ClassLoader getParent() {
    if (this.parent == null)
      return null; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      checkClassLoaderPermission(this.parent, Reflection.getCallerClass()); 
    return this.parent;
  }
  
  @CallerSensitive
  public static ClassLoader getSystemClassLoader() {
    initSystemClassLoader();
    if (scl == null)
      return null; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      checkClassLoaderPermission(scl, Reflection.getCallerClass()); 
    return scl;
  }
  
  private static void initSystemClassLoader() {
    if (!sclSet) {
      if (scl != null)
        throw new IllegalStateException("recursive invocation"); 
      Launcher launcher = Launcher.getLauncher();
      if (launcher != null) {
        Throwable throwable = null;
        scl = launcher.getClassLoader();
        try {
          scl = (ClassLoader)AccessController.doPrivileged(new SystemClassLoaderAction(scl));
        } catch (PrivilegedActionException privilegedActionException) {
          throwable = privilegedActionException.getCause();
          if (throwable instanceof java.lang.reflect.InvocationTargetException)
            throwable = throwable.getCause(); 
        } 
        if (throwable != null) {
          if (throwable instanceof Error)
            throw (Error)throwable; 
          throw new Error(throwable);
        } 
      } 
      sclSet = true;
    } 
  }
  
  boolean isAncestor(ClassLoader paramClassLoader) {
    ClassLoader classLoader = this;
    do {
      classLoader = classLoader.parent;
      if (paramClassLoader == classLoader)
        return true; 
    } while (classLoader != null);
    return false;
  }
  
  private static boolean needsClassLoaderPermissionCheck(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2) { return (paramClassLoader1 == paramClassLoader2) ? false : ((paramClassLoader1 == null) ? false : (!paramClassLoader2.isAncestor(paramClassLoader1))); }
  
  static ClassLoader getClassLoader(Class<?> paramClass) { return (paramClass == null) ? null : paramClass.getClassLoader0(); }
  
  static void checkClassLoaderPermission(ClassLoader paramClassLoader, Class<?> paramClass) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      ClassLoader classLoader;
      if ((classLoader = getClassLoader(paramClass)).needsClassLoaderPermissionCheck(classLoader, paramClassLoader))
        securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION); 
    } 
  }
  
  protected Package definePackage(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, URL paramURL) throws IllegalArgumentException {
    synchronized (this.packages) {
      Package package = getPackage(paramString1);
      if (package != null)
        throw new IllegalArgumentException(paramString1); 
      package = new Package(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramURL, this);
      this.packages.put(paramString1, package);
      return package;
    } 
  }
  
  protected Package getPackage(String paramString) {
    Package package;
    synchronized (this.packages) {
      package = (Package)this.packages.get(paramString);
    } 
    if (package == null) {
      if (this.parent != null) {
        package = this.parent.getPackage(paramString);
      } else {
        package = Package.getSystemPackage(paramString);
      } 
      if (package != null)
        synchronized (this.packages) {
          Package package1 = (Package)this.packages.get(paramString);
          if (package1 == null) {
            this.packages.put(paramString, package);
          } else {
            package = package1;
          } 
        }  
    } 
    return package;
  }
  
  protected Package[] getPackages() {
    Package[] arrayOfPackage;
    HashMap hashMap;
    synchronized (this.packages) {
      hashMap = new HashMap(this.packages);
    } 
    if (this.parent != null) {
      arrayOfPackage = this.parent.getPackages();
    } else {
      arrayOfPackage = Package.getSystemPackages();
    } 
    if (arrayOfPackage != null)
      for (byte b = 0; b < arrayOfPackage.length; b++) {
        String str = arrayOfPackage[b].getName();
        if (hashMap.get(str) == null)
          hashMap.put(str, arrayOfPackage[b]); 
      }  
    return (Package[])hashMap.values().toArray(new Package[hashMap.size()]);
  }
  
  protected String findLibrary(String paramString) { return null; }
  
  private static String[] initializePath(String paramString) {
    String str1 = System.getProperty(paramString, "");
    String str2 = File.pathSeparator;
    int i = str1.length();
    int j = str1.indexOf(str2);
    int m = 0;
    while (j >= 0) {
      m++;
      j = str1.indexOf(str2, j + 1);
    } 
    String[] arrayOfString = new String[m + 1];
    m = j = 0;
    int k;
    for (k = str1.indexOf(str2); k >= 0; k = str1.indexOf(str2, j)) {
      if (k - j > 0) {
        arrayOfString[m++] = str1.substring(j, k);
      } else if (k - j == 0) {
        arrayOfString[m++] = ".";
      } 
      j = k + 1;
    } 
    arrayOfString[m] = str1.substring(j, i);
    return arrayOfString;
  }
  
  static void loadLibrary(Class<?> paramClass, String paramString, boolean paramBoolean) {
    ClassLoader classLoader = (paramClass == null) ? null : paramClass.getClassLoader();
    if (sys_paths == null) {
      usr_paths = initializePath("java.library.path");
      sys_paths = initializePath("sun.boot.library.path");
    } 
    if (paramBoolean) {
      if (loadLibrary0(paramClass, new File(paramString)))
        return; 
      throw new UnsatisfiedLinkError("Can't load library: " + paramString);
    } 
    if (classLoader != null) {
      String str = classLoader.findLibrary(paramString);
      if (str != null) {
        File file = new File(str);
        if (!file.isAbsolute())
          throw new UnsatisfiedLinkError("ClassLoader.findLibrary failed to return an absolute path: " + str); 
        if (loadLibrary0(paramClass, file))
          return; 
        throw new UnsatisfiedLinkError("Can't load " + str);
      } 
    } 
    byte b;
    for (b = 0; b < sys_paths.length; b++) {
      File file = new File(sys_paths[b], System.mapLibraryName(paramString));
      if (loadLibrary0(paramClass, file))
        return; 
      file = ClassLoaderHelper.mapAlternativeName(file);
      if (file != null && loadLibrary0(paramClass, file))
        return; 
    } 
    if (classLoader != null)
      for (b = 0; b < usr_paths.length; b++) {
        File file = new File(usr_paths[b], System.mapLibraryName(paramString));
        if (loadLibrary0(paramClass, file))
          return; 
        file = ClassLoaderHelper.mapAlternativeName(file);
        if (file != null && loadLibrary0(paramClass, file))
          return; 
      }  
    throw new UnsatisfiedLinkError("no " + paramString + " in java.library.path");
  }
  
  private static native String findBuiltinLib(String paramString);
  
  private static boolean loadLibrary0(Class<?> paramClass, final File file) {
    String str = findBuiltinLib(paramFile.getName());
    boolean bool = (str != null);
    if (!bool) {
      boolean bool1 = (AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() { return file.exists() ? Boolean.TRUE : null; }
          }) != null) ? 1 : 0;
      if (!bool1)
        return false; 
      try {
        str = paramFile.getCanonicalPath();
      } catch (IOException iOException) {
        return false;
      } 
    } 
    ClassLoader classLoader = (paramClass == null) ? null : paramClass.getClassLoader();
    Vector vector = (classLoader != null) ? classLoader.nativeLibraries : systemNativeLibraries;
    synchronized (vector) {
      int i = vector.size();
      for (byte b = 0; b < i; b++) {
        NativeLibrary nativeLibrary = (NativeLibrary)vector.elementAt(b);
        if (str.equals(nativeLibrary.name))
          return true; 
      } 
      synchronized (loadedLibraryNames) {
        if (loadedLibraryNames.contains(str))
          throw new UnsatisfiedLinkError("Native Library " + str + " already loaded in another classloader"); 
        int j = nativeLibraryContext.size();
        for (byte b1 = 0; b1 < j; b1++) {
          NativeLibrary nativeLibrary1 = (NativeLibrary)nativeLibraryContext.elementAt(b1);
          if (str.equals(nativeLibrary1.name)) {
            if (classLoader == nativeLibrary1.fromClass.getClassLoader())
              return true; 
            throw new UnsatisfiedLinkError("Native Library " + str + " is being loaded in another classloader");
          } 
        } 
        NativeLibrary nativeLibrary = new NativeLibrary(paramClass, str, bool);
        nativeLibraryContext.push(nativeLibrary);
        try {
          nativeLibrary.load(str, bool);
        } finally {
          nativeLibraryContext.pop();
        } 
        if (nativeLibrary.loaded) {
          loadedLibraryNames.addElement(str);
          vector.addElement(nativeLibrary);
          return true;
        } 
        return false;
      } 
    } 
  }
  
  static long findNative(ClassLoader paramClassLoader, String paramString) {
    Vector vector = (paramClassLoader != null) ? paramClassLoader.nativeLibraries : systemNativeLibraries;
    synchronized (vector) {
      int i = vector.size();
      for (byte b = 0; b < i; b++) {
        NativeLibrary nativeLibrary = (NativeLibrary)vector.elementAt(b);
        long l = nativeLibrary.find(paramString);
        if (l != 0L)
          return l; 
      } 
    } 
    return 0L;
  }
  
  public void setDefaultAssertionStatus(boolean paramBoolean) {
    synchronized (this.assertionLock) {
      if (this.classAssertionStatus == null)
        initializeJavaAssertionMaps(); 
      this.defaultAssertionStatus = paramBoolean;
    } 
  }
  
  public void setPackageAssertionStatus(String paramString, boolean paramBoolean) {
    synchronized (this.assertionLock) {
      if (this.packageAssertionStatus == null)
        initializeJavaAssertionMaps(); 
      this.packageAssertionStatus.put(paramString, Boolean.valueOf(paramBoolean));
    } 
  }
  
  public void setClassAssertionStatus(String paramString, boolean paramBoolean) {
    synchronized (this.assertionLock) {
      if (this.classAssertionStatus == null)
        initializeJavaAssertionMaps(); 
      this.classAssertionStatus.put(paramString, Boolean.valueOf(paramBoolean));
    } 
  }
  
  public void clearAssertionStatus() {
    synchronized (this.assertionLock) {
      this.classAssertionStatus = new HashMap();
      this.packageAssertionStatus = new HashMap();
      this.defaultAssertionStatus = false;
    } 
  }
  
  boolean desiredAssertionStatus(String paramString) {
    synchronized (this.assertionLock) {
      Boolean bool = (Boolean)this.classAssertionStatus.get(paramString);
      if (bool != null)
        return bool.booleanValue(); 
      int i = paramString.lastIndexOf(".");
      if (i < 0) {
        bool = (Boolean)this.packageAssertionStatus.get(null);
        if (bool != null)
          return bool.booleanValue(); 
      } 
      while (i > 0) {
        paramString = paramString.substring(0, i);
        bool = (Boolean)this.packageAssertionStatus.get(paramString);
        if (bool != null)
          return bool.booleanValue(); 
        i = paramString.lastIndexOf(".", i - 1);
      } 
      return this.defaultAssertionStatus;
    } 
  }
  
  private void initializeJavaAssertionMaps() {
    this.classAssertionStatus = new HashMap();
    this.packageAssertionStatus = new HashMap();
    AssertionStatusDirectives assertionStatusDirectives = retrieveDirectives();
    byte b;
    for (b = 0; b < assertionStatusDirectives.classes.length; b++)
      this.classAssertionStatus.put(assertionStatusDirectives.classes[b], Boolean.valueOf(assertionStatusDirectives.classEnabled[b])); 
    for (b = 0; b < assertionStatusDirectives.packages.length; b++)
      this.packageAssertionStatus.put(assertionStatusDirectives.packages[b], Boolean.valueOf(assertionStatusDirectives.packageEnabled[b])); 
    this.defaultAssertionStatus = assertionStatusDirectives.deflt;
  }
  
  private static native AssertionStatusDirectives retrieveDirectives();
  
  static  {
    registerNatives();
    nocerts = new Certificate[0];
    loadedLibraryNames = new Vector();
    systemNativeLibraries = new Vector();
    nativeLibraryContext = new Stack();
  }
  
  static class NativeLibrary {
    long handle;
    
    private int jniVersion;
    
    private final Class<?> fromClass;
    
    String name;
    
    boolean isBuiltin;
    
    boolean loaded;
    
    native void load(String param1String, boolean param1Boolean);
    
    native long find(String param1String);
    
    native void unload(String param1String, boolean param1Boolean);
    
    public NativeLibrary(Class<?> param1Class, String param1String, boolean param1Boolean) {
      this.name = param1String;
      this.fromClass = param1Class;
      this.isBuiltin = param1Boolean;
    }
    
    protected void finalize() {
      synchronized (loadedLibraryNames) {
        if (this.fromClass.getClassLoader() != null && this.loaded) {
          int i = loadedLibraryNames.size();
          for (byte b = 0; b < i; b++) {
            if (this.name.equals(loadedLibraryNames.elementAt(b))) {
              loadedLibraryNames.removeElementAt(b);
              break;
            } 
          } 
          nativeLibraryContext.push(this);
          try {
            unload(this.name, this.isBuiltin);
          } finally {
            nativeLibraryContext.pop();
          } 
        } 
      } 
    }
    
    static Class<?> getFromClass() { return ((NativeLibrary)nativeLibraryContext.peek()).fromClass; }
  }
  
  private static class ParallelLoaders {
    private static final Set<Class<? extends ClassLoader>> loaderTypes = Collections.newSetFromMap(new WeakHashMap());
    
    static boolean register(Class<? extends ClassLoader> param1Class) {
      synchronized (loaderTypes) {
        if (loaderTypes.contains(param1Class.getSuperclass())) {
          loaderTypes.add(param1Class);
          return true;
        } 
        return false;
      } 
    }
    
    static boolean isRegistered(Class<? extends ClassLoader> param1Class) {
      synchronized (loaderTypes) {
        return loaderTypes.contains(param1Class);
      } 
    }
    
    static  {
      synchronized (loaderTypes) {
        loaderTypes.add(ClassLoader.class);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */