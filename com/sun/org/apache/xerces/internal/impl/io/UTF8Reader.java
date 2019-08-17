package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

public class UTF8Reader extends Reader {
  public static final int DEFAULT_BUFFER_SIZE = 2048;
  
  private static final boolean DEBUG_READ = false;
  
  protected InputStream fInputStream;
  
  protected byte[] fBuffer;
  
  protected int fOffset;
  
  private int fSurrogate = -1;
  
  private MessageFormatter fFormatter = null;
  
  private Locale fLocale = null;
  
  public UTF8Reader(InputStream paramInputStream) { this(paramInputStream, 2048, new XMLMessageFormatter(), Locale.getDefault()); }
  
  public UTF8Reader(InputStream paramInputStream, MessageFormatter paramMessageFormatter, Locale paramLocale) { this(paramInputStream, 2048, paramMessageFormatter, paramLocale); }
  
  public UTF8Reader(InputStream paramInputStream, int paramInt, MessageFormatter paramMessageFormatter, Locale paramLocale) {
    this.fInputStream = paramInputStream;
    BufferAllocator bufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    this.fBuffer = bufferAllocator.getByteBuffer(paramInt);
    if (this.fBuffer == null)
      this.fBuffer = new byte[paramInt]; 
    this.fFormatter = paramMessageFormatter;
    this.fLocale = paramLocale;
  }
  
  public int read() throws IOException {
    int i = this.fSurrogate;
    if (this.fSurrogate == -1) {
      byte b = 0;
      int j = (b == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[b++] & 0xFF);
      if (j == -1)
        return -1; 
      if (j < 128) {
        i = (char)j;
      } else if ((j & 0xE0) == 192 && (j & 0x1E) != 0) {
        int k = (b == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[b++] & 0xFF);
        if (k == -1)
          expectedByte(2, 2); 
        if ((k & 0xC0) != 128)
          invalidByte(2, 2, k); 
        i = j << 6 & 0x7C0 | k & 0x3F;
      } else if ((j & 0xF0) == 224) {
        int k = (b == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[b++] & 0xFF);
        if (k == -1)
          expectedByte(2, 3); 
        if ((k & 0xC0) != 128 || (j == 237 && k >= 160) || ((j & 0xF) == 0 && (k & 0x20) == 0))
          invalidByte(2, 3, k); 
        int m = (b == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[b++] & 0xFF);
        if (m == -1)
          expectedByte(3, 3); 
        if ((m & 0xC0) != 128)
          invalidByte(3, 3, m); 
        i = j << 12 & 0xF000 | k << 6 & 0xFC0 | m & 0x3F;
      } else if ((j & 0xF8) == 240) {
        int k = (b == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[b++] & 0xFF);
        if (k == -1)
          expectedByte(2, 4); 
        if ((k & 0xC0) != 128 || ((k & 0x30) == 0 && (j & 0x7) == 0))
          invalidByte(2, 3, k); 
        int m = (b == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[b++] & 0xFF);
        if (m == -1)
          expectedByte(3, 4); 
        if ((m & 0xC0) != 128)
          invalidByte(3, 3, m); 
        int n = (b == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[b++] & 0xFF);
        if (n == -1)
          expectedByte(4, 4); 
        if ((n & 0xC0) != 128)
          invalidByte(4, 4, n); 
        int i1 = j << 2 & 0x1C | k >> 4 & 0x3;
        if (i1 > 16)
          invalidSurrogate(i1); 
        int i2 = i1 - 1;
        int i3 = 0xD800 | i2 << 6 & 0x3C0 | k << 2 & 0x3C | m >> 4 & 0x3;
        int i4 = 0xDC00 | m << 6 & 0x3C0 | n & 0x3F;
        i = i3;
        this.fSurrogate = i4;
      } else {
        invalidByte(1, 1, j);
      } 
    } else {
      this.fSurrogate = -1;
    } 
    return i;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    int i = paramInt1;
    if (this.fSurrogate != -1) {
      paramArrayOfChar[paramInt1 + 1] = (char)this.fSurrogate;
      this.fSurrogate = -1;
      paramInt2--;
      i++;
    } 
    int j = 0;
    if (this.fOffset == 0) {
      if (paramInt2 > this.fBuffer.length)
        paramInt2 = this.fBuffer.length; 
      j = this.fInputStream.read(this.fBuffer, 0, paramInt2);
      if (j == -1)
        return -1; 
      j += i - paramInt1;
    } else {
      j = this.fOffset;
      this.fOffset = 0;
    } 
    int k = j;
    boolean bool = false;
    byte b = 0;
    while (b < k) {
      byte b1 = this.fBuffer[b];
      if (b1 >= 0) {
        paramArrayOfChar[i++] = (char)b1;
        b++;
      } 
    } 
    while (b < k) {
      byte b1 = this.fBuffer[b];
      if (b1 >= 0) {
        paramArrayOfChar[i++] = (char)b1;
      } else {
        byte b2 = b1 & 0xFF;
        if ((b2 & 0xE0) == 192 && (b2 & 0x1E) != 0) {
          int m = -1;
          if (++b < k) {
            m = this.fBuffer[b] & 0xFF;
          } else {
            m = this.fInputStream.read();
            if (m == -1) {
              if (i > paramInt1) {
                this.fBuffer[0] = (byte)b2;
                this.fOffset = 1;
                return i - paramInt1;
              } 
              expectedByte(2, 2);
            } 
            j++;
          } 
          if ((m & 0xC0) != 128) {
            if (i > paramInt1) {
              this.fBuffer[0] = (byte)b2;
              this.fBuffer[1] = (byte)m;
              this.fOffset = 2;
              return i - paramInt1;
            } 
            invalidByte(2, 2, m);
          } 
          byte b3 = b2 << 6 & 0x7C0 | m & 0x3F;
          paramArrayOfChar[i++] = (char)b3;
          j--;
        } else if ((b2 & 0xF0) == 224) {
          int m = -1;
          if (++b < k) {
            m = this.fBuffer[b] & 0xFF;
          } else {
            m = this.fInputStream.read();
            if (m == -1) {
              if (i > paramInt1) {
                this.fBuffer[0] = (byte)b2;
                this.fOffset = 1;
                return i - paramInt1;
              } 
              expectedByte(2, 3);
            } 
            j++;
          } 
          if ((m & 0xC0) != 128 || (b2 == 237 && m >= 160) || ((b2 & 0xF) == 0 && (m & 0x20) == 0)) {
            if (i > paramInt1) {
              this.fBuffer[0] = (byte)b2;
              this.fBuffer[1] = (byte)m;
              this.fOffset = 2;
              return i - paramInt1;
            } 
            invalidByte(2, 3, m);
          } 
          int n = -1;
          if (++b < k) {
            n = this.fBuffer[b] & 0xFF;
          } else {
            n = this.fInputStream.read();
            if (n == -1) {
              if (i > paramInt1) {
                this.fBuffer[0] = (byte)b2;
                this.fBuffer[1] = (byte)m;
                this.fOffset = 2;
                return i - paramInt1;
              } 
              expectedByte(3, 3);
            } 
            j++;
          } 
          if ((n & 0xC0) != 128) {
            if (i > paramInt1) {
              this.fBuffer[0] = (byte)b2;
              this.fBuffer[1] = (byte)m;
              this.fBuffer[2] = (byte)n;
              this.fOffset = 3;
              return i - paramInt1;
            } 
            invalidByte(3, 3, n);
          } 
          byte b3 = b2 << 12 & 0xF000 | m << 6 & 0xFC0 | n & 0x3F;
          paramArrayOfChar[i++] = (char)b3;
          j -= 2;
        } else if ((b2 & 0xF8) == 240) {
          int m = -1;
          if (++b < k) {
            m = this.fBuffer[b] & 0xFF;
          } else {
            m = this.fInputStream.read();
            if (m == -1) {
              if (i > paramInt1) {
                this.fBuffer[0] = (byte)b2;
                this.fOffset = 1;
                return i - paramInt1;
              } 
              expectedByte(2, 4);
            } 
            j++;
          } 
          if ((m & 0xC0) != 128 || ((m & 0x30) == 0 && (b2 & 0x7) == 0)) {
            if (i > paramInt1) {
              this.fBuffer[0] = (byte)b2;
              this.fBuffer[1] = (byte)m;
              this.fOffset = 2;
              return i - paramInt1;
            } 
            invalidByte(2, 4, m);
          } 
          int n = -1;
          if (++b < k) {
            n = this.fBuffer[b] & 0xFF;
          } else {
            n = this.fInputStream.read();
            if (n == -1) {
              if (i > paramInt1) {
                this.fBuffer[0] = (byte)b2;
                this.fBuffer[1] = (byte)m;
                this.fOffset = 2;
                return i - paramInt1;
              } 
              expectedByte(3, 4);
            } 
            j++;
          } 
          if ((n & 0xC0) != 128) {
            if (i > paramInt1) {
              this.fBuffer[0] = (byte)b2;
              this.fBuffer[1] = (byte)m;
              this.fBuffer[2] = (byte)n;
              this.fOffset = 3;
              return i - paramInt1;
            } 
            invalidByte(3, 4, n);
          } 
          int i1 = -1;
          if (++b < k) {
            i1 = this.fBuffer[b] & 0xFF;
          } else {
            i1 = this.fInputStream.read();
            if (i1 == -1) {
              if (i > paramInt1) {
                this.fBuffer[0] = (byte)b2;
                this.fBuffer[1] = (byte)m;
                this.fBuffer[2] = (byte)n;
                this.fOffset = 3;
                return i - paramInt1;
              } 
              expectedByte(4, 4);
            } 
            j++;
          } 
          if ((i1 & 0xC0) != 128) {
            if (i > paramInt1) {
              this.fBuffer[0] = (byte)b2;
              this.fBuffer[1] = (byte)m;
              this.fBuffer[2] = (byte)n;
              this.fBuffer[3] = (byte)i1;
              this.fOffset = 4;
              return i - paramInt1;
            } 
            invalidByte(4, 4, n);
          } 
          if (i + 1 >= paramArrayOfChar.length) {
            this.fBuffer[0] = (byte)b2;
            this.fBuffer[1] = (byte)m;
            this.fBuffer[2] = (byte)n;
            this.fBuffer[3] = (byte)i1;
            this.fOffset = 4;
            return i - paramInt1;
          } 
          byte b3 = b2 << 2 & 0x1C | m >> 4 & 0x3;
          if (b3 > 16)
            invalidSurrogate(b3); 
          byte b4 = b3 - 1;
          int i2 = m & 0xF;
          int i3 = n & 0x3F;
          int i4 = i1 & 0x3F;
          int i5 = 0xD800 | b4 << 6 & 0x3C0 | i2 << 2 | i3 >> 4;
          int i6 = 0xDC00 | i3 << 6 & 0x3C0 | i4;
          paramArrayOfChar[i++] = (char)i5;
          paramArrayOfChar[i++] = (char)i6;
          j -= 2;
        } else {
          if (i > paramInt1) {
            this.fBuffer[0] = (byte)b2;
            this.fOffset = 1;
            return i - paramInt1;
          } 
          invalidByte(1, 1, b2);
        } 
      } 
      b++;
    } 
    return j;
  }
  
  public long skip(long paramLong) throws IOException {
    long l = paramLong;
    char[] arrayOfChar = new char[this.fBuffer.length];
    while (true) {
      int i = (arrayOfChar.length < l) ? arrayOfChar.length : (int)l;
      int j = read(arrayOfChar, 0, i);
      if (j > 0) {
        l -= j;
        if (l <= 0L)
          break; 
        continue;
      } 
      break;
    } 
    return paramLong - l;
  }
  
  public boolean ready() throws IOException { return false; }
  
  public boolean markSupported() throws IOException { return false; }
  
  public void mark(int paramInt) throws IOException { throw new IOException(this.fFormatter.formatMessage(this.fLocale, "OperationNotSupported", new Object[] { "mark()", "UTF-8" })); }
  
  public void reset() throws IOException {
    this.fOffset = 0;
    this.fSurrogate = -1;
  }
  
  public void close() throws IOException {
    BufferAllocator bufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    bufferAllocator.returnByteBuffer(this.fBuffer);
    this.fBuffer = null;
    this.fInputStream.close();
  }
  
  private void expectedByte(int paramInt1, int paramInt2) throws MalformedByteSequenceException { throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "ExpectedByte", new Object[] { Integer.toString(paramInt1), Integer.toString(paramInt2) }); }
  
  private void invalidByte(int paramInt1, int paramInt2, int paramInt3) throws MalformedByteSequenceException { throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidByte", new Object[] { Integer.toString(paramInt1), Integer.toString(paramInt2) }); }
  
  private void invalidSurrogate(int paramInt) throws IOException { throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidHighSurrogate", new Object[] { Integer.toHexString(paramInt) }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\io\UTF8Reader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */