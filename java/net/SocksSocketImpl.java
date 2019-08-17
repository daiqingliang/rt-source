package java.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import sun.net.SocksProxy;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;

class SocksSocketImpl extends PlainSocketImpl implements SocksConsts {
  private String server = null;
  
  private int serverPort = 1080;
  
  private InetSocketAddress external_address;
  
  private boolean useV4 = false;
  
  private Socket cmdsock = null;
  
  private InputStream cmdIn = null;
  
  private OutputStream cmdOut = null;
  
  private boolean applicationSetProxy;
  
  SocksSocketImpl() {}
  
  SocksSocketImpl(String paramString, int paramInt) {
    this.server = paramString;
    this.serverPort = (paramInt == -1) ? 1080 : paramInt;
  }
  
  SocksSocketImpl(Proxy paramProxy) {
    SocketAddress socketAddress = paramProxy.address();
    if (socketAddress instanceof InetSocketAddress) {
      InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
      this.server = inetSocketAddress.getHostString();
      this.serverPort = inetSocketAddress.getPort();
    } 
  }
  
  void setV4() { this.useV4 = true; }
  
  private void privilegedConnect(final String host, final int port, final int timeout) throws IOException {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
              SocksSocketImpl.this.superConnectServer(host, port, timeout);
              SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.getInputStream();
              SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.getOutputStream();
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
  
  private void superConnectServer(String paramString, int paramInt1, int paramInt2) throws IOException { super.connect(new InetSocketAddress(paramString, paramInt1), paramInt2); }
  
  private static int remainingMillis(long paramLong) throws IOException {
    if (paramLong == 0L)
      return 0; 
    long l = paramLong - System.currentTimeMillis();
    if (l > 0L)
      return (int)l; 
    throw new SocketTimeoutException();
  }
  
  private int readSocksReply(InputStream paramInputStream, byte[] paramArrayOfByte) throws IOException { return readSocksReply(paramInputStream, paramArrayOfByte, 0L); }
  
  private int readSocksReply(InputStream paramInputStream, byte[] paramArrayOfByte, long paramLong) throws IOException {
    int i = paramArrayOfByte.length;
    int j = 0;
    for (byte b = 0; j < i && b < 3; b++) {
      int k;
      try {
        k = ((SocketInputStream)paramInputStream).read(paramArrayOfByte, j, i - j, remainingMillis(paramLong));
      } catch (SocketTimeoutException socketTimeoutException) {
        throw new SocketTimeoutException("Connect timed out");
      } 
      if (k < 0)
        throw new SocketException("Malformed reply from SOCKS server"); 
      j += k;
    } 
    return j;
  }
  
  private boolean authenticate(byte paramByte, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream) throws IOException { return authenticate(paramByte, paramInputStream, paramBufferedOutputStream, 0L); }
  
  private boolean authenticate(byte paramByte, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream, long paramLong) throws IOException {
    if (paramByte == 0)
      return true; 
    if (paramByte == 2) {
      String str1;
      String str2 = null;
      final InetAddress addr = InetAddress.getByName(this.server);
      PasswordAuthentication passwordAuthentication = (PasswordAuthentication)AccessController.doPrivileged(new PrivilegedAction<PasswordAuthentication>() {
            public PasswordAuthentication run() { return Authenticator.requestPasswordAuthentication(SocksSocketImpl.this.server, addr, SocksSocketImpl.this.serverPort, "SOCKS5", "SOCKS authentication", null); }
          });
      if (passwordAuthentication != null) {
        str1 = passwordAuthentication.getUserName();
        str2 = new String(passwordAuthentication.getPassword());
      } else {
        str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
      } 
      if (str1 == null)
        return false; 
      paramBufferedOutputStream.write(1);
      paramBufferedOutputStream.write(str1.length());
      try {
        paramBufferedOutputStream.write(str1.getBytes("ISO-8859-1"));
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        assert false;
      } 
      if (str2 != null) {
        paramBufferedOutputStream.write(str2.length());
        try {
          paramBufferedOutputStream.write(str2.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          assert false;
        } 
      } else {
        paramBufferedOutputStream.write(0);
      } 
      paramBufferedOutputStream.flush();
      byte[] arrayOfByte = new byte[2];
      int i = readSocksReply(paramInputStream, arrayOfByte, paramLong);
      if (i != 2 || arrayOfByte[1] != 0) {
        paramBufferedOutputStream.close();
        paramInputStream.close();
        return false;
      } 
      return true;
    } 
    return false;
  }
  
  private void connectV4(InputStream paramInputStream, OutputStream paramOutputStream, InetSocketAddress paramInetSocketAddress, long paramLong) throws IOException {
    if (!(paramInetSocketAddress.getAddress() instanceof Inet4Address))
      throw new SocketException("SOCKS V4 requires IPv4 only addresses"); 
    paramOutputStream.write(4);
    paramOutputStream.write(1);
    paramOutputStream.write(paramInetSocketAddress.getPort() >> 8 & 0xFF);
    paramOutputStream.write(paramInetSocketAddress.getPort() >> 0 & 0xFF);
    paramOutputStream.write(paramInetSocketAddress.getAddress().getAddress());
    String str = getUserName();
    try {
      paramOutputStream.write(str.getBytes("ISO-8859-1"));
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      assert false;
    } 
    paramOutputStream.write(0);
    paramOutputStream.flush();
    byte[] arrayOfByte = new byte[8];
    int i = readSocksReply(paramInputStream, arrayOfByte, paramLong);
    if (i != 8)
      throw new SocketException("Reply from SOCKS server has bad length: " + i); 
    if (arrayOfByte[0] != 0 && arrayOfByte[0] != 4)
      throw new SocketException("Reply from SOCKS server has bad version"); 
    SocketException socketException = null;
    switch (arrayOfByte[1]) {
      case 90:
        this.external_address = paramInetSocketAddress;
        break;
      case 91:
        socketException = new SocketException("SOCKS request rejected");
        break;
      case 92:
        socketException = new SocketException("SOCKS server couldn't reach destination");
        break;
      case 93:
        socketException = new SocketException("SOCKS authentication failed");
        break;
      default:
        socketException = new SocketException("Reply from SOCKS server contains bad status");
        break;
    } 
    if (socketException != null) {
      paramInputStream.close();
      paramOutputStream.close();
      throw socketException;
    } 
  }
  
  protected void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    byte[] arrayOfByte3;
    byte[] arrayOfByte2;
    byte b;
    long l;
    if (paramInt == 0) {
      l = 0L;
    } else {
      long l1 = System.currentTimeMillis() + paramInt;
      l = (l1 < 0L) ? Float.MAX_VALUE : l1;
    } 
    SecurityManager securityManager = System.getSecurityManager();
    if (paramSocketAddress == null || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (securityManager != null)
      if (inetSocketAddress.isUnresolved()) {
        securityManager.checkConnect(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
      } else {
        securityManager.checkConnect(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
      }  
    if (this.server == null) {
      URI uRI;
      ProxySelector proxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction<ProxySelector>() {
            public ProxySelector run() { return ProxySelector.getDefault(); }
          });
      if (proxySelector == null) {
        super.connect(inetSocketAddress, remainingMillis(l));
        return;
      } 
      String str = inetSocketAddress.getHostString();
      if (inetSocketAddress.getAddress() instanceof Inet6Address && !str.startsWith("[") && str.indexOf(":") >= 0)
        str = "[" + str + "]"; 
      try {
        uRI = new URI("socket://" + ParseUtil.encodePath(str) + ":" + inetSocketAddress.getPort());
      } catch (URISyntaxException uRISyntaxException) {
        assert false : uRISyntaxException;
        uRI = null;
      } 
      Proxy proxy = null;
      IOException iOException = null;
      Iterator iterator = null;
      iterator = proxySelector.select(uRI).iterator();
      if (iterator == null || !iterator.hasNext()) {
        super.connect(inetSocketAddress, remainingMillis(l));
        return;
      } 
      while (iterator.hasNext()) {
        proxy = (Proxy)iterator.next();
        if (proxy == null || proxy.type() != Proxy.Type.SOCKS) {
          super.connect(inetSocketAddress, remainingMillis(l));
          return;
        } 
        if (!(proxy.address() instanceof InetSocketAddress))
          throw new SocketException("Unknown address type for proxy: " + proxy); 
        this.server = ((InetSocketAddress)proxy.address()).getHostString();
        this.serverPort = ((InetSocketAddress)proxy.address()).getPort();
        if (proxy instanceof SocksProxy && ((SocksProxy)proxy).protocolVersion() == 4)
          this.useV4 = true; 
        try {
          privilegedConnect(this.server, this.serverPort, remainingMillis(l));
          break;
        } catch (IOException iOException1) {
          proxySelector.connectFailed(uRI, proxy.address(), iOException1);
          this.server = null;
          this.serverPort = -1;
          iOException = iOException1;
        } 
      } 
      if (this.server == null)
        throw new SocketException("Can't connect to SOCKS proxy:" + iOException.getMessage()); 
    } else {
      try {
        privilegedConnect(this.server, this.serverPort, remainingMillis(l));
      } catch (IOException iOException) {
        throw new SocketException(iOException.getMessage());
      } 
    } 
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(this.cmdOut, 512);
    InputStream inputStream = this.cmdIn;
    if (this.useV4) {
      if (inetSocketAddress.isUnresolved())
        throw new UnknownHostException(inetSocketAddress.toString()); 
      connectV4(inputStream, bufferedOutputStream, inetSocketAddress, l);
      return;
    } 
    bufferedOutputStream.write(5);
    bufferedOutputStream.write(2);
    bufferedOutputStream.write(0);
    bufferedOutputStream.write(2);
    bufferedOutputStream.flush();
    byte[] arrayOfByte1 = new byte[2];
    int i = readSocksReply(inputStream, arrayOfByte1, l);
    if (i != 2 || arrayOfByte1[0] != 5) {
      if (inetSocketAddress.isUnresolved())
        throw new UnknownHostException(inetSocketAddress.toString()); 
      connectV4(inputStream, bufferedOutputStream, inetSocketAddress, l);
      return;
    } 
    if (arrayOfByte1[1] == -1)
      throw new SocketException("SOCKS : No acceptable methods"); 
    if (!authenticate(arrayOfByte1[1], inputStream, bufferedOutputStream, l))
      throw new SocketException("SOCKS : authentication failed"); 
    bufferedOutputStream.write(5);
    bufferedOutputStream.write(1);
    bufferedOutputStream.write(0);
    if (inetSocketAddress.isUnresolved()) {
      bufferedOutputStream.write(3);
      bufferedOutputStream.write(inetSocketAddress.getHostName().length());
      try {
        bufferedOutputStream.write(inetSocketAddress.getHostName().getBytes("ISO-8859-1"));
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        assert false;
      } 
      bufferedOutputStream.write(inetSocketAddress.getPort() >> 8 & 0xFF);
      bufferedOutputStream.write(inetSocketAddress.getPort() >> 0 & 0xFF);
    } else if (inetSocketAddress.getAddress() instanceof Inet6Address) {
      bufferedOutputStream.write(4);
      bufferedOutputStream.write(inetSocketAddress.getAddress().getAddress());
      bufferedOutputStream.write(inetSocketAddress.getPort() >> 8 & 0xFF);
      bufferedOutputStream.write(inetSocketAddress.getPort() >> 0 & 0xFF);
    } else {
      bufferedOutputStream.write(1);
      bufferedOutputStream.write(inetSocketAddress.getAddress().getAddress());
      bufferedOutputStream.write(inetSocketAddress.getPort() >> 8 & 0xFF);
      bufferedOutputStream.write(inetSocketAddress.getPort() >> 0 & 0xFF);
    } 
    bufferedOutputStream.flush();
    arrayOfByte1 = new byte[4];
    i = readSocksReply(inputStream, arrayOfByte1, l);
    if (i != 4)
      throw new SocketException("Reply from SOCKS server has bad length"); 
    SocketException socketException = null;
    switch (arrayOfByte1[1]) {
      case 0:
        switch (arrayOfByte1[3]) {
          case 1:
            arrayOfByte2 = new byte[4];
            i = readSocksReply(inputStream, arrayOfByte2, l);
            if (i != 4)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            arrayOfByte1 = new byte[2];
            i = readSocksReply(inputStream, arrayOfByte1, l);
            if (i != 2)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            break;
          case 3:
            b = arrayOfByte1[1];
            arrayOfByte3 = new byte[b];
            i = readSocksReply(inputStream, arrayOfByte3, l);
            if (i != b)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            arrayOfByte1 = new byte[2];
            i = readSocksReply(inputStream, arrayOfByte1, l);
            if (i != 2)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            break;
          case 4:
            b = arrayOfByte1[1];
            arrayOfByte2 = new byte[b];
            i = readSocksReply(inputStream, arrayOfByte2, l);
            if (i != b)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            arrayOfByte1 = new byte[2];
            i = readSocksReply(inputStream, arrayOfByte1, l);
            if (i != 2)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            break;
        } 
        socketException = new SocketException("Reply from SOCKS server contains wrong code");
        break;
      case 1:
        socketException = new SocketException("SOCKS server general failure");
        break;
      case 2:
        socketException = new SocketException("SOCKS: Connection not allowed by ruleset");
        break;
      case 3:
        socketException = new SocketException("SOCKS: Network unreachable");
        break;
      case 4:
        socketException = new SocketException("SOCKS: Host unreachable");
        break;
      case 5:
        socketException = new SocketException("SOCKS: Connection refused");
        break;
      case 6:
        socketException = new SocketException("SOCKS: TTL expired");
        break;
      case 7:
        socketException = new SocketException("SOCKS: Command not supported");
        break;
      case 8:
        socketException = new SocketException("SOCKS: address type not supported");
        break;
    } 
    if (socketException != null) {
      inputStream.close();
      bufferedOutputStream.close();
      throw socketException;
    } 
    this.external_address = inetSocketAddress;
  }
  
  private void bindV4(InputStream paramInputStream, OutputStream paramOutputStream, InetAddress paramInetAddress, int paramInt) throws IOException {
    if (!(paramInetAddress instanceof Inet4Address))
      throw new SocketException("SOCKS V4 requires IPv4 only addresses"); 
    bind(paramInetAddress, paramInt);
    byte[] arrayOfByte1 = paramInetAddress.getAddress();
    InetAddress inetAddress = paramInetAddress;
    if (inetAddress.isAnyLocalAddress()) {
      inetAddress = (InetAddress)AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
            public InetAddress run() { return SocksSocketImpl.this.cmdsock.getLocalAddress(); }
          });
      arrayOfByte1 = inetAddress.getAddress();
    } 
    paramOutputStream.write(4);
    paramOutputStream.write(2);
    paramOutputStream.write(super.getLocalPort() >> 8 & 0xFF);
    paramOutputStream.write(super.getLocalPort() >> 0 & 0xFF);
    paramOutputStream.write(arrayOfByte1);
    String str = getUserName();
    try {
      paramOutputStream.write(str.getBytes("ISO-8859-1"));
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      assert false;
    } 
    paramOutputStream.write(0);
    paramOutputStream.flush();
    byte[] arrayOfByte2 = new byte[8];
    int i = readSocksReply(paramInputStream, arrayOfByte2);
    if (i != 8)
      throw new SocketException("Reply from SOCKS server has bad length: " + i); 
    if (arrayOfByte2[0] != 0 && arrayOfByte2[0] != 4)
      throw new SocketException("Reply from SOCKS server has bad version"); 
    SocketException socketException = null;
    switch (arrayOfByte2[1]) {
      case 90:
        this.external_address = new InetSocketAddress(paramInetAddress, paramInt);
        break;
      case 91:
        socketException = new SocketException("SOCKS request rejected");
        break;
      case 92:
        socketException = new SocketException("SOCKS server couldn't reach destination");
        break;
      case 93:
        socketException = new SocketException("SOCKS authentication failed");
        break;
      default:
        socketException = new SocketException("Reply from SOCKS server contains bad status");
        break;
    } 
    if (socketException != null) {
      paramInputStream.close();
      paramOutputStream.close();
      throw socketException;
    } 
  }
  
  protected void socksBind(InetSocketAddress paramInetSocketAddress) throws IOException {
    byte[] arrayOfByte3;
    byte[] arrayOfByte2;
    byte b2;
    byte b1;
    if (this.socket != null)
      return; 
    if (this.server == null) {
      URI uRI;
      ProxySelector proxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction<ProxySelector>() {
            public ProxySelector run() { return ProxySelector.getDefault(); }
          });
      if (proxySelector == null)
        return; 
      String str = paramInetSocketAddress.getHostString();
      if (paramInetSocketAddress.getAddress() instanceof Inet6Address && !str.startsWith("[") && str.indexOf(":") >= 0)
        str = "[" + str + "]"; 
      try {
        uRI = new URI("serversocket://" + ParseUtil.encodePath(str) + ":" + paramInetSocketAddress.getPort());
      } catch (URISyntaxException uRISyntaxException) {
        assert false : uRISyntaxException;
        uRI = null;
      } 
      Proxy proxy = null;
      Exception exception = null;
      Iterator iterator = null;
      iterator = proxySelector.select(uRI).iterator();
      if (iterator == null || !iterator.hasNext())
        return; 
      while (iterator.hasNext()) {
        proxy = (Proxy)iterator.next();
        if (proxy == null || proxy.type() != Proxy.Type.SOCKS)
          return; 
        if (!(proxy.address() instanceof InetSocketAddress))
          throw new SocketException("Unknown address type for proxy: " + proxy); 
        this.server = ((InetSocketAddress)proxy.address()).getHostString();
        this.serverPort = ((InetSocketAddress)proxy.address()).getPort();
        if (proxy instanceof SocksProxy && ((SocksProxy)proxy).protocolVersion() == 4)
          this.useV4 = true; 
        try {
          AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() throws IOException {
                  SocksSocketImpl.this.cmdsock = new Socket(new PlainSocketImpl());
                  SocksSocketImpl.this.cmdsock.connect(new InetSocketAddress(SocksSocketImpl.this.server, SocksSocketImpl.this.serverPort));
                  SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.cmdsock.getInputStream();
                  SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.cmdsock.getOutputStream();
                  return null;
                }
              });
        } catch (Exception exception1) {
          proxySelector.connectFailed(uRI, proxy.address(), new SocketException(exception1.getMessage()));
          this.server = null;
          this.serverPort = -1;
          this.cmdsock = null;
          exception = exception1;
        } 
      } 
      if (this.server == null || this.cmdsock == null)
        throw new SocketException("Can't connect to SOCKS proxy:" + exception.getMessage()); 
    } else {
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
              public Void run() throws IOException {
                SocksSocketImpl.this.cmdsock = new Socket(new PlainSocketImpl());
                SocksSocketImpl.this.cmdsock.connect(new InetSocketAddress(SocksSocketImpl.this.server, SocksSocketImpl.this.serverPort));
                SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.cmdsock.getInputStream();
                SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.cmdsock.getOutputStream();
                return null;
              }
            });
      } catch (Exception exception) {
        throw new SocketException(exception.getMessage());
      } 
    } 
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(this.cmdOut, 512);
    InputStream inputStream = this.cmdIn;
    if (this.useV4) {
      bindV4(inputStream, bufferedOutputStream, paramInetSocketAddress.getAddress(), paramInetSocketAddress.getPort());
      return;
    } 
    bufferedOutputStream.write(5);
    bufferedOutputStream.write(2);
    bufferedOutputStream.write(0);
    bufferedOutputStream.write(2);
    bufferedOutputStream.flush();
    byte[] arrayOfByte1 = new byte[2];
    int i = readSocksReply(inputStream, arrayOfByte1);
    if (i != 2 || arrayOfByte1[0] != 5) {
      bindV4(inputStream, bufferedOutputStream, paramInetSocketAddress.getAddress(), paramInetSocketAddress.getPort());
      return;
    } 
    if (arrayOfByte1[1] == -1)
      throw new SocketException("SOCKS : No acceptable methods"); 
    if (!authenticate(arrayOfByte1[1], inputStream, bufferedOutputStream))
      throw new SocketException("SOCKS : authentication failed"); 
    bufferedOutputStream.write(5);
    bufferedOutputStream.write(2);
    bufferedOutputStream.write(0);
    int j = paramInetSocketAddress.getPort();
    if (paramInetSocketAddress.isUnresolved()) {
      bufferedOutputStream.write(3);
      bufferedOutputStream.write(paramInetSocketAddress.getHostName().length());
      try {
        bufferedOutputStream.write(paramInetSocketAddress.getHostName().getBytes("ISO-8859-1"));
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        assert false;
      } 
      bufferedOutputStream.write(j >> 8 & 0xFF);
      bufferedOutputStream.write(j >> 0 & 0xFF);
    } else if (paramInetSocketAddress.getAddress() instanceof Inet4Address) {
      byte[] arrayOfByte = paramInetSocketAddress.getAddress().getAddress();
      bufferedOutputStream.write(1);
      bufferedOutputStream.write(arrayOfByte);
      bufferedOutputStream.write(j >> 8 & 0xFF);
      bufferedOutputStream.write(j >> 0 & 0xFF);
      bufferedOutputStream.flush();
    } else if (paramInetSocketAddress.getAddress() instanceof Inet6Address) {
      byte[] arrayOfByte = paramInetSocketAddress.getAddress().getAddress();
      bufferedOutputStream.write(4);
      bufferedOutputStream.write(arrayOfByte);
      bufferedOutputStream.write(j >> 8 & 0xFF);
      bufferedOutputStream.write(j >> 0 & 0xFF);
      bufferedOutputStream.flush();
    } else {
      this.cmdsock.close();
      throw new SocketException("unsupported address type : " + paramInetSocketAddress);
    } 
    arrayOfByte1 = new byte[4];
    i = readSocksReply(inputStream, arrayOfByte1);
    SocketException socketException = null;
    switch (arrayOfByte1[1]) {
      case 0:
        switch (arrayOfByte1[3]) {
          case 1:
            arrayOfByte2 = new byte[4];
            i = readSocksReply(inputStream, arrayOfByte2);
            if (i != 4)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            arrayOfByte1 = new byte[2];
            i = readSocksReply(inputStream, arrayOfByte1);
            if (i != 2)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            b2 = (arrayOfByte1[0] & 0xFF) << 8;
            b2 += (arrayOfByte1[1] & 0xFF);
            this.external_address = new InetSocketAddress(new Inet4Address("", arrayOfByte2), b2);
            break;
          case 3:
            b1 = arrayOfByte1[1];
            arrayOfByte3 = new byte[b1];
            i = readSocksReply(inputStream, arrayOfByte3);
            if (i != b1)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            arrayOfByte1 = new byte[2];
            i = readSocksReply(inputStream, arrayOfByte1);
            if (i != 2)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            b2 = (arrayOfByte1[0] & 0xFF) << 8;
            b2 += (arrayOfByte1[1] & 0xFF);
            this.external_address = new InetSocketAddress(new String(arrayOfByte3), b2);
            break;
          case 4:
            b1 = arrayOfByte1[1];
            arrayOfByte2 = new byte[b1];
            i = readSocksReply(inputStream, arrayOfByte2);
            if (i != b1)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            arrayOfByte1 = new byte[2];
            i = readSocksReply(inputStream, arrayOfByte1);
            if (i != 2)
              throw new SocketException("Reply from SOCKS server badly formatted"); 
            b2 = (arrayOfByte1[0] & 0xFF) << 8;
            b2 += (arrayOfByte1[1] & 0xFF);
            this.external_address = new InetSocketAddress(new Inet6Address("", arrayOfByte2), b2);
            break;
        } 
        break;
      case 1:
        socketException = new SocketException("SOCKS server general failure");
        break;
      case 2:
        socketException = new SocketException("SOCKS: Bind not allowed by ruleset");
        break;
      case 3:
        socketException = new SocketException("SOCKS: Network unreachable");
        break;
      case 4:
        socketException = new SocketException("SOCKS: Host unreachable");
        break;
      case 5:
        socketException = new SocketException("SOCKS: Connection refused");
        break;
      case 6:
        socketException = new SocketException("SOCKS: TTL expired");
        break;
      case 7:
        socketException = new SocketException("SOCKS: Command not supported");
        break;
      case 8:
        socketException = new SocketException("SOCKS: address type not supported");
        break;
    } 
    if (socketException != null) {
      inputStream.close();
      bufferedOutputStream.close();
      this.cmdsock.close();
      this.cmdsock = null;
      throw socketException;
    } 
    this.cmdIn = inputStream;
    this.cmdOut = bufferedOutputStream;
  }
  
  protected void acceptFrom(SocketImpl paramSocketImpl, InetSocketAddress paramInetSocketAddress) throws IOException {
    int k;
    byte[] arrayOfByte;
    int j;
    if (this.cmdsock == null)
      return; 
    InputStream inputStream = this.cmdIn;
    socksBind(paramInetSocketAddress);
    inputStream.read();
    int i = inputStream.read();
    inputStream.read();
    SocketException socketException = null;
    InetSocketAddress inetSocketAddress = null;
    switch (i) {
      case 0:
        i = inputStream.read();
        switch (i) {
          case 1:
            arrayOfByte = new byte[4];
            readSocksReply(inputStream, arrayOfByte);
            j = inputStream.read() << 8;
            j += inputStream.read();
            inetSocketAddress = new InetSocketAddress(new Inet4Address("", arrayOfByte), j);
            break;
          case 3:
            k = inputStream.read();
            arrayOfByte = new byte[k];
            readSocksReply(inputStream, arrayOfByte);
            j = inputStream.read() << 8;
            j += inputStream.read();
            inetSocketAddress = new InetSocketAddress(new String(arrayOfByte), j);
            break;
          case 4:
            arrayOfByte = new byte[16];
            readSocksReply(inputStream, arrayOfByte);
            j = inputStream.read() << 8;
            j += inputStream.read();
            inetSocketAddress = new InetSocketAddress(new Inet6Address("", arrayOfByte), j);
            break;
        } 
        break;
      case 1:
        socketException = new SocketException("SOCKS server general failure");
        break;
      case 2:
        socketException = new SocketException("SOCKS: Accept not allowed by ruleset");
        break;
      case 3:
        socketException = new SocketException("SOCKS: Network unreachable");
        break;
      case 4:
        socketException = new SocketException("SOCKS: Host unreachable");
        break;
      case 5:
        socketException = new SocketException("SOCKS: Connection refused");
        break;
      case 6:
        socketException = new SocketException("SOCKS: TTL expired");
        break;
      case 7:
        socketException = new SocketException("SOCKS: Command not supported");
        break;
      case 8:
        socketException = new SocketException("SOCKS: address type not supported");
        break;
    } 
    if (socketException != null) {
      this.cmdIn.close();
      this.cmdOut.close();
      this.cmdsock.close();
      this.cmdsock = null;
      throw socketException;
    } 
    if (paramSocketImpl instanceof SocksSocketImpl)
      ((SocksSocketImpl)paramSocketImpl).external_address = inetSocketAddress; 
    if (paramSocketImpl instanceof PlainSocketImpl) {
      PlainSocketImpl plainSocketImpl = (PlainSocketImpl)paramSocketImpl;
      plainSocketImpl.setInputStream((SocketInputStream)inputStream);
      plainSocketImpl.setFileDescriptor(this.cmdsock.getImpl().getFileDescriptor());
      plainSocketImpl.setAddress(this.cmdsock.getImpl().getInetAddress());
      plainSocketImpl.setPort(this.cmdsock.getImpl().getPort());
      plainSocketImpl.setLocalPort(this.cmdsock.getImpl().getLocalPort());
    } else {
      paramSocketImpl.fd = (this.cmdsock.getImpl()).fd;
      paramSocketImpl.address = (this.cmdsock.getImpl()).address;
      paramSocketImpl.port = (this.cmdsock.getImpl()).port;
      paramSocketImpl.localport = (this.cmdsock.getImpl()).localport;
    } 
    this.cmdsock = null;
  }
  
  protected InetAddress getInetAddress() { return (this.external_address != null) ? this.external_address.getAddress() : super.getInetAddress(); }
  
  protected int getPort() { return (this.external_address != null) ? this.external_address.getPort() : super.getPort(); }
  
  protected int getLocalPort() { return (this.socket != null) ? super.getLocalPort() : ((this.external_address != null) ? this.external_address.getPort() : super.getLocalPort()); }
  
  protected void close() {
    if (this.cmdsock != null)
      this.cmdsock.close(); 
    this.cmdsock = null;
    super.close();
  }
  
  private String getUserName() {
    String str = "";
    if (this.applicationSetProxy) {
      try {
        str = System.getProperty("user.name");
      } catch (SecurityException securityException) {}
    } else {
      str = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\SocksSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */