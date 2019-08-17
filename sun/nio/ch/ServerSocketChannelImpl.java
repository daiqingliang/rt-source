package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NetworkChannel;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import sun.net.NetHooks;

class ServerSocketChannelImpl extends ServerSocketChannel implements SelChImpl {
  private static NativeDispatcher nd;
  
  private final FileDescriptor fd = Net.serverSocket(true);
  
  private int fdVal = IOUtil.fdVal(this.fd);
  
  private final Object lock = new Object();
  
  private final Object stateLock = new Object();
  
  private static final int ST_UNINITIALIZED = -1;
  
  private static final int ST_INUSE = 0;
  
  private static final int ST_KILLED = 1;
  
  private int state = -1;
  
  private InetSocketAddress localAddress;
  
  private boolean isReuseAddress;
  
  ServerSocket socket;
  
  ServerSocketChannelImpl(SelectorProvider paramSelectorProvider) throws IOException {
    super(paramSelectorProvider);
    this.state = 0;
  }
  
  ServerSocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, boolean paramBoolean) throws IOException {
    super(paramSelectorProvider);
    this.state = 0;
    if (paramBoolean)
      this.localAddress = Net.localAddress(paramFileDescriptor); 
  }
  
  public ServerSocket socket() {
    synchronized (this.stateLock) {
      if (this.socket == null)
        this.socket = ServerSocketAdaptor.create(this); 
      return this.socket;
    } 
  }
  
  public SocketAddress getLocalAddress() throws IOException {
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      return (this.localAddress == null) ? this.localAddress : Net.getRevealedLocalAddress(Net.asInetSocketAddress(this.localAddress));
    } 
  }
  
  public <T> ServerSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException {
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
      } else {
        Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
      } 
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
      return (T)Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
    } 
  }
  
  public final Set<SocketOption<?>> supportedOptions() { return DefaultOptionsHolder.defaultOptions; }
  
  public boolean isBound() {
    synchronized (this.stateLock) {
      return (this.localAddress != null);
    } 
  }
  
  public InetSocketAddress localAddress() {
    synchronized (this.stateLock) {
      return this.localAddress;
    } 
  }
  
  public ServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    synchronized (this.lock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (isBound())
        throw new AlreadyBoundException(); 
      InetSocketAddress inetSocketAddress = (paramSocketAddress == null) ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkListen(inetSocketAddress.getPort()); 
      NetHooks.beforeTcpBind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
      Net.bind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
      Net.listen(this.fd, (paramInt < 1) ? 50 : paramInt);
      synchronized (this.stateLock) {
        this.localAddress = Net.localAddress(this.fd);
      } 
    } 
    return this;
  }
  
  public SocketChannel accept() throws IOException {
    synchronized (this.lock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (!isBound())
        throw new NotYetBoundException(); 
      SocketChannelImpl socketChannelImpl = null;
      i = 0;
      FileDescriptor fileDescriptor = new FileDescriptor();
      InetSocketAddress[] arrayOfInetSocketAddress = new InetSocketAddress[1];
      try {
        begin();
        if (!isOpen())
          return null; 
        this.thread = NativeThread.current();
        while (true) {
          i = accept(this.fd, fileDescriptor, arrayOfInetSocketAddress);
          if (i == -3 && isOpen())
            continue; 
          break;
        } 
      } finally {
        this.thread = 0L;
        end((i > 0));
        assert IOStatus.check(i);
      } 
      if (i < 1)
        return null; 
      IOUtil.configureBlocking(fileDescriptor, true);
      InetSocketAddress inetSocketAddress = arrayOfInetSocketAddress[0];
      socketChannelImpl = new SocketChannelImpl(provider(), fileDescriptor, inetSocketAddress);
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        try {
          securityManager.checkAccept(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
        } catch (SecurityException securityException) {
          socketChannelImpl.close();
          throw securityException;
        }  
      return socketChannelImpl;
    } 
  }
  
  protected void implConfigureBlocking(boolean paramBoolean) throws IOException { IOUtil.configureBlocking(this.fd, paramBoolean); }
  
  protected void implCloseSelectableChannel() throws IOException {
    synchronized (this.stateLock) {
      if (this.state != 1)
        nd.preClose(this.fd); 
      long l = this.thread;
      if (l != 0L)
        NativeThread.signal(l); 
      if (!isRegistered())
        kill(); 
    } 
  }
  
  public void kill() throws IOException {
    synchronized (this.stateLock) {
      if (this.state == 1)
        return; 
      if (this.state == -1) {
        this.state = 1;
        return;
      } 
      assert !isOpen() && !isRegistered();
      nd.close(this.fd);
      this.state = 1;
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
      return ((k & (j ^ 0xFFFFFFFF)) != 0);
    } 
    if ((paramInt1 & Net.POLLIN) != 0 && (i & 0x10) != 0)
      k |= 0x10; 
    paramSelectionKeyImpl.nioReadyOps(k);
    return ((k & (j ^ 0xFFFFFFFF)) != 0);
  }
  
  public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) { return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl); }
  
  public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) { return translateReadyOps(paramInt, 0, paramSelectionKeyImpl); }
  
  int poll(int paramInt, long paramLong) throws IOException {
    assert Thread.holdsLock(blockingLock()) && !isBlocking();
    synchronized (this.lock) {
      i = 0;
      try {
        begin();
        synchronized (this.stateLock) {
          if (!isOpen())
            return 0; 
          this.thread = NativeThread.current();
        } 
        i = Net.poll(this.fd, paramInt, paramLong);
      } finally {
        this.thread = 0L;
        end((i > 0));
      } 
      return i;
    } 
  }
  
  public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
    short s = 0;
    if ((paramInt & 0x10) != 0)
      s |= Net.POLLIN; 
    paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, s);
  }
  
  public FileDescriptor getFD() { return this.fd; }
  
  public int getFDVal() { return this.fdVal; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getClass().getName());
    stringBuffer.append('[');
    if (!isOpen()) {
      stringBuffer.append("closed");
    } else {
      synchronized (this.stateLock) {
        InetSocketAddress inetSocketAddress = localAddress();
        if (inetSocketAddress == null) {
          stringBuffer.append("unbound");
        } else {
          stringBuffer.append(Net.getRevealedLocalAddressAsString(inetSocketAddress));
        } 
      } 
    } 
    stringBuffer.append(']');
    return stringBuffer.toString();
  }
  
  private int accept(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress) throws IOException { return accept0(paramFileDescriptor1, paramFileDescriptor2, paramArrayOfInetSocketAddress); }
  
  private native int accept0(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress) throws IOException;
  
  private static native void initIDs() throws IOException;
  
  static  {
    IOUtil.load();
    initIDs();
    nd = new SocketDispatcher();
  }
  
  private static class DefaultOptionsHolder {
    static final Set<SocketOption<?>> defaultOptions = defaultOptions();
    
    private static Set<SocketOption<?>> defaultOptions() {
      HashSet hashSet = new HashSet(2);
      hashSet.add(StandardSocketOptions.SO_RCVBUF);
      hashSet.add(StandardSocketOptions.SO_REUSEADDR);
      hashSet.add(StandardSocketOptions.IP_TOS);
      return Collections.unmodifiableSet(hashSet);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\ServerSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */