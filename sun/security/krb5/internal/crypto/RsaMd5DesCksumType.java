package sun.security.krb5.internal.crypto;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import javax.crypto.spec.DESKeySpec;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;

public final class RsaMd5DesCksumType extends CksumType {
  public int confounderSize() { return 8; }
  
  public int cksumType() { return 8; }
  
  public boolean isSafe() { return true; }
  
  public int cksumSize() { return 24; }
  
  public int keyType() { return 1; }
  
  public int keySize() { return 8; }
  
  public byte[] calculateKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2) throws KrbCryptoException {
    byte[] arrayOfByte1 = new byte[paramInt1 + confounderSize()];
    byte[] arrayOfByte2 = Confounder.bytes(confounderSize());
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, confounderSize());
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, confounderSize(), paramInt1);
    byte[] arrayOfByte3 = calculateChecksum(arrayOfByte1, arrayOfByte1.length);
    byte[] arrayOfByte4 = new byte[cksumSize()];
    System.arraycopy(arrayOfByte2, 0, arrayOfByte4, 0, confounderSize());
    System.arraycopy(arrayOfByte3, 0, arrayOfByte4, confounderSize(), cksumSize() - confounderSize());
    byte[] arrayOfByte5 = new byte[keySize()];
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte5, 0, paramArrayOfByte2.length);
    for (b = 0; b < arrayOfByte5.length; b++)
      arrayOfByte5[b] = (byte)(arrayOfByte5[b] ^ 0xF0); 
    try {
      if (DESKeySpec.isWeak(arrayOfByte5, 0))
        arrayOfByte5[7] = (byte)(arrayOfByte5[7] ^ 0xF0); 
    } catch (InvalidKeyException b) {
      InvalidKeyException invalidKeyException;
    } 
    byte[] arrayOfByte6 = new byte[arrayOfByte5.length];
    byte[] arrayOfByte7 = new byte[arrayOfByte4.length];
    Des.cbc_encrypt(arrayOfByte4, arrayOfByte7, arrayOfByte5, arrayOfByte6, true);
    return arrayOfByte7;
  }
  
  public boolean verifyKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2) throws KrbCryptoException {
    byte[] arrayOfByte1 = decryptKeyedChecksum(paramArrayOfByte3, paramArrayOfByte2);
    byte[] arrayOfByte2 = new byte[paramInt1 + confounderSize()];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, confounderSize());
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte2, confounderSize(), paramInt1);
    byte[] arrayOfByte3 = calculateChecksum(arrayOfByte2, arrayOfByte2.length);
    byte[] arrayOfByte4 = new byte[cksumSize() - confounderSize()];
    System.arraycopy(arrayOfByte1, confounderSize(), arrayOfByte4, 0, cksumSize() - confounderSize());
    return isChecksumEqual(arrayOfByte4, arrayOfByte3);
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\RsaMd5DesCksumType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */