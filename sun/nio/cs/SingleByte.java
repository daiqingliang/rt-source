package sun.nio.cs;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class SingleByte {
  private static final CoderResult withResult(CoderResult paramCoderResult, Buffer paramBuffer1, int paramInt1, Buffer paramBuffer2, int paramInt2) {
    paramBuffer1.position(paramInt1 - paramBuffer1.arrayOffset());
    paramBuffer2.position(paramInt2 - paramBuffer2.arrayOffset());
    return paramCoderResult;
  }
  
  public static void initC2B(char[] paramArrayOfChar1, char[] paramArrayOfChar2, char[] paramArrayOfChar3, char[] paramArrayOfChar4) {
    byte b1;
    for (b1 = 0; b1 < paramArrayOfChar4.length; b1++)
      paramArrayOfChar4[b1] = '�'; 
    for (b1 = 0; b1 < paramArrayOfChar3.length; b1++)
      paramArrayOfChar3[b1] = '�'; 
    b1 = 0;
    byte b2;
    for (b2 = 0; b2 < paramArrayOfChar1.length; b2++) {
      char c = paramArrayOfChar1[b2];
      if (c != '�') {
        char c1 = c >> '\b';
        if (paramArrayOfChar4[c1] == '�') {
          paramArrayOfChar4[c1] = (char)b1;
          b1 += 256;
        } 
        c1 = paramArrayOfChar4[c1] + (c & 0xFF);
        paramArrayOfChar3[c1] = (char)((b2 >= '') ? (b2 - '') : (b2 + ''));
      } 
    } 
    if (paramArrayOfChar2 != null) {
      b2 = 0;
      while (b2 < paramArrayOfChar2.length) {
        char c1 = paramArrayOfChar2[b2++];
        char c2 = paramArrayOfChar2[b2++];
        char c3 = c2 >> '\b';
        if (paramArrayOfChar4[c3] == '�') {
          paramArrayOfChar4[c3] = (char)b1;
          b1 += 256;
        } 
        c3 = paramArrayOfChar4[c3] + (c2 & 0xFF);
        paramArrayOfChar3[c3] = c1;
      } 
    } 
  }
  
  public static final class Decoder extends CharsetDecoder implements ArrayDecoder {
    private final char[] b2c;
    
    private char repl = '�';
    
    public Decoder(Charset param1Charset, char[] param1ArrayOfChar) {
      super(param1Charset, 1.0F, 1.0F);
      this.b2c = param1ArrayOfChar;
    }
    
    private CoderResult decodeArrayLoop(ByteBuffer param1ByteBuffer, CharBuffer param1CharBuffer) {
      byte[] arrayOfByte = param1ByteBuffer.array();
      int i = param1ByteBuffer.arrayOffset() + param1ByteBuffer.position();
      int j = param1ByteBuffer.arrayOffset() + param1ByteBuffer.limit();
      char[] arrayOfChar = param1CharBuffer.array();
      int k = param1CharBuffer.arrayOffset() + param1CharBuffer.position();
      int m = param1CharBuffer.arrayOffset() + param1CharBuffer.limit();
      CoderResult coderResult = CoderResult.UNDERFLOW;
      if (m - k < j - i) {
        j = i + m - k;
        coderResult = CoderResult.OVERFLOW;
      } 
      while (i < j) {
        char c = decode(arrayOfByte[i]);
        if (c == '�')
          return SingleByte.withResult(CoderResult.unmappableForLength(1), param1ByteBuffer, i, param1CharBuffer, k); 
        arrayOfChar[k++] = c;
        i++;
      } 
      return SingleByte.withResult(coderResult, param1ByteBuffer, i, param1CharBuffer, k);
    }
    
    private CoderResult decodeBufferLoop(ByteBuffer param1ByteBuffer, CharBuffer param1CharBuffer) {
      i = param1ByteBuffer.position();
      try {
        while (param1ByteBuffer.hasRemaining()) {
          char c = decode(param1ByteBuffer.get());
          if (c == '�')
            return CoderResult.unmappableForLength(1); 
          if (!param1CharBuffer.hasRemaining())
            return CoderResult.OVERFLOW; 
          param1CharBuffer.put(c);
          i++;
        } 
        return CoderResult.UNDERFLOW;
      } finally {
        param1ByteBuffer.position(i);
      } 
    }
    
    protected CoderResult decodeLoop(ByteBuffer param1ByteBuffer, CharBuffer param1CharBuffer) { return (param1ByteBuffer.hasArray() && param1CharBuffer.hasArray()) ? decodeArrayLoop(param1ByteBuffer, param1CharBuffer) : decodeBufferLoop(param1ByteBuffer, param1CharBuffer); }
    
    public final char decode(int param1Int) { return this.b2c[param1Int + 128]; }
    
    protected void implReplaceWith(String param1String) { this.repl = param1String.charAt(0); }
    
    public int decode(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, char[] param1ArrayOfChar) {
      if (param1Int2 > param1ArrayOfChar.length)
        param1Int2 = param1ArrayOfChar.length; 
      byte b;
      for (b = 0; b < param1Int2; b++) {
        param1ArrayOfChar[b] = decode(param1ArrayOfByte[param1Int1++]);
        if (param1ArrayOfChar[b] == '�')
          param1ArrayOfChar[b] = this.repl; 
      } 
      return b;
    }
  }
  
  public static final class Encoder extends CharsetEncoder implements ArrayEncoder {
    private Surrogate.Parser sgp;
    
    private final char[] c2b;
    
    private final char[] c2bIndex;
    
    private byte repl = 63;
    
    public Encoder(Charset param1Charset, char[] param1ArrayOfChar1, char[] param1ArrayOfChar2) {
      super(param1Charset, 1.0F, 1.0F);
      this.c2b = param1ArrayOfChar1;
      this.c2bIndex = param1ArrayOfChar2;
    }
    
    public boolean canEncode(char param1Char) { return (encode(param1Char) != 65533); }
    
    public boolean isLegalReplacement(byte[] param1ArrayOfByte) { return ((param1ArrayOfByte.length == 1 && param1ArrayOfByte[0] == 63) || super.isLegalReplacement(param1ArrayOfByte)); }
    
    private CoderResult encodeArrayLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) {
      char[] arrayOfChar = param1CharBuffer.array();
      int i = param1CharBuffer.arrayOffset() + param1CharBuffer.position();
      int j = param1CharBuffer.arrayOffset() + param1CharBuffer.limit();
      byte[] arrayOfByte = param1ByteBuffer.array();
      int k = param1ByteBuffer.arrayOffset() + param1ByteBuffer.position();
      int m = param1ByteBuffer.arrayOffset() + param1ByteBuffer.limit();
      CoderResult coderResult = CoderResult.UNDERFLOW;
      if (m - k < j - i) {
        j = i + m - k;
        coderResult = CoderResult.OVERFLOW;
      } 
      while (i < j) {
        char c = arrayOfChar[i];
        int n = encode(c);
        if (n == 65533) {
          if (Character.isSurrogate(c)) {
            if (this.sgp == null)
              this.sgp = new Surrogate.Parser(); 
            return (this.sgp.parse(c, arrayOfChar, i, j) < 0) ? SingleByte.withResult(this.sgp.error(), param1CharBuffer, i, param1ByteBuffer, k) : SingleByte.withResult(this.sgp.unmappableResult(), param1CharBuffer, i, param1ByteBuffer, k);
          } 
          return SingleByte.withResult(CoderResult.unmappableForLength(1), param1CharBuffer, i, param1ByteBuffer, k);
        } 
        arrayOfByte[k++] = (byte)n;
        i++;
      } 
      return SingleByte.withResult(coderResult, param1CharBuffer, i, param1ByteBuffer, k);
    }
    
    private CoderResult encodeBufferLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) {
      i = param1CharBuffer.position();
      try {
        while (param1CharBuffer.hasRemaining()) {
          char c = param1CharBuffer.get();
          int j = encode(c);
          if (j == 65533) {
            if (Character.isSurrogate(c)) {
              if (this.sgp == null)
                this.sgp = new Surrogate.Parser(); 
              if (this.sgp.parse(c, param1CharBuffer) < 0)
                return this.sgp.error(); 
              return this.sgp.unmappableResult();
            } 
            return CoderResult.unmappableForLength(1);
          } 
          if (!param1ByteBuffer.hasRemaining())
            return CoderResult.OVERFLOW; 
          param1ByteBuffer.put((byte)j);
          i++;
        } 
        return CoderResult.UNDERFLOW;
      } finally {
        param1CharBuffer.position(i);
      } 
    }
    
    protected CoderResult encodeLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) { return (param1CharBuffer.hasArray() && param1ByteBuffer.hasArray()) ? encodeArrayLoop(param1CharBuffer, param1ByteBuffer) : encodeBufferLoop(param1CharBuffer, param1ByteBuffer); }
    
    public final int encode(char param1Char) {
      char c = this.c2bIndex[param1Char >> '\b'];
      return (c == '�') ? 65533 : this.c2b[c + (param1Char & 0xFF)];
    }
    
    protected void implReplaceWith(byte[] param1ArrayOfByte) { this.repl = param1ArrayOfByte[0]; }
    
    public int encode(char[] param1ArrayOfChar, int param1Int1, int param1Int2, byte[] param1ArrayOfByte) {
      byte b = 0;
      int i = param1Int1 + Math.min(param1Int2, param1ArrayOfByte.length);
      while (param1Int1 < i) {
        char c = param1ArrayOfChar[param1Int1++];
        int j = encode(c);
        if (j != 65533) {
          param1ArrayOfByte[b++] = (byte)j;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\SingleByte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */