package java.util;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class UUID extends Object implements Serializable, Comparable<UUID> {
  private static final long serialVersionUID = -4856846361193249489L;
  
  private final long mostSigBits;
  
  private final long leastSigBits;
  
  private UUID(byte[] paramArrayOfByte) {
    long l1 = 0L;
    long l2 = 0L;
    assert paramArrayOfByte.length == 16 : "data must be 16 bytes in length";
    byte b;
    for (b = 0; b < 8; b++)
      l1 = l1 << 8 | (paramArrayOfByte[b] & 0xFF); 
    for (b = 8; b < 16; b++)
      l2 = l2 << 8 | (paramArrayOfByte[b] & 0xFF); 
    this.mostSigBits = l1;
    this.leastSigBits = l2;
  }
  
  public UUID(long paramLong1, long paramLong2) {
    this.mostSigBits = paramLong1;
    this.leastSigBits = paramLong2;
  }
  
  public static UUID randomUUID() {
    SecureRandom secureRandom = Holder.numberGenerator;
    byte[] arrayOfByte = new byte[16];
    secureRandom.nextBytes(arrayOfByte);
    arrayOfByte[6] = (byte)(arrayOfByte[6] & 0xF);
    arrayOfByte[6] = (byte)(arrayOfByte[6] | 0x40);
    arrayOfByte[8] = (byte)(arrayOfByte[8] & 0x3F);
    arrayOfByte[8] = (byte)(arrayOfByte[8] | 0x80);
    return new UUID(arrayOfByte);
  }
  
  public static UUID nameUUIDFromBytes(byte[] paramArrayOfByte) {
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError("MD5 not supported", noSuchAlgorithmException);
    } 
    byte[] arrayOfByte = messageDigest.digest(paramArrayOfByte);
    arrayOfByte[6] = (byte)(arrayOfByte[6] & 0xF);
    arrayOfByte[6] = (byte)(arrayOfByte[6] | 0x30);
    arrayOfByte[8] = (byte)(arrayOfByte[8] & 0x3F);
    arrayOfByte[8] = (byte)(arrayOfByte[8] | 0x80);
    return new UUID(arrayOfByte);
  }
  
  public static UUID fromString(String paramString) {
    String[] arrayOfString = paramString.split("-");
    if (arrayOfString.length != 5)
      throw new IllegalArgumentException("Invalid UUID string: " + paramString); 
    for (byte b = 0; b < 5; b++)
      arrayOfString[b] = "0x" + arrayOfString[b]; 
    long l1 = Long.decode(arrayOfString[0]).longValue();
    l1 <<= 16;
    l1 |= Long.decode(arrayOfString[1]).longValue();
    l1 <<= 16;
    l1 |= Long.decode(arrayOfString[2]).longValue();
    long l2 = Long.decode(arrayOfString[3]).longValue();
    l2 <<= 48;
    l2 |= Long.decode(arrayOfString[4]).longValue();
    return new UUID(l1, l2);
  }
  
  public long getLeastSignificantBits() { return this.leastSigBits; }
  
  public long getMostSignificantBits() { return this.mostSigBits; }
  
  public int version() { return (int)(this.mostSigBits >> 12 & 0xFL); }
  
  public int variant() { return (int)(this.leastSigBits >>> (int)(64L - (this.leastSigBits >>> 62)) & this.leastSigBits >> 63); }
  
  public long timestamp() {
    if (version() != 1)
      throw new UnsupportedOperationException("Not a time-based UUID"); 
    return (this.mostSigBits & 0xFFFL) << 48 | (this.mostSigBits >> 16 & 0xFFFFL) << 32 | this.mostSigBits >>> 32;
  }
  
  public int clockSequence() {
    if (version() != 1)
      throw new UnsupportedOperationException("Not a time-based UUID"); 
    return (int)((this.leastSigBits & 0x3FFF000000000000L) >>> 48);
  }
  
  public long node() {
    if (version() != 1)
      throw new UnsupportedOperationException("Not a time-based UUID"); 
    return this.leastSigBits & 0xFFFFFFFFFFFFL;
  }
  
  public String toString() { return digits(this.mostSigBits >> 32, 8) + "-" + digits(this.mostSigBits >> 16, 4) + "-" + digits(this.mostSigBits, 4) + "-" + digits(this.leastSigBits >> 48, 4) + "-" + digits(this.leastSigBits, 12); }
  
  private static String digits(long paramLong, int paramInt) {
    long l = 1L << paramInt * 4;
    return Long.toHexString(l | paramLong & l - 1L).substring(1);
  }
  
  public int hashCode() {
    long l = this.mostSigBits ^ this.leastSigBits;
    return (int)(l >> 32) ^ (int)l;
  }
  
  public boolean equals(Object paramObject) {
    if (null == paramObject || paramObject.getClass() != UUID.class)
      return false; 
    UUID uUID = (UUID)paramObject;
    return (this.mostSigBits == uUID.mostSigBits && this.leastSigBits == uUID.leastSigBits);
  }
  
  public int compareTo(UUID paramUUID) { return (this.mostSigBits < paramUUID.mostSigBits) ? -1 : ((this.mostSigBits > paramUUID.mostSigBits) ? 1 : ((this.leastSigBits < paramUUID.leastSigBits) ? -1 : ((this.leastSigBits > paramUUID.leastSigBits) ? 1 : 0))); }
  
  private static class Holder {
    static final SecureRandom numberGenerator = new SecureRandom();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\UUID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */