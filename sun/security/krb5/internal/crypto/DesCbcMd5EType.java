package sun.security.krb5.internal.crypto;

import java.security.MessageDigest;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.KrbApErrException;

public final class DesCbcMd5EType extends DesCbcEType {
  public int eType() { return 3; }
  
  public int minimumPadSize() { return 0; }
  
  public int confounderSize() { return 8; }
  
  public int checksumType() { return 7; }
  
  public int checksumSize() { return 16; }
  
  protected byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt) throws KrbCryptoException {
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
    } catch (Exception exception) {
      throw new KrbCryptoException("JCE provider may not be installed. " + exception.getMessage());
    } 
    try {
      messageDigest.update(paramArrayOfByte);
      return messageDigest.digest();
    } catch (Exception exception) {
      throw new KrbCryptoException(exception.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\DesCbcMd5EType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */