package sun.security.krb5.internal.crypto.dk;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.crypto.KeyUsage;
import sun.security.provider.MD4;

public class ArcFourCrypto extends DkCrypto {
  private static final boolean debug = false;
  
  private static final int confounderSize = 8;
  
  private static final byte[] ZERO_IV = { 0, 0, 0, 0, 0, 0, 0, 0 };
  
  private static final int hashSize = 16;
  
  private final int keyLength;
  
  public ArcFourCrypto(int paramInt) { this.keyLength = paramInt; }
  
  protected int getKeySeedLength() { return this.keyLength; }
  
  protected byte[] randomToKey(byte[] paramArrayOfByte) { return paramArrayOfByte; }
  
  public byte[] stringToKey(char[] paramArrayOfChar) throws GeneralSecurityException { return stringToKey(paramArrayOfChar, null); }
  
  private byte[] stringToKey(char[] paramArrayOfChar, byte[] paramArrayOfByte) throws GeneralSecurityException {
    if (paramArrayOfByte != null && paramArrayOfByte.length > 0)
      throw new RuntimeException("Invalid parameter to stringToKey"); 
    arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    try {
      arrayOfByte1 = charToUtf16(paramArrayOfChar);
      MessageDigest messageDigest = MD4.getInstance();
      messageDigest.update(arrayOfByte1);
      arrayOfByte2 = messageDigest.digest();
    } catch (Exception exception) {
      return null;
    } finally {
      if (arrayOfByte1 != null)
        Arrays.fill(arrayOfByte1, (byte)0); 
    } 
    return arrayOfByte2;
  }
  
  protected Cipher getCipher(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws GeneralSecurityException {
    if (paramArrayOfByte2 == null)
      paramArrayOfByte2 = ZERO_IV; 
    SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfByte1, "ARCFOUR");
    Cipher cipher = Cipher.getInstance("ARCFOUR");
    IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfByte2, 0, paramArrayOfByte2.length);
    cipher.init(paramInt, secretKeySpec, ivParameterSpec);
    return cipher;
  }
  
  public int getChecksumLength() { return 16; }
  
  protected byte[] getHmac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GeneralSecurityException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfByte1, "HmacMD5");
    Mac mac = Mac.getInstance("HmacMD5");
    mac.init(secretKeySpec);
    return mac.doFinal(paramArrayOfByte2);
  }
  
  public byte[] calculateChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3) throws GeneralSecurityException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    byte[] arrayOfByte1 = null;
    try {
      byte[] arrayOfByte5 = "signaturekey".getBytes();
      byte[] arrayOfByte6 = new byte[arrayOfByte5.length + 1];
      System.arraycopy(arrayOfByte5, 0, arrayOfByte6, 0, arrayOfByte5.length);
      arrayOfByte1 = getHmac(paramArrayOfByte1, arrayOfByte6);
    } catch (Exception exception) {
      GeneralSecurityException generalSecurityException = new GeneralSecurityException("Calculate Checkum Failed!");
      generalSecurityException.initCause(exception);
      throw generalSecurityException;
    } 
    byte[] arrayOfByte2 = getSalt(paramInt1);
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      GeneralSecurityException generalSecurityException = new GeneralSecurityException("Calculate Checkum Failed!");
      generalSecurityException.initCause(noSuchAlgorithmException);
      throw generalSecurityException;
    } 
    messageDigest.update(arrayOfByte2);
    messageDigest.update(paramArrayOfByte2, paramInt2, paramInt3);
    byte[] arrayOfByte3 = messageDigest.digest();
    byte[] arrayOfByte4 = getHmac(arrayOfByte1, arrayOfByte3);
    if (arrayOfByte4.length == getChecksumLength())
      return arrayOfByte4; 
    if (arrayOfByte4.length > getChecksumLength()) {
      byte[] arrayOfByte = new byte[getChecksumLength()];
      System.arraycopy(arrayOfByte4, 0, arrayOfByte, 0, arrayOfByte.length);
      return arrayOfByte;
    } 
    throw new GeneralSecurityException("checksum size too short: " + arrayOfByte4.length + "; expecting : " + getChecksumLength());
  }
  
  public byte[] encryptSeq(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    byte[] arrayOfByte1 = new byte[4];
    byte[] arrayOfByte2 = getHmac(paramArrayOfByte1, arrayOfByte1);
    arrayOfByte2 = getHmac(arrayOfByte2, paramArrayOfByte2);
    Cipher cipher = Cipher.getInstance("ARCFOUR");
    SecretKeySpec secretKeySpec = new SecretKeySpec(arrayOfByte2, "ARCFOUR");
    cipher.init(1, secretKeySpec);
    return cipher.doFinal(paramArrayOfByte3, paramInt2, paramInt3);
  }
  
  public byte[] decryptSeq(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    byte[] arrayOfByte1 = new byte[4];
    byte[] arrayOfByte2 = getHmac(paramArrayOfByte1, arrayOfByte1);
    arrayOfByte2 = getHmac(arrayOfByte2, paramArrayOfByte2);
    Cipher cipher = Cipher.getInstance("ARCFOUR");
    SecretKeySpec secretKeySpec = new SecretKeySpec(arrayOfByte2, "ARCFOUR");
    cipher.init(2, secretKeySpec);
    return cipher.doFinal(paramArrayOfByte3, paramInt2, paramInt3);
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    byte[] arrayOfByte1 = Confounder.bytes(8);
    int i = roundup(arrayOfByte1.length + paramInt3, 1);
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
    System.arraycopy(paramArrayOfByte4, paramInt2, arrayOfByte2, arrayOfByte1.length, paramInt3);
    byte[] arrayOfByte3 = new byte[paramArrayOfByte1.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte3, 0, paramArrayOfByte1.length);
    byte[] arrayOfByte4 = getSalt(paramInt1);
    byte[] arrayOfByte5 = getHmac(arrayOfByte3, arrayOfByte4);
    byte[] arrayOfByte6 = getHmac(arrayOfByte5, arrayOfByte2);
    byte[] arrayOfByte7 = getHmac(arrayOfByte5, arrayOfByte6);
    Cipher cipher = Cipher.getInstance("ARCFOUR");
    SecretKeySpec secretKeySpec = new SecretKeySpec(arrayOfByte7, "ARCFOUR");
    cipher.init(1, secretKeySpec);
    byte[] arrayOfByte8 = cipher.doFinal(arrayOfByte2, 0, arrayOfByte2.length);
    byte[] arrayOfByte9 = new byte[16 + arrayOfByte8.length];
    System.arraycopy(arrayOfByte6, 0, arrayOfByte9, 0, 16);
    System.arraycopy(arrayOfByte8, 0, arrayOfByte9, 16, arrayOfByte8.length);
    return arrayOfByte9;
  }
  
  public byte[] encryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    byte[] arrayOfByte1 = new byte[paramArrayOfByte1.length];
    for (byte b = 0; b <= 15; b++)
      arrayOfByte1[b] = (byte)(paramArrayOfByte1[b] ^ 0xF0); 
    byte[] arrayOfByte2 = new byte[4];
    byte[] arrayOfByte3 = getHmac(arrayOfByte1, arrayOfByte2);
    arrayOfByte3 = getHmac(arrayOfByte3, paramArrayOfByte2);
    Cipher cipher = Cipher.getInstance("ARCFOUR");
    SecretKeySpec secretKeySpec = new SecretKeySpec(arrayOfByte3, "ARCFOUR");
    cipher.init(1, secretKeySpec);
    return cipher.doFinal(paramArrayOfByte3, paramInt2, paramInt3);
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    byte[] arrayOfByte1 = new byte[paramArrayOfByte1.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, paramArrayOfByte1.length);
    byte[] arrayOfByte2 = getSalt(paramInt1);
    byte[] arrayOfByte3 = getHmac(arrayOfByte1, arrayOfByte2);
    byte[] arrayOfByte4 = new byte[16];
    System.arraycopy(paramArrayOfByte3, paramInt2, arrayOfByte4, 0, 16);
    byte[] arrayOfByte5 = getHmac(arrayOfByte3, arrayOfByte4);
    Cipher cipher = Cipher.getInstance("ARCFOUR");
    SecretKeySpec secretKeySpec = new SecretKeySpec(arrayOfByte5, "ARCFOUR");
    cipher.init(2, secretKeySpec);
    byte[] arrayOfByte6 = cipher.doFinal(paramArrayOfByte3, paramInt2 + 16, paramInt3 - 16);
    byte[] arrayOfByte7 = getHmac(arrayOfByte3, arrayOfByte6);
    boolean bool = false;
    if (arrayOfByte7.length >= 16)
      for (byte b = 0; b < 16; b++) {
        if (arrayOfByte7[b] != paramArrayOfByte3[b]) {
          bool = true;
          break;
        } 
      }  
    if (bool)
      throw new GeneralSecurityException("Checksum failed"); 
    byte[] arrayOfByte8 = new byte[arrayOfByte6.length - 8];
    System.arraycopy(arrayOfByte6, 8, arrayOfByte8, 0, arrayOfByte8.length);
    return arrayOfByte8;
  }
  
  public byte[] decryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3, byte[] paramArrayOfByte4) throws GeneralSecurityException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
    byte[] arrayOfByte1 = new byte[paramArrayOfByte1.length];
    for (byte b = 0; b <= 15; b++)
      arrayOfByte1[b] = (byte)(paramArrayOfByte1[b] ^ 0xF0); 
    byte[] arrayOfByte2 = new byte[4];
    byte[] arrayOfByte3 = getHmac(arrayOfByte1, arrayOfByte2);
    byte[] arrayOfByte4 = new byte[4];
    System.arraycopy(paramArrayOfByte4, 0, arrayOfByte4, 0, arrayOfByte4.length);
    arrayOfByte3 = getHmac(arrayOfByte3, arrayOfByte4);
    Cipher cipher = Cipher.getInstance("ARCFOUR");
    SecretKeySpec secretKeySpec = new SecretKeySpec(arrayOfByte3, "ARCFOUR");
    cipher.init(2, secretKeySpec);
    return cipher.doFinal(paramArrayOfByte3, paramInt2, paramInt3);
  }
  
  private byte[] getSalt(int paramInt) {
    int i = arcfour_translate_usage(paramInt);
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(i & 0xFF);
    arrayOfByte[1] = (byte)(i >> 8 & 0xFF);
    arrayOfByte[2] = (byte)(i >> 16 & 0xFF);
    arrayOfByte[3] = (byte)(i >> 24 & 0xFF);
    return arrayOfByte;
  }
  
  private int arcfour_translate_usage(int paramInt) {
    switch (paramInt) {
      case 3:
        return 8;
      case 9:
        return 8;
      case 23:
        return 13;
    } 
    return paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\dk\ArcFourCrypto.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */