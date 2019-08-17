package sun.rmi.transport.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Channel;
import sun.rmi.transport.Connection;
import sun.rmi.transport.proxy.RMISocketInfo;

public class TCPConnection implements Connection {
  private Socket socket;
  
  private Channel channel;
  
  private InputStream in = null;
  
  private OutputStream out = null;
  
  private long expiration = Float.MAX_VALUE;
  
  private long lastuse = Float.MIN_VALUE;
  
  private long roundtrip = 5L;
  
  TCPConnection(TCPChannel paramTCPChannel, Socket paramSocket, InputStream paramInputStream, OutputStream paramOutputStream) {
    this.socket = paramSocket;
    this.channel = paramTCPChannel;
    this.in = paramInputStream;
    this.out = paramOutputStream;
  }
  
  TCPConnection(TCPChannel paramTCPChannel, InputStream paramInputStream, OutputStream paramOutputStream) { this(paramTCPChannel, null, paramInputStream, paramOutputStream); }
  
  TCPConnection(TCPChannel paramTCPChannel, Socket paramSocket) { this(paramTCPChannel, paramSocket, null, null); }
  
  public OutputStream getOutputStream() throws IOException {
    if (this.out == null)
      this.out = new BufferedOutputStream(this.socket.getOutputStream()); 
    return this.out;
  }
  
  public void releaseOutputStream() throws IOException {
    if (this.out != null)
      this.out.flush(); 
  }
  
  public InputStream getInputStream() throws IOException {
    if (this.in == null)
      this.in = new BufferedInputStream(this.socket.getInputStream()); 
    return this.in;
  }
  
  public void releaseInputStream() throws IOException {}
  
  public boolean isReusable() { return (this.socket != null && this.socket instanceof RMISocketInfo) ? ((RMISocketInfo)this.socket).isReusable() : 1; }
  
  void setExpiration(long paramLong) { this.expiration = paramLong; }
  
  void setLastUseTime(long paramLong) { this.lastuse = paramLong; }
  
  boolean expired(long paramLong) { return (this.expiration <= paramLong); }
  
  public boolean isDead() {
    OutputStream outputStream;
    InputStream inputStream;
    long l = System.currentTimeMillis();
    if (this.roundtrip > 0L && l < this.lastuse + this.roundtrip)
      return false; 
    try {
      inputStream = getInputStream();
      outputStream = getOutputStream();
    } catch (IOException iOException) {
      return true;
    } 
    int i = 0;
    try {
      outputStream.write(82);
      outputStream.flush();
      i = inputStream.read();
    } catch (IOException iOException) {
      TCPTransport.tcpLog.log(Log.VERBOSE, "exception: ", iOException);
      TCPTransport.tcpLog.log(Log.BRIEF, "server ping failed");
      return true;
    } 
    if (i == 83) {
      this.roundtrip = (System.currentTimeMillis() - l) * 2L;
      return false;
    } 
    if (TCPTransport.tcpLog.isLoggable(Log.BRIEF))
      TCPTransport.tcpLog.log(Log.BRIEF, (i == -1) ? "server has been deactivated" : ("server protocol error: ping response = " + i)); 
    return true;
  }
  
  public void close() throws IOException {
    TCPTransport.tcpLog.log(Log.BRIEF, "close connection");
    if (this.socket != null) {
      this.socket.close();
    } else {
      this.in.close();
      this.out.close();
    } 
  }
  
  public Channel getChannel() { return this.channel; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\tcp\TCPConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */