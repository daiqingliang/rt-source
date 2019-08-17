package sun.security.krb5;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.krb5.internal.crypto.Aes128;
import sun.security.krb5.internal.crypto.Aes256;
import sun.security.krb5.internal.crypto.ArcFourHmac;
import sun.security.krb5.internal.crypto.Des;
import sun.security.krb5.internal.crypto.Des3;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.ktab.KeyTab;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncryptionKey implements Cloneable {
  public static final EncryptionKey NULL_KEY = new EncryptionKey(new byte[0], 0, null);
  
  private int keyType;
  
  private byte[] keyValue;
  
  private Integer kvno;
  
  private static final boolean DEBUG = Krb5.DEBUG;
  
  public int getEType() { return this.keyType; }
  
  public final Integer getKeyVersionNumber() { return this.kvno; }
  
  public final byte[] getBytes() { return this.keyValue; }
  
  public Object clone() { return new EncryptionKey(this.keyValue, this.keyType, this.kvno); }
  
  public static EncryptionKey[] acquireSecretKeys(PrincipalName paramPrincipalName, String paramString) {
    if (paramPrincipalName == null)
      throw new IllegalArgumentException("Cannot have null pricipal name to look in keytab."); 
    KeyTab keyTab = KeyTab.getInstance(paramString);
    return keyTab.readServiceKeys(paramPrincipalName);
  }
  
  public static EncryptionKey acquireSecretKey(PrincipalName paramPrincipalName, char[] paramArrayOfChar, int paramInt, PAData.SaltAndParams paramSaltAndParams) throws KrbException {
    byte[] arrayOfByte;
    String str;
    if (paramSaltAndParams != null) {
      str = (paramSaltAndParams.salt != null) ? paramSaltAndParams.salt : paramPrincipalName.getSalt();
      arrayOfByte = paramSaltAndParams.params;
    } else {
      str = paramPrincipalName.getSalt();
      arrayOfByte = null;
    } 
    return acquireSecretKey(paramArrayOfChar, str, paramInt, arrayOfByte);
  }
  
  public static EncryptionKey acquireSecretKey(char[] paramArrayOfChar, String paramString, int paramInt, byte[] paramArrayOfByte) throws KrbException { return new EncryptionKey(stringToKey(paramArrayOfChar, paramString, paramArrayOfByte, paramInt), paramInt, null); }
  
  public static EncryptionKey[] acquireSecretKeys(char[] paramArrayOfChar, String paramString) throws KrbException {
    int[] arrayOfInt = EType.getDefaults("default_tkt_enctypes");
    EncryptionKey[] arrayOfEncryptionKey = new EncryptionKey[arrayOfInt.length];
    for (byte b = 0; b < arrayOfInt.length; b++) {
      if (EType.isSupported(arrayOfInt[b])) {
        arrayOfEncryptionKey[b] = new EncryptionKey(stringToKey(paramArrayOfChar, paramString, null, arrayOfInt[b]), arrayOfInt[b], null);
      } else if (DEBUG) {
        System.out.println("Encryption Type " + EType.toString(arrayOfInt[b]) + " is not supported/enabled");
      } 
    } 
    return arrayOfEncryptionKey;
  }
  
  public EncryptionKey(byte[] paramArrayOfByte, int paramInt, Integer paramInteger) {
    if (paramArrayOfByte != null) {
      this.keyValue = new byte[paramArrayOfByte.length];
      System.arraycopy(paramArrayOfByte, 0, this.keyValue, 0, paramArrayOfByte.length);
    } else {
      throw new IllegalArgumentException("EncryptionKey: Key bytes cannot be null!");
    } 
    this.keyType = paramInt;
    this.kvno = paramInteger;
  }
  
  public EncryptionKey(int paramInt, byte[] paramArrayOfByte) { this(paramArrayOfByte, paramInt, null); }
  
  private static byte[] stringToKey(char[] paramArrayOfChar, String paramString, byte[] paramArrayOfByte, int paramInt) throws KrbCryptoException {
    char[] arrayOfChar1 = paramString.toCharArray();
    arrayOfChar2 = new char[paramArrayOfChar.length + arrayOfChar1.length];
    System.arraycopy(paramArrayOfChar, 0, arrayOfChar2, 0, paramArrayOfChar.length);
    System.arraycopy(arrayOfChar1, 0, arrayOfChar2, paramArrayOfChar.length, arrayOfChar1.length);
    Arrays.fill(arrayOfChar1, '0');
    try {
      switch (paramInt) {
        case 1:
        case 3:
          return Des.string_to_key_bytes(arrayOfChar2);
        case 16:
          return Des3.stringToKey(arrayOfChar2);
        case 23:
          return ArcFourHmac.stringToKey(paramArrayOfChar);
        case 17:
          return Aes128.stringToKey(paramArrayOfChar, paramString, paramArrayOfByte);
        case 18:
          return Aes256.stringToKey(paramArrayOfChar, paramString, paramArrayOfByte);
      } 
      throw new IllegalArgumentException("encryption type " + EType.toString(paramInt) + " not supported");
    } catch (GeneralSecurityException generalSecurityException) {
      KrbCryptoException krbCryptoException = new KrbCryptoException(generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } finally {
      Arrays.fill(arrayOfChar2, '0');
    } 
  }
  
  public EncryptionKey(char[] paramArrayOfChar, String paramString1, String paramString2) throws KrbCryptoException {
    if (paramString2 == null || paramString2.equalsIgnoreCase("DES")) {
      this.keyType = 3;
    } else if (paramString2.equalsIgnoreCase("DESede")) {
      this.keyType = 16;
    } else if (paramString2.equalsIgnoreCase("AES128")) {
      this.keyType = 17;
    } else if (paramString2.equalsIgnoreCase("ArcFourHmac")) {
      this.keyType = 23;
    } else if (paramString2.equalsIgnoreCase("AES256")) {
      this.keyType = 18;
      if (!EType.isSupported(this.keyType))
        throw new IllegalArgumentException("Algorithm " + paramString2 + " not enabled"); 
    } else {
      throw new IllegalArgumentException("Algorithm " + paramString2 + " not supported");
    } 
    this.keyValue = stringToKey(paramArrayOfChar, paramString1, null, this.keyType);
    this.kvno = null;
  }
  
  public EncryptionKey(EncryptionKey paramEncryptionKey) throws KrbCryptoException {
    this.keyValue = Confounder.bytes(paramEncryptionKey.keyValue.length);
    for (b = 0; b < this.keyValue.length; b++)
      this.keyValue[b] = (byte)(this.keyValue[b] ^ paramEncryptionKey.keyValue[b]); 
    this.keyType = paramEncryptionKey.keyType;
    try {
      if (this.keyType == 3 || this.keyType == 1) {
        if (!DESKeySpec.isParityAdjusted(this.keyValue, 0))
          this.keyValue = Des.set_parity(this.keyValue); 
        if (DESKeySpec.isWeak(this.keyValue, 0))
          this.keyValue[7] = (byte)(this.keyValue[7] ^ 0xF0); 
      } 
      if (this.keyType == 16) {
        if (!DESedeKeySpec.isParityAdjusted(this.keyValue, 0))
          this.keyValue = Des3.parityFix(this.keyValue); 
        byte[] arrayOfByte = new byte[8];
        for (byte b1 = 0; b1 < this.keyValue.length; b1 += 8) {
          System.arraycopy(this.keyValue, b1, arrayOfByte, 0, 8);
          if (DESKeySpec.isWeak(arrayOfByte, 0))
            this.keyValue[b1 + 7] = (byte)(this.keyValue[b1 + 7] ^ 0xF0); 
        } 
      } 
    } catch (GeneralSecurityException b) {
      GeneralSecurityException generalSecurityException;
      KrbCryptoException krbCryptoException = new KrbCryptoException(generalSecurityException.getMessage());
      krbCryptoException.initCause(generalSecurityException);
      throw krbCryptoException;
    } 
  }
  
  public EncryptionKey(DerValue paramDerValue) throws Asn1Exception, IOException {
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      this.keyType = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 1) {
      this.keyValue = derValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    } 
    if (derValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(this.keyType);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.putOctetString(this.keyValue);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public void destroy() {
    if (this.keyValue != null)
      for (byte b = 0; b < this.keyValue.length; b++)
        this.keyValue[b] = 0;  
  }
  
  public static EncryptionKey parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new EncryptionKey(derValue2);
  }
  
  public void writeKey(CCacheOutputStream paramCCacheOutputStream) throws IOException {
    paramCCacheOutputStream.write16(this.keyType);
    paramCCacheOutputStream.write16(this.keyType);
    paramCCacheOutputStream.write32(this.keyValue.length);
    for (byte b = 0; b < this.keyValue.length; b++)
      paramCCacheOutputStream.write8(this.keyValue[b]); 
  }
  
  public String toString() { return new String("EncryptionKey: keyType=" + this.keyType + " kvno=" + this.kvno + " keyValue (hex dump)=" + ((this.keyValue == null || this.keyValue.length == 0) ? " Empty Key" : ('\n' + Krb5.hexDumper.encodeBuffer(this.keyValue) + '\n'))); }
  
  public static EncryptionKey findKey(int paramInt, EncryptionKey[] paramArrayOfEncryptionKey) throws KrbException { return findKey(paramInt, null, paramArrayOfEncryptionKey); }
  
  private static boolean versionMatches(Integer paramInteger1, Integer paramInteger2) { return (paramInteger1 == null || paramInteger1.intValue() == 0 || paramInteger2 == null || paramInteger2.intValue() == 0) ? true : paramInteger1.equals(paramInteger2); }
  
  public static EncryptionKey findKey(int paramInt, Integer paramInteger, EncryptionKey[] paramArrayOfEncryptionKey) throws KrbException {
    if (!EType.isSupported(paramInt))
      throw new KrbException("Encryption type " + EType.toString(paramInt) + " is not supported/enabled"); 
    boolean bool = false;
    int i = 0;
    EncryptionKey encryptionKey = null;
    byte b;
    for (b = 0; b < paramArrayOfEncryptionKey.length; b++) {
      int j = paramArrayOfEncryptionKey[b].getEType();
      if (EType.isSupported(j)) {
        Integer integer = paramArrayOfEncryptionKey[b].getKeyVersionNumber();
        if (paramInt == j) {
          bool = true;
          if (versionMatches(paramInteger, integer))
            return paramArrayOfEncryptionKey[b]; 
          if (integer.intValue() > i) {
            encryptionKey = paramArrayOfEncryptionKey[b];
            i = integer.intValue();
          } 
        } 
      } 
    } 
    if (paramInt == 1 || paramInt == 3)
      for (b = 0; b < paramArrayOfEncryptionKey.length; b++) {
        int j = paramArrayOfEncryptionKey[b].getEType();
        if (j == 1 || j == 3) {
          Integer integer = paramArrayOfEncryptionKey[b].getKeyVersionNumber();
          bool = true;
          if (versionMatches(paramInteger, integer))
            return new EncryptionKey(paramInt, paramArrayOfEncryptionKey[b].getBytes()); 
          if (integer.intValue() > i) {
            encryptionKey = new EncryptionKey(paramInt, paramArrayOfEncryptionKey[b].getBytes());
            i = integer.intValue();
          } 
        } 
      }  
    return bool ? encryptionKey : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\EncryptionKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */