package java.nio;

import java.io.FileDescriptor;
import sun.nio.ch.DirectBuffer;

class DirectByteBufferR extends DirectByteBuffer implements DirectBuffer {
  DirectByteBufferR(int paramInt) { super(paramInt); }
  
  protected DirectByteBufferR(int paramInt, long paramLong, FileDescriptor paramFileDescriptor, Runnable paramRunnable) { super(paramInt, paramLong, paramFileDescriptor, paramRunnable); }
  
  DirectByteBufferR(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramDirectBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public ByteBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 0;
    assert m >= 0;
    return new DirectByteBufferR(this, -1, 0, k, k, m);
  }
  
  public ByteBuffer duplicate() { return new DirectByteBufferR(this, markValue(), position(), limit(), capacity(), 0); }
  
  public ByteBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public ByteBuffer put(byte paramByte) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer put(int paramInt, byte paramByte) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer put(ByteBuffer paramByteBuffer) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer put(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public boolean isDirect() { return true; }
  
  public boolean isReadOnly() { return true; }
  
  byte _get(int paramInt) { return unsafe.getByte(this.address + paramInt); }
  
  void _put(int paramInt, byte paramByte) { throw new ReadOnlyBufferException(); }
  
  private ByteBuffer putChar(long paramLong, char paramChar) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putChar(char paramChar) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putChar(int paramInt, char paramChar) { throw new ReadOnlyBufferException(); }
  
  public CharBuffer asCharBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 1;
    return (!unaligned && (this.address + i) % 2L != 0L) ? (this.bigEndian ? new ByteBufferAsCharBufferRB(this, -1, 0, m, m, i) : new ByteBufferAsCharBufferRL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectCharBufferRU(this, -1, 0, m, m, i) : new DirectCharBufferRS(this, -1, 0, m, m, i));
  }
  
  private ByteBuffer putShort(long paramLong, short paramShort) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putShort(short paramShort) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putShort(int paramInt, short paramShort) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer asShortBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 1;
    return (!unaligned && (this.address + i) % 2L != 0L) ? (this.bigEndian ? new ByteBufferAsShortBufferRB(this, -1, 0, m, m, i) : new ByteBufferAsShortBufferRL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectShortBufferRU(this, -1, 0, m, m, i) : new DirectShortBufferRS(this, -1, 0, m, m, i));
  }
  
  private ByteBuffer putInt(long paramLong, int paramInt) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putInt(int paramInt) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putInt(int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public IntBuffer asIntBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 2;
    return (!unaligned && (this.address + i) % 4L != 0L) ? (this.bigEndian ? new ByteBufferAsIntBufferRB(this, -1, 0, m, m, i) : new ByteBufferAsIntBufferRL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectIntBufferRU(this, -1, 0, m, m, i) : new DirectIntBufferRS(this, -1, 0, m, m, i));
  }
  
  private ByteBuffer putLong(long paramLong1, long paramLong2) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putLong(long paramLong) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putLong(int paramInt, long paramLong) { throw new ReadOnlyBufferException(); }
  
  public LongBuffer asLongBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 3;
    return (!unaligned && (this.address + i) % 8L != 0L) ? (this.bigEndian ? new ByteBufferAsLongBufferRB(this, -1, 0, m, m, i) : new ByteBufferAsLongBufferRL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectLongBufferRU(this, -1, 0, m, m, i) : new DirectLongBufferRS(this, -1, 0, m, m, i));
  }
  
  private ByteBuffer putFloat(long paramLong, float paramFloat) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putFloat(float paramFloat) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putFloat(int paramInt, float paramFloat) { throw new ReadOnlyBufferException(); }
  
  public FloatBuffer asFloatBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 2;
    return (!unaligned && (this.address + i) % 4L != 0L) ? (this.bigEndian ? new ByteBufferAsFloatBufferRB(this, -1, 0, m, m, i) : new ByteBufferAsFloatBufferRL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectFloatBufferRU(this, -1, 0, m, m, i) : new DirectFloatBufferRS(this, -1, 0, m, m, i));
  }
  
  private ByteBuffer putDouble(long paramLong, double paramDouble) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putDouble(double paramDouble) { throw new ReadOnlyBufferException(); }
  
  public ByteBuffer putDouble(int paramInt, double paramDouble) { throw new ReadOnlyBufferException(); }
  
  public DoubleBuffer asDoubleBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 3;
    return (!unaligned && (this.address + i) % 8L != 0L) ? (this.bigEndian ? new ByteBufferAsDoubleBufferRB(this, -1, 0, m, m, i) : new ByteBufferAsDoubleBufferRL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectDoubleBufferRU(this, -1, 0, m, m, i) : new DirectDoubleBufferRS(this, -1, 0, m, m, i));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectByteBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */