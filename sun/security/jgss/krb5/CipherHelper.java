package sun.security.jgss.krb5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.ietf.jgss.GSSException;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.internal.crypto.Aes128;
import sun.security.krb5.internal.crypto.Aes256;
import sun.security.krb5.internal.crypto.ArcFourHmac;
import sun.security.krb5.internal.crypto.Des3;

class CipherHelper {
  private static final int KG_USAGE_SEAL = 22;
  
  private static final int KG_USAGE_SIGN = 23;
  
  private static final int KG_USAGE_SEQ = 24;
  
  private static final int DES_CHECKSUM_SIZE = 8;
  
  private static final int DES_IV_SIZE = 8;
  
  private static final int AES_IV_SIZE = 16;
  
  private static final int HMAC_CHECKSUM_SIZE = 8;
  
  private static final int KG_USAGE_SIGN_MS = 15;
  
  private static final boolean DEBUG = Krb5Util.DEBUG;
  
  private static final byte[] ZERO_IV = new byte[8];
  
  private static final byte[] ZERO_IV_AES = new byte[16];
  
  private int etype;
  
  private int sgnAlg;
  
  private int sealAlg;
  
  private byte[] keybytes;
  
  private int proto = 0;
  
  CipherHelper(EncryptionKey paramEncryptionKey) throws GSSException {
    this.etype = paramEncryptionKey.getEType();
    this.keybytes = paramEncryptionKey.getBytes();
    switch (this.etype) {
      case 1:
      case 3:
        this.sgnAlg = 0;
        this.sealAlg = 0;
        return;
      case 16:
        this.sgnAlg = 1024;
        this.sealAlg = 512;
        return;
      case 23:
        this.sgnAlg = 4352;
        this.sealAlg = 4096;
        return;
      case 17:
      case 18:
        this.sgnAlg = -1;
        this.sealAlg = -1;
        this.proto = 1;
        return;
    } 
    throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
  }
  
  int getSgnAlg() { return this.sgnAlg; }
  
  int getSealAlg() { return this.sealAlg; }
  
  int getProto() { return this.proto; }
  
  int getEType() { return this.etype; }
  
  boolean isArcFour() {
    boolean bool = false;
    if (this.etype == 23)
      bool = true; 
    return bool;
  }
  
  byte[] calculateChecksum(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3, int paramInt4) throws GSSException {
    int j;
    byte b2;
    byte[] arrayOfByte2;
    int i;
    byte b1;
    byte[] arrayOfByte1;
    switch (paramInt1) {
      case 0:
        try {
          arrayOfByte1 = MessageDigest.getInstance("MD5");
          arrayOfByte1.update(paramArrayOfByte1);
          arrayOfByte1.update(paramArrayOfByte3, paramInt2, paramInt3);
          if (paramArrayOfByte2 != null)
            arrayOfByte1.update(paramArrayOfByte2); 
          paramArrayOfByte3 = arrayOfByte1.digest();
          paramInt2 = 0;
          paramInt3 = paramArrayOfByte3.length;
          paramArrayOfByte1 = null;
          paramArrayOfByte2 = null;
        } catch (NoSuchAlgorithmException null) {
          GSSException gSSException = new GSSException(11, -1, "Could not get MD5 Message Digest - " + arrayOfByte1.getMessage());
          gSSException.initCause(arrayOfByte1);
          throw gSSException;
        } 
      case 512:
        return getDesCbcChecksum(this.keybytes, paramArrayOfByte1, paramArrayOfByte3, paramInt2, paramInt3);
      case 1024:
        if (paramArrayOfByte1 == null && paramArrayOfByte2 == null) {
          arrayOfByte1 = paramArrayOfByte3;
          i = paramInt3;
          b1 = paramInt2;
        } else {
          i = ((paramArrayOfByte1 != null) ? paramArrayOfByte1.length : 0) + paramInt3 + ((paramArrayOfByte2 != null) ? paramArrayOfByte2.length : 0);
          arrayOfByte1 = new byte[i];
          int k = 0;
          if (paramArrayOfByte1 != null) {
            System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, paramArrayOfByte1.length);
            k = paramArrayOfByte1.length;
          } 
          System.arraycopy(paramArrayOfByte3, paramInt2, arrayOfByte1, k, paramInt3);
          k += paramInt3;
          if (paramArrayOfByte2 != null)
            System.arraycopy(paramArrayOfByte2, 0, arrayOfByte1, k, paramArrayOfByte2.length); 
          b1 = 0;
        } 
        try {
          return Des3.calculateChecksum(this.keybytes, 23, arrayOfByte1, b1, i);
        } catch (GeneralSecurityException null) {
          GSSException gSSException = new GSSException(11, -1, "Could not use HMAC-SHA1-DES3-KD signing algorithm - " + arrayOfByte2.getMessage());
          gSSException.initCause(arrayOfByte2);
          throw gSSException;
        } 
      case 4352:
        if (paramArrayOfByte1 == null && paramArrayOfByte2 == null) {
          arrayOfByte2 = paramArrayOfByte3;
          j = paramInt3;
          b2 = paramInt2;
        } else {
          j = ((paramArrayOfByte1 != null) ? paramArrayOfByte1.length : 0) + paramInt3 + ((paramArrayOfByte2 != null) ? paramArrayOfByte2.length : 0);
          arrayOfByte2 = new byte[j];
          int k = 0;
          if (paramArrayOfByte1 != null) {
            System.arraycopy(paramArrayOfByte1, 0, arrayOfByte2, 0, paramArrayOfByte1.length);
            k = paramArrayOfByte1.length;
          } 
          System.arraycopy(paramArrayOfByte3, paramInt2, arrayOfByte2, k, paramInt3);
          k += paramInt3;
          if (paramArrayOfByte2 != null)
            System.arraycopy(paramArrayOfByte2, 0, arrayOfByte2, k, paramArrayOfByte2.length); 
          b2 = 0;
        } 
        try {
          byte b = 23;
          if (paramInt4 == 257)
            b = 15; 
          byte[] arrayOfByte3 = ArcFourHmac.calculateChecksum(this.keybytes, b, arrayOfByte2, b2, j);
          byte[] arrayOfByte4 = new byte[getChecksumLength()];
          System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 0, arrayOfByte4.length);
          return arrayOfByte4;
        } catch (GeneralSecurityException generalSecurityException) {
          GSSException gSSException = new GSSException(11, -1, "Could not use HMAC_MD5_ARCFOUR signing algorithm - " + generalSecurityException.getMessage());
          gSSException.initCause(generalSecurityException);
          throw gSSException;
        } 
    } 
    throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
  }
  
  byte[] calculateChecksum(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3) throws GSSException {
    int i = ((paramArrayOfByte1 != null) ? paramArrayOfByte1.length : 0) + paramInt2;
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(paramArrayOfByte2, paramInt1, arrayOfByte, 0, paramInt2);
    if (paramArrayOfByte1 != null)
      System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, paramInt2, paramArrayOfByte1.length); 
    switch (this.etype) {
      case 17:
        try {
          return Aes128.calculateChecksum(this.keybytes, paramInt3, arrayOfByte, 0, i);
        } catch (GeneralSecurityException generalSecurityException) {
          GSSException gSSException = new GSSException(11, -1, "Could not use AES128 signing algorithm - " + generalSecurityException.getMessage());
          gSSException.initCause(generalSecurityException);
          throw gSSException;
        } 
      case 18:
        try {
          return Aes256.calculateChecksum(this.keybytes, paramInt3, arrayOfByte, 0, i);
        } catch (GeneralSecurityException generalSecurityException) {
          GSSException gSSException = new GSSException(11, -1, "Could not use AES256 signing algorithm - " + generalSecurityException.getMessage());
          gSSException.initCause(generalSecurityException);
          throw gSSException;
        } 
    } 
    throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
  }
  
  byte[] encryptSeq(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2) throws GSSException {
    byte[] arrayOfByte2;
    byte[] arrayOfByte1;
    switch (this.sgnAlg) {
      case 0:
      case 512:
        try {
          arrayOfByte1 = getInitializedDes(true, this.keybytes, paramArrayOfByte1);
          return arrayOfByte1.doFinal(paramArrayOfByte2, paramInt1, paramInt2);
        } catch (GeneralSecurityException null) {
          arrayOfByte2 = new GSSException(11, -1, "Could not encrypt sequence number using DES - " + arrayOfByte1.getMessage());
          arrayOfByte2.initCause(arrayOfByte1);
          throw arrayOfByte2;
        } 
      case 1024:
        if (paramArrayOfByte1.length == 8) {
          arrayOfByte1 = paramArrayOfByte1;
        } else {
          arrayOfByte1 = new byte[8];
          System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, 8);
        } 
        try {
          return Des3.encryptRaw(this.keybytes, 24, arrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2);
        } catch (Exception null) {
          GSSException gSSException = new GSSException(11, -1, "Could not encrypt sequence number using DES3-KD - " + arrayOfByte2.getMessage());
          gSSException.initCause(arrayOfByte2);
          throw gSSException;
        } 
      case 4352:
        if (paramArrayOfByte1.length == 8) {
          arrayOfByte2 = paramArrayOfByte1;
        } else {
          arrayOfByte2 = new byte[8];
          System.arraycopy(paramArrayOfByte1, 0, arrayOfByte2, 0, 8);
        } 
        try {
          return ArcFourHmac.encryptSeq(this.keybytes, 24, arrayOfByte2, paramArrayOfByte2, paramInt1, paramInt2);
        } catch (Exception exception) {
          GSSException gSSException = new GSSException(11, -1, "Could not encrypt sequence number using RC4-HMAC - " + exception.getMessage());
          gSSException.initCause(exception);
          throw gSSException;
        } 
    } 
    throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
  }
  
  byte[] decryptSeq(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2) throws GSSException {
    byte[] arrayOfByte2;
    byte[] arrayOfByte1;
    switch (this.sgnAlg) {
      case 0:
      case 512:
        try {
          arrayOfByte1 = getInitializedDes(false, this.keybytes, paramArrayOfByte1);
          return arrayOfByte1.doFinal(paramArrayOfByte2, paramInt1, paramInt2);
        } catch (GeneralSecurityException null) {
          arrayOfByte2 = new GSSException(11, -1, "Could not decrypt sequence number using DES - " + arrayOfByte1.getMessage());
          arrayOfByte2.initCause(arrayOfByte1);
          throw arrayOfByte2;
        } 
      case 1024:
        if (paramArrayOfByte1.length == 8) {
          arrayOfByte1 = paramArrayOfByte1;
        } else {
          arrayOfByte1 = new byte[8];
          System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, 8);
        } 
        try {
          return Des3.decryptRaw(this.keybytes, 24, arrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2);
        } catch (Exception null) {
          GSSException gSSException = new GSSException(11, -1, "Could not decrypt sequence number using DES3-KD - " + arrayOfByte2.getMessage());
          gSSException.initCause(arrayOfByte2);
          throw gSSException;
        } 
      case 4352:
        if (paramArrayOfByte1.length == 8) {
          arrayOfByte2 = paramArrayOfByte1;
        } else {
          arrayOfByte2 = new byte[8];
          System.arraycopy(paramArrayOfByte1, 0, arrayOfByte2, 0, 8);
        } 
        try {
          return ArcFourHmac.decryptSeq(this.keybytes, 24, arrayOfByte2, paramArrayOfByte2, paramInt1, paramInt2);
        } catch (Exception exception) {
          GSSException gSSException = new GSSException(11, -1, "Could not decrypt sequence number using RC4-HMAC - " + exception.getMessage());
          gSSException.initCause(exception);
          throw gSSException;
        } 
    } 
    throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
  }
  
  int getChecksumLength() {
    switch (this.etype) {
      case 1:
      case 3:
        return 8;
      case 16:
        return Des3.getChecksumLength();
      case 17:
        return Aes128.getChecksumLength();
      case 18:
        return Aes256.getChecksumLength();
      case 23:
        return 8;
    } 
    throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
  }
  
  void decryptData(WrapToken paramWrapToken, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3) throws GSSException {
    switch (this.sealAlg) {
      case 0:
        desCbcDecrypt(paramWrapToken, getDesEncryptionKey(this.keybytes), paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
        return;
      case 512:
        des3KdDecrypt(paramWrapToken, paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
        return;
      case 4096:
        arcFourDecrypt(paramWrapToken, paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
        return;
    } 
    throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
  }
  
  void decryptData(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4) throws GSSException {
    switch (this.etype) {
      case 17:
        aes128Decrypt(paramWrapToken_v2, paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
        return;
      case 18:
        aes256Decrypt(paramWrapToken_v2, paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
        return;
    } 
    throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
  }
  
  void decryptData(WrapToken paramWrapToken, InputStream paramInputStream, int paramInt1, byte[] paramArrayOfByte, int paramInt2) throws GSSException, IOException {
    byte[] arrayOfByte2;
    byte[] arrayOfByte1;
    switch (this.sealAlg) {
      case 0:
        desCbcDecrypt(paramWrapToken, getDesEncryptionKey(this.keybytes), paramInputStream, paramInt1, paramArrayOfByte, paramInt2);
        return;
      case 512:
        arrayOfByte1 = new byte[paramInt1];
        try {
          Krb5Token.readFully(paramInputStream, arrayOfByte1, 0, paramInt1);
        } catch (IOException iOException) {
          GSSException gSSException = new GSSException(10, -1, "Cannot read complete token");
          gSSException.initCause(iOException);
          throw gSSException;
        } 
        des3KdDecrypt(paramWrapToken, arrayOfByte1, 0, paramInt1, paramArrayOfByte, paramInt2);
        return;
      case 4096:
        arrayOfByte2 = new byte[paramInt1];
        try {
          Krb5Token.readFully(paramInputStream, arrayOfByte2, 0, paramInt1);
        } catch (IOException iOException) {
          GSSException gSSException = new GSSException(10, -1, "Cannot read complete token");
          gSSException.initCause(iOException);
          throw gSSException;
        } 
        arcFourDecrypt(paramWrapToken, arrayOfByte2, 0, paramInt1, paramArrayOfByte, paramInt2);
        return;
    } 
    throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
  }
  
  void decryptData(WrapToken_v2 paramWrapToken_v2, InputStream paramInputStream, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) throws GSSException, IOException {
    byte[] arrayOfByte = new byte[paramInt1];
    try {
      Krb5Token.readFully(paramInputStream, arrayOfByte, 0, paramInt1);
    } catch (IOException iOException) {
      GSSException gSSException = new GSSException(10, -1, "Cannot read complete token");
      gSSException.initCause(iOException);
      throw gSSException;
    } 
    switch (this.etype) {
      case 17:
        aes128Decrypt(paramWrapToken_v2, arrayOfByte, 0, paramInt1, paramArrayOfByte, paramInt2, paramInt3);
        return;
      case 18:
        aes256Decrypt(paramWrapToken_v2, arrayOfByte, 0, paramInt1, paramArrayOfByte, paramInt2, paramInt3);
        return;
    } 
    throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
  }
  
  void encryptData(WrapToken paramWrapToken, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, OutputStream paramOutputStream) throws GSSException, IOException {
    byte[] arrayOfByte2;
    byte[] arrayOfByte1;
    CipherOutputStream cipherOutputStream;
    Cipher cipher;
    switch (this.sealAlg) {
      case 0:
        cipher = getInitializedDes(true, getDesEncryptionKey(this.keybytes), ZERO_IV);
        cipherOutputStream = new CipherOutputStream(paramOutputStream, cipher);
        cipherOutputStream.write(paramArrayOfByte1);
        cipherOutputStream.write(paramArrayOfByte2, paramInt1, paramInt2);
        cipherOutputStream.write(paramArrayOfByte3);
        return;
      case 512:
        arrayOfByte1 = des3KdEncrypt(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
        paramOutputStream.write(arrayOfByte1);
        return;
      case 4096:
        arrayOfByte2 = arcFourEncrypt(paramWrapToken, paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
        paramOutputStream.write(arrayOfByte2);
        return;
    } 
    throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
  }
  
  byte[] encryptData(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, int paramInt3) throws GSSException {
    switch (this.etype) {
      case 17:
        return aes128Encrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramInt1, paramInt2, paramInt3);
      case 18:
        return aes256Encrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramInt1, paramInt2, paramInt3);
    } 
    throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
  }
  
  void encryptData(WrapToken paramWrapToken, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt3) throws GSSException {
    byte[] arrayOfByte2;
    byte[] arrayOfByte1;
    Cipher cipher;
    int i;
    switch (this.sealAlg) {
      case 0:
        i = paramInt3;
        cipher = getInitializedDes(true, getDesEncryptionKey(this.keybytes), ZERO_IV);
        try {
          i += cipher.update(paramArrayOfByte1, 0, paramArrayOfByte1.length, paramArrayOfByte4, i);
          i += cipher.update(paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte4, i);
          cipher.update(paramArrayOfByte3, 0, paramArrayOfByte3.length, paramArrayOfByte4, i);
          cipher.doFinal();
        } catch (GeneralSecurityException generalSecurityException) {
          GSSException gSSException = new GSSException(11, -1, "Could not use DES Cipher - " + generalSecurityException.getMessage());
          gSSException.initCause(generalSecurityException);
          throw gSSException;
        } 
        return;
      case 512:
        arrayOfByte1 = des3KdEncrypt(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
        System.arraycopy(arrayOfByte1, 0, paramArrayOfByte4, paramInt3, arrayOfByte1.length);
        return;
      case 4096:
        arrayOfByte2 = arcFourEncrypt(paramWrapToken, paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
        System.arraycopy(arrayOfByte2, 0, paramArrayOfByte4, paramInt3, arrayOfByte2.length);
        return;
    } 
    throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
  }
  
  int encryptData(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, byte[] paramArrayOfByte4, int paramInt3, int paramInt4) throws GSSException {
    byte[] arrayOfByte = null;
    switch (this.etype) {
      case 17:
        arrayOfByte = aes128Encrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramInt1, paramInt2, paramInt4);
        System.arraycopy(arrayOfByte, 0, paramArrayOfByte4, paramInt3, arrayOfByte.length);
        return arrayOfByte.length;
      case 18:
        arrayOfByte = aes256Encrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramInt1, paramInt2, paramInt4);
        System.arraycopy(arrayOfByte, 0, paramArrayOfByte4, paramInt3, arrayOfByte.length);
        return arrayOfByte.length;
    } 
    throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
  }
  
  private byte[] getDesCbcChecksum(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2) throws GSSException {
    Cipher cipher = getInitializedDes(true, paramArrayOfByte1, ZERO_IV);
    int i = cipher.getBlockSize();
    byte[] arrayOfByte = new byte[i];
    int j = paramInt2 / i;
    int k = paramInt2 % i;
    if (k == 0) {
      System.arraycopy(paramArrayOfByte3, paramInt1 + --j * i, arrayOfByte, 0, i);
    } else {
      System.arraycopy(paramArrayOfByte3, paramInt1 + j * i, arrayOfByte, 0, k);
    } 
    try {
      byte[] arrayOfByte1 = new byte[Math.max(i, (paramArrayOfByte2 == null) ? i : paramArrayOfByte2.length)];
      if (paramArrayOfByte2 != null)
        cipher.update(paramArrayOfByte2, 0, paramArrayOfByte2.length, arrayOfByte1, 0); 
      for (byte b = 0; b < j; b++) {
        cipher.update(paramArrayOfByte3, paramInt1, i, arrayOfByte1, 0);
        paramInt1 += i;
      } 
      byte[] arrayOfByte2 = new byte[i];
      cipher.update(arrayOfByte, 0, i, arrayOfByte2, 0);
      cipher.doFinal();
      return arrayOfByte2;
    } catch (GeneralSecurityException generalSecurityException) {
      GSSException gSSException = new GSSException(11, -1, "Could not use DES Cipher - " + generalSecurityException.getMessage());
      gSSException.initCause(generalSecurityException);
      throw gSSException;
    } 
  }
  
  private final Cipher getInitializedDes(boolean paramBoolean, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws GSSException {
    try {
      IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfByte2);
      SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfByte1, "DES");
      Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
      cipher.init(paramBoolean ? 1 : 2, secretKeySpec, ivParameterSpec);
      return cipher;
    } catch (GeneralSecurityException generalSecurityException) {
      GSSException gSSException = new GSSException(11, -1, generalSecurityException.getMessage());
      gSSException.initCause(generalSecurityException);
      throw gSSException;
    } 
  }
  
  private void desCbcDecrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, int paramInt3) throws GSSException {
    try {
      int i = 0;
      Cipher cipher = getInitializedDes(false, paramArrayOfByte1, ZERO_IV);
      i = cipher.update(paramArrayOfByte2, paramInt1, 8, paramWrapToken.confounder);
      paramInt1 += 8;
      paramInt2 -= 8;
      int j = cipher.getBlockSize();
      int k = paramInt2 / j - 1;
      for (byte b = 0; b < k; b++) {
        i = cipher.update(paramArrayOfByte2, paramInt1, j, paramArrayOfByte3, paramInt3);
        paramInt1 += j;
        paramInt3 += j;
      } 
      byte[] arrayOfByte = new byte[j];
      cipher.update(paramArrayOfByte2, paramInt1, j, arrayOfByte);
      cipher.doFinal();
      byte b1 = arrayOfByte[j - 1];
      if (b1 < 1 || b1 > 8)
        throw new GSSException(10, -1, "Invalid padding on Wrap Token"); 
      paramWrapToken.padding = WrapToken.pads[b1];
      j -= b1;
      System.arraycopy(arrayOfByte, 0, paramArrayOfByte3, paramInt3, j);
    } catch (GeneralSecurityException generalSecurityException) {
      GSSException gSSException = new GSSException(11, -1, "Could not use DES cipher - " + generalSecurityException.getMessage());
      gSSException.initCause(generalSecurityException);
      throw gSSException;
    } 
  }
  
  private void desCbcDecrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, InputStream paramInputStream, int paramInt1, byte[] paramArrayOfByte2, int paramInt2) throws GSSException, IOException {
    int i = 0;
    Cipher cipher = getInitializedDes(false, paramArrayOfByte1, ZERO_IV);
    WrapTokenInputStream wrapTokenInputStream = new WrapTokenInputStream(paramInputStream, paramInt1);
    CipherInputStream cipherInputStream = new CipherInputStream(wrapTokenInputStream, cipher);
    i = cipherInputStream.read(paramWrapToken.confounder);
    paramInt1 -= i;
    int j = cipher.getBlockSize();
    int k = paramInt1 / j - 1;
    for (byte b = 0; b < k; b++) {
      i = cipherInputStream.read(paramArrayOfByte2, paramInt2, j);
      paramInt2 += j;
    } 
    byte[] arrayOfByte = new byte[j];
    i = cipherInputStream.read(arrayOfByte);
    try {
      cipher.doFinal();
    } catch (GeneralSecurityException generalSecurityException) {
      GSSException gSSException = new GSSException(11, -1, "Could not use DES cipher - " + generalSecurityException.getMessage());
      gSSException.initCause(generalSecurityException);
      throw gSSException;
    } 
    byte b1 = arrayOfByte[j - 1];
    if (b1 < 1 || b1 > 8)
      throw new GSSException(10, -1, "Invalid padding on Wrap Token"); 
    paramWrapToken.padding = WrapToken.pads[b1];
    j -= b1;
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte2, paramInt2, j);
  }
  
  private static byte[] getDesEncryptionKey(byte[] paramArrayOfByte) throws GSSException {
    if (paramArrayOfByte.length > 8)
      throw new GSSException(11, -100, "Invalid DES Key!"); 
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    for (byte b = 0; b < paramArrayOfByte.length; b++)
      arrayOfByte[b] = (byte)(paramArrayOfByte[b] ^ 0xF0); 
    return arrayOfByte;
  }
  
  private void des3KdDecrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3) throws GSSException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = Des3.decryptRaw(this.keybytes, 22, ZERO_IV, paramArrayOfByte1, paramInt1, paramInt2);
    } catch (GeneralSecurityException generalSecurityException) {
      GSSException gSSException = new GSSException(11, -1, "Could not use DES3-KD Cipher - " + generalSecurityException.getMessage());
      gSSException.initCause(generalSecurityException);
      throw gSSException;
    } 
    byte b = arrayOfByte[arrayOfByte.length - 1];
    if (b < 1 || b > 8)
      throw new GSSException(10, -1, "Invalid padding on Wrap Token"); 
    paramWrapToken.padding = WrapToken.pads[b];
    int i = arrayOfByte.length - 8 - b;
    System.arraycopy(arrayOfByte, 8, paramArrayOfByte2, paramInt3, i);
    System.arraycopy(arrayOfByte, 0, paramWrapToken.confounder, 0, 8);
  }
  
  private byte[] des3KdEncrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3) throws GSSException {
    byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramInt2 + paramArrayOfByte3.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
    System.arraycopy(paramArrayOfByte2, paramInt1, arrayOfByte, paramArrayOfByte1.length, paramInt2);
    System.arraycopy(paramArrayOfByte3, 0, arrayOfByte, paramArrayOfByte1.length + paramInt2, paramArrayOfByte3.length);
    try {
      return Des3.encryptRaw(this.keybytes, 22, ZERO_IV, arrayOfByte, 0, arrayOfByte.length);
    } catch (Exception exception) {
      GSSException gSSException = new GSSException(11, -1, "Could not use DES3-KD Cipher - " + exception.getMessage());
      gSSException.initCause(exception);
      throw gSSException;
    } 
  }
  
  private void arcFourDecrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3) throws GSSException {
    byte[] arrayOfByte2;
    byte[] arrayOfByte1 = decryptSeq(paramWrapToken.getChecksum(), paramWrapToken.getEncSeqNumber(), 0, 8);
    try {
      arrayOfByte2 = ArcFourHmac.decryptRaw(this.keybytes, 22, ZERO_IV, paramArrayOfByte1, paramInt1, paramInt2, arrayOfByte1);
    } catch (GeneralSecurityException generalSecurityException) {
      GSSException gSSException = new GSSException(11, -1, "Could not use ArcFour Cipher - " + generalSecurityException.getMessage());
      gSSException.initCause(generalSecurityException);
      throw gSSException;
    } 
    byte b = arrayOfByte2[arrayOfByte2.length - 1];
    if (b < 1)
      throw new GSSException(10, -1, "Invalid padding on Wrap Token"); 
    paramWrapToken.padding = WrapToken.pads[b];
    int i = arrayOfByte2.length - 8 - b;
    System.arraycopy(arrayOfByte2, 8, paramArrayOfByte2, paramInt3, i);
    System.arraycopy(arrayOfByte2, 0, paramWrapToken.confounder, 0, 8);
  }
  
  private byte[] arcFourEncrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3) throws GSSException {
    byte[] arrayOfByte1 = new byte[paramArrayOfByte1.length + paramInt2 + paramArrayOfByte3.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, paramArrayOfByte1.length);
    System.arraycopy(paramArrayOfByte2, paramInt1, arrayOfByte1, paramArrayOfByte1.length, paramInt2);
    System.arraycopy(paramArrayOfByte3, 0, arrayOfByte1, paramArrayOfByte1.length + paramInt2, paramArrayOfByte3.length);
    byte[] arrayOfByte2 = new byte[4];
    WrapToken.writeBigEndian(paramWrapToken.getSequenceNumber(), arrayOfByte2);
    try {
      return ArcFourHmac.encryptRaw(this.keybytes, 22, arrayOfByte2, arrayOfByte1, 0, arrayOfByte1.length);
    } catch (Exception exception) {
      GSSException gSSException = new GSSException(11, -1, "Could not use ArcFour Cipher - " + exception.getMessage());
      gSSException.initCause(exception);
      throw gSSException;
    } 
  }
  
  private byte[] aes128Encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, int paramInt3) throws GSSException {
    byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramInt2 + paramArrayOfByte2.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
    System.arraycopy(paramArrayOfByte3, paramInt1, arrayOfByte, paramArrayOfByte1.length, paramInt2);
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, paramArrayOfByte1.length + paramInt2, paramArrayOfByte2.length);
    try {
      return Aes128.encryptRaw(this.keybytes, paramInt3, ZERO_IV_AES, arrayOfByte, 0, arrayOfByte.length);
    } catch (Exception exception) {
      GSSException gSSException = new GSSException(11, -1, "Could not use AES128 Cipher - " + exception.getMessage());
      gSSException.initCause(exception);
      throw gSSException;
    } 
  }
  
  private void aes128Decrypt(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4) throws GSSException {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = Aes128.decryptRaw(this.keybytes, paramInt4, ZERO_IV_AES, paramArrayOfByte1, paramInt1, paramInt2);
    } catch (GeneralSecurityException generalSecurityException) {
      GSSException gSSException = new GSSException(11, -1, "Could not use AES128 Cipher - " + generalSecurityException.getMessage());
      gSSException.initCause(generalSecurityException);
      throw gSSException;
    } 
    int i = arrayOfByte.length - 16 - 16;
    System.arraycopy(arrayOfByte, 16, paramArrayOfByte2, paramInt3, i);
  }
  
  private byte[] aes256Encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, int paramInt3) throws GSSException {
    byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramInt2 + paramArrayOfByte2.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
    System.arraycopy(paramArrayOfByte3, paramInt1, arrayOfByte, paramArrayOfByte1.length, paramInt2);
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, paramArrayOfByte1.length + paramInt2, paramArrayOfByte2.length);
    try {
      return Aes256.encryptRaw(this.keybytes, paramInt3, ZERO_IV_AES, arrayOfByte, 0, arrayOfByte.length);
    } catch (Exception exception) {
      GSSException gSSException = new GSSException(11, -1, "Could not use AES256 Cipher - " + exception.getMessage());
      gSSException.initCause(exception);
      throw gSSException;
    } 
  }
  
  private void aes256Decrypt(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4) throws GSSException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = Aes256.decryptRaw(this.keybytes, paramInt4, ZERO_IV_AES, paramArrayOfByte1, paramInt1, paramInt2);
    } catch (GeneralSecurityException generalSecurityException) {
      GSSException gSSException = new GSSException(11, -1, "Could not use AES128 Cipher - " + generalSecurityException.getMessage());
      gSSException.initCause(generalSecurityException);
      throw gSSException;
    } 
    int i = arrayOfByte.length - 16 - 16;
    System.arraycopy(arrayOfByte, 16, paramArrayOfByte2, paramInt3, i);
  }
  
  class WrapTokenInputStream extends InputStream {
    private InputStream is;
    
    private int length;
    
    private int remaining;
    
    private int temp;
    
    public WrapTokenInputStream(InputStream param1InputStream, int param1Int) {
      this.is = param1InputStream;
      this.length = param1Int;
      this.remaining = param1Int;
    }
    
    public final int read() {
      if (this.remaining == 0)
        return -1; 
      this.temp = this.is.read();
      if (this.temp != -1)
        this.remaining -= this.temp; 
      return this.temp;
    }
    
    public final int read(byte[] param1ArrayOfByte) throws IOException {
      if (this.remaining == 0)
        return -1; 
      this.temp = Math.min(this.remaining, param1ArrayOfByte.length);
      this.temp = this.is.read(param1ArrayOfByte, 0, this.temp);
      if (this.temp != -1)
        this.remaining -= this.temp; 
      return this.temp;
    }
    
    public final int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (this.remaining == 0)
        return -1; 
      this.temp = Math.min(this.remaining, param1Int2);
      this.temp = this.is.read(param1ArrayOfByte, param1Int1, this.temp);
      if (this.temp != -1)
        this.remaining -= this.temp; 
      return this.temp;
    }
    
    public final long skip(long param1Long) throws IOException {
      if (this.remaining == 0)
        return 0L; 
      this.temp = (int)Math.min(this.remaining, param1Long);
      this.temp = (int)this.is.skip(this.temp);
      this.remaining -= this.temp;
      return this.temp;
    }
    
    public final int available() { return Math.min(this.remaining, this.is.available()); }
    
    public final void close() throws IOException { this.remaining = 0; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\CipherHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */