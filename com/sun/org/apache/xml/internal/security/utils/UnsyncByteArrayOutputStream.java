package com.sun.org.apache.xml.internal.security.utils;

import java.io.OutputStream;

public class UnsyncByteArrayOutputStream extends OutputStream {
  private static final int INITIAL_SIZE = 8192;
  
  private byte[] buf = new byte[8192];
  
  private int size = 8192;
  
  private int pos = 0;
  
  public void write(byte[] paramArrayOfByte) {
    if (Integer.MAX_VALUE - this.pos < paramArrayOfByte.length)
      throw new OutOfMemoryError(); 
    int i = this.pos + paramArrayOfByte.length;
    if (i > this.size)
      expandSize(i); 
    System.arraycopy(paramArrayOfByte, 0, this.buf, this.pos, paramArrayOfByte.length);
    this.pos = i;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (Integer.MAX_VALUE - this.pos < paramInt2)
      throw new OutOfMemoryError(); 
    int i = this.pos + paramInt2;
    if (i > this.size)
      expandSize(i); 
    System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.pos, paramInt2);
    this.pos = i;
  }
  
  public void write(int paramInt) {
    if (Integer.MAX_VALUE - this.pos == 0)
      throw new OutOfMemoryError(); 
    int i = this.pos + 1;
    if (i > this.size)
      expandSize(i); 
    this.buf[this.pos++] = (byte)paramInt;
  }
  
  public byte[] toByteArray() {
    byte[] arrayOfByte = new byte[this.pos];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.pos);
    return arrayOfByte;
  }
  
  public void reset() { this.pos = 0; }
  
  private void expandSize(int paramInt) {
    int i = this.size;
    while (paramInt > i) {
      i <<= 1;
      if (i < 0)
        i = Integer.MAX_VALUE; 
    } 
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.pos);
    this.buf = arrayOfByte;
    this.size = i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\UnsyncByteArrayOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */