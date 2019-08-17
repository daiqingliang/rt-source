package java.util.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ZipInputStream extends InflaterInputStream implements ZipConstants {
  private ZipEntry entry;
  
  private int flag;
  
  private CRC32 crc = new CRC32();
  
  private long remaining;
  
  private byte[] tmpbuf = new byte[512];
  
  private static final int STORED = 0;
  
  private static final int DEFLATED = 8;
  
  private boolean closed = false;
  
  private boolean entryEOF = false;
  
  private ZipCoder zc;
  
  private byte[] b = new byte[256];
  
  private void ensureOpen() throws IOException {
    if (this.closed)
      throw new IOException("Stream closed"); 
  }
  
  public ZipInputStream(InputStream paramInputStream) { this(paramInputStream, StandardCharsets.UTF_8); }
  
  public ZipInputStream(InputStream paramInputStream, Charset paramCharset) {
    super(new PushbackInputStream(paramInputStream, 512), new Inflater(true), 512);
    if (paramInputStream == null)
      throw new NullPointerException("in is null"); 
    if (paramCharset == null)
      throw new NullPointerException("charset is null"); 
    this.zc = ZipCoder.get(paramCharset);
  }
  
  public ZipEntry getNextEntry() throws IOException {
    ensureOpen();
    if (this.entry != null)
      closeEntry(); 
    this.crc.reset();
    this.inf.reset();
    if ((this.entry = readLOC()) == null)
      return null; 
    if (this.entry.method == 0)
      this.remaining = this.entry.size; 
    this.entryEOF = false;
    return this.entry;
  }
  
  public void closeEntry() throws IOException {
    ensureOpen();
    while (read(this.tmpbuf, 0, this.tmpbuf.length) != -1);
    this.entryEOF = true;
  }
  
  public int available() throws IOException {
    ensureOpen();
    return this.entryEOF ? 0 : 1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    if (this.entry == null)
      return -1; 
    switch (this.entry.method) {
      case 8:
        paramInt2 = super.read(paramArrayOfByte, paramInt1, paramInt2);
        if (paramInt2 == -1) {
          readEnd(this.entry);
          this.entryEOF = true;
          this.entry = null;
        } else {
          this.crc.update(paramArrayOfByte, paramInt1, paramInt2);
        } 
        return paramInt2;
      case 0:
        if (this.remaining <= 0L) {
          this.entryEOF = true;
          this.entry = null;
          return -1;
        } 
        if (paramInt2 > this.remaining)
          paramInt2 = (int)this.remaining; 
        paramInt2 = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
        if (paramInt2 == -1)
          throw new ZipException("unexpected EOF"); 
        this.crc.update(paramArrayOfByte, paramInt1, paramInt2);
        this.remaining -= paramInt2;
        if (this.remaining == 0L && this.entry.crc != this.crc.getValue())
          throw new ZipException("invalid entry CRC (expected 0x" + Long.toHexString(this.entry.crc) + " but got 0x" + Long.toHexString(this.crc.getValue()) + ")"); 
        return paramInt2;
    } 
    throw new ZipException("invalid compression method");
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("negative skip length"); 
    ensureOpen();
    int i = (int)Math.min(paramLong, 2147483647L);
    int j;
    for (j = 0; j < i; j += k) {
      int k = i - j;
      if (k > this.tmpbuf.length)
        k = this.tmpbuf.length; 
      k = read(this.tmpbuf, 0, k);
      if (k == -1) {
        this.entryEOF = true;
        break;
      } 
    } 
    return j;
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      super.close();
      this.closed = true;
    } 
  }
  
  private ZipEntry readLOC() throws IOException {
    try {
      readFully(this.tmpbuf, 0, 30);
    } catch (EOFException eOFException) {
      return null;
    } 
    if (ZipUtils.get32(this.tmpbuf, 0) != 67324752L)
      return null; 
    this.flag = ZipUtils.get16(this.tmpbuf, 6);
    int i = ZipUtils.get16(this.tmpbuf, 26);
    int j = this.b.length;
    if (i > j) {
      do {
        j *= 2;
      } while (i > j);
      this.b = new byte[j];
    } 
    readFully(this.b, 0, i);
    ZipEntry zipEntry = createZipEntry(((this.flag & 0x800) != 0) ? this.zc.toStringUTF8(this.b, i) : this.zc.toString(this.b, i));
    if ((this.flag & true) == 1)
      throw new ZipException("encrypted ZIP entry not supported"); 
    zipEntry.method = ZipUtils.get16(this.tmpbuf, 8);
    zipEntry.xdostime = ZipUtils.get32(this.tmpbuf, 10);
    if ((this.flag & 0x8) == 8) {
      if (zipEntry.method != 8)
        throw new ZipException("only DEFLATED entries can have EXT descriptor"); 
    } else {
      zipEntry.crc = ZipUtils.get32(this.tmpbuf, 14);
      zipEntry.csize = ZipUtils.get32(this.tmpbuf, 18);
      zipEntry.size = ZipUtils.get32(this.tmpbuf, 22);
    } 
    i = ZipUtils.get16(this.tmpbuf, 28);
    if (i > 0) {
      byte[] arrayOfByte = new byte[i];
      readFully(arrayOfByte, 0, i);
      zipEntry.setExtra0(arrayOfByte, (zipEntry.csize == 4294967295L || zipEntry.size == 4294967295L));
    } 
    return zipEntry;
  }
  
  protected ZipEntry createZipEntry(String paramString) { return new ZipEntry(paramString); }
  
  private void readEnd(ZipEntry paramZipEntry) throws IOException {
    int i = this.inf.getRemaining();
    if (i > 0)
      ((PushbackInputStream)this.in).unread(this.buf, this.len - i, i); 
    if ((this.flag & 0x8) == 8)
      if (this.inf.getBytesWritten() > 4294967295L || this.inf.getBytesRead() > 4294967295L) {
        readFully(this.tmpbuf, 0, 24);
        long l = ZipUtils.get32(this.tmpbuf, 0);
        if (l != 134695760L) {
          paramZipEntry.crc = l;
          paramZipEntry.csize = ZipUtils.get64(this.tmpbuf, 4);
          paramZipEntry.size = ZipUtils.get64(this.tmpbuf, 12);
          ((PushbackInputStream)this.in).unread(this.tmpbuf, 19, 4);
        } else {
          paramZipEntry.crc = ZipUtils.get32(this.tmpbuf, 4);
          paramZipEntry.csize = ZipUtils.get64(this.tmpbuf, 8);
          paramZipEntry.size = ZipUtils.get64(this.tmpbuf, 16);
        } 
      } else {
        readFully(this.tmpbuf, 0, 16);
        long l = ZipUtils.get32(this.tmpbuf, 0);
        if (l != 134695760L) {
          paramZipEntry.crc = l;
          paramZipEntry.csize = ZipUtils.get32(this.tmpbuf, 4);
          paramZipEntry.size = ZipUtils.get32(this.tmpbuf, 8);
          ((PushbackInputStream)this.in).unread(this.tmpbuf, 11, 4);
        } else {
          paramZipEntry.crc = ZipUtils.get32(this.tmpbuf, 4);
          paramZipEntry.csize = ZipUtils.get32(this.tmpbuf, 8);
          paramZipEntry.size = ZipUtils.get32(this.tmpbuf, 12);
        } 
      }  
    if (paramZipEntry.size != this.inf.getBytesWritten())
      throw new ZipException("invalid entry size (expected " + paramZipEntry.size + " but got " + this.inf.getBytesWritten() + " bytes)"); 
    if (paramZipEntry.csize != this.inf.getBytesRead())
      throw new ZipException("invalid entry compressed size (expected " + paramZipEntry.csize + " but got " + this.inf.getBytesRead() + " bytes)"); 
    if (paramZipEntry.crc != this.crc.getValue())
      throw new ZipException("invalid entry CRC (expected 0x" + Long.toHexString(paramZipEntry.crc) + " but got 0x" + Long.toHexString(this.crc.getValue()) + ")"); 
  }
  
  private void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    while (paramInt2 > 0) {
      int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i == -1)
        throw new EOFException(); 
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\ZipInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */