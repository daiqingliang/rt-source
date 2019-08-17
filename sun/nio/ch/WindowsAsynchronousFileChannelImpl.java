package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.util.concurrent.Future;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

public class WindowsAsynchronousFileChannelImpl extends AsynchronousFileChannelImpl implements Iocp.OverlappedChannel, Groupable {
  private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
  
  private static final int ERROR_HANDLE_EOF = 38;
  
  private static final FileDispatcher nd = new FileDispatcherImpl();
  
  private final long handle;
  
  private final int completionKey;
  
  private final Iocp iocp;
  
  private final boolean isDefaultIocp;
  
  private final PendingIoCache ioCache;
  
  static final int NO_LOCK = -1;
  
  static final int LOCKED = 0;
  
  private WindowsAsynchronousFileChannelImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, Iocp paramIocp, boolean paramBoolean3) throws IOException {
    super(paramFileDescriptor, paramBoolean1, paramBoolean2, paramIocp.executor());
    this.handle = fdAccess.getHandle(paramFileDescriptor);
    this.iocp = paramIocp;
    this.isDefaultIocp = paramBoolean3;
    this.ioCache = new PendingIoCache();
    this.completionKey = paramIocp.associate(this, this.handle);
  }
  
  public static AsynchronousFileChannel open(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ThreadPool paramThreadPool) throws IOException {
    boolean bool;
    Iocp iocp1;
    if (paramThreadPool == null) {
      iocp1 = DefaultIocpHolder.defaultIocp;
      bool = true;
    } else {
      iocp1 = (new Iocp(null, paramThreadPool)).start();
      bool = false;
    } 
    try {
      return new WindowsAsynchronousFileChannelImpl(paramFileDescriptor, paramBoolean1, paramBoolean2, iocp1, bool);
    } catch (IOException iOException) {
      if (!bool)
        iocp1.implClose(); 
      throw iOException;
    } 
  }
  
  public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong) { return this.ioCache.remove(paramLong); }
  
  public void close() throws IOException {
    this.closeLock.writeLock().lock();
    try {
      if (this.closed)
        return; 
      this.closed = true;
    } finally {
      this.closeLock.writeLock().unlock();
    } 
    invalidateAllLocks();
    close0(this.handle);
    this.ioCache.close();
    this.iocp.disassociate(this.completionKey);
    if (!this.isDefaultIocp)
      this.iocp.detachFromThreadPool(); 
  }
  
  public AsynchronousChannelGroupImpl group() { return this.iocp; }
  
  private static IOException toIOException(Throwable paramThrowable) {
    if (paramThrowable instanceof IOException) {
      if (paramThrowable instanceof ClosedChannelException)
        paramThrowable = new AsynchronousCloseException(); 
      return (IOException)paramThrowable;
    } 
    return new IOException(paramThrowable);
  }
  
  public long size() throws IOException {
    try {
      begin();
      return nd.size(this.fdObj);
    } finally {
      end();
    } 
  }
  
  public AsynchronousFileChannel truncate(long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative size"); 
    if (!this.writing)
      throw new NonWritableChannelException(); 
    try {
      begin();
      if (paramLong > nd.size(this.fdObj))
        return this; 
      nd.truncate(this.fdObj, paramLong);
    } finally {
      end();
    } 
    return this;
  }
  
  public void force(boolean paramBoolean) throws IOException {
    try {
      begin();
      nd.force(this.fdObj, paramBoolean);
    } finally {
      end();
    } 
  }
  
  <A> Future<FileLock> implLock(long paramLong1, long paramLong2, boolean paramBoolean, A paramA, CompletionHandler<FileLock, ? super A> paramCompletionHandler) {
    if (paramBoolean && !this.reading)
      throw new NonReadableChannelException(); 
    if (!paramBoolean && !this.writing)
      throw new NonWritableChannelException(); 
    fileLockImpl = addToFileLockTable(paramLong1, paramLong2, paramBoolean);
    if (fileLockImpl == null) {
      ClosedChannelException closedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null)
        return CompletedFuture.withFailure(closedChannelException); 
      Invoker.invoke(this, paramCompletionHandler, paramA, null, closedChannelException);
      return null;
    } 
    PendingFuture pendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    LockTask lockTask = new LockTask(paramLong1, fileLockImpl, pendingFuture);
    pendingFuture.setContext(lockTask);
    if (Iocp.supportsThreadAgnosticIo()) {
      lockTask.run();
    } else {
      bool = false;
      try {
        Invoker.invokeOnThreadInThreadPool(this, lockTask);
        bool = true;
      } finally {
        if (!bool)
          removeFromFileLockTable(fileLockImpl); 
      } 
    } 
    return pendingFuture;
  }
  
  public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
    if (paramBoolean && !this.reading)
      throw new NonReadableChannelException(); 
    if (!paramBoolean && !this.writing)
      throw new NonWritableChannelException(); 
    fileLockImpl = addToFileLockTable(paramLong1, paramLong2, paramBoolean);
    if (fileLockImpl == null)
      throw new ClosedChannelException(); 
    bool = false;
    try {
      begin();
      int i = nd.lock(this.fdObj, false, paramLong1, paramLong2, paramBoolean);
      if (i == -1)
        return null; 
      bool = true;
      return fileLockImpl;
    } finally {
      if (!bool)
        removeFromFileLockTable(fileLockImpl); 
      end();
    } 
  }
  
  protected void implRelease(FileLockImpl paramFileLockImpl) throws IOException { nd.release(this.fdObj, paramFileLockImpl.position(), paramFileLockImpl.size()); }
  
  <A> Future<Integer> implRead(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler) {
    if (!this.reading)
      throw new NonReadableChannelException(); 
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (paramByteBuffer.isReadOnly())
      throw new IllegalArgumentException("Read-only buffer"); 
    if (!isOpen()) {
      ClosedChannelException closedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null)
        return CompletedFuture.withFailure(closedChannelException); 
      Invoker.invoke(this, paramCompletionHandler, paramA, null, closedChannelException);
      return null;
    } 
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    if (k == 0) {
      if (paramCompletionHandler == null)
        return CompletedFuture.withResult(Integer.valueOf(0)); 
      Invoker.invoke(this, paramCompletionHandler, paramA, Integer.valueOf(0), null);
      return null;
    } 
    PendingFuture pendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    ReadTask readTask = new ReadTask(paramByteBuffer, i, k, paramLong, pendingFuture);
    pendingFuture.setContext(readTask);
    if (Iocp.supportsThreadAgnosticIo()) {
      readTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, readTask);
    } 
    return pendingFuture;
  }
  
  <A> Future<Integer> implWrite(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler) {
    if (!this.writing)
      throw new NonWritableChannelException(); 
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (!isOpen()) {
      ClosedChannelException closedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null)
        return CompletedFuture.withFailure(closedChannelException); 
      Invoker.invoke(this, paramCompletionHandler, paramA, null, closedChannelException);
      return null;
    } 
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    if (k == 0) {
      if (paramCompletionHandler == null)
        return CompletedFuture.withResult(Integer.valueOf(0)); 
      Invoker.invoke(this, paramCompletionHandler, paramA, Integer.valueOf(0), null);
      return null;
    } 
    PendingFuture pendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    WriteTask writeTask = new WriteTask(paramByteBuffer, i, k, paramLong, pendingFuture);
    pendingFuture.setContext(writeTask);
    if (Iocp.supportsThreadAgnosticIo()) {
      writeTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, writeTask);
    } 
    return pendingFuture;
  }
  
  private static native int readFile(long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4) throws IOException;
  
  private static native int writeFile(long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4) throws IOException;
  
  private static native int lockFile(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4) throws IOException;
  
  private static native void close0(long paramLong);
  
  static  {
    IOUtil.load();
  }
  
  private static class DefaultIocpHolder {
    static final Iocp defaultIocp = defaultIocp();
    
    private static Iocp defaultIocp() {
      try {
        return (new Iocp(null, ThreadPool.createDefault())).start();
      } catch (IOException iOException) {
        throw new InternalError(iOException);
      } 
    }
  }
  
  private class LockTask<A> extends Object implements Runnable, Iocp.ResultHandler {
    private final long position;
    
    private final FileLockImpl fli;
    
    private final PendingFuture<FileLock, A> result;
    
    LockTask(long param1Long, FileLockImpl param1FileLockImpl, PendingFuture<FileLock, A> param1PendingFuture) {
      this.position = param1Long;
      this.fli = param1FileLockImpl;
      this.result = param1PendingFuture;
    }
    
    public void run() throws IOException {
      l = 0L;
      bool = false;
      try {
        WindowsAsynchronousFileChannelImpl.this.begin();
        l = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
        synchronized (this.result) {
          int i = WindowsAsynchronousFileChannelImpl.lockFile(WindowsAsynchronousFileChannelImpl.this.handle, this.position, this.fli.size(), this.fli.isShared(), l);
          if (i == -2) {
            bool = true;
            return;
          } 
          this.result.setResult(this.fli);
        } 
      } catch (Throwable throwable) {
        WindowsAsynchronousFileChannelImpl.this.removeFromFileLockTable(this.fli);
        this.result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(throwable));
      } finally {
        if (!bool && l != 0L)
          WindowsAsynchronousFileChannelImpl.this.ioCache.remove(l); 
        WindowsAsynchronousFileChannelImpl.this.end();
      } 
      Invoker.invoke(this.result);
    }
    
    public void completed(int param1Int, boolean param1Boolean) {
      this.result.setResult(this.fli);
      if (param1Boolean) {
        Invoker.invokeUnchecked(this.result);
      } else {
        Invoker.invoke(this.result);
      } 
    }
    
    public void failed(int param1Int, IOException param1IOException) {
      WindowsAsynchronousFileChannelImpl.this.removeFromFileLockTable(this.fli);
      if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
        this.result.setFailure(param1IOException);
      } else {
        this.result.setFailure(new AsynchronousCloseException());
      } 
      Invoker.invoke(this.result);
    }
  }
  
  private class ReadTask<A> extends Object implements Runnable, Iocp.ResultHandler {
    private final ByteBuffer dst;
    
    private final int pos;
    
    private final int rem;
    
    private final long position;
    
    private final PendingFuture<Integer, A> result;
    
    ReadTask(ByteBuffer param1ByteBuffer, int param1Int1, int param1Int2, long param1Long, PendingFuture<Integer, A> param1PendingFuture) {
      this.dst = param1ByteBuffer;
      this.pos = param1Int1;
      this.rem = param1Int2;
      this.position = param1Long;
      this.result = param1PendingFuture;
    }
    
    void releaseBufferIfSubstituted() throws IOException {
      if (this.buf != this.dst)
        Util.releaseTemporaryDirectBuffer(this.buf); 
    }
    
    void updatePosition(int param1Int) {
      if (param1Int > 0)
        if (this.buf == this.dst) {
          try {
            this.dst.position(this.pos + param1Int);
          } catch (IllegalArgumentException illegalArgumentException) {}
        } else {
          this.buf.position(param1Int).flip();
          try {
            this.dst.put(this.buf);
          } catch (BufferOverflowException bufferOverflowException) {}
        }  
    }
    
    public void run() throws IOException {
      long l2;
      int i = -1;
      l1 = 0L;
      if (this.dst instanceof DirectBuffer) {
        this.buf = this.dst;
        l2 = ((DirectBuffer)this.dst).address() + this.pos;
      } else {
        this.buf = Util.getTemporaryDirectBuffer(this.rem);
        l2 = ((DirectBuffer)this.buf).address();
      } 
      bool = false;
      try {
        WindowsAsynchronousFileChannelImpl.this.begin();
        l1 = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
        i = WindowsAsynchronousFileChannelImpl.readFile(WindowsAsynchronousFileChannelImpl.this.handle, l2, this.rem, this.position, l1);
        if (i == -2) {
          bool = true;
          return;
        } 
        if (i == -1) {
          this.result.setResult(Integer.valueOf(i));
        } else {
          throw new InternalError("Unexpected result: " + i);
        } 
      } catch (Throwable throwable) {
        this.result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(throwable));
      } finally {
        if (!bool) {
          if (l1 != 0L)
            WindowsAsynchronousFileChannelImpl.this.ioCache.remove(l1); 
          releaseBufferIfSubstituted();
        } 
        WindowsAsynchronousFileChannelImpl.this.end();
      } 
      Invoker.invoke(this.result);
    }
    
    public void completed(int param1Int, boolean param1Boolean) {
      updatePosition(param1Int);
      releaseBufferIfSubstituted();
      this.result.setResult(Integer.valueOf(param1Int));
      if (param1Boolean) {
        Invoker.invokeUnchecked(this.result);
      } else {
        Invoker.invoke(this.result);
      } 
    }
    
    public void failed(int param1Int, IOException param1IOException) {
      if (param1Int == 38) {
        completed(-1, false);
      } else {
        releaseBufferIfSubstituted();
        if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
          this.result.setFailure(param1IOException);
        } else {
          this.result.setFailure(new AsynchronousCloseException());
        } 
        Invoker.invoke(this.result);
      } 
    }
  }
  
  private class WriteTask<A> extends Object implements Runnable, Iocp.ResultHandler {
    private final ByteBuffer src;
    
    private final int pos;
    
    private final int rem;
    
    private final long position;
    
    private final PendingFuture<Integer, A> result;
    
    WriteTask(ByteBuffer param1ByteBuffer, int param1Int1, int param1Int2, long param1Long, PendingFuture<Integer, A> param1PendingFuture) {
      this.src = param1ByteBuffer;
      this.pos = param1Int1;
      this.rem = param1Int2;
      this.position = param1Long;
      this.result = param1PendingFuture;
    }
    
    void releaseBufferIfSubstituted() throws IOException {
      if (this.buf != this.src)
        Util.releaseTemporaryDirectBuffer(this.buf); 
    }
    
    void updatePosition(int param1Int) {
      if (param1Int > 0)
        try {
          this.src.position(this.pos + param1Int);
        } catch (IllegalArgumentException illegalArgumentException) {} 
    }
    
    public void run() throws IOException {
      long l2;
      int i = -1;
      long l1 = 0L;
      if (this.src instanceof DirectBuffer) {
        this.buf = this.src;
        l2 = ((DirectBuffer)this.src).address() + this.pos;
      } else {
        this.buf = Util.getTemporaryDirectBuffer(this.rem);
        this.buf.put(this.src);
        this.buf.flip();
        this.src.position(this.pos);
        l2 = ((DirectBuffer)this.buf).address();
      } 
      try {
        WindowsAsynchronousFileChannelImpl.this.begin();
        l1 = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
        i = WindowsAsynchronousFileChannelImpl.writeFile(WindowsAsynchronousFileChannelImpl.this.handle, l2, this.rem, this.position, l1);
        if (i == -2)
          return; 
        throw new InternalError("Unexpected result: " + i);
      } catch (Throwable throwable) {
        this.result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(throwable));
        if (l1 != 0L)
          WindowsAsynchronousFileChannelImpl.this.ioCache.remove(l1); 
        releaseBufferIfSubstituted();
      } finally {
        WindowsAsynchronousFileChannelImpl.this.end();
      } 
      Invoker.invoke(this.result);
    }
    
    public void completed(int param1Int, boolean param1Boolean) {
      updatePosition(param1Int);
      releaseBufferIfSubstituted();
      this.result.setResult(Integer.valueOf(param1Int));
      if (param1Boolean) {
        Invoker.invokeUnchecked(this.result);
      } else {
        Invoker.invoke(this.result);
      } 
    }
    
    public void failed(int param1Int, IOException param1IOException) {
      releaseBufferIfSubstituted();
      if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
        this.result.setFailure(param1IOException);
      } else {
        this.result.setFailure(new AsynchronousCloseException());
      } 
      Invoker.invoke(this.result);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\WindowsAsynchronousFileChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */