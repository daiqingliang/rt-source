package sun.security.krb5.internal.crypto;

public class NullEType extends EType {
  public int eType() { return 0; }
  
  public int minimumPadSize() { return 0; }
  
  public int confounderSize() { return 0; }
  
  public int checksumType() { return 0; }
  
  public int checksumSize() { return 0; }
  
  public int blockSize() { return 1; }
  
  public int keyType() { return 0; }
  
  public int keySize() { return 0; }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) {
    byte[] arrayOfByte = new byte[paramArrayOfByte1.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
    return arrayOfByte;
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) {
    byte[] arrayOfByte = new byte[paramArrayOfByte1.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
    return arrayOfByte;
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) { return (byte[])paramArrayOfByte1.clone(); }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) { return (byte[])paramArrayOfByte1.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\NullEType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */