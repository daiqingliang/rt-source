package java.rmi.server;

public class ServerCloneException extends CloneNotSupportedException {
  public Exception detail;
  
  private static final long serialVersionUID = 6617456357664815945L;
  
  public ServerCloneException(String paramString) {
    super(paramString);
    initCause(null);
  }
  
  public ServerCloneException(String paramString, Exception paramException) {
    super(paramString);
    initCause(null);
    this.detail = paramException;
  }
  
  public String getMessage() { return (this.detail == null) ? super.getMessage() : (super.getMessage() + "; nested exception is: \n\t" + this.detail.toString()); }
  
  public Throwable getCause() { return this.detail; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\ServerCloneException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */