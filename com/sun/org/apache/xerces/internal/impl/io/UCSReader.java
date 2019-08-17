package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class UCSReader extends Reader {
  public static final int DEFAULT_BUFFER_SIZE = 8192;
  
  public static final short UCS2LE = 1;
  
  public static final short UCS2BE = 2;
  
  public static final short UCS4LE = 4;
  
  public static final short UCS4BE = 8;
  
  protected InputStream fInputStream;
  
  protected byte[] fBuffer;
  
  protected short fEncoding;
  
  public UCSReader(InputStream paramInputStream, short paramShort) { this(paramInputStream, 8192, paramShort); }
  
  public UCSReader(InputStream paramInputStream, int paramInt, short paramShort) {
    this.fInputStream = paramInputStream;
    BufferAllocator bufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    this.fBuffer = bufferAllocator.getByteBuffer(paramInt);
    if (this.fBuffer == null)
      this.fBuffer = new byte[paramInt]; 
    this.fEncoding = paramShort;
  }
  
  public int read() throws IOException {
    int i = this.fInputStream.read() & 0xFF;
    if (i == 255)
      return -1; 
    int j = this.fInputStream.read() & 0xFF;
    if (j == 255)
      return -1; 
    if (this.fEncoding >= 4) {
      int k = this.fInputStream.read() & 0xFF;
      if (k == 255)
        return -1; 
      int m = this.fInputStream.read() & 0xFF;
      if (m == 255)
        return -1; 
      System.err.println("b0 is " + (i & 0xFF) + " b1 " + (j & 0xFF) + " b2 " + (k & 0xFF) + " b3 " + (m & 0xFF));
      return (this.fEncoding == 8) ? ((i << 24) + (j << 16) + (k << 8) + m) : ((m << 24) + (k << 16) + (j << 8) + i);
    } 
    return (this.fEncoding == 2) ? ((i << 8) + j) : ((j << 8) + i);
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    int i = paramInt2 << ((this.fEncoding >= 4) ? 2 : 1);
    if (i > this.fBuffer.length)
      i = this.fBuffer.length; 
    int j = this.fInputStream.read(this.fBuffer, 0, i);
    if (j == -1)
      return -1; 
    if (this.fEncoding >= 4) {
      int n = 4 - (j & 0x3) & 0x3;
      for (int i1 = 0; i1 < n; i1++) {
        int i2 = this.fInputStream.read();
        if (i2 == -1) {
          for (int i3 = i1; i3 < n; i3++)
            this.fBuffer[j + i3] = 0; 
          break;
        } 
        this.fBuffer[j + i1] = (byte)i2;
      } 
      j += n;
    } else {
      int n = j & true;
      if (n != 0) {
        j++;
        int i1 = this.fInputStream.read();
        if (i1 == -1) {
          this.fBuffer[j] = 0;
        } else {
          this.fBuffer[j] = (byte)i1;
        } 
      } 
    } 
    int k = j >> ((this.fEncoding >= 4) ? 2 : 1);
    byte b = 0;
    for (int m = 0; m < k; m++) {
      byte b1 = this.fBuffer[b++] & 0xFF;
      byte b2 = this.fBuffer[b++] & 0xFF;
      if (this.fEncoding >= 4) {
        byte b3 = this.fBuffer[b++] & 0xFF;
        byte b4 = this.fBuffer[b++] & 0xFF;
        if (this.fEncoding == 8) {
          paramArrayOfChar[paramInt1 + m] = (char)((b1 << 24) + (b2 << 16) + (b3 << 8) + b4);
        } else {
          paramArrayOfChar[paramInt1 + m] = (char)((b4 << 24) + (b3 << 16) + (b2 << 8) + b1);
        } 
      } else if (this.fEncoding == 2) {
        paramArrayOfChar[paramInt1 + m] = (char)((b1 << 8) + b2);
      } else {
        paramArrayOfChar[paramInt1 + m] = (char)((b2 << 8) + b1);
      } 
    } 
    return k;
  }
  
  public long skip(long paramLong) throws IOException {
    byte b = (this.fEncoding >= 4) ? 2 : 1;
    long l = this.fInputStream.skip(paramLong << b);
    return ((l & (b | true)) == 0L) ? (l >> b) : ((l >> b) + 1L);
  }
  
  public boolean ready() throws IOException { return false; }
  
  public boolean markSupported() throws IOException { return this.fInputStream.markSupported(); }
  
  public void mark(int paramInt) throws IOException { this.fInputStream.mark(paramInt); }
  
  public void reset() throws IOException { this.fInputStream.reset(); }
  
  public void close() throws IOException {
    BufferAllocator bufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    bufferAllocator.returnByteBuffer(this.fBuffer);
    this.fBuffer = null;
    this.fInputStream.close();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\io\UCSReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */