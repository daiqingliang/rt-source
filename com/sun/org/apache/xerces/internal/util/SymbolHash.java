package com.sun.org.apache.xerces.internal.util;

public class SymbolHash {
  protected static final int TABLE_SIZE = 101;
  
  protected static final int MAX_HASH_COLLISIONS = 40;
  
  protected static final int MULTIPLIERS_SIZE = 32;
  
  protected static final int MULTIPLIERS_MASK = 31;
  
  protected int fTableSize;
  
  protected Entry[] fBuckets;
  
  protected int fNum = 0;
  
  protected int[] fHashMultipliers;
  
  public SymbolHash() { this(101); }
  
  public SymbolHash(int paramInt) {
    this.fTableSize = paramInt;
    this.fBuckets = new Entry[this.fTableSize];
  }
  
  public void put(Object paramObject1, Object paramObject2) {
    byte b = 0;
    int i = hash(paramObject1);
    int j = i % this.fTableSize;
    Entry entry;
    for (entry = this.fBuckets[j]; entry != null; entry = entry.next) {
      if (paramObject1.equals(entry.key)) {
        entry.value = paramObject2;
        return;
      } 
      b++;
    } 
    if (this.fNum >= this.fTableSize) {
      rehash();
      j = i % this.fTableSize;
    } else if (b >= 40 && paramObject1 instanceof String) {
      rebalance();
      j = hash(paramObject1) % this.fTableSize;
    } 
    entry = new Entry(paramObject1, paramObject2, this.fBuckets[j]);
    this.fBuckets[j] = entry;
    this.fNum++;
  }
  
  public Object get(Object paramObject) {
    int i = hash(paramObject) % this.fTableSize;
    Entry entry = search(paramObject, i);
    return (entry != null) ? entry.value : null;
  }
  
  public int getLength() { return this.fNum; }
  
  public int getValues(Object[] paramArrayOfObject, int paramInt) {
    byte b = 0;
    int i = 0;
    while (b < this.fTableSize && i < this.fNum) {
      for (Entry entry = this.fBuckets[b]; entry != null; entry = entry.next) {
        paramArrayOfObject[paramInt + i] = entry.value;
        i++;
      } 
      b++;
    } 
    return this.fNum;
  }
  
  public Object[] getEntries() {
    Object[] arrayOfObject = new Object[this.fNum << 1];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < this.fTableSize && b2 < this.fNum << 1) {
      for (Entry entry = this.fBuckets[b1]; entry != null; entry = entry.next) {
        arrayOfObject[b2] = entry.key;
        arrayOfObject[++b2] = entry.value;
        b2++;
      } 
      b1++;
    } 
    return arrayOfObject;
  }
  
  public SymbolHash makeClone() {
    SymbolHash symbolHash = new SymbolHash(this.fTableSize);
    symbolHash.fNum = this.fNum;
    symbolHash.fHashMultipliers = (this.fHashMultipliers != null) ? (int[])this.fHashMultipliers.clone() : null;
    for (byte b = 0; b < this.fTableSize; b++) {
      if (this.fBuckets[b] != null)
        symbolHash.fBuckets[b] = this.fBuckets[b].makeClone(); 
    } 
    return symbolHash;
  }
  
  public void clear() {
    for (byte b = 0; b < this.fTableSize; b++)
      this.fBuckets[b] = null; 
    this.fNum = 0;
    this.fHashMultipliers = null;
  }
  
  protected Entry search(Object paramObject, int paramInt) {
    for (Entry entry = this.fBuckets[paramInt]; entry != null; entry = entry.next) {
      if (paramObject.equals(entry.key))
        return entry; 
    } 
    return null;
  }
  
  protected int hash(Object paramObject) { return (this.fHashMultipliers == null || !(paramObject instanceof String)) ? (paramObject.hashCode() & 0x7FFFFFFF) : hash0((String)paramObject); }
  
  private int hash0(String paramString) {
    int i = 0;
    int j = paramString.length();
    int[] arrayOfInt = this.fHashMultipliers;
    for (byte b = 0; b < j; b++)
      i = i * arrayOfInt[b & 0x1F] + paramString.charAt(b); 
    return i & 0x7FFFFFFF;
  }
  
  protected void rehash() { rehashCommon((this.fBuckets.length << 1) + 1); }
  
  protected void rebalance() {
    if (this.fHashMultipliers == null)
      this.fHashMultipliers = new int[32]; 
    PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
    rehashCommon(this.fBuckets.length);
  }
  
  private void rehashCommon(int paramInt) {
    int i = this.fBuckets.length;
    Entry[] arrayOfEntry1 = this.fBuckets;
    Entry[] arrayOfEntry2 = new Entry[paramInt];
    this.fBuckets = arrayOfEntry2;
    this.fTableSize = this.fBuckets.length;
    int j = i;
    while (j-- > 0) {
      Entry entry = arrayOfEntry1[j];
      while (entry != null) {
        Entry entry1 = entry;
        entry = entry.next;
        int k = hash(entry1.key) % paramInt;
        entry1.next = arrayOfEntry2[k];
        arrayOfEntry2[k] = entry1;
      } 
    } 
  }
  
  protected static final class Entry {
    public Object key = null;
    
    public Object value = null;
    
    public Entry next = null;
    
    public Entry() {}
    
    public Entry(Object param1Object1, Object param1Object2, Entry param1Entry) {}
    
    public Entry makeClone() {
      Entry entry = new Entry();
      entry.key = this.key;
      entry.value = this.value;
      if (this.next != null)
        entry.next = this.next.makeClone(); 
      return entry;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\SymbolHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */