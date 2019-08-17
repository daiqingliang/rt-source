package sun.rmi.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Connection {
  InputStream getInputStream() throws IOException;
  
  void releaseInputStream() throws IOException;
  
  OutputStream getOutputStream() throws IOException;
  
  void releaseOutputStream() throws IOException;
  
  boolean isReusable();
  
  void close() throws IOException;
  
  Channel getChannel();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */