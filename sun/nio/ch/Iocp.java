package sun.nio.ch;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ShutdownChannelGroupException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

class Iocp extends AsynchronousChannelGroupImpl {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long INVALID_HANDLE_VALUE = -1L;
  
  private static final boolean supportsThreadAgnosticIo;
  
  private final ReadWriteLock keyToChannelLock = new ReentrantReadWriteLock();
  
  private final Map<Integer, OverlappedChannel> keyToChannel = new HashMap();
  
  private int nextCompletionKey = 1;
  
  private final long port = createIoCompletionPort(-1L, 0L, 0, fixedThreadCount());
  
  private boolean closed;
  
  private final Set<Long> staleIoSet = new HashSet();
  
  Iocp(AsynchronousChannelProvider paramAsynchronousChannelProvider, ThreadPool paramThreadPool) throws IOException { super(paramAsynchronousChannelProvider, paramThreadPool); }
  
  Iocp start() {
    startThreads(new EventHandlerTask(null));
    return this;
  }
  
  static boolean supportsThreadAgnosticIo() { return supportsThreadAgnosticIo; }
  
  void implClose() {
    synchronized (this) {
      if (this.closed)
        return; 
      this.closed = true;
    } 
    close0(this.port);
    synchronized (this.staleIoSet) {
      for (Long long : this.staleIoSet)
        unsafe.freeMemory(long.longValue()); 
      this.staleIoSet.clear();
    } 
  }
  
  boolean isEmpty() {
    this.keyToChannelLock.writeLock().lock();
    try {
      return this.keyToChannel.isEmpty();
    } finally {
      this.keyToChannelLock.writeLock().unlock();
    } 
  }
  
  final Object attachForeignChannel(final Channel channel, FileDescriptor paramFileDescriptor) throws IOException {
    int i = associate(new OverlappedChannel() {
          public <V, A> PendingFuture<V, A> getByOverlapped(long param1Long) { return null; }
          
          public void close() { channel.close(); }
        },  0L);
    return Integer.valueOf(i);
  }
  
  final void detachForeignChannel(Object paramObject) { disassociate(((Integer)paramObject).intValue()); }
  
  void closeAllChannels() {
    byte b;
    OverlappedChannel[] arrayOfOverlappedChannel = new OverlappedChannel[32];
    do {
      this.keyToChannelLock.writeLock().lock();
      b = 0;
      try {
        for (Integer integer : this.keyToChannel.keySet()) {
          arrayOfOverlappedChannel[b++] = (OverlappedChannel)this.keyToChannel.get(integer);
          if (b >= 32)
            break; 
        } 
      } finally {
        this.keyToChannelLock.writeLock().unlock();
      } 
      for (byte b1 = 0; b1 < b; b1++) {
        try {
          arrayOfOverlappedChannel[b1].close();
        } catch (IOException iOException) {}
      } 
    } while (b > 0);
  }
  
  private void wakeup() {
    try {
      postQueuedCompletionStatus(this.port, 0);
    } catch (IOException iOException) {
      throw new AssertionError(iOException);
    } 
  }
  
  void executeOnHandlerTask(Runnable paramRunnable) {
    synchronized (this) {
      if (this.closed)
        throw new RejectedExecutionException(); 
      offerTask(paramRunnable);
      wakeup();
    } 
  }
  
  void shutdownHandlerTasks() {
    int i = threadCount();
    while (i-- > 0)
      wakeup(); 
  }
  
  int associate(OverlappedChannel paramOverlappedChannel, long paramLong) throws IOException {
    int i;
    this.keyToChannelLock.writeLock().lock();
    try {
      if (isShutdown())
        throw new ShutdownChannelGroupException(); 
      do {
        i = this.nextCompletionKey++;
      } while (i == 0 || this.keyToChannel.containsKey(Integer.valueOf(i)));
      if (paramLong != 0L)
        createIoCompletionPort(paramLong, this.port, i, 0); 
      this.keyToChannel.put(Integer.valueOf(i), paramOverlappedChannel);
    } finally {
      this.keyToChannelLock.writeLock().unlock();
    } 
    return i;
  }
  
  void disassociate(int paramInt) {
    boolean bool = false;
    this.keyToChannelLock.writeLock().lock();
    try {
      this.keyToChannel.remove(Integer.valueOf(paramInt));
      if (this.keyToChannel.isEmpty())
        bool = true; 
    } finally {
      this.keyToChannelLock.writeLock().unlock();
    } 
    if (bool && isShutdown())
      try {
        shutdownNow();
      } catch (IOException iOException) {} 
  }
  
  void makeStale(Long paramLong) {
    synchronized (this.staleIoSet) {
      this.staleIoSet.add(paramLong);
    } 
  }
  
  private void checkIfStale(long paramLong) {
    synchronized (this.staleIoSet) {
      boolean bool = this.staleIoSet.remove(Long.valueOf(paramLong));
      if (bool)
        unsafe.freeMemory(paramLong); 
    } 
  }
  
  private static IOException translateErrorToIOException(int paramInt) {
    String str = getErrorMessage(paramInt);
    if (str == null)
      str = "Unknown error: 0x0" + Integer.toHexString(paramInt); 
    return new IOException(str);
  }
  
  private static native void initIDs();
  
  private static native long createIoCompletionPort(long paramLong1, long paramLong2, int paramInt1, int paramInt2) throws IOException;
  
  private static native void close0(long paramLong);
  
  private static native void getQueuedCompletionStatus(long paramLong, CompletionStatus paramCompletionStatus) throws IOException;
  
  private static native void postQueuedCompletionStatus(long paramLong, int paramInt) throws IOException;
  
  private static native String getErrorMessage(int paramInt);
  
  static  {
    IOUtil.load();
    initIDs();
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.version"));
    String[] arrayOfString = str.split("\\.");
    supportsThreadAgnosticIo = (Integer.parseInt(arrayOfString[0]) >= 6);
  }
  
  private static class CompletionStatus {
    private int error;
    
    private int bytesTransferred;
    
    private int completionKey;
    
    private long overlapped;
    
    private CompletionStatus() {}
    
    int error() { return this.error; }
    
    int bytesTransferred() { return this.bytesTransferred; }
    
    int completionKey() { return this.completionKey; }
    
    long overlapped() { return this.overlapped; }
  }
  
  private class EventHandlerTask implements Runnable {
    private EventHandlerTask() {}
    
    public void run() {
      Invoker.GroupAndInvokeCount groupAndInvokeCount = Invoker.getGroupAndInvokeCount();
      boolean bool1 = (groupAndInvokeCount != null);
      Iocp.CompletionStatus completionStatus = new Iocp.CompletionStatus(null);
      bool2 = false;
      try {
        while (true) {
          if (groupAndInvokeCount != null)
            groupAndInvokeCount.resetInvokeCount(); 
          bool2 = false;
          try {
            Iocp.getQueuedCompletionStatus(Iocp.this.port, completionStatus);
          } catch (IOException iOException) {
            iOException.printStackTrace();
            return;
          } 
          if (completionStatus.completionKey() == 0 && completionStatus.overlapped() == 0L) {
            Runnable runnable = Iocp.this.pollTask();
            if (runnable == null)
              return; 
            bool2 = true;
            runnable.run();
            continue;
          } 
          Iocp.OverlappedChannel overlappedChannel = null;
          Iocp.this.keyToChannelLock.readLock().lock();
          try {
            overlappedChannel = (Iocp.OverlappedChannel)Iocp.this.keyToChannel.get(Integer.valueOf(completionStatus.completionKey()));
            if (overlappedChannel == null) {
              Iocp.this.checkIfStale(completionStatus.overlapped());
              Iocp.this.keyToChannelLock.readLock().unlock();
              continue;
            } 
          } finally {
            Iocp.this.keyToChannelLock.readLock().unlock();
          } 
          PendingFuture pendingFuture = overlappedChannel.getByOverlapped(completionStatus.overlapped());
          if (pendingFuture == null) {
            Iocp.this.checkIfStale(completionStatus.overlapped());
            continue;
          } 
          synchronized (pendingFuture) {
            if (pendingFuture.isDone())
              continue; 
          } 
          int i = completionStatus.error();
          Iocp.ResultHandler resultHandler = (Iocp.ResultHandler)pendingFuture.getContext();
          bool2 = true;
          if (i == 0) {
            resultHandler.completed(completionStatus.bytesTransferred(), bool1);
            continue;
          } 
          resultHandler.failed(i, Iocp.translateErrorToIOException(i));
        } 
      } finally {
        int i = Iocp.this.threadExit(this, bool2);
        if (i == 0 && Iocp.this.isShutdown())
          Iocp.this.implClose(); 
      } 
    }
  }
  
  static interface OverlappedChannel extends Closeable {
    <V, A> PendingFuture<V, A> getByOverlapped(long param1Long);
  }
  
  static interface ResultHandler {
    void completed(int param1Int, boolean param1Boolean);
    
    void failed(int param1Int, IOException param1IOException);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\Iocp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */