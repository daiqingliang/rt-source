package java.net;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.net.www.ParseUtil;

public abstract class JarURLConnection extends URLConnection {
  private URL jarFileURL;
  
  private String entryName;
  
  protected URLConnection jarFileURLConnection;
  
  protected JarURLConnection(URL paramURL) throws MalformedURLException {
    super(paramURL);
    parseSpecs(paramURL);
  }
  
  private void parseSpecs(URL paramURL) throws MalformedURLException {
    String str = paramURL.getFile();
    int i = str.indexOf("!/");
    if (i == -1)
      throw new MalformedURLException("no !/ found in url spec:" + str); 
    this.jarFileURL = new URL(str.substring(0, i++));
    this.entryName = null;
    if (++i != str.length()) {
      this.entryName = str.substring(i, str.length());
      this.entryName = ParseUtil.decode(this.entryName);
    } 
  }
  
  public URL getJarFileURL() { return this.jarFileURL; }
  
  public String getEntryName() { return this.entryName; }
  
  public abstract JarFile getJarFile() throws IOException;
  
  public Manifest getManifest() throws IOException { return getJarFile().getManifest(); }
  
  public JarEntry getJarEntry() throws IOException { return getJarFile().getJarEntry(this.entryName); }
  
  public Attributes getAttributes() throws IOException {
    JarEntry jarEntry = getJarEntry();
    return (jarEntry != null) ? jarEntry.getAttributes() : null;
  }
  
  public Attributes getMainAttributes() throws IOException {
    Manifest manifest = getManifest();
    return (manifest != null) ? manifest.getMainAttributes() : null;
  }
  
  public Certificate[] getCertificates() throws IOException {
    JarEntry jarEntry = getJarEntry();
    return (jarEntry != null) ? jarEntry.getCertificates() : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\JarURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */