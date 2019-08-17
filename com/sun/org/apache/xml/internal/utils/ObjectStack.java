package com.sun.org.apache.xml.internal.utils;

import java.util.EmptyStackException;

public class ObjectStack extends ObjectVector {
  public ObjectStack() {}
  
  public ObjectStack(int paramInt) { super(paramInt); }
  
  public ObjectStack(ObjectStack paramObjectStack) { super(paramObjectStack); }
  
  public Object push(Object paramObject) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      Object[] arrayOfObject = new Object[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfObject, 0, this.m_firstFree + 1);
      this.m_map = arrayOfObject;
    } 
    this.m_map[this.m_firstFree] = paramObject;
    this.m_firstFree++;
    return paramObject;
  }
  
  public Object pop() {
    Object object = this.m_map[--this.m_firstFree];
    this.m_map[this.m_firstFree] = null;
    return object;
  }
  
  public void quickPop(int paramInt) { this.m_firstFree -= paramInt; }
  
  public Object peek() {
    try {
      return this.m_map[this.m_firstFree - 1];
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new EmptyStackException();
    } 
  }
  
  public Object peek(int paramInt) {
    try {
      return this.m_map[this.m_firstFree - 1 + paramInt];
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new EmptyStackException();
    } 
  }
  
  public void setTop(Object paramObject) {
    try {
      this.m_map[this.m_firstFree - 1] = paramObject;
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new EmptyStackException();
    } 
  }
  
  public boolean empty() { return (this.m_firstFree == 0); }
  
  public int search(Object paramObject) {
    int i = lastIndexOf(paramObject);
    return (i >= 0) ? (size() - i) : -1;
  }
  
  public Object clone() { return (ObjectStack)super.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\ObjectStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */