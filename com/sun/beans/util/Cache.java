package com.sun.beans.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Objects;

public abstract class Cache<K, V> extends Object {
  private static final int MAXIMUM_CAPACITY = 1073741824;
  
  private final boolean identity;
  
  private final Kind keyKind;
  
  private final Kind valueKind;
  
  private final ReferenceQueue<Object> queue = new ReferenceQueue();
  
  private int threshold = 6;
  
  private int size;
  
  public abstract V create(K paramK);
  
  public Cache(Kind paramKind1, Kind paramKind2) { this(paramKind1, paramKind2, false); }
  
  public Cache(Kind paramKind1, Kind paramKind2, boolean paramBoolean) {
    Objects.requireNonNull(paramKind1, "keyKind");
    Objects.requireNonNull(paramKind2, "valueKind");
    this.keyKind = paramKind1;
    this.valueKind = paramKind2;
    this.identity = paramBoolean;
  }
  
  public final V get(K paramK) {
    Objects.requireNonNull(paramK, "key");
    removeStaleEntries();
    int i = hash(paramK);
    CacheEntry[] arrayOfCacheEntry = this.table;
    Object object = getEntryValue(paramK, i, arrayOfCacheEntry[index(i, arrayOfCacheEntry)]);
    if (object != null)
      return (V)object; 
    synchronized (this.queue) {
      object = getEntryValue(paramK, i, this.table[index(i, this.table)]);
      if (object != null)
        return (V)object; 
      Object object1 = create(paramK);
      Objects.requireNonNull(object1, "value");
      int j = index(i, this.table);
      this.table[j] = new CacheEntry(i, paramK, object1, this.table[j], null);
      if (++this.size >= this.threshold)
        if (this.table.length == 1073741824) {
          this.threshold = Integer.MAX_VALUE;
        } else {
          removeStaleEntries();
          arrayOfCacheEntry = newTable(this.table.length << 1);
          transfer(this.table, arrayOfCacheEntry);
          if (this.size >= this.threshold / 2) {
            this.table = arrayOfCacheEntry;
            this.threshold <<= 1;
          } else {
            transfer(arrayOfCacheEntry, this.table);
          } 
          removeStaleEntries();
        }  
      return (V)object1;
    } 
  }
  
  public final void remove(K paramK) {
    if (paramK != null)
      synchronized (this.queue) {
        removeStaleEntries();
        int i = hash(paramK);
        int j = index(i, this.table);
        CacheEntry cacheEntry1 = this.table[j];
        for (CacheEntry cacheEntry2 = cacheEntry1; cacheEntry2 != null; cacheEntry2 = cacheEntry) {
          CacheEntry cacheEntry;
          if (cacheEntry2.matches(i, paramK)) {
            if (cacheEntry2 == cacheEntry1) {
              this.table[j] = cacheEntry;
            } else {
              cacheEntry1.next = cacheEntry;
            } 
            cacheEntry2.unlink();
            break;
          } 
          cacheEntry1 = cacheEntry2;
        } 
      }  
  }
  
  public final void clear() {
    synchronized (this.queue) {
      int i = this.table.length;
      while (0 < i--) {
        for (CacheEntry cacheEntry = this.table[i]; cacheEntry != null; cacheEntry = cacheEntry1) {
          CacheEntry cacheEntry1;
          cacheEntry.unlink();
        } 
        this.table[i] = null;
      } 
      while (null != this.queue.poll());
    } 
  }
  
  private int hash(Object paramObject) {
    if (this.identity) {
      int j = System.identityHashCode(paramObject);
      return (j << 1) - (j << 8);
    } 
    int i = paramObject.hashCode();
    i ^= i >>> 20 ^ i >>> 12;
    return i ^ i >>> 7 ^ i >>> 4;
  }
  
  private static int index(int paramInt, Object[] paramArrayOfObject) { return paramInt & paramArrayOfObject.length - 1; }
  
  private CacheEntry<K, V>[] newTable(int paramInt) { return (CacheEntry[])new CacheEntry[paramInt]; }
  
  private V getEntryValue(K paramK, int paramInt, CacheEntry<K, V> paramCacheEntry) {
    while (paramCacheEntry != null) {
      if (paramCacheEntry.matches(paramInt, paramK))
        return (V)paramCacheEntry.value.getReferent(); 
      paramCacheEntry = paramCacheEntry.next;
    } 
    return null;
  }
  
  private void removeStaleEntries() {
    Reference reference = this.queue.poll();
    if (reference != null)
      synchronized (this.queue) {
        do {
          if (reference instanceof Ref) {
            Ref ref = (Ref)reference;
            CacheEntry cacheEntry = (CacheEntry)ref.getOwner();
            if (cacheEntry != null) {
              int i = index(cacheEntry.hash, this.table);
              CacheEntry cacheEntry1 = this.table[i];
              for (CacheEntry cacheEntry2 = cacheEntry1; cacheEntry2 != null; cacheEntry2 = cacheEntry3) {
                CacheEntry cacheEntry3 = cacheEntry2.next;
                if (cacheEntry2 == cacheEntry) {
                  if (cacheEntry2 == cacheEntry1) {
                    this.table[i] = cacheEntry3;
                  } else {
                    cacheEntry1.next = cacheEntry3;
                  } 
                  cacheEntry2.unlink();
                  break;
                } 
                cacheEntry1 = cacheEntry2;
              } 
            } 
          } 
          reference = this.queue.poll();
        } while (reference != null);
      }  
  }
  
  private void transfer(CacheEntry<K, V>[] paramArrayOfCacheEntry1, CacheEntry<K, V>[] paramArrayOfCacheEntry2) {
    int i = paramArrayOfCacheEntry1.length;
    while (0 < i--) {
      CacheEntry<K, V> cacheEntry = paramArrayOfCacheEntry1[i];
      paramArrayOfCacheEntry1[i] = null;
      while (cacheEntry != null) {
        CacheEntry cacheEntry1;
        if (cacheEntry.key.isStale() || cacheEntry.value.isStale()) {
          cacheEntry.unlink();
        } else {
          int j = index(cacheEntry.hash, paramArrayOfCacheEntry2);
          cacheEntry.next = paramArrayOfCacheEntry2[j];
          paramArrayOfCacheEntry2[j] = cacheEntry;
        } 
        cacheEntry = cacheEntry1;
      } 
    } 
  }
  
  private final class CacheEntry<K, V> extends Object {
    private final int hash;
    
    private final Cache.Ref<K> key;
    
    private final Cache.Ref<V> value;
    
    private CacheEntry(int param1Int, K param1K, V param1V, CacheEntry<K, V> param1CacheEntry) {
      this.hash = param1Int;
      this.key = this$0.keyKind.create(this, param1K, this$0.queue);
      this.value = this$0.valueKind.create(this, param1V, this$0.queue);
      this.next = param1CacheEntry;
    }
    
    private boolean matches(int param1Int, Object param1Object) {
      if (this.hash != param1Int)
        return false; 
      Object object = this.key.getReferent();
      return (object == param1Object || (!Cache.this.identity && object != null && object.equals(param1Object)));
    }
    
    private void unlink() {
      this.next = null;
      this.key.removeOwner();
      this.value.removeOwner();
      Cache.this.size--;
    }
  }
  
  public final abstract enum Kind {
    STRONG, SOFT, WEAK;
    
    abstract <T> Cache.Ref<T> create(Object param1Object, T param1T, ReferenceQueue<? super T> param1ReferenceQueue);
    
    static  {
      // Byte code:
      //   0: new com/sun/beans/util/Cache$Kind$1
      //   3: dup
      //   4: ldc 'STRONG'
      //   6: iconst_0
      //   7: invokespecial <init> : (Ljava/lang/String;I)V
      //   10: putstatic com/sun/beans/util/Cache$Kind.STRONG : Lcom/sun/beans/util/Cache$Kind;
      //   13: new com/sun/beans/util/Cache$Kind$2
      //   16: dup
      //   17: ldc 'SOFT'
      //   19: iconst_1
      //   20: invokespecial <init> : (Ljava/lang/String;I)V
      //   23: putstatic com/sun/beans/util/Cache$Kind.SOFT : Lcom/sun/beans/util/Cache$Kind;
      //   26: new com/sun/beans/util/Cache$Kind$3
      //   29: dup
      //   30: ldc 'WEAK'
      //   32: iconst_2
      //   33: invokespecial <init> : (Ljava/lang/String;I)V
      //   36: putstatic com/sun/beans/util/Cache$Kind.WEAK : Lcom/sun/beans/util/Cache$Kind;
      //   39: iconst_3
      //   40: anewarray com/sun/beans/util/Cache$Kind
      //   43: dup
      //   44: iconst_0
      //   45: getstatic com/sun/beans/util/Cache$Kind.STRONG : Lcom/sun/beans/util/Cache$Kind;
      //   48: aastore
      //   49: dup
      //   50: iconst_1
      //   51: getstatic com/sun/beans/util/Cache$Kind.SOFT : Lcom/sun/beans/util/Cache$Kind;
      //   54: aastore
      //   55: dup
      //   56: iconst_2
      //   57: getstatic com/sun/beans/util/Cache$Kind.WEAK : Lcom/sun/beans/util/Cache$Kind;
      //   60: aastore
      //   61: putstatic com/sun/beans/util/Cache$Kind.$VALUES : [Lcom/sun/beans/util/Cache$Kind;
      //   64: return
    }
    
    private static final class Soft<T> extends SoftReference<T> implements Cache.Ref<T> {
      private Object owner;
      
      private Soft(Object param2Object, T param2T, ReferenceQueue<? super T> param2ReferenceQueue) {
        super(param2T, param2ReferenceQueue);
        this.owner = param2Object;
      }
      
      public Object getOwner() { return this.owner; }
      
      public T getReferent() { return (T)get(); }
      
      public boolean isStale() { return (null == get()); }
      
      public void removeOwner() { this.owner = null; }
    }
    
    private static final class Strong<T> extends Object implements Cache.Ref<T> {
      private Object owner;
      
      private final T referent;
      
      private Strong(Object param2Object, T param2T) {
        this.owner = param2Object;
        this.referent = param2T;
      }
      
      public Object getOwner() { return this.owner; }
      
      public T getReferent() { return (T)this.referent; }
      
      public boolean isStale() { return false; }
      
      public void removeOwner() { this.owner = null; }
    }
    
    private static final class Weak<T> extends WeakReference<T> implements Cache.Ref<T> {
      private Object owner;
      
      private Weak(Object param2Object, T param2T, ReferenceQueue<? super T> param2ReferenceQueue) {
        super(param2T, param2ReferenceQueue);
        this.owner = param2Object;
      }
      
      public Object getOwner() { return this.owner; }
      
      public T getReferent() { return (T)get(); }
      
      public boolean isStale() { return (null == get()); }
      
      public void removeOwner() { this.owner = null; }
    }
  }
  
  private static interface Ref<T> {
    Object getOwner();
    
    T getReferent();
    
    boolean isStale();
    
    void removeOwner();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\bean\\util\Cache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */