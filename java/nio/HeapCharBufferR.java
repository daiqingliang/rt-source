package java.nio;

class HeapCharBufferR extends HeapCharBuffer {
  HeapCharBufferR(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  HeapCharBufferR(char[] paramArrayOfChar, int paramInt1, int paramInt2) { super(paramArrayOfChar, paramInt1, paramInt2); }
  
  protected HeapCharBufferR(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public CharBuffer slice() { return new HeapCharBufferR(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public CharBuffer duplicate() { return new HeapCharBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public CharBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public boolean isReadOnly() { return true; }
  
  public CharBuffer put(char paramChar) { throw new ReadOnlyBufferException(); }
  
  public CharBuffer put(int paramInt, char paramChar) { throw new ReadOnlyBufferException(); }
  
  public CharBuffer put(char[] paramArrayOfChar, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public CharBuffer put(CharBuffer paramCharBuffer) { throw new ReadOnlyBufferException(); }
  
  public CharBuffer compact() { throw new ReadOnlyBufferException(); }
  
  String toString(int paramInt1, int paramInt2) {
    try {
      return new String(this.hb, paramInt1 + this.offset, paramInt2 - paramInt1);
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public CharBuffer subSequence(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt2 > length() || paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException(); 
    int i = position();
    return new HeapCharBufferR(this.hb, -1, i + paramInt1, i + paramInt2, capacity(), this.offset);
  }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapCharBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */