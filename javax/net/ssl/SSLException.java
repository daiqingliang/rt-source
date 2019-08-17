package javax.net.ssl;

import java.io.IOException;

public class SSLException extends IOException {
  private static final long serialVersionUID = 4511006460650708967L;
  
  public SSLException(String paramString) { super(paramString); }
  
  public SSLException(String paramString, Throwable paramThrowable) {
    super(paramString);
    initCause(paramThrowable);
  }
  
  public SSLException(Throwable paramThrowable) {
    super((paramThrowable == null) ? null : paramThrowable.toString());
    initCause(paramThrowable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */