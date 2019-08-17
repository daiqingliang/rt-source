package java.util.concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicStampedReference<V> extends Object {
  private static final Unsafe UNSAFE = Unsafe.getUnsafe();
  
  private static final long pairOffset = objectFieldOffset(UNSAFE, "pair", AtomicStampedReference.class);
  
  public AtomicStampedReference(V paramV, int paramInt) { this.pair = Pair.of(paramV, paramInt); }
  
  public V getReference() { return (V)this.pair.reference; }
  
  public int getStamp() { return this.pair.stamp; }
  
  public V get(int[] paramArrayOfInt) {
    Pair pair1 = this.pair;
    paramArrayOfInt[0] = pair1.stamp;
    return (V)pair1.reference;
  }
  
  public boolean weakCompareAndSet(V paramV1, V paramV2, int paramInt1, int paramInt2) { return compareAndSet(paramV1, paramV2, paramInt1, paramInt2); }
  
  public boolean compareAndSet(V paramV1, V paramV2, int paramInt1, int paramInt2) {
    Pair pair1;
    return (paramV1 == pair1.reference && paramInt1 == pair1.stamp && ((paramV2 == pair1.reference && paramInt2 == pair1.stamp) || casPair(pair1, (pair1 = this.pair).of(paramV2, paramInt2))));
  }
  
  public void set(V paramV, int paramInt) {
    Pair pair1 = this.pair;
    if (paramV != pair1.reference || paramInt != pair1.stamp)
      this.pair = Pair.of(paramV, paramInt); 
  }
  
  public boolean attemptStamp(V paramV, int paramInt) {
    Pair pair1;
    return (paramV == pair1.reference && (paramInt == pair1.stamp || casPair(pair1, (pair1 = this.pair).of(paramV, paramInt))));
  }
  
  private boolean casPair(Pair<V> paramPair1, Pair<V> paramPair2) { return UNSAFE.compareAndSwapObject(this, pairOffset, paramPair1, paramPair2); }
  
  static long objectFieldOffset(Unsafe paramUnsafe, String paramString, Class<?> paramClass) {
    try {
      return paramUnsafe.objectFieldOffset(paramClass.getDeclaredField(paramString));
    } catch (NoSuchFieldException noSuchFieldException) {
      NoSuchFieldError noSuchFieldError = new NoSuchFieldError(paramString);
      noSuchFieldError.initCause(noSuchFieldException);
      throw noSuchFieldError;
    } 
  }
  
  private static class Pair<T> extends Object {
    final T reference;
    
    final int stamp;
    
    private Pair(T param1T, int param1Int) {
      this.reference = param1T;
      this.stamp = param1Int;
    }
    
    static <T> Pair<T> of(T param1T, int param1Int) { return new Pair(param1T, param1Int); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicStampedReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */