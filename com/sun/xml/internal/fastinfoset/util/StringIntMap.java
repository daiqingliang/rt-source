package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class StringIntMap extends KeyIntMap {
  protected static final Entry NULL_ENTRY = new Entry(null, 0, -1, null);
  
  protected StringIntMap _readOnlyMap;
  
  protected Entry _lastEntry = NULL_ENTRY;
  
  protected Entry[] _table = new Entry[this._capacity];
  
  protected int _index;
  
  protected int _totalCharacterCount;
  
  public StringIntMap(int paramInt, float paramFloat) { super(paramInt, paramFloat); }
  
  public StringIntMap(int paramInt) { this(paramInt, 0.75F); }
  
  public StringIntMap() { this(16, 0.75F); }
  
  public void clear() {
    for (byte b = 0; b < this._table.length; b++)
      this._table[b] = null; 
    this._lastEntry = NULL_ENTRY;
    this._size = 0;
    this._index = this._readOnlyMapSize;
    this._totalCharacterCount = 0;
  }
  
  public void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean) {
    if (!(paramKeyIntMap instanceof StringIntMap))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramKeyIntMap })); 
    setReadOnlyMap((StringIntMap)paramKeyIntMap, paramBoolean);
  }
  
  public final void setReadOnlyMap(StringIntMap paramStringIntMap, boolean paramBoolean) {
    this._readOnlyMap = paramStringIntMap;
    if (this._readOnlyMap != null) {
      this._readOnlyMapSize = this._readOnlyMap.size();
      this._index = this._size + this._readOnlyMapSize;
      if (paramBoolean)
        clear(); 
    } else {
      this._readOnlyMapSize = 0;
      this._index = this._size;
    } 
  }
  
  public final int getNextIndex() { return this._index++; }
  
  public final int getIndex() { return this._index; }
  
  public final int obtainIndex(String paramString) {
    int i = hashHash(paramString.hashCode());
    if (this._readOnlyMap != null) {
      int k = this._readOnlyMap.get(paramString, i);
      if (k != -1)
        return k; 
    } 
    int j = indexFor(i, this._table.length);
    for (Entry entry = this._table[j]; entry != null; entry = entry._next) {
      if (entry._hash == i && eq(paramString, entry._key))
        return entry._value; 
    } 
    addEntry(paramString, i, j);
    return -1;
  }
  
  public final void add(String paramString) {
    int i = hashHash(paramString.hashCode());
    int j = indexFor(i, this._table.length);
    addEntry(paramString, i, j);
  }
  
  public final int get(String paramString) { return (paramString == this._lastEntry._key) ? this._lastEntry._value : get(paramString, hashHash(paramString.hashCode())); }
  
  public final int getTotalCharacterCount() { return this._totalCharacterCount; }
  
  private final int get(String paramString, int paramInt) {
    if (this._readOnlyMap != null) {
      int j = this._readOnlyMap.get(paramString, paramInt);
      if (j != -1)
        return j; 
    } 
    int i = indexFor(paramInt, this._table.length);
    for (Entry entry = this._table[i]; entry != null; entry = entry._next) {
      if (entry._hash == paramInt && eq(paramString, entry._key)) {
        this._lastEntry = entry;
        return entry._value;
      } 
    } 
    return -1;
  }
  
  private final void addEntry(String paramString, int paramInt1, int paramInt2) {
    Entry entry = this._table[paramInt2];
    this._table[paramInt2] = new Entry(paramString, paramInt1, this._index++, entry);
    this._totalCharacterCount += paramString.length();
    if (this._size++ >= this._threshold)
      resize(2 * this._table.length); 
  }
  
  protected final void resize(int paramInt) {
    this._capacity = paramInt;
    Entry[] arrayOfEntry1 = this._table;
    int i = arrayOfEntry1.length;
    if (i == 1048576) {
      this._threshold = Integer.MAX_VALUE;
      return;
    } 
    Entry[] arrayOfEntry2 = new Entry[this._capacity];
    transfer(arrayOfEntry2);
    this._table = arrayOfEntry2;
    this._threshold = (int)(this._capacity * this._loadFactor);
  }
  
  private final void transfer(Entry[] paramArrayOfEntry) {
    Entry[] arrayOfEntry = this._table;
    int i = paramArrayOfEntry.length;
    for (byte b = 0; b < arrayOfEntry.length; b++) {
      Entry entry = arrayOfEntry[b];
      if (entry != null) {
        arrayOfEntry[b] = null;
        do {
          Entry entry1 = entry._next;
          int j = indexFor(entry._hash, i);
          entry._next = paramArrayOfEntry[j];
          paramArrayOfEntry[j] = entry;
          entry = entry1;
        } while (entry != null);
      } 
    } 
  }
  
  private final boolean eq(String paramString1, String paramString2) { return (paramString1 == paramString2 || paramString1.equals(paramString2)); }
  
  protected static class Entry extends KeyIntMap.BaseEntry {
    final String _key;
    
    Entry _next;
    
    public Entry(String param1String, int param1Int1, int param1Int2, Entry param1Entry) {
      super(param1Int1, param1Int2);
      this._key = param1String;
      this._next = param1Entry;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\StringIntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */