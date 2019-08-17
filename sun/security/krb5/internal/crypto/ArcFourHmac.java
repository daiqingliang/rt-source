package sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.crypto.dk.ArcFourCrypto;

public class ArcFourHmac {
  private static final ArcFourCrypto CRYPTO = new ArcFourCrypto(128);
  
  public static byte[] stringToKey(char[] paramArrayOfChar) throws GeneralSecurityException { return CRYPTO.stringToKey(paramArrayOfChar); }
  
  public static int getChecksumLength() { return CRYPTO.getChecksumLength(); }
  
  public static byte[] calculateChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3) throws GeneralSecurityException { return CRYPTO.calculateChecksum(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramInt2, paramInt3); }
  
  public static byte[] encryptSeq(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException { return CRYPTO.encryptSeq(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3); }
  
  public static byte[] decryptSeq(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException { return CRYPTO.decryptSeq(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3); }
  
  public static byte[] encrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException { return CRYPTO.encrypt(paramArrayOfByte1, paramInt1, paramArrayOfByte2, null, paramArrayOfByte3, paramInt2, paramInt3); }
  
  public static byte[] encryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException { return CRYPTO.encryptRaw(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3); }
  
  public static byte[] decrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException { return CRYPTO.decrypt(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3); }
  
  public static byte[] decryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3, byte[] paramArrayOfByte4) throws GeneralSecurityException { return CRYPTO.decryptRaw(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3, paramArrayOfByte4); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\ArcFourHmac.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */