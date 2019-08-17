package com.sun.corba.se.impl.ior;

public class ByteBuffer {
  protected byte[] elementData;
  
  protected int elementCount;
  
  protected int capacityIncrement;
  
  public ByteBuffer(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt1); 
    this.elementData = new byte[paramInt1];
    this.capacityIncrement = paramInt2;
  }
  
  public ByteBuffer(int paramInt) { this(paramInt, 0); }
  
  public ByteBuffer() { this(200); }
  
  public void trimToSize() {
    int i = this.elementData.length;
    if (this.elementCount < i) {
      byte[] arrayOfByte = this.elementData;
      this.elementData = new byte[this.elementCount];
      System.arraycopy(arrayOfByte, 0, this.elementData, 0, this.elementCount);
    } 
  }
  
  private void ensureCapacityHelper(int paramInt) {
    int i = this.elementData.length;
    if (paramInt > i) {
      byte[] arrayOfByte = this.elementData;
      int j = (this.capacityIncrement > 0) ? (i + this.capacityIncrement) : (i * 2);
      if (j < paramInt)
        j = paramInt; 
      this.elementData = new byte[j];
      System.arraycopy(arrayOfByte, 0, this.elementData, 0, this.elementCount);
    } 
  }
  
  public int capacity() { return this.elementData.length; }
  
  public int size() { return this.elementCount; }
  
  public boolean isEmpty() { return (this.elementCount == 0); }
  
  public void append(byte paramByte) {
    ensureCapacityHelper(this.elementCount + 1);
    this.elementData[this.elementCount++] = paramByte;
  }
  
  public void append(int paramInt) {
    ensureCapacityHelper(this.elementCount + 4);
    doAppend(paramInt);
  }
  
  private void doAppend(int paramInt) {
    int i = paramInt;
    for (int j = 0; j < 4; j++) {
      this.elementData[this.elementCount + j] = (byte)(i & 0xFF);
      i >>= 8;
    } 
    this.elementCount += 4;
  }
  
  public void append(String paramString) {
    byte[] arrayOfByte = paramString.getBytes();
    ensureCapacityHelper(this.elementCount + arrayOfByte.length + 4);
    doAppend(arrayOfByte.length);
    System.arraycopy(arrayOfByte, 0, this.elementData, this.elementCount, arrayOfByte.length);
    this.elementCount += arrayOfByte.length;
  }
  
  public byte[] toArray() { return this.elementData; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */