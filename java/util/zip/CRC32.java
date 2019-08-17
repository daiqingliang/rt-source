package java.util.zip;

import java.nio.ByteBuffer;
import sun.nio.ch.DirectBuffer;

public class CRC32 implements Checksum {
  private int crc;
  
  public void update(int paramInt) { this.crc = update(this.crc, paramInt); }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new ArrayIndexOutOfBoundsException(); 
    this.crc = updateBytes(this.crc, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void update(byte[] paramArrayOfByte) { this.crc = updateBytes(this.crc, paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void update(ByteBuffer paramByteBuffer) {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    int k = j - i;
    if (k <= 0)
      return; 
    if (paramByteBuffer instanceof DirectBuffer) {
      this.crc = updateByteBuffer(this.crc, ((DirectBuffer)paramByteBuffer).address(), i, k);
    } else if (paramByteBuffer.hasArray()) {
      this.crc = updateBytes(this.crc, paramByteBuffer.array(), i + paramByteBuffer.arrayOffset(), k);
    } else {
      byte[] arrayOfByte = new byte[k];
      paramByteBuffer.get(arrayOfByte);
      this.crc = updateBytes(this.crc, arrayOfByte, 0, arrayOfByte.length);
    } 
    paramByteBuffer.position(j);
  }
  
  public void reset() { this.crc = 0; }
  
  public long getValue() { return this.crc & 0xFFFFFFFFL; }
  
  private static native int update(int paramInt1, int paramInt2);
  
  private static native int updateBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  private static native int updateByteBuffer(int paramInt1, long paramLong, int paramInt2, int paramInt3);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\CRC32.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */