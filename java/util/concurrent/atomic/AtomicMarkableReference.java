package java.util.concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicMarkableReference<V> extends Object {
  private static final Unsafe UNSAFE = Unsafe.getUnsafe();
  
  private static final long pairOffset = objectFieldOffset(UNSAFE, "pair", AtomicMarkableReference.class);
  
  public AtomicMarkableReference(V paramV, boolean paramBoolean) { this.pair = Pair.of(paramV, paramBoolean); }
  
  public V getReference() { return (V)this.pair.reference; }
  
  public boolean isMarked() { return this.pair.mark; }
  
  public V get(boolean[] paramArrayOfBoolean) {
    Pair pair1 = this.pair;
    paramArrayOfBoolean[0] = pair1.mark;
    return (V)pair1.reference;
  }
  
  public boolean weakCompareAndSet(V paramV1, V paramV2, boolean paramBoolean1, boolean paramBoolean2) { return compareAndSet(paramV1, paramV2, paramBoolean1, paramBoolean2); }
  
  public boolean compareAndSet(V paramV1, V paramV2, boolean paramBoolean1, boolean paramBoolean2) {
    Pair pair1;
    return (paramV1 == pair1.reference && paramBoolean1 == pair1.mark && ((paramV2 == pair1.reference && paramBoolean2 == pair1.mark) || casPair(pair1, (pair1 = this.pair).of(paramV2, paramBoolean2))));
  }
  
  public void set(V paramV, boolean paramBoolean) {
    Pair pair1 = this.pair;
    if (paramV != pair1.reference || paramBoolean != pair1.mark)
      this.pair = Pair.of(paramV, paramBoolean); 
  }
  
  public boolean attemptMark(V paramV, boolean paramBoolean) {
    Pair pair1;
    return (paramV == pair1.reference && (paramBoolean == pair1.mark || casPair(pair1, (pair1 = this.pair).of(paramV, paramBoolean))));
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
    
    final boolean mark;
    
    private Pair(T param1T, boolean param1Boolean) {
      this.reference = param1T;
      this.mark = param1Boolean;
    }
    
    static <T> Pair<T> of(T param1T, boolean param1Boolean) { return new Pair(param1T, param1Boolean); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicMarkableReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */