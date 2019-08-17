package sun.misc;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import sun.net.util.URLUtil;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;

public class URLClassPath {
  static final String USER_AGENT_JAVA_VERSION = "UA-Java-Version";
  
  static final String JAVA_VERSION = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
  
  private static final boolean DEBUG = (AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.debug")) != null);
  
  private static final boolean DEBUG_LOOKUP_CACHE = (AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.debugLookupCache")) != null);
  
  private static final boolean DISABLE_JAR_CHECKING;
  
  private static final boolean DISABLE_ACC_CHECKING;
  
  private static final boolean DISABLE_CP_URL_CHECK;
  
  private static final boolean DEBUG_CP_URL_CHECK;
  
  private ArrayList<URL> path = new ArrayList();
  
  Stack<URL> urls = new Stack();
  
  ArrayList<Loader> loaders = new ArrayList();
  
  HashMap<String, Loader> lmap = new HashMap();
  
  private URLStreamHandler jarHandler;
  
  private boolean closed = false;
  
  private final AccessControlContext acc;
  
  private URL[] lookupCacheURLs;
  
  private ClassLoader lookupCacheLoader;
  
  public URLClassPath(URL[] paramArrayOfURL, URLStreamHandlerFactory paramURLStreamHandlerFactory, AccessControlContext paramAccessControlContext) {
    for (byte b = 0; b < paramArrayOfURL.length; b++)
      this.path.add(paramArrayOfURL[b]); 
    push(paramArrayOfURL);
    if (paramURLStreamHandlerFactory != null)
      this.jarHandler = paramURLStreamHandlerFactory.createURLStreamHandler("jar"); 
    if (DISABLE_ACC_CHECKING) {
      this.acc = null;
    } else {
      this.acc = paramAccessControlContext;
    } 
  }
  
  public URLClassPath(URL[] paramArrayOfURL) { this(paramArrayOfURL, null, null); }
  
  public URLClassPath(URL[] paramArrayOfURL, AccessControlContext paramAccessControlContext) { this(paramArrayOfURL, null, paramAccessControlContext); }
  
  public List<IOException> closeLoaders() {
    if (this.closed)
      return Collections.emptyList(); 
    LinkedList linkedList = new LinkedList();
    for (Loader loader : this.loaders) {
      try {
        loader.close();
      } catch (IOException iOException) {
        linkedList.add(iOException);
      } 
    } 
    this.closed = true;
    return linkedList;
  }
  
  public void addURL(URL paramURL) {
    if (this.closed)
      return; 
    synchronized (this.urls) {
      if (paramURL == null || this.path.contains(paramURL))
        return; 
      this.urls.add(0, paramURL);
      this.path.add(paramURL);
      if (this.lookupCacheURLs != null)
        disableAllLookupCaches(); 
    } 
  }
  
  public URL[] getURLs() {
    synchronized (this.urls) {
      return (URL[])this.path.toArray(new URL[this.path.size()]);
    } 
  }
  
  public URL findResource(String paramString, boolean paramBoolean) {
    int[] arrayOfInt = getLookupCache(paramString);
    Loader loader;
    for (byte b = 0; (loader = getNextLoader(arrayOfInt, b)) != null; b++) {
      URL uRL = loader.findResource(paramString, paramBoolean);
      if (uRL != null)
        return uRL; 
    } 
    return null;
  }
  
  public Resource getResource(String paramString, boolean paramBoolean) {
    if (DEBUG)
      System.err.println("URLClassPath.getResource(\"" + paramString + "\")"); 
    int[] arrayOfInt = getLookupCache(paramString);
    Loader loader;
    for (byte b = 0; (loader = getNextLoader(arrayOfInt, b)) != null; b++) {
      Resource resource = loader.getResource(paramString, paramBoolean);
      if (resource != null)
        return resource; 
    } 
    return null;
  }
  
  public Enumeration<URL> findResources(final String name, final boolean check) { return new Enumeration<URL>() {
        private int index = 0;
        
        private int[] cache = URLClassPath.this.getLookupCache(name);
        
        private URL url = null;
        
        private boolean next() {
          if (this.url != null)
            return true; 
          URLClassPath.Loader loader;
          while ((loader = URLClassPath.this.getNextLoader(this.cache, this.index++)) != null) {
            this.url = loader.findResource(name, check);
            if (this.url != null)
              return true; 
          } 
          return false;
        }
        
        public boolean hasMoreElements() { return next(); }
        
        public URL nextElement() {
          if (!next())
            throw new NoSuchElementException(); 
          URL uRL = this.url;
          this.url = null;
          return uRL;
        }
      }; }
  
  public Resource getResource(String paramString) { return getResource(paramString, true); }
  
  public Enumeration<Resource> getResources(final String name, final boolean check) { return new Enumeration<Resource>() {
        private int index = 0;
        
        private int[] cache = URLClassPath.this.getLookupCache(name);
        
        private Resource res = null;
        
        private boolean next() {
          if (this.res != null)
            return true; 
          URLClassPath.Loader loader;
          while ((loader = URLClassPath.this.getNextLoader(this.cache, this.index++)) != null) {
            this.res = loader.getResource(name, check);
            if (this.res != null)
              return true; 
          } 
          return false;
        }
        
        public boolean hasMoreElements() { return next(); }
        
        public Resource nextElement() {
          if (!next())
            throw new NoSuchElementException(); 
          Resource resource = this.res;
          this.res = null;
          return resource;
        }
      }; }
  
  public Enumeration<Resource> getResources(String paramString) { return getResources(paramString, true); }
  
  void initLookupCache(ClassLoader paramClassLoader) {
    if ((this.lookupCacheURLs = getLookupCacheURLs(paramClassLoader)) != null) {
      this.lookupCacheLoader = paramClassLoader;
    } else {
      disableAllLookupCaches();
    } 
  }
  
  static void disableAllLookupCaches() { lookupCacheEnabled = false; }
  
  private static native URL[] getLookupCacheURLs(ClassLoader paramClassLoader);
  
  private static native int[] getLookupCacheForClassLoader(ClassLoader paramClassLoader, String paramString);
  
  private static native boolean knownToNotExist0(ClassLoader paramClassLoader, String paramString);
  
  boolean knownToNotExist(String paramString) { return (this.lookupCacheURLs != null && lookupCacheEnabled) ? knownToNotExist0(this.lookupCacheLoader, paramString) : 0; }
  
  private int[] getLookupCache(String paramString) {
    if (this.lookupCacheURLs == null || !lookupCacheEnabled)
      return null; 
    int[] arrayOfInt = getLookupCacheForClassLoader(this.lookupCacheLoader, paramString);
    if (arrayOfInt != null && arrayOfInt.length > 0) {
      int i = arrayOfInt[arrayOfInt.length - 1];
      if (!ensureLoaderOpened(i)) {
        if (DEBUG_LOOKUP_CACHE)
          System.out.println("Expanded loaders FAILED " + this.loaders.size() + " for maxindex=" + i); 
        return null;
      } 
    } 
    return arrayOfInt;
  }
  
  private boolean ensureLoaderOpened(int paramInt) {
    if (this.loaders.size() <= paramInt) {
      if (getLoader(paramInt) == null)
        return false; 
      if (!lookupCacheEnabled)
        return false; 
      if (DEBUG_LOOKUP_CACHE)
        System.out.println("Expanded loaders " + this.loaders.size() + " to index=" + paramInt); 
    } 
    return true;
  }
  
  private void validateLookupCache(int paramInt, String paramString) {
    if (this.lookupCacheURLs != null && lookupCacheEnabled) {
      if (paramInt < this.lookupCacheURLs.length && paramString.equals(URLUtil.urlNoFragString(this.lookupCacheURLs[paramInt])))
        return; 
      if (DEBUG || DEBUG_LOOKUP_CACHE)
        System.out.println("WARNING: resource lookup cache invalidated for lookupCacheLoader at " + paramInt); 
      disableAllLookupCaches();
    } 
  }
  
  private Loader getNextLoader(int[] paramArrayOfInt, int paramInt) {
    if (this.closed)
      return null; 
    if (paramArrayOfInt != null) {
      if (paramInt < paramArrayOfInt.length) {
        Loader loader = (Loader)this.loaders.get(paramArrayOfInt[paramInt]);
        if (DEBUG_LOOKUP_CACHE)
          System.out.println("HASCACHE: Loading from : " + paramArrayOfInt[paramInt] + " = " + loader.getBaseURL()); 
        return loader;
      } 
      return null;
    } 
    return getLoader(paramInt);
  }
  
  private Loader getLoader(int paramInt) {
    if (this.closed)
      return null; 
    while (this.loaders.size() < paramInt + 1) {
      Loader loader;
      URL uRL;
      synchronized (this.urls) {
        if (this.urls.empty())
          return null; 
        uRL = (URL)this.urls.pop();
      } 
      String str = URLUtil.urlNoFragString(uRL);
      if (this.lmap.containsKey(str))
        continue; 
      try {
        loader = getLoader(uRL);
        URL[] arrayOfURL = loader.getClassPath();
        if (arrayOfURL != null)
          push(arrayOfURL); 
      } catch (IOException iOException) {
        continue;
      } catch (SecurityException securityException) {
        if (DEBUG)
          System.err.println("Failed to access " + uRL + ", " + securityException); 
        continue;
      } 
      validateLookupCache(this.loaders.size(), str);
      this.loaders.add(loader);
      this.lmap.put(str, loader);
    } 
    if (DEBUG_LOOKUP_CACHE)
      System.out.println("NOCACHE: Loading from : " + paramInt); 
    return (Loader)this.loaders.get(paramInt);
  }
  
  private Loader getLoader(final URL url) throws IOException {
    try {
      return (Loader)AccessController.doPrivileged(new PrivilegedExceptionAction<Loader>() {
            public URLClassPath.Loader run() throws IOException {
              String str = url.getFile();
              return (str != null && str.endsWith("/")) ? ("file".equals(url.getProtocol()) ? new URLClassPath.FileLoader(url) : new URLClassPath.Loader(url)) : new URLClassPath.JarLoader(url, URLClassPath.this.jarHandler, URLClassPath.this.lmap, URLClassPath.this.acc);
            }
          }this.acc);
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
  
  private void push(URL[] paramArrayOfURL) {
    synchronized (this.urls) {
      for (int i = paramArrayOfURL.length - 1; i >= 0; i--)
        this.urls.push(paramArrayOfURL[i]); 
    } 
  }
  
  public static URL[] pathToURLs(String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
    URL[] arrayOfURL = new URL[stringTokenizer.countTokens()];
    byte b = 0;
    while (stringTokenizer.hasMoreTokens()) {
      File file = new File(stringTokenizer.nextToken());
      try {
        file = new File(file.getCanonicalPath());
      } catch (IOException iOException) {}
      try {
        arrayOfURL[b++] = ParseUtil.fileToEncodedURL(file);
      } catch (IOException iOException) {}
    } 
    if (arrayOfURL.length != b) {
      URL[] arrayOfURL1 = new URL[b];
      System.arraycopy(arrayOfURL, 0, arrayOfURL1, 0, b);
      arrayOfURL = arrayOfURL1;
    } 
    return arrayOfURL;
  }
  
  public URL checkURL(URL paramURL) {
    try {
      check(paramURL);
    } catch (Exception exception) {
      return null;
    } 
    return paramURL;
  }
  
  static void check(URL paramURL) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      URLConnection uRLConnection = paramURL.openConnection();
      Permission permission = uRLConnection.getPermission();
      if (permission != null)
        try {
          securityManager.checkPermission(permission);
        } catch (SecurityException securityException) {
          if (permission instanceof java.io.FilePermission && permission.getActions().indexOf("read") != -1) {
            securityManager.checkRead(permission.getName());
          } else if (permission instanceof java.net.SocketPermission && permission.getActions().indexOf("connect") != -1) {
            URL uRL = paramURL;
            if (uRLConnection instanceof JarURLConnection)
              uRL = ((JarURLConnection)uRLConnection).getJarFileURL(); 
            securityManager.checkConnect(uRL.getHost(), uRL.getPort());
          } else {
            throw securityException;
          } 
        }  
    } 
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.disableJarChecking"));
    DISABLE_JAR_CHECKING = (str != null) ? ((str.equals("true") || str.equals(""))) : false;
    str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.net.URLClassPath.disableRestrictedPermissions"));
    DISABLE_ACC_CHECKING = (str != null) ? ((str.equals("true") || str.equals(""))) : false;
    str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.net.URLClassPath.disableClassPathURLCheck", "true"));
    DISABLE_CP_URL_CHECK = (str != null) ? ((str.equals("true") || str.isEmpty())) : false;
    DEBUG_CP_URL_CHECK = "debug".equals(str);
    lookupCacheEnabled = "true".equals(VM.getSavedProperty("sun.cds.enableSharedLookupCache"));
  }
  
  private static class FileLoader extends Loader {
    private File dir;
    
    FileLoader(URL param1URL) {
      super(param1URL);
      if (!"file".equals(param1URL.getProtocol()))
        throw new IllegalArgumentException("url"); 
      String str = param1URL.getFile().replace('/', File.separatorChar);
      str = ParseUtil.decode(str);
      this.dir = (new File(str)).getCanonicalFile();
    }
    
    URL findResource(String param1String, boolean param1Boolean) {
      Resource resource = getResource(param1String, param1Boolean);
      return (resource != null) ? resource.getURL() : null;
    }
    
    Resource getResource(final String name, boolean param1Boolean) {
      try {
        final File file;
        URL uRL2 = new URL(getBaseURL(), ".");
        final URL url = new URL(getBaseURL(), ParseUtil.encodePath(param1String, false));
        if (!uRL1.getFile().startsWith(uRL2.getFile()))
          return null; 
        if (param1Boolean)
          URLClassPath.check(uRL1); 
        if (param1String.indexOf("..") != -1) {
          file = (new File(this.dir, param1String.replace('/', File.separatorChar))).getCanonicalFile();
          if (!file.getPath().startsWith(this.dir.getPath()))
            return null; 
        } else {
          file = new File(this.dir, param1String.replace('/', File.separatorChar));
        } 
        if (file.exists())
          return new Resource() {
              public String getName() { return name; }
              
              public URL getURL() { return url; }
              
              public URL getCodeSourceURL() { return URLClassPath.FileLoader.this.getBaseURL(); }
              
              public InputStream getInputStream() throws IOException { return new FileInputStream(file); }
              
              public int getContentLength() throws IOException { return (int)file.length(); }
            }; 
      } catch (Exception exception) {
        return null;
      } 
      return null;
    }
  }
  
  static class JarLoader extends Loader {
    private JarFile jar;
    
    private final URL csu;
    
    private JarIndex index;
    
    private MetaIndex metaIndex;
    
    private URLStreamHandler handler;
    
    private final HashMap<String, URLClassPath.Loader> lmap;
    
    private final AccessControlContext acc;
    
    private boolean closed = false;
    
    private static final JavaUtilZipFileAccess zipAccess = SharedSecrets.getJavaUtilZipFileAccess();
    
    JarLoader(URL param1URL, URLStreamHandler param1URLStreamHandler, HashMap<String, URLClassPath.Loader> param1HashMap, AccessControlContext param1AccessControlContext) throws IOException {
      super(new URL("jar", "", -1, param1URL + "!/", param1URLStreamHandler));
      this.csu = param1URL;
      this.handler = param1URLStreamHandler;
      this.lmap = param1HashMap;
      this.acc = param1AccessControlContext;
      if (!isOptimizable(param1URL)) {
        ensureOpen();
      } else {
        String str = param1URL.getFile();
        if (str != null) {
          str = ParseUtil.decode(str);
          File file = new File(str);
          this.metaIndex = MetaIndex.forJar(file);
          if (this.metaIndex != null && !file.exists())
            this.metaIndex = null; 
        } 
        if (this.metaIndex == null)
          ensureOpen(); 
      } 
    }
    
    public void close() {
      if (!this.closed) {
        this.closed = true;
        ensureOpen();
        this.jar.close();
      } 
    }
    
    JarFile getJarFile() { return this.jar; }
    
    private boolean isOptimizable(URL param1URL) { return "file".equals(param1URL.getProtocol()); }
    
    private void ensureOpen() {
      if (this.jar == null)
        try {
          AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() throws IOException {
                  if (DEBUG) {
                    System.err.println("Opening " + URLClassPath.JarLoader.this.csu);
                    Thread.dumpStack();
                  } 
                  URLClassPath.JarLoader.this.jar = URLClassPath.JarLoader.this.getJarFile(URLClassPath.JarLoader.this.csu);
                  URLClassPath.JarLoader.this.index = JarIndex.getJarIndex(URLClassPath.JarLoader.this.jar, URLClassPath.JarLoader.this.metaIndex);
                  if (URLClassPath.JarLoader.this.index != null) {
                    String[] arrayOfString = URLClassPath.JarLoader.this.index.getJarFiles();
                    for (byte b = 0; b < arrayOfString.length; b++) {
                      try {
                        URL uRL = new URL(URLClassPath.JarLoader.this.csu, arrayOfString[b]);
                        String str = URLUtil.urlNoFragString(uRL);
                        if (!URLClassPath.JarLoader.this.lmap.containsKey(str))
                          URLClassPath.JarLoader.this.lmap.put(str, null); 
                      } catch (MalformedURLException malformedURLException) {}
                    } 
                  } 
                  return null;
                }
              }this.acc);
        } catch (PrivilegedActionException privilegedActionException) {
          throw (IOException)privilegedActionException.getException();
        }  
    }
    
    static JarFile checkJar(JarFile param1JarFile) throws IOException {
      if (System.getSecurityManager() != null && !DISABLE_JAR_CHECKING && !zipAccess.startsWithLocHeader(param1JarFile)) {
        IOException iOException = new IOException("Invalid Jar file");
        try {
          param1JarFile.close();
        } catch (IOException iOException1) {
          iOException.addSuppressed(iOException1);
        } 
        throw iOException;
      } 
      return param1JarFile;
    }
    
    private JarFile getJarFile(URL param1URL) throws IOException {
      if (isOptimizable(param1URL)) {
        FileURLMapper fileURLMapper = new FileURLMapper(param1URL);
        if (!fileURLMapper.exists())
          throw new FileNotFoundException(fileURLMapper.getPath()); 
        return checkJar(new JarFile(fileURLMapper.getPath()));
      } 
      URLConnection uRLConnection = getBaseURL().openConnection();
      uRLConnection.setRequestProperty("UA-Java-Version", URLClassPath.JAVA_VERSION);
      JarFile jarFile = ((JarURLConnection)uRLConnection).getJarFile();
      return checkJar(jarFile);
    }
    
    JarIndex getIndex() {
      try {
        ensureOpen();
      } catch (IOException iOException) {
        throw new InternalError(iOException);
      } 
      return this.index;
    }
    
    Resource checkResource(final String name, boolean param1Boolean, final JarEntry entry) {
      final URL url;
      try {
        uRL = new URL(getBaseURL(), ParseUtil.encodePath(param1String, false));
        if (param1Boolean)
          URLClassPath.check(uRL); 
      } catch (MalformedURLException malformedURLException) {
        return null;
      } catch (IOException iOException) {
        return null;
      } catch (AccessControlException accessControlException) {
        return null;
      } 
      return new Resource() {
          public String getName() { return name; }
          
          public URL getURL() { return url; }
          
          public URL getCodeSourceURL() { return URLClassPath.JarLoader.this.csu; }
          
          public InputStream getInputStream() throws IOException { return URLClassPath.JarLoader.this.jar.getInputStream(entry); }
          
          public int getContentLength() throws IOException { return (int)entry.getSize(); }
          
          public Manifest getManifest() throws IOException {
            SharedSecrets.javaUtilJarAccess().ensureInitialization(URLClassPath.JarLoader.this.jar);
            return URLClassPath.JarLoader.this.jar.getManifest();
          }
          
          public Certificate[] getCertificates() { return entry.getCertificates(); }
          
          public CodeSigner[] getCodeSigners() { return entry.getCodeSigners(); }
        };
    }
    
    boolean validIndex(String param1String) {
      String str = param1String;
      int i;
      if ((i = param1String.lastIndexOf("/")) != -1)
        str = param1String.substring(0, i); 
      Enumeration enumeration = this.jar.entries();
      while (enumeration.hasMoreElements()) {
        ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
        String str1 = zipEntry.getName();
        if ((i = str1.lastIndexOf("/")) != -1)
          str1 = str1.substring(0, i); 
        if (str1.equals(str))
          return true; 
      } 
      return false;
    }
    
    URL findResource(String param1String, boolean param1Boolean) {
      Resource resource = getResource(param1String, param1Boolean);
      return (resource != null) ? resource.getURL() : null;
    }
    
    Resource getResource(String param1String, boolean param1Boolean) {
      if (this.metaIndex != null && !this.metaIndex.mayContain(param1String))
        return null; 
      try {
        ensureOpen();
      } catch (IOException iOException) {
        throw new InternalError(iOException);
      } 
      JarEntry jarEntry = this.jar.getJarEntry(param1String);
      if (jarEntry != null)
        return checkResource(param1String, param1Boolean, jarEntry); 
      if (this.index == null)
        return null; 
      HashSet hashSet = new HashSet();
      return getResource(param1String, param1Boolean, hashSet);
    }
    
    Resource getResource(String param1String, boolean param1Boolean, Set<String> param1Set) {
      byte b = 0;
      LinkedList linkedList = null;
      if ((linkedList = this.index.get(param1String)) == null)
        return null; 
      do {
        int i = linkedList.size();
        String[] arrayOfString = (String[])linkedList.toArray(new String[i]);
        while (b < i) {
          final URL url;
          JarLoader jarLoader;
          String str = arrayOfString[b++];
          try {
            uRL = new URL(this.csu, str);
            String str1 = URLUtil.urlNoFragString(uRL);
            if ((jarLoader = (JarLoader)this.lmap.get(str1)) == null) {
              jarLoader = (JarLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<JarLoader>() {
                    public URLClassPath.JarLoader run() throws IOException { return new URLClassPath.JarLoader(url, URLClassPath.JarLoader.this.handler, URLClassPath.JarLoader.this.lmap, URLClassPath.JarLoader.this.acc); }
                  }this.acc);
              JarIndex jarIndex = jarLoader.getIndex();
              if (jarIndex != null) {
                int j = str.lastIndexOf("/");
                jarIndex.merge(this.index, (j == -1) ? null : str.substring(0, j + 1));
              } 
              this.lmap.put(str1, jarLoader);
            } 
          } catch (PrivilegedActionException privilegedActionException) {
            continue;
          } catch (MalformedURLException malformedURLException) {
            continue;
          } 
          boolean bool = !param1Set.add(URLUtil.urlNoFragString(uRL)) ? 1 : 0;
          if (!bool) {
            try {
              jarLoader.ensureOpen();
            } catch (IOException iOException) {
              throw new InternalError(iOException);
            } 
            JarEntry jarEntry = jarLoader.jar.getJarEntry(param1String);
            if (jarEntry != null)
              return jarLoader.checkResource(param1String, param1Boolean, jarEntry); 
            if (!jarLoader.validIndex(param1String))
              throw new InvalidJarIndexException("Invalid index"); 
          } 
          Resource resource;
          if (!bool && jarLoader != this && jarLoader.getIndex() != null && (resource = jarLoader.getResource(param1String, param1Boolean, param1Set)) != null)
            return resource; 
        } 
        linkedList = this.index.get(param1String);
      } while (b < linkedList.size());
      return null;
    }
    
    URL[] getClassPath() {
      if (this.index != null)
        return null; 
      if (this.metaIndex != null)
        return null; 
      ensureOpen();
      parseExtensionsDependencies();
      if (SharedSecrets.javaUtilJarAccess().jarFileHasClassPathAttribute(this.jar)) {
        Manifest manifest = this.jar.getManifest();
        if (manifest != null) {
          Attributes attributes = manifest.getMainAttributes();
          if (attributes != null) {
            String str = attributes.getValue(Attributes.Name.CLASS_PATH);
            if (str != null)
              return parseClassPath(this.csu, str); 
          } 
        } 
      } 
      return null;
    }
    
    private void parseExtensionsDependencies() { ExtensionDependency.checkExtensionsDependencies(this.jar); }
    
    private URL[] parseClassPath(URL param1URL, String param1String) throws MalformedURLException {
      StringTokenizer stringTokenizer = new StringTokenizer(param1String);
      URL[] arrayOfURL = new URL[stringTokenizer.countTokens()];
      byte b = 0;
      while (stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken();
        URL uRL = DISABLE_CP_URL_CHECK ? new URL(param1URL, str) : safeResolve(param1URL, str);
        if (uRL != null) {
          arrayOfURL[b] = uRL;
          b++;
        } 
      } 
      if (b == 0) {
        arrayOfURL = null;
      } else if (b != arrayOfURL.length) {
        arrayOfURL = (URL[])Arrays.copyOf(arrayOfURL, b);
      } 
      return arrayOfURL;
    }
    
    static URL safeResolve(URL param1URL, String param1String) {
      String str = param1String.replace(File.separatorChar, '/');
      try {
        if (!URI.create(str).isAbsolute()) {
          URL uRL = new URL(param1URL, str);
          if (param1URL.getProtocol().equalsIgnoreCase("file"))
            return uRL; 
          String str1 = param1URL.getPath();
          String str2 = uRL.getPath();
          int i = str1.lastIndexOf('/');
          if (i == -1)
            i = str1.length() - 1; 
          if (str2.regionMatches(0, str1, 0, i + 1) && str2.indexOf("..", i) == -1)
            return uRL; 
        } 
      } catch (MalformedURLException|IllegalArgumentException malformedURLException) {}
      if (DEBUG_CP_URL_CHECK)
        System.err.println("Class-Path entry: \"" + param1String + "\" ignored in JAR file " + param1URL); 
      return null;
    }
  }
  
  private static class Loader implements Closeable {
    private final URL base;
    
    private JarFile jarfile;
    
    Loader(URL param1URL) { this.base = param1URL; }
    
    URL getBaseURL() { return this.base; }
    
    URL findResource(String param1String, boolean param1Boolean) {
      URL uRL;
      try {
        uRL = new URL(this.base, ParseUtil.encodePath(param1String, false));
      } catch (MalformedURLException malformedURLException) {
        throw new IllegalArgumentException("name");
      } 
      try {
        if (param1Boolean)
          URLClassPath.check(uRL); 
        URLConnection uRLConnection = uRL.openConnection();
        if (uRLConnection instanceof HttpURLConnection) {
          HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
          httpURLConnection.setRequestMethod("HEAD");
          if (httpURLConnection.getResponseCode() >= 400)
            return null; 
        } else {
          uRLConnection.setUseCaches(false);
          InputStream inputStream = uRLConnection.getInputStream();
          inputStream.close();
        } 
        return uRL;
      } catch (Exception exception) {
        return null;
      } 
    }
    
    Resource getResource(final String name, boolean param1Boolean) {
      final URLConnection uc;
      final URL url;
      try {
        uRL = new URL(this.base, ParseUtil.encodePath(param1String, false));
      } catch (MalformedURLException null) {
        throw new IllegalArgumentException("name");
      } 
      try {
        if (param1Boolean)
          URLClassPath.check(uRL); 
        uRLConnection = uRL.openConnection();
        InputStream inputStream = uRLConnection.getInputStream();
        if (uRLConnection instanceof JarURLConnection) {
          JarURLConnection jarURLConnection = (JarURLConnection)uRLConnection;
          this.jarfile = URLClassPath.JarLoader.checkJar(jarURLConnection.getJarFile());
        } 
      } catch (Exception exception) {
        return null;
      } 
      return new Resource() {
          public String getName() { return name; }
          
          public URL getURL() { return url; }
          
          public URL getCodeSourceURL() { return URLClassPath.Loader.this.base; }
          
          public InputStream getInputStream() throws IOException { return uc.getInputStream(); }
          
          public int getContentLength() throws IOException { return uc.getContentLength(); }
        };
    }
    
    Resource getResource(String param1String) { return getResource(param1String, true); }
    
    public void close() {
      if (this.jarfile != null)
        this.jarfile.close(); 
    }
    
    URL[] getClassPath() { return null; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\URLClassPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */