package sun.rmi.transport.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.LogStream;
import java.rmi.server.RMIFailureHandler;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.transport.Channel;
import sun.rmi.transport.Connection;
import sun.rmi.transport.DGCAckHandler;
import sun.rmi.transport.Endpoint;
import sun.rmi.transport.StreamRemoteCall;
import sun.rmi.transport.Target;
import sun.rmi.transport.Transport;
import sun.rmi.transport.proxy.HttpReceiveSocket;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetLongAction;
import sun.security.action.GetPropertyAction;

public class TCPTransport extends Transport {
  static final Log tcpLog = Log.getLog("sun.rmi.transport.tcp", "tcp", LogStream.parseLevel((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.tcp.logLevel"))));
  
  private static final int maxConnectionThreads = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.maxConnectionThreads", 2147483647))).intValue();
  
  private static final long threadKeepAliveTime = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.tcp.threadKeepAliveTime", 60000L))).longValue();
  
  private static final ExecutorService connectionThreadPool = new ThreadPoolExecutor(0, maxConnectionThreads, threadKeepAliveTime, TimeUnit.MILLISECONDS, new SynchronousQueue(), new ThreadFactory() {
        public Thread newThread(Runnable param1Runnable) { return (Thread)AccessController.doPrivileged(new NewThreadAction(param1Runnable, "TCP Connection(idle)", true, true)); }
      });
  
  private static final boolean disableIncomingHttp = ((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.server.disableIncomingHttp", "true"))).equalsIgnoreCase("true");
  
  private static final AtomicInteger connectionCount = new AtomicInteger(0);
  
  private static final ThreadLocal<ConnectionHandler> threadConnectionHandler = new ThreadLocal();
  
  private static final AccessControlContext NOPERMS_ACC;
  
  private final LinkedList<TCPEndpoint> epList;
  
  private int exportCount = 0;
  
  private ServerSocket server = null;
  
  private final Map<TCPEndpoint, Reference<TCPChannel>> channelTable = new WeakHashMap();
  
  static final RMISocketFactory defaultSocketFactory;
  
  private static final int connectionReadTimeout;
  
  TCPTransport(LinkedList<TCPEndpoint> paramLinkedList) {
    this.epList = paramLinkedList;
    if (tcpLog.isLoggable(Log.BRIEF))
      tcpLog.log(Log.BRIEF, "Version = 2, ep = " + getEndpoint()); 
  }
  
  public void shedConnectionCaches() {
    ArrayList arrayList;
    synchronized (this.channelTable) {
      arrayList = new ArrayList(this.channelTable.values().size());
      for (Reference reference : this.channelTable.values()) {
        TCPChannel tCPChannel = (TCPChannel)reference.get();
        if (tCPChannel != null)
          arrayList.add(tCPChannel); 
      } 
    } 
    for (TCPChannel tCPChannel : arrayList)
      tCPChannel.shedCache(); 
  }
  
  public TCPChannel getChannel(Endpoint paramEndpoint) {
    TCPChannel tCPChannel = null;
    if (paramEndpoint instanceof TCPEndpoint)
      synchronized (this.channelTable) {
        Reference reference = (Reference)this.channelTable.get(paramEndpoint);
        if (reference != null)
          tCPChannel = (TCPChannel)reference.get(); 
        if (tCPChannel == null) {
          TCPEndpoint tCPEndpoint = (TCPEndpoint)paramEndpoint;
          tCPChannel = new TCPChannel(this, tCPEndpoint);
          this.channelTable.put(tCPEndpoint, new WeakReference(tCPChannel));
        } 
      }  
    return tCPChannel;
  }
  
  public void free(Endpoint paramEndpoint) {
    if (paramEndpoint instanceof TCPEndpoint)
      synchronized (this.channelTable) {
        Reference reference = (Reference)this.channelTable.remove(paramEndpoint);
        if (reference != null) {
          TCPChannel tCPChannel = (TCPChannel)reference.get();
          if (tCPChannel != null)
            tCPChannel.shedCache(); 
        } 
      }  
  }
  
  public void exportObject(Target paramTarget) throws RemoteException {
    synchronized (this) {
      listen();
      this.exportCount++;
    } 
    bool = false;
    try {
      super.exportObject(paramTarget);
      bool = true;
    } finally {
      if (!bool)
        synchronized (this) {
          decrementExportCount();
        }  
    } 
  }
  
  protected void targetUnexported() { decrementExportCount(); }
  
  private void decrementExportCount() {
    assert Thread.holdsLock(this);
    this.exportCount--;
    if (this.exportCount == 0 && getEndpoint().getListenPort() != 0) {
      ServerSocket serverSocket = this.server;
      this.server = null;
      try {
        serverSocket.close();
      } catch (IOException iOException) {}
    } 
  }
  
  protected void checkAcceptPermission(AccessControlContext paramAccessControlContext) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return; 
    ConnectionHandler connectionHandler = (ConnectionHandler)threadConnectionHandler.get();
    if (connectionHandler == null)
      throw new Error("checkAcceptPermission not in ConnectionHandler thread"); 
    connectionHandler.checkAcceptPermission(securityManager, paramAccessControlContext);
  }
  
  private TCPEndpoint getEndpoint() {
    synchronized (this.epList) {
      return (TCPEndpoint)this.epList.getLast();
    } 
  }
  
  private void listen() {
    assert Thread.holdsLock(this);
    TCPEndpoint tCPEndpoint = getEndpoint();
    int i = tCPEndpoint.getPort();
    if (this.server == null) {
      if (tcpLog.isLoggable(Log.BRIEF))
        tcpLog.log(Log.BRIEF, "(port " + i + ") create server socket"); 
      try {
        this.server = tCPEndpoint.newServerSocket();
        Thread thread = (Thread)AccessController.doPrivileged(new NewThreadAction(new AcceptLoop(this, this.server), "TCP Accept-" + i, true));
        thread.start();
      } catch (BindException bindException) {
        throw new ExportException("Port already in use: " + i, bindException);
      } catch (IOException iOException) {
        throw new ExportException("Listen failed on port: " + i, iOException);
      } 
    } else {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkListen(i); 
    } 
  }
  
  private static void closeSocket(Socket paramSocket) {
    try {
      paramSocket.close();
    } catch (IOException iOException) {}
  }
  
  void handleMessages(Connection paramConnection, boolean paramBoolean) {
    int i = getEndpoint().getPort();
    try {
      dataInputStream = new DataInputStream(paramConnection.getInputStream());
      do {
        DataOutputStream dataOutputStream;
        StreamRemoteCall streamRemoteCall;
        int j = dataInputStream.read();
        if (j == -1) {
          if (tcpLog.isLoggable(Log.BRIEF))
            tcpLog.log(Log.BRIEF, "(port " + i + ") connection closed"); 
          break;
        } 
        if (tcpLog.isLoggable(Log.BRIEF))
          tcpLog.log(Log.BRIEF, "(port " + i + ") op = " + j); 
        switch (j) {
          case 80:
            streamRemoteCall = new StreamRemoteCall(paramConnection);
            if (!serviceCall(streamRemoteCall))
              return; 
            break;
          case 82:
            dataOutputStream = new DataOutputStream(paramConnection.getOutputStream());
            dataOutputStream.writeByte(83);
            paramConnection.releaseOutputStream();
            break;
          case 84:
            DGCAckHandler.received(UID.read(dataInputStream));
            break;
          default:
            throw new IOException("unknown transport op " + j);
        } 
      } while (paramBoolean);
    } catch (IOException iOException) {
      if (tcpLog.isLoggable(Log.BRIEF))
        tcpLog.log(Log.BRIEF, "(port " + i + ") exception: ", iOException); 
    } finally {
      try {
        paramConnection.close();
      } catch (IOException iOException) {}
    } 
  }
  
  public static String getClientHost() throws ServerNotActiveException {
    ConnectionHandler connectionHandler = (ConnectionHandler)threadConnectionHandler.get();
    if (connectionHandler != null)
      return connectionHandler.getClientHost(); 
    throw new ServerNotActiveException("not in a remote call");
  }
  
  static  {
    Permissions permissions = new Permissions();
    ProtectionDomain[] arrayOfProtectionDomain = { new ProtectionDomain(null, permissions) };
    NOPERMS_ACC = new AccessControlContext(arrayOfProtectionDomain);
    defaultSocketFactory = RMISocketFactory.getDefaultSocketFactory();
    connectionReadTimeout = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.readTimeout", 7200000))).intValue();
  }
  
  private class AcceptLoop implements Runnable {
    private final ServerSocket serverSocket;
    
    private long lastExceptionTime = 0L;
    
    private int recentExceptionCount;
    
    AcceptLoop(ServerSocket param1ServerSocket) { this.serverSocket = param1ServerSocket; }
    
    public void run() {
      try {
        executeAcceptLoop();
      } finally {
        try {
          this.serverSocket.close();
        } catch (IOException iOException) {}
      } 
    }
    
    private void executeAcceptLoop() {
      if (TCPTransport.tcpLog.isLoggable(Log.BRIEF))
        TCPTransport.tcpLog.log(Log.BRIEF, "listening on port " + TCPTransport.this.getEndpoint().getPort()); 
      while (true) {
        socket = null;
        try {
          socket = this.serverSocket.accept();
          InetAddress inetAddress = socket.getInetAddress();
          String str = (inetAddress != null) ? inetAddress.getHostAddress() : "0.0.0.0";
          try {
            connectionThreadPool.execute(new TCPTransport.ConnectionHandler(TCPTransport.this, socket, str));
          } catch (RejectedExecutionException rejectedExecutionException) {
            TCPTransport.closeSocket(socket);
            TCPTransport.tcpLog.log(Log.BRIEF, "rejected connection from " + str);
          } 
        } catch (Throwable throwable) {
          try {
            if (this.serverSocket.isClosed()) {
              if (socket != null)
                TCPTransport.closeSocket(socket); 
              break;
            } 
            try {
              if (TCPTransport.tcpLog.isLoggable(Level.WARNING))
                TCPTransport.tcpLog.log(Level.WARNING, "accept loop for " + this.serverSocket + " throws", throwable); 
            } catch (Throwable throwable1) {}
          } finally {
            if (socket != null)
              TCPTransport.closeSocket(socket); 
          } 
          if (!(throwable instanceof SecurityException))
            try {
              TCPEndpoint.shedConnectionCaches();
            } catch (Throwable throwable1) {} 
          if (throwable instanceof Exception || throwable instanceof OutOfMemoryError || throwable instanceof NoClassDefFoundError) {
            if (!continueAfterAcceptFailure(throwable))
              return; 
            continue;
          } 
          if (throwable instanceof Error)
            throw (Error)throwable; 
          throw new UndeclaredThrowableException(throwable);
        } 
      } 
    }
    
    private boolean continueAfterAcceptFailure(Throwable param1Throwable) {
      RMIFailureHandler rMIFailureHandler = RMISocketFactory.getFailureHandler();
      if (rMIFailureHandler != null)
        return rMIFailureHandler.failure((param1Throwable instanceof Exception) ? (Exception)param1Throwable : new InvocationTargetException(param1Throwable)); 
      throttleLoopOnException();
      return true;
    }
    
    private void throttleLoopOnException() {
      long l = System.currentTimeMillis();
      if (this.lastExceptionTime == 0L || l - this.lastExceptionTime > 5000L) {
        this.lastExceptionTime = l;
        this.recentExceptionCount = 0;
      } else if (++this.recentExceptionCount >= 10) {
        try {
          Thread.sleep(10000L);
        } catch (InterruptedException interruptedException) {}
      } 
    }
  }
  
  private class ConnectionHandler implements Runnable {
    private static final int POST = 1347375956;
    
    private AccessControlContext okContext;
    
    private Map<AccessControlContext, Reference<AccessControlContext>> authCache;
    
    private SecurityManager cacheSecurityManager = null;
    
    private Socket socket;
    
    private String remoteHost;
    
    ConnectionHandler(Socket param1Socket, String param1String) {
      this.socket = param1Socket;
      this.remoteHost = param1String;
    }
    
    String getClientHost() throws ServerNotActiveException { return this.remoteHost; }
    
    void checkAcceptPermission(SecurityManager param1SecurityManager, AccessControlContext param1AccessControlContext) {
      if (param1SecurityManager != this.cacheSecurityManager) {
        this.okContext = null;
        this.authCache = new WeakHashMap();
        this.cacheSecurityManager = param1SecurityManager;
      } 
      if (param1AccessControlContext.equals(this.okContext) || this.authCache.containsKey(param1AccessControlContext))
        return; 
      InetAddress inetAddress = this.socket.getInetAddress();
      String str = (inetAddress != null) ? inetAddress.getHostAddress() : "*";
      param1SecurityManager.checkAccept(str, this.socket.getPort());
      this.authCache.put(param1AccessControlContext, new SoftReference(param1AccessControlContext));
      this.okContext = param1AccessControlContext;
    }
    
    public void run() {
      thread = Thread.currentThread();
      str = thread.getName();
      try {
        thread.setName("RMI TCP Connection(" + connectionCount.incrementAndGet() + ")-" + this.remoteHost);
        AccessController.doPrivileged(() -> {
              run0();
              return null;
            }NOPERMS_ACC);
      } finally {
        thread.setName(str);
      } 
    }
    
    private void run0() {
      TCPEndpoint tCPEndpoint = TCPTransport.this.getEndpoint();
      int i = tCPEndpoint.getPort();
      threadConnectionHandler.set(this);
      try {
        this.socket.setTcpNoDelay(true);
      } catch (Exception exception) {}
      try {
        if (connectionReadTimeout > 0)
          this.socket.setSoTimeout(connectionReadTimeout); 
      } catch (Exception exception) {}
      try {
        ConnectionMultiplexer connectionMultiplexer;
        int m;
        String str;
        TCPConnection tCPConnection;
        TCPChannel tCPChannel;
        TCPEndpoint tCPEndpoint1;
        InputStream inputStream1 = this.socket.getInputStream();
        InputStream inputStream2 = inputStream1.markSupported() ? inputStream1 : new BufferedInputStream(inputStream1);
        inputStream2.mark(4);
        DataInputStream dataInputStream = new DataInputStream(inputStream2);
        int j = dataInputStream.readInt();
        if (j == 1347375956) {
          if (disableIncomingHttp)
            throw new RemoteException("RMI over HTTP is disabled"); 
          TCPTransport.tcpLog.log(Log.BRIEF, "decoding HTTP-wrapped call");
          inputStream2.reset();
          try {
            this.socket = new HttpReceiveSocket(this.socket, inputStream2, null);
            this.remoteHost = "0.0.0.0";
            inputStream1 = this.socket.getInputStream();
            inputStream2 = new BufferedInputStream(inputStream1);
            dataInputStream = new DataInputStream(inputStream2);
            j = dataInputStream.readInt();
          } catch (IOException iOException) {
            throw new RemoteException("Error HTTP-unwrapping call", iOException);
          } 
        } 
        short s = dataInputStream.readShort();
        if (j != 1246907721 || s != 2) {
          TCPTransport.closeSocket(this.socket);
          return;
        } 
        OutputStream outputStream = this.socket.getOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
        int k = this.socket.getPort();
        if (TCPTransport.tcpLog.isLoggable(Log.BRIEF))
          TCPTransport.tcpLog.log(Log.BRIEF, "accepted socket from [" + this.remoteHost + ":" + k + "]"); 
        byte b = dataInputStream.readByte();
        switch (b) {
          case 76:
            tCPEndpoint1 = new TCPEndpoint(this.remoteHost, this.socket.getLocalPort(), tCPEndpoint.getClientSocketFactory(), tCPEndpoint.getServerSocketFactory());
            tCPChannel = new TCPChannel(TCPTransport.this, tCPEndpoint1);
            tCPConnection = new TCPConnection(tCPChannel, this.socket, inputStream2, bufferedOutputStream);
            TCPTransport.this.handleMessages(tCPConnection, false);
            break;
          case 75:
            dataOutputStream.writeByte(78);
            if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
              TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") suggesting " + this.remoteHost + ":" + k); 
            dataOutputStream.writeUTF(this.remoteHost);
            dataOutputStream.writeInt(k);
            dataOutputStream.flush();
            str = dataInputStream.readUTF();
            m = dataInputStream.readInt();
            if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
              TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") client using " + str + ":" + m); 
            tCPEndpoint1 = new TCPEndpoint(this.remoteHost, this.socket.getLocalPort(), tCPEndpoint.getClientSocketFactory(), tCPEndpoint.getServerSocketFactory());
            tCPChannel = new TCPChannel(TCPTransport.this, tCPEndpoint1);
            tCPConnection = new TCPConnection(tCPChannel, this.socket, inputStream2, bufferedOutputStream);
            TCPTransport.this.handleMessages(tCPConnection, true);
            break;
          case 77:
            if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
              TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") accepting multiplex protocol"); 
            dataOutputStream.writeByte(78);
            if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
              TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") suggesting " + this.remoteHost + ":" + k); 
            dataOutputStream.writeUTF(this.remoteHost);
            dataOutputStream.writeInt(k);
            dataOutputStream.flush();
            tCPEndpoint1 = new TCPEndpoint(dataInputStream.readUTF(), dataInputStream.readInt(), tCPEndpoint.getClientSocketFactory(), tCPEndpoint.getServerSocketFactory());
            if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
              TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") client using " + tCPEndpoint1.getHost() + ":" + tCPEndpoint1.getPort()); 
            synchronized (TCPTransport.this.channelTable) {
              tCPChannel = TCPTransport.this.getChannel(tCPEndpoint1);
              connectionMultiplexer = new ConnectionMultiplexer(tCPChannel, inputStream2, outputStream, false);
              tCPChannel.useMultiplexer(connectionMultiplexer);
            } 
            connectionMultiplexer.run();
            break;
          default:
            dataOutputStream.writeByte(79);
            dataOutputStream.flush();
            break;
        } 
      } catch (IOException iOException) {
        TCPTransport.tcpLog.log(Log.BRIEF, "terminated with exception:", iOException);
      } finally {
        TCPTransport.closeSocket(this.socket);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\tcp\TCPTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */