package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import sun.misc.Unsafe;

public class AtomicIntegerArray implements Serializable {
  private static final long serialVersionUID = 2862133569453604235L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final int base = unsafe.arrayBaseOffset(int[].class);
  
  private static final int shift;
  
  private final int[] array;
  
  private long checkedByteOffset(int paramInt) {
    if (paramInt < 0 || paramInt >= this.array.length)
      throw new IndexOutOfBoundsException("index " + paramInt); 
    return byteOffset(paramInt);
  }
  
  private static long byteOffset(int paramInt) { return (paramInt << shift) + base; }
  
  public AtomicIntegerArray(int paramInt) { this.array = new int[paramInt]; }
  
  public AtomicIntegerArray(int[] paramArrayOfInt) { this.array = (int[])paramArrayOfInt.clone(); }
  
  public final int length() { return this.array.length; }
  
  public final int get(int paramInt) { return getRaw(checkedByteOffset(paramInt)); }
  
  private int getRaw(long paramLong) { return unsafe.getIntVolatile(this.array, paramLong); }
  
  public final void set(int paramInt1, int paramInt2) { unsafe.putIntVolatile(this.array, checkedByteOffset(paramInt1), paramInt2); }
  
  public final void lazySet(int paramInt1, int paramInt2) { unsafe.putOrderedInt(this.array, checkedByteOffset(paramInt1), paramInt2); }
  
  public final int getAndSet(int paramInt1, int paramInt2) { return unsafe.getAndSetInt(this.array, checkedByteOffset(paramInt1), paramInt2); }
  
  public final boolean compareAndSet(int paramInt1, int paramInt2, int paramInt3) { return compareAndSetRaw(checkedByteOffset(paramInt1), paramInt2, paramInt3); }
  
  private boolean compareAndSetRaw(long paramLong, int paramInt1, int paramInt2) { return unsafe.compareAndSwapInt(this.array, paramLong, paramInt1, paramInt2); }
  
  public final boolean weakCompareAndSet(int paramInt1, int paramInt2, int paramInt3) { return compareAndSet(paramInt1, paramInt2, paramInt3); }
  
  public final int getAndIncrement(int paramInt) { return getAndAdd(paramInt, 1); }
  
  public final int getAndDecrement(int paramInt) { return getAndAdd(paramInt, -1); }
  
  public final int getAndAdd(int paramInt1, int paramInt2) { return unsafe.getAndAddInt(this.array, checkedByteOffset(paramInt1), paramInt2); }
  
  public final int incrementAndGet(int paramInt) { return getAndAdd(paramInt, 1) + 1; }
  
  public final int decrementAndGet(int paramInt) { return getAndAdd(paramInt, -1) - 1; }
  
  public final int addAndGet(int paramInt1, int paramInt2) { return getAndAdd(paramInt1, paramInt2) + paramInt2; }
  
  public final int getAndUpdate(int paramInt, IntUnaryOperator paramIntUnaryOperator) {
    int j;
    int i;
    long l = checkedByteOffset(paramInt);
    do {
      i = getRaw(l);
      j = paramIntUnaryOperator.applyAsInt(i);
    } while (!compareAndSetRaw(l, i, j));
    return i;
  }
  
  public final int updateAndGet(int paramInt, IntUnaryOperator paramIntUnaryOperator) {
    int j;
    int i;
    long l = checkedByteOffset(paramInt);
    do {
      i = getRaw(l);
      j = paramIntUnaryOperator.applyAsInt(i);
    } while (!compareAndSetRaw(l, i, j));
    return j;
  }
  
  public final int getAndAccumulate(int paramInt1, int paramInt2, IntBinaryOperator paramIntBinaryOperator) {
    int j;
    int i;
    long l = checkedByteOffset(paramInt1);
    do {
      i = getRaw(l);
      j = paramIntBinaryOperator.applyAsInt(i, paramInt2);
    } while (!compareAndSetRaw(l, i, j));
    return i;
  }
  
  public final int accumulateAndGet(int paramInt1, int paramInt2, IntBinaryOperator paramIntBinaryOperator) {
    int j;
    int i;
    long l = checkedByteOffset(paramInt1);
    do {
      i = getRaw(l);
      j = paramIntBinaryOperator.applyAsInt(i, paramInt2);
    } while (!compareAndSetRaw(l, i, j));
    return j;
  }
  
  public String toString() {
    int i = this.array.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(getRaw(byteOffset(b)));
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(',').append(' ');
    } 
  }
  
  static  {
    int i = unsafe.arrayIndexScale(int[].class);
    if ((i & i - 1) != 0)
      throw new Error("data type scale not a power of two"); 
    shift = 31 - Integer.numberOfLeadingZeros(i);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicIntegerArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */