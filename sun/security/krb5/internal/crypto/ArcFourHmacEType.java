package sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;

public final class ArcFourHmacEType extends EType {
  public int eType() { return 23; }
  
  public int minimumPadSize() { return 1; }
  
  public int confounderSize() { return 8; }
  
  public int checksumType() { return -138; }
  
  public int checksumSize() { return ArcFourHmac.getChecksumLength(); }
  
  public int blockSize() { return 1; }
  
  public int keyType() { return 4; }
  
  public int keySize() { return 16; }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbCryptoException {
    byte[] arrayOfByte = new byte[blockSize()];
    return encrypt(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt);
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) throws KrbCryptoException {
    try {
      return ArcFourHmac.encrypt(paramArrayOfByte2, paramInt, paramArrayOfByte3, paramArrayOfByte1, 0, paramArrayOfByte1.length);
    } catch (GeneralSecurityException generalSecurityException) {
      KrbCryptoException krbCryptoException = new KrbCryptoException(generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } 
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbCryptoException {
    byte[] arrayOfByte = new byte[blockSize()];
    return decrypt(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt);
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) throws KrbCryptoException {
    try {
      return ArcFourHmac.decrypt(paramArrayOfByte2, paramInt, paramArrayOfByte3, paramArrayOfByte1, 0, paramArrayOfByte1.length);
    } catch (GeneralSecurityException generalSecurityException) {
      KrbCryptoException krbCryptoException = new KrbCryptoException(generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } 
  }
  
  public byte[] decryptedData(byte[] paramArrayOfByte) { return paramArrayOfByte; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\ArcFourHmacEType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */