package com.sun.corba.se.impl.orbutil;

import java.util.EmptyStackException;

public class StackImpl {
  private Object[] data = new Object[3];
  
  private int top = -1;
  
  public final boolean empty() { return (this.top == -1); }
  
  public final Object peek() {
    if (empty())
      throw new EmptyStackException(); 
    return this.data[this.top];
  }
  
  public final Object pop() {
    Object object = peek();
    this.data[this.top] = null;
    this.top--;
    return object;
  }
  
  private void ensure() {
    if (this.top == this.data.length - 1) {
      int i = 2 * this.data.length;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(this.data, 0, arrayOfObject, 0, this.data.length);
      this.data = arrayOfObject;
    } 
  }
  
  public final Object push(Object paramObject) {
    ensure();
    this.top++;
    this.data[this.top] = paramObject;
    return paramObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\StackImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */