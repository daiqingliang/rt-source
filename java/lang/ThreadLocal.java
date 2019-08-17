package java.lang;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ThreadLocal<T> extends Object {
  private final int threadLocalHashCode = nextHashCode();
  
  private static AtomicInteger nextHashCode = new AtomicInteger();
  
  private static final int HASH_INCREMENT = 1640531527;
  
  private static int nextHashCode() { return nextHashCode.getAndAdd(1640531527); }
  
  protected T initialValue() { return null; }
  
  public static <S> ThreadLocal<S> withInitial(Supplier<? extends S> paramSupplier) { return new SuppliedThreadLocal(paramSupplier); }
  
  public T get() {
    Thread thread = Thread.currentThread();
    ThreadLocalMap threadLocalMap = getMap(thread);
    if (threadLocalMap != null) {
      ThreadLocalMap.Entry entry = threadLocalMap.getEntry(this);
      if (entry != null)
        return (T)entry.value; 
    } 
    return (T)setInitialValue();
  }
  
  private T setInitialValue() {
    Object object = initialValue();
    Thread thread = Thread.currentThread();
    ThreadLocalMap threadLocalMap = getMap(thread);
    if (threadLocalMap != null) {
      threadLocalMap.set(this, object);
    } else {
      createMap(thread, object);
    } 
    return (T)object;
  }
  
  public void set(T paramT) {
    Thread thread = Thread.currentThread();
    ThreadLocalMap threadLocalMap = getMap(thread);
    if (threadLocalMap != null) {
      threadLocalMap.set(this, paramT);
    } else {
      createMap(thread, paramT);
    } 
  }
  
  public void remove() {
    ThreadLocalMap threadLocalMap = getMap(Thread.currentThread());
    if (threadLocalMap != null)
      threadLocalMap.remove(this); 
  }
  
  ThreadLocalMap getMap(Thread paramThread) { return paramThread.threadLocals; }
  
  void createMap(Thread paramThread, T paramT) { paramThread.threadLocals = new ThreadLocalMap(this, paramT); }
  
  static ThreadLocalMap createInheritedMap(ThreadLocalMap paramThreadLocalMap) { return new ThreadLocalMap(paramThreadLocalMap, null); }
  
  T childValue(T paramT) { throw new UnsupportedOperationException(); }
  
  static final class SuppliedThreadLocal<T> extends ThreadLocal<T> {
    private final Supplier<? extends T> supplier;
    
    SuppliedThreadLocal(Supplier<? extends T> param1Supplier) { this.supplier = (Supplier)Objects.requireNonNull(param1Supplier); }
    
    protected T initialValue() { return (T)this.supplier.get(); }
  }
  
  static class ThreadLocalMap {
    private static final int INITIAL_CAPACITY = 16;
    
    private Entry[] table;
    
    private int size = 0;
    
    private int threshold;
    
    private void setThreshold(int param1Int) { this.threshold = param1Int * 2 / 3; }
    
    private static int nextIndex(int param1Int1, int param1Int2) { return (param1Int1 + 1 < param1Int2) ? (param1Int1 + 1) : 0; }
    
    private static int prevIndex(int param1Int1, int param1Int2) { return (param1Int1 - 1 >= 0) ? (param1Int1 - 1) : (param1Int2 - 1); }
    
    ThreadLocalMap(ThreadLocal<?> param1ThreadLocal, Object param1Object) {
      this.table = new Entry[16];
      int i = param1ThreadLocal.threadLocalHashCode & 0xF;
      this.table[i] = new Entry(param1ThreadLocal, param1Object);
      this.size = 1;
      setThreshold(16);
    }
    
    private ThreadLocalMap(ThreadLocalMap param1ThreadLocalMap) {
      Entry[] arrayOfEntry = param1ThreadLocalMap.table;
      int i = arrayOfEntry.length;
      setThreshold(i);
      this.table = new Entry[i];
      for (byte b = 0; b < i; b++) {
        Entry entry = arrayOfEntry[b];
        if (entry != null) {
          ThreadLocal threadLocal = (ThreadLocal)entry.get();
          if (threadLocal != null) {
            Object object = threadLocal.childValue(entry.value);
            Entry entry1 = new Entry(threadLocal, object);
            int j;
            for (j = threadLocal.threadLocalHashCode & i - 1; this.table[j] != null; j = nextIndex(j, i));
            this.table[j] = entry1;
            this.size++;
          } 
        } 
      } 
    }
    
    private Entry getEntry(ThreadLocal<?> param1ThreadLocal) {
      int i = param1ThreadLocal.threadLocalHashCode & this.table.length - 1;
      Entry entry = this.table[i];
      return (entry != null && entry.get() == param1ThreadLocal) ? entry : getEntryAfterMiss(param1ThreadLocal, i, entry);
    }
    
    private Entry getEntryAfterMiss(ThreadLocal<?> param1ThreadLocal, int param1Int, Entry param1Entry) {
      Entry[] arrayOfEntry = this.table;
      int i = arrayOfEntry.length;
      while (param1Entry != null) {
        ThreadLocal threadLocal = (ThreadLocal)param1Entry.get();
        if (threadLocal == param1ThreadLocal)
          return param1Entry; 
        if (threadLocal == null) {
          expungeStaleEntry(param1Int);
        } else {
          param1Int = nextIndex(param1Int, i);
        } 
        param1Entry = arrayOfEntry[param1Int];
      } 
      return null;
    }
    
    private void set(ThreadLocal<?> param1ThreadLocal, Object param1Object) {
      Entry[] arrayOfEntry = this.table;
      int i = arrayOfEntry.length;
      int j = param1ThreadLocal.threadLocalHashCode & i - 1;
      for (Entry entry = arrayOfEntry[j]; entry != null; entry = arrayOfEntry[j = nextIndex(j, i)]) {
        ThreadLocal threadLocal = (ThreadLocal)entry.get();
        if (threadLocal == param1ThreadLocal) {
          entry.value = param1Object;
          return;
        } 
        if (threadLocal == null) {
          replaceStaleEntry(param1ThreadLocal, param1Object, j);
          return;
        } 
      } 
      arrayOfEntry[j] = new Entry(param1ThreadLocal, param1Object);
      int k = ++this.size;
      if (!cleanSomeSlots(j, k) && k >= this.threshold)
        rehash(); 
    }
    
    private void remove(ThreadLocal<?> param1ThreadLocal) {
      Entry[] arrayOfEntry = this.table;
      int i = arrayOfEntry.length;
      int j = param1ThreadLocal.threadLocalHashCode & i - 1;
      for (Entry entry = arrayOfEntry[j]; entry != null; entry = arrayOfEntry[j = nextIndex(j, i)]) {
        if (entry.get() == param1ThreadLocal) {
          entry.clear();
          expungeStaleEntry(j);
          return;
        } 
      } 
    }
    
    private void replaceStaleEntry(ThreadLocal<?> param1ThreadLocal, Object param1Object, int param1Int) {
      Entry[] arrayOfEntry = this.table;
      int i = arrayOfEntry.length;
      int j = param1Int;
      Entry entry;
      int k;
      for (k = prevIndex(param1Int, i); (entry = arrayOfEntry[k]) != null; k = prevIndex(k, i)) {
        if (entry.get() == null)
          j = k; 
      } 
      for (k = nextIndex(param1Int, i); (entry = arrayOfEntry[k]) != null; k = nextIndex(k, i)) {
        ThreadLocal threadLocal = (ThreadLocal)entry.get();
        if (threadLocal == param1ThreadLocal) {
          entry.value = param1Object;
          arrayOfEntry[k] = arrayOfEntry[param1Int];
          arrayOfEntry[param1Int] = entry;
          if (j == param1Int)
            j = k; 
          cleanSomeSlots(expungeStaleEntry(j), i);
          return;
        } 
        if (threadLocal == null && j == param1Int)
          j = k; 
      } 
      (arrayOfEntry[param1Int]).value = null;
      arrayOfEntry[param1Int] = new Entry(param1ThreadLocal, param1Object);
      if (j != param1Int)
        cleanSomeSlots(expungeStaleEntry(j), i); 
    }
    
    private int expungeStaleEntry(int param1Int) {
      Entry[] arrayOfEntry = this.table;
      int i = arrayOfEntry.length;
      (arrayOfEntry[param1Int]).value = null;
      arrayOfEntry[param1Int] = null;
      this.size--;
      Entry entry;
      int j;
      for (j = nextIndex(param1Int, i); (entry = arrayOfEntry[j]) != null; j = nextIndex(j, i)) {
        ThreadLocal threadLocal = (ThreadLocal)entry.get();
        if (threadLocal == null) {
          entry.value = null;
          arrayOfEntry[j] = null;
          this.size--;
        } else {
          int k = threadLocal.threadLocalHashCode & i - 1;
          if (k != j) {
            arrayOfEntry[j] = null;
            while (arrayOfEntry[k] != null)
              k = nextIndex(k, i); 
            arrayOfEntry[k] = entry;
          } 
        } 
      } 
      return j;
    }
    
    private boolean cleanSomeSlots(int param1Int1, int param1Int2) {
      boolean bool = false;
      Entry[] arrayOfEntry = this.table;
      int i = arrayOfEntry.length;
      do {
        param1Int1 = nextIndex(param1Int1, i);
        Entry entry = arrayOfEntry[param1Int1];
        if (entry == null || entry.get() != null)
          continue; 
        param1Int2 = i;
        bool = true;
        param1Int1 = expungeStaleEntry(param1Int1);
      } while (param1Int2 >>>= 1 != 0);
      return bool;
    }
    
    private void rehash() {
      expungeStaleEntries();
      if (this.size >= this.threshold - this.threshold / 4)
        resize(); 
    }
    
    private void resize() {
      Entry[] arrayOfEntry1 = this.table;
      int i = arrayOfEntry1.length;
      int j = i * 2;
      Entry[] arrayOfEntry2 = new Entry[j];
      byte b1 = 0;
      for (byte b2 = 0; b2 < i; b2++) {
        Entry entry = arrayOfEntry1[b2];
        if (entry != null) {
          ThreadLocal threadLocal = (ThreadLocal)entry.get();
          if (threadLocal == null) {
            entry.value = null;
          } else {
            int k;
            for (k = threadLocal.threadLocalHashCode & j - 1; arrayOfEntry2[k] != null; k = nextIndex(k, j));
            arrayOfEntry2[k] = entry;
            b1++;
          } 
        } 
      } 
      setThreshold(j);
      this.size = b1;
      this.table = arrayOfEntry2;
    }
    
    private void expungeStaleEntries() {
      for (Entry entry : this.table) {
        if (entry != null && entry.get() == null)
          expungeStaleEntry(null); 
      } 
    }
    
    static class Entry extends WeakReference<ThreadLocal<?>> {
      Object value;
      
      Entry(ThreadLocal<?> param2ThreadLocal, Object param2Object) {
        super(param2ThreadLocal);
        this.value = param2Object;
      }
    }
  }
  
  static class Entry extends WeakReference<ThreadLocal<?>> {
    Object value;
    
    Entry(ThreadLocal<?> param1ThreadLocal, Object param1Object) {
      super(param1ThreadLocal);
      this.value = param1Object;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ThreadLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */