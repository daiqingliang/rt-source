package java.lang.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

public class PhantomReference<T> extends Reference<T> {
  public T get() { return null; }
  
  public PhantomReference(T paramT, ReferenceQueue<? super T> paramReferenceQueue) { super(paramT, paramReferenceQueue); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ref\PhantomReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */