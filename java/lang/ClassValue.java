package java.lang;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ClassValue<T> extends Object {
  private static final Entry<?>[] EMPTY_CACHE = { null };
  
  final int hashCodeForCache = nextHashCode.getAndAdd(1640531527) & 0x3FFFFFFF;
  
  private static final AtomicInteger nextHashCode = new AtomicInteger();
  
  private static final int HASH_INCREMENT = 1640531527;
  
  static final int HASH_MASK = 1073741823;
  
  final Identity identity = new Identity();
  
  private static final Object CRITICAL_SECTION = new Object();
  
  protected abstract T computeValue(Class<?> paramClass);
  
  public T get(Class<?> paramClass) {
    Entry[] arrayOfEntry;
    Entry entry = ClassValueMap.probeHomeLocation(arrayOfEntry = getCacheCarefully(paramClass), this);
    return match(entry) ? (T)entry.value() : (T)getFromBackup(arrayOfEntry, paramClass);
  }
  
  public void remove(Class<?> paramClass) {
    ClassValueMap classValueMap = getMap(paramClass);
    classValueMap.removeEntry(this);
  }
  
  void put(Class<?> paramClass, T paramT) {
    ClassValueMap classValueMap = getMap(paramClass);
    classValueMap.changeEntry(this, paramT);
  }
  
  private static Entry<?>[] getCacheCarefully(Class<?> paramClass) {
    ClassValueMap classValueMap = paramClass.classValueMap;
    return (classValueMap == null) ? EMPTY_CACHE : classValueMap.getCache();
  }
  
  private T getFromBackup(Entry<?>[] paramArrayOfEntry, Class<?> paramClass) {
    Entry entry = ClassValueMap.probeBackupLocations(paramArrayOfEntry, this);
    return (entry != null) ? (T)entry.value() : (T)getFromHashMap(paramClass);
  }
  
  Entry<T> castEntry(Entry<?> paramEntry) { return paramEntry; }
  
  private T getFromHashMap(Class<?> paramClass) {
    classValueMap = getMap(paramClass);
    do {
      entry = classValueMap.startEntry(this);
      if (!entry.isPromise())
        return (T)entry.value(); 
      try {
        entry = makeEntry(entry.version(), computeValue(paramClass));
      } finally {
        entry = classValueMap.finishEntry(this, entry);
      } 
    } while (entry == null);
    return (T)entry.value();
  }
  
  boolean match(Entry<?> paramEntry) { return (paramEntry != null && paramEntry.get() == this.version); }
  
  Version<T> version() { return this.version; }
  
  void bumpVersion() { this.version = new Version(this); }
  
  private static ClassValueMap getMap(Class<?> paramClass) {
    ClassValueMap classValueMap = paramClass.classValueMap;
    return (classValueMap != null) ? classValueMap : initializeMap(paramClass);
  }
  
  private static ClassValueMap initializeMap(Class<?> paramClass) {
    ClassValueMap classValueMap;
    synchronized (CRITICAL_SECTION) {
      if ((classValueMap = paramClass.classValueMap) == null)
        paramClass.classValueMap = classValueMap = new ClassValueMap(paramClass); 
    } 
    return classValueMap;
  }
  
  static <T> Entry<T> makeEntry(Version<T> paramVersion, T paramT) { return new Entry(paramVersion, paramT); }
  
  static class ClassValueMap extends WeakHashMap<Identity, Entry<?>> {
    private final Class<?> type;
    
    private ClassValue.Entry<?>[] cacheArray;
    
    private int cacheLoad;
    
    private int cacheLoadLimit;
    
    private static final int INITIAL_ENTRIES = 32;
    
    private static final int CACHE_LOAD_LIMIT = 67;
    
    private static final int PROBE_LIMIT = 6;
    
    ClassValueMap(Class<?> param1Class) {
      this.type = param1Class;
      sizeCache(32);
    }
    
    ClassValue.Entry<?>[] getCache() { return this.cacheArray; }
    
    <T> ClassValue.Entry<T> startEntry(ClassValue<T> param1ClassValue) {
      ClassValue.Entry entry = (ClassValue.Entry)get(param1ClassValue.identity);
      ClassValue.Version version = param1ClassValue.version();
      if (entry == null) {
        entry = version.promise();
        put(param1ClassValue.identity, entry);
        return entry;
      } 
      if (entry.isPromise()) {
        if (entry.version() != version) {
          entry = version.promise();
          put(param1ClassValue.identity, entry);
        } 
        return entry;
      } 
      if (entry.version() != version) {
        entry = entry.refreshVersion(version);
        put(param1ClassValue.identity, entry);
      } 
      checkCacheLoad();
      addToCache(param1ClassValue, entry);
      return entry;
    }
    
    <T> ClassValue.Entry<T> finishEntry(ClassValue<T> param1ClassValue, ClassValue.Entry<T> param1Entry) {
      ClassValue.Entry entry = (ClassValue.Entry)get(param1ClassValue.identity);
      if (param1Entry == entry) {
        assert param1Entry.isPromise();
        remove(param1ClassValue.identity);
        return null;
      } 
      if (entry != null && entry.isPromise() && entry.version() == param1Entry.version()) {
        ClassValue.Version version = param1ClassValue.version();
        if (param1Entry.version() != version)
          param1Entry = param1Entry.refreshVersion(version); 
        put(param1ClassValue.identity, param1Entry);
        checkCacheLoad();
        addToCache(param1ClassValue, param1Entry);
        return param1Entry;
      } 
      return null;
    }
    
    void removeEntry(ClassValue<?> param1ClassValue) {
      ClassValue.Entry entry = (ClassValue.Entry)remove(param1ClassValue.identity);
      if (entry != null)
        if (entry.isPromise()) {
          put(param1ClassValue.identity, entry);
        } else {
          param1ClassValue.bumpVersion();
          removeStaleEntries(param1ClassValue);
        }  
    }
    
    <T> void changeEntry(ClassValue<T> param1ClassValue, T param1T) {
      ClassValue.Entry entry1 = (ClassValue.Entry)get(param1ClassValue.identity);
      ClassValue.Version version = param1ClassValue.version();
      if (entry1 != null) {
        if (entry1.version() == version && entry1.value() == param1T)
          return; 
        param1ClassValue.bumpVersion();
        removeStaleEntries(param1ClassValue);
      } 
      ClassValue.Entry entry2 = ClassValue.makeEntry(version, param1T);
      put(param1ClassValue.identity, entry2);
      checkCacheLoad();
      addToCache(param1ClassValue, entry2);
    }
    
    static ClassValue.Entry<?> loadFromCache(ClassValue.Entry<?>[] param1ArrayOfEntry, int param1Int) { return param1ArrayOfEntry[param1Int & param1ArrayOfEntry.length - 1]; }
    
    static <T> ClassValue.Entry<T> probeHomeLocation(ClassValue.Entry<?>[] param1ArrayOfEntry, ClassValue<T> param1ClassValue) { return param1ClassValue.castEntry(loadFromCache(param1ArrayOfEntry, param1ClassValue.hashCodeForCache)); }
    
    static <T> ClassValue.Entry<T> probeBackupLocations(ClassValue.Entry<?>[] param1ArrayOfEntry, ClassValue<T> param1ClassValue) {
      int i = param1ArrayOfEntry.length - 1;
      int j = param1ClassValue.hashCodeForCache & i;
      ClassValue.Entry<?> entry = param1ArrayOfEntry[j];
      if (entry == null)
        return null; 
      int k = -1;
      for (int m = j + 1; m < j + 6; m++) {
        ClassValue.Entry<?> entry1 = param1ArrayOfEntry[m & i];
        if (entry1 == null)
          break; 
        if (param1ClassValue.match(entry1)) {
          param1ArrayOfEntry[j] = entry1;
          if (k >= 0) {
            param1ArrayOfEntry[m & i] = ClassValue.Entry.DEAD_ENTRY;
          } else {
            k = m;
          } 
          param1ArrayOfEntry[k & i] = (entryDislocation(param1ArrayOfEntry, k, entry) < 6) ? entry : ClassValue.Entry.DEAD_ENTRY;
          return param1ClassValue.castEntry(entry1);
        } 
        if (!entry1.isLive() && k < 0)
          k = m; 
      } 
      return null;
    }
    
    private static int entryDislocation(ClassValue.Entry<?>[] param1ArrayOfEntry, int param1Int, ClassValue.Entry<?> param1Entry) {
      ClassValue classValue = param1Entry.classValueOrNull();
      if (classValue == null)
        return 0; 
      int i = param1ArrayOfEntry.length - 1;
      return param1Int - classValue.hashCodeForCache & i;
    }
    
    private void sizeCache(int param1Int) {
      assert (param1Int & param1Int - 1) == 0;
      this.cacheLoad = 0;
      this.cacheLoadLimit = (int)(param1Int * 67.0D / 100.0D);
      this.cacheArray = new ClassValue.Entry[param1Int];
    }
    
    private void checkCacheLoad() {
      if (this.cacheLoad >= this.cacheLoadLimit)
        reduceCacheLoad(); 
    }
    
    private void reduceCacheLoad() {
      removeStaleEntries();
      if (this.cacheLoad < this.cacheLoadLimit)
        return; 
      ClassValue.Entry[] arrayOfEntry = getCache();
      if (arrayOfEntry.length > 1073741823)
        return; 
      sizeCache(arrayOfEntry.length * 2);
      for (ClassValue.Entry entry : arrayOfEntry) {
        if (entry != null && entry.isLive())
          addToCache(entry); 
      } 
    }
    
    private void removeStaleEntries(ClassValue.Entry<?>[] param1ArrayOfEntry, int param1Int1, int param1Int2) {
      int i = param1ArrayOfEntry.length - 1;
      int j = 0;
      for (int k = param1Int1; k < param1Int1 + param1Int2; k++) {
        ClassValue.Entry<?> entry = param1ArrayOfEntry[k & i];
        if (entry != null && !entry.isLive()) {
          ClassValue.Entry entry1 = null;
          entry1 = findReplacement(param1ArrayOfEntry, k);
          param1ArrayOfEntry[k & i] = entry1;
          if (entry1 == null)
            j++; 
        } 
      } 
      this.cacheLoad = Math.max(0, this.cacheLoad - j);
    }
    
    private ClassValue.Entry<?> findReplacement(ClassValue.Entry<?>[] param1ArrayOfEntry, int param1Int) {
      ClassValue.Entry<?> entry = null;
      byte b = -1;
      int i = 0;
      int j = param1ArrayOfEntry.length - 1;
      for (int k = param1Int + 1; k < param1Int + 6; k++) {
        ClassValue.Entry<?> entry1 = param1ArrayOfEntry[k & j];
        if (entry1 == null)
          break; 
        if (entry1.isLive()) {
          int m = entryDislocation(param1ArrayOfEntry, k, entry1);
          if (m != 0) {
            int n = k - m;
            if (n <= param1Int)
              if (n == param1Int) {
                b = 1;
                i = k;
                entry = entry1;
              } else if (b <= 0) {
                b = 0;
                i = k;
                entry = entry1;
              }  
          } 
        } 
      } 
      if (b >= 0)
        if (param1ArrayOfEntry[i + true & j] != null) {
          param1ArrayOfEntry[i & j] = ClassValue.Entry.DEAD_ENTRY;
        } else {
          param1ArrayOfEntry[i & j] = null;
          this.cacheLoad--;
        }  
      return entry;
    }
    
    private void removeStaleEntries(ClassValue<?> param1ClassValue) { removeStaleEntries(getCache(), param1ClassValue.hashCodeForCache, 6); }
    
    private void removeStaleEntries() {
      ClassValue.Entry[] arrayOfEntry = getCache();
      removeStaleEntries(arrayOfEntry, 0, arrayOfEntry.length + 6 - 1);
    }
    
    private <T> void addToCache(ClassValue.Entry<T> param1Entry) {
      ClassValue classValue = param1Entry.classValueOrNull();
      if (classValue != null)
        addToCache(classValue, param1Entry); 
    }
    
    private <T> void addToCache(ClassValue<T> param1ClassValue, ClassValue.Entry<T> param1Entry) {
      ClassValue.Entry[] arrayOfEntry = getCache();
      int i = arrayOfEntry.length - 1;
      int j = param1ClassValue.hashCodeForCache & i;
      ClassValue.Entry entry = placeInCache(arrayOfEntry, j, param1Entry, false);
      if (entry == null)
        return; 
      int k = entryDislocation(arrayOfEntry, j, entry);
      int m = j - k;
      for (int n = m; n < m + 6; n++) {
        if (placeInCache(arrayOfEntry, n & i, entry, true) == null)
          return; 
      } 
    }
    
    private ClassValue.Entry<?> placeInCache(ClassValue.Entry<?>[] param1ArrayOfEntry, int param1Int, ClassValue.Entry<?> param1Entry, boolean param1Boolean) {
      ClassValue.Entry entry = overwrittenEntry(param1ArrayOfEntry[param1Int]);
      if (param1Boolean && entry != null)
        return param1Entry; 
      param1ArrayOfEntry[param1Int] = param1Entry;
      return entry;
    }
    
    private <T> ClassValue.Entry<T> overwrittenEntry(ClassValue.Entry<T> param1Entry) {
      if (param1Entry == null) {
        this.cacheLoad++;
      } else if (param1Entry.isLive()) {
        return param1Entry;
      } 
      return null;
    }
  }
  
  static class Entry<T> extends WeakReference<Version<T>> {
    final Object value;
    
    static final Entry<?> DEAD_ENTRY = new Entry(null, null);
    
    Entry(ClassValue.Version<T> param1Version, T param1T) {
      super(param1Version);
      this.value = param1T;
    }
    
    private void assertNotPromise() { assert !isPromise(); }
    
    Entry(ClassValue.Version<T> param1Version) {
      super(param1Version);
      this.value = this;
    }
    
    T value() {
      assertNotPromise();
      return (T)this.value;
    }
    
    boolean isPromise() { return (this.value == this); }
    
    ClassValue.Version<T> version() { return (ClassValue.Version)get(); }
    
    ClassValue<T> classValueOrNull() {
      ClassValue.Version version = version();
      return (version == null) ? null : version.classValue();
    }
    
    boolean isLive() {
      ClassValue.Version version = version();
      if (version == null)
        return false; 
      if (version.isLive())
        return true; 
      clear();
      return false;
    }
    
    Entry<T> refreshVersion(ClassValue.Version<T> param1Version) {
      assertNotPromise();
      Entry entry = new Entry(param1Version, this.value);
      clear();
      return entry;
    }
  }
  
  static class Identity {}
  
  static class Version<T> extends Object {
    private final ClassValue<T> classValue;
    
    private final ClassValue.Entry<T> promise = new ClassValue.Entry(this);
    
    Version(ClassValue<T> param1ClassValue) { this.classValue = param1ClassValue; }
    
    ClassValue<T> classValue() { return this.classValue; }
    
    ClassValue.Entry<T> promise() { return this.promise; }
    
    boolean isLive() { return (this.classValue.version() == this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ClassValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */