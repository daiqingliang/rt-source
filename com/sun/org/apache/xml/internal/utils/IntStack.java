package com.sun.org.apache.xml.internal.utils;

import java.util.EmptyStackException;

public class IntStack extends IntVector {
  public IntStack() {}
  
  public IntStack(int paramInt) { super(paramInt); }
  
  public IntStack(IntStack paramIntStack) { super(paramIntStack); }
  
  public int push(int paramInt) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_firstFree + 1);
      this.m_map = arrayOfInt;
    } 
    this.m_map[this.m_firstFree] = paramInt;
    this.m_firstFree++;
    return paramInt;
  }
  
  public final int pop() { return this.m_map[--this.m_firstFree]; }
  
  public final void quickPop(int paramInt) { this.m_firstFree -= paramInt; }
  
  public final int peek() {
    try {
      return this.m_map[this.m_firstFree - 1];
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new EmptyStackException();
    } 
  }
  
  public int peek(int paramInt) {
    try {
      return this.m_map[this.m_firstFree - 1 + paramInt];
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new EmptyStackException();
    } 
  }
  
  public void setTop(int paramInt) {
    try {
      this.m_map[this.m_firstFree - 1] = paramInt;
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new EmptyStackException();
    } 
  }
  
  public boolean empty() { return (this.m_firstFree == 0); }
  
  public int search(int paramInt) {
    int i = lastIndexOf(paramInt);
    return (i >= 0) ? (size() - i) : -1;
  }
  
  public Object clone() throws CloneNotSupportedException { return (IntStack)super.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\IntStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */