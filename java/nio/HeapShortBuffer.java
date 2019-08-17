package java.nio;

class HeapShortBuffer extends ShortBuffer {
  HeapShortBuffer(int paramInt1, int paramInt2) { super(-1, 0, paramInt2, paramInt1, new short[paramInt1], 0); }
  
  HeapShortBuffer(short[] paramArrayOfShort, int paramInt1, int paramInt2) { super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfShort.length, paramArrayOfShort, 0); }
  
  protected HeapShortBuffer(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfShort, paramInt5); }
  
  public ShortBuffer slice() { return new HeapShortBuffer(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public ShortBuffer duplicate() { return new HeapShortBuffer(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public ShortBuffer asReadOnlyBuffer() { return new HeapShortBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  protected int ix(int paramInt) { return paramInt + this.offset; }
  
  public short get() { return this.hb[ix(nextGetIndex())]; }
  
  public short get(int paramInt) { return this.hb[ix(checkIndex(paramInt))]; }
  
  public ShortBuffer get(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfShort.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    System.arraycopy(this.hb, ix(position()), paramArrayOfShort, paramInt1, paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public boolean isDirect() { return false; }
  
  public boolean isReadOnly() { return false; }
  
  public ShortBuffer put(short paramShort) {
    this.hb[ix(nextPutIndex())] = paramShort;
    return this;
  }
  
  public ShortBuffer put(int paramInt, short paramShort) {
    this.hb[ix(checkIndex(paramInt))] = paramShort;
    return this;
  }
  
  public ShortBuffer put(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfShort.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    System.arraycopy(paramArrayOfShort, paramInt1, this.hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public ShortBuffer put(ShortBuffer paramShortBuffer) {
    if (paramShortBuffer instanceof HeapShortBuffer) {
      if (paramShortBuffer == this)
        throw new IllegalArgumentException(); 
      HeapShortBuffer heapShortBuffer = (HeapShortBuffer)paramShortBuffer;
      int i = heapShortBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      System.arraycopy(heapShortBuffer.hb, heapShortBuffer.ix(heapShortBuffer.position()), this.hb, ix(position()), i);
      heapShortBuffer.position(heapShortBuffer.position() + i);
      position(position() + i);
    } else if (paramShortBuffer.isDirect()) {
      int i = paramShortBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      paramShortBuffer.get(this.hb, ix(position()), i);
      position(position() + i);
    } else {
      super.put(paramShortBuffer);
    } 
    return this;
  }
  
  public ShortBuffer compact() {
    System.arraycopy(this.hb, ix(position()), this.hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
  }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapShortBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */