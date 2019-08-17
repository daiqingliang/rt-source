package java.nio;

class HeapFloatBuffer extends FloatBuffer {
  HeapFloatBuffer(int paramInt1, int paramInt2) { super(-1, 0, paramInt2, paramInt1, new float[paramInt1], 0); }
  
  HeapFloatBuffer(float[] paramArrayOfFloat, int paramInt1, int paramInt2) { super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfFloat.length, paramArrayOfFloat, 0); }
  
  protected HeapFloatBuffer(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfFloat, paramInt5); }
  
  public FloatBuffer slice() { return new HeapFloatBuffer(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public FloatBuffer duplicate() { return new HeapFloatBuffer(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public FloatBuffer asReadOnlyBuffer() { return new HeapFloatBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  protected int ix(int paramInt) { return paramInt + this.offset; }
  
  public float get() { return this.hb[ix(nextGetIndex())]; }
  
  public float get(int paramInt) { return this.hb[ix(checkIndex(paramInt))]; }
  
  public FloatBuffer get(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    System.arraycopy(this.hb, ix(position()), paramArrayOfFloat, paramInt1, paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public boolean isDirect() { return false; }
  
  public boolean isReadOnly() { return false; }
  
  public FloatBuffer put(float paramFloat) {
    this.hb[ix(nextPutIndex())] = paramFloat;
    return this;
  }
  
  public FloatBuffer put(int paramInt, float paramFloat) {
    this.hb[ix(checkIndex(paramInt))] = paramFloat;
    return this;
  }
  
  public FloatBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    System.arraycopy(paramArrayOfFloat, paramInt1, this.hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer) {
    if (paramFloatBuffer instanceof HeapFloatBuffer) {
      if (paramFloatBuffer == this)
        throw new IllegalArgumentException(); 
      HeapFloatBuffer heapFloatBuffer = (HeapFloatBuffer)paramFloatBuffer;
      int i = heapFloatBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      System.arraycopy(heapFloatBuffer.hb, heapFloatBuffer.ix(heapFloatBuffer.position()), this.hb, ix(position()), i);
      heapFloatBuffer.position(heapFloatBuffer.position() + i);
      position(position() + i);
    } else if (paramFloatBuffer.isDirect()) {
      int i = paramFloatBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      paramFloatBuffer.get(this.hb, ix(position()), i);
      position(position() + i);
    } else {
      super.put(paramFloatBuffer);
    } 
    return this;
  }
  
  public FloatBuffer compact() {
    System.arraycopy(this.hb, ix(position()), this.hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
  }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapFloatBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */