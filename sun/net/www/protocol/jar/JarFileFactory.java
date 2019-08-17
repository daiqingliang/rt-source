package sun.net.www.protocol.jar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.HashMap;
import java.util.jar.JarFile;
import sun.net.util.URLUtil;

class JarFileFactory implements URLJarFile.URLJarFileCloseController {
  private static final HashMap<String, JarFile> fileCache = new HashMap();
  
  private static final HashMap<JarFile, URL> urlCache = new HashMap();
  
  private static final JarFileFactory instance = new JarFileFactory();
  
  public static JarFileFactory getInstance() { return instance; }
  
  URLConnection getConnection(JarFile paramJarFile) throws IOException {
    URL uRL;
    synchronized (instance) {
      uRL = (URL)urlCache.get(paramJarFile);
    } 
    return (uRL != null) ? uRL.openConnection() : null;
  }
  
  public JarFile get(URL paramURL) throws IOException { return get(paramURL, true); }
  
  JarFile get(URL paramURL, boolean paramBoolean) throws IOException {
    JarFile jarFile;
    if (paramURL.getProtocol().equalsIgnoreCase("file")) {
      jarFile = paramURL.getHost();
      if (jarFile != null && !jarFile.equals("") && !jarFile.equalsIgnoreCase("localhost"))
        paramURL = new URL("file", "", "//" + jarFile + paramURL.getPath()); 
    } 
    if (paramBoolean) {
      synchronized (instance) {
        jarFile = getCachedJarFile(paramURL);
      } 
      if (jarFile == null) {
        JarFile jarFile1 = URLJarFile.getJarFile(paramURL, this);
        synchronized (instance) {
          jarFile = getCachedJarFile(paramURL);
          if (jarFile == null) {
            fileCache.put(URLUtil.urlNoFragString(paramURL), jarFile1);
            urlCache.put(jarFile1, paramURL);
            jarFile = jarFile1;
          } else if (jarFile1 != null) {
            jarFile1.close();
          } 
        } 
      } 
    } else {
      jarFile = URLJarFile.getJarFile(paramURL, this);
    } 
    if (jarFile == null)
      throw new FileNotFoundException(paramURL.toString()); 
    return jarFile;
  }
  
  public void close(JarFile paramJarFile) {
    synchronized (instance) {
      URL uRL = (URL)urlCache.remove(paramJarFile);
      if (uRL != null)
        fileCache.remove(URLUtil.urlNoFragString(uRL)); 
    } 
  }
  
  private JarFile getCachedJarFile(URL paramURL) throws IOException {
    assert Thread.holdsLock(instance);
    JarFile jarFile = (JarFile)fileCache.get(URLUtil.urlNoFragString(paramURL));
    if (jarFile != null) {
      Permission permission = getPermission(jarFile);
      if (permission != null) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          try {
            securityManager.checkPermission(permission);
          } catch (SecurityException securityException) {
            if (permission instanceof java.io.FilePermission && permission.getActions().indexOf("read") != -1) {
              securityManager.checkRead(permission.getName());
            } else if (permission instanceof java.net.SocketPermission && permission.getActions().indexOf("connect") != -1) {
              securityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
            } else {
              throw securityException;
            } 
          }  
      } 
    } 
    return jarFile;
  }
  
  private Permission getPermission(JarFile paramJarFile) {
    try {
      URLConnection uRLConnection = getConnection(paramJarFile);
      if (uRLConnection != null)
        return uRLConnection.getPermission(); 
    } catch (IOException iOException) {}
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\jar\JarFileFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */