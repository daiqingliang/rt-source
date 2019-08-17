package java.nio;

public abstract class Buffer {
  static final int SPLITERATOR_CHARACTERISTICS = 16464;
  
  private int mark = -1;
  
  private int position = 0;
  
  private int limit;
  
  private int capacity;
  
  long address;
  
  Buffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt4 < 0)
      throw new IllegalArgumentException("Negative capacity: " + paramInt4); 
    this.capacity = paramInt4;
    limit(paramInt3);
    position(paramInt2);
    if (paramInt1 >= 0) {
      if (paramInt1 > paramInt2)
        throw new IllegalArgumentException("mark > position: (" + paramInt1 + " > " + paramInt2 + ")"); 
      this.mark = paramInt1;
    } 
  }
  
  public final int capacity() { return this.capacity; }
  
  public final int position() { return this.position; }
  
  public final Buffer position(int paramInt) {
    if (paramInt > this.limit || paramInt < 0)
      throw new IllegalArgumentException(); 
    this.position = paramInt;
    if (this.mark > this.position)
      this.mark = -1; 
    return this;
  }
  
  public final int limit() { return this.limit; }
  
  public final Buffer limit(int paramInt) {
    if (paramInt > this.capacity || paramInt < 0)
      throw new IllegalArgumentException(); 
    this.limit = paramInt;
    if (this.position > this.limit)
      this.position = this.limit; 
    if (this.mark > this.limit)
      this.mark = -1; 
    return this;
  }
  
  public final Buffer mark() {
    this.mark = this.position;
    return this;
  }
  
  public final Buffer reset() {
    int i = this.mark;
    if (i < 0)
      throw new InvalidMarkException(); 
    this.position = i;
    return this;
  }
  
  public final Buffer clear() {
    this.position = 0;
    this.limit = this.capacity;
    this.mark = -1;
    return this;
  }
  
  public final Buffer flip() {
    this.limit = this.position;
    this.position = 0;
    this.mark = -1;
    return this;
  }
  
  public final Buffer rewind() {
    this.position = 0;
    this.mark = -1;
    return this;
  }
  
  public final int remaining() { return this.limit - this.position; }
  
  public final boolean hasRemaining() { return (this.position < this.limit); }
  
  public abstract boolean isReadOnly();
  
  public abstract boolean hasArray();
  
  public abstract Object array();
  
  public abstract int arrayOffset();
  
  public abstract boolean isDirect();
  
  final int nextGetIndex() {
    if (this.position >= this.limit)
      throw new BufferUnderflowException(); 
    return this.position++;
  }
  
  final int nextGetIndex(int paramInt) {
    if (this.limit - this.position < paramInt)
      throw new BufferUnderflowException(); 
    int i = this.position;
    this.position += paramInt;
    return i;
  }
  
  final int nextPutIndex() {
    if (this.position >= this.limit)
      throw new BufferOverflowException(); 
    return this.position++;
  }
  
  final int nextPutIndex(int paramInt) {
    if (this.limit - this.position < paramInt)
      throw new BufferOverflowException(); 
    int i = this.position;
    this.position += paramInt;
    return i;
  }
  
  final int checkIndex(int paramInt) {
    if (paramInt < 0 || paramInt >= this.limit)
      throw new IndexOutOfBoundsException(); 
    return paramInt;
  }
  
  final int checkIndex(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt2 > this.limit - paramInt1)
      throw new IndexOutOfBoundsException(); 
    return paramInt1;
  }
  
  final int markValue() { return this.mark; }
  
  final void truncate() {
    this.mark = -1;
    this.position = 0;
    this.limit = 0;
    this.capacity = 0;
  }
  
  final void discardMark() { this.mark = -1; }
  
  static void checkBounds(int paramInt1, int paramInt2, int paramInt3) {
    if ((paramInt1 | paramInt2 | paramInt1 + paramInt2 | paramInt3 - paramInt1 + paramInt2) < 0)
      throw new IndexOutOfBoundsException(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\Buffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */