package javax.management.remote;

import java.io.IOException;

public class JMXServerErrorException extends IOException {
  private static final long serialVersionUID = 3996732239558744666L;
  
  private final Error cause;
  
  public JMXServerErrorException(String paramString, Error paramError) {
    super(paramString);
    this.cause = paramError;
  }
  
  public Throwable getCause() { return this.cause; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXServerErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */