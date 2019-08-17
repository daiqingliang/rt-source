package java.io;

public class WriteAbortedException extends ObjectStreamException {
  private static final long serialVersionUID = -3326426625597282442L;
  
  public Exception detail;
  
  public WriteAbortedException(String paramString, Exception paramException) {
    super(paramString);
    initCause(null);
    this.detail = paramException;
  }
  
  public String getMessage() { return (this.detail == null) ? super.getMessage() : (super.getMessage() + "; " + this.detail.toString()); }
  
  public Throwable getCause() { return this.detail; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\WriteAbortedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */