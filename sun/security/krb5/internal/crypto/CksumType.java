package sun.security.krb5.internal.crypto;

import sun.security.krb5.Config;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.Krb5;

public abstract class CksumType {
  private static boolean DEBUG = Krb5.DEBUG;
  
  public static CksumType getInstance(int paramInt) throws KdcErrException {
    HmacSha1Des3KdCksumType hmacSha1Des3KdCksumType;
    DesMacKCksumType desMacKCksumType;
    HmacMd5ArcFourCksumType hmacMd5ArcFourCksumType;
    RsaMd5CksumType rsaMd5CksumType;
    DesMacCksumType desMacCksumType;
    HmacSha1Aes128CksumType hmacSha1Aes128CksumType;
    RsaMd5DesCksumType rsaMd5DesCksumType;
    HmacSha1Aes256CksumType hmacSha1Aes256CksumType;
    Crc32CksumType crc32CksumType = null;
    String str = null;
    switch (paramInt) {
      case 1:
        crc32CksumType = new Crc32CksumType();
        str = "sun.security.krb5.internal.crypto.Crc32CksumType";
        break;
      case 4:
        desMacCksumType = new DesMacCksumType();
        str = "sun.security.krb5.internal.crypto.DesMacCksumType";
        break;
      case 5:
        desMacKCksumType = new DesMacKCksumType();
        str = "sun.security.krb5.internal.crypto.DesMacKCksumType";
        break;
      case 7:
        rsaMd5CksumType = new RsaMd5CksumType();
        str = "sun.security.krb5.internal.crypto.RsaMd5CksumType";
        break;
      case 8:
        rsaMd5DesCksumType = new RsaMd5DesCksumType();
        str = "sun.security.krb5.internal.crypto.RsaMd5DesCksumType";
        break;
      case 12:
        hmacSha1Des3KdCksumType = new HmacSha1Des3KdCksumType();
        str = "sun.security.krb5.internal.crypto.HmacSha1Des3KdCksumType";
        break;
      case 15:
        hmacSha1Aes128CksumType = new HmacSha1Aes128CksumType();
        str = "sun.security.krb5.internal.crypto.HmacSha1Aes128CksumType";
        break;
      case 16:
        hmacSha1Aes256CksumType = new HmacSha1Aes256CksumType();
        str = "sun.security.krb5.internal.crypto.HmacSha1Aes256CksumType";
        break;
      case -138:
        hmacMd5ArcFourCksumType = new HmacMd5ArcFourCksumType();
        str = "sun.security.krb5.internal.crypto.HmacMd5ArcFourCksumType";
        break;
      default:
        throw new KdcErrException(15);
    } 
    if (DEBUG)
      System.out.println(">>> CksumType: " + str); 
    return hmacMd5ArcFourCksumType;
  }
  
  public static CksumType getInstance() throws KdcErrException {
    int i = 7;
    try {
      Config config;
      if ((i = (config = Config.getInstance()).getType(config.get(new String[] { "libdefaults", "ap_req_checksum_type" }))) == -1 && (i = Config.getType(config.get(new String[] { "libdefaults", "checksum_type" }))) == -1)
        i = 7; 
    } catch (KrbException krbException) {}
    return getInstance(i);
  }
  
  public abstract int confounderSize();
  
  public abstract int cksumType();
  
  public abstract boolean isSafe();
  
  public abstract int cksumSize();
  
  public abstract int keyType();
  
  public abstract int keySize();
  
  public abstract byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt) throws KrbCryptoException;
  
  public abstract byte[] calculateKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2) throws KrbCryptoException;
  
  public abstract boolean verifyKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2) throws KrbCryptoException;
  
  public static boolean isChecksumEqual(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    if (paramArrayOfByte1 == paramArrayOfByte2)
      return true; 
    if ((paramArrayOfByte1 == null && paramArrayOfByte2 != null) || (paramArrayOfByte1 != null && paramArrayOfByte2 == null))
      return false; 
    if (paramArrayOfByte1.length != paramArrayOfByte2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfByte1.length; b++) {
      if (paramArrayOfByte1[b] != paramArrayOfByte2[b])
        return false; 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\CksumType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */