package java.nio;

class HeapDoubleBuffer extends DoubleBuffer {
  HeapDoubleBuffer(int paramInt1, int paramInt2) { super(-1, 0, paramInt2, paramInt1, new double[paramInt1], 0); }
  
  HeapDoubleBuffer(double[] paramArrayOfDouble, int paramInt1, int paramInt2) { super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfDouble.length, paramArrayOfDouble, 0); }
  
  protected HeapDoubleBuffer(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfDouble, paramInt5); }
  
  public DoubleBuffer slice() { return new HeapDoubleBuffer(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public DoubleBuffer duplicate() { return new HeapDoubleBuffer(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public DoubleBuffer asReadOnlyBuffer() { return new HeapDoubleBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  protected int ix(int paramInt) { return paramInt + this.offset; }
  
  public double get() { return this.hb[ix(nextGetIndex())]; }
  
  public double get(int paramInt) { return this.hb[ix(checkIndex(paramInt))]; }
  
  public DoubleBuffer get(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfDouble.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    System.arraycopy(this.hb, ix(position()), paramArrayOfDouble, paramInt1, paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public boolean isDirect() { return false; }
  
  public boolean isReadOnly() { return false; }
  
  public DoubleBuffer put(double paramDouble) {
    this.hb[ix(nextPutIndex())] = paramDouble;
    return this;
  }
  
  public DoubleBuffer put(int paramInt, double paramDouble) {
    this.hb[ix(checkIndex(paramInt))] = paramDouble;
    return this;
  }
  
  public DoubleBuffer put(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfDouble.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    System.arraycopy(paramArrayOfDouble, paramInt1, this.hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public DoubleBuffer put(DoubleBuffer paramDoubleBuffer) {
    if (paramDoubleBuffer instanceof HeapDoubleBuffer) {
      if (paramDoubleBuffer == this)
        throw new IllegalArgumentException(); 
      HeapDoubleBuffer heapDoubleBuffer = (HeapDoubleBuffer)paramDoubleBuffer;
      int i = heapDoubleBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      System.arraycopy(heapDoubleBuffer.hb, heapDoubleBuffer.ix(heapDoubleBuffer.position()), this.hb, ix(position()), i);
      heapDoubleBuffer.position(heapDoubleBuffer.position() + i);
      position(position() + i);
    } else if (paramDoubleBuffer.isDirect()) {
      int i = paramDoubleBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      paramDoubleBuffer.get(this.hb, ix(position()), i);
      position(position() + i);
    } else {
      super.put(paramDoubleBuffer);
    } 
    return this;
  }
  
  public DoubleBuffer compact() {
    System.arraycopy(this.hb, ix(position()), this.hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
  }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapDoubleBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */