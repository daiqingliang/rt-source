package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectIntBufferU extends IntBuffer implements DirectBuffer {
  protected static final Unsafe unsafe = Bits.unsafe();
  
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(int[].class);
  
  protected static final boolean unaligned = Bits.unaligned();
  
  private final Object att;
  
  public Object attachment() { return this.att; }
  
  public Cleaner cleaner() { return null; }
  
  DirectIntBufferU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = paramDirectBuffer.address() + paramInt5;
    this.att = paramDirectBuffer;
  }
  
  public IntBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 2;
    assert m >= 0;
    return new DirectIntBufferU(this, -1, 0, k, k, m);
  }
  
  public IntBuffer duplicate() { return new DirectIntBufferU(this, markValue(), position(), limit(), capacity(), 0); }
  
  public IntBuffer asReadOnlyBuffer() { return new DirectIntBufferRU(this, markValue(), position(), limit(), capacity(), 0); }
  
  public long address() { return this.address; }
  
  private long ix(int paramInt) { return this.address + (paramInt << 2); }
  
  public int get() { return unsafe.getInt(ix(nextGetIndex())); }
  
  public int get(int paramInt) { return unsafe.getInt(ix(checkIndex(paramInt))); }
  
  public IntBuffer get(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    if (paramInt2 << 2 > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfInt.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferUnderflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToIntArray(ix(i), paramArrayOfInt, paramInt1 << 2, paramInt2 << 2);
      } else {
        Bits.copyToArray(ix(i), paramArrayOfInt, arrayBaseOffset, paramInt1 << 2, paramInt2 << 2);
      } 
      position(i + paramInt2);
    } else {
      super.get(paramArrayOfInt, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public IntBuffer put(int paramInt) {
    unsafe.putInt(ix(nextPutIndex()), paramInt);
    return this;
  }
  
  public IntBuffer put(int paramInt1, int paramInt2) {
    unsafe.putInt(ix(checkIndex(paramInt1)), paramInt2);
    return this;
  }
  
  public IntBuffer put(IntBuffer paramIntBuffer) {
    if (paramIntBuffer instanceof DirectIntBufferU) {
      if (paramIntBuffer == this)
        throw new IllegalArgumentException(); 
      DirectIntBufferU directIntBufferU = (DirectIntBufferU)paramIntBuffer;
      int i = directIntBufferU.position();
      int j = directIntBufferU.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      int m = position();
      int n = limit();
      assert m <= n;
      int i1 = (m <= n) ? (n - m) : 0;
      if (k > i1)
        throw new BufferOverflowException(); 
      unsafe.copyMemory(directIntBufferU.ix(i), ix(m), k << 2);
      directIntBufferU.position(i + k);
      position(m + k);
    } else if (paramIntBuffer.hb != null) {
      int i = paramIntBuffer.position();
      int j = paramIntBuffer.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      put(paramIntBuffer.hb, paramIntBuffer.offset + i, k);
      paramIntBuffer.position(i + k);
    } else {
      super.put(paramIntBuffer);
    } 
    return this;
  }
  
  public IntBuffer put(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    if (paramInt2 << 2 > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfInt.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferOverflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromIntArray(paramArrayOfInt, paramInt1 << 2, ix(i), paramInt2 << 2);
      } else {
        Bits.copyFromArray(paramArrayOfInt, arrayBaseOffset, paramInt1 << 2, ix(i), paramInt2 << 2);
      } 
      position(i + paramInt2);
    } else {
      super.put(paramArrayOfInt, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public IntBuffer compact() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    unsafe.copyMemory(ix(i), ix(0), k << 2);
    position(k);
    limit(capacity());
    discardMark();
    return this;
  }
  
  public boolean isDirect() { return true; }
  
  public boolean isReadOnly() { return false; }
  
  public ByteOrder order() { return (ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectIntBufferU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */