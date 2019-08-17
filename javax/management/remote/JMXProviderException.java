package javax.management.remote;

import java.io.IOException;

public class JMXProviderException extends IOException {
  private static final long serialVersionUID = -3166703627550447198L;
  
  private Throwable cause = null;
  
  public JMXProviderException() {}
  
  public JMXProviderException(String paramString) { super(paramString); }
  
  public JMXProviderException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() { return this.cause; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXProviderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */