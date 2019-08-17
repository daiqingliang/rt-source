package java.util.zip;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Vector;
import sun.security.action.GetPropertyAction;

public class ZipOutputStream extends DeflaterOutputStream implements ZipConstants {
  private static final boolean inhibitZip64 = Boolean.parseBoolean((String)AccessController.doPrivileged(new GetPropertyAction("jdk.util.zip.inhibitZip64", "false")));
  
  private XEntry current;
  
  private Vector<XEntry> xentries = new Vector();
  
  private HashSet<String> names = new HashSet();
  
  private CRC32 crc = new CRC32();
  
  private long written = 0L;
  
  private long locoff = 0L;
  
  private byte[] comment;
  
  private int method = 8;
  
  private boolean finished;
  
  private boolean closed = false;
  
  private final ZipCoder zc;
  
  public static final int STORED = 0;
  
  public static final int DEFLATED = 8;
  
  private static int version(ZipEntry paramZipEntry) throws ZipException {
    switch (paramZipEntry.method) {
      case 8:
        return 20;
      case 0:
        return 10;
    } 
    throw new ZipException("unsupported compression method");
  }
  
  private void ensureOpen() throws IOException {
    if (this.closed)
      throw new IOException("Stream closed"); 
  }
  
  public ZipOutputStream(OutputStream paramOutputStream) { this(paramOutputStream, StandardCharsets.UTF_8); }
  
  public ZipOutputStream(OutputStream paramOutputStream, Charset paramCharset) {
    super(paramOutputStream, new Deflater(-1, true));
    if (paramCharset == null)
      throw new NullPointerException("charset is null"); 
    this.zc = ZipCoder.get(paramCharset);
    this.usesDefaultDeflater = true;
  }
  
  public void setComment(String paramString) {
    if (paramString != null) {
      this.comment = this.zc.getBytes(paramString);
      if (this.comment.length > 65535)
        throw new IllegalArgumentException("ZIP file comment too long."); 
    } 
  }
  
  public void setMethod(int paramInt) {
    if (paramInt != 8 && paramInt != 0)
      throw new IllegalArgumentException("invalid compression method"); 
    this.method = paramInt;
  }
  
  public void setLevel(int paramInt) { this.def.setLevel(paramInt); }
  
  public void putNextEntry(ZipEntry paramZipEntry) throws IOException {
    ensureOpen();
    if (this.current != null)
      closeEntry(); 
    if (paramZipEntry.xdostime == -1L)
      paramZipEntry.setTime(System.currentTimeMillis()); 
    if (paramZipEntry.method == -1)
      paramZipEntry.method = this.method; 
    paramZipEntry.flag = 0;
    switch (paramZipEntry.method) {
      case 8:
        if (paramZipEntry.size == -1L || paramZipEntry.csize == -1L || paramZipEntry.crc == -1L)
          paramZipEntry.flag = 8; 
        break;
      case 0:
        if (paramZipEntry.size == -1L) {
          paramZipEntry.size = paramZipEntry.csize;
        } else if (paramZipEntry.csize == -1L) {
          paramZipEntry.csize = paramZipEntry.size;
        } else if (paramZipEntry.size != paramZipEntry.csize) {
          throw new ZipException("STORED entry where compressed != uncompressed size");
        } 
        if (paramZipEntry.size == -1L || paramZipEntry.crc == -1L)
          throw new ZipException("STORED entry missing size, compressed size, or crc-32"); 
        break;
      default:
        throw new ZipException("unsupported compression method");
    } 
    if (!this.names.add(paramZipEntry.name))
      throw new ZipException("duplicate entry: " + paramZipEntry.name); 
    if (this.zc.isUTF8())
      paramZipEntry.flag |= 0x800; 
    this.current = new XEntry(paramZipEntry, this.written);
    this.xentries.add(this.current);
    writeLOC(this.current);
  }
  
  public void closeEntry() throws IOException {
    ensureOpen();
    if (this.current != null) {
      ZipEntry zipEntry = this.current.entry;
      switch (zipEntry.method) {
        case 8:
          this.def.finish();
          while (!this.def.finished())
            deflate(); 
          if ((zipEntry.flag & 0x8) == 0) {
            if (zipEntry.size != this.def.getBytesRead())
              throw new ZipException("invalid entry size (expected " + zipEntry.size + " but got " + this.def.getBytesRead() + " bytes)"); 
            if (zipEntry.csize != this.def.getBytesWritten())
              throw new ZipException("invalid entry compressed size (expected " + zipEntry.csize + " but got " + this.def.getBytesWritten() + " bytes)"); 
            if (zipEntry.crc != this.crc.getValue())
              throw new ZipException("invalid entry CRC-32 (expected 0x" + Long.toHexString(zipEntry.crc) + " but got 0x" + Long.toHexString(this.crc.getValue()) + ")"); 
          } else {
            zipEntry.size = this.def.getBytesRead();
            zipEntry.csize = this.def.getBytesWritten();
            zipEntry.crc = this.crc.getValue();
            writeEXT(zipEntry);
          } 
          this.def.reset();
          this.written += zipEntry.csize;
          break;
        case 0:
          if (zipEntry.size != this.written - this.locoff)
            throw new ZipException("invalid entry size (expected " + zipEntry.size + " but got " + (this.written - this.locoff) + " bytes)"); 
          if (zipEntry.crc != this.crc.getValue())
            throw new ZipException("invalid entry crc-32 (expected 0x" + Long.toHexString(zipEntry.crc) + " but got 0x" + Long.toHexString(this.crc.getValue()) + ")"); 
          break;
        default:
          throw new ZipException("invalid compression method");
      } 
      this.crc.reset();
      this.current = null;
    } 
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return; 
    if (this.current == null)
      throw new ZipException("no current ZIP entry"); 
    ZipEntry zipEntry = this.current.entry;
    switch (zipEntry.method) {
      case 8:
        super.write(paramArrayOfByte, paramInt1, paramInt2);
        break;
      case 0:
        this.written += paramInt2;
        if (this.written - this.locoff > zipEntry.size)
          throw new ZipException("attempt to write past end of STORED entry"); 
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
        break;
      default:
        throw new ZipException("invalid compression method");
    } 
    this.crc.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void finish() throws IOException {
    ensureOpen();
    if (this.finished)
      return; 
    if (this.current != null)
      closeEntry(); 
    long l = this.written;
    for (XEntry xEntry : this.xentries)
      writeCEN(xEntry); 
    writeEND(l, this.written - l);
    this.finished = true;
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      super.close();
      this.closed = true;
    } 
  }
  
  private void writeLOC(XEntry paramXEntry) throws IOException {
    ZipEntry zipEntry = paramXEntry.entry;
    int i = zipEntry.flag;
    boolean bool1 = false;
    int j = getExtraLen(zipEntry.extra);
    writeInt(67324752L);
    if ((i & 0x8) == 8) {
      writeShort(version(zipEntry));
      writeShort(i);
      writeShort(zipEntry.method);
      writeInt(zipEntry.xdostime);
      writeInt(0L);
      writeInt(0L);
      writeInt(0L);
    } else {
      if (zipEntry.csize >= 4294967295L || zipEntry.size >= 4294967295L) {
        bool1 = true;
        writeShort(45);
      } else {
        writeShort(version(zipEntry));
      } 
      writeShort(i);
      writeShort(zipEntry.method);
      writeInt(zipEntry.xdostime);
      writeInt(zipEntry.crc);
      if (bool1) {
        writeInt(4294967295L);
        writeInt(4294967295L);
        j += 20;
      } else {
        writeInt(zipEntry.csize);
        writeInt(zipEntry.size);
      } 
    } 
    byte[] arrayOfByte = this.zc.getBytes(zipEntry.name);
    writeShort(arrayOfByte.length);
    boolean bool2 = false;
    byte b = 0;
    if (zipEntry.mtime != null) {
      bool2 += true;
      b |= true;
    } 
    if (zipEntry.atime != null) {
      bool2 += true;
      b |= 0x2;
    } 
    if (zipEntry.ctime != null) {
      bool2 += true;
      b |= 0x4;
    } 
    if (b != 0)
      j += bool2 + 5; 
    writeShort(j);
    writeBytes(arrayOfByte, 0, arrayOfByte.length);
    if (bool1) {
      writeShort(1);
      writeShort(16);
      writeLong(zipEntry.size);
      writeLong(zipEntry.csize);
    } 
    if (b != 0) {
      writeShort(21589);
      writeShort(bool2 + true);
      writeByte(b);
      if (zipEntry.mtime != null)
        writeInt(ZipUtils.fileTimeToUnixTime(zipEntry.mtime)); 
      if (zipEntry.atime != null)
        writeInt(ZipUtils.fileTimeToUnixTime(zipEntry.atime)); 
      if (zipEntry.ctime != null)
        writeInt(ZipUtils.fileTimeToUnixTime(zipEntry.ctime)); 
    } 
    writeExtra(zipEntry.extra);
    this.locoff = this.written;
  }
  
  private void writeEXT(ZipEntry paramZipEntry) throws IOException {
    writeInt(134695760L);
    writeInt(paramZipEntry.crc);
    if (paramZipEntry.csize >= 4294967295L || paramZipEntry.size >= 4294967295L) {
      writeLong(paramZipEntry.csize);
      writeLong(paramZipEntry.size);
    } else {
      writeInt(paramZipEntry.csize);
      writeInt(paramZipEntry.size);
    } 
  }
  
  private void writeCEN(XEntry paramXEntry) throws IOException {
    byte[] arrayOfByte2;
    ZipEntry zipEntry = paramXEntry.entry;
    int i = zipEntry.flag;
    int j = version(zipEntry);
    long l1 = zipEntry.csize;
    long l2 = zipEntry.size;
    long l3 = paramXEntry.offset;
    byte b1 = 0;
    boolean bool = false;
    if (zipEntry.csize >= 4294967295L) {
      l1 = 4294967295L;
      b1 += true;
      bool = true;
    } 
    if (zipEntry.size >= 4294967295L) {
      l2 = 4294967295L;
      b1 += true;
      bool = true;
    } 
    if (paramXEntry.offset >= 4294967295L) {
      l3 = 4294967295L;
      b1 += true;
      bool = true;
    } 
    writeInt(33639248L);
    if (bool) {
      writeShort(45);
      writeShort(45);
    } else {
      writeShort(j);
      writeShort(j);
    } 
    writeShort(i);
    writeShort(zipEntry.method);
    writeInt(zipEntry.xdostime);
    writeInt(zipEntry.crc);
    writeInt(l1);
    writeInt(l2);
    byte[] arrayOfByte1 = this.zc.getBytes(zipEntry.name);
    writeShort(arrayOfByte1.length);
    int k = getExtraLen(zipEntry.extra);
    if (bool)
      k += b1 + 4; 
    byte b2 = 0;
    if (zipEntry.mtime != null) {
      k += 4;
      b2 |= true;
    } 
    if (zipEntry.atime != null)
      b2 |= 0x2; 
    if (zipEntry.ctime != null)
      b2 |= 0x4; 
    if (b2 != 0)
      k += 5; 
    writeShort(k);
    if (zipEntry.comment != null) {
      arrayOfByte2 = this.zc.getBytes(zipEntry.comment);
      writeShort(Math.min(arrayOfByte2.length, 65535));
    } else {
      arrayOfByte2 = null;
      writeShort(0);
    } 
    writeShort(0);
    writeShort(0);
    writeInt(0L);
    writeInt(l3);
    writeBytes(arrayOfByte1, 0, arrayOfByte1.length);
    if (bool) {
      writeShort(1);
      writeShort(b1);
      if (l2 == 4294967295L)
        writeLong(zipEntry.size); 
      if (l1 == 4294967295L)
        writeLong(zipEntry.csize); 
      if (l3 == 4294967295L)
        writeLong(paramXEntry.offset); 
    } 
    if (b2 != 0) {
      writeShort(21589);
      if (zipEntry.mtime != null) {
        writeShort(5);
        writeByte(b2);
        writeInt(ZipUtils.fileTimeToUnixTime(zipEntry.mtime));
      } else {
        writeShort(1);
        writeByte(b2);
      } 
    } 
    writeExtra(zipEntry.extra);
    if (arrayOfByte2 != null)
      writeBytes(arrayOfByte2, 0, Math.min(arrayOfByte2.length, 65535)); 
  }
  
  private void writeEND(long paramLong1, long paramLong2) throws IOException {
    boolean bool = false;
    long l1 = paramLong2;
    long l2 = paramLong1;
    if (l1 >= 4294967295L) {
      l1 = 4294967295L;
      bool = true;
    } 
    if (l2 >= 4294967295L) {
      l2 = 4294967295L;
      bool = true;
    } 
    int i = this.xentries.size();
    if (i >= 65535) {
      bool |= (!inhibitZip64 ? 1 : 0);
      if (bool)
        i = 65535; 
    } 
    if (bool) {
      long l = this.written;
      writeInt(101075792L);
      writeLong(44L);
      writeShort(45);
      writeShort(45);
      writeInt(0L);
      writeInt(0L);
      writeLong(this.xentries.size());
      writeLong(this.xentries.size());
      writeLong(paramLong2);
      writeLong(paramLong1);
      writeInt(117853008L);
      writeInt(0L);
      writeLong(l);
      writeInt(1L);
    } 
    writeInt(101010256L);
    writeShort(0);
    writeShort(0);
    writeShort(i);
    writeShort(i);
    writeInt(l1);
    writeInt(l2);
    if (this.comment != null) {
      writeShort(this.comment.length);
      writeBytes(this.comment, 0, this.comment.length);
    } else {
      writeShort(0);
    } 
  }
  
  private int getExtraLen(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return 0; 
    int i = 0;
    int j = paramArrayOfByte.length;
    int k;
    for (k = 0; k + 4 <= j; k += n + 4) {
      int m = ZipUtils.get16(paramArrayOfByte, k);
      int n = ZipUtils.get16(paramArrayOfByte, k + 2);
      if (n < 0 || k + 4 + n > j)
        break; 
      if (m == 21589 || m == 1)
        i += n + 4; 
    } 
    return j - i;
  }
  
  private void writeExtra(byte[] paramArrayOfByte) throws IOException {
    if (paramArrayOfByte != null) {
      int i = paramArrayOfByte.length;
      int j;
      for (j = 0; j + 4 <= i; j += m + 4) {
        int k = ZipUtils.get16(paramArrayOfByte, j);
        int m = ZipUtils.get16(paramArrayOfByte, j + 2);
        if (m < 0 || j + 4 + m > i) {
          writeBytes(paramArrayOfByte, j, i - j);
          return;
        } 
        if (k != 21589 && k != 1)
          writeBytes(paramArrayOfByte, j, m + 4); 
      } 
      if (j < i)
        writeBytes(paramArrayOfByte, j, i - j); 
    } 
  }
  
  private void writeByte(int paramInt) {
    OutputStream outputStream = this.out;
    outputStream.write(paramInt & 0xFF);
    this.written++;
  }
  
  private void writeShort(int paramInt) {
    OutputStream outputStream = this.out;
    outputStream.write(paramInt >>> 0 & 0xFF);
    outputStream.write(paramInt >>> 8 & 0xFF);
    this.written += 2L;
  }
  
  private void writeInt(long paramLong) throws IOException {
    OutputStream outputStream = this.out;
    outputStream.write((int)(paramLong >>> false & 0xFFL));
    outputStream.write((int)(paramLong >>> 8 & 0xFFL));
    outputStream.write((int)(paramLong >>> 16 & 0xFFL));
    outputStream.write((int)(paramLong >>> 24 & 0xFFL));
    this.written += 4L;
  }
  
  private void writeLong(long paramLong) throws IOException {
    OutputStream outputStream = this.out;
    outputStream.write((int)(paramLong >>> false & 0xFFL));
    outputStream.write((int)(paramLong >>> 8 & 0xFFL));
    outputStream.write((int)(paramLong >>> 16 & 0xFFL));
    outputStream.write((int)(paramLong >>> 24 & 0xFFL));
    outputStream.write((int)(paramLong >>> 32 & 0xFFL));
    outputStream.write((int)(paramLong >>> 40 & 0xFFL));
    outputStream.write((int)(paramLong >>> 48 & 0xFFL));
    outputStream.write((int)(paramLong >>> 56 & 0xFFL));
    this.written += 8L;
  }
  
  private void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
    this.written += paramInt2;
  }
  
  private static class XEntry {
    final ZipEntry entry;
    
    final long offset;
    
    public XEntry(ZipEntry param1ZipEntry, long param1Long) {
      this.entry = param1ZipEntry;
      this.offset = param1Long;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\ZipOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */