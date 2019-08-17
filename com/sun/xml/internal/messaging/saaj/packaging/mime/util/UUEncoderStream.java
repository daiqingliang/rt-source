package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UUEncoderStream extends FilterOutputStream {
  private byte[] buffer;
  
  private int bufsize = 0;
  
  private boolean wrotePrefix = false;
  
  protected String name;
  
  protected int mode;
  
  public UUEncoderStream(OutputStream paramOutputStream) { this(paramOutputStream, "encoder.buf", 644); }
  
  public UUEncoderStream(OutputStream paramOutputStream, String paramString) { this(paramOutputStream, paramString, 644); }
  
  public UUEncoderStream(OutputStream paramOutputStream, String paramString, int paramInt) {
    super(paramOutputStream);
    this.name = paramString;
    this.mode = paramInt;
    this.buffer = new byte[45];
  }
  
  public void setNameMode(String paramString, int paramInt) {
    this.name = paramString;
    this.mode = paramInt;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    for (int i = 0; i < paramInt2; i++)
      write(paramArrayOfByte[paramInt1 + i]); 
  }
  
  public void write(byte[] paramArrayOfByte) throws IOException { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void write(int paramInt) throws IOException {
    this.buffer[this.bufsize++] = (byte)paramInt;
    if (this.bufsize == 45) {
      writePrefix();
      encode();
      this.bufsize = 0;
    } 
  }
  
  public void flush() throws IOException {
    if (this.bufsize > 0) {
      writePrefix();
      encode();
    } 
    writeSuffix();
    this.out.flush();
  }
  
  public void close() throws IOException {
    flush();
    this.out.close();
  }
  
  private void writePrefix() throws IOException {
    if (!this.wrotePrefix) {
      PrintStream printStream = new PrintStream(this.out);
      printStream.println("begin " + this.mode + " " + this.name);
      printStream.flush();
      this.wrotePrefix = true;
    } 
  }
  
  private void writeSuffix() throws IOException {
    PrintStream printStream = new PrintStream(this.out);
    printStream.println(" \nend");
    printStream.flush();
  }
  
  private void encode() throws IOException {
    byte b = 0;
    this.out.write((this.bufsize & 0x3F) + 32);
    while (b < this.bufsize) {
      boolean bool2;
      boolean bool1;
      byte b1 = this.buffer[b++];
      if (b < this.bufsize) {
        bool1 = this.buffer[b++];
        if (b < this.bufsize) {
          bool2 = this.buffer[b++];
        } else {
          bool2 = true;
        } 
      } else {
        bool1 = true;
        bool2 = true;
      } 
      byte b2 = b1 >>> 2 & 0x3F;
      byte b3 = b1 << 4 & 0x30 | bool1 >>> 4 & 0xF;
      byte b4 = bool1 << 2 & 0x3C | bool2 >>> 6 & 0x3;
      byte b5 = bool2 & 0x3F;
      this.out.write(b2 + 32);
      this.out.write(b3 + 32);
      this.out.write(b4 + 32);
      this.out.write(b5 + 32);
    } 
    this.out.write(10);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\UUEncoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */