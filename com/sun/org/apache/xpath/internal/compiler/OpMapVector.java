package com.sun.org.apache.xpath.internal.compiler;

public class OpMapVector {
  protected int m_blocksize;
  
  protected int[] m_map;
  
  protected int m_lengthPos = 0;
  
  protected int m_mapSize;
  
  public OpMapVector(int paramInt1, int paramInt2, int paramInt3) {
    this.m_blocksize = paramInt2;
    this.m_mapSize = paramInt1;
    this.m_lengthPos = paramInt3;
    this.m_map = new int[paramInt1];
  }
  
  public final int elementAt(int paramInt) { return this.m_map[paramInt]; }
  
  public final void setElementAt(int paramInt1, int paramInt2) {
    if (paramInt2 >= this.m_mapSize) {
      int i = this.m_mapSize;
      this.m_mapSize += this.m_blocksize;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfInt, 0, i);
      this.m_map = arrayOfInt;
    } 
    this.m_map[paramInt2] = paramInt1;
  }
  
  public final void setToSize(int paramInt) {
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_map[this.m_lengthPos]);
    this.m_mapSize = paramInt;
    this.m_map = arrayOfInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\compiler\OpMapVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */