package sun.security.krb5.internal.crypto;

import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.Cipher;
import sun.security.krb5.Config;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.Krb5;

public abstract class EType {
  private static final boolean DEBUG = Krb5.DEBUG;
  
  private static boolean allowWeakCrypto;
  
  private static final int[] BUILTIN_ETYPES;
  
  private static final int[] BUILTIN_ETYPES_NOAES256;
  
  public static void initStatic() {
    boolean bool = false;
    try {
      Config config = Config.getInstance();
      String str = config.get(new String[] { "libdefaults", "allow_weak_crypto" });
      if (str != null && str.equals("true"))
        bool = true; 
    } catch (Exception exception) {
      if (DEBUG)
        System.out.println("Exception in getting allow_weak_crypto, using default value " + exception.getMessage()); 
    } 
    allowWeakCrypto = bool;
  }
  
  public static EType getInstance(int paramInt) throws KdcErrException {
    String str2;
    Aes128CtsHmacSha1EType aes128CtsHmacSha1EType;
    Des3CbcHmacSha1KdEType des3CbcHmacSha1KdEType;
    DesCbcMd5EType desCbcMd5EType;
    Aes256CtsHmacSha1EType aes256CtsHmacSha1EType;
    ArcFourHmacEType arcFourHmacEType;
    DesCbcCrcEType desCbcCrcEType;
    NullEType nullEType = null;
    String str1 = null;
    switch (paramInt) {
      case 0:
        nullEType = new NullEType();
        str1 = "sun.security.krb5.internal.crypto.NullEType";
        break;
      case 1:
        desCbcCrcEType = new DesCbcCrcEType();
        str1 = "sun.security.krb5.internal.crypto.DesCbcCrcEType";
        break;
      case 3:
        desCbcMd5EType = new DesCbcMd5EType();
        str1 = "sun.security.krb5.internal.crypto.DesCbcMd5EType";
        break;
      case 16:
        des3CbcHmacSha1KdEType = new Des3CbcHmacSha1KdEType();
        str1 = "sun.security.krb5.internal.crypto.Des3CbcHmacSha1KdEType";
        break;
      case 17:
        aes128CtsHmacSha1EType = new Aes128CtsHmacSha1EType();
        str1 = "sun.security.krb5.internal.crypto.Aes128CtsHmacSha1EType";
        break;
      case 18:
        aes256CtsHmacSha1EType = new Aes256CtsHmacSha1EType();
        str1 = "sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType";
        break;
      case 23:
        arcFourHmacEType = new ArcFourHmacEType();
        str1 = "sun.security.krb5.internal.crypto.ArcFourHmacEType";
        break;
      default:
        str2 = "encryption type = " + toString(paramInt) + " (" + paramInt + ")";
        throw new KdcErrException(14, str2);
    } 
    if (DEBUG)
      System.out.println(">>> EType: " + str1); 
    return arcFourHmacEType;
  }
  
  public abstract int eType();
  
  public abstract int minimumPadSize();
  
  public abstract int confounderSize();
  
  public abstract int checksumType();
  
  public abstract int checksumSize();
  
  public abstract int blockSize();
  
  public abstract int keyType();
  
  public abstract int keySize();
  
  public abstract byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbCryptoException;
  
  public abstract byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) throws KrbCryptoException;
  
  public abstract byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbCryptoException;
  
  public abstract byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) throws KrbCryptoException;
  
  public int dataSize(byte[] paramArrayOfByte) { return paramArrayOfByte.length - startOfData(); }
  
  public int padSize(byte[] paramArrayOfByte) { return paramArrayOfByte.length - confounderSize() - checksumSize() - dataSize(paramArrayOfByte); }
  
  public int startOfChecksum() { return confounderSize(); }
  
  public int startOfData() { return confounderSize() + checksumSize(); }
  
  public int startOfPad(byte[] paramArrayOfByte) { return confounderSize() + checksumSize() + dataSize(paramArrayOfByte); }
  
  public byte[] decryptedData(byte[] paramArrayOfByte) {
    int i = dataSize(paramArrayOfByte);
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(paramArrayOfByte, startOfData(), arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public static int[] getBuiltInDefaults() {
    int[] arrayOfInt;
    int i = 0;
    try {
      i = Cipher.getMaxAllowedKeyLength("AES");
    } catch (Exception null) {}
    if (i < 256) {
      arrayOfInt = BUILTIN_ETYPES_NOAES256;
    } else {
      arrayOfInt = BUILTIN_ETYPES;
    } 
    return !allowWeakCrypto ? Arrays.copyOfRange(arrayOfInt, 0, arrayOfInt.length - 2) : arrayOfInt;
  }
  
  public static int[] getDefaults(String paramString) throws KrbException {
    Config config = null;
    try {
      config = Config.getInstance();
    } catch (KrbException krbException) {
      if (DEBUG) {
        System.out.println("Exception while getting " + paramString + krbException.getMessage());
        System.out.println("Using default builtin etypes");
      } 
      return getBuiltInDefaults();
    } 
    return config.defaultEtype(paramString);
  }
  
  public static int[] getDefaults(String paramString, EncryptionKey[] paramArrayOfEncryptionKey) throws KrbException {
    int[] arrayOfInt = getDefaults(paramString);
    ArrayList arrayList = new ArrayList(arrayOfInt.length);
    int i;
    for (i = 0; i < arrayOfInt.length; i++) {
      if (EncryptionKey.findKey(arrayOfInt[i], paramArrayOfEncryptionKey) != null)
        arrayList.add(Integer.valueOf(arrayOfInt[i])); 
    } 
    i = arrayList.size();
    if (i <= 0) {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b1 = 0; b1 < paramArrayOfEncryptionKey.length; b1++) {
        stringBuffer.append(toString(paramArrayOfEncryptionKey[b1].getEType()));
        stringBuffer.append(" ");
      } 
      throw new KrbException("Do not have keys of types listed in " + paramString + " available; only have keys of following type: " + stringBuffer.toString());
    } 
    arrayOfInt = new int[i];
    for (byte b = 0; b < i; b++)
      arrayOfInt[b] = ((Integer)arrayList.get(b)).intValue(); 
    return arrayOfInt;
  }
  
  public static boolean isSupported(int paramInt, int[] paramArrayOfInt) {
    for (byte b = 0; b < paramArrayOfInt.length; b++) {
      if (paramInt == paramArrayOfInt[b])
        return true; 
    } 
    return false;
  }
  
  public static boolean isSupported(int paramInt) {
    int[] arrayOfInt = getBuiltInDefaults();
    return isSupported(paramInt, arrayOfInt);
  }
  
  public static String toString(int paramInt) {
    switch (paramInt) {
      case 0:
        return "NULL";
      case 1:
        return "DES CBC mode with CRC-32";
      case 2:
        return "DES CBC mode with MD4";
      case 3:
        return "DES CBC mode with MD5";
      case 4:
        return "reserved";
      case 5:
        return "DES3 CBC mode with MD5";
      case 6:
        return "reserved";
      case 7:
        return "DES3 CBC mode with SHA1";
      case 9:
        return "DSA with SHA1- Cms0ID";
      case 10:
        return "MD5 with RSA encryption - Cms0ID";
      case 11:
        return "SHA1 with RSA encryption - Cms0ID";
      case 12:
        return "RC2 CBC mode with Env0ID";
      case 13:
        return "RSA encryption with Env0ID";
      case 14:
        return "RSAES-0AEP-ENV-0ID";
      case 15:
        return "DES-EDE3-CBC-ENV-0ID";
      case 16:
        return "DES3 CBC mode with SHA1-KD";
      case 17:
        return "AES128 CTS mode with HMAC SHA1-96";
      case 18:
        return "AES256 CTS mode with HMAC SHA1-96";
      case 23:
        return "RC4 with HMAC";
      case 24:
        return "RC4 with HMAC EXP";
    } 
    return "Unknown (" + paramInt + ")";
  }
  
  static  {
    initStatic();
    BUILTIN_ETYPES = new int[] { 18, 17, 16, 23, 1, 3 };
    BUILTIN_ETYPES_NOAES256 = new int[] { 17, 16, 23, 1, 3 };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\EType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */