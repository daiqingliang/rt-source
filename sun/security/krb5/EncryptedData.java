package sun.security.krb5;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.crypto.EType;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncryptedData implements Cloneable {
  int eType;
  
  Integer kvno;
  
  byte[] cipher;
  
  byte[] plain;
  
  public static final int ETYPE_NULL = 0;
  
  public static final int ETYPE_DES_CBC_CRC = 1;
  
  public static final int ETYPE_DES_CBC_MD4 = 2;
  
  public static final int ETYPE_DES_CBC_MD5 = 3;
  
  public static final int ETYPE_ARCFOUR_HMAC = 23;
  
  public static final int ETYPE_ARCFOUR_HMAC_EXP = 24;
  
  public static final int ETYPE_DES3_CBC_HMAC_SHA1_KD = 16;
  
  public static final int ETYPE_AES128_CTS_HMAC_SHA1_96 = 17;
  
  public static final int ETYPE_AES256_CTS_HMAC_SHA1_96 = 18;
  
  private EncryptedData() {}
  
  public Object clone() {
    EncryptedData encryptedData = new EncryptedData();
    encryptedData.eType = this.eType;
    if (this.kvno != null)
      encryptedData.kvno = new Integer(this.kvno.intValue()); 
    if (this.cipher != null) {
      encryptedData.cipher = new byte[this.cipher.length];
      System.arraycopy(this.cipher, 0, encryptedData.cipher, 0, this.cipher.length);
    } 
    return encryptedData;
  }
  
  public EncryptedData(int paramInt, Integer paramInteger, byte[] paramArrayOfByte) {
    this.eType = paramInt;
    this.kvno = paramInteger;
    this.cipher = paramArrayOfByte;
  }
  
  public EncryptedData(EncryptionKey paramEncryptionKey, byte[] paramArrayOfByte, int paramInt) throws KdcErrException, KrbCryptoException {
    EType eType1 = EType.getInstance(paramEncryptionKey.getEType());
    this.cipher = eType1.encrypt(paramArrayOfByte, paramEncryptionKey.getBytes(), paramInt);
    this.eType = paramEncryptionKey.getEType();
    this.kvno = paramEncryptionKey.getKeyVersionNumber();
  }
  
  public byte[] decrypt(EncryptionKey paramEncryptionKey, int paramInt) throws KdcErrException, KrbApErrException, KrbCryptoException {
    if (this.eType != paramEncryptionKey.getEType())
      throw new KrbCryptoException("EncryptedData is encrypted using keytype " + EType.toString(this.eType) + " but decryption key is of type " + EType.toString(paramEncryptionKey.getEType())); 
    EType eType1 = EType.getInstance(this.eType);
    this.plain = eType1.decrypt(this.cipher, paramEncryptionKey.getBytes(), paramInt);
    return eType1.decryptedData(this.plain);
  }
  
  private byte[] decryptedData() throws KdcErrException {
    if (this.plain != null) {
      EType eType1 = EType.getInstance(this.eType);
      return eType1.decryptedData(this.plain);
    } 
    return null;
  }
  
  private EncryptedData(DerValue paramDerValue) throws Asn1Exception, IOException {
    DerValue derValue = null;
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      this.eType = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    if ((paramDerValue.getData().peekByte() & 0x1F) == 1) {
      derValue = paramDerValue.getData().getDerValue();
      int i = derValue.getData().getBigInteger().intValue();
      this.kvno = new Integer(i);
    } else {
      this.kvno = null;
    } 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 2) {
      this.cipher = derValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    } 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws KdcErrException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(BigInteger.valueOf(this.eType));
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    if (this.kvno != null) {
      derOutputStream2.putInteger(BigInteger.valueOf(this.kvno.longValue()));
      derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
      derOutputStream2 = new DerOutputStream();
    } 
    derOutputStream2.putOctetString(this.cipher);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)2), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public static EncryptedData parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new EncryptedData(derValue2);
  }
  
  public byte[] reset(byte[] paramArrayOfByte) {
    byte[] arrayOfByte = null;
    if ((paramArrayOfByte[1] & 0xFF) < 128) {
      arrayOfByte = new byte[paramArrayOfByte[1] + 2];
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte[1] + 2);
    } else if ((paramArrayOfByte[1] & 0xFF) > 128) {
      byte b = paramArrayOfByte[1] & 0x7F;
      byte b1 = 0;
      for (byte b2 = 0; b2 < b; b2++)
        b1 |= (paramArrayOfByte[b2 + 2] & 0xFF) << 8 * (b - b2 - 1); 
      arrayOfByte = new byte[b1 + b + 2];
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, b1 + b + 2);
    } 
    return arrayOfByte;
  }
  
  public int getEType() { return this.eType; }
  
  public Integer getKeyVersionNumber() { return this.kvno; }
  
  public byte[] getBytes() throws KdcErrException { return this.cipher; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\EncryptedData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */