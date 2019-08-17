package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

class ISO_8859_1 extends Charset implements HistoricallyNamedCharset {
  public ISO_8859_1() { super("ISO-8859-1", StandardCharsets.aliases_ISO_8859_1); }
  
  public String historicalName() { return "ISO8859_1"; }
  
  public boolean contains(Charset paramCharset) { return (paramCharset instanceof US_ASCII || paramCharset instanceof ISO_8859_1); }
  
  public CharsetDecoder newDecoder() { return new Decoder(this, null); }
  
  public CharsetEncoder newEncoder() { return new Encoder(this, null); }
  
  private static class Decoder extends CharsetDecoder implements ArrayDecoder {
    private Decoder(Charset param1Charset) { super(param1Charset, 1.0F, 1.0F); }
    
    private CoderResult decodeArrayLoop(ByteBuffer param1ByteBuffer, CharBuffer param1CharBuffer) {
      byte[] arrayOfByte = param1ByteBuffer.array();
      i = param1ByteBuffer.arrayOffset() + param1ByteBuffer.position();
      int j = param1ByteBuffer.arrayOffset() + param1ByteBuffer.limit();
      assert i <= j;
      i = (i <= j) ? i : j;
      char[] arrayOfChar = param1CharBuffer.array();
      k = param1CharBuffer.arrayOffset() + param1CharBuffer.position();
      int m = param1CharBuffer.arrayOffset() + param1CharBuffer.limit();
      assert k <= m;
      k = (k <= m) ? k : m;
      try {
        while (i < j) {
          byte b = arrayOfByte[i];
          if (k >= m)
            return CoderResult.OVERFLOW; 
          arrayOfChar[k++] = (char)(b & 0xFF);
          i++;
        } 
        return CoderResult.UNDERFLOW;
      } finally {
        param1ByteBuffer.position(i - param1ByteBuffer.arrayOffset());
        param1CharBuffer.position(k - param1CharBuffer.arrayOffset());
      } 
    }
    
    private CoderResult decodeBufferLoop(ByteBuffer param1ByteBuffer, CharBuffer param1CharBuffer) {
      i = param1ByteBuffer.position();
      try {
        while (param1ByteBuffer.hasRemaining()) {
          byte b = param1ByteBuffer.get();
          if (!param1CharBuffer.hasRemaining())
            return CoderResult.OVERFLOW; 
          param1CharBuffer.put((char)(b & 0xFF));
          i++;
        } 
        return CoderResult.UNDERFLOW;
      } finally {
        param1ByteBuffer.position(i);
      } 
    }
    
    protected CoderResult decodeLoop(ByteBuffer param1ByteBuffer, CharBuffer param1CharBuffer) { return (param1ByteBuffer.hasArray() && param1CharBuffer.hasArray()) ? decodeArrayLoop(param1ByteBuffer, param1CharBuffer) : decodeBufferLoop(param1ByteBuffer, param1CharBuffer); }
    
    public int decode(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, char[] param1ArrayOfChar) {
      if (param1Int2 > param1ArrayOfChar.length)
        param1Int2 = param1ArrayOfChar.length; 
      byte b = 0;
      while (b < param1Int2)
        param1ArrayOfChar[b++] = (char)(param1ArrayOfByte[param1Int1++] & 0xFF); 
      return b;
    }
  }
  
  private static class Encoder extends CharsetEncoder implements ArrayEncoder {
    private final Surrogate.Parser sgp = new Surrogate.Parser();
    
    private byte repl = 63;
    
    private Encoder(Charset param1Charset) { super(param1Charset, 1.0F, 1.0F); }
    
    public boolean canEncode(char param1Char) { return (param1Char <= 'ÿ'); }
    
    public boolean isLegalReplacement(byte[] param1ArrayOfByte) { return true; }
    
    private static int encodeISOArray(char[] param1ArrayOfChar, int param1Int1, byte[] param1ArrayOfByte, int param1Int2, int param1Int3) {
      byte b;
      for (b = 0; b < param1Int3; b++) {
        char c = param1ArrayOfChar[param1Int1++];
        if (c > 'ÿ')
          break; 
        param1ArrayOfByte[param1Int2++] = (byte)c;
      } 
      return b;
    }
    
    private CoderResult encodeArrayLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) {
      char[] arrayOfChar = param1CharBuffer.array();
      i = param1CharBuffer.arrayOffset();
      j = i + param1CharBuffer.position();
      int k = i + param1CharBuffer.limit();
      assert j <= k;
      j = (j <= k) ? j : k;
      byte[] arrayOfByte = param1ByteBuffer.array();
      m = param1ByteBuffer.arrayOffset();
      n = m + param1ByteBuffer.position();
      int i1 = m + param1ByteBuffer.limit();
      assert n <= i1;
      n = (n <= i1) ? n : i1;
      int i2 = i1 - n;
      int i3 = k - j;
      int i4 = (i2 < i3) ? i2 : i3;
      try {
        int i5 = (i4 <= 0) ? 0 : encodeISOArray(arrayOfChar, j, arrayOfByte, n, i4);
        j += i5;
        n += i5;
        if (i5 != i4) {
          if (this.sgp.parse(arrayOfChar[j], arrayOfChar, j, k) < 0)
            return this.sgp.error(); 
          return this.sgp.unmappableResult();
        } 
        if (i4 < i3)
          return CoderResult.OVERFLOW; 
        return CoderResult.UNDERFLOW;
      } finally {
        param1CharBuffer.position(j - i);
        param1ByteBuffer.position(n - m);
      } 
    }
    
    private CoderResult encodeBufferLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) {
      i = param1CharBuffer.position();
      try {
        while (param1CharBuffer.hasRemaining()) {
          char c = param1CharBuffer.get();
          if (c <= 'ÿ') {
            if (!param1ByteBuffer.hasRemaining())
              return CoderResult.OVERFLOW; 
            param1ByteBuffer.put((byte)c);
            i++;
            continue;
          } 
          if (this.sgp.parse(c, param1CharBuffer) < 0)
            return this.sgp.error(); 
          return this.sgp.unmappableResult();
        } 
        return CoderResult.UNDERFLOW;
      } finally {
        param1CharBuffer.position(i);
      } 
    }
    
    protected CoderResult encodeLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) { return (param1CharBuffer.hasArray() && param1ByteBuffer.hasArray()) ? encodeArrayLoop(param1CharBuffer, param1ByteBuffer) : encodeBufferLoop(param1CharBuffer, param1ByteBuffer); }
    
    protected void implReplaceWith(byte[] param1ArrayOfByte) { this.repl = param1ArrayOfByte[0]; }
    
    public int encode(char[] param1ArrayOfChar, int param1Int1, int param1Int2, byte[] param1ArrayOfByte) {
      int i = 0;
      int j = Math.min(param1Int2, param1ArrayOfByte.length);
      int k = param1Int1 + j;
      while (param1Int1 < k) {
        int m = (j <= 0) ? 0 : encodeISOArray(param1ArrayOfChar, param1Int1, param1ArrayOfByte, i, j);
        param1Int1 += m;
        i += m;
        if (m != j) {
          char c = param1ArrayOfChar[param1Int1++];
          if (Character.isHighSurrogate(c) && param1Int1 < k && Character.isLowSurrogate(param1ArrayOfChar[param1Int1])) {
            if (param1Int2 > param1ArrayOfByte.length) {
              k++;
              param1Int2--;
            } 
            param1Int1++;
          } 
          param1ArrayOfByte[i++] = this.repl;
          j = Math.min(k - param1Int1, param1ArrayOfByte.length - i);
        } 
      } 
      return i;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\ISO_8859_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */