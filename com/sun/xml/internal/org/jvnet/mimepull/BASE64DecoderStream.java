package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class BASE64DecoderStream extends FilterInputStream {
  private byte[] buffer = new byte[3];
  
  private int bufsize = 0;
  
  private int index = 0;
  
  private byte[] input_buffer = new byte[8190];
  
  private int input_pos = 0;
  
  private int input_len = 0;
  
  private boolean ignoreErrors = false;
  
  private static final char[] pem_array = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  private static final byte[] pem_convert_array = new byte[256];
  
  public BASE64DecoderStream(InputStream paramInputStream) {
    super(paramInputStream);
    this.ignoreErrors = PropUtil.getBooleanSystemProperty("mail.mime.base64.ignoreerrors", false);
  }
  
  public BASE64DecoderStream(InputStream paramInputStream, boolean paramBoolean) {
    super(paramInputStream);
    this.ignoreErrors = paramBoolean;
  }
  
  public int read() throws IOException {
    if (this.index >= this.bufsize) {
      this.bufsize = decode(this.buffer, 0, this.buffer.length);
      if (this.bufsize <= 0)
        return -1; 
      this.index = 0;
    } 
    return this.buffer[this.index++] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = paramInt1;
    while (this.index < this.bufsize && paramInt2 > 0) {
      paramArrayOfByte[paramInt1++] = this.buffer[this.index++];
      paramInt2--;
    } 
    if (this.index >= this.bufsize)
      this.bufsize = this.index = 0; 
    int j = paramInt2 / 3 * 3;
    if (j > 0) {
      int k = decode(paramArrayOfByte, paramInt1, j);
      paramInt1 += k;
      paramInt2 -= k;
      if (k != j)
        return (paramInt1 == i) ? -1 : (paramInt1 - i); 
    } 
    while (paramInt2 > 0) {
      int k = read();
      if (k == -1)
        break; 
      paramArrayOfByte[paramInt1++] = (byte)k;
      paramInt2--;
    } 
    return (paramInt1 == i) ? -1 : (paramInt1 - i);
  }
  
  public long skip(long paramLong) throws IOException {
    long l;
    for (l = 0L; paramLong-- > 0L && read() >= 0; l++);
    return l;
  }
  
  public boolean markSupported() { return false; }
  
  public int available() throws IOException { return this.in.available() * 3 / 4 + this.bufsize - this.index; }
  
  private int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = paramInt1;
    while (paramInt2 >= 3) {
      byte b = 0;
      int j;
      for (j = 0; b < 4; j |= k) {
        int k = getByte();
        if (k == -1 || k == -2) {
          boolean bool;
          if (k == -1) {
            if (!b)
              return paramInt1 - i; 
            if (!this.ignoreErrors)
              throw new DecodingException("BASE64Decoder: Error in encoded stream: needed 4 valid base64 characters but only got " + b + " before EOF" + recentChars()); 
            bool = true;
          } else {
            if (b < 2 && !this.ignoreErrors)
              throw new DecodingException("BASE64Decoder: Error in encoded stream: needed at least 2 valid base64 characters, but only got " + b + " before padding character (=)" + recentChars()); 
            if (b == 0)
              return paramInt1 - i; 
            bool = false;
          } 
          int m = b - 1;
          if (m == 0)
            m = 1; 
          b++;
          j <<= 6;
          while (b < 4) {
            if (!bool) {
              k = getByte();
              if (k == -1) {
                if (!this.ignoreErrors)
                  throw new DecodingException("BASE64Decoder: Error in encoded stream: hit EOF while looking for padding characters (=)" + recentChars()); 
              } else if (k != -2 && !this.ignoreErrors) {
                throw new DecodingException("BASE64Decoder: Error in encoded stream: found valid base64 character after a padding character (=)" + recentChars());
              } 
            } 
            j <<= 6;
            b++;
          } 
          j >>= 8;
          if (m == 2)
            paramArrayOfByte[paramInt1 + 1] = (byte)(j & 0xFF); 
          j >>= 8;
          paramArrayOfByte[paramInt1] = (byte)(j & 0xFF);
          paramInt1 += m;
          return paramInt1 - i;
        } 
        j <<= 6;
        b++;
      } 
      paramArrayOfByte[paramInt1 + 2] = (byte)(j & 0xFF);
      j >>= 8;
      paramArrayOfByte[paramInt1 + 1] = (byte)(j & 0xFF);
      j >>= 8;
      paramArrayOfByte[paramInt1] = (byte)(j & 0xFF);
      paramInt2 -= 3;
      paramInt1 += 3;
    } 
    return paramInt1 - i;
  }
  
  private int getByte() throws IOException {
    byte b;
    do {
      if (this.input_pos >= this.input_len) {
        try {
          this.input_len = this.in.read(this.input_buffer);
        } catch (EOFException eOFException) {
          return -1;
        } 
        if (this.input_len <= 0)
          return -1; 
        this.input_pos = 0;
      } 
      b = this.input_buffer[this.input_pos++] & 0xFF;
      if (b == 61)
        return -2; 
      b = pem_convert_array[b];
    } while (b == -1);
    return b;
  }
  
  private String recentChars() {
    StringBuilder stringBuilder = new StringBuilder();
    int i = (this.input_pos > 10) ? 10 : this.input_pos;
    if (i > 0) {
      stringBuilder.append(", the ").append(i).append(" most recent characters were: \"");
      for (int j = this.input_pos - i; j < this.input_pos; j++) {
        char c = (char)(this.input_buffer[j] & 0xFF);
        switch (c) {
          case '\r':
            stringBuilder.append("\\r");
            break;
          case '\n':
            stringBuilder.append("\\n");
            break;
          case '\t':
            stringBuilder.append("\\t");
            break;
          default:
            if (c >= ' ' && c < '') {
              stringBuilder.append(c);
              break;
            } 
            stringBuilder.append("\\").append(c);
            break;
        } 
      } 
      stringBuilder.append("\"");
    } 
    return stringBuilder.toString();
  }
  
  public static byte[] decode(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length / 4 * 3;
    if (i == 0)
      return paramArrayOfByte; 
    if (paramArrayOfByte[paramArrayOfByte.length - 1] == 61) {
      i--;
      if (paramArrayOfByte[paramArrayOfByte.length - 2] == 61)
        i--; 
    } 
    byte[] arrayOfByte = new byte[i];
    byte b1 = 0;
    byte b2 = 0;
    for (i = paramArrayOfByte.length; i > 0; i -= 4) {
      byte b3 = 3;
      byte b = pem_convert_array[paramArrayOfByte[b1++] & 0xFF];
      b <<= 6;
      b |= pem_convert_array[paramArrayOfByte[b1++] & 0xFF];
      b <<= 6;
      if (paramArrayOfByte[b1] != 61) {
        b |= pem_convert_array[paramArrayOfByte[b1++] & 0xFF];
      } else {
        b3--;
      } 
      b <<= 6;
      if (paramArrayOfByte[b1] != 61) {
        b |= pem_convert_array[paramArrayOfByte[b1++] & 0xFF];
      } else {
        b3--;
      } 
      if (b3 > 2)
        arrayOfByte[b2 + 2] = (byte)(b & 0xFF); 
      b >>= 8;
      if (b3 > 1)
        arrayOfByte[b2 + true] = (byte)(b & 0xFF); 
      b >>= 8;
      arrayOfByte[b2] = (byte)(b & 0xFF);
      b2 += b3;
    } 
    return arrayOfByte;
  }
  
  static  {
    byte b;
    for (b = 0; b < 'ÿ'; b++)
      pem_convert_array[b] = -1; 
    for (b = 0; b < pem_array.length; b++)
      pem_convert_array[pem_array[b]] = (byte)b; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\BASE64DecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */