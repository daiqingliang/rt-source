package javax.management;

public class MBeanException extends JMException {
  private static final long serialVersionUID = 4066342430588744142L;
  
  private Exception exception;
  
  public MBeanException(Exception paramException) { this.exception = paramException; }
  
  public MBeanException(Exception paramException, String paramString) {
    super(paramString);
    this.exception = paramException;
  }
  
  public Exception getTargetException() { return this.exception; }
  
  public Throwable getCause() { return this.exception; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */