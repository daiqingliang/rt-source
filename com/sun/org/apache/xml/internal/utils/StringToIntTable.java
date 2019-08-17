package com.sun.org.apache.xml.internal.utils;

public class StringToIntTable {
  public static final int INVALID_KEY = -10000;
  
  private int m_blocksize = 8;
  
  private String[] m_map = new String[this.m_blocksize];
  
  private int[] m_values = new int[this.m_blocksize];
  
  private int m_firstFree = 0;
  
  private int m_mapSize = this.m_blocksize;
  
  public StringToIntTable() {}
  
  public StringToIntTable(int paramInt) {}
  
  public final int getLength() { return this.m_firstFree; }
  
  public final void put(String paramString, int paramInt) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      String[] arrayOfString = new String[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfString, 0, this.m_firstFree + 1);
      this.m_map = arrayOfString;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_values, 0, arrayOfInt, 0, this.m_firstFree + 1);
      this.m_values = arrayOfInt;
    } 
    this.m_map[this.m_firstFree] = paramString;
    this.m_values[this.m_firstFree] = paramInt;
    this.m_firstFree++;
  }
  
  public final int get(String paramString) {
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b].equals(paramString))
        return this.m_values[b]; 
    } 
    return -10000;
  }
  
  public final int getIgnoreCase(String paramString) {
    if (null == paramString)
      return -10000; 
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b].equalsIgnoreCase(paramString))
        return this.m_values[b]; 
    } 
    return -10000;
  }
  
  public final boolean contains(String paramString) {
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b].equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public final String[] keys() {
    String[] arrayOfString = new String[this.m_firstFree];
    for (byte b = 0; b < this.m_firstFree; b++)
      arrayOfString[b] = this.m_map[b]; 
    return arrayOfString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\StringToIntTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */