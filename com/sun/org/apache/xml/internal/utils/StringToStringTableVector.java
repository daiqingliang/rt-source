package com.sun.org.apache.xml.internal.utils;

public class StringToStringTableVector {
  private int m_blocksize = 8;
  
  private StringToStringTable[] m_map = new StringToStringTable[this.m_blocksize];
  
  private int m_firstFree = 0;
  
  private int m_mapSize = this.m_blocksize;
  
  public StringToStringTableVector() {}
  
  public StringToStringTableVector(int paramInt) {}
  
  public final int getLength() { return this.m_firstFree; }
  
  public final int size() { return this.m_firstFree; }
  
  public final void addElement(StringToStringTable paramStringToStringTable) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      StringToStringTable[] arrayOfStringToStringTable = new StringToStringTable[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfStringToStringTable, 0, this.m_firstFree + 1);
      this.m_map = arrayOfStringToStringTable;
    } 
    this.m_map[this.m_firstFree] = paramStringToStringTable;
    this.m_firstFree++;
  }
  
  public final String get(String paramString) {
    for (int i = this.m_firstFree - 1; i >= 0; i--) {
      String str = this.m_map[i].get(paramString);
      if (str != null)
        return str; 
    } 
    return null;
  }
  
  public final boolean containsKey(String paramString) {
    for (int i = this.m_firstFree - 1; i >= 0; i--) {
      if (this.m_map[i].get(paramString) != null)
        return true; 
    } 
    return false;
  }
  
  public final void removeLastElem() {
    if (this.m_firstFree > 0) {
      this.m_map[this.m_firstFree] = null;
      this.m_firstFree--;
    } 
  }
  
  public final StringToStringTable elementAt(int paramInt) { return this.m_map[paramInt]; }
  
  public final boolean contains(StringToStringTable paramStringToStringTable) {
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b].equals(paramStringToStringTable))
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\StringToStringTableVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */