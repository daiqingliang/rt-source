package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.misc.Unsafe;

public class AtomicLong extends Number implements Serializable {
  private static final long serialVersionUID = 1927816293512124184L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long valueOffset;
  
  static final boolean VM_SUPPORTS_LONG_CAS = VMSupportsCS8();
  
  private static native boolean VMSupportsCS8();
  
  public AtomicLong(long paramLong) { this.value = paramLong; }
  
  public AtomicLong() {}
  
  public final long get() { return this.value; }
  
  public final void set(long paramLong) { this.value = paramLong; }
  
  public final void lazySet(long paramLong) { unsafe.putOrderedLong(this, valueOffset, paramLong); }
  
  public final long getAndSet(long paramLong) { return unsafe.getAndSetLong(this, valueOffset, paramLong); }
  
  public final boolean compareAndSet(long paramLong1, long paramLong2) { return unsafe.compareAndSwapLong(this, valueOffset, paramLong1, paramLong2); }
  
  public final boolean weakCompareAndSet(long paramLong1, long paramLong2) { return unsafe.compareAndSwapLong(this, valueOffset, paramLong1, paramLong2); }
  
  public final long getAndIncrement() { return unsafe.getAndAddLong(this, valueOffset, 1L); }
  
  public final long getAndDecrement() { return unsafe.getAndAddLong(this, valueOffset, -1L); }
  
  public final long getAndAdd(long paramLong) { return unsafe.getAndAddLong(this, valueOffset, paramLong); }
  
  public final long incrementAndGet() { return unsafe.getAndAddLong(this, valueOffset, 1L) + 1L; }
  
  public final long decrementAndGet() { return unsafe.getAndAddLong(this, valueOffset, -1L) - 1L; }
  
  public final long addAndGet(long paramLong) { return unsafe.getAndAddLong(this, valueOffset, paramLong) + paramLong; }
  
  public final long getAndUpdate(LongUnaryOperator paramLongUnaryOperator) {
    long l2;
    long l1;
    do {
      l1 = get();
      l2 = paramLongUnaryOperator.applyAsLong(l1);
    } while (!compareAndSet(l1, l2));
    return l1;
  }
  
  public final long updateAndGet(LongUnaryOperator paramLongUnaryOperator) {
    long l2;
    long l1;
    do {
      l1 = get();
      l2 = paramLongUnaryOperator.applyAsLong(l1);
    } while (!compareAndSet(l1, l2));
    return l2;
  }
  
  public final long getAndAccumulate(long paramLong, LongBinaryOperator paramLongBinaryOperator) {
    long l2;
    long l1;
    do {
      l1 = get();
      l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
    } while (!compareAndSet(l1, l2));
    return l1;
  }
  
  public final long accumulateAndGet(long paramLong, LongBinaryOperator paramLongBinaryOperator) {
    long l2;
    long l1;
    do {
      l1 = get();
      l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
    } while (!compareAndSet(l1, l2));
    return l2;
  }
  
  public String toString() { return Long.toString(get()); }
  
  public int intValue() { return (int)get(); }
  
  public long longValue() { return get(); }
  
  public float floatValue() { return (float)get(); }
  
  public double doubleValue() { return get(); }
  
  static  {
    try {
      valueOffset = unsafe.objectFieldOffset(AtomicLong.class.getDeclaredField("value"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */