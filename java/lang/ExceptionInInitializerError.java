package java.lang;

public class ExceptionInInitializerError extends LinkageError {
  private static final long serialVersionUID = 1521711792217232256L;
  
  private Throwable exception;
  
  public ExceptionInInitializerError() { initCause(null); }
  
  public ExceptionInInitializerError(Throwable paramThrowable) {
    initCause(null);
    this.exception = paramThrowable;
  }
  
  public ExceptionInInitializerError(String paramString) {
    super(paramString);
    initCause(null);
  }
  
  public Throwable getException() { return this.exception; }
  
  public Throwable getCause() { return this.exception; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ExceptionInInitializerError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */