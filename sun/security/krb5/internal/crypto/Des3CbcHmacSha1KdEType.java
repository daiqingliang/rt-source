package sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;

public final class Des3CbcHmacSha1KdEType extends EType {
  public int eType() { return 16; }
  
  public int minimumPadSize() { return 0; }
  
  public int confounderSize() { return blockSize(); }
  
  public int checksumType() { return 12; }
  
  public int checksumSize() { return Des3.getChecksumLength(); }
  
  public int blockSize() { return 8; }
  
  public int keyType() { return 2; }
  
  public int keySize() { return 24; }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbCryptoException {
    byte[] arrayOfByte = new byte[blockSize()];
    return encrypt(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt);
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) throws KrbCryptoException {
    try {
      return Des3.encrypt(paramArrayOfByte2, paramInt, paramArrayOfByte3, paramArrayOfByte1, 0, paramArrayOfByte1.length);
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
      return Des3.decrypt(paramArrayOfByte2, paramInt, paramArrayOfByte3, paramArrayOfByte1, 0, paramArrayOfByte1.length);
    } catch (GeneralSecurityException generalSecurityException) {
      KrbCryptoException krbCryptoException = new KrbCryptoException(generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } 
  }
  
  public byte[] decryptedData(byte[] paramArrayOfByte) { return paramArrayOfByte; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\Des3CbcHmacSha1KdEType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */