package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class AsynchronousFileChannel implements AsynchronousChannel {
  private static final FileAttribute<?>[] NO_ATTRIBUTES = new FileAttribute[0];
  
  public static AsynchronousFileChannel open(Path paramPath, Set<? extends OpenOption> paramSet, ExecutorService paramExecutorService, FileAttribute<?>... paramVarArgs) throws IOException {
    FileSystemProvider fileSystemProvider = paramPath.getFileSystem().provider();
    return fileSystemProvider.newAsynchronousFileChannel(paramPath, paramSet, paramExecutorService, paramVarArgs);
  }
  
  public static AsynchronousFileChannel open(Path paramPath, OpenOption... paramVarArgs) throws IOException {
    HashSet hashSet = new HashSet(paramVarArgs.length);
    Collections.addAll(hashSet, paramVarArgs);
    return open(paramPath, hashSet, null, NO_ATTRIBUTES);
  }
  
  public abstract long size() throws IOException;
  
  public abstract AsynchronousFileChannel truncate(long paramLong) throws IOException;
  
  public abstract void force(boolean paramBoolean) throws IOException;
  
  public abstract <A> void lock(long paramLong1, long paramLong2, boolean paramBoolean, A paramA, CompletionHandler<FileLock, ? super A> paramCompletionHandler);
  
  public final <A> void lock(A paramA, CompletionHandler<FileLock, ? super A> paramCompletionHandler) { lock(0L, Float.MAX_VALUE, false, paramA, paramCompletionHandler); }
  
  public abstract Future<FileLock> lock(long paramLong1, long paramLong2, boolean paramBoolean);
  
  public final Future<FileLock> lock() { return lock(0L, Float.MAX_VALUE, false); }
  
  public abstract FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException;
  
  public final FileLock tryLock() throws IOException { return tryLock(0L, Float.MAX_VALUE, false); }
  
  public abstract <A> void read(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public abstract Future<Integer> read(ByteBuffer paramByteBuffer, long paramLong);
  
  public abstract <A> void write(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public abstract Future<Integer> write(ByteBuffer paramByteBuffer, long paramLong);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\AsynchronousFileChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */