package java.lang.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

public class SoftReference<T> extends Reference<T> {
  private static long clock;
  
  private long timestamp = clock;
  
  public SoftReference(T paramT) { super(paramT); }
  
  public SoftReference(T paramT, ReferenceQueue<? super T> paramReferenceQueue) { super(paramT, paramReferenceQueue); }
  
  public T get() {
    Object object = super.get();
    if (object != null && this.timestamp != clock)
      this.timestamp = clock; 
    return (T)object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ref\SoftReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */