package java.nio;

import java.util.Spliterator;
import java.util.function.IntConsumer;

class CharBufferSpliterator implements Spliterator.OfInt {
  private final CharBuffer buffer;
  
  private int index;
  
  private final int limit;
  
  CharBufferSpliterator(CharBuffer paramCharBuffer) { this(paramCharBuffer, paramCharBuffer.position(), paramCharBuffer.limit()); }
  
  CharBufferSpliterator(CharBuffer paramCharBuffer, int paramInt1, int paramInt2) {
    assert paramInt1 <= paramInt2;
    this.buffer = paramCharBuffer;
    this.index = (paramInt1 <= paramInt2) ? paramInt1 : paramInt2;
    this.limit = paramInt2;
  }
  
  public Spliterator.OfInt trySplit() {
    int i = this.index;
    int j = i + this.limit >>> 1;
    return (i >= j) ? null : new CharBufferSpliterator(this.buffer, i, this.index = j);
  }
  
  public void forEachRemaining(IntConsumer paramIntConsumer) {
    if (paramIntConsumer == null)
      throw new NullPointerException(); 
    CharBuffer charBuffer = this.buffer;
    int i = this.index;
    int j = this.limit;
    this.index = j;
    while (i < j)
      paramIntConsumer.accept(charBuffer.getUnchecked(i++)); 
  }
  
  public boolean tryAdvance(IntConsumer paramIntConsumer) {
    if (paramIntConsumer == null)
      throw new NullPointerException(); 
    if (this.index >= 0 && this.index < this.limit) {
      paramIntConsumer.accept(this.buffer.getUnchecked(this.index++));
      return true;
    } 
    return false;
  }
  
  public long estimateSize() { return (this.limit - this.index); }
  
  public int characteristics() { return 16464; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\CharBufferSpliterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */