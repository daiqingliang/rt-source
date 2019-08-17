package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMStateSet {
  int fBitCount;
  
  int fByteCount;
  
  int fBits1;
  
  int fBits2;
  
  byte[] fByteArray;
  
  public CMStateSet(int paramInt) {
    this.fBitCount = paramInt;
    if (this.fBitCount < 0)
      throw new RuntimeException("ImplementationMessages.VAL_CMSI"); 
    if (this.fBitCount > 64) {
      this.fByteCount = this.fBitCount / 8;
      if (this.fBitCount % 8 != 0)
        this.fByteCount++; 
      this.fByteArray = new byte[this.fByteCount];
    } 
    zeroBits();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    try {
      stringBuffer.append("{");
      for (byte b = 0; b < this.fBitCount; b++) {
        if (getBit(b))
          stringBuffer.append(" " + b); 
      } 
      stringBuffer.append(" }");
    } catch (RuntimeException runtimeException) {}
    return stringBuffer.toString();
  }
  
  public final void intersection(CMStateSet paramCMStateSet) {
    if (this.fBitCount < 65) {
      this.fBits1 &= paramCMStateSet.fBits1;
      this.fBits2 &= paramCMStateSet.fBits2;
    } else {
      for (int i = this.fByteCount - 1; i >= 0; i--)
        this.fByteArray[i] = (byte)(this.fByteArray[i] & paramCMStateSet.fByteArray[i]); 
    } 
  }
  
  public final boolean getBit(int paramInt) {
    if (paramInt >= this.fBitCount)
      throw new RuntimeException("ImplementationMessages.VAL_CMSI"); 
    if (this.fBitCount < 65) {
      int j = 1 << paramInt % 32;
      return (paramInt < 32) ? (((this.fBits1 & j) != 0)) : (((this.fBits2 & j) != 0));
    } 
    byte b = (byte)(1 << paramInt % 8);
    int i = paramInt >> 3;
    return ((this.fByteArray[i] & b) != 0);
  }
  
  public final boolean isEmpty() {
    if (this.fBitCount < 65)
      return (this.fBits1 == 0 && this.fBits2 == 0); 
    for (int i = this.fByteCount - 1; i >= 0; i--) {
      if (this.fByteArray[i] != 0)
        return false; 
    } 
    return true;
  }
  
  final boolean isSameSet(CMStateSet paramCMStateSet) {
    if (this.fBitCount != paramCMStateSet.fBitCount)
      return false; 
    if (this.fBitCount < 65)
      return (this.fBits1 == paramCMStateSet.fBits1 && this.fBits2 == paramCMStateSet.fBits2); 
    for (int i = this.fByteCount - 1; i >= 0; i--) {
      if (this.fByteArray[i] != paramCMStateSet.fByteArray[i])
        return false; 
    } 
    return true;
  }
  
  public final void union(CMStateSet paramCMStateSet) {
    if (this.fBitCount < 65) {
      this.fBits1 |= paramCMStateSet.fBits1;
      this.fBits2 |= paramCMStateSet.fBits2;
    } else {
      for (int i = this.fByteCount - 1; i >= 0; i--)
        this.fByteArray[i] = (byte)(this.fByteArray[i] | paramCMStateSet.fByteArray[i]); 
    } 
  }
  
  public final void setBit(int paramInt) {
    if (paramInt >= this.fBitCount)
      throw new RuntimeException("ImplementationMessages.VAL_CMSI"); 
    if (this.fBitCount < 65) {
      int i = 1 << paramInt % 32;
      if (paramInt < 32) {
        this.fBits1 &= (i ^ 0xFFFFFFFF);
        this.fBits1 |= i;
      } else {
        this.fBits2 &= (i ^ 0xFFFFFFFF);
        this.fBits2 |= i;
      } 
    } else {
      byte b = (byte)(1 << paramInt % 8);
      int i = paramInt >> 3;
      this.fByteArray[i] = (byte)(this.fByteArray[i] & (b ^ 0xFFFFFFFF));
      this.fByteArray[i] = (byte)(this.fByteArray[i] | b);
    } 
  }
  
  public final void setTo(CMStateSet paramCMStateSet) {
    if (this.fBitCount != paramCMStateSet.fBitCount)
      throw new RuntimeException("ImplementationMessages.VAL_CMSI"); 
    if (this.fBitCount < 65) {
      this.fBits1 = paramCMStateSet.fBits1;
      this.fBits2 = paramCMStateSet.fBits2;
    } else {
      for (int i = this.fByteCount - 1; i >= 0; i--)
        this.fByteArray[i] = paramCMStateSet.fByteArray[i]; 
    } 
  }
  
  public final void zeroBits() {
    if (this.fBitCount < 65) {
      this.fBits1 = 0;
      this.fBits2 = 0;
    } else {
      for (int i = this.fByteCount - 1; i >= 0; i--)
        this.fByteArray[i] = 0; 
    } 
  }
  
  public boolean equals(Object paramObject) { return !(paramObject instanceof CMStateSet) ? false : isSameSet((CMStateSet)paramObject); }
  
  public int hashCode() {
    if (this.fBitCount < 65)
      return this.fBits1 + this.fBits2 * 31; 
    byte b = 0;
    for (int i = this.fByteCount - 1; i >= 0; i--)
      b = this.fByteArray[i] + b * 31; 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\CMStateSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */