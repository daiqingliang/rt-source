package java.nio;

class HeapShortBufferR extends HeapShortBuffer {
  HeapShortBufferR(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  HeapShortBufferR(short[] paramArrayOfShort, int paramInt1, int paramInt2) { super(paramArrayOfShort, paramInt1, paramInt2); }
  
  protected HeapShortBufferR(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramArrayOfShort, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public ShortBuffer slice() { return new HeapShortBufferR(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public ShortBuffer duplicate() { return new HeapShortBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public ShortBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public boolean isReadOnly() { return true; }
  
  public ShortBuffer put(short paramShort) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer put(int paramInt, short paramShort) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer put(short[] paramArrayOfShort, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer put(ShortBuffer paramShortBuffer) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapShortBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */