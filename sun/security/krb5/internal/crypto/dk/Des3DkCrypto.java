package sun.security.krb5.internal.crypto.dk;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Des3DkCrypto extends DkCrypto {
  private static final byte[] ZERO_IV = { 0, 0, 0, 0, 0, 0, 0, 0 };
  
  protected int getKeySeedLength() { return 168; }
  
  public byte[] stringToKey(char[] paramArrayOfChar) throws GeneralSecurityException {
    arrayOfByte = null;
    try {
      arrayOfByte = charToUtf8(paramArrayOfChar);
      return stringToKey(arrayOfByte, null);
    } finally {
      if (arrayOfByte != null)
        Arrays.fill(arrayOfByte, (byte)0); 
    } 
  }
  
  private byte[] stringToKey(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GeneralSecurityException {
    if (paramArrayOfByte2 != null && paramArrayOfByte2.length > 0)
      throw new RuntimeException("Invalid parameter to stringToKey"); 
    byte[] arrayOfByte = randomToKey(nfold(paramArrayOfByte1, getKeySeedLength()));
    return dk(arrayOfByte, KERBEROS_CONSTANT);
  }
  
  public byte[] parityFix(byte[] paramArrayOfByte) throws GeneralSecurityException {
    setParityBit(paramArrayOfByte);
    return paramArrayOfByte;
  }
  
  protected byte[] randomToKey(byte[] paramArrayOfByte) throws GeneralSecurityException {
    if (paramArrayOfByte.length != 21)
      throw new IllegalArgumentException("input must be 168 bits"); 
    byte[] arrayOfByte1 = keyCorrection(des3Expand(paramArrayOfByte, 0, 7));
    byte[] arrayOfByte2 = keyCorrection(des3Expand(paramArrayOfByte, 7, 14));
    byte[] arrayOfByte3 = keyCorrection(des3Expand(paramArrayOfByte, 14, 21));
    byte[] arrayOfByte4 = new byte[24];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte4, 0, 8);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte4, 8, 8);
    System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 16, 8);
    return arrayOfByte4;
  }
  
  private static byte[] keyCorrection(byte[] paramArrayOfByte) throws GeneralSecurityException {
    try {
      if (DESKeySpec.isWeak(paramArrayOfByte, 0))
        paramArrayOfByte[7] = (byte)(paramArrayOfByte[7] ^ 0xF0); 
    } catch (InvalidKeyException invalidKeyException) {}
    return paramArrayOfByte;
  }
  
  private static byte[] des3Expand(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 - paramInt1 != 7)
      throw new IllegalArgumentException("Invalid length of DES Key Value:" + paramInt1 + "," + paramInt2); 
    byte[] arrayOfByte = new byte[8];
    byte b1 = 0;
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, 7);
    byte b2 = 0;
    for (int i = paramInt1; i < paramInt2; i++) {
      byte b = (byte)(paramArrayOfByte[i] & true);
      b2 = (byte)(b2 + true);
      if (b != 0)
        b1 = (byte)(b1 | b << b2); 
    } 
    arrayOfByte[7] = b1;
    setParityBit(arrayOfByte);
    return arrayOfByte;
  }
  
  private static void setParityBit(byte[] paramArrayOfByte) {
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      byte b1 = paramArrayOfByte[b] & 0xFE;
      b1 |= Integer.bitCount(b1) & true ^ true;
      paramArrayOfByte[b] = (byte)b1;
    } 
  }
  
  protected Cipher getCipher(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws GeneralSecurityException {
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("desede");
    DESedeKeySpec dESedeKeySpec = new DESedeKeySpec(paramArrayOfByte1, 0);
    SecretKey secretKey = secretKeyFactory.generateSecret(dESedeKeySpec);
    if (paramArrayOfByte2 == null)
      paramArrayOfByte2 = ZERO_IV; 
    Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
    IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfByte2, 0, paramArrayOfByte2.length);
    cipher.init(paramInt, secretKey, ivParameterSpec);
    return cipher;
  }
  
  public int getChecksumLength() { return 20; }
  
  protected byte[] getHmac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GeneralSecurityException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfByte1, "HmacSHA1");
    Mac mac = Mac.getInstance("HmacSHA1");
    mac.init(secretKeySpec);
    return mac.doFinal(paramArrayOfByte2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\dk\Des3DkCrypto.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */