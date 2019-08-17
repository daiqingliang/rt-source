package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

final class DataHead {
  DataFile dataFile;
  
  private final MIMEPart part;
  
  boolean readOnce;
  
  private Throwable consumedAt;
  
  DataHead(MIMEPart paramMIMEPart) { this.part = paramMIMEPart; }
  
  void addBody(ByteBuffer paramByteBuffer) {
    synchronized (this) {
      this.inMemory += paramByteBuffer.limit();
    } 
    if (this.tail != null) {
      this.tail = this.tail.createNext(this, paramByteBuffer);
    } else {
      this.head = this.tail = new Chunk(new MemoryData(paramByteBuffer, this.part.msg.config));
    } 
  }
  
  void doneParsing() {}
  
  void moveTo(File paramFile) {
    if (this.dataFile != null) {
      this.dataFile.renameTo(paramFile);
    } else {
      try {
        fileOutputStream = new FileOutputStream(paramFile);
        try {
          InputStream inputStream = readOnce();
          byte[] arrayOfByte = new byte[8192];
          int i;
          while ((i = inputStream.read(arrayOfByte)) != -1)
            fileOutputStream.write(arrayOfByte, 0, i); 
        } finally {
          if (fileOutputStream != null)
            fileOutputStream.close(); 
        } 
      } catch (IOException iOException) {
        throw new MIMEParsingException(iOException);
      } 
    } 
  }
  
  void close() {
    this.head = this.tail = null;
    if (this.dataFile != null)
      this.dataFile.close(); 
  }
  
  public InputStream read() {
    if (this.readOnce)
      throw new IllegalStateException("readOnce() is called before, read() cannot be called later."); 
    while (this.tail == null) {
      if (!this.part.msg.makeProgress())
        throw new IllegalStateException("No such MIME Part: " + this.part); 
    } 
    if (this.head == null)
      throw new IllegalStateException("Already read. Probably readOnce() is called before."); 
    return new ReadMultiStream();
  }
  
  private boolean unconsumed() {
    if (this.consumedAt != null) {
      AssertionError assertionError = new AssertionError("readOnce() is already called before. See the nested exception from where it's called.");
      assertionError.initCause(this.consumedAt);
      throw assertionError;
    } 
    this.consumedAt = (new Exception()).fillInStackTrace();
    return true;
  }
  
  public InputStream readOnce() {
    assert unconsumed();
    if (this.readOnce)
      throw new IllegalStateException("readOnce() is called before. It can only be called once."); 
    this.readOnce = true;
    while (this.tail == null) {
      if (!this.part.msg.makeProgress() && this.tail == null)
        throw new IllegalStateException("No such Part: " + this.part); 
    } 
    ReadOnceStream readOnceStream = new ReadOnceStream();
    this.head = null;
    return readOnceStream;
  }
  
  class ReadMultiStream extends InputStream {
    Chunk current = DataHead.this.head;
    
    int offset;
    
    int len = this.current.data.size();
    
    byte[] buf = this.current.data.read();
    
    boolean closed;
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (!fetch())
        return -1; 
      param1Int2 = Math.min(param1Int2, this.len - this.offset);
      System.arraycopy(this.buf, this.offset, param1ArrayOfByte, param1Int1, param1Int2);
      this.offset += param1Int2;
      return param1Int2;
    }
    
    public int read() throws IOException { return !fetch() ? -1 : (this.buf[this.offset++] & 0xFF); }
    
    void adjustInMemoryUsage() {}
    
    private boolean fetch() {
      if (this.closed)
        throw new IOException("Stream already closed"); 
      if (this.current == null)
        return false; 
      while (this.offset == this.len) {
        while (!this.this$0.part.parsed && this.current.next == null)
          this.this$0.part.msg.makeProgress(); 
        this.current = this.current.next;
        if (this.current == null)
          return false; 
        adjustInMemoryUsage();
        this.offset = 0;
        this.buf = this.current.data.read();
        this.len = this.current.data.size();
      } 
      return true;
    }
    
    public void close() {
      super.close();
      this.current = null;
      this.closed = true;
    }
  }
  
  final class ReadOnceStream extends ReadMultiStream {
    ReadOnceStream() { super(DataHead.this); }
    
    void adjustInMemoryUsage() {
      synchronized (DataHead.this) {
        DataHead.this.inMemory -= this.current.data.size();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\DataHead.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */