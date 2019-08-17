package com.sun.org.apache.xml.internal.utils;

public final class BoolStack implements Cloneable {
  private boolean[] m_values;
  
  private int m_allocatedSize;
  
  private int m_index;
  
  public BoolStack() { this(32); }
  
  public BoolStack(int paramInt) {
    this.m_allocatedSize = paramInt;
    this.m_values = new boolean[paramInt];
    this.m_index = -1;
  }
  
  public final int size() { return this.m_index + 1; }
  
  public final void clear() { this.m_index = -1; }
  
  public final boolean push(boolean paramBoolean) {
    if (this.m_index == this.m_allocatedSize - 1)
      grow(); 
    this.m_values[++this.m_index] = paramBoolean;
    return paramBoolean;
  }
  
  public final boolean pop() { return this.m_values[this.m_index--]; }
  
  public final boolean popAndTop() {
    this.m_index--;
    return (this.m_index >= 0) ? this.m_values[this.m_index] : false;
  }
  
  public final void setTop(boolean paramBoolean) { this.m_values[this.m_index] = paramBoolean; }
  
  public final boolean peek() { return this.m_values[this.m_index]; }
  
  public final boolean peekOrFalse() { return (this.m_index > -1) ? this.m_values[this.m_index] : false; }
  
  public final boolean peekOrTrue() { return (this.m_index > -1) ? this.m_values[this.m_index] : true; }
  
  public boolean isEmpty() { return (this.m_index == -1); }
  
  private void grow() {
    this.m_allocatedSize *= 2;
    boolean[] arrayOfBoolean = new boolean[this.m_allocatedSize];
    System.arraycopy(this.m_values, 0, arrayOfBoolean, 0, this.m_index + 1);
    this.m_values = arrayOfBoolean;
  }
  
  public Object clone() throws CloneNotSupportedException { return super.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\BoolStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */