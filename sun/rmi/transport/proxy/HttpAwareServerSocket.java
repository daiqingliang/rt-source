package sun.rmi.transport.proxy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import sun.rmi.runtime.Log;

class HttpAwareServerSocket extends ServerSocket {
  public HttpAwareServerSocket(int paramInt) throws IOException { super(paramInt); }
  
  public HttpAwareServerSocket(int paramInt1, int paramInt2) throws IOException { super(paramInt1, paramInt2); }
  
  public Socket accept() throws IOException {
    Socket socket = super.accept();
    BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
    RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "socket accepted (checking for POST)");
    bufferedInputStream.mark(4);
    boolean bool = (bufferedInputStream.read() == 80 && bufferedInputStream.read() == 79 && bufferedInputStream.read() == 83 && bufferedInputStream.read() == 84) ? 1 : 0;
    bufferedInputStream.reset();
    if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF))
      RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, bool ? "POST found, HTTP socket returned" : "POST not found, direct socket returned"); 
    return bool ? new HttpReceiveSocket(socket, bufferedInputStream, null) : new WrappedSocket(socket, bufferedInputStream, null);
  }
  
  public String toString() { return "HttpAware" + super.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\HttpAwareServerSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */