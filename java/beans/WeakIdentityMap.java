package java.beans;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

abstract class WeakIdentityMap<T> extends Object {
  private static final int MAXIMUM_CAPACITY = 1073741824;
  
  private static final Object NULL = new Object();
  
  private final ReferenceQueue<Object> queue = new ReferenceQueue();
  
  private int threshold = 6;
  
  private int size = 0;
  
  public T get(Object paramObject) {
    removeStaleEntries();
    if (paramObject == null)
      paramObject = NULL; 
    int i = paramObject.hashCode();
    Entry[] arrayOfEntry = this.table;
    int j = getIndex(arrayOfEntry, i);
    for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.isMatched(paramObject, i))
        return (T)entry.value; 
    } 
    synchronized (NULL) {
      j = getIndex(this.table, i);
      for (Entry entry1 = this.table[j]; entry1 != null; entry1 = entry1.next) {
        if (entry1.isMatched(paramObject, i))
          return (T)entry1.value; 
      } 
      Object object = create(paramObject);
      this.table[j] = new Entry(paramObject, i, object, this.queue, this.table[j]);
      if (++this.size >= this.threshold)
        if (this.table.length == 1073741824) {
          this.threshold = Integer.MAX_VALUE;
        } else {
          removeStaleEntries();
          arrayOfEntry = newTable(this.table.length * 2);
          transfer(this.table, arrayOfEntry);
          if (this.size >= this.threshold / 2) {
            this.table = arrayOfEntry;
            this.threshold *= 2;
          } else {
            transfer(arrayOfEntry, this.table);
          } 
        }  
      return (T)object;
    } 
  }
  
  protected abstract T create(Object paramObject);
  
  private void removeStaleEntries() {
    Reference reference = this.queue.poll();
    if (reference != null)
      synchronized (NULL) {
        do {
          Entry entry1;
          int i = getIndex(this.table, entry1.hash);
          Entry entry2 = this.table[i];
          for (Entry entry3 = entry2; entry3 != null; entry3 = entry) {
            Entry entry = entry3.next;
            if (entry3 == entry1) {
              if (entry2 == entry1) {
                this.table[i] = entry;
              } else {
                entry2.next = entry;
              } 
              entry1.value = null;
              entry1.next = null;
              this.size--;
              break;
            } 
            entry2 = entry3;
          } 
          reference = this.queue.poll();
        } while (reference != null);
      }  
  }
  
  private void transfer(Entry<T>[] paramArrayOfEntry1, Entry<T>[] paramArrayOfEntry2) {
    for (byte b = 0; b < paramArrayOfEntry1.length; b++) {
      Entry<T> entry = paramArrayOfEntry1[b];
      paramArrayOfEntry1[b] = null;
      while (entry != null) {
        Entry entry1 = entry.next;
        Object object = entry.get();
        if (object == null) {
          entry.value = null;
          entry.next = null;
          this.size--;
        } else {
          int i = getIndex(paramArrayOfEntry2, entry.hash);
          entry.next = paramArrayOfEntry2[i];
          paramArrayOfEntry2[i] = entry;
        } 
        entry = entry1;
      } 
    } 
  }
  
  private Entry<T>[] newTable(int paramInt) { return (Entry[])new Entry[paramInt]; }
  
  private static int getIndex(Entry<?>[] paramArrayOfEntry, int paramInt) { return paramInt & paramArrayOfEntry.length - 1; }
  
  private static class Entry<T> extends WeakReference<Object> {
    private final int hash;
    
    Entry(Object param1Object, int param1Int, T param1T, ReferenceQueue<Object> param1ReferenceQueue, Entry<T> param1Entry) {
      super(param1Object, param1ReferenceQueue);
      this.hash = param1Int;
      this.value = param1T;
      this.next = param1Entry;
    }
    
    boolean isMatched(Object param1Object, int param1Int) { return (this.hash == param1Int && param1Object == get()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\WeakIdentityMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */