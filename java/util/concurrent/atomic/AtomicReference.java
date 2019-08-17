package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

public class AtomicReference<V> extends Object implements Serializable {
  private static final long serialVersionUID = -1848883965231344442L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long valueOffset;
  
  public AtomicReference(V paramV) { this.value = paramV; }
  
  public AtomicReference() {}
  
  public final V get() { return (V)this.value; }
  
  public final void set(V paramV) { this.value = paramV; }
  
  public final void lazySet(V paramV) { unsafe.putOrderedObject(this, valueOffset, paramV); }
  
  public final boolean compareAndSet(V paramV1, V paramV2) { return unsafe.compareAndSwapObject(this, valueOffset, paramV1, paramV2); }
  
  public final boolean weakCompareAndSet(V paramV1, V paramV2) { return unsafe.compareAndSwapObject(this, valueOffset, paramV1, paramV2); }
  
  public final V getAndSet(V paramV) { return (V)unsafe.getAndSetObject(this, valueOffset, paramV); }
  
  public final V getAndUpdate(UnaryOperator<V> paramUnaryOperator) {
    Object object2;
    Object object1;
    do {
      object1 = get();
      object2 = paramUnaryOperator.apply(object1);
    } while (!compareAndSet(object1, object2));
    return (V)object1;
  }
  
  public final V updateAndGet(UnaryOperator<V> paramUnaryOperator) {
    Object object2;
    Object object1;
    do {
      object1 = get();
      object2 = paramUnaryOperator.apply(object1);
    } while (!compareAndSet(object1, object2));
    return (V)object2;
  }
  
  public final V getAndAccumulate(V paramV, BinaryOperator<V> paramBinaryOperator) {
    Object object2;
    Object object1;
    do {
      object1 = get();
      object2 = paramBinaryOperator.apply(object1, paramV);
    } while (!compareAndSet(object1, object2));
    return (V)object1;
  }
  
  public final V accumulateAndGet(V paramV, BinaryOperator<V> paramBinaryOperator) {
    Object object2;
    Object object1;
    do {
      object1 = get();
      object2 = paramBinaryOperator.apply(object1, paramV);
    } while (!compareAndSet(object1, object2));
    return (V)object2;
  }
  
  public String toString() { return String.valueOf(get()); }
  
  static  {
    try {
      valueOffset = unsafe.objectFieldOffset(AtomicReference.class.getDeclaredField("value"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */