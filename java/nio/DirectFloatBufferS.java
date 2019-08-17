package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectFloatBufferS extends FloatBuffer implements DirectBuffer {
  protected static final Unsafe unsafe = Bits.unsafe();
  
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(float[].class);
  
  protected static final boolean unaligned = Bits.unaligned();
  
  private final Object att;
  
  public Object attachment() { return this.att; }
  
  public Cleaner cleaner() { return null; }
  
  DirectFloatBufferS(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = paramDirectBuffer.address() + paramInt5;
    this.att = paramDirectBuffer;
  }
  
  public FloatBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 2;
    assert m >= 0;
    return new DirectFloatBufferS(this, -1, 0, k, k, m);
  }
  
  public FloatBuffer duplicate() { return new DirectFloatBufferS(this, markValue(), position(), limit(), capacity(), 0); }
  
  public FloatBuffer asReadOnlyBuffer() { return new DirectFloatBufferRS(this, markValue(), position(), limit(), capacity(), 0); }
  
  public long address() { return this.address; }
  
  private long ix(int paramInt) { return this.address + (paramInt << 2); }
  
  public float get() { return Float.intBitsToFloat(Bits.swap(unsafe.getInt(ix(nextGetIndex())))); }
  
  public float get(int paramInt) { return Float.intBitsToFloat(Bits.swap(unsafe.getInt(ix(checkIndex(paramInt))))); }
  
  public FloatBuffer get(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    if (paramInt2 << 2 > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferUnderflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToIntArray(ix(i), paramArrayOfFloat, paramInt1 << 2, paramInt2 << 2);
      } else {
        Bits.copyToArray(ix(i), paramArrayOfFloat, arrayBaseOffset, paramInt1 << 2, paramInt2 << 2);
      } 
      position(i + paramInt2);
    } else {
      super.get(paramArrayOfFloat, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public FloatBuffer put(float paramFloat) {
    unsafe.putInt(ix(nextPutIndex()), Bits.swap(Float.floatToRawIntBits(paramFloat)));
    return this;
  }
  
  public FloatBuffer put(int paramInt, float paramFloat) {
    unsafe.putInt(ix(checkIndex(paramInt)), Bits.swap(Float.floatToRawIntBits(paramFloat)));
    return this;
  }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer) {
    if (paramFloatBuffer instanceof DirectFloatBufferS) {
      if (paramFloatBuffer == this)
        throw new IllegalArgumentException(); 
      DirectFloatBufferS directFloatBufferS = (DirectFloatBufferS)paramFloatBuffer;
      int i = directFloatBufferS.position();
      int j = directFloatBufferS.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      int m = position();
      int n = limit();
      assert m <= n;
      int i1 = (m <= n) ? (n - m) : 0;
      if (k > i1)
        throw new BufferOverflowException(); 
      unsafe.copyMemory(directFloatBufferS.ix(i), ix(m), k << 2);
      directFloatBufferS.position(i + k);
      position(m + k);
    } else if (paramFloatBuffer.hb != null) {
      int i = paramFloatBuffer.position();
      int j = paramFloatBuffer.limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      put(paramFloatBuffer.hb, paramFloatBuffer.offset + i, k);
      paramFloatBuffer.position(i + k);
    } else {
      super.put(paramFloatBuffer);
    } 
    return this;
  }
  
  public FloatBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    if (paramInt2 << 2 > 6L) {
      checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
      int i = position();
      int j = limit();
      assert i <= j;
      int k = (i <= j) ? (j - i) : 0;
      if (paramInt2 > k)
        throw new BufferOverflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromIntArray(paramArrayOfFloat, paramInt1 << 2, ix(i), paramInt2 << 2);
      } else {
        Bits.copyFromArray(paramArrayOfFloat, arrayBaseOffset, paramInt1 << 2, ix(i), paramInt2 << 2);
      } 
      position(i + paramInt2);
    } else {
      super.put(paramArrayOfFloat, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public FloatBuffer compact() {
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
  
  public ByteOrder order() { return (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectFloatBufferS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */