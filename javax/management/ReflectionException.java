package javax.management;

public class ReflectionException extends JMException {
  private static final long serialVersionUID = 9170809325636915553L;
  
  private Exception exception;
  
  public ReflectionException(Exception paramException) { this.exception = paramException; }
  
  public ReflectionException(Exception paramException, String paramString) {
    super(paramString);
    this.exception = paramException;
  }
  
  public Exception getTargetException() { return this.exception; }
  
  public Throwable getCause() { return this.exception; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\ReflectionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */