package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.Socket;
import java.rmi.ConnectIOException;
import java.rmi.RemoteException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.WeakHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.RuntimeUtil;
import sun.rmi.transport.Channel;
import sun.rmi.transport.Connection;
import sun.rmi.transport.Endpoint;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetLongAction;

public class TCPChannel implements Channel {
  private final TCPEndpoint ep;
  
  private final TCPTransport tr;
  
  private final List<TCPConnection> freeList = new ArrayList();
  
  private Future<?> reaper = null;
  
  private boolean usingMultiplexer = false;
  
  private ConnectionMultiplexer multiplexer = null;
  
  private ConnectionAcceptor acceptor;
  
  private AccessControlContext okContext;
  
  private WeakHashMap<AccessControlContext, Reference<AccessControlContext>> authcache;
  
  private SecurityManager cacheSecurityManager = null;
  
  private static final long idleTimeout = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.connectionTimeout", 15000L))).longValue();
  
  private static final int handshakeTimeout = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.handshakeTimeout", 60000))).intValue();
  
  private static final int responseTimeout = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.responseTimeout", 0))).intValue();
  
  private static final ScheduledExecutorService scheduler = ((RuntimeUtil)AccessController.doPrivileged(new RuntimeUtil.GetInstanceAction())).getScheduler();
  
  TCPChannel(TCPTransport paramTCPTransport, TCPEndpoint paramTCPEndpoint) {
    this.tr = paramTCPTransport;
    this.ep = paramTCPEndpoint;
  }
  
  public Endpoint getEndpoint() { return this.ep; }
  
  private void checkConnectPermission() throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return; 
    if (securityManager != this.cacheSecurityManager) {
      this.okContext = null;
      this.authcache = new WeakHashMap();
      this.cacheSecurityManager = securityManager;
    } 
    AccessControlContext accessControlContext = AccessController.getContext();
    if (this.okContext == null || (!this.okContext.equals(accessControlContext) && !this.authcache.containsKey(accessControlContext))) {
      securityManager.checkConnect(this.ep.getHost(), this.ep.getPort());
      this.authcache.put(accessControlContext, new SoftReference(accessControlContext));
    } 
    this.okContext = accessControlContext;
  }
  
  public Connection newConnection() throws RemoteException {
    TCPConnection tCPConnection;
    do {
      tCPConnection = null;
      synchronized (this.freeList) {
        int i = this.freeList.size() - 1;
        if (i >= 0) {
          checkConnectPermission();
          tCPConnection = (TCPConnection)this.freeList.get(i);
          this.freeList.remove(i);
        } 
      } 
      if (tCPConnection == null)
        continue; 
      if (!tCPConnection.isDead()) {
        TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
        return tCPConnection;
      } 
      free(tCPConnection, false);
    } while (tCPConnection != null);
    return createConnection();
  }
  
  private Connection createConnection() throws RemoteException {
    TCPConnection tCPConnection;
    TCPTransport.tcpLog.log(Log.BRIEF, "create connection");
    if (!this.usingMultiplexer) {
      Socket socket = this.ep.newSocket();
      tCPConnection = new TCPConnection(this, socket);
      try {
        DataOutputStream dataOutputStream = new DataOutputStream(tCPConnection.getOutputStream());
        writeTransportHeader(dataOutputStream);
        if (!tCPConnection.isReusable()) {
          dataOutputStream.writeByte(76);
        } else {
          dataOutputStream.writeByte(75);
          dataOutputStream.flush();
          int i = 0;
          try {
            i = socket.getSoTimeout();
            socket.setSoTimeout(handshakeTimeout);
          } catch (Exception exception) {}
          DataInputStream dataInputStream = new DataInputStream(tCPConnection.getInputStream());
          byte b = dataInputStream.readByte();
          if (b != 78)
            throw new ConnectIOException((b == 79) ? "JRMP StreamProtocol not supported by server" : "non-JRMP server at remote endpoint"); 
          String str = dataInputStream.readUTF();
          int j = dataInputStream.readInt();
          if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
            TCPTransport.tcpLog.log(Log.VERBOSE, "server suggested " + str + ":" + j); 
          TCPEndpoint.setLocalHost(str);
          TCPEndpoint tCPEndpoint = TCPEndpoint.getLocalEndpoint(0, null, null);
          dataOutputStream.writeUTF(tCPEndpoint.getHost());
          dataOutputStream.writeInt(tCPEndpoint.getPort());
          if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
            TCPTransport.tcpLog.log(Log.VERBOSE, "using " + tCPEndpoint.getHost() + ":" + tCPEndpoint.getPort()); 
          try {
            socket.setSoTimeout((i != 0) ? i : responseTimeout);
          } catch (Exception exception) {}
          dataOutputStream.flush();
        } 
      } catch (IOException iOException) {
        try {
          tCPConnection.close();
        } catch (Exception exception) {}
        if (iOException instanceof RemoteException)
          throw (RemoteException)iOException; 
        throw new ConnectIOException("error during JRMP connection establishment", iOException);
      } 
    } else {
      try {
        tCPConnection = this.multiplexer.openConnection();
      } catch (IOException iOException) {
        synchronized (this) {
          this.usingMultiplexer = false;
          this.multiplexer = null;
        } 
        throw new ConnectIOException("error opening virtual connection over multiplexed connection", iOException);
      } 
    } 
    return tCPConnection;
  }
  
  public void free(Connection paramConnection, boolean paramBoolean) {
    if (paramConnection == null)
      return; 
    if (paramBoolean && paramConnection.isReusable()) {
      long l = System.currentTimeMillis();
      TCPConnection tCPConnection = (TCPConnection)paramConnection;
      TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
      synchronized (this.freeList) {
        this.freeList.add(tCPConnection);
        if (this.reaper == null) {
          TCPTransport.tcpLog.log(Log.BRIEF, "create reaper");
          this.reaper = scheduler.scheduleWithFixedDelay(new Runnable() {
                public void run() throws SecurityException {
                  TCPTransport.tcpLog.log(Log.VERBOSE, "wake up");
                  TCPChannel.this.freeCachedConnections();
                }
              }idleTimeout, idleTimeout, TimeUnit.MILLISECONDS);
        } 
      } 
      tCPConnection.setLastUseTime(l);
      tCPConnection.setExpiration(l + idleTimeout);
    } else {
      TCPTransport.tcpLog.log(Log.BRIEF, "close connection");
      try {
        paramConnection.close();
      } catch (IOException iOException) {}
    } 
  }
  
  private void writeTransportHeader(DataOutputStream paramDataOutputStream) throws RemoteException {
    try {
      DataOutputStream dataOutputStream = new DataOutputStream(paramDataOutputStream);
      dataOutputStream.writeInt(1246907721);
      dataOutputStream.writeShort(2);
    } catch (IOException iOException) {
      throw new ConnectIOException("error writing JRMP transport header", iOException);
    } 
  }
  
  void useMultiplexer(ConnectionMultiplexer paramConnectionMultiplexer) {
    this.multiplexer = paramConnectionMultiplexer;
    this.usingMultiplexer = true;
  }
  
  void acceptMultiplexConnection(Connection paramConnection) {
    if (this.acceptor == null) {
      this.acceptor = new ConnectionAcceptor(this.tr);
      this.acceptor.startNewAcceptor();
    } 
    this.acceptor.accept(paramConnection);
  }
  
  public void shedCache() throws SecurityException {
    Connection[] arrayOfConnection;
    synchronized (this.freeList) {
      arrayOfConnection = (Connection[])this.freeList.toArray(new Connection[this.freeList.size()]);
      this.freeList.clear();
    } 
    int i = arrayOfConnection.length;
    while (--i >= 0) {
      Connection connection = arrayOfConnection[i];
      arrayOfConnection[i] = null;
      try {
        connection.close();
      } catch (IOException iOException) {}
    } 
  }
  
  private void freeCachedConnections() throws SecurityException {
    synchronized (this.freeList) {
      int i = this.freeList.size();
      if (i > 0) {
        long l = System.currentTimeMillis();
        ListIterator listIterator = this.freeList.listIterator(i);
        while (listIterator.hasPrevious()) {
          TCPConnection tCPConnection = (TCPConnection)listIterator.previous();
          if (tCPConnection.expired(l)) {
            TCPTransport.tcpLog.log(Log.VERBOSE, "connection timeout expired");
            try {
              tCPConnection.close();
            } catch (IOException iOException) {}
            listIterator.remove();
          } 
        } 
      } 
      if (this.freeList.isEmpty()) {
        this.reaper.cancel(false);
        this.reaper = null;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\tcp\TCPChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */