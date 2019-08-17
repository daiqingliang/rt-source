package sun.security.krb5.internal.crypto.dk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.crypto.KeyUsage;

public abstract class DkCrypto {
  protected static final boolean debug = false;
  
  static final byte[] KERBEROS_CONSTANT = { 107, 101, 114, 98, 101, 114, 111, 115 };
  
  protected abstract int getKeySeedLength();
  
  protected abstract byte[] randomToKey(byte[] paramArrayOfByte);
  
  protected abstract Cipher getCipher(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws GeneralSecurityException;
  
  public abstract int getChecksumLength();
  
  protected abstract byte[] getHmac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GeneralSecurityException;
  
  public byte[] encrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
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
      Cipher cipher = getCipher(arrayOfByte1, paramArrayOfByte2, 1);
      int i = cipher.getBlockSize();
      byte[] arrayOfByte4 = Confounder.bytes(i);
      int j = roundup(arrayOfByte4.length + paramInt3, i);
      byte[] arrayOfByte5 = new byte[j];
      System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 0, arrayOfByte4.length);
      System.arraycopy(paramArrayOfByte4, paramInt2, arrayOfByte5, arrayOfByte4.length, paramInt3);
      Arrays.fill(arrayOfByte5, arrayOfByte4.length + paramInt3, j, (byte)0);
      int k = cipher.getOutputSize(j);
      int m = k + getChecksumLength();
      byte[] arrayOfByte6 = new byte[m];
      cipher.doFinal(arrayOfByte5, 0, j, arrayOfByte6, 0);
      if (paramArrayOfByte3 != null && paramArrayOfByte3.length == i)
        System.arraycopy(arrayOfByte6, k - i, paramArrayOfByte3, 0, i); 
      arrayOfByte3[4] = 85;
      arrayOfByte2 = dk(paramArrayOfByte1, arrayOfByte3);
      byte[] arrayOfByte7 = getHmac(arrayOfByte2, arrayOfByte5);
      System.arraycopy(arrayOfByte7, 0, arrayOfByte6, k, getChecksumLength());
      return arrayOfByte6;
    } finally {
      if (arrayOfByte1 != null)
        Arrays.fill(arrayOfByte1, 0, arrayOfByte1.length, (byte)0); 
      if (arrayOfByte2 != null)
        Arrays.fill(arrayOfByte2, 0, arrayOfByte2.length, (byte)0); 
    } 
  }
  
  public byte[] encryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    Cipher cipher = getCipher(paramArrayOfByte1, paramArrayOfByte2, 1);
    int i = cipher.getBlockSize();
    if (paramInt3 % i != 0)
      throw new GeneralSecurityException("length of data to be encrypted (" + paramInt3 + ") is not a multiple of the blocksize (" + i + ")"); 
    int j = cipher.getOutputSize(paramInt3);
    byte[] arrayOfByte = new byte[j];
    cipher.doFinal(paramArrayOfByte3, 0, paramInt3, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public byte[] decryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    Cipher cipher = getCipher(paramArrayOfByte1, paramArrayOfByte2, 2);
    int i = cipher.getBlockSize();
    if (paramInt3 % i != 0)
      throw new GeneralSecurityException("length of data to be decrypted (" + paramInt3 + ") is not a multiple of the blocksize (" + i + ")"); 
    return cipher.doFinal(paramArrayOfByte3, paramInt2, paramInt3);
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3) throws GeneralSecurityException, KrbCryptoException {
    if (!KeyUsage.isValid(paramInt1))
      throw new GeneralSecurityException("Invalid key usage number: " + paramInt1); 
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
      Cipher cipher = getCipher(arrayOfByte1, paramArrayOfByte2, 2);
      int i = cipher.getBlockSize();
      int j = getChecksumLength();
      int k = paramInt3 - j;
      byte[] arrayOfByte4 = cipher.doFinal(paramArrayOfByte3, paramInt2, k);
      arrayOfByte3[4] = 85;
      arrayOfByte2 = dk(paramArrayOfByte1, arrayOfByte3);
      byte[] arrayOfByte5 = getHmac(arrayOfByte2, arrayOfByte4);
      boolean bool = false;
      if (arrayOfByte5.length >= j)
        for (int m = 0; m < j; m++) {
          if (arrayOfByte5[m] != paramArrayOfByte3[k + m]) {
            bool = true;
            break;
          } 
        }  
      if (bool)
        throw new GeneralSecurityException("Checksum failed"); 
      if (paramArrayOfByte2 != null && paramArrayOfByte2.length == i)
        System.arraycopy(paramArrayOfByte3, paramInt2 + k - i, paramArrayOfByte2, 0, i); 
      byte[] arrayOfByte6 = new byte[arrayOfByte4.length - i];
      System.arraycopy(arrayOfByte4, i, arrayOfByte6, 0, arrayOfByte6.length);
      return arrayOfByte6;
    } finally {
      if (arrayOfByte1 != null)
        Arrays.fill(arrayOfByte1, 0, arrayOfByte1.length, (byte)0); 
      if (arrayOfByte2 != null)
        Arrays.fill(arrayOfByte2, 0, arrayOfByte2.length, (byte)0); 
    } 
  }
  
  int roundup(int paramInt1, int paramInt2) { return (paramInt1 + paramInt2 - 1) / paramInt2 * paramInt2; }
  
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
  
  byte[] dk(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GeneralSecurityException { return randomToKey(dr(paramArrayOfByte1, paramArrayOfByte2)); }
  
  private byte[] dr(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GeneralSecurityException {
    Cipher cipher = getCipher(paramArrayOfByte1, null, 1);
    int i = cipher.getBlockSize();
    if (paramArrayOfByte2.length != i)
      paramArrayOfByte2 = nfold(paramArrayOfByte2, i * 8); 
    byte[] arrayOfByte1 = paramArrayOfByte2;
    int j = getKeySeedLength() >> 3;
    byte[] arrayOfByte2 = new byte[j];
    boolean bool = false;
    int k = 0;
    while (k < j) {
      byte[] arrayOfByte = cipher.doFinal(arrayOfByte1);
      int m = (j - k <= arrayOfByte.length) ? (j - k) : arrayOfByte.length;
      System.arraycopy(arrayOfByte, 0, arrayOfByte2, k, m);
      k += m;
      arrayOfByte1 = arrayOfByte;
    } 
    return arrayOfByte2;
  }
  
  static byte[] nfold(byte[] paramArrayOfByte, int paramInt) {
    int i = paramArrayOfByte.length;
    paramInt >>= 3;
    int j = paramInt;
    int k = i;
    while (k != 0) {
      int i1 = k;
      k = j % k;
      j = i1;
    } 
    int m = paramInt * i / j;
    byte[] arrayOfByte = new byte[paramInt];
    Arrays.fill(arrayOfByte, (byte)0);
    byte b = 0;
    int n;
    for (n = m - 1; n >= 0; n--) {
      int i1 = ((i << 3) - 1 + ((i << 3) + 13) * n / i + (i - n % i << 3)) % (i << 3);
      byte b1 = ((paramArrayOfByte[(i - 1 - (i1 >>> 3)) % i] & 0xFF) << 8 | paramArrayOfByte[(i - (i1 >>> 3)) % i] & 0xFF) >>> (i1 & 0x7) + 1 & 0xFF;
      b += b1;
      byte b2 = arrayOfByte[n % paramInt] & 0xFF;
      b += b2;
      arrayOfByte[n % paramInt] = (byte)(b & 0xFF);
      b >>>= 8;
    } 
    if (b != 0)
      for (n = paramInt - 1; n >= 0; n--) {
        b += (arrayOfByte[n] & 0xFF);
        arrayOfByte[n] = (byte)(b & 0xFF);
        b >>>= 8;
      }  
    return arrayOfByte;
  }
  
  static String bytesToString(byte[] paramArrayOfByte) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      if ((paramArrayOfByte[b] & 0xFF) < 16) {
        stringBuffer.append("0" + Integer.toHexString(paramArrayOfByte[b] & 0xFF));
      } else {
        stringBuffer.append(Integer.toHexString(paramArrayOfByte[b] & 0xFF));
      } 
    } 
    return stringBuffer.toString();
  }
  
  private static byte[] binaryStringToBytes(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    byte[] arrayOfByte = new byte[arrayOfChar.length / 2];
    for (byte b = 0; b < arrayOfByte.length; b++) {
      byte b1 = Byte.parseByte(new String(arrayOfChar, b * 2, 1), 16);
      byte b2 = Byte.parseByte(new String(arrayOfChar, b * 2 + 1, 1), 16);
      arrayOfByte[b] = (byte)(b1 << 4 | b2);
    } 
    return arrayOfByte;
  }
  
  static void traceOutput(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(paramInt2);
      (new HexDumpEncoder()).encodeBuffer(new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2), byteArrayOutputStream);
      System.err.println(paramString + ":" + byteArrayOutputStream.toString());
    } catch (Exception exception) {}
  }
  
  static byte[] charToUtf8(char[] paramArrayOfChar) {
    Charset charset = Charset.forName("UTF-8");
    CharBuffer charBuffer = CharBuffer.wrap(paramArrayOfChar);
    ByteBuffer byteBuffer = charset.encode(charBuffer);
    int i = byteBuffer.limit();
    byte[] arrayOfByte = new byte[i];
    byteBuffer.get(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  static byte[] charToUtf16(char[] paramArrayOfChar) {
    Charset charset = Charset.forName("UTF-16LE");
    CharBuffer charBuffer = CharBuffer.wrap(paramArrayOfChar);
    ByteBuffer byteBuffer = charset.encode(charBuffer);
    int i = byteBuffer.limit();
    byte[] arrayOfByte = new byte[i];
    byteBuffer.get(arrayOfByte, 0, i);
    return arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\dk\DkCrypto.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */