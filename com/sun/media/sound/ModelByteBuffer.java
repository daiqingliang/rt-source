package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Collection;

public final class ModelByteBuffer {
  private ModelByteBuffer root = this;
  
  private File file;
  
  private long fileoffset;
  
  private byte[] buffer;
  
  private long offset;
  
  private final long len;
  
  private ModelByteBuffer(ModelByteBuffer paramModelByteBuffer, long paramLong1, long paramLong2, boolean paramBoolean) {
    this.root = paramModelByteBuffer.root;
    this.offset = 0L;
    long l = paramModelByteBuffer.len;
    if (paramLong1 < 0L)
      paramLong1 = 0L; 
    if (paramLong1 > l)
      paramLong1 = l; 
    if (paramLong2 < 0L)
      paramLong2 = 0L; 
    if (paramLong2 > l)
      paramLong2 = l; 
    if (paramLong1 > paramLong2)
      paramLong1 = paramLong2; 
    this.offset = paramLong1;
    this.len = paramLong2 - paramLong1;
    if (paramBoolean) {
      this.buffer = this.root.buffer;
      if (this.root.file != null) {
        this.file = this.root.file;
        this.root.fileoffset += arrayOffset();
        this.offset = 0L;
      } else {
        this.offset = arrayOffset();
      } 
      this.root = this;
    } 
  }
  
  public ModelByteBuffer(byte[] paramArrayOfByte) {
    this.buffer = paramArrayOfByte;
    this.offset = 0L;
    this.len = paramArrayOfByte.length;
  }
  
  public ModelByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    this.buffer = paramArrayOfByte;
    this.offset = paramInt1;
    this.len = paramInt2;
  }
  
  public ModelByteBuffer(File paramFile) {
    this.file = paramFile;
    this.fileoffset = 0L;
    this.len = paramFile.length();
  }
  
  public ModelByteBuffer(File paramFile, long paramLong1, long paramLong2) {
    this.file = paramFile;
    this.fileoffset = paramLong1;
    this.len = paramLong2;
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException {
    if (this.root.file != null && this.root.buffer == null) {
      InputStream inputStream = getInputStream();
      byte[] arrayOfByte = new byte[1024];
      int i;
      while ((i = inputStream.read(arrayOfByte)) != -1)
        paramOutputStream.write(arrayOfByte, 0, i); 
    } else {
      paramOutputStream.write(array(), (int)arrayOffset(), (int)capacity());
    } 
  }
  
  public InputStream getInputStream() {
    if (this.root.file != null && this.root.buffer == null)
      try {
        return new RandomFileInputStream();
      } catch (IOException iOException) {
        return null;
      }  
    return new ByteArrayInputStream(array(), (int)arrayOffset(), (int)capacity());
  }
  
  public ModelByteBuffer subbuffer(long paramLong) { return subbuffer(paramLong, capacity()); }
  
  public ModelByteBuffer subbuffer(long paramLong1, long paramLong2) { return subbuffer(paramLong1, paramLong2, false); }
  
  public ModelByteBuffer subbuffer(long paramLong1, long paramLong2, boolean paramBoolean) { return new ModelByteBuffer(this, paramLong1, paramLong2, paramBoolean); }
  
  public byte[] array() { return this.root.buffer; }
  
  public long arrayOffset() { return (this.root != this) ? (this.root.arrayOffset() + this.offset) : this.offset; }
  
  public long capacity() { return this.len; }
  
  public ModelByteBuffer getRoot() { return this.root; }
  
  public File getFile() { return this.file; }
  
  public long getFilePointer() { return this.fileoffset; }
  
  public static void loadAll(Collection<ModelByteBuffer> paramCollection) throws IOException {
    File file1 = null;
    randomAccessFile = null;
    try {
      for (ModelByteBuffer modelByteBuffer : paramCollection) {
        modelByteBuffer = modelByteBuffer.root;
        if (modelByteBuffer.file == null || modelByteBuffer.buffer != null)
          continue; 
        if (file1 == null || !file1.equals(modelByteBuffer.file)) {
          if (randomAccessFile != null) {
            randomAccessFile.close();
            randomAccessFile = null;
          } 
          file1 = modelByteBuffer.file;
          randomAccessFile = new RandomAccessFile(modelByteBuffer.file, "r");
        } 
        randomAccessFile.seek(modelByteBuffer.fileoffset);
        byte[] arrayOfByte = new byte[(int)modelByteBuffer.capacity()];
        int i = 0;
        int j = arrayOfByte.length;
        while (i != j) {
          if (j - i > 65536) {
            randomAccessFile.readFully(arrayOfByte, i, 65536);
            i += 65536;
            continue;
          } 
          randomAccessFile.readFully(arrayOfByte, i, j - i);
          i = j;
        } 
        modelByteBuffer.buffer = arrayOfByte;
        modelByteBuffer.offset = 0L;
      } 
    } finally {
      if (randomAccessFile != null)
        randomAccessFile.close(); 
    } 
  }
  
  public void load() throws IOException {
    if (this.root != this) {
      this.root.load();
      return;
    } 
    if (this.buffer != null)
      return; 
    if (this.file == null)
      throw new IllegalStateException("No file associated with this ByteBuffer!"); 
    DataInputStream dataInputStream = new DataInputStream(getInputStream());
    this.buffer = new byte[(int)capacity()];
    this.offset = 0L;
    dataInputStream.readFully(this.buffer);
    dataInputStream.close();
  }
  
  public void unload() throws IOException {
    if (this.root != this) {
      this.root.unload();
      return;
    } 
    if (this.file == null)
      throw new IllegalStateException("No file associated with this ByteBuffer!"); 
    this.root.buffer = null;
  }
  
  private class RandomFileInputStream extends InputStream {
    private final RandomAccessFile raf;
    
    private long left;
    
    private long mark = 0L;
    
    private long markleft = 0L;
    
    RandomFileInputStream() throws IOException {
      this.raf = new RandomAccessFile(this$0.root.file, "r");
      this.raf.seek(this$0.root.fileoffset + this$0.arrayOffset());
      this.left = this$0.capacity();
    }
    
    public int available() throws IOException { return (this.left > 2147483647L) ? Integer.MAX_VALUE : (int)this.left; }
    
    public void mark(int param1Int) {
      try {
        this.mark = this.raf.getFilePointer();
        this.markleft = this.left;
      } catch (IOException iOException) {}
    }
    
    public boolean markSupported() { return true; }
    
    public void reset() throws IOException {
      this.raf.seek(this.mark);
      this.left = this.markleft;
    }
    
    public long skip(long param1Long) throws IOException {
      if (param1Long < 0L)
        return 0L; 
      if (param1Long > this.left)
        param1Long = this.left; 
      long l = this.raf.getFilePointer();
      this.raf.seek(l + param1Long);
      this.left -= param1Long;
      return param1Long;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (param1Int2 > this.left)
        param1Int2 = (int)this.left; 
      if (this.left == 0L)
        return -1; 
      param1Int2 = this.raf.read(param1ArrayOfByte, param1Int1, param1Int2);
      if (param1Int2 == -1)
        return -1; 
      this.left -= param1Int2;
      return param1Int2;
    }
    
    public int read(byte[] param1ArrayOfByte) throws IOException {
      int i = param1ArrayOfByte.length;
      if (i > this.left)
        i = (int)this.left; 
      if (this.left == 0L)
        return -1; 
      i = this.raf.read(param1ArrayOfByte, 0, i);
      if (i == -1)
        return -1; 
      this.left -= i;
      return i;
    }
    
    public int read() throws IOException {
      if (this.left == 0L)
        return -1; 
      int i = this.raf.read();
      if (i == -1)
        return -1; 
      this.left--;
      return i;
    }
    
    public void close() throws IOException { this.raf.close(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */