package java.util.concurrent.atomic;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

public class AtomicReferenceArray<E> extends Object implements Serializable {
  private static final long serialVersionUID = -6209656149925076980L;
  
  private static final Unsafe unsafe;
  
  private static final int base;
  
  private static final int shift;
  
  private static final long arrayFieldOffset;
  
  private final Object[] array;
  
  private long checkedByteOffset(int paramInt) {
    if (paramInt < 0 || paramInt >= this.array.length)
      throw new IndexOutOfBoundsException("index " + paramInt); 
    return byteOffset(paramInt);
  }
  
  private static long byteOffset(int paramInt) { return (paramInt << shift) + base; }
  
  public AtomicReferenceArray(int paramInt) { this.array = new Object[paramInt]; }
  
  public AtomicReferenceArray(E[] paramArrayOfE) { this.array = Arrays.copyOf(paramArrayOfE, paramArrayOfE.length, Object[].class); }
  
  public final int length() { return this.array.length; }
  
  public final E get(int paramInt) { return (E)getRaw(checkedByteOffset(paramInt)); }
  
  private E getRaw(long paramLong) { return (E)unsafe.getObjectVolatile(this.array, paramLong); }
  
  public final void set(int paramInt, E paramE) { unsafe.putObjectVolatile(this.array, checkedByteOffset(paramInt), paramE); }
  
  public final void lazySet(int paramInt, E paramE) { unsafe.putOrderedObject(this.array, checkedByteOffset(paramInt), paramE); }
  
  public final E getAndSet(int paramInt, E paramE) { return (E)unsafe.getAndSetObject(this.array, checkedByteOffset(paramInt), paramE); }
  
  public final boolean compareAndSet(int paramInt, E paramE1, E paramE2) { return compareAndSetRaw(checkedByteOffset(paramInt), paramE1, paramE2); }
  
  private boolean compareAndSetRaw(long paramLong, E paramE1, E paramE2) { return unsafe.compareAndSwapObject(this.array, paramLong, paramE1, paramE2); }
  
  public final boolean weakCompareAndSet(int paramInt, E paramE1, E paramE2) { return compareAndSet(paramInt, paramE1, paramE2); }
  
  public final E getAndUpdate(int paramInt, UnaryOperator<E> paramUnaryOperator) {
    Object object2;
    Object object1;
    long l = checkedByteOffset(paramInt);
    do {
      object1 = getRaw(l);
      object2 = paramUnaryOperator.apply(object1);
    } while (!compareAndSetRaw(l, object1, object2));
    return (E)object1;
  }
  
  public final E updateAndGet(int paramInt, UnaryOperator<E> paramUnaryOperator) {
    Object object2;
    Object object1;
    long l = checkedByteOffset(paramInt);
    do {
      object1 = getRaw(l);
      object2 = paramUnaryOperator.apply(object1);
    } while (!compareAndSetRaw(l, object1, object2));
    return (E)object2;
  }
  
  public final E getAndAccumulate(int paramInt, E paramE, BinaryOperator<E> paramBinaryOperator) {
    Object object2;
    Object object1;
    long l = checkedByteOffset(paramInt);
    do {
      object1 = getRaw(l);
      object2 = paramBinaryOperator.apply(object1, paramE);
    } while (!compareAndSetRaw(l, object1, object2));
    return (E)object1;
  }
  
  public final E accumulateAndGet(int paramInt, E paramE, BinaryOperator<E> paramBinaryOperator) {
    Object object2;
    Object object1;
    long l = checkedByteOffset(paramInt);
    do {
      object1 = getRaw(l);
      object2 = paramBinaryOperator.apply(object1, paramE);
    } while (!compareAndSetRaw(l, object1, object2));
    return (E)object2;
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
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException, InvalidObjectException {
    Object[] arrayOfObject = paramObjectInputStream.readFields().get("array", null);
    if (arrayOfObject == null || !arrayOfObject.getClass().isArray())
      throw new InvalidObjectException("Not array type"); 
    if (arrayOfObject.getClass() != Object[].class)
      arrayOfObject = Arrays.copyOf((Object[])arrayOfObject, Array.getLength(arrayOfObject), Object[].class); 
    unsafe.putObjectVolatile(this, arrayFieldOffset, arrayOfObject);
  }
  
  static  {
    try {
      unsafe = Unsafe.getUnsafe();
      arrayFieldOffset = unsafe.objectFieldOffset(AtomicReferenceArray.class.getDeclaredField("array"));
      base = unsafe.arrayBaseOffset(Object[].class);
      int i = unsafe.arrayIndexScale(Object[].class);
      if ((i & i - 1) != 0)
        throw new Error("data type scale not a power of two"); 
      shift = 31 - Integer.numberOfLeadingZeros(i);
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicReferenceArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */