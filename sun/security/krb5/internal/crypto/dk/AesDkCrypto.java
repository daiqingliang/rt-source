package sun.security.krb5.internal.crypto.dk;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.crypto.KeyUsage;

public class AesDkCrypto extends DkCrypto {
  private static final boolean debug = false;
  
  private static final int BLOCK_SIZE = 16;
  
  private static final int DEFAULT_ITERATION_COUNT = 4096;
  
  private static final byte[] ZERO_IV = { 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0 };
  
  private static final int hashSize = 12;
  
  private final int keyLength;
  
  public AesDkCrypto(int paramInt) { this.keyLength = paramInt; }
  
  protected int getKeySeedLength() { return this.keyLength; }
  
  public byte[] stringToKey(char[] paramArrayOfChar, String paramString, byte[] paramArrayOfByte) throws GeneralSecurityException {
    arrayOfByte = null;
    try {
      arrayOfByte = paramString.getBytes("UTF-8");
      return stringToKey(paramArrayOfChar, arrayOfByte, paramArrayOfByte);
    } catch (Exception exception) {
      return null;
    } finally {
      if (arrayOfByte != null)
        Arrays.fill(arrayOfByte, (byte)0); 
    } 
  }
  
  private byte[] stringToKey(char[] paramArrayOfChar, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GeneralSecurityException {
    int i = 4096;
    if (paramArrayOfByte2 != null) {
      if (paramArrayOfByte2.length != 4)
        throw new RuntimeException("Invalid parameter to stringToKey"); 
      i = readBigEndian(paramArrayOfByte2, 0, 4);
    } 
    byte[] arrayOfByte = randomToKey(PBKDF2(paramArrayOfChar, paramArrayOfByte1, i, getKeySeedLength()));
    return dk(arrayOfByte, KERBEROS_CONSTANT);
  }
  
  protected byte[] randomToKey(byte[] paramArrayOfByte) { return paramArrayOfByte; }
  
  protected Cipher getCipher(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws GeneralSecurityException {
    if (paramArrayOfByte2 == null)
      paramArrayOfByte2 = ZERO_IV; 
    SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfByte1, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
    IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfByte2, 0, paramArrayOfByte2.length);
    cipher.init(paramInt, secretKeySpec, ivParameterSpec);
    return cipher;
  }
  
  public int getChecksumLength() { return 12; }
  
  protected byte[] getHmac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GeneralSecurityException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfByte1, "HMAC");
    Mac mac = Mac.getInstance("HmacSHA1");
    mac.init(secretKeySpec);
    byte[] arrayOfByte1 = mac.doFinal(paramArrayOfByte2);
    byte[] arrayOfByte2 = new byte[12];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, 12);
    return arrayOfByte2;
  }
  
  public byte[] calculateChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3) throws GeneralSecurityException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    byte[] arrayOfByte1 = new byte[5];
    arrayOfByte1[0] = (byte)(paramInt1 >> 24 & 0xFF);
    arrayOfByte1[1] = (byte)(paramInt1 >> 16 & 0xFF);
    arrayOfByte1[2] = (byte)(paramInt1 >> 8 & 0xFF);
    arrayOfByte1[3] = (byte)(paramInt1 & 0xFF);
    arrayOfByte1[4] = -103;
    arrayOfByte2 = dk(paramArrayOfByte1, arrayOfByte1);
    try {
      byte[] arrayOfByte = getHmac(arrayOfByte2, paramArrayOfByte2);
      if (arrayOfByte.length == getChecksumLength())
        return arrayOfByte; 
      if (arrayOfByte.length > getChecksumLength()) {
        byte[] arrayOfByte3 = new byte[getChecksumLength()];
        System.arraycopy(arrayOfByte, 0, arrayOfByte3, 0, arrayOfByte3.length);
        return arrayOfByte3;
      } 
      throw new GeneralSecurityException("checksum size too short: " + arrayOfByte.length + "; expecting : " + getChecksumLength());
    } finally {
      Arrays.fill(arrayOfByte2, 0, arrayOfByte2.length, (byte)0);
    } 
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    return encryptCTS(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramArrayOfByte4, paramInt2, paramInt3, true);
  }
  
  public byte[] encryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    return encryptCTS(paramArrayOfByte1, paramInt1, paramArrayOfByte2, null, paramArrayOfByte3, paramInt2, paramInt3, false);
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    return decryptCTS(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3, true);
  }
  
  public byte[] decryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    return decryptCTS(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3, false);
  }
  
  private byte[] encryptCTS(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt2, int paramInt3, boolean paramBoolean) throws GeneralSecurityException, KrbCryptoException {
    arrayOfByte1 = null;
    arrayOfByte2 = null;
    try {
      byte[] arrayOfByte3 = new byte[5];
      arrayOfByte3[0] = (byte)(paramInt1 >> 24 & 0xFF);
      arrayOfByte3[1] = (byte)(paramInt1 >> 16 & 0xFF);
      arrayOfByte3[2] = (byte)(paramInt1 >> 8 & 0xFF);
      arrayOfByte3[3] = (byte)(paramInt1 & 0xFF);
      arrayOfByte3[4] = -86;
      arrayOfByte1 = dk(paramArrayOfByte1, arrayOfByte3);
      byte[] arrayOfByte4 = null;
      if (paramBoolean) {
        byte[] arrayOfByte = Confounder.bytes(16);
        arrayOfByte4 = new byte[arrayOfByte.length + paramInt3];
        System.arraycopy(arrayOfByte, 0, arrayOfByte4, 0, arrayOfByte.length);
        System.arraycopy(paramArrayOfByte4, paramInt2, arrayOfByte4, arrayOfByte.length, paramInt3);
      } else {
        arrayOfByte4 = new byte[paramInt3];
        System.arraycopy(paramArrayOfByte4, paramInt2, arrayOfByte4, 0, paramInt3);
      } 
      byte[] arrayOfByte5 = new byte[arrayOfByte4.length + 12];
      Cipher cipher = Cipher.getInstance("AES/CTS/NoPadding");
      SecretKeySpec secretKeySpec = new SecretKeySpec(arrayOfByte1, "AES");
      IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfByte2, 0, paramArrayOfByte2.length);
      cipher.init(1, secretKeySpec, ivParameterSpec);
      cipher.doFinal(arrayOfByte4, 0, arrayOfByte4.length, arrayOfByte5);
      arrayOfByte3[4] = 85;
      arrayOfByte2 = dk(paramArrayOfByte1, arrayOfByte3);
      byte[] arrayOfByte6 = getHmac(arrayOfByte2, arrayOfByte4);
      System.arraycopy(arrayOfByte6, 0, arrayOfByte5, arrayOfByte4.length, arrayOfByte6.length);
      return arrayOfByte5;
    } finally {
      if (arrayOfByte1 != null)
        Arrays.fill(arrayOfByte1, 0, arrayOfByte1.length, (byte)0); 
      if (arrayOfByte2 != null)
        Arrays.fill(arrayOfByte2, 0, arrayOfByte2.length, (byte)0); 
    } 
  }
  
  private byte[] decryptCTS(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3, boolean paramBoolean) throws GeneralSecurityException {
    arrayOfByte1 = null;
    arrayOfByte2 = null;
    try {
      byte[] arrayOfByte3 = new byte[5];
      arrayOfByte3[0] = (byte)(paramInt1 >> 24 & 0xFF);
      arrayOfByte3[1] = (byte)(paramInt1 >> 16 & 0xFF);
      arrayOfByte3[2] = (byte)(paramInt1 >> 8 & 0xFF);
      arrayOfByte3[3] = (byte)(paramInt1 & 0xFF);
      arrayOfByte3[4] = -86;
      arrayOfByte1 = dk(paramArrayOfByte1, arrayOfByte3);
      Cipher cipher = Cipher.getInstance("AES/CTS/NoPadding");
      SecretKeySpec secretKeySpec = new SecretKeySpec(arrayOfByte1, "AES");
      IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfByte2, 0, paramArrayOfByte2.length);
      cipher.init(2, secretKeySpec, ivParameterSpec);
      byte[] arrayOfByte4 = cipher.doFinal(paramArrayOfByte3, paramInt2, paramInt3 - 12);
      arrayOfByte3[4] = 85;
      arrayOfByte2 = dk(paramArrayOfByte1, arrayOfByte3);
      byte[] arrayOfByte5 = getHmac(arrayOfByte2, arrayOfByte4);
      int i = paramInt2 + paramInt3 - 12;
      boolean bool = false;
      if (arrayOfByte5.length >= 12)
        for (int j = 0; j < 12; j++) {
          if (arrayOfByte5[j] != paramArrayOfByte3[i + j]) {
            bool = true;
            break;
          } 
        }  
      if (bool)
        throw new GeneralSecurityException("Checksum failed"); 
      if (paramBoolean) {
        byte[] arrayOfByte = new byte[arrayOfByte4.length - 16];
        System.arraycopy(arrayOfByte4, 16, arrayOfByte, 0, arrayOfByte.length);
        return arrayOfByte;
      } 
      return arrayOfByte4;
    } finally {
      if (arrayOfByte1 != null)
        Arrays.fill(arrayOfByte1, 0, arrayOfByte1.length, (byte)0); 
      if (arrayOfByte2 != null)
        Arrays.fill(arrayOfByte2, 0, arrayOfByte2.length, (byte)0); 
    } 
  }
  
  private static byte[] PBKDF2(char[] paramArrayOfChar, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GeneralSecurityException {
    PBEKeySpec pBEKeySpec = new PBEKeySpec(paramArrayOfChar, paramArrayOfByte, paramInt1, paramInt2);
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    SecretKey secretKey = secretKeyFactory.generateSecret(pBEKeySpec);
    return secretKey.getEncoded();
  }
  
  public static final int readBigEndian(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    byte b = 0;
    int i = (paramInt2 - 1) * 8;
    while (paramInt2 > 0) {
      b += ((paramArrayOfByte[paramInt1] & 0xFF) << i);
      i -= 8;
      paramInt1++;
      paramInt2--;
    } 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\dk\AesDkCrypto.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */