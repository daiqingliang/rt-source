package com.sun.org.apache.xerces.internal.util;

public final class IntStack {
  private int fDepth;
  
  private int[] fData;
  
  public int size() { return this.fDepth; }
  
  public void push(int paramInt) {
    ensureCapacity(this.fDepth + 1);
    this.fData[this.fDepth++] = paramInt;
  }
  
  public int peek() { return this.fData[this.fDepth - 1]; }
  
  public int elementAt(int paramInt) { return this.fData[paramInt]; }
  
  public int pop() { return this.fData[--this.fDepth]; }
  
  public void clear() { this.fDepth = 0; }
  
  public void print() {
    System.out.print('(');
    System.out.print(this.fDepth);
    System.out.print(") {");
    for (byte b = 0; b < this.fDepth; b++) {
      if (b == 3) {
        System.out.print(" ...");
        break;
      } 
      System.out.print(' ');
      System.out.print(this.fData[b]);
      if (b < this.fDepth - 1)
        System.out.print(','); 
    } 
    System.out.print(" }");
    System.out.println();
  }
  
  private void ensureCapacity(int paramInt) {
    if (this.fData == null) {
      this.fData = new int[32];
    } else if (this.fData.length <= paramInt) {
      int[] arrayOfInt = new int[this.fData.length * 2];
      System.arraycopy(this.fData, 0, arrayOfInt, 0, this.fData.length);
      this.fData = arrayOfInt;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\IntStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */