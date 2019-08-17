package javax.management;

public class RuntimeOperationsException extends JMRuntimeException {
  private static final long serialVersionUID = -8408923047489133588L;
  
  private RuntimeException runtimeException;
  
  public RuntimeOperationsException(RuntimeException paramRuntimeException) { this.runtimeException = paramRuntimeException; }
  
  public RuntimeOperationsException(RuntimeException paramRuntimeException, String paramString) {
    super(paramString);
    this.runtimeException = paramRuntimeException;
  }
  
  public RuntimeException getTargetException() { return this.runtimeException; }
  
  public Throwable getCause() { return this.runtimeException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\RuntimeOperationsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */