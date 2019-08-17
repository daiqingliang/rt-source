package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteOutputStream extends OutputStream {
  protected byte[] buf;
  
  protected int count = 0;
  
  public ByteOutputStream() { this(1024); }
  
  public ByteOutputStream(int paramInt) { this.buf = new byte[paramInt]; }
  
  public void write(InputStream paramInputStream) throws IOException {
    if (paramInputStream instanceof java.io.ByteArrayInputStream) {
      int i = paramInputStream.available();
      ensureCapacity(i);
      this.count += paramInputStream.read(this.buf, this.count, i);
      return;
    } 
    while (true) {
      int i = this.buf.length - this.count;
      int j = paramInputStream.read(this.buf, this.count, i);
      if (j < 0)
        return; 
      this.count += j;
      if (i == j)
        ensureCapacity(this.count); 
    } 
  }
  
  public void write(int paramInt) {
    ensureCapacity(1);
    this.buf[this.count] = (byte)paramInt;
    this.count++;
  }
  
  private void ensureCapacity(int paramInt) {
    int i = paramInt + this.count;
    if (i > this.buf.length) {
      byte[] arrayOfByte = new byte[Math.max(this.buf.length << 1, i)];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
      this.buf = arrayOfByte;
    } 
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    ensureCapacity(paramInt2);
    System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.count, paramInt2);
    this.count += paramInt2;
  }
  
  public void write(byte[] paramArrayOfByte) { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void writeAsAscii(String paramString) {
    int i = paramString.length();
    ensureCapacity(i);
    int j = this.count;
    for (byte b = 0; b < i; b++)
      this.buf[j++] = (byte)paramString.charAt(b); 
    this.count = j;
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException { paramOutputStream.write(this.buf, 0, this.count); }
  
  public void reset() { this.count = 0; }
  
  public byte[] toByteArray() {
    byte[] arrayOfByte = new byte[this.count];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
    return arrayOfByte;
  }
  
  public int size() { return this.count; }
  
  public ByteInputStream newInputStream() { return new ByteInputStream(this.buf, this.count); }
  
  public String toString() { return new String(this.buf, 0, this.count); }
  
  public void close() {}
  
  public byte[] getBytes() { return this.buf; }
  
  public int getCount() { return this.count; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\ByteOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */