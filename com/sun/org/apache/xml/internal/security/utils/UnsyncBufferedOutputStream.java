package com.sun.org.apache.xml.internal.security.utils;

import java.io.IOException;
import java.io.OutputStream;

public class UnsyncBufferedOutputStream extends OutputStream {
  static final int size = 8192;
  
  private int pointer = 0;
  
  private final OutputStream out;
  
  private final byte[] buf = new byte[8192];
  
  public UnsyncBufferedOutputStream(OutputStream paramOutputStream) { this.out = paramOutputStream; }
  
  public void write(byte[] paramArrayOfByte) throws IOException { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = this.pointer + paramInt2;
    if (i > 8192) {
      flushBuffer();
      if (paramInt2 > 8192) {
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      } 
      i = paramInt2;
    } 
    System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.pointer, paramInt2);
    this.pointer = i;
  }
  
  private void flushBuffer() throws IOException {
    if (this.pointer > 0)
      this.out.write(this.buf, 0, this.pointer); 
    this.pointer = 0;
  }
  
  public void write(int paramInt) throws IOException {
    if (this.pointer >= 8192)
      flushBuffer(); 
    this.buf[this.pointer++] = (byte)paramInt;
  }
  
  public void flush() throws IOException {
    flushBuffer();
    this.out.flush();
  }
  
  public void close() throws IOException {
    flush();
    this.out.close();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\UnsyncBufferedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */