package javax.security.cert;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;

public abstract class Certificate {
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Certificate))
      return false; 
    try {
      byte[] arrayOfByte1 = getEncoded();
      byte[] arrayOfByte2 = ((Certificate)paramObject).getEncoded();
      if (arrayOfByte1.length != arrayOfByte2.length)
        return false; 
      for (byte b = 0; b < arrayOfByte1.length; b++) {
        if (arrayOfByte1[b] != arrayOfByte2[b])
          return false; 
      } 
      return true;
    } catch (CertificateException certificateException) {
      return false;
    } 
  }
  
  public int hashCode() {
    byte b = 0;
    try {
      byte[] arrayOfByte = getEncoded();
      for (byte b1 = 1; b1 < arrayOfByte.length; b1++)
        b += arrayOfByte[b1] * b1; 
      return b;
    } catch (CertificateException certificateException) {
      return b;
    } 
  }
  
  public abstract byte[] getEncoded() throws CertificateEncodingException;
  
  public abstract void verify(PublicKey paramPublicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
  
  public abstract void verify(PublicKey paramPublicKey, String paramString) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
  
  public abstract String toString();
  
  public abstract PublicKey getPublicKey();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\cert\Certificate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */