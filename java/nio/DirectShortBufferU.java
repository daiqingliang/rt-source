package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectShortBufferU extends ShortBuffer implements DirectBuffer {
  protected static final Unsafe unsafe = Bits.unsafe();
  
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(short[].class);
  
  protected static final boolean unaligned = Bits.unaligned();
  
  private final Object att;
  
  public Object attachment() { return this.att; }
  
  public Cleaner cleaner() { return null; }
  
  DirectShortBufferU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = paramDirectBuffer.address() + paramInt5;
    this.att = paramDirectBuffer;
  }
  
  public ShortBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 1;
    assert m >= 0;
    return new DirectShortBufferU(this, -1, 0, k, k, m);
  }
  
  public ShortBuffer duplicate() { return new DirectShortBufferU(this, markValue(), position(), limit(), capacity(), 0); }
  
  public ShortBuffer asReadOnlyBuffer() { return new DirectShortBufferRU(this, markValue(), position(), limit(), capacity(), 0); }
  
  public long address() { return this.address; }
  
  private long ix(int paramInt) { return this.address + (paramInt << true); }
  
  public short get() { return unsafe.getShort(ix(nextGetIndex())); }
  
  public short get(int paramInt) { return unsafe.getShort(ix(checkIndex(paramInt))); }
  
  public ShortBuffer get(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    if (paramInt2 << true > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfShort.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferUnderflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToShortArray(ix(i), paramArrayOfShort, paramInt1 << true, paramInt2 << true);
      } else {
        Bits.copyToArray(ix(i), paramArrayOfShort, arrayBaseOffset, paramInt1 << true, paramInt2 << true);
      } 
      position(i + paramInt2);
    } else {
      super.get(paramArrayOfShort, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public ShortBuffer put(short paramShort) {
    unsafe.putShort(ix(nextPutIndex()), paramShort);
    return this;
  }
  
  public ShortBuffer put(int paramInt, short paramShort) {
    unsafe.putShort(ix(checkIndex(paramInt)), paramShort);
    return this;
  }
  
  public ShortBuffer put(ShortBuffer paramShortBuffer) {
    if (paramShortBuffer instanceof DirectShortBufferU) {
      if (paramShortBuffer == this)
        throw new IllegalArgumentException(); 
      DirectShortBufferU directShortBufferU = (DirectShortBufferU)paramShortBuffer;
      int i = directShortBufferU.position();
      int j = directShortBufferU.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      int m = position();
      int n = limit();
      assert m <= n;
      int i1 = (m <= n) ? (n - m) : 0;
      if (k > i1)
        throw new BufferOverflowException(); 
      unsafe.copyMemory(directShortBufferU.ix(i), ix(m), k << true);
      directShortBufferU.position(i + k);
      position(m + k);
    } else if (paramShortBuffer.hb != null) {
      int i = paramShortBuffer.position();
      int j = paramShortBuffer.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      put(paramShortBuffer.hb, paramShortBuffer.offset + i, k);
      paramShortBuffer.position(i + k);
    } else {
      super.put(paramShortBuffer);
    } 
    return this;
  }
  
  public ShortBuffer put(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    if (paramInt2 << true > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfShort.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferOverflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromShortArray(paramArrayOfShort, paramInt1 << true, ix(i), paramInt2 << true);
      } else {
        Bits.copyFromArray(paramArrayOfShort, arrayBaseOffset, paramInt1 << true, ix(i), paramInt2 << true);
      } 
      position(i + paramInt2);
    } else {
      super.put(paramArrayOfShort, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public ShortBuffer compact() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    unsafe.copyMemory(ix(i), ix(0), k << true);
    position(k);
    limit(capacity());
    discardMark();
    return this;
  }
  
  public boolean isDirect() { return true; }
  
  public boolean isReadOnly() { return false; }
  
  public ByteOrder order() { return (ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectShortBufferU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */