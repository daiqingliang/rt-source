package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;

public class StringVector implements Serializable {
  static final long serialVersionUID = 4995234972032919748L;
  
  protected int m_blocksize = 8;
  
  protected String[] m_map = new String[this.m_blocksize];
  
  protected int m_firstFree = 0;
  
  protected int m_mapSize = this.m_blocksize;
  
  public StringVector() {}
  
  public StringVector(int paramInt) {}
  
  public int getLength() { return this.m_firstFree; }
  
  public final int size() { return this.m_firstFree; }
  
  public final void addElement(String paramString) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      String[] arrayOfString = new String[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfString, 0, this.m_firstFree + 1);
      this.m_map = arrayOfString;
    } 
    this.m_map[this.m_firstFree] = paramString;
    this.m_firstFree++;
  }
  
  public final String elementAt(int paramInt) { return this.m_map[paramInt]; }
  
  public final boolean contains(String paramString) {
    if (null == paramString)
      return false; 
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b].equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public final boolean containsIgnoreCase(String paramString) {
    if (null == paramString)
      return false; 
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b].equalsIgnoreCase(paramString))
        return true; 
    } 
    return false;
  }
  
  public final void push(String paramString) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      String[] arrayOfString = new String[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfString, 0, this.m_firstFree + 1);
      this.m_map = arrayOfString;
    } 
    this.m_map[this.m_firstFree] = paramString;
    this.m_firstFree++;
  }
  
  public final String pop() {
    if (this.m_firstFree <= 0)
      return null; 
    this.m_firstFree--;
    String str = this.m_map[this.m_firstFree];
    this.m_map[this.m_firstFree] = null;
    return str;
  }
  
  public final String peek() { return (this.m_firstFree <= 0) ? null : this.m_map[this.m_firstFree - 1]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\StringVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */