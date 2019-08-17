package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class QPDecoderStream extends FilterInputStream {
  protected byte[] ba = new byte[2];
  
  protected int spaces = 0;
  
  public QPDecoderStream(InputStream paramInputStream) { super(new PushbackInputStream(paramInputStream, 2)); }
  
  public int read() throws IOException {
    if (this.spaces > 0) {
      this.spaces--;
      return 32;
    } 
    int i = this.in.read();
    if (i == 32) {
      while ((i = this.in.read()) == 32)
        this.spaces++; 
      if (i == 13 || i == 10 || i == -1) {
        this.spaces = 0;
      } else {
        ((PushbackInputStream)this.in).unread(i);
        i = 32;
      } 
      return i;
    } 
    if (i == 61) {
      int j = this.in.read();
      if (j == 10)
        return read(); 
      if (j == 13) {
        int k = this.in.read();
        if (k != 10)
          ((PushbackInputStream)this.in).unread(k); 
        return read();
      } 
      if (j == -1)
        return -1; 
      this.ba[0] = (byte)j;
      this.ba[1] = (byte)this.in.read();
      try {
        return ASCIIUtility.parseInt(this.ba, 0, 2, 16);
      } catch (NumberFormatException numberFormatException) {
        ((PushbackInputStream)this.in).unread(this.ba);
        return i;
      } 
    } 
    return i;
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
  
  public int available() throws IOException { return this.in.available(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\QPDecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */