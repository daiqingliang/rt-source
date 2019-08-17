package sun.security.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.CryptoPrimitive;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import sun.misc.HexDumpEncoder;
import sun.security.timestamp.TimestampToken;
import sun.security.util.ConstraintsParameters;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.util.KeyUtil;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.X500Name;

public class SignerInfo implements DerEncoder {
  private static final Set<CryptoPrimitive> DIGEST_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.MESSAGE_DIGEST));
  
  private static final Set<CryptoPrimitive> SIG_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
  
  private static final DisabledAlgorithmConstraints JAR_DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
  
  BigInteger version = BigInteger.ONE;
  
  X500Name issuerName;
  
  BigInteger certificateSerialNumber;
  
  AlgorithmId digestAlgorithmId;
  
  AlgorithmId digestEncryptionAlgorithmId;
  
  byte[] encryptedDigest;
  
  Timestamp timestamp;
  
  private boolean hasTimestamp = true;
  
  private static final Debug debug = Debug.getInstance("jar");
  
  PKCS9Attributes authenticatedAttributes;
  
  PKCS9Attributes unauthenticatedAttributes;
  
  public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte) {
    this.issuerName = paramX500Name;
    this.certificateSerialNumber = paramBigInteger;
    this.digestAlgorithmId = paramAlgorithmId1;
    this.digestEncryptionAlgorithmId = paramAlgorithmId2;
    this.encryptedDigest = paramArrayOfByte;
  }
  
  public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, PKCS9Attributes paramPKCS9Attributes1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte, PKCS9Attributes paramPKCS9Attributes2) {
    this.issuerName = paramX500Name;
    this.certificateSerialNumber = paramBigInteger;
    this.digestAlgorithmId = paramAlgorithmId1;
    this.authenticatedAttributes = paramPKCS9Attributes1;
    this.digestEncryptionAlgorithmId = paramAlgorithmId2;
    this.encryptedDigest = paramArrayOfByte;
    this.unauthenticatedAttributes = paramPKCS9Attributes2;
  }
  
  public SignerInfo(DerInputStream paramDerInputStream) throws IOException, ParsingException { this(paramDerInputStream, false); }
  
  public SignerInfo(DerInputStream paramDerInputStream, boolean paramBoolean) throws IOException, ParsingException {
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(2);
    byte[] arrayOfByte = arrayOfDerValue[0].toByteArray();
    this.issuerName = new X500Name(new DerValue((byte)48, arrayOfByte));
    this.certificateSerialNumber = arrayOfDerValue[1].getBigInteger();
    DerValue derValue = paramDerInputStream.getDerValue();
    this.digestAlgorithmId = AlgorithmId.parse(derValue);
    if (paramBoolean) {
      paramDerInputStream.getSet(0);
    } else if ((byte)paramDerInputStream.peekByte() == -96) {
      this.authenticatedAttributes = new PKCS9Attributes(paramDerInputStream);
    } 
    derValue = paramDerInputStream.getDerValue();
    this.digestEncryptionAlgorithmId = AlgorithmId.parse(derValue);
    this.encryptedDigest = paramDerInputStream.getOctetString();
    if (paramBoolean) {
      paramDerInputStream.getSet(0);
    } else if (paramDerInputStream.available() != 0 && (byte)paramDerInputStream.peekByte() == -95) {
      this.unauthenticatedAttributes = new PKCS9Attributes(paramDerInputStream, true);
    } 
    if (paramDerInputStream.available() != 0)
      throw new ParsingException("extra data at the end"); 
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { derEncode(paramDerOutputStream); }
  
  public void derEncode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(this.version);
    DerOutputStream derOutputStream2 = new DerOutputStream();
    this.issuerName.encode(derOutputStream2);
    derOutputStream2.putInteger(this.certificateSerialNumber);
    derOutputStream1.write((byte)48, derOutputStream2);
    this.digestAlgorithmId.encode(derOutputStream1);
    if (this.authenticatedAttributes != null)
      this.authenticatedAttributes.encode((byte)-96, derOutputStream1); 
    this.digestEncryptionAlgorithmId.encode(derOutputStream1);
    derOutputStream1.putOctetString(this.encryptedDigest);
    if (this.unauthenticatedAttributes != null)
      this.unauthenticatedAttributes.encode((byte)-95, derOutputStream1); 
    DerOutputStream derOutputStream3 = new DerOutputStream();
    derOutputStream3.write((byte)48, derOutputStream1);
    paramOutputStream.write(derOutputStream3.toByteArray());
  }
  
  public X509Certificate getCertificate(PKCS7 paramPKCS7) throws IOException { return paramPKCS7.getCertificate(this.certificateSerialNumber, this.issuerName); }
  
  public ArrayList<X509Certificate> getCertificateChain(PKCS7 paramPKCS7) throws IOException {
    boolean bool;
    X509Certificate x509Certificate = paramPKCS7.getCertificate(this.certificateSerialNumber, this.issuerName);
    if (x509Certificate == null)
      return null; 
    ArrayList arrayList = new ArrayList();
    arrayList.add(x509Certificate);
    X509Certificate[] arrayOfX509Certificate = paramPKCS7.getCertificates();
    if (arrayOfX509Certificate == null || x509Certificate.getSubjectDN().equals(x509Certificate.getIssuerDN()))
      return arrayList; 
    Principal principal = x509Certificate.getIssuerDN();
    int i = 0;
    do {
      bool = false;
      for (byte b = i; b < arrayOfX509Certificate.length; b++) {
        if (principal.equals(arrayOfX509Certificate[b].getSubjectDN())) {
          arrayList.add(arrayOfX509Certificate[b]);
          if (arrayOfX509Certificate[b].getSubjectDN().equals(arrayOfX509Certificate[b].getIssuerDN())) {
            i = arrayOfX509Certificate.length;
          } else {
            principal = arrayOfX509Certificate[b].getIssuerDN();
            X509Certificate x509Certificate1 = arrayOfX509Certificate[i];
            arrayOfX509Certificate[i] = arrayOfX509Certificate[b];
            arrayOfX509Certificate[b] = x509Certificate1;
            i++;
          } 
          bool = true;
          break;
        } 
      } 
    } while (bool);
    return arrayList;
  }
  
  SignerInfo verify(PKCS7 paramPKCS7, byte[] paramArrayOfByte) throws NoSuchAlgorithmException, SignatureException {
    try {
      byte[] arrayOfByte;
      ContentInfo contentInfo = paramPKCS7.getContentInfo();
      if (paramArrayOfByte == null)
        paramArrayOfByte = contentInfo.getContentBytes(); 
      Timestamp timestamp1 = null;
      try {
        timestamp1 = getTimestamp();
      } catch (Exception exception) {}
      ConstraintsParameters constraintsParameters = new ConstraintsParameters(timestamp1);
      String str1 = getDigestAlgorithmId().getName();
      if (this.authenticatedAttributes == null) {
        arrayOfByte = paramArrayOfByte;
      } else {
        ObjectIdentifier objectIdentifier = (ObjectIdentifier)this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.CONTENT_TYPE_OID);
        if (objectIdentifier == null || !objectIdentifier.equals(contentInfo.contentType))
          return null; 
        byte[] arrayOfByte1 = (byte[])this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.MESSAGE_DIGEST_OID);
        if (arrayOfByte1 == null)
          return null; 
        try {
          JAR_DISABLED_CHECK.permits(str1, constraintsParameters);
        } catch (CertPathValidatorException certPathValidatorException) {
          throw new SignatureException(certPathValidatorException.getMessage(), certPathValidatorException);
        } 
        MessageDigest messageDigest = MessageDigest.getInstance(str1);
        byte[] arrayOfByte2 = messageDigest.digest(paramArrayOfByte);
        if (arrayOfByte1.length != arrayOfByte2.length)
          return null; 
        for (byte b = 0; b < arrayOfByte1.length; b++) {
          if (arrayOfByte1[b] != arrayOfByte2[b])
            return null; 
        } 
        arrayOfByte = this.authenticatedAttributes.getDerEncoding();
      } 
      String str2 = getDigestEncryptionAlgorithmId().getName();
      String str3 = AlgorithmId.getEncAlgFromSigAlg(str2);
      if (str3 != null)
        str2 = str3; 
      String str4 = AlgorithmId.makeSigAlg(str1, str2);
      try {
        JAR_DISABLED_CHECK.permits(str4, constraintsParameters);
      } catch (CertPathValidatorException certPathValidatorException) {
        throw new SignatureException(certPathValidatorException.getMessage(), certPathValidatorException);
      } 
      X509Certificate x509Certificate = getCertificate(paramPKCS7);
      if (x509Certificate == null)
        return null; 
      PublicKey publicKey = x509Certificate.getPublicKey();
      if (!JAR_DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, publicKey))
        throw new SignatureException("Public key check failed. Disabled key used: " + KeyUtil.getKeySize(publicKey) + " bit " + publicKey.getAlgorithm()); 
      if (x509Certificate.hasUnsupportedCriticalExtension())
        throw new SignatureException("Certificate has unsupported critical extension(s)"); 
      boolean[] arrayOfBoolean = x509Certificate.getKeyUsage();
      if (arrayOfBoolean != null) {
        KeyUsageExtension keyUsageExtension;
        try {
          keyUsageExtension = new KeyUsageExtension(arrayOfBoolean);
        } catch (IOException iOException) {
          throw new SignatureException("Failed to parse keyUsage extension");
        } 
        boolean bool1 = keyUsageExtension.get("digital_signature").booleanValue();
        boolean bool2 = keyUsageExtension.get("non_repudiation").booleanValue();
        if (!bool1 && !bool2)
          throw new SignatureException("Key usage restricted: cannot be used for digital signatures"); 
      } 
      Signature signature = Signature.getInstance(str4);
      signature.initVerify(publicKey);
      signature.update(arrayOfByte);
      if (signature.verify(this.encryptedDigest))
        return this; 
    } catch (IOException iOException) {
      throw new SignatureException("IO error verifying signature:\n" + iOException.getMessage());
    } catch (InvalidKeyException invalidKeyException) {
      throw new SignatureException("InvalidKey: " + invalidKeyException.getMessage());
    } 
    return null;
  }
  
  SignerInfo verify(PKCS7 paramPKCS7) throws NoSuchAlgorithmException, SignatureException { return verify(paramPKCS7, null); }
  
  public BigInteger getVersion() { return this.version; }
  
  public X500Name getIssuerName() { return this.issuerName; }
  
  public BigInteger getCertificateSerialNumber() { return this.certificateSerialNumber; }
  
  public AlgorithmId getDigestAlgorithmId() { return this.digestAlgorithmId; }
  
  public PKCS9Attributes getAuthenticatedAttributes() { return this.authenticatedAttributes; }
  
  public AlgorithmId getDigestEncryptionAlgorithmId() { return this.digestEncryptionAlgorithmId; }
  
  public byte[] getEncryptedDigest() { return this.encryptedDigest; }
  
  public PKCS9Attributes getUnauthenticatedAttributes() { return this.unauthenticatedAttributes; }
  
  public PKCS7 getTsToken() throws IOException {
    if (this.unauthenticatedAttributes == null)
      return null; 
    PKCS9Attribute pKCS9Attribute = this.unauthenticatedAttributes.getAttribute(PKCS9Attribute.SIGNATURE_TIMESTAMP_TOKEN_OID);
    return (pKCS9Attribute == null) ? null : new PKCS7((byte[])pKCS9Attribute.getValue());
  }
  
  public Timestamp getTimestamp() throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException {
    if (this.timestamp != null || !this.hasTimestamp)
      return this.timestamp; 
    PKCS7 pKCS7 = getTsToken();
    if (pKCS7 == null) {
      this.hasTimestamp = false;
      return null;
    } 
    byte[] arrayOfByte = pKCS7.getContentInfo().getData();
    SignerInfo[] arrayOfSignerInfo = pKCS7.verify(arrayOfByte);
    ArrayList arrayList = arrayOfSignerInfo[0].getCertificateChain(pKCS7);
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    CertPath certPath = certificateFactory.generateCertPath(arrayList);
    TimestampToken timestampToken = new TimestampToken(arrayOfByte);
    verifyTimestamp(timestampToken);
    this.timestamp = new Timestamp(timestampToken.getDate(), certPath);
    return this.timestamp;
  }
  
  private void verifyTimestamp(TimestampToken paramTimestampToken) throws NoSuchAlgorithmException, SignatureException {
    String str = paramTimestampToken.getHashAlgorithm().getName();
    if (!JAR_DISABLED_CHECK.permits(DIGEST_PRIMITIVE_SET, str, null))
      throw new SignatureException("Timestamp token digest check failed. Disabled algorithm used: " + str); 
    MessageDigest messageDigest = MessageDigest.getInstance(str);
    if (!Arrays.equals(paramTimestampToken.getHashedMessage(), messageDigest.digest(this.encryptedDigest)))
      throw new SignatureException("Signature timestamp (#" + paramTimestampToken.getSerialNumber() + ") generated on " + paramTimestampToken.getDate() + " is inapplicable"); 
    if (debug != null) {
      debug.println();
      debug.println("Detected signature timestamp (#" + paramTimestampToken.getSerialNumber() + ") generated on " + paramTimestampToken.getDate());
      debug.println();
    } 
  }
  
  public String toString() {
    HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
    String str = "";
    str = str + "Signer Info for (issuer): " + this.issuerName + "\n";
    str = str + "\tversion: " + Debug.toHexString(this.version) + "\n";
    str = str + "\tcertificateSerialNumber: " + Debug.toHexString(this.certificateSerialNumber) + "\n";
    str = str + "\tdigestAlgorithmId: " + this.digestAlgorithmId + "\n";
    if (this.authenticatedAttributes != null)
      str = str + "\tauthenticatedAttributes: " + this.authenticatedAttributes + "\n"; 
    str = str + "\tdigestEncryptionAlgorithmId: " + this.digestEncryptionAlgorithmId + "\n";
    str = str + "\tencryptedDigest: \n" + hexDumpEncoder.encodeBuffer(this.encryptedDigest) + "\n";
    if (this.unauthenticatedAttributes != null)
      str = str + "\tunauthenticatedAttributes: " + this.unauthenticatedAttributes + "\n"; 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs\SignerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */