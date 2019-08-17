package java.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Base64 {
  public static Encoder getEncoder() { return Encoder.RFC4648; }
  
  public static Encoder getUrlEncoder() { return Encoder.RFC4648_URLSAFE; }
  
  public static Encoder getMimeEncoder() { return Encoder.RFC2045; }
  
  public static Encoder getMimeEncoder(int paramInt, byte[] paramArrayOfByte) {
    Objects.requireNonNull(paramArrayOfByte);
    int[] arrayOfInt = fromBase64;
    for (byte b : paramArrayOfByte) {
      if (arrayOfInt[b & 0xFF] != -1)
        throw new IllegalArgumentException("Illegal base64 line separator character 0x" + Integer.toString(b, 16)); 
    } 
    return (paramInt <= 0) ? Encoder.RFC4648 : new Encoder(false, paramArrayOfByte, paramInt >> 2 << 2, true, null);
  }
  
  public static Decoder getDecoder() { return Decoder.RFC4648; }
  
  public static Decoder getUrlDecoder() { return Decoder.RFC4648_URLSAFE; }
  
  public static Decoder getMimeDecoder() { return Decoder.RFC2045; }
  
  private static class DecInputStream extends InputStream {
    private final InputStream is;
    
    private final boolean isMIME;
    
    private final int[] base64;
    
    private int bits = 0;
    
    private int nextin = 18;
    
    private int nextout = -8;
    
    private boolean eof = false;
    
    private boolean closed = false;
    
    private byte[] sbBuf = new byte[1];
    
    DecInputStream(InputStream param1InputStream, int[] param1ArrayOfInt, boolean param1Boolean) {
      this.is = param1InputStream;
      this.base64 = param1ArrayOfInt;
      this.isMIME = param1Boolean;
    }
    
    public int read() throws IOException { return (read(this.sbBuf, 0, 1) == -1) ? -1 : (this.sbBuf[0] & 0xFF); }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (this.closed)
        throw new IOException("Stream is closed"); 
      if (this.eof && this.nextout < 0)
        return -1; 
      if (param1Int1 < 0 || param1Int2 < 0 || param1Int2 > param1ArrayOfByte.length - param1Int1)
        throw new IndexOutOfBoundsException(); 
      int i = param1Int1;
      if (this.nextout >= 0) {
        do {
          if (param1Int2 == 0)
            return param1Int1 - i; 
          param1ArrayOfByte[param1Int1++] = (byte)(this.bits >> this.nextout);
          param1Int2--;
          this.nextout -= 8;
        } while (this.nextout >= 0);
        this.bits = 0;
      } 
      while (param1Int2 > 0) {
        int j = this.is.read();
        if (j == -1) {
          this.eof = true;
          if (this.nextin != 18) {
            if (this.nextin == 12)
              throw new IOException("Base64 stream has one un-decoded dangling byte."); 
            param1ArrayOfByte[param1Int1++] = (byte)(this.bits >> 16);
            param1Int2--;
            if (this.nextin == 0)
              if (param1Int2 == 0) {
                this.bits >>= 8;
                this.nextout = 0;
              } else {
                param1ArrayOfByte[param1Int1++] = (byte)(this.bits >> 8);
              }  
          } 
          return (param1Int1 == i) ? -1 : (param1Int1 - i);
        } 
        if (j == 61) {
          if (this.nextin == 18 || this.nextin == 12 || (this.nextin == 6 && this.is.read() != 61))
            throw new IOException("Illegal base64 ending sequence:" + this.nextin); 
          param1ArrayOfByte[param1Int1++] = (byte)(this.bits >> 16);
          param1Int2--;
          if (this.nextin == 0)
            if (param1Int2 == 0) {
              this.bits >>= 8;
              this.nextout = 0;
            } else {
              param1ArrayOfByte[param1Int1++] = (byte)(this.bits >> 8);
            }  
          this.eof = true;
          break;
        } 
        if ((j = this.base64[j]) == -1) {
          if (this.isMIME)
            continue; 
          throw new IOException("Illegal base64 character " + Integer.toString(j, 16));
        } 
        this.bits |= j << this.nextin;
        if (this.nextin == 0) {
          this.nextin = 18;
          this.nextout = 16;
          while (this.nextout >= 0) {
            param1ArrayOfByte[param1Int1++] = (byte)(this.bits >> this.nextout);
            param1Int2--;
            this.nextout -= 8;
            if (param1Int2 == 0 && this.nextout >= 0)
              return param1Int1 - i; 
          } 
          this.bits = 0;
          continue;
        } 
        this.nextin -= 6;
      } 
      return param1Int1 - i;
    }
    
    public int available() throws IOException {
      if (this.closed)
        throw new IOException("Stream is closed"); 
      return this.is.available();
    }
    
    public void close() {
      if (!this.closed) {
        this.closed = true;
        this.is.close();
      } 
    }
  }
  
  public static class Decoder {
    private final boolean isURL;
    
    private final boolean isMIME;
    
    private static final int[] fromBase64 = new int[256];
    
    private static final int[] fromBase64URL;
    
    static final Decoder RFC4648;
    
    static final Decoder RFC4648_URLSAFE;
    
    static final Decoder RFC2045;
    
    private Decoder(boolean param1Boolean1, boolean param1Boolean2) {
      this.isURL = param1Boolean1;
      this.isMIME = param1Boolean2;
    }
    
    public byte[] decode(byte[] param1ArrayOfByte) {
      byte[] arrayOfByte = new byte[outLength(param1ArrayOfByte, 0, param1ArrayOfByte.length)];
      int i = decode0(param1ArrayOfByte, 0, param1ArrayOfByte.length, arrayOfByte);
      if (i != arrayOfByte.length)
        arrayOfByte = Arrays.copyOf(arrayOfByte, i); 
      return arrayOfByte;
    }
    
    public byte[] decode(String param1String) { return decode(param1String.getBytes(StandardCharsets.ISO_8859_1)); }
    
    public int decode(byte[] param1ArrayOfByte1, byte[] param1ArrayOfByte2) {
      int i = outLength(param1ArrayOfByte1, 0, param1ArrayOfByte1.length);
      if (param1ArrayOfByte2.length < i)
        throw new IllegalArgumentException("Output byte array is too small for decoding all input bytes"); 
      return decode0(param1ArrayOfByte1, 0, param1ArrayOfByte1.length, param1ArrayOfByte2);
    }
    
    public ByteBuffer decode(ByteBuffer param1ByteBuffer) {
      int i = param1ByteBuffer.position();
      try {
        int j;
        byte b;
        byte[] arrayOfByte1;
        if (param1ByteBuffer.hasArray()) {
          arrayOfByte1 = param1ByteBuffer.array();
          b = param1ByteBuffer.arrayOffset() + param1ByteBuffer.position();
          j = param1ByteBuffer.arrayOffset() + param1ByteBuffer.limit();
          param1ByteBuffer.position(param1ByteBuffer.limit());
        } else {
          arrayOfByte1 = new byte[param1ByteBuffer.remaining()];
          param1ByteBuffer.get(arrayOfByte1);
          b = 0;
          j = arrayOfByte1.length;
        } 
        byte[] arrayOfByte2 = new byte[outLength(arrayOfByte1, b, j)];
        return ByteBuffer.wrap(arrayOfByte2, 0, decode0(arrayOfByte1, b, j, arrayOfByte2));
      } catch (IllegalArgumentException illegalArgumentException) {
        param1ByteBuffer.position(i);
        throw illegalArgumentException;
      } 
    }
    
    public InputStream wrap(InputStream param1InputStream) {
      Objects.requireNonNull(param1InputStream);
      return new Base64.DecInputStream(param1InputStream, this.isURL ? fromBase64URL : fromBase64, this.isMIME);
    }
    
    private int outLength(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      int[] arrayOfInt = this.isURL ? fromBase64URL : fromBase64;
      int i = 0;
      int j = param1Int2 - param1Int1;
      if (j == 0)
        return 0; 
      if (j < 2) {
        if (this.isMIME && arrayOfInt[0] == -1)
          return 0; 
        throw new IllegalArgumentException("Input byte[] should at least have 2 bytes for base64 bytes");
      } 
      if (this.isMIME) {
        int k = 0;
        while (param1Int1 < param1Int2) {
          byte b = param1ArrayOfByte[param1Int1++] & 0xFF;
          if (b == 61) {
            j -= param1Int2 - param1Int1 + 1;
            break;
          } 
          int m;
          if ((m = arrayOfInt[b]) == -1)
            k++; 
        } 
        j -= k;
      } else if (param1ArrayOfByte[param1Int2 - 1] == 61) {
        i++;
        if (param1ArrayOfByte[param1Int2 - 2] == 61)
          i++; 
      } 
      if (i == 0 && (j & 0x3) != 0)
        i = 4 - (j & 0x3); 
      return 3 * (j + 3) / 4 - i;
    }
    
    private int decode0(byte[] param1ArrayOfByte1, int param1Int1, int param1Int2, byte[] param1ArrayOfByte2) {
      int[] arrayOfInt = this.isURL ? fromBase64URL : fromBase64;
      byte b = 0;
      int i = 0;
      int j = 18;
      while (param1Int1 < param1Int2) {
        byte b1 = param1ArrayOfByte1[param1Int1++] & 0xFF;
        int k;
        if ((k = arrayOfInt[b1]) < 0) {
          if (k == -2) {
            if ((j == 6 && (param1Int1 == param1Int2 || param1ArrayOfByte1[param1Int1++] != 61)) || j == 18)
              throw new IllegalArgumentException("Input byte array has wrong 4-byte ending unit"); 
            break;
          } 
          if (this.isMIME)
            continue; 
          throw new IllegalArgumentException("Illegal base64 character " + Integer.toString(param1ArrayOfByte1[param1Int1 - 1], 16));
        } 
        i |= k << j;
        j -= 6;
        if (j < 0) {
          param1ArrayOfByte2[b++] = (byte)(i >> 16);
          param1ArrayOfByte2[b++] = (byte)(i >> 8);
          param1ArrayOfByte2[b++] = (byte)i;
          j = 18;
          i = 0;
        } 
      } 
      if (j == 6) {
        param1ArrayOfByte2[b++] = (byte)(i >> 16);
      } else if (j == 0) {
        param1ArrayOfByte2[b++] = (byte)(i >> 16);
        param1ArrayOfByte2[b++] = (byte)(i >> 8);
      } else if (j == 12) {
        throw new IllegalArgumentException("Last unit does not have enough valid bits");
      } 
      while (param1Int1 < param1Int2) {
        if (this.isMIME && arrayOfInt[param1ArrayOfByte1[param1Int1++]] < 0)
          continue; 
        throw new IllegalArgumentException("Input byte array has incorrect ending byte at " + param1Int1);
      } 
      return b;
    }
    
    static  {
      Arrays.fill(fromBase64, -1);
      byte b;
      for (b = 0; b < toBase64.length; b++)
        fromBase64[toBase64[b]] = b; 
      fromBase64[61] = -2;
      fromBase64URL = new int[256];
      Arrays.fill(fromBase64URL, -1);
      for (b = 0; b < toBase64URL.length; b++)
        fromBase64URL[toBase64URL[b]] = b; 
      fromBase64URL[61] = -2;
      RFC4648 = new Decoder(false, false);
      RFC4648_URLSAFE = new Decoder(true, false);
      RFC2045 = new Decoder(false, true);
    }
  }
  
  private static class EncOutputStream extends FilterOutputStream {
    private int leftover = 0;
    
    private int b0;
    
    private int b1;
    
    private int b2;
    
    private boolean closed = false;
    
    private final char[] base64;
    
    private final byte[] newline;
    
    private final int linemax;
    
    private final boolean doPadding;
    
    private int linepos = 0;
    
    EncOutputStream(OutputStream param1OutputStream, char[] param1ArrayOfChar, byte[] param1ArrayOfByte, int param1Int, boolean param1Boolean) {
      super(param1OutputStream);
      this.base64 = param1ArrayOfChar;
      this.newline = param1ArrayOfByte;
      this.linemax = param1Int;
      this.doPadding = param1Boolean;
    }
    
    public void write(int param1Int) throws IOException {
      byte[] arrayOfByte = new byte[1];
      arrayOfByte[0] = (byte)(param1Int & 0xFF);
      write(arrayOfByte, 0, 1);
    }
    
    private void checkNewline() {
      if (this.linepos == this.linemax) {
        this.out.write(this.newline);
        this.linepos = 0;
      } 
    }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (this.closed)
        throw new IOException("Stream is closed"); 
      if (param1Int1 < 0 || param1Int2 < 0 || param1Int2 > param1ArrayOfByte.length - param1Int1)
        throw new ArrayIndexOutOfBoundsException(); 
      if (param1Int2 == 0)
        return; 
      if (this.leftover != 0) {
        if (this.leftover == 1) {
          this.b1 = param1ArrayOfByte[param1Int1++] & 0xFF;
          if (--param1Int2 == 0) {
            this.leftover++;
            return;
          } 
        } 
        this.b2 = param1ArrayOfByte[param1Int1++] & 0xFF;
        param1Int2--;
        checkNewline();
        this.out.write(this.base64[this.b0 >> 2]);
        this.out.write(this.base64[this.b0 << 4 & 0x3F | this.b1 >> 4]);
        this.out.write(this.base64[this.b1 << 2 & 0x3F | this.b2 >> 6]);
        this.out.write(this.base64[this.b2 & 0x3F]);
        this.linepos += 4;
      } 
      int i = param1Int2 / 3;
      this.leftover = param1Int2 - i * 3;
      while (i-- > 0) {
        checkNewline();
        byte b = (param1ArrayOfByte[param1Int1++] & 0xFF) << 16 | (param1ArrayOfByte[param1Int1++] & 0xFF) << 8 | param1ArrayOfByte[param1Int1++] & 0xFF;
        this.out.write(this.base64[b >>> 18 & 0x3F]);
        this.out.write(this.base64[b >>> 12 & 0x3F]);
        this.out.write(this.base64[b >>> 6 & 0x3F]);
        this.out.write(this.base64[b & 0x3F]);
        this.linepos += 4;
      } 
      if (this.leftover == 1) {
        this.b0 = param1ArrayOfByte[param1Int1++] & 0xFF;
      } else if (this.leftover == 2) {
        this.b0 = param1ArrayOfByte[param1Int1++] & 0xFF;
        this.b1 = param1ArrayOfByte[param1Int1++] & 0xFF;
      } 
    }
    
    public void close() {
      if (!this.closed) {
        this.closed = true;
        if (this.leftover == 1) {
          checkNewline();
          this.out.write(this.base64[this.b0 >> 2]);
          this.out.write(this.base64[this.b0 << 4 & 0x3F]);
          if (this.doPadding) {
            this.out.write(61);
            this.out.write(61);
          } 
        } else if (this.leftover == 2) {
          checkNewline();
          this.out.write(this.base64[this.b0 >> 2]);
          this.out.write(this.base64[this.b0 << 4 & 0x3F | this.b1 >> 4]);
          this.out.write(this.base64[this.b1 << 2 & 0x3F]);
          if (this.doPadding)
            this.out.write(61); 
        } 
        this.leftover = 0;
        this.out.close();
      } 
    }
  }
  
  public static class Encoder {
    private final byte[] newline;
    
    private final int linemax;
    
    private final boolean isURL;
    
    private final boolean doPadding;
    
    private static final char[] toBase64 = { 
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
        '8', '9', '+', '/' };
    
    private static final char[] toBase64URL = { 
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
        '8', '9', '-', '_' };
    
    private static final int MIMELINEMAX = 76;
    
    private static final byte[] CRLF = { 13, 10 };
    
    static final Encoder RFC4648 = new Encoder(false, null, -1, true);
    
    static final Encoder RFC4648_URLSAFE = new Encoder(true, null, -1, true);
    
    static final Encoder RFC2045 = new Encoder(false, CRLF, 76, true);
    
    private Encoder(boolean param1Boolean1, byte[] param1ArrayOfByte, int param1Int, boolean param1Boolean2) {
      this.isURL = param1Boolean1;
      this.newline = param1ArrayOfByte;
      this.linemax = param1Int;
      this.doPadding = param1Boolean2;
    }
    
    private final int outLength(int param1Int) {
      int i = 0;
      if (this.doPadding) {
        i = 4 * (param1Int + 2) / 3;
      } else {
        int j = param1Int % 3;
        i = 4 * param1Int / 3 + ((j == 0) ? 0 : (j + 1));
      } 
      if (this.linemax > 0)
        i += (i - 1) / this.linemax * this.newline.length; 
      return i;
    }
    
    public byte[] encode(byte[] param1ArrayOfByte) {
      int i = outLength(param1ArrayOfByte.length);
      byte[] arrayOfByte = new byte[i];
      int j = encode0(param1ArrayOfByte, 0, param1ArrayOfByte.length, arrayOfByte);
      return (j != arrayOfByte.length) ? Arrays.copyOf(arrayOfByte, j) : arrayOfByte;
    }
    
    public int encode(byte[] param1ArrayOfByte1, byte[] param1ArrayOfByte2) {
      int i = outLength(param1ArrayOfByte1.length);
      if (param1ArrayOfByte2.length < i)
        throw new IllegalArgumentException("Output byte array is too small for encoding all input bytes"); 
      return encode0(param1ArrayOfByte1, 0, param1ArrayOfByte1.length, param1ArrayOfByte2);
    }
    
    public String encodeToString(byte[] param1ArrayOfByte) {
      byte[] arrayOfByte = encode(param1ArrayOfByte);
      return new String(arrayOfByte, 0, 0, arrayOfByte.length);
    }
    
    public ByteBuffer encode(ByteBuffer param1ByteBuffer) {
      int i = outLength(param1ByteBuffer.remaining());
      byte[] arrayOfByte = new byte[i];
      int j = 0;
      if (param1ByteBuffer.hasArray()) {
        j = encode0(param1ByteBuffer.array(), param1ByteBuffer.arrayOffset() + param1ByteBuffer.position(), param1ByteBuffer.arrayOffset() + param1ByteBuffer.limit(), arrayOfByte);
        param1ByteBuffer.position(param1ByteBuffer.limit());
      } else {
        byte[] arrayOfByte1 = new byte[param1ByteBuffer.remaining()];
        param1ByteBuffer.get(arrayOfByte1);
        j = encode0(arrayOfByte1, 0, arrayOfByte1.length, arrayOfByte);
      } 
      if (j != arrayOfByte.length)
        arrayOfByte = Arrays.copyOf(arrayOfByte, j); 
      return ByteBuffer.wrap(arrayOfByte);
    }
    
    public OutputStream wrap(OutputStream param1OutputStream) {
      Objects.requireNonNull(param1OutputStream);
      return new Base64.EncOutputStream(param1OutputStream, this.isURL ? toBase64URL : toBase64, this.newline, this.linemax, this.doPadding);
    }
    
    public Encoder withoutPadding() { return !this.doPadding ? this : new Encoder(this.isURL, this.newline, this.linemax, false); }
    
    private int encode0(byte[] param1ArrayOfByte1, int param1Int1, int param1Int2, byte[] param1ArrayOfByte2) {
      char[] arrayOfChar = this.isURL ? toBase64URL : toBase64;
      int i = param1Int1;
      int j = (param1Int2 - param1Int1) / 3 * 3;
      int k = param1Int1 + j;
      if (this.linemax > 0 && j > this.linemax / 4 * 3)
        j = this.linemax / 4 * 3; 
      int m = 0;
      while (i < k) {
        int n = Math.min(i + j, k);
        int i1 = i;
        byte b = m;
        while (i1 < n) {
          byte b1 = (param1ArrayOfByte1[i1++] & 0xFF) << 16 | (param1ArrayOfByte1[i1++] & 0xFF) << 8 | param1ArrayOfByte1[i1++] & 0xFF;
          param1ArrayOfByte2[b++] = (byte)arrayOfChar[b1 >>> 18 & 0x3F];
          param1ArrayOfByte2[b++] = (byte)arrayOfChar[b1 >>> 12 & 0x3F];
          param1ArrayOfByte2[b++] = (byte)arrayOfChar[b1 >>> 6 & 0x3F];
          param1ArrayOfByte2[b++] = (byte)arrayOfChar[b1 & 0x3F];
        } 
        i1 = (n - i) / 3 * 4;
        m += i1;
        i = n;
        if (i1 == this.linemax && i < param1Int2)
          for (byte b1 : this.newline)
            param1ArrayOfByte2[m++] = b1;  
      } 
      if (i < param1Int2) {
        byte b = param1ArrayOfByte1[i++] & 0xFF;
        param1ArrayOfByte2[m++] = (byte)arrayOfChar[b >> 2];
        if (i == param1Int2) {
          param1ArrayOfByte2[m++] = (byte)arrayOfChar[b << 4 & 0x3F];
          if (this.doPadding) {
            param1ArrayOfByte2[m++] = 61;
            param1ArrayOfByte2[m++] = 61;
          } 
        } else {
          byte b1 = param1ArrayOfByte1[i++] & 0xFF;
          param1ArrayOfByte2[m++] = (byte)arrayOfChar[b << 4 & 0x3F | b1 >> 4];
          param1ArrayOfByte2[m++] = (byte)arrayOfChar[b1 << 2 & 0x3F];
          if (this.doPadding)
            param1ArrayOfByte2[m++] = 61; 
        } 
      } 
      return m;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */