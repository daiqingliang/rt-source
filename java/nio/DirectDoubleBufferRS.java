package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectDoubleBufferRS extends DirectDoubleBufferS implements DirectBuffer {
  DirectDoubleBufferRS(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramDirectBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public DoubleBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = i << 3;
    assert m >= 0;
    return new DirectDoubleBufferRS(this, -1, 0, k, k, m);
  }
  
  public DoubleBuffer duplicate() { return new DirectDoubleBufferRS(this, markValue(), position(), limit(), capacity(), 0); }
  
  public DoubleBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public DoubleBuffer put(double paramDouble) { throw new ReadOnlyBufferException(); }
  
  public DoubleBuffer put(int paramInt, double paramDouble) { throw new ReadOnlyBufferException(); }
  
  public DoubleBuffer put(DoubleBuffer paramDoubleBuffer) { throw new ReadOnlyBufferException(); }
  
  public DoubleBuffer put(double[] paramArrayOfDouble, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public DoubleBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public boolean isDirect() { return true; }
  
  public boolean isReadOnly() { return true; }
  
  public ByteOrder order() { return (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\DirectDoubleBufferRS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */