package com.sun.media.sound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public final class RIFFWriter extends OutputStream {
  private int chunktype = 0;
  
  private RandomAccessWriter raf;
  
  private final long chunksizepointer;
  
  private final long startpointer;
  
  private RIFFWriter childchunk = null;
  
  private boolean open = true;
  
  private boolean writeoverride = false;
  
  public RIFFWriter(String paramString1, String paramString2) throws IOException { this(new RandomAccessFileWriter(paramString1), paramString2, 0); }
  
  public RIFFWriter(File paramFile, String paramString) throws IOException { this(new RandomAccessFileWriter(paramFile), paramString, 0); }
  
  public RIFFWriter(OutputStream paramOutputStream, String paramString) throws IOException { this(new RandomAccessByteWriter(paramOutputStream), paramString, 0); }
  
  private RIFFWriter(RandomAccessWriter paramRandomAccessWriter, String paramString, int paramInt) throws IOException {
    if (paramInt == 0 && paramRandomAccessWriter.length() != 0L)
      paramRandomAccessWriter.setLength(0L); 
    this.raf = paramRandomAccessWriter;
    if (paramRandomAccessWriter.getPointer() % 2L != 0L)
      paramRandomAccessWriter.write(0); 
    if (paramInt == 0) {
      paramRandomAccessWriter.write("RIFF".getBytes("ascii"));
    } else if (paramInt == 1) {
      paramRandomAccessWriter.write("LIST".getBytes("ascii"));
    } else {
      paramRandomAccessWriter.write((paramString + "    ").substring(0, 4).getBytes("ascii"));
    } 
    this.chunksizepointer = paramRandomAccessWriter.getPointer();
    this.chunktype = 2;
    writeUnsignedInt(0L);
    this.chunktype = paramInt;
    this.startpointer = paramRandomAccessWriter.getPointer();
    if (paramInt != 2)
      paramRandomAccessWriter.write((paramString + "    ").substring(0, 4).getBytes("ascii")); 
  }
  
  public void seek(long paramLong) throws IOException { this.raf.seek(paramLong); }
  
  public long getFilePointer() throws IOException { return this.raf.getPointer(); }
  
  public void setWriteOverride(boolean paramBoolean) { this.writeoverride = paramBoolean; }
  
  public boolean getWriteOverride() { return this.writeoverride; }
  
  public void close() throws IOException {
    if (!this.open)
      return; 
    if (this.childchunk != null) {
      this.childchunk.close();
      this.childchunk = null;
    } 
    int i = this.chunktype;
    long l = this.raf.getPointer();
    this.raf.seek(this.chunksizepointer);
    this.chunktype = 2;
    writeUnsignedInt(l - this.startpointer);
    if (i == 0) {
      this.raf.close();
    } else {
      this.raf.seek(l);
    } 
    this.open = false;
    this.raf = null;
  }
  
  public void write(int paramInt) throws IOException {
    if (!this.writeoverride) {
      if (this.chunktype != 2)
        throw new IllegalArgumentException("Only chunks can write bytes!"); 
      if (this.childchunk != null) {
        this.childchunk.close();
        this.childchunk = null;
      } 
    } 
    this.raf.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (!this.writeoverride) {
      if (this.chunktype != 2)
        throw new IllegalArgumentException("Only chunks can write bytes!"); 
      if (this.childchunk != null) {
        this.childchunk.close();
        this.childchunk = null;
      } 
    } 
    this.raf.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public RIFFWriter writeList(String paramString) throws IOException {
    if (this.chunktype == 2)
      throw new IllegalArgumentException("Only LIST and RIFF can write lists!"); 
    if (this.childchunk != null) {
      this.childchunk.close();
      this.childchunk = null;
    } 
    this.childchunk = new RIFFWriter(this.raf, paramString, 1);
    return this.childchunk;
  }
  
  public RIFFWriter writeChunk(String paramString) throws IOException {
    if (this.chunktype == 2)
      throw new IllegalArgumentException("Only LIST and RIFF can write chunks!"); 
    if (this.childchunk != null) {
      this.childchunk.close();
      this.childchunk = null;
    } 
    this.childchunk = new RIFFWriter(this.raf, paramString, 2);
    return this.childchunk;
  }
  
  public void writeString(String paramString) throws IOException {
    byte[] arrayOfByte = paramString.getBytes();
    write(arrayOfByte);
  }
  
  public void writeString(String paramString, int paramInt) throws IOException {
    byte[] arrayOfByte = paramString.getBytes();
    if (arrayOfByte.length > paramInt) {
      write(arrayOfByte, 0, paramInt);
    } else {
      write(arrayOfByte);
      for (int i = arrayOfByte.length; i < paramInt; i++)
        write(0); 
    } 
  }
  
  public void writeByte(int paramInt) throws IOException { write(paramInt); }
  
  public void writeShort(short paramShort) throws IOException {
    write(paramShort >>> 0 & 0xFF);
    write(paramShort >>> 8 & 0xFF);
  }
  
  public void writeInt(int paramInt) throws IOException {
    write(paramInt >>> 0 & 0xFF);
    write(paramInt >>> 8 & 0xFF);
    write(paramInt >>> 16 & 0xFF);
    write(paramInt >>> 24 & 0xFF);
  }
  
  public void writeLong(long paramLong) throws IOException {
    write((int)(paramLong >>> false) & 0xFF);
    write((int)(paramLong >>> 8) & 0xFF);
    write((int)(paramLong >>> 16) & 0xFF);
    write((int)(paramLong >>> 24) & 0xFF);
    write((int)(paramLong >>> 32) & 0xFF);
    write((int)(paramLong >>> 40) & 0xFF);
    write((int)(paramLong >>> 48) & 0xFF);
    write((int)(paramLong >>> 56) & 0xFF);
  }
  
  public void writeUnsignedByte(int paramInt) throws IOException { writeByte((byte)paramInt); }
  
  public void writeUnsignedShort(int paramInt) throws IOException { writeShort((short)paramInt); }
  
  public void writeUnsignedInt(long paramLong) throws IOException { writeInt((int)paramLong); }
  
  private static class RandomAccessByteWriter implements RandomAccessWriter {
    byte[] buff = new byte[32];
    
    int length = 0;
    
    int pos = 0;
    
    byte[] s;
    
    final OutputStream stream;
    
    RandomAccessByteWriter(OutputStream param1OutputStream) { this.stream = param1OutputStream; }
    
    public void seek(long param1Long) throws IOException { this.pos = (int)param1Long; }
    
    public long getPointer() throws IOException { return this.pos; }
    
    public void close() throws IOException {
      this.stream.write(this.buff, 0, this.length);
      this.stream.close();
    }
    
    public void write(int param1Int) throws IOException {
      if (this.s == null)
        this.s = new byte[1]; 
      this.s[0] = (byte)param1Int;
      write(this.s, 0, 1);
    }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      int i = this.pos + param1Int2;
      if (i > this.length)
        setLength(i); 
      int j = param1Int1 + param1Int2;
      for (int k = param1Int1; k < j; k++)
        this.buff[this.pos++] = param1ArrayOfByte[k]; 
    }
    
    public void write(byte[] param1ArrayOfByte) throws IOException { write(param1ArrayOfByte, 0, param1ArrayOfByte.length); }
    
    public long length() throws IOException { return this.length; }
    
    public void setLength(long param1Long) throws IOException {
      this.length = (int)param1Long;
      if (this.length > this.buff.length) {
        int i = Math.max(this.buff.length << 1, this.length);
        byte[] arrayOfByte = new byte[i];
        System.arraycopy(this.buff, 0, arrayOfByte, 0, this.buff.length);
        this.buff = arrayOfByte;
      } 
    }
  }
  
  private static class RandomAccessFileWriter implements RandomAccessWriter {
    RandomAccessFile raf;
    
    RandomAccessFileWriter(File param1File) throws FileNotFoundException { this.raf = new RandomAccessFile(param1File, "rw"); }
    
    RandomAccessFileWriter(String param1String) throws IOException { this.raf = new RandomAccessFile(param1String, "rw"); }
    
    public void seek(long param1Long) throws IOException { this.raf.seek(param1Long); }
    
    public long getPointer() throws IOException { return this.raf.getFilePointer(); }
    
    public void close() throws IOException { this.raf.close(); }
    
    public void write(int param1Int) throws IOException { this.raf.write(param1Int); }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException { this.raf.write(param1ArrayOfByte, param1Int1, param1Int2); }
    
    public void write(byte[] param1ArrayOfByte) throws IOException { this.raf.write(param1ArrayOfByte); }
    
    public long length() throws IOException { return this.raf.length(); }
    
    public void setLength(long param1Long) throws IOException { this.raf.setLength(param1Long); }
  }
  
  private static interface RandomAccessWriter {
    void seek(long param1Long) throws IOException;
    
    long getPointer() throws IOException;
    
    void close() throws IOException;
    
    void write(int param1Int) throws IOException;
    
    void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException;
    
    void write(byte[] param1ArrayOfByte) throws IOException;
    
    long length() throws IOException;
    
    void setLength(long param1Long) throws IOException;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\RIFFWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */