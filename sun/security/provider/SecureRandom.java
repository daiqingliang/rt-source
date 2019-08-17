package sun.security.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandomSpi;

public final class SecureRandom extends SecureRandomSpi implements Serializable {
  private static final long serialVersionUID = 3581829991155417889L;
  
  private static final int DIGEST_SIZE = 20;
  
  private MessageDigest digest;
  
  private byte[] state;
  
  private byte[] remainder;
  
  private int remCount;
  
  public SecureRandom() { init(null); }
  
  private SecureRandom(byte[] paramArrayOfByte) { init(paramArrayOfByte); }
  
  private void init(byte[] paramArrayOfByte) {
    try {
      this.digest = MessageDigest.getInstance("SHA", "SUN");
    } catch (NoSuchProviderException|NoSuchAlgorithmException noSuchProviderException) {
      try {
        this.digest = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new InternalError("internal error: SHA-1 not available.", noSuchAlgorithmException);
      } 
    } 
    if (paramArrayOfByte != null)
      engineSetSeed(paramArrayOfByte); 
  }
  
  public byte[] engineGenerateSeed(int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    SeedGenerator.generateSeed(arrayOfByte);
    return arrayOfByte;
  }
  
  public void engineSetSeed(byte[] paramArrayOfByte) {
    if (this.state != null) {
      this.digest.update(this.state);
      for (byte b = 0; b < this.state.length; b++)
        this.state[b] = 0; 
    } 
    this.state = this.digest.digest(paramArrayOfByte);
  }
  
  private static void updateState(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    byte b = 1;
    boolean bool = false;
    for (byte b1 = 0; b1 < paramArrayOfByte1.length; b1++) {
      byte b2 = paramArrayOfByte1[b1] + paramArrayOfByte2[b1] + b;
      byte b3 = (byte)b2;
      bool |= ((paramArrayOfByte1[b1] != b3) ? 1 : 0);
      paramArrayOfByte1[b1] = b3;
      b = b2 >> 8;
    } 
    if (!bool)
      paramArrayOfByte1[0] = (byte)(paramArrayOfByte1[0] + 1); 
  }
  
  public void engineNextBytes(byte[] paramArrayOfByte) {
    int i = 0;
    byte[] arrayOfByte = this.remainder;
    if (this.state == null) {
      byte[] arrayOfByte1 = new byte[20];
      seeder.engineNextBytes(arrayOfByte1);
      this.state = this.digest.digest(arrayOfByte1);
    } 
    int j = this.remCount;
    if (j > 0) {
      int k = (paramArrayOfByte.length - i < 20 - j) ? (paramArrayOfByte.length - i) : (20 - j);
      for (byte b = 0; b < k; b++) {
        paramArrayOfByte[b] = arrayOfByte[j];
        arrayOfByte[j++] = 0;
      } 
      this.remCount += k;
      i += k;
    } 
    while (i < paramArrayOfByte.length) {
      this.digest.update(this.state);
      arrayOfByte = this.digest.digest();
      updateState(this.state, arrayOfByte);
      int k = (paramArrayOfByte.length - i > 20) ? 20 : (paramArrayOfByte.length - i);
      for (byte b = 0; b < k; b++) {
        paramArrayOfByte[i++] = arrayOfByte[b];
        arrayOfByte[b] = 0;
      } 
      this.remCount += k;
    } 
    this.remainder = arrayOfByte;
    this.remCount %= 20;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.digest = MessageDigest.getInstance("SHA", "SUN");
    } catch (NoSuchProviderException|NoSuchAlgorithmException noSuchProviderException) {
      try {
        this.digest = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new InternalError("internal error: SHA-1 not available.", noSuchAlgorithmException);
      } 
    } 
  }
  
  private static class SeederHolder {
    private static final SecureRandom seeder = new SecureRandom(SeedGenerator.getSystemEntropy(), null);
    
    static  {
      byte[] arrayOfByte = new byte[20];
      SeedGenerator.generateSeed(arrayOfByte);
      seeder.engineSetSeed(arrayOfByte);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\SecureRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */