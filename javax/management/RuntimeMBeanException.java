package javax.management;

public class RuntimeMBeanException extends JMRuntimeException {
  private static final long serialVersionUID = 5274912751982730171L;
  
  private RuntimeException runtimeException;
  
  public RuntimeMBeanException(RuntimeException paramRuntimeException) { this.runtimeException = paramRuntimeException; }
  
  public RuntimeMBeanException(RuntimeException paramRuntimeException, String paramString) {
    super(paramString);
    this.runtimeException = paramRuntimeException;
  }
  
  public RuntimeException getTargetException() { return this.runtimeException; }
  
  public Throwable getCause() { return this.runtimeException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\RuntimeMBeanException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */