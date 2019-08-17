package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import java.io.IOException;
import java.io.Writer;

public class XMLWriter extends Writer {
  private Writer writer;
  
  private int size;
  
  private XMLStringBuffer buffer = new XMLStringBuffer(12288);
  
  private static final int THRESHHOLD_LENGTH = 4096;
  
  private static final boolean DEBUG = false;
  
  public XMLWriter(Writer paramWriter) { this(paramWriter, 4096); }
  
  public XMLWriter(Writer paramWriter, int paramInt) {
    this.writer = paramWriter;
    this.size = paramInt;
  }
  
  public void write(int paramInt) throws IOException {
    ensureOpen();
    this.buffer.append((char)paramInt);
    conditionalWrite();
  }
  
  public void write(char[] paramArrayOfChar) throws IOException { write(paramArrayOfChar, 0, paramArrayOfChar.length); }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (paramInt2 > this.size) {
      writeBufferedData();
      this.writer.write(paramArrayOfChar, paramInt1, paramInt2);
    } else {
      this.buffer.append(paramArrayOfChar, paramInt1, paramInt2);
      conditionalWrite();
    } 
  }
  
  public void write(String paramString, int paramInt1, int paramInt2) throws IOException { write(paramString.toCharArray(), paramInt1, paramInt2); }
  
  public void write(String paramString) throws IOException {
    if (paramString.length() > this.size) {
      writeBufferedData();
      this.writer.write(paramString);
    } else {
      this.buffer.append(paramString);
      conditionalWrite();
    } 
  }
  
  public void close() throws IOException {
    if (this.writer == null)
      return; 
    flush();
    this.writer.close();
    this.writer = null;
  }
  
  public void flush() throws IOException {
    ensureOpen();
    writeBufferedData();
    this.writer.flush();
  }
  
  public void reset() throws IOException {
    this.writer = null;
    this.buffer.clear();
    this.size = 4096;
  }
  
  public void setWriter(Writer paramWriter) {
    this.writer = paramWriter;
    this.buffer.clear();
    this.size = 4096;
  }
  
  public void setWriter(Writer paramWriter, int paramInt) {
    this.writer = paramWriter;
    this.size = paramInt;
  }
  
  protected Writer getWriter() { return this.writer; }
  
  private void conditionalWrite() throws IOException {
    if (this.buffer.length > this.size)
      writeBufferedData(); 
  }
  
  private void writeBufferedData() throws IOException {
    this.writer.write(this.buffer.ch, this.buffer.offset, this.buffer.length);
    this.buffer.clear();
  }
  
  private void ensureOpen() throws IOException {
    if (this.writer == null)
      throw new IOException("Stream closed"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\writers\XMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */