package java.util.logging;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketHandler extends StreamHandler {
  private Socket sock;
  
  private String host;
  
  private int port;
  
  private void configure() {
    LogManager logManager = LogManager.getLogManager();
    String str = getClass().getName();
    setLevel(logManager.getLevelProperty(str + ".level", Level.ALL));
    setFilter(logManager.getFilterProperty(str + ".filter", null));
    setFormatter(logManager.getFormatterProperty(str + ".formatter", new XMLFormatter()));
    try {
      setEncoding(logManager.getStringProperty(str + ".encoding", null));
    } catch (Exception exception) {
      try {
        setEncoding(null);
      } catch (Exception exception1) {}
    } 
    this.port = logManager.getIntProperty(str + ".port", 0);
    this.host = logManager.getStringProperty(str + ".host", null);
  }
  
  public SocketHandler() {
    configure();
    try {
      connect();
    } catch (IOException iOException) {
      System.err.println("SocketHandler: connect failed to " + this.host + ":" + this.port);
      throw iOException;
    } 
    this.sealed = true;
  }
  
  public SocketHandler(String paramString, int paramInt) throws IOException {
    configure();
    this.sealed = true;
    this.port = paramInt;
    this.host = paramString;
    connect();
  }
  
  private void connect() {
    if (this.port == 0)
      throw new IllegalArgumentException("Bad port: " + this.port); 
    if (this.host == null)
      throw new IllegalArgumentException("Null host name: " + this.host); 
    this.sock = new Socket(this.host, this.port);
    OutputStream outputStream = this.sock.getOutputStream();
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
    setOutputStream(bufferedOutputStream);
  }
  
  public void close() {
    super.close();
    if (this.sock != null)
      try {
        this.sock.close();
      } catch (IOException iOException) {} 
    this.sock = null;
  }
  
  public void publish(LogRecord paramLogRecord) {
    if (!isLoggable(paramLogRecord))
      return; 
    super.publish(paramLogRecord);
    flush();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\SocketHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */