package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.NoRouteToHostException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.server.LogStream;
import java.rmi.server.RMISocketFactory;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Hashtable;
import java.util.Vector;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetLongAction;
import sun.security.action.GetPropertyAction;

public class RMIMasterSocketFactory extends RMISocketFactory {
  static int logLevel = LogStream.parseLevel(getLogLevel());
  
  static final Log proxyLog = Log.getLog("sun.rmi.transport.tcp.proxy", "transport", logLevel);
  
  private static long connectTimeout = getConnectTimeout();
  
  private static final boolean eagerHttpFallback = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.transport.proxy.eagerHttpFallback"))).booleanValue();
  
  private Hashtable<String, RMISocketFactory> successTable = new Hashtable();
  
  private static final int MaxRememberedHosts = 64;
  
  private Vector<String> hostList = new Vector(64);
  
  protected RMISocketFactory initialFactory = new RMIDirectSocketFactory();
  
  protected Vector<RMISocketFactory> altFactoryList = new Vector(2);
  
  private static String getLogLevel() { return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.proxy.logLevel")); }
  
  private static long getConnectTimeout() { return ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.proxy.connectTimeout", 15000L))).longValue(); }
  
  public RMIMasterSocketFactory() {
    boolean bool = false;
    try {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("http.proxyHost"));
      if (str == null)
        str = (String)AccessController.doPrivileged(new GetPropertyAction("proxyHost")); 
      boolean bool1 = ((String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.disableHttp", "true"))).equalsIgnoreCase("true");
      if (!bool1 && str != null && str.length() > 0)
        bool = true; 
    } catch (Exception exception) {}
    if (bool) {
      this.altFactoryList.addElement(new RMIHttpToPortSocketFactory());
      this.altFactoryList.addElement(new RMIHttpToCGISocketFactory());
    } 
  }
  
  public Socket createSocket(String paramString, int paramInt) throws IOException {
    if (proxyLog.isLoggable(Log.BRIEF))
      proxyLog.log(Log.BRIEF, "host: " + paramString + ", port: " + paramInt); 
    if (this.altFactoryList.size() == 0)
      return this.initialFactory.createSocket(paramString, paramInt); 
    rMISocketFactory = (RMISocketFactory)this.successTable.get(paramString);
    if (rMISocketFactory != null) {
      if (proxyLog.isLoggable(Log.BRIEF))
        proxyLog.log(Log.BRIEF, "previously successful factory found: " + rMISocketFactory); 
      return rMISocketFactory.createSocket(paramString, paramInt);
    } 
    Socket socket1 = null;
    socket2 = null;
    AsyncConnector asyncConnector = new AsyncConnector(this.initialFactory, paramString, paramInt, AccessController.getContext());
    throwable = null;
    try {
      synchronized (asyncConnector) {
        Thread thread = (Thread)AccessController.doPrivileged(new NewThreadAction(asyncConnector, "AsyncConnector", true));
        thread.start();
        try {
          long l1 = System.currentTimeMillis();
          long l2 = l1 + connectTimeout;
          do {
            asyncConnector.wait(l2 - l1);
            socket1 = checkConnector(asyncConnector);
            if (socket1 != null)
              break; 
            l1 = System.currentTimeMillis();
          } while (l1 < l2);
        } catch (InterruptedException interruptedException) {
          throw new InterruptedIOException("interrupted while waiting for connector");
        } 
      } 
      if (socket1 == null)
        throw new NoRouteToHostException("connect timed out: " + paramString); 
      proxyLog.log(Log.BRIEF, "direct socket connection successful");
      return socket1;
    } catch (UnknownHostException|NoRouteToHostException unknownHostException) {
      throwable = unknownHostException;
    } catch (SocketException socketException) {
      if (eagerHttpFallback) {
        throwable = socketException;
      } else {
        throw socketException;
      } 
    } finally {
      if (throwable != null) {
        if (proxyLog.isLoggable(Log.BRIEF))
          proxyLog.log(Log.BRIEF, "direct socket connection failed: ", throwable); 
        byte b = 0;
        while (b < this.altFactoryList.size()) {
          rMISocketFactory = (RMISocketFactory)this.altFactoryList.elementAt(b);
          if (proxyLog.isLoggable(Log.BRIEF))
            proxyLog.log(Log.BRIEF, "trying with factory: " + rMISocketFactory); 
          try (Socket null = rMISocketFactory.createSocket(paramString, paramInt)) {
            inputStream = socket.getInputStream();
            int i = inputStream.read();
          } catch (IOException iOException) {
            if (proxyLog.isLoggable(Log.BRIEF))
              proxyLog.log(Log.BRIEF, "factory failed: ", iOException); 
          } 
          proxyLog.log(Log.BRIEF, "factory succeeded");
          try {
            socket2 = rMISocketFactory.createSocket(paramString, paramInt);
            break;
          } catch (IOException iOException) {
            break;
          } 
        } 
      } 
    } 
    synchronized (this.successTable) {
      try {
        synchronized (asyncConnector) {
          socket1 = checkConnector(asyncConnector);
        } 
        if (socket1 != null) {
          if (socket2 != null)
            socket2.close(); 
          return socket1;
        } 
        asyncConnector.notUsed();
      } catch (UnknownHostException|NoRouteToHostException unknownHostException) {
        throwable = unknownHostException;
      } catch (SocketException socketException) {
        if (eagerHttpFallback) {
          throwable = socketException;
        } else {
          throw socketException;
        } 
      } 
      if (socket2 != null) {
        rememberFactory(paramString, rMISocketFactory);
        return socket2;
      } 
      throw throwable;
    } 
  }
  
  void rememberFactory(String paramString, RMISocketFactory paramRMISocketFactory) {
    synchronized (this.successTable) {
      while (this.hostList.size() >= 64) {
        this.successTable.remove(this.hostList.elementAt(0));
        this.hostList.removeElementAt(0);
      } 
      this.hostList.addElement(paramString);
      this.successTable.put(paramString, paramRMISocketFactory);
    } 
  }
  
  Socket checkConnector(AsyncConnector paramAsyncConnector) throws IOException {
    Exception exception = paramAsyncConnector.getException();
    if (exception != null) {
      exception.fillInStackTrace();
      if (exception instanceof IOException)
        throw (IOException)exception; 
      if (exception instanceof RuntimeException)
        throw (RuntimeException)exception; 
      throw new Error("internal error: unexpected checked exception: " + exception.toString());
    } 
    return paramAsyncConnector.getSocket();
  }
  
  public ServerSocket createServerSocket(int paramInt) throws IOException { return this.initialFactory.createServerSocket(paramInt); }
  
  private class AsyncConnector implements Runnable {
    private RMISocketFactory factory;
    
    private String host;
    
    private int port;
    
    private AccessControlContext acc;
    
    private Exception exception = null;
    
    private Socket socket = null;
    
    private boolean cleanUp = false;
    
    AsyncConnector(RMISocketFactory param1RMISocketFactory, String param1String, int param1Int, AccessControlContext param1AccessControlContext) {
      this.factory = param1RMISocketFactory;
      this.host = param1String;
      this.port = param1Int;
      this.acc = param1AccessControlContext;
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkConnect(param1String, param1Int); 
    }
    
    public void run() {
      try {
        Socket socket1 = this.factory.createSocket(this.host, this.port);
        synchronized (this) {
          this.socket = socket1;
          notify();
        } 
        RMIMasterSocketFactory.this.rememberFactory(this.host, this.factory);
        synchronized (this) {
          if (this.cleanUp)
            try {
              this.socket.close();
            } catch (IOException iOException) {} 
        } 
      } catch (Exception exception1) {
        synchronized (this) {
          this.exception = exception1;
          notify();
        } 
      } 
    }
    
    private Exception getException() { return this.exception; }
    
    private Socket getSocket() { return this.socket; }
    
    void notUsed() {
      if (this.socket != null)
        try {
          this.socket.close();
        } catch (IOException iOException) {} 
      this.cleanUp = true;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\RMIMasterSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */