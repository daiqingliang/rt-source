package java.util.jar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import sun.security.util.ManifestEntryVerifier;

public class JarInputStream extends ZipInputStream {
  private Manifest man;
  
  private JarEntry first;
  
  private JarVerifier jv;
  
  private ManifestEntryVerifier mev;
  
  private final boolean doVerify;
  
  private boolean tryManifest;
  
  public JarInputStream(InputStream paramInputStream) throws IOException { this(paramInputStream, true); }
  
  public JarInputStream(InputStream paramInputStream, boolean paramBoolean) throws IOException {
    super(paramInputStream);
    this.doVerify = paramBoolean;
    JarEntry jarEntry = (JarEntry)super.getNextEntry();
    if (jarEntry != null && jarEntry.getName().equalsIgnoreCase("META-INF/"))
      jarEntry = (JarEntry)super.getNextEntry(); 
    this.first = checkManifest(jarEntry);
  }
  
  private JarEntry checkManifest(JarEntry paramJarEntry) throws IOException {
    if (paramJarEntry != null && "META-INF/MANIFEST.MF".equalsIgnoreCase(paramJarEntry.getName())) {
      this.man = new Manifest();
      byte[] arrayOfByte = getBytes(new BufferedInputStream(this));
      this.man.read(new ByteArrayInputStream(arrayOfByte));
      closeEntry();
      if (this.doVerify) {
        this.jv = new JarVerifier(arrayOfByte);
        this.mev = new ManifestEntryVerifier(this.man);
      } 
      return (JarEntry)super.getNextEntry();
    } 
    return paramJarEntry;
  }
  
  private byte[] getBytes(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = new byte[8192];
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
    int i;
    while ((i = paramInputStream.read(arrayOfByte, 0, arrayOfByte.length)) != -1)
      byteArrayOutputStream.write(arrayOfByte, 0, i); 
    return byteArrayOutputStream.toByteArray();
  }
  
  public Manifest getManifest() { return this.man; }
  
  public ZipEntry getNextEntry() throws IOException {
    JarEntry jarEntry;
    if (this.first == null) {
      jarEntry = (JarEntry)super.getNextEntry();
      if (this.tryManifest) {
        jarEntry = checkManifest(jarEntry);
        this.tryManifest = false;
      } 
    } else {
      jarEntry = this.first;
      if (this.first.getName().equalsIgnoreCase("META-INF/INDEX.LIST"))
        this.tryManifest = true; 
      this.first = null;
    } 
    if (this.jv != null && jarEntry != null)
      if (this.jv.nothingToVerify() == true) {
        this.jv = null;
        this.mev = null;
      } else {
        this.jv.beginEntry(jarEntry, this.mev);
      }  
    return jarEntry;
  }
  
  public JarEntry getNextJarEntry() throws IOException { return (JarEntry)getNextEntry(); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    byte b;
    if (this.first == null) {
      b = super.read(paramArrayOfByte, paramInt1, paramInt2);
    } else {
      b = -1;
    } 
    if (this.jv != null)
      this.jv.update(b, paramArrayOfByte, paramInt1, paramInt2, this.mev); 
    return b;
  }
  
  protected ZipEntry createZipEntry(String paramString) {
    JarEntry jarEntry = new JarEntry(paramString);
    if (this.man != null)
      jarEntry.attr = this.man.getAttributes(paramString); 
    return jarEntry;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\jar\JarInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */