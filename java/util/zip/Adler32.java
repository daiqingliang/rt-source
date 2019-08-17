package java.util.zip;

import java.nio.ByteBuffer;
import sun.nio.ch.DirectBuffer;

public class Adler32 implements Checksum {
  private int adler = 1;
  
  public void update(int paramInt) { this.adler = update(this.adler, paramInt); }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new ArrayIndexOutOfBoundsException(); 
    this.adler = updateBytes(this.adler, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void update(byte[] paramArrayOfByte) { this.adler = updateBytes(this.adler, paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void update(ByteBuffer paramByteBuffer) {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    int k = j - i;
    if (k <= 0)
      return; 
    if (paramByteBuffer instanceof DirectBuffer) {
      this.adler = updateByteBuffer(this.adler, ((DirectBuffer)paramByteBuffer).address(), i, k);
    } else if (paramByteBuffer.hasArray()) {
      this.adler = updateBytes(this.adler, paramByteBuffer.array(), i + paramByteBuffer.arrayOffset(), k);
    } else {
      byte[] arrayOfByte = new byte[k];
      paramByteBuffer.get(arrayOfByte);
      this.adler = updateBytes(this.adler, arrayOfByte, 0, arrayOfByte.length);
    } 
    paramByteBuffer.position(j);
  }
  
  public void reset() { this.adler = 1; }
  
  public long getValue() { return this.adler & 0xFFFFFFFFL; }
  
  private static native int update(int paramInt1, int paramInt2);
  
  private static native int updateBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  private static native int updateByteBuffer(int paramInt1, long paramLong, int paramInt2, int paramInt3);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\Adler32.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */