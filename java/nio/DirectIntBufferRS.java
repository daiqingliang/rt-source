package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectIntBufferRS extends DirectIntBufferS implements DirectBuffer {
  DirectIntBufferRS(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramDirectBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public IntBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 2;
    assert m >= 0;
    return new DirectIntBufferRS(this, -1, 0, k, k, m);
  }
  
  public IntBuffer duplicate() { return new DirectIntBufferRS(this, markValue(), position(), limit(), capacity(), 0); }
  
  public IntBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public IntBuffer put(int paramInt) { throw new ReadOnlyBufferException(); }
  
  public IntBuffer put(int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public IntBuffer put(IntBuffer paramIntBuffer) { throw new ReadOnlyBufferException(); }
  
  public IntBuffer put(int[] paramArrayOfInt, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public IntBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public boolean isDirect() { return true; }
  
  public boolean isReadOnly() { return true; }
  
  public ByteOrder order() { return (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectIntBufferRS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */