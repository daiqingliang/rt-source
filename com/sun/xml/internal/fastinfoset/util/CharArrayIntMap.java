package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class CharArrayIntMap extends KeyIntMap {
  private CharArrayIntMap _readOnlyMap;
  
  protected int _totalCharacterCount;
  
  private Entry[] _table = new Entry[this._capacity];
  
  public CharArrayIntMap(int paramInt, float paramFloat) { super(paramInt, paramFloat); }
  
  public CharArrayIntMap(int paramInt) { this(paramInt, 0.75F); }
  
  public CharArrayIntMap() { this(16, 0.75F); }
  
  public final void clear() {
    for (byte b = 0; b < this._table.length; b++)
      this._table[b] = null; 
    this._size = 0;
    this._totalCharacterCount = 0;
  }
  
  public final void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean) {
    if (!(paramKeyIntMap instanceof CharArrayIntMap))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramKeyIntMap })); 
    setReadOnlyMap((CharArrayIntMap)paramKeyIntMap, paramBoolean);
  }
  
  public final void setReadOnlyMap(CharArrayIntMap paramCharArrayIntMap, boolean paramBoolean) {
    this._readOnlyMap = paramCharArrayIntMap;
    if (this._readOnlyMap != null) {
      this._readOnlyMapSize = this._readOnlyMap.size();
      if (paramBoolean)
        clear(); 
    } else {
      this._readOnlyMapSize = 0;
    } 
  }
  
  public final int get(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = hashHash(CharArray.hashCode(paramArrayOfChar, paramInt1, paramInt2));
    return get(paramArrayOfChar, paramInt1, paramInt2, i);
  }
  
  public final int obtainIndex(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = hashHash(CharArray.hashCode(paramArrayOfChar, paramInt1, paramInt2));
    if (this._readOnlyMap != null) {
      int k = this._readOnlyMap.get(paramArrayOfChar, paramInt1, paramInt2, i);
      if (k != -1)
        return k; 
    } 
    int j = indexFor(i, this._table.length);
    for (Entry entry = this._table[j]; entry != null; entry = entry._next) {
      if (entry._hash == i && entry.equalsCharArray(paramArrayOfChar, paramInt1, paramInt2))
        return entry._value; 
    } 
    if (paramBoolean) {
      char[] arrayOfChar = new char[paramInt2];
      System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, paramInt2);
      paramArrayOfChar = arrayOfChar;
      paramInt1 = 0;
    } 
    addEntry(paramArrayOfChar, paramInt1, paramInt2, i, this._size + this._readOnlyMapSize, j);
    return -1;
  }
  
  public final int getTotalCharacterCount() { return this._totalCharacterCount; }
  
  private final int get(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3) {
    if (this._readOnlyMap != null) {
      int j = this._readOnlyMap.get(paramArrayOfChar, paramInt1, paramInt2, paramInt3);
      if (j != -1)
        return j; 
    } 
    int i = indexFor(paramInt3, this._table.length);
    for (Entry entry = this._table[i]; entry != null; entry = entry._next) {
      if (entry._hash == paramInt3 && entry.equalsCharArray(paramArrayOfChar, paramInt1, paramInt2))
        return entry._value; 
    } 
    return -1;
  }
  
  private final void addEntry(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    Entry entry = this._table[paramInt5];
    this._table[paramInt5] = new Entry(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4, entry);
    this._totalCharacterCount += paramInt2;
    if (this._size++ >= this._threshold)
      resize(2 * this._table.length); 
  }
  
  private final void resize(int paramInt) {
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
  
  static class Entry extends KeyIntMap.BaseEntry {
    final char[] _ch;
    
    final int _start;
    
    final int _length;
    
    Entry _next;
    
    public Entry(char[] param1ArrayOfChar, int param1Int1, int param1Int2, int param1Int3, int param1Int4, Entry param1Entry) {
      super(param1Int3, param1Int4);
      this._ch = param1ArrayOfChar;
      this._start = param1Int1;
      this._length = param1Int2;
      this._next = param1Entry;
    }
    
    public final boolean equalsCharArray(char[] param1ArrayOfChar, int param1Int1, int param1Int2) {
      if (this._length == param1Int2) {
        int i = this._length;
        int j = this._start;
        int k = param1Int1;
        while (i-- != 0) {
          if (this._ch[j++] != param1ArrayOfChar[k++])
            return false; 
        } 
        return true;
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\CharArrayIntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */