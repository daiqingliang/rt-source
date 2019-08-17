package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

final class SerializerTraceWriter extends Writer implements WriterChain {
  private final Writer m_writer;
  
  private final SerializerTrace m_tracer;
  
  private int buf_length;
  
  private byte[] buf;
  
  private int count;
  
  private void setBufferSize(int paramInt) {
    this.buf = new byte[paramInt + 3];
    this.buf_length = paramInt;
    this.count = 0;
  }
  
  public SerializerTraceWriter(Writer paramWriter, SerializerTrace paramSerializerTrace) {
    this.m_writer = paramWriter;
    this.m_tracer = paramSerializerTrace;
    setBufferSize(1024);
  }
  
  private void flushBuffer() throws IOException {
    if (this.count > 0) {
      char[] arrayOfChar = new char[this.count];
      for (byte b = 0; b < this.count; b++)
        arrayOfChar[b] = (char)this.buf[b]; 
      if (this.m_tracer != null)
        this.m_tracer.fireGenerateEvent(12, arrayOfChar, 0, arrayOfChar.length); 
      this.count = 0;
    } 
  }
  
  public void flush() throws IOException {
    if (this.m_writer != null)
      this.m_writer.flush(); 
    flushBuffer();
  }
  
  public void close() throws IOException {
    if (this.m_writer != null)
      this.m_writer.close(); 
    flushBuffer();
  }
  
  public void write(int paramInt) {
    if (this.m_writer != null)
      this.m_writer.write(paramInt); 
    if (this.count >= this.buf_length)
      flushBuffer(); 
    if (paramInt < 128) {
      this.buf[this.count++] = (byte)paramInt;
    } else if (paramInt < 2048) {
      this.buf[this.count++] = (byte)(192 + (paramInt >> 6));
      this.buf[this.count++] = (byte)(128 + (paramInt & 0x3F));
    } else {
      this.buf[this.count++] = (byte)(224 + (paramInt >> 12));
      this.buf[this.count++] = (byte)(128 + (paramInt >> 6 & 0x3F));
      this.buf[this.count++] = (byte)(128 + (paramInt & 0x3F));
    } 
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    if (this.m_writer != null)
      this.m_writer.write(paramArrayOfChar, paramInt1, paramInt2); 
    int i = (paramInt2 << 1) + paramInt2;
    if (i >= this.buf_length) {
      flushBuffer();
      setBufferSize(2 * i);
    } 
    if (i > this.buf_length - this.count)
      flushBuffer(); 
    int j = paramInt2 + paramInt1;
    for (int k = paramInt1; k < j; k++) {
      char c = paramArrayOfChar[k];
      if (c < '') {
        this.buf[this.count++] = (byte)c;
      } else if (c < 'ࠀ') {
        this.buf[this.count++] = (byte)('À' + (c >> '\006'));
        this.buf[this.count++] = (byte)('' + (c & 0x3F));
      } else {
        this.buf[this.count++] = (byte)('à' + (c >> '\f'));
        this.buf[this.count++] = (byte)('' + (c >> '\006' & 0x3F));
        this.buf[this.count++] = (byte)('' + (c & 0x3F));
      } 
    } 
  }
  
  public void write(String paramString) throws IOException {
    if (this.m_writer != null)
      this.m_writer.write(paramString); 
    int i = paramString.length();
    int j = (i << 1) + i;
    if (j >= this.buf_length) {
      flushBuffer();
      setBufferSize(2 * j);
    } 
    if (j > this.buf_length - this.count)
      flushBuffer(); 
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c < '') {
        this.buf[this.count++] = (byte)c;
      } else if (c < 'ࠀ') {
        this.buf[this.count++] = (byte)('À' + (c >> '\006'));
        this.buf[this.count++] = (byte)('' + (c & 0x3F));
      } else {
        this.buf[this.count++] = (byte)('à' + (c >> '\f'));
        this.buf[this.count++] = (byte)('' + (c >> '\006' & 0x3F));
        this.buf[this.count++] = (byte)('' + (c & 0x3F));
      } 
    } 
  }
  
  public Writer getWriter() { return this.m_writer; }
  
  public OutputStream getOutputStream() {
    OutputStream outputStream = null;
    if (this.m_writer instanceof WriterChain)
      outputStream = ((WriterChain)this.m_writer).getOutputStream(); 
    return outputStream;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\SerializerTraceWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */