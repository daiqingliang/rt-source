package javax.xml.soap;

public class SOAPException extends Exception {
  private Throwable cause;
  
  public SOAPException() { this.cause = null; }
  
  public SOAPException(String paramString) {
    super(paramString);
    this.cause = null;
  }
  
  public SOAPException(String paramString, Throwable paramThrowable) {
    super(paramString);
    initCause(paramThrowable);
  }
  
  public SOAPException(Throwable paramThrowable) {
    super(paramThrowable.toString());
    initCause(paramThrowable);
  }
  
  public String getMessage() {
    String str = super.getMessage();
    return (str == null && this.cause != null) ? this.cause.getMessage() : str;
  }
  
  public Throwable getCause() { return this.cause; }
  
  public Throwable initCause(Throwable paramThrowable) {
    if (this.cause != null)
      throw new IllegalStateException("Can't override cause"); 
    if (paramThrowable == this)
      throw new IllegalArgumentException("Self-causation not permitted"); 
    this.cause = paramThrowable;
    return this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\SOAPException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */