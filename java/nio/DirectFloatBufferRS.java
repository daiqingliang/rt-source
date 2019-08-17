package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectFloatBufferRS extends DirectFloatBufferS implements DirectBuffer {
  DirectFloatBufferRS(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramDirectBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public FloatBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 2;
    assert m >= 0;
    return new DirectFloatBufferRS(this, -1, 0, k, k, m);
  }
  
  public FloatBuffer duplicate() { return new DirectFloatBufferRS(this, markValue(), position(), limit(), capacity(), 0); }
  
  public FloatBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public FloatBuffer put(float paramFloat) { throw new ReadOnlyBufferException(); }
  
  public FloatBuffer put(int paramInt, float paramFloat) { throw new ReadOnlyBufferException(); }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer) { throw new ReadOnlyBufferException(); }
  
  public FloatBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public FloatBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public boolean isDirect() { return true; }
  
  public boolean isReadOnly() { return true; }
  
  public ByteOrder order() { return (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectFloatBufferRS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */