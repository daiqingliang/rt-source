package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectDoubleBufferS extends DoubleBuffer implements DirectBuffer {
  protected static final Unsafe unsafe = Bits.unsafe();
  
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(double[].class);
  
  protected static final boolean unaligned = Bits.unaligned();
  
  private final Object att;
  
  public Object attachment() { return this.att; }
  
  public Cleaner cleaner() { return null; }
  
  DirectDoubleBufferS(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = paramDirectBuffer.address() + paramInt5;
    this.att = paramDirectBuffer;
  }
  
  public DoubleBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 3;
    assert m >= 0;
    return new DirectDoubleBufferS(this, -1, 0, k, k, m);
  }
  
  public DoubleBuffer duplicate() { return new DirectDoubleBufferS(this, markValue(), position(), limit(), capacity(), 0); }
  
  public DoubleBuffer asReadOnlyBuffer() { return new DirectDoubleBufferRS(this, markValue(), position(), limit(), capacity(), 0); }
  
  public long address() { return this.address; }
  
  private long ix(int paramInt) { return this.address + (paramInt << 3); }
  
  public double get() { return Double.longBitsToDouble(Bits.swap(unsafe.getLong(ix(nextGetIndex())))); }
  
  public double get(int paramInt) { return Double.longBitsToDouble(Bits.swap(unsafe.getLong(ix(checkIndex(paramInt))))); }
  
  public DoubleBuffer get(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    if (paramInt2 << 3 > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfDouble.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferUnderflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToLongArray(ix(i), paramArrayOfDouble, paramInt1 << 3, paramInt2 << 3);
      } else {
        Bits.copyToArray(ix(i), paramArrayOfDouble, arrayBaseOffset, paramInt1 << 3, paramInt2 << 3);
      } 
      position(i + paramInt2);
    } else {
      super.get(paramArrayOfDouble, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public DoubleBuffer put(double paramDouble) {
    unsafe.putLong(ix(nextPutIndex()), Bits.swap(Double.doubleToRawLongBits(paramDouble)));
    return this;
  }
  
  public DoubleBuffer put(int paramInt, double paramDouble) {
    unsafe.putLong(ix(checkIndex(paramInt)), Bits.swap(Double.doubleToRawLongBits(paramDouble)));
    return this;
  }
  
  public DoubleBuffer put(DoubleBuffer paramDoubleBuffer) {
    if (paramDoubleBuffer instanceof DirectDoubleBufferS) {
      if (paramDoubleBuffer == this)
        throw new IllegalArgumentException(); 
      DirectDoubleBufferS directDoubleBufferS = (DirectDoubleBufferS)paramDoubleBuffer;
      int i = directDoubleBufferS.position();
      int j = directDoubleBufferS.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      int m = position();
      int n = limit();
      assert m <= n;
      int i1 = (m <= n) ? (n - m) : 0;
      if (k > i1)
        throw new BufferOverflowException(); 
      unsafe.copyMemory(directDoubleBufferS.ix(i), ix(m), k << 3);
      directDoubleBufferS.position(i + k);
      position(m + k);
    } else if (paramDoubleBuffer.hb != null) {
      int i = paramDoubleBuffer.position();
      int j = paramDoubleBuffer.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      put(paramDoubleBuffer.hb, paramDoubleBuffer.offset + i, k);
      paramDoubleBuffer.position(i + k);
    } else {
      super.put(paramDoubleBuffer);
    } 
    return this;
  }
  
  public DoubleBuffer put(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    if (paramInt2 << 3 > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfDouble.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferOverflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromLongArray(paramArrayOfDouble, paramInt1 << 3, ix(i), paramInt2 << 3);
      } else {
        Bits.copyFromArray(paramArrayOfDouble, arrayBaseOffset, paramInt1 << 3, ix(i), paramInt2 << 3);
      } 
      position(i + paramInt2);
    } else {
      super.put(paramArrayOfDouble, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public DoubleBuffer compact() {
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectDoubleBufferS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */