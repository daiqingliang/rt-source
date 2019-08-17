package sun.misc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Paths;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.net.www.ParseUtil;

public class Launcher {
  private static URLStreamHandlerFactory factory = new Factory(null);
  
  private static Launcher launcher = new Launcher();
  
  private static String bootClassPath = System.getProperty("sun.boot.class.path");
  
  private ClassLoader loader;
  
  private static URLStreamHandler fileHandler;
  
  public static Launcher getLauncher() { return launcher; }
  
  public Launcher() {
    try {
      extClassLoader = ExtClassLoader.getExtClassLoader();
    } catch (IOException iOException) {
      throw new InternalError("Could not create extension class loader", iOException);
    } 
    try {
      this.loader = AppClassLoader.getAppClassLoader(extClassLoader);
    } catch (IOException iOException) {
      throw new InternalError("Could not create application class loader", iOException);
    } 
    Thread.currentThread().setContextClassLoader(this.loader);
    String str = System.getProperty("java.security.manager");
    if (str != null) {
      SecurityManager securityManager = null;
      if ("".equals(str) || "default".equals(str)) {
        securityManager = new SecurityManager();
      } else {
        try {
          securityManager = (SecurityManager)this.loader.loadClass(str).newInstance();
        } catch (IllegalAccessException illegalAccessException) {
        
        } catch (InstantiationException instantiationException) {
        
        } catch (ClassNotFoundException classNotFoundException) {
        
        } catch (ClassCastException classCastException) {}
      } 
      if (securityManager != null) {
        System.setSecurityManager(securityManager);
      } else {
        throw new InternalError("Could not create SecurityManager: " + str);
      } 
    } 
  }
  
  public ClassLoader getClassLoader() { return this.loader; }
  
  public static URLClassPath getBootstrapClassPath() { return BootClassPathHolder.bcp; }
  
  private static URL[] pathToURLs(File[] paramArrayOfFile) {
    URL[] arrayOfURL = new URL[paramArrayOfFile.length];
    for (byte b = 0; b < paramArrayOfFile.length; b++)
      arrayOfURL[b] = getFileURL(paramArrayOfFile[b]); 
    return arrayOfURL;
  }
  
  private static File[] getClassPath(String paramString) {
    File[] arrayOfFile;
    if (paramString != null) {
      byte b1 = 0;
      byte b2 = 1;
      int i = 0;
      int j;
      for (j = 0; (i = paramString.indexOf(File.pathSeparator, j)) != -1; j = i + 1)
        b2++; 
      arrayOfFile = new File[b2];
      for (j = i = 0; (i = paramString.indexOf(File.pathSeparator, j)) != -1; j = i + 1) {
        if (i - j > 0) {
          arrayOfFile[b1++] = new File(paramString.substring(j, i));
        } else {
          arrayOfFile[b1++] = new File(".");
        } 
      } 
      if (j < paramString.length()) {
        arrayOfFile[b1++] = new File(paramString.substring(j));
      } else {
        arrayOfFile[b1++] = new File(".");
      } 
      if (b1 != b2) {
        File[] arrayOfFile1 = new File[b1];
        System.arraycopy(arrayOfFile, 0, arrayOfFile1, 0, b1);
        arrayOfFile = arrayOfFile1;
      } 
    } else {
      arrayOfFile = new File[0];
    } 
    return arrayOfFile;
  }
  
  static URL getFileURL(File paramFile) {
    try {
      paramFile = paramFile.getCanonicalFile();
    } catch (IOException iOException) {}
    try {
      return ParseUtil.fileToEncodedURL(paramFile);
    } catch (MalformedURLException malformedURLException) {
      throw new InternalError(malformedURLException);
    } 
  }
  
  static class AppClassLoader extends URLClassLoader {
    final URLClassPath ucp = SharedSecrets.getJavaNetAccess().getURLClassPath(this);
    
    public static ClassLoader getAppClassLoader(final ClassLoader extcl) throws IOException {
      final String s = System.getProperty("java.class.path");
      final File[] path = (str == null) ? new File[0] : Launcher.getClassPath(str);
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<AppClassLoader>() {
            public Launcher.AppClassLoader run() {
              URL[] arrayOfURL = (s == null) ? new URL[0] : Launcher.pathToURLs(path);
              return new Launcher.AppClassLoader(arrayOfURL, extcl);
            }
          });
    }
    
    AppClassLoader(URL[] param1ArrayOfURL, ClassLoader param1ClassLoader) {
      super(param1ArrayOfURL, param1ClassLoader, factory);
      this.ucp.initLookupCache(this);
    }
    
    public Class<?> loadClass(String param1String, boolean param1Boolean) throws ClassNotFoundException {
      int i = param1String.lastIndexOf('.');
      if (i != -1) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          securityManager.checkPackageAccess(param1String.substring(0, i)); 
      } 
      if (this.ucp.knownToNotExist(param1String)) {
        Class clazz = findLoadedClass(param1String);
        if (clazz != null) {
          if (param1Boolean)
            resolveClass(clazz); 
          return clazz;
        } 
        throw new ClassNotFoundException(param1String);
      } 
      return super.loadClass(param1String, param1Boolean);
    }
    
    protected PermissionCollection getPermissions(CodeSource param1CodeSource) {
      PermissionCollection permissionCollection = super.getPermissions(param1CodeSource);
      permissionCollection.add(new RuntimePermission("exitVM"));
      return permissionCollection;
    }
    
    private void appendToClassPathForInstrumentation(String param1String) {
      assert Thread.holdsLock(this);
      addURL(Launcher.getFileURL(new File(param1String)));
    }
    
    private static AccessControlContext getContext(File[] param1ArrayOfFile) throws MalformedURLException {
      PathPermissions pathPermissions = new PathPermissions(param1ArrayOfFile);
      ProtectionDomain protectionDomain = new ProtectionDomain(new CodeSource(pathPermissions.getCodeBase(), (Certificate[])null), pathPermissions);
      return new AccessControlContext(new ProtectionDomain[] { protectionDomain });
    }
    
    static  {
      ClassLoader.registerAsParallelCapable();
    }
  }
  
  private static class BootClassPathHolder {
    static final URLClassPath bcp;
    
    static  {
      URL[] arrayOfURL;
      if (bootClassPath != null) {
        arrayOfURL = (URL[])AccessController.doPrivileged(new PrivilegedAction<URL[]>() {
              public URL[] run() {
                File[] arrayOfFile = Launcher.getClassPath(bootClassPath);
                int i = arrayOfFile.length;
                HashSet hashSet = new HashSet();
                for (byte b = 0; b < i; b++) {
                  File file = arrayOfFile[b];
                  if (!file.isDirectory())
                    file = file.getParentFile(); 
                  if (file != null && hashSet.add(file))
                    MetaIndex.registerDirectory(file); 
                } 
                return Launcher.pathToURLs(arrayOfFile);
              }
            });
      } else {
        arrayOfURL = new URL[0];
      } 
      bcp = new URLClassPath(arrayOfURL, factory, null);
      bcp.initLookupCache(null);
    }
  }
  
  static class ExtClassLoader extends URLClassLoader {
    public static ExtClassLoader getExtClassLoader() throws IOException {
      if (instance == null)
        synchronized (ExtClassLoader.class) {
          if (instance == null)
            instance = createExtClassLoader(); 
        }  
      return instance;
    }
    
    private static ExtClassLoader createExtClassLoader() throws IOException {
      try {
        return (ExtClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<ExtClassLoader>() {
              public Launcher.ExtClassLoader run() throws IOException {
                File[] arrayOfFile = Launcher.ExtClassLoader.getExtDirs();
                int i = arrayOfFile.length;
                for (byte b = 0; b < i; b++)
                  MetaIndex.registerDirectory(arrayOfFile[b]); 
                return new Launcher.ExtClassLoader(arrayOfFile);
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (IOException)privilegedActionException.getException();
      } 
    }
    
    void addExtURL(URL param1URL) { addURL(param1URL); }
    
    public ExtClassLoader(File[] param1ArrayOfFile) throws IOException {
      super(getExtURLs(param1ArrayOfFile), null, factory);
      SharedSecrets.getJavaNetAccess().getURLClassPath(this).initLookupCache(this);
    }
    
    private static File[] getExtDirs() {
      File[] arrayOfFile;
      String str = System.getProperty("java.ext.dirs");
      if (str != null) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, File.pathSeparator);
        int i = stringTokenizer.countTokens();
        arrayOfFile = new File[i];
        for (byte b = 0; b < i; b++)
          arrayOfFile[b] = new File(stringTokenizer.nextToken()); 
      } else {
        arrayOfFile = new File[0];
      } 
      return arrayOfFile;
    }
    
    private static URL[] getExtURLs(File[] param1ArrayOfFile) {
      Vector vector = new Vector();
      for (byte b = 0; b < param1ArrayOfFile.length; b++) {
        String[] arrayOfString = param1ArrayOfFile[b].list();
        if (arrayOfString != null)
          for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
            if (!arrayOfString[b1].equals("meta-index")) {
              File file = new File(param1ArrayOfFile[b], arrayOfString[b1]);
              vector.add(Launcher.getFileURL(file));
            } 
          }  
      } 
      URL[] arrayOfURL = new URL[vector.size()];
      vector.copyInto(arrayOfURL);
      return arrayOfURL;
    }
    
    public String findLibrary(String param1String) {
      param1String = System.mapLibraryName(param1String);
      URL[] arrayOfURL = getURLs();
      File file = null;
      for (byte b = 0; b < arrayOfURL.length; b++) {
        URI uRI;
        try {
          uRI = arrayOfURL[b].toURI();
        } catch (URISyntaxException uRISyntaxException) {}
        File file1 = Paths.get(uRI).toFile().getParentFile();
        if (file1 != null && !file1.equals(file)) {
          String str = VM.getSavedProperty("os.arch");
          if (str != null) {
            File file3 = new File(new File(file1, str), param1String);
            if (file3.exists())
              return file3.getAbsolutePath(); 
          } 
          File file2 = new File(file1, param1String);
          if (file2.exists())
            return file2.getAbsolutePath(); 
        } 
        file = file1;
      } 
      return null;
    }
    
    private static AccessControlContext getContext(File[] param1ArrayOfFile) throws MalformedURLException {
      PathPermissions pathPermissions = new PathPermissions(param1ArrayOfFile);
      ProtectionDomain protectionDomain = new ProtectionDomain(new CodeSource(pathPermissions.getCodeBase(), (Certificate[])null), pathPermissions);
      return new AccessControlContext(new ProtectionDomain[] { protectionDomain });
    }
    
    static  {
      ClassLoader.registerAsParallelCapable();
      instance = null;
    }
  }
  
  private static class Factory implements URLStreamHandlerFactory {
    private static String PREFIX = "sun.net.www.protocol";
    
    private Factory() {}
    
    public URLStreamHandler createURLStreamHandler(String param1String) {
      String str = PREFIX + "." + param1String + ".Handler";
      try {
        Class clazz = Class.forName(str);
        return (URLStreamHandler)clazz.newInstance();
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw new InternalError("could not load " + param1String + "system protocol handler", reflectiveOperationException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Launcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */