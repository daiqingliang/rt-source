package javax.xml.parsers;

public class FactoryConfigurationError extends Error {
  private static final long serialVersionUID = -827108682472263355L;
  
  private Exception exception = null;
  
  public FactoryConfigurationError() {}
  
  public FactoryConfigurationError(String paramString) { super(paramString); }
  
  public FactoryConfigurationError(Exception paramException) { super(paramException.toString()); }
  
  public FactoryConfigurationError(Exception paramException, String paramString) { super(paramString); }
  
  public String getMessage() {
    String str = super.getMessage();
    return (str == null && this.exception != null) ? this.exception.getMessage() : str;
  }
  
  public Exception getException() { return this.exception; }
  
  public Throwable getCause() { return this.exception; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\parsers\FactoryConfigurationError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */