package sun.security.krb5.internal.crypto;

public class Crc32CksumType extends CksumType {
  public int confounderSize() { return 0; }
  
  public int cksumType() { return 1; }
  
  public boolean isSafe() { return false; }
  
  public int cksumSize() { return 4; }
  
  public int keyType() { return 0; }
  
  public int keySize() { return 0; }
  
  public byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt) { return crc32.byte2crc32sum_bytes(paramArrayOfByte, paramInt); }
  
  public byte[] calculateKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2) { return null; }
  
  public boolean verifyKeyedChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2) { return false; }
  
  public static byte[] int2quad(long paramLong) {
    byte[] arrayOfByte = new byte[4];
    for (byte b = 0; b < 4; b++)
      arrayOfByte[b] = (byte)(int)(paramLong >>> b * 8 & 0xFFL); 
    return arrayOfByte;
  }
  
  public static long bytes2long(byte[] paramArrayOfByte) {
    null = 0L;
    null |= (paramArrayOfByte[0] & 0xFFL) << 24;
    null |= (paramArrayOfByte[1] & 0xFFL) << 16;
    null |= (paramArrayOfByte[2] & 0xFFL) << 8;
    return paramArrayOfByte[3] & 0xFFL;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\Crc32CksumType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */