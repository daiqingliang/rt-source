package java.nio;

class HeapLongBufferR extends HeapLongBuffer {
  HeapLongBufferR(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  HeapLongBufferR(long[] paramArrayOfLong, int paramInt1, int paramInt2) { super(paramArrayOfLong, paramInt1, paramInt2); }
  
  protected HeapLongBufferR(long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramArrayOfLong, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public LongBuffer slice() { return new HeapLongBufferR(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public LongBuffer duplicate() { return new HeapLongBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public LongBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public boolean isReadOnly() { return true; }
  
  public LongBuffer put(long paramLong) { throw new ReadOnlyBufferException(); }
  
  public LongBuffer put(int paramInt, long paramLong) { throw new ReadOnlyBufferException(); }
  
  public LongBuffer put(long[] paramArrayOfLong, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public LongBuffer put(LongBuffer paramLongBuffer) { throw new ReadOnlyBufferException(); }
  
  public LongBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapLongBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */