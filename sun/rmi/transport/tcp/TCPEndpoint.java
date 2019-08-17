package sun.rmi.transport.tcp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.security.AccessController;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.transport.Channel;
import sun.rmi.transport.Endpoint;
import sun.rmi.transport.Target;
import sun.rmi.transport.Transport;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;

public class TCPEndpoint implements Endpoint {
  private String host;
  
  private int port;
  
  private final RMIClientSocketFactory csf;
  
  private final RMIServerSocketFactory ssf;
  
  private int listenPort = -1;
  
  private TCPTransport transport = null;
  
  private static String localHost;
  
  private static boolean localHostKnown = true;
  
  private static final Map<TCPEndpoint, LinkedList<TCPEndpoint>> localEndpoints;
  
  private static final int FORMAT_HOST_PORT = 0;
  
  private static final int FORMAT_HOST_PORT_FACTORY = 1;
  
  private static int getInt(String paramString, int paramInt) { return ((Integer)AccessController.doPrivileged(new GetIntegerAction(paramString, paramInt))).intValue(); }
  
  private static boolean getBoolean(String paramString) { return ((Boolean)AccessController.doPrivileged(new GetBooleanAction(paramString))).booleanValue(); }
  
  private static String getHostnameProperty() { return (String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.hostname")); }
  
  public TCPEndpoint(String paramString, int paramInt) { this(paramString, paramInt, null, null); }
  
  public TCPEndpoint(String paramString, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) {
    if (paramString == null)
      paramString = ""; 
    this.host = paramString;
    this.port = paramInt;
    this.csf = paramRMIClientSocketFactory;
    this.ssf = paramRMIServerSocketFactory;
  }
  
  public static TCPEndpoint getLocalEndpoint(int paramInt) { return getLocalEndpoint(paramInt, null, null); }
  
  public static TCPEndpoint getLocalEndpoint(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) {
    TCPEndpoint tCPEndpoint = null;
    synchronized (localEndpoints) {
      TCPEndpoint tCPEndpoint1 = new TCPEndpoint(null, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
      LinkedList linkedList = (LinkedList)localEndpoints.get(tCPEndpoint1);
      String str = resampleLocalHost();
      if (linkedList == null) {
        tCPEndpoint = new TCPEndpoint(str, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
        linkedList = new LinkedList();
        linkedList.add(tCPEndpoint);
        tCPEndpoint.listenPort = paramInt;
        tCPEndpoint.transport = new TCPTransport(linkedList);
        localEndpoints.put(tCPEndpoint1, linkedList);
        if (TCPTransport.tcpLog.isLoggable(Log.BRIEF))
          TCPTransport.tcpLog.log(Log.BRIEF, "created local endpoint for socket factory " + paramRMIServerSocketFactory + " on port " + paramInt); 
      } else {
        synchronized (linkedList) {
          tCPEndpoint = (TCPEndpoint)linkedList.getLast();
          String str1 = tCPEndpoint.host;
          int i = tCPEndpoint.port;
          TCPTransport tCPTransport = tCPEndpoint.transport;
          if (str != null && !str.equals(str1)) {
            if (i != 0)
              linkedList.clear(); 
            tCPEndpoint = new TCPEndpoint(str, i, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
            tCPEndpoint.listenPort = paramInt;
            tCPEndpoint.transport = tCPTransport;
            linkedList.add(tCPEndpoint);
          } 
        } 
      } 
    } 
    return tCPEndpoint;
  }
  
  private static String resampleLocalHost() {
    String str = getHostnameProperty();
    synchronized (localEndpoints) {
      if (str != null)
        if (!localHostKnown) {
          setLocalHost(str);
        } else if (!str.equals(localHost)) {
          localHost = str;
          if (TCPTransport.tcpLog.isLoggable(Log.BRIEF))
            TCPTransport.tcpLog.log(Log.BRIEF, "updated local hostname to: " + localHost); 
        }  
      return localHost;
    } 
  }
  
  static void setLocalHost(String paramString) {
    synchronized (localEndpoints) {
      if (!localHostKnown) {
        localHost = paramString;
        localHostKnown = true;
        if (TCPTransport.tcpLog.isLoggable(Log.BRIEF))
          TCPTransport.tcpLog.log(Log.BRIEF, "local host set to " + paramString); 
        for (LinkedList linkedList : localEndpoints.values()) {
          synchronized (linkedList) {
            for (TCPEndpoint tCPEndpoint : linkedList)
              tCPEndpoint.host = paramString; 
          } 
        } 
      } 
    } 
  }
  
  static void setDefaultPort(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) {
    TCPEndpoint tCPEndpoint = new TCPEndpoint(null, 0, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
    synchronized (localEndpoints) {
      LinkedList linkedList = (LinkedList)localEndpoints.get(tCPEndpoint);
      synchronized (linkedList) {
        int i = linkedList.size();
        TCPEndpoint tCPEndpoint2 = (TCPEndpoint)linkedList.getLast();
        for (TCPEndpoint tCPEndpoint3 : linkedList)
          tCPEndpoint3.port = paramInt; 
        if (i > 1) {
          linkedList.clear();
          linkedList.add(tCPEndpoint2);
        } 
      } 
      TCPEndpoint tCPEndpoint1 = new TCPEndpoint(null, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
      localEndpoints.put(tCPEndpoint1, linkedList);
      if (TCPTransport.tcpLog.isLoggable(Log.BRIEF))
        TCPTransport.tcpLog.log(Log.BRIEF, "default port for server socket factory " + paramRMIServerSocketFactory + " and client socket factory " + paramRMIClientSocketFactory + " set to " + paramInt); 
    } 
  }
  
  public Transport getOutboundTransport() {
    TCPEndpoint tCPEndpoint = getLocalEndpoint(0, null, null);
    return tCPEndpoint.transport;
  }
  
  private static Collection<TCPTransport> allKnownTransports() {
    HashSet hashSet;
    synchronized (localEndpoints) {
      hashSet = new HashSet(localEndpoints.size());
      for (LinkedList linkedList : localEndpoints.values()) {
        TCPEndpoint tCPEndpoint = (TCPEndpoint)linkedList.getFirst();
        hashSet.add(tCPEndpoint.transport);
      } 
    } 
    return hashSet;
  }
  
  public static void shedConnectionCaches() {
    for (TCPTransport tCPTransport : allKnownTransports())
      tCPTransport.shedConnectionCaches(); 
  }
  
  public void exportObject(Target paramTarget) throws RemoteException { this.transport.exportObject(paramTarget); }
  
  public Channel getChannel() { return getOutboundTransport().getChannel(this); }
  
  public String getHost() { return this.host; }
  
  public int getPort() { return this.port; }
  
  public int getListenPort() { return this.listenPort; }
  
  public Transport getInboundTransport() { return this.transport; }
  
  public RMIClientSocketFactory getClientSocketFactory() { return this.csf; }
  
  public RMIServerSocketFactory getServerSocketFactory() { return this.ssf; }
  
  public String toString() { return "[" + this.host + ":" + this.port + ((this.ssf != null) ? ("," + this.ssf) : "") + ((this.csf != null) ? ("," + this.csf) : "") + "]"; }
  
  public int hashCode() { return this.port; }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof TCPEndpoint) {
      TCPEndpoint tCPEndpoint = (TCPEndpoint)paramObject;
      if (this.port != tCPEndpoint.port || !this.host.equals(tCPEndpoint.host))
        return false; 
      if (!(((this.csf == null) ? 1 : 0) ^ ((tCPEndpoint.csf == null) ? 1 : 0)))
        if (!(((this.ssf == null) ? 1 : 0) ^ ((tCPEndpoint.ssf == null) ? 1 : 0)))
          return (this.csf != null && (this.csf.getClass() != tCPEndpoint.csf.getClass() || !this.csf.equals(tCPEndpoint.csf))) ? false : (!(this.ssf != null && (this.ssf.getClass() != tCPEndpoint.ssf.getClass() || !this.ssf.equals(tCPEndpoint.ssf))));  
      return false;
    } 
    return false;
  }
  
  public void write(ObjectOutput paramObjectOutput) throws IOException {
    if (this.csf == null) {
      paramObjectOutput.writeByte(0);
      paramObjectOutput.writeUTF(this.host);
      paramObjectOutput.writeInt(this.port);
    } else {
      paramObjectOutput.writeByte(1);
      paramObjectOutput.writeUTF(this.host);
      paramObjectOutput.writeInt(this.port);
      paramObjectOutput.writeObject(this.csf);
    } 
  }
  
  public static TCPEndpoint read(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    int i;
    String str;
    RMIClientSocketFactory rMIClientSocketFactory = null;
    byte b = paramObjectInput.readByte();
    switch (b) {
      case 0:
        str = paramObjectInput.readUTF();
        i = paramObjectInput.readInt();
        return new TCPEndpoint(str, i, rMIClientSocketFactory, null);
      case 1:
        str = paramObjectInput.readUTF();
        i = paramObjectInput.readInt();
        rMIClientSocketFactory = (RMIClientSocketFactory)paramObjectInput.readObject();
        return new TCPEndpoint(str, i, rMIClientSocketFactory, null);
    } 
    throw new IOException("invalid endpoint format");
  }
  
  public void writeHostPortFormat(DataOutput paramDataOutput) throws IOException {
    if (this.csf != null)
      throw new InternalError("TCPEndpoint.writeHostPortFormat: called for endpoint with non-null socket factory"); 
    paramDataOutput.writeUTF(this.host);
    paramDataOutput.writeInt(this.port);
  }
  
  public static TCPEndpoint readHostPortFormat(DataInput paramDataInput) throws IOException {
    String str = paramDataInput.readUTF();
    int i = paramDataInput.readInt();
    return new TCPEndpoint(str, i);
  }
  
  private static RMISocketFactory chooseFactory() {
    RMISocketFactory rMISocketFactory = RMISocketFactory.getSocketFactory();
    if (rMISocketFactory == null)
      rMISocketFactory = TCPTransport.defaultSocketFactory; 
    return rMISocketFactory;
  }
  
  Socket newSocket() throws RemoteException {
    Socket socket;
    if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
      TCPTransport.tcpLog.log(Log.VERBOSE, "opening socket to " + this); 
    try {
      RMIClientSocketFactory rMIClientSocketFactory = this.csf;
      if (rMIClientSocketFactory == null)
        rMIClientSocketFactory = chooseFactory(); 
      socket = rMIClientSocketFactory.createSocket(this.host, this.port);
    } catch (UnknownHostException unknownHostException) {
      throw new UnknownHostException("Unknown host: " + this.host, unknownHostException);
    } catch (ConnectException connectException) {
      throw new ConnectException("Connection refused to host: " + this.host, connectException);
    } catch (IOException iOException) {
      try {
        shedConnectionCaches();
      } catch (OutOfMemoryError|Exception outOfMemoryError) {}
      throw new ConnectIOException("Exception creating connection to: " + this.host, iOException);
    } 
    try {
      socket.setTcpNoDelay(true);
    } catch (Exception exception) {}
    try {
      socket.setKeepAlive(true);
    } catch (Exception exception) {}
    return socket;
  }
  
  ServerSocket newServerSocket() throws IOException {
    if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
      TCPTransport.tcpLog.log(Log.VERBOSE, "creating server socket on " + this); 
    RMIServerSocketFactory rMIServerSocketFactory = this.ssf;
    if (rMIServerSocketFactory == null)
      rMIServerSocketFactory = chooseFactory(); 
    ServerSocket serverSocket = rMIServerSocketFactory.createServerSocket(this.listenPort);
    if (this.listenPort == 0)
      setDefaultPort(serverSocket.getLocalPort(), this.csf, this.ssf); 
    return serverSocket;
  }
  
  static  {
    localHost = getHostnameProperty();
    if (localHost == null)
      try {
        InetAddress inetAddress = InetAddress.getLocalHost();
        byte[] arrayOfByte = inetAddress.getAddress();
        if (arrayOfByte[0] == Byte.MAX_VALUE && arrayOfByte[1] == 0 && arrayOfByte[2] == 0 && arrayOfByte[3] == 1)
          localHostKnown = false; 
        if (getBoolean("java.rmi.server.useLocalHostName")) {
          localHost = FQDN.attemptFQDN(inetAddress);
        } else {
          localHost = inetAddress.getHostAddress();
        } 
      } catch (Exception exception) {
        localHostKnown = false;
        localHost = null;
      }  
    if (TCPTransport.tcpLog.isLoggable(Log.BRIEF))
      TCPTransport.tcpLog.log(Log.BRIEF, "localHostKnown = " + localHostKnown + ", localHost = " + localHost); 
    localEndpoints = new HashMap();
  }
  
  private static class FQDN implements Runnable {
    private String reverseLookup;
    
    private String hostAddress;
    
    private FQDN(String param1String) { this.hostAddress = param1String; }
    
    static String attemptFQDN(InetAddress param1InetAddress) throws UnknownHostException {
      String str = param1InetAddress.getHostName();
      if (str.indexOf('.') < 0) {
        String str1 = param1InetAddress.getHostAddress();
        FQDN fQDN = new FQDN(str1);
        int i = TCPEndpoint.getInt("sun.rmi.transport.tcp.localHostNameTimeOut", 10000);
        try {
          synchronized (fQDN) {
            fQDN.getFQDN();
            fQDN.wait(i);
          } 
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
        } 
        str = fQDN.getHost();
        if (str == null || str.equals("") || str.indexOf('.') < 0)
          str = str1; 
      } 
      return str;
    }
    
    private void getFQDN() {
      Thread thread = (Thread)AccessController.doPrivileged(new NewThreadAction(this, "FQDN Finder", true));
      thread.start();
    }
    
    private String getHost() { return this.reverseLookup; }
    
    public void run() {
      str = null;
      try {
        str = InetAddress.getByName(this.hostAddress).getHostName();
      } catch (UnknownHostException unknownHostException) {
      
      } finally {
        synchronized (this) {
          this.reverseLookup = str;
          notify();
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\tcp\TCPEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */