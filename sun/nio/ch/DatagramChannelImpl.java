package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.NetworkChannel;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.ResourceManager;

class DatagramChannelImpl extends DatagramChannel implements SelChImpl {
  private static NativeDispatcher nd = new DatagramDispatcher();
  
  private final FileDescriptor fd;
  
  private final int fdVal;
  
  private final ProtocolFamily family;
  
  private InetAddress cachedSenderInetAddress;
  
  private int cachedSenderPort;
  
  private final Object readLock = new Object();
  
  private final Object writeLock = new Object();
  
  private final Object stateLock = new Object();
  
  private static final int ST_UNINITIALIZED = -1;
  
  private static final int ST_UNCONNECTED = 0;
  
  private static final int ST_CONNECTED = 1;
  
  private static final int ST_KILLED = 2;
  
  private int state = -1;
  
  private InetSocketAddress localAddress;
  
  private InetSocketAddress remoteAddress;
  
  private DatagramSocket socket;
  
  private MembershipRegistry registry;
  
  private boolean reuseAddressEmulated;
  
  private boolean isReuseAddress;
  
  private SocketAddress sender;
  
  public DatagramChannelImpl(SelectorProvider paramSelectorProvider) throws IOException {
    super(paramSelectorProvider);
    ResourceManager.beforeUdpCreate();
    try {
      this.family = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
      this.fd = Net.socket(this.family, false);
      this.fdVal = IOUtil.fdVal(this.fd);
      this.state = 0;
    } catch (IOException iOException) {
      ResourceManager.afterUdpClose();
      throw iOException;
    } 
  }
  
  public DatagramChannelImpl(SelectorProvider paramSelectorProvider, ProtocolFamily paramProtocolFamily) throws IOException {
    super(paramSelectorProvider);
    if (paramProtocolFamily != StandardProtocolFamily.INET && paramProtocolFamily != StandardProtocolFamily.INET6) {
      if (paramProtocolFamily == null)
        throw new NullPointerException("'family' is null"); 
      throw new UnsupportedOperationException("Protocol family not supported");
    } 
    if (paramProtocolFamily == StandardProtocolFamily.INET6 && !Net.isIPv6Available())
      throw new UnsupportedOperationException("IPv6 not available"); 
    this.family = paramProtocolFamily;
    this.fd = Net.socket(paramProtocolFamily, false);
    this.fdVal = IOUtil.fdVal(this.fd);
    this.state = 0;
  }
  
  public DatagramChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor) throws IOException {
    super(paramSelectorProvider);
    this.family = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
    this.fd = paramFileDescriptor;
    this.fdVal = IOUtil.fdVal(paramFileDescriptor);
    this.state = 0;
    this.localAddress = Net.localAddress(paramFileDescriptor);
  }
  
  public DatagramSocket socket() {
    synchronized (this.stateLock) {
      if (this.socket == null)
        this.socket = DatagramSocketAdaptor.create(this); 
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
  
  public <T> DatagramChannel setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException {
    if (paramSocketOption == null)
      throw new NullPointerException(); 
    if (!supportedOptions().contains(paramSocketOption))
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported"); 
    synchronized (this.stateLock) {
      ensureOpen();
      if (paramSocketOption == StandardSocketOptions.IP_TOS || paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL || paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP) {
        Net.setSocketOption(this.fd, this.family, paramSocketOption, paramT);
        return this;
      } 
      if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF) {
        if (paramT == null)
          throw new IllegalArgumentException("Cannot set IP_MULTICAST_IF to 'null'"); 
        NetworkInterface networkInterface = (NetworkInterface)paramT;
        if (this.family == StandardProtocolFamily.INET6) {
          int i = networkInterface.getIndex();
          if (i == -1)
            throw new IOException("Network interface cannot be identified"); 
          Net.setInterface6(this.fd, i);
        } else {
          Inet4Address inet4Address = Net.anyInet4Address(networkInterface);
          if (inet4Address == null)
            throw new IOException("Network interface not configured for IPv4"); 
          int i = Net.inet4AsInt(inet4Address);
          Net.setInterface4(this.fd, i);
        } 
        return this;
      } 
      if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind() && this.localAddress != null) {
        this.reuseAddressEmulated = true;
        this.isReuseAddress = ((Boolean)paramT).booleanValue();
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
      ensureOpen();
      if (paramSocketOption == StandardSocketOptions.IP_TOS || paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL || paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP)
        return (T)Net.getSocketOption(this.fd, this.family, paramSocketOption); 
      if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF) {
        if (this.family == StandardProtocolFamily.INET) {
          int j = Net.getInterface4(this.fd);
          if (j == 0)
            return null; 
          InetAddress inetAddress = Net.inet4FromInt(j);
          NetworkInterface networkInterface1 = NetworkInterface.getByInetAddress(inetAddress);
          if (networkInterface1 == null)
            throw new IOException("Unable to map address to interface"); 
          return (T)networkInterface1;
        } 
        int i = Net.getInterface6(this.fd);
        if (i == 0)
          return null; 
        NetworkInterface networkInterface = NetworkInterface.getByIndex(i);
        if (networkInterface == null)
          throw new IOException("Unable to map index to interface"); 
        return (T)networkInterface;
      } 
      if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR && this.reuseAddressEmulated)
        return (T)Boolean.valueOf(this.isReuseAddress); 
      return (T)Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
    } 
  }
  
  public final Set<SocketOption<?>> supportedOptions() { return DefaultOptionsHolder.defaultOptions; }
  
  private void ensureOpen() throws ClosedChannelException {
    if (!isOpen())
      throw new ClosedChannelException(); 
  }
  
  public SocketAddress receive(ByteBuffer paramByteBuffer) throws IOException {
    if (paramByteBuffer.isReadOnly())
      throw new IllegalArgumentException("Read-only buffer"); 
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    synchronized (this.readLock) {
      ensureOpen();
      if (localAddress() == null)
        bind(null); 
      i = 0;
      byteBuffer = null;
      try {
        begin();
        if (!isOpen())
          return null; 
        SecurityManager securityManager = System.getSecurityManager();
        this.readerThread = NativeThread.current();
        if (isConnected() || securityManager == null) {
          do {
            i = receive(this.fd, paramByteBuffer);
          } while (i == -3 && isOpen());
          if (i == -2)
            return null; 
        } else {
          byteBuffer = Util.getTemporaryDirectBuffer(paramByteBuffer.remaining());
          while (true) {
            i = receive(this.fd, byteBuffer);
            if (i != -3 || !isOpen()) {
              if (i == -2)
                return null; 
              InetSocketAddress inetSocketAddress = (InetSocketAddress)this.sender;
              try {
                securityManager.checkAccept(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
                break;
              } catch (SecurityException securityException) {
                byteBuffer.clear();
                i = 0;
              } 
            } 
          } 
          byteBuffer.flip();
          paramByteBuffer.put(byteBuffer);
        } 
        return this.sender;
      } finally {
        if (byteBuffer != null)
          Util.releaseTemporaryDirectBuffer(byteBuffer); 
        this.readerThread = 0L;
        end((i > 0 || i == -2));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  private int receive(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer) throws IOException {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    if (paramByteBuffer instanceof DirectBuffer && k > 0)
      return receiveIntoNativeBuffer(paramFileDescriptor, paramByteBuffer, k, i); 
    int m = Math.max(k, 1);
    byteBuffer = Util.getTemporaryDirectBuffer(m);
    try {
      int n = receiveIntoNativeBuffer(paramFileDescriptor, byteBuffer, m, 0);
      byteBuffer.flip();
      if (n > 0 && k > 0)
        paramByteBuffer.put(byteBuffer); 
      return n;
    } finally {
      Util.releaseTemporaryDirectBuffer(byteBuffer);
    } 
  }
  
  private int receiveIntoNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2) throws IOException {
    int i = receive0(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + paramInt2, paramInt1, isConnected());
    if (i > 0)
      paramByteBuffer.position(paramInt2 + i); 
    return i;
  }
  
  public int send(ByteBuffer paramByteBuffer, SocketAddress paramSocketAddress) throws IOException {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    synchronized (this.writeLock) {
      ensureOpen();
      InetSocketAddress inetSocketAddress = Net.checkAddress(paramSocketAddress);
      InetAddress inetAddress = inetSocketAddress.getAddress();
      if (inetAddress == null)
        throw new IOException("Target address not resolved"); 
      synchronized (this.stateLock) {
        if (!isConnected()) {
          if (paramSocketAddress == null)
            throw new NullPointerException(); 
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null)
            if (inetAddress.isMulticastAddress()) {
              securityManager.checkMulticast(inetAddress);
            } else {
              securityManager.checkConnect(inetAddress.getHostAddress(), inetSocketAddress.getPort());
            }  
        } else {
          if (!paramSocketAddress.equals(this.remoteAddress))
            throw new IllegalArgumentException("Connected address not equal to target address"); 
          return write(paramByteBuffer);
        } 
      } 
      i = 0;
      try {
        begin();
        if (!isOpen())
          return 0; 
        this.writerThread = NativeThread.current();
        do {
          i = send(this.fd, paramByteBuffer, inetSocketAddress);
        } while (i == -3 && isOpen());
        synchronized (this.stateLock) {
          if (isOpen() && this.localAddress == null)
            this.localAddress = Net.localAddress(this.fd); 
        } 
        return IOStatus.normalize(i);
      } finally {
        this.writerThread = 0L;
        end((i > 0 || i == -2));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  private int send(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, InetSocketAddress paramInetSocketAddress) throws IOException {
    if (paramByteBuffer instanceof DirectBuffer)
      return sendFromNativeBuffer(paramFileDescriptor, paramByteBuffer, paramInetSocketAddress); 
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    byteBuffer = Util.getTemporaryDirectBuffer(k);
    try {
      byteBuffer.put(paramByteBuffer);
      byteBuffer.flip();
      paramByteBuffer.position(i);
      int m = sendFromNativeBuffer(paramFileDescriptor, byteBuffer, paramInetSocketAddress);
      if (m > 0)
        paramByteBuffer.position(i + m); 
      return m;
    } finally {
      Util.releaseTemporaryDirectBuffer(byteBuffer);
    } 
  }
  
  private int sendFromNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, InetSocketAddress paramInetSocketAddress) throws IOException {
    int m;
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    int k = (i <= j) ? (j - i) : 0;
    boolean bool = (this.family != StandardProtocolFamily.INET);
    try {
      m = send0(bool, paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k, paramInetSocketAddress.getAddress(), paramInetSocketAddress.getPort());
    } catch (PortUnreachableException portUnreachableException) {
      if (isConnected())
        throw portUnreachableException; 
      m = k;
    } 
    if (m > 0)
      paramByteBuffer.position(i + m); 
    return m;
  }
  
  public int read(ByteBuffer paramByteBuffer) throws IOException {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    synchronized (this.readLock) {
      synchronized (this.stateLock) {
        ensureOpen();
        if (!isConnected())
          throw new NotYetConnectedException(); 
      } 
      i = 0;
      try {
        begin();
        if (!isOpen())
          return 0; 
        this.readerThread = NativeThread.current();
        do {
          i = IOUtil.read(this.fd, paramByteBuffer, -1L, nd);
        } while (i == -3 && isOpen());
        return IOStatus.normalize(i);
      } finally {
        this.readerThread = 0L;
        end((i > 0 || i == -2));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    synchronized (this.readLock) {
      synchronized (this.stateLock) {
        ensureOpen();
        if (!isConnected())
          throw new NotYetConnectedException(); 
      } 
      l = 0L;
      try {
        begin();
        if (!isOpen())
          return 0L; 
        this.readerThread = NativeThread.current();
        do {
          l = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.readerThread = 0L;
        end((l > 0L || l == -2L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public int write(ByteBuffer paramByteBuffer) throws IOException {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    synchronized (this.writeLock) {
      synchronized (this.stateLock) {
        ensureOpen();
        if (!isConnected())
          throw new NotYetConnectedException(); 
      } 
      i = 0;
      try {
        begin();
        if (!isOpen())
          return 0; 
        this.writerThread = NativeThread.current();
        do {
          i = IOUtil.write(this.fd, paramByteBuffer, -1L, nd);
        } while (i == -3 && isOpen());
        return IOStatus.normalize(i);
      } finally {
        this.writerThread = 0L;
        end((i > 0 || i == -2));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    synchronized (this.writeLock) {
      synchronized (this.stateLock) {
        ensureOpen();
        if (!isConnected())
          throw new NotYetConnectedException(); 
      } 
      l = 0L;
      try {
        begin();
        if (!isOpen())
          return 0L; 
        this.writerThread = NativeThread.current();
        do {
          l = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.writerThread = 0L;
        end((l > 0L || l == -2L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  protected void implConfigureBlocking(boolean paramBoolean) throws IOException { IOUtil.configureBlocking(this.fd, paramBoolean); }
  
  public SocketAddress localAddress() throws IOException {
    synchronized (this.stateLock) {
      return this.localAddress;
    } 
  }
  
  public SocketAddress remoteAddress() throws IOException {
    synchronized (this.stateLock) {
      return this.remoteAddress;
    } 
  }
  
  public DatagramChannel bind(SocketAddress paramSocketAddress) throws IOException {
    synchronized (this.readLock) {
      synchronized (this.writeLock) {
        synchronized (this.stateLock) {
          InetSocketAddress inetSocketAddress;
          ensureOpen();
          if (this.localAddress != null)
            throw new AlreadyBoundException(); 
          if (paramSocketAddress == null) {
            if (this.family == StandardProtocolFamily.INET) {
              inetSocketAddress = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0);
            } else {
              inetSocketAddress = new InetSocketAddress(0);
            } 
          } else {
            inetSocketAddress = Net.checkAddress(paramSocketAddress);
            if (this.family == StandardProtocolFamily.INET) {
              InetAddress inetAddress = inetSocketAddress.getAddress();
              if (!(inetAddress instanceof Inet4Address))
                throw new UnsupportedAddressTypeException(); 
            } 
          } 
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null)
            securityManager.checkListen(inetSocketAddress.getPort()); 
          Net.bind(this.family, this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
          this.localAddress = Net.localAddress(this.fd);
        } 
      } 
    } 
    return this;
  }
  
  public boolean isConnected() {
    synchronized (this.stateLock) {
      return (this.state == 1);
    } 
  }
  
  void ensureOpenAndUnconnected() throws ClosedChannelException {
    synchronized (this.stateLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (this.state != 0)
        throw new IllegalStateException("Connect already invoked"); 
    } 
  }
  
  public DatagramChannel connect(SocketAddress paramSocketAddress) throws IOException {
    boolean bool = false;
    synchronized (this.readLock) {
      synchronized (this.writeLock) {
        synchronized (this.stateLock) {
          ensureOpenAndUnconnected();
          InetSocketAddress inetSocketAddress = Net.checkAddress(paramSocketAddress);
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null)
            securityManager.checkConnect(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort()); 
          int i = Net.connect(this.family, this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
          if (i <= 0)
            throw new Error(); 
          this.state = 1;
          this.remoteAddress = inetSocketAddress;
          this.sender = inetSocketAddress;
          this.cachedSenderInetAddress = inetSocketAddress.getAddress();
          this.cachedSenderPort = inetSocketAddress.getPort();
          this.localAddress = Net.localAddress(this.fd);
          bool1 = false;
          synchronized (blockingLock()) {
            try {
              bool1 = isBlocking();
              ByteBuffer byteBuffer = ByteBuffer.allocate(1);
              if (bool1)
                configureBlocking(false); 
              do {
                byteBuffer.clear();
              } while (receive(byteBuffer) != null);
            } finally {
              if (bool1)
                configureBlocking(true); 
            } 
          } 
        } 
      } 
    } 
    return this;
  }
  
  public DatagramChannel disconnect() throws IOException {
    synchronized (this.readLock) {
      synchronized (this.writeLock) {
        synchronized (this.stateLock) {
          if (!isConnected() || !isOpen())
            return this; 
          InetSocketAddress inetSocketAddress = this.remoteAddress;
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null)
            securityManager.checkConnect(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort()); 
          boolean bool = (this.family == StandardProtocolFamily.INET6);
          disconnect0(this.fd, bool);
          this.remoteAddress = null;
          this.state = 0;
          this.localAddress = Net.localAddress(this.fd);
        } 
      } 
    } 
    return this;
  }
  
  private MembershipKey innerJoin(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2) throws IOException {
    if (!paramInetAddress1.isMulticastAddress())
      throw new IllegalArgumentException("Group not a multicast address"); 
    if (paramInetAddress1 instanceof Inet4Address) {
      if (this.family == StandardProtocolFamily.INET6 && !Net.canIPv6SocketJoinIPv4Group())
        throw new IllegalArgumentException("IPv6 socket cannot join IPv4 multicast group"); 
    } else if (paramInetAddress1 instanceof java.net.Inet6Address) {
      if (this.family != StandardProtocolFamily.INET6)
        throw new IllegalArgumentException("Only IPv6 sockets can join IPv6 multicast group"); 
    } else {
      throw new IllegalArgumentException("Address type not supported");
    } 
    if (paramInetAddress2 != null) {
      if (paramInetAddress2.isAnyLocalAddress())
        throw new IllegalArgumentException("Source address is a wildcard address"); 
      if (paramInetAddress2.isMulticastAddress())
        throw new IllegalArgumentException("Source address is multicast address"); 
      if (paramInetAddress2.getClass() != paramInetAddress1.getClass())
        throw new IllegalArgumentException("Source address is different type to group"); 
    } 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkMulticast(paramInetAddress1); 
    synchronized (this.stateLock) {
      MembershipKeyImpl.Type4 type4;
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (this.registry == null) {
        this.registry = new MembershipRegistry();
      } else {
        type4 = this.registry.checkMembership(paramInetAddress1, paramNetworkInterface, paramInetAddress2);
        if (type4 != null)
          return type4; 
      } 
      if (this.family == StandardProtocolFamily.INET6 && (paramInetAddress1 instanceof java.net.Inet6Address || Net.canJoin6WithIPv4Group())) {
        int i = paramNetworkInterface.getIndex();
        if (i == -1)
          throw new IOException("Network interface cannot be identified"); 
        byte[] arrayOfByte1 = Net.inet6AsByteArray(paramInetAddress1);
        byte[] arrayOfByte2 = (paramInetAddress2 == null) ? null : Net.inet6AsByteArray(paramInetAddress2);
        int j = Net.join6(this.fd, arrayOfByte1, i, arrayOfByte2);
        if (j == -2)
          throw new UnsupportedOperationException(); 
        type4 = new MembershipKeyImpl.Type6(this, paramInetAddress1, paramNetworkInterface, paramInetAddress2, arrayOfByte1, i, arrayOfByte2);
      } else {
        Inet4Address inet4Address = Net.anyInet4Address(paramNetworkInterface);
        if (inet4Address == null)
          throw new IOException("Network interface not configured for IPv4"); 
        int i = Net.inet4AsInt(paramInetAddress1);
        int j = Net.inet4AsInt(inet4Address);
        byte b = (paramInetAddress2 == null) ? 0 : Net.inet4AsInt(paramInetAddress2);
        int k = Net.join4(this.fd, i, j, b);
        if (k == -2)
          throw new UnsupportedOperationException(); 
        type4 = new MembershipKeyImpl.Type4(this, paramInetAddress1, paramNetworkInterface, paramInetAddress2, i, j, b);
      } 
      this.registry.add(type4);
      return type4;
    } 
  }
  
  public MembershipKey join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException { return innerJoin(paramInetAddress, paramNetworkInterface, null); }
  
  public MembershipKey join(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2) throws IOException {
    if (paramInetAddress2 == null)
      throw new NullPointerException("source address is null"); 
    return innerJoin(paramInetAddress1, paramNetworkInterface, paramInetAddress2);
  }
  
  void drop(MembershipKeyImpl paramMembershipKeyImpl) {
    assert paramMembershipKeyImpl.channel() == this;
    synchronized (this.stateLock) {
      if (!paramMembershipKeyImpl.isValid())
        return; 
      try {
        if (paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6) {
          MembershipKeyImpl.Type6 type6 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
          Net.drop6(this.fd, type6.groupAddress(), type6.index(), type6.source());
        } else {
          MembershipKeyImpl.Type4 type4 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
          Net.drop4(this.fd, type4.groupAddress(), type4.interfaceAddress(), type4.source());
        } 
      } catch (IOException iOException) {
        throw new AssertionError(iOException);
      } 
      paramMembershipKeyImpl.invalidate();
      this.registry.remove(paramMembershipKeyImpl);
    } 
  }
  
  void block(MembershipKeyImpl paramMembershipKeyImpl, InetAddress paramInetAddress) throws IOException {
    assert paramMembershipKeyImpl.channel() == this;
    assert paramMembershipKeyImpl.sourceAddress() == null;
    synchronized (this.stateLock) {
      int i;
      if (!paramMembershipKeyImpl.isValid())
        throw new IllegalStateException("key is no longer valid"); 
      if (paramInetAddress.isAnyLocalAddress())
        throw new IllegalArgumentException("Source address is a wildcard address"); 
      if (paramInetAddress.isMulticastAddress())
        throw new IllegalArgumentException("Source address is multicast address"); 
      if (paramInetAddress.getClass() != paramMembershipKeyImpl.group().getClass())
        throw new IllegalArgumentException("Source address is different type to group"); 
      if (paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6) {
        MembershipKeyImpl.Type6 type6 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
        i = Net.block6(this.fd, type6.groupAddress(), type6.index(), Net.inet6AsByteArray(paramInetAddress));
      } else {
        MembershipKeyImpl.Type4 type4 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
        i = Net.block4(this.fd, type4.groupAddress(), type4.interfaceAddress(), Net.inet4AsInt(paramInetAddress));
      } 
      if (i == -2)
        throw new UnsupportedOperationException(); 
    } 
  }
  
  void unblock(MembershipKeyImpl paramMembershipKeyImpl, InetAddress paramInetAddress) throws IOException {
    assert paramMembershipKeyImpl.channel() == this;
    assert paramMembershipKeyImpl.sourceAddress() == null;
    synchronized (this.stateLock) {
      if (!paramMembershipKeyImpl.isValid())
        throw new IllegalStateException("key is no longer valid"); 
      try {
        if (paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6) {
          MembershipKeyImpl.Type6 type6 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
          Net.unblock6(this.fd, type6.groupAddress(), type6.index(), Net.inet6AsByteArray(paramInetAddress));
        } else {
          MembershipKeyImpl.Type4 type4 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
          Net.unblock4(this.fd, type4.groupAddress(), type4.interfaceAddress(), Net.inet4AsInt(paramInetAddress));
        } 
      } catch (IOException iOException) {
        throw new AssertionError(iOException);
      } 
    } 
  }
  
  protected void implCloseSelectableChannel() throws ClosedChannelException {
    synchronized (this.stateLock) {
      if (this.state != 2)
        nd.preClose(this.fd); 
      ResourceManager.afterUdpClose();
      if (this.registry != null)
        this.registry.invalidateAll(); 
      long l;
      if ((l = this.readerThread) != 0L)
        NativeThread.signal(l); 
      if ((l = this.writerThread) != 0L)
        NativeThread.signal(l); 
      if (!isRegistered())
        kill(); 
    } 
  }
  
  public void kill() throws ClosedChannelException {
    synchronized (this.stateLock) {
      if (this.state == 2)
        return; 
      if (this.state == -1) {
        this.state = 2;
        return;
      } 
      assert !isOpen() && !isRegistered();
      nd.close(this.fd);
      this.state = 2;
    } 
  }
  
  protected void finalize() throws ClosedChannelException {
    if (this.fd != null)
      close(); 
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
    if ((paramInt1 & Net.POLLIN) != 0 && (i & true) != 0)
      k |= 0x1; 
    if ((paramInt1 & Net.POLLOUT) != 0 && (i & 0x4) != 0)
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
        this.readerThread = 0L;
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
      s |= Net.POLLIN; 
    paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, s);
  }
  
  public FileDescriptor getFD() { return this.fd; }
  
  public int getFDVal() { return this.fdVal; }
  
  private static native void initIDs() throws ClosedChannelException;
  
  private static native void disconnect0(FileDescriptor paramFileDescriptor, boolean paramBoolean) throws IOException;
  
  private native int receive0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt, boolean paramBoolean) throws IOException;
  
  private native int send0(boolean paramBoolean, FileDescriptor paramFileDescriptor, long paramLong, int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException;
  
  static  {
    IOUtil.load();
    initIDs();
  }
  
  private static class DefaultOptionsHolder {
    static final Set<SocketOption<?>> defaultOptions = defaultOptions();
    
    private static Set<SocketOption<?>> defaultOptions() {
      HashSet hashSet = new HashSet(8);
      hashSet.add(StandardSocketOptions.SO_SNDBUF);
      hashSet.add(StandardSocketOptions.SO_RCVBUF);
      hashSet.add(StandardSocketOptions.SO_REUSEADDR);
      hashSet.add(StandardSocketOptions.SO_BROADCAST);
      hashSet.add(StandardSocketOptions.IP_TOS);
      hashSet.add(StandardSocketOptions.IP_MULTICAST_IF);
      hashSet.add(StandardSocketOptions.IP_MULTICAST_TTL);
      hashSet.add(StandardSocketOptions.IP_MULTICAST_LOOP);
      if (ExtendedOptionsImpl.flowSupported())
        hashSet.add(ExtendedSocketOptions.SO_FLOW_SLA); 
      return Collections.unmodifiableSet(hashSet);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\DatagramChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */