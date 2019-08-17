package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NetworkChannel;
import java.nio.channels.NoConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.NetHooks;

class SocketChannelImpl extends SocketChannel implements SelChImpl {
  private static NativeDispatcher nd;
  
  private final FileDescriptor fd = Net.socket(true);
  
  private final int fdVal = IOUtil.fdVal(this.fd);
  
  private final Object readLock = new Object();
  
  private final Object writeLock = new Object();
  
  private final Object stateLock = new Object();
  
  private boolean isReuseAddress;
  
  private static final int ST_UNINITIALIZED = -1;
  
  private static final int ST_UNCONNECTED = 0;
  
  private static final int ST_PENDING = 1;
  
  private static final int ST_CONNECTED = 2;
  
  private static final int ST_KILLPENDING = 3;
  
  private static final int ST_KILLED = 4;
  
  private int state = -1;
  
  private InetSocketAddress localAddress;
  
  private InetSocketAddress remoteAddress;
  
  private boolean isInputOpen = true;
  
  private boolean isOutputOpen = true;
  
  private boolean readyToConnect = false;
  
  private Socket socket;
  
  SocketChannelImpl(SelectorProvider paramSelectorProvider) throws IOException {
    super(paramSelectorProvider);
    this.state = 0;
  }
  
  SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, boolean paramBoolean) throws IOException {
    super(paramSelectorProvider);
    this.state = 0;
    if (paramBoolean)
      this.localAddress = Net.localAddress(paramFileDescriptor); 
  }
  
  SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress) throws IOException {
    super(paramSelectorProvider);
    this.state = 2;
    this.localAddress = Net.localAddress(paramFileDescriptor);
    this.remoteAddress = paramInetSocketAddress;
  }
  
  public Socket socket() {
    synchronized (this.stateLock) {
      if (this.socket == null)
        this.socket = SocketAdaptor.create(this); 
      return this.socket;
    } 
  }
  
  public SocketAddress getLocalAddress() throws IOException {
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      return Net.getRevealedLocalAddress(this.localAddress);
    } 
  }
  
  public SocketAddress getRemoteAddress() throws IOException {
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      return this.remoteAddress;
    } 
  }
  
  public <T> SocketChannel setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException {
    if (paramSocketOption == null)
      throw new NullPointerException(); 
    if (!supportedOptions().contains(paramSocketOption))
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported"); 
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (paramSocketOption == StandardSocketOptions.IP_TOS) {
        StandardProtocolFamily standardProtocolFamily = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
        Net.setSocketOption(this.fd, standardProtocolFamily, paramSocketOption, paramT);
        return this;
      } 
      if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
        this.isReuseAddress = ((Boolean)paramT).booleanValue();
        return this;
      } 
      Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
      return this;
    } 
  }
  
  public <T> T getOption(SocketOption<T> paramSocketOption) throws IOException {
    if (paramSocketOption == null)
      throw new NullPointerException(); 
    if (!supportedOptions().contains(paramSocketOption))
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported"); 
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind())
        return (T)Boolean.valueOf(this.isReuseAddress); 
      if (paramSocketOption == StandardSocketOptions.IP_TOS) {
        StandardProtocolFamily standardProtocolFamily = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
        return (T)Net.getSocketOption(this.fd, standardProtocolFamily, paramSocketOption);
      } 
      return (T)Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
    } 
  }
  
  public final Set<SocketOption<?>> supportedOptions() { return DefaultOptionsHolder.defaultOptions; }
  
  private boolean ensureReadOpen() throws ClosedChannelException {
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (!isConnected())
        throw new NotYetConnectedException(); 
      if (!this.isInputOpen)
        return false; 
      return true;
    } 
  }
  
  private void ensureWriteOpen() throws ClosedChannelException {
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (!this.isOutputOpen)
        throw new ClosedChannelException(); 
      if (!isConnected())
        throw new NotYetConnectedException(); 
    } 
  }
  
  private void readerCleanup() throws ClosedChannelException {
    synchronized (this.stateLock) {
      this.readerThread = 0L;
      if (this.state == 3)
        kill(); 
    } 
  }
  
  private void writerCleanup() throws ClosedChannelException {
    synchronized (this.stateLock) {
      this.writerThread = 0L;
      if (this.state == 3)
        kill(); 
    } 
  }
  
  public int read(ByteBuffer paramByteBuffer) throws IOException {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    synchronized (this.readLock) {
      if (!ensureReadOpen())
        return -1; 
      i = 0;
      try {
        begin();
        synchronized (this.stateLock) {
          if (!isOpen())
            return 0; 
          this.readerThread = NativeThread.current();
        } 
        while (true) {
          i = IOUtil.read(this.fd, paramByteBuffer, -1L, nd);
          if (i == -3 && isOpen())
            continue; 
          break;
        } 
        return IOStatus.normalize(i);
      } finally {
        readerCleanup();
        end((i > 0 || i == -2));
        synchronized (this.stateLock) {
          if (i <= 0 && !this.isInputOpen)
            return -1; 
        } 
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    synchronized (this.readLock) {
      if (!ensureReadOpen())
        return -1L; 
      l = 0L;
      try {
        begin();
        synchronized (this.stateLock) {
          if (!isOpen())
            return 0L; 
          this.readerThread = NativeThread.current();
        } 
        while (true) {
          l = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
          if (l == -3L && isOpen())
            continue; 
          break;
        } 
        return IOStatus.normalize(l);
      } finally {
        readerCleanup();
        end((l > 0L || l == -2L));
        synchronized (this.stateLock) {
          if (l <= 0L && !this.isInputOpen)
            return -1L; 
        } 
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public int write(ByteBuffer paramByteBuffer) throws IOException {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    synchronized (this.writeLock) {
      ensureWriteOpen();
      i = 0;
      try {
        begin();
        synchronized (this.stateLock) {
          if (!isOpen())
            return 0; 
          this.writerThread = NativeThread.current();
        } 
        while (true) {
          i = IOUtil.write(this.fd, paramByteBuffer, -1L, nd);
          if (i == -3 && isOpen())
            continue; 
          break;
        } 
        return IOStatus.normalize(i);
      } finally {
        writerCleanup();
        end((i > 0 || i == -2));
        synchronized (this.stateLock) {
          if (i <= 0 && !this.isOutputOpen)
            throw new AsynchronousCloseException(); 
        } 
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    synchronized (this.writeLock) {
      ensureWriteOpen();
      l = 0L;
      try {
        begin();
        synchronized (this.stateLock) {
          if (!isOpen())
            return 0L; 
          this.writerThread = NativeThread.current();
        } 
        while (true) {
          l = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
          if (l == -3L && isOpen())
            continue; 
          break;
        } 
        return IOStatus.normalize(l);
      } finally {
        writerCleanup();
        end((l > 0L || l == -2L));
        synchronized (this.stateLock) {
          if (l <= 0L && !this.isOutputOpen)
            throw new AsynchronousCloseException(); 
        } 
        assert IOStatus.check(l);
      } 
    } 
  }
  
  int sendOutOfBandData(byte paramByte) throws IOException {
    synchronized (this.writeLock) {
      ensureWriteOpen();
      i = 0;
      try {
        begin();
        synchronized (this.stateLock) {
          if (!isOpen())
            return 0; 
          this.writerThread = NativeThread.current();
        } 
        while (true) {
          i = sendOutOfBandData(this.fd, paramByte);
          if (i == -3 && isOpen())
            continue; 
          break;
        } 
        return IOStatus.normalize(i);
      } finally {
        writerCleanup();
        end((i > 0 || i == -2));
        synchronized (this.stateLock) {
          if (i <= 0 && !this.isOutputOpen)
            throw new AsynchronousCloseException(); 
        } 
        assert IOStatus.check(i);
      } 
    } 
  }
  
  protected void implConfigureBlocking(boolean paramBoolean) throws IOException { IOUtil.configureBlocking(this.fd, paramBoolean); }
  
  public InetSocketAddress localAddress() {
    synchronized (this.stateLock) {
      return this.localAddress;
    } 
  }
  
  public SocketAddress remoteAddress() throws IOException {
    synchronized (this.stateLock) {
      return this.remoteAddress;
    } 
  }
  
  public SocketChannel bind(SocketAddress paramSocketAddress) throws IOException {
    synchronized (this.readLock) {
      synchronized (this.writeLock) {
        synchronized (this.stateLock) {
          if (!isOpen())
            throw new ClosedChannelException(); 
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
      } 
    } 
    return this;
  }
  
  public boolean isConnected() throws ClosedChannelException {
    synchronized (this.stateLock) {
      return (this.state == 2);
    } 
  }
  
  public boolean isConnectionPending() throws ClosedChannelException {
    synchronized (this.stateLock) {
      return (this.state == 1);
    } 
  }
  
  void ensureOpenAndUnconnected() throws ClosedChannelException {
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (this.state == 2)
        throw new AlreadyConnectedException(); 
      if (this.state == 1)
        throw new ConnectionPendingException(); 
    } 
  }
  
  public boolean connect(SocketAddress paramSocketAddress) throws IOException {
    boolean bool = false;
    synchronized (this.readLock) {
      synchronized (this.writeLock) {
        ensureOpenAndUnconnected();
        InetSocketAddress inetSocketAddress = Net.checkAddress(paramSocketAddress);
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          securityManager.checkConnect(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort()); 
        synchronized (blockingLock()) {
          i = 0;
          try {
            try {
              begin();
              synchronized (this.stateLock) {
                if (!isOpen())
                  return false; 
                if (this.localAddress == null)
                  NetHooks.beforeTcpConnect(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort()); 
                this.readerThread = NativeThread.current();
              } 
              while (true) {
                InetAddress inetAddress = inetSocketAddress.getAddress();
                if (inetAddress.isAnyLocalAddress())
                  inetAddress = InetAddress.getLocalHost(); 
                i = Net.connect(this.fd, inetAddress, inetSocketAddress.getPort());
                if (i == -3 && isOpen())
                  continue; 
                break;
              } 
            } finally {
              readerCleanup();
              end((i > 0 || i == -2));
              assert IOStatus.check(i);
            } 
          } catch (IOException iOException) {
            close();
            throw iOException;
          } 
          synchronized (this.stateLock) {
            this.remoteAddress = inetSocketAddress;
            if (i > 0) {
              this.state = 2;
              if (isOpen())
                this.localAddress = Net.localAddress(this.fd); 
              return true;
            } 
            if (!isBlocking()) {
              this.state = 1;
            } else {
              assert false;
            } 
          } 
        } 
        return false;
      } 
    } 
  }
  
  public boolean finishConnect() throws ClosedChannelException {
    synchronized (this.readLock) {
      synchronized (this.writeLock) {
        synchronized (this.stateLock) {
          if (!isOpen())
            throw new ClosedChannelException(); 
          if (this.state == 2)
            return true; 
          if (this.state != 1)
            throw new NoConnectionPendingException(); 
        } 
        i = 0;
        try {
          try {
            begin();
            synchronized (blockingLock()) {
              synchronized (this.stateLock) {
                if (!isOpen())
                  return false; 
                this.readerThread = NativeThread.current();
              } 
              if (!isBlocking()) {
                while (true) {
                  i = checkConnect(this.fd, false, this.readyToConnect);
                  if (i == -3 && isOpen())
                    continue; 
                  break;
                } 
              } else {
                while (true) {
                  i = checkConnect(this.fd, true, this.readyToConnect);
                  if (i == 0 || (i == -3 && isOpen()))
                    continue; 
                  break;
                } 
              } 
            } 
          } finally {
            synchronized (this.stateLock) {
              this.readerThread = 0L;
              if (this.state == 3) {
                kill();
                i = 0;
              } 
            } 
            end((i > 0 || i == -2));
            assert IOStatus.check(i);
          } 
        } catch (IOException iOException) {
          close();
          throw iOException;
        } 
        if (i > 0) {
          synchronized (this.stateLock) {
            this.state = 2;
            if (isOpen())
              this.localAddress = Net.localAddress(this.fd); 
          } 
          return true;
        } 
        return false;
      } 
    } 
  }
  
  public SocketChannel shutdownInput() throws IOException {
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (!isConnected())
        throw new NotYetConnectedException(); 
      if (this.isInputOpen) {
        Net.shutdown(this.fd, 0);
        if (this.readerThread != 0L)
          NativeThread.signal(this.readerThread); 
        this.isInputOpen = false;
      } 
      return this;
    } 
  }
  
  public SocketChannel shutdownOutput() throws IOException {
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (!isConnected())
        throw new NotYetConnectedException(); 
      if (this.isOutputOpen) {
        Net.shutdown(this.fd, 1);
        if (this.writerThread != 0L)
          NativeThread.signal(this.writerThread); 
        this.isOutputOpen = false;
      } 
      return this;
    } 
  }
  
  public boolean isInputOpen() throws ClosedChannelException {
    synchronized (this.stateLock) {
      return this.isInputOpen;
    } 
  }
  
  public boolean isOutputOpen() throws ClosedChannelException {
    synchronized (this.stateLock) {
      return this.isOutputOpen;
    } 
  }
  
  protected void implCloseSelectableChannel() throws ClosedChannelException {
    synchronized (this.stateLock) {
      this.isInputOpen = false;
      this.isOutputOpen = false;
      if (this.state != 4)
        nd.preClose(this.fd); 
      if (this.readerThread != 0L)
        NativeThread.signal(this.readerThread); 
      if (this.writerThread != 0L)
        NativeThread.signal(this.writerThread); 
      if (!isRegistered())
        kill(); 
    } 
  }
  
  public void kill() throws ClosedChannelException {
    synchronized (this.stateLock) {
      if (this.state == 4)
        return; 
      if (this.state == -1) {
        this.state = 4;
        return;
      } 
      assert !isOpen() && !isRegistered();
      if (this.readerThread == 0L && this.writerThread == 0L) {
        nd.close(this.fd);
        this.state = 4;
      } else {
        this.state = 3;
      } 
    } 
  }
  
  public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl) {
    int i = paramSelectionKeyImpl.nioInterestOps();
    int j = paramSelectionKeyImpl.nioReadyOps();
    int k = paramInt2;
    if ((paramInt1 & Net.POLLNVAL) != 0)
      return false; 
    if ((paramInt1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
      k = i;
      paramSelectionKeyImpl.nioReadyOps(k);
      this.readyToConnect = true;
      return ((k & (j ^ 0xFFFFFFFF)) != 0);
    } 
    if ((paramInt1 & Net.POLLIN) != 0 && (i & true) != 0 && this.state == 2)
      k |= 0x1; 
    if ((paramInt1 & Net.POLLCONN) != 0 && (i & 0x8) != 0 && (this.state == 0 || this.state == 1)) {
      k |= 0x8;
      this.readyToConnect = true;
    } 
    if ((paramInt1 & Net.POLLOUT) != 0 && (i & 0x4) != 0 && this.state == 2)
      k |= 0x4; 
    paramSelectionKeyImpl.nioReadyOps(k);
    return ((k & (j ^ 0xFFFFFFFF)) != 0);
  }
  
  public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) { return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl); }
  
  public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) { return translateReadyOps(paramInt, 0, paramSelectionKeyImpl); }
  
  int poll(int paramInt, long paramLong) throws IOException {
    assert Thread.holdsLock(blockingLock()) && !isBlocking();
    synchronized (this.readLock) {
      i = 0;
      try {
        begin();
        synchronized (this.stateLock) {
          if (!isOpen())
            return 0; 
          this.readerThread = NativeThread.current();
        } 
        i = Net.poll(this.fd, paramInt, paramLong);
      } finally {
        readerCleanup();
        end((i > 0));
      } 
      return i;
    } 
  }
  
  public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
    short s = 0;
    if ((paramInt & true) != 0)
      s |= Net.POLLIN; 
    if ((paramInt & 0x4) != 0)
      s |= Net.POLLOUT; 
    if ((paramInt & 0x8) != 0)
      s |= Net.POLLCONN; 
    paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, s);
  }
  
  public FileDescriptor getFD() { return this.fd; }
  
  public int getFDVal() { return this.fdVal; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getClass().getSuperclass().getName());
    stringBuffer.append('[');
    if (!isOpen()) {
      stringBuffer.append("closed");
    } else {
      synchronized (this.stateLock) {
        switch (this.state) {
          case 0:
            stringBuffer.append("unconnected");
            break;
          case 1:
            stringBuffer.append("connection-pending");
            break;
          case 2:
            stringBuffer.append("connected");
            if (!this.isInputOpen)
              stringBuffer.append(" ishut"); 
            if (!this.isOutputOpen)
              stringBuffer.append(" oshut"); 
            break;
        } 
        InetSocketAddress inetSocketAddress = localAddress();
        if (inetSocketAddress != null) {
          stringBuffer.append(" local=");
          stringBuffer.append(Net.getRevealedLocalAddressAsString(inetSocketAddress));
        } 
        if (remoteAddress() != null) {
          stringBuffer.append(" remote=");
          stringBuffer.append(remoteAddress().toString());
        } 
      } 
    } 
    stringBuffer.append(']');
    return stringBuffer.toString();
  }
  
  private static native int checkConnect(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2) throws IOException;
  
  private static native int sendOutOfBandData(FileDescriptor paramFileDescriptor, byte paramByte) throws IOException;
  
  static  {
    IOUtil.load();
    nd = new SocketDispatcher();
  }
  
  private static class DefaultOptionsHolder {
    static final Set<SocketOption<?>> defaultOptions = defaultOptions();
    
    private static Set<SocketOption<?>> defaultOptions() {
      HashSet hashSet = new HashSet(8);
      hashSet.add(StandardSocketOptions.SO_SNDBUF);
      hashSet.add(StandardSocketOptions.SO_RCVBUF);
      hashSet.add(StandardSocketOptions.SO_KEEPALIVE);
      hashSet.add(StandardSocketOptions.SO_REUSEADDR);
      hashSet.add(StandardSocketOptions.SO_LINGER);
      hashSet.add(StandardSocketOptions.TCP_NODELAY);
      hashSet.add(StandardSocketOptions.IP_TOS);
      hashSet.add(ExtendedSocketOption.SO_OOBINLINE);
      if (ExtendedOptionsImpl.flowSupported())
        hashSet.add(ExtendedSocketOptions.SO_FLOW_SLA); 
      return Collections.unmodifiableSet(hashSet);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\SocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */