package sun.security.pkcs12;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PKCS12Attribute;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.x500.X500Principal;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public final class PKCS12KeyStore extends KeyStoreSpi {
  public static final int VERSION_3 = 3;
  
  private static final String[] KEY_PROTECTION_ALGORITHM = { "keystore.pkcs12.keyProtectionAlgorithm", "keystore.PKCS12.keyProtectionAlgorithm" };
  
  private static final int MAX_ITERATION_COUNT = 5000000;
  
  private static final int PBE_ITERATION_COUNT = 50000;
  
  private static final int MAC_ITERATION_COUNT = 100000;
  
  private static final int SALT_LEN = 20;
  
  private static final String[] CORE_ATTRIBUTES = { "1.2.840.113549.1.9.20", "1.2.840.113549.1.9.21", "2.16.840.1.113894.746875.1.1" };
  
  private static final Debug debug = Debug.getInstance("pkcs12");
  
  private static final int[] keyBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 2 };
  
  private static final int[] certBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 3 };
  
  private static final int[] secretBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 5 };
  
  private static final int[] pkcs9Name = { 1, 2, 840, 113549, 1, 9, 20 };
  
  private static final int[] pkcs9KeyId = { 1, 2, 840, 113549, 1, 9, 21 };
  
  private static final int[] pkcs9certType = { 1, 2, 840, 113549, 1, 9, 22, 1 };
  
  private static final int[] pbeWithSHAAnd40BitRC2CBC = { 1, 2, 840, 113549, 1, 12, 1, 6 };
  
  private static final int[] pbeWithSHAAnd3KeyTripleDESCBC = { 1, 2, 840, 113549, 1, 12, 1, 3 };
  
  private static final int[] pbes2 = { 1, 2, 840, 113549, 1, 5, 13 };
  
  private static final int[] TrustedKeyUsage = { 2, 16, 840, 1, 113894, 746875, 1, 1 };
  
  private static final int[] AnyExtendedKeyUsage = { 2, 5, 29, 37, 0 };
  
  private static ObjectIdentifier PKCS8ShroudedKeyBag_OID;
  
  private static ObjectIdentifier CertBag_OID;
  
  private static ObjectIdentifier SecretBag_OID;
  
  private static ObjectIdentifier PKCS9FriendlyName_OID;
  
  private static ObjectIdentifier PKCS9LocalKeyId_OID;
  
  private static ObjectIdentifier PKCS9CertType_OID;
  
  private static ObjectIdentifier pbeWithSHAAnd40BitRC2CBC_OID;
  
  private static ObjectIdentifier pbeWithSHAAnd3KeyTripleDESCBC_OID;
  
  private static ObjectIdentifier pbes2_OID;
  
  private static ObjectIdentifier TrustedKeyUsage_OID;
  
  private static ObjectIdentifier[] AnyUsage;
  
  private int counter = 0;
  
  private int privateKeyCount = 0;
  
  private int secretKeyCount = 0;
  
  private int certificateCount = 0;
  
  private SecureRandom random;
  
  private Map<String, Entry> entries = Collections.synchronizedMap(new LinkedHashMap());
  
  private ArrayList<KeyEntry> keyList = new ArrayList();
  
  private LinkedHashMap<X500Principal, X509Certificate> certsMap = new LinkedHashMap();
  
  private ArrayList<CertEntry> certEntries = new ArrayList();
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar) throws NoSuchAlgorithmException, UnrecoverableKeyException {
    ObjectIdentifier objectIdentifier;
    AlgorithmParameters algorithmParameters;
    byte[] arrayOfByte2;
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    SecretKeySpec secretKeySpec = null;
    if (entry == null || !(entry instanceof KeyEntry))
      return null; 
    byte[] arrayOfByte1 = null;
    if (entry instanceof PrivateKeyEntry) {
      arrayOfByte1 = ((PrivateKeyEntry)entry).protectedPrivKey;
    } else if (entry instanceof SecretKeyEntry) {
      arrayOfByte1 = ((SecretKeyEntry)entry).protectedSecretKey;
    } else {
      throw new UnrecoverableKeyException("Error locating key");
    } 
    try {
      EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte1);
      arrayOfByte2 = encryptedPrivateKeyInfo.getEncryptedData();
      DerValue derValue = new DerValue(encryptedPrivateKeyInfo.getAlgorithm().encode());
      DerInputStream derInputStream = derValue.toDerInputStream();
      objectIdentifier = derInputStream.getOID();
      algorithmParameters = parseAlgParameters(objectIdentifier, derInputStream);
    } catch (IOException iOException) {
      UnrecoverableKeyException unrecoverableKeyException = new UnrecoverableKeyException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo: " + iOException);
      unrecoverableKeyException.initCause(iOException);
      throw unrecoverableKeyException;
    } 
    try {
      byte[] arrayOfByte;
      int i = 0;
      if (algorithmParameters != null) {
        PBEParameterSpec pBEParameterSpec;
        try {
          pBEParameterSpec = (PBEParameterSpec)algorithmParameters.getParameterSpec(PBEParameterSpec.class);
        } catch (InvalidParameterSpecException null) {
          throw new IOException("Invalid PBE algorithm parameters");
        } 
        i = pBEParameterSpec.getIterationCount();
        if (i > 5000000)
          throw new IOException("PBE iteration count too large"); 
      } 
      while (true) {
        try {
          SecretKey secretKey = getPBEKey(paramArrayOfChar);
          Cipher cipher = Cipher.getInstance(mapPBEParamsToAlgorithm(objectIdentifier, algorithmParameters));
          cipher.init(2, secretKey, algorithmParameters);
          arrayOfByte = cipher.doFinal(arrayOfByte2);
          break;
        } catch (Exception exception) {
          if (paramArrayOfChar.length == 0) {
            paramArrayOfChar = new char[1];
            continue;
          } 
          throw exception;
        } 
      } 
      DerValue derValue = new DerValue(arrayOfByte);
      DerInputStream derInputStream = derValue.toDerInputStream();
      int j = derInputStream.getInteger();
      DerValue[] arrayOfDerValue = derInputStream.getSequence(2);
      AlgorithmId algorithmId = new AlgorithmId(arrayOfDerValue[0].getOID());
      String str = algorithmId.getName();
      if (entry instanceof PrivateKeyEntry) {
        KeyFactory keyFactory = KeyFactory.getInstance(str);
        PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(arrayOfByte);
        secretKeySpec = keyFactory.generatePrivate(pKCS8EncodedKeySpec);
        if (debug != null)
          debug.println("Retrieved a protected private key at alias '" + paramString + "' (" + (new AlgorithmId(objectIdentifier)).getName() + " iterations: " + i + ")"); 
      } else {
        byte[] arrayOfByte3 = derInputStream.getOctetString();
        SecretKeySpec secretKeySpec1 = new SecretKeySpec(arrayOfByte3, str);
        if (str.startsWith("PBE")) {
          SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(str);
          KeySpec keySpec = secretKeyFactory.getKeySpec(secretKeySpec1, PBEKeySpec.class);
          SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        } else {
          secretKeySpec = secretKeySpec1;
        } 
        if (debug != null)
          debug.println("Retrieved a protected secret key at alias '" + paramString + "' (" + (new AlgorithmId(objectIdentifier)).getName() + " iterations: " + i + ")"); 
      } 
    } catch (Exception exception) {
      UnrecoverableKeyException unrecoverableKeyException = new UnrecoverableKeyException("Get Key failed: " + exception.getMessage());
      unrecoverableKeyException.initCause(exception);
      throw unrecoverableKeyException;
    } 
    return secretKeySpec;
  }
  
  public Certificate[] engineGetCertificateChain(String paramString) {
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (entry != null && entry instanceof PrivateKeyEntry) {
      if (((PrivateKeyEntry)entry).chain == null)
        return null; 
      if (debug != null)
        debug.println("Retrieved a " + ((PrivateKeyEntry)entry).chain.length + "-certificate chain at alias '" + paramString + "'"); 
      return (Certificate[])((PrivateKeyEntry)entry).chain.clone();
    } 
    return null;
  }
  
  public Certificate engineGetCertificate(String paramString) {
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (entry == null)
      return null; 
    if (entry instanceof CertEntry && ((CertEntry)entry).trustedKeyUsage != null) {
      if (debug != null)
        if (Arrays.equals(AnyUsage, ((CertEntry)entry).trustedKeyUsage)) {
          debug.println("Retrieved a certificate at alias '" + paramString + "' (trusted for any purpose)");
        } else {
          debug.println("Retrieved a certificate at alias '" + paramString + "' (trusted for limited purposes)");
        }  
      return ((CertEntry)entry).cert;
    } 
    if (entry instanceof PrivateKeyEntry) {
      if (((PrivateKeyEntry)entry).chain == null)
        return null; 
      if (debug != null)
        debug.println("Retrieved a certificate at alias '" + paramString + "'"); 
      return ((PrivateKeyEntry)entry).chain[0];
    } 
    return null;
  }
  
  public Date engineGetCreationDate(String paramString) {
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    return (entry != null) ? new Date(entry.date.getTime()) : null;
  }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    passwordProtection = new KeyStore.PasswordProtection(paramArrayOfChar);
    try {
      setKeyEntry(paramString, paramKey, passwordProtection, paramArrayOfCertificate, null);
    } finally {
      try {
        passwordProtection.destroy();
      } catch (DestroyFailedException destroyFailedException) {}
    } 
  }
  
  private void setKeyEntry(String paramString, Key paramKey, KeyStore.PasswordProtection paramPasswordProtection, Certificate[] paramArrayOfCertificate, Set<KeyStore.Entry.Attribute> paramSet) throws KeyStoreException {
    try {
      SecretKeyEntry secretKeyEntry;
      if (paramKey instanceof PrivateKey) {
        PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(null);
        privateKeyEntry.date = new Date();
        if (paramKey.getFormat().equals("PKCS#8") || paramKey.getFormat().equals("PKCS8")) {
          if (debug != null)
            debug.println("Setting a protected private key at alias '" + paramString + "'"); 
          privateKeyEntry.protectedPrivKey = encryptPrivateKey(paramKey.getEncoded(), paramPasswordProtection);
        } else {
          throw new KeyStoreException("Private key is not encodedas PKCS#8");
        } 
        if (paramArrayOfCertificate != null) {
          if (paramArrayOfCertificate.length > 1 && !validateChain(paramArrayOfCertificate))
            throw new KeyStoreException("Certificate chain is not valid"); 
          privateKeyEntry.chain = (Certificate[])paramArrayOfCertificate.clone();
          this.certificateCount += paramArrayOfCertificate.length;
          if (debug != null)
            debug.println("Setting a " + paramArrayOfCertificate.length + "-certificate chain at alias '" + paramString + "'"); 
        } 
        this.privateKeyCount++;
        secretKeyEntry = privateKeyEntry;
      } else if (paramKey instanceof SecretKey) {
        SecretKeyEntry secretKeyEntry1 = new SecretKeyEntry(null);
        secretKeyEntry1.date = new Date();
        DerOutputStream derOutputStream1 = new DerOutputStream();
        DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(0);
        AlgorithmId algorithmId = AlgorithmId.get(paramKey.getAlgorithm());
        algorithmId.encode(derOutputStream2);
        derOutputStream2.putOctetString(paramKey.getEncoded());
        derOutputStream1.write((byte)48, derOutputStream2);
        secretKeyEntry1.protectedSecretKey = encryptPrivateKey(derOutputStream1.toByteArray(), paramPasswordProtection);
        if (debug != null)
          debug.println("Setting a protected secret key at alias '" + paramString + "'"); 
        this.secretKeyCount++;
        secretKeyEntry = secretKeyEntry1;
      } else {
        throw new KeyStoreException("Unsupported Key type");
      } 
      secretKeyEntry.attributes = new HashSet();
      if (paramSet != null)
        secretKeyEntry.attributes.addAll(paramSet); 
      secretKeyEntry.keyId = ("Time " + secretKeyEntry.date.getTime()).getBytes("UTF8");
      secretKeyEntry.alias = paramString.toLowerCase(Locale.ENGLISH);
      this.entries.put(paramString.toLowerCase(Locale.ENGLISH), secretKeyEntry);
    } catch (Exception exception) {
      throw new KeyStoreException("Key protection  algorithm not found: " + exception, exception);
    } 
  }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    try {
      new EncryptedPrivateKeyInfo(paramArrayOfByte);
    } catch (IOException iOException) {
      throw new KeyStoreException("Private key is not stored as PKCS#8 EncryptedPrivateKeyInfo: " + iOException, iOException);
    } 
    PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(null);
    privateKeyEntry.date = new Date();
    if (debug != null)
      debug.println("Setting a protected private key at alias '" + paramString + "'"); 
    try {
      privateKeyEntry.keyId = ("Time " + privateKeyEntry.date.getTime()).getBytes("UTF8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    privateKeyEntry.alias = paramString.toLowerCase(Locale.ENGLISH);
    privateKeyEntry.protectedPrivKey = (byte[])paramArrayOfByte.clone();
    if (paramArrayOfCertificate != null) {
      if (paramArrayOfCertificate.length > 1 && !validateChain(paramArrayOfCertificate))
        throw new KeyStoreException("Certificate chain is not valid"); 
      privateKeyEntry.chain = (Certificate[])paramArrayOfCertificate.clone();
      this.certificateCount += paramArrayOfCertificate.length;
      if (debug != null)
        debug.println("Setting a " + privateKeyEntry.chain.length + "-certificate chain at alias '" + paramString + "'"); 
    } 
    this.privateKeyCount++;
    this.entries.put(paramString.toLowerCase(Locale.ENGLISH), privateKeyEntry);
  }
  
  private byte[] getSalt() {
    byte[] arrayOfByte = new byte[20];
    if (this.random == null)
      this.random = new SecureRandom(); 
    this.random.nextBytes(arrayOfByte);
    return arrayOfByte;
  }
  
  private AlgorithmParameters getPBEAlgorithmParameters(String paramString) throws IOException {
    AlgorithmParameters algorithmParameters = null;
    PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(getSalt(), 50000);
    try {
      algorithmParameters = AlgorithmParameters.getInstance(paramString);
      algorithmParameters.init(pBEParameterSpec);
    } catch (Exception exception) {
      throw new IOException("getPBEAlgorithmParameters failed: " + exception.getMessage(), exception);
    } 
    return algorithmParameters;
  }
  
  private AlgorithmParameters parseAlgParameters(ObjectIdentifier paramObjectIdentifier, DerInputStream paramDerInputStream) throws IOException {
    AlgorithmParameters algorithmParameters = null;
    try {
      DerValue derValue;
      if (paramDerInputStream.available() == 0) {
        derValue = null;
      } else {
        derValue = paramDerInputStream.getDerValue();
        if (derValue.tag == 5)
          derValue = null; 
      } 
      if (derValue != null) {
        if (paramObjectIdentifier.equals(pbes2_OID)) {
          algorithmParameters = AlgorithmParameters.getInstance("PBES2");
        } else {
          algorithmParameters = AlgorithmParameters.getInstance("PBE");
        } 
        algorithmParameters.init(derValue.toByteArray());
      } 
    } catch (Exception exception) {
      throw new IOException("parseAlgParameters failed: " + exception.getMessage(), exception);
    } 
    return algorithmParameters;
  }
  
  private SecretKey getPBEKey(char[] paramArrayOfChar) throws IOException {
    SecretKey secretKey = null;
    try {
      PBEKeySpec pBEKeySpec = new PBEKeySpec(paramArrayOfChar);
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBE");
      secretKey = secretKeyFactory.generateSecret(pBEKeySpec);
      pBEKeySpec.clearPassword();
    } catch (Exception exception) {
      throw new IOException("getSecretKey failed: " + exception.getMessage(), exception);
    } 
    return secretKey;
  }
  
  private byte[] encryptPrivateKey(byte[] paramArrayOfByte, KeyStore.PasswordProtection paramPasswordProtection) throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
    byte[] arrayOfByte = null;
    try {
      AlgorithmParameters algorithmParameters;
      String str = paramPasswordProtection.getProtectionAlgorithm();
      if (str != null) {
        AlgorithmParameterSpec algorithmParameterSpec = paramPasswordProtection.getProtectionParameters();
        if (algorithmParameterSpec != null) {
          algorithmParameters = AlgorithmParameters.getInstance(str);
          algorithmParameters.init(algorithmParameterSpec);
        } else {
          algorithmParameters = getPBEAlgorithmParameters(str);
        } 
      } else {
        str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
              public String run() {
                String str = Security.getProperty(KEY_PROTECTION_ALGORITHM[0]);
                if (str == null)
                  str = Security.getProperty(KEY_PROTECTION_ALGORITHM[1]); 
                return str;
              }
            });
        if (str == null || str.isEmpty())
          str = "PBEWithSHA1AndDESede"; 
        algorithmParameters = getPBEAlgorithmParameters(str);
      } 
      ObjectIdentifier objectIdentifier = mapPBEAlgorithmToOID(str);
      if (objectIdentifier == null)
        throw new IOException("PBE algorithm '" + str + " 'is not supported for key entry protection"); 
      SecretKey secretKey = getPBEKey(paramPasswordProtection.getPassword());
      Cipher cipher = Cipher.getInstance(str);
      cipher.init(1, secretKey, algorithmParameters);
      byte[] arrayOfByte1 = cipher.doFinal(paramArrayOfByte);
      AlgorithmId algorithmId = new AlgorithmId(objectIdentifier, cipher.getParameters());
      if (debug != null)
        debug.println("  (Cipher algorithm: " + cipher.getAlgorithm() + ")"); 
      EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(algorithmId, arrayOfByte1);
      arrayOfByte = encryptedPrivateKeyInfo.getEncoded();
    } catch (Exception exception) {
      UnrecoverableKeyException unrecoverableKeyException = new UnrecoverableKeyException("Encrypt Private Key failed: " + exception.getMessage());
      unrecoverableKeyException.initCause(exception);
      throw unrecoverableKeyException;
    } 
    return arrayOfByte;
  }
  
  private static ObjectIdentifier mapPBEAlgorithmToOID(String paramString) throws NoSuchAlgorithmException { return paramString.toLowerCase(Locale.ENGLISH).startsWith("pbewithhmacsha") ? pbes2_OID : AlgorithmId.get(paramString).getOID(); }
  
  private static String mapPBEParamsToAlgorithm(ObjectIdentifier paramObjectIdentifier, AlgorithmParameters paramAlgorithmParameters) throws NoSuchAlgorithmException { return (paramObjectIdentifier.equals(pbes2_OID) && paramAlgorithmParameters != null) ? paramAlgorithmParameters.toString() : paramObjectIdentifier.toString(); }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate) throws KeyStoreException { setCertEntry(paramString, paramCertificate, null); }
  
  private void setCertEntry(String paramString, Certificate paramCertificate, Set<KeyStore.Entry.Attribute> paramSet) throws KeyStoreException {
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (entry != null && entry instanceof KeyEntry)
      throw new KeyStoreException("Cannot overwrite own certificate"); 
    CertEntry certEntry = new CertEntry((X509Certificate)paramCertificate, null, paramString, AnyUsage, paramSet);
    this.certificateCount++;
    this.entries.put(paramString, certEntry);
    if (debug != null)
      debug.println("Setting a trusted certificate at alias '" + paramString + "'"); 
  }
  
  public void engineDeleteEntry(String paramString) throws KeyStoreException {
    if (debug != null)
      debug.println("Removing entry at alias '" + paramString + "'"); 
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (entry instanceof PrivateKeyEntry) {
      PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry)entry;
      if (privateKeyEntry.chain != null)
        this.certificateCount -= privateKeyEntry.chain.length; 
      this.privateKeyCount--;
    } else if (entry instanceof CertEntry) {
      this.certificateCount--;
    } else if (entry instanceof SecretKeyEntry) {
      this.secretKeyCount--;
    } 
    this.entries.remove(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public Enumeration<String> engineAliases() { return Collections.enumeration(this.entries.keySet()); }
  
  public boolean engineContainsAlias(String paramString) { return this.entries.containsKey(paramString.toLowerCase(Locale.ENGLISH)); }
  
  public int engineSize() { return this.entries.size(); }
  
  public boolean engineIsKeyEntry(String paramString) {
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    return (entry != null && entry instanceof KeyEntry);
  }
  
  public boolean engineIsCertificateEntry(String paramString) {
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    return (entry != null && entry instanceof CertEntry && ((CertEntry)entry).trustedKeyUsage != null);
  }
  
  public boolean engineEntryInstanceOf(String paramString, Class<? extends KeyStore.Entry> paramClass) {
    if (paramClass == KeyStore.TrustedCertificateEntry.class)
      return engineIsCertificateEntry(paramString); 
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    return (paramClass == KeyStore.PrivateKeyEntry.class) ? ((entry != null && entry instanceof PrivateKeyEntry)) : ((paramClass == KeyStore.SecretKeyEntry.class) ? ((entry != null && entry instanceof SecretKeyEntry)) : false);
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate) {
    Certificate certificate = null;
    Enumeration enumeration = engineAliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      Entry entry = (Entry)this.entries.get(str);
      if (entry instanceof PrivateKeyEntry) {
        if (((PrivateKeyEntry)entry).chain != null)
          certificate = ((PrivateKeyEntry)entry).chain[0]; 
      } else if (entry instanceof CertEntry && ((CertEntry)entry).trustedKeyUsage != null) {
        certificate = ((CertEntry)entry).cert;
      } else {
        continue;
      } 
      if (certificate != null && certificate.equals(paramCertificate))
        return str; 
    } 
    return null;
  }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException {
    if (paramArrayOfChar == null)
      throw new IllegalArgumentException("password can't be null"); 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(3);
    byte[] arrayOfByte1 = derOutputStream2.toByteArray();
    derOutputStream1.write(arrayOfByte1);
    DerOutputStream derOutputStream3 = new DerOutputStream();
    DerOutputStream derOutputStream4 = new DerOutputStream();
    if (this.privateKeyCount > 0 || this.secretKeyCount > 0) {
      if (debug != null)
        debug.println("Storing " + (this.privateKeyCount + this.secretKeyCount) + " protected key(s) in a PKCS#7 data"); 
      byte[] arrayOfByte = createSafeContent();
      ContentInfo contentInfo1 = new ContentInfo(arrayOfByte);
      contentInfo1.encode(derOutputStream4);
    } 
    if (this.certificateCount > 0) {
      if (debug != null)
        debug.println("Storing " + this.certificateCount + " certificate(s) in a PKCS#7 encryptedData"); 
      byte[] arrayOfByte = createEncryptedData(paramArrayOfChar);
      ContentInfo contentInfo1 = new ContentInfo(ContentInfo.ENCRYPTED_DATA_OID, new DerValue(arrayOfByte));
      contentInfo1.encode(derOutputStream4);
    } 
    DerOutputStream derOutputStream5 = new DerOutputStream();
    derOutputStream5.write((byte)48, derOutputStream4);
    byte[] arrayOfByte2 = derOutputStream5.toByteArray();
    ContentInfo contentInfo = new ContentInfo(arrayOfByte2);
    contentInfo.encode(derOutputStream3);
    byte[] arrayOfByte3 = derOutputStream3.toByteArray();
    derOutputStream1.write(arrayOfByte3);
    byte[] arrayOfByte4 = calculateMac(paramArrayOfChar, arrayOfByte2);
    derOutputStream1.write(arrayOfByte4);
    DerOutputStream derOutputStream6 = new DerOutputStream();
    derOutputStream6.write((byte)48, derOutputStream1);
    byte[] arrayOfByte5 = derOutputStream6.toByteArray();
    paramOutputStream.write(arrayOfByte5);
    paramOutputStream.flush();
  }
  
  public KeyStore.Entry engineGetEntry(String paramString, KeyStore.ProtectionParameter paramProtectionParameter) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
    if (!engineContainsAlias(paramString))
      return null; 
    Entry entry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
    if (paramProtectionParameter == null)
      if (engineIsCertificateEntry(paramString)) {
        if (entry instanceof CertEntry && ((CertEntry)entry).trustedKeyUsage != null) {
          if (debug != null)
            debug.println("Retrieved a trusted certificate at alias '" + paramString + "'"); 
          return new KeyStore.TrustedCertificateEntry(((CertEntry)entry).cert, getAttributes(entry));
        } 
      } else {
        throw new UnrecoverableKeyException("requested entry requires a password");
      }  
    if (paramProtectionParameter instanceof KeyStore.PasswordProtection) {
      if (engineIsCertificateEntry(paramString))
        throw new UnsupportedOperationException("trusted certificate entries are not password-protected"); 
      if (engineIsKeyEntry(paramString)) {
        KeyStore.PasswordProtection passwordProtection = (KeyStore.PasswordProtection)paramProtectionParameter;
        char[] arrayOfChar = passwordProtection.getPassword();
        Key key = engineGetKey(paramString, arrayOfChar);
        if (key instanceof PrivateKey) {
          Certificate[] arrayOfCertificate = engineGetCertificateChain(paramString);
          return new KeyStore.PrivateKeyEntry((PrivateKey)key, arrayOfCertificate, getAttributes(entry));
        } 
        if (key instanceof SecretKey)
          return new KeyStore.SecretKeyEntry((SecretKey)key, getAttributes(entry)); 
      } else if (!engineIsKeyEntry(paramString)) {
        throw new UnsupportedOperationException("untrusted certificate entries are not password-protected");
      } 
    } 
    throw new UnsupportedOperationException();
  }
  
  public void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter) throws KeyStoreException {
    if (paramProtectionParameter != null && !(paramProtectionParameter instanceof KeyStore.PasswordProtection))
      throw new KeyStoreException("unsupported protection parameter"); 
    KeyStore.PasswordProtection passwordProtection = null;
    if (paramProtectionParameter != null)
      passwordProtection = (KeyStore.PasswordProtection)paramProtectionParameter; 
    if (paramEntry instanceof KeyStore.TrustedCertificateEntry) {
      if (paramProtectionParameter != null && passwordProtection.getPassword() != null)
        throw new KeyStoreException("trusted certificate entries are not password-protected"); 
      KeyStore.TrustedCertificateEntry trustedCertificateEntry = (KeyStore.TrustedCertificateEntry)paramEntry;
      setCertEntry(paramString, trustedCertificateEntry.getTrustedCertificate(), trustedCertificateEntry.getAttributes());
      return;
    } 
    if (paramEntry instanceof KeyStore.PrivateKeyEntry) {
      if (passwordProtection == null || passwordProtection.getPassword() == null)
        throw new KeyStoreException("non-null password required to create PrivateKeyEntry"); 
      KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)paramEntry;
      setKeyEntry(paramString, privateKeyEntry.getPrivateKey(), passwordProtection, privateKeyEntry.getCertificateChain(), privateKeyEntry.getAttributes());
      return;
    } 
    if (paramEntry instanceof KeyStore.SecretKeyEntry) {
      if (passwordProtection == null || passwordProtection.getPassword() == null)
        throw new KeyStoreException("non-null password required to create SecretKeyEntry"); 
      KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry)paramEntry;
      setKeyEntry(paramString, secretKeyEntry.getSecretKey(), passwordProtection, (Certificate[])null, secretKeyEntry.getAttributes());
      return;
    } 
    throw new KeyStoreException("unsupported entry type: " + paramEntry.getClass().getName());
  }
  
  private Set<KeyStore.Entry.Attribute> getAttributes(Entry paramEntry) {
    if (paramEntry.attributes == null)
      paramEntry.attributes = new HashSet(); 
    paramEntry.attributes.add(new PKCS12Attribute(PKCS9FriendlyName_OID.toString(), paramEntry.alias));
    byte[] arrayOfByte = paramEntry.keyId;
    if (arrayOfByte != null)
      paramEntry.attributes.add(new PKCS12Attribute(PKCS9LocalKeyId_OID.toString(), Debug.toString(arrayOfByte))); 
    if (paramEntry instanceof CertEntry) {
      ObjectIdentifier[] arrayOfObjectIdentifier = ((CertEntry)paramEntry).trustedKeyUsage;
      if (arrayOfObjectIdentifier != null)
        if (arrayOfObjectIdentifier.length == 1) {
          paramEntry.attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID.toString(), arrayOfObjectIdentifier[0].toString()));
        } else {
          paramEntry.attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID.toString(), Arrays.toString(arrayOfObjectIdentifier)));
        }  
    } 
    return paramEntry.attributes;
  }
  
  private byte[] generateHash(byte[] paramArrayOfByte) throws IOException {
    byte[] arrayOfByte = null;
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
      messageDigest.update(paramArrayOfByte);
      arrayOfByte = messageDigest.digest();
    } catch (Exception exception) {
      throw new IOException("generateHash failed: " + exception, exception);
    } 
    return arrayOfByte;
  }
  
  private byte[] calculateMac(char[] paramArrayOfChar, byte[] paramArrayOfByte) throws IOException {
    byte[] arrayOfByte = null;
    String str = "SHA1";
    try {
      byte[] arrayOfByte1 = getSalt();
      Mac mac = Mac.getInstance("HmacPBESHA1");
      PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(arrayOfByte1, 100000);
      SecretKey secretKey = getPBEKey(paramArrayOfChar);
      mac.init(secretKey, pBEParameterSpec);
      mac.update(paramArrayOfByte);
      byte[] arrayOfByte2 = mac.doFinal();
      MacData macData = new MacData(str, arrayOfByte2, arrayOfByte1, 100000);
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.write(macData.getEncoded());
      arrayOfByte = derOutputStream.toByteArray();
    } catch (Exception exception) {
      throw new IOException("calculateMac failed: " + exception, exception);
    } 
    return arrayOfByte;
  }
  
  private boolean validateChain(Certificate[] paramArrayOfCertificate) {
    for (byte b = 0; b < paramArrayOfCertificate.length - 1; b++) {
      X500Principal x500Principal1 = ((X509Certificate)paramArrayOfCertificate[b]).getIssuerX500Principal();
      X500Principal x500Principal2 = ((X509Certificate)paramArrayOfCertificate[b + true]).getSubjectX500Principal();
      if (!x500Principal1.equals(x500Principal2))
        return false; 
    } 
    HashSet hashSet = new HashSet(Arrays.asList(paramArrayOfCertificate));
    return (hashSet.size() == paramArrayOfCertificate.length);
  }
  
  private byte[] getBagAttributes(String paramString, byte[] paramArrayOfByte, Set<KeyStore.Entry.Attribute> paramSet) throws IOException { return getBagAttributes(paramString, paramArrayOfByte, null, paramSet); }
  
  private byte[] getBagAttributes(String paramString, byte[] paramArrayOfByte, ObjectIdentifier[] paramArrayOfObjectIdentifier, Set<KeyStore.Entry.Attribute> paramSet) throws IOException {
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    byte[] arrayOfByte3 = null;
    if (paramString == null && paramArrayOfByte == null && arrayOfByte3 == null)
      return null; 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    if (paramString != null) {
      DerOutputStream derOutputStream3 = new DerOutputStream();
      derOutputStream3.putOID(PKCS9FriendlyName_OID);
      DerOutputStream derOutputStream4 = new DerOutputStream();
      DerOutputStream derOutputStream5 = new DerOutputStream();
      derOutputStream4.putBMPString(paramString);
      derOutputStream3.write((byte)49, derOutputStream4);
      derOutputStream5.write((byte)48, derOutputStream3);
      arrayOfByte2 = derOutputStream5.toByteArray();
    } 
    if (paramArrayOfByte != null) {
      DerOutputStream derOutputStream3 = new DerOutputStream();
      derOutputStream3.putOID(PKCS9LocalKeyId_OID);
      DerOutputStream derOutputStream4 = new DerOutputStream();
      DerOutputStream derOutputStream5 = new DerOutputStream();
      derOutputStream4.putOctetString(paramArrayOfByte);
      derOutputStream3.write((byte)49, derOutputStream4);
      derOutputStream5.write((byte)48, derOutputStream3);
      arrayOfByte1 = derOutputStream5.toByteArray();
    } 
    if (paramArrayOfObjectIdentifier != null) {
      DerOutputStream derOutputStream3 = new DerOutputStream();
      derOutputStream3.putOID(TrustedKeyUsage_OID);
      DerOutputStream derOutputStream4 = new DerOutputStream();
      DerOutputStream derOutputStream5 = new DerOutputStream();
      for (ObjectIdentifier objectIdentifier : paramArrayOfObjectIdentifier)
        derOutputStream4.putOID(objectIdentifier); 
      derOutputStream3.write((byte)49, derOutputStream4);
      derOutputStream5.write((byte)48, derOutputStream3);
      arrayOfByte3 = derOutputStream5.toByteArray();
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    if (arrayOfByte2 != null)
      derOutputStream2.write(arrayOfByte2); 
    if (arrayOfByte1 != null)
      derOutputStream2.write(arrayOfByte1); 
    if (arrayOfByte3 != null)
      derOutputStream2.write(arrayOfByte3); 
    if (paramSet != null)
      for (KeyStore.Entry.Attribute attribute : paramSet) {
        String str = attribute.getName();
        if (CORE_ATTRIBUTES[0].equals(str) || CORE_ATTRIBUTES[1].equals(str) || CORE_ATTRIBUTES[2].equals(str))
          continue; 
        derOutputStream2.write(((PKCS12Attribute)attribute).getEncoded());
      }  
    derOutputStream1.write((byte)49, derOutputStream2);
    return derOutputStream1.toByteArray();
  }
  
  private byte[] createEncryptedData(char[] paramArrayOfChar) throws CertificateException, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    Enumeration enumeration = engineAliases();
    while (enumeration.hasMoreElements()) {
      Certificate[] arrayOfCertificate;
      String str = (String)enumeration.nextElement();
      Entry entry = (Entry)this.entries.get(str);
      if (entry instanceof PrivateKeyEntry) {
        PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry)entry;
        if (privateKeyEntry.chain != null) {
          arrayOfCertificate = privateKeyEntry.chain;
        } else {
          arrayOfCertificate = new Certificate[0];
        } 
      } else if (entry instanceof CertEntry) {
        arrayOfCertificate = new Certificate[] { ((CertEntry)entry).cert };
      } else {
        arrayOfCertificate = new Certificate[0];
      } 
      for (byte b = 0; b < arrayOfCertificate.length; b++) {
        DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.putOID(CertBag_OID);
        DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.putOID(PKCS9CertType_OID);
        DerOutputStream derOutputStream7 = new DerOutputStream();
        X509Certificate x509Certificate = (X509Certificate)arrayOfCertificate[b];
        derOutputStream7.putOctetString(x509Certificate.getEncoded());
        derOutputStream6.write(DerValue.createTag(-128, true, (byte)0), derOutputStream7);
        DerOutputStream derOutputStream8 = new DerOutputStream();
        derOutputStream8.write((byte)48, derOutputStream6);
        byte[] arrayOfByte3 = derOutputStream8.toByteArray();
        DerOutputStream derOutputStream9 = new DerOutputStream();
        derOutputStream9.write(arrayOfByte3);
        derOutputStream5.write(DerValue.createTag(-128, true, (byte)0), derOutputStream9);
        byte[] arrayOfByte4 = null;
        if (!b) {
          if (entry instanceof KeyEntry) {
            KeyEntry keyEntry = (KeyEntry)entry;
            arrayOfByte4 = getBagAttributes(keyEntry.alias, keyEntry.keyId, keyEntry.attributes);
          } else {
            CertEntry certEntry = (CertEntry)entry;
            arrayOfByte4 = getBagAttributes(certEntry.alias, certEntry.keyId, certEntry.trustedKeyUsage, certEntry.attributes);
          } 
        } else {
          arrayOfByte4 = getBagAttributes(x509Certificate.getSubjectX500Principal().getName(), null, entry.attributes);
        } 
        if (arrayOfByte4 != null)
          derOutputStream5.write(arrayOfByte4); 
        derOutputStream1.write((byte)48, derOutputStream5);
      } 
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    byte[] arrayOfByte1 = derOutputStream2.toByteArray();
    byte[] arrayOfByte2 = encryptContent(arrayOfByte1, paramArrayOfChar);
    DerOutputStream derOutputStream3 = new DerOutputStream();
    DerOutputStream derOutputStream4 = new DerOutputStream();
    derOutputStream3.putInteger(0);
    derOutputStream3.write(arrayOfByte2);
    derOutputStream4.write((byte)48, derOutputStream3);
    return derOutputStream4.toByteArray();
  }
  
  private byte[] createSafeContent() {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    Enumeration enumeration = engineAliases();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      Entry entry = (Entry)this.entries.get(str);
      if (entry == null || !(entry instanceof KeyEntry))
        continue; 
      DerOutputStream derOutputStream = new DerOutputStream();
      KeyEntry keyEntry = (KeyEntry)entry;
      if (keyEntry instanceof PrivateKeyEntry) {
        derOutputStream.putOID(PKCS8ShroudedKeyBag_OID);
        byte[] arrayOfByte1 = ((PrivateKeyEntry)keyEntry).protectedPrivKey;
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = null;
        try {
          encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte1);
        } catch (IOException iOException) {
          throw new IOException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo" + iOException.getMessage());
        } 
        DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.write(encryptedPrivateKeyInfo.getEncoded());
        derOutputStream.write(DerValue.createTag(-128, true, (byte)0), derOutputStream3);
      } else if (keyEntry instanceof SecretKeyEntry) {
        derOutputStream.putOID(SecretBag_OID);
        DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOID(PKCS8ShroudedKeyBag_OID);
        DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.putOctetString(((SecretKeyEntry)keyEntry).protectedSecretKey);
        derOutputStream3.write(DerValue.createTag(-128, true, (byte)0), derOutputStream4);
        DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write((byte)48, derOutputStream3);
        byte[] arrayOfByte1 = derOutputStream5.toByteArray();
        DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write(arrayOfByte1);
        derOutputStream.write(DerValue.createTag(-128, true, (byte)0), derOutputStream6);
      } else {
        continue;
      } 
      byte[] arrayOfByte = getBagAttributes(str, entry.keyId, entry.attributes);
      derOutputStream.write(arrayOfByte);
      derOutputStream1.write((byte)48, derOutputStream);
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  private byte[] encryptContent(byte[] paramArrayOfByte, char[] paramArrayOfChar) throws IOException {
    byte[] arrayOfByte1 = null;
    AlgorithmParameters algorithmParameters = getPBEAlgorithmParameters("PBEWithSHA1AndRC2_40");
    DerOutputStream derOutputStream1 = new DerOutputStream();
    AlgorithmId algorithmId = new AlgorithmId(pbeWithSHAAnd40BitRC2CBC_OID, algorithmParameters);
    algorithmId.encode(derOutputStream1);
    byte[] arrayOfByte2 = derOutputStream1.toByteArray();
    try {
      SecretKey secretKey = getPBEKey(paramArrayOfChar);
      Cipher cipher = Cipher.getInstance("PBEWithSHA1AndRC2_40");
      cipher.init(1, secretKey, algorithmParameters);
      arrayOfByte1 = cipher.doFinal(paramArrayOfByte);
      if (debug != null)
        debug.println("  (Cipher algorithm: " + cipher.getAlgorithm() + ")"); 
    } catch (Exception exception) {
      throw new IOException("Failed to encrypt safe contents entry: " + exception, exception);
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putOID(ContentInfo.DATA_OID);
    derOutputStream2.write(arrayOfByte2);
    DerOutputStream derOutputStream3 = new DerOutputStream();
    derOutputStream3.putOctetString(arrayOfByte1);
    derOutputStream2.writeImplicit(DerValue.createTag(-128, false, (byte)0), derOutputStream3);
    DerOutputStream derOutputStream4 = new DerOutputStream();
    derOutputStream4.write((byte)48, derOutputStream2);
    return derOutputStream4.toByteArray();
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException {
    byte[] arrayOfByte;
    Object object1 = null;
    Object object2 = null;
    Object object3 = null;
    if (paramInputStream == null)
      return; 
    this.counter = 0;
    DerValue derValue = new DerValue(paramInputStream);
    DerInputStream derInputStream1 = derValue.toDerInputStream();
    int i = derInputStream1.getInteger();
    if (i != 3)
      throw new IOException("PKCS12 keystore not in version 3 format"); 
    this.entries.clear();
    ContentInfo contentInfo = new ContentInfo(derInputStream1);
    ObjectIdentifier objectIdentifier = contentInfo.getContentType();
    if (objectIdentifier.equals(ContentInfo.DATA_OID)) {
      arrayOfByte = contentInfo.getData();
    } else {
      throw new IOException("public key protected PKCS12 not supported");
    } 
    DerInputStream derInputStream2 = new DerInputStream(arrayOfByte);
    DerValue[] arrayOfDerValue = derInputStream2.getSequence(2);
    int j = arrayOfDerValue.length;
    this.privateKeyCount = 0;
    this.secretKeyCount = 0;
    this.certificateCount = 0;
    byte b1 = 0;
    while (true) {
      byte[] arrayOfByte1;
      if (b1 < j) {
        byte[] arrayOfByte2 = null;
        DerInputStream derInputStream3 = new DerInputStream(arrayOfDerValue[b1].toByteArray());
        ContentInfo contentInfo1 = new ContentInfo(derInputStream3);
        objectIdentifier = contentInfo1.getContentType();
        arrayOfByte1 = null;
        if (objectIdentifier.equals(ContentInfo.DATA_OID)) {
          if (debug != null)
            debug.println("Loading PKCS#7 data"); 
          arrayOfByte1 = contentInfo1.getData();
        } else {
          if (objectIdentifier.equals(ContentInfo.ENCRYPTED_DATA_OID)) {
            if (paramArrayOfChar == null) {
              if (debug != null)
                debug.println("Warning: skipping PKCS#7 encryptedData - no password was supplied"); 
            } else {
              DerInputStream derInputStream4 = contentInfo1.getContent().toDerInputStream();
              int k = derInputStream4.getInteger();
              DerValue[] arrayOfDerValue1 = derInputStream4.getSequence(2);
              ObjectIdentifier objectIdentifier1 = arrayOfDerValue1[0].getOID();
              arrayOfByte2 = arrayOfDerValue1[1].toByteArray();
              if (!arrayOfDerValue1[2].isContextSpecific((byte)0))
                throw new IOException("encrypted content not present!"); 
              byte b = 4;
              if (arrayOfDerValue1[2].isConstructed())
                b = (byte)(b | 0x20); 
              arrayOfDerValue1[2].resetTag(b);
              arrayOfByte1 = arrayOfDerValue1[2].getOctetString();
              DerInputStream derInputStream5 = arrayOfDerValue1[1].toDerInputStream();
              ObjectIdentifier objectIdentifier2 = derInputStream5.getOID();
              AlgorithmParameters algorithmParameters = parseAlgParameters(objectIdentifier2, derInputStream5);
              int m = 0;
              if (algorithmParameters != null) {
                PBEParameterSpec pBEParameterSpec;
                try {
                  pBEParameterSpec = (PBEParameterSpec)algorithmParameters.getParameterSpec(PBEParameterSpec.class);
                } catch (InvalidParameterSpecException invalidParameterSpecException) {
                  throw new IOException("Invalid PBE algorithm parameters");
                } 
                m = pBEParameterSpec.getIterationCount();
                if (m > 5000000)
                  throw new IOException("PBE iteration count too large"); 
              } 
              if (debug != null)
                debug.println("Loading PKCS#7 encryptedData (" + (new AlgorithmId(objectIdentifier2)).getName() + " iterations: " + m + ")"); 
              while (true) {
                try {
                  SecretKey secretKey = getPBEKey(paramArrayOfChar);
                  Cipher cipher = Cipher.getInstance(objectIdentifier2.toString());
                  cipher.init(2, secretKey, algorithmParameters);
                  arrayOfByte1 = cipher.doFinal(arrayOfByte1);
                  break;
                } catch (Exception exception) {
                  if (paramArrayOfChar.length == 0) {
                    paramArrayOfChar = new char[1];
                    continue;
                  } 
                  throw new IOException("keystore password was incorrect", new UnrecoverableKeyException("failed to decrypt safe contents entry: " + exception));
                } 
              } 
              derInputStream4 = new DerInputStream(arrayOfByte1);
              loadSafeContents(derInputStream4, paramArrayOfChar);
            } 
          } else {
            throw new IOException("public key protected PKCS12 not supported");
          } 
          b1++;
          continue;
        } 
      } else {
        break;
      } 
      DerInputStream derInputStream = new DerInputStream(arrayOfByte1);
      loadSafeContents(derInputStream, paramArrayOfChar);
    } 
    if (paramArrayOfChar != null && derInputStream1.available() > 0) {
      MacData macData = new MacData(derInputStream1);
      int k = macData.getIterations();
      try {
        if (k > 5000000)
          throw new InvalidAlgorithmParameterException("MAC iteration count too large: " + k); 
        String str = macData.getDigestAlgName().toUpperCase(Locale.ENGLISH);
        str = str.replace("-", "");
        Mac mac = Mac.getInstance("HmacPBE" + str);
        PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(macData.getSalt(), k);
        SecretKey secretKey = getPBEKey(paramArrayOfChar);
        mac.init(secretKey, pBEParameterSpec);
        mac.update(arrayOfByte);
        byte[] arrayOfByte1 = mac.doFinal();
        if (debug != null)
          debug.println("Checking keystore integrity (" + mac.getAlgorithm() + " iterations: " + k + ")"); 
        if (!MessageDigest.isEqual(macData.getDigest(), arrayOfByte1))
          throw new UnrecoverableKeyException("Failed PKCS12 integrity checking"); 
      } catch (Exception exception) {
        throw new IOException("Integrity check failed: " + exception, exception);
      } 
    } 
    PrivateKeyEntry[] arrayOfPrivateKeyEntry = (PrivateKeyEntry[])this.keyList.toArray(new PrivateKeyEntry[this.keyList.size()]);
    for (byte b2 = 0; b2 < arrayOfPrivateKeyEntry.length; b2++) {
      PrivateKeyEntry privateKeyEntry = arrayOfPrivateKeyEntry[b2];
      if (privateKeyEntry.keyId != null) {
        ArrayList arrayList = new ArrayList();
        X509Certificate x509Certificate;
        label105: for (x509Certificate = findMatchedCertificate(privateKeyEntry); x509Certificate != null; x509Certificate = (X509Certificate)this.certsMap.get(x500Principal)) {
          if (!arrayList.isEmpty())
            for (X509Certificate x509Certificate1 : arrayList) {
              if (x509Certificate.equals(x509Certificate1)) {
                if (debug != null) {
                  debug.println("Loop detected in certificate chain. Skip adding repeated cert to chain. Subject: " + x509Certificate.getSubjectX500Principal().toString());
                  break label105;
                } 
                break label105;
              } 
            }  
          arrayList.add(x509Certificate);
          X500Principal x500Principal = x509Certificate.getIssuerX500Principal();
          if (x500Principal.equals(x509Certificate.getSubjectX500Principal()))
            break; 
        } 
        if (arrayList.size() > 0)
          privateKeyEntry.chain = (Certificate[])arrayList.toArray(new Certificate[arrayList.size()]); 
      } 
    } 
    if (debug != null) {
      if (this.privateKeyCount > 0)
        debug.println("Loaded " + this.privateKeyCount + " protected private key(s)"); 
      if (this.secretKeyCount > 0)
        debug.println("Loaded " + this.secretKeyCount + " protected secret key(s)"); 
      if (this.certificateCount > 0)
        debug.println("Loaded " + this.certificateCount + " certificate(s)"); 
    } 
    this.certEntries.clear();
    this.certsMap.clear();
    this.keyList.clear();
  }
  
  private X509Certificate findMatchedCertificate(PrivateKeyEntry paramPrivateKeyEntry) {
    CertEntry certEntry1 = null;
    CertEntry certEntry2 = null;
    for (CertEntry certEntry : this.certEntries) {
      if (Arrays.equals(paramPrivateKeyEntry.keyId, certEntry.keyId)) {
        certEntry1 = certEntry;
        if (paramPrivateKeyEntry.alias.equalsIgnoreCase(certEntry.alias))
          return certEntry.cert; 
        continue;
      } 
      if (paramPrivateKeyEntry.alias.equalsIgnoreCase(certEntry.alias))
        certEntry2 = certEntry; 
    } 
    return (certEntry1 != null) ? certEntry1.cert : ((certEntry2 != null) ? certEntry2.cert : null);
  }
  
  private void loadSafeContents(DerInputStream paramDerInputStream, char[] paramArrayOfChar) throws IOException, NoSuchAlgorithmException, CertificateException {
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(2);
    int i = arrayOfDerValue.length;
    for (byte b = 0; b < i; b++) {
      Object object;
      SecretKeyEntry secretKeyEntry = null;
      DerInputStream derInputStream = arrayOfDerValue[b].toDerInputStream();
      ObjectIdentifier objectIdentifier = derInputStream.getOID();
      DerValue derValue = derInputStream.getDerValue();
      if (!derValue.isContextSpecific((byte)0))
        throw new IOException("unsupported PKCS12 bag value type " + derValue.tag); 
      derValue = derValue.data.getDerValue();
      if (objectIdentifier.equals(PKCS8ShroudedKeyBag_OID)) {
        object = new PrivateKeyEntry(null);
        object.protectedPrivKey = derValue.toByteArray();
        secretKeyEntry = object;
        this.privateKeyCount++;
      } else if (objectIdentifier.equals(CertBag_OID)) {
        object = new DerInputStream(derValue.toByteArray());
        DerValue[] arrayOfDerValue1 = object.getSequence(2);
        ObjectIdentifier objectIdentifier1 = arrayOfDerValue1[0].getOID();
        if (!arrayOfDerValue1[1].isContextSpecific((byte)0))
          throw new IOException("unsupported PKCS12 cert value type " + (arrayOfDerValue1[1]).tag); 
        DerValue derValue1 = (arrayOfDerValue1[1]).data.getDerValue();
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        X509Certificate x509Certificate2 = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(derValue1.getOctetString()));
        X509Certificate x509Certificate1 = x509Certificate2;
        this.certificateCount++;
      } else if (objectIdentifier.equals(SecretBag_OID)) {
        object = new DerInputStream(derValue.toByteArray());
        DerValue[] arrayOfDerValue1 = object.getSequence(2);
        ObjectIdentifier objectIdentifier1 = arrayOfDerValue1[0].getOID();
        if (!arrayOfDerValue1[1].isContextSpecific((byte)0))
          throw new IOException("unsupported PKCS12 secret value type " + (arrayOfDerValue1[1]).tag); 
        DerValue derValue1 = (arrayOfDerValue1[1]).data.getDerValue();
        SecretKeyEntry secretKeyEntry1 = new SecretKeyEntry(null);
        secretKeyEntry1.protectedSecretKey = derValue1.getOctetString();
        secretKeyEntry = secretKeyEntry1;
        this.secretKeyCount++;
      } else if (debug != null) {
        debug.println("Unsupported PKCS12 bag type: " + objectIdentifier);
      } 
      try {
        object = derInputStream.getSet(3);
      } catch (IOException iOException) {
        object = null;
      } 
      String str = null;
      byte[] arrayOfByte = null;
      ObjectIdentifier[] arrayOfObjectIdentifier = null;
      HashSet hashSet = new HashSet();
      if (object != null)
        for (byte b1 = 0; b1 < object.length; b1++) {
          DerValue[] arrayOfDerValue2;
          byte[] arrayOfByte1 = object[b1].toByteArray();
          DerInputStream derInputStream1 = new DerInputStream(arrayOfByte1);
          DerValue[] arrayOfDerValue1 = derInputStream1.getSequence(2);
          ObjectIdentifier objectIdentifier1 = arrayOfDerValue1[0].getOID();
          DerInputStream derInputStream2 = new DerInputStream(arrayOfDerValue1[1].toByteArray());
          try {
            arrayOfDerValue2 = derInputStream2.getSet(1);
          } catch (IOException iOException) {
            throw new IOException("Attribute " + objectIdentifier1 + " should have a value " + iOException.getMessage());
          } 
          if (objectIdentifier1.equals(PKCS9FriendlyName_OID)) {
            str = arrayOfDerValue2[0].getBMPString();
          } else if (objectIdentifier1.equals(PKCS9LocalKeyId_OID)) {
            arrayOfByte = arrayOfDerValue2[0].getOctetString();
          } else if (objectIdentifier1.equals(TrustedKeyUsage_OID)) {
            arrayOfObjectIdentifier = new ObjectIdentifier[arrayOfDerValue2.length];
            for (byte b2 = 0; b2 < arrayOfDerValue2.length; b2++)
              arrayOfObjectIdentifier[b2] = arrayOfDerValue2[b2].getOID(); 
          } else {
            hashSet.add(new PKCS12Attribute(arrayOfByte1));
          } 
        }  
      if (secretKeyEntry instanceof KeyEntry) {
        KeyEntry keyEntry = (KeyEntry)secretKeyEntry;
        if (secretKeyEntry instanceof PrivateKeyEntry && arrayOfByte == null)
          if (this.privateKeyCount == 1) {
            arrayOfByte = "01".getBytes("UTF8");
          } else {
            continue;
          }  
        keyEntry.keyId = arrayOfByte;
        String str1 = new String(arrayOfByte, "UTF8");
        Date date = null;
        if (str1.startsWith("Time "))
          try {
            date = new Date(Long.parseLong(str1.substring(5)));
          } catch (Exception exception) {
            date = null;
          }  
        if (date == null)
          date = new Date(); 
        keyEntry.date = date;
        if (secretKeyEntry instanceof PrivateKeyEntry)
          this.keyList.add((PrivateKeyEntry)keyEntry); 
        if (keyEntry.attributes == null)
          keyEntry.attributes = new HashSet(); 
        keyEntry.attributes.addAll(hashSet);
        if (str == null)
          str = getUnfriendlyName(); 
        keyEntry.alias = str;
        this.entries.put(str.toLowerCase(Locale.ENGLISH), keyEntry);
        continue;
      } 
      if (secretKeyEntry instanceof X509Certificate) {
        X509Certificate x509Certificate = (X509Certificate)secretKeyEntry;
        if (arrayOfByte == null && this.privateKeyCount == 1 && b == 0)
          arrayOfByte = "01".getBytes("UTF8"); 
        if (arrayOfObjectIdentifier != null) {
          if (str == null)
            str = getUnfriendlyName(); 
          CertEntry certEntry = new CertEntry(x509Certificate, arrayOfByte, str, arrayOfObjectIdentifier, hashSet);
          this.entries.put(str.toLowerCase(Locale.ENGLISH), certEntry);
        } else {
          this.certEntries.add(new CertEntry(x509Certificate, arrayOfByte, str));
        } 
        X500Principal x500Principal = x509Certificate.getSubjectX500Principal();
        if (x500Principal != null && !this.certsMap.containsKey(x500Principal))
          this.certsMap.put(x500Principal, x509Certificate); 
      } 
      continue;
    } 
  }
  
  private String getUnfriendlyName() {
    this.counter++;
    return String.valueOf(this.counter);
  }
  
  static  {
    try {
      PKCS8ShroudedKeyBag_OID = new ObjectIdentifier(keyBag);
      CertBag_OID = new ObjectIdentifier(certBag);
      SecretBag_OID = new ObjectIdentifier(secretBag);
      PKCS9FriendlyName_OID = new ObjectIdentifier(pkcs9Name);
      PKCS9LocalKeyId_OID = new ObjectIdentifier(pkcs9KeyId);
      PKCS9CertType_OID = new ObjectIdentifier(pkcs9certType);
      pbeWithSHAAnd40BitRC2CBC_OID = new ObjectIdentifier(pbeWithSHAAnd40BitRC2CBC);
      pbeWithSHAAnd3KeyTripleDESCBC_OID = new ObjectIdentifier(pbeWithSHAAnd3KeyTripleDESCBC);
      pbes2_OID = new ObjectIdentifier(pbes2);
      TrustedKeyUsage_OID = new ObjectIdentifier(TrustedKeyUsage);
      AnyUsage = new ObjectIdentifier[] { new ObjectIdentifier(AnyExtendedKeyUsage) };
    } catch (IOException iOException) {}
  }
  
  private static class CertEntry extends Entry {
    final X509Certificate cert;
    
    ObjectIdentifier[] trustedKeyUsage;
    
    CertEntry(X509Certificate param1X509Certificate, byte[] param1ArrayOfByte, String param1String) { this(param1X509Certificate, param1ArrayOfByte, param1String, null, null); }
    
    CertEntry(X509Certificate param1X509Certificate, byte[] param1ArrayOfByte, String param1String, ObjectIdentifier[] param1ArrayOfObjectIdentifier, Set<? extends KeyStore.Entry.Attribute> param1Set) {
      super(null);
      this.cert = param1X509Certificate;
      this.keyId = param1ArrayOfByte;
      this.alias = param1String;
      this.trustedKeyUsage = param1ArrayOfObjectIdentifier;
      this.attributes = new HashSet();
      if (param1Set != null)
        this.attributes.addAll(param1Set); 
    }
  }
  
  private static class Entry {
    Date date;
    
    String alias;
    
    byte[] keyId;
    
    Set<KeyStore.Entry.Attribute> attributes;
    
    private Entry() {}
  }
  
  private static class KeyEntry extends Entry {
    private KeyEntry() { super(null); }
  }
  
  private static class PrivateKeyEntry extends KeyEntry {
    byte[] protectedPrivKey;
    
    Certificate[] chain;
    
    private PrivateKeyEntry() { super(null); }
  }
  
  private static class SecretKeyEntry extends KeyEntry {
    byte[] protectedSecretKey;
    
    private SecretKeyEntry() { super(null); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs12\PKCS12KeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */