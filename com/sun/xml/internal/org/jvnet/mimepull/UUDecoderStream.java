package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class UUDecoderStream extends FilterInputStream {
  private String name;
  
  private int mode;
  
  private byte[] buffer = new byte[45];
  
  private int bufsize = 0;
  
  private int index = 0;
  
  private boolean gotPrefix = false;
  
  private boolean gotEnd = false;
  
  private LineInputStream lin;
  
  private boolean ignoreErrors;
  
  private boolean ignoreMissingBeginEnd;
  
  private String readAhead;
  
  public UUDecoderStream(InputStream paramInputStream) {
    super(paramInputStream);
    this.lin = new LineInputStream(paramInputStream);
    this.ignoreErrors = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoreerrors", false);
    this.ignoreMissingBeginEnd = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoremissingbeginend", false);
  }
  
  public UUDecoderStream(InputStream paramInputStream, boolean paramBoolean1, boolean paramBoolean2) {
    super(paramInputStream);
    this.lin = new LineInputStream(paramInputStream);
    this.ignoreErrors = paramBoolean1;
    this.ignoreMissingBeginEnd = paramBoolean2;
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
    if (this.gotPrefix)
      return; 
    this.mode = 438;
    this.name = "encoder.buf";
    while (true) {
      String str = this.lin.readLine();
      if (str == null) {
        if (!this.ignoreMissingBeginEnd)
          throw new DecodingException("UUDecoder: Missing begin"); 
        this.gotPrefix = true;
        this.gotEnd = true;
        break;
      } 
      if (str.regionMatches(false, 0, "begin", 0, 5)) {
        try {
          this.mode = Integer.parseInt(str.substring(6, 9));
        } catch (NumberFormatException numberFormatException) {
          if (!this.ignoreErrors)
            throw new DecodingException("UUDecoder: Error in mode: " + numberFormatException.toString()); 
        } 
        if (str.length() > 10) {
          this.name = str.substring(10);
        } else if (!this.ignoreErrors) {
          throw new DecodingException("UUDecoder: Missing name: " + str);
        } 
        this.gotPrefix = true;
        break;
      } 
      if (this.ignoreMissingBeginEnd && str.length() != 0) {
        char c1 = str.charAt(0);
        c1 = c1 - ' ' & 0x3F;
        char c2 = (c1 * '\b' + '\005') / '\006';
        if (c2 == '\000' || str.length() >= c2 + '\001') {
          this.readAhead = str;
          this.gotPrefix = true;
          break;
        } 
      } 
    } 
  }
  
  private boolean decode() {
    String str;
    if (this.gotEnd)
      return false; 
    this.bufsize = 0;
    char c = Character.MIN_VALUE;
    while (true) {
      if (this.readAhead != null) {
        str = this.readAhead;
        this.readAhead = null;
      } else {
        str = this.lin.readLine();
      } 
      if (str == null) {
        if (!this.ignoreMissingBeginEnd)
          throw new DecodingException("UUDecoder: Missing end at EOF"); 
        this.gotEnd = true;
        return false;
      } 
      if (str.equals("end")) {
        this.gotEnd = true;
        return false;
      } 
      if (str.length() == 0)
        continue; 
      c = str.charAt(0);
      if (c < ' ') {
        if (!this.ignoreErrors)
          throw new DecodingException("UUDecoder: Buffer format error"); 
        continue;
      } 
      c = c - ' ' & 0x3F;
      if (c == '\000') {
        str = this.lin.readLine();
        if ((str == null || !str.equals("end")) && !this.ignoreMissingBeginEnd)
          throw new DecodingException("UUDecoder: Missing End after count 0 line"); 
        this.gotEnd = true;
        return false;
      } 
      char c1 = (c * '\b' + '\005') / '\006';
      if (str.length() < c1 + '\001') {
        if (!this.ignoreErrors)
          throw new DecodingException("UUDecoder: Short buffer error"); 
        continue;
      } 
      break;
    } 
    byte b = 1;
    while (this.bufsize < c) {
      byte b1 = (byte)(str.charAt(b++) - ' ' & 0x3F);
      byte b2 = (byte)(str.charAt(b++) - ' ' & 0x3F);
      this.buffer[this.bufsize++] = (byte)(b1 << 2 & 0xFC | b2 >>> 4 & 0x3);
      if (this.bufsize < c) {
        b1 = b2;
        b2 = (byte)(str.charAt(b++) - ' ' & 0x3F);
        this.buffer[this.bufsize++] = (byte)(b1 << 4 & 0xF0 | b2 >>> 2 & 0xF);
      } 
      if (this.bufsize < c) {
        b1 = b2;
        b2 = (byte)(str.charAt(b++) - ' ' & 0x3F);
        this.buffer[this.bufsize++] = (byte)(b1 << 6 & 0xC0 | b2 & 0x3F);
      } 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\UUDecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */