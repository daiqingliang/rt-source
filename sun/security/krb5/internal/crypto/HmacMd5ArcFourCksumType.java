package sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;

public class HmacMd5ArcFourCksumType extends CksumType {
  public int confounderSize() { return 8; }
  
  public int cksumType() { return -138; }
  
  public boolean isSafe() { return true; }
  
  public int cksumSize() { return 16; }
  
  public int keyType() { return 4; }
  
  public int keySize() { return 16; }
  
  public byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt) { return null; }
  
  public byte[] calculateKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2) throws KrbCryptoException {
    try {
      return ArcFourHmac.calculateChecksum(paramArrayOfByte2, paramInt2, paramArrayOfByte1, 0, paramInt1);
    } catch (GeneralSecurityException generalSecurityException) {
      KrbCryptoException krbCryptoException = new KrbCryptoException(generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } 
  }
  
  public boolean verifyKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2) throws KrbCryptoException {
    try {
      byte[] arrayOfByte = ArcFourHmac.calculateChecksum(paramArrayOfByte2, paramInt2, paramArrayOfByte1, 0, paramInt1);
      return isChecksumEqual(paramArrayOfByte3, arrayOfByte);
    } catch (GeneralSecurityException generalSecurityException) {
      KrbCryptoException krbCryptoException = new KrbCryptoException(generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\HmacMd5ArcFourCksumType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */