package java.rmi.activation;

public class ActivationException extends Exception {
  public Throwable detail;
  
  private static final long serialVersionUID = -4320118837291406071L;
  
  public ActivationException() { initCause(null); }
  
  public ActivationException(String paramString) {
    super(paramString);
    initCause(null);
  }
  
  public ActivationException(String paramString, Throwable paramThrowable) {
    super(paramString);
    initCause(null);
    this.detail = paramThrowable;
  }
  
  public String getMessage() { return (this.detail == null) ? super.getMessage() : (super.getMessage() + "; nested exception is: \n\t" + this.detail.toString()); }
  
  public Throwable getCause() { return this.detail; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\activation\ActivationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */