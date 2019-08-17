package java.nio;

class HeapIntBuffer extends IntBuffer {
  HeapIntBuffer(int paramInt1, int paramInt2) { super(-1, 0, paramInt2, paramInt1, new int[paramInt1], 0); }
  
  HeapIntBuffer(int[] paramArrayOfInt, int paramInt1, int paramInt2) { super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfInt.length, paramArrayOfInt, 0); }
  
  protected HeapIntBuffer(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5); }
  
  public IntBuffer slice() { return new HeapIntBuffer(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public IntBuffer duplicate() { return new HeapIntBuffer(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public IntBuffer asReadOnlyBuffer() { return new HeapIntBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  protected int ix(int paramInt) { return paramInt + this.offset; }
  
  public int get() { return this.hb[ix(nextGetIndex())]; }
  
  public int get(int paramInt) { return this.hb[ix(checkIndex(paramInt))]; }
  
  public IntBuffer get(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfInt.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    System.arraycopy(this.hb, ix(position()), paramArrayOfInt, paramInt1, paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public boolean isDirect() { return false; }
  
  public boolean isReadOnly() { return false; }
  
  public IntBuffer put(int paramInt) {
    this.hb[ix(nextPutIndex())] = paramInt;
    return this;
  }
  
  public IntBuffer put(int paramInt1, int paramInt2) {
    this.hb[ix(checkIndex(paramInt1))] = paramInt2;
    return this;
  }
  
  public IntBuffer put(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfInt.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    System.arraycopy(paramArrayOfInt, paramInt1, this.hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public IntBuffer put(IntBuffer paramIntBuffer) {
    if (paramIntBuffer instanceof HeapIntBuffer) {
      if (paramIntBuffer == this)
        throw new IllegalArgumentException(); 
      HeapIntBuffer heapIntBuffer = (HeapIntBuffer)paramIntBuffer;
      int i = heapIntBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      System.arraycopy(heapIntBuffer.hb, heapIntBuffer.ix(heapIntBuffer.position()), this.hb, ix(position()), i);
      heapIntBuffer.position(heapIntBuffer.position() + i);
      position(position() + i);
    } else if (paramIntBuffer.isDirect()) {
      int i = paramIntBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      paramIntBuffer.get(this.hb, ix(position()), i);
      position(position() + i);
    } else {
      super.put(paramIntBuffer);
    } 
    return this;
  }
  
  public IntBuffer compact() {
    System.arraycopy(this.hb, ix(position()), this.hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
  }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapIntBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */