package java.nio;

class HeapLongBuffer extends LongBuffer {
  HeapLongBuffer(int paramInt1, int paramInt2) { super(-1, 0, paramInt2, paramInt1, new long[paramInt1], 0); }
  
  HeapLongBuffer(long[] paramArrayOfLong, int paramInt1, int paramInt2) { super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfLong.length, paramArrayOfLong, 0); }
  
  protected HeapLongBuffer(long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramInt5); }
  
  public LongBuffer slice() { return new HeapLongBuffer(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public LongBuffer duplicate() { return new HeapLongBuffer(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public LongBuffer asReadOnlyBuffer() { return new HeapLongBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  protected int ix(int paramInt) { return paramInt + this.offset; }
  
  public long get() { return this.hb[ix(nextGetIndex())]; }
  
  public long get(int paramInt) { return this.hb[ix(checkIndex(paramInt))]; }
  
  public LongBuffer get(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfLong.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    System.arraycopy(this.hb, ix(position()), paramArrayOfLong, paramInt1, paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public boolean isDirect() { return false; }
  
  public boolean isReadOnly() { return false; }
  
  public LongBuffer put(long paramLong) {
    this.hb[ix(nextPutIndex())] = paramLong;
    return this;
  }
  
  public LongBuffer put(int paramInt, long paramLong) {
    this.hb[ix(checkIndex(paramInt))] = paramLong;
    return this;
  }
  
  public LongBuffer put(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfLong.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    System.arraycopy(paramArrayOfLong, paramInt1, this.hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public LongBuffer put(LongBuffer paramLongBuffer) {
    if (paramLongBuffer instanceof HeapLongBuffer) {
      if (paramLongBuffer == this)
        throw new IllegalArgumentException(); 
      HeapLongBuffer heapLongBuffer = (HeapLongBuffer)paramLongBuffer;
      int i = heapLongBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      System.arraycopy(heapLongBuffer.hb, heapLongBuffer.ix(heapLongBuffer.position()), this.hb, ix(position()), i);
      heapLongBuffer.position(heapLongBuffer.position() + i);
      position(position() + i);
    } else if (paramLongBuffer.isDirect()) {
      int i = paramLongBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      paramLongBuffer.get(this.hb, ix(position()), i);
      position(position() + i);
    } else {
      super.put(paramLongBuffer);
    } 
    return this;
  }
  
  public LongBuffer compact() {
    System.arraycopy(this.hb, ix(position()), this.hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
  }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapLongBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */