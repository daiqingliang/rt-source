package com.sun.java_cup.internal.runtime;

public class Symbol {
  public int sym;
  
  public int parse_state;
  
  boolean used_by_parser = false;
  
  public int left;
  
  public int right;
  
  public Object value;
  
  public Symbol(int paramInt1, int paramInt2, int paramInt3, Object paramObject) {
    this(paramInt1);
    this.left = paramInt2;
    this.right = paramInt3;
    this.value = paramObject;
  }
  
  public Symbol(int paramInt, Object paramObject) {
    this(paramInt);
    this.left = -1;
    this.right = -1;
    this.value = paramObject;
  }
  
  public Symbol(int paramInt1, int paramInt2, int paramInt3) {
    this.sym = paramInt1;
    this.left = paramInt2;
    this.right = paramInt3;
    this.value = null;
  }
  
  public Symbol(int paramInt) {
    this(paramInt, -1);
    this.left = -1;
    this.right = -1;
    this.value = null;
  }
  
  public Symbol(int paramInt1, int paramInt2) {
    this.sym = paramInt1;
    this.parse_state = paramInt2;
  }
  
  public String toString() { return "#" + this.sym; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java_cup\internal\runtime\Symbol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */