package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class US_ASCII extends Charset implements HistoricallyNamedCharset {
  public US_ASCII() { super("US-ASCII", StandardCharsets.aliases_US_ASCII); }
  
  public String historicalName() { return "ASCII"; }
  
  public boolean contains(Charset paramCharset) { return paramCharset instanceof US_ASCII; }
  
  public CharsetDecoder newDecoder() { return new Decoder(this, null); }
  
  public CharsetEncoder newEncoder() { return new Encoder(this, null); }
  
  private static class Decoder extends CharsetDecoder implements ArrayDecoder {
    private char repl = '�';
    
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
          if (b >= 0) {
            if (k >= m)
              return CoderResult.OVERFLOW; 
            arrayOfChar[k++] = (char)b;
            i++;
            continue;
          } 
          return CoderResult.malformedForLength(1);
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
          if (b >= 0) {
            if (!param1CharBuffer.hasRemaining())
              return CoderResult.OVERFLOW; 
            param1CharBuffer.put((char)b);
            i++;
            continue;
          } 
          return CoderResult.malformedForLength(1);
        } 
        return CoderResult.UNDERFLOW;
      } finally {
        param1ByteBuffer.position(i);
      } 
    }
    
    protected CoderResult decodeLoop(ByteBuffer param1ByteBuffer, CharBuffer param1CharBuffer) { return (param1ByteBuffer.hasArray() && param1CharBuffer.hasArray()) ? decodeArrayLoop(param1ByteBuffer, param1CharBuffer) : decodeBufferLoop(param1ByteBuffer, param1CharBuffer); }
    
    protected void implReplaceWith(String param1String) { this.repl = param1String.charAt(0); }
    
    public int decode(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, char[] param1ArrayOfChar) {
      byte b = 0;
      param1Int2 = Math.min(param1Int2, param1ArrayOfChar.length);
      while (b < param1Int2) {
        byte b1 = param1ArrayOfByte[param1Int1++];
        if (b1 >= 0) {
          param1ArrayOfChar[b++] = (char)b1;
          continue;
        } 
        param1ArrayOfChar[b++] = this.repl;
      } 
      return b;
    }
  }
  
  private static class Encoder extends CharsetEncoder implements ArrayEncoder {
    private final Surrogate.Parser sgp = new Surrogate.Parser();
    
    private byte repl = 63;
    
    private Encoder(Charset param1Charset) { super(param1Charset, 1.0F, 1.0F); }
    
    public boolean canEncode(char param1Char) { return (param1Char < ''); }
    
    public boolean isLegalReplacement(byte[] param1ArrayOfByte) { return ((param1ArrayOfByte.length == 1 && param1ArrayOfByte[0] >= 0) || super.isLegalReplacement(param1ArrayOfByte)); }
    
    private CoderResult encodeArrayLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) {
      char[] arrayOfChar = param1CharBuffer.array();
      i = param1CharBuffer.arrayOffset() + param1CharBuffer.position();
      int j = param1CharBuffer.arrayOffset() + param1CharBuffer.limit();
      assert i <= j;
      i = (i <= j) ? i : j;
      byte[] arrayOfByte = param1ByteBuffer.array();
      k = param1ByteBuffer.arrayOffset() + param1ByteBuffer.position();
      int m = param1ByteBuffer.arrayOffset() + param1ByteBuffer.limit();
      assert k <= m;
      k = (k <= m) ? k : m;
      try {
        while (i < j) {
          char c = arrayOfChar[i];
          if (c < '') {
            if (k >= m)
              return CoderResult.OVERFLOW; 
            arrayOfByte[k] = (byte)c;
            i++;
            k++;
            continue;
          } 
          if (this.sgp.parse(c, arrayOfChar, i, j) < 0)
            return this.sgp.error(); 
          return this.sgp.unmappableResult();
        } 
        return CoderResult.UNDERFLOW;
      } finally {
        param1CharBuffer.position(i - param1CharBuffer.arrayOffset());
        param1ByteBuffer.position(k - param1ByteBuffer.arrayOffset());
      } 
    }
    
    private CoderResult encodeBufferLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) {
      i = param1CharBuffer.position();
      try {
        while (param1CharBuffer.hasRemaining()) {
          char c = param1CharBuffer.get();
          if (c < '') {
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
      byte b = 0;
      int i = param1Int1 + Math.min(param1Int2, param1ArrayOfByte.length);
      while (param1Int1 < i) {
        char c = param1ArrayOfChar[param1Int1++];
        if (c < '') {
          param1ArrayOfByte[b++] = (byte)c;
          continue;
        } 
        if (Character.isHighSurrogate(c) && param1Int1 < i && Character.isLowSurrogate(param1ArrayOfChar[param1Int1])) {
          if (param1Int2 > param1ArrayOfByte.length) {
            i++;
            param1Int2--;
          } 
          param1Int1++;
        } 
        param1ArrayOfByte[b++] = this.repl;
      } 
      return b;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\US_ASCII.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */