package com.sun.org.apache.xml.internal.utils;

public class StringToStringTable {
  private int m_blocksize = 16;
  
  private String[] m_map = new String[this.m_blocksize];
  
  private int m_firstFree = 0;
  
  private int m_mapSize = this.m_blocksize;
  
  public StringToStringTable() {}
  
  public StringToStringTable(int paramInt) {}
  
  public final int getLength() { return this.m_firstFree; }
  
  public final void put(String paramString1, String paramString2) {
    if (this.m_firstFree + 2 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      String[] arrayOfString = new String[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfString, 0, this.m_firstFree + 1);
      this.m_map = arrayOfString;
    } 
    this.m_map[this.m_firstFree] = paramString1;
    this.m_firstFree++;
    this.m_map[this.m_firstFree] = paramString2;
    this.m_firstFree++;
  }
  
  public final String get(String paramString) {
    for (boolean bool = false; bool < this.m_firstFree; bool += true) {
      if (this.m_map[bool].equals(paramString))
        return this.m_map[bool + true]; 
    } 
    return null;
  }
  
  public final void remove(String paramString) {
    for (byte b = 0; b < this.m_firstFree; b += 2) {
      if (this.m_map[b].equals(paramString)) {
        if (b + 2 < this.m_firstFree)
          System.arraycopy(this.m_map, b + 2, this.m_map, b, this.m_firstFree - b + 2); 
        this.m_firstFree -= 2;
        this.m_map[this.m_firstFree] = null;
        this.m_map[this.m_firstFree + 1] = null;
        break;
      } 
    } 
  }
  
  public final String getIgnoreCase(String paramString) {
    if (null == paramString)
      return null; 
    for (boolean bool = false; bool < this.m_firstFree; bool += true) {
      if (this.m_map[bool].equalsIgnoreCase(paramString))
        return this.m_map[bool + true]; 
    } 
    return null;
  }
  
  public final String getByValue(String paramString) {
    for (boolean bool = true; bool < this.m_firstFree; bool += true) {
      if (this.m_map[bool].equals(paramString))
        return this.m_map[bool - true]; 
    } 
    return null;
  }
  
  public final String elementAt(int paramInt) { return this.m_map[paramInt]; }
  
  public final boolean contains(String paramString) {
    for (boolean bool = false; bool < this.m_firstFree; bool += true) {
      if (this.m_map[bool].equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public final boolean containsValue(String paramString) {
    for (boolean bool = true; bool < this.m_firstFree; bool += true) {
      if (this.m_map[bool].equals(paramString))
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\StringToStringTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */