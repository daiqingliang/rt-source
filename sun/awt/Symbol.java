package sun.awt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class Symbol extends Charset {
  public Symbol() { super("Symbol", null); }
  
  public CharsetEncoder newEncoder() { return new Encoder(this); }
  
  public CharsetDecoder newDecoder() { throw new Error("Decoder is not implemented for Symbol Charset"); }
  
  public boolean contains(Charset paramCharset) { return paramCharset instanceof Symbol; }
  
  private static class Encoder extends CharsetEncoder {
    private static byte[] table_math = { 
        34, 0, 100, 36, 0, -58, 68, -47, -50, -49, 
        0, 0, 0, 39, 0, 80, 0, -27, 45, 0, 
        0, -92, 0, 42, -80, -73, -42, 0, 0, -75, 
        -91, 0, 0, 0, 0, -67, 0, 0, 0, -39, 
        -38, -57, -56, -14, 0, 0, 0, 0, 0, 0, 
        0, 0, 92, 0, 0, 0, 0, 0, 0, 0, 
        126, 0, 0, 0, 0, 0, 0, 0, 0, 64, 
        0, 0, -69, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, -71, -70, 0, 0, 
        -93, -77, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        -52, -55, -53, 0, -51, -54, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, -59, 
        0, -60, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 94, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, -32, -41, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, -68 };
    
    private static byte[] table_greek = { 
        65, 66, 71, 68, 69, 90, 72, 81, 73, 75, 
        76, 77, 78, 88, 79, 80, 82, 0, 83, 84, 
        85, 70, 67, 89, 87, 0, 0, 0, 0, 0, 
        0, 0, 97, 98, 103, 100, 101, 122, 104, 113, 
        105, 107, 108, 109, 110, 120, 111, 112, 114, 86, 
        115, 116, 117, 102, 99, 121, 119, 0, 0, 0, 
        0, 0, 0, 0, 74, -95, 0, 0, 106, 118 };
    
    public Encoder(Charset param1Charset) { super(param1Charset, 1.0F, 1.0F); }
    
    public boolean canEncode(char param1Char) {
      if (param1Char >= '∀' && param1Char <= '⋯') {
        if (table_math[param1Char - '∀'] != 0)
          return true; 
      } else if (param1Char >= 'Α' && param1Char <= 'ϖ' && table_greek[param1Char - 'Α'] != 0) {
        return true;
      } 
      return false;
    }
    
    protected CoderResult encodeLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) {
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
          if (m - k < 1)
            return CoderResult.OVERFLOW; 
          if (!canEncode(c))
            return CoderResult.unmappableForLength(1); 
          i++;
          if (c >= '∀' && c <= '⋯') {
            arrayOfByte[k++] = table_math[c - '∀'];
            continue;
          } 
          if (c >= 'Α' && c <= 'ϖ')
            arrayOfByte[k++] = table_greek[c - 'Α']; 
        } 
        return CoderResult.UNDERFLOW;
      } finally {
        param1CharBuffer.position(i - param1CharBuffer.arrayOffset());
        param1ByteBuffer.position(k - param1ByteBuffer.arrayOffset());
      } 
    }
    
    public boolean isLegalReplacement(byte[] param1ArrayOfByte) { return true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\Symbol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */