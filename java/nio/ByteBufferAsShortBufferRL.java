package java.nio;

class ByteBufferAsShortBufferRL extends ByteBufferAsShortBufferL {
  ByteBufferAsShortBufferRL(ByteBuffer paramByteBuffer) { super(paramByteBuffer); }
  
  ByteBufferAsShortBufferRL(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramByteBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public ShortBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    int m = (i << 1) + this.offset;
    assert m >= 0;
    return new ByteBufferAsShortBufferRL(this.bb, -1, 0, k, k, m);
  }
  
  public ShortBuffer duplicate() { return new ByteBufferAsShortBufferRL(this.bb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public ShortBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public ShortBuffer put(short paramShort) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer put(int paramInt, short paramShort) { throw new ReadOnlyBufferException(); }
  
  public ShortBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public boolean isDirect() { return this.bb.isDirect(); }
  
  public boolean isReadOnly() { return true; }
  
  public ByteOrder order() { return ByteOrder.LITTLE_ENDIAN; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\ByteBufferAsShortBufferRL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */