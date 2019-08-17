package java.util.jar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import sun.misc.IOUtils;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;
import sun.security.util.ManifestEntryVerifier;
import sun.security.util.SignatureFileVerifier;

public class JarFile extends ZipFile {
  private SoftReference<Manifest> manRef;
  
  private JarEntry manEntry;
  
  private JarVerifier jv;
  
  private boolean jvInitialized;
  
  private boolean verify;
  
  private boolean hasClassPathAttribute;
  
  public static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
  
  private static final char[] CLASSPATH_CHARS;
  
  private static final int[] CLASSPATH_LASTOCC;
  
  private static final int[] CLASSPATH_OPTOSFT;
  
  private static String javaHome;
  
  public JarFile(String paramString) throws IOException { this(new File(paramString), true, 1); }
  
  public JarFile(String paramString, boolean paramBoolean) throws IOException { this(new File(paramString), paramBoolean, 1); }
  
  public JarFile(File paramFile) throws IOException { this(paramFile, true, 1); }
  
  public JarFile(File paramFile, boolean paramBoolean) throws IOException { this(paramFile, paramBoolean, 1); }
  
  public JarFile(File paramFile, boolean paramBoolean, int paramInt) throws IOException {
    super(paramFile, paramInt);
    this.verify = paramBoolean;
  }
  
  public Manifest getManifest() throws IOException { return getManifestFromReference(); }
  
  private Manifest getManifestFromReference() throws IOException {
    Manifest manifest = (this.manRef != null) ? (Manifest)this.manRef.get() : null;
    if (manifest == null) {
      JarEntry jarEntry = getManEntry();
      if (jarEntry != null) {
        if (this.verify) {
          byte[] arrayOfByte = getBytes(jarEntry);
          if (!this.jvInitialized)
            this.jv = new JarVerifier(arrayOfByte); 
          manifest = new Manifest(this.jv, new ByteArrayInputStream(arrayOfByte));
        } else {
          manifest = new Manifest(super.getInputStream(jarEntry));
        } 
        this.manRef = new SoftReference(manifest);
      } 
    } 
    return manifest;
  }
  
  private native String[] getMetaInfEntryNames();
  
  public JarEntry getJarEntry(String paramString) { return (JarEntry)getEntry(paramString); }
  
  public ZipEntry getEntry(String paramString) {
    ZipEntry zipEntry = super.getEntry(paramString);
    return (zipEntry != null) ? new JarFileEntry(zipEntry) : null;
  }
  
  public Enumeration<JarEntry> entries() { return new JarEntryIterator(null); }
  
  public Stream<JarEntry> stream() { return StreamSupport.stream(Spliterators.spliterator(new JarEntryIterator(null), size(), 1297), false); }
  
  private void maybeInstantiateVerifier() throws IOException {
    if (this.jv != null)
      return; 
    if (this.verify) {
      String[] arrayOfString = getMetaInfEntryNames();
      if (arrayOfString != null)
        for (byte b = 0; b < arrayOfString.length; b++) {
          String str = arrayOfString[b].toUpperCase(Locale.ENGLISH);
          if (str.endsWith(".DSA") || str.endsWith(".RSA") || str.endsWith(".EC") || str.endsWith(".SF")) {
            getManifest();
            return;
          } 
        }  
      this.verify = false;
    } 
  }
  
  private void initializeVerifier() throws IOException {
    ManifestEntryVerifier manifestEntryVerifier = null;
    try {
      String[] arrayOfString = getMetaInfEntryNames();
      if (arrayOfString != null)
        for (byte b = 0; b < arrayOfString.length; b++) {
          String str = arrayOfString[b].toUpperCase(Locale.ENGLISH);
          if ("META-INF/MANIFEST.MF".equals(str) || SignatureFileVerifier.isBlockOrSF(str)) {
            JarEntry jarEntry = getJarEntry(arrayOfString[b]);
            if (jarEntry == null)
              throw new JarException("corrupted jar file"); 
            if (manifestEntryVerifier == null)
              manifestEntryVerifier = new ManifestEntryVerifier(getManifestFromReference()); 
            byte[] arrayOfByte = getBytes(jarEntry);
            if (arrayOfByte != null && arrayOfByte.length > 0) {
              this.jv.beginEntry(jarEntry, manifestEntryVerifier);
              this.jv.update(arrayOfByte.length, arrayOfByte, 0, arrayOfByte.length, manifestEntryVerifier);
              this.jv.update(-1, null, 0, 0, manifestEntryVerifier);
            } 
          } 
        }  
    } catch (IOException iOException) {
      this.jv = null;
      this.verify = false;
      if (JarVerifier.debug != null) {
        JarVerifier.debug.println("jarfile parsing error!");
        iOException.printStackTrace();
      } 
    } 
    if (this.jv != null) {
      this.jv.doneWithMeta();
      if (JarVerifier.debug != null)
        JarVerifier.debug.println("done with meta!"); 
      if (this.jv.nothingToVerify()) {
        if (JarVerifier.debug != null)
          JarVerifier.debug.println("nothing to verify!"); 
        this.jv = null;
        this.verify = false;
      } 
    } 
  }
  
  private byte[] getBytes(ZipEntry paramZipEntry) throws IOException {
    try (InputStream null = super.getInputStream(paramZipEntry)) {
      return IOUtils.readFully(inputStream, (int)paramZipEntry.getSize(), true);
    } 
  }
  
  public InputStream getInputStream(ZipEntry paramZipEntry) throws IOException {
    maybeInstantiateVerifier();
    if (this.jv == null)
      return super.getInputStream(paramZipEntry); 
    if (!this.jvInitialized) {
      initializeVerifier();
      this.jvInitialized = true;
      if (this.jv == null)
        return super.getInputStream(paramZipEntry); 
    } 
    return new JarVerifier.VerifierStream(getManifestFromReference(), (paramZipEntry instanceof JarFileEntry) ? (JarEntry)paramZipEntry : getJarEntry(paramZipEntry.getName()), super.getInputStream(paramZipEntry), this.jv);
  }
  
  private JarEntry getManEntry() {
    if (this.manEntry == null) {
      this.manEntry = getJarEntry("META-INF/MANIFEST.MF");
      if (this.manEntry == null) {
        String[] arrayOfString = getMetaInfEntryNames();
        if (arrayOfString != null)
          for (byte b = 0; b < arrayOfString.length; b++) {
            if ("META-INF/MANIFEST.MF".equals(arrayOfString[b].toUpperCase(Locale.ENGLISH))) {
              this.manEntry = getJarEntry(arrayOfString[b]);
              break;
            } 
          }  
      } 
    } 
    return this.manEntry;
  }
  
  boolean hasClassPathAttribute() throws IOException {
    checkForSpecialAttributes();
    return this.hasClassPathAttribute;
  }
  
  private boolean match(char[] paramArrayOfChar, byte[] paramArrayOfByte, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    int i = paramArrayOfChar.length;
    int j = paramArrayOfByte.length - i;
    int k = 0;
    label19: while (k <= j) {
      for (int m = i - 1; m >= 0; m--) {
        char c = (char)paramArrayOfByte[k + m];
        c = ((c - 'A' | 'Z' - c) >= '\000') ? (char)(c + ' ') : c;
        if (c != paramArrayOfChar[m]) {
          k += Math.max(m + 1 - paramArrayOfInt1[c & 0x7F], paramArrayOfInt2[m]);
          continue label19;
        } 
      } 
      return true;
    } 
    return false;
  }
  
  private void checkForSpecialAttributes() throws IOException {
    if (this.hasCheckedSpecialAttributes)
      return; 
    if (!isKnownNotToHaveSpecialAttributes()) {
      JarEntry jarEntry = getManEntry();
      if (jarEntry != null) {
        byte[] arrayOfByte = getBytes(jarEntry);
        if (match(CLASSPATH_CHARS, arrayOfByte, CLASSPATH_LASTOCC, CLASSPATH_OPTOSFT))
          this.hasClassPathAttribute = true; 
      } 
    } 
    this.hasCheckedSpecialAttributes = true;
  }
  
  private boolean isKnownNotToHaveSpecialAttributes() throws IOException {
    if (javaHome == null)
      javaHome = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")); 
    if (jarNames == null) {
      String[] arrayOfString = new String[11];
      String str = File.separator;
      byte b = 0;
      arrayOfString[b++] = str + "rt.jar";
      arrayOfString[b++] = str + "jsse.jar";
      arrayOfString[b++] = str + "jce.jar";
      arrayOfString[b++] = str + "charsets.jar";
      arrayOfString[b++] = str + "dnsns.jar";
      arrayOfString[b++] = str + "zipfs.jar";
      arrayOfString[b++] = str + "localedata.jar";
      arrayOfString[b++] = str = "cldrdata.jar";
      arrayOfString[b++] = str + "sunjce_provider.jar";
      arrayOfString[b++] = str + "sunpkcs11.jar";
      arrayOfString[b++] = str + "sunec.jar";
      jarNames = arrayOfString;
    } 
    String str1 = getName();
    String str2 = javaHome;
    if (str1.startsWith(str2)) {
      String[] arrayOfString = jarNames;
      for (byte b = 0; b < arrayOfString.length; b++) {
        if (str1.endsWith(arrayOfString[b]))
          return true; 
      } 
    } 
    return false;
  }
  
  void ensureInitialization() throws IOException {
    try {
      maybeInstantiateVerifier();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    if (this.jv != null && !this.jvInitialized) {
      initializeVerifier();
      this.jvInitialized = true;
    } 
  }
  
  JarEntry newEntry(ZipEntry paramZipEntry) { return new JarFileEntry(paramZipEntry); }
  
  Enumeration<String> entryNames(CodeSource[] paramArrayOfCodeSource) {
    ensureInitialization();
    if (this.jv != null)
      return this.jv.entryNames(this, paramArrayOfCodeSource); 
    boolean bool = false;
    for (byte b = 0; b < paramArrayOfCodeSource.length; b++) {
      if (paramArrayOfCodeSource[b].getCodeSigners() == null) {
        bool = true;
        break;
      } 
    } 
    return bool ? unsignedEntryNames() : new Enumeration<String>() {
        public boolean hasMoreElements() throws IOException { return false; }
        
        public String nextElement() { throw new NoSuchElementException(); }
      };
  }
  
  Enumeration<JarEntry> entries2() {
    ensureInitialization();
    if (this.jv != null)
      return this.jv.entries2(this, super.entries()); 
    final Enumeration enum_ = super.entries();
    return new Enumeration<JarEntry>() {
        ZipEntry entry;
        
        public boolean hasMoreElements() throws IOException {
          if (this.entry != null)
            return true; 
          while (enum_.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry)enum_.nextElement();
            if (JarVerifier.isSigningRelated(zipEntry.getName()))
              continue; 
            this.entry = zipEntry;
            return true;
          } 
          return false;
        }
        
        public JarFile.JarFileEntry nextElement() {
          if (hasMoreElements()) {
            ZipEntry zipEntry = this.entry;
            this.entry = null;
            return new JarFile.JarFileEntry(JarFile.this, zipEntry);
          } 
          throw new NoSuchElementException();
        }
      };
  }
  
  CodeSource[] getCodeSources(URL paramURL) {
    ensureInitialization();
    if (this.jv != null)
      return this.jv.getCodeSources(this, paramURL); 
    Enumeration enumeration = unsignedEntryNames();
    return enumeration.hasMoreElements() ? new CodeSource[] { JarVerifier.getUnsignedCS(paramURL) } : null;
  }
  
  private Enumeration<String> unsignedEntryNames() {
    final Enumeration entries = entries();
    return new Enumeration<String>() {
        String name;
        
        public boolean hasMoreElements() throws IOException {
          if (this.name != null)
            return true; 
          while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry)entries.nextElement();
            String str = zipEntry.getName();
            if (zipEntry.isDirectory() || JarVerifier.isSigningRelated(str))
              continue; 
            this.name = str;
            return true;
          } 
          return false;
        }
        
        public String nextElement() {
          if (hasMoreElements()) {
            String str = this.name;
            this.name = null;
            return str;
          } 
          throw new NoSuchElementException();
        }
      };
  }
  
  CodeSource getCodeSource(URL paramURL, String paramString) {
    ensureInitialization();
    if (this.jv != null) {
      if (this.jv.eagerValidation) {
        CodeSource codeSource = null;
        JarEntry jarEntry = getJarEntry(paramString);
        if (jarEntry != null) {
          codeSource = this.jv.getCodeSource(paramURL, this, jarEntry);
        } else {
          codeSource = this.jv.getCodeSource(paramURL, paramString);
        } 
        return codeSource;
      } 
      return this.jv.getCodeSource(paramURL, paramString);
    } 
    return JarVerifier.getUnsignedCS(paramURL);
  }
  
  void setEagerValidation(boolean paramBoolean) {
    try {
      maybeInstantiateVerifier();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    if (this.jv != null)
      this.jv.setEagerValidation(paramBoolean); 
  }
  
  List<Object> getManifestDigests() {
    ensureInitialization();
    return (this.jv != null) ? this.jv.getManifestDigests() : new ArrayList();
  }
  
  static  {
    SharedSecrets.setJavaUtilJarAccess(new JavaUtilJarAccessImpl());
    CLASSPATH_CHARS = new char[] { 'c', 'l', 'a', 's', 's', '-', 'p', 'a', 't', 'h' };
    CLASSPATH_LASTOCC = new int[128];
    CLASSPATH_OPTOSFT = new int[10];
    CLASSPATH_LASTOCC[99] = 1;
    CLASSPATH_LASTOCC[108] = 2;
    CLASSPATH_LASTOCC[115] = 5;
    CLASSPATH_LASTOCC[45] = 6;
    CLASSPATH_LASTOCC[112] = 7;
    CLASSPATH_LASTOCC[97] = 8;
    CLASSPATH_LASTOCC[116] = 9;
    CLASSPATH_LASTOCC[104] = 10;
    for (byte b = 0; b < 9; b++)
      CLASSPATH_OPTOSFT[b] = 10; 
    CLASSPATH_OPTOSFT[9] = 1;
  }
  
  private class JarEntryIterator extends Object implements Enumeration<JarEntry>, Iterator<JarEntry> {
    final Enumeration<? extends ZipEntry> e = JarFile.this.entries();
    
    private JarEntryIterator() throws IOException {}
    
    public boolean hasNext() throws IOException { return this.e.hasMoreElements(); }
    
    public JarEntry next() {
      ZipEntry zipEntry = (ZipEntry)this.e.nextElement();
      return new JarFile.JarFileEntry(JarFile.this, zipEntry);
    }
    
    public boolean hasMoreElements() throws IOException { return hasNext(); }
    
    public JarEntry nextElement() { return next(); }
  }
  
  private class JarFileEntry extends JarEntry {
    JarFileEntry(ZipEntry param1ZipEntry) { super(param1ZipEntry); }
    
    public Attributes getAttributes() throws IOException {
      Manifest manifest = JarFile.this.getManifest();
      return (manifest != null) ? manifest.getAttributes(getName()) : null;
    }
    
    public Certificate[] getCertificates() {
      try {
        JarFile.this.maybeInstantiateVerifier();
      } catch (IOException iOException) {
        throw new RuntimeException(iOException);
      } 
      if (this.certs == null && JarFile.this.jv != null)
        this.certs = JarFile.this.jv.getCerts(JarFile.this, this); 
      return (this.certs == null) ? null : (Certificate[])this.certs.clone();
    }
    
    public CodeSigner[] getCodeSigners() {
      try {
        JarFile.this.maybeInstantiateVerifier();
      } catch (IOException iOException) {
        throw new RuntimeException(iOException);
      } 
      if (this.signers == null && JarFile.this.jv != null)
        this.signers = JarFile.this.jv.getCodeSigners(JarFile.this, this); 
      return (this.signers == null) ? null : (CodeSigner[])this.signers.clone();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\jar\JarFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */