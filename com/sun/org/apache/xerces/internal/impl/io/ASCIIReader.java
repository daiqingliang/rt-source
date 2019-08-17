package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

public class ASCIIReader extends Reader {
  public static final int DEFAULT_BUFFER_SIZE = 2048;
  
  protected InputStream fInputStream;
  
  protected byte[] fBuffer;
  
  private MessageFormatter fFormatter = null;
  
  private Locale fLocale = null;
  
  public ASCIIReader(InputStream paramInputStream, MessageFormatter paramMessageFormatter, Locale paramLocale) { this(paramInputStream, 2048, paramMessageFormatter, paramLocale); }
  
  public ASCIIReader(InputStream paramInputStream, int paramInt, MessageFormatter paramMessageFormatter, Locale paramLocale) {
    this.fInputStream = paramInputStream;
    BufferAllocator bufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    this.fBuffer = bufferAllocator.getByteBuffer(paramInt);
    if (this.fBuffer == null)
      this.fBuffer = new byte[paramInt]; 
    this.fFormatter = paramMessageFormatter;
    this.fLocale = paramLocale;
  }
  
  public int read() throws IOException {
    int i = this.fInputStream.read();
    if (i >= 128)
      throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(i) }); 
    return i;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 > this.fBuffer.length)
      paramInt2 = this.fBuffer.length; 
    int i = this.fInputStream.read(this.fBuffer, 0, paramInt2);
    for (int j = 0; j < i; j++) {
      byte b = this.fBuffer[j];
      if (b < 0)
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(b & 0xFF) }); 
      paramArrayOfChar[paramInt1 + j] = (char)b;
    } 
    return i;
  }
  
  public long skip(long paramLong) throws IOException { return this.fInputStream.skip(paramLong); }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\io\ASCIIReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */