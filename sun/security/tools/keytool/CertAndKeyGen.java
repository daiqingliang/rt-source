package sun.security.tools.keytool;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import sun.security.x509.X509Key;

public final class CertAndKeyGen {
  private SecureRandom prng;
  
  private String sigAlg;
  
  private KeyPairGenerator keyGen;
  
  private PublicKey publicKey;
  
  private PrivateKey privateKey;
  
  public CertAndKeyGen(String paramString1, String paramString2) throws NoSuchAlgorithmException {
    this.keyGen = KeyPairGenerator.getInstance(paramString1);
    this.sigAlg = paramString2;
  }
  
  public CertAndKeyGen(String paramString1, String paramString2, String paramString3) throws NoSuchAlgorithmException, NoSuchProviderException {
    if (paramString3 == null) {
      this.keyGen = KeyPairGenerator.getInstance(paramString1);
    } else {
      try {
        this.keyGen = KeyPairGenerator.getInstance(paramString1, paramString3);
      } catch (Exception exception) {
        this.keyGen = KeyPairGenerator.getInstance(paramString1);
      } 
    } 
    this.sigAlg = paramString2;
  }
  
  public void setRandom(SecureRandom paramSecureRandom) { this.prng = paramSecureRandom; }
  
  public void generate(int paramInt) throws InvalidKeyException {
    KeyPair keyPair;
    try {
      if (this.prng == null)
        this.prng = new SecureRandom(); 
      this.keyGen.initialize(paramInt, this.prng);
      keyPair = this.keyGen.generateKeyPair();
    } catch (Exception exception) {
      throw new IllegalArgumentException(exception.getMessage());
    } 
    this.publicKey = keyPair.getPublic();
    this.privateKey = keyPair.getPrivate();
    if (!"X.509".equalsIgnoreCase(this.publicKey.getFormat()))
      throw new IllegalArgumentException("publicKey's is not X.509, but " + this.publicKey.getFormat()); 
  }
  
  public X509Key getPublicKey() { return !(this.publicKey instanceof X509Key) ? null : (X509Key)this.publicKey; }
  
  public PublicKey getPublicKeyAnyway() { return this.publicKey; }
  
  public PrivateKey getPrivateKey() { return this.privateKey; }
  
  public X509Certificate getSelfCertificate(X500Name paramX500Name, Date paramDate, long paramLong) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException { return getSelfCertificate(paramX500Name, paramDate, paramLong, null); }
  
  public X509Certificate getSelfCertificate(X500Name paramX500Name, Date paramDate, long paramLong, CertificateExtensions paramCertificateExtensions) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
    try {
      Date date = new Date();
      date.setTime(paramDate.getTime() + paramLong * 1000L);
      CertificateValidity certificateValidity = new CertificateValidity(paramDate, date);
      X509CertInfo x509CertInfo = new X509CertInfo();
      x509CertInfo.set("version", new CertificateVersion(2));
      x509CertInfo.set("serialNumber", new CertificateSerialNumber((new Random()).nextInt() & 0x7FFFFFFF));
      AlgorithmId algorithmId = AlgorithmId.get(this.sigAlg);
      x509CertInfo.set("algorithmID", new CertificateAlgorithmId(algorithmId));
      x509CertInfo.set("subject", paramX500Name);
      x509CertInfo.set("key", new CertificateX509Key(this.publicKey));
      x509CertInfo.set("validity", certificateValidity);
      x509CertInfo.set("issuer", paramX500Name);
      if (paramCertificateExtensions != null)
        x509CertInfo.set("extensions", paramCertificateExtensions); 
      X509CertImpl x509CertImpl = new X509CertImpl(x509CertInfo);
      x509CertImpl.sign(this.privateKey, this.sigAlg);
      return x509CertImpl;
    } catch (IOException iOException) {
      throw new CertificateEncodingException("getSelfCert: " + iOException.getMessage());
    } 
  }
  
  public X509Certificate getSelfCertificate(X500Name paramX500Name, long paramLong) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException { return getSelfCertificate(paramX500Name, new Date(), paramLong); }
  
  public PKCS10 getCertRequest(X500Name paramX500Name) throws InvalidKeyException, SignatureException {
    PKCS10 pKCS10 = new PKCS10(this.publicKey);
    try {
      Signature signature = Signature.getInstance(this.sigAlg);
      signature.initSign(this.privateKey);
      pKCS10.encodeAndSign(paramX500Name, signature);
    } catch (CertificateException certificateException) {
      throw new SignatureException(this.sigAlg + " CertificateException");
    } catch (IOException iOException) {
      throw new SignatureException(this.sigAlg + " IOException");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new SignatureException(this.sigAlg + " unavailable?");
    } 
    return pKCS10;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\keytool\CertAndKeyGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */