package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

class PlainSocketImpl extends AbstractPlainSocketImpl {
  private AbstractPlainSocketImpl impl;
  
  private static float version;
  
  private static boolean preferIPv4Stack = false;
  
  private static boolean useDualStackImpl = false;
  
  private static String exclBindProp;
  
  private static boolean exclusiveBind = true;
  
  PlainSocketImpl() {
    if (useDualStackImpl) {
      this.impl = new DualStackPlainSocketImpl(exclusiveBind);
    } else {
      this.impl = new TwoStacksPlainSocketImpl(exclusiveBind);
    } 
  }
  
  PlainSocketImpl(FileDescriptor paramFileDescriptor) {
    if (useDualStackImpl) {
      this.impl = new DualStackPlainSocketImpl(paramFileDescriptor, exclusiveBind);
    } else {
      this.impl = new TwoStacksPlainSocketImpl(paramFileDescriptor, exclusiveBind);
    } 
  }
  
  protected FileDescriptor getFileDescriptor() { return this.impl.getFileDescriptor(); }
  
  protected InetAddress getInetAddress() { return this.impl.getInetAddress(); }
  
  protected int getPort() { return this.impl.getPort(); }
  
  protected int getLocalPort() { return this.impl.getLocalPort(); }
  
  void setSocket(Socket paramSocket) { this.impl.setSocket(paramSocket); }
  
  Socket getSocket() { return this.impl.getSocket(); }
  
  void setServerSocket(ServerSocket paramServerSocket) { this.impl.setServerSocket(paramServerSocket); }
  
  ServerSocket getServerSocket() { return this.impl.getServerSocket(); }
  
  public String toString() { return this.impl.toString(); }
  
  protected void create(boolean paramBoolean) throws IOException {
    this.impl.create(paramBoolean);
    this.fd = this.impl.fd;
  }
  
  protected void connect(String paramString, int paramInt) throws UnknownHostException, IOException { this.impl.connect(paramString, paramInt); }
  
  protected void connect(InetAddress paramInetAddress, int paramInt) throws IOException { this.impl.connect(paramInetAddress, paramInt); }
  
  protected void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException { this.impl.connect(paramSocketAddress, paramInt); }
  
  public void setOption(int paramInt, Object paramObject) throws SocketException { this.impl.setOption(paramInt, paramObject); }
  
  public Object getOption(int paramInt) throws SocketException { return this.impl.getOption(paramInt); }
  
  void doConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2) throws IOException { this.impl.doConnect(paramInetAddress, paramInt1, paramInt2); }
  
  protected void bind(InetAddress paramInetAddress, int paramInt) throws IOException { this.impl.bind(paramInetAddress, paramInt); }
  
  protected void accept(SocketImpl paramSocketImpl) throws IOException {
    if (paramSocketImpl instanceof PlainSocketImpl) {
      AbstractPlainSocketImpl abstractPlainSocketImpl = ((PlainSocketImpl)paramSocketImpl).impl;
      abstractPlainSocketImpl.address = new InetAddress();
      abstractPlainSocketImpl.fd = new FileDescriptor();
      this.impl.accept(abstractPlainSocketImpl);
      paramSocketImpl.fd = abstractPlainSocketImpl.fd;
    } else {
      this.impl.accept(paramSocketImpl);
    } 
  }
  
  void setFileDescriptor(FileDescriptor paramFileDescriptor) { this.impl.setFileDescriptor(paramFileDescriptor); }
  
  void setAddress(InetAddress paramInetAddress) { this.impl.setAddress(paramInetAddress); }
  
  void setPort(int paramInt) { this.impl.setPort(paramInt); }
  
  void setLocalPort(int paramInt) { this.impl.setLocalPort(paramInt); }
  
  protected InputStream getInputStream() throws IOException { return this.impl.getInputStream(); }
  
  void setInputStream(SocketInputStream paramSocketInputStream) { this.impl.setInputStream(paramSocketInputStream); }
  
  protected OutputStream getOutputStream() throws IOException { return this.impl.getOutputStream(); }
  
  protected void close() {
    try {
      this.impl.close();
    } finally {
      this.fd = null;
    } 
  }
  
  void reset() {
    try {
      this.impl.reset();
    } finally {
      this.fd = null;
    } 
  }
  
  protected void shutdownInput() { this.impl.shutdownInput(); }
  
  protected void shutdownOutput() { this.impl.shutdownOutput(); }
  
  protected void sendUrgentData(int paramInt) { this.impl.sendUrgentData(paramInt); }
  
  FileDescriptor acquireFD() { return this.impl.acquireFD(); }
  
  void releaseFD() { this.impl.releaseFD(); }
  
  public boolean isConnectionReset() { return this.impl.isConnectionReset(); }
  
  public boolean isConnectionResetPending() { return this.impl.isConnectionResetPending(); }
  
  public void setConnectionReset() { this.impl.setConnectionReset(); }
  
  public void setConnectionResetPending() { this.impl.setConnectionResetPending(); }
  
  public boolean isClosedOrPending() { return this.impl.isClosedOrPending(); }
  
  public int getTimeout() { return this.impl.getTimeout(); }
  
  void socketCreate(boolean paramBoolean) throws IOException { this.impl.socketCreate(paramBoolean); }
  
  void socketConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2) throws IOException { this.impl.socketConnect(paramInetAddress, paramInt1, paramInt2); }
  
  void socketBind(InetAddress paramInetAddress, int paramInt) throws IOException { this.impl.socketBind(paramInetAddress, paramInt); }
  
  void socketListen(int paramInt) { this.impl.socketListen(paramInt); }
  
  void socketAccept(SocketImpl paramSocketImpl) throws IOException { this.impl.socketAccept(paramSocketImpl); }
  
  int socketAvailable() { return this.impl.socketAvailable(); }
  
  void socketClose0(boolean paramBoolean) throws IOException { this.impl.socketClose0(paramBoolean); }
  
  void socketShutdown(int paramInt) { this.impl.socketShutdown(paramInt); }
  
  void socketSetOption(int paramInt, boolean paramBoolean, Object paramObject) throws SocketException { this.impl.socketSetOption(paramInt, paramBoolean, paramObject); }
  
  int socketGetOption(int paramInt, Object paramObject) throws SocketException { return this.impl.socketGetOption(paramInt, paramObject); }
  
  void socketSendUrgentData(int paramInt) { this.impl.socketSendUrgentData(paramInt); }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            version = 0.0F;
            try {
              version = Float.parseFloat(System.getProperties().getProperty("os.version"));
              preferIPv4Stack = Boolean.parseBoolean(System.getProperties().getProperty("java.net.preferIPv4Stack"));
              exclBindProp = System.getProperty("sun.net.useExclusiveBind");
            } catch (NumberFormatException numberFormatException) {
              assert false : numberFormatException;
            } 
            return null;
          }
        });
    if (version >= 6.0D && !preferIPv4Stack)
      useDualStackImpl = true; 
    if (exclBindProp != null) {
      exclusiveBind = (exclBindProp.length() == 0) ? true : Boolean.parseBoolean(exclBindProp);
    } else if (version < 6.0D) {
      exclusiveBind = false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\PlainSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */