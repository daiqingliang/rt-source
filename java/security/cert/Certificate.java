package java.security.cert;

import java.io.ByteArrayInputStream;
import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;
import sun.security.x509.X509CertImpl;

public abstract class Certificate implements Serializable {
  private static final long serialVersionUID = -3585440601605666277L;
  
  private final String type;
  
  private int hash = -1;
  
  protected Certificate(String paramString) { this.type = paramString; }
  
  public final String getType() { return this.type; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Certificate))
      return false; 
    try {
      byte[] arrayOfByte1 = X509CertImpl.getEncodedInternal(this);
      byte[] arrayOfByte2 = X509CertImpl.getEncodedInternal((Certificate)paramObject);
      return Arrays.equals(arrayOfByte1, arrayOfByte2);
    } catch (CertificateException certificateException) {
      return false;
    } 
  }
  
  public int hashCode() {
    int i = this.hash;
    if (i == -1) {
      try {
        i = Arrays.hashCode(X509CertImpl.getEncodedInternal(this));
      } catch (CertificateException certificateException) {
        i = 0;
      } 
      this.hash = i;
    } 
    return i;
  }
  
  public abstract byte[] getEncoded() throws CertificateEncodingException;
  
  public abstract void verify(PublicKey paramPublicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
  
  public abstract void verify(PublicKey paramPublicKey, String paramString) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
  
  public void verify(PublicKey paramPublicKey, Provider paramProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException { throw new UnsupportedOperationException(); }
  
  public abstract String toString();
  
  public abstract PublicKey getPublicKey();
  
  protected Object writeReplace() throws ObjectStreamException {
    try {
      return new CertificateRep(this.type, getEncoded());
    } catch (CertificateException certificateException) {
      throw new NotSerializableException("java.security.cert.Certificate: " + this.type + ": " + certificateException.getMessage());
    } 
  }
  
  protected static class CertificateRep implements Serializable {
    private static final long serialVersionUID = -8563758940495660020L;
    
    private String type;
    
    private byte[] data;
    
    protected CertificateRep(String param1String, byte[] param1ArrayOfByte) {
      this.type = param1String;
      this.data = param1ArrayOfByte;
    }
    
    protected Object readResolve() throws ObjectStreamException {
      try {
        CertificateFactory certificateFactory = CertificateFactory.getInstance(this.type);
        return certificateFactory.generateCertificate(new ByteArrayInputStream(this.data));
      } catch (CertificateException certificateException) {
        throw new NotSerializableException("java.security.cert.Certificate: " + this.type + ": " + certificateException.getMessage());
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\Certificate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */