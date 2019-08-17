package java.util.jar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import sun.security.util.Debug;
import sun.security.util.ManifestDigester;
import sun.security.util.ManifestEntryVerifier;
import sun.security.util.SignatureFileVerifier;

class JarVerifier {
  static final Debug debug = Debug.getInstance("jar");
  
  private Hashtable<String, CodeSigner[]> verifiedSigners;
  
  private Hashtable<String, CodeSigner[]> sigFileSigners;
  
  private Hashtable<String, byte[]> sigFileData;
  
  private ArrayList<SignatureFileVerifier> pendingBlocks;
  
  private ArrayList<CodeSigner[]> signerCache;
  
  private boolean parsingBlockOrSF = false;
  
  private boolean parsingMeta = true;
  
  private boolean anyToVerify = true;
  
  private ByteArrayOutputStream baos;
  
  byte[] manifestRawBytes = null;
  
  boolean eagerValidation;
  
  private Object csdomain = new Object();
  
  private List<Object> manifestDigests;
  
  private Map<URL, Map<CodeSigner[], CodeSource>> urlToCodeSourceMap = new HashMap();
  
  private Map<CodeSigner[], CodeSource> signerToCodeSource = new HashMap();
  
  private URL lastURL;
  
  private Map<CodeSigner[], CodeSource> lastURLMap;
  
  private CodeSigner[] emptySigner = new CodeSigner[0];
  
  private Map<String, CodeSigner[]> signerMap;
  
  private Enumeration<String> emptyEnumeration = new Enumeration<String>() {
      public boolean hasMoreElements() { return false; }
      
      public String nextElement() { throw new NoSuchElementException(); }
    };
  
  private List<CodeSigner[]> jarCodeSigners;
  
  public JarVerifier(byte[] paramArrayOfByte) {
    this.manifestRawBytes = paramArrayOfByte;
    this.sigFileSigners = new Hashtable();
    this.verifiedSigners = new Hashtable();
    this.sigFileData = new Hashtable(11);
    this.pendingBlocks = new ArrayList();
    this.baos = new ByteArrayOutputStream();
    this.manifestDigests = new ArrayList();
  }
  
  public void beginEntry(JarEntry paramJarEntry, ManifestEntryVerifier paramManifestEntryVerifier) throws IOException {
    if (paramJarEntry == null)
      return; 
    if (debug != null)
      debug.println("beginEntry " + paramJarEntry.getName()); 
    String str = paramJarEntry.getName();
    if (this.parsingMeta) {
      String str1 = str.toUpperCase(Locale.ENGLISH);
      if (str1.startsWith("META-INF/") || str1.startsWith("/META-INF/")) {
        if (paramJarEntry.isDirectory()) {
          paramManifestEntryVerifier.setEntry(null, paramJarEntry);
          return;
        } 
        if (str1.equals("META-INF/MANIFEST.MF") || str1.equals("META-INF/INDEX.LIST"))
          return; 
        if (SignatureFileVerifier.isBlockOrSF(str1)) {
          this.parsingBlockOrSF = true;
          this.baos.reset();
          paramManifestEntryVerifier.setEntry(null, paramJarEntry);
          return;
        } 
      } 
    } 
    if (this.parsingMeta)
      doneWithMeta(); 
    if (paramJarEntry.isDirectory()) {
      paramManifestEntryVerifier.setEntry(null, paramJarEntry);
      return;
    } 
    if (str.startsWith("./"))
      str = str.substring(2); 
    if (str.startsWith("/"))
      str = str.substring(1); 
    if (!str.equals("META-INF/MANIFEST.MF") && (this.sigFileSigners.get(str) != null || this.verifiedSigners.get(str) != null)) {
      paramManifestEntryVerifier.setEntry(str, paramJarEntry);
      return;
    } 
    paramManifestEntryVerifier.setEntry(null, paramJarEntry);
  }
  
  public void update(int paramInt, ManifestEntryVerifier paramManifestEntryVerifier) throws IOException {
    if (paramInt != -1) {
      if (this.parsingBlockOrSF) {
        this.baos.write(paramInt);
      } else {
        paramManifestEntryVerifier.update((byte)paramInt);
      } 
    } else {
      processEntry(paramManifestEntryVerifier);
    } 
  }
  
  public void update(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, ManifestEntryVerifier paramManifestEntryVerifier) throws IOException {
    if (paramInt1 != -1) {
      if (this.parsingBlockOrSF) {
        this.baos.write(paramArrayOfByte, paramInt2, paramInt1);
      } else {
        paramManifestEntryVerifier.update(paramArrayOfByte, paramInt2, paramInt1);
      } 
    } else {
      processEntry(paramManifestEntryVerifier);
    } 
  }
  
  private void processEntry(ManifestEntryVerifier paramManifestEntryVerifier) throws IOException {
    if (!this.parsingBlockOrSF) {
      JarEntry jarEntry = paramManifestEntryVerifier.getEntry();
      if (jarEntry != null && jarEntry.signers == null) {
        jarEntry.signers = paramManifestEntryVerifier.verify(this.verifiedSigners, this.sigFileSigners);
        jarEntry.certs = mapSignersToCertArray(jarEntry.signers);
      } 
    } else {
      try {
        this.parsingBlockOrSF = false;
        if (debug != null)
          debug.println("processEntry: processing block"); 
        String str1 = paramManifestEntryVerifier.getEntry().getName().toUpperCase(Locale.ENGLISH);
        if (str1.endsWith(".SF")) {
          String str = str1.substring(0, str1.length() - 3);
          byte[] arrayOfByte = this.baos.toByteArray();
          this.sigFileData.put(str, arrayOfByte);
          for (SignatureFileVerifier signatureFileVerifier1 : this.pendingBlocks) {
            if (signatureFileVerifier1.needSignatureFile(str)) {
              if (debug != null)
                debug.println("processEntry: processing pending block"); 
              signatureFileVerifier1.setSignatureFile(arrayOfByte);
              signatureFileVerifier1.process(this.sigFileSigners, this.manifestDigests);
            } 
          } 
          return;
        } 
        String str2 = str1.substring(0, str1.lastIndexOf("."));
        if (this.signerCache == null)
          this.signerCache = new ArrayList(); 
        if (this.manDig == null)
          synchronized (this.manifestRawBytes) {
            if (this.manDig == null) {
              this.manDig = new ManifestDigester(this.manifestRawBytes);
              this.manifestRawBytes = null;
            } 
          }  
        SignatureFileVerifier signatureFileVerifier = new SignatureFileVerifier(this.signerCache, this.manDig, str1, this.baos.toByteArray());
        if (signatureFileVerifier.needSignatureFileBytes()) {
          byte[] arrayOfByte = (byte[])this.sigFileData.get(str2);
          if (arrayOfByte == null) {
            if (debug != null)
              debug.println("adding pending block"); 
            this.pendingBlocks.add(signatureFileVerifier);
            return;
          } 
          signatureFileVerifier.setSignatureFile(arrayOfByte);
        } 
        signatureFileVerifier.process(this.sigFileSigners, this.manifestDigests);
      } catch (IOException iOException) {
        if (debug != null)
          debug.println("processEntry caught: " + iOException); 
      } catch (SignatureException signatureException) {
        if (debug != null)
          debug.println("processEntry caught: " + signatureException); 
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        if (debug != null)
          debug.println("processEntry caught: " + noSuchAlgorithmException); 
      } catch (CertificateException certificateException) {
        if (debug != null)
          debug.println("processEntry caught: " + certificateException); 
      } 
    } 
  }
  
  @Deprecated
  public Certificate[] getCerts(String paramString) { return mapSignersToCertArray(getCodeSigners(paramString)); }
  
  public Certificate[] getCerts(JarFile paramJarFile, JarEntry paramJarEntry) { return mapSignersToCertArray(getCodeSigners(paramJarFile, paramJarEntry)); }
  
  public CodeSigner[] getCodeSigners(String paramString) { return (CodeSigner[])this.verifiedSigners.get(paramString); }
  
  public CodeSigner[] getCodeSigners(JarFile paramJarFile, JarEntry paramJarEntry) {
    String str = paramJarEntry.getName();
    if (this.eagerValidation && this.sigFileSigners.get(str) != null)
      try {
        InputStream inputStream = paramJarFile.getInputStream(paramJarEntry);
        byte[] arrayOfByte = new byte[1024];
        int i;
        for (i = arrayOfByte.length; i != -1; i = inputStream.read(arrayOfByte, 0, arrayOfByte.length));
        inputStream.close();
      } catch (IOException iOException) {} 
    return getCodeSigners(str);
  }
  
  private static Certificate[] mapSignersToCertArray(CodeSigner[] paramArrayOfCodeSigner) {
    if (paramArrayOfCodeSigner != null) {
      ArrayList arrayList = new ArrayList();
      for (byte b = 0; b < paramArrayOfCodeSigner.length; b++)
        arrayList.addAll(paramArrayOfCodeSigner[b].getSignerCertPath().getCertificates()); 
      return (Certificate[])arrayList.toArray(new Certificate[arrayList.size()]);
    } 
    return null;
  }
  
  boolean nothingToVerify() { return !this.anyToVerify; }
  
  void doneWithMeta() {
    this.parsingMeta = false;
    this.anyToVerify = !this.sigFileSigners.isEmpty();
    this.baos = null;
    this.sigFileData = null;
    this.pendingBlocks = null;
    this.signerCache = null;
    this.manDig = null;
    if (this.sigFileSigners.containsKey("META-INF/MANIFEST.MF")) {
      CodeSigner[] arrayOfCodeSigner = (CodeSigner[])this.sigFileSigners.remove("META-INF/MANIFEST.MF");
      this.verifiedSigners.put("META-INF/MANIFEST.MF", arrayOfCodeSigner);
    } 
  }
  
  private CodeSource mapSignersToCodeSource(URL paramURL, CodeSigner[] paramArrayOfCodeSigner) {
    Map map;
    if (paramURL == this.lastURL) {
      map = this.lastURLMap;
    } else {
      map = (Map)this.urlToCodeSourceMap.get(paramURL);
      if (map == null) {
        map = new HashMap();
        this.urlToCodeSourceMap.put(paramURL, map);
      } 
      this.lastURLMap = map;
      this.lastURL = paramURL;
    } 
    CodeSource codeSource = (CodeSource)map.get(paramArrayOfCodeSigner);
    if (codeSource == null) {
      codeSource = new VerifierCodeSource(this.csdomain, paramURL, paramArrayOfCodeSigner);
      this.signerToCodeSource.put(paramArrayOfCodeSigner, codeSource);
    } 
    return codeSource;
  }
  
  private CodeSource[] mapSignersToCodeSources(URL paramURL, List<CodeSigner[]> paramList, boolean paramBoolean) {
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < paramList.size(); b++)
      arrayList.add(mapSignersToCodeSource(paramURL, (CodeSigner[])paramList.get(b))); 
    if (paramBoolean)
      arrayList.add(mapSignersToCodeSource(paramURL, null)); 
    return (CodeSource[])arrayList.toArray(new CodeSource[arrayList.size()]);
  }
  
  private CodeSigner[] findMatchingSigners(CodeSource paramCodeSource) {
    if (paramCodeSource instanceof VerifierCodeSource) {
      VerifierCodeSource verifierCodeSource = (VerifierCodeSource)paramCodeSource;
      if (verifierCodeSource.isSameDomain(this.csdomain))
        return ((VerifierCodeSource)paramCodeSource).getPrivateSigners(); 
    } 
    CodeSource[] arrayOfCodeSource = mapSignersToCodeSources(paramCodeSource.getLocation(), getJarCodeSigners(), true);
    ArrayList arrayList = new ArrayList();
    int i;
    for (i = 0; i < arrayOfCodeSource.length; i++)
      arrayList.add(arrayOfCodeSource[i]); 
    i = arrayList.indexOf(paramCodeSource);
    if (i != -1) {
      CodeSigner[] arrayOfCodeSigner = ((VerifierCodeSource)arrayList.get(i)).getPrivateSigners();
      if (arrayOfCodeSigner == null)
        arrayOfCodeSigner = this.emptySigner; 
      return arrayOfCodeSigner;
    } 
    return null;
  }
  
  private Map<String, CodeSigner[]> signerMap() {
    if (this.signerMap == null) {
      this.signerMap = new HashMap(this.verifiedSigners.size() + this.sigFileSigners.size());
      this.signerMap.putAll(this.verifiedSigners);
      this.signerMap.putAll(this.sigFileSigners);
    } 
    return this.signerMap;
  }
  
  public Enumeration<String> entryNames(JarFile paramJarFile, CodeSource[] paramArrayOfCodeSource) {
    Map map = signerMap();
    final Iterator itor = map.entrySet().iterator();
    boolean bool = false;
    ArrayList arrayList1 = new ArrayList(paramArrayOfCodeSource.length);
    for (byte b = 0; b < paramArrayOfCodeSource.length; b++) {
      CodeSigner[] arrayOfCodeSigner = findMatchingSigners(paramArrayOfCodeSource[b]);
      if (arrayOfCodeSigner != null) {
        if (arrayOfCodeSigner.length > 0) {
          arrayList1.add(arrayOfCodeSigner);
        } else {
          bool = true;
        } 
      } else {
        bool = true;
      } 
    } 
    final ArrayList signersReq = arrayList1;
    final Enumeration enum2 = bool ? unsignedEntryNames(paramJarFile) : this.emptyEnumeration;
    return new Enumeration<String>() {
        String name;
        
        public boolean hasMoreElements() {
          if (this.name != null)
            return true; 
          while (itor.hasNext()) {
            Map.Entry entry = (Map.Entry)itor.next();
            if (signersReq.contains(entry.getValue())) {
              this.name = (String)entry.getKey();
              return true;
            } 
          } 
          if (enum2.hasMoreElements()) {
            this.name = (String)enum2.nextElement();
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
  
  public Enumeration<JarEntry> entries2(final JarFile jar, Enumeration<? extends ZipEntry> paramEnumeration) {
    final HashMap map = new HashMap();
    hashMap.putAll(signerMap());
    final Enumeration<? extends ZipEntry> enum_ = paramEnumeration;
    return new Enumeration<JarEntry>() {
        Enumeration<String> signers = null;
        
        JarEntry entry;
        
        public boolean hasMoreElements() {
          if (this.entry != null)
            return true; 
          while (enum_.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry)enum_.nextElement();
            if (JarVerifier.isSigningRelated(zipEntry.getName()))
              continue; 
            this.entry = jar.newEntry(zipEntry);
            return true;
          } 
          if (this.signers == null)
            this.signers = Collections.enumeration(map.keySet()); 
          if (this.signers.hasMoreElements()) {
            String str = (String)this.signers.nextElement();
            this.entry = jar.newEntry(new ZipEntry(str));
            return true;
          } 
          return false;
        }
        
        public JarEntry nextElement() {
          if (hasMoreElements()) {
            JarEntry jarEntry = this.entry;
            map.remove(jarEntry.getName());
            this.entry = null;
            return jarEntry;
          } 
          throw new NoSuchElementException();
        }
      };
  }
  
  static boolean isSigningRelated(String paramString) { return SignatureFileVerifier.isSigningRelated(paramString); }
  
  private Enumeration<String> unsignedEntryNames(JarFile paramJarFile) {
    final Map map = signerMap();
    final Enumeration entries = paramJarFile.entries();
    return new Enumeration<String>() {
        String name;
        
        public boolean hasMoreElements() {
          if (this.name != null)
            return true; 
          while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry)entries.nextElement();
            String str = zipEntry.getName();
            if (!zipEntry.isDirectory() && !JarVerifier.isSigningRelated(str) && map.get(str) == null) {
              this.name = str;
              return true;
            } 
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
  
  private List<CodeSigner[]> getJarCodeSigners() {
    if (this.jarCodeSigners == null) {
      HashSet hashSet = new HashSet();
      hashSet.addAll(signerMap().values());
      this.jarCodeSigners = new ArrayList();
      this.jarCodeSigners.addAll(hashSet);
    } 
    return this.jarCodeSigners;
  }
  
  public CodeSource[] getCodeSources(JarFile paramJarFile, URL paramURL) {
    boolean bool = unsignedEntryNames(paramJarFile).hasMoreElements();
    return mapSignersToCodeSources(paramURL, getJarCodeSigners(), bool);
  }
  
  public CodeSource getCodeSource(URL paramURL, String paramString) {
    CodeSigner[] arrayOfCodeSigner = (CodeSigner[])signerMap().get(paramString);
    return mapSignersToCodeSource(paramURL, arrayOfCodeSigner);
  }
  
  public CodeSource getCodeSource(URL paramURL, JarFile paramJarFile, JarEntry paramJarEntry) { return mapSignersToCodeSource(paramURL, getCodeSigners(paramJarFile, paramJarEntry)); }
  
  public void setEagerValidation(boolean paramBoolean) { this.eagerValidation = paramBoolean; }
  
  public List<Object> getManifestDigests() { return Collections.unmodifiableList(this.manifestDigests); }
  
  static CodeSource getUnsignedCS(URL paramURL) { return new VerifierCodeSource(null, paramURL, (Certificate[])null); }
  
  boolean isTrustedManifestEntry(String paramString) {
    CodeSigner[] arrayOfCodeSigner1 = (CodeSigner[])this.verifiedSigners.get("META-INF/MANIFEST.MF");
    if (arrayOfCodeSigner1 == null)
      return true; 
    CodeSigner[] arrayOfCodeSigner2 = (CodeSigner[])this.sigFileSigners.get(paramString);
    if (arrayOfCodeSigner2 == null)
      arrayOfCodeSigner2 = (CodeSigner[])this.verifiedSigners.get(paramString); 
    return (arrayOfCodeSigner2 != null && arrayOfCodeSigner2.length == arrayOfCodeSigner1.length);
  }
  
  private static class VerifierCodeSource extends CodeSource {
    private static final long serialVersionUID = -9047366145967768825L;
    
    URL vlocation;
    
    CodeSigner[] vsigners;
    
    Certificate[] vcerts;
    
    Object csdomain;
    
    VerifierCodeSource(Object param1Object, URL param1URL, CodeSigner[] param1ArrayOfCodeSigner) {
      super(param1URL, param1ArrayOfCodeSigner);
      this.csdomain = param1Object;
      this.vlocation = param1URL;
      this.vsigners = param1ArrayOfCodeSigner;
    }
    
    VerifierCodeSource(Object param1Object, URL param1URL, Certificate[] param1ArrayOfCertificate) {
      super(param1URL, param1ArrayOfCertificate);
      this.csdomain = param1Object;
      this.vlocation = param1URL;
      this.vcerts = param1ArrayOfCertificate;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (param1Object instanceof VerifierCodeSource) {
        VerifierCodeSource verifierCodeSource = (VerifierCodeSource)param1Object;
        if (isSameDomain(verifierCodeSource.csdomain))
          return (verifierCodeSource.vsigners != this.vsigners || verifierCodeSource.vcerts != this.vcerts) ? false : ((verifierCodeSource.vlocation != null) ? verifierCodeSource.vlocation.equals(this.vlocation) : ((this.vlocation != null) ? this.vlocation.equals(verifierCodeSource.vlocation) : 1)); 
      } 
      return super.equals(param1Object);
    }
    
    boolean isSameDomain(Object param1Object) { return (this.csdomain == param1Object); }
    
    private CodeSigner[] getPrivateSigners() { return this.vsigners; }
    
    private Certificate[] getPrivateCertificates() { return this.vcerts; }
  }
  
  static class VerifierStream extends InputStream {
    private InputStream is;
    
    private JarVerifier jv;
    
    private ManifestEntryVerifier mev;
    
    private long numLeft;
    
    VerifierStream(Manifest param1Manifest, JarEntry param1JarEntry, InputStream param1InputStream, JarVerifier param1JarVerifier) throws IOException {
      this.is = param1InputStream;
      this.jv = param1JarVerifier;
      this.mev = new ManifestEntryVerifier(param1Manifest);
      this.jv.beginEntry(param1JarEntry, this.mev);
      this.numLeft = param1JarEntry.getSize();
      if (this.numLeft == 0L)
        this.jv.update(-1, this.mev); 
    }
    
    public int read() throws IOException {
      if (this.numLeft > 0L) {
        int i = this.is.read();
        this.jv.update(i, this.mev);
        this.numLeft--;
        if (this.numLeft == 0L)
          this.jv.update(-1, this.mev); 
        return i;
      } 
      return -1;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (this.numLeft > 0L && this.numLeft < param1Int2)
        param1Int2 = (int)this.numLeft; 
      if (this.numLeft > 0L) {
        int i = this.is.read(param1ArrayOfByte, param1Int1, param1Int2);
        this.jv.update(i, param1ArrayOfByte, param1Int1, param1Int2, this.mev);
        this.numLeft -= i;
        if (this.numLeft == 0L)
          this.jv.update(-1, param1ArrayOfByte, param1Int1, param1Int2, this.mev); 
        return i;
      } 
      return -1;
    }
    
    public void close() {
      if (this.is != null)
        this.is.close(); 
      this.is = null;
      this.mev = null;
      this.jv = null;
    }
    
    public int available() throws IOException { return this.is.available(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\jar\JarVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */