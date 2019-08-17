package java.nio;

class StringCharBuffer extends CharBuffer {
  CharSequence str;
  
  StringCharBuffer(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    super(-1, paramInt1, paramInt2, paramCharSequence.length());
    int i = paramCharSequence.length();
    if (paramInt1 < 0 || paramInt1 > i || paramInt2 < paramInt1 || paramInt2 > i)
      throw new IndexOutOfBoundsException(); 
    this.str = paramCharSequence;
  }
  
  public CharBuffer slice() { return new StringCharBuffer(this.str, -1, 0, remaining(), remaining(), this.offset + position()); }
  
  private StringCharBuffer(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4, null, paramInt5);
    this.str = paramCharSequence;
  }
  
  public CharBuffer duplicate() { return new StringCharBuffer(this.str, markValue(), position(), limit(), capacity(), this.offset); }
  
  public CharBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public final char get() { return this.str.charAt(nextGetIndex() + this.offset); }
  
  public final char get(int paramInt) { return this.str.charAt(checkIndex(paramInt) + this.offset); }
  
  char getUnchecked(int paramInt) { return this.str.charAt(paramInt + this.offset); }
  
  public final CharBuffer put(char paramChar) { throw new ReadOnlyBufferException(); }
  
  public final CharBuffer put(int paramInt, char paramChar) { throw new ReadOnlyBufferException(); }
  
  public final CharBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public final boolean isReadOnly() { return true; }
  
  final String toString(int paramInt1, int paramInt2) { return this.str.toString().substring(paramInt1 + this.offset, paramInt2 + this.offset); }
  
  public final CharBuffer subSequence(int paramInt1, int paramInt2) {
    try {
      int i = position();
      return new StringCharBuffer(this.str, -1, i + checkIndex(paramInt1, i), i + checkIndex(paramInt2, i), capacity(), this.offset);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public boolean isDirect() { return false; }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\StringCharBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */