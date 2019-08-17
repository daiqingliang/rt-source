package java.util.zip;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.WeakHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import sun.misc.JavaUtilZipFileAccess;
import sun.misc.PerfCounter;
import sun.misc.SharedSecrets;
import sun.misc.VM;

public class ZipFile implements ZipConstants, Closeable {
  private long jzfile;
  
  private final String name;
  
  private final int total;
  
  private final boolean locsig;
  
  private static final int STORED = 0;
  
  private static final int DEFLATED = 8;
  
  public static final int OPEN_READ = 1;
  
  public static final int OPEN_DELETE = 4;
  
  private static final boolean usemmap;
  
  private static final boolean ensuretrailingslash;
  
  private ZipCoder zc;
  
  private final Map<InputStream, Inflater> streams = new WeakHashMap();
  
  private Deque<Inflater> inflaterCache = new ArrayDeque();
  
  private static final int JZENTRY_NAME = 0;
  
  private static final int JZENTRY_EXTRA = 1;
  
  private static final int JZENTRY_COMMENT = 2;
  
  private static native void initIDs();
  
  public ZipFile(String paramString) throws IOException { this(new File(paramString), 1); }
  
  public ZipFile(File paramFile, int paramInt) throws IOException { this(paramFile, paramInt, StandardCharsets.UTF_8); }
  
  public ZipFile(File paramFile) throws ZipException, IOException { this(paramFile, 1); }
  
  public ZipFile(File paramFile, int paramInt, Charset paramCharset) throws IOException {
    if ((paramInt & true) == 0 || (paramInt & 0xFFFFFFFA) != 0)
      throw new IllegalArgumentException("Illegal mode: 0x" + Integer.toHexString(paramInt)); 
    String str = paramFile.getPath();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkRead(str);
      if ((paramInt & 0x4) != 0)
        securityManager.checkDelete(str); 
    } 
    if (paramCharset == null)
      throw new NullPointerException("charset is null"); 
    this.zc = ZipCoder.get(paramCharset);
    long l = System.nanoTime();
    this.jzfile = open(str, paramInt, paramFile.lastModified(), usemmap);
    PerfCounter.getZipFileOpenTime().addElapsedTimeFrom(l);
    PerfCounter.getZipFileCount().increment();
    this.name = str;
    this.total = getTotal(this.jzfile);
    this.locsig = startsWithLOC(this.jzfile);
  }
  
  public ZipFile(String paramString, Charset paramCharset) throws IOException { this(new File(paramString), 1, paramCharset); }
  
  public ZipFile(File paramFile, Charset paramCharset) throws IOException { this(paramFile, 1, paramCharset); }
  
  public String getComment() {
    synchronized (this) {
      ensureOpen();
      byte[] arrayOfByte = getCommentBytes(this.jzfile);
      if (arrayOfByte == null)
        return null; 
      return this.zc.toString(arrayOfByte, arrayOfByte.length);
    } 
  }
  
  public ZipEntry getEntry(String paramString) {
    if (paramString == null)
      throw new NullPointerException("name"); 
    long l = 0L;
    synchronized (this) {
      ensureOpen();
      l = getEntry(this.jzfile, this.zc.getBytes(paramString), true);
      if (l != 0L) {
        ZipEntry zipEntry = ensuretrailingslash ? getZipEntry(null, l) : getZipEntry(paramString, l);
        freeEntry(this.jzfile, l);
        return zipEntry;
      } 
    } 
    return null;
  }
  
  private static native long getEntry(long paramLong, byte[] paramArrayOfByte, boolean paramBoolean);
  
  private static native void freeEntry(long paramLong1, long paramLong2);
  
  public InputStream getInputStream(ZipEntry paramZipEntry) throws IOException {
    if (paramZipEntry == null)
      throw new NullPointerException("entry"); 
    long l = 0L;
    ZipFileInputStream zipFileInputStream = null;
    synchronized (this) {
      ZipFileInflaterInputStream zipFileInflaterInputStream;
      Inflater inflater;
      long l1;
      ensureOpen();
      if (!this.zc.isUTF8() && (paramZipEntry.flag & 0x800) != 0) {
        l = getEntry(this.jzfile, this.zc.getBytesUTF8(paramZipEntry.name), false);
      } else {
        l = getEntry(this.jzfile, this.zc.getBytes(paramZipEntry.name), false);
      } 
      if (l == 0L)
        return null; 
      zipFileInputStream = new ZipFileInputStream(l);
      switch (getEntryMethod(l)) {
        case 0:
          synchronized (this.streams) {
            this.streams.put(zipFileInputStream, null);
          } 
          return zipFileInputStream;
        case 8:
          l1 = getEntrySize(l) + 2L;
          if (l1 > 65536L)
            l1 = 8192L; 
          if (l1 <= 0L)
            l1 = 4096L; 
          inflater = getInflater();
          zipFileInflaterInputStream = new ZipFileInflaterInputStream(zipFileInputStream, inflater, (int)l1);
          synchronized (this.streams) {
            this.streams.put(zipFileInflaterInputStream, inflater);
          } 
          return zipFileInflaterInputStream;
      } 
      throw new ZipException("invalid compression method");
    } 
  }
  
  private Inflater getInflater() {
    synchronized (this.inflaterCache) {
      Inflater inflater;
      while (null != (inflater = (Inflater)this.inflaterCache.poll())) {
        if (false == inflater.ended())
          return inflater; 
      } 
    } 
    return new Inflater(true);
  }
  
  private void releaseInflater(Inflater paramInflater) {
    if (false == paramInflater.ended()) {
      paramInflater.reset();
      synchronized (this.inflaterCache) {
        this.inflaterCache.add(paramInflater);
      } 
    } 
  }
  
  public String getName() { return this.name; }
  
  public Enumeration<? extends ZipEntry> entries() { return new ZipEntryIterator(); }
  
  public Stream<? extends ZipEntry> stream() { return StreamSupport.stream(Spliterators.spliterator(new ZipEntryIterator(), size(), 1297), false); }
  
  private ZipEntry getZipEntry(String paramString, long paramLong) {
    ZipEntry zipEntry = new ZipEntry();
    zipEntry.flag = getEntryFlag(paramLong);
    if (paramString != null) {
      zipEntry.name = paramString;
    } else {
      byte[] arrayOfByte1 = getEntryBytes(paramLong, 0);
      if (arrayOfByte1 == null) {
        zipEntry.name = "";
      } else if (!this.zc.isUTF8() && (zipEntry.flag & 0x800) != 0) {
        zipEntry.name = this.zc.toStringUTF8(arrayOfByte1, arrayOfByte1.length);
      } else {
        zipEntry.name = this.zc.toString(arrayOfByte1, arrayOfByte1.length);
      } 
    } 
    zipEntry.xdostime = getEntryTime(paramLong);
    zipEntry.crc = getEntryCrc(paramLong);
    zipEntry.size = getEntrySize(paramLong);
    zipEntry.csize = getEntryCSize(paramLong);
    zipEntry.method = getEntryMethod(paramLong);
    zipEntry.setExtra0(getEntryBytes(paramLong, 1), false);
    byte[] arrayOfByte = getEntryBytes(paramLong, 2);
    if (arrayOfByte == null) {
      zipEntry.comment = null;
    } else if (!this.zc.isUTF8() && (zipEntry.flag & 0x800) != 0) {
      zipEntry.comment = this.zc.toStringUTF8(arrayOfByte, arrayOfByte.length);
    } else {
      zipEntry.comment = this.zc.toString(arrayOfByte, arrayOfByte.length);
    } 
    return zipEntry;
  }
  
  private static native long getNextEntry(long paramLong, int paramInt);
  
  public int size() {
    ensureOpen();
    return this.total;
  }
  
  public void close() {
    if (this.closeRequested)
      return; 
    this.closeRequested = true;
    synchronized (this) {
      synchronized (this.streams) {
        if (false == this.streams.isEmpty()) {
          HashMap hashMap = new HashMap(this.streams);
          this.streams.clear();
          for (Map.Entry entry : hashMap.entrySet()) {
            ((InputStream)entry.getKey()).close();
            Inflater inflater = (Inflater)entry.getValue();
            if (inflater != null)
              inflater.end(); 
          } 
        } 
      } 
      synchronized (this.inflaterCache) {
        Inflater inflater;
        while (null != (inflater = (Inflater)this.inflaterCache.poll()))
          inflater.end(); 
      } 
      if (this.jzfile != 0L) {
        long l = this.jzfile;
        this.jzfile = 0L;
        close(l);
      } 
    } 
  }
  
  protected void finalize() { close(); }
  
  private static native void close(long paramLong);
  
  private void ensureOpen() {
    if (this.closeRequested)
      throw new IllegalStateException("zip file closed"); 
    if (this.jzfile == 0L)
      throw new IllegalStateException("The object is not initialized."); 
  }
  
  private void ensureOpenOrZipException() {
    if (this.closeRequested)
      throw new ZipException("ZipFile closed"); 
  }
  
  private boolean startsWithLocHeader() { return this.locsig; }
  
  private static native long open(String paramString, int paramInt, long paramLong, boolean paramBoolean) throws IOException;
  
  private static native int getTotal(long paramLong);
  
  private static native boolean startsWithLOC(long paramLong);
  
  private static native int read(long paramLong1, long paramLong2, long paramLong3, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native long getEntryTime(long paramLong);
  
  private static native long getEntryCrc(long paramLong);
  
  private static native long getEntryCSize(long paramLong);
  
  private static native long getEntrySize(long paramLong);
  
  private static native int getEntryMethod(long paramLong);
  
  private static native int getEntryFlag(long paramLong);
  
  private static native byte[] getCommentBytes(long paramLong);
  
  private static native byte[] getEntryBytes(long paramLong, int paramInt);
  
  private static native String getZipMessage(long paramLong);
  
  static  {
    initIDs();
    String str = VM.getSavedProperty("sun.zip.disableMemoryMapping");
    usemmap = (str == null || (str.length() != 0 && !str.equalsIgnoreCase("true")));
    str = VM.getSavedProperty("jdk.util.zip.ensureTrailingSlash");
    ensuretrailingslash = (str == null || !str.equalsIgnoreCase("false"));
    SharedSecrets.setJavaUtilZipFileAccess(new JavaUtilZipFileAccess() {
          public boolean startsWithLocHeader(ZipFile param1ZipFile) { return param1ZipFile.startsWithLocHeader(); }
        });
  }
  
  private class ZipEntryIterator extends Object implements Enumeration<ZipEntry>, Iterator<ZipEntry> {
    private int i = 0;
    
    public ZipEntryIterator() { this$0.ensureOpen(); }
    
    public boolean hasMoreElements() { return hasNext(); }
    
    public boolean hasNext() {
      synchronized (ZipFile.this) {
        ZipFile.this.ensureOpen();
        return (this.i < ZipFile.this.total);
      } 
    }
    
    public ZipEntry nextElement() { return next(); }
    
    public ZipEntry next() {
      synchronized (ZipFile.this) {
        ZipFile.this.ensureOpen();
        if (this.i >= ZipFile.this.total)
          throw new NoSuchElementException(); 
        long l = ZipFile.getNextEntry(ZipFile.this.jzfile, this.i++);
        if (l == 0L) {
          String str;
          if (ZipFile.this.closeRequested) {
            str = "ZipFile concurrently closed";
          } else {
            str = ZipFile.getZipMessage(ZipFile.this.jzfile);
          } 
          throw new ZipError("jzentry == 0,\n jzfile = " + ZipFile.this.jzfile + ",\n total = " + ZipFile.this.total + ",\n name = " + ZipFile.this.name + ",\n i = " + this.i + ",\n message = " + str);
        } 
        ZipEntry zipEntry = ZipFile.this.getZipEntry(null, l);
        ZipFile.freeEntry(ZipFile.this.jzfile, l);
        return zipEntry;
      } 
    }
  }
  
  private class ZipFileInflaterInputStream extends InflaterInputStream {
    private boolean eof = false;
    
    private final ZipFile.ZipFileInputStream zfin;
    
    ZipFileInflaterInputStream(ZipFile.ZipFileInputStream param1ZipFileInputStream, Inflater param1Inflater, int param1Int) {
      super(param1ZipFileInputStream, param1Inflater, param1Int);
      this.zfin = param1ZipFileInputStream;
    }
    
    public void close() {
      Inflater inflater;
      if (this.closeRequested)
        return; 
      this.closeRequested = true;
      super.close();
      synchronized (ZipFile.this.streams) {
        inflater = (Inflater)ZipFile.this.streams.remove(this);
      } 
      if (inflater != null)
        ZipFile.this.releaseInflater(inflater); 
    }
    
    protected void fill() {
      if (this.eof)
        throw new EOFException("Unexpected end of ZLIB input stream"); 
      this.len = this.in.read(this.buf, 0, this.buf.length);
      if (this.len == -1) {
        this.buf[0] = 0;
        this.len = 1;
        this.eof = true;
      } 
      this.inf.setInput(this.buf, 0, this.len);
    }
    
    public int available() {
      if (this.closeRequested)
        return 0; 
      long l = this.zfin.size() - this.inf.getBytesWritten();
      return (l > 2147483647L) ? Integer.MAX_VALUE : (int)l;
    }
    
    protected void finalize() { close(); }
  }
  
  private class ZipFileInputStream extends InputStream {
    protected long jzentry;
    
    private long pos = 0L;
    
    protected long rem;
    
    protected long size;
    
    ZipFileInputStream(long param1Long) {
      this.rem = ZipFile.getEntryCSize(param1Long);
      this.size = ZipFile.getEntrySize(param1Long);
      this.jzentry = param1Long;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      synchronized (ZipFile.this) {
        long l1 = this.rem;
        long l2 = this.pos;
        if (l1 == 0L)
          return -1; 
        if (param1Int2 <= 0)
          return 0; 
        if (param1Int2 > l1)
          param1Int2 = (int)l1; 
        ZipFile.this.ensureOpenOrZipException();
        param1Int2 = ZipFile.read(ZipFile.this.jzfile, this.jzentry, l2, param1ArrayOfByte, param1Int1, param1Int2);
        if (param1Int2 > 0) {
          this.pos = l2 + param1Int2;
          this.rem = l1 - param1Int2;
        } 
      } 
      if (this.rem == 0L)
        close(); 
      return param1Int2;
    }
    
    public int read() {
      byte[] arrayOfByte = new byte[1];
      return (read(arrayOfByte, 0, 1) == 1) ? (arrayOfByte[0] & 0xFF) : -1;
    }
    
    public long skip(long param1Long) {
      if (param1Long > this.rem)
        param1Long = this.rem; 
      this.pos += param1Long;
      this.rem -= param1Long;
      if (this.rem == 0L)
        close(); 
      return param1Long;
    }
    
    public int available() { return (this.rem > 2147483647L) ? Integer.MAX_VALUE : (int)this.rem; }
    
    public long size() { return this.size; }
    
    public void close() {
      if (this.zfisCloseRequested)
        return; 
      this.zfisCloseRequested = true;
      this.rem = 0L;
      synchronized (ZipFile.this) {
        if (this.jzentry != 0L && ZipFile.this.jzfile != 0L) {
          ZipFile.freeEntry(ZipFile.this.jzfile, this.jzentry);
          this.jzentry = 0L;
        } 
      } 
      synchronized (ZipFile.this.streams) {
        ZipFile.this.streams.remove(this);
      } 
    }
    
    protected void finalize() { close(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\ZipFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */