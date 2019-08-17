package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NetworkChannel;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.NetHooks;

abstract class AsynchronousSocketChannelImpl extends AsynchronousSocketChannel implements Cancellable, Groupable {
  protected final FileDescriptor fd = Net.socket(true);
  
  protected final Object stateLock = new Object();
  
  static final int ST_UNINITIALIZED = -1;
  
  static final int ST_UNCONNECTED = 0;
  
  static final int ST_PENDING = 1;
  
  static final int ST_CONNECTED = 2;
  
  private final Object readLock = new Object();
  
  private boolean reading;
  
  private boolean readShutdown;
  
  private boolean readKilled;
  
  private final Object writeLock = new Object();
  
  private boolean writing;
  
  private boolean writeShutdown;
  
  private boolean writeKilled;
  
  private final ReadWriteLock closeLock = new ReentrantReadWriteLock();
  
  private boolean isReuseAddress;
  
  AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl) throws IOException {
    super(paramAsynchronousChannelGroupImpl.provider());
    this.state = 0;
  }
  
  AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress) throws IOException {
    super(paramAsynchronousChannelGroupImpl.provider());
    this.state = 2;
    this.localAddress = Net.localAddress(paramFileDescriptor);
    this.remoteAddress = paramInetSocketAddress;
  }
  
  public final boolean isOpen() { return this.open; }
  
  final void begin() throws IOException {
    this.closeLock.readLock().lock();
    if (!isOpen())
      throw new ClosedChannelException(); 
  }
  
  final void end() throws IOException { this.closeLock.readLock().unlock(); }
  
  abstract void implClose() throws IOException;
  
  public final void close() throws IOException {
    this.closeLock.writeLock().lock();
    try {
      if (!this.open)
        return; 
      this.open = false;
    } finally {
      this.closeLock.writeLock().unlock();
    } 
    implClose();
  }
  
  final void enableReading(boolean paramBoolean) {
    synchronized (this.readLock) {
      this.reading = false;
      if (paramBoolean)
        this.readKilled = true; 
    } 
  }
  
  final void enableReading() throws IOException { enableReading(false); }
  
  final void enableWriting(boolean paramBoolean) {
    synchronized (this.writeLock) {
      this.writing = false;
      if (paramBoolean)
        this.writeKilled = true; 
    } 
  }
  
  final void enableWriting() throws IOException { enableWriting(false); }
  
  final void killReading() throws IOException {
    synchronized (this.readLock) {
      this.readKilled = true;
    } 
  }
  
  final void killWriting() throws IOException {
    synchronized (this.writeLock) {
      this.writeKilled = true;
    } 
  }
  
  final void killConnect() throws IOException {
    killReading();
    killWriting();
  }
  
  abstract <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler);
  
  public final Future<Void> connect(SocketAddress paramSocketAddress) { return implConnect(paramSocketAddress, null, null); }
  
  public final <A> void connect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler) {
    if (paramCompletionHandler == null)
      throw new NullPointerException("'handler' is null"); 
    implConnect(paramSocketAddress, paramA, paramCompletionHandler);
  }
  
  abstract <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler);
  
  private <V extends Number, A> Future<V> read(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler) {
    if (!isOpen()) {
      ClosedChannelException closedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null)
        return CompletedFuture.withFailure(closedChannelException); 
      Invoker.invoke(this, paramCompletionHandler, paramA, null, closedChannelException);
      return null;
    } 
    if (this.remoteAddress == null)
      throw new NotYetConnectedException(); 
    boolean bool1 = (paramBoolean || paramByteBuffer.hasRemaining()) ? 1 : 0;
    boolean bool2 = false;
    synchronized (this.readLock) {
      if (this.readKilled)
        throw new IllegalStateException("Reading not allowed due to timeout or cancellation"); 
      if (this.reading)
        throw new ReadPendingException(); 
      if (this.readShutdown) {
        bool2 = true;
      } else if (bool1) {
        this.reading = true;
      } 
    } 
    if (bool2 || !bool1) {
      Integer integer;
      if (paramBoolean) {
        integer = bool2 ? Long.valueOf(-1L) : Long.valueOf(0L);
      } else {
        integer = Integer.valueOf(bool2 ? -1 : 0);
      } 
      if (paramCompletionHandler == null)
        return CompletedFuture.withResult(integer); 
      Invoker.invoke(this, paramCompletionHandler, paramA, integer, null);
      return null;
    } 
    return implRead(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final Future<Integer> read(ByteBuffer paramByteBuffer) {
    if (paramByteBuffer.isReadOnly())
      throw new IllegalArgumentException("Read-only buffer"); 
    return read(false, paramByteBuffer, null, 0L, TimeUnit.MILLISECONDS, null, null);
  }
  
  public final <A> void read(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler) {
    if (paramCompletionHandler == null)
      throw new NullPointerException("'handler' is null"); 
    if (paramByteBuffer.isReadOnly())
      throw new IllegalArgumentException("Read-only buffer"); 
    read(false, paramByteBuffer, null, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final <A> void read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler) {
    if (paramCompletionHandler == null)
      throw new NullPointerException("'handler' is null"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    ByteBuffer[] arrayOfByteBuffer = Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2);
    for (byte b = 0; b < arrayOfByteBuffer.length; b++) {
      if (arrayOfByteBuffer[b].isReadOnly())
        throw new IllegalArgumentException("Read-only buffer"); 
    } 
    read(true, null, arrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  abstract <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler);
  
  private <V extends Number, A> Future<V> write(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler) {
    boolean bool1 = (paramBoolean || paramByteBuffer.hasRemaining()) ? 1 : 0;
    boolean bool2 = false;
    if (isOpen()) {
      if (this.remoteAddress == null)
        throw new NotYetConnectedException(); 
      synchronized (this.writeLock) {
        if (this.writeKilled)
          throw new IllegalStateException("Writing not allowed due to timeout or cancellation"); 
        if (this.writing)
          throw new WritePendingException(); 
        if (this.writeShutdown) {
          bool2 = true;
        } else if (bool1) {
          this.writing = true;
        } 
      } 
    } else {
      bool2 = true;
    } 
    if (bool2) {
      ClosedChannelException closedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null)
        return CompletedFuture.withFailure(closedChannelException); 
      Invoker.invoke(this, paramCompletionHandler, paramA, null, closedChannelException);
      return null;
    } 
    if (!bool1) {
      Long long = paramBoolean ? Long.valueOf(0L) : Integer.valueOf(0);
      if (paramCompletionHandler == null)
        return CompletedFuture.withResult(long); 
      Invoker.invoke(this, paramCompletionHandler, paramA, long, null);
      return null;
    } 
    return implWrite(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final Future<Integer> write(ByteBuffer paramByteBuffer) { return write(false, paramByteBuffer, null, 0L, TimeUnit.MILLISECONDS, null, null); }
  
  public final <A> void write(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler) {
    if (paramCompletionHandler == null)
      throw new NullPointerException("'handler' is null"); 
    write(false, paramByteBuffer, null, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final <A> void write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler) {
    if (paramCompletionHandler == null)
      throw new NullPointerException("'handler' is null"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    paramArrayOfByteBuffer = Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2);
    write(true, null, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final AsynchronousSocketChannel bind(SocketAddress paramSocketAddress) throws IOException {
    try {
      begin();
      synchronized (this.stateLock) {
        if (this.state == 1)
          throw new ConnectionPendingException(); 
        if (this.localAddress != null)
          throw new AlreadyBoundException(); 
        InetSocketAddress inetSocketAddress = (paramSocketAddress == null) ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          securityManager.checkListen(inetSocketAddress.getPort()); 
        NetHooks.beforeTcpBind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
        Net.bind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
        this.localAddress = Net.localAddress(this.fd);
      } 
    } finally {
      end();
    } 
    return this;
  }
  
  public final SocketAddress getLocalAddress() throws IOException {
    if (!isOpen())
      throw new ClosedChannelException(); 
    return Net.getRevealedLocalAddress(this.localAddress);
  }
  
  public final <T> AsynchronousSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException {
    if (paramSocketOption == null)
      throw new NullPointerException(); 
    if (!supportedOptions().contains(paramSocketOption))
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported"); 
    try {
      begin();
      if (this.writeShutdown)
        throw new IOException("Connection has been shutdown for writing"); 
      if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
        this.isReuseAddress = ((Boolean)paramT).booleanValue();
      } else {
        Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
      } 
      return this;
    } finally {
      end();
    } 
  }
  
  public final <T> T getOption(SocketOption<T> paramSocketOption) throws IOException {
    if (paramSocketOption == null)
      throw new NullPointerException(); 
    if (!supportedOptions().contains(paramSocketOption))
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported"); 
    try {
      begin();
      if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
        bool = Boolean.valueOf(this.isReuseAddress);
        return (T)bool;
      } 
      object = Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
      return (T)object;
    } finally {
      end();
    } 
  }
  
  public final Set<SocketOption<?>> supportedOptions() { return DefaultOptionsHolder.defaultOptions; }
  
  public final SocketAddress getRemoteAddress() throws IOException {
    if (!isOpen())
      throw new ClosedChannelException(); 
    return this.remoteAddress;
  }
  
  public final AsynchronousSocketChannel shutdownInput() throws IOException {
    try {
      begin();
      if (this.remoteAddress == null)
        throw new NotYetConnectedException(); 
      synchronized (this.readLock) {
        if (!this.readShutdown) {
          Net.shutdown(this.fd, 0);
          this.readShutdown = true;
        } 
      } 
    } finally {
      end();
    } 
    return this;
  }
  
  public final AsynchronousSocketChannel shutdownOutput() throws IOException {
    try {
      begin();
      if (this.remoteAddress == null)
        throw new NotYetConnectedException(); 
      synchronized (this.writeLock) {
        if (!this.writeShutdown) {
          Net.shutdown(this.fd, 1);
          this.writeShutdown = true;
        } 
      } 
    } finally {
      end();
    } 
    return this;
  }
  
  public final String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getClass().getName());
    stringBuilder.append('[');
    synchronized (this.stateLock) {
      if (!isOpen()) {
        stringBuilder.append("closed");
      } else {
        switch (this.state) {
          case 0:
            stringBuilder.append("unconnected");
            break;
          case 1:
            stringBuilder.append("connection-pending");
            break;
          case 2:
            stringBuilder.append("connected");
            if (this.readShutdown)
              stringBuilder.append(" ishut"); 
            if (this.writeShutdown)
              stringBuilder.append(" oshut"); 
            break;
        } 
        if (this.localAddress != null) {
          stringBuilder.append(" local=");
          stringBuilder.append(Net.getRevealedLocalAddressAsString(this.localAddress));
        } 
        if (this.remoteAddress != null) {
          stringBuilder.append(" remote=");
          stringBuilder.append(this.remoteAddress.toString());
        } 
      } 
    } 
    stringBuilder.append(']');
    return stringBuilder.toString();
  }
  
  private static class DefaultOptionsHolder {
    static final Set<SocketOption<?>> defaultOptions = defaultOptions();
    
    private static Set<SocketOption<?>> defaultOptions() {
      HashSet hashSet = new HashSet(5);
      hashSet.add(StandardSocketOptions.SO_SNDBUF);
      hashSet.add(StandardSocketOptions.SO_RCVBUF);
      hashSet.add(StandardSocketOptions.SO_KEEPALIVE);
      hashSet.add(StandardSocketOptions.SO_REUSEADDR);
      hashSet.add(StandardSocketOptions.TCP_NODELAY);
      if (ExtendedOptionsImpl.flowSupported())
        hashSet.add(ExtendedSocketOptions.SO_FLOW_SLA); 
      return Collections.unmodifiableSet(hashSet);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\AsynchronousSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */