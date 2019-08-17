package java.nio;

class HeapFloatBufferR extends HeapFloatBuffer {
  HeapFloatBufferR(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  HeapFloatBufferR(float[] paramArrayOfFloat, int paramInt1, int paramInt2) { super(paramArrayOfFloat, paramInt1, paramInt2); }
  
  protected HeapFloatBufferR(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramArrayOfFloat, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public FloatBuffer slice() { return new HeapFloatBufferR(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public FloatBuffer duplicate() { return new HeapFloatBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public FloatBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public boolean isReadOnly() { return true; }
  
  public FloatBuffer put(float paramFloat) { throw new ReadOnlyBufferException(); }
  
  public FloatBuffer put(int paramInt, float paramFloat) { throw new ReadOnlyBufferException(); }
  
  public FloatBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer) { throw new ReadOnlyBufferException(); }
  
  public FloatBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapFloatBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */