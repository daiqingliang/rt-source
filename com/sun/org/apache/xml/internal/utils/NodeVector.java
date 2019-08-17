package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;

public class NodeVector implements Serializable, Cloneable {
  static final long serialVersionUID = -713473092200731870L;
  
  private int m_blocksize = 32;
  
  private int[] m_map;
  
  protected int m_firstFree = 0;
  
  private int m_mapSize = 0;
  
  public NodeVector() {}
  
  public NodeVector(int paramInt) {}
  
  public Object clone() throws CloneNotSupportedException {
    NodeVector nodeVector = (NodeVector)super.clone();
    if (null != this.m_map && this.m_map == nodeVector.m_map) {
      nodeVector.m_map = new int[this.m_map.length];
      System.arraycopy(this.m_map, 0, nodeVector.m_map, 0, this.m_map.length);
    } 
    return nodeVector;
  }
  
  public int size() { return this.m_firstFree; }
  
  public void addElement(int paramInt) {
    if (this.m_firstFree + 1 >= this.m_mapSize)
      if (null == this.m_map) {
        this.m_map = new int[this.m_blocksize];
        this.m_mapSize = this.m_blocksize;
      } else {
        this.m_mapSize += this.m_blocksize;
        int[] arrayOfInt = new int[this.m_mapSize];
        System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_firstFree + 1);
        this.m_map = arrayOfInt;
      }  
    this.m_map[this.m_firstFree] = paramInt;
    this.m_firstFree++;
  }
  
  public final void push(int paramInt) {
    int i = this.m_firstFree;
    if (i + 1 >= this.m_mapSize)
      if (null == this.m_map) {
        this.m_map = new int[this.m_blocksize];
        this.m_mapSize = this.m_blocksize;
      } else {
        this.m_mapSize += this.m_blocksize;
        int[] arrayOfInt = new int[this.m_mapSize];
        System.arraycopy(this.m_map, 0, arrayOfInt, 0, i + 1);
        this.m_map = arrayOfInt;
      }  
    this.m_map[i] = paramInt;
    this.m_firstFree = ++i;
  }
  
  public final int pop() {
    this.m_firstFree--;
    int i = this.m_map[this.m_firstFree];
    this.m_map[this.m_firstFree] = -1;
    return i;
  }
  
  public final int popAndTop() {
    this.m_firstFree--;
    this.m_map[this.m_firstFree] = -1;
    return (this.m_firstFree == 0) ? -1 : this.m_map[this.m_firstFree - 1];
  }
  
  public final void popQuick() {
    this.m_firstFree--;
    this.m_map[this.m_firstFree] = -1;
  }
  
  public final int peepOrNull() { return (null != this.m_map && this.m_firstFree > 0) ? this.m_map[this.m_firstFree - 1] : -1; }
  
  public final void pushPair(int paramInt1, int paramInt2) {
    if (null == this.m_map) {
      this.m_map = new int[this.m_blocksize];
      this.m_mapSize = this.m_blocksize;
    } else if (this.m_firstFree + 2 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_firstFree);
      this.m_map = arrayOfInt;
    } 
    this.m_map[this.m_firstFree] = paramInt1;
    this.m_map[this.m_firstFree + 1] = paramInt2;
    this.m_firstFree += 2;
  }
  
  public final void popPair() {
    this.m_firstFree -= 2;
    this.m_map[this.m_firstFree] = -1;
    this.m_map[this.m_firstFree + 1] = -1;
  }
  
  public final void setTail(int paramInt) { this.m_map[this.m_firstFree - 1] = paramInt; }
  
  public final void setTailSub1(int paramInt) { this.m_map[this.m_firstFree - 2] = paramInt; }
  
  public final int peepTail() { return this.m_map[this.m_firstFree - 1]; }
  
  public final int peepTailSub1() { return this.m_map[this.m_firstFree - 2]; }
  
  public void insertInOrder(int paramInt) {
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (paramInt < this.m_map[b]) {
        insertElementAt(paramInt, b);
        return;
      } 
    } 
    addElement(paramInt);
  }
  
  public void insertElementAt(int paramInt1, int paramInt2) {
    if (null == this.m_map) {
      this.m_map = new int[this.m_blocksize];
      this.m_mapSize = this.m_blocksize;
    } else if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_firstFree + 1);
      this.m_map = arrayOfInt;
    } 
    if (paramInt2 <= this.m_firstFree - 1)
      System.arraycopy(this.m_map, paramInt2, this.m_map, paramInt2 + 1, this.m_firstFree - paramInt2); 
    this.m_map[paramInt2] = paramInt1;
    this.m_firstFree++;
  }
  
  public void appendNodes(NodeVector paramNodeVector) {
    int i = paramNodeVector.size();
    if (null == this.m_map) {
      this.m_mapSize = i + this.m_blocksize;
      this.m_map = new int[this.m_mapSize];
    } else if (this.m_firstFree + i >= this.m_mapSize) {
      this.m_mapSize += i + this.m_blocksize;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_firstFree + i);
      this.m_map = arrayOfInt;
    } 
    System.arraycopy(paramNodeVector.m_map, 0, this.m_map, this.m_firstFree, i);
    this.m_firstFree += i;
  }
  
  public void removeAllElements() {
    if (null == this.m_map)
      return; 
    for (byte b = 0; b < this.m_firstFree; b++)
      this.m_map[b] = -1; 
    this.m_firstFree = 0;
  }
  
  public void RemoveAllNoClear() {
    if (null == this.m_map)
      return; 
    this.m_firstFree = 0;
  }
  
  public boolean removeElement(int paramInt) {
    if (null == this.m_map)
      return false; 
    for (int i = 0; i < this.m_firstFree; i++) {
      int j = this.m_map[i];
      if (j == paramInt) {
        if (i > this.m_firstFree) {
          System.arraycopy(this.m_map, i + true, this.m_map, i - true, this.m_firstFree - i);
        } else {
          this.m_map[i] = -1;
        } 
        this.m_firstFree--;
        return true;
      } 
    } 
    return false;
  }
  
  public void removeElementAt(int paramInt) {
    if (null == this.m_map)
      return; 
    if (paramInt > this.m_firstFree) {
      System.arraycopy(this.m_map, paramInt + 1, this.m_map, paramInt - 1, this.m_firstFree - paramInt);
    } else {
      this.m_map[paramInt] = -1;
    } 
  }
  
  public void setElementAt(int paramInt1, int paramInt2) {
    if (null == this.m_map) {
      this.m_map = new int[this.m_blocksize];
      this.m_mapSize = this.m_blocksize;
    } 
    if (paramInt2 == -1)
      addElement(paramInt1); 
    this.m_map[paramInt2] = paramInt1;
  }
  
  public int elementAt(int paramInt) { return (null == this.m_map) ? -1 : this.m_map[paramInt]; }
  
  public boolean contains(int paramInt) {
    if (null == this.m_map)
      return false; 
    for (byte b = 0; b < this.m_firstFree; b++) {
      int i = this.m_map[b];
      if (i == paramInt)
        return true; 
    } 
    return false;
  }
  
  public int indexOf(int paramInt1, int paramInt2) {
    if (null == this.m_map)
      return -1; 
    for (int i = paramInt2; i < this.m_firstFree; i++) {
      int j = this.m_map[i];
      if (j == paramInt1)
        return i; 
    } 
    return -1;
  }
  
  public int indexOf(int paramInt) {
    if (null == this.m_map)
      return -1; 
    for (byte b = 0; b < this.m_firstFree; b++) {
      int i = this.m_map[b];
      if (i == paramInt)
        return b; 
    } 
    return -1;
  }
  
  public void sort(int[] paramArrayOfInt, int paramInt1, int paramInt2) throws Exception {
    int i = paramInt1;
    int j = paramInt2;
    if (i >= j)
      return; 
    if (i == j - 1) {
      if (paramArrayOfInt[i] > paramArrayOfInt[j]) {
        int m = paramArrayOfInt[i];
        paramArrayOfInt[i] = paramArrayOfInt[j];
        paramArrayOfInt[j] = m;
      } 
      return;
    } 
    int k = paramArrayOfInt[(i + j) / 2];
    paramArrayOfInt[(i + j) / 2] = paramArrayOfInt[j];
    paramArrayOfInt[j] = k;
    while (i < j) {
      while (paramArrayOfInt[i] <= k && i < j)
        i++; 
      while (k <= paramArrayOfInt[j] && i < j)
        j--; 
      if (i < j) {
        int m = paramArrayOfInt[i];
        paramArrayOfInt[i] = paramArrayOfInt[j];
        paramArrayOfInt[j] = m;
      } 
    } 
    paramArrayOfInt[paramInt2] = paramArrayOfInt[j];
    paramArrayOfInt[j] = k;
    sort(paramArrayOfInt, paramInt1, i - 1);
    sort(paramArrayOfInt, j + 1, paramInt2);
  }
  
  public void sort() { sort(this.m_map, 0, this.m_firstFree - 1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\NodeVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */