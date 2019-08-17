package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public class CacheTable {
  private boolean noReverseMap;
  
  static final int INITIAL_SIZE = 16;
  
  static final int MAX_SIZE = 1073741824;
  
  int size;
  
  int entryCount;
  
  private Entry[] map;
  
  private Entry[] rmap;
  
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private CacheTable() {}
  
  public CacheTable(ORB paramORB, boolean paramBoolean) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    this.noReverseMap = paramBoolean;
    this.size = 16;
    this.entryCount = 0;
    initTables();
  }
  
  private void initTables() {
    this.map = new Entry[this.size];
    this.rmap = this.noReverseMap ? null : new Entry[this.size];
  }
  
  private void grow() {
    if (this.size == 1073741824)
      return; 
    Entry[] arrayOfEntry = this.map;
    int i = this.size;
    this.size <<= 1;
    initTables();
    for (byte b = 0; b < i; b++) {
      for (Entry entry = arrayOfEntry[b]; entry != null; entry = entry.next)
        put_table(entry.key, entry.val); 
    } 
  }
  
  private int moduloTableSize(int paramInt) {
    paramInt += (paramInt << 9 ^ 0xFFFFFFFF);
    paramInt ^= paramInt >>> 14;
    paramInt += (paramInt << 4);
    paramInt ^= paramInt >>> 10;
    return paramInt & this.size - 1;
  }
  
  private int hash(Object paramObject) { return moduloTableSize(System.identityHashCode(paramObject)); }
  
  private int hash(int paramInt) { return moduloTableSize(paramInt); }
  
  public final void put(Object paramObject, int paramInt) {
    if (put_table(paramObject, paramInt)) {
      this.entryCount++;
      if (this.entryCount > this.size * 3 / 4)
        grow(); 
    } 
  }
  
  private boolean put_table(Object paramObject, int paramInt) {
    int i = hash(paramObject);
    Entry entry;
    for (entry = this.map[i]; entry != null; entry = entry.next) {
      if (entry.key == paramObject) {
        if (entry.val != paramInt)
          throw this.wrapper.duplicateIndirectionOffset(); 
        return false;
      } 
    } 
    entry = new Entry(paramObject, paramInt);
    entry.next = this.map[i];
    this.map[i] = entry;
    if (!this.noReverseMap) {
      int j = hash(paramInt);
      entry.rnext = this.rmap[j];
      this.rmap[j] = entry;
    } 
    return true;
  }
  
  public final boolean containsKey(Object paramObject) { return (getVal(paramObject) != -1); }
  
  public final int getVal(Object paramObject) {
    int i = hash(paramObject);
    for (Entry entry = this.map[i]; entry != null; entry = entry.next) {
      if (entry.key == paramObject)
        return entry.val; 
    } 
    return -1;
  }
  
  public final boolean containsVal(int paramInt) { return (getKey(paramInt) != null); }
  
  public final boolean containsOrderedVal(int paramInt) { return containsVal(paramInt); }
  
  public final Object getKey(int paramInt) {
    int i = hash(paramInt);
    for (Entry entry = this.rmap[i]; entry != null; entry = entry.rnext) {
      if (entry.val == paramInt)
        return entry.key; 
    } 
    return null;
  }
  
  public void done() {
    this.map = null;
    this.rmap = null;
  }
  
  class Entry {
    Object key;
    
    int val;
    
    Entry next;
    
    Entry rnext;
    
    public Entry(Object param1Object, int param1Int) {
      this.key = param1Object;
      this.val = param1Int;
      this.next = null;
      this.rnext = null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\CacheTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */