package java.security;

import java.nio.ByteBuffer;
import sun.security.jca.JCAUtil;

public abstract class MessageDigestSpi {
  private byte[] tempArray;
  
  protected int engineGetDigestLength() { return 0; }
  
  protected abstract void engineUpdate(byte paramByte);
  
  protected abstract void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  protected void engineUpdate(ByteBuffer paramByteBuffer) {
    if (!paramByteBuffer.hasRemaining())
      return; 
    if (paramByteBuffer.hasArray()) {
      byte[] arrayOfByte = paramByteBuffer.array();
      int i = paramByteBuffer.arrayOffset();
      int j = paramByteBuffer.position();
      int k = paramByteBuffer.limit();
      engineUpdate(arrayOfByte, i + j, k - j);
      paramByteBuffer.position(k);
    } else {
      int i = paramByteBuffer.remaining();
      int j = JCAUtil.getTempArraySize(i);
      if (this.tempArray == null || j > this.tempArray.length)
        this.tempArray = new byte[j]; 
      while (i > 0) {
        int k = Math.min(i, this.tempArray.length);
        paramByteBuffer.get(this.tempArray, 0, k);
        engineUpdate(this.tempArray, 0, k);
        i -= k;
      } 
    } 
  }
  
  protected abstract byte[] engineDigest();
  
  protected int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws DigestException {
    byte[] arrayOfByte = engineDigest();
    if (paramInt2 < arrayOfByte.length)
      throw new DigestException("partial digests not returned"); 
    if (paramArrayOfByte.length - paramInt1 < arrayOfByte.length)
      throw new DigestException("insufficient space in the output buffer to store the digest"); 
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt1, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  protected abstract void engineReset();
  
  public Object clone() throws CloneNotSupportedException {
    if (this instanceof Cloneable)
      return super.clone(); 
    throw new CloneNotSupportedException();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\MessageDigestSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */