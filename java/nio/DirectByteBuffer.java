package java.nio;

import java.io.FileDescriptor;
import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.nio.ch.DirectBuffer;

class DirectByteBuffer extends MappedByteBuffer implements DirectBuffer {
  protected static final Unsafe unsafe = Bits.unsafe();
  
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(byte[].class);
  
  protected static final boolean unaligned = Bits.unaligned();
  
  private final Object att;
  
  private final Cleaner cleaner;
  
  public Object attachment() { return this.att; }
  
  public Cleaner cleaner() { return this.cleaner; }
  
  DirectByteBuffer(int paramInt) {
    super(-1, 0, paramInt, paramInt);
    boolean bool = VM.isDirectMemoryPageAligned();
    int i = Bits.pageSize();
    long l1 = Math.max(1L, paramInt + (bool ? i : 0));
    Bits.reserveMemory(l1, paramInt);
    long l2 = 0L;
    try {
      l2 = unsafe.allocateMemory(l1);
    } catch (OutOfMemoryError outOfMemoryError) {
      Bits.unreserveMemory(l1, paramInt);
      throw outOfMemoryError;
    } 
    unsafe.setMemory(l2, l1, (byte)0);
    if (bool && l2 % i != 0L) {
      this.address = l2 + i - (l2 & (i - 1));
    } else {
      this.address = l2;
    } 
    this.cleaner = Cleaner.create(this, new Deallocator(l2, l1, paramInt, null));
    this.att = null;
  }
  
  DirectByteBuffer(long paramLong, int paramInt, Object paramObject) {
    super(-1, 0, paramInt, paramInt);
    this.address = paramLong;
    this.cleaner = null;
    this.att = paramObject;
  }
  
  private DirectByteBuffer(long paramLong, int paramInt) {
    super(-1, 0, paramInt, paramInt);
    this.address = paramLong;
    this.cleaner = null;
    this.att = null;
  }
  
  protected DirectByteBuffer(int paramInt, long paramLong, FileDescriptor paramFileDescriptor, Runnable paramRunnable) {
    super(-1, 0, paramInt, paramInt, paramFileDescriptor);
    this.address = paramLong;
    this.cleaner = Cleaner.create(this, paramRunnable);
    this.att = null;
  }
  
  DirectByteBuffer(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = paramDirectBuffer.address() + paramInt5;
    this.cleaner = null;
    this.att = paramDirectBuffer;
  }
  
  public ByteBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 0;
    assert m >= 0;
    return new DirectByteBuffer(this, -1, 0, k, k, m);
  }
  
  public ByteBuffer duplicate() { return new DirectByteBuffer(this, markValue(), position(), limit(), capacity(), 0); }
  
  public ByteBuffer asReadOnlyBuffer() { return new DirectByteBufferR(this, markValue(), position(), limit(), capacity(), 0); }
  
  public long address() { return this.address; }
  
  private long ix(int paramInt) { return this.address + (paramInt << false); }
  
  public byte get() { return unsafe.getByte(ix(nextGetIndex())); }
  
  public byte get(int paramInt) { return unsafe.getByte(ix(checkIndex(paramInt))); }
  
  public ByteBuffer get(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 << false > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfByte.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferUnderflowException(); 
      Bits.copyToArray(ix(i), paramArrayOfByte, arrayBaseOffset, paramInt1 << false, paramInt2 << false);
      position(i + paramInt2);
    } else {
      super.get(paramArrayOfByte, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public ByteBuffer put(byte paramByte) {
    unsafe.putByte(ix(nextPutIndex()), paramByte);
    return this;
  }
  
  public ByteBuffer put(int paramInt, byte paramByte) {
    unsafe.putByte(ix(checkIndex(paramInt)), paramByte);
    return this;
  }
  
  public ByteBuffer put(ByteBuffer paramByteBuffer) {
    if (paramByteBuffer instanceof DirectByteBuffer) {
      if (paramByteBuffer == this)
        throw new IllegalArgumentException(); 
      DirectByteBuffer directByteBuffer = (DirectByteBuffer)paramByteBuffer;
      int i = directByteBuffer.position();
      int j = directByteBuffer.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      int m = position();
      int n = limit();
      assert m <= n;
      int i1 = (m <= n) ? (n - m) : 0;
      if (k > i1)
        throw new BufferOverflowException(); 
      unsafe.copyMemory(directByteBuffer.ix(i), ix(m), k << false);
      directByteBuffer.position(i + k);
      position(m + k);
    } else if (paramByteBuffer.hb != null) {
      int i = paramByteBuffer.position();
      int j = paramByteBuffer.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      put(paramByteBuffer.hb, paramByteBuffer.offset + i, k);
      paramByteBuffer.position(i + k);
    } else {
      super.put(paramByteBuffer);
    } 
    return this;
  }
  
  public ByteBuffer put(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 << false > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfByte.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferOverflowException(); 
      Bits.copyFromArray(paramArrayOfByte, arrayBaseOffset, paramInt1 << false, ix(i), paramInt2 << false);
      position(i + paramInt2);
    } else {
      super.put(paramArrayOfByte, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public ByteBuffer compact() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    unsafe.copyMemory(ix(i), ix(0), k << false);
    position(k);
    limit(capacity());
    discardMark();
    return this;
  }
  
  public boolean isDirect() { return true; }
  
  public boolean isReadOnly() { return false; }
  
  byte _get(int paramInt) { return unsafe.getByte(this.address + paramInt); }
  
  void _put(int paramInt, byte paramByte) { unsafe.putByte(this.address + paramInt, paramByte); }
  
  private char getChar(long paramLong) {
    if (unaligned) {
      char c = unsafe.getChar(paramLong);
      return this.nativeByteOrder ? c : Bits.swap(c);
    } 
    return Bits.getChar(paramLong, this.bigEndian);
  }
  
  public char getChar() { return getChar(ix(nextGetIndex(2))); }
  
  public char getChar(int paramInt) { return getChar(ix(checkIndex(paramInt, 2))); }
  
  private ByteBuffer putChar(long paramLong, char paramChar) {
    if (unaligned) {
      char c = paramChar;
      unsafe.putChar(paramLong, this.nativeByteOrder ? c : Bits.swap(c));
    } else {
      Bits.putChar(paramLong, paramChar, this.bigEndian);
    } 
    return this;
  }
  
  public ByteBuffer putChar(char paramChar) {
    putChar(ix(nextPutIndex(2)), paramChar);
    return this;
  }
  
  public ByteBuffer putChar(int paramInt, char paramChar) {
    putChar(ix(checkIndex(paramInt, 2)), paramChar);
    return this;
  }
  
  public CharBuffer asCharBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 1;
    return (!unaligned && (this.address + i) % 2L != 0L) ? (this.bigEndian ? new ByteBufferAsCharBufferB(this, -1, 0, m, m, i) : new ByteBufferAsCharBufferL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectCharBufferU(this, -1, 0, m, m, i) : new DirectCharBufferS(this, -1, 0, m, m, i));
  }
  
  private short getShort(long paramLong) {
    if (unaligned) {
      short s = unsafe.getShort(paramLong);
      return this.nativeByteOrder ? s : Bits.swap(s);
    } 
    return Bits.getShort(paramLong, this.bigEndian);
  }
  
  public short getShort() { return getShort(ix(nextGetIndex(2))); }
  
  public short getShort(int paramInt) { return getShort(ix(checkIndex(paramInt, 2))); }
  
  private ByteBuffer putShort(long paramLong, short paramShort) {
    if (unaligned) {
      short s = paramShort;
      unsafe.putShort(paramLong, this.nativeByteOrder ? s : Bits.swap(s));
    } else {
      Bits.putShort(paramLong, paramShort, this.bigEndian);
    } 
    return this;
  }
  
  public ByteBuffer putShort(short paramShort) {
    putShort(ix(nextPutIndex(2)), paramShort);
    return this;
  }
  
  public ByteBuffer putShort(int paramInt, short paramShort) {
    putShort(ix(checkIndex(paramInt, 2)), paramShort);
    return this;
  }
  
  public ShortBuffer asShortBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 1;
    return (!unaligned && (this.address + i) % 2L != 0L) ? (this.bigEndian ? new ByteBufferAsShortBufferB(this, -1, 0, m, m, i) : new ByteBufferAsShortBufferL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectShortBufferU(this, -1, 0, m, m, i) : new DirectShortBufferS(this, -1, 0, m, m, i));
  }
  
  private int getInt(long paramLong) {
    if (unaligned) {
      int i = unsafe.getInt(paramLong);
      return this.nativeByteOrder ? i : Bits.swap(i);
    } 
    return Bits.getInt(paramLong, this.bigEndian);
  }
  
  public int getInt() { return getInt(ix(nextGetIndex(4))); }
  
  public int getInt(int paramInt) { return getInt(ix(checkIndex(paramInt, 4))); }
  
  private ByteBuffer putInt(long paramLong, int paramInt) {
    if (unaligned) {
      int i = paramInt;
      unsafe.putInt(paramLong, this.nativeByteOrder ? i : Bits.swap(i));
    } else {
      Bits.putInt(paramLong, paramInt, this.bigEndian);
    } 
    return this;
  }
  
  public ByteBuffer putInt(int paramInt) {
    putInt(ix(nextPutIndex(4)), paramInt);
    return this;
  }
  
  public ByteBuffer putInt(int paramInt1, int paramInt2) {
    putInt(ix(checkIndex(paramInt1, 4)), paramInt2);
    return this;
  }
  
  public IntBuffer asIntBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 2;
    return (!unaligned && (this.address + i) % 4L != 0L) ? (this.bigEndian ? new ByteBufferAsIntBufferB(this, -1, 0, m, m, i) : new ByteBufferAsIntBufferL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectIntBufferU(this, -1, 0, m, m, i) : new DirectIntBufferS(this, -1, 0, m, m, i));
  }
  
  private long getLong(long paramLong) {
    if (unaligned) {
      long l = unsafe.getLong(paramLong);
      return this.nativeByteOrder ? l : Bits.swap(l);
    } 
    return Bits.getLong(paramLong, this.bigEndian);
  }
  
  public long getLong() { return getLong(ix(nextGetIndex(8))); }
  
  public long getLong(int paramInt) { return getLong(ix(checkIndex(paramInt, 8))); }
  
  private ByteBuffer putLong(long paramLong1, long paramLong2) {
    if (unaligned) {
      long l = paramLong2;
      unsafe.putLong(paramLong1, this.nativeByteOrder ? l : Bits.swap(l));
    } else {
      Bits.putLong(paramLong1, paramLong2, this.bigEndian);
    } 
    return this;
  }
  
  public ByteBuffer putLong(long paramLong) {
    putLong(ix(nextPutIndex(8)), paramLong);
    return this;
  }
  
  public ByteBuffer putLong(int paramInt, long paramLong) {
    putLong(ix(checkIndex(paramInt, 8)), paramLong);
    return this;
  }
  
  public LongBuffer asLongBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 3;
    return (!unaligned && (this.address + i) % 8L != 0L) ? (this.bigEndian ? new ByteBufferAsLongBufferB(this, -1, 0, m, m, i) : new ByteBufferAsLongBufferL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectLongBufferU(this, -1, 0, m, m, i) : new DirectLongBufferS(this, -1, 0, m, m, i));
  }
  
  private float getFloat(long paramLong) {
    if (unaligned) {
      int i = unsafe.getInt(paramLong);
      return Float.intBitsToFloat(this.nativeByteOrder ? i : Bits.swap(i));
    } 
    return Bits.getFloat(paramLong, this.bigEndian);
  }
  
  public float getFloat() { return getFloat(ix(nextGetIndex(4))); }
  
  public float getFloat(int paramInt) { return getFloat(ix(checkIndex(paramInt, 4))); }
  
  private ByteBuffer putFloat(long paramLong, float paramFloat) {
    if (unaligned) {
      int i = Float.floatToRawIntBits(paramFloat);
      unsafe.putInt(paramLong, this.nativeByteOrder ? i : Bits.swap(i));
    } else {
      Bits.putFloat(paramLong, paramFloat, this.bigEndian);
    } 
    return this;
  }
  
  public ByteBuffer putFloat(float paramFloat) {
    putFloat(ix(nextPutIndex(4)), paramFloat);
    return this;
  }
  
  public ByteBuffer putFloat(int paramInt, float paramFloat) {
    putFloat(ix(checkIndex(paramInt, 4)), paramFloat);
    return this;
  }
  
  public FloatBuffer asFloatBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 2;
    return (!unaligned && (this.address + i) % 4L != 0L) ? (this.bigEndian ? new ByteBufferAsFloatBufferB(this, -1, 0, m, m, i) : new ByteBufferAsFloatBufferL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectFloatBufferU(this, -1, 0, m, m, i) : new DirectFloatBufferS(this, -1, 0, m, m, i));
  }
  
  private double getDouble(long paramLong) {
    if (unaligned) {
      long l = unsafe.getLong(paramLong);
      return Double.longBitsToDouble(this.nativeByteOrder ? l : Bits.swap(l));
    } 
    return Bits.getDouble(paramLong, this.bigEndian);
  }
  
  public double getDouble() { return getDouble(ix(nextGetIndex(8))); }
  
  public double getDouble(int paramInt) { return getDouble(ix(checkIndex(paramInt, 8))); }
  
  private ByteBuffer putDouble(long paramLong, double paramDouble) {
    if (unaligned) {
      long l = Double.doubleToRawLongBits(paramDouble);
      unsafe.putLong(paramLong, this.nativeByteOrder ? l : Bits.swap(l));
    } else {
      Bits.putDouble(paramLong, paramDouble, this.bigEndian);
    } 
    return this;
  }
  
  public ByteBuffer putDouble(double paramDouble) {
    putDouble(ix(nextPutIndex(8)), paramDouble);
    return this;
  }
  
  public ByteBuffer putDouble(int paramInt, double paramDouble) {
    putDouble(ix(checkIndex(paramInt, 8)), paramDouble);
    return this;
  }
  
  public DoubleBuffer asDoubleBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = k >> 3;
    return (!unaligned && (this.address + i) % 8L != 0L) ? (this.bigEndian ? new ByteBufferAsDoubleBufferB(this, -1, 0, m, m, i) : new ByteBufferAsDoubleBufferL(this, -1, 0, m, m, i)) : (this.nativeByteOrder ? new DirectDoubleBufferU(this, -1, 0, m, m, i) : new DirectDoubleBufferS(this, -1, 0, m, m, i));
  }
  
  private static class Deallocator implements Runnable {
    private static Unsafe unsafe = Unsafe.getUnsafe();
    
    private long address;
    
    private long size;
    
    private int capacity;
    
    private Deallocator(long param1Long1, long param1Long2, int param1Int) {
      assert param1Long1 != 0L;
      this.address = param1Long1;
      this.size = param1Long2;
      this.capacity = param1Int;
    }
    
    public void run() {
      if (this.address == 0L)
        return; 
      unsafe.freeMemory(this.address);
      this.address = 0L;
      Bits.unreserveMemory(this.size, this.capacity);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */