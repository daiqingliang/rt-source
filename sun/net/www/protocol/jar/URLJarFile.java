package sun.net.www.protocol.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import sun.net.www.ParseUtil;

public class URLJarFile extends JarFile {
  private static URLJarFileCallBack callback = null;
  
  private URLJarFileCloseController closeController = null;
  
  private static int BUF_SIZE = 2048;
  
  private Manifest superMan;
  
  private Attributes superAttr;
  
  private Map<String, Attributes> superEntries;
  
  static JarFile getJarFile(URL paramURL) throws IOException { return getJarFile(paramURL, null); }
  
  static JarFile getJarFile(URL paramURL, URLJarFileCloseController paramURLJarFileCloseController) throws IOException { return isFileURL(paramURL) ? new URLJarFile(paramURL, paramURLJarFileCloseController) : retrieve(paramURL, paramURLJarFileCloseController); }
  
  public URLJarFile(File paramFile) throws IOException { this(paramFile, null); }
  
  public URLJarFile(File paramFile, URLJarFileCloseController paramURLJarFileCloseController) throws IOException {
    super(paramFile, true, 5);
    this.closeController = paramURLJarFileCloseController;
  }
  
  private URLJarFile(URL paramURL, URLJarFileCloseController paramURLJarFileCloseController) throws IOException {
    super(ParseUtil.decode(paramURL.getFile()));
    this.closeController = paramURLJarFileCloseController;
  }
  
  private static boolean isFileURL(URL paramURL) {
    if (paramURL.getProtocol().equalsIgnoreCase("file")) {
      String str = paramURL.getHost();
      if (str == null || str.equals("") || str.equals("~") || str.equalsIgnoreCase("localhost"))
        return true; 
    } 
    return false;
  }
  
  protected void finalize() throws IOException { close(); }
  
  public ZipEntry getEntry(String paramString) {
    ZipEntry zipEntry = super.getEntry(paramString);
    if (zipEntry != null) {
      if (zipEntry instanceof JarEntry)
        return new URLJarFileEntry((JarEntry)zipEntry); 
      throw new InternalError(getClass() + " returned unexpected entry type " + zipEntry.getClass());
    } 
    return null;
  }
  
  public Manifest getManifest() throws IOException {
    if (!isSuperMan())
      return null; 
    Manifest manifest = new Manifest();
    Attributes attributes = manifest.getMainAttributes();
    attributes.putAll((Map)this.superAttr.clone());
    if (this.superEntries != null) {
      Map map = manifest.getEntries();
      for (String str : this.superEntries.keySet()) {
        Attributes attributes1 = (Attributes)this.superEntries.get(str);
        map.put(str, (Attributes)attributes1.clone());
      } 
    } 
    return manifest;
  }
  
  public void close() throws IOException {
    if (this.closeController != null)
      this.closeController.close(this); 
    super.close();
  }
  
  private boolean isSuperMan() throws IOException {
    if (this.superMan == null)
      this.superMan = super.getManifest(); 
    if (this.superMan != null) {
      this.superAttr = this.superMan.getMainAttributes();
      this.superEntries = this.superMan.getEntries();
      return true;
    } 
    return false;
  }
  
  private static JarFile retrieve(URL paramURL) throws IOException { return retrieve(paramURL, null); }
  
  private static JarFile retrieve(URL paramURL, final URLJarFileCloseController closeController) throws IOException {
    if (callback != null)
      return callback.retrieve(paramURL); 
    JarFile jarFile = null;
    try (InputStream null = paramURL.openConnection().getInputStream()) {
      jarFile = (JarFile)AccessController.doPrivileged(new PrivilegedExceptionAction<JarFile>() {
            public JarFile run() throws IOException {
              Path path = Files.createTempFile("jar_cache", null, new java.nio.file.attribute.FileAttribute[0]);
              try {
                Files.copy(in, path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
                URLJarFile uRLJarFile = new URLJarFile(path.toFile(), closeController);
                path.toFile().deleteOnExit();
                return uRLJarFile;
              } catch (Throwable throwable) {
                try {
                  Files.delete(path);
                } catch (IOException iOException) {
                  throwable.addSuppressed(iOException);
                } 
                throw throwable;
              } 
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
    return jarFile;
  }
  
  public static void setCallBack(URLJarFileCallBack paramURLJarFileCallBack) { callback = paramURLJarFileCallBack; }
  
  public static interface URLJarFileCloseController {
    void close(JarFile param1JarFile);
  }
  
  private class URLJarFileEntry extends JarEntry {
    private JarEntry je;
    
    URLJarFileEntry(JarEntry param1JarEntry) {
      super(param1JarEntry);
      this.je = param1JarEntry;
    }
    
    public Attributes getAttributes() throws IOException {
      if (URLJarFile.this.isSuperMan()) {
        Map map = URLJarFile.this.superEntries;
        if (map != null) {
          Attributes attributes = (Attributes)map.get(getName());
          if (attributes != null)
            return (Attributes)attributes.clone(); 
        } 
      } 
      return null;
    }
    
    public Certificate[] getCertificates() {
      Certificate[] arrayOfCertificate = this.je.getCertificates();
      return (arrayOfCertificate == null) ? null : (Certificate[])arrayOfCertificate.clone();
    }
    
    public CodeSigner[] getCodeSigners() {
      CodeSigner[] arrayOfCodeSigner = this.je.getCodeSigners();
      return (arrayOfCodeSigner == null) ? null : (CodeSigner[])arrayOfCodeSigner.clone();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\jar\URLJarFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */