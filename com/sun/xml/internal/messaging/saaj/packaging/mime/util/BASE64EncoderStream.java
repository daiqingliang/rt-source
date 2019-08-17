package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BASE64EncoderStream extends FilterOutputStream {
  private byte[] buffer = new byte[3];
  
  private int bufsize = 0;
  
  private int count = 0;
  
  private int bytesPerLine;
  
  private static final char[] pem_array = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  public BASE64EncoderStream(OutputStream paramOutputStream, int paramInt) {
    super(paramOutputStream);
    this.bytesPerLine = paramInt;
  }
  
  public BASE64EncoderStream(OutputStream paramOutputStream) { this(paramOutputStream, 76); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    for (int i = 0; i < paramInt2; i++)
      write(paramArrayOfByte[paramInt1 + i]); 
  }
  
  public void write(byte[] paramArrayOfByte) throws IOException { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void write(int paramInt) throws IOException {
    this.buffer[this.bufsize++] = (byte)paramInt;
    if (this.bufsize == 3) {
      encode();
      this.bufsize = 0;
    } 
  }
  
  public void flush() throws IOException {
    if (this.bufsize > 0) {
      encode();
      this.bufsize = 0;
    } 
    this.out.flush();
  }
  
  public void close() throws IOException {
    flush();
    this.out.close();
  }
  
  private void encode() throws IOException {
    if (this.count + 4 > this.bytesPerLine) {
      this.out.write(13);
      this.out.write(10);
      this.count = 0;
    } 
    if (this.bufsize == 1) {
      byte b = this.buffer[0];
      boolean bool1 = false;
      boolean bool2 = false;
      this.out.write(pem_array[b >>> 2 & 0x3F]);
      this.out.write(pem_array[(b << 4 & 0x30) + (bool1 >>> 4 & 0xF)]);
      this.out.write(61);
      this.out.write(61);
    } else if (this.bufsize == 2) {
      byte b1 = this.buffer[0];
      byte b2 = this.buffer[1];
      boolean bool = false;
      this.out.write(pem_array[b1 >>> 2 & 0x3F]);
      this.out.write(pem_array[(b1 << 4 & 0x30) + (b2 >>> 4 & 0xF)]);
      this.out.write(pem_array[(b2 << 2 & 0x3C) + (bool >>> 6 & 0x3)]);
      this.out.write(61);
    } else {
      byte b1 = this.buffer[0];
      byte b2 = this.buffer[1];
      byte b3 = this.buffer[2];
      this.out.write(pem_array[b1 >>> 2 & 0x3F]);
      this.out.write(pem_array[(b1 << 4 & 0x30) + (b2 >>> 4 & 0xF)]);
      this.out.write(pem_array[(b2 << 2 & 0x3C) + (b3 >>> 6 & 0x3)]);
      this.out.write(pem_array[b3 & 0x3F]);
    } 
    this.count += 4;
  }
  
  public static byte[] encode(byte[] paramArrayOfByte) {
    if (paramArrayOfByte.length == 0)
      return paramArrayOfByte; 
    byte[] arrayOfByte = new byte[(paramArrayOfByte.length + 2) / 3 * 4];
    byte b1 = 0;
    byte b2 = 0;
    for (int i = paramArrayOfByte.length; i > 0; i -= 3) {
      if (i == 1) {
        byte b = paramArrayOfByte[b1++];
        boolean bool1 = false;
        boolean bool2 = false;
        arrayOfByte[b2++] = (byte)pem_array[b >>> 2 & 0x3F];
        arrayOfByte[b2++] = (byte)pem_array[(b << 4 & 0x30) + (bool1 >>> 4 & 0xF)];
        arrayOfByte[b2++] = 61;
        arrayOfByte[b2++] = 61;
      } else if (i == 2) {
        byte b3 = paramArrayOfByte[b1++];
        byte b4 = paramArrayOfByte[b1++];
        boolean bool = false;
        arrayOfByte[b2++] = (byte)pem_array[b3 >>> 2 & 0x3F];
        arrayOfByte[b2++] = (byte)pem_array[(b3 << 4 & 0x30) + (b4 >>> 4 & 0xF)];
        arrayOfByte[b2++] = (byte)pem_array[(b4 << 2 & 0x3C) + (bool >>> 6 & 0x3)];
        arrayOfByte[b2++] = 61;
      } else {
        byte b3 = paramArrayOfByte[b1++];
        byte b4 = paramArrayOfByte[b1++];
        byte b5 = paramArrayOfByte[b1++];
        arrayOfByte[b2++] = (byte)pem_array[b3 >>> 2 & 0x3F];
        arrayOfByte[b2++] = (byte)pem_array[(b3 << 4 & 0x30) + (b4 >>> 4 & 0xF)];
        arrayOfByte[b2++] = (byte)pem_array[(b4 << 2 & 0x3C) + (b5 >>> 6 & 0x3)];
        arrayOfByte[b2++] = (byte)pem_array[b5 & 0x3F];
      } 
    } 
    return arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\BASE64EncoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */