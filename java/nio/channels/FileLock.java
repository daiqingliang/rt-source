package java.nio.channels;

import java.io.IOException;

public abstract class FileLock implements AutoCloseable {
  private final Channel channel;
  
  private final long position;
  
  private final long size;
  
  private final boolean shared;
  
  protected FileLock(FileChannel paramFileChannel, long paramLong1, long paramLong2, boolean paramBoolean) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (paramLong2 < 0L)
      throw new IllegalArgumentException("Negative size"); 
    if (paramLong1 + paramLong2 < 0L)
      throw new IllegalArgumentException("Negative position + size"); 
    this.channel = paramFileChannel;
    this.position = paramLong1;
    this.size = paramLong2;
    this.shared = paramBoolean;
  }
  
  protected FileLock(AsynchronousFileChannel paramAsynchronousFileChannel, long paramLong1, long paramLong2, boolean paramBoolean) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (paramLong2 < 0L)
      throw new IllegalArgumentException("Negative size"); 
    if (paramLong1 + paramLong2 < 0L)
      throw new IllegalArgumentException("Negative position + size"); 
    this.channel = paramAsynchronousFileChannel;
    this.position = paramLong1;
    this.size = paramLong2;
    this.shared = paramBoolean;
  }
  
  public final FileChannel channel() { return (this.channel instanceof FileChannel) ? (FileChannel)this.channel : null; }
  
  public Channel acquiredBy() { return this.channel; }
  
  public final long position() { return this.position; }
  
  public final long size() { return this.size; }
  
  public final boolean isShared() { return this.shared; }
  
  public final boolean overlaps(long paramLong1, long paramLong2) { return (paramLong1 + paramLong2 <= this.position) ? false : (!(this.position + this.size <= paramLong1)); }
  
  public abstract boolean isValid();
  
  public abstract void release() throws IOException;
  
  public final void close() throws IOException { release(); }
  
  public final String toString() { return getClass().getName() + "[" + this.position + ":" + this.size + " " + (this.shared ? "shared" : "exclusive") + " " + (isValid() ? "valid" : "invalid") + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\FileLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */