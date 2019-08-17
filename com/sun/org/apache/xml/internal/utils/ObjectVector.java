package com.sun.org.apache.xml.internal.utils;

public class ObjectVector implements Cloneable {
  protected int m_blocksize;
  
  protected Object[] m_map;
  
  protected int m_firstFree = 0;
  
  protected int m_mapSize;
  
  public ObjectVector() {
    this.m_blocksize = 32;
    this.m_mapSize = this.m_blocksize;
    this.m_map = new Object[this.m_blocksize];
  }
  
  public ObjectVector(int paramInt) {
    this.m_blocksize = paramInt;
    this.m_mapSize = paramInt;
    this.m_map = new Object[paramInt];
  }
  
  public ObjectVector(int paramInt1, int paramInt2) {
    this.m_blocksize = paramInt2;
    this.m_mapSize = paramInt1;
    this.m_map = new Object[paramInt1];
  }
  
  public ObjectVector(ObjectVector paramObjectVector) {
    this.m_map = new Object[paramObjectVector.m_mapSize];
    this.m_mapSize = paramObjectVector.m_mapSize;
    this.m_firstFree = paramObjectVector.m_firstFree;
    this.m_blocksize = paramObjectVector.m_blocksize;
    System.arraycopy(paramObjectVector.m_map, 0, this.m_map, 0, this.m_firstFree);
  }
  
  public final int size() { return this.m_firstFree; }
  
  public final void setSize(int paramInt) { this.m_firstFree = paramInt; }
  
  public final void addElement(Object paramObject) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      Object[] arrayOfObject = new Object[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfObject, 0, this.m_firstFree + 1);
      this.m_map = arrayOfObject;
    } 
    this.m_map[this.m_firstFree] = paramObject;
    this.m_firstFree++;
  }
  
  public final void addElements(Object paramObject, int paramInt) {
    if (this.m_firstFree + paramInt >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize + paramInt;
      Object[] arrayOfObject = new Object[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfObject, 0, this.m_firstFree + 1);
      this.m_map = arrayOfObject;
    } 
    for (byte b = 0; b < paramInt; b++) {
      this.m_map[this.m_firstFree] = paramObject;
      this.m_firstFree++;
    } 
  }
  
  public final void addElements(int paramInt) {
    if (this.m_firstFree + paramInt >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize + paramInt;
      Object[] arrayOfObject = new Object[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfObject, 0, this.m_firstFree + 1);
      this.m_map = arrayOfObject;
    } 
    this.m_firstFree += paramInt;
  }
  
  public final void insertElementAt(Object paramObject, int paramInt) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      Object[] arrayOfObject = new Object[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfObject, 0, this.m_firstFree + 1);
      this.m_map = arrayOfObject;
    } 
    if (paramInt <= this.m_firstFree - 1)
      System.arraycopy(this.m_map, paramInt, this.m_map, paramInt + 1, this.m_firstFree - paramInt); 
    this.m_map[paramInt] = paramObject;
    this.m_firstFree++;
  }
  
  public final void removeAllElements() {
    for (byte b = 0; b < this.m_firstFree; b++)
      this.m_map[b] = null; 
    this.m_firstFree = 0;
  }
  
  public final boolean removeElement(Object paramObject) {
    for (int i = 0; i < this.m_firstFree; i++) {
      if (this.m_map[i] == paramObject) {
        if (i + true < this.m_firstFree) {
          System.arraycopy(this.m_map, i + true, this.m_map, i - true, this.m_firstFree - i);
        } else {
          this.m_map[i] = null;
        } 
        this.m_firstFree--;
        return true;
      } 
    } 
    return false;
  }
  
  public final void removeElementAt(int paramInt) {
    if (paramInt > this.m_firstFree) {
      System.arraycopy(this.m_map, paramInt + 1, this.m_map, paramInt, this.m_firstFree);
    } else {
      this.m_map[paramInt] = null;
    } 
    this.m_firstFree--;
  }
  
  public final void setElementAt(Object paramObject, int paramInt) { this.m_map[paramInt] = paramObject; }
  
  public final Object elementAt(int paramInt) { return this.m_map[paramInt]; }
  
  public final boolean contains(Object paramObject) {
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b] == paramObject)
        return true; 
    } 
    return false;
  }
  
  public final int indexOf(Object paramObject, int paramInt) {
    for (int i = paramInt; i < this.m_firstFree; i++) {
      if (this.m_map[i] == paramObject)
        return i; 
    } 
    return Integer.MIN_VALUE;
  }
  
  public final int indexOf(Object paramObject) {
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b] == paramObject)
        return b; 
    } 
    return Integer.MIN_VALUE;
  }
  
  public final int lastIndexOf(Object paramObject) {
    for (int i = this.m_firstFree - 1; i >= 0; i--) {
      if (this.m_map[i] == paramObject)
        return i; 
    } 
    return Integer.MIN_VALUE;
  }
  
  public final void setToSize(int paramInt) {
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(this.m_map, 0, arrayOfObject, 0, this.m_firstFree);
    this.m_mapSize = paramInt;
    this.m_map = arrayOfObject;
  }
  
  public Object clone() throws CloneNotSupportedException { return new ObjectVector(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\ObjectVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */