package com.sun.jmx.snmp.agent;

final class LongList {
  public static int DEFAULT_CAPACITY = 10;
  
  public static int DEFAULT_INCREMENT = 10;
  
  private final int DELTA;
  
  private int size = 0;
  
  public long[] list;
  
  LongList() { this(DEFAULT_CAPACITY, DEFAULT_INCREMENT); }
  
  LongList(int paramInt) { this(paramInt, DEFAULT_INCREMENT); }
  
  LongList(int paramInt1, int paramInt2) {
    this.DELTA = paramInt2;
    this.list = allocate(paramInt1);
  }
  
  public final int size() { return this.size; }
  
  public final boolean add(long paramLong) {
    if (this.size >= this.list.length)
      resize(); 
    this.list[this.size++] = paramLong;
    return true;
  }
  
  public final void add(int paramInt, long paramLong) {
    if (paramInt > this.size)
      throw new IndexOutOfBoundsException(); 
    if (paramInt >= this.list.length)
      resize(); 
    if (paramInt == this.size) {
      this.list[this.size++] = paramLong;
      return;
    } 
    System.arraycopy(this.list, paramInt, this.list, paramInt + 1, this.size - paramInt);
    this.list[paramInt] = paramLong;
    this.size++;
  }
  
  public final void add(int paramInt1, long[] paramArrayOfLong, int paramInt2, int paramInt3) {
    if (paramInt3 <= 0)
      return; 
    if (paramInt1 > this.size)
      throw new IndexOutOfBoundsException(); 
    ensure(this.size + paramInt3);
    if (paramInt1 < this.size)
      System.arraycopy(this.list, paramInt1, this.list, paramInt1 + paramInt3, this.size - paramInt1); 
    System.arraycopy(paramArrayOfLong, paramInt2, this.list, paramInt1, paramInt3);
    this.size += paramInt3;
  }
  
  public final long remove(int paramInt1, int paramInt2) {
    if (paramInt2 < 1 || paramInt1 < 0)
      return -1L; 
    if (paramInt1 + paramInt2 > this.size)
      return -1L; 
    long l = this.list[paramInt1];
    int i = this.size;
    this.size -= paramInt2;
    if (paramInt1 == this.size)
      return l; 
    System.arraycopy(this.list, paramInt1 + paramInt2, this.list, paramInt1, this.size - paramInt1);
    return l;
  }
  
  public final long remove(int paramInt) {
    if (paramInt >= this.size)
      return -1L; 
    long l = this.list[paramInt];
    this.list[paramInt] = 0L;
    if (paramInt == --this.size)
      return l; 
    System.arraycopy(this.list, paramInt + 1, this.list, paramInt, this.size - paramInt);
    return l;
  }
  
  public final long[] toArray(long[] paramArrayOfLong) {
    System.arraycopy(this.list, 0, paramArrayOfLong, 0, this.size);
    return paramArrayOfLong;
  }
  
  public final long[] toArray() { return toArray(new long[this.size]); }
  
  private final void resize() {
    long[] arrayOfLong = allocate(this.list.length + this.DELTA);
    System.arraycopy(this.list, 0, arrayOfLong, 0, this.size);
    this.list = arrayOfLong;
  }
  
  private final void ensure(int paramInt) {
    if (this.list.length < paramInt) {
      int i = this.list.length + this.DELTA;
      paramInt = (paramInt < i) ? i : paramInt;
      long[] arrayOfLong = allocate(paramInt);
      System.arraycopy(this.list, 0, arrayOfLong, 0, this.size);
      this.list = arrayOfLong;
    } 
  }
  
  private final long[] allocate(int paramInt) { return new long[paramInt]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\LongList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */