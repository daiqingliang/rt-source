package java.security;

public class PrivilegedActionException extends Exception {
  private static final long serialVersionUID = 4724086851538908602L;
  
  private Exception exception;
  
  public PrivilegedActionException(Exception paramException) {
    super((Throwable)null);
    this.exception = paramException;
  }
  
  public Exception getException() { return this.exception; }
  
  public Throwable getCause() { return this.exception; }
  
  public String toString() {
    String str = getClass().getName();
    return (this.exception != null) ? (str + ": " + this.exception.toString()) : str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\PrivilegedActionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */