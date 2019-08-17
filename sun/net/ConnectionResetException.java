package sun.net;

import java.net.SocketException;

public class ConnectionResetException extends SocketException {
  private static final long serialVersionUID = -7633185991801851556L;
  
  public ConnectionResetException(String paramString) { super(paramString); }
  
  public ConnectionResetException() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ConnectionResetException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */