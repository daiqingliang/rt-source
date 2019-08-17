package java.net;

import java.io.Closeable;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.misc.JavaNetAccess;
import sun.misc.PerfCounter;
import sun.misc.Resource;
import sun.misc.SharedSecrets;
import sun.misc.URLClassPath;
import sun.net.www.ParseUtil;

public class URLClassLoader extends SecureClassLoader implements Closeable {
  private final URLClassPath ucp;
  
  private final AccessControlContext acc;
  
  private WeakHashMap<Closeable, Void> closeables = new WeakHashMap();
  
  public URLClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader) {
    super(paramClassLoader);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkCreateClassLoader(); 
    this.acc = AccessController.getContext();
    this.ucp = new URLClassPath(paramArrayOfURL, this.acc);
  }
  
  URLClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader, AccessControlContext paramAccessControlContext) {
    super(paramClassLoader);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkCreateClassLoader(); 
    this.acc = paramAccessControlContext;
    this.ucp = new URLClassPath(paramArrayOfURL, paramAccessControlContext);
  }
  
  public URLClassLoader(URL[] paramArrayOfURL) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkCreateClassLoader(); 
    this.acc = AccessController.getContext();
    this.ucp = new URLClassPath(paramArrayOfURL, this.acc);
  }
  
  URLClassLoader(URL[] paramArrayOfURL, AccessControlContext paramAccessControlContext) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkCreateClassLoader(); 
    this.acc = paramAccessControlContext;
    this.ucp = new URLClassPath(paramArrayOfURL, paramAccessControlContext);
  }
  
  public URLClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader, URLStreamHandlerFactory paramURLStreamHandlerFactory) {
    super(paramClassLoader);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkCreateClassLoader(); 
    this.acc = AccessController.getContext();
    this.ucp = new URLClassPath(paramArrayOfURL, paramURLStreamHandlerFactory, this.acc);
  }
  
  public InputStream getResourceAsStream(String paramString) {
    URL uRL = getResource(paramString);
    try {
      if (uRL == null)
        return null; 
      URLConnection uRLConnection = uRL.openConnection();
      InputStream inputStream = uRLConnection.getInputStream();
      if (uRLConnection instanceof JarURLConnection) {
        JarURLConnection jarURLConnection = (JarURLConnection)uRLConnection;
        JarFile jarFile = jarURLConnection.getJarFile();
        synchronized (this.closeables) {
          if (!this.closeables.containsKey(jarFile))
            this.closeables.put(jarFile, null); 
        } 
      } else if (uRLConnection instanceof sun.net.www.protocol.file.FileURLConnection) {
        synchronized (this.closeables) {
          this.closeables.put(inputStream, null);
        } 
      } 
      return inputStream;
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public void close() throws IOException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("closeClassLoader")); 
    List list = this.ucp.closeLoaders();
    synchronized (this.closeables) {
      Set set = this.closeables.keySet();
      for (Closeable closeable : set) {
        try {
          closeable.close();
        } catch (IOException iOException1) {
          list.add(iOException1);
        } 
      } 
      this.closeables.clear();
    } 
    if (list.isEmpty())
      return; 
    IOException iOException = (IOException)list.remove(0);
    for (IOException iOException1 : list)
      iOException.addSuppressed(iOException1); 
    throw iOException;
  }
  
  protected void addURL(URL paramURL) { this.ucp.addURL(paramURL); }
  
  public URL[] getURLs() { return this.ucp.getURLs(); }
  
  protected Class<?> findClass(final String name) throws ClassNotFoundException {
    Class clazz;
    try {
      clazz = (Class)AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
            public Class<?> run() throws ClassNotFoundException {
              String str = name.replace('.', '/').concat(".class");
              Resource resource = URLClassLoader.this.ucp.getResource(str, false);
              if (resource != null)
                try {
                  return URLClassLoader.this.defineClass(name, resource);
                } catch (IOException iOException) {
                  throw new ClassNotFoundException(name, iOException);
                }  
              return null;
            }
          }this.acc);
    } catch (PrivilegedActionException privilegedActionException) {
      throw (ClassNotFoundException)privilegedActionException.getException();
    } 
    if (clazz == null)
      throw new ClassNotFoundException(paramString); 
    return clazz;
  }
  
  private Package getAndVerifyPackage(String paramString, Manifest paramManifest, URL paramURL) {
    Package package = getPackage(paramString);
    if (package != null)
      if (package.isSealed()) {
        if (!package.isSealed(paramURL))
          throw new SecurityException("sealing violation: package " + paramString + " is sealed"); 
      } else if (paramManifest != null && isSealed(paramString, paramManifest)) {
        throw new SecurityException("sealing violation: can't seal package " + paramString + ": already loaded");
      }  
    return package;
  }
  
  private void definePackageInternal(String paramString, Manifest paramManifest, URL paramURL) {
    if (getAndVerifyPackage(paramString, paramManifest, paramURL) == null)
      try {
        if (paramManifest != null) {
          definePackage(paramString, paramManifest, paramURL);
        } else {
          definePackage(paramString, null, null, null, null, null, null, null);
        } 
      } catch (IllegalArgumentException illegalArgumentException) {
        if (getAndVerifyPackage(paramString, paramManifest, paramURL) == null)
          throw new AssertionError("Cannot find package " + paramString); 
      }  
  }
  
  private Class<?> defineClass(String paramString, Resource paramResource) throws IOException {
    long l = System.nanoTime();
    int i = paramString.lastIndexOf('.');
    URL uRL = paramResource.getCodeSourceURL();
    if (i != -1) {
      String str = paramString.substring(0, i);
      Manifest manifest = paramResource.getManifest();
      definePackageInternal(str, manifest, uRL);
    } 
    ByteBuffer byteBuffer = paramResource.getByteBuffer();
    if (byteBuffer != null) {
      CodeSigner[] arrayOfCodeSigner1 = paramResource.getCodeSigners();
      CodeSource codeSource1 = new CodeSource(uRL, arrayOfCodeSigner1);
      PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(l);
      return defineClass(paramString, byteBuffer, codeSource1);
    } 
    byte[] arrayOfByte = paramResource.getBytes();
    CodeSigner[] arrayOfCodeSigner = paramResource.getCodeSigners();
    CodeSource codeSource = new CodeSource(uRL, arrayOfCodeSigner);
    PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(l);
    return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length, codeSource);
  }
  
  protected Package definePackage(String paramString, Manifest paramManifest, URL paramURL) {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    URL uRL = null;
    Attributes attributes = SharedSecrets.javaUtilJarAccess().getTrustedAttributes(paramManifest, paramString.replace('.', '/').concat("/"));
    if (attributes != null) {
      str1 = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
      str2 = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
      str3 = attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
      str4 = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
      str5 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
      str6 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
      str7 = attributes.getValue(Attributes.Name.SEALED);
    } 
    attributes = paramManifest.getMainAttributes();
    if (attributes != null) {
      if (str1 == null)
        str1 = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE); 
      if (str2 == null)
        str2 = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION); 
      if (str3 == null)
        str3 = attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR); 
      if (str4 == null)
        str4 = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE); 
      if (str5 == null)
        str5 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION); 
      if (str6 == null)
        str6 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR); 
      if (str7 == null)
        str7 = attributes.getValue(Attributes.Name.SEALED); 
    } 
    if ("true".equalsIgnoreCase(str7))
      uRL = paramURL; 
    return definePackage(paramString, str1, str2, str3, str4, str5, str6, uRL);
  }
  
  private boolean isSealed(String paramString, Manifest paramManifest) {
    Attributes attributes = SharedSecrets.javaUtilJarAccess().getTrustedAttributes(paramManifest, paramString.replace('.', '/').concat("/"));
    String str = null;
    if (attributes != null)
      str = attributes.getValue(Attributes.Name.SEALED); 
    if (str == null && (attributes = paramManifest.getMainAttributes()) != null)
      str = attributes.getValue(Attributes.Name.SEALED); 
    return "true".equalsIgnoreCase(str);
  }
  
  public URL findResource(final String name) {
    URL uRL = (URL)AccessController.doPrivileged(new PrivilegedAction<URL>() {
          public URL run() { return URLClassLoader.this.ucp.findResource(name, true); }
        }this.acc);
    return (uRL != null) ? this.ucp.checkURL(uRL) : null;
  }
  
  public Enumeration<URL> findResources(String paramString) throws IOException {
    final Enumeration e = this.ucp.findResources(paramString, true);
    return new Enumeration<URL>() {
        private URL url = null;
        
        private boolean next() {
          if (this.url != null)
            return true; 
          do {
            URL uRL = (URL)AccessController.doPrivileged(new PrivilegedAction<URL>() {
                  public URL run() { return !e.hasMoreElements() ? null : (URL)e.nextElement(); }
                },  URLClassLoader.this.acc);
            if (uRL == null)
              break; 
            this.url = URLClassLoader.this.ucp.checkURL(uRL);
          } while (this.url == null);
          return (this.url != null);
        }
        
        public URL nextElement() {
          if (!next())
            throw new NoSuchElementException(); 
          URL uRL = this.url;
          this.url = null;
          return uRL;
        }
        
        public boolean hasMoreElements() { return next(); }
      };
  }
  
  protected PermissionCollection getPermissions(CodeSource paramCodeSource) {
    Object object;
    SocketPermission socketPermission;
    PermissionCollection permissionCollection = super.getPermissions(paramCodeSource);
    URL uRL = paramCodeSource.getLocation();
    try {
      object = uRL.openConnection();
      socketPermission = object.getPermission();
    } catch (IOException iOException) {
      socketPermission = null;
      object = null;
    } 
    if (socketPermission instanceof FilePermission) {
      String str = socketPermission.getName();
      if (str.endsWith(File.separator)) {
        str = str + "-";
        socketPermission = new FilePermission(str, "read");
      } 
    } else if (socketPermission == null && uRL.getProtocol().equals("file")) {
      String str = uRL.getFile().replace('/', File.separatorChar);
      str = ParseUtil.decode(str);
      if (str.endsWith(File.separator))
        str = str + "-"; 
      socketPermission = new FilePermission(str, "read");
    } else {
      URL uRL1 = uRL;
      if (object instanceof JarURLConnection)
        uRL1 = ((JarURLConnection)object).getJarFileURL(); 
      String str = uRL1.getHost();
      if (str != null && str.length() > 0)
        socketPermission = new SocketPermission(str, "connect,accept"); 
    } 
    if (socketPermission != null) {
      final SecurityManager sm = System.getSecurityManager();
      if (securityManager != null) {
        final SocketPermission fp = socketPermission;
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() throws SecurityException {
                sm.checkPermission(fp);
                return null;
              }
            },  this.acc);
      } 
      permissionCollection.add(socketPermission);
    } 
    return permissionCollection;
  }
  
  public static URLClassLoader newInstance(final URL[] urls, final ClassLoader parent) {
    final AccessControlContext acc = AccessController.getContext();
    return (URLClassLoader)AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
          public URLClassLoader run() { return new FactoryURLClassLoader(urls, parent, acc); }
        });
  }
  
  public static URLClassLoader newInstance(final URL[] urls) {
    final AccessControlContext acc = AccessController.getContext();
    return (URLClassLoader)AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
          public URLClassLoader run() { return new FactoryURLClassLoader(urls, acc); }
        });
  }
  
  static  {
    SharedSecrets.setJavaNetAccess(new JavaNetAccess() {
          public URLClassPath getURLClassPath(URLClassLoader param1URLClassLoader) { return param1URLClassLoader.ucp; }
          
          public String getOriginalHostName(InetAddress param1InetAddress) { return param1InetAddress.holder.getOriginalHostName(); }
        });
    ClassLoader.registerAsParallelCapable();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\URLClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */