package com.sun.jmx.remote.internal;

import java.util.AbstractList;

public class ArrayQueue<T> extends AbstractList<T> {
  private int capacity;
  
  private T[] queue;
  
  private int head;
  
  private int tail;
  
  public ArrayQueue(int paramInt) {
    this.capacity = paramInt + 1;
    this.queue = newArray(paramInt + 1);
    this.head = 0;
    this.tail = 0;
  }
  
  public void resize(int paramInt) {
    int i = size();
    if (paramInt < i)
      throw new IndexOutOfBoundsException("Resizing would lose data"); 
    if (++paramInt == this.capacity)
      return; 
    Object[] arrayOfObject = newArray(paramInt);
    for (byte b = 0; b < i; b++)
      arrayOfObject[b] = get(b); 
    this.capacity = paramInt;
    this.queue = arrayOfObject;
    this.head = 0;
    this.tail = i;
  }
  
  private T[] newArray(int paramInt) { return (T[])(Object[])new Object[paramInt]; }
  
  public boolean add(T paramT) {
    this.queue[this.tail] = paramT;
    int i = (this.tail + 1) % this.capacity;
    if (i == this.head)
      throw new IndexOutOfBoundsException("Queue full"); 
    this.tail = i;
    return true;
  }
  
  public T remove(int paramInt) {
    if (paramInt != 0)
      throw new IllegalArgumentException("Can only remove head of queue"); 
    if (this.head == this.tail)
      throw new IndexOutOfBoundsException("Queue empty"); 
    Object object = this.queue[this.head];
    this.queue[this.head] = null;
    this.head = (this.head + 1) % this.capacity;
    return (T)object;
  }
  
  public T get(int paramInt) {
    int i = size();
    if (paramInt < 0 || paramInt >= i) {
      String str = "Index " + paramInt + ", queue size " + i;
      throw new IndexOutOfBoundsException(str);
    } 
    int j = (this.head + paramInt) % this.capacity;
    return (T)this.queue[j];
  }
  
  public int size() {
    int i = this.tail - this.head;
    if (i < 0)
      i += this.capacity; 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\ArrayQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */