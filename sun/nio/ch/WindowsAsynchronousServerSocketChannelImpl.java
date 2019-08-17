package sun.nio.ch;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.misc.Unsafe;

class WindowsAsynchronousServerSocketChannelImpl extends AsynchronousServerSocketChannelImpl implements Iocp.OverlappedChannel {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final int DATA_BUFFER_SIZE = 88;
  
  private final long handle;
  
  private final int completionKey;
  
  private final Iocp iocp;
  
  private final PendingIoCache ioCache;
  
  private final long dataBuffer;
  
  private AtomicBoolean accepting = new AtomicBoolean();
  
  WindowsAsynchronousServerSocketChannelImpl(Iocp paramIocp) throws IOException {
    super(paramIocp);
    long l = IOUtil.fdVal(this.fd);
    try {
      i = paramIocp.associate(this, l);
    } catch (IOException iOException) {
      closesocket0(l);
      throw iOException;
    } 
    this.handle = l;
    this.completionKey = i;
    this.iocp = paramIocp;
    this.ioCache = new PendingIoCache();
    this.dataBuffer = unsafe.allocateMemory(88L);
  }
  
  public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong) { return this.ioCache.remove(paramLong); }
  
  void implClose() throws IOException {
    closesocket0(this.handle);
    this.ioCache.close();
    this.iocp.disassociate(this.completionKey);
    unsafe.freeMemory(this.dataBuffer);
  }
  
  public AsynchronousChannelGroupImpl group() { return this.iocp; }
  
  Future<AsynchronousSocketChannel> implAccept(Object paramObject, CompletionHandler<AsynchronousSocketChannel, Object> paramCompletionHandler) {
    if (!isOpen()) {
      ClosedChannelException closedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null)
        return CompletedFuture.withFailure(closedChannelException); 
      Invoker.invokeIndirectly(this, paramCompletionHandler, paramObject, null, closedChannelException);
      return null;
    } 
    if (isAcceptKilled())
      throw new RuntimeException("Accept not allowed due to cancellation"); 
    if (this.localAddress == null)
      throw new NotYetBoundException(); 
    WindowsAsynchronousSocketChannelImpl windowsAsynchronousSocketChannelImpl = null;
    IOException iOException = null;
    try {
      begin();
      windowsAsynchronousSocketChannelImpl = new WindowsAsynchronousSocketChannelImpl(this.iocp, false);
    } catch (IOException iOException1) {
      iOException = iOException1;
    } finally {
      end();
    } 
    if (iOException != null) {
      if (paramCompletionHandler == null)
        return CompletedFuture.withFailure(iOException); 
      Invoker.invokeIndirectly(this, paramCompletionHandler, paramObject, null, iOException);
      return null;
    } 
    AccessControlContext accessControlContext = (System.getSecurityManager() == null) ? null : AccessController.getContext();
    PendingFuture pendingFuture = new PendingFuture(this, paramCompletionHandler, paramObject);
    AcceptTask acceptTask = new AcceptTask(windowsAsynchronousSocketChannelImpl, accessControlContext, pendingFuture);
    pendingFuture.setContext(acceptTask);
    if (!this.accepting.compareAndSet(false, true))
      throw new AcceptPendingException(); 
    if (Iocp.supportsThreadAgnosticIo()) {
      acceptTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, acceptTask);
    } 
    return pendingFuture;
  }
  
  private static native void initIDs() throws IOException;
  
  private static native int accept0(long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws IOException;
  
  private static native void updateAcceptContext(long paramLong1, long paramLong2) throws IOException;
  
  private static native void closesocket0(long paramLong) throws IOException;
  
  static  {
    IOUtil.load();
    initIDs();
  }
  
  private class AcceptTask implements Runnable, Iocp.ResultHandler {
    private final WindowsAsynchronousSocketChannelImpl channel;
    
    private final AccessControlContext acc;
    
    private final PendingFuture<AsynchronousSocketChannel, Object> result;
    
    AcceptTask(WindowsAsynchronousSocketChannelImpl param1WindowsAsynchronousSocketChannelImpl, AccessControlContext param1AccessControlContext, PendingFuture<AsynchronousSocketChannel, Object> param1PendingFuture) {
      this.channel = param1WindowsAsynchronousSocketChannelImpl;
      this.acc = param1AccessControlContext;
      this.result = param1PendingFuture;
    }
    
    void enableAccept() throws IOException { WindowsAsynchronousServerSocketChannelImpl.this.accepting.set(false); }
    
    void closeChildChannel() throws IOException {
      try {
        this.channel.close();
      } catch (IOException iOException) {}
    }
    
    void finishAccept() throws IOException {
      WindowsAsynchronousServerSocketChannelImpl.updateAcceptContext(WindowsAsynchronousServerSocketChannelImpl.this.handle, this.channel.handle());
      InetSocketAddress inetSocketAddress1 = Net.localAddress(this.channel.fd);
      final InetSocketAddress remote = Net.remoteAddress(this.channel.fd);
      this.channel.setConnected(inetSocketAddress1, inetSocketAddress2);
      if (this.acc != null)
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                SecurityManager securityManager = System.getSecurityManager();
                securityManager.checkAccept(remote.getAddress().getHostAddress(), remote.getPort());
                return null;
              }
            }this.acc); 
    }
    
    public void run() throws IOException {
      long l = 0L;
      try {
        WindowsAsynchronousServerSocketChannelImpl.this.begin();
        try {
          this.channel.begin();
          synchronized (this.result) {
            l = WindowsAsynchronousServerSocketChannelImpl.this.ioCache.add(this.result);
            int i = WindowsAsynchronousServerSocketChannelImpl.accept0(WindowsAsynchronousServerSocketChannelImpl.this.handle, this.channel.handle(), l, WindowsAsynchronousServerSocketChannelImpl.this.dataBuffer);
            if (i == -2)
              return; 
            finishAccept();
            enableAccept();
            this.result.setResult(this.channel);
          } 
        } finally {
          this.channel.end();
        } 
      } catch (Throwable throwable) {
        if (l != 0L)
          WindowsAsynchronousServerSocketChannelImpl.this.ioCache.remove(l); 
        closeChildChannel();
        if (throwable instanceof ClosedChannelException)
          throwable = new AsynchronousCloseException(); 
        if (!(throwable instanceof IOException) && !(throwable instanceof SecurityException))
          throwable = new IOException(throwable); 
        enableAccept();
        this.result.setFailure(throwable);
      } finally {
        WindowsAsynchronousServerSocketChannelImpl.this.end();
      } 
      if (this.result.isCancelled())
        closeChildChannel(); 
      Invoker.invokeIndirectly(this.result);
    }
    
    public void completed(int param1Int, boolean param1Boolean) {
      try {
        if (WindowsAsynchronousServerSocketChannelImpl.this.iocp.isShutdown())
          throw new IOException(new ShutdownChannelGroupException()); 
        try {
          WindowsAsynchronousServerSocketChannelImpl.this.begin();
          try {
            this.channel.begin();
            finishAccept();
          } finally {
            this.channel.end();
          } 
        } finally {
          WindowsAsynchronousServerSocketChannelImpl.this.end();
        } 
        enableAccept();
        this.result.setResult(this.channel);
      } catch (Throwable throwable) {
        enableAccept();
        closeChildChannel();
        if (throwable instanceof ClosedChannelException)
          throwable = new AsynchronousCloseException(); 
        if (!(throwable instanceof IOException) && !(throwable instanceof SecurityException))
          throwable = new IOException(throwable); 
        this.result.setFailure(throwable);
      } 
      if (this.result.isCancelled())
        closeChildChannel(); 
      Invoker.invokeIndirectly(this.result);
    }
    
    public void failed(int param1Int, IOException param1IOException) {
      enableAccept();
      closeChildChannel();
      if (WindowsAsynchronousServerSocketChannelImpl.this.isOpen()) {
        this.result.setFailure(param1IOException);
      } else {
        this.result.setFailure(new AsynchronousCloseException());
      } 
      Invoker.invokeIndirectly(this.result);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\WindowsAsynchronousServerSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */