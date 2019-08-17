package sun.misc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class CacheEnumerator implements Enumeration {
  boolean keys;
  
  int index;
  
  CacheEntry[] table;
  
  CacheEntry entry;
  
  CacheEnumerator(CacheEntry[] paramArrayOfCacheEntry, boolean paramBoolean) {
    this.table = paramArrayOfCacheEntry;
    this.keys = paramBoolean;
    this.index = paramArrayOfCacheEntry.length;
  }
  
  public boolean hasMoreElements() {
    while (this.index >= 0) {
      while (this.entry != null) {
        if (this.entry.check() != null)
          return true; 
        this.entry = this.entry.next;
      } 
      while (--this.index >= 0 && (this.entry = this.table[this.index]) == null);
    } 
    return false;
  }
  
  public Object nextElement() {
    while (this.index >= 0) {
      if (this.entry == null)
        while (--this.index >= 0 && (this.entry = this.table[this.index]) == null); 
      if (this.entry != null) {
        CacheEntry cacheEntry = this.entry;
        this.entry = cacheEntry.next;
        if (cacheEntry.check() != null)
          return this.keys ? cacheEntry.key : cacheEntry.check(); 
      } 
    } 
    throw new NoSuchElementException("CacheEnumerator");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\CacheEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */