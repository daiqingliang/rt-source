package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.Type;

final class SlotAllocator {
  private int _firstAvailableSlot;
  
  private int _size = 8;
  
  private int _free = 0;
  
  private int[] _slotsTaken = new int[this._size];
  
  public void initialize(LocalVariableGen[] paramArrayOfLocalVariableGen) {
    int i = paramArrayOfLocalVariableGen.length;
    int j = 0;
    for (byte b = 0; b < i; b++) {
      int k = paramArrayOfLocalVariableGen[b].getType().getSize();
      int m = paramArrayOfLocalVariableGen[b].getIndex();
      j = Math.max(j, m + k);
    } 
    this._firstAvailableSlot = j;
  }
  
  public int allocateSlot(Type paramType) {
    int i = paramType.getSize();
    int j = this._free;
    int k = this._firstAvailableSlot;
    byte b = 0;
    if (this._free + i > this._size) {
      int[] arrayOfInt = new int[this._size *= 2];
      for (byte b1 = 0; b1 < j; b1++)
        arrayOfInt[b1] = this._slotsTaken[b1]; 
      this._slotsTaken = arrayOfInt;
    } 
    while (b < j) {
      if (k + i <= this._slotsTaken[b]) {
        for (int n = j - 1; n >= b; n--)
          this._slotsTaken[n + i] = this._slotsTaken[n]; 
        break;
      } 
      k = this._slotsTaken[b++] + 1;
    } 
    for (int m = 0; m < i; m++)
      this._slotsTaken[b + m] = k + m; 
    this._free += i;
    return k;
  }
  
  public void releaseSlot(LocalVariableGen paramLocalVariableGen) {
    int i = paramLocalVariableGen.getType().getSize();
    int j = paramLocalVariableGen.getIndex();
    int k = this._free;
    for (int m = 0; m < k; m++) {
      if (this._slotsTaken[m] == j) {
        int n = m + i;
        while (n < k)
          this._slotsTaken[m++] = this._slotsTaken[n++]; 
        this._free -= i;
        return;
      } 
    } 
    String str = "Variable slot allocation error(size=" + i + ", slot=" + j + ", limit=" + k + ")";
    ErrorMsg errorMsg = new ErrorMsg("INTERNAL_ERR", str);
    throw new Error(errorMsg.toString());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\SlotAllocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */