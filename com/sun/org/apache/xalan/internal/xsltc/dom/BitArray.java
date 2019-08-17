package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BitArray implements Externalizable {
  static final long serialVersionUID = -4876019880708377663L;
  
  private int[] _bits;
  
  private int _bitSize;
  
  private int _intSize;
  
  private int _mask;
  
  private static final int[] _masks = { 
      Integer.MIN_VALUE, 1073741824, 536870912, 268435456, 134217728, 67108864, 33554432, 16777216, 8388608, 4194304, 
      2097152, 1048576, 524288, 262144, 131072, 65536, 32768, 16384, 8192, 4096, 
      2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 
      2, 1 };
  
  private static final boolean DEBUG_ASSERTIONS = false;
  
  private int _pos = Integer.MAX_VALUE;
  
  private int _node = 0;
  
  private int _int = 0;
  
  private int _bit = 0;
  
  int _first = Integer.MAX_VALUE;
  
  int _last = Integer.MIN_VALUE;
  
  public BitArray() { this(32); }
  
  public BitArray(int paramInt) {
    if (paramInt < 32)
      paramInt = 32; 
    this._bitSize = paramInt;
    this._intSize = (this._bitSize >>> 5) + 1;
    this._bits = new int[this._intSize + 1];
  }
  
  public BitArray(int paramInt, int[] paramArrayOfInt) {
    if (paramInt < 32)
      paramInt = 32; 
    this._bitSize = paramInt;
    this._intSize = (this._bitSize >>> 5) + 1;
    this._bits = paramArrayOfInt;
  }
  
  public void setMask(int paramInt) { this._mask = paramInt; }
  
  public int getMask() { return this._mask; }
  
  public final int size() { return this._bitSize; }
  
  public final boolean getBit(int paramInt) { return ((this._bits[paramInt >>> 5] & _masks[paramInt % 32]) != 0); }
  
  public final int getNextBit(int paramInt) {
    for (int i = paramInt >>> 5; i <= this._intSize; i++) {
      int j = this._bits[i];
      if (j != 0)
        for (int k = paramInt % 32; k < 32; k++) {
          if ((j & _masks[k]) != 0)
            return (i << 5) + k; 
        }  
      paramInt = 0;
    } 
    return -1;
  }
  
  public final int getBitNumber(int paramInt) {
    if (paramInt == this._pos)
      return this._node; 
    if (paramInt < this._pos)
      this._int = this._bit = this._pos = 0; 
    while (this._int <= this._intSize) {
      int i = this._bits[this._int];
      if (i != 0) {
        while (this._bit < 32) {
          if ((i & _masks[this._bit]) != 0 && ++this._pos == paramInt) {
            this._node = (this._int << 5) + this._bit - 1;
            return this._node;
          } 
          this._bit++;
        } 
        this._bit = 0;
      } 
      this._int++;
    } 
    return 0;
  }
  
  public final int[] data() { return this._bits; }
  
  public final void setBit(int paramInt) {
    if (paramInt >= this._bitSize)
      return; 
    int i = paramInt >>> 5;
    if (i < this._first)
      this._first = i; 
    if (i > this._last)
      this._last = i; 
    this._bits[i] = this._bits[i] | _masks[paramInt % 32];
  }
  
  public final BitArray merge(BitArray paramBitArray) {
    if (this._last == -1) {
      this._bits = paramBitArray._bits;
    } else if (paramBitArray._last != -1) {
      int i = (this._first < paramBitArray._first) ? this._first : paramBitArray._first;
      int j = (this._last > paramBitArray._last) ? this._last : paramBitArray._last;
      if (paramBitArray._intSize > this._intSize) {
        if (j > this._intSize)
          j = this._intSize; 
        for (int k = i; k <= j; k++)
          paramBitArray._bits[k] = paramBitArray._bits[k] | this._bits[k]; 
        this._bits = paramBitArray._bits;
      } else {
        if (j > paramBitArray._intSize)
          j = paramBitArray._intSize; 
        for (int k = i; k <= j; k++)
          this._bits[k] = this._bits[k] | paramBitArray._bits[k]; 
      } 
    } 
    return this;
  }
  
  public final void resize(int paramInt) {
    if (paramInt > this._bitSize) {
      this._intSize = (paramInt >>> 5) + 1;
      int[] arrayOfInt = new int[this._intSize + 1];
      System.arraycopy(this._bits, 0, arrayOfInt, 0, (this._bitSize >>> 5) + 1);
      this._bits = arrayOfInt;
      this._bitSize = paramInt;
    } 
  }
  
  public BitArray cloneArray() { return new BitArray(this._intSize, this._bits); }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException {
    paramObjectOutput.writeInt(this._bitSize);
    paramObjectOutput.writeInt(this._mask);
    paramObjectOutput.writeObject(this._bits);
    paramObjectOutput.flush();
  }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    this._bitSize = paramObjectInput.readInt();
    this._intSize = (this._bitSize >>> 5) + 1;
    this._mask = paramObjectInput.readInt();
    this._bits = (int[])paramObjectInput.readObject();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\BitArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */