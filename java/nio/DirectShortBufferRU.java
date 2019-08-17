package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectShortBufferRU extends DirectShortBufferU implements DirectBuffer {
  DirectShortBufferRU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramDirectBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public ShortBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 1;
    assert m >= 0;
    return new DirectShortBufferRU(this, -1, 0, k, k, m);
  }
  
  public ShortBuffer duplicate() { return new DirectShortBufferRU(this, markValue(), position(), limit(), capacity(), 0); }
  
  public ShortBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public ShortBuffer put(short paramShort) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer put(int paramInt, short paramShort) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer put(ShortBuffer paramShortBuffer) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer put(short[] paramArrayOfShort, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public boolean isDirect() { return true; }
  
  public boolean isReadOnly() { return true; }
  
  public ByteOrder order() { return (ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectShortBufferRU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */