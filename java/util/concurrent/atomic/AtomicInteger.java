package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import sun.misc.Unsafe;

public class AtomicInteger extends Number implements Serializable {
  private static final long serialVersionUID = 6214790243416807050L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long valueOffset;
  
  public AtomicInteger(int paramInt) { this.value = paramInt; }
  
  public AtomicInteger() {}
  
  public final int get() { return this.value; }
  
  public final void set(int paramInt) { this.value = paramInt; }
  
  public final void lazySet(int paramInt) { unsafe.putOrderedInt(this, valueOffset, paramInt); }
  
  public final int getAndSet(int paramInt) { return unsafe.getAndSetInt(this, valueOffset, paramInt); }
  
  public final boolean compareAndSet(int paramInt1, int paramInt2) { return unsafe.compareAndSwapInt(this, valueOffset, paramInt1, paramInt2); }
  
  public final boolean weakCompareAndSet(int paramInt1, int paramInt2) { return unsafe.compareAndSwapInt(this, valueOffset, paramInt1, paramInt2); }
  
  public final int getAndIncrement() { return unsafe.getAndAddInt(this, valueOffset, 1); }
  
  public final int getAndDecrement() { return unsafe.getAndAddInt(this, valueOffset, -1); }
  
  public final int getAndAdd(int paramInt) { return unsafe.getAndAddInt(this, valueOffset, paramInt); }
  
  public final int incrementAndGet() { return unsafe.getAndAddInt(this, valueOffset, 1) + 1; }
  
  public final int decrementAndGet() { return unsafe.getAndAddInt(this, valueOffset, -1) - 1; }
  
  public final int addAndGet(int paramInt) { return unsafe.getAndAddInt(this, valueOffset, paramInt) + paramInt; }
  
  public final int getAndUpdate(IntUnaryOperator paramIntUnaryOperator) {
    int j;
    int i;
    do {
      i = get();
      j = paramIntUnaryOperator.applyAsInt(i);
    } while (!compareAndSet(i, j));
    return i;
  }
  
  public final int updateAndGet(IntUnaryOperator paramIntUnaryOperator) {
    int j;
    int i;
    do {
      i = get();
      j = paramIntUnaryOperator.applyAsInt(i);
    } while (!compareAndSet(i, j));
    return j;
  }
  
  public final int getAndAccumulate(int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    int j;
    int i;
    do {
      i = get();
      j = paramIntBinaryOperator.applyAsInt(i, paramInt);
    } while (!compareAndSet(i, j));
    return i;
  }
  
  public final int accumulateAndGet(int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    int j;
    int i;
    do {
      i = get();
      j = paramIntBinaryOperator.applyAsInt(i, paramInt);
    } while (!compareAndSet(i, j));
    return j;
  }
  
  public String toString() { return Integer.toString(get()); }
  
  public int intValue() { return get(); }
  
  public long longValue() { return get(); }
  
  public float floatValue() { return get(); }
  
  public double doubleValue() { return get(); }
  
  static  {
    try {
      valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */