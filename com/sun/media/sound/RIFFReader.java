package com.sun.media.sound;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class RIFFReader extends InputStream {
  private final RIFFReader root;
  
  private long filepointer = 0L;
  
  private final String fourcc;
  
  private String riff_type = null;
  
  private long ckSize = 2147483647L;
  
  private InputStream stream;
  
  private long avail = 2147483647L;
  
  private RIFFReader lastiterator = null;
  
  public RIFFReader(InputStream paramInputStream) throws IOException {
    if (paramInputStream instanceof RIFFReader) {
      this.root = ((RIFFReader)paramInputStream).root;
    } else {
      this.root = this;
    } 
    this.stream = paramInputStream;
    do {
      i = read();
      if (i == -1) {
        this.fourcc = "";
        this.riff_type = null;
        this.avail = 0L;
        return;
      } 
    } while (i == 0);
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)i;
    readFully(arrayOfByte, 1, 3);
    this.fourcc = new String(arrayOfByte, "ascii");
    this.ckSize = readUnsignedInt();
    this.avail = this.ckSize;
    if (getFormat().equals("RIFF") || getFormat().equals("LIST")) {
      if (this.avail > 2147483647L)
        throw new RIFFInvalidDataException("Chunk size too big"); 
      byte[] arrayOfByte1 = new byte[4];
      readFully(arrayOfByte1);
      this.riff_type = new String(arrayOfByte1, "ascii");
    } 
  }
  
  public long getFilePointer() throws IOException { return this.root.filepointer; }
  
  public boolean hasNextChunk() throws IOException {
    if (this.lastiterator != null)
      this.lastiterator.finish(); 
    return (this.avail != 0L);
  }
  
  public RIFFReader nextChunk() throws IOException {
    if (this.lastiterator != null)
      this.lastiterator.finish(); 
    if (this.avail == 0L)
      return null; 
    this.lastiterator = new RIFFReader(this);
    return this.lastiterator;
  }
  
  public String getFormat() { return this.fourcc; }
  
  public String getType() { return this.riff_type; }
  
  public long getSize() throws IOException { return this.ckSize; }
  
  public int read() throws IOException {
    if (this.avail == 0L)
      return -1; 
    int i = this.stream.read();
    if (i == -1) {
      this.avail = 0L;
      return -1;
    } 
    this.avail--;
    this.filepointer++;
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.avail == 0L)
      return -1; 
    if (paramInt2 > this.avail) {
      int j = this.stream.read(paramArrayOfByte, paramInt1, (int)this.avail);
      if (j != -1)
        this.filepointer += j; 
      this.avail = 0L;
      return j;
    } 
    int i = this.stream.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i == -1) {
      this.avail = 0L;
      return -1;
    } 
    this.avail -= i;
    this.filepointer += i;
    return i;
  }
  
  public final void readFully(byte[] paramArrayOfByte) throws IOException { readFully(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public final void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    while (paramInt2 > 0) {
      int i = read(paramArrayOfByte, paramInt1, paramInt2);
      if (i < 0)
        throw new EOFException(); 
      if (i == 0)
        Thread.yield(); 
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong <= 0L || this.avail == 0L)
      return 0L; 
    long l = Math.min(paramLong, this.avail);
    while (l > 0L) {
      long l1 = Math.min(this.stream.skip(l), l);
      if (l1 == 0L) {
        Thread.yield();
        if (this.stream.read() == -1) {
          this.avail = 0L;
          break;
        } 
        l1 = 1L;
      } 
      l -= l1;
      this.avail -= l1;
      this.filepointer += l1;
    } 
    return paramLong - l;
  }
  
  public int available() throws IOException { return (int)this.avail; }
  
  public void finish() throws IOException {
    if (this.avail != 0L)
      skip(this.avail); 
  }
  
  public String readString(int paramInt) throws IOException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = new byte[paramInt];
    } catch (OutOfMemoryError outOfMemoryError) {
      throw new IOException("Length too big", outOfMemoryError);
    } 
    readFully(arrayOfByte);
    for (byte b = 0; b < arrayOfByte.length; b++) {
      if (arrayOfByte[b] == 0)
        return new String(arrayOfByte, 0, b, "ascii"); 
    } 
    return new String(arrayOfByte, "ascii");
  }
  
  public byte readByte() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return (byte)i;
  }
  
  public short readShort() throws IOException {
    int i = read();
    int j = read();
    if (i < 0)
      throw new EOFException(); 
    if (j < 0)
      throw new EOFException(); 
    return (short)(i | j << 8);
  }
  
  public int readInt() throws IOException {
    int i = read();
    int j = read();
    int k = read();
    int m = read();
    if (i < 0)
      throw new EOFException(); 
    if (j < 0)
      throw new EOFException(); 
    if (k < 0)
      throw new EOFException(); 
    if (m < 0)
      throw new EOFException(); 
    return i + (j << 8) | k << 16 | m << 24;
  }
  
  public long readLong() throws IOException {
    long l1 = read();
    long l2 = read();
    long l3 = read();
    long l4 = read();
    long l5 = read();
    long l6 = read();
    long l7 = read();
    long l8 = read();
    if (l1 < 0L)
      throw new EOFException(); 
    if (l2 < 0L)
      throw new EOFException(); 
    if (l3 < 0L)
      throw new EOFException(); 
    if (l4 < 0L)
      throw new EOFException(); 
    if (l5 < 0L)
      throw new EOFException(); 
    if (l6 < 0L)
      throw new EOFException(); 
    if (l7 < 0L)
      throw new EOFException(); 
    if (l8 < 0L)
      throw new EOFException(); 
    return l1 | l2 << 8 | l3 << 16 | l4 << 24 | l5 << 32 | l6 << 40 | l7 << 48 | l8 << 56;
  }
  
  public int readUnsignedByte() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return i;
  }
  
  public int readUnsignedShort() throws IOException {
    int i = read();
    int j = read();
    if (i < 0)
      throw new EOFException(); 
    if (j < 0)
      throw new EOFException(); 
    return i | j << 8;
  }
  
  public long readUnsignedInt() throws IOException {
    long l1 = read();
    long l2 = read();
    long l3 = read();
    long l4 = read();
    if (l1 < 0L)
      throw new EOFException(); 
    if (l2 < 0L)
      throw new EOFException(); 
    if (l3 < 0L)
      throw new EOFException(); 
    if (l4 < 0L)
      throw new EOFException(); 
    return l1 + (l2 << 8) | l3 << 16 | l4 << 24;
  }
  
  public void close() throws IOException {
    finish();
    if (this == this.root)
      this.stream.close(); 
    this.stream = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\RIFFReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */