package java.net;

import java.io.InterruptedIOException;

public class SocketTimeoutException extends InterruptedIOException {
  private static final long serialVersionUID = -8846654841826352300L;
  
  public SocketTimeoutException(String paramString) { super(paramString); }
  
  public SocketTimeoutException() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\SocketTimeoutException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */