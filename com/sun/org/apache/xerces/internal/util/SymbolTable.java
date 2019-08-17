package com.sun.org.apache.xerces.internal.util;

public class SymbolTable {
  protected static final int TABLE_SIZE = 101;
  
  protected static final int MAX_HASH_COLLISIONS = 40;
  
  protected static final int MULTIPLIERS_SIZE = 32;
  
  protected static final int MULTIPLIERS_MASK = 31;
  
  protected Entry[] fBuckets = null;
  
  protected int fTableSize;
  
  protected int fCount;
  
  protected int fThreshold;
  
  protected float fLoadFactor;
  
  protected final int fCollisionThreshold;
  
  protected int[] fHashMultipliers;
  
  public SymbolTable(int paramInt, float paramFloat) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt); 
    if (paramFloat <= 0.0F || Float.isNaN(paramFloat))
      throw new IllegalArgumentException("Illegal Load: " + paramFloat); 
    if (paramInt == 0)
      paramInt = 1; 
    this.fLoadFactor = paramFloat;
    this.fTableSize = paramInt;
    this.fBuckets = new Entry[this.fTableSize];
    this.fThreshold = (int)(this.fTableSize * paramFloat);
    this.fCollisionThreshold = (int)(40.0F * paramFloat);
    this.fCount = 0;
  }
  
  public SymbolTable(int paramInt) { this(paramInt, 0.75F); }
  
  public SymbolTable() { this(101, 0.75F); }
  
  public String addSymbol(String paramString) {
    byte b = 0;
    int i = hash(paramString) % this.fTableSize;
    for (Entry entry = this.fBuckets[i]; entry != null; entry = entry.next) {
      if (entry.symbol.equals(paramString))
        return entry.symbol; 
      b++;
    } 
    return addSymbol0(paramString, i, b);
  }
  
  private String addSymbol0(String paramString, int paramInt1, int paramInt2) {
    if (this.fCount >= this.fThreshold) {
      rehash();
      paramInt1 = hash(paramString) % this.fTableSize;
    } else if (paramInt2 >= this.fCollisionThreshold) {
      rebalance();
      paramInt1 = hash(paramString) % this.fTableSize;
    } 
    Entry entry = new Entry(paramString, this.fBuckets[paramInt1]);
    this.fBuckets[paramInt1] = entry;
    this.fCount++;
    return entry.symbol;
  }
  
  public String addSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    byte b = 0;
    int i = hash(paramArrayOfChar, paramInt1, paramInt2) % this.fTableSize;
    for (Entry entry = this.fBuckets[i]; entry != null; entry = entry.next) {
      if (paramInt2 == entry.characters.length) {
        int j = 0;
        while (true) {
          if (j < paramInt2) {
            if (paramArrayOfChar[paramInt1 + j] != entry.characters[j]) {
              b++;
              break;
            } 
            j++;
            continue;
          } 
          return entry.symbol;
        } 
      } else {
        b++;
      } 
    } 
    return addSymbol0(paramArrayOfChar, paramInt1, paramInt2, i, b);
  }
  
  private String addSymbol0(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.fCount >= this.fThreshold) {
      rehash();
      paramInt3 = hash(paramArrayOfChar, paramInt1, paramInt2) % this.fTableSize;
    } else if (paramInt4 >= this.fCollisionThreshold) {
      rebalance();
      paramInt3 = hash(paramArrayOfChar, paramInt1, paramInt2) % this.fTableSize;
    } 
    Entry entry = new Entry(paramArrayOfChar, paramInt1, paramInt2, this.fBuckets[paramInt3]);
    this.fBuckets[paramInt3] = entry;
    this.fCount++;
    return entry.symbol;
  }
  
  public int hash(String paramString) { return (this.fHashMultipliers == null) ? (paramString.hashCode() & 0x7FFFFFFF) : hash0(paramString); }
  
  private int hash0(String paramString) {
    int i = 0;
    int j = paramString.length();
    int[] arrayOfInt = this.fHashMultipliers;
    for (byte b = 0; b < j; b++)
      i = i * arrayOfInt[b & 0x1F] + paramString.charAt(b); 
    return i & 0x7FFFFFFF;
  }
  
  public int hash(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (this.fHashMultipliers == null) {
      char c = Character.MIN_VALUE;
      for (int i = 0; i < paramInt2; i++)
        c = c * 31 + paramArrayOfChar[paramInt1 + i]; 
      return c & 0x7FFFFFFF;
    } 
    return hash0(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private int hash0(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = 0;
    int[] arrayOfInt = this.fHashMultipliers;
    for (int j = 0; j < paramInt2; j++)
      i = i * arrayOfInt[j & 0x1F] + paramArrayOfChar[paramInt1 + j]; 
    return i & 0x7FFFFFFF;
  }
  
  protected void rehash() { rehashCommon(this.fBuckets.length * 2 + 1); }
  
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
    this.fThreshold = (int)(paramInt * this.fLoadFactor);
    this.fBuckets = arrayOfEntry2;
    this.fTableSize = this.fBuckets.length;
    int j = i;
    while (j-- > 0) {
      Entry entry = arrayOfEntry1[j];
      while (entry != null) {
        Entry entry1 = entry;
        entry = entry.next;
        int k = hash(entry1.symbol) % paramInt;
        entry1.next = arrayOfEntry2[k];
        arrayOfEntry2[k] = entry1;
      } 
    } 
  }
  
  public boolean containsSymbol(String paramString) {
    int i = hash(paramString) % this.fTableSize;
    int j = paramString.length();
    for (Entry entry = this.fBuckets[i]; entry != null; entry = entry.next) {
      if (j == entry.characters.length) {
        byte b = 0;
        while (true) {
          if (b < j) {
            if (paramString.charAt(b) != entry.characters[b])
              break; 
            b++;
            continue;
          } 
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public boolean containsSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = hash(paramArrayOfChar, paramInt1, paramInt2) % this.fTableSize;
    for (Entry entry = this.fBuckets[i]; entry != null; entry = entry.next) {
      if (paramInt2 == entry.characters.length) {
        int j = 0;
        while (true) {
          if (j < paramInt2) {
            if (paramArrayOfChar[paramInt1 + j] != entry.characters[j])
              break; 
            j++;
            continue;
          } 
          return true;
        } 
      } 
    } 
    return false;
  }
  
  protected static final class Entry {
    public final String symbol;
    
    public final char[] characters;
    
    public Entry next;
    
    public Entry(String param1String, Entry param1Entry) {
      this.symbol = param1String.intern();
      this.characters = new char[param1String.length()];
      param1String.getChars(0, this.characters.length, this.characters, 0);
      this.next = param1Entry;
    }
    
    public Entry(char[] param1ArrayOfChar, int param1Int1, int param1Int2, Entry param1Entry) {
      this.characters = new char[param1Int2];
      System.arraycopy(param1ArrayOfChar, param1Int1, this.characters, 0, param1Int2);
      this.symbol = (new String(this.characters)).intern();
      this.next = param1Entry;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\SymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */