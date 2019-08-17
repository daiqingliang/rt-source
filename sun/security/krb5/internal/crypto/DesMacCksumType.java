package sun.security.krb5.internal.crypto;

import java.security.InvalidKeyException;
import javax.crypto.spec.DESKeySpec;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;

public class DesMacCksumType extends CksumType {
  public int confounderSize() { return 8; }
  
  public int cksumType() { return 4; }
  
  public boolean isSafe() { return true; }
  
  public int cksumSize() { return 16; }
  
  public int keyType() { return 1; }
  
  public int keySize() { return 8; }
  
  public byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt) { return null; }
  
  public byte[] calculateKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2) throws KrbCryptoException {
    byte[] arrayOfByte1 = new byte[paramInt1 + confounderSize()];
    byte[] arrayOfByte2 = Confounder.bytes(confounderSize());
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, confounderSize());
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, confounderSize(), paramInt1);
    try {
      if (DESKeySpec.isWeak(paramArrayOfByte2, 0))
        paramArrayOfByte2[7] = (byte)(paramArrayOfByte2[7] ^ 0xF0); 
    } catch (InvalidKeyException invalidKeyException) {}
    byte[] arrayOfByte3 = new byte[paramArrayOfByte2.length];
    byte[] arrayOfByte4 = Des.des_cksum(arrayOfByte3, arrayOfByte1, paramArrayOfByte2);
    byte[] arrayOfByte5 = new byte[cksumSize()];
    System.arraycopy(arrayOfByte2, 0, arrayOfByte5, 0, confounderSize());
    System.arraycopy(arrayOfByte4, 0, arrayOfByte5, confounderSize(), cksumSize() - confounderSize());
    byte[] arrayOfByte6 = new byte[keySize()];
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte6, 0, paramArrayOfByte2.length);
    for (b = 0; b < arrayOfByte6.length; b++)
      arrayOfByte6[b] = (byte)(arrayOfByte6[b] ^ 0xF0); 
    try {
      if (DESKeySpec.isWeak(arrayOfByte6, 0))
        arrayOfByte6[7] = (byte)(arrayOfByte6[7] ^ 0xF0); 
    } catch (InvalidKeyException b) {
      InvalidKeyException invalidKeyException;
    } 
    byte[] arrayOfByte7 = new byte[arrayOfByte6.length];
    byte[] arrayOfByte8 = new byte[arrayOfByte5.length];
    Des.cbc_encrypt(arrayOfByte5, arrayOfByte8, arrayOfByte6, arrayOfByte7, true);
    return arrayOfByte8;
  }
  
  public boolean verifyKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2) throws KrbCryptoException {
    byte[] arrayOfByte1 = decryptKeyedChecksum(paramArrayOfByte3, paramArrayOfByte2);
    byte[] arrayOfByte2 = new byte[paramInt1 + confounderSize()];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, confounderSize());
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte2, confounderSize(), paramInt1);
    try {
      if (DESKeySpec.isWeak(paramArrayOfByte2, 0))
        paramArrayOfByte2[7] = (byte)(paramArrayOfByte2[7] ^ 0xF0); 
    } catch (InvalidKeyException invalidKeyException) {}
    byte[] arrayOfByte3 = new byte[paramArrayOfByte2.length];
    byte[] arrayOfByte4 = Des.des_cksum(arrayOfByte3, arrayOfByte2, paramArrayOfByte2);
    byte[] arrayOfByte5 = new byte[cksumSize() - confounderSize()];
    System.arraycopy(arrayOfByte1, confounderSize(), arrayOfByte5, 0, cksumSize() - confounderSize());
    return isChecksumEqual(arrayOfByte5, arrayOfByte4);
  }
  
  private byte[] decryptKeyedChecksum(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws KrbCryptoException {
    byte[] arrayOfByte1 = new byte[keySize()];
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte1, 0, paramArrayOfByte2.length);
    for (b = 0; b < arrayOfByte1.length; b++)
      arrayOfByte1[b] = (byte)(arrayOfByte1[b] ^ 0xF0); 
    try {
      if (DESKeySpec.isWeak(arrayOfByte1, 0))
        arrayOfByte1[7] = (byte)(arrayOfByte1[7] ^ 0xF0); 
    } catch (InvalidKeyException b) {
      InvalidKeyException invalidKeyException;
    } 
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length];
    byte[] arrayOfByte3 = new byte[paramArrayOfByte1.length];
    Des.cbc_encrypt(paramArrayOfByte1, arrayOfByte3, arrayOfByte1, arrayOfByte2, false);
    return arrayOfByte3;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\DesMacCksumType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */