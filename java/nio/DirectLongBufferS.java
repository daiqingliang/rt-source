package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectLongBufferS extends LongBuffer implements DirectBuffer {
  protected static final Unsafe unsafe = Bits.unsafe();
  
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(long[].class);
  
  protected static final boolean unaligned = Bits.unaligned();
  
  private final Object att;
  
  public Object attachment() { return this.att; }
  
  public Cleaner cleaner() { return null; }
  
  DirectLongBufferS(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = paramDirectBuffer.address() + paramInt5;
    this.att = paramDirectBuffer;
  }
  
  public LongBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 3;
    assert m >= 0;
    return new DirectLongBufferS(this, -1, 0, k, k, m);
  }
  
  public LongBuffer duplicate() { return new DirectLongBufferS(this, markValue(), position(), limit(), capacity(), 0); }
  
  public LongBuffer asReadOnlyBuffer() { return new DirectLongBufferRS(this, markValue(), position(), limit(), capacity(), 0); }
  
  public long address() { return this.address; }
  
  private long ix(int paramInt) { return this.address + (paramInt << 3); }
  
  public long get() { return Bits.swap(unsafe.getLong(ix(nextGetIndex()))); }
  
  public long get(int paramInt) { return Bits.swap(unsafe.getLong(ix(checkIndex(paramInt)))); }
  
  public LongBuffer get(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    if (paramInt2 << 3 > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfLong.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferUnderflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToLongArray(ix(i), paramArrayOfLong, paramInt1 << 3, paramInt2 << 3);
      } else {
        Bits.copyToArray(ix(i), paramArrayOfLong, arrayBaseOffset, paramInt1 << 3, paramInt2 << 3);
      } 
      position(i + paramInt2);
    } else {
      super.get(paramArrayOfLong, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public LongBuffer put(long paramLong) {
    unsafe.putLong(ix(nextPutIndex()), Bits.swap(paramLong));
    return this;
  }
  
  public LongBuffer put(int paramInt, long paramLong) {
    unsafe.putLong(ix(checkIndex(paramInt)), Bits.swap(paramLong));
    return this;
  }
  
  public LongBuffer put(LongBuffer paramLongBuffer) {
    if (paramLongBuffer instanceof DirectLongBufferS) {
      if (paramLongBuffer == this)
        throw new IllegalArgumentException(); 
      DirectLongBufferS directLongBufferS = (DirectLongBufferS)paramLongBuffer;
      int i = directLongBufferS.position();
      int j = directLongBufferS.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      int m = position();
      int n = limit();
      assert m <= n;
      int i1 = (m <= n) ? (n - m) : 0;
      if (k > i1)
        throw new BufferOverflowException(); 
      unsafe.copyMemory(directLongBufferS.ix(i), ix(m), k << 3);
      directLongBufferS.position(i + k);
      position(m + k);
    } else if (paramLongBuffer.hb != null) {
      int i = paramLongBuffer.position();
      int j = paramLongBuffer.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      put(paramLongBuffer.hb, paramLongBuffer.offset + i, k);
      paramLongBuffer.position(i + k);
    } else {
      super.put(paramLongBuffer);
    } 
    return this;
  }
  
  public LongBuffer put(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    if (paramInt2 << 3 > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfLong.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferOverflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromLongArray(paramArrayOfLong, paramInt1 << 3, ix(i), paramInt2 << 3);
      } else {
        Bits.copyFromArray(paramArrayOfLong, arrayBaseOffset, paramInt1 << 3, ix(i), paramInt2 << 3);
      } 
      position(i + paramInt2);
    } else {
      super.put(paramArrayOfLong, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public LongBuffer compact() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    unsafe.copyMemory(ix(i), ix(0), k << 3);
    position(k);
    limit(capacity());
    discardMark();
    return this;
  }
  
  public boolean isDirect() { return true; }
  
  public boolean isReadOnly() { return false; }
  
  public ByteOrder order() { return (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectLongBufferS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */