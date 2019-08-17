package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BASE64DecoderStream extends FilterInputStream {
  private byte[] buffer = new byte[3];
  
  private int bufsize = 0;
  
  private int index = 0;
  
  private static final char[] pem_array = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  private static final byte[] pem_convert_array = new byte[256];
  
  private byte[] decode_buffer = new byte[4];
  
  public BASE64DecoderStream(InputStream paramInputStream) { super(paramInputStream); }
  
  public int read() throws IOException {
    if (this.index >= this.bufsize) {
      decode();
      if (this.bufsize == 0)
        return -1; 
      this.index = 0;
    } 
    return this.buffer[this.index++] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i;
    for (i = 0; i < paramInt2; i++) {
      int j;
      if ((j = read()) == -1) {
        if (!i)
          i = -1; 
        break;
      } 
      paramArrayOfByte[paramInt1 + i] = (byte)j;
    } 
    return i;
  }
  
  public boolean markSupported() { return false; }
  
  public int available() throws IOException { return this.in.available() * 3 / 4 + this.bufsize - this.index; }
  
  private void decode() throws IOException {
    this.bufsize = 0;
    byte b = 0;
    while (b < 4) {
      int i = this.in.read();
      if (i == -1) {
        if (!b)
          return; 
        throw new IOException("Error in encoded stream, got " + b);
      } 
      if ((i >= 0 && i < 256 && i == 61) || pem_convert_array[i] != -1)
        this.decode_buffer[b++] = (byte)i; 
    } 
    byte b1 = pem_convert_array[this.decode_buffer[0] & 0xFF];
    byte b2 = pem_convert_array[this.decode_buffer[1] & 0xFF];
    this.buffer[this.bufsize++] = (byte)(b1 << 2 & 0xFC | b2 >>> 4 & 0x3);
    if (this.decode_buffer[2] == 61)
      return; 
    b1 = b2;
    b2 = pem_convert_array[this.decode_buffer[2] & 0xFF];
    this.buffer[this.bufsize++] = (byte)(b1 << 4 & 0xF0 | b2 >>> 2 & 0xF);
    if (this.decode_buffer[3] == 61)
      return; 
    b1 = b2;
    b2 = pem_convert_array[this.decode_buffer[3] & 0xFF];
    this.buffer[this.bufsize++] = (byte)(b1 << 6 & 0xC0 | b2 & 0x3F);
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
      byte b3 = pem_convert_array[paramArrayOfByte[b1++] & 0xFF];
      byte b4 = pem_convert_array[paramArrayOfByte[b1++] & 0xFF];
      arrayOfByte[b2++] = (byte)(b3 << 2 & 0xFC | b4 >>> 4 & 0x3);
      if (paramArrayOfByte[b1] == 61)
        return arrayOfByte; 
      b3 = b4;
      b4 = pem_convert_array[paramArrayOfByte[b1++] & 0xFF];
      arrayOfByte[b2++] = (byte)(b3 << 4 & 0xF0 | b4 >>> 2 & 0xF);
      if (paramArrayOfByte[b1] == 61)
        return arrayOfByte; 
      b3 = b4;
      b4 = pem_convert_array[paramArrayOfByte[b1++] & 0xFF];
      arrayOfByte[b2++] = (byte)(b3 << 6 & 0xC0 | b4 & 0x3F);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\BASE64DecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */