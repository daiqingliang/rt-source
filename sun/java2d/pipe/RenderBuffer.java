package sun.java2d.pipe;

import sun.misc.Unsafe;

public class RenderBuffer {
  protected static final long SIZEOF_BYTE = 1L;
  
  protected static final long SIZEOF_SHORT = 2L;
  
  protected static final long SIZEOF_INT = 4L;
  
  protected static final long SIZEOF_FLOAT = 4L;
  
  protected static final long SIZEOF_LONG = 8L;
  
  protected static final long SIZEOF_DOUBLE = 8L;
  
  private static final int COPY_FROM_ARRAY_THRESHOLD = 6;
  
  protected final Unsafe unsafe = Unsafe.getUnsafe();
  
  protected final long baseAddress;
  
  protected final long endAddress;
  
  protected long curAddress;
  
  protected final int capacity;
  
  protected RenderBuffer(int paramInt) {
    this.curAddress = this.baseAddress = this.unsafe.allocateMemory(paramInt);
    this.endAddress = this.baseAddress + paramInt;
    this.capacity = paramInt;
  }
  
  public static RenderBuffer allocate(int paramInt) { return new RenderBuffer(paramInt); }
  
  public final long getAddress() { return this.baseAddress; }
  
  public final int capacity() { return this.capacity; }
  
  public final int remaining() { return (int)(this.endAddress - this.curAddress); }
  
  public final int position() { return (int)(this.curAddress - this.baseAddress); }
  
  public final void position(long paramLong) { this.curAddress = this.baseAddress + paramLong; }
  
  public final void clear() { this.curAddress = this.baseAddress; }
  
  public final RenderBuffer skip(long paramLong) {
    this.curAddress += paramLong;
    return this;
  }
  
  public final RenderBuffer putByte(byte paramByte) {
    this.unsafe.putByte(this.curAddress, paramByte);
    this.curAddress++;
    return this;
  }
  
  public RenderBuffer put(byte[] paramArrayOfByte) { return put(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public RenderBuffer put(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 > 6) {
      long l1 = paramInt1 * 1L + Unsafe.ARRAY_BYTE_BASE_OFFSET;
      long l2 = paramInt2 * 1L;
      this.unsafe.copyMemory(paramArrayOfByte, l1, null, this.curAddress, l2);
      position(position() + l2);
    } else {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++)
        putByte(paramArrayOfByte[j]); 
    } 
    return this;
  }
  
  public final RenderBuffer putShort(short paramShort) {
    this.unsafe.putShort(this.curAddress, paramShort);
    this.curAddress += 2L;
    return this;
  }
  
  public RenderBuffer put(short[] paramArrayOfShort) { return put(paramArrayOfShort, 0, paramArrayOfShort.length); }
  
  public RenderBuffer put(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    if (paramInt2 > 6) {
      long l1 = paramInt1 * 2L + Unsafe.ARRAY_SHORT_BASE_OFFSET;
      long l2 = paramInt2 * 2L;
      this.unsafe.copyMemory(paramArrayOfShort, l1, null, this.curAddress, l2);
      position(position() + l2);
    } else {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++)
        putShort(paramArrayOfShort[j]); 
    } 
    return this;
  }
  
  public final RenderBuffer putInt(int paramInt1, int paramInt2) {
    this.unsafe.putInt(this.baseAddress + paramInt1, paramInt2);
    return this;
  }
  
  public final RenderBuffer putInt(int paramInt) {
    this.unsafe.putInt(this.curAddress, paramInt);
    this.curAddress += 4L;
    return this;
  }
  
  public RenderBuffer put(int[] paramArrayOfInt) { return put(paramArrayOfInt, 0, paramArrayOfInt.length); }
  
  public RenderBuffer put(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    if (paramInt2 > 6) {
      long l1 = paramInt1 * 4L + Unsafe.ARRAY_INT_BASE_OFFSET;
      long l2 = paramInt2 * 4L;
      this.unsafe.copyMemory(paramArrayOfInt, l1, null, this.curAddress, l2);
      position(position() + l2);
    } else {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++)
        putInt(paramArrayOfInt[j]); 
    } 
    return this;
  }
  
  public final RenderBuffer putFloat(float paramFloat) {
    this.unsafe.putFloat(this.curAddress, paramFloat);
    this.curAddress += 4L;
    return this;
  }
  
  public RenderBuffer put(float[] paramArrayOfFloat) { return put(paramArrayOfFloat, 0, paramArrayOfFloat.length); }
  
  public RenderBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    if (paramInt2 > 6) {
      long l1 = paramInt1 * 4L + Unsafe.ARRAY_FLOAT_BASE_OFFSET;
      long l2 = paramInt2 * 4L;
      this.unsafe.copyMemory(paramArrayOfFloat, l1, null, this.curAddress, l2);
      position(position() + l2);
    } else {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++)
        putFloat(paramArrayOfFloat[j]); 
    } 
    return this;
  }
  
  public final RenderBuffer putLong(long paramLong) {
    this.unsafe.putLong(this.curAddress, paramLong);
    this.curAddress += 8L;
    return this;
  }
  
  public RenderBuffer put(long[] paramArrayOfLong) { return put(paramArrayOfLong, 0, paramArrayOfLong.length); }
  
  public RenderBuffer put(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    if (paramInt2 > 6) {
      long l1 = paramInt1 * 8L + Unsafe.ARRAY_LONG_BASE_OFFSET;
      long l2 = paramInt2 * 8L;
      this.unsafe.copyMemory(paramArrayOfLong, l1, null, this.curAddress, l2);
      position(position() + l2);
    } else {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++)
        putLong(paramArrayOfLong[j]); 
    } 
    return this;
  }
  
  public final RenderBuffer putDouble(double paramDouble) {
    this.unsafe.putDouble(this.curAddress, paramDouble);
    this.curAddress += 8L;
    return this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\RenderBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */