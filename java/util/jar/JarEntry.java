package java.util.jar;

import java.io.IOException;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.zip.ZipEntry;

public class JarEntry extends ZipEntry {
  Attributes attr;
  
  Certificate[] certs;
  
  CodeSigner[] signers;
  
  public JarEntry(String paramString) { super(paramString); }
  
  public JarEntry(ZipEntry paramZipEntry) { super(paramZipEntry); }
  
  public JarEntry(JarEntry paramJarEntry) {
    this(paramJarEntry);
    this.attr = paramJarEntry.attr;
    this.certs = paramJarEntry.certs;
    this.signers = paramJarEntry.signers;
  }
  
  public Attributes getAttributes() throws IOException { return this.attr; }
  
  public Certificate[] getCertificates() { return (this.certs == null) ? null : (Certificate[])this.certs.clone(); }
  
  public CodeSigner[] getCodeSigners() { return (this.signers == null) ? null : (CodeSigner[])this.signers.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\jar\JarEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */