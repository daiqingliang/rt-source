package com.sun.xml.internal.ws.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArrayBuffer extends OutputStream {
  protected byte[] buf;
  
  private int count;
  
  private static final int CHUNK_SIZE = 4096;
  
  public ByteArrayBuffer() { this(32); }
  
  public ByteArrayBuffer(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException(); 
    this.buf = new byte[paramInt];
  }
  
  public ByteArrayBuffer(byte[] paramArrayOfByte) { this(paramArrayOfByte, paramArrayOfByte.length); }
  
  public ByteArrayBuffer(byte[] paramArrayOfByte, int paramInt) {
    this.buf = paramArrayOfByte;
    this.count = paramInt;
  }
  
  public final void write(InputStream paramInputStream) throws IOException {
    while (true) {
      int i = this.buf.length - this.count;
      int j = paramInputStream.read(this.buf, this.count, i);
      if (j < 0)
        return; 
      this.count += j;
      if (i == j)
        ensureCapacity(this.buf.length * 2); 
    } 
  }
  
  public final void write(int paramInt) {
    int i = this.count + 1;
    ensureCapacity(i);
    this.buf[this.count] = (byte)paramInt;
    this.count = i;
  }
  
  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    int i = this.count + paramInt2;
    ensureCapacity(i);
    System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.count, paramInt2);
    this.count = i;
  }
  
  private void ensureCapacity(int paramInt) {
    if (paramInt > this.buf.length) {
      byte[] arrayOfByte = new byte[Math.max(this.buf.length << 1, paramInt)];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
      this.buf = arrayOfByte;
    } 
  }
  
  public final void writeTo(OutputStream paramOutputStream) throws IOException {
    int i = this.count;
    for (int j = 0; i > 0; j += k) {
      int k = (i > 4096) ? 4096 : i;
      paramOutputStream.write(this.buf, j, k);
      i -= k;
    } 
  }
  
  public final void reset() { this.count = 0; }
  
  public final byte[] toByteArray() {
    byte[] arrayOfByte = new byte[this.count];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
    return arrayOfByte;
  }
  
  public final int size() { return this.count; }
  
  public final byte[] getRawData() { return this.buf; }
  
  public void close() {}
  
  public final InputStream newInputStream() { return new ByteArrayInputStream(this.buf, 0, this.count); }
  
  public final InputStream newInputStream(int paramInt1, int paramInt2) { return new ByteArrayInputStream(this.buf, paramInt1, paramInt2); }
  
  public String toString() { return new String(this.buf, 0, this.count); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\ByteArrayBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */