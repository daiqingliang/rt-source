package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream extends InputStream {
  protected InputStream source;
  
  protected OutputStream copySink;
  
  public TeeInputStream(InputStream paramInputStream, OutputStream paramOutputStream) {
    this.copySink = paramOutputStream;
    this.source = paramInputStream;
  }
  
  public int read() throws IOException {
    int i = this.source.read();
    this.copySink.write(i);
    return i;
  }
  
  public int available() throws IOException { return this.source.available(); }
  
  public void close() throws IOException { this.source.close(); }
  
  public void mark(int paramInt) { this.source.mark(paramInt); }
  
  public boolean markSupported() { return this.source.markSupported(); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = this.source.read(paramArrayOfByte, paramInt1, paramInt2);
    this.copySink.write(paramArrayOfByte, paramInt1, paramInt2);
    return i;
  }
  
  public int read(byte[] paramArrayOfByte) throws IOException {
    int i = this.source.read(paramArrayOfByte);
    this.copySink.write(paramArrayOfByte);
    return i;
  }
  
  public void reset() throws IOException { this.source.reset(); }
  
  public long skip(long paramLong) throws IOException { return this.source.skip(paramLong); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\TeeInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */