package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class FixedEntryStringIntMap extends StringIntMap {
  private StringIntMap.Entry _fixedEntry;
  
  public FixedEntryStringIntMap(String paramString, int paramInt, float paramFloat) {
    super(paramInt, paramFloat);
    int i = hashHash(paramString.hashCode());
    int j = indexFor(i, this._table.length);
    this._table[j] = this._fixedEntry = new StringIntMap.Entry(paramString, i, this._index++, null);
    if (this._size++ >= this._threshold)
      resize(2 * this._table.length); 
  }
  
  public FixedEntryStringIntMap(String paramString, int paramInt) { this(paramString, paramInt, 0.75F); }
  
  public FixedEntryStringIntMap(String paramString) { this(paramString, 16, 0.75F); }
  
  public final void clear() {
    int i;
    for (i = 0; i < this._table.length; i++)
      this._table[i] = null; 
    this._lastEntry = NULL_ENTRY;
    if (this._fixedEntry != null) {
      i = indexFor(this._fixedEntry._hash, this._table.length);
      this._table[i] = this._fixedEntry;
      this._fixedEntry._next = null;
      this._size = 1;
      this._index = this._readOnlyMapSize + 1;
    } else {
      this._size = 0;
      this._index = this._readOnlyMapSize;
    } 
  }
  
  public final void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean) {
    if (!(paramKeyIntMap instanceof FixedEntryStringIntMap))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramKeyIntMap })); 
    setReadOnlyMap((FixedEntryStringIntMap)paramKeyIntMap, paramBoolean);
  }
  
  public final void setReadOnlyMap(FixedEntryStringIntMap paramFixedEntryStringIntMap, boolean paramBoolean) {
    this._readOnlyMap = paramFixedEntryStringIntMap;
    if (this._readOnlyMap != null) {
      paramFixedEntryStringIntMap.removeFixedEntry();
      this._readOnlyMapSize = paramFixedEntryStringIntMap.size();
      this._index = this._readOnlyMapSize + this._size;
      if (paramBoolean)
        clear(); 
    } else {
      this._readOnlyMapSize = 0;
    } 
  }
  
  private final void removeFixedEntry() {
    if (this._fixedEntry != null) {
      int i = indexFor(this._fixedEntry._hash, this._table.length);
      StringIntMap.Entry entry = this._table[i];
      if (entry == this._fixedEntry) {
        this._table[i] = this._fixedEntry._next;
      } else {
        StringIntMap.Entry entry1;
        for (entry1 = entry; entry1._next != this._fixedEntry; entry1 = entry1._next);
        entry1._next = this._fixedEntry._next;
      } 
      this._fixedEntry = null;
      this._size--;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\FixedEntryStringIntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */