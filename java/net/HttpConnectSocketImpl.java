package java.net;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class HttpConnectSocketImpl extends PlainSocketImpl {
  private static final String httpURLClazzStr = "sun.net.www.protocol.http.HttpURLConnection";
  
  private static final String netClientClazzStr = "sun.net.NetworkClient";
  
  private static final String doTunnelingStr = "doTunneling";
  
  private static final Field httpField;
  
  private static final Field serverSocketField;
  
  private static final Method doTunneling;
  
  private final String server;
  
  private InetSocketAddress external_address;
  
  private HashMap<Integer, Object> optionsMap = new HashMap();
  
  HttpConnectSocketImpl(String paramString, int paramInt) {
    this.server = paramString;
    this.port = paramInt;
  }
  
  HttpConnectSocketImpl(Proxy paramProxy) {
    SocketAddress socketAddress = paramProxy.address();
    if (!(socketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
    this.server = inetSocketAddress.getHostString();
    this.port = inetSocketAddress.getPort();
  }
  
  protected void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    if (paramSocketAddress == null || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    String str1 = inetSocketAddress.isUnresolved() ? inetSocketAddress.getHostName() : inetSocketAddress.getAddress().getHostAddress();
    int i = inetSocketAddress.getPort();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkConnect(str1, i); 
    String str2 = "http://" + str1 + ":" + i;
    Socket socket = privilegedDoTunnel(str2, paramInt);
    this.external_address = inetSocketAddress;
    close();
    AbstractPlainSocketImpl abstractPlainSocketImpl = (AbstractPlainSocketImpl)socket.impl;
    (getSocket()).impl = abstractPlainSocketImpl;
    Set set = this.optionsMap.entrySet();
    try {
      for (Map.Entry entry : set)
        abstractPlainSocketImpl.setOption(((Integer)entry.getKey()).intValue(), entry.getValue()); 
    } catch (IOException iOException) {}
  }
  
  public void setOption(int paramInt, Object paramObject) throws SocketException {
    super.setOption(paramInt, paramObject);
    if (this.external_address != null)
      return; 
    this.optionsMap.put(Integer.valueOf(paramInt), paramObject);
  }
  
  private Socket privilegedDoTunnel(final String urlString, final int timeout) throws IOException {
    try {
      return (Socket)AccessController.doPrivileged(new PrivilegedExceptionAction<Socket>() {
            public Socket run() throws IOException { return HttpConnectSocketImpl.this.doTunnel(urlString, timeout); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
  
  private Socket doTunnel(String paramString, int paramInt) throws IOException {
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.server, this.port));
    URL uRL = new URL(paramString);
    HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection(proxy);
    httpURLConnection.setConnectTimeout(paramInt);
    httpURLConnection.setReadTimeout(this.timeout);
    httpURLConnection.connect();
    doTunneling(httpURLConnection);
    try {
      Object object = httpField.get(httpURLConnection);
      return (Socket)serverSocketField.get(object);
    } catch (IllegalAccessException illegalAccessException) {
      throw new InternalError("Should not reach here", illegalAccessException);
    } 
  }
  
  private void doTunneling(HttpURLConnection paramHttpURLConnection) {
    try {
      doTunneling.invoke(paramHttpURLConnection, new Object[0]);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new InternalError("Should not reach here", reflectiveOperationException);
    } 
  }
  
  protected InetAddress getInetAddress() { return (this.external_address != null) ? this.external_address.getAddress() : super.getInetAddress(); }
  
  protected int getPort() { return (this.external_address != null) ? this.external_address.getPort() : super.getPort(); }
  
  protected int getLocalPort() { return (this.socket != null) ? super.getLocalPort() : ((this.external_address != null) ? this.external_address.getPort() : super.getLocalPort()); }
  
  static  {
    try {
      Class clazz1 = Class.forName("sun.net.www.protocol.http.HttpURLConnection", true, null);
      httpField = clazz1.getDeclaredField("http");
      doTunneling = clazz1.getDeclaredMethod("doTunneling", new Class[0]);
      Class clazz2 = Class.forName("sun.net.NetworkClient", true, null);
      serverSocketField = clazz2.getDeclaredField("serverSocket");
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              httpField.setAccessible(true);
              serverSocketField.setAccessible(true);
              return null;
            }
          });
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new InternalError("Should not reach here", reflectiveOperationException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\HttpConnectSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */