package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;

final class ChunkInputStream extends InputStream {
  Chunk current;
  
  int offset;
  
  int len;
  
  final MIMEMessage msg;
  
  final MIMEPart part;
  
  byte[] buf;
  
  public ChunkInputStream(MIMEMessage paramMIMEMessage, MIMEPart paramMIMEPart, Chunk paramChunk) {
    this.current = paramChunk;
    this.len = this.current.data.size();
    this.buf = this.current.data.read();
    this.msg = paramMIMEMessage;
    this.part = paramMIMEPart;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (!fetch())
      return -1; 
    paramInt2 = Math.min(paramInt2, this.len - this.offset);
    System.arraycopy(this.buf, this.offset, paramArrayOfByte, paramInt1, paramInt2);
    return paramInt2;
  }
  
  public int read() throws IOException { return !fetch() ? -1 : (this.buf[this.offset++] & 0xFF); }
  
  private boolean fetch() {
    if (this.current == null)
      throw new IllegalStateException("Stream already closed"); 
    while (this.offset == this.len) {
      while (!this.part.parsed && this.current.next == null)
        this.msg.makeProgress(); 
      this.current = this.current.next;
      if (this.current == null)
        return false; 
      this.offset = 0;
      this.buf = this.current.data.read();
      this.len = this.current.data.size();
    } 
    return true;
  }
  
  public void close() throws IOException {
    super.close();
    this.current = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\ChunkInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */