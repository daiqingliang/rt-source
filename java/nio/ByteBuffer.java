package java.nio;

public abstract class ByteBuffer extends Buffer implements Comparable<ByteBuffer> {
  final byte[] hb;
  
  final int offset;
  
  boolean isReadOnly;
  
  boolean bigEndian = true;
  
  boolean nativeByteOrder = (Bits.byteOrder() == ByteOrder.BIG_ENDIAN);
  
  ByteBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.hb = paramArrayOfByte;
    this.offset = paramInt5;
  }
  
  ByteBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0); }
  
  public static ByteBuffer allocateDirect(int paramInt) { return new DirectByteBuffer(paramInt); }
  
  public static ByteBuffer allocate(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return new HeapByteBuffer(paramInt, paramInt);
  }
  
  public static ByteBuffer wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    try {
      return new HeapByteBuffer(paramArrayOfByte, paramInt1, paramInt2);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public static ByteBuffer wrap(byte[] paramArrayOfByte) { return wrap(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public abstract ByteBuffer slice();
  
  public abstract ByteBuffer duplicate();
  
  public abstract ByteBuffer asReadOnlyBuffer();
  
  public abstract byte get();
  
  public abstract ByteBuffer put(byte paramByte);
  
  public abstract byte get(int paramInt);
  
  public abstract ByteBuffer put(int paramInt, byte paramByte);
  
  public ByteBuffer get(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfByte.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      paramArrayOfByte[j] = get(); 
    return this;
  }
  
  public ByteBuffer get(byte[] paramArrayOfByte) { return get(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public ByteBuffer put(ByteBuffer paramByteBuffer) {
    if (paramByteBuffer == this)
      throw new IllegalArgumentException(); 
    if (isReadOnly())
      throw new ReadOnlyBufferException(); 
    int i = paramByteBuffer.remaining();
    if (i > remaining())
      throw new BufferOverflowException(); 
    for (byte b = 0; b < i; b++)
      put(paramByteBuffer.get()); 
    return this;
  }
  
  public ByteBuffer put(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfByte.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      put(paramArrayOfByte[j]); 
    return this;
  }
  
  public final ByteBuffer put(byte[] paramArrayOfByte) { return put(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public final boolean hasArray() { return (this.hb != null && !this.isReadOnly); }
  
  public final byte[] array() {
    if (this.hb == null)
      throw new UnsupportedOperationException(); 
    if (this.isReadOnly)
      throw new ReadOnlyBufferException(); 
    return this.hb;
  }
  
  public final int arrayOffset() {
    if (this.hb == null)
      throw new UnsupportedOperationException(); 
    if (this.isReadOnly)
      throw new ReadOnlyBufferException(); 
    return this.offset;
  }
  
  public abstract ByteBuffer compact();
  
  public abstract boolean isDirect();
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getClass().getName());
    stringBuffer.append("[pos=");
    stringBuffer.append(position());
    stringBuffer.append(" lim=");
    stringBuffer.append(limit());
    stringBuffer.append(" cap=");
    stringBuffer.append(capacity());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  public int hashCode() {
    byte b = 1;
    int i = position();
    for (int j = limit() - 1; j >= i; j--)
      b = 31 * b + get(j); 
    return b;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ByteBuffer))
      return false; 
    ByteBuffer byteBuffer = (ByteBuffer)paramObject;
    if (remaining() != byteBuffer.remaining())
      return false; 
    int i = position();
    int j = limit() - 1;
    for (int k = byteBuffer.limit() - 1; j >= i; k--) {
      if (!equals(get(j), byteBuffer.get(k)))
        return false; 
      j--;
    } 
    return true;
  }
  
  private static boolean equals(byte paramByte1, byte paramByte2) { return (paramByte1 == paramByte2); }
  
  public int compareTo(ByteBuffer paramByteBuffer) {
    int i = position() + Math.min(remaining(), paramByteBuffer.remaining());
    int j = position();
    for (int k = paramByteBuffer.position(); j < i; k++) {
      int m = compare(get(j), paramByteBuffer.get(k));
      if (m != 0)
        return m; 
      j++;
    } 
    return remaining() - paramByteBuffer.remaining();
  }
  
  private static int compare(byte paramByte1, byte paramByte2) { return Byte.compare(paramByte1, paramByte2); }
  
  public final ByteOrder order() { return this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN; }
  
  public final ByteBuffer order(ByteOrder paramByteOrder) {
    this.bigEndian = (paramByteOrder == ByteOrder.BIG_ENDIAN);
    this.nativeByteOrder = (this.bigEndian == ((Bits.byteOrder() == ByteOrder.BIG_ENDIAN)));
    return this;
  }
  
  abstract byte _get(int paramInt);
  
  abstract void _put(int paramInt, byte paramByte);
  
  public abstract char getChar();
  
  public abstract ByteBuffer putChar(char paramChar);
  
  public abstract char getChar(int paramInt);
  
  public abstract ByteBuffer putChar(int paramInt, char paramChar);
  
  public abstract CharBuffer asCharBuffer();
  
  public abstract short getShort();
  
  public abstract ByteBuffer putShort(short paramShort);
  
  public abstract short getShort(int paramInt);
  
  public abstract ByteBuffer putShort(int paramInt, short paramShort);
  
  public abstract ShortBuffer asShortBuffer();
  
  public abstract int getInt();
  
  public abstract ByteBuffer putInt(int paramInt);
  
  public abstract int getInt(int paramInt);
  
  public abstract ByteBuffer putInt(int paramInt1, int paramInt2);
  
  public abstract IntBuffer asIntBuffer();
  
  public abstract long getLong();
  
  public abstract ByteBuffer putLong(long paramLong);
  
  public abstract long getLong(int paramInt);
  
  public abstract ByteBuffer putLong(int paramInt, long paramLong);
  
  public abstract LongBuffer asLongBuffer();
  
  public abstract float getFloat();
  
  public abstract ByteBuffer putFloat(float paramFloat);
  
  public abstract float getFloat(int paramInt);
  
  public abstract ByteBuffer putFloat(int paramInt, float paramFloat);
  
  public abstract FloatBuffer asFloatBuffer();
  
  public abstract double getDouble();
  
  public abstract ByteBuffer putDouble(double paramDouble);
  
  public abstract double getDouble(int paramInt);
  
  public abstract ByteBuffer putDouble(int paramInt, double paramDouble);
  
  public abstract DoubleBuffer asDoubleBuffer();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\ByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */