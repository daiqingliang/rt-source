package java.nio;

class HeapIntBufferR extends HeapIntBuffer {
  HeapIntBufferR(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  HeapIntBufferR(int[] paramArrayOfInt, int paramInt1, int paramInt2) { super(paramArrayOfInt, paramInt1, paramInt2); }
  
  protected HeapIntBufferR(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public IntBuffer slice() { return new HeapIntBufferR(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public IntBuffer duplicate() { return new HeapIntBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public IntBuffer asReadOnlyBuffer() { return duplicate(); }
  
  public boolean isReadOnly() { return true; }
  
  public IntBuffer put(int paramInt) { throw new ReadOnlyBufferException(); }
  
  public IntBuffer put(int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public IntBuffer put(int[] paramArrayOfInt, int paramInt1, int paramInt2) { throw new ReadOnlyBufferException(); }
  
  public IntBuffer put(IntBuffer paramIntBuffer) { throw new ReadOnlyBufferException(); }
  
  public IntBuffer compact() { throw new ReadOnlyBufferException(); }
  
  public ByteOrder order() { return ByteOrder.nativeOrder(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapIntBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */