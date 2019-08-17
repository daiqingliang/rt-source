package java.util.zip;

import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ZipEntry implements ZipConstants, Cloneable {
  String name;
  
  long xdostime = -1L;
  
  FileTime mtime;
  
  FileTime atime;
  
  FileTime ctime;
  
  long crc = -1L;
  
  long size = -1L;
  
  long csize = -1L;
  
  int method = -1;
  
  int flag = 0;
  
  byte[] extra;
  
  String comment;
  
  public static final int STORED = 0;
  
  public static final int DEFLATED = 8;
  
  static final long DOSTIME_BEFORE_1980 = 2162688L;
  
  private static final long UPPER_DOSTIME_BOUND = 4036608000000L;
  
  public ZipEntry(String paramString) {
    Objects.requireNonNull(paramString, "name");
    if (paramString.length() > 65535)
      throw new IllegalArgumentException("entry name too long"); 
    this.name = paramString;
  }
  
  public ZipEntry(ZipEntry paramZipEntry) {
    Objects.requireNonNull(paramZipEntry, "entry");
    this.name = paramZipEntry.name;
    this.xdostime = paramZipEntry.xdostime;
    this.mtime = paramZipEntry.mtime;
    this.atime = paramZipEntry.atime;
    this.ctime = paramZipEntry.ctime;
    this.crc = paramZipEntry.crc;
    this.size = paramZipEntry.size;
    this.csize = paramZipEntry.csize;
    this.method = paramZipEntry.method;
    this.flag = paramZipEntry.flag;
    this.extra = paramZipEntry.extra;
    this.comment = paramZipEntry.comment;
  }
  
  ZipEntry() {}
  
  public String getName() { return this.name; }
  
  public void setTime(long paramLong) {
    this.xdostime = ZipUtils.javaToExtendedDosTime(paramLong);
    if (this.xdostime != 2162688L && paramLong <= 4036608000000L) {
      this.mtime = null;
    } else {
      this.mtime = FileTime.from(paramLong, TimeUnit.MILLISECONDS);
    } 
  }
  
  public long getTime() { return (this.mtime != null) ? this.mtime.toMillis() : ((this.xdostime != -1L) ? ZipUtils.extendedDosToJavaTime(this.xdostime) : -1L); }
  
  public ZipEntry setLastModifiedTime(FileTime paramFileTime) {
    this.mtime = (FileTime)Objects.requireNonNull(paramFileTime, "lastModifiedTime");
    this.xdostime = ZipUtils.javaToExtendedDosTime(paramFileTime.to(TimeUnit.MILLISECONDS));
    return this;
  }
  
  public FileTime getLastModifiedTime() { return (this.mtime != null) ? this.mtime : ((this.xdostime == -1L) ? null : FileTime.from(getTime(), TimeUnit.MILLISECONDS)); }
  
  public ZipEntry setLastAccessTime(FileTime paramFileTime) {
    this.atime = (FileTime)Objects.requireNonNull(paramFileTime, "lastAccessTime");
    return this;
  }
  
  public FileTime getLastAccessTime() { return this.atime; }
  
  public ZipEntry setCreationTime(FileTime paramFileTime) {
    this.ctime = (FileTime)Objects.requireNonNull(paramFileTime, "creationTime");
    return this;
  }
  
  public FileTime getCreationTime() { return this.ctime; }
  
  public void setSize(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("invalid entry size"); 
    this.size = paramLong;
  }
  
  public long getSize() { return this.size; }
  
  public long getCompressedSize() { return this.csize; }
  
  public void setCompressedSize(long paramLong) { this.csize = paramLong; }
  
  public void setCrc(long paramLong) {
    if (paramLong < 0L || paramLong > 4294967295L)
      throw new IllegalArgumentException("invalid entry crc-32"); 
    this.crc = paramLong;
  }
  
  public long getCrc() { return this.crc; }
  
  public void setMethod(int paramInt) {
    if (paramInt != 0 && paramInt != 8)
      throw new IllegalArgumentException("invalid compression method"); 
    this.method = paramInt;
  }
  
  public int getMethod() { return this.method; }
  
  public void setExtra(byte[] paramArrayOfByte) { setExtra0(paramArrayOfByte, false); }
  
  void setExtra0(byte[] paramArrayOfByte, boolean paramBoolean) {
    if (paramArrayOfByte != null) {
      if (paramArrayOfByte.length > 65535)
        throw new IllegalArgumentException("invalid extra field length"); 
      int i = 0;
      int j = paramArrayOfByte.length;
      while (i + 4 < j) {
        int i2;
        int i1;
        int n;
        int k = ZipUtils.get16(paramArrayOfByte, i);
        int m = ZipUtils.get16(paramArrayOfByte, i + 2);
        i += 4;
        if (i + m > j)
          break; 
        switch (k) {
          case 1:
            if (paramBoolean && m >= 16) {
              this.size = ZipUtils.get64(paramArrayOfByte, i);
              this.csize = ZipUtils.get64(paramArrayOfByte, i + 8);
            } 
            break;
          case 10:
            if (m < 32)
              break; 
            n = i + 4;
            if (ZipUtils.get16(paramArrayOfByte, n) != 1 || ZipUtils.get16(paramArrayOfByte, n + 2) != 24)
              break; 
            this.mtime = ZipUtils.winTimeToFileTime(ZipUtils.get64(paramArrayOfByte, n + 4));
            this.atime = ZipUtils.winTimeToFileTime(ZipUtils.get64(paramArrayOfByte, n + 12));
            this.ctime = ZipUtils.winTimeToFileTime(ZipUtils.get64(paramArrayOfByte, n + 20));
            break;
          case 21589:
            i1 = Byte.toUnsignedInt(paramArrayOfByte[i]);
            i2 = 1;
            if ((i1 & true) != 0 && i2 + 4 <= m) {
              this.mtime = ZipUtils.unixTimeToFileTime(ZipUtils.get32(paramArrayOfByte, i + i2));
              i2 += 4;
            } 
            if ((i1 & 0x2) != 0 && i2 + 4 <= m) {
              this.atime = ZipUtils.unixTimeToFileTime(ZipUtils.get32(paramArrayOfByte, i + i2));
              i2 += 4;
            } 
            if ((i1 & 0x4) != 0 && i2 + 4 <= m) {
              this.ctime = ZipUtils.unixTimeToFileTime(ZipUtils.get32(paramArrayOfByte, i + i2));
              i2 += 4;
            } 
            break;
        } 
        i += m;
      } 
    } 
    this.extra = paramArrayOfByte;
  }
  
  public byte[] getExtra() { return this.extra; }
  
  public void setComment(String paramString) { this.comment = paramString; }
  
  public String getComment() { return this.comment; }
  
  public boolean isDirectory() { return this.name.endsWith("/"); }
  
  public String toString() { return getName(); }
  
  public int hashCode() { return this.name.hashCode(); }
  
  public Object clone() {
    try {
      ZipEntry zipEntry = (ZipEntry)super.clone();
      zipEntry.extra = (this.extra == null) ? null : (byte[])this.extra.clone();
      return zipEntry;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\ZipEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */