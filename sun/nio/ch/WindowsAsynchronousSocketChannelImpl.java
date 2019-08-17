package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

class WindowsAsynchronousSocketChannelImpl extends AsynchronousSocketChannelImpl implements Iocp.OverlappedChannel {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static int addressSize = unsafe.addressSize();
  
  private static final int SIZEOF_WSABUF = dependsArch(8, 16);
  
  private static final int OFFSETOF_LEN = 0;
  
  private static final int OFFSETOF_BUF = dependsArch(4, 8);
  
  private static final int MAX_WSABUF = 16;
  
  private static final int SIZEOF_WSABUFARRAY = 16 * SIZEOF_WSABUF;
  
  final long handle;
  
  private final Iocp iocp;
  
  private final int completionKey;
  
  private final PendingIoCache ioCache;
  
  private final long readBufferArray;
  
  private final long writeBufferArray;
  
  private static int dependsArch(int paramInt1, int paramInt2) { return (addressSize == 4) ? paramInt1 : paramInt2; }
  
  WindowsAsynchronousSocketChannelImpl(Iocp paramIocp, boolean paramBoolean) throws IOException {
    super(paramIocp);
    long l = IOUtil.fdVal(this.fd);
    int i = 0;
    try {
      i = paramIocp.associate(this, l);
    } catch (ShutdownChannelGroupException shutdownChannelGroupException) {
      if (paramBoolean) {
        closesocket0(l);
        throw shutdownChannelGroupException;
      } 
    } catch (IOException iOException) {
      closesocket0(l);
      throw iOException;
    } 
    this.handle = l;
    this.iocp = paramIocp;
    this.completionKey = i;
    this.ioCache = new PendingIoCache();
    this.readBufferArray = unsafe.allocateMemory(SIZEOF_WSABUFARRAY);
    this.writeBufferArray = unsafe.allocateMemory(SIZEOF_WSABUFARRAY);
  }
  
  WindowsAsynchronousSocketChannelImpl(Iocp paramIocp) throws IOException { this(paramIocp, true); }
  
  public AsynchronousChannelGroupImpl group() { return this.iocp; }
  
  public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong) { return this.ioCache.remove(paramLong); }
  
  long handle() { return this.handle; }
  
  void setConnected(InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2) {
    synchronized (this.stateLock) {
      this.state = 2;
      this.localAddress = paramInetSocketAddress1;
      this.remoteAddress = paramInetSocketAddress2;
    } 
  }
  
  void implClose() throws IOException {
    closesocket0(this.handle);
    this.ioCache.close();
    unsafe.freeMemory(this.readBufferArray);
    unsafe.freeMemory(this.writeBufferArray);
    if (this.completionKey != 0)
      this.iocp.disassociate(this.completionKey); 
  }
  
  public void onCancel(PendingFuture<?, ?> paramPendingFuture) {
    if (paramPendingFuture.getContext() instanceof ConnectTask)
      killConnect(); 
    if (paramPendingFuture.getContext() instanceof ReadTask)
      killReading(); 
    if (paramPendingFuture.getContext() instanceof WriteTask)
      killWriting(); 
  }
  
  private void doPrivilegedBind(final SocketAddress sa) throws IOException {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
              WindowsAsynchronousSocketChannelImpl.this.bind(sa);
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
  
  <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler) {
    if (!isOpen()) {
      ClosedChannelException closedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null)
        return CompletedFuture.withFailure(closedChannelException); 
      Invoker.invoke(this, paramCompletionHandler, paramA, null, closedChannelException);
      return null;
    } 
    InetSocketAddress inetSocketAddress = Net.checkAddress(paramSocketAddress);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkConnect(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort()); 
    IOException iOException = null;
    synchronized (this.stateLock) {
      if (this.state == 2)
        throw new AlreadyConnectedException(); 
      if (this.state == 1)
        throw new ConnectionPendingException(); 
      if (this.localAddress == null)
        try {
          InetSocketAddress inetSocketAddress1 = new InetSocketAddress(0);
          if (securityManager == null) {
            bind(inetSocketAddress1);
          } else {
            doPrivilegedBind(inetSocketAddress1);
          } 
        } catch (IOException iOException1) {
          iOException = iOException1;
        }  
      if (iOException == null)
        this.state = 1; 
    } 
    if (iOException != null) {
      try {
        close();
      } catch (IOException iOException1) {}
      if (paramCompletionHandler == null)
        return CompletedFuture.withFailure(iOException); 
      Invoker.invoke(this, paramCompletionHandler, paramA, null, iOException);
      return null;
    } 
    PendingFuture pendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    ConnectTask connectTask = new ConnectTask(inetSocketAddress, pendingFuture);
    pendingFuture.setContext(connectTask);
    if (Iocp.supportsThreadAgnosticIo()) {
      connectTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, connectTask);
    } 
    return pendingFuture;
  }
  
  <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler) {
    ByteBuffer[] arrayOfByteBuffer;
    PendingFuture pendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    if (paramBoolean) {
      arrayOfByteBuffer = paramArrayOfByteBuffer;
    } else {
      arrayOfByteBuffer = new ByteBuffer[1];
      arrayOfByteBuffer[0] = paramByteBuffer;
    } 
    final ReadTask readTask = new ReadTask(arrayOfByteBuffer, paramBoolean, pendingFuture);
    pendingFuture.setContext(readTask);
    if (paramLong > 0L) {
      Future future = this.iocp.schedule(new Runnable() {
            public void run() throws IOException { readTask.timeout(); }
          },  paramLong, paramTimeUnit);
      pendingFuture.setTimeoutTask(future);
    } 
    if (Iocp.supportsThreadAgnosticIo()) {
      readTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, readTask);
    } 
    return pendingFuture;
  }
  
  <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler) {
    ByteBuffer[] arrayOfByteBuffer;
    PendingFuture pendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    if (paramBoolean) {
      arrayOfByteBuffer = paramArrayOfByteBuffer;
    } else {
      arrayOfByteBuffer = new ByteBuffer[1];
      arrayOfByteBuffer[0] = paramByteBuffer;
    } 
    final WriteTask writeTask = new WriteTask(arrayOfByteBuffer, paramBoolean, pendingFuture);
    pendingFuture.setContext(writeTask);
    if (paramLong > 0L) {
      Future future = this.iocp.schedule(new Runnable() {
            public void run() throws IOException { writeTask.timeout(); }
          },  paramLong, paramTimeUnit);
      pendingFuture.setTimeoutTask(future);
    } 
    if (Iocp.supportsThreadAgnosticIo()) {
      writeTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, writeTask);
    } 
    return pendingFuture;
  }
  
  private static native void initIDs() throws IOException;
  
  private static native int connect0(long paramLong1, boolean paramBoolean, InetAddress paramInetAddress, int paramInt, long paramLong2) throws IOException;
  
  private static native void updateConnectContext(long paramLong) throws IOException;
  
  private static native int read0(long paramLong1, int paramInt, long paramLong2, long paramLong3) throws IOException;
  
  private static native int write0(long paramLong1, int paramInt, long paramLong2, long paramLong3) throws IOException;
  
  private static native void shutdown0(long paramLong, int paramInt) throws IOException;
  
  private static native void closesocket0(long paramLong) throws IOException;
  
  static  {
    IOUtil.load();
    initIDs();
  }
  
  private class ConnectTask<A> extends Object implements Runnable, Iocp.ResultHandler {
    private final InetSocketAddress remote;
    
    private final PendingFuture<Void, A> result;
    
    ConnectTask(InetSocketAddress param1InetSocketAddress, PendingFuture<Void, A> param1PendingFuture) {
      this.remote = param1InetSocketAddress;
      this.result = param1PendingFuture;
    }
    
    private void closeChannel() throws IOException {
      try {
        WindowsAsynchronousSocketChannelImpl.this.close();
      } catch (IOException iOException) {}
    }
    
    private IOException toIOException(Throwable param1Throwable) {
      if (param1Throwable instanceof IOException) {
        if (param1Throwable instanceof ClosedChannelException)
          param1Throwable = new AsynchronousCloseException(); 
        return (IOException)param1Throwable;
      } 
      return new IOException(param1Throwable);
    }
    
    private void afterConnect() throws IOException {
      WindowsAsynchronousSocketChannelImpl.updateConnectContext(WindowsAsynchronousSocketChannelImpl.this.handle);
      synchronized (WindowsAsynchronousSocketChannelImpl.this.stateLock) {
        WindowsAsynchronousSocketChannelImpl.this.state = 2;
        WindowsAsynchronousSocketChannelImpl.this.remoteAddress = this.remote;
      } 
    }
    
    public void run() throws IOException {
      long l = 0L;
      Throwable throwable = null;
      try {
        WindowsAsynchronousSocketChannelImpl.this.begin();
        synchronized (this.result) {
          l = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
          int i = WindowsAsynchronousSocketChannelImpl.connect0(WindowsAsynchronousSocketChannelImpl.this.handle, Net.isIPv6Available(), this.remote.getAddress(), this.remote.getPort(), l);
          if (i == -2)
            return; 
          afterConnect();
          this.result.setResult(null);
        } 
      } catch (Throwable throwable1) {
        if (l != 0L)
          WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(l); 
        throwable = throwable1;
      } finally {
        WindowsAsynchronousSocketChannelImpl.this.end();
      } 
      if (throwable != null) {
        closeChannel();
        this.result.setFailure(toIOException(throwable));
      } 
      Invoker.invoke(this.result);
    }
    
    public void completed(int param1Int, boolean param1Boolean) {
      Throwable throwable = null;
      try {
        WindowsAsynchronousSocketChannelImpl.this.begin();
        afterConnect();
        this.result.setResult(null);
      } catch (Throwable throwable1) {
        throwable = throwable1;
      } finally {
        WindowsAsynchronousSocketChannelImpl.this.end();
      } 
      if (throwable != null) {
        closeChannel();
        this.result.setFailure(toIOException(throwable));
      } 
      if (param1Boolean) {
        Invoker.invokeUnchecked(this.result);
      } else {
        Invoker.invoke(this.result);
      } 
    }
    
    public void failed(int param1Int, IOException param1IOException) {
      if (WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
        closeChannel();
        this.result.setFailure(param1IOException);
      } else {
        this.result.setFailure(new AsynchronousCloseException());
      } 
      Invoker.invoke(this.result);
    }
  }
  
  private class ReadTask<V, A> extends Object implements Runnable, Iocp.ResultHandler {
    private final ByteBuffer[] bufs;
    
    private final int numBufs;
    
    private final boolean scatteringRead;
    
    private final PendingFuture<V, A> result;
    
    private ByteBuffer[] shadow;
    
    ReadTask(ByteBuffer[] param1ArrayOfByteBuffer, boolean param1Boolean, PendingFuture<V, A> param1PendingFuture) {
      this.bufs = param1ArrayOfByteBuffer;
      this.numBufs = (param1ArrayOfByteBuffer.length > 16) ? 16 : param1ArrayOfByteBuffer.length;
      this.scatteringRead = param1Boolean;
      this.result = param1PendingFuture;
    }
    
    void prepareBuffers() throws IOException {
      this.shadow = new ByteBuffer[this.numBufs];
      long l = WindowsAsynchronousSocketChannelImpl.this.readBufferArray;
      for (byte b = 0; b < this.numBufs; b++) {
        long l1;
        ByteBuffer byteBuffer = this.bufs[b];
        int i = byteBuffer.position();
        int j = byteBuffer.limit();
        assert i <= j;
        int k = (i <= j) ? (j - i) : 0;
        if (!(byteBuffer instanceof DirectBuffer)) {
          ByteBuffer byteBuffer1 = Util.getTemporaryDirectBuffer(k);
          this.shadow[b] = byteBuffer1;
          l1 = ((DirectBuffer)byteBuffer1).address();
        } else {
          this.shadow[b] = byteBuffer;
          l1 = ((DirectBuffer)byteBuffer).address() + i;
        } 
        unsafe.putAddress(l + OFFSETOF_BUF, l1);
        unsafe.putInt(l + 0L, k);
        l += SIZEOF_WSABUF;
      } 
    }
    
    void updateBuffers(int param1Int) {
      byte b;
      for (b = 0; b < this.numBufs; b++) {
        ByteBuffer byteBuffer = this.shadow[b];
        int i = byteBuffer.position();
        int j = byteBuffer.remaining();
        if (param1Int >= j) {
          param1Int -= j;
          int k = i + j;
          try {
            byteBuffer.position(k);
          } catch (IllegalArgumentException illegalArgumentException) {}
        } else {
          if (param1Int > 0) {
            assert (i + param1Int) < 2147483647L;
            int k = i + param1Int;
            try {
              byteBuffer.position(k);
              break;
            } catch (IllegalArgumentException illegalArgumentException) {
              break;
            } 
          } 
          break;
        } 
      } 
      for (b = 0; b < this.numBufs; b++) {
        if (!(this.bufs[b] instanceof DirectBuffer)) {
          this.shadow[b].flip();
          try {
            this.bufs[b].put(this.shadow[b]);
          } catch (BufferOverflowException bufferOverflowException) {}
        } 
      } 
    }
    
    void releaseBuffers() throws IOException {
      for (byte b = 0; b < this.numBufs; b++) {
        if (!(this.bufs[b] instanceof DirectBuffer))
          Util.releaseTemporaryDirectBuffer(this.shadow[b]); 
      } 
    }
    
    public void run() throws IOException {
      l = 0L;
      bool1 = false;
      bool2 = false;
      try {
        WindowsAsynchronousSocketChannelImpl.this.begin();
        prepareBuffers();
        bool1 = true;
        l = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
        int i = WindowsAsynchronousSocketChannelImpl.read0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.readBufferArray, l);
        if (i == -2) {
          bool2 = true;
          return;
        } 
        if (i == -1) {
          WindowsAsynchronousSocketChannelImpl.this.enableReading();
          if (this.scatteringRead) {
            this.result.setResult(Long.valueOf(-1L));
          } else {
            this.result.setResult(Integer.valueOf(-1));
          } 
        } else {
          throw new InternalError("Read completed immediately");
        } 
      } catch (Throwable throwable) {
        WindowsAsynchronousSocketChannelImpl.this.enableReading();
        if (throwable instanceof ClosedChannelException)
          throwable = new AsynchronousCloseException(); 
        if (!(throwable instanceof IOException))
          throwable = new IOException(throwable); 
        this.result.setFailure(throwable);
      } finally {
        if (!bool2) {
          if (l != 0L)
            WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(l); 
          if (bool1)
            releaseBuffers(); 
        } 
        WindowsAsynchronousSocketChannelImpl.this.end();
      } 
      Invoker.invoke(this.result);
    }
    
    public void completed(int param1Int, boolean param1Boolean) {
      if (param1Int == 0) {
        param1Int = -1;
      } else {
        updateBuffers(param1Int);
      } 
      releaseBuffers();
      synchronized (this.result) {
        if (this.result.isDone())
          return; 
        WindowsAsynchronousSocketChannelImpl.this.enableReading();
        if (this.scatteringRead) {
          this.result.setResult(Long.valueOf(param1Int));
        } else {
          this.result.setResult(Integer.valueOf(param1Int));
        } 
      } 
      if (param1Boolean) {
        Invoker.invokeUnchecked(this.result);
      } else {
        Invoker.invoke(this.result);
      } 
    }
    
    public void failed(int param1Int, IOException param1IOException) {
      releaseBuffers();
      if (!WindowsAsynchronousSocketChannelImpl.this.isOpen())
        param1IOException = new AsynchronousCloseException(); 
      synchronized (this.result) {
        if (this.result.isDone())
          return; 
        WindowsAsynchronousSocketChannelImpl.this.enableReading();
        this.result.setFailure(param1IOException);
      } 
      Invoker.invoke(this.result);
    }
    
    void timeout() throws IOException {
      synchronized (this.result) {
        if (this.result.isDone())
          return; 
        WindowsAsynchronousSocketChannelImpl.this.enableReading(true);
        this.result.setFailure(new InterruptedByTimeoutException());
      } 
      Invoker.invoke(this.result);
    }
  }
  
  private class WriteTask<V, A> extends Object implements Runnable, Iocp.ResultHandler {
    private final ByteBuffer[] bufs;
    
    private final int numBufs;
    
    private final boolean gatheringWrite;
    
    private final PendingFuture<V, A> result;
    
    private ByteBuffer[] shadow;
    
    WriteTask(ByteBuffer[] param1ArrayOfByteBuffer, boolean param1Boolean, PendingFuture<V, A> param1PendingFuture) {
      this.bufs = param1ArrayOfByteBuffer;
      this.numBufs = (param1ArrayOfByteBuffer.length > 16) ? 16 : param1ArrayOfByteBuffer.length;
      this.gatheringWrite = param1Boolean;
      this.result = param1PendingFuture;
    }
    
    void prepareBuffers() throws IOException {
      this.shadow = new ByteBuffer[this.numBufs];
      long l = WindowsAsynchronousSocketChannelImpl.this.writeBufferArray;
      for (byte b = 0; b < this.numBufs; b++) {
        long l1;
        ByteBuffer byteBuffer = this.bufs[b];
        int i = byteBuffer.position();
        int j = byteBuffer.limit();
        assert i <= j;
        int k = (i <= j) ? (j - i) : 0;
        if (!(byteBuffer instanceof DirectBuffer)) {
          ByteBuffer byteBuffer1 = Util.getTemporaryDirectBuffer(k);
          byteBuffer1.put(byteBuffer);
          byteBuffer1.flip();
          byteBuffer.position(i);
          this.shadow[b] = byteBuffer1;
          l1 = ((DirectBuffer)byteBuffer1).address();
        } else {
          this.shadow[b] = byteBuffer;
          l1 = ((DirectBuffer)byteBuffer).address() + i;
        } 
        unsafe.putAddress(l + OFFSETOF_BUF, l1);
        unsafe.putInt(l + 0L, k);
        l += SIZEOF_WSABUF;
      } 
    }
    
    void updateBuffers(int param1Int) {
      for (byte b = 0; b < this.numBufs; b++) {
        ByteBuffer byteBuffer = this.bufs[b];
        int i = byteBuffer.position();
        int j = byteBuffer.limit();
        int k = (i <= j) ? (j - i) : j;
        if (param1Int >= k) {
          param1Int -= k;
          int m = i + k;
          try {
            byteBuffer.position(m);
          } catch (IllegalArgumentException illegalArgumentException) {}
        } else {
          if (param1Int > 0) {
            assert (i + param1Int) < 2147483647L;
            int m = i + param1Int;
            try {
              byteBuffer.position(m);
              break;
            } catch (IllegalArgumentException illegalArgumentException) {
              break;
            } 
          } 
          break;
        } 
      } 
    }
    
    void releaseBuffers() throws IOException {
      for (byte b = 0; b < this.numBufs; b++) {
        if (!(this.bufs[b] instanceof DirectBuffer))
          Util.releaseTemporaryDirectBuffer(this.shadow[b]); 
      } 
    }
    
    public void run() throws IOException {
      l = 0L;
      bool1 = false;
      bool2 = false;
      boolean bool3 = false;
      try {
        WindowsAsynchronousSocketChannelImpl.this.begin();
        prepareBuffers();
        bool1 = true;
        l = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
        int i = WindowsAsynchronousSocketChannelImpl.write0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.writeBufferArray, l);
        if (i == -2) {
          bool2 = true;
          return;
        } 
        if (i == -1) {
          bool3 = true;
          throw new ClosedChannelException();
        } 
        throw new InternalError("Write completed immediately");
      } catch (Throwable throwable) {
        WindowsAsynchronousSocketChannelImpl.this.enableWriting();
        if (!bool3 && throwable instanceof ClosedChannelException)
          throwable = new AsynchronousCloseException(); 
        if (!(throwable instanceof IOException))
          throwable = new IOException(throwable); 
        this.result.setFailure(throwable);
      } finally {
        if (!bool2) {
          if (l != 0L)
            WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(l); 
          if (bool1)
            releaseBuffers(); 
        } 
        WindowsAsynchronousSocketChannelImpl.this.end();
      } 
      Invoker.invoke(this.result);
    }
    
    public void completed(int param1Int, boolean param1Boolean) {
      updateBuffers(param1Int);
      releaseBuffers();
      synchronized (this.result) {
        if (this.result.isDone())
          return; 
        WindowsAsynchronousSocketChannelImpl.this.enableWriting();
        if (this.gatheringWrite) {
          this.result.setResult(Long.valueOf(param1Int));
        } else {
          this.result.setResult(Integer.valueOf(param1Int));
        } 
      } 
      if (param1Boolean) {
        Invoker.invokeUnchecked(this.result);
      } else {
        Invoker.invoke(this.result);
      } 
    }
    
    public void failed(int param1Int, IOException param1IOException) {
      releaseBuffers();
      if (!WindowsAsynchronousSocketChannelImpl.this.isOpen())
        param1IOException = new AsynchronousCloseException(); 
      synchronized (this.result) {
        if (this.result.isDone())
          return; 
        WindowsAsynchronousSocketChannelImpl.this.enableWriting();
        this.result.setFailure(param1IOException);
      } 
      Invoker.invoke(this.result);
    }
    
    void timeout() throws IOException {
      synchronized (this.result) {
        if (this.result.isDone())
          return; 
        WindowsAsynchronousSocketChannelImpl.this.enableWriting(true);
        this.result.setFailure(new InterruptedByTimeoutException());
      } 
      Invoker.invoke(this.result);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\WindowsAsynchronousSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */