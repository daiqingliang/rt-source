package com.sun.xml.internal.org.jvnet.staxex;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Base64EncoderStream extends FilterOutputStream {
  private byte[] buffer = new byte[3];
  
  private int bufsize = 0;
  
  private XMLStreamWriter outWriter;
  
  private static final char[] pem_array = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  public Base64EncoderStream(OutputStream paramOutputStream) { super(paramOutputStream); }
  
  public Base64EncoderStream(XMLStreamWriter paramXMLStreamWriter, OutputStream paramOutputStream) {
    super(paramOutputStream);
    this.outWriter = paramXMLStreamWriter;
  }
  
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
    try {
      this.outWriter.flush();
    } catch (XMLStreamException xMLStreamException) {
      Logger.getLogger(Base64EncoderStream.class.getName()).log(Level.SEVERE, null, xMLStreamException);
      throw new IOException(xMLStreamException);
    } 
  }
  
  public void close() throws IOException {
    flush();
    this.out.close();
  }
  
  private void encode() throws IOException {
    char[] arrayOfChar = new char[4];
    if (this.bufsize == 1) {
      byte b = this.buffer[0];
      boolean bool1 = false;
      boolean bool2 = false;
      arrayOfChar[0] = pem_array[b >>> 2 & 0x3F];
      arrayOfChar[1] = pem_array[(b << 4 & 0x30) + (bool1 >>> 4 & 0xF)];
      arrayOfChar[2] = '=';
      arrayOfChar[3] = '=';
    } else if (this.bufsize == 2) {
      byte b1 = this.buffer[0];
      byte b2 = this.buffer[1];
      boolean bool = false;
      arrayOfChar[0] = pem_array[b1 >>> 2 & 0x3F];
      arrayOfChar[1] = pem_array[(b1 << 4 & 0x30) + (b2 >>> 4 & 0xF)];
      arrayOfChar[2] = pem_array[(b2 << 2 & 0x3C) + (bool >>> 6 & 0x3)];
      arrayOfChar[3] = '=';
    } else {
      byte b1 = this.buffer[0];
      byte b2 = this.buffer[1];
      byte b3 = this.buffer[2];
      arrayOfChar[0] = pem_array[b1 >>> 2 & 0x3F];
      arrayOfChar[1] = pem_array[(b1 << 4 & 0x30) + (b2 >>> 4 & 0xF)];
      arrayOfChar[2] = pem_array[(b2 << 2 & 0x3C) + (b3 >>> 6 & 0x3)];
      arrayOfChar[3] = pem_array[b3 & 0x3F];
    } 
    try {
      this.outWriter.writeCharacters(arrayOfChar, 0, 4);
    } catch (XMLStreamException xMLStreamException) {
      Logger.getLogger(Base64EncoderStream.class.getName()).log(Level.SEVERE, null, xMLStreamException);
      throw new IOException(xMLStreamException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\staxex\Base64EncoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */