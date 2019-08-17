package java.nio;

class HeapByteBuffer extends ByteBuffer {
  HeapByteBuffer(int paramInt1, int paramInt2) { super(-1, 0, paramInt2, paramInt1, new byte[paramInt1], 0); }
  
  HeapByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfByte.length, paramArrayOfByte, 0); }
  
  protected HeapByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, paramInt5); }
  
  public ByteBuffer slice() { return new HeapByteBuffer(this.hb, -1, 0, remaining(), remaining(), position() + this.offset); }
  
  public ByteBuffer duplicate() { return new HeapByteBuffer(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  public ByteBuffer asReadOnlyBuffer() { return new HeapByteBufferR(this.hb, markValue(), position(), limit(), capacity(), this.offset); }
  
  protected int ix(int paramInt) { return paramInt + this.offset; }
  
  public byte get() { return this.hb[ix(nextGetIndex())]; }
  
  public byte get(int paramInt) { return this.hb[ix(checkIndex(paramInt))]; }
  
  public ByteBuffer get(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfByte.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    System.arraycopy(this.hb, ix(position()), paramArrayOfByte, paramInt1, paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public boolean isDirect() { return false; }
  
  public boolean isReadOnly() { return false; }
  
  public ByteBuffer put(byte paramByte) {
    this.hb[ix(nextPutIndex())] = paramByte;
    return this;
  }
  
  public ByteBuffer put(int paramInt, byte paramByte) {
    this.hb[ix(checkIndex(paramInt))] = paramByte;
    return this;
  }
  
  public ByteBuffer put(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfByte.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    System.arraycopy(paramArrayOfByte, paramInt1, this.hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public ByteBuffer put(ByteBuffer paramByteBuffer) {
    if (paramByteBuffer instanceof HeapByteBuffer) {
      if (paramByteBuffer == this)
        throw new IllegalArgumentException(); 
      HeapByteBuffer heapByteBuffer = (HeapByteBuffer)paramByteBuffer;
      int i = heapByteBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      System.arraycopy(heapByteBuffer.hb, heapByteBuffer.ix(heapByteBuffer.position()), this.hb, ix(position()), i);
      heapByteBuffer.position(heapByteBuffer.position() + i);
      position(position() + i);
    } else if (paramByteBuffer.isDirect()) {
      int i = paramByteBuffer.remaining();
      if (i > remaining())
        throw new BufferOverflowException(); 
      paramByteBuffer.get(this.hb, ix(position()), i);
      position(position() + i);
    } else {
      super.put(paramByteBuffer);
    } 
    return this;
  }
  
  public ByteBuffer compact() {
    System.arraycopy(this.hb, ix(position()), this.hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
  }
  
  byte _get(int paramInt) { return this.hb[paramInt]; }
  
  void _put(int paramInt, byte paramByte) { this.hb[paramInt] = paramByte; }
  
  public char getChar() { return Bits.getChar(this, ix(nextGetIndex(2)), this.bigEndian); }
  
  public char getChar(int paramInt) { return Bits.getChar(this, ix(checkIndex(paramInt, 2)), this.bigEndian); }
  
  public ByteBuffer putChar(char paramChar) {
    Bits.putChar(this, ix(nextPutIndex(2)), paramChar, this.bigEndian);
    return this;
  }
  
  public ByteBuffer putChar(int paramInt, char paramChar) {
    Bits.putChar(this, ix(checkIndex(paramInt, 2)), paramChar, this.bigEndian);
    return this;
  }
  
  public CharBuffer asCharBuffer() {
    int i = remaining() >> 1;
    int j = this.offset + position();
    return this.bigEndian ? new ByteBufferAsCharBufferB(this, -1, 0, i, i, j) : new ByteBufferAsCharBufferL(this, -1, 0, i, i, j);
  }
  
  public short getShort() { return Bits.getShort(this, ix(nextGetIndex(2)), this.bigEndian); }
  
  public short getShort(int paramInt) { return Bits.getShort(this, ix(checkIndex(paramInt, 2)), this.bigEndian); }
  
  public ByteBuffer putShort(short paramShort) {
    Bits.putShort(this, ix(nextPutIndex(2)), paramShort, this.bigEndian);
    return this;
  }
  
  public ByteBuffer putShort(int paramInt, short paramShort) {
    Bits.putShort(this, ix(checkIndex(paramInt, 2)), paramShort, this.bigEndian);
    return this;
  }
  
  public ShortBuffer asShortBuffer() {
    int i = remaining() >> 1;
    int j = this.offset + position();
    return this.bigEndian ? new ByteBufferAsShortBufferB(this, -1, 0, i, i, j) : new ByteBufferAsShortBufferL(this, -1, 0, i, i, j);
  }
  
  public int getInt() { return Bits.getInt(this, ix(nextGetIndex(4)), this.bigEndian); }
  
  public int getInt(int paramInt) { return Bits.getInt(this, ix(checkIndex(paramInt, 4)), this.bigEndian); }
  
  public ByteBuffer putInt(int paramInt) {
    Bits.putInt(this, ix(nextPutIndex(4)), paramInt, this.bigEndian);
    return this;
  }
  
  public ByteBuffer putInt(int paramInt1, int paramInt2) {
    Bits.putInt(this, ix(checkIndex(paramInt1, 4)), paramInt2, this.bigEndian);
    return this;
  }
  
  public IntBuffer asIntBuffer() {
    int i = remaining() >> 2;
    int j = this.offset + position();
    return this.bigEndian ? new ByteBufferAsIntBufferB(this, -1, 0, i, i, j) : new ByteBufferAsIntBufferL(this, -1, 0, i, i, j);
  }
  
  public long getLong() { return Bits.getLong(this, ix(nextGetIndex(8)), this.bigEndian); }
  
  public long getLong(int paramInt) { return Bits.getLong(this, ix(checkIndex(paramInt, 8)), this.bigEndian); }
  
  public ByteBuffer putLong(long paramLong) {
    Bits.putLong(this, ix(nextPutIndex(8)), paramLong, this.bigEndian);
    return this;
  }
  
  public ByteBuffer putLong(int paramInt, long paramLong) {
    Bits.putLong(this, ix(checkIndex(paramInt, 8)), paramLong, this.bigEndian);
    return this;
  }
  
  public LongBuffer asLongBuffer() {
    int i = remaining() >> 3;
    int j = this.offset + position();
    return this.bigEndian ? new ByteBufferAsLongBufferB(this, -1, 0, i, i, j) : new ByteBufferAsLongBufferL(this, -1, 0, i, i, j);
  }
  
  public float getFloat() { return Bits.getFloat(this, ix(nextGetIndex(4)), this.bigEndian); }
  
  public float getFloat(int paramInt) { return Bits.getFloat(this, ix(checkIndex(paramInt, 4)), this.bigEndian); }
  
  public ByteBuffer putFloat(float paramFloat) {
    Bits.putFloat(this, ix(nextPutIndex(4)), paramFloat, this.bigEndian);
    return this;
  }
  
  public ByteBuffer putFloat(int paramInt, float paramFloat) {
    Bits.putFloat(this, ix(checkIndex(paramInt, 4)), paramFloat, this.bigEndian);
    return this;
  }
  
  public FloatBuffer asFloatBuffer() {
    int i = remaining() >> 2;
    int j = this.offset + position();
    return this.bigEndian ? new ByteBufferAsFloatBufferB(this, -1, 0, i, i, j) : new ByteBufferAsFloatBufferL(this, -1, 0, i, i, j);
  }
  
  public double getDouble() { return Bits.getDouble(this, ix(nextGetIndex(8)), this.bigEndian); }
  
  public double getDouble(int paramInt) { return Bits.getDouble(this, ix(checkIndex(paramInt, 8)), this.bigEndian); }
  
  public ByteBuffer putDouble(double paramDouble) {
    Bits.putDouble(this, ix(nextPutIndex(8)), paramDouble, this.bigEndian);
    return this;
  }
  
  public ByteBuffer putDouble(int paramInt, double paramDouble) {
    Bits.putDouble(this, ix(checkIndex(paramInt, 8)), paramDouble, this.bigEndian);
    return this;
  }
  
  public DoubleBuffer asDoubleBuffer() {
    int i = remaining() >> 3;
    int j = this.offset + position();
    return this.bigEndian ? new ByteBufferAsDoubleBufferB(this, -1, 0, i, i, j) : new ByteBufferAsDoubleBufferL(this, -1, 0, i, i, j);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\HeapByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */