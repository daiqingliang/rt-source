package java.nio;

class HeapCharBuffer extends CharBuffer {
  HeapCharBuffer(int paramInt1, int paramInt2) { super(-1, 0, paramInt2, paramInt1, new char[paramInt1], 0); }
  
  HeapCharBuffer(char[] paramArrayOfChar, int paramInt1, int paramInt2) { super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfChar.length, paramArrayOfChar, 0); }
  
  protected HeapCharBuffer(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfChar, paramInt5); }
  
  public CharBuffer slice() { return new HeapCharBuffer(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public CharBuffer duplicate() { return new HeapCharBuffer(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public CharBuffer asReadOnlyBuffer() { return new HeapCharBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  protected int ix(int paramInt) { return paramInt + this.offset; }
  
  public char get() { return this.hb[ix(nextGetIndex())]; }
  
  public char get(int paramInt) { return this.hb[ix(checkIndex(paramInt))]; }
  
  char getUnchecked(int paramInt) { return this.hb[ix(paramInt)]; }
  
  public CharBuffer get(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfChar.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    System.arraycopy(this.hb, ix(position()), paramArrayOfChar, paramInt1, paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public boolean isDirect() { return false; }
  
  public boolean isReadOnly() { return false; }
  
  public CharBuffer put(char paramChar) {
    this.hb[ix(nextPutIndex())] = paramChar;
    return this;
  }
  
  public CharBuffer put(int paramInt, char paramChar) {
    this.hb[ix(checkIndex(paramInt))] = paramChar;
    return this;
  }
  
  public CharBuffer put(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfChar.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    System.arraycopy(paramArrayOfChar, paramInt1, this.hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public CharBuffer put(CharBuffer paramCharBuffer) {
    if (paramCharBuffer instanceof HeapCharBuffer) {
      if (paramCharBuffer == this)
        throw new IllegalArgumentException(); 
      HeapCharBuffer heapCharBuffer = (HeapCharBuffer)paramCharBuffer;
      int i = heapCharBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      System.arraycopy(heapCharBuffer.hb, heapCharBuffer.ix(heapCharBuffer.position()), this.hb, ix(position()), i);
      heapCharBuffer.position(heapCharBuffer.position() + i);
      position(position() + i);
    } else if (paramCharBuffer.isDirect()) {
      int i = paramCharBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      paramCharBuffer.get(this.hb, ix(position()), i);
      position(position() + i);
    } else {
      super.put(paramCharBuffer);
    } 
    return this;
  }
  
  public CharBuffer compact() {
    System.arraycopy(this.hb, ix(position()), this.hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
  }
  
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
    return new HeapCharBuffer(this.hb, -1, i + paramInt1, i + paramInt2, capacity(), this.offset);
  }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapCharBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */