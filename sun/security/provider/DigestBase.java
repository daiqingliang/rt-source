package sun.security.provider;

import java.security.DigestException;
import java.security.MessageDigestSpi;
import java.security.ProviderException;

abstract class DigestBase extends MessageDigestSpi implements Cloneable {
  private byte[] oneByte;
  
  private final String algorithm;
  
  private final int digestLength;
  
  private final int blockSize;
  
  byte[] buffer;
  
  private int bufOfs;
  
  long bytesProcessed;
  
  static final byte[] padding = new byte[136];
  
  DigestBase(String paramString, int paramInt1, int paramInt2) {
    this.algorithm = paramString;
    this.digestLength = paramInt1;
    this.blockSize = paramInt2;
    this.buffer = new byte[paramInt2];
  }
  
  protected final int engineGetDigestLength() { return this.digestLength; }
  
  protected final void engineUpdate(byte paramByte) {
    if (this.oneByte == null)
      this.oneByte = new byte[1]; 
    this.oneByte[0] = paramByte;
    engineUpdate(this.oneByte, 0, 1);
  }
  
  protected final void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return; 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new ArrayIndexOutOfBoundsException(); 
    if (this.bytesProcessed < 0L)
      engineReset(); 
    this.bytesProcessed += paramInt2;
    if (this.bufOfs != 0) {
      int i = Math.min(paramInt2, this.blockSize - this.bufOfs);
      System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, this.bufOfs, i);
      this.bufOfs += i;
      paramInt1 += i;
      paramInt2 -= i;
      if (this.bufOfs >= this.blockSize) {
        implCompress(this.buffer, 0);
        this.bufOfs = 0;
      } 
    } 
    if (paramInt2 >= this.blockSize) {
      int i = paramInt1 + paramInt2;
      paramInt1 = implCompressMultiBlock(paramArrayOfByte, paramInt1, i - this.blockSize);
      paramInt2 = i - paramInt1;
    } 
    if (paramInt2 > 0) {
      System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, 0, paramInt2);
      this.bufOfs = paramInt2;
    } 
  }
  
  private int implCompressMultiBlock(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    while (paramInt1 <= paramInt2) {
      implCompress(paramArrayOfByte, paramInt1);
      paramInt1 += this.blockSize;
    } 
    return paramInt1;
  }
  
  protected final void engineReset() {
    if (this.bytesProcessed == 0L)
      return; 
    implReset();
    this.bufOfs = 0;
    this.bytesProcessed = 0L;
  }
  
  protected final byte[] engineDigest() {
    byte[] arrayOfByte = new byte[this.digestLength];
    try {
      engineDigest(arrayOfByte, 0, arrayOfByte.length);
    } catch (DigestException digestException) {
      throw (ProviderException)(new ProviderException("Internal error")).initCause(digestException);
    } 
    return arrayOfByte;
  }
  
  protected final int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 < this.digestLength)
      throw new DigestException("Length must be at least " + this.digestLength + " for " + this.algorithm + "digests"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new DigestException("Buffer too short to store digest"); 
    if (this.bytesProcessed < 0L)
      engineReset(); 
    implDigest(paramArrayOfByte, paramInt1);
    this.bytesProcessed = -1L;
    return this.digestLength;
  }
  
  abstract void implCompress(byte[] paramArrayOfByte, int paramInt);
  
  abstract void implDigest(byte[] paramArrayOfByte, int paramInt);
  
  abstract void implReset();
  
  public Object clone() throws CloneNotSupportedException {
    DigestBase digestBase = (DigestBase)super.clone();
    digestBase.buffer = (byte[])digestBase.buffer.clone();
    return digestBase;
  }
  
  static  {
    padding[0] = Byte.MIN_VALUE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\DigestBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */