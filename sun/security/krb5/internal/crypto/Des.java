package sun.security.krb5.internal.crypto;

import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.KrbCryptoException;

public final class Des {
  private static final String CHARSET = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.krb5.msinterop.des.s2kcharset"));
  
  private static final long[] bad_keys = { 
      72340172838076673L, -72340172838076674L, 2242545357980376863L, -2242545357980376864L, 143554428589179390L, -143554428589179391L, 2296870857142767345L, -2296870857142767346L, 135110050437988849L, -2305315235293957887L, 
      2305315235293957886L, -135110050437988850L, 80784550989267214L, 2234100979542855169L, -2234100979542855170L, -80784550989267215L };
  
  private static final byte[] good_parity = { 
      1, 1, 2, 2, 4, 4, 7, 7, 8, 8, 
      11, 11, 13, 13, 14, 14, 16, 16, 19, 19, 
      21, 21, 22, 22, 25, 25, 26, 26, 28, 28, 
      31, 31, 32, 32, 35, 35, 37, 37, 38, 38, 
      41, 41, 42, 42, 44, 44, 47, 47, 49, 49, 
      50, 50, 52, 52, 55, 55, 56, 56, 59, 59, 
      61, 61, 62, 62, 64, 64, 67, 67, 69, 69, 
      70, 70, 73, 73, 74, 74, 76, 76, 79, 79, 
      81, 81, 82, 82, 84, 84, 87, 87, 88, 88, 
      91, 91, 93, 93, 94, 94, 97, 97, 98, 98, 
      100, 100, 103, 103, 104, 104, 107, 107, 109, 109, 
      110, 110, 112, 112, 115, 115, 117, 117, 118, 118, 
      121, 121, 122, 122, 124, 124, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE, 
      -125, -125, -123, -123, -122, -122, -119, -119, -118, -118, 
      -116, -116, -113, -113, -111, -111, -110, -110, -108, -108, 
      -105, -105, -104, -104, -101, -101, -99, -99, -98, -98, 
      -95, -95, -94, -94, -92, -92, -89, -89, -88, -88, 
      -85, -85, -83, -83, -82, -82, -80, -80, -77, -77, 
      -75, -75, -74, -74, -71, -71, -70, -70, -68, -68, 
      -65, -65, -63, -63, -62, -62, -60, -60, -57, -57, 
      -56, -56, -53, -53, -51, -51, -50, -50, -48, -48, 
      -45, -45, -43, -43, -42, -42, -39, -39, -38, -38, 
      -36, -36, -33, -33, -32, -32, -29, -29, -27, -27, 
      -26, -26, -23, -23, -22, -22, -20, -20, -17, -17, 
      -15, -15, -14, -14, -12, -12, -9, -9, -8, -8, 
      -5, -5, -3, -3, -2, -2 };
  
  public static final byte[] set_parity(byte[] paramArrayOfByte) {
    for (byte b = 0; b < 8; b++)
      paramArrayOfByte[b] = good_parity[paramArrayOfByte[b] & 0xFF]; 
    return paramArrayOfByte;
  }
  
  public static final long set_parity(long paramLong) { return octet2long(set_parity(long2octet(paramLong))); }
  
  public static final boolean bad_key(long paramLong) {
    for (byte b = 0; b < bad_keys.length; b++) {
      if (bad_keys[b] == paramLong)
        return true; 
    } 
    return false;
  }
  
  public static final boolean bad_key(byte[] paramArrayOfByte) { return bad_key(octet2long(paramArrayOfByte)); }
  
  public static long octet2long(byte[] paramArrayOfByte) { return octet2long(paramArrayOfByte, 0); }
  
  public static long octet2long(byte[] paramArrayOfByte, int paramInt) {
    long l = 0L;
    for (int i = 0; i < 8; i++) {
      if (i + paramInt < paramArrayOfByte.length)
        l |= (paramArrayOfByte[i + paramInt] & 0xFFL) << (7 - i) * 8; 
    } 
    return l;
  }
  
  public static byte[] long2octet(long paramLong) {
    byte[] arrayOfByte = new byte[8];
    for (byte b = 0; b < 8; b++)
      arrayOfByte[b] = (byte)(int)(paramLong >>> (7 - b) * 8 & 0xFFL); 
    return arrayOfByte;
  }
  
  public static void long2octet(long paramLong, byte[] paramArrayOfByte) { long2octet(paramLong, paramArrayOfByte, 0); }
  
  public static void long2octet(long paramLong, byte[] paramArrayOfByte, int paramInt) {
    for (int i = 0; i < 8; i++) {
      if (i + paramInt < paramArrayOfByte.length)
        paramArrayOfByte[i + paramInt] = (byte)(int)(paramLong >>> (7 - i) * 8 & 0xFFL); 
    } 
  }
  
  public static void cbc_encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, boolean paramBoolean) throws KrbCryptoException {
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance("DES/CBC/NoPadding");
    } catch (GeneralSecurityException generalSecurityException) {
      KrbCryptoException krbCryptoException = new KrbCryptoException("JCE provider may not be installed. " + generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } 
    IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfByte4);
    SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfByte3, "DES");
    try {
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
      SecretKeySpec secretKeySpec1 = secretKeySpec;
      if (paramBoolean) {
        cipher.init(1, secretKeySpec1, ivParameterSpec);
      } else {
        cipher.init(2, secretKeySpec1, ivParameterSpec);
      } 
      byte[] arrayOfByte = cipher.doFinal(paramArrayOfByte1);
      System.arraycopy(arrayOfByte, 0, paramArrayOfByte2, 0, arrayOfByte.length);
    } catch (GeneralSecurityException generalSecurityException) {
      KrbCryptoException krbCryptoException = new KrbCryptoException(generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } 
  }
  
  public static long char_to_key(char[] paramArrayOfChar) throws KrbCryptoException {
    long l1 = 0L;
    long l2 = 0L;
    byte[] arrayOfByte1 = null;
    try {
      if (CHARSET == null) {
        arrayOfByte1 = (new String(paramArrayOfChar)).getBytes();
      } else {
        arrayOfByte1 = (new String(paramArrayOfChar)).getBytes(CHARSET);
      } 
    } catch (Exception exception) {
      if (arrayOfByte1 != null)
        Arrays.fill(arrayOfByte1, 0, arrayOfByte1.length, (byte)0); 
      KrbCryptoException krbCryptoException = new KrbCryptoException("Unable to convert passwd, " + exception);
      krbCryptoException.initCause(exception);
      throw krbCryptoException;
    } 
    byte[] arrayOfByte2 = pad(arrayOfByte1);
    byte[] arrayOfByte3 = new byte[8];
    int i = arrayOfByte2.length / 8 + ((arrayOfByte2.length % 8 == 0) ? 0 : 1);
    for (byte b = 0; b < i; b++) {
      long l = octet2long(arrayOfByte2, b * 8) & 0x7F7F7F7F7F7F7F7FL;
      if (b % 2 == 1) {
        long l3 = 0L;
        for (byte b1 = 0; b1 < 64; b1++)
          l3 |= (l & 1L << b1) >>> b1 << 63 - b1; 
        l = l3 >>> true;
      } 
      l1 ^= l << true;
    } 
    l1 = set_parity(l1);
    if (bad_key(l1)) {
      byte[] arrayOfByte = long2octet(l1);
      arrayOfByte[7] = (byte)(arrayOfByte[7] ^ 0xF0);
      l1 = octet2long(arrayOfByte);
    } 
    arrayOfByte3 = des_cksum(long2octet(l1), arrayOfByte2, long2octet(l1));
    l1 = octet2long(set_parity(arrayOfByte3));
    if (bad_key(l1)) {
      byte[] arrayOfByte = long2octet(l1);
      arrayOfByte[7] = (byte)(arrayOfByte[7] ^ 0xF0);
      l1 = octet2long(arrayOfByte);
    } 
    if (arrayOfByte1 != null)
      Arrays.fill(arrayOfByte1, 0, arrayOfByte1.length, (byte)0); 
    if (arrayOfByte2 != null)
      Arrays.fill(arrayOfByte2, 0, arrayOfByte2.length, (byte)0); 
    return l1;
  }
  
  public static byte[] des_cksum(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) throws KrbCryptoException {
    Cipher cipher = null;
    byte[] arrayOfByte = new byte[8];
    try {
      cipher = Cipher.getInstance("DES/CBC/NoPadding");
    } catch (Exception exception) {
      KrbCryptoException krbCryptoException = new KrbCryptoException("JCE provider may not be installed. " + exception.getMessage());
      krbCryptoException.initCause(exception);
      throw krbCryptoException;
    } 
    IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfByte1);
    SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfByte3, "DES");
    try {
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
      SecretKeySpec secretKeySpec1 = secretKeySpec;
      cipher.init(1, secretKeySpec1, ivParameterSpec);
      for (byte b = 0; b < paramArrayOfByte2.length / 8; b++) {
        arrayOfByte = cipher.doFinal(paramArrayOfByte2, b * 8, 8);
        cipher.init(1, secretKeySpec1, new IvParameterSpec(arrayOfByte));
      } 
    } catch (GeneralSecurityException generalSecurityException) {
      KrbCryptoException krbCryptoException = new KrbCryptoException(generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } 
    return arrayOfByte;
  }
  
  static byte[] pad(byte[] paramArrayOfByte) {
    int i;
    if (paramArrayOfByte.length < 8) {
      i = paramArrayOfByte.length;
    } else {
      i = paramArrayOfByte.length % 8;
    } 
    if (i == 0)
      return paramArrayOfByte; 
    byte[] arrayOfByte = new byte[8 - i + paramArrayOfByte.length];
    for (int j = arrayOfByte.length - 1; j > paramArrayOfByte.length - 1; j--)
      arrayOfByte[j] = 0; 
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
    return arrayOfByte;
  }
  
  public static byte[] string_to_key_bytes(char[] paramArrayOfChar) throws KrbCryptoException { return long2octet(char_to_key(paramArrayOfChar)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\Des.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */