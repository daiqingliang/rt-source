package javax.management;

public class RuntimeErrorException extends JMRuntimeException {
  private static final long serialVersionUID = 704338937753949796L;
  
  private Error error;
  
  public RuntimeErrorException(Error paramError) { this.error = paramError; }
  
  public RuntimeErrorException(Error paramError, String paramString) {
    super(paramString);
    this.error = paramError;
  }
  
  public Error getTargetError() { return this.error; }
  
  public Throwable getCause() { return this.error; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\RuntimeErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */