package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.CodeSigner;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarException;
import java.util.jar.Manifest;
import sun.security.jca.Providers;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;

public class SignatureFileVerifier {
  private static final Debug debug = Debug.getInstance("jar");
  
  private static final DisabledAlgorithmConstraints JAR_DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
  
  private ArrayList<CodeSigner[]> signerCache;
  
  private static final String ATTR_DIGEST = "-DIGEST-Manifest-Main-Attributes".toUpperCase(Locale.ENGLISH);
  
  private PKCS7 block;
  
  private byte[] sfBytes;
  
  private String name;
  
  private ManifestDigester md;
  
  private HashMap<String, MessageDigest> createdDigests;
  
  private boolean workaround = false;
  
  private CertificateFactory certificateFactory = null;
  
  private Map<String, Boolean> permittedAlgs = new HashMap();
  
  private Timestamp timestamp = null;
  
  private static final char[] hexc = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
  
  public SignatureFileVerifier(ArrayList<CodeSigner[]> paramArrayList, ManifestDigester paramManifestDigester, String paramString, byte[] paramArrayOfByte) throws IOException, CertificateException {
    object = null;
    try {
      object = Providers.startJarVerification();
      this.block = new PKCS7(paramArrayOfByte);
      this.sfBytes = this.block.getContentInfo().getData();
      this.certificateFactory = CertificateFactory.getInstance("X509");
    } finally {
      Providers.stopJarVerification(object);
    } 
    this.name = paramString.substring(0, paramString.lastIndexOf('.')).toUpperCase(Locale.ENGLISH);
    this.md = paramManifestDigester;
    this.signerCache = paramArrayList;
  }
  
  public boolean needSignatureFileBytes() { return (this.sfBytes == null); }
  
  public boolean needSignatureFile(String paramString) { return this.name.equalsIgnoreCase(paramString); }
  
  public void setSignatureFile(byte[] paramArrayOfByte) { this.sfBytes = paramArrayOfByte; }
  
  public static boolean isBlockOrSF(String paramString) { return (paramString.endsWith(".SF") || paramString.endsWith(".DSA") || paramString.endsWith(".RSA") || paramString.endsWith(".EC")); }
  
  public static boolean isSigningRelated(String paramString) {
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    if (!paramString.startsWith("META-INF/"))
      return false; 
    paramString = paramString.substring(9);
    if (paramString.indexOf('/') != -1)
      return false; 
    if (isBlockOrSF(paramString) || paramString.equals("MANIFEST.MF"))
      return true; 
    if (paramString.startsWith("SIG-")) {
      int i = paramString.lastIndexOf('.');
      if (i != -1) {
        String str = paramString.substring(i + 1);
        if (str.length() > 3 || str.length() < 1)
          return false; 
        for (byte b = 0; b < str.length(); b++) {
          char c = str.charAt(b);
          if ((c < 'A' || c > 'Z') && (c < '0' || c > '9'))
            return false; 
        } 
      } 
      return true;
    } 
    return false;
  }
  
  private MessageDigest getDigest(String paramString) throws SignatureException {
    if (this.createdDigests == null)
      this.createdDigests = new HashMap(); 
    MessageDigest messageDigest = (MessageDigest)this.createdDigests.get(paramString);
    if (messageDigest == null)
      try {
        messageDigest = MessageDigest.getInstance(paramString);
        this.createdDigests.put(paramString, messageDigest);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
    return messageDigest;
  }
  
  public void process(Hashtable<String, CodeSigner[]> paramHashtable, List<Object> paramList) throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException {
    object = null;
    try {
      object = Providers.startJarVerification();
      processImpl(paramHashtable, paramList);
    } finally {
      Providers.stopJarVerification(object);
    } 
  }
  
  private void processImpl(Hashtable<String, CodeSigner[]> paramHashtable, List<Object> paramList) throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException {
    Manifest manifest = new Manifest();
    manifest.read(new ByteArrayInputStream(this.sfBytes));
    String str = manifest.getMainAttributes().getValue(Attributes.Name.SIGNATURE_VERSION);
    if (str == null || !str.equalsIgnoreCase("1.0"))
      return; 
    SignerInfo[] arrayOfSignerInfo = this.block.verify(this.sfBytes);
    if (arrayOfSignerInfo == null)
      throw new SecurityException("cannot verify signature block file " + this.name); 
    CodeSigner[] arrayOfCodeSigner = getSigners(arrayOfSignerInfo, this.block);
    if (arrayOfCodeSigner == null)
      return; 
    for (CodeSigner codeSigner : arrayOfCodeSigner) {
      if (debug != null)
        debug.println("Gathering timestamp for:  " + codeSigner.toString()); 
      if (codeSigner.getTimestamp() == null) {
        this.timestamp = null;
        break;
      } 
      if (this.timestamp == null) {
        this.timestamp = codeSigner.getTimestamp();
      } else if (this.timestamp.getTimestamp().before(codeSigner.getTimestamp().getTimestamp())) {
        this.timestamp = codeSigner.getTimestamp();
      } 
    } 
    Iterator iterator = manifest.getEntries().entrySet().iterator();
    boolean bool = verifyManifestHash(manifest, this.md, paramList);
    if (!bool && !verifyManifestMainAttrs(manifest, this.md))
      throw new SecurityException("Invalid signature file digest for Manifest main attributes"); 
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry)iterator.next();
      String str1 = (String)entry.getKey();
      if (bool || verifySection((Attributes)entry.getValue(), str1, this.md)) {
        if (str1.startsWith("./"))
          str1 = str1.substring(2); 
        if (str1.startsWith("/"))
          str1 = str1.substring(1); 
        updateSigners(arrayOfCodeSigner, paramHashtable, str1);
        if (debug != null)
          debug.println("processSignature signed name = " + str1); 
        continue;
      } 
      if (debug != null)
        debug.println("processSignature unsigned name = " + str1); 
    } 
    updateSigners(arrayOfCodeSigner, paramHashtable, "META-INF/MANIFEST.MF");
  }
  
  boolean permittedCheck(String paramString1, String paramString2) {
    Boolean bool = (Boolean)this.permittedAlgs.get(paramString2);
    if (bool == null) {
      try {
        JAR_DISABLED_CHECK.permits(paramString2, new ConstraintsParameters(this.timestamp));
      } catch (GeneralSecurityException generalSecurityException) {
        this.permittedAlgs.put(paramString2, Boolean.FALSE);
        this.permittedAlgs.put(paramString1.toUpperCase(), Boolean.FALSE);
        if (debug != null)
          if (generalSecurityException.getMessage() != null) {
            debug.println(paramString1 + ":  " + generalSecurityException.getMessage());
          } else {
            debug.println(paramString1 + ":  " + paramString2 + " was disabled, no exception msg given.");
            generalSecurityException.printStackTrace();
          }  
        return false;
      } 
      this.permittedAlgs.put(paramString2, Boolean.TRUE);
      return true;
    } 
    return bool.booleanValue();
  }
  
  String getWeakAlgorithms(String paramString) {
    String str = "";
    try {
      for (String str1 : this.permittedAlgs.keySet()) {
        if (str1.endsWith(paramString))
          str = str + str1.substring(0, str1.length() - paramString.length()) + " "; 
      } 
    } catch (RuntimeException runtimeException) {
      str = "Unknown Algorithm(s).  Error processing " + paramString + ".  " + runtimeException.getMessage();
    } 
    return (str.length() == 0) ? "Unknown Algorithm(s)" : str;
  }
  
  private boolean verifyManifestHash(Manifest paramManifest, ManifestDigester paramManifestDigester, List<Object> paramList) throws IOException, SignatureException {
    Attributes attributes = paramManifest.getMainAttributes();
    boolean bool = false;
    boolean bool1 = true;
    boolean bool2 = false;
    for (Map.Entry entry : attributes.entrySet()) {
      String str = entry.getKey().toString();
      if (str.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST-MANIFEST")) {
        String str1 = str.substring(0, str.length() - 16);
        bool2 = true;
        if (!permittedCheck(str, str1))
          continue; 
        bool1 = false;
        paramList.add(str);
        paramList.add(entry.getValue());
        MessageDigest messageDigest = getDigest(str1);
        if (messageDigest != null) {
          byte[] arrayOfByte1 = paramManifestDigester.manifestDigest(messageDigest);
          byte[] arrayOfByte2 = Base64.getMimeDecoder().decode((String)entry.getValue());
          if (debug != null) {
            debug.println("Signature File: Manifest digest " + str1);
            debug.println("  sigfile  " + toHex(arrayOfByte2));
            debug.println("  computed " + toHex(arrayOfByte1));
            debug.println();
          } 
          if (MessageDigest.isEqual(arrayOfByte1, arrayOfByte2))
            bool = true; 
        } 
      } 
    } 
    if (debug != null) {
      debug.println("PermittedAlgs mapping: ");
      for (String str : this.permittedAlgs.keySet())
        debug.println(str + " : " + ((Boolean)this.permittedAlgs.get(str)).toString()); 
    } 
    if (bool2 && bool1)
      throw new SignatureException("Manifest hash check failed (DIGEST-MANIFEST). Disabled algorithm(s) used: " + getWeakAlgorithms("-DIGEST-MANIFEST")); 
    return bool;
  }
  
  private boolean verifyManifestMainAttrs(Manifest paramManifest, ManifestDigester paramManifestDigester) throws IOException, SignatureException {
    Attributes attributes = paramManifest.getMainAttributes();
    boolean bool = true;
    boolean bool1 = true;
    boolean bool2 = false;
    for (Map.Entry entry : attributes.entrySet()) {
      String str = entry.getKey().toString();
      if (str.toUpperCase(Locale.ENGLISH).endsWith(ATTR_DIGEST)) {
        String str1 = str.substring(0, str.length() - ATTR_DIGEST.length());
        bool2 = true;
        if (!permittedCheck(str, str1))
          continue; 
        bool1 = false;
        MessageDigest messageDigest = getDigest(str1);
        if (messageDigest != null) {
          ManifestDigester.Entry entry1 = paramManifestDigester.get("Manifest-Main-Attributes", false);
          byte[] arrayOfByte1 = entry1.digest(messageDigest);
          byte[] arrayOfByte2 = Base64.getMimeDecoder().decode((String)entry.getValue());
          if (debug != null) {
            debug.println("Signature File: Manifest Main Attributes digest " + messageDigest.getAlgorithm());
            debug.println("  sigfile  " + toHex(arrayOfByte2));
            debug.println("  computed " + toHex(arrayOfByte1));
            debug.println();
          } 
          if (MessageDigest.isEqual(arrayOfByte1, arrayOfByte2))
            continue; 
          bool = false;
          if (debug != null) {
            debug.println("Verification of Manifest main attributes failed");
            debug.println();
          } 
          break;
        } 
      } 
    } 
    if (debug != null) {
      debug.println("PermittedAlgs mapping: ");
      for (String str : this.permittedAlgs.keySet())
        debug.println(str + " : " + ((Boolean)this.permittedAlgs.get(str)).toString()); 
    } 
    if (bool2 && bool1)
      throw new SignatureException("Manifest Main Attribute check failed (" + ATTR_DIGEST + ").  Disabled algorithm(s) used: " + getWeakAlgorithms(ATTR_DIGEST)); 
    return bool;
  }
  
  private boolean verifySection(Attributes paramAttributes, String paramString, ManifestDigester paramManifestDigester) throws IOException, SignatureException {
    boolean bool = false;
    ManifestDigester.Entry entry = paramManifestDigester.get(paramString, this.block.isOldStyle());
    boolean bool1 = true;
    boolean bool2 = false;
    if (entry == null)
      throw new SecurityException("no manifest section for signature file entry " + paramString); 
    if (paramAttributes != null)
      for (Map.Entry entry1 : paramAttributes.entrySet()) {
        String str = entry1.getKey().toString();
        if (str.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST")) {
          String str1 = str.substring(0, str.length() - 7);
          bool2 = true;
          if (!permittedCheck(str, str1))
            continue; 
          bool1 = false;
          MessageDigest messageDigest = getDigest(str1);
          if (messageDigest != null) {
            byte[] arrayOfByte2;
            boolean bool3 = false;
            byte[] arrayOfByte1 = Base64.getMimeDecoder().decode((String)entry1.getValue());
            if (this.workaround) {
              arrayOfByte2 = entry.digestWorkaround(messageDigest);
            } else {
              arrayOfByte2 = entry.digest(messageDigest);
            } 
            if (debug != null) {
              debug.println("Signature Block File: " + paramString + " digest=" + messageDigest.getAlgorithm());
              debug.println("  expected " + toHex(arrayOfByte1));
              debug.println("  computed " + toHex(arrayOfByte2));
              debug.println();
            } 
            if (MessageDigest.isEqual(arrayOfByte2, arrayOfByte1)) {
              bool = true;
              bool3 = true;
            } else if (!this.workaround) {
              arrayOfByte2 = entry.digestWorkaround(messageDigest);
              if (MessageDigest.isEqual(arrayOfByte2, arrayOfByte1)) {
                if (debug != null) {
                  debug.println("  re-computed " + toHex(arrayOfByte2));
                  debug.println();
                } 
                this.workaround = true;
                bool = true;
                bool3 = true;
              } 
            } 
            if (!bool3)
              throw new SecurityException("invalid " + messageDigest.getAlgorithm() + " signature file digest for " + paramString); 
          } 
        } 
      }  
    if (debug != null) {
      debug.println("PermittedAlgs mapping: ");
      for (String str : this.permittedAlgs.keySet())
        debug.println(str + " : " + ((Boolean)this.permittedAlgs.get(str)).toString()); 
    } 
    if (bool2 && bool1)
      throw new SignatureException("Manifest Main Attribute check failed (DIGEST).  Disabled algorithm(s) used: " + getWeakAlgorithms("DIGEST")); 
    return bool;
  }
  
  private CodeSigner[] getSigners(SignerInfo[] paramArrayOfSignerInfo, PKCS7 paramPKCS7) throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException {
    ArrayList arrayList = null;
    for (byte b = 0; b < paramArrayOfSignerInfo.length; b++) {
      SignerInfo signerInfo = paramArrayOfSignerInfo[b];
      ArrayList arrayList1 = signerInfo.getCertificateChain(paramPKCS7);
      CertPath certPath = this.certificateFactory.generateCertPath(arrayList1);
      if (arrayList == null)
        arrayList = new ArrayList(); 
      arrayList.add(new CodeSigner(certPath, signerInfo.getTimestamp()));
      if (debug != null)
        debug.println("Signature Block Certificate: " + arrayList1.get(0)); 
    } 
    return (arrayList != null) ? (CodeSigner[])arrayList.toArray(new CodeSigner[arrayList.size()]) : null;
  }
  
  static String toHex(byte[] paramArrayOfByte) {
    StringBuilder stringBuilder = new StringBuilder(paramArrayOfByte.length * 2);
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      stringBuilder.append(hexc[paramArrayOfByte[b] >> 4 & 0xF]);
      stringBuilder.append(hexc[paramArrayOfByte[b] & 0xF]);
    } 
    return stringBuilder.toString();
  }
  
  static boolean contains(CodeSigner[] paramArrayOfCodeSigner, CodeSigner paramCodeSigner) {
    for (byte b = 0; b < paramArrayOfCodeSigner.length; b++) {
      if (paramArrayOfCodeSigner[b].equals(paramCodeSigner))
        return true; 
    } 
    return false;
  }
  
  static boolean isSubSet(CodeSigner[] paramArrayOfCodeSigner1, CodeSigner[] paramArrayOfCodeSigner2) {
    if (paramArrayOfCodeSigner2 == paramArrayOfCodeSigner1)
      return true; 
    for (byte b = 0; b < paramArrayOfCodeSigner1.length; b++) {
      if (!contains(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1[b]))
        return false; 
    } 
    return true;
  }
  
  static boolean matches(CodeSigner[] paramArrayOfCodeSigner1, CodeSigner[] paramArrayOfCodeSigner2, CodeSigner[] paramArrayOfCodeSigner3) {
    if (paramArrayOfCodeSigner2 == null && paramArrayOfCodeSigner1 == paramArrayOfCodeSigner3)
      return true; 
    if (paramArrayOfCodeSigner2 != null && !isSubSet(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1))
      return false; 
    if (!isSubSet(paramArrayOfCodeSigner3, paramArrayOfCodeSigner1))
      return false; 
    for (byte b = 0; b < paramArrayOfCodeSigner1.length; b++) {
      boolean bool = ((paramArrayOfCodeSigner2 != null && contains(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1[b])) || contains(paramArrayOfCodeSigner3, paramArrayOfCodeSigner1[b])) ? 1 : 0;
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  void updateSigners(CodeSigner[] paramArrayOfCodeSigner, Hashtable<String, CodeSigner[]> paramHashtable, String paramString) {
    CodeSigner[] arrayOfCodeSigner2;
    CodeSigner[] arrayOfCodeSigner1 = (CodeSigner[])paramHashtable.get(paramString);
    for (int i = this.signerCache.size() - 1; i != -1; i--) {
      arrayOfCodeSigner2 = (CodeSigner[])this.signerCache.get(i);
      if (matches(arrayOfCodeSigner2, arrayOfCodeSigner1, paramArrayOfCodeSigner)) {
        paramHashtable.put(paramString, arrayOfCodeSigner2);
        return;
      } 
    } 
    if (arrayOfCodeSigner1 == null) {
      arrayOfCodeSigner2 = paramArrayOfCodeSigner;
    } else {
      arrayOfCodeSigner2 = new CodeSigner[arrayOfCodeSigner1.length + paramArrayOfCodeSigner.length];
      System.arraycopy(arrayOfCodeSigner1, 0, arrayOfCodeSigner2, 0, arrayOfCodeSigner1.length);
      System.arraycopy(paramArrayOfCodeSigner, 0, arrayOfCodeSigner2, arrayOfCodeSigner1.length, paramArrayOfCodeSigner.length);
    } 
    this.signerCache.add(arrayOfCodeSigner2);
    paramHashtable.put(paramString, arrayOfCodeSigner2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\SignatureFileVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */