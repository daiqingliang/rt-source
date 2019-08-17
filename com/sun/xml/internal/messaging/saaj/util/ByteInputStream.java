package com.sun.xml.internal.messaging.saaj.util;

import java.io.ByteArrayInputStream;

public class ByteInputStream extends ByteArrayInputStream {
  private static final byte[] EMPTY_ARRAY = new byte[0];
  
  public ByteInputStream() { this(EMPTY_ARRAY, 0); }
  
  public ByteInputStream(byte[] paramArrayOfByte, int paramInt) { super(paramArrayOfByte, 0, paramInt); }
  
  public ByteInputStream(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { super(paramArrayOfByte, paramInt1, paramInt2); }
  
  public byte[] getBytes() { return this.buf; }
  
  public int getCount() { return this.count; }
  
  public void close() { reset(); }
  
  public void setBuf(byte[] paramArrayOfByte) {
    this.buf = paramArrayOfByte;
    this.pos = 0;
    this.count = paramArrayOfByte.length;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\ByteInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */