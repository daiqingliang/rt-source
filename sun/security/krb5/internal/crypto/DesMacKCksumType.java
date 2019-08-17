package sun.security.krb5.internal.crypto;

import java.security.InvalidKeyException;
import javax.crypto.spec.DESKeySpec;
import sun.security.krb5.KrbCryptoException;

public class DesMacKCksumType extends CksumType {
  public int confounderSize() { return 0; }
  
  public int cksumType() { return 5; }
  
  public boolean isSafe() { return true; }
  
  public int cksumSize() { return 16; }
  
  public int keyType() { return 1; }
  
  public int keySize() { return 8; }
  
  public byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt) { return null; }
  
  public byte[] calculateKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2) throws KrbCryptoException {
    try {
      if (DESKeySpec.isWeak(paramArrayOfByte2, 0))
        paramArrayOfByte2[7] = (byte)(paramArrayOfByte2[7] ^ 0xF0); 
    } catch (InvalidKeyException invalidKeyException) {}
    byte[] arrayOfByte = new byte[paramArrayOfByte2.length];
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, 0, paramArrayOfByte2.length);
    return Des.des_cksum(arrayOfByte, paramArrayOfByte1, paramArrayOfByte2);
  }
  
  public boolean verifyKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2) throws KrbCryptoException {
    byte[] arrayOfByte = calculateKeyedChecksum(paramArrayOfByte1, paramArrayOfByte1.length, paramArrayOfByte2, paramInt2);
    return isChecksumEqual(paramArrayOfByte3, arrayOfByte);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\DesMacKCksumType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */