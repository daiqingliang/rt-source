package java.util.concurrent.atomic;

import java.io.Serializable;
import sun.misc.Unsafe;

public class AtomicBoolean implements Serializable {
  private static final long serialVersionUID = 4654671469794556979L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long valueOffset;
  
  public AtomicBoolean(boolean paramBoolean) { this.value = paramBoolean ? 1 : 0; }
  
  public AtomicBoolean() {}
  
  public final boolean get() { return (this.value != 0); }
  
  public final boolean compareAndSet(boolean paramBoolean1, boolean paramBoolean2) {
    byte b1 = paramBoolean1 ? 1 : 0;
    byte b2 = paramBoolean2 ? 1 : 0;
    return unsafe.compareAndSwapInt(this, valueOffset, b1, b2);
  }
  
  public boolean weakCompareAndSet(boolean paramBoolean1, boolean paramBoolean2) {
    byte b1 = paramBoolean1 ? 1 : 0;
    byte b2 = paramBoolean2 ? 1 : 0;
    return unsafe.compareAndSwapInt(this, valueOffset, b1, b2);
  }
  
  public final void set(boolean paramBoolean) { this.value = paramBoolean ? 1 : 0; }
  
  public final void lazySet(boolean paramBoolean) {
    byte b = paramBoolean ? 1 : 0;
    unsafe.putOrderedInt(this, valueOffset, b);
  }
  
  public final boolean getAndSet(boolean paramBoolean) {
    boolean bool;
    do {
      bool = get();
    } while (!compareAndSet(bool, paramBoolean));
    return bool;
  }
  
  public String toString() { return Boolean.toString(get()); }
  
  static  {
    try {
      valueOffset = unsafe.objectFieldOffset(AtomicBoolean.class.getDeclaredField("value"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */