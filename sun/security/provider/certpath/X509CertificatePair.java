package sun.security.provider.certpath;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import javax.security.auth.x500.X500Principal;
import sun.security.provider.X509Factory;
import sun.security.util.Cache;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.X509CertImpl;

public class X509CertificatePair {
  private static final byte TAG_FORWARD = 0;
  
  private static final byte TAG_REVERSE = 1;
  
  private X509Certificate forward;
  
  private X509Certificate reverse;
  
  private byte[] encoded;
  
  private static final Cache<Object, X509CertificatePair> cache = Cache.newSoftMemoryCache(750);
  
  public X509CertificatePair() {}
  
  public X509CertificatePair(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2) throws CertificateException {
    if (paramX509Certificate1 == null && paramX509Certificate2 == null)
      throw new CertificateException("at least one of certificate pair must be non-null"); 
    this.forward = paramX509Certificate1;
    this.reverse = paramX509Certificate2;
    checkPair();
  }
  
  private X509CertificatePair(byte[] paramArrayOfByte) throws CertificateException {
    try {
      parse(new DerValue(paramArrayOfByte));
      this.encoded = paramArrayOfByte;
    } catch (IOException iOException) {
      throw new CertificateException(iOException.toString());
    } 
    checkPair();
  }
  
  public static void clearCache() { cache.clear(); }
  
  public static X509CertificatePair generateCertificatePair(byte[] paramArrayOfByte) throws CertificateException {
    Cache.EqualByteArray equalByteArray = new Cache.EqualByteArray(paramArrayOfByte);
    X509CertificatePair x509CertificatePair = (X509CertificatePair)cache.get(equalByteArray);
    if (x509CertificatePair != null)
      return x509CertificatePair; 
    x509CertificatePair = new X509CertificatePair(paramArrayOfByte);
    equalByteArray = new Cache.EqualByteArray(x509CertificatePair.encoded);
    cache.put(equalByteArray, x509CertificatePair);
    return x509CertificatePair;
  }
  
  public void setForward(X509Certificate paramX509Certificate) throws CertificateException {
    checkPair();
    this.forward = paramX509Certificate;
  }
  
  public void setReverse(X509Certificate paramX509Certificate) throws CertificateException {
    checkPair();
    this.reverse = paramX509Certificate;
  }
  
  public X509Certificate getForward() { return this.forward; }
  
  public X509Certificate getReverse() { return this.reverse; }
  
  public byte[] getEncoded() throws CertificateEncodingException {
    try {
      if (this.encoded == null) {
        DerOutputStream derOutputStream = new DerOutputStream();
        emit(derOutputStream);
        this.encoded = derOutputStream.toByteArray();
      } 
    } catch (IOException iOException) {
      throw new CertificateEncodingException(iOException.toString());
    } 
    return this.encoded;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("X.509 Certificate Pair: [\n");
    if (this.forward != null)
      stringBuilder.append("  Forward: ").append(this.forward).append("\n"); 
    if (this.reverse != null)
      stringBuilder.append("  Reverse: ").append(this.reverse).append("\n"); 
    stringBuilder.append("]");
    return stringBuilder.toString();
  }
  
  private void parse(DerValue paramDerValue) throws IOException, CertificateException {
    if (paramDerValue.tag != 48)
      throw new IOException("Sequence tag missing for X509CertificatePair"); 
    while (paramDerValue.data != null && paramDerValue.data.available() != 0) {
      DerValue derValue = paramDerValue.data.getDerValue();
      short s = (short)(byte)(derValue.tag & 0x1F);
      switch (s) {
        case 0:
          if (derValue.isContextSpecific() && derValue.isConstructed()) {
            if (this.forward != null)
              throw new IOException("Duplicate forward certificate in X509CertificatePair"); 
            derValue = derValue.data.getDerValue();
            this.forward = X509Factory.intern(new X509CertImpl(derValue.toByteArray()));
          } 
          continue;
        case 1:
          if (derValue.isContextSpecific() && derValue.isConstructed()) {
            if (this.reverse != null)
              throw new IOException("Duplicate reverse certificate in X509CertificatePair"); 
            derValue = derValue.data.getDerValue();
            this.reverse = X509Factory.intern(new X509CertImpl(derValue.toByteArray()));
          } 
          continue;
      } 
      throw new IOException("Invalid encoding of X509CertificatePair");
    } 
    if (this.forward == null && this.reverse == null)
      throw new CertificateException("at least one of certificate pair must be non-null"); 
  }
  
  private void emit(DerOutputStream paramDerOutputStream) throws IOException, CertificateEncodingException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.forward != null) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      derOutputStream1.putDerValue(new DerValue(this.forward.getEncoded()));
      derOutputStream.write(DerValue.createTag(-128, true, (byte)0), derOutputStream1);
    } 
    if (this.reverse != null) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      derOutputStream1.putDerValue(new DerValue(this.reverse.getEncoded()));
      derOutputStream.write(DerValue.createTag(-128, true, (byte)1), derOutputStream1);
    } 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  private void checkPair() {
    if (this.forward == null || this.reverse == null)
      return; 
    X500Principal x500Principal1 = this.forward.getSubjectX500Principal();
    X500Principal x500Principal2 = this.forward.getIssuerX500Principal();
    X500Principal x500Principal3 = this.reverse.getSubjectX500Principal();
    X500Principal x500Principal4 = this.reverse.getIssuerX500Principal();
    if (!x500Principal2.equals(x500Principal3) || !x500Principal4.equals(x500Principal1))
      throw new CertificateException("subject and issuer names in forward and reverse certificates do not match"); 
    try {
      PublicKey publicKey = this.reverse.getPublicKey();
      if (!(publicKey instanceof DSAPublicKey) || ((DSAPublicKey)publicKey).getParams() != null)
        this.forward.verify(publicKey); 
      publicKey = this.forward.getPublicKey();
      if (!(publicKey instanceof DSAPublicKey) || ((DSAPublicKey)publicKey).getParams() != null)
        this.reverse.verify(publicKey); 
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CertificateException("invalid signature: " + generalSecurityException.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\X509CertificatePair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */