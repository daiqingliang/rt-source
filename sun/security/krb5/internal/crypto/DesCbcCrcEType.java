package sun.security.krb5.internal.crypto;

import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.KrbApErrException;

public class DesCbcCrcEType extends DesCbcEType {
  public int eType() { return 1; }
  
  public int minimumPadSize() { return 4; }
  
  public int confounderSize() { return 8; }
  
  public int checksumType() { return 1; }
  
  public int checksumSize() { return 4; }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbCryptoException { return encrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte2, paramInt); }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbCryptoException { return decrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte2, paramInt); }
  
  protected byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt) { return crc32.byte2crc32sum_bytes(paramArrayOfByte, paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\DesCbcCrcEType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */