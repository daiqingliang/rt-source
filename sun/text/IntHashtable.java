package sun.text;

public final class IntHashtable {
  private int defaultValue = 0;
  
  private int primeIndex;
  
  private static final float HIGH_WATER_FACTOR = 0.4F;
  
  private int highWaterMark;
  
  private static final float LOW_WATER_FACTOR = 0.0F;
  
  private int lowWaterMark;
  
  private int count;
  
  private int[] values;
  
  private int[] keyList;
  
  private static final int EMPTY = -2147483648;
  
  private static final int DELETED = -2147483647;
  
  private static final int MAX_UNUSED = -2147483647;
  
  private static final int[] PRIMES = { 
      17, 37, 67, 131, 257, 521, 1031, 2053, 4099, 8209, 
      16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319, 8388617, 
      16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827, Integer.MAX_VALUE };
  
  public IntHashtable() { initialize(3); }
  
  public IntHashtable(int paramInt) { initialize(leastGreaterPrimeIndex((int)(paramInt / 0.4F))); }
  
  public int size() { return this.count; }
  
  public boolean isEmpty() { return (this.count == 0); }
  
  public void put(int paramInt1, int paramInt2) {
    if (this.count > this.highWaterMark)
      rehash(); 
    int i = find(paramInt1);
    if (this.keyList[i] <= -2147483647) {
      this.keyList[i] = paramInt1;
      this.count++;
    } 
    this.values[i] = paramInt2;
  }
  
  public int get(int paramInt) { return this.values[find(paramInt)]; }
  
  public void remove(int paramInt) {
    int i = find(paramInt);
    if (this.keyList[i] > -2147483647) {
      this.keyList[i] = -2147483647;
      this.values[i] = this.defaultValue;
      this.count--;
      if (this.count < this.lowWaterMark)
        rehash(); 
    } 
  }
  
  public int getDefaultValue() { return this.defaultValue; }
  
  public void setDefaultValue(int paramInt) {
    this.defaultValue = paramInt;
    rehash();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject.getClass() != getClass())
      return false; 
    IntHashtable intHashtable = (IntHashtable)paramObject;
    if (intHashtable.size() != this.count || intHashtable.defaultValue != this.defaultValue)
      return false; 
    for (byte b = 0; b < this.keyList.length; b++) {
      int i = this.keyList[b];
      if (i > -2147483647 && intHashtable.get(i) != this.values[b])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = 465;
    int j = 1362796821;
    byte b;
    for (b = 0; b < this.keyList.length; b++) {
      i = i * j + 1;
      i += this.keyList[b];
    } 
    for (b = 0; b < this.values.length; b++) {
      i = i * j + 1;
      i += this.values[b];
    } 
    return i;
  }
  
  public Object clone() throws CloneNotSupportedException {
    IntHashtable intHashtable = (IntHashtable)super.clone();
    this.values = (int[])this.values.clone();
    this.keyList = (int[])this.keyList.clone();
    return intHashtable;
  }
  
  private void initialize(int paramInt) {
    if (paramInt < 0) {
      paramInt = 0;
    } else if (paramInt >= PRIMES.length) {
      System.out.println("TOO BIG");
      paramInt = PRIMES.length - 1;
    } 
    this.primeIndex = paramInt;
    int i = PRIMES[paramInt];
    this.values = new int[i];
    this.keyList = new int[i];
    for (byte b = 0; b < i; b++) {
      this.keyList[b] = Integer.MIN_VALUE;
      this.values[b] = this.defaultValue;
    } 
    this.count = 0;
    this.lowWaterMark = (int)(i * 0.0F);
    this.highWaterMark = (int)(i * 0.4F);
  }
  
  private void rehash() {
    int[] arrayOfInt1 = this.values;
    int[] arrayOfInt2 = this.keyList;
    int i = this.primeIndex;
    if (this.count > this.highWaterMark) {
      i++;
    } else if (this.count < this.lowWaterMark) {
      i -= 2;
    } 
    initialize(i);
    for (int j = arrayOfInt1.length - 1; j >= 0; j--) {
      int k = arrayOfInt2[j];
      if (k > -2147483647)
        putInternal(k, arrayOfInt1[j]); 
    } 
  }
  
  public void putInternal(int paramInt1, int paramInt2) {
    int i = find(paramInt1);
    if (this.keyList[i] < -2147483647) {
      this.keyList[i] = paramInt1;
      this.count++;
    } 
    this.values[i] = paramInt2;
  }
  
  private int find(int paramInt) {
    if (paramInt <= -2147483647)
      throw new IllegalArgumentException("key can't be less than 0xFFFFFFFE"); 
    int i = -1;
    int j = (paramInt ^ 0x4000000) % this.keyList.length;
    if (j < 0)
      j = -j; 
    int k = 0;
    do {
      int m = this.keyList[j];
      if (m == paramInt)
        return j; 
      if (m <= -2147483647) {
        if (m == Integer.MIN_VALUE) {
          if (i >= 0)
            j = i; 
          return j;
        } 
        if (i < 0)
          i = j; 
      } 
      if (!k) {
        k = paramInt % (this.keyList.length - 1);
        if (k < 0)
          k = -k; 
        k++;
      } 
      j = (j + k) % this.keyList.length;
    } while (j != i);
    return j;
  }
  
  private static int leastGreaterPrimeIndex(int paramInt) {
    byte b;
    for (b = 0; b < PRIMES.length && paramInt >= PRIMES[b]; b++);
    return (b == 0) ? 0 : (b - 1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\IntHashtable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */