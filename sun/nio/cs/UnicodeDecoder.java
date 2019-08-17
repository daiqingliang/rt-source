package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

abstract class UnicodeDecoder extends CharsetDecoder {
  protected static final char BYTE_ORDER_MARK = '﻿';
  
  protected static final char REVERSED_MARK = '￾';
  
  protected static final int NONE = 0;
  
  protected static final int BIG = 1;
  
  protected static final int LITTLE = 2;
  
  private final int expectedByteOrder;
  
  private int currentByteOrder;
  
  private int defaultByteOrder = 1;
  
  public UnicodeDecoder(Charset paramCharset, int paramInt) {
    super(paramCharset, 0.5F, 1.0F);
    this.expectedByteOrder = this.currentByteOrder = paramInt;
  }
  
  public UnicodeDecoder(Charset paramCharset, int paramInt1, int paramInt2) {
    this(paramCharset, paramInt1);
    this.defaultByteOrder = paramInt2;
  }
  
  private char decode(int paramInt1, int paramInt2) { return (this.currentByteOrder == 1) ? (char)(paramInt1 << 8 | paramInt2) : (char)(paramInt2 << 8 | paramInt1); }
  
  protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer) {
    i = paramByteBuffer.position();
    try {
      while (paramByteBuffer.remaining() > 1) {
        byte b1 = paramByteBuffer.get() & 0xFF;
        byte b2 = paramByteBuffer.get() & 0xFF;
        if (this.currentByteOrder == 0) {
          char c1 = (char)(b1 << 8 | b2);
          if (c1 == '﻿') {
            this.currentByteOrder = 1;
            i += 2;
            continue;
          } 
          if (c1 == '￾') {
            this.currentByteOrder = 2;
            i += 2;
            continue;
          } 
          this.currentByteOrder = this.defaultByteOrder;
        } 
        char c = decode(b1, b2);
        if (c == '￾')
          return CoderResult.malformedForLength(2); 
        if (Character.isSurrogate(c)) {
          if (Character.isHighSurrogate(c)) {
            if (paramByteBuffer.remaining() < 2)
              return CoderResult.UNDERFLOW; 
            char c1 = decode(paramByteBuffer.get() & 0xFF, paramByteBuffer.get() & 0xFF);
            if (!Character.isLowSurrogate(c1))
              return CoderResult.malformedForLength(4); 
            if (paramCharBuffer.remaining() < 2)
              return CoderResult.OVERFLOW; 
            i += 4;
            paramCharBuffer.put(c);
            paramCharBuffer.put(c1);
            continue;
          } 
          return CoderResult.malformedForLength(2);
        } 
        if (!paramCharBuffer.hasRemaining())
          return CoderResult.OVERFLOW; 
        i += 2;
        paramCharBuffer.put(c);
      } 
      return CoderResult.UNDERFLOW;
    } finally {
      paramByteBuffer.position(i);
    } 
  }
  
  protected void implReset() { this.currentByteOrder = this.expectedByteOrder; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\UnicodeDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */