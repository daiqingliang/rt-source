package sun.security.krb5;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.crypto.CksumType;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Checksum {
  private int cksumType;
  
  private byte[] checksum;
  
  public static final int CKSUMTYPE_NULL = 0;
  
  public static final int CKSUMTYPE_CRC32 = 1;
  
  public static final int CKSUMTYPE_RSA_MD4 = 2;
  
  public static final int CKSUMTYPE_RSA_MD4_DES = 3;
  
  public static final int CKSUMTYPE_DES_MAC = 4;
  
  public static final int CKSUMTYPE_DES_MAC_K = 5;
  
  public static final int CKSUMTYPE_RSA_MD4_DES_K = 6;
  
  public static final int CKSUMTYPE_RSA_MD5 = 7;
  
  public static final int CKSUMTYPE_RSA_MD5_DES = 8;
  
  public static final int CKSUMTYPE_HMAC_SHA1_DES3_KD = 12;
  
  public static final int CKSUMTYPE_HMAC_SHA1_96_AES128 = 15;
  
  public static final int CKSUMTYPE_HMAC_SHA1_96_AES256 = 16;
  
  public static final int CKSUMTYPE_HMAC_MD5_ARCFOUR = -138;
  
  static int CKSUMTYPE_DEFAULT;
  
  static int SAFECKSUMTYPE_DEFAULT;
  
  private static boolean DEBUG = Krb5.DEBUG;
  
  public static void initStatic() {
    String str = null;
    Config config = null;
    try {
      config = Config.getInstance();
      str = config.get(new String[] { "libdefaults", "default_checksum" });
      if (str != null) {
        CKSUMTYPE_DEFAULT = Config.getType(str);
      } else {
        CKSUMTYPE_DEFAULT = 7;
      } 
    } catch (Exception exception) {
      if (DEBUG) {
        System.out.println("Exception in getting default checksum value from the configuration Setting default checksum to be RSA-MD5");
        exception.printStackTrace();
      } 
      CKSUMTYPE_DEFAULT = 7;
    } 
    try {
      str = config.get(new String[] { "libdefaults", "safe_checksum_type" });
      if (str != null) {
        SAFECKSUMTYPE_DEFAULT = Config.getType(str);
      } else {
        SAFECKSUMTYPE_DEFAULT = 8;
      } 
    } catch (Exception exception) {
      if (DEBUG) {
        System.out.println("Exception in getting safe default checksum value from the configuration Setting  safe default checksum to be RSA-MD5");
        exception.printStackTrace();
      } 
      SAFECKSUMTYPE_DEFAULT = 8;
    } 
  }
  
  public Checksum(byte[] paramArrayOfByte, int paramInt) {
    this.cksumType = paramInt;
    this.checksum = paramArrayOfByte;
  }
  
  public Checksum(int paramInt, byte[] paramArrayOfByte) throws KdcErrException, KrbCryptoException {
    this.cksumType = paramInt;
    CksumType cksumType1 = CksumType.getInstance(this.cksumType);
    if (!cksumType1.isSafe()) {
      this.checksum = cksumType1.calculateChecksum(paramArrayOfByte, paramArrayOfByte.length);
    } else {
      throw new KdcErrException(50);
    } 
  }
  
  public Checksum(int paramInt1, byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey, int paramInt2) throws KdcErrException, KrbApErrException, KrbCryptoException {
    this.cksumType = paramInt1;
    CksumType cksumType1 = CksumType.getInstance(this.cksumType);
    if (!cksumType1.isSafe())
      throw new KrbApErrException(50); 
    this.checksum = cksumType1.calculateKeyedChecksum(paramArrayOfByte, paramArrayOfByte.length, paramEncryptionKey.getBytes(), paramInt2);
  }
  
  public boolean verifyKeyedChecksum(byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey, int paramInt) throws KdcErrException, KrbApErrException, KrbCryptoException {
    CksumType cksumType1 = CksumType.getInstance(this.cksumType);
    if (!cksumType1.isSafe())
      throw new KrbApErrException(50); 
    return cksumType1.verifyKeyedChecksum(paramArrayOfByte, paramArrayOfByte.length, paramEncryptionKey.getBytes(), this.checksum, paramInt);
  }
  
  boolean isEqual(Checksum paramChecksum) throws KdcErrException {
    CksumType cksumType1;
    return (this.cksumType != paramChecksum.cksumType) ? false : (cksumType1 = CksumType.getInstance(this.cksumType)).isChecksumEqual(this.checksum, paramChecksum.checksum);
  }
  
  private Checksum(DerValue paramDerValue) throws Asn1Exception, IOException {
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    DerValue derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 0) {
      this.cksumType = derValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    } 
    derValue = paramDerValue.getData().getDerValue();
    if ((derValue.getTag() & 0x1F) == 1) {
      this.checksum = derValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    } 
    if (paramDerValue.getData().available() > 0)
      throw new Asn1Exception(906); 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(BigInteger.valueOf(this.cksumType));
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.putOctetString(this.checksum);
    derOutputStream1.write(DerValue.createTag(-128, true, (byte)1), derOutputStream2);
    derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    return derOutputStream2.toByteArray();
  }
  
  public static Checksum parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new Checksum(derValue2);
  }
  
  public final byte[] getBytes() throws Asn1Exception, IOException { return this.checksum; }
  
  public final int getType() { return this.cksumType; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Checksum))
      return false; 
    try {
      return isEqual((Checksum)paramObject);
    } catch (KdcErrException kdcErrException) {
      return false;
    } 
  }
  
  public int hashCode() {
    int i = 17;
    i = 37 * i + this.cksumType;
    if (this.checksum != null)
      i = 37 * i + Arrays.hashCode(this.checksum); 
    return i;
  }
  
  static  {
    initStatic();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\Checksum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */