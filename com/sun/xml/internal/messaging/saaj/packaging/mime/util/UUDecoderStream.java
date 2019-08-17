package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UUDecoderStream extends FilterInputStream {
  private String name;
  
  private int mode;
  
  private byte[] buffer;
  
  private int bufsize = 0;
  
  private int index = 0;
  
  private boolean gotPrefix = false;
  
  private boolean gotEnd = false;
  
  private LineInputStream lin;
  
  public UUDecoderStream(InputStream paramInputStream) {
    super(paramInputStream);
    this.lin = new LineInputStream(paramInputStream);
    this.buffer = new byte[45];
  }
  
  public int read() throws IOException {
    if (this.index >= this.bufsize) {
      readPrefix();
      if (!decode())
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
  
  public String getName() throws IOException {
    readPrefix();
    return this.name;
  }
  
  public int getMode() throws IOException {
    readPrefix();
    return this.mode;
  }
  
  private void readPrefix() throws IOException {
    String str;
    if (this.gotPrefix)
      return; 
    do {
      str = this.lin.readLine();
      if (str == null)
        throw new IOException("UUDecoder error: No Begin"); 
    } while (!str.regionMatches(true, 0, "begin", 0, 5));
    try {
      this.mode = Integer.parseInt(str.substring(6, 9));
    } catch (NumberFormatException numberFormatException) {
      throw new IOException("UUDecoder error: " + numberFormatException.toString());
    } 
    this.name = str.substring(10);
    this.gotPrefix = true;
  }
  
  private boolean decode() {
    String str;
    if (this.gotEnd)
      return false; 
    this.bufsize = 0;
    do {
      str = this.lin.readLine();
      if (str == null)
        throw new IOException("Missing End"); 
      if (str.regionMatches(true, 0, "end", 0, 3)) {
        this.gotEnd = true;
        return false;
      } 
    } while (str.length() == 0);
    char c1 = str.charAt(0);
    if (c1 < ' ')
      throw new IOException("Buffer format error"); 
    c1 = c1 - ' ' & 0x3F;
    if (c1 == '\000') {
      str = this.lin.readLine();
      if (str == null || !str.regionMatches(true, 0, "end", 0, 3))
        throw new IOException("Missing End"); 
      this.gotEnd = true;
      return false;
    } 
    char c2 = (c1 * '\b' + '\005') / '\006';
    if (str.length() < c2 + '\001')
      throw new IOException("Short buffer error"); 
    byte b = 1;
    while (this.bufsize < c1) {
      byte b1 = (byte)(str.charAt(b++) - ' ' & 0x3F);
      byte b2 = (byte)(str.charAt(b++) - ' ' & 0x3F);
      this.buffer[this.bufsize++] = (byte)(b1 << 2 & 0xFC | b2 >>> 4 & 0x3);
      if (this.bufsize < c1) {
        b1 = b2;
        b2 = (byte)(str.charAt(b++) - ' ' & 0x3F);
        this.buffer[this.bufsize++] = (byte)(b1 << 4 & 0xF0 | b2 >>> 2 & 0xF);
      } 
      if (this.bufsize < c1) {
        b1 = b2;
        b2 = (byte)(str.charAt(b++) - ' ' & 0x3F);
        this.buffer[this.bufsize++] = (byte)(b1 << 6 & 0xC0 | b2 & 0x3F);
      } 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\UUDecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */