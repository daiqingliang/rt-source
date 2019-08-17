package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class FileChannel extends AbstractInterruptibleChannel implements SeekableByteChannel, GatheringByteChannel, ScatteringByteChannel {
  private static final FileAttribute<?>[] NO_ATTRIBUTES = new FileAttribute[0];
  
  public static FileChannel open(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs) throws IOException {
    FileSystemProvider fileSystemProvider = paramPath.getFileSystem().provider();
    return fileSystemProvider.newFileChannel(paramPath, paramSet, paramVarArgs);
  }
  
  public static FileChannel open(Path paramPath, OpenOption... paramVarArgs) throws IOException {
    HashSet hashSet = new HashSet(paramVarArgs.length);
    Collections.addAll(hashSet, paramVarArgs);
    return open(paramPath, hashSet, NO_ATTRIBUTES);
  }
  
  public abstract int read(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  public final long read(ByteBuffer[] paramArrayOfByteBuffer) throws IOException { return read(paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length); }
  
  public abstract int write(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  public final long write(ByteBuffer[] paramArrayOfByteBuffer) throws IOException { return write(paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length); }
  
  public abstract long position() throws IOException;
  
  public abstract FileChannel position(long paramLong) throws IOException;
  
  public abstract long size() throws IOException;
  
  public abstract FileChannel truncate(long paramLong) throws IOException;
  
  public abstract void force(boolean paramBoolean) throws IOException;
  
  public abstract long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel) throws IOException;
  
  public abstract long transferFrom(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2) throws IOException;
  
  public abstract int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException;
  
  public abstract int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException;
  
  public abstract MappedByteBuffer map(MapMode paramMapMode, long paramLong1, long paramLong2) throws IOException;
  
  public abstract FileLock lock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException;
  
  public final FileLock lock() throws IOException { return lock(0L, Float.MAX_VALUE, false); }
  
  public abstract FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException;
  
  public final FileLock tryLock() throws IOException { return tryLock(0L, Float.MAX_VALUE, false); }
  
  public static class MapMode {
    public static final MapMode READ_ONLY = new MapMode("READ_ONLY");
    
    public static final MapMode READ_WRITE = new MapMode("READ_WRITE");
    
    public static final MapMode PRIVATE = new MapMode("PRIVATE");
    
    private final String name;
    
    private MapMode(String param1String) { this.name = param1String; }
    
    public String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\FileChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */