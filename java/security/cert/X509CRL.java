package java.security.cert;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.X509CRLImpl;

public abstract class X509CRL extends CRL implements X509Extension {
  private X500Principal issuerPrincipal;
  
  protected X509CRL() { super("X.509"); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof X509CRL))
      return false; 
    try {
      byte[] arrayOfByte1 = X509CRLImpl.getEncodedInternal(this);
      byte[] arrayOfByte2 = X509CRLImpl.getEncodedInternal((X509CRL)paramObject);
      return Arrays.equals(arrayOfByte1, arrayOfByte2);
    } catch (CRLException cRLException) {
      return false;
    } 
  }
  
  public int hashCode() {
    byte b = 0;
    try {
      byte[] arrayOfByte = X509CRLImpl.getEncodedInternal(this);
      for (byte b1 = 1; b1 < arrayOfByte.length; b1++)
        b += arrayOfByte[b1] * b1; 
      return b;
    } catch (CRLException cRLException) {
      return b;
    } 
  }
  
  public abstract byte[] getEncoded() throws CRLException;
  
  public abstract void verify(PublicKey paramPublicKey) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
  
  public abstract void verify(PublicKey paramPublicKey, String paramString) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
  
  public void verify(PublicKey paramPublicKey, Provider paramProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException { X509CRLImpl.verify(this, paramPublicKey, paramProvider); }
  
  public abstract int getVersion();
  
  public abstract Principal getIssuerDN();
  
  public X500Principal getIssuerX500Principal() {
    if (this.issuerPrincipal == null)
      this.issuerPrincipal = X509CRLImpl.getIssuerX500Principal(this); 
    return this.issuerPrincipal;
  }
  
  public abstract Date getThisUpdate();
  
  public abstract Date getNextUpdate();
  
  public abstract X509CRLEntry getRevokedCertificate(BigInteger paramBigInteger);
  
  public X509CRLEntry getRevokedCertificate(X509Certificate paramX509Certificate) {
    X500Principal x500Principal1 = paramX509Certificate.getIssuerX500Principal();
    X500Principal x500Principal2 = getIssuerX500Principal();
    return !x500Principal1.equals(x500Principal2) ? null : getRevokedCertificate(paramX509Certificate.getSerialNumber());
  }
  
  public abstract Set<? extends X509CRLEntry> getRevokedCertificates();
  
  public abstract byte[] getTBSCertList() throws CRLException;
  
  public abstract byte[] getSignature() throws CRLException;
  
  public abstract String getSigAlgName();
  
  public abstract String getSigAlgOID();
  
  public abstract byte[] getSigAlgParams() throws CRLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\X509CRL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */