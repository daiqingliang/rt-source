package sun.security.krb5.internal.crypto;

import java.security.MessageDigest;
import sun.security.krb5.KrbCryptoException;

public final class RsaMd5CksumType extends CksumType {
  public int confounderSize() { return 0; }
  
  public int cksumType() { return 7; }
  
  public boolean isSafe() { return false; }
  
  public int cksumSize() { return 16; }
  
  public int keyType() { return 0; }
  
  public int keySize() { return 0; }
  
  public byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt) throws KrbCryptoException {
    MessageDigest messageDigest;
    byte[] arrayOfByte = null;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
    } catch (Exception exception) {
      throw new KrbCryptoException("JCE provider may not be installed. " + exception.getMessage());
    } 
    try {
      messageDigest.update(paramArrayOfByte);
      arrayOfByte = messageDigest.digest();
    } catch (Exception exception) {
      throw new KrbCryptoException(exception.getMessage());
    } 
    return arrayOfByte;
  }
  
  public byte[] calculateKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2) throws KrbCryptoException { return null; }
  
  public boolean verifyKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2) throws KrbCryptoException { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\RsaMd5CksumType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */