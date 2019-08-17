package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadAllStream extends InputStream {
  @NotNull
  private final MemoryStream memStream = new MemoryStream(null);
  
  @NotNull
  private final FileStream fileStream = new FileStream(null);
  
  private boolean readAll;
  
  private boolean closed;
  
  private static final Logger LOGGER = Logger.getLogger(ReadAllStream.class.getName());
  
  public void readAll(InputStream paramInputStream, long paramLong) throws IOException {
    assert !this.readAll;
    this.readAll = true;
    boolean bool = this.memStream.readAll(paramInputStream, paramLong);
    if (!bool)
      this.fileStream.readAll(paramInputStream); 
  }
  
  public int read() throws IOException {
    int i = this.memStream.read();
    if (i == -1)
      i = this.fileStream.read(); 
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = this.memStream.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i == -1)
      i = this.fileStream.read(paramArrayOfByte, paramInt1, paramInt2); 
    return i;
  }
  
  public void close() {
    if (!this.closed) {
      this.memStream.close();
      this.fileStream.close();
      this.closed = true;
    } 
  }
  
  private static class FileStream extends InputStream {
    @Nullable
    private File tempFile;
    
    @Nullable
    private FileInputStream fin;
    
    private FileStream() {}
    
    void readAll(InputStream param1InputStream) throws IOException {
      this.tempFile = File.createTempFile("jaxws", ".bin");
      fileOutputStream = new FileOutputStream(this.tempFile);
      try {
        byte[] arrayOfByte = new byte[8192];
        int i;
        while ((i = param1InputStream.read(arrayOfByte)) != -1)
          fileOutputStream.write(arrayOfByte, 0, i); 
      } finally {
        fileOutputStream.close();
      } 
      this.fin = new FileInputStream(this.tempFile);
    }
    
    public int read() throws IOException { return (this.fin != null) ? this.fin.read() : -1; }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException { return (this.fin != null) ? this.fin.read(param1ArrayOfByte, param1Int1, param1Int2) : -1; }
    
    public void close() {
      if (this.fin != null)
        this.fin.close(); 
      if (this.tempFile != null) {
        boolean bool = this.tempFile.delete();
        if (!bool)
          LOGGER.log(Level.INFO, "File {0} could not be deleted", this.tempFile); 
      } 
    }
  }
  
  private static class MemoryStream extends InputStream {
    private Chunk head;
    
    private Chunk tail;
    
    private int curOff;
    
    private MemoryStream() {}
    
    private void add(byte[] param1ArrayOfByte, int param1Int) {
      if (this.tail != null) {
        this.tail = this.tail.createNext(param1ArrayOfByte, 0, param1Int);
      } else {
        this.head = this.tail = new Chunk(param1ArrayOfByte, 0, param1Int);
      } 
    }
    
    boolean readAll(InputStream param1InputStream, long param1Long) throws IOException {
      long l = 0L;
      do {
        byte[] arrayOfByte = new byte[8192];
        int i = fill(param1InputStream, arrayOfByte);
        l += i;
        if (i != 0)
          add(arrayOfByte, i); 
        if (i != arrayOfByte.length)
          return true; 
      } while (l <= param1Long);
      return false;
    }
    
    private int fill(InputStream param1InputStream, byte[] param1ArrayOfByte) throws IOException {
      int i;
      int j;
      for (j = 0; j < param1ArrayOfByte.length && (i = param1InputStream.read(param1ArrayOfByte, j, param1ArrayOfByte.length - j)) != -1; j += i);
      return j;
    }
    
    public int read() throws IOException { return !fetch() ? -1 : (this.head.buf[this.curOff++] & 0xFF); }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (!fetch())
        return -1; 
      param1Int2 = Math.min(param1Int2, this.head.len - this.curOff - this.head.off);
      System.arraycopy(this.head.buf, this.curOff, param1ArrayOfByte, param1Int1, param1Int2);
      this.curOff += param1Int2;
      return param1Int2;
    }
    
    private boolean fetch() {
      if (this.head == null)
        return false; 
      if (this.curOff == this.head.off + this.head.len) {
        this.head = this.head.next;
        if (this.head == null)
          return false; 
        this.curOff = this.head.off;
      } 
      return true;
    }
    
    private static final class Chunk {
      Chunk next;
      
      final byte[] buf;
      
      final int off;
      
      final int len;
      
      public Chunk(byte[] param2ArrayOfByte, int param2Int1, int param2Int2) {
        this.buf = param2ArrayOfByte;
        this.off = param2Int1;
        this.len = param2Int2;
      }
      
      public Chunk createNext(byte[] param2ArrayOfByte, int param2Int1, int param2Int2) { return this.next = new Chunk(param2ArrayOfByte, param2Int1, param2Int2); }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\ReadAllStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */