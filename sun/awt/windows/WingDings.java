package sun.awt.windows;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public final class WingDings extends Charset {
  public WingDings() { super("WingDings", null); }
  
  public CharsetEncoder newEncoder() { return new Encoder(this); }
  
  public CharsetDecoder newDecoder() { throw new Error("Decoder isn't implemented for WingDings Charset"); }
  
  public boolean contains(Charset paramCharset) { return paramCharset instanceof WingDings; }
  
  private static class Encoder extends CharsetEncoder {
    private static byte[] table = { 
        0, 35, 34, 0, 0, 0, 41, 62, 81, 42, 
        0, 0, 65, 63, 0, 0, 0, 0, 0, -4, 
        0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 
        86, 0, 88, 89, 0, 0, 0, 0, 0, 0, 
        0, 0, -75, 0, 0, 0, 0, 0, -74, 0, 
        0, 0, -83, -81, -84, 0, 0, 0, 0, 0, 
        0, 0, 0, 124, 123, 0, 0, 0, 84, 0, 
        0, 0, 0, 0, 0, 0, 0, -90, 0, 0, 
        0, 113, 114, 0, 0, 0, 117, 0, 0, 0, 
        0, 0, 0, 125, 126, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, -116, -115, 
        -114, -113, -112, -111, -110, -109, -108, -107, -127, -126, 
        -125, -124, -123, -122, -121, -120, -119, -118, -116, -115, 
        -114, -113, -112, -111, -110, -109, -108, -107, -24, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, -24, -40, 0, 0, -60, -58, 0, 0, -16, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, -36, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0 };
    
    public Encoder(Charset param1Charset) { super(param1Charset, 1.0F, 1.0F); }
    
    public boolean canEncode(char param1Char) { return (param1Char >= '✁' && param1Char <= '➾') ? ((table[param1Char - '✀'] != 0)) : false; }
    
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
          arrayOfByte[k++] = table[c - '✀'];
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WingDings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */