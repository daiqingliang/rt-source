package javax.security.sasl;

import java.io.IOException;

public class SaslException extends IOException {
  private Throwable _exception;
  
  private static final long serialVersionUID = 4579784287983423626L;
  
  public SaslException() {}
  
  public SaslException(String paramString) { super(paramString); }
  
  public SaslException(String paramString, Throwable paramThrowable) {
    super(paramString);
    if (paramThrowable != null)
      initCause(paramThrowable); 
  }
  
  public Throwable getCause() { return this._exception; }
  
  public Throwable initCause(Throwable paramThrowable) {
    super.initCause(paramThrowable);
    this._exception = paramThrowable;
    return this;
  }
  
  public String toString() {
    String str = super.toString();
    if (this._exception != null && this._exception != this)
      str = str + " [Caused by " + this._exception.toString() + "]"; 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\sasl\SaslException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */