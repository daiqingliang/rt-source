package sun.security.rsa;

import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import sun.security.jca.JCAUtil;

public final class RSAPadding {
  public static final int PAD_BLOCKTYPE_1 = 1;
  
  public static final int PAD_BLOCKTYPE_2 = 2;
  
  public static final int PAD_NONE = 3;
  
  public static final int PAD_OAEP_MGF1 = 4;
  
  private final int type;
  
  private final int paddedSize;
  
  private SecureRandom random;
  
  private final int maxDataSize;
  
  private MessageDigest md;
  
  private MessageDigest mgfMd;
  
  private byte[] lHash;
  
  private static final Map<String, byte[]> emptyHashes = Collections.synchronizedMap(new HashMap());
  
  public static RSAPadding getInstance(int paramInt1, int paramInt2) throws InvalidKeyException, InvalidAlgorithmParameterException { return new RSAPadding(paramInt1, paramInt2, null, null); }
  
  public static RSAPadding getInstance(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException { return new RSAPadding(paramInt1, paramInt2, paramSecureRandom, null); }
  
  public static RSAPadding getInstance(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, OAEPParameterSpec paramOAEPParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException { return new RSAPadding(paramInt1, paramInt2, paramSecureRandom, paramOAEPParameterSpec); }
  
  private RSAPadding(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, OAEPParameterSpec paramOAEPParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    this.type = paramInt1;
    this.paddedSize = paramInt2;
    this.random = paramSecureRandom;
    if (paramInt2 < 64)
      throw new InvalidKeyException("Padded size must be at least 64"); 
    switch (paramInt1) {
      case 1:
      case 2:
        this.maxDataSize = paramInt2 - 11;
        return;
      case 3:
        this.maxDataSize = paramInt2;
        return;
      case 4:
        str1 = "SHA-1";
        str2 = "SHA-1";
        arrayOfByte = null;
        try {
          if (paramOAEPParameterSpec != null) {
            str1 = paramOAEPParameterSpec.getDigestAlgorithm();
            String str3 = paramOAEPParameterSpec.getMGFAlgorithm();
            if (!str3.equalsIgnoreCase("MGF1"))
              throw new InvalidAlgorithmParameterException("Unsupported MGF algo: " + str3); 
            str2 = ((MGF1ParameterSpec)paramOAEPParameterSpec.getMGFParameters()).getDigestAlgorithm();
            PSource pSource = paramOAEPParameterSpec.getPSource();
            String str4 = pSource.getAlgorithm();
            if (!str4.equalsIgnoreCase("PSpecified"))
              throw new InvalidAlgorithmParameterException("Unsupported pSource algo: " + str4); 
            arrayOfByte = ((PSource.PSpecified)pSource).getValue();
          } 
          this.mgfMd = (this.md = MessageDigest.getInstance(str1)).getInstance(str2);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          throw new InvalidKeyException("Digest " + str1 + " not available", noSuchAlgorithmException);
        } 
        this.lHash = getInitialHash(this.md, arrayOfByte);
        i = this.lHash.length;
        this.maxDataSize = paramInt2 - 2 - 2 * i;
        if (this.maxDataSize <= 0)
          throw new InvalidKeyException("Key is too short for encryption using OAEPPadding with " + str1 + " and MGF1" + str2); 
        return;
    } 
    throw new InvalidKeyException("Invalid padding: " + paramInt1);
  }
  
  private static byte[] getInitialHash(MessageDigest paramMessageDigest, byte[] paramArrayOfByte) {
    byte[] arrayOfByte;
    if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
      String str = paramMessageDigest.getAlgorithm();
      arrayOfByte = (byte[])emptyHashes.get(str);
      if (arrayOfByte == null) {
        arrayOfByte = paramMessageDigest.digest();
        emptyHashes.put(str, arrayOfByte);
      } 
    } else {
      arrayOfByte = paramMessageDigest.digest(paramArrayOfByte);
    } 
    return arrayOfByte;
  }
  
  public int getMaxDataSize() { return this.maxDataSize; }
  
  public byte[] pad(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws BadPaddingException { return pad(RSACore.convert(paramArrayOfByte, paramInt1, paramInt2)); }
  
  public byte[] pad(byte[] paramArrayOfByte) throws BadPaddingException {
    if (paramArrayOfByte.length > this.maxDataSize)
      throw new BadPaddingException("Data must be shorter than " + (this.maxDataSize + 1) + " bytes but received " + paramArrayOfByte.length + " bytes."); 
    switch (this.type) {
      case 3:
        return paramArrayOfByte;
      case 1:
      case 2:
        return padV15(paramArrayOfByte);
      case 4:
        return padOAEP(paramArrayOfByte);
    } 
    throw new AssertionError();
  }
  
  public byte[] unpad(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws BadPaddingException { return unpad(RSACore.convert(paramArrayOfByte, paramInt1, paramInt2)); }
  
  public byte[] unpad(byte[] paramArrayOfByte) throws BadPaddingException {
    if (paramArrayOfByte.length != this.paddedSize)
      throw new BadPaddingException("Decryption error.The padded array length (" + paramArrayOfByte.length + ") is not the specified padded size (" + this.paddedSize + ")"); 
    switch (this.type) {
      case 3:
        return paramArrayOfByte;
      case 1:
      case 2:
        return unpadV15(paramArrayOfByte);
      case 4:
        return unpadOAEP(paramArrayOfByte);
    } 
    throw new AssertionError();
  }
  
  private byte[] padV15(byte[] paramArrayOfByte) throws BadPaddingException {
    byte[] arrayOfByte = new byte[this.paddedSize];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, this.paddedSize - paramArrayOfByte.length, paramArrayOfByte.length);
    int i = this.paddedSize - 3 - paramArrayOfByte.length;
    byte b = 0;
    arrayOfByte[b++] = 0;
    arrayOfByte[b++] = (byte)this.type;
    if (this.type == 1) {
      while (i-- > 0)
        arrayOfByte[b++] = -1; 
    } else {
      if (this.random == null)
        this.random = JCAUtil.getSecureRandom(); 
      byte[] arrayOfByte1 = new byte[64];
      int j = -1;
      while (i-- > 0) {
        byte b1;
        do {
          if (j < 0) {
            this.random.nextBytes(arrayOfByte1);
            j = arrayOfByte1.length - 1;
          } 
          b1 = arrayOfByte1[j--] & 0xFF;
        } while (b1 == 0);
        arrayOfByte[b++] = (byte)b1;
      } 
    } 
    return arrayOfByte;
  }
  
  private byte[] unpadV15(byte[] paramArrayOfByte) throws BadPaddingException {
    byte b = 0;
    boolean bool = false;
    if (paramArrayOfByte[b++] != 0)
      bool = true; 
    if (paramArrayOfByte[b++] != this.type)
      bool = true; 
    int i = 0;
    while (b < paramArrayOfByte.length) {
      byte b1 = paramArrayOfByte[b++] & 0xFF;
      if (b1 == 0 && !i)
        i = b; 
      if (b == paramArrayOfByte.length && i == 0)
        bool = true; 
      if (this.type == 1 && b1 != 255 && i == 0)
        bool = true; 
    } 
    int j = paramArrayOfByte.length - i;
    if (j > this.maxDataSize)
      bool = true; 
    byte[] arrayOfByte1 = new byte[i];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, i);
    byte[] arrayOfByte2 = new byte[j];
    System.arraycopy(paramArrayOfByte, i, arrayOfByte2, 0, j);
    BadPaddingException badPaddingException = new BadPaddingException("Decryption error");
    if (bool)
      throw badPaddingException; 
    return arrayOfByte2;
  }
  
  private byte[] padOAEP(byte[] paramArrayOfByte) throws BadPaddingException {
    if (this.random == null)
      this.random = JCAUtil.getSecureRandom(); 
    int i = this.lHash.length;
    byte[] arrayOfByte1 = new byte[i];
    this.random.nextBytes(arrayOfByte1);
    byte[] arrayOfByte2 = new byte[this.paddedSize];
    byte b = 1;
    int j = i;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, b, j);
    int k = i + 1;
    int m = arrayOfByte2.length - k;
    int n = this.paddedSize - paramArrayOfByte.length;
    System.arraycopy(this.lHash, 0, arrayOfByte2, k, i);
    arrayOfByte2[n - 1] = 1;
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte2, n, paramArrayOfByte.length);
    mgf1(arrayOfByte2, b, j, arrayOfByte2, k, m);
    mgf1(arrayOfByte2, k, m, arrayOfByte2, b, j);
    return arrayOfByte2;
  }
  
  private byte[] unpadOAEP(byte[] paramArrayOfByte) throws BadPaddingException {
    byte[] arrayOfByte1 = paramArrayOfByte;
    boolean bool = false;
    int i = this.lHash.length;
    if (arrayOfByte1[0] != 0)
      bool = true; 
    byte b = 1;
    int j = i;
    int k = i + 1;
    int m = arrayOfByte1.length - k;
    mgf1(arrayOfByte1, k, m, arrayOfByte1, b, j);
    mgf1(arrayOfByte1, b, j, arrayOfByte1, k, m);
    int n;
    for (n = 0; n < i; n++) {
      if (this.lHash[n] != arrayOfByte1[k + n])
        bool = true; 
    } 
    n = k + i;
    int i1 = -1;
    int i2;
    for (i2 = n; i2 < arrayOfByte1.length; i2++) {
      byte b1 = arrayOfByte1[i2];
      if (i1 == -1 && b1 != 0)
        if (b1 == 1) {
          i1 = i2;
        } else {
          bool = true;
        }  
    } 
    if (i1 == -1) {
      bool = true;
      i1 = arrayOfByte1.length - 1;
    } 
    i2 = i1 + 1;
    byte[] arrayOfByte2 = new byte[i2 - n];
    System.arraycopy(arrayOfByte1, n, arrayOfByte2, 0, arrayOfByte2.length);
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length - i2];
    System.arraycopy(arrayOfByte1, i2, arrayOfByte3, 0, arrayOfByte3.length);
    BadPaddingException badPaddingException = new BadPaddingException("Decryption error");
    if (bool)
      throw badPaddingException; 
    return arrayOfByte3;
  }
  
  private void mgf1(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4) throws BadPaddingException {
    byte[] arrayOfByte1 = new byte[4];
    byte[] arrayOfByte2 = new byte[this.mgfMd.getDigestLength()];
    while (paramInt4 > 0) {
      this.mgfMd.update(paramArrayOfByte1, paramInt1, paramInt2);
      this.mgfMd.update(arrayOfByte1);
      try {
        this.mgfMd.digest(arrayOfByte2, 0, arrayOfByte2.length);
      } catch (DigestException digestException) {
        throw new BadPaddingException(digestException.toString());
      } 
      int i = 0;
      while (i < arrayOfByte2.length && paramInt4 > 0) {
        paramArrayOfByte2[paramInt3++] = (byte)(paramArrayOfByte2[paramInt3++] ^ arrayOfByte2[i++]);
        paramInt4--;
      } 
      if (paramInt4 > 0) {
        i = arrayOfByte1.length - 1;
        arrayOfByte1[i] = (byte)(arrayOfByte1[i] + 1);
        while ((byte)(arrayOfByte1[i] + 1) == 0 && i > 0)
          i--; 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\rsa\RSAPadding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */